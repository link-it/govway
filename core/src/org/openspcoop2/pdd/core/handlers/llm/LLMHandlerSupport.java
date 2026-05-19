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
package org.openspcoop2.pdd.core.handlers.llm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.rest.OpenSPCoop2Message_json_impl;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.BaseContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * Helper di utilità per i 4 handler LLM. Centralizza:
 * <ul>
 *   <li>lettura/scrittura del PdDContext (chiavi LLM_FORMATO, LLM_PROVIDER)</li>
 *   <li>lettura del body bytes da un OpenSPCoop2Message REST</li>
 *   <li>costruzione di un OpenSPCoop2Message_json_impl con body + header</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LLMHandlerSupport {

	private static final Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();

	private LLMHandlerSupport() {
		// utility class
	}

	/**
	 * Marca il PdDContext come "transazione LLM" se l'API in lavorazione ha
	 * un {@link FormatoSpecifica} di tipo LLM. Da invocare nei punti d'ingresso
	 * (RicezioneContenutiApplicativi per le fruizioni, RicezioneBuste per le
	 * erogazioni) subito dopo il caricamento dell'AccordoServizioParteComune.
	 * <p>
	 * Effetto: gli handler LLM (InRequest, OutRequest, InResponse, OutResponse)
	 * si attiveranno; le transazioni non-LLM restano del tutto trasparenti.
	 * </p>
	 * <p>
	 * Imposta solo il dialetto front-door derivato dal FormatoSpecifica;
	 * il providerId back-end viene risolto in {@code LLMOutboundRequestHandler}
	 * leggendo la LLM Provider Policy associata al connettore.
	 * </p>
	 */
	public static void populateLLMContext(PdDContext pddContext, AccordoServizioParteComune apc) {
		if (pddContext == null || apc == null) {
			return;
		}
		FormatoSpecifica fs = apc.getFormatoSpecifica();
		if (fs == null || !fs.isLLM()) {
			return;
		}
		LLMDialect dialect = LLMDialect.fromValue(fs.getValue());
		if (dialect == null) {
			return;
		}
		pddContext.addObject(LLMHandlerConstants.PDD_CTX_LLM_FORMATO, dialect);
	}

	/**
	 * Salva nel PdDContext il providerId back-end (es. {@code "anthropic"},
	 * {@code "openai"}) risolto dal {@code LLMOutboundRequestHandler}.
	 */
	public static void setLLMProvider(PdDContext pddContext, String providerId) {
		if (pddContext == null || providerId == null) {
			return;
		}
		pddContext.addObject(LLMHandlerConstants.PDD_CTX_LLM_PROVIDER, providerId);
	}

	/**
	 * Recupera dal PdDContext il dialetto LLM dell'API in lavorazione.
	 * Ritorna null se la chiave non è presente (transazione non-LLM): in tal
	 * caso gli handler LLM devono restare no-op.
	 */
	public static LLMDialect getLLMFormato(BaseContext context) {
		PdDContext pddContext = context != null ? context.getPddContext() : null;
		if (pddContext == null) {
			return null;
		}
		Object o = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_FORMATO);
		return o instanceof LLMDialect ? (LLMDialect) o : null;
	}

	/**
	 * Recupera l'idTransazione dal PdDContext via {@link org.openspcoop2.core.constants.Costanti#ID_TRANSAZIONE}.
	 * Necessario per {@code setInputStreamLazyBuffer} e simili che lo usano come
	 * identificatore del file di backing del buffer su disco quando supera la soglia.
	 */
	public static String getIdTransazione(BaseContext context) {
		PdDContext pddContext = context != null ? context.getPddContext() : null;
		if (pddContext == null) {
			return null;
		}
		Object o = pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		return o instanceof String ? (String) o : null;
	}

	/**
	 * Indica se la transazione è in modalità streaming (client ha chiesto stream:true).
	 * Letto dal PdDContext via {@link LLMHandlerConstants#PDD_CTX_LLM_STREAM}. Ritorna
	 * false anche se la chiave manca: in questo modo le transazioni non-LLM (no flag)
	 * sono trattate come sync.
	 */
	public static boolean isLLMStream(BaseContext context) {
		PdDContext pddContext = context != null ? context.getPddContext() : null;
		if (pddContext == null) {
			return false;
		}
		Object o = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_STREAM);
		return o instanceof Boolean && Boolean.TRUE.equals(o);
	}

	/**
	 * Recupera dal PdDContext l'identificativo del provider back-end (es. "anthropic").
	 */
	public static String getLLMProvider(BaseContext context) {
		PdDContext pddContext = context != null ? context.getPddContext() : null;
		if (pddContext == null) {
			return null;
		}
		Object o = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_PROVIDER);
		return o instanceof String ? (String) o : null;
	}

	/**
	 * Estrae il body bytes da un messaggio REST. Ritorna byte[0] se il messaggio
	 * non ha content. Se l'header {@code Content-Encoding: gzip} è presente (o il
	 * magic byte 0x1f 0x8b come fallback quando l'header non è ancora popolato
	 * da Apache HC5), decomprime il payload prima di restituirlo: il connettore
	 * HC5 di GovWay disabilita la decompressione automatica per preservare il
	 * body veicolato, ma i trasformatori canonical hanno bisogno del JSON in chiaro.
	 * <p>
	 * Per leggere i byte raw senza il roundtrip String UTF-8 di
	 * {@code OpenSPCoop2Message_json_impl.buildContent()} (che corromperebbe i
	 * byte binari gzip), usa {@code setInputStreamLazyBuffer + getInputStream}
	 * oppure {@code getInputStreamFromContentBuffer} se il buffer è già popolato.
	 * </p>
	 */
	public static byte[] readBody(OpenSPCoop2Message msg, Map<String, List<String>> responseHeaders, String idTransazione) throws MessageException, MessageNotSupportedException {
		if (msg == null) {
			log.warn("readBody: messaggio null, ritorno body vuoto");
			return new byte[0];
		}
		OpenSPCoop2RestMessage<?> rest = msg.castAsRest();
		if (!rest.hasContent()) {
			log.warn("readBody: rest.hasContent()=false, ritorno body vuoto (messageRole={}, contentType={})",
					msg.getMessageRole(), rest.getContentType());
			return new byte[0];
		}
		byte[] raw = readRawBytes(rest, idTransazione);
		if (raw.length == 0) {
			log.warn("readBody: readRawBytes ritorna 0 byte nonostante hasContent=true (messageRole={}, contentType={})",
					msg.getMessageRole(), rest.getContentType());
		}
		String detectedFrom = detectGzip(msg, responseHeaders, raw);
		if (detectedFrom != null) {
			log.info("readBody: body gzip rilevato da {}, decomprimo {} bytes", detectedFrom, raw.length);
			byte[] decompressed = decodeGzip(raw);
			log.info("readBody: gzip decompresso {} bytes -> {} bytes", raw.length, decompressed.length);
			stripContentEncoding(msg);
			return decompressed;
		}
		if (log.isDebugEnabled()) {
			log.debug("readBody: nessuna evidenza di gzip (header + magic byte), ritorno body raw ({} bytes)", raw.length);
		}
		return raw;
	}

	/**
	 * Legge il body raw bytes preservando eventuali byte binari (es. gzip).
	 * Strategia (analoga a {@code ValidatoreMessaggiApplicativiRest}):
	 * <ol>
	 *   <li>{@code setInputStreamLazyBuffer(idTransazione)}: se ritorna true, wrappa
	 *       lo stream in un buffer dinamico e legge via {@link OpenSPCoop2RestMessage#getInputStream()}
	 *       — i byte vengono preservati intatti e popolano contemporaneamente il
	 *       contentBuffer del messaggio per consumi successivi.</li>
	 *   <li>fallback: se il content è già stato bufferizzato da uno step precedente
	 *       (es. validatore), legge via {@link OpenSPCoop2RestMessage#getInputStreamFromContentBuffer()}.</li>
	 *   <li>altrimenti lancia {@link MessageException}: non possiamo ricadere su
	 *       {@code getContentAsByteArray()} perché su {@code OpenSPCoop2Message_json_impl}
	 *       fa un roundtrip String UTF-8 che corromperebbe byte binari gzip
	 *       (es. 0x8b → replacement char).</li>
	 * </ol>
	 */
	private static byte[] readRawBytes(OpenSPCoop2RestMessage<?> rest, String idTransazione) throws MessageException {
		try {
			boolean lazy = rest.setInputStreamLazyBuffer(idTransazione);
			if (lazy) {
				return readAllBytes(rest.getInputStream());
			}
			InputStream fromBuffer = rest.getInputStreamFromContentBuffer();
			if (fromBuffer != null) {
				return readAllBytes(fromBuffer);
			}
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException("readRawBytes: errore lettura body raw: " + e.getMessage(), e);
		}
		throw new MessageException("readRawBytes: body non leggibile in modo raw (lazy buffer non attivabile e contentBuffer null): rischio di corruzione byte binari");
	}

	private static byte[] readAllBytes(InputStream in) throws MessageException {
		if (in == null) {
			return new byte[0];
		}
		try (InputStream src = in;
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			CopyStream.copy(src, out);
			return out.toByteArray();
		} catch (Exception e) {
			throw new MessageException("readAllBytes: errore copia stream: " + e.getMessage(), e);
		}
	}

	/**
	 * Rileva se il body è gzip. Controlla in ordine: header Content-Encoding nella mappa
	 * passata dal chiamante (es. {@code InResponseContext.getResponseHeaders()}),
	 * header Content-Encoding nel TransportContext del messaggio, e infine il magic
	 * byte gzip (0x1f 0x8b) sui primi 2 byte del payload. Il fallback magic byte è
	 * necessario perché Apache HC5 popola {@code Content-Encoding} progressivamente,
	 * e al momento dell'invocazione di {@code InResponseHandler} l'header può non
	 * essere ancora presente nella mappa header. Ritorna una stringa identificativa
	 * della fonte di detection (per debug log), o null se il body non è gzip.
	 */
	private static String detectGzip(OpenSPCoop2Message msg, Map<String, List<String>> responseHeaders, byte[] raw) {
		String ceCtx = TransportUtils.getFirstValue(responseHeaders, HttpConstants.CONTENT_ENCODING);
		if (ceCtx != null && ceCtx.toLowerCase().contains("gzip")) {
			return "header Content-Encoding (InResponseContext)";
		}
		String ceMsg = readContentEncoding(msg);
		if (ceMsg != null && ceMsg.toLowerCase().contains("gzip")) {
			return "header Content-Encoding (Message TransportContext)";
		}
		if (hasGzipMagic(raw)) {
			return "magic byte 1f 8b (header Content-Encoding non ancora popolato)";
		}
		return null;
	}

	private static boolean hasGzipMagic(byte[] raw) {
		return raw != null && raw.length >= 2
				&& (raw[0] & 0xff) == 0x1f
				&& (raw[1] & 0xff) == 0x8b;
	}

	private static void stripContentEncoding(OpenSPCoop2Message msg) {
		if (MessageRole.RESPONSE.equals(msg.getMessageRole())) {
			TransportResponseContext ctx = msg.getTransportResponseContext();
			if (ctx != null) {
				ctx.removeHeader(HttpConstants.CONTENT_ENCODING);
			}
			return;
		}
		TransportRequestContext ctx = msg.getTransportRequestContext();
		if (ctx != null) {
			ctx.removeHeader(HttpConstants.CONTENT_ENCODING);
		}
	}

	/**
	 * Cattura il {@link TransportResponseContext} del messaggio response originale
	 * (dal provider) e lo salva nel PdDContext per essere ri-applicato al messaggio
	 * finale verso il client. Vengono rimossi Content-Encoding e Content-Length
	 * perché il body sarà ricostruito in chiaro dai trasformatori canonical.
	 */
	public static void captureResponseHeaders(OpenSPCoop2Message msg, PdDContext pddContext) {
		if (msg == null || pddContext == null) {
			return;
		}
		TransportResponseContext ctx = msg.getTransportResponseContext();
		if (ctx == null) {
			return;
		}
		ctx.removeHeader(HttpConstants.CONTENT_ENCODING);
		ctx.removeHeader(HttpConstants.CONTENT_LENGTH);
		pddContext.addObject(LLMHandlerConstants.PDD_CTX_LLM_RESPONSE_HEADERS, ctx);
	}

	/**
	 * Ri-applica al nuovo messaggio finale il {@link TransportResponseContext}
	 * catturato da {@link #captureResponseHeaders}, preservando gli header utili
	 * del provider (rate-limit, request-id, ecc.) attraverso la trasformazione
	 * canonical.
	 */
	public static void applyCapturedResponseHeaders(OpenSPCoop2Message msg, PdDContext pddContext) {
		if (msg == null || pddContext == null) {
			return;
		}
		Object o = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_RESPONSE_HEADERS);
		if (o instanceof TransportResponseContext) {
			msg.setTransportResponseContext((TransportResponseContext) o);
		}
	}

	private static String readContentEncoding(OpenSPCoop2Message msg) {
		if (MessageRole.RESPONSE.equals(msg.getMessageRole())) {
			TransportResponseContext ctx = msg.getTransportResponseContext();
			return ctx != null ? ctx.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING) : null;
		}
		TransportRequestContext ctx = msg.getTransportRequestContext();
		return ctx != null ? ctx.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING) : null;
	}

	private static byte[] decodeGzip(byte[] raw) {
		if (raw == null || raw.length == 0) {
			return raw;
		}
		try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(raw));
				ByteArrayOutputStream out = new ByteArrayOutputStream(raw.length * 2)) {
			CopyStream.copy(gz, out);
			return out.toByteArray();
		} catch (Exception e) {
			// fallback conservativo: se la decompressione fallisce ritorniamo il raw,
			// il transformer fallira' con un messaggio diagnostico chiaro
			log.warn("decodeGzip: decompressione fallita ({} bytes), ritorno il body raw: {}", raw.length, e.getMessage(), e);
			return raw;
		}
	}

	/**
	 * Costruisce un OpenSPCoop2Message_json_impl con il body fornito e aggiunge
	 * eventuali header HTTP statici tramite {@code forceTransportHeader}.
	 */
	public static OpenSPCoop2Message_json_impl buildJsonMessage(byte[] body, Map<String, String> headers) throws MessageException {
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		OpenSPCoop2Message_json_impl msg = new OpenSPCoop2Message_json_impl(factory, new ByteArrayInputStream(body != null ? body : new byte[0]), HttpConstants.CONTENT_TYPE_JSON);
		applyHeaders(msg, headers);
		return msg;
	}

	/**
	 * Aggiorna in-place il content di un OpenSPCoop2RestMessage&lt;String&gt; (es. JSON)
	 * con il body fornito, e applica gli eventuali header HTTP statici tramite
	 * {@code forceTransportHeader}. Necessario quando la pipeline GovWay ha già
	 * "linkato" il messaggio al connettore prima della catena handler (vedi
	 * {@code ConsegnaContenutiApplicativi:2101}): sostituirlo con setMessaggio()
	 * non aggiornerebbe il body veicolato.
	 */
	@SuppressWarnings("unchecked")
	public static void applyJsonBody(OpenSPCoop2Message msg, byte[] body, Map<String, String> headers) throws MessageException, MessageNotSupportedException {
		if (msg == null) {
			throw new MessageException("messaggio nullo: impossibile aggiornare il body");
		}
		OpenSPCoop2RestMessage<String> rest = (OpenSPCoop2RestMessage<String>) msg.castAsRest();
		String content = body != null ? new String(body, java.nio.charset.StandardCharsets.UTF_8) : "";
		rest.updateContent(content);
		applyHeaders(msg, headers);
	}

	private static void applyHeaders(OpenSPCoop2Message msg, Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return;
		}
		for (Map.Entry<String, String> e : headers.entrySet()) {
			msg.forceTransportHeader(e.getKey(), e.getValue());
		}
	}
}
