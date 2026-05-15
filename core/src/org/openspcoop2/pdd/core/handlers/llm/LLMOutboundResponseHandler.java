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
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformerRegistry;
import org.openspcoop2.message.rest.OpenSPCoop2Message_json_impl;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.handlers.OutResponseHandler;

/**
 * Quarto handler della pipeline LLM (catena OutResponse, in testa, prima del send al client).
 * <ul>
 *   <li>verifica che la transazione sia un flusso LLM</li>
 *   <li>recupera il canonical response dal messaggio</li>
 *   <li>applica l'outbound front-door response transformer del dialetto del client</li>
 *   <li>sostituisce il messaggio con un {@code OpenSPCoop2Message_json_impl} contenente
 *       la response nel dialetto richiesto (OpenAI o Anthropic)</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMOutboundResponseHandler implements OutResponseHandler {

	@Override
	public void invoke(OutResponseContext context) throws HandlerException {
		org.slf4j.Logger log = context.getLogCore();
		LLMDialect dialect = LLMHandlerSupport.getLLMFormato(context);
		if (dialect == null) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMOutboundResponseHandler: PdDContext non marcato LLM, no-op");
			}
			return;
		}
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMOutboundResponseHandler: invocato con dialetto={}", dialect.getValue());
		}
		if (LLMHandlerSupport.isLLMStream(context)) {
			// Pendant del LLMInboundResponseHandler: in modalità stream il canonical
			// non è popolato (skipped lì), e il body del messaggio è ancora la SSE provider.
			// Niente da trasformare: pass-through del flusso. Quando i chunk transformer
			// SSE saranno pronti, qui andrà invocato l'encoder front-door per dialect.
			if (log != null) {
				log.warn("LLMOutboundResponseHandler: stream:true, encoder canonical disabilitato (chunk transformer non implementato), pass-through SSE");
			}
			return;
		}
		try {
			CanonicalChatResponse canonical = extractCanonical(context);
			LLMOutboundFrontDoorResponseTransformer transformer = LLMTransformerRegistry.getOutboundFrontDoorResponseTransformer(dialect);
			byte[] body = transformer.transform(canonical);
			// Aggiornamento in-place: stessa motivazione del LLMOutboundRequestHandler.
			// Il flusso GovWay legge l'OpenSPCoop2Message dal connettore già linkato.
			LLMHandlerSupport.applyJsonBody(context.getMessaggio(), body, null);
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMOutboundResponseHandler: errore nella trasformazione canonical → " + dialect.getValue() + ": " + e.getMessage(), e);
		}
	}

	private CanonicalChatResponse extractCanonical(OutResponseContext context) throws HandlerException {
		Object o = context.getPddContext() != null
				? context.getPddContext().getObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_RESPONSE)
				: null;
		if (o instanceof CanonicalChatResponse) {
			return (CanonicalChatResponse) o;
		}
		throw new HandlerException("LLMOutboundResponseHandler: CanonicalChatResponse assente nel PdDContext (l'InResponseHandler LLM non ha popolato il context?)");
	}
}
