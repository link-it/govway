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
package org.openspcoop2.utils.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.digest.DigestType;
import org.openspcoop2.utils.digest.DigestUtils;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * Calcola la firma <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html">AWS Signature Version 4</a>
 * per una HTTP request, restituendo gli header da aggiungere alla richiesta:
 * <ul>
 *   <li>{@code Authorization: AWS4-HMAC-SHA256 Credential=..., SignedHeaders=..., Signature=...}</li>
 *   <li>{@code X-Amz-Date}: timestamp ISO8601 compatto usato in firma (obbligatorio)</li>
 *   <li>{@code X-Amz-Content-Sha256}: SHA-256 esadecimale del payload (raccomandato per servizi non-S3
 *       come Bedrock/STS/Lambda/SageMaker)</li>
 *   <li>{@code X-Amz-Security-Token}: solo se la firma usa credenziali temporanee (STS, IAM Role)</li>
 * </ul>
 *
 * <p>Signer puro: usa {@code javax.crypto.Mac} (HMAC-SHA256) della JDK standard e le utility
 * GovWay {@link org.openspcoop2.utils.digest.DigestUtils} (SHA-256) e
 * {@link org.openspcoop2.utils.io.HexBinaryUtilities} (encoding hex). Nessuna dipendenza dall'AWS SDK.
 * Riutilizzabile per qualsiasi servizio AWS o endpoint AWS-compatible (S3, Bedrock, Lambda,
 * SageMaker, MinIO, Cloudflare R2, ecc.) cambiando solo il parametro {@code service}.</p>
 *
 * <p>Istanza thread-safe: lo state è immutabile, i metodi statici di calcolo non condividono dati
 * tra invocazioni. Sicuro come singleton riutilizzato per più request.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AWSSignatureV4 {

	/** Nome dell'algoritmo nella signature line dell'header Authorization. */
	public static final String ALGORITHM = "AWS4-HMAC-SHA256";

	/** Nome dell'algoritmo HMAC usato in ogni step della derivazione. */
	private static final String HMAC_ALG = "HmacSHA256";

	/** Suffisso del credential scope previsto da SigV4. */
	private static final String REQUEST_TYPE_TERMINATOR = "aws4_request";

	/** Header name conforme alla spec (camelCase nel valore non importa, gli header HTTP sono case-insensitive). */
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_X_AMZ_DATE = "X-Amz-Date";
	public static final String HEADER_X_AMZ_CONTENT_SHA256 = "X-Amz-Content-Sha256";
	public static final String HEADER_X_AMZ_SECURITY_TOKEN = "X-Amz-Security-Token";

	/** Versioni lowercase dei nomi header per la canonical request (precalcolate una volta sola). */
	private static final String HEADER_AUTHORIZATION_LC = HEADER_AUTHORIZATION.toLowerCase(Locale.ROOT);
	private static final String HEADER_X_AMZ_DATE_LC = HEADER_X_AMZ_DATE.toLowerCase(Locale.ROOT);
	private static final String HEADER_X_AMZ_CONTENT_SHA256_LC = HEADER_X_AMZ_CONTENT_SHA256.toLowerCase(Locale.ROOT);
	private static final String HEADER_X_AMZ_SECURITY_TOKEN_LC = HEADER_X_AMZ_SECURITY_TOKEN.toLowerCase(Locale.ROOT);

	/** Header obbligatorio host nella canonical request (lowercase per la spec SigV4). */
	private static final String HEADER_HOST_LC = "host";

	/** Suffisso e prefisso usati per costruire l'header Authorization SigV4. */
	private static final String AUTH_PREFIX_SECRET = "AWS4";
	private static final String AUTH_FIELD_CREDENTIAL = "Credential=";
	private static final String AUTH_FIELD_SIGNED_HEADERS = "SignedHeaders=";
	private static final String AUTH_FIELD_SIGNATURE = "Signature=";
	private static final String AUTH_FIELD_SEPARATOR = ", ";

	/** Schema/porte default HTTP/HTTPS. */
	private static final String SCHEME_HTTPS = "https";
	private static final String SCHEME_HTTP = "http";
	private static final int PORT_HTTPS_DEFAULT = 443;
	private static final int PORT_HTTP_DEFAULT = 80;

	/** Hash SHA-256 del payload vuoto (precalcolato — costante AWS). */
	private static final String EMPTY_PAYLOAD_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

	/** Charset UTF-8 (la spec SigV4 usa sempre UTF-8 per il canonical request e la string-to-sign). */
	private static final Charset UTF_8 = Charset.forName(org.openspcoop2.utils.resources.Charset.UTF_8.getValue());

	/** Formati di data riusati per ogni firma (cloni perché SimpleDateFormat non è thread-safe). */
	private static final String DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	/** Credenziali e contesto immutabili del signer. */
	private final String accessKeyId;
	private final String secretAccessKey;
	private final String region;
	private final String service;
	private final String sessionToken;

	/**
	 * Crea un signer per credenziali permanenti (IAM User access key statico).
	 *
	 * @param accessKeyId     Access Key ID (formato {@code AKIA*} per chiavi permanenti)
	 * @param secretAccessKey Secret Access Key (mai inviato in chiaro: è la chiave HMAC)
	 * @param region          regione AWS (es. {@code eu-central-1})
	 * @param service         nome servizio AWS (es. {@code bedrock}, {@code sts}, {@code s3})
	 */
	public AWSSignatureV4(String accessKeyId, String secretAccessKey, String region, String service) {
		this(accessKeyId, secretAccessKey, region, service, null);
	}

	/**
	 * Crea un signer; supporta sia credenziali permanenti sia temporanee.
	 *
	 * @param accessKeyId     Access Key ID ({@code AKIA*} permanente o {@code ASIA*} temporaneo)
	 * @param secretAccessKey Secret Access Key
	 * @param region          regione AWS
	 * @param service         nome servizio AWS
	 * @param sessionToken    session token STS (null se credenziali permanenti)
	 */
	public AWSSignatureV4(String accessKeyId, String secretAccessKey, String region, String service, String sessionToken) {
		if (accessKeyId == null || accessKeyId.isEmpty()) {
			throw new IllegalArgumentException("accessKeyId nullo o vuoto");
		}
		if (secretAccessKey == null || secretAccessKey.isEmpty()) {
			throw new IllegalArgumentException("secretAccessKey nullo o vuoto");
		}
		if (region == null || region.isEmpty()) {
			throw new IllegalArgumentException("region nulla o vuota");
		}
		if (service == null || service.isEmpty()) {
			throw new IllegalArgumentException("service nullo o vuoto");
		}
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.region = region;
		this.service = service;
		this.sessionToken = (sessionToken != null && !sessionToken.isEmpty()) ? sessionToken : null;
	}

	/**
	 * Calcola gli header SigV4 per una HTTP request al timestamp corrente.
	 *
	 * @param httpMethod      metodo HTTP (es. {@code GET}, {@code POST})
	 * @param requestUri      URI della request (host + path + query); host serve per il canonical header,
	 *                        path per la canonical URI, query per la canonical query string
	 * @param requestHeaders  header HTTP correnti della request (di solito {@code Content-Type} e altri);
	 *                        il signer li include tra i {@code SignedHeaders} oltre a {@code host},
	 *                        {@code x-amz-date} e {@code x-amz-content-sha256}
	 * @param body            payload della request (può essere null o array vuoto per request senza body)
	 * @return Map (ordinata, insertion-order) degli header da aggiungere/sovrascrivere sulla request
	 *         prima del send.
	 */
	public Map<String, String> sign(String httpMethod, URI requestUri, Map<String, String> requestHeaders, byte[] body) throws UtilsException {
		return sign(httpMethod, requestUri, requestHeaders, body, new Date());
	}

	/**
	 * Variante che accetta il body come {@link InputStream}: utile quando il payload è già stato
	 * serializzato dal connettore (es. su filesystem in NIO streaming) e potrebbe essere troppo
	 * grande per essere caricato interamente in memoria. Lo sha256 del payload viene calcolato
	 * leggendo lo stream una sola volta in chunk.
	 * <p>
	 * Lo stream viene consumato completamente da questo metodo. Il chiamante deve aprire uno
	 * stream separato per inviare il body al server (es. da {@code DumpByteArrayOutputStream.getInputStream()}
	 * che restituisce sempre un nuovo InputStream).
	 * </p>
	 */
	public Map<String, String> sign(String httpMethod, URI requestUri, Map<String, String> requestHeaders, InputStream body) throws UtilsException {
		return sign(httpMethod, requestUri, requestHeaders, body, new Date());
	}

	/**
	 * Variante che permette di specificare il timestamp di firma — utile per test deterministici
	 * con i test vector AWS, sconsigliata in produzione (usare sempre {@code now}).
	 */
	public Map<String, String> sign(String httpMethod, URI requestUri, Map<String, String> requestHeaders, byte[] body, Date signingDate) throws UtilsException {
		InputStream is = body != null ? new ByteArrayInputStream(body) : new ByteArrayInputStream(new byte[0]);
		return sign(httpMethod, requestUri, requestHeaders, is, signingDate);
	}

	/**
	 * Variante completa con timestamp esplicito e body come stream.
	 */
	public Map<String, String> sign(String httpMethod, URI requestUri, Map<String, String> requestHeaders, InputStream body, Date signingDate) throws UtilsException {
		if (httpMethod == null || httpMethod.isEmpty()) {
			throw new UtilsException("httpMethod nullo o vuoto");
		}
		if (requestUri == null) {
			throw new UtilsException("requestUri nullo");
		}
		if (signingDate == null) {
			throw new UtilsException("signingDate nullo");
		}
		try {
			String amzDate = formatAmzDate(signingDate);
			String dateStamp = formatDate(signingDate);
			String payloadHash = body != null ? sha256HexFromStream(body) : EMPTY_PAYLOAD_SHA256;

			Map<String, String> headersForCanonical = collectHeadersForCanonical(requestUri, requestHeaders, amzDate);

			String canonicalRequest = buildCanonicalRequest(httpMethod, requestUri, headersForCanonical, payloadHash);
			String signedHeadersList = buildSignedHeadersList(headersForCanonical);

			String credentialScope = dateStamp + "/" + this.region + "/" + this.service + "/" + REQUEST_TYPE_TERMINATOR;
			String stringToSign = ALGORITHM + "\n" +
					amzDate + "\n" +
					credentialScope + "\n" +
					sha256Hex(canonicalRequest.getBytes(UTF_8));

			byte[] signingKey = deriveSigningKey(this.secretAccessKey, dateStamp, this.region, this.service);
			String signature = toHex(hmacSha256(signingKey, stringToSign.getBytes(UTF_8)));

			String authorization = ALGORITHM + " " +
					AUTH_FIELD_CREDENTIAL + this.accessKeyId + "/" + credentialScope + AUTH_FIELD_SEPARATOR +
					AUTH_FIELD_SIGNED_HEADERS + signedHeadersList + AUTH_FIELD_SEPARATOR +
					AUTH_FIELD_SIGNATURE + signature;

			Map<String, String> out = new LinkedHashMap<>();
			out.put(HEADER_AUTHORIZATION, authorization);
			out.put(HEADER_X_AMZ_DATE, amzDate);
			out.put(HEADER_X_AMZ_CONTENT_SHA256, payloadHash);
			if (this.sessionToken != null) {
				out.put(HEADER_X_AMZ_SECURITY_TOKEN, this.sessionToken);
			}
			return out;
		} catch (UtilsException e) {
			throw e;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new UtilsException("Errore nel calcolo della firma AWS SigV4: " + e.getMessage(), e);
		}
	}


	/* ============================================================================
	 * Canonical request building
	 * ============================================================================ */

	/**
	 * Raccoglie e normalizza gli header da firmare. Aggiunge sempre {@code host},
	 * {@code x-amz-date}, {@code x-amz-content-sha256} ai signed headers; include
	 * anche {@code x-amz-security-token} se presente (perché va firmato per evitare
	 * tampering del token in transito). Header con value lowercase = NO (solo il
	 * nome va lowercase; il value va trim ma case-preserved).
	 */
	private Map<String, String> collectHeadersForCanonical(URI uri, Map<String, String> userHeaders, String amzDate) {
		// TreeMap per ordine lessicografico lowercase
		Map<String, String> map = new TreeMap<>();

		if (userHeaders != null) {
			for (Map.Entry<String, String> e : userHeaders.entrySet()) {
				String normalized = normalizeHeaderName(e.getKey());
				if (normalized != null && e.getValue() != null && !isReservedHeader(normalized)) {
					map.put(normalized, collapseSpaces(e.getValue()));
				}
			}
		}
		// Header firmati per la canonical request: host, x-amz-date sempre; x-amz-security-token solo se temporanee.
		// NOTA: X-Amz-Content-Sha256 viene aggiunto agli header HTTP da inviare al server ma NON viene firmato
		// (allineato al comportamento AWS SDK v2 per servizi non-S3 come Bedrock/STS/IAM/Lambda; S3 ha regole proprie
		// e richiederebbe un flag aggiuntivo per essere incluso anche nei SignedHeaders).
		map.put(HEADER_HOST_LC, buildHostHeader(uri));
		map.put(HEADER_X_AMZ_DATE_LC, amzDate);
		if (this.sessionToken != null) {
			map.put(HEADER_X_AMZ_SECURITY_TOKEN_LC, this.sessionToken);
		}
		return map;
	}

	/** Lowercase + trim del nome header. Ritorna null se il nome è null/vuoto. */
	private String normalizeHeaderName(String rawName) {
		if (rawName == null) {
			return null;
		}
		String n = rawName.toLowerCase(Locale.ROOT).trim();
		return n.isEmpty() ? null : n;
	}

	/** Header gestiti dal signer e che vanno ignorati se passati nell'input dell'utente. */
	private boolean isReservedHeader(String lowercaseName) {
		return HEADER_AUTHORIZATION_LC.equals(lowercaseName)
				|| HEADER_X_AMZ_DATE_LC.equals(lowercaseName)
				|| HEADER_X_AMZ_CONTENT_SHA256_LC.equals(lowercaseName)
				|| HEADER_X_AMZ_SECURITY_TOKEN_LC.equals(lowercaseName);
	}

	private String buildHostHeader(URI uri) {
		String host = uri.getHost();
		int port = uri.getPort();
		if (port < 0) {
			return host;
		}
		String scheme = uri.getScheme();
		// Default port HTTPS/HTTP non va incluso nel canonical host
		if ((SCHEME_HTTPS.equalsIgnoreCase(scheme) && port == PORT_HTTPS_DEFAULT)
				|| (SCHEME_HTTP.equalsIgnoreCase(scheme) && port == PORT_HTTP_DEFAULT)) {
			return host;
		}
		return host + ":" + port;
	}

	private String buildCanonicalRequest(String httpMethod, URI uri, Map<String, String> headers, String payloadHash) {
		StringBuilder sb = new StringBuilder();
		sb.append(httpMethod.toUpperCase(Locale.ROOT)).append('\n');
		sb.append(canonicalUri(uri.getRawPath())).append('\n');
		sb.append(canonicalQueryString(uri.getRawQuery())).append('\n');
		for (Map.Entry<String, String> h : headers.entrySet()) {
			sb.append(h.getKey()).append(':').append(h.getValue()).append('\n');
		}
		sb.append('\n');
		sb.append(buildSignedHeadersList(headers)).append('\n');
		sb.append(payloadHash);
		return sb.toString();
	}

	private String buildSignedHeadersList(Map<String, String> headers) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String name : headers.keySet()) {
			if (!first) {
				sb.append(';');
			}
			sb.append(name);
			first = false;
		}
		return sb.toString();
	}

	/**
	 * Canonical URI per SigV4. Per i servizi non-S3 (Bedrock, STS, IAM, Lambda, ...) la spec
	 * richiede che ogni segmento del path sia URL-encoded <strong>due volte</strong> nel
	 * canonical request: il path HTTP inviato resta encoded una sola volta, AWS riencoda
	 * lato server prima di ricalcolare la firma. Esempio Bedrock:
	 * <pre>
	 * HTTP path:  /model/anthropic.claude-...-v1%3A0/converse-stream
	 * Canonical:  /model/anthropic.claude-...-v1%253A0/converse-stream
	 * </pre>
	 * <p>
	 * S3 è l'unica eccezione documentata (single-encoding) e non è coperto da questa
	 * implementazione: se in futuro servirà supportarlo, distinguere via flag al costruttore.
	 * </p>
	 * <p>
	 * Implementazione idempotente (decode-then-encode×2): se il chiamante passa il path con
	 * segmenti già percent-encoded (es. modelId Bedrock con {@code :} → {@code %3A}), il
	 * decode iniziale lo normalizza prima di applicare l'encoding doppio.
	 * </p>
	 */
	private String canonicalUri(String rawPath) {
		if (rawPath == null || rawPath.isEmpty()) {
			return "/";
		}
		String[] segments = rawPath.split("/", -1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				sb.append('/');
			}
			String segment = segments[i];
			if (!segment.isEmpty()) {
				String decoded = TransportUtils.urlDecodePath(segment, UTF_8.name());
				String onceEncoded = TransportUtils.urlEncodePath(decoded, UTF_8.name());
				String twiceEncoded = TransportUtils.urlEncodePath(onceEncoded, UTF_8.name());
				sb.append(twiceEncoded);
			}
		}
		return sb.toString();
	}

	/**
	 * Canonical query string: parametri ordinati per nome (lessicograficamente), value escaped, e
	 * concatenati con {@code &}. Se la query è vuota, ritorna stringa vuota.
	 */
	private String canonicalQueryString(String rawQuery) {
		if (rawQuery == null || rawQuery.isEmpty()) {
			return "";
		}
		List<String[]> params = new ArrayList<>();
		for (String pair : rawQuery.split("&")) {
			int eq = pair.indexOf('=');
			String key = eq < 0 ? pair : pair.substring(0, eq);
			String value = eq < 0 ? "" : pair.substring(eq + 1);
			params.add(new String[]{key, value});
		}
		params.sort((a, b) -> {
			int c = a[0].compareTo(b[0]);
			return c != 0 ? c : a[1].compareTo(b[1]);
		});
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String[] p : params) {
			if (!first) {
				sb.append('&');
			}
			sb.append(p[0]).append('=').append(p[1]);
			first = false;
		}
		return sb.toString();
	}

	/** Collassa whitespace multipli in singolo spazio e trim — come da spec SigV4 sui valori header. */
	private String collapseSpaces(String value) {
		return value.trim().replaceAll("\\s+", " ");
	}


	/* ============================================================================
	 * Crypto primitives
	 * ============================================================================ */

	/**
	 * Deriva la signing key con 4 HMAC-SHA256 in catena: il secret viene "speso" solo nel primo
	 * step (prefisso {@code AWS4}); il risultato finale è valido per (data, regione, servizio).
	 * Cacheabile per giorno+regione+servizio per ridurre il costo crypto, ma per richieste poco
	 * frequenti non è necessario.
	 */
	public static byte[] deriveSigningKey(String secretAccessKey, String dateStamp, String region, String service) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] kSecret = (AUTH_PREFIX_SECRET + secretAccessKey).getBytes(UTF_8);
		byte[] kDate = hmacSha256(kSecret, dateStamp.getBytes(UTF_8));
		byte[] kRegion = hmacSha256(kDate, region.getBytes(UTF_8));
		byte[] kService = hmacSha256(kRegion, service.getBytes(UTF_8));
		return hmacSha256(kService, REQUEST_TYPE_TERMINATOR.getBytes(UTF_8));
	}

	public static byte[] hmacSha256(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(HMAC_ALG);
		mac.init(new SecretKeySpec(key, HMAC_ALG));
		return mac.doFinal(data);
	}

	/**
	 * SHA-256 in formato esadecimale lowercase, come richiesto da SigV4 nella canonical request.
	 * Delega alle utility GovWay {@code DigestUtils} + {@code HexBinaryUtilities}.
	 */
	public static String sha256Hex(byte[] data) throws UtilsException {
		return DigestUtils.getDigestValue(data, DigestType.SHA256.getAlgorithmName(), DigestEncoding.HEX);
	}

	/**
	 * SHA-256 in formato esadecimale lowercase calcolato leggendo lo stream in chunk —
	 * utile per body troppo grandi per la memoria (es. file serializzati su filesystem
	 * dal connettore NIO). Lo stream viene consumato completamente e chiuso.
	 */
	public static String sha256HexFromStream(InputStream data) throws UtilsException {
		try {
			MessageDigest md = MessageDigest.getInstance(DigestType.SHA256.getAlgorithmName());
			byte[] buf = new byte[8192];
			int n;
			while ((n = data.read(buf)) > 0) {
				md.update(buf, 0, n);
			}
			return HexBinaryUtilities.encodeAsString(md.digest());
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new UtilsException("Errore nel calcolo dello SHA-256 da stream: " + e.getMessage(), e);
		} finally {
			try {
				data.close();
			} catch (IOException ignore) {
				// stream chiusura best-effort
			}
		}
	}

	/**
	 * Converte un array di byte in stringa esadecimale lowercase (delega {@code HexBinaryUtilities}).
	 */
	public static String toHex(byte[] bytes) throws UtilsException {
		return HexBinaryUtilities.encodeAsString(bytes);
	}


	/* ============================================================================
	 * Date utilities
	 * ============================================================================ */

	public static String formatAmzDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		sdf.setTimeZone(UTC);
		return sdf.format(d);
	}

	public static String formatDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(UTC);
		return sdf.format(d);
	}

	/** Espone l'hash del payload vuoto come costante riusabile. */
	public static String emptyPayloadSha256() {
		return EMPTY_PAYLOAD_SHA256;
	}

}
