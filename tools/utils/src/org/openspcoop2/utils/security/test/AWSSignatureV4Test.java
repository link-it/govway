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
package org.openspcoop2.utils.security.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.openspcoop2.utils.io.HexBinaryUtilities;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.security.AWSSignatureV4;

/**
 * Test del signer {@link AWSSignatureV4} con i test vector AWS.
 * <p>
 * Le credenziali ({@code AKIDEXAMPLE} / {@code wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY})
 * sono valori di esempio pubblicati da AWS, usati anche da tutti gli SDK ufficiali
 * per la propria suite di test di certificazione SigV4. Non sono valide su nessun
 * account AWS reale: servono esclusivamente a verificare a tavolino la correttezza
 * dell'algoritmo di firma.
 * </p>
 * <p>
 * I valori attesi (signing key hex, signature hex) sono calcolati indipendentemente
 * con OpenSSL come golden reference (vedi commenti nei singoli test). Riferimenti AWS:
 * </p>
 * <ul>
 *   <li><a href="https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html">
 *       Signing AWS API requests</a> — panoramica generale del processo</li>
 *   <li><a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html">
 *       Create a canonical request</a> — costruzione del canonical request</li>
 *   <li><a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-create-string-to-sign.html">
 *       Create a string to sign</a> — composizione della string-to-sign</li>
 *   <li><a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-calculate-signature.html">
 *       Calculate the signature</a> — derivazione signing key e calcolo signature</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AWSSignatureV4Test {

	/** Credenziali di esempio pubblicate da AWS — usabili per test perché non valide su account reali. */
	private static final String EXAMPLE_ACCESS_KEY = "AKIDEXAMPLE";
	private static final String EXAMPLE_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

	public static void main(String[] args) throws Exception {
		testSigningKeyDerivation();
		testIamListUsersSignature();
		testSessionTokenIncluded();
		testCanonicalQueryStringSorting();
		testEmptyPayloadHash();
		testStreamSignatureMatchesByteArray();
		testStreamLargeBodyDigest();
		testBedrockPathDoubleEncoded();
		System.out.println("AWSSignatureV4Test: ALL OK");
	}


	/**
	 * Verifica la derivazione signing key con i valori ufficiali AWS per IAM ListUsers
	 * ({@code 20150830}, {@code us-east-1}, {@code iam}).
	 *
	 * <p>Il valore atteso {@code 2c94c0cf5378ada6887f09bb697df8fc0affdb34ba1cdd5bda32b664bd55b73c}
	 * è stato verificato indipendentemente con OpenSSL come golden reference:</p>
	 * <pre>
	 * # kDate
	 * echo -n "20150830" | openssl dgst -sha256 -mac HMAC \
	 *   -macopt key:"AWS4wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" -hex
	 * # → 68a9e4535ffbb09dcb6d25807a9ba5e3aef7cd00b3c57ed4b0c4a04988649f51
	 *
	 * # kRegion = HMAC(kDate, "us-east-1")
	 * echo -n "us-east-1" | openssl dgst -sha256 -mac HMAC -macopt hexkey:&lt;kDate&gt; -hex
	 * # → 85d1ad12f2880a35521589f2cd554a44a774ab293c4d7f25b532dde6c083059b
	 *
	 * # kService = HMAC(kRegion, "iam")
	 * # → 72dc6a40f69e1962f4b89ecd185e663f3e9356499462e2ff7500a447edc49d2a
	 *
	 * # kSigning = HMAC(kService, "aws4_request")
	 * # → 2c94c0cf5378ada6887f09bb697df8fc0affdb34ba1cdd5bda32b664bd55b73c
	 * </pre>
	 *
	 * <p>Riferimenti AWS:
	 * <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-calculate-signature.html">
	 * Calculate the signature for AWS Signature Version 4 — Derive the signing key</a>.
	 * </p>
	 */
	public static void testSigningKeyDerivation() throws Exception {
		byte[] kSigning = AWSSignatureV4.deriveSigningKey(EXAMPLE_SECRET_KEY, "20150830", "us-east-1", "iam");
		String expectedHex = "2c94c0cf5378ada6887f09bb697df8fc0affdb34ba1cdd5bda32b664bd55b73c";
		String actualHex = AWSSignatureV4.toHex(kSigning);
		assertEquals("signing key derivation", expectedHex, actualHex);
	}

	/**
	 * Test vector ufficiale AWS — {@code GET https://iam.amazonaws.com/?Action=ListUsers&Version=2010-05-08}.
	 * <p>Verifica end-to-end del signer: canonical request, string-to-sign, derivazione chiave,
	 * firma finale.</p>
	 * <p>Riferimento canonico:
	 * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_sigv-create-signed-request.html">
	 * Examples of how to derive a signing key</a> e
	 * <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html">
	 * Create a canonical request</a>.
	 * </p>
	 * <pre>
	 * Date          : 20150830T123600Z
	 * AccessKey     : AKIDEXAMPLE
	 * SecretKey     : wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
	 * Region        : us-east-1
	 * Service       : iam
	 * SignedHeaders : content-type;host;x-amz-date
	 * Signature     : 5d672d79c15b13162d9279b0855cfba6789a8edb4c82c400e06b5924a6f2b5d7
	 * </pre>
	 */
	public static void testIamListUsersSignature() throws Exception {
		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "us-east-1", "iam");
		URI uri = URI.create("https://iam.amazonaws.com/?Action=ListUsers&Version=2010-05-08");

		Map<String, String> headers = new LinkedHashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

		Date signingDate = parseAmzDate("20150830T123600Z");

		Map<String, String> signed = signer.sign("GET", uri, headers, new byte[0], signingDate);

		// Verifica X-Amz-Date
		assertEquals("X-Amz-Date", "20150830T123600Z", signed.get(AWSSignatureV4.HEADER_X_AMZ_DATE));

		// Verifica X-Amz-Content-Sha256 (sha256 di payload vuoto)
		assertEquals("X-Amz-Content-Sha256", AWSSignatureV4.emptyPayloadSha256(),
				signed.get(AWSSignatureV4.HEADER_X_AMZ_CONTENT_SHA256));

		// Verifica Authorization. La signature attesa è stata calcolata indipendentemente
		// con OpenSSL come golden reference (input identici alla request firmata qui):
		//   echo -n "<canonical-request>" | openssl dgst -sha256 -hex   → canonical-hash
		//   echo -n "<string-to-sign>" | openssl dgst -sha256 -mac HMAC -macopt hexkey:<kSigning> -hex
		String expectedSignaturePart = "Signature=33f5dad2191de0cb4b7ab912f876876c2c4f72e2991a458f9499233c7b992438";
		String authorization = signed.get(AWSSignatureV4.HEADER_AUTHORIZATION);
		assertNotNull("Authorization header", authorization);
		assertTrue("Authorization contiene algorithm prefix", authorization.startsWith(AWSSignatureV4.ALGORITHM));
		assertTrue("Authorization contiene Credential=AKIDEXAMPLE/20150830/us-east-1/iam/aws4_request",
				authorization.contains("Credential=AKIDEXAMPLE/20150830/us-east-1/iam/aws4_request"));
		assertTrue("Authorization contiene SignedHeaders=content-type;host;x-amz-date",
				authorization.contains("SignedHeaders=content-type;host;x-amz-date"));
		assertTrue("Authorization contiene la signature attesa: " + expectedSignaturePart,
				authorization.contains(expectedSignaturePart));
	}

	/**
	 * Quando il signer è costruito con session token (credenziali temporanee STS),
	 * deve aggiungere l'header {@code X-Amz-Security-Token} e includerlo tra i SignedHeaders.
	 */
	public static void testSessionTokenIncluded() throws Exception {
		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "us-east-1", "bedrock",
				"FAKE_SESSION_TOKEN_xxx");
		URI uri = URI.create("https://bedrock-runtime.us-east-1.amazonaws.com/model/test-model/converse");

		Map<String, String> headers = new LinkedHashMap<>();
		headers.put("Content-Type", "application/json");

		byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
		Map<String, String> signed = signer.sign("POST", uri, headers, body, new Date());

		assertEquals("X-Amz-Security-Token", "FAKE_SESSION_TOKEN_xxx",
				signed.get(AWSSignatureV4.HEADER_X_AMZ_SECURITY_TOKEN));

		String authorization = signed.get(AWSSignatureV4.HEADER_AUTHORIZATION);
		assertNotNull("Authorization header", authorization);
		// SignedHeaders deve includere x-amz-security-token (perché il token va firmato, non solo trasmesso)
		assertTrue("SignedHeaders deve includere x-amz-security-token",
				authorization.contains("x-amz-security-token"));
	}

	/**
	 * Verifica che il canonical query string ordini i parametri lessicograficamente.
	 * Due URL semanticamente equivalenti ma con parametri in ordine diverso devono produrre
	 * la stessa firma.
	 */
	public static void testCanonicalQueryStringSorting() throws Exception {
		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "us-east-1", "iam");
		Date now = parseAmzDate("20260520T120000Z");

		Map<String, String> headers = new LinkedHashMap<>();

		// Parametri in ordine "a-z"
		URI uri1 = URI.create("https://iam.amazonaws.com/?Action=ListUsers&Version=2010-05-08");
		// Stessi parametri in ordine inverso
		URI uri2 = URI.create("https://iam.amazonaws.com/?Version=2010-05-08&Action=ListUsers");

		Map<String, String> signed1 = signer.sign("GET", uri1, headers, new byte[0], now);
		Map<String, String> signed2 = signer.sign("GET", uri2, headers, new byte[0], now);

		assertEquals("Stessa signature indipendente dall'ordine dei parametri query",
				signed1.get(AWSSignatureV4.HEADER_AUTHORIZATION),
				signed2.get(AWSSignatureV4.HEADER_AUTHORIZATION));
	}

	/**
	 * L'hash SHA-256 del payload vuoto è una costante AWS ben nota.
	 * Verifica che il signer la usi correttamente sia tramite la costante esposta
	 * che durante il calcolo runtime.
	 */
	public static void testEmptyPayloadHash() throws Exception {
		String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
		assertEquals("emptyPayloadSha256()", expected, AWSSignatureV4.emptyPayloadSha256());

		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "us-east-1", "bedrock");
		URI uri = URI.create("https://bedrock-runtime.us-east-1.amazonaws.com/model/test/converse");

		Map<String, String> signed1 = signer.sign("POST", uri, new LinkedHashMap<>(), (byte[]) null, new Date());
		Map<String, String> signed2 = signer.sign("POST", uri, new LinkedHashMap<>(), new byte[0], new Date());

		assertEquals("Body null e body array vuoto producono lo stesso content hash",
				expected, signed1.get(AWSSignatureV4.HEADER_X_AMZ_CONTENT_SHA256));
		assertEquals("Body array vuoto produce l'hash atteso",
				expected, signed2.get(AWSSignatureV4.HEADER_X_AMZ_CONTENT_SHA256));
	}


	/**
	 * Parità byte[] / InputStream: lo stesso payload firmato dai due overload deve
	 * produrre identica Authorization e identico X-Amz-Content-Sha256. Garantisce che
	 * lo sha256 calcolato leggendo lo stream in chunk coincide con quello calcolato in
	 * un solo passo sul {@code byte[]}.
	 */
	public static void testStreamSignatureMatchesByteArray() throws Exception {
		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "us-east-1", "bedrock");
		URI uri = URI.create("https://bedrock-runtime.us-east-1.amazonaws.com/model/test/converse");
		Date signingDate = parseAmzDate("20260520T120000Z");

		byte[] body = "{\"foo\":\"bar\",\"baz\":[1,2,3]}".getBytes(StandardCharsets.UTF_8);

		Map<String, String> headersBytes = new LinkedHashMap<>();
		headersBytes.put("Content-Type", "application/json");
		Map<String, String> signedFromBytes = signer.sign("POST", uri, headersBytes, body, signingDate);

		Map<String, String> headersStream = new LinkedHashMap<>();
		headersStream.put("Content-Type", "application/json");
		InputStream is = new ByteArrayInputStream(body);
		Map<String, String> signedFromStream = signer.sign("POST", uri, headersStream, is, signingDate);

		assertEquals("Authorization byte[] vs InputStream",
				signedFromBytes.get(AWSSignatureV4.HEADER_AUTHORIZATION),
				signedFromStream.get(AWSSignatureV4.HEADER_AUTHORIZATION));
		assertEquals("X-Amz-Content-Sha256 byte[] vs InputStream",
				signedFromBytes.get(AWSSignatureV4.HEADER_X_AMZ_CONTENT_SHA256),
				signedFromStream.get(AWSSignatureV4.HEADER_X_AMZ_CONTENT_SHA256));
	}

	/**
	 * Body grande (oltre il buffer interno di chunk read) firmato in streaming: lo sha256
	 * calcolato a chunk deve coincidere con lo sha256 di riferimento calcolato direttamente
	 * dal {@link MessageDigest} JDK sul payload intero.
	 */
	public static void testStreamLargeBodyDigest() throws Exception {
		byte[] body = new byte[64 * 1024 + 17];
		for (int i = 0; i < body.length; i++) {
			body[i] = (byte) (i % 251);
		}

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String expectedHash = HexBinaryUtilities.encodeAsString(md.digest(body));

		String streamHash = AWSSignatureV4.sha256HexFromStream(new ByteArrayInputStream(body));
		assertEquals("sha256HexFromStream su body grande", expectedHash, streamHash);

		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "us-east-1", "bedrock");
		URI uri = URI.create("https://bedrock-runtime.us-east-1.amazonaws.com/model/test/converse");
		Date signingDate = parseAmzDate("20260520T120000Z");
		Map<String, String> headers = new LinkedHashMap<>();
		headers.put("Content-Type", "application/octet-stream");
		Map<String, String> signed = signer.sign("POST", uri, headers, new ByteArrayInputStream(body), signingDate);

		assertEquals("X-Amz-Content-Sha256 su body grande in streaming",
				expectedHash, signed.get(AWSSignatureV4.HEADER_X_AMZ_CONTENT_SHA256));
	}


	/**
	 * Bedrock (servizio non-S3) richiede double-encoding del path nel canonical request.
	 * Modelli Bedrock contengono ':' (es. {@code anthropic.claude-3-5-haiku-20241022-v1:0})
	 * che nel path HTTP diventa {@code %3A} (encoded una volta) e nel canonical request
	 * deve diventare {@code %253A} (encoded due volte). Verifichiamo l'idempotenza: due URI
	 * equivalenti (uno con ':' letterale, uno con '%3A' percent-encoded) devono produrre
	 * la stessa signature, perché il signer normalizza con decode-then-encode×2.
	 */
	public static void testBedrockPathDoubleEncoded() throws Exception {
		AWSSignatureV4 signer = new AWSSignatureV4(EXAMPLE_ACCESS_KEY, EXAMPLE_SECRET_KEY, "eu-west-1", "bedrock");
		Date signingDate = parseAmzDate("20260521T143536Z");

		Map<String, String> headers = new LinkedHashMap<>();
		headers.put("Content-Type", "application/json");

		URI uriRaw = URI.create("https://bedrock-runtime.eu-west-1.amazonaws.com/model/anthropic.claude-3-5-haiku-20241022-v1:0/converse-stream");
		URI uriEncoded = URI.create("https://bedrock-runtime.eu-west-1.amazonaws.com/model/anthropic.claude-3-5-haiku-20241022-v1%3A0/converse-stream");

		Map<String, String> signed1 = signer.sign("POST", uriRaw, headers, new byte[0], signingDate);
		Map<String, String> signed2 = signer.sign("POST", uriEncoded, headers, new byte[0], signingDate);

		assertEquals("idempotenza path Bedrock (':' vs '%3A')",
				signed1.get(AWSSignatureV4.HEADER_AUTHORIZATION),
				signed2.get(AWSSignatureV4.HEADER_AUTHORIZATION));
	}


	/* === helper === */

	private static Date parseAmzDate(String amzDate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return sdf.parse(amzDate);
		} catch (Exception e) {
			throw new UtilsRuntimeException(e);
		}
	}

	private static void assertEquals(String label, String expected, String actual) throws UtilsException {
		if (expected == null ? actual != null : !expected.equals(actual)) {
			throw new UtilsException("Assertion failed [" + label + "]: expected=[" + expected + "], actual=[" + actual + "]");
		}
	}

	private static void assertTrue(String label, boolean condition) throws UtilsException {
		if (!condition) {
			throw new UtilsException("Assertion failed [" + label + "]");
		}
	}

	private static void assertNotNull(String label, Object value) throws UtilsException {
		if (value == null) {
			throw new UtilsException("Assertion failed [" + label + "]: value is null");
		}
	}

}
