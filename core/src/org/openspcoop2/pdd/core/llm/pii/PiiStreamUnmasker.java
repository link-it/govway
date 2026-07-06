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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * De-tokenizer stateful per lo streaming: i {@code text_delta} spezzano il testo in punti
 * arbitrari, quindi uno pseudonimo può arrivare diviso tra due chunk. Questo buffer trattiene
 * la coda che potrebbe contenere uno pseudonimo parziale ed emette solo la parte sicura,
 * già ripristinata. Una istanza per content-block index.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class PiiStreamUnmasker {

	private final PiiVault vault;
	private final int maxPseudonymLength;
	private final Map<Integer, StringBuilder> pending = new LinkedHashMap<>();
	/** per-indice: true se il blocco è un input JSON di tool_use (ripristino JSON-safe). */
	private final Map<Integer, Boolean> jsonMode = new LinkedHashMap<>();
	/**
	 * Snapshot degli pseudonimi reversibili, preso una sola volta alla costruzione: durante l'unmask
	 * della risposta il vault non viene più modificato (nessuna nuova tokenizzazione), quindi evitiamo
	 * di ri-allocare la lista ad ogni chunk in {@link #safeCut(StringBuilder)}.
	 */
	private final List<String> reversiblePseudonyms;

	public PiiStreamUnmasker(PiiVault vault) {
		this.vault = vault;
		this.maxPseudonymLength = Math.max(1, vault != null ? vault.getMaxReversiblePseudonymLength() : 1);
		this.reversiblePseudonyms = vault != null ? vault.getReversiblePseudonyms() : Collections.emptyList();
	}

	/** Accoda un chunk di TESTO per l'indice e restituisce il prefisso sicuro, già ripristinato. */
	public String feed(int index, String chunk) {
		return feed(index, chunk, false);
	}

	/** Come {@link #feed(int,String)} ma il chunk è un frammento JSON (input_json_delta): ripristino JSON-safe. */
	public String feedJson(int index, String chunk) {
		return feed(index, chunk, true);
	}

	private String feed(int index, String chunk, boolean json) {
		if (this.vault == null) {
			return chunk;
		}
		this.jsonMode.put(index, json);
		StringBuilder sb = this.pending.computeIfAbsent(index, k -> new StringBuilder());
		if (chunk != null) {
			sb.append(chunk);
		}
		int cut = safeCut(sb);
		if (cut <= 0) {
			return "";
		}
		String safe = sb.substring(0, cut);
		sb.delete(0, cut);
		return restore(safe, json);
	}

	/** Svuota la coda per l'indice (da chiamare su content_block_stop). */
	public String flush(int index) {
		if (this.vault == null) {
			return "";
		}
		StringBuilder sb = this.pending.remove(index);
		boolean json = Boolean.TRUE.equals(this.jsonMode.remove(index));
		if (sb == null || sb.length() == 0) {
			return "";
		}
		return restore(sb.toString(), json);
	}

	/** Svuota tutte le code residue (da chiamare su message_stop): index -> testo ripristinato. */
	public Map<Integer, String> flushAll() {
		Map<Integer, String> tails = new LinkedHashMap<>();
		if (this.vault == null) {
			return tails;
		}
		for (Map.Entry<Integer, StringBuilder> e : this.pending.entrySet()) {
			if (e.getValue() != null && e.getValue().length() > 0) {
				boolean json = Boolean.TRUE.equals(this.jsonMode.get(e.getKey()));
				tails.put(e.getKey(), restore(e.getValue().toString(), json));
			}
		}
		this.pending.clear();
		this.jsonMode.clear();
		return tails;
	}

	private String restore(String text, boolean json) {
		return json ? this.vault.detokenizeJson(text) : this.vault.detokenize(text);
	}

	/**
	 * Determina il punto di taglio: si trattengono gli ultimi (maxPseudonymLength-1) caratteri,
	 * spostando il taglio a sinistra se uno pseudonimo noto lo attraverserebbe (per non emettere
	 * uno pseudonimo parziale).
	 */
	private int safeCut(StringBuilder sb) {
		int len = sb.length();
		int cut = len - (this.maxPseudonymLength - 1);
		if (cut <= 0) {
			return 0;
		}
		String buffer = sb.toString();
		boolean changed = true;
		int guard = 0;
		while (changed && guard++ < 64) {
			changed = false;
			for (String p : this.reversiblePseudonyms) {
				int from = 0;
				int idx;
				while ((idx = buffer.indexOf(p, from)) >= 0) {
					int end = idx + p.length();
					if (idx < cut && end > cut) {
						cut = idx; // lo pseudonimo attraversa il taglio: sposto prima del suo inizio
						changed = true;
					}
					from = idx + 1;
					if (idx >= cut) {
						break;
					}
				}
			}
		}
		return Math.max(cut, 0);
	}
}
