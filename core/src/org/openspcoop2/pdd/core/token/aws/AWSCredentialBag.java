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
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openspcoop2.pdd.core.token.IBackendCredentialBag;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.AWSSignatureV4;
import org.openspcoop2.utils.transport.http.HttpConstants;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Bag delle credenziali AWS da usare per firmare le request via
 * {@link AWSSignatureV4}.
 * <p>
 * Output di una Token Policy di tipo {@code AWS Signature V4}, è trasportata dentro
 * {@code InformazioniNegoziazioneToken.backendCredentialBag} come {@link IBackendCredentialBag}:
 * il connettore non la conosce nello specifico, chiama solo
 * {@link #buildBackendHeaders(URI, String, String, byte[])} e aggiunge gli header risultanti.
 * </p>
 * <p>
 * Le due modalità di popolamento:
 * </p>
 * <ul>
 *   <li><b>Statica</b>: i campi sono presi direttamente dalla configurazione della
 *       Token Policy ({@code accessKeyId}, {@code secretAccessKey}, {@code region},
 *       {@code service}), senza chiamata STS, e {@code sessionToken} è {@code null}.</li>
 *   <li><b>AssumeRole</b>: la Token Policy chiama STS via SigV4, parsa la response e
 *       popola la bag con la tripla temporanea (l'{@code accessKeyId} è del tipo
 *       {@code ASIA*}, e {@code sessionToken} è valorizzato). La data di scadenza
 *       è scritta nel campo {@code expiresIn} di {@code InformazioniNegoziazioneToken}
 *       in modo che la cache standard delle Token Policy possa rinegoziare in
 *       automatico prima della scadenza.</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AWSCredentialBag implements IBackendCredentialBag, Cloneable {

	private static final long serialVersionUID = 1L;

	private String accessKeyId;

	/**
	 * Mascherato in JSON via {@link AwsSecretMasker.Serializer} (DB audit). L'istanza Java
	 * in memoria conserva il valore reale per la firma SigV4.
	 */
	@JsonSerialize(using = AwsSecretMasker.Serializer.class)
	private String secretAccessKey;

	private String region;
	private String service;

	/** Mascherato in JSON come {@link #secretAccessKey}; in memoria valore reale. */
	@JsonSerialize(using = AwsSecretMasker.Serializer.class)
	private String sessionToken;

	public AWSCredentialBag() {
		// default constructor (serialization)
	}

	public AWSCredentialBag(String accessKeyId, String secretAccessKey, String region, String service) {
		this(accessKeyId, secretAccessKey, region, service, null);
	}

	public AWSCredentialBag(String accessKeyId, String secretAccessKey, String region, String service, String sessionToken) {
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.region = region;
		this.service = service;
		this.sessionToken = sessionToken;
	}

	public String getAccessKeyId() {
		return this.accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey() {
		return this.secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getService() {
		return this.service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSessionToken() {
		return this.sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * Indica se le credenziali sono temporanee (presenza di session token, tipico
	 * pattern post-AssumeRole).
	 */
	public boolean isTemporary() {
		return this.sessionToken != null && !this.sessionToken.isEmpty();
	}

	@Override
	public Object clone() {
		return new AWSCredentialBag(this.accessKeyId, this.secretAccessKey, this.region, this.service, this.sessionToken);
	}

	/**
	 * {@code toString()} con segreti mascherati: copre il canale di leak via log
	 * (es. {@code log.debug(bag)} o {@code BaseBean.toString()} che usa reflection
	 * e invoca ricorsivamente questo {@code toString()} sul bag).
	 */
	@Override
	public String toString() {
		return "AWSCredentialBag{accessKeyId='" + this.accessKeyId + "'"
				+ ", region='" + this.region + "'"
				+ ", service='" + this.service + "'"
				+ ", secretAccessKey='" + AwsSecretMasker.mask(this.secretAccessKey) + "'"
				+ ", sessionToken='" + AwsSecretMasker.mask(this.sessionToken) + "'"
				+ "}";
	}

	/**
	 * Calcola gli header AWS Signature V4 da aggiungere alla request: {@code Authorization}
	 * (firma), {@code X-Amz-Date}, {@code X-Amz-Content-Sha256} e — solo per credenziali
	 * temporanee — {@code X-Amz-Security-Token}.
	 * <p>
	 * Se {@code contentType} è valorizzato viene firmato insieme agli header obbligatori
	 * (host, x-amz-date, eventuale security-token). Il body è letto in streaming per
	 * calcolarne lo sha256 (richiesto da SigV4); body null è trattato come payload vuoto.
	 * Lo stream viene consumato dal signer; il connettore aprirà un nuovo stream per
	 * l'invio HTTP successivo.
	 * </p>
	 */
	@Override
	public Map<String, String> buildBackendHeaders(URI requestUri, String httpMethod, String contentType, InputStream body)
			throws UtilsException {
		AWSSignatureV4 signer = new AWSSignatureV4(this.accessKeyId, this.secretAccessKey,
				this.region, this.service, this.sessionToken);
		Map<String, String> headersToSign = new LinkedHashMap<>();
		if (contentType != null && !contentType.isEmpty()) {
			headersToSign.put(HttpConstants.CONTENT_TYPE, contentType);
		}
		return signer.sign(httpMethod, requestUri, headersToSign,
				body != null ? body : new ByteArrayInputStream(new byte[0]));
	}

	/** SigV4 firma l'hash sha256 del payload: serve quindi accesso al body. */
	@Override
	public boolean requiresRequestBody() {
		return true;
	}

}
