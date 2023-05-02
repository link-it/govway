/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.modipa.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ConsoleItemInfo;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * ModIDynamicConfigurationAccordiParteComuneUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities {
	
	private ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities() {}

	
	static void addProfiloSicurezzaMessaggio(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean rest, boolean action) throws ProtocolException {
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_ID, 
				ModIConsoleCostanti.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL));
		
		String labelSelezione = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL;
		
		boolean corniceSicurezza = modiProperties.isSicurezzaMessaggio_corniceSicurezza_enabled();
		
		if(action) {
			
			labelSelezione = "";
			
			StringConsoleItem modeItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_ID, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL);
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_LABEL_RIDEFINISCI,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
			modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_DEFAULT_VALUE);
			modeItem.setReloadOnChange(true);
			configuration.addConsoleItem(modeItem);
		}
		
		StringConsoleItem profiloSicurezzaMessaggioItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID, 
				labelSelezione);
		if(action) {
			profiloSicurezzaMessaggioItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaMessaggioItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_UNDEFINED,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED);
		if(rest) {
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01);
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02);
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301);
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302);
			profiloSicurezzaMessaggioItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401);
			profiloSicurezzaMessaggioItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402);
		}
		else {
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01);
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02);
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301);
			profiloSicurezzaMessaggioItem.addLabelValue((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302);
		}
		profiloSicurezzaMessaggioItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE);
		if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioItem.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioItem.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioItem.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioItem.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) && rest) {
			profiloSicurezzaMessaggioItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) && rest) {
			profiloSicurezzaMessaggioItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST_NOTE);
		}
		/**if(corniceSicurezza) {*/
		profiloSicurezzaMessaggioItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaMessaggioItem);
		
		StringConsoleItem profiloSicurezzaMessaggioSorgenteTokenIdAuthItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL);
		profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_LOCALE,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE);
		profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_PDND,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND);
		profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_OAUTH,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH);
		profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_DEFAULT_VALUE);
		if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE)) {
			profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_LOCALE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND)) {
			profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_PDND);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH)) {
			profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_OAUTH);
		}
		profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(profiloSicurezzaMessaggioSorgenteTokenIdAuthItem);
		
		if(rest) {
			
			// !! Nel caso di 2 header, quello integrity viene prodotto solo se c'è un payload o uno degli header indicati da firmare.
			StringConsoleItem profiloSicurezzaMessaggioHeaderItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL);
			profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA.
					replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA);
			profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION);
			if(isProfiloSicurezza03or04(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
				profiloSicurezzaMessaggioHeaderItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE);
				
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA.
						replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA);
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.
						replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE);
				
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM,
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM);
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM,
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM);
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE,
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE);
				
				profiloSicurezzaMessaggioHeaderItem.setReloadOnChange(true);
			}
			else if(isProfiloSicurezza01(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) || isProfiloSicurezza02(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
				profiloSicurezzaMessaggioHeaderItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE);
			}
			else {
				profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.HIDDEN);
			}
			configuration.addConsoleItem(profiloSicurezzaMessaggioHeaderItem);
			
			StringConsoleItem profiloSicurezzaMessaggioHeaderCustomItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_EDIT,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_ID, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_LABEL);
			profiloSicurezzaMessaggioHeaderCustomItem.setDefaultValue(modiProperties.getRestSecurityTokenHeaderModI());
			profiloSicurezzaMessaggioHeaderCustomItem.setType(ConsoleItemType.HIDDEN);
			configuration.addConsoleItem(profiloSicurezzaMessaggioHeaderCustomItem);
			
		}
		
		StringConsoleItem profiloSicurezzaMessaggioConfigurazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL);
		profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI);
		profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA);
		profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA);
		if(!rest && isProfiloSicurezza03(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS,
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS);
			profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS,
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS);
			profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS,
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS);
		}
		if(rest) {
			profiloSicurezzaMessaggioConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PERSONALIZZATO,
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO);
			profiloSicurezzaMessaggioConfigurazioneItem.setReloadOnChange(true);
		}
		profiloSicurezzaMessaggioConfigurazioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE);
		if(!isProfiloSicurezza01(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) &&
				!isProfiloSicurezza02(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) &&
				!isProfiloSicurezza03(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) &&
				!isProfiloSicurezza04(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) ) {
			profiloSicurezzaMessaggioConfigurazioneItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaMessaggioConfigurazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaMessaggioConfigurazioneItem);
				
		BooleanConsoleItem profiloSicurezzaMessaggioRequestDigest = (BooleanConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
				ConsoleItemType.CHECKBOX,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL);
		if(!isProfiloSicurezza03or04(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioRequestDigest.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaMessaggioRequestDigest.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_DEFAULT);
		ConsoleItemInfo cDigest = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL);
		cDigest.setHeaderBody(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_REST_INFO_HEADER : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_SOAP_INFO_HEADER );
		profiloSicurezzaMessaggioRequestDigest.setInfo(cDigest);
		profiloSicurezzaMessaggioRequestDigest.setLabelRight(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL_RIGHT);
		configuration.addConsoleItem(profiloSicurezzaMessaggioRequestDigest);
		
		if(corniceSicurezza) {
			BooleanConsoleItem profiloSicurezzaMessaggioCorniceSicurezza = (BooleanConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
					ConsoleItemType.CHECKBOX,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL);
			if(!isProfiloSicurezza03(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
				profiloSicurezzaMessaggioCorniceSicurezza.setType(ConsoleItemType.HIDDEN);
			}
			profiloSicurezzaMessaggioCorniceSicurezza.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_DEFAULT_VALUE);
			ConsoleItemInfo consoleItemInfoCorniceSicurezza = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL);
			consoleItemInfoCorniceSicurezza.setHeaderBody(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_HEADER : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_HEADER );
			consoleItemInfoCorniceSicurezza.setListBody(rest ? ModIConsoleCostanti.getModipaProfiloSicurezzaMessaggioCorniceSicurezzaRestInfoValori() : ModIConsoleCostanti.getModipaProfiloSicurezzaMessaggioCorniceSicurezzaSoapInfoValori() );
			profiloSicurezzaMessaggioCorniceSicurezza.setInfo(consoleItemInfoCorniceSicurezza);
			profiloSicurezzaMessaggioCorniceSicurezza.setLabelRight(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL_RIGHT);
			configuration.addConsoleItem(profiloSicurezzaMessaggioCorniceSicurezza);
		}
		
		
		// Configurazione
		
		boolean showConfigurazione = true;
		if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE)) {
			showConfigurazione = false;
		}
		
		BaseConsoleItem subTitleRichiesta = ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_LABEL); 
		if(!showConfigurazione) {
			subTitleRichiesta.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(subTitleRichiesta);
		
		StringConsoleItem profiloSicurezzaRichiestaConfigurazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL);
		profiloSicurezzaRichiestaConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_ABILITATO,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO);
		profiloSicurezzaRichiestaConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_DISABILITATO,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO);
		profiloSicurezzaRichiestaConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_PERSONALIZZATO,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO);
		profiloSicurezzaRichiestaConfigurazioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_DEFAULT_VALUE);
		if(!showConfigurazione) {
			profiloSicurezzaRichiestaConfigurazioneItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaRichiestaConfigurazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaRichiestaConfigurazioneItem);
			
		StringConsoleItem profiloSicurezzaRichiestaConfigurazioneContentTypeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TAGS,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_LABEL);
		profiloSicurezzaRichiestaConfigurazioneContentTypeItem.setInfo(buildConsoleItemInfoSicurezzaContentType(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_LABEL));
		if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_PERSONALIZZATO.equals(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_DEFAULT_VALUE)) {
			profiloSicurezzaRichiestaConfigurazioneContentTypeItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaRichiestaConfigurazioneContentTypeItem.setRequired(true);
		configuration.addConsoleItem(profiloSicurezzaRichiestaConfigurazioneContentTypeItem);
		
		BaseConsoleItem subTitleRisposta = ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_LABEL); 
		if(!showConfigurazione) {
			subTitleRisposta.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(subTitleRisposta);
		
		StringConsoleItem profiloSicurezzaRispostaConfigurazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL);
		profiloSicurezzaRispostaConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_ABILITATO,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO);
		profiloSicurezzaRispostaConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_DISABILITATO,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO);
		profiloSicurezzaRispostaConfigurazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_PERSONALIZZATO,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO);
		profiloSicurezzaRispostaConfigurazioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE);
		if(!showConfigurazione) {
			profiloSicurezzaRispostaConfigurazioneItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaRispostaConfigurazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaRispostaConfigurazioneItem);
			
		StringConsoleItem profiloSicurezzaRispostaConfigurazioneContentTypeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TAGS,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_LABEL);
		profiloSicurezzaRispostaConfigurazioneContentTypeItem.setInfo(buildConsoleItemInfoSicurezzaContentType(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_LABEL));
		if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_PERSONALIZZATO.equals(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE)) {
			profiloSicurezzaRispostaConfigurazioneContentTypeItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaRispostaConfigurazioneContentTypeItem.setRequired(true);
		configuration.addConsoleItem(profiloSicurezzaRispostaConfigurazioneContentTypeItem);
		
		StringConsoleItem profiloSicurezzaRispostaConfigurazioneReturnCodeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TAGS,
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL);
		ConsoleItemInfo cRT = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL);
		cRT.setHeaderBody(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID_INFO);
		profiloSicurezzaRispostaConfigurazioneReturnCodeItem.setInfo(cRT);
		if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_PERSONALIZZATO.equals(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE)) {
			profiloSicurezzaRispostaConfigurazioneReturnCodeItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaRispostaConfigurazioneReturnCodeItem.setRequired(true);
		configuration.addConsoleItem(profiloSicurezzaRispostaConfigurazioneReturnCodeItem);
		
	}
	
	static boolean isProfiloSicurezza01(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(value);
	}
	
	static boolean isProfiloSicurezza02(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(value);
	}
	
	static boolean isProfiloSicurezza03or04(String value) {
		return isProfiloSicurezza03(value) || isProfiloSicurezza04(value);
	}
	
	static boolean isProfiloSicurezza03(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(value)
				||
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(value);
	}
	
	static boolean isProfiloSicurezza04(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(value)
				||
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(value);
	}
	
	static ConsoleItemInfo buildConsoleItemInfoSicurezzaContentType(String label) {
		
		try {
			ConsoleItemInfo c = new ConsoleItemInfo(label);
			
			c.setHeaderBody(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_O_RISPOSTA_CONTENT_TYPE_MODE_ID_INFO_CONTENT_TYPE);
			
			c.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI);
			
			return c;
			
		}catch(Exception t) {
			try {
				LoggerWrapperFactory.getLogger("govwayConsole.core").error(t.getMessage(),t);
			}catch(Exception tInternal) {
				/**System.err.println("ERRORE: "+t.getMessage());
				t.printStackTrace(System.err);*/
			}
			return null;
		}
		
	}
	
	static void updateProfiloSicurezzaMessaggio(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties,
			boolean rest, boolean action) throws ProtocolException {
		
		String postBackElementName = null;
		try {
			postBackElementName = consoleHelper.getPostBackElementName();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		boolean corniceSicurezza = modiProperties.isSicurezzaMessaggio_corniceSicurezza_enabled();
		StringProperty profiloSicurezzaMessaggioItemValue = null;
		
		AbstractConsoleItem<?> profiloSicurezzaMessaggioItemModifyNote = null;
		boolean ridefinito = false;
		
		if(action) {
		
			StringProperty modeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_ID);
						
			AbstractConsoleItem<?> profiloSicurezzaMessaggioItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
			profiloSicurezzaMessaggioItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
			
			if(modeItemValue!=null && ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(modeItemValue.getValue())) {
				profiloSicurezzaMessaggioItem.setType(ConsoleItemType.SELECT);
				
				profiloSicurezzaMessaggioItemModifyNote = profiloSicurezzaMessaggioItem;
				
				ridefinito = true;
			}
			else {
				if(profiloSicurezzaMessaggioItem!=null) {
					profiloSicurezzaMessaggioItem.setType(ConsoleItemType.HIDDEN);
				}
				if(profiloSicurezzaMessaggioItemValue!=null) {
					profiloSicurezzaMessaggioItemValue.setValue(null);
				}
			}
			
		}
		else {
		
			profiloSicurezzaMessaggioItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
				
			profiloSicurezzaMessaggioItemModifyNote = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
						
		}
		String sicurezzaMessaggioPatternValue = profiloSicurezzaMessaggioItemValue!=null ? profiloSicurezzaMessaggioItemValue.getValue() : null;
		if(profiloSicurezzaMessaggioItemModifyNote!=null) {
			if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(sicurezzaMessaggioPatternValue)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(sicurezzaMessaggioPatternValue)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(sicurezzaMessaggioPatternValue)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(sicurezzaMessaggioPatternValue)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(sicurezzaMessaggioPatternValue) && rest) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(sicurezzaMessaggioPatternValue) && rest) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST_NOTE);
			}
		}
		
		boolean isSicurezza01 = false;
		boolean isSicurezza02 = false;
		boolean isSicurezza03 = false;
		boolean isSicurezza04 = false;
		if(profiloSicurezzaMessaggioItemValue!=null && profiloSicurezzaMessaggioItemValue.getValue()!=null) {
			isSicurezza01 = isProfiloSicurezza01(profiloSicurezzaMessaggioItemValue.getValue());
			isSicurezza02 = isProfiloSicurezza02(profiloSicurezzaMessaggioItemValue.getValue());
			isSicurezza03 = isProfiloSicurezza03(profiloSicurezzaMessaggioItemValue.getValue());
			isSicurezza04 = isProfiloSicurezza04(profiloSicurezzaMessaggioItemValue.getValue());
		}
		boolean isSicurezza03or04 = isSicurezza03 || isSicurezza04;
		
		
		AbstractConsoleItem<?> profiloSicurezzaMessaggioSorgenteTokenIdAuthItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID);
		boolean sorgenteTokenLocale = true;
		if(profiloSicurezzaMessaggioSorgenteTokenIdAuthItem!=null) {
			
			StringProperty profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID);
			
			if(profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue!=null && 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID.equals(postBackElementName)) {
				// modificato il pattern
				if(	ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(sicurezzaMessaggioPatternValue) 
						||
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(sicurezzaMessaggioPatternValue)
				){
					profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue.setValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND);
				}
				else {
					// altrimenti lascio quello impostato precedentemente che per gli altri profili va bene
					/** profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue.setValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE); */
				}
			}
			
			String sorgenteTokenIdAuthValue = profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue!=null ? profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue.getValue() : null;
			if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE.equals(sorgenteTokenIdAuthValue)) {
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_LOCALE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteTokenIdAuthValue)) {
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_PDND);
				sorgenteTokenLocale = false;
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteTokenIdAuthValue)) {
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_OAUTH);
				sorgenteTokenLocale = false;
			}
			
			if(isSicurezza01 || isSicurezza02 || isSicurezza03 || isSicurezza04) {
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setType(ConsoleItemType.SELECT);
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setReloadOnChange(true);
			}
			else {
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setType(ConsoleItemType.HIDDEN);
				profiloSicurezzaMessaggioSorgenteTokenIdAuthItem.setReloadOnChange(false);
				if(profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue!=null) {
					profiloSicurezzaMessaggioSorgenteTokenIdAuthItemValue.setValue(null);
				}
			}
		}
		
		
		if(rest) {
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHeaderItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID);
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHeaderCustomItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_ID);
			if(isSicurezza01 || isSicurezza02 || isSicurezza03 || isSicurezza04) {
				profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.SELECT);
				
				StringProperty profiloSicurezzaMessaggioHeaderItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID);
				if(profiloSicurezzaMessaggioHeaderItemValue!=null && ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID.equals(postBackElementName)){
					
					String headerSelezionato = profiloSicurezzaMessaggioHeaderItemValue.getValue();
					boolean agidPresente = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA.equals(headerSelezionato) ||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(headerSelezionato)  ||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA.equals(headerSelezionato) ||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM.equals(headerSelezionato) ||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE.equals(headerSelezionato)  ||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM.equals(headerSelezionato);
					boolean authorizationOnlyPresente = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION.equals(headerSelezionato);
					if(isSicurezza01 || isSicurezza02) {
						// se è stato modificato (postback) in 01 o 02, e non c'e' l'header authorization imposto il null per forzare il default
						if(!authorizationOnlyPresente) {
							profiloSicurezzaMessaggioHeaderItemValue.setValue(null);
						}
					}
					else if(isSicurezza03or04 &&
						// se è stato modificato (postback) in 0301 o 0302, e non c'e' l'header agid imposto il null per forzare il default
						!agidPresente) {
						profiloSicurezzaMessaggioHeaderItemValue.setValue(null);
					}
										
				}
				
				((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).clearMapLabelValues();
				
				if(isSicurezza03or04) {
					
					profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.SELECT);	
					
					if(sorgenteTokenLocale) {
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA.
								replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA);
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION,
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION);
					}
					
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA.
							replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA);
					if(sorgenteTokenLocale) {
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.
								replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE);
					}
					
					if(sorgenteTokenLocale) {
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM,
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM);
					}
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM,
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM);
					if(sorgenteTokenLocale) {
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE,
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE);
					}
					
					if(profiloSicurezzaMessaggioHeaderItemValue!=null &&
							(profiloSicurezzaMessaggioHeaderItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaMessaggioHeaderItemValue.getValue()))
						) {
						profiloSicurezzaMessaggioHeaderItemValue.setValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE);
					}
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE);
					
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).setReloadOnChange(true);
				}
				else if(isSicurezza01 || isSicurezza02) {

					if(sorgenteTokenLocale) {
						
						profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.SELECT);	
						
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION,
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION);
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA.
								replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, modiProperties.getRestSecurityTokenHeaderModI()),
								ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA);
					}
					else {
						profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.HIDDEN);	
					}
					
					if(profiloSicurezzaMessaggioHeaderItemValue!=null &&
							(profiloSicurezzaMessaggioHeaderItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaMessaggioHeaderItemValue.getValue()))
						) {
						profiloSicurezzaMessaggioHeaderItemValue.setValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE);
					}
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE);
					
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).setReloadOnChange(false);
				}
				
				boolean custom = false;
				if(isSicurezza03or04 &&
					profiloSicurezzaMessaggioHeaderItemValue!=null &&
					profiloSicurezzaMessaggioHeaderItemValue.getValue()!=null && 
					StringUtils.isNotEmpty(profiloSicurezzaMessaggioHeaderItemValue.getValue())
						) {
					custom =
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM.equals(profiloSicurezzaMessaggioHeaderItemValue.getValue())
							||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM.equals(profiloSicurezzaMessaggioHeaderItemValue.getValue())
							||
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggioHeaderItemValue.getValue());
				}
				if(custom) {
					if(profiloSicurezzaMessaggioHeaderCustomItem!=null) {
						profiloSicurezzaMessaggioHeaderCustomItem.setType(ConsoleItemType.TEXT_EDIT);
						profiloSicurezzaMessaggioHeaderCustomItem.setRequired(true);
						((StringConsoleItem)profiloSicurezzaMessaggioHeaderCustomItem).setDefaultValue(modiProperties.getRestSecurityTokenHeaderModI());
						if(postBackElementName!=null && postBackElementName.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID)) {
							StringProperty profiloSicurezzaMessaggioHeaderCustomItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_ID);
							if(profiloSicurezzaMessaggioHeaderCustomItemValue!=null &&
								(profiloSicurezzaMessaggioHeaderCustomItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaMessaggioHeaderCustomItemValue.getValue()))
							) {
								profiloSicurezzaMessaggioHeaderCustomItemValue.setValue(modiProperties.getRestSecurityTokenHeaderModI());
							}
						}
					}
				}
				else {
					if(profiloSicurezzaMessaggioHeaderCustomItem!=null) {
						profiloSicurezzaMessaggioHeaderCustomItem.setType(ConsoleItemType.HIDDEN);
						profiloSicurezzaMessaggioHeaderCustomItem.setRequired(false);
						StringProperty profiloSicurezzaMessaggioHeaderCustomItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_ID);
						if(profiloSicurezzaMessaggioHeaderCustomItemValue!=null) {
							profiloSicurezzaMessaggioHeaderCustomItemValue.setValue(null);
						}
					}
				}
			}
			else {
				profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.HIDDEN);
				if(!ridefinito) {
					StringProperty profiloSicurezzaMessaggioHeaderItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID);
					if(profiloSicurezzaMessaggioHeaderItemValue!=null) {
						profiloSicurezzaMessaggioHeaderItemValue.setValue(null);
					}
				}
				
				if(profiloSicurezzaMessaggioHeaderCustomItem!=null) {
					profiloSicurezzaMessaggioHeaderCustomItem.setType(ConsoleItemType.HIDDEN);
					StringProperty profiloSicurezzaMessaggioHeaderCustomItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_ID);
					if(profiloSicurezzaMessaggioHeaderCustomItemValue!=null) {
						profiloSicurezzaMessaggioHeaderCustomItemValue.setValue(null);
					}
				}
			}
		}
		
		
		
		// Configurazione
		
		AbstractConsoleItem<?> profiloSicurezzaConfigurazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID);
		
		StringProperty profiloSicurezzaMessaggioConfigurazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID);
		
		
		// personalizzo lista applicabilita'
				
		if(!rest) {
			
			// verifica soap attachments
			if(isSicurezza03) {
				if(!((StringConsoleItem)profiloSicurezzaConfigurazioneItem).getMapLabelValues().containsKey(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS)) {
					((StringConsoleItem)profiloSicurezzaConfigurazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS,
							ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS);
				}
				if(!((StringConsoleItem)profiloSicurezzaConfigurazioneItem).getMapLabelValues().containsKey(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS)) {
					((StringConsoleItem)profiloSicurezzaConfigurazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS,
							ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS);
				}
				if(!((StringConsoleItem)profiloSicurezzaConfigurazioneItem).getMapLabelValues().containsKey(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS)) {
					((StringConsoleItem)profiloSicurezzaConfigurazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS,
							ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS);
				}
			}
			else {
				((StringConsoleItem)profiloSicurezzaConfigurazioneItem).removeLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS);
				((StringConsoleItem)profiloSicurezzaConfigurazioneItem).removeLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS);
				((StringConsoleItem)profiloSicurezzaConfigurazioneItem).removeLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS);
				
				if(profiloSicurezzaMessaggioConfigurazioneItemValue!=null &&
					profiloSicurezzaMessaggioConfigurazioneItemValue.getValue()!=null && !StringUtils.isEmpty(profiloSicurezzaMessaggioConfigurazioneItemValue.getValue())) {
					String v = profiloSicurezzaMessaggioConfigurazioneItemValue.getValue();
					if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(v) ||
							ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS.equals(v) ||
							ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(v) ) {
						profiloSicurezzaMessaggioConfigurazioneItemValue.setValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE);
					}
				}
			}
		}
		
		// parametri che serviranno a nascondere requestDigest e infoUtente
		boolean sicurezzaSullaRichiesta = false;
		boolean sicurezzaSullaRisposta = false;
		
		
		boolean configurazionePersonalizzata = false;
		if(isSicurezza01 || isSicurezza02 || isSicurezza03 || isSicurezza04) {
			
			if(sorgenteTokenLocale || isSicurezza03 || isSicurezza04) {
				
				profiloSicurezzaConfigurazioneItem.setType(ConsoleItemType.SELECT);
				profiloSicurezzaConfigurazioneItem.setReloadOnChange(true);
			
				((StringConsoleItem)profiloSicurezzaConfigurazioneItem).setDefaultValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE);
				String secValue = ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE;
				if(profiloSicurezzaMessaggioConfigurazioneItemValue!=null) {
					if(profiloSicurezzaMessaggioConfigurazioneItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaMessaggioConfigurazioneItemValue.getValue())) {
						profiloSicurezzaMessaggioConfigurazioneItemValue.setValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE);
					}
					else {
						secValue = profiloSicurezzaMessaggioConfigurazioneItemValue.getValue(); 
					}
					
					if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(secValue)) {
						configurazionePersonalizzata = true;
					}
				}
				
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(secValue) ||
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(secValue)) {
					sicurezzaSullaRichiesta = true;
					sicurezzaSullaRisposta = true;
				}
				else if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA.equals(secValue) ||
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS.equals(secValue)) {
					sicurezzaSullaRichiesta = true;
				}
				else if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(secValue) ||
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(secValue)) {
					sicurezzaSullaRisposta = true;
				}
			}
			else {
				profiloSicurezzaConfigurazioneItem.setType(ConsoleItemType.HIDDEN);
				profiloSicurezzaConfigurazioneItem.setReloadOnChange(false);
				
				if(profiloSicurezzaMessaggioConfigurazioneItemValue!=null) {
					profiloSicurezzaMessaggioConfigurazioneItemValue.setValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA);
				}
			}
			
		}
		else {
			profiloSicurezzaConfigurazioneItem.setType(ConsoleItemType.HIDDEN);
			if(profiloSicurezzaMessaggioConfigurazioneItemValue!=null) {
				profiloSicurezzaMessaggioConfigurazioneItemValue.setValue(null);
			}
		}
				
		BaseConsoleItem subTitleRichiesta = ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_ID);
		subTitleRichiesta.setType(configurazionePersonalizzata ? ConsoleItemType.SUBTITLE : ConsoleItemType.HIDDEN);
				
		AbstractConsoleItem<?> profiloSicurezzaRichiestaConfigurazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID);
		profiloSicurezzaRichiestaConfigurazioneItem.setType(configurazionePersonalizzata ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		if(configurazionePersonalizzata) {
			((StringConsoleItem)profiloSicurezzaRichiestaConfigurazioneItem).setDefaultValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_DEFAULT_VALUE);
		}
		
		StringProperty profiloSicurezzaRichiestaConfigurazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID);
		boolean richiestaPersonalizzata = false;
		if(configurazionePersonalizzata) {
			String secValue = ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_DEFAULT_VALUE;
			if(profiloSicurezzaRichiestaConfigurazioneItemValue!=null) {
				if(profiloSicurezzaRichiestaConfigurazioneItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaRichiestaConfigurazioneItemValue.getValue())) {
					profiloSicurezzaRichiestaConfigurazioneItemValue.setValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_DEFAULT_VALUE);
				}
				else {
					secValue = profiloSicurezzaRichiestaConfigurazioneItemValue.getValue();
				}
				
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO.equals(secValue)) {
					richiestaPersonalizzata = true;
				}
			}
			if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO.equals(secValue) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO.equals(secValue) // per una qualche richiesta sarà abilitato
					) {
				sicurezzaSullaRichiesta = true;
			}
		}
		else {
			if(profiloSicurezzaRichiestaConfigurazioneItemValue!=null) {
				profiloSicurezzaRichiestaConfigurazioneItemValue.setValue(null);
			}
		}

		AbstractConsoleItem<?> profiloSicurezzaRichiestaConfigurazioneContentTypeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
		profiloSicurezzaRichiestaConfigurazioneContentTypeItem.setType(richiestaPersonalizzata ? ConsoleItemType.TAGS : ConsoleItemType.HIDDEN);
		
		StringProperty profiloSicurezzaRichiestaConfigurazioneContentTypeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
		if(!richiestaPersonalizzata && profiloSicurezzaRichiestaConfigurazioneContentTypeItemValue!=null) {
			profiloSicurezzaRichiestaConfigurazioneContentTypeItemValue.setValue(null);
		}
		
		BaseConsoleItem subTitleRisposta = ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_ID);
		subTitleRisposta.setType(configurazionePersonalizzata ? ConsoleItemType.SUBTITLE : ConsoleItemType.HIDDEN);
				
		AbstractConsoleItem<?> profiloSicurezzaRispostaConfigurazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_ID);
		profiloSicurezzaRispostaConfigurazioneItem.setType(configurazionePersonalizzata ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		if(configurazionePersonalizzata) {
			((StringConsoleItem)profiloSicurezzaRispostaConfigurazioneItem).setDefaultValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE);
		}
		
		StringProperty profiloSicurezzaRispostaConfigurazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_ID);
		boolean rispostaPersonalizzata = false;
		if(configurazionePersonalizzata) {
			String secValue = ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE;
			if(profiloSicurezzaRispostaConfigurazioneItemValue!=null) {
				if(profiloSicurezzaRispostaConfigurazioneItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaRispostaConfigurazioneItemValue.getValue())) {
					profiloSicurezzaRispostaConfigurazioneItemValue.setValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE);
				}
				else {
					secValue = profiloSicurezzaRispostaConfigurazioneItemValue.getValue();
				}
				
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO.equals(secValue)) {
					rispostaPersonalizzata = true;
				}
			}
			if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO.equals(secValue) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO.equals(secValue) // per una qualche risposta sarà abilitato
				) {
				sicurezzaSullaRisposta = true;
			}
		}
		else {
			if(profiloSicurezzaRispostaConfigurazioneItemValue!=null) {
				profiloSicurezzaRispostaConfigurazioneItemValue.setValue(null);
			}
		}
			
		AbstractConsoleItem<?> profiloSicurezzaRispostaConfigurazioneContentTypeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
		profiloSicurezzaRispostaConfigurazioneContentTypeItem.setType(rispostaPersonalizzata ? ConsoleItemType.TAGS : ConsoleItemType.HIDDEN);
		
		StringProperty profiloSicurezzaRispostaConfigurazioneContentTypeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
		if(!rispostaPersonalizzata && profiloSicurezzaRispostaConfigurazioneContentTypeItemValue!=null) {
			profiloSicurezzaRispostaConfigurazioneContentTypeItemValue.setValue(null);
		}
		
		AbstractConsoleItem<?> profiloSicurezzaRispostaConfigurazioneReturnCodeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
		profiloSicurezzaRispostaConfigurazioneReturnCodeItem.setType(rispostaPersonalizzata ? ConsoleItemType.TAGS : ConsoleItemType.HIDDEN);
		
		StringProperty profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
		if(!rispostaPersonalizzata && profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue!=null) {
			profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.setValue(null);
		}
		
		
		// InfoUtente e DigestRichiesta
		
		if(corniceSicurezza) {
			AbstractConsoleItem<?> profiloSicurezzaMessaggioCorniceSicurezzaItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_ID);
			BooleanProperty profiloSicurezzaMessaggioCorniceSicurezzaItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_ID);
			
			if(isSicurezza03 && sicurezzaSullaRichiesta) {
				profiloSicurezzaMessaggioCorniceSicurezzaItem.setType(ConsoleItemType.CHECKBOX);
			}
			else {
				if(profiloSicurezzaMessaggioCorniceSicurezzaItem!=null) {
					profiloSicurezzaMessaggioCorniceSicurezzaItem.setType(ConsoleItemType.HIDDEN);
				}
				if(profiloSicurezzaMessaggioCorniceSicurezzaItemValue!=null) {
					profiloSicurezzaMessaggioCorniceSicurezzaItemValue.setValue(null);
				}
			}
		}
		
		AbstractConsoleItem<?> profiloSicurezzaMessaggioRequestDigestItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID);
		BooleanProperty profiloSicurezzaMessaggioRequestDigestItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID);
		
		if(isSicurezza03or04 && sicurezzaSullaRichiesta && sicurezzaSullaRisposta) {
			profiloSicurezzaMessaggioRequestDigestItem.setType(ConsoleItemType.CHECKBOX);
		}
		else {
			if(profiloSicurezzaMessaggioRequestDigestItem!=null) {
				profiloSicurezzaMessaggioRequestDigestItem.setType(ConsoleItemType.HIDDEN);
			}
			if(profiloSicurezzaMessaggioRequestDigestItemValue!=null) {
				profiloSicurezzaMessaggioRequestDigestItemValue.setValue(null);
			}
		}
	}
	
	static void validateProfiloSicurezzaMessaggio(ProtocolProperties properties,
			boolean rest) throws ProtocolException {
		
		if(rest) {
			StringProperty profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
			if(profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue!=null && profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.getValue()!=null && 
					StringUtils.isNotEmpty(profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.getValue())) {
				String v = profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.getValue();
				List<String> codici = new ArrayList<>();
				if(v.contains(",")) {
					String [] tmp = v.split(",");
					for (int i = 0; i < tmp.length; i++) {
						codici.add(tmp[i].trim());
					}
				}
				else {
					codici.add(v.trim());
				}
				if(!codici.isEmpty()) {
					for (String codice : codici) {
						if(codice.contains("-")) {
							String [] tmp = codice.split("-");
							if(tmp==null || tmp.length!=2) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' possiede un formato errato; atteso: codiceMin-codiceMax");
							}
							String codiceMin = tmp[0];
							String codiceMax = tmp[1];
							if(codiceMin==null || StringUtils.isEmpty(codiceMin.trim())) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' possiede un formato errato (intervallo minimo non definito); atteso: codiceMin-codiceMax");
							}
							if(codiceMax==null || StringUtils.isEmpty(codiceMax.trim())) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' possiede un formato errato (intervallo massimo non definito); atteso: codiceMin-codiceMax");
							}
							codiceMin = codiceMin.trim();
							codiceMax = codiceMax.trim();
							int codiceMinInt = -1;
							try {
								codiceMinInt = Integer.valueOf(codiceMin);
							}catch(Exception e) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' contiene un intervallo minimo '"+codiceMin+"' che non è un numero intero");
							}
							int codiceMaxInt = -1;
							try {
								codiceMaxInt = Integer.valueOf(codiceMax);
							}catch(Exception e) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' contiene un intervallo massimo '"+codiceMax+"' che non è un numero intero");
							}
							if(codiceMaxInt<=codiceMinInt) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' contiene un intervallo massimo '"+codiceMax+"' minore o uguale all'intervallo minimo '"+codiceMin+"'");
							}
						}
						else {
							try {
								@SuppressWarnings("unused")
								int codiceInt = Integer.parseInt(codice);
							}catch(Exception e) {
								throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
										" '"+codice+"' non è un numero intero");
							}
						}
					}
				}
			}
		}
		
	}
	
	static List<String> getProfiloSicurezzaMessaggio(AccordoServizioParteComune api, String portType) {
		return getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID, api, portType, false);
	}
	static boolean isProfiloSicurezzaMessaggioConIntegrita(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getProfiloSicurezzaMessaggio(api, portType);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	static boolean isProfiloSicurezzaMessaggioCorniceSicurezza(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_ID, api, portType, true);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String v : tmp) {
				if(v!=null && "true".equals(v)) {
					return true;
				}
			}
		}
		return false;
	}
	static boolean isProfiloSicurezzaMessaggioConHeaderDuplicati(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	static List<String> getPropertySicurezzaMessaggioEngine(String propertyName, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		return RegistroServiziUtils.fillPropertyProtocollo(propertyName, api, portType, booleanValue);
	}
	
	static boolean isSicurezzaMessaggioRequired(AccordoServizioParteComune api, String portType) {
		
		List<String> apiValues = getProfiloSicurezzaMessaggio(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String apiValue : apiValues) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(apiValue)) {
					return isProfiloSicurezzaMessaggioGenerazioneTokenIdAuthLocale(api, portType);
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(apiValue)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	static boolean isSicurezzaMessaggioRiferimentoX509Required(AccordoServizioParteComune api, String portType) {
		
		List<String> apiValues = getProfiloSicurezzaMessaggio(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String apiValue : apiValues) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(apiValue)) {
					return isProfiloSicurezzaMessaggioGenerazioneTokenIdAuthLocale(api, portType);
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(apiValue)) {
					return true;
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(apiValue)) {
					return isProfiloSicurezzaMessaggioGenerazioneTokenIdAuthLocale(api, portType); // (per token id-uath)
				}
			}
		}
		
		return false;
	}
	
	static boolean isSicurezzaMessaggioKidModeSupported(AccordoServizioParteComune api, String portType) {
		
		List<String> apiValues = getProfiloSicurezzaMessaggio(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String apiValue : apiValues) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(apiValue)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	static boolean isProfiloSicurezzaMessaggioGenerazioneTokenIdAuthLocale(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String sorgenteToken : tmp) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken)) {
					return false;
				}
			}
		}
		return true; // default
	}
	
	static boolean isProfiloSicurezzaMessaggioApplicabileRichiesta(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String applicabilita : tmp) {
				if(isProfiloSicurezzaMessaggioApplicabile(applicabilita, true) ) {
					return true;
				}
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(applicabilita) &&
					isProfiloSicurezzaMessaggioApplicabileRichiestaCustom(api, portType)) {
					return true;
				}
			}
		}
		return false;
	}
	static boolean isProfiloSicurezzaMessaggioApplicabileRisposta(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String applicabilita : tmp) {
				if(isProfiloSicurezzaMessaggioApplicabile(applicabilita, false) ) {
					return true;
				}
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(applicabilita) &&
					isProfiloSicurezzaMessaggioApplicabileRispostaCustom(api, portType)) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isProfiloSicurezzaMessaggioApplicabile(String applicabilita, boolean richiesta) {
		if(richiesta) {
			return ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(applicabilita) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA.equals(applicabilita) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(applicabilita) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS.equals(applicabilita);
		}	
		else {
			return ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(applicabilita) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(applicabilita) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(applicabilita) ||
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(applicabilita);
		}
	}
	private static boolean isProfiloSicurezzaMessaggioApplicabileRichiestaCustom(AccordoServizioParteComune api, String portType) {
		List<String> tmpCustom = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID, api, portType, false);
		if(tmpCustom!=null && !tmpCustom.isEmpty()) {
			for (String profiloSicurezzaMessaggioCustom : tmpCustom) {
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO.equals(profiloSicurezzaMessaggioCustom) ||
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO.equals(profiloSicurezzaMessaggioCustom) ) {
					return true;
				}	
			}
		}
		return false;
	}
	private static boolean isProfiloSicurezzaMessaggioApplicabileRispostaCustom(AccordoServizioParteComune api, String portType) {
		List<String> tmpCustom = getPropertySicurezzaMessaggioEngine(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_ID, api, portType, false);
		if(tmpCustom!=null && !tmpCustom.isEmpty()) {
			for (String profiloSicurezzaMessaggioCustom : tmpCustom) {
				if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO.equals(profiloSicurezzaMessaggioCustom) ||
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO.equals(profiloSicurezzaMessaggioCustom) ) {
					return true;
				}	
			}
		}
		return false;
	}
}
