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
package org.openspcoop2.protocol.modipa.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.AzioniUtils;
import org.openspcoop2.protocol.modipa.config.ModIAuditClaimConfig;
import org.openspcoop2.protocol.modipa.config.ModIAuditConfig;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.config.ModISignalHubConfig;
import org.openspcoop2.protocol.modipa.config.ModISignalHubParamConfig;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ConsoleItemInfo;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.NumberConsoleItem;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.properties.SubtitleConsoleItem;
import org.openspcoop2.protocol.sdk.properties.TitleConsoleItem;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaFruizioniServizio;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaRuoli;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.slf4j.Logger;

/**
 * ModIDynamicConfigurationAccordiParteSpecificaUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Burlon Tommaso (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities {
	
	private ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities() {}

	static void addSicurezzaMessaggio(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean rest, boolean fruizione, boolean request, 
			boolean casoSpecialeModificaNomeFruizione, boolean digest, 
			String patternDatiCorniceSicurezza, String schemaDatiCorniceSicurezza, 
			boolean headerDuplicati,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IDServizio idServizio, IDSoggetto idFruitore,
			boolean riferimentoX509, boolean kidMode,
			boolean auditOnly,
			boolean tokenNonLocale) throws ProtocolException {
		
		boolean requiredValue = !casoSpecialeModificaNomeFruizione;
		
		// Title e SubTitle Label
		
		if(request) {
			
			BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
					ModIConsoleCostanti.MODIPA_API_IMPL_RICHIESTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_RICHIESTA_LABEL);
			configuration.addConsoleItem(titolo );
			
			configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_LABEL));
		}
		else {
			
			BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
					ModIConsoleCostanti.MODIPA_API_IMPL_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_RISPOSTA_LABEL);
			configuration.addConsoleItem(titolo );
			
			configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_LABEL));
		}
		
		
		// Configurazione Firma
		
		if(rest) {
			addConfigurazioneFirmaRest(modiProperties,
					configuration, fruizione, request, digest, requiredValue, riferimentoX509);
		}
		else {
			addConfigurazioneFirmaSoap(modiProperties,
					configuration, fruizione, request, digest);
		}
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			// truststore per i certificati
			ModIDynamicConfigurationKeystoreUtilities.addTrustStoreCertificatiConfigChoice(configuration, false);
			
			if(rest) {
				// ssl per le url (x5u)
				ModIDynamicConfigurationKeystoreUtilities.addTrustStoreSSLConfigChoice(configuration, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE);
			}
		}
		
		if(!fruizione && !request) {
			// keystore
			ModIDynamicConfigurationKeystoreUtilities.addTrustStoreKeystoreErogazioneConfigChoice(configuration);
		}
		else if(fruizione && request) {
			// keystore
			ModIDynamicConfigurationKeystoreUtilities.addTrustStoreKeystoreFruizioneConfigChoice(configuration, tokenNonLocale);
		}

		
		
		// Created Ttl Time

		String idProfiloSicurezzaMessaggioIatTtlItem = null;
		String idProfiloSicurezzaMessaggioIatTtlSecondsItem = null;
		if(fruizione && !request) {
			idProfiloSicurezzaMessaggioIatTtlItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_RISPOSTA_ID;
			idProfiloSicurezzaMessaggioIatTtlSecondsItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_RISPOSTA_ID;
		}
		else if(!fruizione && request) {
			idProfiloSicurezzaMessaggioIatTtlItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_RICHIESTA_ID;
			idProfiloSicurezzaMessaggioIatTtlSecondsItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_RICHIESTA_ID;
		}
		if(idProfiloSicurezzaMessaggioIatTtlItem!=null && idProfiloSicurezzaMessaggioIatTtlSecondsItem!=null) {
			
			boolean modeIsDefault = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT);
			String labelModeItem = modeIsDefault ?
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_VALORE_DEFAULT : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_VALORE_RIDEFINITO;
			
			StringConsoleItem modeItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					idProfiloSicurezzaMessaggioIatTtlItem, 
					labelModeItem);
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT);
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_RIDEFINISCI,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_RIDEFINISCI);
			modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_DEFAULT_VALUE);
			modeItem.setReloadOnChange(true, true);
			configuration.addConsoleItem(modeItem);
			
			NumberConsoleItem secondsItem = (NumberConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
					ConsoleItemType.NUMBER,
					idProfiloSicurezzaMessaggioIatTtlSecondsItem,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_LABEL);
			secondsItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_NOTE);
			secondsItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_DEFAULT_VALUE);
			secondsItem.setRequired(requiredValue);
			secondsItem.setMin(1);
			if(modeIsDefault) {
				secondsItem.setRequired(false);
				secondsItem.setType(ConsoleItemType.HIDDEN);
			}
			configuration.addConsoleItem(secondsItem);
		}
		
		
		
		// Expiration Time

		String idProfiloSicurezzaMessaggioExpItem = null;
		if(fruizione && request) {
			idProfiloSicurezzaMessaggioExpItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_RICHIESTA_ID;
		}
		else if(!fruizione && !request) {
			idProfiloSicurezzaMessaggioExpItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_RISPOSTA_ID;
		}
		if(idProfiloSicurezzaMessaggioExpItem!=null) {
			NumberConsoleItem profiloSicurezzaMessaggioTTLItem = (NumberConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
					ConsoleItemType.NUMBER,
					idProfiloSicurezzaMessaggioExpItem,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_LABEL);
			if(fruizione) {
				profiloSicurezzaMessaggioTTLItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_NOTE);
			}
			else {
				profiloSicurezzaMessaggioTTLItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_NOTE_RESPONSE);
			}
			profiloSicurezzaMessaggioTTLItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_DEFAULT_VALUE);
			profiloSicurezzaMessaggioTTLItem.setRequired(requiredValue);
			profiloSicurezzaMessaggioTTLItem.setMin(1);
			configuration.addConsoleItem(profiloSicurezzaMessaggioTTLItem);
		}
		
		
		// Audit
		
		if(request) {
			StringConsoleItem profiloSicurezzaMessaggioAudienceItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_ID, 
					rest ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL :  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL);
			profiloSicurezzaMessaggioAudienceItem.setNote(fruizione ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_FRUIZIONE_NOTE :
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_EROGAZIONE_NOTE);
			profiloSicurezzaMessaggioAudienceItem.setRows(2);
			if(fruizione) {
				ConsoleItemInfo info = new ConsoleItemInfo(rest ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL :  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL);
				info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INFO);
				if(rest) {
					info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_REQUEST);
				}
				else {
					info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_REQUEST);
				}
				profiloSicurezzaMessaggioAudienceItem.setInfo(info);
			}
			configuration.addConsoleItem(profiloSicurezzaMessaggioAudienceItem);
		}
		else {
			if(fruizione) {
				BooleanConsoleItem profiloSicurezzaMessaggioAudienceItem = (BooleanConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.CHECKBOX,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_ID, 
						rest ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL :  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL);
				profiloSicurezzaMessaggioAudienceItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_FRUIZIONE_KEYSTORE_SA_NOTE);
				profiloSicurezzaMessaggioAudienceItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_DEFAULT);
				profiloSicurezzaMessaggioAudienceItem.setReloadOnChange(true, true);
				configuration.addConsoleItem(profiloSicurezzaMessaggioAudienceItem);
				
				StringConsoleItem audValueItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_AREA,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_ID, 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_LABEL);
				audValueItem.setRows(2);
				ConsoleItemInfo info = new ConsoleItemInfo(rest ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL :  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL);
				info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INFO);
				if(rest) {
					info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_RESPONSE);
				}
				else {
					info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_RESPONSE);
				}
				audValueItem.setInfo(info);
				configuration.addConsoleItem(audValueItem);
			}
		}
		
		
		// Claims
		if(rest && !auditOnly && 
				( 
						(request && fruizione)
						||
						(!request && !fruizione)
				)
			) {
			StringConsoleItem profiloSicurezzaMessaggioRestJwtClaimsItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					((request && fruizione) ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RISPOSTA_ID), 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL);
			profiloSicurezzaMessaggioRestJwtClaimsItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE);
			ConsoleItemInfo info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL);
			try {
				info.setHeaderBody(DynamicHelperCostanti.getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(request, 
						modiProperties.getUsedRestSecurityClaims(request, digest)));
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			info.setListBody(request ? DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST :
				DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE);
			profiloSicurezzaMessaggioRestJwtClaimsItem.setInfo(info);
			profiloSicurezzaMessaggioRestJwtClaimsItem.setRows(2);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRestJwtClaimsItem);
		}
		
		
		// Cornice Sicurezza
		
		if(request && patternDatiCorniceSicurezza!=null && StringUtils.isNotEmpty(patternDatiCorniceSicurezza)) {
			
			SubtitleConsoleItem subtitleItem = null;
			if(fruizione || 
					(
							/** serve per differenziarlo dall'audience dell'authorization token via PDND nell'erogazione !auditOnly &&  */
							!ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)
					)
				) {
				subtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_RICHIESTA_ID, 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_RICHIESTA_LABEL);
				subtitleItem.setCloseable(true);
				configuration.addConsoleItem(subtitleItem);
			}
			
			String lastItemId = null;
			
			if(fruizione) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)) {
					lastItemId = addCorniceSicurezzaLegacy(configuration, rest);
				}
				else {
					lastItemId = addCorniceSicurezzaSchema(configuration, rest,
							schemaDatiCorniceSicurezza, auditOnly);
				}
			}
			else {
				if(
						/** serve per differenziarlo dall'audience dell'authorization token via PDND nell'erogazione !auditOnly && */ 
						!ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)
				) {
					lastItemId = addCorniceSicurezzaSchemaAudience(configuration);
				}
			}
			
			if(subtitleItem!=null) {
				subtitleItem.setLastItemId(lastItemId);
			}
			
			// NOTA: se si aggiunge un elemento a questo posizione, riconfigurare setLastItemId nel subsection item
		}
		
		
				
		// Header Duplicati
		if(rest && headerDuplicati && 
				( 
						(request && fruizione)
						||
						(!request && !fruizione)
				)
			) {
			
			SubtitleConsoleItem subtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_GENERAZIONE_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI())); 
			subtitleItem.setCloseable(true);
			subtitleItem.setLastItemId(request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RISPOSTA_ID);
			configuration.addConsoleItem(subtitleItem);
						
			StringConsoleItem jtiItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL);
			jtiItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL_SAME,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_SAME);
			jtiItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL_DIFFERENT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DIFFERENT);
			jtiItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DEFAULT);
			jtiItem.setUseDefaultValueForCloseableSection(true);
			jtiItem.setReloadOnChange(true, true);
			configuration.addConsoleItem(jtiItem);
			
			StringConsoleItem jtiAsIdMessaggioItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL);
			jtiAsIdMessaggioItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION);
			jtiAsIdMessaggioItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL_MODI.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()),
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI);
			jtiAsIdMessaggioItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_DEFAULT);
			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DEFAULT.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_SAME)) {
				jtiAsIdMessaggioItem.setType(ConsoleItemType.HIDDEN);
			}
			else {
				jtiAsIdMessaggioItem.setUseDefaultValueForCloseableSection(true);
			}
			configuration.addConsoleItem(jtiAsIdMessaggioItem);
			
			if((request && fruizione)) {
				StringConsoleItem audItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.SELECT,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RICHIESTA_ID, 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL);
				audItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_SAME,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
				audItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_DIFFERENT,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
				audItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT);
				audItem.setUseDefaultValueForCloseableSection(true);
				audItem.setReloadOnChange(true, true);
				configuration.addConsoleItem(audItem);
				
				StringConsoleItem audValueItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_AREA,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID, 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_LABEL);
				audValueItem.setRows(2);
				if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME)) {
					audValueItem.setType(ConsoleItemType.HIDDEN);
				}
				else {
					audValueItem.setRequired(true);
				}
				configuration.addConsoleItem(audValueItem);
			}
			
			StringConsoleItem authorizationClaimsItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					(request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RISPOSTA_ID), 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL);
			authorizationClaimsItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_NOTE);
			ConsoleItemInfo infoAuthorization = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL);
			try {
				infoAuthorization.setHeaderBody(DynamicHelperCostanti.getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(request, 
						modiProperties.getUsedRestSecurityClaims(request, digest)));
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			infoAuthorization.setListBody(request ? DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST :
				DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE);
			authorizationClaimsItem.setInfo(infoAuthorization);
			authorizationClaimsItem.setRows(2);
			authorizationClaimsItem.setDefaultValue("");
			authorizationClaimsItem.setUseDefaultValueForCloseableSection(true);
			configuration.addConsoleItem(authorizationClaimsItem);
			
			StringConsoleItem modiClaimsItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					(request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RISPOSTA_ID), 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL.
						replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()));
			modiClaimsItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_NOTE);
			ConsoleItemInfo infoModi = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()));
			try {
				infoModi.setHeaderBody(DynamicHelperCostanti.getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(request, 
						modiProperties.getUsedRestSecurityClaims(request, digest)));
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			infoModi.setListBody(request ? DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST :
				DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE);
			modiClaimsItem.setInfo(infoModi);
			modiClaimsItem.setRows(2);
			modiClaimsItem.setDefaultValue("");
			modiClaimsItem.setUseDefaultValueForCloseableSection(true);
			configuration.addConsoleItem(modiClaimsItem);
			
			// NOTA: se si aggiunge un elemento a questo posizione, riconfigurare setLastItemId nel subsection item
		}
		
		// Header Duplicati
		if(rest && headerDuplicati && 
				( 
						(!request && fruizione)
						||
						(request && !fruizione)
				)
			) {
						
			SubtitleConsoleItem subtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_VALIDAZIONE_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()));
			subtitleItem.setCloseable(true);
			if(request) {
				subtitleItem.setLastItemId(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RICHIESTA_ID);
			}
			else {
				subtitleItem.setLastItemId(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RISPOSTA_ID);
			}
			configuration.addConsoleItem(subtitleItem);
			
			StringConsoleItem jtiItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL);
			jtiItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION);
			jtiItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_MODI.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()),
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI);
			jtiItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_DEFAULT);
			jtiItem.setUseDefaultValueForCloseableSection(true);
			configuration.addConsoleItem(jtiItem);
			
			StringConsoleItem audItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RICHIESTA_ID :
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL);
			audItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_SAME,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
			audItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_DIFFERENT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
			audItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT);
			audItem.setUseDefaultValueForCloseableSection(true);
			audItem.setReloadOnChange(true, true);
			configuration.addConsoleItem(audItem);
			
			StringConsoleItem audValueItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID :
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_LABEL);
			audValueItem.setRows(2);
			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME)) {
				audValueItem.setType(ConsoleItemType.HIDDEN);
			}
			else {
				audValueItem.setRequired(true);
			}
			ConsoleItemInfo info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL);
			info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INFO);
			info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_RESPONSE);
			audValueItem.setInfo(info);
			configuration.addConsoleItem(audValueItem);
			
			// NOTA: se si aggiunge un elemento a questo posizione, riconfigurare setLastItemId nel subsection item
			
		}
		
		
		// Header Duplicati (gestione con token non locale)
		if(!headerDuplicati && tokenNonLocale && 
				request && !fruizione	
			) {
			
			String labelSub = null;
			if(rest) {
				labelSub = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI());
			}
			else {
				labelSub = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_SOAP_LABEL;
			}
			
			SubtitleConsoleItem subtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_VALIDAZIONE_ID, 
					labelSub);
			subtitleItem.setCloseable(true);
			subtitleItem.setLastItemId(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_RICHIESTA_ID);
			configuration.addConsoleItem(subtitleItem);
			
			StringConsoleItem jtiItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_RICHIESTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL);
			jtiItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION);
			jtiItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_MODI.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()),
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI);
			jtiItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_DEFAULT);
			jtiItem.setUseDefaultValueForCloseableSection(true);
			configuration.addConsoleItem(jtiItem);
						
			// NOTA: se si aggiunge un elemento a questo posizione, riconfigurare setLastItemId nel subsection item
			
		}
		
		
		// Sicurezza OAuth
		
		if( fruizione && request ) {
			boolean sicurezzaMessaggioPresente = true;
			addSicurezzaTokenSignedJWT(rest,
					configuration,
					consoleOperationType, consoleHelper, 
					registryReader, configIntegrationReader, 
					idServizio, idFruitore,
					sicurezzaMessaggioPresente);
		}
		
		
		
		// TrustStore
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			
			// truststore per i certificati
			ModIDynamicConfigurationKeystoreUtilities.addTrustStoreConfigSubSection(configuration, false, false);
			
			if(rest) {
			
				// ssl per le url (x5u)
				ModIDynamicConfigurationKeystoreUtilities.addTrustStoreConfigSubSection(configuration, true, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE);
			
			}
			
		}
		
		
		// KeyStore
		
		if(!fruizione && !request) {
			ModIDynamicConfigurationKeystoreUtilities.addKeystoreConfig(configuration, true, false, requiredValue);
			
			boolean sicurezzaMessaggioPresente = true;
			if(rest && kidMode) {
				addSicurezzaOAuth(rest,
						configuration,
						consoleOperationType, consoleHelper, 
						registryReader, configIntegrationReader, 
						idServizio,
						sicurezzaMessaggioPresente);
			}
			
		}
		
		if(fruizione && request) {
			ModIDynamicConfigurationKeystoreUtilities.addKeystoreConfig(configuration, true, false, requiredValue);
		}
		
	}
	
	private static String addCorniceSicurezzaSchema(ConsoleConfiguration configuration, boolean rest,
			String schemaDatiCorniceSicurezza, boolean auditOnly) throws ProtocolException {
		
		String lastItemModeId = null; 
		
		List<ModIAuditConfig> list = ModIProperties.getInstance().getAuditConfig();
		if(list!=null && !list.isEmpty()) {
			for (ModIAuditConfig modIAuditConfig : list) {
				if(modIAuditConfig.getNome().equals(schemaDatiCorniceSicurezza)) {
					lastItemModeId = addCorniceSicurezzaSchema(configuration, rest,
							modIAuditConfig,
							auditOnly);
					break;
				}
			}
		}
		
		return lastItemModeId;
	}
	private static String addCorniceSicurezzaSchemaAudience(ConsoleConfiguration configuration) throws ProtocolException {
		String id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_RICHIESTA_ID;
		StringConsoleItem audItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				id, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_LABEL);
		audItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_LABEL_SAME,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_SAME);
		audItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_LABEL_DIFFERENT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_DIFFERENT);
		audItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_DEFAULT);
		audItem.setUseDefaultValueForCloseableSection(true);
		audItem.setReloadOnChange(true, true);
		configuration.addConsoleItem(audItem);
		
		StringConsoleItem audValueItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_AUDIT_CUSTOM_RICHIESTA_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_AUDIT_CUSTOM_LABEL);
		audValueItem.setRows(2);
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_DEFAULT.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_SAME)) {
			audValueItem.setType(ConsoleItemType.HIDDEN);
		}
		else {
			audValueItem.setRequired(true);
		}
		configuration.addConsoleItem(audValueItem);	
		
		return id;
	}
	
	private static String addCorniceSicurezzaSchema(ConsoleConfiguration configuration, boolean rest,
			ModIAuditConfig modIAuditConfig, boolean auditOnly) throws ProtocolException {
		
		String lastItemModeId = null; 
				
		if(modIAuditConfig.getClaims()!=null && !modIAuditConfig.getClaims().isEmpty()) {
			
			if(!auditOnly) {
				addCorniceSicurezzaSchemaAudience(configuration);
			}
			
			for (ModIAuditClaimConfig claimConfig : modIAuditConfig.getClaims()) {
				lastItemModeId = addCorniceSicurezzaSchemaItem(configuration, claimConfig, rest);
			}
		}
		
		return lastItemModeId;
	}
	
	private static String addCorniceSicurezzaSchemaItem(ConsoleConfiguration configuration, ModIAuditClaimConfig claimConfig, boolean rest) throws ProtocolException {
		
		String modeId = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_ID_PREFIX+claimConfig.getNome(); 
		
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				modeId, 
				claimConfig.getLabel());
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_VALUE);
		modeItem.setUseDefaultValueForCloseableSection(true);
		modeItem.setReloadOnChange(true, true);
		configuration.addConsoleItem(modeItem);
		
		StringConsoleItem ridefineItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_ID_PREFIX+claimConfig.getNome(),
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_LABEL);
		ridefineItem.setRequired(true);
		ridefineItem.setRows(2);
		ridefineItem.setInfo(buildConsoleItemInfoCorniceSicurezza(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL, rest));
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT)) {
			ridefineItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(ridefineItem);
		
		return modeId;
	}
	
	static String addCorniceSicurezzaLegacy(ConsoleConfiguration configuration, boolean rest) throws ProtocolException {
		StringConsoleItem modeCodiceEnteItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL);
		modeCodiceEnteItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		modeCodiceEnteItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		modeCodiceEnteItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_DEFAULT_VALUE);
		modeCodiceEnteItem.setUseDefaultValueForCloseableSection(true);
		modeCodiceEnteItem.setReloadOnChange(true, true);
		configuration.addConsoleItem(modeCodiceEnteItem);
		
		StringConsoleItem profiloSicurezzaMessaggioCorniceCodiceEnteItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_LABEL);
		profiloSicurezzaMessaggioCorniceCodiceEnteItem.setRequired(true);
		profiloSicurezzaMessaggioCorniceCodiceEnteItem.setRows(2);
		profiloSicurezzaMessaggioCorniceCodiceEnteItem.setInfo(buildConsoleItemInfoCorniceSicurezza(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL, rest));
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT)) {
			profiloSicurezzaMessaggioCorniceCodiceEnteItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(profiloSicurezzaMessaggioCorniceCodiceEnteItem);
		
		StringConsoleItem modeUserItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL);
		modeUserItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		modeUserItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		modeUserItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_VALUE);
		modeUserItem.setUseDefaultValueForCloseableSection(true);
		modeUserItem.setReloadOnChange(true, true);
		configuration.addConsoleItem(modeUserItem);
		
		StringConsoleItem profiloSicurezzaMessaggioCorniceUserItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_LABEL);
		profiloSicurezzaMessaggioCorniceUserItem.setRequired(true);
		profiloSicurezzaMessaggioCorniceUserItem.setRows(2);
		profiloSicurezzaMessaggioCorniceUserItem.setInfo(buildConsoleItemInfoCorniceSicurezza(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL, rest));
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT)) {
			profiloSicurezzaMessaggioCorniceUserItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(profiloSicurezzaMessaggioCorniceUserItem);
		
		StringConsoleItem modeIPUserItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL);
		modeIPUserItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		modeIPUserItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		modeIPUserItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_VALUE);
		modeIPUserItem.setUseDefaultValueForCloseableSection(true);
		modeIPUserItem.setReloadOnChange(true, true);
		configuration.addConsoleItem(modeIPUserItem);
		
		StringConsoleItem profiloSicurezzaMessaggioCorniceIPUserItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_LABEL);
		profiloSicurezzaMessaggioCorniceIPUserItem.setRequired(true);
		profiloSicurezzaMessaggioCorniceIPUserItem.setRows(2);
		profiloSicurezzaMessaggioCorniceIPUserItem.setInfo(buildConsoleItemInfoCorniceSicurezza(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL, rest));
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT)) {
			profiloSicurezzaMessaggioCorniceIPUserItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(profiloSicurezzaMessaggioCorniceIPUserItem);
		
		return ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_ID; // devo ritornare l'ultimo elemento "mode" per la chiusura della sezione
	}
	
	
	static boolean addSicurezzaTokenSignedJWT(boolean rest,
			ConsoleConfiguration configuration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IDServizio idServizio, IDSoggetto idFruitore,
			boolean sicurezzaMessaggioPresente) throws ProtocolException {
				
		if( registryReader!=null && configIntegrationReader!=null ) {
			
			boolean tokenSignedJWT = false;
			boolean pdnd = false;
			if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
				
				String tokenPolicyViaAPI = null;
				try {
					tokenPolicyViaAPI = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY_VIA_API);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				if(tokenPolicyViaAPI!=null && StringUtils.isNotEmpty(tokenPolicyViaAPI) && !Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicyViaAPI)) {
					tokenSignedJWT = isTokenPolicySignedJWT(configIntegrationReader, tokenPolicyViaAPI);
					if(tokenSignedJWT) {
						pdnd = isTokenPolicyPdnd(configIntegrationReader, tokenPolicyViaAPI);
					}
				}
				else {
				
					String tokenPolicyStato = null;
					try {
						tokenPolicyStato = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
					if(isEnabled(tokenPolicyStato)) {
					
						String tokenPolicy = null;
						try {
							tokenPolicy = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
						if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
							tokenSignedJWT = isTokenPolicySignedJWT(configIntegrationReader, tokenPolicy);
							if(tokenSignedJWT) {
								pdnd = isTokenPolicyPdnd(configIntegrationReader, tokenPolicy);
							}
						}
						
					}
					
				}
			}
			else {
				
				if(idServizio!=null && idFruitore!=null) {
					try {
						AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
						if(asps!=null && asps.sizeFruitoreList()>0) {
							for (Fruitore fruitore : asps.getFruitoreList()) {
								if(fruitore!=null) {
									IDSoggetto check = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
									if(idFruitore.equals(check)) {
										if(fruitore.getConnettore()!=null && !TipiConnettore.DISABILITATO.getNome().equals(fruitore.getConnettore().getTipo()) &&
											fruitore.getConnettore().sizePropertyList()>0) {
											for (Property p : fruitore.getConnettore().getPropertyList()) {
												if(CostantiConnettori.CONNETTORE_TOKEN_POLICY.equals(p.getNome())){
													String tokenPolicy = p.getValore();
													if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
														if(!tokenSignedJWT) {
															// se ancora non ne ho trovata una verifico
															tokenSignedJWT = isTokenPolicySignedJWT(configIntegrationReader, tokenPolicy);
														}
														if(tokenSignedJWT &&
															!pdnd) {
															// se non ne ho trovata ancora una pdnd, verifico
															pdnd = isTokenPolicyPdnd(configIntegrationReader, tokenPolicy);
														}
													}
												}
											}
										}
										if(fruitore.getConfigurazioneAzioneList()!=null && !fruitore.getConfigurazioneAzioneList().isEmpty()) {
											for (ConfigurazioneServizioAzione csa : fruitore.getConfigurazioneAzioneList()) {
												if(csa!=null &&
													csa.getConnettore()!=null && !TipiConnettore.DISABILITATO.getNome().equals(csa.getConnettore().getTipo()) &&
													csa.getConnettore().sizePropertyList()>0) {
													for (Property p : csa.getConnettore().getPropertyList()) {
														if(CostantiConnettori.CONNETTORE_TOKEN_POLICY.equals(p.getNome())){
															String tokenPolicy = p.getValore();
															if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
																if(!tokenSignedJWT) {
																	// se ancora non ne ho trovata una verifico
																	tokenSignedJWT = isTokenPolicySignedJWT(configIntegrationReader, tokenPolicy);
																}
																if(tokenSignedJWT &&
																	!pdnd) {
																	// se non ne ho trovata ancora una pdnd, verifico
																	pdnd = isTokenPolicyPdnd(configIntegrationReader, tokenPolicy);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
					
			}
			
			if(tokenSignedJWT) {
			
				addSicurezzaOAuth(rest, configuration,
						pdnd,
						sicurezzaMessaggioPresente,
						true);
				
			}
			
			return tokenSignedJWT;
		}
		
		return false;
	}
	
	private static void addSicurezzaOAuth(boolean rest,
			ConsoleConfiguration configuration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IDServizio idServizio,
			boolean sicurezzaMessaggioPresente) throws ProtocolException {
				
		if( registryReader!=null && configIntegrationReader!=null ) {
			
			boolean pdnd = false;
			if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
				
				String tokenPolicy = null;
				try {
					tokenPolicy = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_EROGAZIONE_TOKEN_POLICY);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
					pdnd = isTokenPolicyErogazionePDND(tokenPolicy);		
				}
			}
			else {
				pdnd = isTokenPolicyErogazionePDNDChangeOperation(registryReader, configIntegrationReader, 
						idServizio);
			}
			
			addSicurezzaOAuth(rest, configuration,
					pdnd,
					sicurezzaMessaggioPresente,
					false);
			
		}

	}
	private static boolean isTokenPolicyErogazionePDNDChangeOperation(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IDServizio idServizio) throws ProtocolException {
		if(idServizio!=null) {
			try {
				AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
				if(asps!=null) {
					List<MappingErogazionePortaApplicativa> list = configIntegrationReader.getMappingErogazionePortaApplicativaList(idServizio);
					return isTokenPolicyErogazionePDNDChangeOperation(list, configIntegrationReader);
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return false;
	}
	private static boolean isTokenPolicyErogazionePDNDChangeOperation(List<MappingErogazionePortaApplicativa> list, IConfigIntegrationReader configIntegrationReader) throws RegistryNotFound, RegistryException {
		if(list!=null && !list.isEmpty()) {
			for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : list) {
				PortaApplicativa pa = configIntegrationReader.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
				if(pa!=null && pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null) {
					boolean pdnd = isTokenPolicyErogazionePDND(pa.getGestioneToken().getPolicy());
					if(pdnd) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isTokenPolicyErogazionePDND(String tokenPolicy) {
		if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !Costanti.CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
			return tokenPolicy.toLowerCase().contains("pdnd");			
		}
		return false;
	}
	
	private static void addSicurezzaOAuth(boolean rest, ConsoleConfiguration configuration,
			boolean pdnd,
			boolean sicurezzaMessaggioPresente,
			boolean section) throws ProtocolException {
		if(section) {
			if(sicurezzaMessaggioPresente) {
				String label = pdnd ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_LABEL_PDND : 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_LABEL_OAUTH;
				SubtitleConsoleItem subtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_ID, 
						label);
				subtitleItem.setCloseable(true);
				subtitleItem.setLastItemId(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID);
				configuration.addConsoleItem(subtitleItem);
			}
			else {
				String label = pdnd ? ModIConsoleCostanti.MODIPA_API_IMPL_SICUREZZA_OAUTH_LABEL_PDND : 
					ModIConsoleCostanti.MODIPA_API_IMPL_SICUREZZA_OAUTH_LABEL_OAUTH;
				BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
						ModIConsoleCostanti.MODIPA_API_IMPL_SICUREZZA_OAUTH_ID, 
						label);
				configuration.addConsoleItem(titolo );
			}
		}
				
		StringConsoleItem profiloSicurezzaOauthKidItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_LABEL);
		profiloSicurezzaOauthKidItem.setRows(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ROWS);
		ConsoleItemInfo info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_LABEL);
		info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_INFO);
		if(rest) {
			info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_REST_INFO_VALORI_REQUEST);
		}
		else {
			info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SOAP_INFO_VALORI_REQUEST);
		}
		profiloSicurezzaOauthKidItem.setInfo(info);
		if(sicurezzaMessaggioPresente && section) {
			profiloSicurezzaOauthKidItem.setDefaultValue("");
			profiloSicurezzaOauthKidItem.setUseDefaultValueForCloseableSection(true);
		}
		configuration.addConsoleItem(profiloSicurezzaOauthKidItem);
		
		
		
		StringConsoleItem profiloSicurezzaOauthIdentificativoItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_LABEL);
		profiloSicurezzaOauthIdentificativoItem.setRows(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ROWS);
		info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_LABEL);
		info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_INFO);
		if(rest) {
			info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_REST_INFO_VALORI_REQUEST);
		}
		else {
			info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SOAP_INFO_VALORI_REQUEST);
		}
		profiloSicurezzaOauthIdentificativoItem.setInfo(info);
		if(sicurezzaMessaggioPresente && section) {
			profiloSicurezzaOauthIdentificativoItem.setDefaultValue("");
			profiloSicurezzaOauthIdentificativoItem.setUseDefaultValueForCloseableSection(true);
		}
		configuration.addConsoleItem(profiloSicurezzaOauthIdentificativoItem);
		
		
		
		// NOTA: se si aggiunge un elemento a questo posizione, riconfigurare setLastItemId nel subsection item
	}
			
	
	static boolean isTokenPolicySignedJWT(IConfigIntegrationReader configIntegrationReader, String tokenPolicy) throws ProtocolException {
		return ModIPropertiesUtils.isTokenPolicySignedJWT(configIntegrationReader, tokenPolicy);
	}
	
	static boolean isTokenPolicyPdnd(IConfigIntegrationReader configIntegrationReader, String tokenPolicy) throws ProtocolException {
		return ModIPropertiesUtils.isTokenPolicyPdnd(configIntegrationReader, tokenPolicy);
	}
	
	private static boolean isEnabled(String v) {
		return "true".equals(v) || "yes".equals(v);
	}
	
	static ConsoleItemInfo buildConsoleItemInfoCorniceSicurezza(String intestazione, boolean rest) {
		
		try {
			ConsoleItemInfo c = new ConsoleItemInfo(intestazione);
			
			c.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			
			boolean modi = true;
			boolean fruizione = false; // e' ininfluente tanto poi faccio il forceNoSecToken
			boolean forceNoSecToken = true;
			if(rest) {
				c.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI(modi,fruizione,forceNoSecToken));
			}
			else {
				c.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI(modi,fruizione,forceNoSecToken));
			}
			
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
	
	static void addConfigurazioneFirmaRest(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean fruizione, boolean request, boolean digest, boolean requiredValue, boolean riferimentoX509) throws ProtocolException {
		String idProfiloSicurezzaMessaggioAlgItem = null;
		if(fruizione && request) {
			idProfiloSicurezzaMessaggioAlgItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RICHIESTA_ID;
		}
		else if(!fruizione && !request) {
			idProfiloSicurezzaMessaggioAlgItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RISPOSTA_ID;
		}
		if(idProfiloSicurezzaMessaggioAlgItem!=null) {
			StringConsoleItem profiloSicurezzaMessaggioAlgItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					idProfiloSicurezzaMessaggioAlgItem, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_LABEL);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS256,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS256);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS384,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS384);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS512,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS512);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES256,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES256);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES384,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES384);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES512,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES512);
			profiloSicurezzaMessaggioAlgItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_DEFAULT_VALUE);
			configuration.addConsoleItem(profiloSicurezzaMessaggioAlgItem);
		}
		
		if(digest) {
			String idProfiloSicurezzaMessaggioDigestEncodingItem = null;
			if(fruizione && request) {
				idProfiloSicurezzaMessaggioDigestEncodingItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_RICHIESTA_ID;
			}
			else if(!fruizione && !request) {
				idProfiloSicurezzaMessaggioDigestEncodingItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_RISPOSTA_ID;
			}
			if(idProfiloSicurezzaMessaggioDigestEncodingItem!=null) {
				StringConsoleItem profiloSicurezzaMessaggioDigestEncodingItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.SELECT,
						idProfiloSicurezzaMessaggioDigestEncodingItem, 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL);
				profiloSicurezzaMessaggioDigestEncodingItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_BASE64,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_VALUE_BASE64);
				profiloSicurezzaMessaggioDigestEncodingItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_HEX,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_VALUE_HEX);
				try {
					profiloSicurezzaMessaggioDigestEncodingItem.setDefaultValue(modiProperties.getRestSecurityTokenDigestDefaultEncoding().name());
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				profiloSicurezzaMessaggioDigestEncodingItem.setType(ConsoleItemType.HIDDEN);
				configuration.addConsoleItem(profiloSicurezzaMessaggioDigestEncodingItem);
			}
		}
		
		if(digest &&
			( (request && fruizione) || (!request && !fruizione) ) 
		){

			StringConsoleItem profiloSicurezzaMessaggioHttpHeadersItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TAGS,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL);
			/**profiloSicurezzaMessaggioHttpHeadersItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_NOTE);
			//profiloSicurezzaMessaggioHttpHeadersItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_DEFAULT_VALUE); */
			try {
				profiloSicurezzaMessaggioHttpHeadersItem.setDefaultValue(modiProperties.getRestSecurityTokenSignedHeadersAsString());
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			profiloSicurezzaMessaggioHttpHeadersItem.setRequired(true);
			configuration.addConsoleItem(profiloSicurezzaMessaggioHttpHeadersItem);
			
		}
		
		if(!request && riferimentoX509) {
			StringConsoleItem profiloSicurezzaMessaggioRifX509AsRequestItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_LABEL);
			profiloSicurezzaMessaggioRifX509AsRequestItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_LABEL_TRUE,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE);
			profiloSicurezzaMessaggioRifX509AsRequestItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_LABEL_FALSE,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE);
			profiloSicurezzaMessaggioRifX509AsRequestItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_DEFAULT_VALUE);
			profiloSicurezzaMessaggioRifX509AsRequestItem.setReloadOnChange(true, true);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509AsRequestItem);
		}
		
		if(riferimentoX509) {
			String rifX509Id = request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_ID :
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_ID;
			StringConsoleItem profiloSicurezzaMessaggioRifX509Item = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.MULTI_SELECT,
					rifX509Id, 
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL : "");
			profiloSicurezzaMessaggioRifX509Item.setRows(3);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL_X5C,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL_X5T,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL_X5U,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U);
			List<String> profiloSicurezzaMessaggioRifX509ItemDefault = new ArrayList<>();
			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_DEFAULT_VALUE) {
				profiloSicurezzaMessaggioRifX509ItemDefault.add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C);
			}
			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5T_DEFAULT_VALUE) {
				profiloSicurezzaMessaggioRifX509ItemDefault.add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T);
			}
			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE) {
				profiloSicurezzaMessaggioRifX509ItemDefault.add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U);
			}
			ProtocolPropertiesUtils.setDefaultValueMultiSelect(profiloSicurezzaMessaggioRifX509ItemDefault, profiloSicurezzaMessaggioRifX509Item);
			profiloSicurezzaMessaggioRifX509Item.setReloadOnChange(true, true);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509Item);
			
			String rifX509Xc5ChainId = null;
			if(fruizione && request) {
				rifX509Xc5ChainId = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_ID;
			}
			else if(!fruizione && !request) {
				rifX509Xc5ChainId = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_ID;
			}
			if(rifX509Xc5ChainId!=null) {
				BooleanConsoleItem profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificate = (BooleanConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.CHECKBOX,
						rifX509Xc5ChainId,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_LABEL);
				if(!ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_DEFAULT_VALUE) {
					profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificate.setType(ConsoleItemType.HIDDEN);
				}
				profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificate.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_DEFAULT_VALUE);
				configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificate);
			}
		}
		
		String idUrl = null;
		if(fruizione && request) {
			idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X5U_URL_ID;
		}
		else if(!fruizione && !request) {
			idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X5U_URL_ID;
		}
		if(idUrl!=null) {
			StringConsoleItem profiloSicurezzaMessaggioX5UItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				idUrl, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
			profiloSicurezzaMessaggioX5UItem.setRows(2);
			profiloSicurezzaMessaggioX5UItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_NOTE);
			ConsoleItemInfo infoX5U = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
			infoX5U.setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_INFO);
			profiloSicurezzaMessaggioX5UItem.setInfo(infoX5U);
			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE) {
				profiloSicurezzaMessaggioX5UItem.setRequired(requiredValue);
			}
			else {
				profiloSicurezzaMessaggioX5UItem.setRequired(false);
				profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.HIDDEN);
			}
			
			if(fruizione && request) {
				profiloSicurezzaMessaggioX5UItem.setRequired(false);
				profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.HIDDEN);
			}
			
			configuration.addConsoleItem(profiloSicurezzaMessaggioX5UItem);
		}

	}
	
	static void addConfigurazioneFirmaSoap(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean fruizione, boolean request, boolean digest) throws ProtocolException {
		
		String idProfiloSicurezzaMessaggioAlgItem = null;
		if(fruizione && request) {
			idProfiloSicurezzaMessaggioAlgItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RICHIESTA_ID;
		}
		else if(!fruizione && !request) {
			idProfiloSicurezzaMessaggioAlgItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RISPOSTA_ID;
		}
		if(idProfiloSicurezzaMessaggioAlgItem!=null) {
			StringConsoleItem profiloSicurezzaMessaggioAlgItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					idProfiloSicurezzaMessaggioAlgItem, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_DSA_SHA_256,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_DSA_SHA_256);
			if((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue())) {
				profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_224,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_224);
			}
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_256,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_256);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_384,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_384);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_512,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_512);
			if((modiProperties.isModIVersioneBozza()!=null && modiProperties.isModIVersioneBozza().booleanValue())) {
				profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_224,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_224);
			}
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_256,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_256);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_384,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_384);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_512,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_512);
			profiloSicurezzaMessaggioAlgItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DEFAULT_VALUE);
			configuration.addConsoleItem(profiloSicurezzaMessaggioAlgItem);
		}
		
		String idProfiloSicurezzaMessaggioAlgC14NItem = null;
		if(fruizione && request) {
			idProfiloSicurezzaMessaggioAlgC14NItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_RICHIESTA_ID;
		}
		else if(!fruizione && !request) {
			idProfiloSicurezzaMessaggioAlgC14NItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_RISPOSTA_ID;
		}
		if(idProfiloSicurezzaMessaggioAlgC14NItem!=null) {
			StringConsoleItem profiloSicurezzaMessaggioAlgC14NItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					idProfiloSicurezzaMessaggioAlgC14NItem, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL);
			profiloSicurezzaMessaggioAlgC14NItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_10,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_INCLUSIVE_C14N_10);
			profiloSicurezzaMessaggioAlgC14NItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_11,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_INCLUSIVE_C14N_11);
			profiloSicurezzaMessaggioAlgC14NItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_EXCLUSIVE_C14N_10,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_EXCLUSIVE_C14N_10);
			profiloSicurezzaMessaggioAlgC14NItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_DEFAULT_VALUE);
			configuration.addConsoleItem(profiloSicurezzaMessaggioAlgC14NItem);
		}
		
		
		if(digest &&
			( (request && fruizione) || (!request && !fruizione) ) 
			){

			StringConsoleItem profiloSicurezzaMessaggioSoapHeadersItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL);
			profiloSicurezzaMessaggioSoapHeadersItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_NOTE);
			profiloSicurezzaMessaggioSoapHeadersItem.setRequired(false);
			profiloSicurezzaMessaggioSoapHeadersItem.setRows(3);
			ConsoleItemInfo info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL);
			info.setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_INFO);
			profiloSicurezzaMessaggioSoapHeadersItem.setInfo(info);
			configuration.addConsoleItem(profiloSicurezzaMessaggioSoapHeadersItem);
			
		}
		
		
		String rifX509Id = null;
		if(fruizione && request) {
			rifX509Id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_ID;
		}
		else if(!fruizione && !request) {
			rifX509Id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_ID;
		}
		if(rifX509Id!=null) {

			StringConsoleItem profiloSicurezzaMessaggioRifX509Item = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					rifX509Id, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_BINARY_SECURITY_TOKEN,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_SECURITY_TOKEN_REFERENCE,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_X509,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_THUMBPRINT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT);
			profiloSicurezzaMessaggioRifX509Item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_SKI,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI);
			profiloSicurezzaMessaggioRifX509Item.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_DEFAULT_VALUE);
			profiloSicurezzaMessaggioRifX509Item.setReloadOnChange(true, true);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509Item);
			
			BooleanConsoleItem profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificate = (BooleanConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
					ConsoleItemType.CHECKBOX,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_ID:
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_ID,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_LABEL);
			if(! ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_DEFAULT_VALUE)) {
				profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificate.setType(ConsoleItemType.HIDDEN);
			}
			profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificate.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_DEFAULT_VALUE);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificate);
			
			BooleanConsoleItem profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenIncludeSignatureToken = (BooleanConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
					ConsoleItemType.CHECKBOX,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_ID:
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_ID,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_LABEL);
			if( !ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_DEFAULT_VALUE)
				&&
				!ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_DEFAULT_VALUE)
				&&
				!ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_DEFAULT_VALUE)
				) {
				profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenIncludeSignatureToken.setType(ConsoleItemType.HIDDEN);
			}
			profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenIncludeSignatureToken.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_DEFAULT_VALUE);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenIncludeSignatureToken);
			
		}
	}
	
	static void updateSicurezzaMessaggio(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, 
			boolean rest, boolean fruizione, boolean request, boolean casoSpecialeModificaNomeFruizione,
			String patternDatiCorniceSicurezza, String schemaDatiCorniceSicurezza, 
			boolean headerDuplicati,
			IConsoleHelper consoleHelper,
			boolean kidMode, 
			boolean tokenNonLocale) throws ProtocolException {
		
		boolean requiredValue = !casoSpecialeModificaNomeFruizione;
		
		if(fruizione && !request) {
			
			String idFruizioneKeystoreFruizioneMode = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_ID;
			StringProperty profiloSicurezzaKeystoreModeFruizioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idFruizioneKeystoreFruizioneMode);
			boolean keystoreDefinitoInFruizione = false;
			if(profiloSicurezzaKeystoreModeFruizioneItemValue!=null && profiloSicurezzaKeystoreModeFruizioneItemValue.getValue()!=null && 
					ModIConsoleCostanti.MODIPA_KEYSTORE_FRUIZIONE.equals(profiloSicurezzaKeystoreModeFruizioneItemValue.getValue())) {
				keystoreDefinitoInFruizione = true;
			}
			
			String idPropertyAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_ID;
			String idPropertyAudValue = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_ID;
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyAud);
			if(profiloSicurezzaMessaggioRestAudItem!=null) {
				if(keystoreDefinitoInFruizione) {
					profiloSicurezzaMessaggioRestAudItem.setNote(null);
				}
				else {
					profiloSicurezzaMessaggioRestAudItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_FRUIZIONE_KEYSTORE_SA_NOTE);
				}
			}
			
			BooleanProperty profiloSicurezzaMessaggioRestAudItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAud);
			StringProperty profiloSicurezzaMessaggioRestAudValueItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAudValue);
			AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudValueItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyAudValue);
			if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
				if(profiloSicurezzaMessaggioRestAudItemValue!=null && profiloSicurezzaMessaggioRestAudItemValue.getValue()!=null && profiloSicurezzaMessaggioRestAudItemValue.getValue().booleanValue()) {
					profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.TEXT_AREA);
					profiloSicurezzaMessaggioRestAudValueItem.setRequired(keystoreDefinitoInFruizione);
				}
				else {
					if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
						profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
						profiloSicurezzaMessaggioRestAudValueItem.setRequired(false);
					}
					if(profiloSicurezzaMessaggioRestAudValueItemValue!=null) {
						profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
					}
				}
			}

		}
		
		boolean keystoreDefinitoInFruizione = false;
		if(fruizione && request) {
			String idFruizioneKeystoreFruizioneMode = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_ID;
			String idFruizioneKeystoreMode = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_ID;
			
			StringProperty profiloSicurezzaKeystoreModeFruizioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idFruizioneKeystoreFruizioneMode);
			
			AbstractConsoleItem<?> profiloSicurezzaKeystoreModeItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idFruizioneKeystoreMode);
						
			if(profiloSicurezzaKeystoreModeFruizioneItemValue!=null && profiloSicurezzaKeystoreModeFruizioneItemValue.getValue()!=null && 
					ModIConsoleCostanti.MODIPA_KEYSTORE_FRUIZIONE.equals(profiloSicurezzaKeystoreModeFruizioneItemValue.getValue())) {
				if(profiloSicurezzaKeystoreModeItem!=null) {
					profiloSicurezzaKeystoreModeItem.setType(ConsoleItemType.SELECT);
					keystoreDefinitoInFruizione = true;
				}
			}
			else {
				if(profiloSicurezzaKeystoreModeItem!=null) {
					profiloSicurezzaKeystoreModeItem.setType(ConsoleItemType.HIDDEN);
				}
				StringProperty profiloSicurezzaKeystoreModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idFruizioneKeystoreMode);
				if(profiloSicurezzaKeystoreModeItemValue!=null) {
					profiloSicurezzaKeystoreModeItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_DEFAULT_VALUE);
				}
			}
			
		}
		
		// Created Ttl Time

		String idProfiloSicurezzaMessaggioIatTtlItem = null;
		String idProfiloSicurezzaMessaggioIatTtlSecondsItem = null;
		if(fruizione && !request) {
			idProfiloSicurezzaMessaggioIatTtlItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_RISPOSTA_ID;
			idProfiloSicurezzaMessaggioIatTtlSecondsItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_RISPOSTA_ID;
		}
		else if(!fruizione && request) {
			idProfiloSicurezzaMessaggioIatTtlItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_RICHIESTA_ID;
			idProfiloSicurezzaMessaggioIatTtlSecondsItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_RICHIESTA_ID;
		}
		if(idProfiloSicurezzaMessaggioIatTtlItem!=null && idProfiloSicurezzaMessaggioIatTtlSecondsItem!=null) {
			
			AbstractConsoleItem<?> modeItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idProfiloSicurezzaMessaggioIatTtlItem);
			StringProperty modeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProfiloSicurezzaMessaggioIatTtlItem);
			boolean modeIsDefault = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT);
			if(modeItemValue!=null && modeItemValue.getValue()!=null) {
				modeIsDefault = modeItemValue.getValue().equals(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT);
			}
			
			if(modeItem!=null) {
				modeItem.setLabel(modeIsDefault ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_VALORE_DEFAULT :
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_VALORE_RIDEFINITO);
			}
			
			AbstractConsoleItem<?> secondsItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idProfiloSicurezzaMessaggioIatTtlSecondsItem);
			NumberProperty secondsItemValue = (NumberProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProfiloSicurezzaMessaggioIatTtlSecondsItem);
			if(secondsItem!=null) {
				if(modeIsDefault) {
					secondsItem.setRequired(false);
					secondsItem.setType(ConsoleItemType.HIDDEN);
					if(secondsItemValue!=null) {
						secondsItemValue.setValue(null);
					}
				}
				else {
					secondsItem.setRequired(requiredValue);
					secondsItem.setType(ConsoleItemType.NUMBER);
					if(secondsItemValue!=null && secondsItemValue.getValue()==null) {
						secondsItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_DEFAULT_VALUE);
					}
				}
			}
			
		}
		
		boolean x5url = false;
		if(rest) {
		
			String idProfiloSicurezzaMessaggioDigestEncodingItem = null;
			if(fruizione && request) {
				idProfiloSicurezzaMessaggioDigestEncodingItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_RICHIESTA_ID;
			}
			else if(!fruizione && !request) {
				idProfiloSicurezzaMessaggioDigestEncodingItem = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_RISPOSTA_ID;
			}
			if(idProfiloSicurezzaMessaggioDigestEncodingItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioDigestEncodingItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProfiloSicurezzaMessaggioDigestEncodingItem);
				DigestEncoding actualValue = null;
				if(profiloSicurezzaMessaggioDigestEncodingItemValue!=null && profiloSicurezzaMessaggioDigestEncodingItemValue.getValue()!=null && StringUtils.isNotEmpty(profiloSicurezzaMessaggioDigestEncodingItemValue.getValue())) {
					try {
						actualValue = DigestEncoding.valueOf(profiloSicurezzaMessaggioDigestEncodingItemValue.getValue());
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				AbstractConsoleItem<?> profiloSicurezzaMessaggioDigestEncodingItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idProfiloSicurezzaMessaggioDigestEncodingItem);
				
				boolean show = false;
				try {
					show = modiProperties.isRestSecurityTokenDigestEncodingChoice();
					if(show && consoleHelper.isModalitaStandard()) {
						show = false;
					}
					if(!show &&
						actualValue!=null && !actualValue.equals(modiProperties.getRestSecurityTokenDigestDefaultEncoding())) {
						show=true;
					}
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				if(show) {
					if(profiloSicurezzaMessaggioDigestEncodingItem!=null) {
						profiloSicurezzaMessaggioDigestEncodingItem.setType(ConsoleItemType.SELECT);
					}
				}
				else {
					if(profiloSicurezzaMessaggioDigestEncodingItem!=null) {
						profiloSicurezzaMessaggioDigestEncodingItem.setType(ConsoleItemType.HIDDEN);
					}
					if(profiloSicurezzaMessaggioDigestEncodingItemValue!=null) {
						profiloSicurezzaMessaggioDigestEncodingItemValue.setValue(null);
					}
				}
				
			}
			
					
			boolean x5uRichiesta = false;
			boolean x5cRichiesta = false;
			StringProperty profiloSicurezzaMessaggioRequestRifX509ItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_ID);
			if(profiloSicurezzaMessaggioRequestRifX509ItemValue!=null && profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue())) {
				List<String> list = ProtocolPropertiesUtils.getListFromMultiSelectValue(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue());
				if(list!=null && list.contains(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U)) {
					x5uRichiesta = true;
				}
				else {
					x5uRichiesta = false;
				}
				if(list!=null && list.contains(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C)) {
					x5cRichiesta = true;
				}
				else {
					x5cRichiesta = false;
				}
			}
			
			boolean x5uRisposta = false;
			boolean x5cRisposta = false;
			if(!request) {
				StringProperty profiloSicurezzaMessaggioRifX509AsRequestItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_ID);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioResponseX5UItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_ID);
				
				if(profiloSicurezzaMessaggioResponseX5UItem!=null) {
					if(profiloSicurezzaMessaggioRifX509AsRequestItemValue!=null && 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE.equals(profiloSicurezzaMessaggioRifX509AsRequestItemValue.getValue())){
						profiloSicurezzaMessaggioResponseX5UItem.setType(ConsoleItemType.MULTI_SELECT);
						((StringConsoleItem)profiloSicurezzaMessaggioResponseX5UItem).setRows(3);
						
						StringProperty profiloSicurezzaMessaggioResponseRifX509ItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_ID);
						if(profiloSicurezzaMessaggioResponseRifX509ItemValue!=null && profiloSicurezzaMessaggioResponseRifX509ItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioResponseRifX509ItemValue.getValue())) {
							List<String> list = ProtocolPropertiesUtils.getListFromMultiSelectValue(profiloSicurezzaMessaggioResponseRifX509ItemValue.getValue());
							if(list!=null && list.contains(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U)) {
								x5uRisposta = true;
							}
							else {
								x5uRisposta = false;
							}
							if(list!=null && list.contains(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C)) {
								x5cRisposta = true;
							}
							else {
								x5cRisposta = false;
							}
						}
					}
					else {
						profiloSicurezzaMessaggioResponseX5UItem.setType(ConsoleItemType.HIDDEN);
						
						x5uRisposta = x5uRichiesta;
						
						x5cRisposta = x5cRichiesta;
					}
				}
			}
			
			boolean x5c = false;
			if(request) {
				x5c = x5cRichiesta; 
			}
			else {
				x5c = x5cRisposta;
			}
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificateItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_ID:
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_ID
							);
			if(profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificateItem!=null) {
				if(x5c) {
					profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificateItem.setType(ConsoleItemType.CHECKBOX);
				}
				else {
					profiloSicurezzaMessaggioRifX509ItemX5CSingleCertificateItem.setType(ConsoleItemType.HIDDEN);
				}
			}
			
			
			if(request) {
				x5url = x5uRichiesta; 
			}
			else {
				x5url = x5uRisposta;
			}
			
			String idUrl = null;
			if(fruizione && request) {
				idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X5U_URL_ID;
			}
			else if(!fruizione && !request) {
				idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X5U_URL_ID;
			}
			if(idUrl!=null) {
				AbstractConsoleItem<?> profiloSicurezzaMessaggioX5UItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idUrl);
				if(profiloSicurezzaMessaggioX5UItem!=null) {
					if(x5url && (!fruizione || keystoreDefinitoInFruizione)) {
						profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.TEXT_AREA);
						profiloSicurezzaMessaggioX5UItem.setRequired(requiredValue);
					}
					else {
						profiloSicurezzaMessaggioX5UItem.setRequired(false);
						profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.HIDDEN);
					}
				}
				
				StringProperty profiloSicurezzaMessaggioX5UItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idUrl);
				if(profiloSicurezzaMessaggioX5UItemItemValue!=null &&
					(!x5url || (fruizione && !keystoreDefinitoInFruizione) ) 
					){
					profiloSicurezzaMessaggioX5UItemItemValue.setValue(null);
				}
			}
			
			
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHttpHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID
							);
			if(profiloSicurezzaMessaggioHttpHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioHttpHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID);
				if(profiloSicurezzaMessaggioHttpHeadersItemValue!=null &&
					(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue()==null || "".equals(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue())) 
					){
					try {
						profiloSicurezzaMessaggioHttpHeadersItemValue.setValue(modiProperties.getRestSecurityTokenSignedHeadersAsString());
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				
			}
			
			
			
			// Claims
			if(
					(request && fruizione)
					||
					(!request && !fruizione)
					) {
				
				String idProperty = ((request && fruizione) ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestJwtClaimsItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idProperty);
				if(profiloSicurezzaMessaggioRestJwtClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtClaimsItemValue.getValue()!=null && 
						profiloSicurezzaMessaggioRestJwtClaimsItem!=null) {
					String [] tmp = profiloSicurezzaMessaggioRestJwtClaimsItemValue.getValue().split("\n");
					if(tmp.length>2) {
						((StringConsoleItem)profiloSicurezzaMessaggioRestJwtClaimsItem).setRows((tmp.length>10) ? 10 : tmp.length);
					}
					else {
						((StringConsoleItem)profiloSicurezzaMessaggioRestJwtClaimsItem).setRows(2);
					}
				}
			}
			
			
			// Header Duplicati
			if( headerDuplicati && 
					( 
							(request && fruizione)
							||
							(!request && !fruizione)
					)
				) {
			
				String idPropertyJti = ((request && fruizione) ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_RISPOSTA_ID);
				String idPropertyJtiAsIdMessaggio = ((request && fruizione) ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RISPOSTA_ID);
				
				StringProperty profiloSicurezzaMessaggioRestJtiItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyJti);
				StringProperty profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyJtiAsIdMessaggio);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestJtiAsIdMessaggioItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyJtiAsIdMessaggio);
				if(profiloSicurezzaMessaggioRestJtiAsIdMessaggioItem!=null) {
					if(profiloSicurezzaMessaggioRestJtiItemValue!=null && ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DIFFERENT.equals(profiloSicurezzaMessaggioRestJtiItemValue.getValue())) {
						profiloSicurezzaMessaggioRestJtiAsIdMessaggioItem.setType(ConsoleItemType.SELECT);
						if(profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue!=null &&
							profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue.getValue()==null) {
							profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_DEFAULT);
						}
					}
					else {
						profiloSicurezzaMessaggioRestJtiAsIdMessaggioItem.setType(ConsoleItemType.HIDDEN);
						if(profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue!=null) {
							profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_DEFAULT);
						}
					}
				}
				
				
				String idPropertyClaimsAuthorization = ((request && fruizione) ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyClaimsAuthorization);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyClaimsAuthorization);
				if(profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItemValue!=null && profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItemValue.getValue()!=null && 
						profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItem!=null) {
					String [] tmp = profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItemValue.getValue().split("\n");
					if(tmp.length>2) {
						((StringConsoleItem)profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItem).setRows((tmp.length>10) ? 10 : tmp.length);
					}
					else {
						((StringConsoleItem)profiloSicurezzaMessaggioRestJwtClaimsAuthorizationItem).setRows(2);
					}
				}
				
				String idPropertyClaimsModi = ((request && fruizione) ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtClaimsModiItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyClaimsModi);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestJwtClaimsModiItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyClaimsModi);
				if(profiloSicurezzaMessaggioRestJwtClaimsModiItemValue!=null && profiloSicurezzaMessaggioRestJwtClaimsModiItemValue.getValue()!=null && 
						profiloSicurezzaMessaggioRestJwtClaimsModiItem!=null) {
					String [] tmp = profiloSicurezzaMessaggioRestJwtClaimsModiItemValue.getValue().split("\n");
					if(tmp.length>2) {
						((StringConsoleItem)profiloSicurezzaMessaggioRestJwtClaimsModiItem).setRows((tmp.length>10) ? 10 : tmp.length);
					}
					else {
						((StringConsoleItem)profiloSicurezzaMessaggioRestJwtClaimsModiItem).setRows(2);
					}
				}
			}
			
			if( headerDuplicati && request ) {
					
				String idPropertyAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RICHIESTA_ID;
				String idPropertyAudValue = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID;
				
				StringProperty profiloSicurezzaMessaggioRestAudItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAud);
				StringProperty profiloSicurezzaMessaggioRestAudValueItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAudValue);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudValueItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyAudValue);
				if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
					if(profiloSicurezzaMessaggioRestAudItemValue!=null && ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(profiloSicurezzaMessaggioRestAudItemValue.getValue())) {
						profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.TEXT_AREA);
						profiloSicurezzaMessaggioRestAudValueItem.setRequired(true);
					}
					else {
						if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
							profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
							profiloSicurezzaMessaggioRestAudValueItem.setRequired(false);
						}
						if(profiloSicurezzaMessaggioRestAudValueItemValue!=null) {
							profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
						}
					}
				}
				
			}
			
			if( headerDuplicati && !request && fruizione ) {
				
				String idPropertyAudVerifica = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_ID;				
				BooleanProperty profiloSicurezzaMessaggioRestAudVerificaItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAudVerifica);
				boolean verificaAbilitata = profiloSicurezzaMessaggioRestAudVerificaItemValue!=null && profiloSicurezzaMessaggioRestAudVerificaItemValue.getValue()!=null && profiloSicurezzaMessaggioRestAudVerificaItemValue.getValue(); 
				
				String idPropertyAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RISPOSTA_ID;
				String idPropertyAudValue = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RISPOSTA_ID;
				
				StringProperty profiloSicurezzaMessaggioRestAudItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAud);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), profiloSicurezzaMessaggioRestAudItemValue);
				if(profiloSicurezzaMessaggioRestAudItem!=null) {
					if(verificaAbilitata) {
						profiloSicurezzaMessaggioRestAudItem.setType(ConsoleItemType.SELECT);
					}
					else {
						profiloSicurezzaMessaggioRestAudItem.setType(ConsoleItemType.HIDDEN);
						profiloSicurezzaMessaggioRestAudItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT);
					}
				}
				
				StringProperty profiloSicurezzaMessaggioRestAudValueItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAudValue);
				AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudValueItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyAudValue);
				if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
					if(verificaAbilitata && profiloSicurezzaMessaggioRestAudItemValue!=null && ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(profiloSicurezzaMessaggioRestAudItemValue.getValue())) {
						profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.TEXT_AREA);
						profiloSicurezzaMessaggioRestAudValueItem.setRequired(true);
					}
					else {
						if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
							profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
							profiloSicurezzaMessaggioRestAudValueItem.setRequired(false);
						}
						if(profiloSicurezzaMessaggioRestAudValueItemValue!=null) {
							profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
						}
					}
				}
				
			}
			
		}
		else {
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificateItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_ID:
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_ID
							);
			
			if(profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificateItem!=null) {
				String rifX509Id = request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_ID :
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_ID;
				
				StringProperty profiloSicurezzaMessaggioRequestRifX509ItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, rifX509Id);
				if(profiloSicurezzaMessaggioRequestRifX509ItemValue!=null) {
					if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN.equals(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue())) {
						profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificateItem.setType(ConsoleItemType.CHECKBOX);
					}
					else {
						profiloSicurezzaMessaggioRifX509ItemBinarySecurityTokenSingleCertificateItem.setType(ConsoleItemType.HIDDEN);
					}
				}
			}
			
			
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioRifX509ItemIncludeSignatureTokenItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_ID:
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_ID
							);
			
			if(profiloSicurezzaMessaggioRifX509ItemIncludeSignatureTokenItem!=null) {
				String rifX509Id = request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_ID :
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_ID;
				
				StringProperty profiloSicurezzaMessaggioRequestRifX509ItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, rifX509Id);
				if(profiloSicurezzaMessaggioRequestRifX509ItemValue!=null) {
					if( ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE.equals(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue())
							||
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT.equals(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue())
							||
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI.equals(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue())
							) {
						profiloSicurezzaMessaggioRifX509ItemIncludeSignatureTokenItem.setType(ConsoleItemType.CHECKBOX);
					}
					else {
						profiloSicurezzaMessaggioRifX509ItemIncludeSignatureTokenItem.setType(ConsoleItemType.HIDDEN);
					}
				}
			}
			
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioSoapHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID
							);
			if(profiloSicurezzaMessaggioSoapHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioSoapHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID);
				if( 
						(profiloSicurezzaMessaggioSoapHeadersItemValue==null || profiloSicurezzaMessaggioSoapHeadersItemValue.getValue()==null || "".equals(profiloSicurezzaMessaggioSoapHeadersItemValue.getValue()))
						&&
						consoleHelper.isModalitaStandard() 
						&&
						profiloSicurezzaMessaggioSoapHeadersItem!=null
					) {
					profiloSicurezzaMessaggioSoapHeadersItem.setType(ConsoleItemType.HIDDEN);
				}
				
			}
		}
		
		// Cornice Sicurezza
		
		if(request && patternDatiCorniceSicurezza!=null && StringUtils.isNotEmpty(patternDatiCorniceSicurezza)) {
			
			if(fruizione) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)) {
					updateCorniceSicurezzaLegacy(consoleConfiguration, properties);
				}
				else {
					updateCorniceSicurezzaSchema(consoleConfiguration, properties,
							schemaDatiCorniceSicurezza);
				}
			}
			else {
				if(!ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)) {
					updateCorniceSicurezzaSchemaAudience(consoleConfiguration, properties);
				}
			}
			
		}
		
		// Sicurezza OAauth
		
		if(fruizione && request) {
			BaseConsoleItem subTitleItem = 	
						ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_ID);
			if(subTitleItem!=null) {
				if(keystoreDefinitoInFruizione) {
					subTitleItem.setType(ConsoleItemType.SUBTITLE);	
				}
				else {
					subTitleItem.setType(ConsoleItemType.HIDDEN);	
				}
			}
			
			AbstractConsoleItem<?> oauthIdentificativoItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID);
			if(oauthIdentificativoItem!=null) {
				if(keystoreDefinitoInFruizione) {
					oauthIdentificativoItem.setType(ConsoleItemType.TEXT_AREA);
				}
				else {
					oauthIdentificativoItem.setType(ConsoleItemType.HIDDEN);
					StringProperty oauthIdentificativoItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID);
					if(oauthIdentificativoItemValue!=null) {
						oauthIdentificativoItemValue.setValue(null);
					}
				}
			}
			
			AbstractConsoleItem<?> oauthKidItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ID);
			if(oauthKidItem!=null) {
				if(keystoreDefinitoInFruizione) {
					oauthKidItem.setType(ConsoleItemType.TEXT_AREA);
				}
				else {
					oauthKidItem.setType(ConsoleItemType.HIDDEN);
					StringProperty oauthKidItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ID);
					if(oauthKidItemValue!=null) {
						oauthKidItemValue.setValue(null);
					}
				}
			}
		}
		
		
		// TrustStore
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			
			boolean addTrustStoreTypesChiaviPubbliche = rest || kidMode; // jwk
			List<RemoteStoreConfig> remoteStoreConfig = null;
			if(kidMode) {
				remoteStoreConfig = modiProperties.getRemoteStoreConfig();
			}
			
			// truststore per i certificati
			ModIDynamicConfigurationKeystoreUtilities.updateTrustConfig(consoleConfiguration, properties, false, false, requiredValue,
					addTrustStoreTypesChiaviPubbliche, remoteStoreConfig);
			
			if(rest) {
			
				// ssl per le url (x5u)
				ModIDynamicConfigurationKeystoreUtilities.updateTrustConfig(consoleConfiguration, properties, true, x5url, requiredValue,
						false, null);
			
			}
		}
		
		// KeyStore
		
		if(!fruizione && !request) {
			
			boolean hideSceltaArchivioFilePath = false;
			boolean addHiddenSubjectIssuer = false;
			boolean ridefinisci = ModIDynamicConfigurationKeystoreUtilities.updateKeystoreConfig(consoleConfiguration, properties, true, 
					hideSceltaArchivioFilePath, addHiddenSubjectIssuer, 
					requiredValue, null,
					rest);
			
			updateSicurezzaOAuth(consoleConfiguration, properties, ridefinisci);
		}
		
		if(fruizione && request) {
			
			boolean hideSceltaArchivioFilePath = false;
			boolean addHiddenSubjectIssuer = false;
			ModIDynamicConfigurationKeystoreUtilities.updateKeystoreConfig(consoleConfiguration, properties, true, 
					hideSceltaArchivioFilePath, addHiddenSubjectIssuer, 
					requiredValue, null,
					rest);
		}
		
		
	}
	
	private static void updateSicurezzaOAuth(ConsoleConfiguration configuration, ProtocolProperties properties, boolean ridefinisci) throws ProtocolException {
		
		AbstractConsoleItem<?> profiloSicurezzaOauthKidItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(configuration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ID);
		if(profiloSicurezzaOauthKidItem!=null) {
			profiloSicurezzaOauthKidItem.setType(ridefinisci ? ConsoleItemType.TEXT_AREA : ConsoleItemType.HIDDEN);
		}
		StringProperty profiloSicurezzaOauthKidItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ID);
		if(profiloSicurezzaOauthKidItemValue!=null && !ridefinisci) {
			profiloSicurezzaOauthKidItemValue.setValue(null);
		}
		
		AbstractConsoleItem<?> profiloSicurezzaOauthIdentificativoItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(configuration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID);
		if(profiloSicurezzaOauthIdentificativoItem!=null) {
			profiloSicurezzaOauthIdentificativoItem.setType(ridefinisci ? ConsoleItemType.TEXT_AREA : ConsoleItemType.HIDDEN);
		}
		StringProperty profiloSicurezzaOauthIdentificativoItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID);
		if(profiloSicurezzaOauthIdentificativoItemValue!=null && !ridefinisci) {
			profiloSicurezzaOauthIdentificativoItemValue.setValue(null);
		}
		
	}
	
	private static void updateCorniceSicurezzaSchemaAudience(ConsoleConfiguration configuration, ProtocolProperties properties) throws ProtocolException {
		String idPropertyAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_RICHIESTA_ID;
		String idPropertyAudValue = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_AUDIT_CUSTOM_RICHIESTA_ID;
		
		StringProperty profiloSicurezzaMessaggioRestAudItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAud);
		StringProperty profiloSicurezzaMessaggioRestAudValueItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAudValue);
		AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudValueItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(configuration.getConsoleItem(), idPropertyAudValue);
		if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
			if(profiloSicurezzaMessaggioRestAudItemValue!=null && ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_DIFFERENT.equals(profiloSicurezzaMessaggioRestAudItemValue.getValue())) {
				profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.TEXT_AREA);
				profiloSicurezzaMessaggioRestAudValueItem.setRequired(true);
			}
			else {
				profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
				profiloSicurezzaMessaggioRestAudValueItem.setRequired(false);
				if(profiloSicurezzaMessaggioRestAudValueItemValue!=null) {
					profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
				}
			}
		}
	}
	
	private static void updateCorniceSicurezzaSchema(ConsoleConfiguration configuration, ProtocolProperties properties,
			String schemaDatiCorniceSicurezza) throws ProtocolException {
		
		List<ModIAuditConfig> list = ModIProperties.getInstance().getAuditConfig();
		if(list!=null && !list.isEmpty()) {
			
			updateCorniceSicurezzaSchemaAudience(configuration, properties);
			
			for (ModIAuditConfig modIAuditConfig : list) {
				if(modIAuditConfig.getNome().equals(schemaDatiCorniceSicurezza)) {
					updateCorniceSicurezzaSchema(configuration, properties,
							modIAuditConfig);
					break;
				}
			}
		}
		
	}
	private static void updateCorniceSicurezzaSchema(ConsoleConfiguration configuration, ProtocolProperties properties,
			ModIAuditConfig modIAuditConfig) throws ProtocolException {
		if(modIAuditConfig.getClaims()!=null && !modIAuditConfig.getClaims().isEmpty()) {
			for (ModIAuditClaimConfig claimConfig : modIAuditConfig.getClaims()) {
				updateCorniceSicurezzaSchema(configuration, properties, claimConfig);
			}
		}
	}
	
	private static void updateCorniceSicurezzaSchema(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, ModIAuditClaimConfig claimConfig) throws ProtocolException {
		
		String modeId = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_ID_PREFIX+claimConfig.getNome();
		
		boolean ridefinisciItem = false;
		StringProperty selectSchemaModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				modeId);
		if(selectSchemaModeItemValue==null) {
			ridefinisciItem = false;	
		}
		else {
			ridefinisciItem = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectSchemaModeItemValue.getValue());
		}
	
		AbstractConsoleItem<?> modeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), modeId);
		if(ridefinisciItem) {
			modeItem.setInfo(null);
		}
		else {
			modeItem.setInfo(new ConsoleItemInfo(claimConfig.getLabel()));
			modeItem.getInfo().setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_INFO_INTESTAZIONE.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_INFO_INTESTAZIONE_CLAIM, claimConfig.getNome()));
			modeItem.getInfo().setListBody(new ArrayList<>());
			modeItem.getInfo().getListBody().addAll(claimConfig.getRulesInfo());
		}
		
		String id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_ID_PREFIX+claimConfig.getNome();
		AbstractConsoleItem<?> valueItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), id);
		if(ridefinisciItem) {
			valueItem.setType(ConsoleItemType.TEXT_AREA);
		}
		else {
			valueItem.setType(ConsoleItemType.HIDDEN);
			StringProperty selectItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
			if(selectItemValue!=null) {
				selectItemValue.setValue(null);
			}
		}
		
	}
	
	private static void updateCorniceSicurezzaLegacy(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		updateCorniceSicurezzaLegacyCodiceEnte(consoleConfiguration, properties);
		
		updateCorniceSicurezzaLegacyUser(consoleConfiguration, properties);
		
		updateCorniceSicurezzaLegacyIpUser(consoleConfiguration, properties);
	}
	private static void updateCorniceSicurezzaLegacyCodiceEnte(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		boolean ridefinisciCodiceEnte = false;
		StringProperty selectCodiceEnteModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_ID);
		if(selectCodiceEnteModeItemValue==null) {
			ridefinisciCodiceEnte = false;	
		}
		else {
			ridefinisciCodiceEnte = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectCodiceEnteModeItemValue.getValue());
		}
	
		AbstractConsoleItem<?> codiceEnteModeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_ID);
		if(ridefinisciCodiceEnte) {
			codiceEnteModeItem.setInfo(null);
		}
		else {
			codiceEnteModeItem.setInfo(new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL));
			codiceEnteModeItem.getInfo().setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_DEFAULT_INFO_INTESTAZIONE);
		}
		
		AbstractConsoleItem<?> codiceEnteItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_ID);
		if(ridefinisciCodiceEnte) {
			codiceEnteItem.setType(ConsoleItemType.TEXT_AREA);
		}
		else {
			codiceEnteItem.setType(ConsoleItemType.HIDDEN);
			StringProperty selectCodiceEnteItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_ID);
			if(selectCodiceEnteItemValue!=null) {
				selectCodiceEnteItemValue.setValue(null);
			}
		}
	}
	private static void updateCorniceSicurezzaLegacyUser(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		boolean ridefinisciUser = false;
		StringProperty selectUserModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_ID);
		if(selectUserModeItemValue==null) {
			ridefinisciUser = false;	
		}
		else {
			ridefinisciUser = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectUserModeItemValue.getValue());
		}
	
		AbstractConsoleItem<?> userModeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_ID);
		if(ridefinisciUser) {
			userModeItem.setInfo(null);
		}
		else {
			userModeItem.setInfo(new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL));
			userModeItem.getInfo().setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_INFO_INTESTAZIONE);
			userModeItem.getInfo().setListBody(new ArrayList<>());
			userModeItem.getInfo().getListBody().add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_INFO_HTTP);
			userModeItem.getInfo().getListBody().add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_INFO_URL);
		}
		
		AbstractConsoleItem<?> userItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_ID);
		if(ridefinisciUser) {
			userItem.setType(ConsoleItemType.TEXT_AREA);
		}
		else {
			userItem.setType(ConsoleItemType.HIDDEN);
			StringProperty selectUserItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_ID);
			if(selectUserItemValue!=null) {
				selectUserItemValue.setValue(null);
			}
		}
	}
	private static void updateCorniceSicurezzaLegacyIpUser(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		boolean ridefinisciIpUser = false;
		StringProperty selectIpUserModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_ID);
		if(selectIpUserModeItemValue==null) {
			ridefinisciIpUser = false;	
		}
		else {
			ridefinisciIpUser = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectIpUserModeItemValue.getValue());
		}
		
		AbstractConsoleItem<?> ipUserModeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_ID);
		if(ridefinisciIpUser) {
			ipUserModeItem.setInfo(null);
		}
		else {
			ipUserModeItem.setInfo(new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL));
			ipUserModeItem.getInfo().setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_INFO_INTESTAZIONE);
			ipUserModeItem.getInfo().setListBody(new ArrayList<>());
			ipUserModeItem.getInfo().getListBody().add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_INFO_HTTP);
			ipUserModeItem.getInfo().getListBody().add(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_INFO_URL);
		}
		
		AbstractConsoleItem<?> ipUserItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_ID);
		if(ridefinisciIpUser) {
			ipUserItem.setType(ConsoleItemType.TEXT_AREA);
		}
		else {
			ipUserItem.setType(ConsoleItemType.HIDDEN);
			StringProperty selectIPUserItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_ID);
			if(selectIPUserItemValue!=null) {
				selectIPUserItemValue.setValue(null);
			}
		}
	}
	
	
	static void addPdndInfo(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean rest) throws ProtocolException {
		
		
		TitleConsoleItem titolo = (TitleConsoleItem) ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_LABEL);
		configuration.addConsoleItem(titolo );
		
		StringConsoleItem modiEServiceIdItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_ID,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_LABEL
					);
		modiEServiceIdItem.setRows(1);
		configuration.addConsoleItem(modiEServiceIdItem);
		
		StringConsoleItem modiDescriptorIdItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_ID,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_LABEL
					);
		modiDescriptorIdItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_NOTE);
		modiDescriptorIdItem.setRows(1);
		configuration.addConsoleItem(modiDescriptorIdItem);
		
		if(modiProperties.isSignalHubEnabled()) {
		
			BooleanConsoleItem modiSignalHubItem = (BooleanConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
					ConsoleItemType.CHECKBOX,
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_LABEL);
			modiSignalHubItem.setReloadOnChange(true, true);
			configuration.addConsoleItem(modiSignalHubItem);
		
			addSignalHubInfo(modiProperties,
					configuration, rest);
		}
		
	}
	
	private static void addSignalHubInfo(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean rest) throws ProtocolException {
		
		// SignalHub sub-section
		
		SubtitleConsoleItem modiSignalHubSubtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SUBTITLE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SUBTITLE_LABEL
				); 
		modiSignalHubSubtitleItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubSubtitleItem);
		
		// pseudonymization (checkbox aggiunta solo se abilitata dalle properties)
		if(modiProperties.isSignalHubPseudonymizationChoiceEnabled()) {
			BooleanConsoleItem modiSignalHubPseudonymizationItem = (BooleanConsoleItem)
					ProtocolPropertiesFactory.newConsoleItem(
					ConsoleItemValueType.BOOLEAN,
					ConsoleItemType.CHECKBOX,
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PSEUDONYMIZATION_ID,
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PSEUDONYMIZATION_LABEL);
			modiSignalHubPseudonymizationItem.setDefaultValue(true);
			modiSignalHubPseudonymizationItem.setReloadOnChange(true, true);
			modiSignalHubPseudonymizationItem.setType(ConsoleItemType.HIDDEN);
			configuration.addConsoleItem(modiSignalHubPseudonymizationItem);
		}
		
		// operation
		StringConsoleItem modiSignalHubOpItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ID, 
				(rest ? ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_REST_LABEL : ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_SOAP_LABEL ));
		modiSignalHubOpItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_NOTE);
		modiSignalHubOpItem.setRequired(true);
		modiSignalHubOpItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubOpItem);

		// alg
		StringConsoleItem modiSignalHubAlgItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_LABEL);
		List<String> algo = modiProperties.getSignalHubAlgorithms();
		if(algo!=null && !algo.isEmpty()) {
			for (String a : algo) {
				modiSignalHubAlgItem.addLabelValue(a,a);
			}
		}
		modiSignalHubAlgItem.setDefaultValue(modiProperties.getSignalHubDefaultAlgorithm());
		modiSignalHubAlgItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubAlgItem);
		
		// seed size
		NumberConsoleItem modiSignalHubAlgDimensioneSeme = (NumberConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_LABEL);
		List<Integer> size = modiProperties.getSignalHubSeedSize();
		if(size!=null && !size.isEmpty()) {
			for (Integer a : size) {
				modiSignalHubAlgDimensioneSeme.addLabelValue(a.toString(),a.longValue());
			}
		}
		modiSignalHubAlgDimensioneSeme.setDefaultValue(modiProperties.getSignalHubDefaultSeedSize().longValue());
		modiSignalHubAlgDimensioneSeme.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubAlgDimensioneSeme);
		
		// seed lifetime
		NumberConsoleItem modiSignalHubAlgScadenzaSeme = (NumberConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
				ConsoleItemType.TEXT_EDIT,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_LABEL);
		if(!modiProperties.isSignalHubSeedLifetimeUnlimited()) {
			modiSignalHubAlgScadenzaSeme.setRequired(true);
			modiSignalHubAlgScadenzaSeme.setDefaultValue(modiProperties.getSignalHubDeSeedSeedLifetimeDaysDefault().longValue());
		}
		modiSignalHubAlgScadenzaSeme.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubAlgScadenzaSeme);

		
		// publisher
		StringConsoleItem modiSignalHubPublisherItem = (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT,
						ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_TEXT_ID, 
						ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_TEXT_LABEL);
		modiSignalHubPublisherItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_TEXT);
		configuration.addConsoleItem(modiSignalHubPublisherItem);
		
		// publisher applicative
		StringConsoleItem modiSignalHubPublisherSAItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_LABEL);
		modiSignalHubPublisherSAItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubPublisherSAItem);
		
		// publisher role
		StringConsoleItem modiSignalHubPublisherRoleItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_LABEL);
		modiSignalHubPublisherRoleItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_NOTE);
		modiSignalHubPublisherRoleItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(modiSignalHubPublisherRoleItem);
	}
	
	static void updatePdndInfo(ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties,
			AccordoServizioParteComune api, String portType, IDServizio idServizio,
			IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader,
			Logger log) throws ProtocolException {
		
		ModIProperties modiProperties = ModIProperties.getInstance();
		
		boolean signalHub = false;
		if(modiProperties.isSignalHubEnabled()) {
			BooleanProperty modiSignalHubItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ID);
			signalHub = modiSignalHubItemValue!=null && modiSignalHubItemValue.getValue()!=null && modiSignalHubItemValue.getValue().booleanValue();
		}
		
		
		// eService id
		AbstractConsoleItem<?> modiEServiceIdItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_ID);
		if(modiEServiceIdItem!=null) {
			modiEServiceIdItem.setRequired(signalHub);
		}
	
		
		// Signal hub
		if(modiProperties.isSignalHubEnabled()) {
			
			updateSignalHubInfo(consoleConfiguration, consoleHelper, properties,
					api, portType, idServizio,
					log, registryReader,
					signalHub);
			
			updateSignalHubPublisherInfo(consoleConfiguration, properties,
					idServizio,
					registryReader,
					configIntegrationReader,
					log,
					signalHub);
		}
	}
	
	private static void updateSignalHubInfo(ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties,
			AccordoServizioParteComune api, String portType, IDServizio idServizio,
			Logger log, IRegistryReader registryReader,
			boolean signalHub) throws ProtocolException {

		ModIProperties modiProperties = ModIProperties.getInstance();

		// Recupero valore checkbox pseudonymization
		boolean pseudonymization = true;
		if(signalHub) {
			if(!modiProperties.isSignalHubPseudonymizationChoiceEnabled()) {
				// Se la scelta non è abilitata (default), la pseudoanonimizzazione è sempre attiva
				pseudonymization = true;
			} else {
				// Se la scelta è abilitata, leggo il valore dalla checkbox
				BooleanProperty modiSignalHubPseudonymizationItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PSEUDONYMIZATION_ID);
				if(modiSignalHubPseudonymizationItemValue!=null) {
					if(modiSignalHubPseudonymizationItemValue.getValue()==null) {
						pseudonymization = false;
						// retrocompatibilità
						try {
							String editMode = consoleHelper.getParameter(CostantiDB.DATA_ELEMENT_EDIT_MODE_NAME);
							if(consoleHelper.getPostBackElementName()==null && !CostantiDB.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END.equals(editMode)) {
								AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
								if(asps!=null && asps.sizeProtocolPropertyList()>0) {
									String v = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(asps.getProtocolPropertyList(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID);
									if(v!=null && StringUtils.isNotEmpty(v)) {
										pseudonymization = true;
									}
								}
							}
						}catch(Exception e) {
							log.error("Lettura parametro signalHub pseudonymization fallita: "+e.getMessage(),e);
						}
					}
					else {
						pseudonymization = modiSignalHubPseudonymizationItemValue.getValue().booleanValue();
					}
				}
				else {
					pseudonymization = false;
				}
			}
		}

		// Calcolo del parametro enabled: i campi algoritmo/seed sono visibili solo se signalHub E pseudonymization sono entrambi attivi
		boolean enabled = signalHub && pseudonymization;

		// signalHub subsection
		BaseConsoleItem modiSignalHubSubtitleItem =
				ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SUBTITLE_ID);
		if(modiSignalHubSubtitleItem!=null) {
			if(signalHub) {
				modiSignalHubSubtitleItem.setType(ConsoleItemType.SUBTITLE);
			}
			else {
				modiSignalHubSubtitleItem.setType(ConsoleItemType.HIDDEN);
			}
		}

		
		// signalHub pseudonymization
		updatePdndInfoSignalHubPseudonymization(consoleConfiguration, properties,
				signalHub && modiProperties.isSignalHubPseudonymizationChoiceEnabled(),
				pseudonymization);
		
		// signalHub operation
		updatePdndInfoSignalHubOperation(api, portType, idServizio,
				consoleConfiguration, properties,
				log, enabled);

		// signalHub algorithm
		updatePdndInfoSignalHubAlgo(modiProperties,
				consoleConfiguration, properties,
				enabled);

		// seed
		updatePdndInfoSignalHubSeed(modiProperties,
				consoleConfiguration, properties,
				enabled);
		
	}
	private static void updateSignalHubPublisherInfo(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IDServizio idServizio,
			IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader,
			Logger log,
			boolean signalHub) throws ProtocolException {
		
		// publisher
		updatePdndInfoSignalHubPublisher(consoleConfiguration, properties,
				idServizio,
				registryReader,
				configIntegrationReader,
				log,
				signalHub);
		
	}
	private static void updatePdndInfoSignalHubOperation(AccordoServizioParteComune api, String portType, IDServizio idServizio,
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			Logger log, boolean signalHub) throws ProtocolException {
		AbstractConsoleItem<?> modiSignalHubOpItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ID);
		if(modiSignalHubOpItem!=null) {
			modiSignalHubOpItem.setRequired(signalHub);
			if(signalHub) {
				modiSignalHubOpItem.setType(ConsoleItemType.SELECT);
				addSignalHubOperations(api, idServizio, portType, modiSignalHubOpItem, log);
			}
			else {
				modiSignalHubOpItem.setType(ConsoleItemType.HIDDEN);
				modiSignalHubOpItem.clearMapLabelValues();
			}
		}
		StringProperty modiSignalHubOpItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ID);
		if(modiSignalHubOpItemValue!=null && modiSignalHubOpItemValue.getValue()!=null && !signalHub) {
			modiSignalHubOpItemValue.setValue(null);
		}
	}
	private static void updatePdndInfoSignalHubPseudonymization(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean enabled, boolean pseudonymization) throws ProtocolException {

		// La checkbox esiste solo se la scelta è abilitata dalle properties
		AbstractConsoleItem<?> modiSignalHubPseudonymizationItem =
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PSEUDONYMIZATION_ID);
		if(modiSignalHubPseudonymizationItem!=null) {
			if(enabled) {
				modiSignalHubPseudonymizationItem.setType(ConsoleItemType.CHECKBOX);
			}
			else {
				modiSignalHubPseudonymizationItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		BooleanProperty modiSignalHubPseudonymizationItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PSEUDONYMIZATION_ID);
		// Se signalHub è abilitato e il valore è null, imposta il default a true
		if(modiSignalHubPseudonymizationItemValue != null
				&& enabled
				&& modiSignalHubPseudonymizationItemValue.getValue() == null) {
			modiSignalHubPseudonymizationItemValue.setValue(pseudonymization);
		}
	}
	private static void updatePdndInfoSignalHubAlgo(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean enabled) throws ProtocolException {
		AbstractConsoleItem<?> modiSignalHubAlgItem =
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID);
		if(modiSignalHubAlgItem!=null) {
			if(enabled) {
				modiSignalHubAlgItem.setType(ConsoleItemType.SELECT);
			}
			else {
				modiSignalHubAlgItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		StringProperty modiSignalHubAlgItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID);
		if(modiSignalHubAlgItemValue!=null) {
			if(enabled) {
				if(modiSignalHubAlgItemValue.getValue()==null || StringUtils.isEmpty(modiSignalHubAlgItemValue.getValue())) {
					modiSignalHubAlgItemValue.setValue(modiProperties.getSignalHubDefaultAlgorithm()); // default
				}
			}
			else {
				if(modiSignalHubAlgItemValue.getValue()!=null) {
					modiSignalHubAlgItemValue.setValue(null);
				}
			}
		}
	}
	private static void updatePdndInfoSignalHubSeed(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean enabled) throws ProtocolException {

		// signalHub seed size
		updatePdndInfoSignalHubSeedSize(modiProperties,
				consoleConfiguration, properties,
				enabled);

		// signalHub seed lifetime
		updatePdndInfoSignalHubSeedLifeTime(consoleConfiguration, properties,
				enabled);

	}
	private static void updatePdndInfoSignalHubSeedSize(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean enabled) throws ProtocolException {
		AbstractConsoleItem<?> modiSignalHubSeedSizeItem =
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ID);
		if(modiSignalHubSeedSizeItem!=null) {
			if(enabled) {
				modiSignalHubSeedSizeItem.setType(ConsoleItemType.SELECT);
			}
			else {
				modiSignalHubSeedSizeItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		NumberProperty modiSignalHubSeedSizeItemValue = (NumberProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ID);
		if(modiSignalHubSeedSizeItemValue!=null) {
			if(enabled) {
				if(modiSignalHubSeedSizeItemValue.getValue()==null) {
					modiSignalHubSeedSizeItemValue.setValue(modiProperties.getSignalHubDefaultSeedSize().longValue()); // default
				}
			}
			else {
				if(modiSignalHubSeedSizeItemValue.getValue()!=null) {
					modiSignalHubSeedSizeItemValue.setValue(null);
				}
			}
		}
	}
	private static void updatePdndInfoSignalHubSeedLifeTime(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean enabled) throws ProtocolException {
		AbstractConsoleItem<?> modiSignalHubSeedLifeTimeItem =
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_ID);
		if(modiSignalHubSeedLifeTimeItem!=null) {
			modiSignalHubSeedLifeTimeItem.setRequired(enabled);
			if(enabled) {
				modiSignalHubSeedLifeTimeItem.setType(ConsoleItemType.TEXT_EDIT);
			}
			else {
				modiSignalHubSeedLifeTimeItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		NumberProperty modiSignalHubSeedLifeTimeItemValue = (NumberProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_ID);
		if(modiSignalHubSeedLifeTimeItemValue!=null &&
			!enabled &&
			modiSignalHubSeedLifeTimeItemValue.getValue()!=null) {
			modiSignalHubSeedLifeTimeItemValue.setValue(null);
		}
	}
	private static void updatePdndInfoSignalHubPublisher(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IDServizio idServizio,
			IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader,
			Logger log,
			boolean signalHub) throws ProtocolException {
		
		AbstractConsoleItem<?> modiSignalHubPublisherItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_TEXT_ID);
		if(modiSignalHubPublisherItem!=null) {
			if(signalHub) {
				modiSignalHubPublisherItem.setType(ConsoleItemType.TEXT);
			}
			else {
				modiSignalHubPublisherItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		StringProperty modiSignalHubPublisherItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_TEXT_ID);
		if(modiSignalHubPublisherItemValue!=null) {
			if(signalHub) {
				modiSignalHubPublisherItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_TEXT);
			}
			else {
				modiSignalHubPublisherItemValue.setValue(null);
			}
		}
		
		updatePdndInfoSignalHubPublisherSA(consoleConfiguration, properties,
				idServizio,
				registryReader,
				configIntegrationReader,
				log,
				signalHub);
		
		updatePdndInfoSignalHubPublisherRole(consoleConfiguration, properties,
				registryReader,
				log,
				signalHub);
	}
	private static void updatePdndInfoSignalHubPublisherSA(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IDServizio idServizio,
			IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader,
			Logger log,
			boolean signalHub) throws ProtocolException {
		AbstractConsoleItem<?> modiSignalHubPublisherSAItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
		if(modiSignalHubPublisherSAItem!=null) {
			if(signalHub) {
				modiSignalHubPublisherSAItem.setType(ConsoleItemType.SELECT);
				addSignalHubServiziApplicativi(registryReader,
						configIntegrationReader,
						modiSignalHubPublisherSAItem, log,
						idServizio);
			}
			else {
				modiSignalHubPublisherSAItem.setType(ConsoleItemType.HIDDEN);
				modiSignalHubPublisherSAItem.clearMapLabelValues();
			}
		}
		StringProperty modiSignalHubPublisherSAItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
		if(modiSignalHubPublisherSAItemValue!=null && 
			!signalHub) {
			modiSignalHubPublisherSAItemValue.setValue(null);
		}
	}
	private static void updatePdndInfoSignalHubPublisherRole(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IRegistryReader registryReader,
			Logger log,
			boolean signalHub) throws ProtocolException {
		AbstractConsoleItem<?> modiSignalHubPublisherRoleItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID);
		if(modiSignalHubPublisherRoleItem!=null) {
			if(signalHub) {
				modiSignalHubPublisherRoleItem.setType(ConsoleItemType.SELECT);
				addSignalHubRuoli(registryReader,
						modiSignalHubPublisherRoleItem, log);
			}
			else {
				modiSignalHubPublisherRoleItem.setType(ConsoleItemType.HIDDEN);
				modiSignalHubPublisherRoleItem.clearMapLabelValues();
			}
		}
		StringProperty modiSignalHubPublisherRoleItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID);
		if(modiSignalHubPublisherRoleItemValue!=null && 
			!signalHub) {
			modiSignalHubPublisherRoleItemValue.setValue(null);
		}
	}
	
	private static void addSignalHubOperations(AccordoServizioParteComune api, IDServizio idServizio, String portType, AbstractConsoleItem<?> modiSignalHubOpItem, Logger log) throws ProtocolException {
		Map<String,String> azioni = getSignalHubOperations(api, idServizio, portType, log);
		if(azioni!=null && !azioni.isEmpty()) {
			for (Map.Entry<String,String> entry : azioni.entrySet()) {
				String azioneId = entry.getKey();
				String azioneLabel = entry.getValue();
				((StringConsoleItem)modiSignalHubOpItem).addLabelValue(azioneLabel,azioneId);
			}
		}	
	}
	
	private static Map<String, String> getSignalHubOperations(AccordoServizioParteComune api, IDServizio idServizio, String portType, Logger log) throws ProtocolException {
		
		AccordoServizioParteSpecifica aspsRicercaAzioni = new AccordoServizioParteSpecifica();
		aspsRicercaAzioni.setTipo(idServizio.getTipo());
		aspsRicercaAzioni.setNome(idServizio.getNome());
		aspsRicercaAzioni.setVersione(idServizio.getVersione());
		if(idServizio.getSoggettoErogatore()!=null) {
			aspsRicercaAzioni.setTipoSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo());
			aspsRicercaAzioni.setNomeSoggettoErogatore(idServizio.getSoggettoErogatore().getNome());
		}
		aspsRicercaAzioni.setPortType(portType);
		
		AccordoServizioParteComuneSintetico aspcSelected = new AccordoServizioParteComuneSintetico(api);

		
		List<String> filtraAzioniUtilizzate = new ArrayList<>();
		Map<String,String> azioni = null;
		try {
			azioni = AzioniUtils.getAzioniConLabel(aspsRicercaAzioni, aspcSelected, true, false, 
					filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, log); 
		}catch(Exception e) {
			throw new ProtocolException("Lettura azioni '"+idServizio+"' non riuscita: "+e.getMessage(),e);
		}
		return azioni;
	}
	
	private static IDPortaDelegata readPDSignalHub(IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader,
			Logger log,
			IDServizio idServizio, IDAccordo idAccordoSignalHubPushAPI) {
		ProtocolFiltroRicercaFruizioniServizio filtro = new ProtocolFiltroRicercaFruizioniServizio();
		filtro.setIdAccordoServizioParteComune(idAccordoSignalHubPushAPI);
		filtro.setSoggettoFruitore(idServizio.getSoggettoErogatore());
		try {
			List<IDFruizione> list = registryReader.findIdFruizioni(filtro);
			if(list!=null && !list.isEmpty()) {
				return readPDSignalHub(configIntegrationReader,list);
			}
		}catch(Exception e) {
			log.error("Lettura id servizio signalhub api push fallita: "+e.getMessage(),e);
		}
		return null;
	}
	private static IDPortaDelegata readPDSignalHub(IConfigIntegrationReader configIntegrationReader,List<IDFruizione> list) throws RegistryException {
		// prendo la prima
		IDFruizione idF = list.get(0);
		if(idF!=null) {
			List<MappingFruizionePortaDelegata> listMF = configIntegrationReader.getMappingFruizionePortaDelegataList(idF.getIdFruitore(), idF.getIdServizio());
			if(listMF!=null && !listMF.isEmpty()) {
				for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : listMF) {
					if(mappingFruizionePortaDelegata.isDefault()) {
						return mappingFruizionePortaDelegata.getIdPortaDelegata();
					}
				}
			}
		}
		return null;
	}
	private static void addSignalHubServiziApplicativi(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			AbstractConsoleItem<?> modiSignalHubPublisherSAItem, Logger log,
			IDServizio idServizio) {
		
		((StringConsoleItem)modiSignalHubPublisherSAItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);

		if(idServizio!=null) {
			try {
				List<IDServizioApplicativo> idSA = getSignalHubServiziApplicativi(registryReader, configIntegrationReader, idServizio, log);
				if(idSA!=null && !idSA.isEmpty()) {
					List<String> nomi = new ArrayList<>();
					for (IDServizioApplicativo idServizioApplicativo : idSA) {
						nomi.add(idServizioApplicativo.getNome());
					}
					Collections.sort(nomi);
					for (String s : nomi) {
						((StringConsoleItem)modiSignalHubPublisherSAItem).addLabelValue(s,s);
					}
				}
			}catch(Exception e) {
				log.error("Lettura porta delegata per signalhub api push fallita: "+e.getMessage(),e);
			}
		}
	}
	
	private static List<IDServizioApplicativo> getSignalHubServiziApplicativi(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio idServizio, Logger log) throws RegistryNotFound, RegistryException, DriverRegistroServiziException, ProtocolException {
		IDAccordo idAccordoSignalHubPushAPI = ModIDynamicConfigurationAccordiParteComuneUtilities.getIdAccordoSignalHubPush(registryReader, ModIProperties.getInstance());
		IDPortaDelegata idPD = readPDSignalHub(registryReader,
				configIntegrationReader,
				log,
				idServizio, idAccordoSignalHubPushAPI);
		PortaDelegata pd = configIntegrationReader.getPortaDelegata(idPD);
		return configIntegrationReader.findIdServiziApplicativiByPdAuth(pd, true, true);
	}
	
	private static void addSignalHubRuoli(IRegistryReader registryReader,
			AbstractConsoleItem<?> modiSignalHubPublisherRoleItem, Logger log) {
			
		((StringConsoleItem)modiSignalHubPublisherRoleItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		
		List<IDRuolo>  l = getSignalHubRuoli(registryReader, log);
			
		if(l!=null && !l.isEmpty()) {
			for (IDRuolo idRuolo : l) {
				((StringConsoleItem)modiSignalHubPublisherRoleItem).addLabelValue(idRuolo.getNome(),idRuolo.getNome());
			}
		}

	}
	
	private static List<IDRuolo> getSignalHubRuoli(IRegistryReader registryReader, Logger log) {
		List<IDRuolo>  l = List.of();
		try {
			ProtocolFiltroRicercaRuoli filtro = new ProtocolFiltroRicercaRuoli();
			filtro.setContesto(RuoloContesto.PORTA_DELEGATA);
			filtro.setTipologia(RuoloTipologia.INTERNO);
			l = registryReader.findIdRuoli(filtro);
		}catch(RegistryNotFound e) {
			log.debug("Ruoli per signalhub api push non presenti"+e.getMessage(),e);
		}catch(Exception e) {
			log.error("Lettura id accordo signalhub api push fallita: "+e.getMessage(),e);
		}
		return l;
	}
	
	
	public static void validatePdndInfo(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, AccordoServizioParteComune api, IDServizio idServizio, String portType, 
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, Logger log) throws ProtocolException {
		
		ModIProperties modiProperties = ModIProperties.getInstance();

		boolean signalHub = false;
		if(modiProperties.isSignalHubEnabled()) {
			BooleanProperty modiSignalHubItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ID);
			signalHub = modiSignalHubItemValue!=null && modiSignalHubItemValue.getValue()!=null && modiSignalHubItemValue.getValue().booleanValue();
		}
		
		// eServiceId
		
		String modiEServiceIdItemValue = validatePdndInfoId(consoleConfiguration, properties,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_ID, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_LABEL);
		
		// descriptorId
		
		String modiDescriptorIdItemValue = validatePdndInfoId(consoleConfiguration, properties,
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_ID, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_LABEL);
		
		validatePdndInfoIdExists(registryReader, idServizio, modiEServiceIdItemValue, modiDescriptorIdItemValue,
				ModIProperties.getInstance().isPdndEServiceIdCheckUnique(),
				ModIProperties.getInstance().isPdndDescriptorIdCheckUnique(),
				signalHub);
		
		// signalHub
		if(signalHub) {
			if (modiEServiceIdItemValue == null)
				throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ID_ESERVICE_UNDEFINED);
			validatePdndInfoSignalHub(registryReader, configIntegrationReader, api, idServizio, portType, consoleConfiguration, properties, log);
		}
	}
	private static String validatePdndInfoId(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			String id, String label) throws ProtocolException {
		AbstractConsoleItem<?> modiEServiceIdItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
						id
						);
		StringProperty modiEServiceIdItemValue = null;
		if(modiEServiceIdItem!=null) {
			
			modiEServiceIdItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
			String idValue = null;
			if(modiEServiceIdItemValue!=null && modiEServiceIdItemValue.getValue()!=null && !"".equals(modiEServiceIdItemValue.getValue())) {
				try {
					InputValidationUtils.validateTextAreaInput(modiEServiceIdItemValue.getValue(), label);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				idValue = modiEServiceIdItemValue.getValue();
			}
			
			return idValue;
		}
		return null;
	}
	private static void validatePdndInfoIdExists(IRegistryReader registryReader, IDServizio idServizio,
			String eServiceId, String descriptorIds, boolean checkUniqueEServiceId, boolean checkUniqueDescriptorId, boolean signalHub) throws ProtocolException {
		
		if (eServiceId == null)
			return;
		
		/**
		 * Per impostazione predefinita è possibile registrare più erogazioni con lo stesso eServiceId (checkUniqueEServiceId=false), a condizione che siano associati descriptorId differenti.
		 * Qualora si desideri consentire la registrazione di più erogazioni con lo stesso eServiceId e lo stesso descriptorId, è necessario che checkUniqueDescriptorId sia false.
		 * NOTA: In caso di disabilitazione del controllo 'checkUniqueDescriptorId', 
		 * la registrazione di più erogazioni con identico eServiceId e descriptorId sarà consentita esclusivamente se il signalHub non risulta abilitato sull'erogazione.
		 */
		
		ProtocolFiltroRicercaServizi filtro = new ProtocolFiltroRicercaServizi();
		filtro.setProtocolPropertiesServizi(new ProtocolProperties());
		filtro.getProtocolPropertiesServizi().addProperty(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_ID, eServiceId, null);
		List<IDServizio> idServices = validatePdndInfoIdExists(registryReader, idServizio, filtro);
		
		// se presente un solo serviceId configurazione corretta
		if (idServices.isEmpty())
			return;
		IDServizio sameId = idServices.get(0);
		
		// se presente un erogazione con stesso serviceId e configurazione abilita il controllo di unicità errore
		if (checkUniqueEServiceId) {
			String msg = String.format("L'erogazione '%s' v%d' erogata da '%s' risulta già registrata con il campo '%s' valorizzato con l'identificativo fornito", 
					sameId.getNome(),
					sameId.getVersione(),
					sameId.getSoggettoErogatore().getNome(),
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_LABEL);
			throw new ProtocolException(msg);
		}
	

		if(!signalHub && !checkUniqueDescriptorId) {
			return;
		}
		
		Set<String> descriptorSet = descriptorIds == null ? Set.of() : new HashSet<>(ModISecurityConfig.convertToList(descriptorIds));
		List<ProtocolProperty> properties;
		
		for (IDServizio idService : idServices) {
			try {
				AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idService);
				properties = asps.getProtocolPropertyList();
			} catch (Exception e) {
				throw new ProtocolException("Errore nella lettura delle protocol properties", e);
			}
			 
			String foundDescriptors = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(
				     properties,
				     ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_ID
			);
			 
			// se esiste un erogazione con stesso serviceId ma descriptorId non valorizzato -> errore
			if (foundDescriptors == null) {
				 String msg = String.format("Impossibile aggiungere l'erogazione. L'erogazione '%s v%d' erogata da '%s' ha lo stesso valore nel campo '%s' ma il campo '%s' risulta non valorizzato",
						 idService.getNome(),
						 idService.getVersione(),
						 idService.getSoggettoErogatore().getNome(),
						 ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_LABEL,
						 ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_LABEL);
				throw new ProtocolException(msg);
			}
			
			Optional<String> conflictId = ModISecurityConfig.convertToList(foundDescriptors)
				.stream()
				.filter(descriptorSet::contains)
				.findAny();
			
			// se esiste un erogazione con stesso serviceId e che contiene almeno uno dei descriptorId inseriti -> errore
			if (conflictId.isPresent()) {
				String msg = String.format("L'erogazione '%s' v%d' erogata da '%s' risulta già registrata con il campo '%s' valorizzato con l'identificativo fornito '%s'", 
						idService.getNome(),
						idService.getVersione(),
						idService.getSoggettoErogatore().getNome(),
						ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_LABEL,
						conflictId.get());
				throw new ProtocolException(msg);
			}
		}
		
		// se non presente descriptor id -> errore
		if (descriptorIds == null) {
			String msg = String.format("L'erogazione '%s' v%d' erogata da '%s' risulta già registrata con il campo '%s' valorizzato con l'identificativo fornito, necessario valorizzare il campo '%s'", 
					sameId.getNome(),
					sameId.getVersione(),
					sameId.getSoggettoErogatore().getNome(),
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID_LABEL,
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID_LABEL);
			throw new ProtocolException(msg);
		}
	}
	private static List<IDServizio> validatePdndInfoIdExists(IRegistryReader registryReader, IDServizio idServizio, 
			ProtocolFiltroRicercaServizi filter) throws ProtocolException {
		List<IDServizio> list = null;
		try {
			list = registryReader.findIdAccordiServizioParteSpecifica(filter);
			if (list != null) {
				return list.stream()
						.filter(found -> !found.equals(idServizio, false))
						.collect(Collectors.toList());
			}
		}catch(RegistryNotFound notFound) {
			// ignore
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		return List.of();
	}
	private static void validatePdndInfoSignalHub(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, AccordoServizioParteComune api, IDServizio idServizio, String portType, 
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, Logger log) throws ProtocolException {

		ModIProperties modiProperties = ModIProperties.getInstance();

		// Determina se la pseudonymization è attiva
		boolean pseudonymization = false;
		if(!modiProperties.isSignalHubPseudonymizationChoiceEnabled()) {
			// Se la scelta non è abilitata (default), la pseudoanonimizzazione è sempre attiva
			pseudonymization = true;
		} else {
			// Se la scelta è abilitata, leggo il valore dalla checkbox
			BooleanProperty modiSignalHubPseudonymizationItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PSEUDONYMIZATION_ID);
			pseudonymization = modiSignalHubPseudonymizationItemValue!=null && modiSignalHubPseudonymizationItemValue.getValue()!=null && modiSignalHubPseudonymizationItemValue.getValue().booleanValue();
		}


		// Le validazioni di algorithm, seed size e lifetime vengono eseguite solo se la pseudonymization è attiva
		if(pseudonymization) {
			// operation
			validatePdndInfoSignalHubOperation(api, idServizio, portType, consoleConfiguration, properties, log);
			
			// algorithm
			validatePdndInfoSignalHubAlgorithm(consoleConfiguration, properties);

			// seed size
			validatePdndInfoSignalHubSeedSize(consoleConfiguration, properties);

			// lifetime
			validatePdndInfoSignalHubLifeTime(consoleConfiguration, properties);
		}

		// publisher
		validatePdndInfoSignalHubPublisher(registryReader, configIntegrationReader, idServizio, consoleConfiguration, properties, log);

	}
	private static void validatePdndInfoSignalHubOperation(AccordoServizioParteComune api, IDServizio idServizio, String portType,ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, Logger log) throws ProtocolException {
		AbstractConsoleItem<?> profiloSignalHubOperationItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
						ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ID
						);
		StringProperty profiloSignalHubOperationItemValue = null;
		String profiloSignalHubOperation = null;
		if(profiloSignalHubOperationItem != null) {
			profiloSignalHubOperationItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ID);
			if(profiloSignalHubOperationItemValue!=null && profiloSignalHubOperationItemValue.getValue()!=null && !"".equals(profiloSignalHubOperationItemValue.getValue())) {
				profiloSignalHubOperation = profiloSignalHubOperationItemValue.getValue();	
			}
		}
		if(profiloSignalHubOperation==null || "".equals(profiloSignalHubOperation) ||
				ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(profiloSignalHubOperation)) {
			throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ERROR_UNDEFINED);
		}
		
		Map<String, String> operations = getSignalHubOperations(api, idServizio, portType, log);
		if (!operations.containsKey(profiloSignalHubOperation)) {
			throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ERROR_WRONG);
		}
	}
	private static void validatePdndInfoSignalHubAlgorithm(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		AbstractConsoleItem<?> profiloSignalHubAlgorithmItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID
				);
		
		if(profiloSignalHubAlgorithmItem!=null) {
			
			StringProperty profiloSignalHubAlgorithmItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID);
			if(profiloSignalHubAlgorithmItemValue!=null && profiloSignalHubAlgorithmItemValue.getValue()!=null && !"".equals(profiloSignalHubAlgorithmItemValue.getValue())) {
				List<String> algorithms = ModIProperties.getInstance().getSignalHubAlgorithms();
				
				if (!algorithms.contains(profiloSignalHubAlgorithmItemValue.getValue())) {
					throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ERROR_WRONG);
				}
			}
			
		}
	}
	
	private static void validatePdndInfoSignalHubSeedSize(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		AbstractConsoleItem<?> profiloSignalHubSeedSizetem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ID
				);
		Integer seedSize = null;
		
		if(profiloSignalHubSeedSizetem != null) {
			
			NumberProperty profiloSignalHubSeedSizeItemValue = (NumberProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ID);
			if(profiloSignalHubSeedSizeItemValue!=null && profiloSignalHubSeedSizeItemValue.getValue()!=null) {
				List<Integer> seedSizes = ModIProperties.getInstance().getSignalHubSeedSize();
				seedSize = profiloSignalHubSeedSizeItemValue.getValue().intValue();
				
				if (!seedSizes.contains(seedSize)) {
					throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ERROR_WRONG);
				}
			}
			
		}
	}
	
	private static void validatePdndInfoSignalHubLifeTime(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		AbstractConsoleItem<?> profiloSignalHubSeedLifeTimeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
						ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_ID
						);
		if(profiloSignalHubSeedLifeTimeItem!=null) {
			
			NumberProperty profiloSignalHubSeedLifeTimeItemValue = (NumberProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_ID);
			if(profiloSignalHubSeedLifeTimeItemValue!=null && profiloSignalHubSeedLifeTimeItemValue.getValue()!=null) {
				try {
					long i = (profiloSignalHubSeedLifeTimeItemValue.getValue());
					if(i<=0) {
						throw new ProtocolException("deve essere fornito un numero intero maggiore di 0");
					}
				}catch(Exception e) {
					throw new ProtocolException(CostantiLabel.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_LABEL+" '"+profiloSignalHubSeedLifeTimeItemValue.getValue()+"' non valido; must be a positive integer greater than 0");
				}
			}
		}
	}
	
	
	private static void validatePdndInfoSignalHubPublisher(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio idServizio, 
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, Logger log) throws ProtocolException {
		
		boolean publisherSAundefined =  validatePdndIfnoSignalHubPublisherSA(registryReader, configIntegrationReader, idServizio, consoleConfiguration, properties, log);
		
		boolean publisherRoleUndefined = validatePdndIfnoSignalHubPublisherRole(registryReader, consoleConfiguration, properties, log);
		
		if(publisherSAundefined && publisherRoleUndefined) {
			throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ERROR_UNDEFINED);
		}
	}
	
	private static boolean validatePdndIfnoSignalHubPublisherSA(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio idServizio, 
			ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, Logger log)  throws ProtocolException {
		AbstractConsoleItem<?> profiloSignalHubSAItem = ProtocolPropertiesUtils.getAbstractConsoleItem(
				consoleConfiguration.getConsoleItem(),
				ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
		StringProperty profiloSignalHubSAItemValue = null;
		String profiloSignalHubSA = null;
		if (profiloSignalHubSAItem != null) {
			profiloSignalHubSAItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties,
					ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
			if (profiloSignalHubSAItemValue != null && 
					profiloSignalHubSAItemValue.getValue() != null &&
					!"".equals(profiloSignalHubSAItemValue.getValue()) &&
					!ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(profiloSignalHubSAItemValue.getValue())) {
				profiloSignalHubSA = profiloSignalHubSAItemValue.getValue();

				try {
					String finalSAName = profiloSignalHubSA;
					List<IDServizioApplicativo> idSAs = getSignalHubServiziApplicativi(registryReader,
							configIntegrationReader, idServizio, log);
					long matched = idSAs.stream().filter(id -> id.getNome().equals(finalSAName)).count();
					if (matched < 1) {
						throw new ProtocolException(
								ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_UNDEFINED);
					}
				} catch (RegistryNotFound | RegistryException | DriverRegistroServiziException e) {
					throw new ProtocolException(
							ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_UNDEFINED, e);
				}

			}
		}
		
		return profiloSignalHubSA==null 
				|| "".equals(profiloSignalHubSA) 
				|| ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(profiloSignalHubSA);
	}
	
	private static boolean validatePdndIfnoSignalHubPublisherRole(IRegistryReader registryReader, ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, Logger log)  throws ProtocolException {
		AbstractConsoleItem<?> profiloSignalHubRoleItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
						ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID
						);
		StringProperty profiloSignalHubRoleItemValue = null;
		String profiloSignalHubRole = null;
		if(profiloSignalHubRoleItem!=null) {
			profiloSignalHubRoleItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID);
			if(profiloSignalHubRoleItemValue!=null && 
					profiloSignalHubRoleItemValue.getValue()!=null && 
					!"".equals(profiloSignalHubRoleItemValue.getValue()) &&
					!ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(profiloSignalHubRoleItemValue.getValue())) {
				profiloSignalHubRole = profiloSignalHubRoleItemValue.getValue();
				
				String finalRole = profiloSignalHubRole;
				List<IDRuolo> roles = getSignalHubRuoli(registryReader, log);
				long matched = roles.stream().filter(r -> r.getNome().equals(finalRole)).count();
				if (matched == 0) {
					throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_UNDEFINED);
				}
			}
		}
		
		return profiloSignalHubRole==null 
				|| "".equals(profiloSignalHubRole) 
				|| ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(profiloSignalHubRole);
	}
	
	static void addSignaHubFruizioneConfig(ModIProperties modiProperties,
			ConsoleConfiguration configuration, boolean rest) throws ProtocolException {
		
		
		TitleConsoleItem titolo = (TitleConsoleItem) ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_LABEL);
		configuration.addConsoleItem(titolo );
		
		ModISignalHubConfig c = modiProperties.getSignalHubConfig();
		if(c!=null && c.getClaims()!=null && !c.getClaims().isEmpty()) {
			for (ModISignalHubParamConfig modISignalHubParamConfig : c.getClaims()) {
				addSignaHubFruizioneConfig(configuration, modISignalHubParamConfig, rest);
			}
		}
		
	}
	
	private static String addSignaHubFruizioneConfig(ConsoleConfiguration configuration, ModISignalHubParamConfig paramConfig, boolean rest) throws ProtocolException {
		
		String modeId = ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_ID_PREFIX+paramConfig.getNome(); 
		
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				modeId, 
				paramConfig.getLabel());
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_DEFAULT_VALUE);
		modeItem.setReloadOnChange(true, true);
		configuration.addConsoleItem(modeItem);
		
		StringConsoleItem ridefineItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_VALUE_ID_PREFIX+paramConfig.getNome(),
				ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_VALUE_LABEL);
		ridefineItem.setRequired(true);
		ridefineItem.setRows(2);
		ridefineItem.setInfo(buildConsoleItemInfoCorniceSicurezza(paramConfig.getLabel(), rest));
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT)) {
			ridefineItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(ridefineItem);
		
		return modeId;
	}
	
	static void updateSignaHubFruizioneConfig(ModIProperties modiProperties,
			ConsoleConfiguration configuration, ProtocolProperties properties) throws ProtocolException {
		
		ModISignalHubConfig c = modiProperties.getSignalHubConfig();
		if(c!=null && c.getClaims()!=null && !c.getClaims().isEmpty()) {
			for (ModISignalHubParamConfig modISignalHubParamConfig : c.getClaims()) {
				updateSignaHubFruizioneConfig(configuration, properties, modISignalHubParamConfig);
			}
		}
		
	}
	
	private static void updateSignaHubFruizioneConfig(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, ModISignalHubParamConfig paramConfig) throws ProtocolException {
		
		String modeId = ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_ID_PREFIX+paramConfig.getNome();
		
		boolean ridefinisciItem = false;
		StringProperty selectSchemaModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				modeId);
		if(selectSchemaModeItemValue==null) {
			ridefinisciItem = false;	
		}
		else {
			ridefinisciItem = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectSchemaModeItemValue.getValue());
		}
	
		AbstractConsoleItem<?> modeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), modeId);
		if(ridefinisciItem) {
			modeItem.setInfo(null);
		}
		else {
			modeItem.setInfo(new ConsoleItemInfo(paramConfig.getLabel()));
			modeItem.getInfo().setHeaderBody(ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_MODE_DEFAULT_INFO_INTESTAZIONE.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_MODE_DEFAULT_INFO_INTESTAZIONE_PARAM, paramConfig.getNome()));
			modeItem.getInfo().setListBody(new ArrayList<>());
			modeItem.getInfo().getListBody().addAll(paramConfig.getRulesInfo());
		}
		
		String id = ModIConsoleCostanti.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_VALUE_ID_PREFIX+paramConfig.getNome();
		AbstractConsoleItem<?> valueItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), id);
		if(ridefinisciItem) {
			valueItem.setType(ConsoleItemType.TEXT_AREA);
		}
		else {
			valueItem.setType(ConsoleItemType.HIDDEN);
			StringProperty selectItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
			if(selectItemValue!=null) {
				selectItemValue.setValue(null);
			}
		}
		
	}
}
	
