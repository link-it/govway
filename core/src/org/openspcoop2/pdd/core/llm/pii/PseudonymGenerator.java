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

/**
 * Genera pseudonimi "format-preserving" (valori finti plausibili e, dove applicabile,
 * con checksum corretto) per ciascuna categoria. Deterministici per numero di sequenza
 * (il {@link PiiVault} assegna un progressivo per ogni valore reale distinto), così lo
 * stesso originale produce sempre lo stesso pseudonimo entro il vault.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class PseudonymGenerator {

	private PseudonymGenerator() {}

	/** Categoria redact-only: lo pseudonimo NON è reversibile (non va ripristinato in risposta). */
	public static boolean isRedactOnly(String category) {
		return Costanti.PII_CATEGORY_VALUE_SECRET.equals(category);
	}

	/** Pseudonimo per una categoria redact-only. */
	public static String redacted(String category, int seq) {
		return "[SECRET_" + seq + "]";
	}

	/** Genera lo pseudonimo per la categoria indicata e il progressivo seq (>=1). */
	public static String generate(String category, int seq) {
		if (category == null) {
			return token("PII", seq);
		}
		switch (category) {
		case Costanti.PII_CATEGORY_VALUE_EMAIL:
			// nota: il vault compone lo pseudonimo email con un dominio-finto distinto (EMAIL_DOMAIN);
			// questo ramo resta come fallback quando il dominio non è estraibile.
			return "user" + seq + "@example.com";
		case Costanti.PII_CATEGORY_VALUE_EMAIL_DOMAIN:
			// dominio-finto distinto per dominio reale, INCREMENTALE (seq): stesso dominio nella stessa
			// posizione tende a produrre lo stesso pseudonimo tra richieste diverse anche SENZA cache
			// (stabilità best-effort, non garantita: dipende dall'ordine). La garanzia piena è la
			// cache di sessione (Fase 5). Sotto example.com (spazio riservato RFC 2606).
			return "example" + seq + ".com";
		case Costanti.PII_CATEGORY_VALUE_PHONE:
			return "+39 351 " + pad(seq, 7);
		case Costanti.PII_CATEGORY_VALUE_IBAN:
			return fakeIban(seq);
		case Costanti.PII_CATEGORY_VALUE_CARD:
			return fakeCard(seq);
		case Costanti.PII_CATEGORY_VALUE_CF:
			return fakeCodiceFiscale(seq);
		case Costanti.PII_CATEGORY_VALUE_PIVA:
			return fakePartitaIva(seq);
		case Costanti.PII_CATEGORY_VALUE_IP:
			return "10." + (seq / 65536 % 256) + "." + (seq / 256 % 256) + "." + (seq % 256);
		case Costanti.PII_CATEGORY_VALUE_MAC:
			return String.format("02:00:00:%02x:%02x:%02x", (seq >> 16) & 0xFF, (seq >> 8) & 0xFF, seq & 0xFF);
		case Costanti.PII_CATEGORY_VALUE_PLATE:
			return "ZZ" + pad(seq % 1000, 3) + "ZZ";
		case Costanti.PII_CATEGORY_VALUE_DOC_ID:
			return "XX" + pad(seq, 7);
		case Costanti.PII_CATEGORY_VALUE_USERNAME_PATH:
			return "user" + seq;
		case Costanti.PII_CATEGORY_VALUE_CUSTOM:
		default:
			return token("PII", seq);
		}
	}

	private static String token(String type, int seq) {
		return "[" + type + "_" + seq + "]";
	}

	private static String pad(int n, int len) {
		String s = Integer.toString(Math.abs(n));
		if (s.length() >= len) {
			return s.substring(s.length() - len);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = s.length(); i < len; i++) {
			sb.append('0');
		}
		return sb.append(s).toString();
	}

	/** IBAN IT valido (27 char): IT + 2 check + 1 lettera CIN + 22 cifre; check via mod-97. */
	private static String fakeIban(int seq) {
		char cin = (char) ('A' + (seq % 26));
		String bban = cin + pad(seq, 22);
		// check digits: (bban + "IT00") -> numerico -> 98 - mod97
		String rearranged = bban + "IT00";
		StringBuilder numeric = new StringBuilder();
		for (int i = 0; i < rearranged.length(); i++) {
			char c = rearranged.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				numeric.append(c - 'A' + 10);
			} else {
				numeric.append(c);
			}
		}
		int check = 98 - PiiValidators.mod97(numeric.toString());
		return "IT" + pad(check, 2) + bban;
	}

	/** Numero carta 16 cifre Luhn-valido. */
	private static String fakeCard(int seq) {
		String first15 = "4000" + pad(seq, 11); // prefisso 4 (Visa-like)
		int sum = 0;
		boolean alternate = true; // la 16a cifra è di controllo: le altre alternano partendo dalla penultima
		for (int i = first15.length() - 1; i >= 0; i--) {
			int n = first15.charAt(i) - '0';
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n -= 9;
				}
			}
			sum += n;
			alternate = !alternate;
		}
		int check = (10 - (sum % 10)) % 10;
		return first15 + check;
	}

	/** Codice Fiscale in formato valido con carattere di controllo corretto. */
	private static String fakeCodiceFiscale(int seq) {
		char[] letters = new char[26];
		for (int i = 0; i < 26; i++) {
			letters[i] = (char) ('A' + i);
		}
		StringBuilder first15 = new StringBuilder();
		// 6 lettere
		for (int i = 0; i < 6; i++) {
			first15.append(letters[(seq + i * 7) % 26]);
		}
		first15.append(pad(seq % 100, 2));       // 2 cifre (anno)
		first15.append(letters[seq % 26]);       // 1 lettera (mese)
		first15.append(pad((seq % 28) + 1, 2));  // 2 cifre (giorno)
		first15.append(letters[(seq + 3) % 26]); // 1 lettera (comune)
		first15.append(pad(seq % 1000, 3));      // 3 cifre (comune)
		char control = PiiValidators.codiceFiscaleControlChar(first15.toString());
		return first15.append(control).toString();
	}

	/** Partita IVA 11 cifre con check digit valido. */
	private static String fakePartitaIva(int seq) {
		String first10 = pad(seq, 10);
		int sum = 0;
		for (int i = 0; i < 10; i++) {
			int n = first10.charAt(i) - '0';
			if (i % 2 == 1) {
				n *= 2;
				if (n > 9) {
					n -= 9;
				}
			}
			sum += n;
		}
		int check = (10 - (sum % 10)) % 10;
		return first10 + check;
	}
}
