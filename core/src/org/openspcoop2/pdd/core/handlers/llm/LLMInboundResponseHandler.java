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

import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformerRegistry;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;

/**
 * Terzo handler della pipeline LLM (catena InResponse, in coda).
 * <ul>
 *   <li>verifica che la transazione sia un flusso LLM (PdDContext popolato)</li>
 *   <li>legge il body della response provider (es. Anthropic Messages response)</li>
 *   <li>parsa con l'inbound provider response transformer registrato per il providerId</li>
 *   <li>sostituisce il messaggio con un {@link OpenSPCoop2LLMCanonicalMessage} che ospita
 *       un {@link CanonicalChatResponse}</li>
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
		if (LLMHandlerSupport.isLLMStream(context)) {
			// In modalità streaming la response provider è SSE (text/event-stream): non si
			// può parsare come body JSON unico. Il chunk transformer SSE end-to-end è ancora
			// da implementare (task #17). Per ora pass-through: lasciamo il body raw senza
			// trasformazione, così Anthropic→Anthropic stream funziona come proxy puro;
			// OpenAI→Anthropic stream produrrà eventi Anthropic non tradotti, che il client
			// OpenAI non sa parsare — è il segnale che servono i chunk transformer.
			if (log != null) {
				log.warn("LLMInboundResponseHandler: stream:true, parser canonical disabilitato (chunk transformer non implementato), pass-through SSE");
			}
			return;
		}
		String providerId = LLMHandlerSupport.getLLMProvider(context);
		if (providerId == null) {
			throw new HandlerException("LLMInboundResponseHandler: providerId mancante nel PdDContext");
		}
		try {
			LLMHandlerSupport.captureResponseHeaders(context.getMessaggio(), context.getPddContext());
			byte[] body = LLMHandlerSupport.readBody(context.getMessaggio(), context.getResponseHeaders(), LLMHandlerSupport.getIdTransazione(context));
			LLMInboundProviderResponseTransformer transformer = LLMTransformerRegistry.getInboundProviderResponseTransformer(providerId);
			CanonicalChatResponse canonical = transformer.transform(body);
			// Il canonical viaggia nel PdDContext, non nel messaggio
			context.getPddContext().addObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_RESPONSE, canonical);
		} catch (Exception e) {
			throw new HandlerException("LLMInboundResponseHandler: errore nella trasformazione " + providerId + " → canonical: " + e.getMessage(), e);
		}
	}
}
