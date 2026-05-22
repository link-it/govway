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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * Provider IProvider per la form-definition {@code llmProviderDefinition.xml}.
 * Fornisce alla console di gestione i metadati delle proprietà configurabili
 * di un'istanza LLM Provider:
 * <ul>
 *   <li>validazione del nome dell'istanza (regex standard policy)</li>
 *   <li>validazione dei campi del form (tipo provider, base URL)</li>
 *   <li>popolamento delle select (tipi provider supportati)</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMProviderConfigProvider implements IProvider {

	private static final List<String> SUPPORTED_PROVIDER_TYPES = Arrays.asList(
			Costanti.LLM_PROVIDER_TYPE_VALUE_ANTHROPIC,
			Costanti.LLM_PROVIDER_TYPE_VALUE_OPENAI,
			Costanti.LLM_PROVIDER_TYPE_VALUE_AWS_BEDROCK);

	private static final List<String> SUPPORTED_PROVIDER_LABELS = Arrays.asList(
			"Anthropic",
			"OpenAI",
			"AWS Bedrock");

	@Override
	public void validateId(String name) throws ProviderException, ProviderValidationException {
		if (name == null || StringUtils.isEmpty(name)) {
			throw new ProviderValidationException("Deve essere indicato un nome che identifica il LLM Provider");
		}
		if (name.contains(" ")) {
			throw new ProviderValidationException("Il nome associato al LLM Provider non deve contenere spazi");
		}
		boolean match;
		try {
			match = RegularExpressionEngine.isMatch(name, "^[_A-Za-z][\\-_A-Za-z0-9]*$");
		} catch (Exception e) {
			throw new ProviderException(e.getMessage(), e);
		}
		if (!match) {
			throw new ProviderValidationException("Il nome associato al LLM Provider può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , e '-'");
		}
	}

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		Properties pDefault = mapProperties != null ? MultiPropertiesUtilities.getDefaultProperties(mapProperties) : null;
		if (pDefault == null) {
			throw new ProviderValidationException("Proprietà di default non trovate per il LLM Provider");
		}
		validateProviderType(pDefault);
		validateBaseUrl(pDefault);
	}

	private void validateProviderType(Properties pDefault) throws ProviderValidationException {
		String type = pDefault.getProperty(Costanti.LLM_PROVIDER_TYPE);
		if (type == null || StringUtils.isEmpty(type)) {
			throw new ProviderValidationException("Indicare il tipo di LLM Provider");
		}
		if (!SUPPORTED_PROVIDER_TYPES.contains(type)) {
			throw new ProviderValidationException("Tipo di LLM Provider non supportato: " + type
					+ " (supportati: " + SUPPORTED_PROVIDER_TYPES + ")");
		}
	}

	private void validateBaseUrl(Properties pDefault) throws ProviderValidationException {
		String baseUrl = pDefault.getProperty(Costanti.LLM_PROVIDER_BASE_URL);
		if (baseUrl == null || StringUtils.isEmpty(baseUrl)) {
			throw new ProviderValidationException("Indicare la Base URL del LLM Provider");
		}
		String url = baseUrl.trim();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			throw new ProviderValidationException("La Base URL del LLM Provider deve iniziare con http:// o https://");
		}
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if (Costanti.ID_PROVIDER_TYPE.equals(id)) {
			return new ArrayList<>(SUPPORTED_PROVIDER_TYPES);
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if (Costanti.ID_PROVIDER_TYPE.equals(id)) {
			return new ArrayList<>(SUPPORTED_PROVIDER_LABELS);
		}
		return new ArrayList<>();
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if (Costanti.ID_PROVIDER_TYPE.equals(id)) {
			return Costanti.LLM_PROVIDER_TYPE_VALUE_ANTHROPIC;
		}
		return null;
	}
}
