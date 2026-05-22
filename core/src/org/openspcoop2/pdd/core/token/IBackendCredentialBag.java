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
package org.openspcoop2.pdd.core.token;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;

/**
 * Astrazione delle "credenziali backend" prodotte da una Token Policy quando il
 * meccanismo di autenticazione verso il backend NON è un Bearer token statico ma
 * richiede di emettere uno o più header HTTP <em>calcolati al momento del send</em>
 * (timestamp, hash del payload, firma).
 * <p>
 * Il connettore non conosce i singoli protocolli (AWS SigV4, Azure SAS, ecc.): vede
 * solo l'interfaccia, chiama {@link #buildBackendHeaders} e aggiunge il risultato
 * agli header HTTP. Tutti i dettagli (algoritmi, secret, scopes) restano incapsulati
 * nell'implementazione concreta della bag, popolata dalla Token Policy in fase di
 * negoziazione.
 * </p>
 * <p>
 * Implementazioni correnti:
 * </p>
 * <ul>
 *   <li>{@code AWSCredentialBag} (package {@code pdd.core.token.aws}) — AWS Signature V4
 *       per Bedrock/STS/S3/Lambda/SageMaker/ecc.</li>
 * </ul>
 * <p>
 * Estensioni future plausibili: Azure Shared Access Signature, GCP Service Account
 * JWT-bearer with caching, schemi HMAC custom per webhook firmati.
 * </p>
 * <p>
 * Le implementazioni devono essere {@link Serializable} perché trasportate dentro
 * {@link InformazioniNegoziazioneToken} e cacheabili.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface IBackendCredentialBag extends Serializable {

	/**
	 * Costruisce gli header HTTP da aggiungere alla request in costruzione verso il backend.
	 * Il connettore invoca questo metodo immediatamente prima del send, dopo che il body
	 * e gli header content-type sono fissati, perché alcune implementazioni (es. SigV4)
	 * incorporano l'hash del body e/o un timestamp corrente.
	 * <p>
	 * Il body è passato come {@link InputStream} per consentire alle implementazioni di
	 * calcolare un eventuale digest in streaming (caso AWS SigV4: sha256 dell'intero
	 * payload, anche quando il body è troppo grande per stare in memoria ed è stato
	 * serializzato dal connettore su filesystem). Lo stream viene consumato per intero da
	 * questo metodo; l'invio HTTP successivo apre uno stream separato dalla stessa sorgente.
	 * </p>
	 *
	 * @param requestUri    URI completa della request (host + path + query)
	 * @param httpMethod    metodo HTTP (es. {@code POST})
	 * @param contentType   valore dell'header {@code Content-Type} (o null se la request
	 *                      non ne ha uno — tipico delle GET)
	 * @param body          stream del payload della request (può essere null per body vuoto)
	 * @return mappa header (case-insensitive HTTP) → valore, da aggiungere alla request.
	 *         Mai null; può essere vuota se l'implementazione non vuole aggiungere niente.
	 * @throws UtilsException se il calcolo della firma/dei token fallisce o se la lettura
	 *                        dello stream del body fallisce
	 */
	Map<String, String> buildBackendHeaders(URI requestUri, String httpMethod, String contentType, InputStream body)
			throws UtilsException;

	/**
	 * Indica se questa bag legge il body della request per costruire gli header (es. AWS SigV4
	 * calcola lo sha256 del payload). Quando ritorna {@code true} il connettore deve garantire
	 * di fornire uno stream sul body (eventualmente bufferizzandolo se il flow nativo lo
	 * scriverebbe direttamente in rete senza buffer intermedio, come {@code HttpURLConnection}).
	 * <p>
	 * Default {@code false}: la maggior parte degli schemi (bearer-like, header statici)
	 * non necessita del payload.
	 * </p>
	 */
	default boolean requiresRequestBody() {
		return false;
	}

}
