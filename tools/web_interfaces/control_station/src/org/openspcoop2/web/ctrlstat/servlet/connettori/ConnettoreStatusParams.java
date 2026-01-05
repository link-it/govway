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

package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Classe che contiene i parametri per il connettore Status
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreStatusParams {	
	private boolean parsingErrors = false;
	
	// parametri connettore status
	private String statusResponseType;
	private Boolean testConnectivity;
	private Boolean testStatistics;
	private String period;
	private Integer periodValue;
	private Integer statLifetime;
	
	private static final String CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI = "ModI";
	private static final String CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_PERSONALIZZATO = "Personalizzato";
	
	private static final List<String> possibleResponseTypeValues = List.of(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI, CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_PERSONALIZZATO);
	private static final List<String> possibleResponseTypeLabels = List.of(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI, ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_PERSONALIZZATO);
	private static final List<String> possiblePersonalizedResponseTypeValues = List.of("vuoto", "xml", "json", "text");
	private static final List<String> possiblePersonalizedResponseTypeLabels = List.of(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_CUSTOM_EMPTY, 
			ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_CUSTOM_XML, 
			ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_CUSTOM_JSON, 
			ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_CUSTOM_TEXT);
	private static final List<String> possiblePeriods = Arrays.asList(TipoPeriodoStatistico.toArray());
	
	private static final String SEPARATOR_VALORE_NON_CORRETTO = ", valore non corretto, possibli valori: ";
	
	private void sendError(PageData pd, String msg) {
		if (pd != null)
			pd.setMessage(msg);
		this.parsingError(true);
	}
	
	
	/**
	 * Funzione che server per controllare e riempire i dati del connettore dai parametri
	 * @param helper, ConsoleHelper per ottenere tutti i parametri del connettore
	 * @param map, equivalente all'helper ma in formato Map
	 * @param serviceBinding, tipo di servizio utile per il checking
	 * @param pd, PageData utile per riportare gli errori di parsing alla console
	 * @throws DriverControlStationException
	 */
	protected void fillFrom(ConsoleHelper helper, Map<String, String> map, ServiceBinding serviceBinding, PageData pd) throws DriverControlStationException {
		String responseType = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_RESPONSE_TYPE);
		String personalizedType = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_RESPONSE_PERSONALIZED);
		
		if (responseType != null && !possibleResponseTypeValues.contains(responseType))
			sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT + SEPARATOR_VALORE_NON_CORRETTO + String.join(",", possibleResponseTypeValues));
		if (personalizedType != null && !possiblePersonalizedResponseTypeValues.contains(personalizedType))
			sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT + SEPARATOR_VALORE_NON_CORRETTO + String.join(",", possiblePersonalizedResponseTypeValues));
		
		String type = (responseType == null || responseType.equals(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI)) ? responseType : personalizedType;
		
		if (serviceBinding != null && serviceBinding.equals(ServiceBinding.SOAP) && type != null && !type.equals(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI))
			sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT + " valore non corretto per binding di tipo " + serviceBinding);
			
		String testConnectivityRaw = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_TEST_CONNECTIVITY);
		String testStatisticsRaw = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_TEST_STATISTICS);
		
		
		Integer periodValueParsed = null;
		Integer statLifetimeParsed = null;
		String periodRaw = null;
		if (ServletUtils.isCheckBoxEnabled(testStatisticsRaw)) {
			periodRaw = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_PERIOD);
			
			if (periodRaw != null && !possiblePeriods.contains(periodRaw))
				sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_STATUS_PERIOD + SEPARATOR_VALORE_NON_CORRETTO + String.join(",", possiblePeriods));
			
			String periodValueRaw = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_PERIOD_VALUE);
			try {
				if (periodValueRaw != null) {
					periodValueParsed = Integer.parseInt(periodValueRaw);
			        if (periodValueParsed <= 0)
			        	sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_STATUS_PERIOD + ", accetta solo valori > 0");
				}
		    } catch(NumberFormatException | NullPointerException e) { 
		    	sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_STATUS_PERIOD + ", accetta solo valore interi"); 
		    }
			
			String statLifetimeRaw = getParameter(helper, map, ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_STAT_LIFETIME);
			statLifetimeParsed = null;
			try {
				if (statLifetimeRaw != null && !statLifetimeRaw.equals("")) {
					statLifetimeParsed = Integer.parseInt(statLifetimeRaw);
			        if (statLifetimeParsed <= 0)
			        	sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_STATUS_STAT_LIFETIME + ", accetta solo valori > 0");
				}
		    } catch(NumberFormatException | NullPointerException e) { 
		    	sendError(pd, ConnettoriCostanti.LABEL_CONNETTORE_STATUS_STAT_LIFETIME + ", accetta solo valore interi"); 
		    }
		}
		
		this.statusResponseType(type)
			.testConnectivity(testConnectivityRaw == null ? null : ServletUtils.isCheckBoxEnabled(testConnectivityRaw))
			.period(periodRaw)
			.periodValue(periodValueParsed)
			.statLifetime(statLifetimeParsed)
			.testStatistics(testStatisticsRaw == null ?  null : ServletUtils.isCheckBoxEnabled(testStatisticsRaw));
	}
	
	public void updateFromDB(Map<String, String> map) {
		if (map == null)
			return;
		
		String type = map.get(CostantiDB.CONNETTORE_STATUS_RESPONSE_TYPE);
		if (type != null && getStatusResponseType() == null)
			this.statusResponseType(map.get(CostantiDB.CONNETTORE_STATUS_RESPONSE_TYPE));
		
		String connectivity = map.get(CostantiDB.CONNETTORE_STATUS_TEST_CONNECTIVITY);
		if (connectivity != null && isTestConnectivity() == null)
			this.testConnectivity(Boolean.valueOf(connectivity));
		
		String periodRaw = map.get(CostantiDB.CONNETTORE_STATUS_STATISTICAL_PERIOD);
		if (periodRaw != null && getPeriod() == null)
			this.period(periodRaw);
		
		String periodValueRaw = map.get(CostantiDB.CONNETTORE_STATUS_STATISTICAL_PERIOD_VALUE);
		if (periodValueRaw != null && getPeriodValue() == null)
			this.periodValue(Integer.valueOf(periodValueRaw));
		
		String statLifetimeRaw = map.get(CostantiDB.CONNETTORE_STATUS_STAT_LIFETIME);
		if (statLifetimeRaw != null && getStatLifetime() == null)
			this.statLifetime(Integer.valueOf(statLifetimeRaw));
		
		if (this.isTestStatistics() == null)
			this.testStatistics(periodRaw != null);
	}
	
	/**
	 * Salva i dati nel DB
	 * @param config connettore lato config
	 * @param registry connettore lato registry
	 */
	protected void fillTo(org.openspcoop2.core.config.Connettore config, org.openspcoop2.core.registry.Connettore registry) {
		this.setCustom(config, registry, true);
		this.setProperty(config, registry, CostantiDB.CONNETTORE_STATUS_RESPONSE_TYPE, getStatusResponseType());
		this.setProperty(config, registry, CostantiDB.CONNETTORE_STATUS_TEST_CONNECTIVITY, Boolean.toString(Objects.requireNonNullElse(isTestConnectivity(), false)));
		
		if (this.isTestStatistics() == Boolean.TRUE) {
			this.setProperty(config, registry, CostantiDB.CONNETTORE_STATUS_STATISTICAL_PERIOD, getPeriod());
			this.setProperty(config, registry, CostantiDB.CONNETTORE_STATUS_STATISTICAL_PERIOD_VALUE, getPeriodValue().toString());
		
			if (this.getStatLifetime() != null) {
				this.setProperty(config, registry, CostantiDB.CONNETTORE_STATUS_STAT_LIFETIME, getStatLifetime().toString());
			}
		}

	}

	/**
	 * Funzione che serve a riempire le maschere nella console
	 * @param dati, tutti gli elementi appartenenti alla mascher
	 * @param serviceBinding (SOAP, REST)
	 * @param postBackViaPost se eseguire il postBack utilizzando Post
	 * @return Ritorna la lista di dati aggiornata (ridondante)
	 */
	public List<DataElement> getDati(List<DataElement> dati, ServiceBinding serviceBinding, boolean postBackViaPost) {
		
		// elementi per impostare il response type
		DataElement responseTypeSelect = new DataElement();
		responseTypeSelect.setType(DataElementType.SELECT);
		responseTypeSelect.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT);
		responseTypeSelect.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_RESPONSE_TYPE);
		responseTypeSelect.setValues(possibleResponseTypeValues);		
		responseTypeSelect.setLabels(possibleResponseTypeLabels);
		
		DataElementInfo dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT);
		dInfo.setHeaderBody(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO);
		dInfo.setListBody(ConnettoriCostanti.getLabelConnettoreResponseInputDataElementInfoFormati());
		responseTypeSelect.setInfo(dInfo);
		
		DataElement personalizedResponseSelect = new DataElement();
		personalizedResponseSelect.setValues(possiblePersonalizedResponseTypeValues);
		personalizedResponseSelect.setLabels(possiblePersonalizedResponseTypeLabels);
		personalizedResponseSelect.setSelected(possiblePersonalizedResponseTypeValues.get(0));
		personalizedResponseSelect.setValue(possiblePersonalizedResponseTypeValues.get(0));
		personalizedResponseSelect.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_RESPONSE_PERSONALIZED);
		
		dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT);
		dInfo.setHeaderBody(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO);
		dInfo.setListBody(ConnettoriCostanti.getLabelConnettoreResponseInputDataElementInfoFormatiCustom());
		personalizedResponseSelect.setInfo(dInfo);
		
		if (getStatusResponseType() != null && !getStatusResponseType().equals(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI)) {
			personalizedResponseSelect.setType(DataElementType.SELECT);
			personalizedResponseSelect.setSelected(getStatusResponseType());
			personalizedResponseSelect.setValue(getStatusResponseType());
			responseTypeSelect.setSelected(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_PERSONALIZZATO);
		} else {
			personalizedResponseSelect.setType(DataElementType.HIDDEN);
			responseTypeSelect.setSelected(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI);
		}
		
		if (serviceBinding != null && serviceBinding.equals(ServiceBinding.SOAP)) {
			personalizedResponseSelect.setType(DataElementType.HIDDEN);
			responseTypeSelect.setType(DataElementType.TEXT);
			responseTypeSelect.setSelected(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI);
			responseTypeSelect.setValue(CONNETTORE_RESPONSE_INPUT_DATA_ELEMENT_INFO_FORMATO_MODI);
		}
		
		
		// Verifiche
		DataElement verificheTitle = new DataElement();
		verificheTitle.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_VERIFICHE);
		verificheTitle.setType(DataElementType.SUBTITLE);
		
		
		// elementi per impostare la verifica connettivita
		DataElement connectivityCheckbox = new DataElement();
		connectivityCheckbox.setLabelRight(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_TEST_CONNECTIVITY);
		connectivityCheckbox.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_TEST_CONNECTIVITY);
		connectivityCheckbox.setType(DataElementType.CHECKBOX);
		connectivityCheckbox.setValue(Objects.requireNonNullElse(isTestConnectivity(), Boolean.FALSE).booleanValue() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		connectivityCheckbox.setSelected(Objects.requireNonNullElse(isTestConnectivity(), Boolean.FALSE).booleanValue());
		
		dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_TEST_CONNECTIVITY);
		dInfo.setBody(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_TEST_CONNECTIVITY_DATA_ELEMENT_INFO);
		connectivityCheckbox.setInfo(dInfo);

				
		
		// elementi per impostare la verifica statistica
		DataElement statCheckbox = new DataElement();
		statCheckbox.setLabelRight(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_TEST_STATISTICS);
		statCheckbox.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_TEST_STATISTICS);
		statCheckbox.setType(DataElementType.CHECKBOX);
		statCheckbox.setValue(this.isTestStatistics() == Boolean.TRUE ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		statCheckbox.setSelected(this.isTestStatistics() == Boolean.TRUE);
		
		dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_TEST_STATISTICS);
		dInfo.setBody(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_TEST_STATISTICS_DATA_ELEMENT_INFO);
		statCheckbox.setInfo(dInfo);
		
		if(postBackViaPost) {
			responseTypeSelect.setPostBack_viaPOST(true);
			statCheckbox.setPostBack_viaPOST(true);
		}
		else {
			responseTypeSelect.setPostBack(true);
			statCheckbox.setPostBack(true);
		}
		
		
		DataElement intervalTitle = new DataElement();
		intervalTitle.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_OBSERVATION_INTERVAL);
		intervalTitle.setType(DataElementType.SUBTITLE);
		
		dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_OBSERVATION_INTERVAL);
		dInfo.setBody(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_OBSERVATION_INTERVAL_DATA_ELEMENT_INFO);
		intervalTitle.setInfo(dInfo);
		
		DataElement periodElement = new DataElement();
		periodElement.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_PERIOD);
		periodElement.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_PERIOD);
		periodElement.setValues(possiblePeriods);
		periodElement.setValue(Objects.requireNonNullElse(getPeriod(), TipoPeriodoStatistico.GIORNALIERO.getValue()));
		periodElement.setSelected(getPeriod());
		periodElement.setType(DataElementType.SELECT);
			
		DataElement periodValueElement = new DataElement();
		periodValueElement.setRequired(true);
		periodValueElement.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_OBSERVATION_INTERVAL);
		periodValueElement.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_PERIOD_VALUE);
		periodValueElement.setType(DataElementType.NUMBER);
		periodValueElement.setValue(Objects.requireNonNullElse(getPeriodValue(), 1).toString());
		
		DataElement lifetimeElement = new DataElement();
		lifetimeElement.setType(DataElementType.TEXT_EDIT);
		lifetimeElement.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_STAT_LIFETIME);
		lifetimeElement.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_STATUS_STAT_LIFETIME);
		lifetimeElement.setValue(this.getStatLifetime() == null ? "" : this.getStatLifetime().toString());
			
		dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_STAT_LIFETIME);
		dInfo.setBody(ConnettoriCostanti.LABEL_CONNETTORE_STATUS_STAT_LIFETIME_DATA_ELEMENT_INFO);
		lifetimeElement.setInfo(dInfo);
		
		if (this.isTestStatistics() != Boolean.TRUE) {
			intervalTitle.setType(DataElementType.HIDDEN);
			periodElement.setType(DataElementType.HIDDEN);
			periodValueElement.setType(DataElementType.HIDDEN);
			lifetimeElement.setType(DataElementType.HIDDEN);
		}
		
		dati.add(responseTypeSelect);
		dati.add(personalizedResponseSelect);
		dati.add(verificheTitle);
		dati.add(connectivityCheckbox);
		dati.add(statCheckbox);
		dati.add(intervalTitle);
		dati.add(periodElement);
		dati.add(periodValueElement);
		dati.add(lifetimeElement);
		
		return dati;
	}
	
	
	
	// funzioni per astrarre concettor di ConsoleHelper e Mappa
	private static String getParameter(ConsoleHelper helper, Map<String, String> map, String key) throws DriverControlStationException {
		return helper == null ? map.get(key) : helper.getParameter(key);
	}
	
	public static ConnettoreStatusParams fillFrom(ConsoleHelper helper) throws DriverControlStationException {
		ConnettoreStatusParams connettoreStatusParams = new ConnettoreStatusParams();
		if (helper != null)
			connettoreStatusParams.fillFrom(helper, null, null, null);
		return connettoreStatusParams;
	}
	
	public static ConnettoreStatusParams check(ConsoleHelper helper, ServiceBinding serviceBinding, PageData pd) throws DriverControlStationException {
		ConnettoreStatusParams connettoreStatusParams = new ConnettoreStatusParams();
		if (helper != null)
			connettoreStatusParams.fillFrom(helper, null, serviceBinding, pd);
		return connettoreStatusParams;
	}
	
	public static ConnettoreStatusParams fillFrom(Map<String, String> map) throws DriverControlStationException {
		ConnettoreStatusParams connettoreStatusParams = new ConnettoreStatusParams();
		if (map != null)
			connettoreStatusParams.fillFrom(null, map, null, null);
		return connettoreStatusParams;
	}
	
	public static ConnettoreStatusParams check(Map<String, String> map, ServiceBinding serviceBinding, PageData pd) throws DriverControlStationException {
		ConnettoreStatusParams connettoreStatusParams = new ConnettoreStatusParams();
		if (map != null)
			connettoreStatusParams.fillFrom(null, map, serviceBinding, pd);
		return connettoreStatusParams;
	}
	
	
	// funzioni per astrarre config e registry
	private void setCustom(org.openspcoop2.core.config.Connettore config, org.openspcoop2.core.registry.Connettore registry, boolean value) {
		if (config != null) {
			config.setCustom(value);
		}
		
		if (registry != null) {
			registry.setCustom(value);
		}
	}
	
	private void setProperty(org.openspcoop2.core.config.Connettore config, org.openspcoop2.core.registry.Connettore registry, String key, String value) {
		if (config != null) {
			org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
			prop.setNome(key);
			prop.setValore(value);
			config.addProperty(prop);
		}
		
		if (registry != null) {
			org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(key);
			prop.setValore(value);
			registry.addProperty(prop);
		}
	}
	
	public void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore) {
		this.fillTo(connettore, null);		
	}
	
	public void fillConnettoreRegistry(org.openspcoop2.core.registry.Connettore connettore) {
		this.fillTo(null, connettore);
	}
	
	// setters and getters
	
	public void addDati(List<DataElement> dati, ServiceBinding serviceBinding, boolean postBackViaPost) {
		getDati(dati, serviceBinding, postBackViaPost);
	}
	
	public void addDatiHidden(List<DataElement> dati, ServiceBinding serviceBinding) {
		List<DataElement> newDati = getDati(new ArrayList<>(), serviceBinding, true);
		for (DataElement de : newDati) {
			de.setType(DataElementType.HIDDEN);
			dati.add(de);
		}
	}
	
	public ConnettoreStatusParams statusResponseType(String statusResponseType) {
		this.statusResponseType = statusResponseType;
		return this;
	}
	
	public String getStatusResponseType() {
		return this.statusResponseType;
	}
	
	public ConnettoreStatusParams testConnectivity(Boolean testConnectivity) {
		this.testConnectivity = testConnectivity;
		return this;
	}
	
	public Boolean isTestConnectivity() {
		return this.testConnectivity;
	}
	
	public ConnettoreStatusParams testStatistics(Boolean testStatistics) {
		this.testStatistics = testStatistics;
		return this;
	}
	
	public Boolean isTestStatistics() {
		return this.testStatistics;
	}
	
	public ConnettoreStatusParams period(String period) {
		this.period = period;
		return this;
	}
	
	public String getPeriod() {
		return this.period;
	}
	
	public ConnettoreStatusParams periodValue(Integer periodValue) {
		this.periodValue = periodValue;
		return this;
	}
	
	public Integer getPeriodValue() {
		return this.periodValue;
	}
	
	private ConnettoreStatusParams parsingError(boolean parsingErrors) {
		this.parsingErrors = parsingErrors;
		return this;
	}
	
	public boolean getParsingErrors() {
		return this.parsingErrors;
	}
	
	public ConnettoreStatusParams statLifetime(Integer statLifetime) {
		this.statLifetime = statLifetime;
		return this;
	}
	
	public Integer getStatLifetime() {
		return this.statLifetime;
	}
		
}
