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

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2MessageDynamicContent;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.stream.ChunkTransformInputStream;
import org.openspcoop2.message.llm.stream.LLMProviderStreamReader;
import org.openspcoop2.message.llm.stream.LLMProviderStreamTransport;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMInboundProviderChunkDecoder;
import org.openspcoop2.message.llm.transform.LLMInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorChunkEncoder;
import org.openspcoop2.message.llm.transform.LLMOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformerRegistry;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Terzo handler della pipeline LLM (catena InResponse, in coda).
 * <p>
 * Due modalità di operazione:
 * </p>
 * <ul>
 *   <li><strong>Sync</strong> (stream=false): legge tutto il body della response,
 *       parsa con l'inbound provider response transformer e popola
 *       {@link LLMHandlerConstants#PDD_CTX_LLM_CANONICAL_RESPONSE}.</li>
 *   <li><strong>Stream</strong> (stream=true): wrappa l'InputStream del messaggio con
 *       un {@link ChunkTransformInputStream} che traduce on-the-fly i chunk dal
 *       transport/dialect provider al SSE front-door del dialect client.
 *       Eventuale decompressione gzip viene applicata prima del transformer.</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMInboundResponseHandler implements InResponseHandler {

	@Override
	public void invoke(InResponseContext context) throws HandlerException {
		org.slf4j.Logger log = context.getLogCore();
		LLMDialect dialect = LLMHandlerSupport.getLLMFormato(context);
		if (dialect == null) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMInboundResponseHandler: PdDContext non marcato LLM, no-op");
			}
			return;
		}
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMInboundResponseHandler: invocato con dialetto={}", dialect.getValue());
		}
		String providerId = LLMHandlerSupport.getLLMProvider(context);
		if (providerId == null) {
			throw new HandlerException("LLMInboundResponseHandler: providerId mancante nel PdDContext");
		}
		if (LLMHandlerSupport.isLLMStream(context)) {
			// Su HTTP non-2xx il body NON è streaming: provider risponde con JSON di errore.
			// Bedrock event-stream lo wrappa comunque in un frame binario → estraiamo il payload
			// e lo sostituiamo nel messaggio. Altri transport (SSE/NDJSON) rispondono con JSON puro
			// per gli errori, lasciamo il body inalterato.
			if (isHttpError(context.getMessaggio())) {
				unwrapErrorBodyIfNeeded(context, providerId, log);
				return;
			}
			handleStream(context, dialect, providerId, log);
			return;
		}
		handleSync(context, providerId);
	}

	private void unwrapErrorBodyIfNeeded(InResponseContext context, String providerId, org.slf4j.Logger log) throws HandlerException {
		LLMProviderStreamTransport transport;
		try {
			transport = resolveProviderStreamTransport(providerId);
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: impossibile risolvere il transport del provider " + providerId + ": " + e.getMessage(), e);
		}
		if (transport != LLMProviderStreamTransport.AWS_EVENT_STREAM) {
			// SSE/NDJSON: l'error body è già JSON puro, nessun unwrap necessario
			return;
		}
		OpenSPCoop2Message msg = context.getMessaggio();
		if (!(msg instanceof AbstractBaseOpenSPCoop2MessageDynamicContent)) {
			return;
		}
		AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn = (AbstractBaseOpenSPCoop2MessageDynamicContent<?>) msg;
		try {
			InputStream src = dyn.getInputStream();
			String ce = readContentEncoding(dyn);
			if (ce != null && ce.toLowerCase().contains(HttpConstants.CONTENT_ENCODING_VALUE_GZIP)) {
				src = new GZIPInputStream(src);
			}
			// Bufferizza interamente il body: AWS in caso di errore può rispondere in due modi
			//   a) frame event-stream binario contenente JSON (es. 400 'Invalid model identifier')
			//   b) JSON puro non wrappato (es. 403 signature mismatch)
			// Tentiamo l'unwrap: se fallisce (body non è event-stream), passiamo il body originale.
			byte[] body = org.openspcoop2.utils.Utilities.getAsByteArray(src);
			byte[] result;
			try {
				String payload = org.openspcoop2.message.llm.stream.AwsEventStreamReader
						.unwrapFirstFramePayload(new java.io.ByteArrayInputStream(body));
				result = payload != null ? payload.getBytes(java.nio.charset.StandardCharsets.UTF_8) : body;
				if (log != null && payload != null) {
					log.info("LLMInboundResponseHandler: error frame AWS event-stream unwrapped → JSON puro (provider={})", providerId);
				}
			} catch (java.io.IOException unwrapEx) {
				// Body non in formato event-stream → JSON puro, passiamo tale quale al client.
				if (log != null && log.isDebugEnabled()) {
					log.debug("LLMInboundResponseHandler: body di errore non in formato event-stream, passato tale quale (provider={}): {}",
							providerId, unwrapEx.getMessage());
				}
				result = body;
			}
			stripContentEncoding(msg);
			dyn.applyStreamWrapper(new java.io.ByteArrayInputStream(result));
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nella gestione del body di errore: " + e.getMessage(), e);
		}
	}

	private static boolean isHttpError(OpenSPCoop2Message msg) {
		if (msg == null) {
			return false;
		}
		TransportResponseContext ctx = msg.getTransportResponseContext();
		if (ctx == null || ctx.getCodiceTrasporto() == null) {
			return false;
		}
		try {
			int code = Integer.parseInt(ctx.getCodiceTrasporto());
			return code < 200 || code >= 300;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	/* === sync (non streaming) === */

	private void handleSync(InResponseContext context, String providerId) throws HandlerException {
		try {
			LLMHandlerSupport.captureResponseHeaders(context.getMessaggio(), context.getPddContext());
			byte[] body = LLMHandlerSupport.readBody(context.getMessaggio(), context.getResponseHeaders(), LLMHandlerSupport.getIdTransazione(context));
			LLMInboundProviderResponseTransformer transformer = LLMTransformerRegistry.getInboundProviderResponseTransformer(providerId);
			CanonicalChatResponse canonical = transformer.transform(body);
			context.getPddContext().addObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_RESPONSE, canonical);
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nella trasformazione " + providerId + " → canonical: " + e.getMessage(), e);
		}
	}


	/* === streaming === */

	private void handleStream(InResponseContext context, LLMDialect dialect, String providerId, org.slf4j.Logger log) throws HandlerException {
		OpenSPCoop2Message msg = context.getMessaggio();
		if (!(msg instanceof AbstractBaseOpenSPCoop2MessageDynamicContent)) {
			throw new HandlerException("LLMInboundResponseHandler: messaggio response non è AbstractBaseOpenSPCoop2MessageDynamicContent (class=" + (msg != null ? msg.getClass().getName() : "null") + "): wrap streaming non applicabile");
		}
		AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn = (AbstractBaseOpenSPCoop2MessageDynamicContent<?>) msg;
		try {
			InputStream wrapped = buildStreamingWrapper(dyn, dialect, providerId);
			stripContentEncoding(msg);
			dyn.applyStreamWrapper(wrapped);
			if (log != null) {
				log.info("LLMInboundResponseHandler: stream wrapper applicato (dialect={} provider={})", dialect.getValue(), providerId);
			}
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nel setup del chunk transformer streaming: " + e.getMessage(), e);
		}
	}

	private InputStream buildStreamingWrapper(AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn, LLMDialect dialect, String providerId) throws Exception {
		InputStream src = dyn.getInputStream();
		String ce = readContentEncoding(dyn);
		if (ce != null && ce.toLowerCase().contains(HttpConstants.CONTENT_ENCODING_VALUE_GZIP)) {
			src = new GZIPInputStream(src);
		}
		LLMProviderStreamTransport transport = resolveProviderStreamTransport(providerId);
		LLMProviderStreamReader reader = LLMTransformerRegistry.newProviderStreamReader(transport);
		LLMInboundProviderChunkDecoder decoder = LLMTransformerRegistry.newInboundProviderChunkDecoder(providerId);
		LLMOutboundFrontDoorChunkEncoder encoder = LLMTransformerRegistry.newOutboundFrontDoorChunkEncoder(dialect);
		return new ChunkTransformInputStream(src, reader, decoder, encoder);
	}

	private LLMProviderStreamTransport resolveProviderStreamTransport(String providerId) throws Exception {
		// Recuperiamo il transport del provider dal request transformer registrato:
		// rispetta il design "il provider che ha generato la request dichiara il transport
		// di response", evitando di hardcodare SSE qui.
		LLMOutboundProviderRequestTransformer t = LLMTransformerRegistry.getOutboundProviderRequestTransformer(providerId);
		LLMProviderStreamTransport transport = t.getProviderStreamTransport();
		if (transport == null) {
			throw new HandlerException("LLMInboundResponseHandler: providerStreamTransport non dichiarato per providerId=" + providerId);
		}
		return transport;
	}

	private String readContentEncoding(OpenSPCoop2Message msg) {
		TransportResponseContext ctx = msg.getTransportResponseContext();
		return ctx != null ? ctx.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING) : null;
	}

	private void stripContentEncoding(OpenSPCoop2Message msg) {
		TransportResponseContext ctx = msg.getTransportResponseContext();
		if (ctx != null) {
			ctx.removeHeader(HttpConstants.CONTENT_ENCODING);
		}
	}
}
