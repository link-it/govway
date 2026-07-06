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
package org.openspcoop2.pdd.core.llm.pii;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * Provider IProvider per la form-definition {@code piiMasking.xml}.
 * Fornisce alla console di gestione i metadati delle proprietà configurabili di una
 * Regola PII:
 * <ul>
 *   <li>validazione del nome dell'istanza (regex standard policy)</li>
 *   <li>popolamento delle select tipo-detector e categoria</li>
 *   <li>pre-compilazione della textarea delle espressioni regolari in base alla
 *       categoria selezionata ({@link #dynamicUpdate}), attingendo a
 *       {@link PiiDefaultPatterns}; le modifiche dell'utente non vengono sovrascritte</li>
 *   <li>validazione dei campi del form (categoria supportata, regex compilabili)</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMPiiMaskingConfigProvider implements IProvider {

	private static final List<String> SUPPORTED_DETECTOR_TYPES = Arrays.asList(
			Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP,
			Costanti.PII_DETECTOR_TYPE_VALUE_PLUGIN);

	private static final List<String> SUPPORTED_DETECTOR_TYPE_LABELS = Arrays.asList(
			"Espressioni Regolari",
			"Personalizzato");

	// Categorie ordinate per label (italiano), con "Personalizzato" sempre in fondo.
	private static final List<String> SUPPORTED_CATEGORIES = Arrays.asList(
			Costanti.PII_CATEGORY_VALUE_CARD,
			Costanti.PII_CATEGORY_VALUE_CF,
			Costanti.PII_CATEGORY_VALUE_DOC_ID,
			Costanti.PII_CATEGORY_VALUE_EMAIL,
			Costanti.PII_CATEGORY_VALUE_IBAN,
			Costanti.PII_CATEGORY_VALUE_IP,
			Costanti.PII_CATEGORY_VALUE_MAC,
			Costanti.PII_CATEGORY_VALUE_PIVA,
			Costanti.PII_CATEGORY_VALUE_SECRET,
			Costanti.PII_CATEGORY_VALUE_PLATE,
			Costanti.PII_CATEGORY_VALUE_PHONE,
			Costanti.PII_CATEGORY_VALUE_USERNAME_PATH,
			Costanti.PII_CATEGORY_VALUE_CUSTOM);

	private static final List<String> SUPPORTED_CATEGORY_LABELS = Arrays.asList(
			"Carta di credito",
			"Codice Fiscale",
			"Documento d'identità",
			"Email",
			"IBAN",
			"Indirizzo IP",
			"MAC Address",
			"Partita IVA",
			"Segreti / Credenziali",
			"Targa",
			"Telefono",
			"Username (path)",
			"Altro");

	/** Tipi detector supportati (per i filtri di ricerca). */
	public static List<String> supportedDetectorTypes() {
		return new ArrayList<>(SUPPORTED_DETECTOR_TYPES);
	}
	/** Label leggibile di un tipo detector (es. regexp -> "Espressioni Regolari"). */
	public static String detectorTypeLabel(String detectorType) {
		int i = SUPPORTED_DETECTOR_TYPES.indexOf(detectorType);
		return i >= 0 ? SUPPORTED_DETECTOR_TYPE_LABELS.get(i) : detectorType;
	}
	/** Categorie supportate per un dato tipo detector (oggi tutte, indipendenti dal tipo). */
	public static List<String> supportedCategories(String detectorType) {
		return new ArrayList<>(SUPPORTED_CATEGORIES);
	}
	/** Label leggibile di una categoria (es. cf -> "Codice Fiscale"). */
	public static String categoryLabel(String category) {
		int i = SUPPORTED_CATEGORIES.indexOf(category);
		return i >= 0 ? SUPPORTED_CATEGORY_LABELS.get(i) : category;
	}

	@Override
	public void validateId(String name) throws ProviderException, ProviderValidationException {
		if (name == null || StringUtils.isEmpty(name)) {
			throw new ProviderValidationException("Deve essere indicato un nome che identifica la Regola PII");
		}
		if (name.contains(" ")) {
			throw new ProviderValidationException("Il nome associato alla Regola PII non deve contenere spazi");
		}
		if (name.length() > Costanti.LLM_PII_ENTITY_NAME_MAX_LENGTH) {
			throw new ProviderValidationException("Il nome associato alla Regola PII non può superare "
					+ Costanti.LLM_PII_ENTITY_NAME_MAX_LENGTH + " caratteri");
		}
		boolean match;
		try {
			match = RegularExpressionEngine.isMatch(name, "^[_A-Za-z][\\-_A-Za-z0-9]*$");
		} catch (Exception e) {
			throw new ProviderException(e.getMessage(), e);
		}
		if (!match) {
			throw new ProviderValidationException("Il nome associato alla Regola PII può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , e '-'");
		}
	}

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		Properties pDefault = mapProperties != null ? MultiPropertiesUtilities.getDefaultProperties(mapProperties) : null;
		if (pDefault == null) {
			throw new ProviderValidationException("Proprietà di default non trovate per la Regola PII");
		}
		String detectorType = validateDetectorType(pDefault);
		validateCategory(pDefault);
		if (Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP.equals(detectorType)) {
			validatePatterns(pDefault);
		}
		else if (Costanti.PII_DETECTOR_TYPE_VALUE_PLUGIN.equals(detectorType)) {
			validatePluginClassName(pDefault);
		}
	}

	private void validatePluginClassName(Properties pDefault) throws ProviderValidationException {
		String className = pDefault.getProperty(Costanti.LLM_PII_PLUGIN_CLASS_NAME);
		if (className == null || StringUtils.isEmpty(className.trim())) {
			throw new ProviderValidationException("Indicare la Classe Plugin per la Regola PII di tipo Personalizzato");
		}
		// Validazione formale del nome classe Java (package.Classe), senza caricarla (potrebbe non essere sul classpath console).
		if (!className.trim().matches("^[A-Za-z_$][A-Za-z0-9_$]*(\\.[A-Za-z_$][A-Za-z0-9_$]*)+$")) {
			throw new ProviderValidationException("La Classe Plugin non è un nome di classe Java valido ('" + className + "')");
		}
	}

	private String validateDetectorType(Properties pDefault) throws ProviderValidationException {
		String detectorType = pDefault.getProperty(Costanti.LLM_PII_DETECTOR_TYPE);
		if (detectorType == null || StringUtils.isEmpty(detectorType)) {
			throw new ProviderValidationException("Indicare il Tipo della Regola PII");
		}
		if (!SUPPORTED_DETECTOR_TYPES.contains(detectorType)) {
			throw new ProviderValidationException("Tipo non supportato: " + detectorType
					+ " (supportati: " + SUPPORTED_DETECTOR_TYPES + ")");
		}
		return detectorType;
	}

	private void validateCategory(Properties pDefault) throws ProviderValidationException {
		String category = pDefault.getProperty(Costanti.LLM_PII_CATEGORY);
		if (category == null || StringUtils.isEmpty(category)) {
			throw new ProviderValidationException("Indicare la Categoria della Regola PII");
		}
		if (!SUPPORTED_CATEGORIES.contains(category)) {
			throw new ProviderValidationException("Categoria non supportata: " + category
					+ " (supportate: " + SUPPORTED_CATEGORIES + ")");
		}
	}

	private void validatePatterns(Properties pDefault) throws ProviderValidationException {
		String patterns = pDefault.getProperty(Costanti.LLM_PII_PATTERNS);
		if (patterns == null || StringUtils.isEmpty(patterns.trim())) {
			throw new ProviderValidationException("Indicare almeno una espressione regolare per la Regola PII");
		}
		boolean atLeastOne = false;
		for (String line : patterns.split("\\r?\\n")) {
			String regex = line.trim();
			if (regex.isEmpty()) {
				continue;
			}
			atLeastOne = true;
			try {
				Pattern.compile(regex);
			} catch (PatternSyntaxException e) {
				throw new ProviderValidationException("Espressione regolare non valida ('" + regex + "'): " + e.getDescription());
			}
		}
		if (!atLeastOne) {
			throw new ProviderValidationException("Indicare almeno una espressione regolare per la Regola PII");
		}
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if (Costanti.ID_PII_DETECTOR_TYPE.equals(id)) {
			return new ArrayList<>(SUPPORTED_DETECTOR_TYPES);
		}
		if (Costanti.ID_PII_CATEGORY.equals(id)) {
			return new ArrayList<>(SUPPORTED_CATEGORIES);
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if (Costanti.ID_PII_DETECTOR_TYPE.equals(id)) {
			return new ArrayList<>(SUPPORTED_DETECTOR_TYPE_LABELS);
		}
		if (Costanti.ID_PII_CATEGORY.equals(id)) {
			return new ArrayList<>(SUPPORTED_CATEGORY_LABELS);
		}
		return new ArrayList<>();
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if (Costanti.ID_PII_DETECTOR_TYPE.equals(id)) {
			return Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP;
		}
		if (Costanti.ID_PII_CATEGORY.equals(id)) {
			return Costanti.PII_CATEGORY_VALUE_EMAIL;
		}
		return null;
	}

	/**
	 * Pre-compila la textarea delle espressioni regolari in base alla categoria selezionata.
	 * <p>
	 * La sostituzione avviene SOLO se la textarea è vuota oppure contiene ancora i default
	 * di una categoria nota (cioè l'utente non l'ha personalizzata): così cambiando categoria
	 * le regex si aggiornano, ma le modifiche manuali dell'utente non vengono sovrascritte.
	 */
	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		if (item == null || !Costanti.ID_PII_PATTERNS.equals(item.getName())) {
			return actualValue;
		}
		String category = mapNameValue != null ? mapNameValue.get(Costanti.ID_PII_CATEGORY) : null;
		if (category == null) {
			return actualValue;
		}
		if (PiiDefaultPatterns.hasDefaults(category)) {
			// categoria con default: pre-compila se la textarea è vuota o contiene ancora i
			// default di un'altra categoria (così cambiando categoria le regex si aggiornano);
			// le modifiche manuali dell'utente non vengono sovrascritte.
			if (isBlankOrKnownDefault(actualValue)) {
				return PiiDefaultPatterns.getPatternsAsText(category);
			}
			return actualValue;
		}
		// categoria 'Personalizzato' (nessun default): azzera la textarea se non è stata
		// personalizzata dall'utente (cioè è vuota o contiene ancora i default di un'altra categoria).
		if (isBlankOrKnownDefault(actualValue)) {
			return "";
		}
		return actualValue;
	}

	/** true se il valore è vuoto o coincide con le regex di default di una qualsiasi categoria nota (= non personalizzato). */
	private boolean isBlankOrKnownDefault(String value) {
		if (value == null || value.trim().isEmpty()) {
			return true;
		}
		String normalized = value.trim();
		for (String cat : PiiDefaultPatterns.getCategories()) {
			if (normalized.equals(PiiDefaultPatterns.getPatternsAsText(cat).trim())) {
				return true;
			}
		}
		return false;
	}
}
