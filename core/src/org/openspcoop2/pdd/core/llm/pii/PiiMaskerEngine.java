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

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalMessage;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolResultBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;

/**
 * Orchestratore del PII Masking: naviga gli oggetti canonical e applica in sequenza i
 * masker delle Regole PII (ordine = "prima vince", poiché una porzione già sostituita da
 * pseudonimo non viene ri-mascherata). La reversibilità è delegata al {@link PiiVault}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class PiiMaskerEngine {

	private final List<PiiRuleMasker> maskers;

	private PiiMaskerEngine(List<PiiRuleMasker> maskers) {
		this.maskers = maskers;
	}

	/** Costruisce l'engine risolvendo un masker per ciascuna Regola PII (fail-closed su errore). */
	public static PiiMaskerEngine build(List<PiiRuleConfig> rules) throws PiiMaskingException {
		List<PiiRuleMasker> list = new ArrayList<>();
		if (rules != null) {
			for (PiiRuleConfig rule : rules) {
				list.add(PiiMaskerRegistry.createMasker(rule));
			}
		}
		return new PiiMaskerEngine(list);
	}

	public boolean hasMaskers() {
		return !this.maskers.isEmpty();
	}

	/** Maschera la conversazione della richiesta (system + testo; tool_result/tool_use se abilitati). */
	public void maskRequest(CanonicalChatRequest request, PiiBindingConfig cfg, PiiVault vault, PiiFindings findings) {
		if (request == null) {
			return;
		}
		request.setSystem(maskText(request.getSystem(), vault, findings));
		if (request.getMessages() != null) {
			for (CanonicalMessage msg : request.getMessages()) {
				maskBlocks(msg != null ? msg.getContent() : null, cfg, vault, findings);
			}
		}
	}

	private void maskBlocks(List<CanonicalContentBlock> blocks, PiiBindingConfig cfg, PiiVault vault, PiiFindings findings) {
		if (blocks == null) {
			return;
		}
		for (CanonicalContentBlock block : blocks) {
			if (block instanceof CanonicalTextBlock) {
				CanonicalTextBlock t = (CanonicalTextBlock) block;
				t.setText(maskText(t.getText(), vault, findings));
			} else if (block instanceof CanonicalToolResultBlock && cfg != null && cfg.isMaskToolResult()) {
				CanonicalToolResultBlock tr = (CanonicalToolResultBlock) block;
				tr.setContent(maskText(tr.getContent(), vault, findings));
			} else if (block instanceof CanonicalToolUseBlock && cfg != null && cfg.isMaskToolUse()) {
				// 6a: maschera la PII presente negli argomenti dei tool_use storici (l'unmask della
				// chiamata nuova avviene in risposta). Coerente col tool_result: vault condiviso.
				CanonicalToolUseBlock tu = (CanonicalToolUseBlock) block;
				maskJsonValue(tu.getInput(), vault, findings);
			}
		}
	}

	/** Maschera ricorsivamente i soli valori stringa dentro l'input JSON di un tool_use (chiavi escluse). */
	@SuppressWarnings("unchecked")
	private void maskJsonValue(Object value, PiiVault vault, PiiFindings findings) {
		if (value instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) value;
			for (Map.Entry<String, Object> e : map.entrySet()) {
				Object v = e.getValue();
				if (v instanceof String) {
					e.setValue(maskText((String) v, vault, findings));
				} else {
					maskJsonValue(v, vault, findings);
				}
			}
		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			for (int i = 0; i < list.size(); i++) {
				Object v = list.get(i);
				if (v instanceof String) {
					list.set(i, maskText((String) v, vault, findings));
				} else {
					maskJsonValue(v, vault, findings);
				}
			}
		}
	}

	/** Applica in sequenza tutti i masker a un testo. */
	public String maskText(String text, PiiVault vault, PiiFindings findings) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		String result = text;
		for (PiiRuleMasker masker : this.maskers) {
			result = masker.mask(result, vault, findings);
		}
		return result;
	}

	/**
	 * Ripristina la PII nella response (sync): sempre i blocchi di testo; se {@code unmaskToolUse},
	 * anche gli argomenti dei tool_use (6b) così i tool operano sui valori reali.
	 */
	public static void unmaskResponse(CanonicalChatResponse response, PiiVault vault, boolean unmaskToolUse) {
		if (response == null || response.getContent() == null || vault == null) {
			return;
		}
		for (CanonicalContentBlock block : response.getContent()) {
			if (block instanceof CanonicalTextBlock) {
				CanonicalTextBlock t = (CanonicalTextBlock) block;
				t.setText(vault.detokenize(t.getText()));
			} else if (block instanceof CanonicalToolUseBlock && unmaskToolUse) {
				CanonicalToolUseBlock tu = (CanonicalToolUseBlock) block;
				unmaskJsonValue(tu.getInput(), vault);
			}
		}
	}

	/** Ripristina ricorsivamente i valori stringa dentro l'input JSON di un tool_use (sync). */
	@SuppressWarnings("unchecked")
	private static void unmaskJsonValue(Object value, PiiVault vault) {
		if (value instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) value;
			for (Map.Entry<String, Object> e : map.entrySet()) {
				Object v = e.getValue();
				if (v instanceof String) {
					e.setValue(vault.detokenize((String) v));
				} else {
					unmaskJsonValue(v, vault);
				}
			}
		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			for (int i = 0; i < list.size(); i++) {
				Object v = list.get(i);
				if (v instanceof String) {
					list.set(i, vault.detokenize((String) v));
				} else {
					unmaskJsonValue(v, vault);
				}
			}
		}
	}
}
