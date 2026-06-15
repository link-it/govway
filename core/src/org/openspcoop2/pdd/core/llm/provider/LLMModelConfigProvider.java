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
 * Provider IProvider per la form-definition {@code model.xml}.
 * Fornisce alla console di gestione i metadati delle proprietà configurabili
 * di un'istanza LLM Model:
 * <ul>
 *   <li>validazione del nome dell'istanza (regex standard policy)</li>
 *   <li>validazione dei campi del form (family, modality)</li>
 *   <li>popolamento delle select (family/modality canonizzate)</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMModelConfigProvider implements IProvider {

	private static final List<String> SUPPORTED_FAMILIES = Arrays.asList(
			Costanti.LLM_MODEL_FAMILY_VALUE_LIGHT,
			Costanti.LLM_MODEL_FAMILY_VALUE_BALANCED,
			Costanti.LLM_MODEL_FAMILY_VALUE_TOP,
			Costanti.LLM_MODEL_FAMILY_VALUE_REASONING,
			Costanti.LLM_MODEL_FAMILY_VALUE_OTHER);

	private static final List<String> SUPPORTED_FAMILY_LABELS = Arrays.asList(
			"Light",
			"Balanced",
			"Top",
			"Reasoning",
			"Other");

	private static final List<String> SUPPORTED_MODALITIES = Arrays.asList(
			Costanti.LLM_MODEL_MODALITY_VALUE_CHAT,
			Costanti.LLM_MODEL_MODALITY_VALUE_COMPLETION,
			Costanti.LLM_MODEL_MODALITY_VALUE_EMBEDDING,
			Costanti.LLM_MODEL_MODALITY_VALUE_MULTIMODAL,
			Costanti.LLM_MODEL_MODALITY_VALUE_IMAGE,
			Costanti.LLM_MODEL_MODALITY_VALUE_AUDIO);

	private static final List<String> SUPPORTED_MODALITY_LABELS = Arrays.asList(
			"Chat",
			"Completion",
			"Embedding",
			"Multimodal",
			"Image",
			"Audio");

	@Override
	public void validateId(String name) throws ProviderException, ProviderValidationException {
		if (name == null || StringUtils.isEmpty(name)) {
			throw new ProviderValidationException("Deve essere indicato un nome che identifica il LLM Model");
		}
		if (name.contains(" ")) {
			throw new ProviderValidationException("Il nome associato al LLM Model non deve contenere spazi");
		}
		if (name.length() > Costanti.LLM_ENTITY_NAME_MAX_LENGTH) {
			throw new ProviderValidationException("Il nome associato al LLM Model non può superare "
					+ Costanti.LLM_ENTITY_NAME_MAX_LENGTH + " caratteri");
		}
		boolean match;
		try {
			match = RegularExpressionEngine.isMatch(name, "^[_A-Za-z][\\-_A-Za-z0-9]*$");
		} catch (Exception e) {
			throw new ProviderException(e.getMessage(), e);
		}
		if (!match) {
			throw new ProviderValidationException("Il nome associato al LLM Model può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , e '-'");
		}
	}

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		Properties pDefault = mapProperties != null ? MultiPropertiesUtilities.getDefaultProperties(mapProperties) : null;
		if (pDefault == null) {
			throw new ProviderValidationException("Proprietà di default non trovate per il LLM Model");
		}
		validateFamily(pDefault);
		validateModality(pDefault);
	}

	private void validateFamily(Properties pDefault) throws ProviderValidationException {
		String family = pDefault.getProperty(Costanti.LLM_MODEL_FAMILY);
		if (family == null || StringUtils.isEmpty(family)) {
			throw new ProviderValidationException("Indicare la Family del LLM Model");
		}
		if (!SUPPORTED_FAMILIES.contains(family)) {
			throw new ProviderValidationException("Family non supportata: " + family
					+ " (supportate: " + SUPPORTED_FAMILIES + ")");
		}
	}

	private void validateModality(Properties pDefault) throws ProviderValidationException {
		String modality = pDefault.getProperty(Costanti.LLM_MODEL_MODALITY);
		if (modality == null || StringUtils.isEmpty(modality)) {
			throw new ProviderValidationException("Indicare la Modality del LLM Model");
		}
		if (!SUPPORTED_MODALITIES.contains(modality)) {
			throw new ProviderValidationException("Modality non supportata: " + modality
					+ " (supportate: " + SUPPORTED_MODALITIES + ")");
		}
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if (Costanti.ID_MODEL_FAMILY.equals(id)) {
			return new ArrayList<>(SUPPORTED_FAMILIES);
		}
		if (Costanti.ID_MODEL_MODALITY.equals(id)) {
			return new ArrayList<>(SUPPORTED_MODALITIES);
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if (Costanti.ID_MODEL_FAMILY.equals(id)) {
			return new ArrayList<>(SUPPORTED_FAMILY_LABELS);
		}
		if (Costanti.ID_MODEL_MODALITY.equals(id)) {
			return new ArrayList<>(SUPPORTED_MODALITY_LABELS);
		}
		return new ArrayList<>();
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if (Costanti.ID_MODEL_FAMILY.equals(id)) {
			return Costanti.LLM_MODEL_FAMILY_VALUE_BALANCED;
		}
		if (Costanti.ID_MODEL_MODALITY.equals(id)) {
			return Costanti.LLM_MODEL_MODALITY_VALUE_CHAT;
		}
		return null;
	}
}
