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

import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestione dei segnaposto della modalità di sostituzione "Segnaposto": formato
 * {@code [[PII:<etichetta>:<n>]]}, escape dei segnaposto letterali presenti nel testo del client
 * ({@code [[PII-Escape:...]]}, ricorsivo e reversibile senza vault), ripristino in risposta
 * (sync e streaming) e individuazione dei segnaposto non risolvibili.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class PiiPlaceholders {

	public static final String PREFIX = "[[PII";
	public static final String ESCAPE = "-Escape";
	private static final String SEPARATOR = ":";
	private static final String SUFFIX = "]]";

	private static final int LABEL_MAX_LENGTH_TOLERANT = 80;

	private static final Pattern GENERATED = Pattern.compile(
			"\\[\\[PII:[^:\\[\\]\\r\\n]{1," + LABEL_MAX_LENGTH_TOLERANT + "}:\\d{1,9}\\]\\]");

	private static final Pattern RESPONSE_TEXT = Pattern.compile(
			"\\\\?\\[\\\\?\\[(?i:PII):[^:\\[\\]\\r\\n]{1," + LABEL_MAX_LENGTH_TOLERANT + "}:0*(\\d{1,9})\\\\?\\]\\\\?\\]");

	private static final Pattern RESPONSE_JSON = Pattern.compile(
			"\\[\\[(?i:PII):[^:\\[\\]\\r\\n\"\\\\]{1," + LABEL_MAX_LENGTH_TOLERANT + "}:0*(\\d{1,9})\\]\\]");

	private static final Pattern LITERAL = Pattern.compile(
			"(\\\\?\\[\\\\?\\[)((?i:PII))(?=(?i:-Escape)*:)");

	private static final Pattern ESCAPED = Pattern.compile(
			"(\\\\?\\[\\\\?\\[)((?i:PII))((?i:-Escape)+)(?=:)");

	private static final Pattern PARTIAL_AT_END = Pattern.compile(
			"(?:\\\\?\\[(?:\\\\?\\[(?:[Pp](?:[Ii](?:[Ii](?:[:\\-][^\\[\\]\\r\\n]{0," + (LABEL_MAX_LENGTH_TOLERANT + 32) + "}(?:\\\\?\\](?:\\\\)?)?)?)?)?)?)?\\\\?|\\\\)$");

	private static final int PARTIAL_WINDOW = LABEL_MAX_LENGTH_TOLERANT + 64;

	private PiiPlaceholders() {}

	public static String sanitizeLabel(String label) {
		if (label == null) {
			return null;
		}
		String s = label.replaceAll("[:\\[\\]\\r\\n\\t]", " ").replaceAll("\\s+", " ").trim();
		if (s.length() > LABEL_MAX_LENGTH_TOLERANT) {
			s = s.substring(0, LABEL_MAX_LENGTH_TOLERANT).trim();
		}
		return s;
	}

	public static boolean containsReservedChars(String label) {
		return label != null && label.matches("(?s).*[:\\[\\]\\r\\n\\t].*");
	}

	public static String build(String label, int n) {
		return PREFIX + SEPARATOR + label + SEPARATOR + n + SUFFIX;
	}

	public static String escapeLiterals(String text) {
		if (text == null || text.indexOf('[') < 0) {
			return text;
		}
		Matcher m = LITERAL.matcher(text);
		if (!m.find()) {
			return text;
		}
		m.reset();
		return m.replaceAll(mr -> Matcher.quoteReplacement(mr.group(1) + mr.group(2) + ESCAPE));
	}

	public static String unescapeLiterals(String text) {
		if (text == null || text.indexOf('[') < 0) {
			return text;
		}
		Matcher m = ESCAPED.matcher(text);
		if (!m.find()) {
			return text;
		}
		m.reset();
		return m.replaceAll(mr -> {
			String escapes = mr.group(3);
			return Matcher.quoteReplacement(mr.group(1) + mr.group(2) + escapes.substring(0, escapes.length() - ESCAPE.length()));
		});
	}

	public static String applyOutsideGenerated(String text, UnaryOperator<String> f) {
		if (text == null || text.indexOf(PREFIX + SEPARATOR) < 0) {
			return f.apply(text);
		}
		Matcher m = GENERATED.matcher(text);
		StringBuilder sb = new StringBuilder(text.length() + 16);
		int last = 0;
		while (m.find()) {
			sb.append(f.apply(text.substring(last, m.start())));
			sb.append(m.group());
			last = m.end();
		}
		sb.append(f.apply(text.substring(last)));
		return sb.toString();
	}

	public static String restore(String text, PiiVault vault, boolean placeholderMode, boolean json, Set<String> unresolved) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		String r = text;
		if (vault != null) {
			r = json ? vault.detokenizeJson(r) : vault.detokenize(r);
		}
		if (!placeholderMode) {
			return r;
		}
		r = restorePlaceholders(r, vault, json, unresolved);
		return unescapeLiterals(r);
	}

	private static String restorePlaceholders(String text, PiiVault vault, boolean json, Set<String> unresolved) {
		if (text.indexOf('[') < 0) {
			return text;
		}
		Matcher m = (json ? RESPONSE_JSON : RESPONSE_TEXT).matcher(text);
		if (!m.find()) {
			return text;
		}
		m.reset();
		return m.replaceAll(mr -> {
			String original = null;
			boolean redacted = false;
			try {
				int n = Integer.parseInt(mr.group(1));
				if (vault != null) {
					original = vault.getPlaceholderOriginal(n);
					redacted = original == null && vault.isPlaceholderRedacted(n);
				}
			} catch (NumberFormatException e) {
				original = null;
			}
			if (original != null) {
				return Matcher.quoteReplacement(json ? PiiVault.jsonEscape(original) : original);
			}
			if (!redacted && unresolved != null) {
				unresolved.add(mr.group());
			}
			return Matcher.quoteReplacement(mr.group());
		});
	}

	public static int partialStartIndex(CharSequence buffer) {
		if (buffer == null || buffer.length() == 0) {
			return -1;
		}
		int len = buffer.length();
		int from = Math.max(0, len - PARTIAL_WINDOW);
		Matcher m = PARTIAL_AT_END.matcher(buffer);
		m.region(from, len);
		if (m.find()) {
			return m.start();
		}
		return -1;
	}

	public static int adjustCut(String buffer, int cut, boolean json) {
		if (buffer == null || cut <= 0) {
			return cut;
		}
		int c = cut;
		Matcher m = (json ? RESPONSE_JSON : RESPONSE_TEXT).matcher(buffer);
		while (m.find() && m.start() < c) {
			if (m.end() > c) {
				c = m.start();
			}
		}
		Matcher e = ESCAPED.matcher(buffer);
		while (e.find() && e.start() < c) {
			if (e.end() + SEPARATOR.length() > c) {
				c = e.start();
			}
		}
		int p = partialStartIndex(buffer);
		if (p >= 0 && p < c) {
			c = p;
		}
		return c;
	}

	public static String appendInstructions(String system, String instruction, String escapeInstruction) {
		StringBuilder add = new StringBuilder();
		if (instruction != null && !instruction.trim().isEmpty()) {
			add.append(instruction.trim());
		}
		if (escapeInstruction != null && !escapeInstruction.trim().isEmpty()) {
			if (add.length() > 0) {
				add.append("\n");
			}
			add.append(escapeInstruction.trim());
		}
		if (add.length() == 0) {
			return system;
		}
		if (system == null || system.trim().isEmpty()) {
			return add.toString();
		}
		return system + "\n\n" + add.toString();
	}
}
