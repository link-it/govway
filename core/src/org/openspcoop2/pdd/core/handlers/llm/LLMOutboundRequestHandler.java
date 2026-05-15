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

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMProviderRequest;
import org.openspcoop2.message.llm.transform.LLMTransformerRegistry;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.handlers.OutRequestHandler;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * Secondo handler della pipeline LLM (catena OutRequest, in testa, prima del connettore).
 * <ul>
 *   <li>recupera il messaggio canonical dal context</li>
 *   <li>applica l'outbound provider transformer (canonical → JSON Anthropic + header)</li>
 *   <li>sostituisce il messaggio con un {@code OpenSPCoop2Message_json_impl} contenente
 *       il payload Anthropic, in modo che il connettore HC5 standard lo invii senza
 *       conoscere il dominio LLM</li>
 *   <li>aggiunge gli header HTTP richiesti dal provider (es. {@code anthropic-version})
 *       via {@code forceTransportHeader}</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMOutboundRequestHandler implements OutRequestHandler {

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {
		org.slf4j.Logger log = context.getLogCore();
		LLMDialect dialect = LLMHandlerSupport.getLLMFormato(context);
		if (dialect == null) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMOutboundRequestHandler: PdDContext non marcato LLM, no-op");
			}
			return;
		}
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMOutboundRequestHandler: invocato con dialetto={}", dialect.getValue());
		}
		String providerId = LLMHandlerSupport.getLLMProvider(context);
		if (providerId == null) {
			throw new HandlerException("LLMOutboundRequestHandler: providerId mancante nel PdDContext");
		}
		try {
			CanonicalChatRequest canonical = extractCanonical(context);
			LLMOutboundProviderRequestTransformer transformer = LLMTransformerRegistry.getOutboundProviderRequestTransformer(providerId);
			LLMProviderRequest providerRequest = transformer.transform(canonical);
			// Aggiorniamo il contenuto del messaggio esistente in-place: il ConnettoreMsg ha
			// già il riferimento all'oggetto (settato prima dell'invocazione degli handler in
			// ConsegnaContenutiApplicativi:2101), quindi sostituirlo con context.setMessaggio()
			// non avrebbe effetto sul body veicolato al backend.
			LLMHandlerSupport.applyJsonBody(context.getMessaggio(), providerRequest.getBody(), providerRequest.getHeaders());
			// Re-routing del path verso l'endpoint nativo del provider: il connettore tiene
			// solo la base URL (es. https://api.anthropic.com/v1) e il transformer outbound
			// decide il path-risorsa (es. /messages per chat). La URL finale viene poi
			// costruita dal connettore via RestUtilities.buildUrl come base + functionParameters.
			applyProviderResourcePath(context.getMessaggio(), transformer, canonical, log);
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMOutboundRequestHandler: errore nella trasformazione canonical → " + providerId + ": " + e.getMessage(), e);
		}
	}

	private void applyProviderResourcePath(OpenSPCoop2Message msg, LLMOutboundProviderRequestTransformer transformer, CanonicalChatRequest canonical, org.slf4j.Logger log) {
		String resourcePath = transformer.getProviderResourcePath(canonical);
		if (resourcePath == null || msg == null) {
			return;
		}
		TransportRequestContext ctx = msg.getTransportRequestContext();
		if (ctx == null) {
			if (log != null) {
				log.warn("LLMOutboundRequestHandler: TransportRequestContext null, impossibile applicare resourcePath={}", resourcePath);
			}
			return;
		}
		ctx.setFunctionParameters(resourcePath);
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMOutboundRequestHandler: functionParameters sovrascritto -> {}", resourcePath);
		}
	}

	private CanonicalChatRequest extractCanonical(OutRequestContext context) throws HandlerException {
		Object o = context.getPddContext() != null
				? context.getPddContext().getObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_REQUEST)
				: null;
		if (o instanceof CanonicalChatRequest) {
			return (CanonicalChatRequest) o;
		}
		throw new HandlerException("LLMOutboundRequestHandler: CanonicalChatRequest assente nel PdDContext (l'InRequestProtocolHandler LLM non ha popolato il context?)");
	}
}
