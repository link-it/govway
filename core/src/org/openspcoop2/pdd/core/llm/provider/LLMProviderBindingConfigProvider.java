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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.mvc.properties.provider.ExternalResources;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Provider IProvider per la form-definition {@code binding.xml}.
 * Fornisce alla console di gestione i metadati delle proprietà configurabili
 * di un'istanza LLM Provider Binding (incrocio Provider × Model + pricing):
 * <ul>
 *   <li>validazione del nome dell'istanza (regex standard policy)</li>
 *   <li>popolamento dinamico delle select Provider/Model leggendo i
 *       {@code generic_properties} di tipologia {@code llmProvider} /
 *       {@code llmModel} (via {@link ExternalResources#getConnection()})</li>
 *   <li>tooltip esplicativo (i) per il campo Vendor Model Id via {@link ProviderInfo}</li>
 *   <li>validazione del vendor model id (non vuoto)</li>
 *   <li>validazione del pricing input/output (numerico non negativo)</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMProviderBindingConfigProvider implements IProvider {

	/**
	 * Valore "non selezionato" standard della console GovWay (vedi
	 * {@code Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO}). Viene sempre prependuto
	 * come prima opzione delle select Provider/Model: garantisce sia che il framework
	 * MVC properties trovi almeno un value (quando il DB e' vuoto) sia che il
	 * salvataggio possa essere intercettato in {@link #validate} se l'utente non ha
	 * scelto un'istanza reale.
	 */
	private static final String NON_SELEZIONATO = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO;

	@Override
	public void validateId(String name) throws ProviderException, ProviderValidationException {
		if (name == null || StringUtils.isEmpty(name)) {
			throw new ProviderValidationException("Deve essere indicato un nome che identifica il LLM Provider Binding");
		}
		if (name.contains(" ")) {
			throw new ProviderValidationException("Il nome associato al LLM Provider Binding non deve contenere spazi");
		}
		if (name.length() > Costanti.LLM_ENTITY_NAME_MAX_LENGTH) {
			throw new ProviderValidationException("Il nome associato al LLM Provider Binding non può superare "
					+ Costanti.LLM_ENTITY_NAME_MAX_LENGTH + " caratteri");
		}
		boolean match;
		try {
			match = RegularExpressionEngine.isMatch(name, "^[_A-Za-z][\\-_A-Za-z0-9]*$");
		} catch (Exception e) {
			throw new ProviderException(e.getMessage(), e);
		}
		if (!match) {
			throw new ProviderValidationException("Il nome associato al LLM Provider Binding può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , e '-'");
		}
	}

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		Properties pDefault = mapProperties != null ? MultiPropertiesUtilities.getDefaultProperties(mapProperties) : null;
		if (pDefault == null) {
			throw new ProviderValidationException("Proprietà di default non trovate per il LLM Provider Binding");
		}
		validateRef(pDefault, Costanti.LLM_PROVIDER_BINDING_PROVIDER, "LLM Provider");
		validateRef(pDefault, Costanti.LLM_PROVIDER_BINDING_MODEL, "LLM Model");
		validateVendorModelId(pDefault);
		validatePrice(pDefault, Costanti.LLM_PROVIDER_BINDING_PRICE_INPUT, "Prezzo Input");
		validatePrice(pDefault, Costanti.LLM_PROVIDER_BINDING_PRICE_OUTPUT, "Prezzo Output");
		validateDivisor(pDefault, Costanti.LLM_PROVIDER_BINDING_PRICE_INPUT_DIVISOR, "Token Input / Divisore");
		validateDivisor(pDefault, Costanti.LLM_PROVIDER_BINDING_PRICE_OUTPUT_DIVISOR, "Token Output / Divisore");
		validatePiiMasking(pDefault);
	}

	private void validatePiiMasking(Properties pDefault) throws ProviderValidationException {
		String enabled = pDefault.getProperty(Costanti.LLM_PROVIDER_BINDING_PII_ENABLED);
		if (!"true".equalsIgnoreCase(enabled)) {
			return; // PII Masking disabilitato: niente da validare
		}
		// Almeno un tipo di Regola PII deve essere selezionato.
		String types = pDefault.getProperty(Costanti.LLM_PROVIDER_BINDING_PII_TYPE);
		if (types == null || types.trim().isEmpty()) {
			throw new ProviderValidationException("Selezionare almeno un Tipo di Regola PII da applicare");
		}
		// Se è selezionato il tipo RegExp e non si applicano tutte le Regole, occorre selezionarne almeno una.
		if (csvContains(types, org.openspcoop2.pdd.core.llm.pii.Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP)) {
			String refsAll = pDefault.getProperty(Costanti.LLM_PROVIDER_BINDING_PII_REGEXP_REFS_ALL);
			if (!"true".equalsIgnoreCase(refsAll)) {
				String refs = pDefault.getProperty(Costanti.LLM_PROVIDER_BINDING_PII_REGEXP_REFS);
				if (refs == null || refs.trim().isEmpty()) {
					throw new ProviderValidationException("Selezionare almeno una Regola PII RegExp, oppure abilitare 'Tutte le Regole RegExp'");
				}
			}
		}
	}

	private static boolean csvContains(String csv, String value) {
		if (csv == null || value == null) {
			return false;
		}
		for (String t : csv.split(",")) {
			if (value.equals(t.trim())) {
				return true;
			}
		}
		return false;
	}

	private void validateRef(Properties pDefault, String propertyName, String labelForError) throws ProviderValidationException {
		String value = pDefault.getProperty(propertyName);
		if (value == null || StringUtils.isEmpty(value) || NON_SELEZIONATO.equals(value)) {
			throw new ProviderValidationException("Indicare il " + labelForError + " associato al Binding");
		}
		if (value.length() > Costanti.LLM_ENTITY_NAME_MAX_LENGTH) {
			throw new ProviderValidationException("Il nome del " + labelForError + " non può superare "
					+ Costanti.LLM_ENTITY_NAME_MAX_LENGTH + " caratteri");
		}
	}

	private void validateVendorModelId(Properties pDefault) throws ProviderValidationException {
		String vendorId = pDefault.getProperty(Costanti.LLM_PROVIDER_BINDING_VENDOR_MODEL_ID);
		if (vendorId == null || StringUtils.isEmpty(vendorId.trim())) {
			throw new ProviderValidationException("Indicare il Vendor Model Id (l'identificativo del modello come richiesto dall'API del Provider concreto)");
		}
	}

	private void validatePrice(Properties pDefault, String propertyName, String labelForError) throws ProviderValidationException {
		String value = pDefault.getProperty(propertyName);
		if (value == null || StringUtils.isEmpty(value.trim())) {
			return; // pricing opzionale
		}
		double parsed;
		try {
			parsed = Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			throw new ProviderValidationException(labelForError + ": valore non numerico ('" + value + "')");
		}
		if (parsed < 0) {
			throw new ProviderValidationException(labelForError + ": valore non può essere negativo");
		}
	}

	private void validateDivisor(Properties pDefault, String propertyName, String labelForError) throws ProviderValidationException {
		String value = pDefault.getProperty(propertyName);
		if (value == null || StringUtils.isEmpty(value.trim())) {
			return; // assente → si applica il default Costanti.LLM_PROVIDER_BINDING_PRICE_DIVISOR_DEFAULT al runtime
		}
		long parsed;
		try {
			parsed = Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			throw new ProviderValidationException(labelForError + ": valore non numerico intero ('" + value + "')");
		}
		if (parsed <= 0) {
			throw new ProviderValidationException(labelForError + ": deve essere un intero positivo");
		}
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		return getValues(id, null);
	}

	@Override
	public List<String> getValues(String id, ExternalResources externalResources) throws ProviderException {
		if (Costanti.ID_BINDING_PROVIDER.equals(id)) {
			return prependNonSelezionato(loadGenericPropertiesNames(externalResources, Costanti.TIPOLOGIA));
		}
		if (Costanti.ID_BINDING_MODEL.equals(id)) {
			return prependNonSelezionato(loadGenericPropertiesNames(externalResources, Costanti.TIPOLOGIA_MODEL));
		}
		if (Costanti.ID_BINDING_PII_TYPE.equals(id)) {
			// multiSelect: distinct dei tipi detector effettivamente censiti tra le Regole PII.
			return loadDistinctPiiDetectorTypes(externalResources);
		}
		if (Costanti.ID_BINDING_PII_REGEXP_REFS.equals(id)) {
			// multiSelect: nomi delle Regole PII (oggi tutte di tipo RegExp; nessun valore "non selezionato").
			return loadGenericPropertiesNames(externalResources, org.openspcoop2.pdd.core.llm.pii.Costanti.TIPOLOGIA);
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		return getLabels(id, null);
	}

	@Override
	public List<String> getLabels(String id, ExternalResources externalResources) throws ProviderException {
		if (Costanti.ID_BINDING_PII_TYPE.equals(id)) {
			// Per i tipi detector mostriamo una label leggibile (es. regexp -> "Espressioni Regolari").
			List<String> labels = new ArrayList<>();
			for (String v : getValues(id, externalResources)) {
				labels.add(piiDetectorTypeLabel(v));
			}
			return labels;
		}
		// Negli altri casi la label coincide con il valore (nome dell'entità).
		return getValues(id, externalResources);
	}

	private static String piiDetectorTypeLabel(String detectorType) {
		return org.openspcoop2.pdd.core.llm.pii.LLMPiiMaskingConfigProvider.detectorTypeLabel(detectorType);
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if (Costanti.ID_BINDING_PROVIDER.equals(id) || Costanti.ID_BINDING_MODEL.equals(id)) {
			return NON_SELEZIONATO;
		}
		return null;
	}

	private static List<String> prependNonSelezionato(List<String> values) {
		List<String> result = new ArrayList<>(values.size() + 1);
		result.add(NON_SELEZIONATO);
		result.addAll(values);
		return result;
	}

	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException {
		if ("bindingVendorModelId".equals(id)) {
			ProviderInfo info = new ProviderInfo();
			info.setHeaderBody("Identificativo del modello come richiesto dall'API del Provider concreto. Esempi:");
			List<String> body = new ArrayList<>();
			body.add("Anthropic direct: claude-3-5-haiku-20241022");
			body.add("AWS Bedrock: anthropic.claude-3-5-haiku-20241022-v1:0");
			body.add("OpenAI: gpt-4o-mini");
			info.setListBody(body);
			return info;
		}
		return null;
	}

	private List<String> loadGenericPropertiesNames(ExternalResources externalResources, String tipologia) throws ProviderException {
		List<String> nomi = new ArrayList<>();
		if (externalResources == null || externalResources.getConnection() == null) {
			return nomi;
		}
		Connection con = externalResources.getConnection();
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(externalResources.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_NAME);
			sqlQueryObject.addWhereCondition("tipologia = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_NAME);
			sqlQueryObject.setSortType(true);
			String sql = sqlQueryObject.createSQLQuery();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, tipologia);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						nomi.add(rs.getString(CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_NAME));
					}
				}
			}
		} catch (Exception e) {
			throw new ProviderException("Errore lettura nomi tipologia '" + tipologia + "': " + e.getMessage(), e);
		}
		return nomi;
	}

	/** Distinct dei valori della property {@code llmPiiMasking.detectorType} tra tutte le Regole PII censite. */
	private List<String> loadDistinctPiiDetectorTypes(ExternalResources externalResources) throws ProviderException {
		List<String> tipi = new ArrayList<>();
		if (externalResources == null || externalResources.getConnection() == null) {
			return tipi;
		}
		Connection con = externalResources.getConnection();
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(externalResources.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_GENERIC_PROPERTY, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_GENERIC_PROPERTIES + ".tipologia = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_GENERIC_PROPERTY + "." + CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ID_PROPS
					+ " = " + CostantiDB.CONFIG_GENERIC_PROPERTIES + "." + CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_ID);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_GENERIC_PROPERTY + "." + CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME + " = ?");
			String sql = sqlQueryObject.createSQLQuery();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, org.openspcoop2.pdd.core.llm.pii.Costanti.TIPOLOGIA);
				ps.setString(2, org.openspcoop2.pdd.core.llm.pii.Costanti.LLM_PII_DETECTOR_TYPE);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String v = rs.getString(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE);
						if (v != null && !v.isEmpty()) {
							tipi.add(v);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ProviderException("Errore lettura dei tipi detector PII: " + e.getMessage(), e);
		}
		return tipi;
	}
}
