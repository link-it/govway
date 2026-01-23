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

package org.openspcoop2.utils.transport.http.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * ContentTypeTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ContentTypeTest {

	public static void main(String[] args) throws UtilsException {
		System.out.println("=== ContentTypeUtilities Test ===\n");

		System.out.println("Verifica equals");
		testEqualsString();
		testEqualsList();

		System.out.println("\nVerifica validateContentType");
		testValidateContentType();

		System.out.println("\nVerifica readBaseTypeFromContentType");
		testReadBaseType();

		System.out.println("\nVerifica readPrimaryTypeFromContentType");
		testReadPrimaryType();

		System.out.println("\nVerifica readSubTypeFromContentType");
		testReadSubType();

		System.out.println("\nVerifica readCharsetFromContentType");
		testReadCharset();

		System.out.println("\nVerifica normalizeToRfc7230");
		testNormalizeToRfc7230();

		System.out.println("\nVerifica buildContentType");
		testBuildContentType();

		System.out.println("\nVerifica isMultipart*");
		testIsMultipart();

		System.out.println("\nVerifica readMultipartBoundaryFromContentType");
		testReadMultipartBoundary();

		System.out.println("\nVerifica isMatch");
		testIsMatch();

		System.out.println("\n=== Tutti i test sono passati ===");
	}

	public static void testEqualsString() throws UtilsException {
		System.out.println("--- Test equals(String, String) ---\n");

		// Test senza parametri
		assertEqualsTrue("application/json", "application/json",
				"Content-Type identici senza parametri");
		assertEqualsFalse("application/json", "application/xml",
				"Content-Type diversi senza parametri");
		assertEqualsFalse("application/json", "text/json",
				"Content-Type con stesso subtype ma type diverso");

		// Test case insensitive per type/subtype
		assertEqualsTrue("application/json", "APPLICATION/JSON",
				"Content-Type case insensitive");
		assertEqualsTrue("Application/Json", "application/json",
				"Content-Type mixed case");

		// Test con singolo parametro - spazi
		assertEqualsTrue("application/json;charset=UTF-8", "application/json; charset=UTF-8",
				"Spazio dopo punto e virgola");
		assertEqualsTrue("application/json; charset=UTF-8", "application/json;charset=UTF-8",
				"Spazio dopo punto e virgola (invertito)");
		assertEqualsTrue("application/json ; charset=UTF-8", "application/json;charset=UTF-8",
				"Spazio prima del punto e virgola");
		assertEqualsTrue("application/json ; charset=UTF-8", "application/json;  charset=UTF-8",
				"Spazi multipli dopo punto e virgola");
		assertEqualsTrue("application/json  ;  charset=UTF-8", "application/json;charset=UTF-8",
				"Spazi multipli prima e dopo punto e virgola");

		// Test con singolo parametro - tab
		assertEqualsTrue("application/json;\tcharset=UTF-8", "application/json;charset=UTF-8",
				"Tab dopo punto e virgola");
		assertEqualsTrue("application/json\t;\tcharset=UTF-8", "application/json;charset=UTF-8",
				"Tab prima e dopo punto e virgola");
		assertEqualsTrue("application/json\t\t;\t\tcharset=UTF-8", "application/json;charset=UTF-8",
				"Tab multipli prima e dopo punto e virgola");

		// Test con singolo parametro - mix spazi e tab
		assertEqualsTrue("application/json;\t charset=UTF-8", "application/json;charset=UTF-8",
				"Tab e spazio dopo punto e virgola");
		assertEqualsTrue("application/json; \tcharset=UTF-8", "application/json;charset=UTF-8",
				"Spazio e tab dopo punto e virgola");
		assertEqualsTrue("application/json \t;\t charset=UTF-8", "application/json;charset=UTF-8",
				"Spazio-tab prima e tab-spazio dopo punto e virgola");
		assertEqualsTrue("application/json\t ;\t charset=UTF-8", "application/json; charset=UTF-8",
				"Tab-spazio prima e tab-spazio dopo punto e virgola");
		assertEqualsTrue("application/json \t ; \t charset=UTF-8", "application/json;charset=UTF-8",
				"Mix spazi e tab multipli attorno al punto e virgola");

		// Test case insensitive per parametri
		assertEqualsTrue("application/json; charset=UTF-8", "application/json; CHARSET=UTF-8",
				"Nome parametro case insensitive");
		assertEqualsTrue("application/json; charset=utf-8", "application/json; charset=UTF-8",
				"Valore charset case insensitive");

		// Test con più parametri - stesso ordine
		assertEqualsTrue("application/json; charset=UTF-8; boundary=something",
				"application/json; charset=UTF-8; boundary=something",
				"Due parametri stesso ordine");
		assertEqualsTrue("application/json;charset=UTF-8;boundary=something",
				"application/json; charset=UTF-8; boundary=something",
				"Due parametri stesso ordine con spazi differenti");

		// Test con più parametri - ordine diverso
		assertEqualsTrue("application/json; charset=UTF-8; boundary=something",
				"application/json; boundary=something; charset=UTF-8",
				"Due parametri ordine diverso");
		assertEqualsTrue("text/xml; charset=UTF-8; action=test",
				"text/xml; action=test; charset=UTF-8",
				"Due parametri ordine diverso (xml)");

		// Test con tre parametri in ordine diverso (valori semplici senza caratteri speciali)
		assertEqualsTrue("multipart/related; type=text; start=cid; boundary=abc",
				"multipart/related; boundary=abc; type=text; start=cid",
				"Tre parametri in ordine diverso");
		assertEqualsTrue("multipart/related; type=text; start=cid; boundary=abc",
				"multipart/related; start=cid; boundary=abc; type=text",
				"Tre parametri in altro ordine diverso");
		// Test con tre parametri in ordine diverso (valori con caratteri speciali quotati)
		// NOTA RFC 2045: I valori dei parametri che contengono caratteri "tspecials" devono essere quotati.
		// RFC 2045 Section 5.1 definisce: tspecials := "(" / ")" / "<" / ">" / "@" / "," / ";" / ":" / "\" / <"> / "/" / "[" / "]" / "?" / "="
		// Il carattere "/" è un tspecials, quindi "application/xop+xml" e "cid:root" (che contiene ":")
		// devono essere racchiusi tra doppi apici. Senza quotatura, il parsing fallisce correttamente
		// secondo le specifiche RFC (non è un bug del parser).
		assertEqualsTrue("multipart/related; type=\"application/xop+xml\"; start=\"cid:root\"; boundary=abc",
				"multipart/related; boundary=abc; type=\"application/xop+xml\"; start=\"cid:root\"",
				"Tre parametri in ordine diverso con valori quotati");

		// Test con più parametri - tab
		assertEqualsTrue("application/json;\tcharset=UTF-8;\tboundary=abc",
				"application/json; charset=UTF-8; boundary=abc",
				"Due parametri con tab");
		assertEqualsTrue("application/json\t;\tcharset=UTF-8\t;\tboundary=abc",
				"application/json;charset=UTF-8;boundary=abc",
				"Due parametri con tab prima e dopo ogni punto e virgola");

		// Test con più parametri - mix spazi e tab
		assertEqualsTrue("application/json; \tcharset=UTF-8;\t boundary=abc",
				"application/json; charset=UTF-8; boundary=abc",
				"Due parametri con mix spazi e tab");
		assertEqualsTrue("text/xml\t; charset=UTF-8 ;\taction=test",
				"text/xml; action=test; charset=UTF-8",
				"Due parametri ordine diverso con mix spazi e tab");
		assertEqualsTrue("multipart/related;\ttype=app \t;\t start=cid\t; boundary=abc",
				"multipart/related; boundary=abc; start=cid; type=app",
				"Tre parametri ordine diverso con mix spazi e tab");

		// Test valori parametri con caratteri speciali
		assertEqualsTrue("text/xml; action=\"urn:test:action\"",
				"text/xml; action=\"urn:test:action\"",
				"Parametro con valore quotato");

		// Test content-type complessi
		assertEqualsTrue("multipart/related; type=\"application/xop+xml\"; start=\"<root>\"; boundary=\"----=_Part\"",
				"multipart/related; boundary=\"----=_Part\"; start=\"<root>\"; type=\"application/xop+xml\"",
				"Content-Type multipart con parametri in ordine diverso");

		// Test content-type complessi con tab e spazi
		assertEqualsTrue("multipart/related;\ttype=\"application/xop+xml\";\tstart=\"<root>\";\tboundary=\"----=_Part\"",
				"multipart/related; boundary=\"----=_Part\"; start=\"<root>\"; type=\"application/xop+xml\"",
				"Content-Type multipart con tab e parametri in ordine diverso");
		assertEqualsTrue("multipart/related \t;\t type=\"application/xop+xml\" \t;\t start=\"<root>\" \t;\t boundary=\"----=_Part\"",
				"multipart/related;boundary=\"----=_Part\";start=\"<root>\";type=\"application/xop+xml\"",
				"Content-Type multipart con mix spazi-tab e parametri in ordine diverso");

		// Test con parametri diversi - deve fallire
		assertEqualsFalse("application/json; charset=UTF-8", "application/json; charset=ISO-8859-1",
				"Stesso tipo ma charset diverso");
		assertEqualsFalse("application/json; charset=UTF-8", "application/json",
				"Uno con charset, l'altro senza");
		assertEqualsFalse("application/json; charset=UTF-8; extra=value", "application/json; charset=UTF-8",
				"Numero diverso di parametri");
	}

	public static void testEqualsList() throws UtilsException {
		System.out.println("\n--- Test equals(List<String>, String) ---\n");

		// Test lista con match
		List<String> list1 = new ArrayList<>();
		list1.add("application/xml");
		list1.add("application/json; charset=UTF-8");
		list1.add("text/plain");
		assertEqualsListTrue(list1, "application/json;charset=UTF-8",
				"Lista contiene content-type equivalente");

		// Test lista senza match
		List<String> list2 = new ArrayList<>();
		list2.add("application/xml");
		list2.add("text/plain");
		assertEqualsListFalse(list2, "application/json",
				"Lista non contiene content-type");

		// Test lista con match ordine parametri diverso
		List<String> list3 = new ArrayList<>();
		list3.add("text/xml; action=test; charset=UTF-8");
		assertEqualsListTrue(list3, "text/xml; charset=UTF-8; action=test",
				"Lista con match parametri ordine diverso");

		// Test lista con match e spazi/tab
		List<String> list4 = new ArrayList<>();
		list4.add("application/json;\tcharset=UTF-8");
		assertEqualsListTrue(list4, "application/json; charset=UTF-8",
				"Lista con match e tab");

		// Test lista con match e mix spazi/tab
		List<String> list5 = new ArrayList<>();
		list5.add("text/xml \t;\t action=test \t;\t charset=UTF-8");
		assertEqualsListTrue(list5, "text/xml; charset=UTF-8; action=test",
				"Lista con match, mix spazi-tab e ordine parametri diverso");

		// Test lista vuota
		List<String> listEmpty = new ArrayList<>();
		assertEqualsListFalse(listEmpty, "application/json",
				"Lista vuota");

		// Test lista null
		assertEqualsListFalse(null, "application/json",
				"Lista null");

		// Test lista con più elementi equivalenti
		List<String> list6 = new ArrayList<>();
		list6.add("application/json;charset=UTF-8");
		list6.add("application/json; charset=UTF-8");
		list6.add("APPLICATION/JSON; CHARSET=UTF-8");
		list6.add("application/json;\tcharset=UTF-8");
		list6.add("application/json \t; \tcharset=UTF-8");
		assertEqualsListTrue(list6, "application/json; charset=UTF-8",
				"Lista con più elementi equivalenti (spazi, tab, case)");
	}

	private static void assertEqualsTrue(String ct1, String ct2, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.equals(ct1, ct2);
		if (result) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       '" + ct1 + "' == '" + ct2 + "' (atteso: true, ottenuto: false)";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertEqualsFalse(String ct1, String ct2, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.equals(ct1, ct2);
		if (!result) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       '" + ct1 + "' != '" + ct2 + "' (atteso: false, ottenuto: true)";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertEqualsListTrue(List<String> list, String ct, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.equals(list, ct);
		if (result) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Lista: " + list + " contiene equivalente di '" + ct + "' (atteso: true, ottenuto: false)";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertEqualsListFalse(List<String> list, String ct, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.equals(list, ct);
		if (!result) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Lista: " + list + " non contiene equivalente di '" + ct + "' (atteso: false, ottenuto: true)";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	public static void testValidateContentType() throws UtilsException {
		System.out.println("\n--- Test validateContentType ---\n");

		// Test content-type validi (non devono lanciare eccezione)
		assertValidContentType("application/json", "Content-Type semplice");
		assertValidContentType("application/xml", "Content-Type XML");
		assertValidContentType("text/plain", "Content-Type text/plain");
		assertValidContentType("text/html", "Content-Type text/html");

		// Test con parametri
		assertValidContentType("application/json; charset=UTF-8", "Content-Type con charset");
		assertValidContentType("application/json;charset=UTF-8", "Content-Type con charset senza spazio");
		assertValidContentType("application/json;\tcharset=UTF-8", "Content-Type con charset e tab");
		assertValidContentType("text/xml; charset=UTF-8; action=\"urn:test\"", "Content-Type con più parametri");

		// Test case insensitive
		assertValidContentType("APPLICATION/JSON", "Content-Type uppercase");
		assertValidContentType("Application/Json", "Content-Type mixed case");
		assertValidContentType("APPLICATION/JSON; CHARSET=UTF-8", "Content-Type e parametro uppercase");

		// Test multipart validi
		assertValidContentType("multipart/related; boundary=abc", "Multipart related con boundary");
		assertValidContentType("multipart/mixed; boundary=\"----=_Part_123\"", "Multipart mixed con boundary quotato");
		assertValidContentType("multipart/form-data; boundary=----WebKitFormBoundary", "Multipart form-data");
		assertValidContentType("multipart/alternative; boundary=frontier", "Multipart alternative");

		// Test multipart related con type MTOM (richiede start-info per essere valido)
		assertValidContentType(
				"multipart/related; type=\"application/xop+xml\"; start-info=\"text/xml\"; boundary=abc",
				"Multipart MTOM completo con start-info");

		// Test content-type con spazi (validi secondo RFC)
		assertValidContentType("application/json ; charset=UTF-8", "Content-Type con spazio prima del ;");
		assertValidContentType("application/json;  charset=UTF-8", "Content-Type con doppio spazio dopo ;");
		assertValidContentType("application/json  ;  charset=UTF-8", "Content-Type con doppi spazi attorno al ;");

		// Test content-type con tab (validi secondo RFC)
		assertValidContentType("application/json\t;\tcharset=UTF-8", "Content-Type con tab attorno al ;");
		assertValidContentType("application/json;\t\tcharset=UTF-8", "Content-Type con doppio tab dopo ;");
		assertValidContentType("application/json\t\t;\t\tcharset=UTF-8", "Content-Type con doppi tab attorno al ;");

		// Test content-type con mix spazi e tab (validi secondo RFC)
		assertValidContentType("application/json \t;\t charset=UTF-8", "Content-Type con spazio-tab e tab-spazio");
		assertValidContentType("application/json\t ;\t charset=UTF-8", "Content-Type con tab-spazio e tab-spazio");
		assertValidContentType("application/json \t ; \t charset=UTF-8", "Content-Type con spazio-tab-spazio attorno al ;");
		assertValidContentType("application/json\t \t;\t \tcharset=UTF-8", "Content-Type con tab-spazio-tab attorno al ;");

		// Test con più parametri e mix spazi/tab
		assertValidContentType("text/xml;\tcharset=UTF-8;\taction=\"test\"", "Due parametri con tab");
		assertValidContentType("text/xml \t;\t charset=UTF-8 \t;\t action=\"test\"", "Due parametri con mix spazi-tab");
		// NOTA: per multipart/related con parametro type, il valore deve essere un media type valido
		// (es. "text/plain" non "text") altrimenti il parser SAAJ fallisce
		assertValidContentType("multipart/related; boundary=abc; type=\"text/plain\"", "Multipart con type valido");

		// Test null e stringa vuota (sono considerati validi - nessuna validazione necessaria)
		assertValidContentType(null, "Content-Type null");
		assertValidContentType("", "Content-Type vuoto");

		// Test content-type non validi (devono lanciare eccezione)
		assertInvalidContentType("application", "Content-Type senza subtype");
		assertInvalidContentType("application/", "Content-Type con subtype vuoto");
		assertInvalidContentType("/json", "Content-Type senza type");
		assertInvalidContentType("applicationjson", "Content-Type senza /");
		assertInvalidContentType("application/json; charset", "Parametro senza valore");
		assertInvalidContentType("application/json; =UTF-8", "Parametro senza nome");
		assertInvalidContentType("application//json", "Content-Type con doppio /");

		// Test caratteri non validi (non quotati)
		// NOTA RFC 2045: I caratteri tspecials devono essere quotati nei valori dei parametri
		assertInvalidContentType("text/xml; action=urn:test:action", "Valore parametro con : non quotato");
	}

	private static void assertValidContentType(String ct, String description) throws UtilsException {
		try {
			ContentTypeUtilities.validateContentType(ct);
			System.out.println("[PASS] " + description + " - valido");
		} catch (UtilsException e) {
			String msg = "[FAIL] " + description + "\n       Content-Type '" + ct + "' dovrebbe essere valido ma ha lanciato: " + e.getMessage();
			System.out.println(msg);
			throw new UtilsException(msg, e);
		}
	}

	private static void assertInvalidContentType(String ct, String description) throws UtilsException {
		try {
			ContentTypeUtilities.validateContentType(ct);
			String msg = "[FAIL] " + description + "\n       Content-Type '" + ct + "' dovrebbe essere invalido ma non ha lanciato eccezione";
			System.out.println(msg);
			throw new UtilsException(msg);
		} catch (UtilsException e) {
			System.out.println("[PASS] " + description + " - correttamente rifiutato: " + e.getMessage());
		}
	}

	// ==================== Test readBaseTypeFromContentType ====================

	public static void testReadBaseType() throws UtilsException {
		System.out.println("--- Test readBaseTypeFromContentType ---\n");

		assertBaseType("application/json", "application/json", "Content-Type semplice");
		assertBaseType("application/json; charset=UTF-8", "application/json", "Content-Type con parametro");
		assertBaseType("application/json;\tcharset=UTF-8", "application/json", "Content-Type con tab");
		assertBaseType("application/json \t; \t charset=UTF-8", "application/json", "Content-Type con mix spazi-tab");
		assertBaseType("APPLICATION/JSON", "APPLICATION/JSON", "Content-Type uppercase");
		assertBaseType("text/xml; charset=UTF-8; action=\"test\"", "text/xml", "Content-Type con più parametri");
		assertBaseType("multipart/related; boundary=abc", "multipart/related", "Multipart");
	}

	private static void assertBaseType(String ct, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.readBaseTypeFromContentType(ct);
		if (expected.equalsIgnoreCase(result)) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Input: '" + ct + "' - Atteso: '" + expected + "', Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test readPrimaryTypeFromContentType ====================

	public static void testReadPrimaryType() throws UtilsException {
		System.out.println("--- Test readPrimaryTypeFromContentType ---\n");

		assertPrimaryType("application/json", "application", "Content-Type application");
		assertPrimaryType("text/plain", "text", "Content-Type text");
		assertPrimaryType("multipart/related", "multipart", "Content-Type multipart");
		assertPrimaryType("image/png", "image", "Content-Type image");
		assertPrimaryType("application/json; charset=UTF-8", "application", "Content-Type con parametro");
		assertPrimaryType("APPLICATION/JSON", "APPLICATION", "Content-Type uppercase");
	}

	private static void assertPrimaryType(String ct, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.readPrimaryTypeFromContentType(ct);
		if (expected.equalsIgnoreCase(result)) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Input: '" + ct + "' - Atteso: '" + expected + "', Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test readSubTypeFromContentType ====================

	public static void testReadSubType() throws UtilsException {
		System.out.println("--- Test readSubTypeFromContentType ---\n");

		assertSubType("application/json", "json", "Content-Type json");
		assertSubType("text/plain", "plain", "Content-Type plain");
		assertSubType("multipart/related", "related", "Content-Type related");
		assertSubType("application/xop+xml", "xop+xml", "Content-Type con +");
		assertSubType("application/json; charset=UTF-8", "json", "Content-Type con parametro");
		assertSubType("APPLICATION/JSON", "JSON", "Content-Type uppercase");
	}

	private static void assertSubType(String ct, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.readSubTypeFromContentType(ct);
		if (expected.equalsIgnoreCase(result)) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Input: '" + ct + "' - Atteso: '" + expected + "', Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test readCharsetFromContentType ====================

	public static void testReadCharset() throws UtilsException {
		System.out.println("--- Test readCharsetFromContentType ---\n");

		assertCharset("application/json; charset=UTF-8", "UTF-8", "Charset UTF-8");
		assertCharset("application/json; charset=ISO-8859-1", "ISO-8859-1", "Charset ISO-8859-1");
		assertCharset("application/json;charset=UTF-8", "UTF-8", "Charset senza spazio");
		assertCharset("application/json;\tcharset=UTF-8", "UTF-8", "Charset con tab");
		assertCharset("application/json; CHARSET=UTF-8", "UTF-8", "Charset uppercase");
		assertCharset("text/xml; charset=UTF-8; action=\"test\"", "UTF-8", "Charset con altri parametri");
		assertCharset("application/json", null, "Senza charset");
		assertCharset("application/json; boundary=abc", null, "Con altro parametro ma senza charset");
	}

	private static void assertCharset(String ct, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.readCharsetFromContentType(ct);
		boolean match = (expected == null && result == null) || (expected != null && expected.equalsIgnoreCase(result));
		if (match) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Input: '" + ct + "' - Atteso: '" + expected + "', Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test normalizeToRfc7230 ====================

	public static void testNormalizeToRfc7230() throws UtilsException {
		System.out.println("--- Test normalizeToRfc7230 ---\n");

		// Test rimozione tab
		assertNormalize("application/json;\tcharset=UTF-8", "application/json; charset=UTF-8", "Tab singolo");
		assertNormalize("application/json;\t\tcharset=UTF-8", "application/json;  charset=UTF-8", "Tab doppio");
		assertNormalize("application/json\t;\tcharset=UTF-8", "application/json ; charset=UTF-8", "Tab attorno al ;");

		// Test rimozione newline
		assertNormalize("application/json;\r\ncharset=UTF-8", "application/json; charset=UTF-8", "CRLF");
		assertNormalize("application/json;\ncharset=UTF-8", "application/json; charset=UTF-8", "LF");
		assertNormalize("application/json;\rcharset=UTF-8", "application/json; charset=UTF-8", "CR");

		// Test mix
		assertNormalize("application/json\t;\r\n\tcharset=UTF-8", "application/json ;  charset=UTF-8", "Mix tab e CRLF");

		// Test trim spazi iniziali/finali
		assertNormalize("  application/json  ", "application/json", "Trim spazi");
		assertNormalize("\tapplication/json\t", "application/json", "Trim tab");
	}

	private static void assertNormalize(String input, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.normalizeToRfc7230(input);
		if (expected.equals(result)) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Input: '" + escape(input) + "' - Atteso: '" + escape(expected) + "', Ottenuto: '" + escape(result) + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static String escape(String s) {
		return s.replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
	}

	// ==================== Test buildContentType ====================

	public static void testBuildContentType() throws UtilsException {
		System.out.println("--- Test buildContentType ---\n");

		// Test senza parametri
		assertBuildContentType("application/json", null, "application/json", "Senza parametri");

		// Test con un parametro
		Map<String, String> params1 = new HashMap<>();
		params1.put("charset", "UTF-8");
		assertBuildContentType("application/json", params1, "application/json; charset=UTF-8", "Con charset");

		// Test con più parametri
		Map<String, String> params2 = new HashMap<>();
		params2.put("charset", "UTF-8");
		params2.put("boundary", "abc");
		// L'ordine dei parametri può variare, quindi verifichiamo che contenga entrambi
		String result = ContentTypeUtilities.buildContentType("multipart/related", params2);
		if (result.contains("charset=UTF-8") && result.contains("boundary=abc") && result.startsWith("multipart/related")) {
			System.out.println("[PASS] Con più parametri");
		} else {
			String msg = "[FAIL] Con più parametri\n       Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}

		// Test con parametri vuoti
		assertBuildContentType("text/plain", new HashMap<>(), "text/plain", "Con mappa vuota");
	}

	private static void assertBuildContentType(String baseType, Map<String, String> params, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.buildContentType(baseType, params);
		if (expected.equals(result)) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Atteso: '" + expected + "', Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test isMultipart* ====================

	public static void testIsMultipart() throws UtilsException {
		System.out.println("--- Test isMultipart* ---\n");

		// isMultipartType
		assertIsMultipartType("multipart/related; boundary=abc", true, "multipart/related");
		assertIsMultipartType("multipart/mixed; boundary=abc", true, "multipart/mixed");
		assertIsMultipartType("multipart/form-data; boundary=abc", true, "multipart/form-data");
		assertIsMultipartType("multipart/alternative; boundary=abc", true, "multipart/alternative");
		assertIsMultipartType("application/json", false, "application/json");
		assertIsMultipartType("text/plain", false, "text/plain");

		// isMultipartRelated
		assertIsMultipartRelated("multipart/related; boundary=abc", true, "multipart/related");
		assertIsMultipartRelated("multipart/mixed; boundary=abc", false, "multipart/mixed non è related");
		assertIsMultipartRelated("MULTIPART/RELATED; boundary=abc", true, "multipart/related uppercase");

		// isMultipartMixed
		assertIsMultipartMixed("multipart/mixed; boundary=abc", true, "multipart/mixed");
		assertIsMultipartMixed("multipart/related; boundary=abc", false, "multipart/related non è mixed");

		// isMultipartFormData
		assertIsMultipartFormData("multipart/form-data; boundary=abc", true, "multipart/form-data");
		assertIsMultipartFormData("multipart/related; boundary=abc", false, "multipart/related non è form-data");

		// isMultipartAlternative
		assertIsMultipartAlternative("multipart/alternative; boundary=abc", true, "multipart/alternative");
		assertIsMultipartAlternative("multipart/related; boundary=abc", false, "multipart/related non è alternative");

		// isMultipartContentType (stesso di isMultipartType)
		assertIsMultipartContentType("multipart/anything; boundary=abc", true, "multipart/anything");
		assertIsMultipartContentType("application/json", false, "application/json");
	}

	private static void assertIsMultipartType(String ct, boolean expected, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMultipartType(ct);
		if (result == expected) {
			System.out.println("[PASS] isMultipartType: " + description);
		} else {
			String msg = "[FAIL] isMultipartType: " + description + "\n       Input: '" + ct + "' - Atteso: " + expected + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertIsMultipartRelated(String ct, boolean expected, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMultipartRelated(ct);
		if (result == expected) {
			System.out.println("[PASS] isMultipartRelated: " + description);
		} else {
			String msg = "[FAIL] isMultipartRelated: " + description + "\n       Input: '" + ct + "' - Atteso: " + expected + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertIsMultipartMixed(String ct, boolean expected, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMultipartMixed(ct);
		if (result == expected) {
			System.out.println("[PASS] isMultipartMixed: " + description);
		} else {
			String msg = "[FAIL] isMultipartMixed: " + description + "\n       Input: '" + ct + "' - Atteso: " + expected + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertIsMultipartFormData(String ct, boolean expected, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMultipartFormData(ct);
		if (result == expected) {
			System.out.println("[PASS] isMultipartFormData: " + description);
		} else {
			String msg = "[FAIL] isMultipartFormData: " + description + "\n       Input: '" + ct + "' - Atteso: " + expected + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertIsMultipartAlternative(String ct, boolean expected, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMultipartAlternative(ct);
		if (result == expected) {
			System.out.println("[PASS] isMultipartAlternative: " + description);
		} else {
			String msg = "[FAIL] isMultipartAlternative: " + description + "\n       Input: '" + ct + "' - Atteso: " + expected + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertIsMultipartContentType(String ct, boolean expected, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMultipartContentType(ct);
		if (result == expected) {
			System.out.println("[PASS] isMultipartContentType: " + description);
		} else {
			String msg = "[FAIL] isMultipartContentType: " + description + "\n       Input: '" + ct + "' - Atteso: " + expected + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test readMultipartBoundaryFromContentType ====================

	public static void testReadMultipartBoundary() throws UtilsException {
		System.out.println("--- Test readMultipartBoundaryFromContentType ---\n");

		assertMultipartBoundary("multipart/related; boundary=abc", "abc", "Boundary semplice");
		assertMultipartBoundary("multipart/related; boundary=\"----=_Part_123\"", "----=_Part_123", "Boundary quotato");
		assertMultipartBoundary("multipart/related; type=\"text/xml\"; boundary=xyz", "xyz", "Boundary con altri parametri");
		assertMultipartBoundary("multipart/related; BOUNDARY=abc", "abc", "Boundary uppercase");
		assertMultipartBoundary("multipart/related; boundary=abc123-_.", "abc123-_.", "Boundary con caratteri speciali");
		assertMultipartBoundary("application/json", null, "Content-Type senza boundary");
		assertMultipartBoundary("multipart/related", null, "Multipart senza boundary");
	}

	private static void assertMultipartBoundary(String ct, String expected, String description) throws UtilsException {
		String result = ContentTypeUtilities.readMultipartBoundaryFromContentType(ct);
		boolean match = (expected == null && result == null) || (expected != null && expected.equals(result));
		if (match) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       Input: '" + ct + "' - Atteso: '" + expected + "', Ottenuto: '" + result + "'";
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	// ==================== Test isMatch ====================

	public static void testIsMatch() throws UtilsException {
		System.out.println("--- Test isMatch ---\n");

		// Match esatto
		assertIsMatch("application/json", "application/json", true, "Match esatto");
		assertIsMatch("application/json; charset=UTF-8", "application/json", true, "Match base type con parametri");

		// Match con wildcard
		assertIsMatch("application/json", "application/*", true, "Wildcard subtype");
		assertIsMatch("application/json", "*/*", true, "Wildcard completo");
		assertIsMatch("application/soap+xml", "application/*+xml", true, "Wildcard con suffisso +xml");
		assertIsMatch("text/plain", "application/*", false, "Wildcard subtype non match");

		// Case insensitive
		assertIsMatch("APPLICATION/JSON", "application/json", true, "Case insensitive");
		assertIsMatch("application/json", "APPLICATION/JSON", true, "Case insensitive inverso");

		// Match con lista
		List<String> list1 = new ArrayList<>();
		list1.add("application/xml");
		list1.add("application/json");
		assertIsMatchList("application/json", list1, true, "Match in lista");

		List<String> list2 = new ArrayList<>();
		list2.add("application/xml");
		list2.add("text/plain");
		assertIsMatchList("application/json", list2, false, "No match in lista");

		// Lista con wildcard
		List<String> list3 = new ArrayList<>();
		list3.add("text/*");
		list3.add("application/json");
		assertIsMatchList("text/html", list3, true, "Match wildcard in lista");

		// Empty content type
		List<String> list4 = new ArrayList<>();
		list4.add("empty");
		assertIsMatchList(null, list4, true, "Match empty con null");
		assertIsMatchList("", list4, true, "Match empty con stringa vuota");

		// Lista vuota o null
		assertIsMatchList("application/json", new ArrayList<>(), true, "Lista vuota sempre true");
		assertIsMatchList("application/json", null, true, "Lista null sempre true");
	}

	private static void assertIsMatch(String contentType, String expected, boolean expectedResult, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMatch(null, contentType, expected);
		if (result == expectedResult) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       ContentType: '" + contentType + "', Expected: '" + expected + "' - Atteso: " + expectedResult + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}

	private static void assertIsMatchList(String contentType, List<String> expectedList, boolean expectedResult, String description) throws UtilsException {
		boolean result = ContentTypeUtilities.isMatch(null, contentType, expectedList);
		if (result == expectedResult) {
			System.out.println("[PASS] " + description);
		} else {
			String msg = "[FAIL] " + description + "\n       ContentType: '" + contentType + "', Lista: " + expectedList + " - Atteso: " + expectedResult + ", Ottenuto: " + result;
			System.out.println(msg);
			throw new UtilsException(msg);
		}
	}
}
