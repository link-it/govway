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
package org.openspcoop2.pdd.core.llm.provider;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;

/**
 * Costanti del dominio "LLM Provider": tipologia generic_properties per il
 * lookup delle policy AI Provider configurate in console, e nomi delle property
 * presenti nel form-definition XML.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class Costanti {

	/**
	 * Tipologia usata nella tabella {@code generic_properties} per discriminare
	 * le policy LLM Provider dalle altre tipologie (token, attribute authority, ...).
	 * Allineata a {@link CostantiConfigurazione#GENERIC_PROPERTIES_LLM_PROVIDER}.
	 */
	public static final String TIPOLOGIA = CostantiConfigurazione.GENERIC_PROPERTIES_LLM_PROVIDER;

	/** Tipologia generic_properties per i Modelli LLM logici (es. claude-3-5-haiku). */
	public static final String TIPOLOGIA_MODEL = CostantiConfigurazione.GENERIC_PROPERTIES_LLM_MODEL;

	/** Tipologia generic_properties per i Provider Binding (matrice Provider x Model). */
	public static final String TIPOLOGIA_PROVIDER_BINDING = CostantiConfigurazione.GENERIC_PROPERTIES_LLM_PROVIDER_BINDING;

	/** Property name (XML + generic_property.nome) per il tipo di provider. */
	public static final String LLM_PROVIDER_TYPE = "llmProvider.type";

	/** Property name per la base URL del provider. */
	public static final String LLM_PROVIDER_BASE_URL = "llmProvider.baseUrl";

	/**
	 * Identificativo dell'item {@code <item type="select" name="providerType">} nel form
	 * di configurazione. Il framework {@code IProvider} passa questo {@code id} (il
	 * {@code name=} dell'item XML, non il nome della property) alle callback
	 * {@code getValues}/{@code getLabels}/{@code getDefault}.
	 */
	public static final String ID_PROVIDER_TYPE = "providerType";

	/** Valori supportati di {@link #LLM_PROVIDER_TYPE} (MVP). */
	public static final String LLM_PROVIDER_TYPE_VALUE_ANTHROPIC = "anthropic";
	public static final String LLM_PROVIDER_TYPE_VALUE_OPENAI = "openai";
	public static final String LLM_PROVIDER_TYPE_VALUE_AWS_BEDROCK = "awsBedrock";

	/** Property name (XML + generic_property.nome) per la family del Model. */
	public static final String LLM_MODEL_FAMILY = "llmModel.family";

	/** Property name per la modality del Model. */
	public static final String LLM_MODEL_MODALITY = "llmModel.modality";

	/** Property name per la descrizione del Model. */
	public static final String LLM_MODEL_DESCRIPTION = "llmModel.description";

	/** Id dell'item {@code <item type="select" name="modelFamily">} nel form Model. */
	public static final String ID_MODEL_FAMILY = "modelFamily";

	/** Id dell'item {@code <item type="select" name="modelModality">} nel form Model. */
	public static final String ID_MODEL_MODALITY = "modelModality";

	/** Valori canonici della family del Model (taglio dimensionale, vendor-indipendente). */
	public static final String LLM_MODEL_FAMILY_VALUE_LIGHT = "light";
	public static final String LLM_MODEL_FAMILY_VALUE_BALANCED = "balanced";
	public static final String LLM_MODEL_FAMILY_VALUE_TOP = "top";
	public static final String LLM_MODEL_FAMILY_VALUE_REASONING = "reasoning";
	public static final String LLM_MODEL_FAMILY_VALUE_OTHER = "other";

	/** Valori canonici della modality del Model (tipo di task supportato). */
	public static final String LLM_MODEL_MODALITY_VALUE_CHAT = "chat";
	public static final String LLM_MODEL_MODALITY_VALUE_COMPLETION = "completion";
	public static final String LLM_MODEL_MODALITY_VALUE_EMBEDDING = "embedding";
	public static final String LLM_MODEL_MODALITY_VALUE_MULTIMODAL = "multimodal";
	public static final String LLM_MODEL_MODALITY_VALUE_IMAGE = "image";
	public static final String LLM_MODEL_MODALITY_VALUE_AUDIO = "audio";

	/** Property name per il riferimento (nome) all'istanza Provider del binding. */
	public static final String LLM_PROVIDER_BINDING_PROVIDER = "llmProviderBinding.provider";

	/** Property name per il riferimento (nome) all'istanza Model del binding. */
	public static final String LLM_PROVIDER_BINDING_MODEL = "llmProviderBinding.model";

	/** Id dell'item {@code <item type="select" name="bindingProvider">} nel form Binding (popolato da generic_properties tipologia=llmProvider). */
	public static final String ID_BINDING_PROVIDER = "bindingProvider";

	/** Id dell'item {@code <item type="select" name="bindingModel">} nel form Binding (popolato da generic_properties tipologia=llmModel). */
	public static final String ID_BINDING_MODEL = "bindingModel";

	/** Property name per il vendor model id passato all'API del Provider concreto. */
	public static final String LLM_PROVIDER_BINDING_VENDOR_MODEL_ID = "llmProviderBinding.vendorModelId";

	/** Property name per il prezzo input (USD per {@link #LLM_PROVIDER_BINDING_PRICE_INPUT_DIVISOR} token) del binding. */
	public static final String LLM_PROVIDER_BINDING_PRICE_INPUT = "llmProviderBinding.priceInput";

	/** Property name per il prezzo output (USD per {@link #LLM_PROVIDER_BINDING_PRICE_OUTPUT_DIVISOR} token) del binding. */
	public static final String LLM_PROVIDER_BINDING_PRICE_OUTPUT = "llmProviderBinding.priceOutput";

	/** Property name per il divisore applicato al conteggio token in input nel calcolo del costo
	 * (cost_input = inputTokens / divisor * priceInput). Default {@link #LLM_PROVIDER_BINDING_PRICE_DIVISOR_DEFAULT}. */
	public static final String LLM_PROVIDER_BINDING_PRICE_INPUT_DIVISOR = "llmProviderBinding.priceInputDivisor";

	/** Property name per il divisore applicato al conteggio token in output nel calcolo del costo
	 * (cost_output = outputTokens / divisor * priceOutput). Default {@link #LLM_PROVIDER_BINDING_PRICE_DIVISOR_DEFAULT}. */
	public static final String LLM_PROVIDER_BINDING_PRICE_OUTPUT_DIVISOR = "llmProviderBinding.priceOutputDivisor";

	/** Default per i divisori dei prezzi: 1.000.000 (prezzo per 1M token, convenzione tipica del listino LLM). */
	public static final String LLM_PROVIDER_BINDING_PRICE_DIVISOR_DEFAULT = "1000000";

	/** Property name per la descrizione del binding. */
	public static final String LLM_PROVIDER_BINDING_DESCRIPTION = "llmProviderBinding.description";

	/**
	 * Lunghezza massima del nome di un LLM Provider / Model / Provider Binding.
	 * Coerente con il vincolo VARCHAR(64) delle colonne audit
	 * {@code transazioni_llm.llm_provider/llm_model/llm_provider_binding} e
	 * {@code statistiche_*_llm.llm_provider/llm_model/llm_provider_binding}.
	 */
	public static final int LLM_ENTITY_NAME_MAX_LENGTH = 64;

	private Costanti() {
		// utility class
	}
}
