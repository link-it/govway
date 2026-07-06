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
 * Validatori category-specific usati per abbattere i falsi positivi in fase di detection
 * (quando la Regola PII ha la validazione abilitata). Le categorie prive di validatore
 * dedicato restituiscono sempre {@code true}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class PiiValidators {

	private PiiValidators() {}

	/** Valida un candidato per la categoria indicata; true se non c'è un validatore specifico. */
	public static boolean isValid(String category, String candidate) {
		if (candidate == null) {
			return false;
		}
		if (category == null) {
			return true;
		}
		switch (category) {
		case Costanti.PII_CATEGORY_VALUE_CARD:
			return luhnValid(digitsOnly(candidate));
		case Costanti.PII_CATEGORY_VALUE_IBAN:
			return ibanMod97Valid(candidate);
		case Costanti.PII_CATEGORY_VALUE_CF:
			return codiceFiscaleValid(candidate);
		case Costanti.PII_CATEGORY_VALUE_PIVA:
			return partitaIvaValid(digitsOnly(candidate));
		case Costanti.PII_CATEGORY_VALUE_PHONE:
			return phoneValid(candidate);
		case Costanti.PII_CATEGORY_VALUE_IP:
			return ipValid(candidate);
		default:
			return true;
		}
	}

	static String digitsOnly(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/** Algoritmo di Luhn (carte di credito). */
	public static boolean luhnValid(String digits) {
		if (digits == null || digits.length() < 12) {
			return false;
		}
		int sum = 0;
		boolean alternate = false;
		for (int i = digits.length() - 1; i >= 0; i--) {
			int n = digits.charAt(i) - '0';
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n -= 9;
				}
			}
			sum += n;
			alternate = !alternate;
		}
		return sum % 10 == 0;
	}

	/** Validazione IBAN mod-97 (ISO 13616). */
	public static boolean ibanMod97Valid(String iban) {
		if (iban == null) {
			return false;
		}
		String s = iban.replace(" ", "").toUpperCase();
		if (s.length() < 15 || s.length() > 34 || !s.matches("[A-Z]{2}\\d{2}[A-Z0-9]+")) {
			return false;
		}
		String rearranged = s.substring(4) + s.substring(0, 4);
		StringBuilder numeric = new StringBuilder();
		for (int i = 0; i < rearranged.length(); i++) {
			char c = rearranged.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				numeric.append(c - 'A' + 10);
			} else {
				numeric.append(c);
			}
		}
		return mod97(numeric.toString()) == 1;
	}

	static int mod97(String numeric) {
		int remainder = 0;
		for (int i = 0; i < numeric.length(); i++) {
			remainder = (remainder * 10 + (numeric.charAt(i) - '0')) % 97;
		}
		return remainder;
	}

	/** Validazione del carattere di controllo del Codice Fiscale italiano. */
	public static boolean codiceFiscaleValid(String cf) {
		if (cf == null) {
			return false;
		}
		String s = cf.trim().toUpperCase();
		if (!s.matches("[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]")) {
			return false;
		}
		return s.charAt(15) == codiceFiscaleControlChar(s.substring(0, 15));
	}

	static char codiceFiscaleControlChar(String first15) {
		int sum = 0;
		for (int i = 0; i < 15; i++) {
			char c = first15.charAt(i);
			boolean odd = (i % 2) == 0; // posizioni dispari 1-based
			sum += odd ? cfOddValue(c) : cfEvenValue(c);
		}
		return (char) ('A' + (sum % 26));
	}

	private static int cfEvenValue(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		return c - 'A';
	}

	private static int cfOddValue(char c) {
		int[] odd = {1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23};
		if (c >= '0' && c <= '9') {
			return odd[c - '0'];
		}
		return odd[c - 'A'];
	}

	/** Validazione checksum Partita IVA italiana (11 cifre, algoritmo Luhn-pari/dispari). */
	public static boolean partitaIvaValid(String digits) {
		if (digits == null || digits.length() != 11) {
			return false;
		}
		int sum = 0;
		for (int i = 0; i < 11; i++) {
			int n = digits.charAt(i) - '0';
			if (i % 2 == 1) { // posizioni pari 1-based
				n *= 2;
				if (n > 9) {
					n -= 9;
				}
			}
			sum += n;
		}
		return sum % 10 == 0;
	}

	/**
	 * Validazione indirizzo IP v4 o v6 via commons-validator: scarta i falsi positivi che la regex
	 * (larga: non verifica il range degli ottetti né la forma IPv6) lascia passare, es. {@code 999.888.777.666}
	 * o sequenze/versioni tipo {@code 2024.10.01}.
	 */
	public static boolean ipValid(String ip) {
		if (ip == null) {
			return false;
		}
		return org.apache.commons.validator.routines.InetAddressValidator.getInstance().isValid(ip.trim());
	}

	/** Validazione numero di telefono via libphonenumber (default region IT). */
	public static boolean phoneValid(String phone) {
		try {
			com.google.i18n.phonenumbers.PhoneNumberUtil util = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
			com.google.i18n.phonenumbers.Phonenumber.PhoneNumber number = util.parse(phone, "IT");
			return util.isValidNumber(number);
		} catch (Exception e) {
			return false;
		}
	}
}
