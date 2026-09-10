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
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalError;
import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStart;
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
 *   <li><strong>Errore</strong> (HTTP non 2xx, sia sync che stream): il body di errore del
 *       provider viene normalizzato in {@link org.openspcoop2.message.llm.CanonicalError} e
 *       riscritto nell'envelope di errore del dialetto del client.</li>
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
		stripProviderHeaders(context.getMessaggio(), providerId, log);
		if (isHttpError(context.getMessaggio())) {
			handleProviderError(context, dialect, providerId, log);
			return;
		}
		if (LLMHandlerSupport.isLLMStream(context)) {
			handleStream(context, dialect, providerId, log);
			return;
		}
		handleSync(context, dialect, providerId);
	}

	/* === errore del provider (HTTP non 2xx) === */

	/**
	 * Su HTTP non-2xx il provider non risponde con uno stream ma con un body di errore JSON
	 * (Bedrock lo wrappa comunque in un frame event-stream binario, che qui viene estratto).
	 * L'errore viene normalizzato nel modello canonical e riscritto nell'envelope di errore
	 * del dialetto del client: un errore prodotto da un provider back-end deve arrivare al
	 * client nella forma che il suo SDK si aspetta, qualunque sia il provider.
	 */
	private void handleProviderError(InResponseContext context, LLMDialect dialect, String providerId, org.slf4j.Logger log) throws HandlerException {
		boolean stream = LLMHandlerSupport.isLLMStream(context);
		OpenSPCoop2Message msg = context.getMessaggio();
		int httpStatus = readHttpStatus(msg);
		byte[] body;
		try {
			body = stream ? readStreamErrorBody(context, providerId, log) : readSyncErrorBody(context);
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nella lettura del body di errore del provider " + providerId + ": " + e.getMessage(), e);
		}
		CanonicalError error;
		try {
			error = LLMTransformerRegistry.getInboundProviderResponseTransformer(providerId).transformError(body, httpStatus);
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nel parsing del body di errore del provider " + providerId + ": " + e.getMessage(), e);
		}
		context.getPddContext().addObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_ERROR, error);
		if (log != null) {
			log.info("LLMInboundResponseHandler: errore provider {} (status={} code={}) mappato sul dialetto {}",
					providerId, httpStatus, error.getCode(), dialect.getValue());
		}
		if (!stream) {
			// sync: il body viene riscritto dall'OutResponse handler, come nel flusso di successo
			return;
		}
		try {
			byte[] out = LLMTransformerRegistry.getOutboundFrontDoorResponseTransformer(dialect).transformError(error);
			AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn = (AbstractBaseOpenSPCoop2MessageDynamicContent<?>) msg;
			stripContentEncoding(msg);
			forceResponseContentType(msg, HttpConstants.CONTENT_TYPE_JSON);
			dyn.applyStreamWrapper(new java.io.ByteArrayInputStream(out));
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nella riscrittura del body di errore nel dialetto " + dialect.getValue() + ": " + e.getMessage(), e);
		}
	}

	private byte[] readSyncErrorBody(InResponseContext context) throws Exception {
		return LLMHandlerSupport.readBody(context.getMessaggio(), context.getResponseHeaders(), LLMHandlerSupport.getIdTransazione(context));
	}

	/**
	 * Legge il body di errore di una richiesta in streaming: il messaggio non è ancora
	 * materializzato, quindi viene bufferizzato interamente in memoria (i body di errore
	 * sono piccoli). Per il transport AWS event-stream viene estratto il payload JSON dal
	 * frame binario: AWS in caso di errore può rispondere sia con il frame (es. 400
	 * 'Invalid model identifier') sia con JSON puro non wrappato (es. 403 signature mismatch).
	 */
	private byte[] readStreamErrorBody(InResponseContext context, String providerId, org.slf4j.Logger log) throws Exception {
		OpenSPCoop2Message msg = context.getMessaggio();
		if (!(msg instanceof AbstractBaseOpenSPCoop2MessageDynamicContent)) {
			throw new HandlerException("LLMInboundResponseHandler: messaggio response non è AbstractBaseOpenSPCoop2MessageDynamicContent (class=" + (msg != null ? msg.getClass().getName() : "null") + "): lettura del body di errore non applicabile");
		}
		AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn = (AbstractBaseOpenSPCoop2MessageDynamicContent<?>) msg;
		InputStream src = dyn.getInputStream();
		String ce = readContentEncoding(dyn);
		if (ce != null && ce.toLowerCase().contains(HttpConstants.CONTENT_ENCODING_VALUE_GZIP)) {
			src = new GZIPInputStream(src);
		}
		byte[] body = org.openspcoop2.utils.Utilities.getAsByteArray(src);
		if (resolveProviderStreamTransport(providerId) != LLMProviderStreamTransport.AWS_EVENT_STREAM) {
			return body;
		}
		try {
			String payload = org.openspcoop2.message.llm.stream.AwsEventStreamReader
					.unwrapFirstFramePayload(new java.io.ByteArrayInputStream(body));
			if (payload != null) {
				if (log != null) {
					log.info("LLMInboundResponseHandler: error frame AWS event-stream unwrapped → JSON puro (provider={})", providerId);
				}
				return payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			}
			return body;
		} catch (java.io.IOException unwrapEx) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMInboundResponseHandler: body di errore non in formato event-stream, letto tale quale (provider={}): {}",
						providerId, unwrapEx.getMessage());
			}
			return body;
		}
	}

	private static boolean isHttpError(OpenSPCoop2Message msg) {
		int code = readHttpStatus(msg);
		return code > 0 && (code < 200 || code >= 300);
	}

	private static int readHttpStatus(OpenSPCoop2Message msg) {
		if (msg == null) {
			return 0;
		}
		TransportResponseContext ctx = msg.getTransportResponseContext();
		if (ctx == null || ctx.getCodiceTrasporto() == null) {
			return 0;
		}
		try {
			return Integer.parseInt(ctx.getCodiceTrasporto());
		} catch (NumberFormatException e) {
			return 0;
		}
	}


	/* === sync (non streaming) === */

	private void handleSync(InResponseContext context, LLMDialect dialect, String providerId) throws HandlerException {
		try {
			LLMHandlerSupport.captureResponseHeaders(context.getMessaggio(), context.getPddContext());
			byte[] body = LLMHandlerSupport.readBody(context.getMessaggio(), context.getResponseHeaders(), LLMHandlerSupport.getIdTransazione(context));
			LLMInboundProviderResponseTransformer transformer = LLMTransformerRegistry.getInboundProviderResponseTransformer(providerId);
			CanonicalChatResponse canonical = transformer.transform(body);
			applyFrontDoorIdentity(canonical, dialect, context.getPddContext());
			context.getPddContext().addObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_RESPONSE, canonical);
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nella trasformazione " + providerId + " → canonical: " + e.getMessage(), e);
		}
	}

	/**
	 * Valorizza id e model della response quando il provider non li restituisce (es. AWS Bedrock):
	 * i client SDK dei dialetti front-door li considerano campi obbligatori.
	 */
	private void applyFrontDoorIdentity(CanonicalChatResponse canonical, LLMDialect dialect, org.openspcoop2.pdd.core.PdDContext pddContext) {
		if (canonical == null) {
			return;
		}
		if (canonical.getId() == null || canonical.getId().isEmpty()) {
			canonical.setId(LLMHandlerSupport.buildLLMResponseId(dialect, LLMHandlerSupport.getIdTransazione(pddContext)));
		}
		if (canonical.getModel() == null || canonical.getModel().isEmpty()) {
			String model = LLMHandlerSupport.getLLMResponseModel(pddContext);
			if (model != null) {
				canonical.setModel(model);
			}
		}
	}


	/* === streaming === */

	private void handleStream(InResponseContext context, LLMDialect dialect, String providerId, org.slf4j.Logger log) throws HandlerException {
		OpenSPCoop2Message msg = context.getMessaggio();
		if (!(msg instanceof AbstractBaseOpenSPCoop2MessageDynamicContent)) {
			throw new HandlerException("LLMInboundResponseHandler: messaggio response non è AbstractBaseOpenSPCoop2MessageDynamicContent (class=" + (msg != null ? msg.getClass().getName() : "null") + "): wrap streaming non applicabile");
		}
		AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn = (AbstractBaseOpenSPCoop2MessageDynamicContent<?>) msg;
		final org.openspcoop2.pdd.core.PdDContext pddContext = context.getPddContext();
		try {
			InputStream wrapped = buildStreamingWrapper(dyn, dialect, providerId, pddContext);
			stripContentEncoding(msg);
			// La front-door espone sempre SSE, qualunque sia il transport del provider
			// (es. Bedrock risponde application/vnd.amazon.eventstream).
			forceResponseContentType(msg, HttpConstants.CONTENT_TYPE_EVENT_STREAM);
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

	private InputStream buildStreamingWrapper(AbstractBaseOpenSPCoop2MessageDynamicContent<?> dyn, LLMDialect dialect, String providerId,
			final org.openspcoop2.pdd.core.PdDContext pddContext) throws Exception {
		InputStream rawSrc = dyn.getInputStream();
		InputStream src = rawSrc;
		String ce = readContentEncoding(dyn);
		boolean gzip = ce != null && ce.toLowerCase().contains(HttpConstants.CONTENT_ENCODING_VALUE_GZIP);
		if (gzip) {
			src = new GZIPInputStream(rawSrc);
		}
		LLMProviderStreamTransport transport = resolveProviderStreamTransport(providerId);
		LLMProviderStreamReader reader = LLMTransformerRegistry.newProviderStreamReader(transport);
		LLMInboundProviderChunkDecoder decoder = LLMTransformerRegistry.newInboundProviderChunkDecoder(providerId);
		LLMOutboundFrontDoorChunkEncoder encoder = LLMTransformerRegistry.newOutboundFrontDoorChunkEncoder(dialect);
		// Observer: accumula nel PdDContext l'usage emesso dai chunk per la riga transazioni_llm.
		java.util.function.Consumer<org.openspcoop2.message.llm.CanonicalUsage> usageObserver =
				pddContext != null ? (u -> LLMHandlerSupport.accumulateLLMStreamUsage(pddContext, u)) : null;
		// Interceptor PII: unmask dei text_delta se la richiesta è stata mascherata (vault presente).
		java.util.function.UnaryOperator<java.util.List<org.openspcoop2.message.llm.stream.CanonicalStreamEvent>> piiInterceptor = null;
		if (pddContext != null) {
			Object v = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_PII_VAULT);
			if (v instanceof org.openspcoop2.pdd.core.llm.pii.PiiVault
					&& !((org.openspcoop2.pdd.core.llm.pii.PiiVault) v).isEmpty()) {
				Object cfgObj = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_PII_BINDING_CONFIG);
				boolean unmaskToolUse = (cfgObj instanceof org.openspcoop2.pdd.core.llm.pii.PiiBindingConfig)
						&& ((org.openspcoop2.pdd.core.llm.pii.PiiBindingConfig) cfgObj).isMaskToolUse();
				piiInterceptor = new org.openspcoop2.pdd.core.llm.pii.PiiStreamEventInterceptor((org.openspcoop2.pdd.core.llm.pii.PiiVault) v, unmaskToolUse);
			}
		}
		java.util.function.UnaryOperator<java.util.List<CanonicalStreamEvent>> interceptor =
				composeInterceptors(buildFrontDoorIdentityInterceptor(dialect, pddContext), piiInterceptor);
		ChunkTransformInputStream ct = new ChunkTransformInputStream(src, reader, decoder, encoder, usageObserver, interceptor);
		if (gzip) {
			// GZIPInputStream si ferma al trailer senza drenare il sottostante: forziamo il drain a EOF
			// così l'istrumentazione del connettore (letturaPayloadRisposta.completata, RESPONSE_COMPLETE_DATE,
			// rilascio connessione al pool) scatta anche con Content-Encoding gzip.
			ct.setDrainUnderlyingOnEof(rawSrc);
		}
		return ct;
	}

	/**
	 * Interceptor che valorizza id e model del {@code message_start} quando il provider non li
	 * veicola nei chunk (es. AWS Bedrock): i client SDK dei dialetti front-door li considerano
	 * campi obbligatori e falliscono il parsing del primo evento se assenti.
	 */
	private java.util.function.UnaryOperator<java.util.List<CanonicalStreamEvent>> buildFrontDoorIdentityInterceptor(LLMDialect dialect,
			final org.openspcoop2.pdd.core.PdDContext pddContext) {
		final String id = LLMHandlerSupport.buildLLMResponseId(dialect, LLMHandlerSupport.getIdTransazione(pddContext));
		final String model = LLMHandlerSupport.getLLMResponseModel(pddContext);
		return events -> {
			for (CanonicalStreamEvent event : events) {
				if (event instanceof CanonicalStreamMessageStart) {
					CanonicalStreamMessageStart messageStart = (CanonicalStreamMessageStart) event;
					if (messageStart.getId() == null || messageStart.getId().isEmpty()) {
						messageStart.setId(id);
					}
					if ((messageStart.getModel() == null || messageStart.getModel().isEmpty()) && model != null) {
						messageStart.setModel(model);
					}
				}
			}
			return events;
		};
	}

	private java.util.function.UnaryOperator<java.util.List<CanonicalStreamEvent>> composeInterceptors(
			java.util.function.UnaryOperator<java.util.List<CanonicalStreamEvent>> first,
			java.util.function.UnaryOperator<java.util.List<CanonicalStreamEvent>> second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		return events -> second.apply(first.apply(events));
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

	private void forceResponseContentType(OpenSPCoop2Message msg, String contentType) throws org.openspcoop2.message.exception.MessageException {
		TransportResponseContext ctx = msg.getTransportResponseContext();
		if (ctx != null) {
			ctx.removeHeader(HttpConstants.CONTENT_TYPE);
		}
		msg.setContentType(contentType);
	}

	private void stripProviderHeaders(OpenSPCoop2Message msg, String providerId, org.slf4j.Logger log) {
		if (msg == null) {
			return;
		}
		TransportResponseContext ctx = msg.getTransportResponseContext();
		if (ctx == null || ctx.getHeaders() == null || ctx.getHeaders().isEmpty()) {
			return;
		}
		String [] prefixes;
		try {
			prefixes = OpenSPCoop2Properties.getInstance().getLLMResponseStripProviderHeaders(providerId);
		} catch (Exception e) {
			if (log != null) {
				log.error("LLMInboundResponseHandler: lettura header da rimuovere per provider {} fallita: {}", providerId, e.getMessage(), e);
			}
			return;
		}
		if (prefixes == null || prefixes.length == 0) {
			return;
		}
		java.util.List<String> toRemove = new java.util.ArrayList<>();
		for (String headerName : ctx.getHeaders().keySet()) {
			if (headerName == null) {
				continue;
			}
			String hn = headerName.toLowerCase();
			for (String p : prefixes) {
				if (p != null && !p.isEmpty() && hn.startsWith(p.toLowerCase())) {
					toRemove.add(headerName);
					break;
				}
			}
		}
		for (String h : toRemove) {
			ctx.removeHeader(h);
		}
		if (log != null && log.isDebugEnabled() && !toRemove.isEmpty()) {
			log.debug("LLMInboundResponseHandler: rimossi {} header specifici del provider {} dalla risposta verso il client: {}", toRemove.size(), providerId, toRemove);
		}
	}
}
