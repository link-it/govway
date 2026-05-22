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

	private Costanti() {
		// utility class
	}
}
