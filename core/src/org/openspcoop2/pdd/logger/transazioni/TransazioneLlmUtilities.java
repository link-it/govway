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
package org.openspcoop2.pdd.logger.transazioni;

import java.util.Date;

import org.openspcoop2.core.transazioni.TransazioneLlm;
import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalUsage;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.llm.LLMHandlerSupport;
import org.slf4j.Logger;

/**
 * Costruzione del bean {@link TransazioneLlm} a partire dal {@link PdDContext} popolato
 * dagli handler LLM (LLMConnectorResolver / LLMOutboundRequestHandler / LLMInboundResponseHandler).
 * Le scritture su {@code transazioni_llm} avvengono in {@link TracciamentoManager} agganciandosi
 * alla stessa logica INSERT/UPDATE del bean {@code Transazione} principale, in modo che la
 * fase di fatto attiva (IN_REQUEST, OUT_REQUEST, OUT_RESPONSE o POST_OUT_RESPONSE) decida
 * autonomamente il momento del primo persist e dei successivi aggiornamenti.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class TransazioneLlmUtilities {

	private TransazioneLlmUtilities() {
		// utility class
	}

	/**
	 * Costruisce un {@link TransazioneLlm} popolando i campi disponibili sul PdDContext.
	 * Ritorna {@code null} se il PdDContext non e' marcato LLM (skip silenzioso, coerente
	 * col pattern degli handler).
	 */
	public static TransazioneLlm fill(String idTransazione, Date dataIngressoRichiesta, PdDContext pddContext, Logger log) {
		if (idTransazione == null || idTransazione.isEmpty() || pddContext == null) {
			return null;
		}
		String providerType = LLMHandlerSupport.getLLMProvider(pddContext);
		String modelName    = LLMHandlerSupport.getLLMModelName(pddContext);
		String bindingName  = LLMHandlerSupport.getLLMProviderBindingName(pddContext);
		// Se nessuna delle tre identita' LLM e' nota, non e' una transazione LLM: skip.
		if ((providerType == null || providerType.isEmpty())
				&& (modelName == null || modelName.isEmpty())
				&& (bindingName == null || bindingName.isEmpty())) {
			return null;
		}

		TransazioneLlm bean = new TransazioneLlm();
		bean.setIdTransazione(idTransazione);
		bean.setDataIngressoRichiesta(dataIngressoRichiesta);
		bean.setLlmProvider(providerType);
		bean.setLlmModel(modelName);
		bean.setLlmProviderBinding(bindingName);

		// Sync: usage dentro la canonical response. Streaming: usage accumulato chunk-by-chunk
		// dal ChunkTransformInputStream via observer (vedi LLMHandlerSupport.accumulateLLMStreamUsage).
		CanonicalUsage usage = null;
		CanonicalChatResponse canonical = LLMHandlerSupport.getLLMCanonicalResponse(pddContext);
		if (canonical != null && canonical.getUsage() != null) {
			usage = canonical.getUsage();
		}
		if (usage == null) {
			usage = LLMHandlerSupport.getLLMStreamUsage(pddContext);
		}
		Long inputTokens = null;
		Long outputTokens = null;
		if (usage != null) {
			if (usage.getInputTokens() != null) {
				inputTokens = Long.valueOf(usage.getInputTokens().longValue());
				bean.setTokenInput(inputTokens);
			}
			if (usage.getOutputTokens() != null) {
				outputTokens = Long.valueOf(usage.getOutputTokens().longValue());
				bean.setTokenOutput(outputTokens);
			}
		}

		Double priceInput = LLMHandlerSupport.getLLMPriceInput(pddContext);
		Double priceOutput = LLMHandlerSupport.getLLMPriceOutput(pddContext);
		long divisorInput = LLMHandlerSupport.getLLMPriceInputDivisor(pddContext);
		long divisorOutput = LLMHandlerSupport.getLLMPriceOutputDivisor(pddContext);
		double cost = 0d;
		if (inputTokens != null && priceInput != null && divisorInput > 0) {
			cost += (inputTokens.doubleValue() / divisorInput) * priceInput.doubleValue();
		}
		if (outputTokens != null && priceOutput != null && divisorOutput > 0) {
			cost += (outputTokens.doubleValue() / divisorOutput) * priceOutput.doubleValue();
		}
		bean.setCostEstimated(cost);

		if (log != null && log.isDebugEnabled()) {
			log.debug("TransazioneLlm fill: idTransazione={} provider={} model={} binding={} tokenInput={} tokenOutput={} cost={}",
					idTransazione, providerType, modelName, bindingName, inputTokens, outputTokens, cost);
		}
		return bean;
	}

}
