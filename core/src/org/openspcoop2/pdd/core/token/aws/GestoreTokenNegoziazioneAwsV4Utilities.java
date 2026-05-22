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
package org.openspcoop2.pdd.core.token.aws;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken_DatiRichiesta;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.security.AWSSignatureV4;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper riusabili per le Token Policy di tipo {@code awsv4}.
 * <p>
 * Per la modalità <b>Statica</b> espone {@link #negotiateStatic(PolicyNegoziazioneToken,
 * InformazioniNegoziazioneToken_DatiRichiesta)}: niente chiamata HTTP, le credenziali
 * sono direttamente quelle della policy. {@code expiresIn=null} → mai rinegoziata dalla
 * cache.
 * </p>
 * <p>
 * Per la modalità <b>AssumeRole</b> il flow HTTP riusa l'infrastruttura di
 * {@code GestoreTokenNegoziazioneUtilities} (connettore GovWay con proxy/TLS/timeout
 * dalla policy). Questa classe espone solo gli helper specifici AWS:
 * </p>
 * <ul>
 *   <li>{@link #buildAssumeRoleFormBody(PolicyNegoziazioneToken)} —
 *       costruisce il body {@code application/x-www-form-urlencoded} per
 *       {@code Action=AssumeRole}.</li>
 *   <li>{@link #signAssumeRoleHeaders(PolicyNegoziazioneToken, String, byte[], String)}
 *       — calcola gli header SigV4 ({@code Authorization}, {@code X-Amz-Date},
 *       {@code X-Amz-Content-Sha256}) per la POST a STS.</li>
 *   <li>{@link #buildInformazioniFromStsResponse(InformazioniNegoziazioneToken_DatiRichiesta,
 *       Integer, byte[], PolicyNegoziazioneToken)} — parsa la response XML,
 *       costruisce {@link AWSCredentialBag} con la tripla temporanea, popola
 *       {@code expiresIn} con l'{@code Expiration} STS.</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class GestoreTokenNegoziazioneAwsV4Utilities {

	/* === Costanti interne al package AWS: non esposte al flow base === */

	/** Versione dell'API STS Query Protocol. Stabile dal 2011. */
	private static final String STS_API_VERSION = "2011-06-15";

	/** Service identifier per la firma SigV4 verso STS. */
	private static final String STS_SERVICE = "sts";

	/* Parametri form del POST STS AssumeRole. */
	private static final String FORM_ACTION = "Action";
	private static final String FORM_ACTION_VALUE = "AssumeRole";
	private static final String FORM_VERSION = "Version";
	private static final String FORM_ROLE_ARN = "RoleArn";
	private static final String FORM_ROLE_SESSION_NAME = "RoleSessionName";
	private static final String FORM_DURATION_SECONDS = "DurationSeconds";
	private static final String FORM_EXTERNAL_ID = "ExternalId";

	/* Element names della response AssumeRoleResponse XML. */
	private static final String XML_NS_STS_2011 = "https://sts.amazonaws.com/doc/2011-06-15/";
	private static final String XML_CREDENTIALS = "Credentials";
	private static final String XML_ACCESS_KEY_ID = "AccessKeyId";
	private static final String XML_SECRET_ACCESS_KEY = "SecretAccessKey";
	private static final String XML_SESSION_TOKEN = "SessionToken";
	private static final String XML_EXPIRATION = "Expiration";
	private static final String XML_ERROR = "Error";
	private static final String XML_ERROR_CODE = "Code";
	private static final String XML_ERROR_MESSAGE = "Message";

	private GestoreTokenNegoziazioneAwsV4Utilities() {
		// utility class
	}


	/* === Modalità statica === */

	/**
	 * Costruisce un {@link InformazioniNegoziazioneToken} con la {@link AWSCredentialBag}
	 * popolata dai campi della policy. Nessuna chiamata di rete; {@code expiresIn=null}
	 * significa che la cache standard NON pianifica rinegoziazioni (le credenziali
	 * permanenti non scadono).
	 */
	public static InformazioniNegoziazioneToken negotiateStatic(PolicyNegoziazioneToken policy,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) throws UtilsException {

		String accessKeyId = policy.getAwsAccessKeyId();
		String secretAccessKey = policy.getAwsSecretAccessKey();
		String region = policy.getAwsRegion();
		if (isEmpty(accessKeyId) || isEmpty(secretAccessKey) || isEmpty(region)) {
			throw new UtilsException("Configurazione AWS Signature V4 (modalità statica) incompleta: "
					+ "richiesti accessKeyId, secretAccessKey, region");
		}
		AWSCredentialBag bag = new AWSCredentialBag(accessKeyId, secretAccessKey, region, policy.getAwsService(), null);

		InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken();
		info.setRequest(datiRichiesta);
		info.setBackendCredentialBag(bag);
		info.setValid(true);
		info.setRetrievedIn(DateManager.getDate());
		// expiresIn intenzionalmente null: credenziali permanenti, mai scadono dal punto di vista cache
		return info;
	}


	/* === Helper per modalità AssumeRole — usati dal flow OAuth in GestoreTokenNegoziazioneUtilities === */

	/**
	 * Popola la mappa dei parametri form della request STS AssumeRole. Il flow OAuth
	 * standard si occupa poi di serializzarli come {@code application/x-www-form-urlencoded}
	 * (URL-encoding applicato dal {@link TransportUtils#buildUrlWithParameters} downstream).
	 * <p>
	 * Validation dei campi obbligatori (roleArn, sessionName) inclusa.
	 * </p>
	 */
	public static void populateAssumeRoleFormParameters(PolicyNegoziazioneToken policy,
			Map<String, List<String>> pContent) throws UtilsException {
		String roleArn = policy.getAwsRoleArn();
		String sessionName = policy.getAwsSessionName();
		if (isEmpty(roleArn) || isEmpty(sessionName)) {
			throw new UtilsException("Configurazione AWS Signature V4 (modalità AssumeRole) incompleta: "
					+ "richiesti roleArn, sessionName");
		}
		TransportUtils.setParameter(pContent, FORM_ACTION, FORM_ACTION_VALUE);
		TransportUtils.setParameter(pContent, FORM_VERSION, STS_API_VERSION);
		TransportUtils.setParameter(pContent, FORM_ROLE_ARN, roleArn);
		TransportUtils.setParameter(pContent, FORM_ROLE_SESSION_NAME, sessionName);
		String durationSeconds = policy.getAwsSessionDurationSeconds();
		if (!isEmpty(durationSeconds)) {
			TransportUtils.setParameter(pContent, FORM_DURATION_SECONDS, durationSeconds.trim());
		}
		String externalId = policy.getAwsExternalId();
		if (!isEmpty(externalId)) {
			TransportUtils.setParameter(pContent, FORM_EXTERNAL_ID, externalId);
		}
	}

	/**
	 * Calcola gli header AWS Signature V4 da iniettare nella request STS:
	 * {@code Authorization}, {@code X-Amz-Date}, {@code X-Amz-Content-Sha256}.
	 * Le credenziali firma sono quelle <em>permanenti</em> dalla policy
	 * (AccessKeyId+SecretAccessKey {@code AKIA*}); STS userà la firma per emettere
	 * la tripla temporanea {@code ASIA*}.
	 *
	 * @param policy       policy con accessKeyId/secretAccessKey/region
	 * @param endpoint     URL STS (es. {@code https://sts.<region>.amazonaws.com/}) — la
	 *                     configurazione URL viene dal flow OAuth/policy, non da AWS V4
	 * @param body         body finale serializzato della request
	 * @param contentType  Content-Type effettivo che il flow OAuth setterà sulla request
	 *                     HTTP (la firma SigV4 lo include nei SignedHeaders, quindi
	 *                     deve coincidere esattamente con l'header inviato)
	 */
	public static Map<String, String> signAssumeRoleHeaders(PolicyNegoziazioneToken policy,
			String endpoint, byte[] body, String contentType) throws UtilsException {
		String accessKeyId = policy.getAwsAccessKeyId();
		String secretAccessKey = policy.getAwsSecretAccessKey();
		String region = policy.getAwsRegion();
		if (isEmpty(accessKeyId) || isEmpty(secretAccessKey) || isEmpty(region)) {
			throw new UtilsException("Configurazione AWS Signature V4 (modalità AssumeRole) incompleta: "
					+ "richiesti accessKeyId, secretAccessKey, region");
		}
		URI uri;
		try {
			uri = new URI(endpoint);
		} catch (Exception e) {
			throw new UtilsException("Endpoint STS non valido [" + endpoint + "]: " + e.getMessage(), e);
		}
		AWSSignatureV4 signer = new AWSSignatureV4(accessKeyId, secretAccessKey, region, STS_SERVICE);
		Map<String, String> headersToSign = new LinkedHashMap<>();
		if (contentType != null && !contentType.isEmpty()) {
			headersToSign.put(HttpConstants.CONTENT_TYPE, contentType);
		}
		return signer.sign(HttpRequestMethod.POST.name(), uri, headersToSign, body);
	}

	/**
	 * Parsa la response XML di {@code sts:AssumeRole}, costruisce l'{@link AWSCredentialBag}
	 * temporanea e ritorna un {@link InformazioniNegoziazioneToken} con
	 * {@code expiresIn} = {@code Expiration} STS (per il refresh automatico via cache).
	 *
	 * @param status status HTTP della response STS
	 * @param body   payload XML della response
	 * @throws UtilsException se status != 2xx o se il parsing fallisce
	 */
	public static InformazioniNegoziazioneToken buildInformazioniFromStsResponse(
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta,
			Integer status, byte[] body, PolicyNegoziazioneToken policy) throws UtilsException {

		byte[] safeBody = body != null ? body : new byte[0];
		int statusVal = status != null ? status.intValue() : 0;
		if (statusVal < 200 || statusVal >= 300) {
			throw buildErrorFromXml(statusVal, safeBody);
		}
		Credentials creds = parseAssumeRoleXml(safeBody);
		AWSCredentialBag bag = new AWSCredentialBag(creds.accessKeyId, creds.secretAccessKey,
				policy.getAwsRegion(), policy.getAwsService(), creds.sessionToken);

		InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken();
		info.setRequest(datiRichiesta);
		info.setBackendCredentialBag(bag);
		info.setValid(true);
		info.setRetrievedIn(DateManager.getDate());
		info.setExpiresIn(creds.expiration);
		// La rawResponse XML STS contiene <SecretAccessKey> e <SessionToken> in chiaro: la
		// mascheriamo già in memoria (la rawResponse è usata solo per audit, non per la firma)
		// così copriamo automaticamente tutti i percorsi di output (DB transazioni, log, toString).
		info.setRawResponse(AwsSecretMasker.maskStsXmlSecrets(new String(safeBody, StandardCharsets.UTF_8)));
		return info;
	}


	/* === XML parsing interno === */

	/** Wrapper interno dei 4 campi delle credenziali temporanee STS. */
	private static class Credentials {
		String accessKeyId;
		String secretAccessKey;
		String sessionToken;
		Date expiration;
	}

	private static Credentials parseAssumeRoleXml(byte[] body) throws UtilsException {
		try {
			Document doc = parseXml(body);
			Element credentialsElem = findFirstElementByLocalName(doc.getDocumentElement(), XML_CREDENTIALS);
			if (credentialsElem == null) {
				throw new UtilsException("Response STS AssumeRole non contiene <Credentials>: "
						+ new String(body, StandardCharsets.UTF_8));
			}
			Credentials c = new Credentials();
			c.accessKeyId = textOfChild(credentialsElem, XML_ACCESS_KEY_ID);
			c.secretAccessKey = textOfChild(credentialsElem, XML_SECRET_ACCESS_KEY);
			c.sessionToken = textOfChild(credentialsElem, XML_SESSION_TOKEN);
			String expirationStr = textOfChild(credentialsElem, XML_EXPIRATION);
			if (isEmpty(c.accessKeyId) || isEmpty(c.secretAccessKey) || isEmpty(c.sessionToken) || isEmpty(expirationStr)) {
				throw new UtilsException("Response STS AssumeRole incompleta: "
						+ "accessKeyId/secretAccessKey/sessionToken/expiration richiesti");
			}
			c.expiration = parseStsTimestamp(expirationStr);
			return c;
		} catch (UtilsException e) {
			throw e;
		} catch (Exception e) {
			throw new UtilsException("Errore nel parsing della response STS AssumeRole: " + e.getMessage(), e);
		}
	}

	private static UtilsException buildErrorFromXml(int statusCode, byte[] body) {
		String code = null;
		String message = null;
		try {
			Document doc = parseXml(body);
			Element root = doc.getDocumentElement();
			Element errorElem = findFirstElementByLocalName(root, XML_ERROR);
			if (errorElem != null) {
				code = textOfChild(errorElem, XML_ERROR_CODE);
				message = textOfChild(errorElem, XML_ERROR_MESSAGE);
			}
		} catch (Exception ignore) {
			// payload non XML: lo trasmettiamo come stringa raw
		}
		String summary = (code != null ? code + ": " : "") + (message != null ? message : "");
		String raw = new String(body, StandardCharsets.UTF_8);
		return new UtilsException("AWS STS AssumeRole HTTP " + statusCode
				+ (summary.isEmpty() ? "" : (" [" + summary + "]")) + " body=" + raw);
	}

	private static Document parseXml(byte[] body) throws UtilsException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(new ByteArrayInputStream(body));
		} catch (Exception e) {
			throw new UtilsException("Errore nel parsing XML della response STS: " + e.getMessage(), e);
		}
	}

	private static Element findFirstElementByLocalName(Element root, String localName) {
		if (root == null) {
			return null;
		}
		NodeList all = root.getElementsByTagNameNS(XML_NS_STS_2011, localName);
		if (all.getLength() > 0) {
			return (Element) all.item(0);
		}
		all = root.getElementsByTagName(localName);
		if (all.getLength() > 0) {
			return (Element) all.item(0);
		}
		return null;
	}

	private static String textOfChild(Element parent, String localName) {
		Element child = findFirstElementByLocalName(parent, localName);
		if (child == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		NodeList children = child.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.CDATA_SECTION_NODE) {
				sb.append(n.getNodeValue());
			}
		}
		String s = sb.toString().trim();
		return s.isEmpty() ? null : s;
	}

	private static Date parseStsTimestamp(String s) throws UtilsException {
		String[] patterns = { "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" };
		for (String p : patterns) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(p);
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				return sdf.parse(s);
			} catch (Exception ignore) {
				// try next
			}
		}
		throw new UtilsException("Timestamp STS Expiration non parseabile [" + s + "]");
	}

	private static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}
}
