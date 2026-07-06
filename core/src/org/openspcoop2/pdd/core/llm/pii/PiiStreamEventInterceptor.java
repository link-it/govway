/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.pdd.core.llm.pii;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamTextDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamToolUseDelta;

/**
 * Interceptor sugli eventi canonical dello streaming: ripristina la PII nei {@code text_delta}
 * usando un {@link PiiStreamUnmasker} (con hold-back per pseudonimi spezzati tra chunk). Sui
 * {@code content_block_stop} / {@code message_stop} emette la coda trattenuta prima dello stop.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class PiiStreamEventInterceptor implements UnaryOperator<List<CanonicalStreamEvent>> {

	private final PiiStreamUnmasker unmasker;
	private final boolean unmaskToolUse;
	/** indici dei content-block che sono tool_use (per emettere il tipo di delta corretto nel flush). */
	private final java.util.Set<Integer> toolUseIndices = new java.util.HashSet<>();

	public PiiStreamEventInterceptor(PiiVault vault, boolean unmaskToolUse) {
		this.unmasker = new PiiStreamUnmasker(vault);
		this.unmaskToolUse = unmaskToolUse;
	}

	@Override
	public List<CanonicalStreamEvent> apply(List<CanonicalStreamEvent> events) {
		if (events == null || events.isEmpty()) {
			return events;
		}
		List<CanonicalStreamEvent> out = new ArrayList<>(events.size() + 1);
		for (CanonicalStreamEvent event : events) {
			if (event instanceof CanonicalStreamTextDelta) {
				CanonicalStreamTextDelta td = (CanonicalStreamTextDelta) event;
				String safe = this.unmasker.feed(td.getIndex(), td.getText());
				if (safe != null && !safe.isEmpty()) {
					td.setText(safe);
					out.add(td);
				}
				// se non c'è testo sicuro da emettere ora, il delta viene trattenuto (droppato)
			} else if (event instanceof CanonicalStreamToolUseDelta && this.unmaskToolUse) {
				// unmask degli argomenti (input_json_delta): ripristina i valori reali verso l'Agent
				CanonicalStreamToolUseDelta tu = (CanonicalStreamToolUseDelta) event;
				this.toolUseIndices.add(tu.getIndex());
				String safe = this.unmasker.feedJson(tu.getIndex(), tu.getPartialJsonInput());
				if (safe != null && !safe.isEmpty()) {
					tu.setPartialJsonInput(safe);
					out.add(tu);
				}
			} else if (event instanceof CanonicalStreamContentBlockStop) {
				CanonicalStreamContentBlockStop stop = (CanonicalStreamContentBlockStop) event;
				String tail = this.unmasker.flush(stop.getIndex());
				if (tail != null && !tail.isEmpty()) {
					out.add(makeTail(stop.getIndex(), tail));
				}
				out.add(stop);
			} else if (event instanceof CanonicalStreamMessageStop) {
				Map<Integer, String> tails = this.unmasker.flushAll();
				for (Map.Entry<Integer, String> e : tails.entrySet()) {
					if (e.getValue() != null && !e.getValue().isEmpty()) {
						out.add(makeTail(e.getKey(), e.getValue()));
					}
				}
				out.add(event);
			} else {
				out.add(event);
			}
		}
		return out;
	}

	/** Costruisce il delta di coda con il tipo corretto (tool_use se l'indice è un blocco tool_use). */
	private CanonicalStreamEvent makeTail(int index, String text) {
		return this.toolUseIndices.contains(index)
				? new CanonicalStreamToolUseDelta(index, text)
				: new CanonicalStreamTextDelta(index, text);
	}
}
