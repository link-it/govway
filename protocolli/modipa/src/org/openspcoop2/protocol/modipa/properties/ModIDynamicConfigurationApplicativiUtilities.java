/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.validator.IdentificazioneApplicativoMittenteUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
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
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.certificate.CertificateInfo;

/**
 * ModIDynamicConfigurationApplicativiUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationApplicativiUtilities {
	
	private ModIDynamicConfigurationApplicativiUtilities() {}

	private static final String VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO = "Verificare le configurazioni dove risulta associato l'applicativo.";
	
	private static List<String> getPddOperative(IRegistryReader registryReader) throws RegistryException{
		List<String> pddOperative = null;
		try {
			pddOperative = registryReader.findIdPorteDominio(true);
		}catch(RegistryNotFound notFound) {
			// ignore
		}
		return pddOperative;
	}
	
	static ConsoleConfiguration getDynamicConfigServizioApplicativo(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		boolean esterno = false;
		try {
			String dominio = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO);
			if( 
					(dominio==null || "".equals(dominio)) 
					&&
					ConsoleOperationType.CHANGE.equals(consoleOperationType)
				) {
				Soggetto soggetto = registryReader.getSoggetto(id.getIdSoggettoProprietario());
				if(soggetto.getPortaDominio()==null || "".equals(soggetto.getPortaDominio())) {
					dominio = PddTipologia.ESTERNO.toString();
				}
				else {
					List<String> pddOperative = getPddOperative(registryReader);
					if(pddOperative==null || pddOperative.isEmpty() || !pddOperative.contains(soggetto.getPortaDominio())) {
						dominio = PddTipologia.ESTERNO.toString();	
					}
					else {
						dominio = PddTipologia.OPERATIVO.toString();
					}
				}
			}
			esterno = PddTipologia.ESTERNO.toString().equals(dominio);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		boolean isClient = true;
		try {
			String client = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA);
			isClient = (client==null) || ("".equals(client)) || (CostantiConfigurazione.CLIENT.equals(client)) || (CostantiConfigurazione.CLIENT_OR_SERVER.equals(client));
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(id);
				isClient = CostantiConfigurazione.CLIENT.equals(sa.getTipo()) || sa.isUseAsClient();
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		if(isClient) {
			if(!esterno) {
				
				BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
						ModIConsoleCostanti.MODIPA_APPLICATIVI_ID, 
						ModIConsoleCostanti.MODIPA_APPLICATIVI_LABEL);
				configuration.addConsoleItem(titolo );
				
				BaseConsoleItem subTitolo = ProtocolPropertiesFactory.newSubTitleItem(
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL);
				configuration.addConsoleItem(subTitolo );
				
				BooleanConsoleItem booleanConsoleItem = 
						(BooleanConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, ConsoleItemType.CHECKBOX,
								ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_ID, ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_LABEL);
				booleanConsoleItem.setDefaultValue(false);
				booleanConsoleItem.setReloadOnChange(true);
				configuration.addConsoleItem(booleanConsoleItem);
				
				ModIDynamicConfigurationKeystoreUtilities.addKeystoreConfig(configuration, false, true, true);
				
				BaseConsoleItem subTitoloModiAUTH = ProtocolPropertiesFactory.newSubTitleItem(
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_MODI_AUTH_SUBTITLE_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_MODI_AUTH_SUBTITLE_LABEL);
				configuration.addConsoleItem(subTitoloModiAUTH);
			}
			
			if(esterno) {
				BaseConsoleItem subTitolo = ProtocolPropertiesFactory.newSubTitleItem(
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_ID, 
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PARAMETRI_RISPOSTA);
				configuration.addConsoleItem(subTitolo );
			}
			
			String labelSicurezzaMessaggioAudienceItem = esterno ? 
					ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL : 
					ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL;
			StringConsoleItem profiloSicurezzaMessaggioAudienceItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID, 
					labelSicurezzaMessaggioAudienceItem);
			profiloSicurezzaMessaggioAudienceItem.setRows(2);
			profiloSicurezzaMessaggioAudienceItem.setNote(esterno ? 
					ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_NOTE:
					ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_NOTE);
			ConsoleItemInfo infoAud = new ConsoleItemInfo(labelSicurezzaMessaggioAudienceItem);
			if(esterno) {
				infoAud.setHeaderBody(ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO);
			}
			else {
				infoAud.setHeaderBody(ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO);
			}
			profiloSicurezzaMessaggioAudienceItem.setInfo(infoAud);
			profiloSicurezzaMessaggioAudienceItem.setRequired(false);
			configuration.addConsoleItem(profiloSicurezzaMessaggioAudienceItem);
			
			if(!esterno) {
				StringConsoleItem profiloSicurezzaMessaggioX5UItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT_AREA,
					ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID, 
					ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
				profiloSicurezzaMessaggioX5UItem.setRows(2);
				profiloSicurezzaMessaggioX5UItem.setNote(ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_NOTE);
				ConsoleItemInfo infoX5U = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
				infoX5U.setHeaderBody(ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_INFO);
				profiloSicurezzaMessaggioX5UItem.setInfo(infoX5U);
				profiloSicurezzaMessaggioX5UItem.setRequired(false);
				configuration.addConsoleItem(profiloSicurezzaMessaggioX5UItem);
			}
						
			if(!esterno) {
				BaseConsoleItem subTitolo = ProtocolPropertiesFactory.newSubTitleItem(
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_SUBTITLE_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL);
				configuration.addConsoleItem(subTitolo );
				
				BooleanConsoleItem booleanConsoleItem = 
						(BooleanConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, ConsoleItemType.CHECKBOX,
								ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_ID, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_LABEL);
				booleanConsoleItem.setDefaultValue(false);
				booleanConsoleItem.setReloadOnChange(true);
				configuration.addConsoleItem(booleanConsoleItem);
				
				StringConsoleItem tokenPolicyItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.SELECT,
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_LABEL);
				
				List<GenericProperties> gestorePolicyTokenList = null;
				try {
					gestorePolicyTokenList = configIntegrationReader.getTokenPolicyValidazione();
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				
				String [] policyLabels = null;
				String [] policyValues = null;
				boolean tokenPolicyUndefined = true; // !ConsoleOperationType.CHANGE.equals(consoleOperationType)
				if(tokenPolicyUndefined){
					policyLabels = new String[gestorePolicyTokenList.size() + 1];
					policyValues = new String[gestorePolicyTokenList.size() + 1];
					
					policyLabels[0] = ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED;
					policyValues[0] = ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED;
				}
				else {
					policyLabels = new String[gestorePolicyTokenList.size()];
					policyValues = new String[gestorePolicyTokenList.size()];
				}
				
				for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
					GenericProperties genericProperties = gestorePolicyTokenList.get(i);
					if(tokenPolicyUndefined){
						policyLabels[(i+1)] = genericProperties.getNome();
						policyValues[(i+1)] = genericProperties.getNome();
					}
					else {
						policyLabels[i] = genericProperties.getNome();
						policyValues[i] = genericProperties.getNome();
					}
				}
				
				for (int i = 0; i < policyValues.length; i++) {
					tokenPolicyItem.addLabelValue(policyLabels[i],policyValues[i]);
				}
				if(!ConsoleOperationType.CHANGE.equals(consoleOperationType)){
					tokenPolicyItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
				}
				tokenPolicyItem.setReloadOnChange(false);
				configuration.addConsoleItem(tokenPolicyItem);
				
				StringConsoleItem tokenClientIdItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_LABEL);
				tokenClientIdItem.setRequired(true);
				configuration.addConsoleItem(tokenClientIdItem);
				
				StringConsoleItem tokenKIDItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_KID_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_KID_LABEL);
				tokenKIDItem.setRequired(false);
				ConsoleItemInfo info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_KID_LABEL);
				info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_INFO);
				info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_REST_INFO_VALORI_REQUEST); // uso rest volutamente
				tokenKIDItem.setInfo(info);
				configuration.addConsoleItem(tokenKIDItem);
			}
			
			return configuration;
		}
		else {
			return null;
		}
		
	}
	
	static void updateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		
		boolean esterno = false;
		try {
			String dominio = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO);
			esterno = PddTipologia.ESTERNO.toString().equals(dominio);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		boolean isClient = true;
		try {
			String client = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA);
			isClient = (client==null) || ("".equals(client)) || (CostantiConfigurazione.CLIENT.equals(client)) || (CostantiConfigurazione.CLIENT_OR_SERVER.equals(client));
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(id);
				isClient = CostantiConfigurazione.CLIENT.equals(sa.getTipo()) || sa.isUseAsClient();
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		
		if(!esterno && isClient) {
			
			ConfigurazioneMultitenant configurazioneMultitenant = null;
						
			BooleanProperty booleanModeItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_ID);
			if(booleanModeItemValue!=null && booleanModeItemValue.getValue()!=null && booleanModeItemValue.getValue().booleanValue()) {
				boolean hideSceltaArchivioFilePath = false;
				try {
					configurazioneMultitenant = configIntegrationReader.getConfigurazioneMultitenant();
				}
				catch(RegistryNotFound notFound) {
					// ignore
				}
				catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				/**
				if(configurazioneMultitenant!=null &&
						StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
					hideSceltaArchivioFilePath = true;
				}
				FIX: visualizzo sempre: ho aggiunto un commento. Altrimenti se poi uno modifica la configurazione multitenat, gli applicativi gia' configurati con modalita 'path' vanno in errore
				*/			
				boolean addHiddenSubjectIssuer = true;
				boolean rest = true; // un applicativo può essere utilizzato sia da API REST che da API SOAP
				ModIDynamicConfigurationKeystoreUtilities.updateKeystoreConfig(consoleConfiguration, properties, false, 
						hideSceltaArchivioFilePath, addHiddenSubjectIssuer,
						true, configurazioneMultitenant,
						rest);
				
				BaseConsoleItem subTitoloModIAuth = ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_MODI_AUTH_SUBTITLE_ID);
				if(subTitoloModIAuth!=null) {
					subTitoloModIAuth.setType(ConsoleItemType.SUBTITLE);
				}

			}
			else {

				// devo annullare eventuale archivio caricato
				BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
				if(archiveItemValue!=null) {
					archiveItemValue.setValue(null);
					archiveItemValue.setFileName(null);
					archiveItemValue.setClearContent(true);
				}
				
				// devo annullare eventuale certificato caricato
				BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
				if(certificateItemValue!=null) {
					certificateItemValue.setValue(null);
					certificateItemValue.setFileName(null);
					certificateItemValue.setClearContent(true);
				}
				
				StringProperty keyAliasMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID);
				if(keyAliasMODIItemValue!=null) {
					keyAliasMODIItemValue.setValue(null);
				}
				
				StringProperty keyPasswordMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
				if(keyPasswordMODIItemValue!=null) {
					keyPasswordMODIItemValue.setValue(null);
				}
				
				StringProperty keyCNIssuerMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID);
				if(keyCNIssuerMODIItemValue!=null) {
					keyCNIssuerMODIItemValue.setValue(null);
				}
				
				StringProperty keyCNSubjectMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID);
				if(keyCNSubjectMODIItemValue!=null) {
					keyCNSubjectMODIItemValue.setValue(null);
				}
				
				StringProperty keystoreModeMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
				if(keystoreModeMODIItemValue!=null) {
					keystoreModeMODIItemValue.setValue(null);
				}
				
				StringProperty keystorePasswordMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
				if(keystorePasswordMODIItemValue!=null) {
					keystorePasswordMODIItemValue.setValue(null);
				}
				
				StringProperty keystorePathMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
				if(keystorePathMODIItemValue!=null) {
					keystorePathMODIItemValue.setValue(null);
				}
				
				StringProperty keystorePathPublicKeyMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID);
				if(keystorePathPublicKeyMODIItemValue!=null) {
					keystorePathPublicKeyMODIItemValue.setValue(null);
				}
				
				StringProperty keystoreKeyAlgoMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_KEY_ALGORITHM_ID);
				if(keystoreKeyAlgoMODIItemValue!=null) {
					keystoreKeyAlgoMODIItemValue.setValue(null);
				}
				
				StringProperty keystoreTypeMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
				if(keystoreTypeMODIItemValue!=null) {
					keystoreTypeMODIItemValue.setValue(null);
				}
				
				AbstractConsoleItem<?> profiloSicurezzaMessaggioAudienceItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID);
				if(profiloSicurezzaMessaggioAudienceItem!=null) {
					profiloSicurezzaMessaggioAudienceItem.setType(ConsoleItemType.HIDDEN);
				}
				StringProperty profiloSicurezzaMessaggioAudienceItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID);
				if(profiloSicurezzaMessaggioAudienceItemValue!=null) {
					profiloSicurezzaMessaggioAudienceItemValue.setValue(null);
				}
				
				AbstractConsoleItem<?> profiloSicurezzaMessaggioX5UItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID);
				if(profiloSicurezzaMessaggioX5UItem!=null) {
					profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.HIDDEN);
				}
				StringProperty profiloSicurezzaMessaggioX5UItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID);
				if(profiloSicurezzaMessaggioX5UItemValue!=null) {
					profiloSicurezzaMessaggioX5UItemValue.setValue(null);
				}
				
				BaseConsoleItem subTitoloModIAuth = ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_MODI_AUTH_SUBTITLE_ID);
				if(subTitoloModIAuth!=null) {
					subTitoloModIAuth.setType(ConsoleItemType.HIDDEN);
				}
				
			}
		
			
			BooleanProperty booleanModeItemTokenValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_ID);
			if(booleanModeItemTokenValue!=null && booleanModeItemTokenValue.getValue()!=null && booleanModeItemTokenValue.getValue().booleanValue()) {
				
				StringProperty tokenPolicyItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID);
				
				AbstractConsoleItem<?> tokenPolicyItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID);
				boolean tokenPolicyDefined = false;
				if(tokenPolicyItem!=null) {
					if(configurazioneMultitenant==null) {
						try {
							configurazioneMultitenant = configIntegrationReader.getConfigurazioneMultitenant();
						}
						catch(RegistryNotFound notFound) {
							// ignore
						}
						catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
					
					if(configurazioneMultitenant!=null &&
							StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
							!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
						if(tokenPolicyItemValue==null || tokenPolicyItemValue.getValue()==null || StringUtils.isEmpty(tokenPolicyItemValue.getValue()) || 
								ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(tokenPolicyItemValue.getValue())) {
							tokenPolicyItem.setNote(ModIConsoleCostanti.MODIPASICUREZZA_TOKEN_POLICY_NOTE);
						}
						else {
							tokenPolicyDefined = true;
						}
						tokenPolicyItem.setType(ConsoleItemType.SELECT);
						tokenPolicyItem.setRequired(false);
						tokenPolicyItem.setReloadOnChange(true);
					}
					else {
						tokenPolicyItem.setNote(null);
						tokenPolicyItem.setType(ConsoleItemType.HIDDEN);
						tokenPolicyItem.setRequired(false);
					}
				}
				
				AbstractConsoleItem<?> tokenClientIdItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
				if(tokenClientIdItem!=null) {
					tokenClientIdItem.setType(ConsoleItemType.TEXT_EDIT);
					tokenClientIdItem.setRequired(true);
					if(tokenPolicyDefined) {
						tokenClientIdItem.setInfo(null);
					}
					else {
						ConsoleItemInfo info = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_LABEL);
						info.setHeaderBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_INFO);
						info.setListBody(DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_REST_INFO_VALORI_REQUEST); // uso rest volutamente
						tokenClientIdItem.setInfo(info);
					}
				}
				
				AbstractConsoleItem<?> tokenKidItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_KID_ID);
				if(tokenKidItem!=null) {
					// verifico se abilitato il certificato
					if(booleanModeItemValue!=null && booleanModeItemValue.getValue()!=null && booleanModeItemValue.getValue().booleanValue()) {
						tokenKidItem.setType(ConsoleItemType.TEXT_EDIT);
					}
					else {
						tokenKidItem.setType(ConsoleItemType.HIDDEN);
					}
				}
			}
			else {
				
				StringProperty tokenPolicyItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID);
				if(tokenPolicyItemValue!=null) {
					tokenPolicyItemValue.setValue(null);
				}
								
				AbstractConsoleItem<?> tokenPolicyItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID);
				if(tokenPolicyItem!=null) {
					tokenPolicyItem.setType(ConsoleItemType.HIDDEN);
					tokenPolicyItem.setRequired(false);
					tokenPolicyItem.setNote(null);
				}
				
				
				AbstractConsoleItem<?> tokenClientIdItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
				if(tokenClientIdItem!=null) {
					tokenClientIdItem.setType(ConsoleItemType.HIDDEN);
					tokenClientIdItem.setRequired(false);
				}
				
				StringProperty tokenClientIdItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
				if(tokenClientIdItemValue!=null) {
					tokenClientIdItemValue.setValue(null);
				}
				
				AbstractConsoleItem<?> tokenKidItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_KID_ID);
				if(tokenKidItem!=null) {
					tokenKidItem.setType(ConsoleItemType.HIDDEN);
				}
				
				StringProperty tokenKidItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_KID_ID);
				if(tokenKidItemValue!=null) {
					tokenKidItemValue.setValue(null);
				}
				
			}

		}
		
	}
	
	static void validateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		
		boolean esterno = false;
		try {
			String dominio = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO);
			esterno = PddTipologia.ESTERNO.toString().equals(dominio);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		boolean isClient = true;
		try {
			String client = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA);
			isClient = (client==null) || ("".equals(client)) || (CostantiConfigurazione.CLIENT.equals(client)) || (CostantiConfigurazione.CLIENT_OR_SERVER.equals(client));
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(id);
				isClient = CostantiConfigurazione.CLIENT.equals(sa.getTipo()) || sa.isUseAsClient();
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		if(isClient) {
						
			boolean verifyKeystoreConfig = false;
			boolean verifyCertificateConfig = false;
			boolean changeBinary = false;
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				try {
					String p = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_PP_CHANGE_BINARY);
					if(Costanti.CONSOLE_PARAMETRO_PP_CHANGE_BINARY_VALUE_TRUE.equalsIgnoreCase(p)) {
						verifyKeystoreConfig = true;
						
						changeBinary = true;
						
						StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
						if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
							String modalita = selectModeItemValue.getValue();
							if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
								verifyKeystoreConfig = true;
							}
							else {
								verifyCertificateConfig = true;
								verifyKeystoreConfig = false;
							}
						}
						
						if(verifyKeystoreConfig) {
							BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
							if(archiveItemValue==null || archiveItemValue.getValue()==null) {
								throw new ProtocolException("Archivio non fornito");
							}
						}
						else if(verifyCertificateConfig) {
							BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
							if(certificateItemValue==null || certificateItemValue.getValue()==null) {
								throw new ProtocolException("Certificato non fornito");
							}
						}
					}
					else {
						// devo verificare se c'e' stato un cambio nella modalita
						StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
						if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
							String modalita = selectModeItemValue.getValue();
							if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
								verifyKeystoreConfig = true;
							}
							else {
								verifyCertificateConfig = true;
							}
						}
					}
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
			else if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
				
				StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
				if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
					String modalita = selectModeItemValue.getValue();
					if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
						verifyKeystoreConfig = true;
					}
					else {
						verifyCertificateConfig = true;
					}
				}

			}
	
			CertificateInfo cert = null;
			if(!esterno) {
			
				if(verifyKeystoreConfig) {
					// NOTA: se si attiva anche la validazione durante il change binary, poi non si riesce a modificarlo poiche' la password o l'alis, o qualche parametro non è compatibile con il nuovo archivio.
					
					try {
						cert = ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(properties, false);
					}catch(Exception e) {
						throw new ProtocolException("Verificare i parametri indicati per il keystore in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL_MSG_ERROR+": "+e.getMessage(),e);
					}
				}
				else if(verifyCertificateConfig) {
					try {
						cert = ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(properties, true);
					}catch(Exception e) {
						throw new ProtocolException("Verificare il certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL_MSG_ERROR+": "+e.getMessage(),e);
					}
				
				}
					
				if(verifyCertificateConfig &&
					// il controllo lo devo fare solamente se definisco una modalità per cui consente di fornire a parte il certificato
					// la modalita 'verifyKeystoreConfig' è associata alla modalità archivio dove si carica il PKCS12.
					// in questo caso se abilito la validazione sotto indicata, non posso definire 2 applicativi con lo stesso PKCS12, che invece potrebbe dover servire.
					// Serve in una fase di fruizione, nella fase di erogazione con multitenant, poi verrà identificato uno a casa ma non è evitabile.
					// In questi casi si consiglia di usare anzi una modalità filesyste o HSM dove si fornirà il certificato in più solo per l'applicativo che deve essere identificato lato erogazione
					cert!=null && cert.getSubject()!=null) {
						
					ProtocolFiltroRicercaServiziApplicativi filtro = IdentificazioneApplicativoMittenteUtils.createFilter(cert.getSubject().toString(), 
							cert.getIssuer().toString());
					
					List<IDServizioApplicativo> list = null;
					try {
						list = configIntegrationReader.findIdServiziApplicativi(filtro);
					}catch(RegistryNotFound notFound) {
						// ignore
					}
					catch(Exception t) {
						throw new ProtocolException("Errore non atteso durante la verfica del certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL_MSG_ERROR+": "+t.getMessage(),t);
					}
					if(list!=null) {
						for (IDServizioApplicativo idServizioApplicativoSubjectIssuerCheck : list) {
							// Possono esistere piu' sil che hanno un CN con subject e issuer.
							
							java.security.cert.Certificate certificatoCheck = null;
							try {
								ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(idServizioApplicativoSubjectIssuerCheck);
								certificatoCheck = IdentificazioneApplicativoMittenteUtils.readServizioApplicativoByCertificate(sa, null);
							}catch(Exception t) {
								throw new ProtocolException("Errore non atteso durante la verfica del certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL_MSG_ERROR+": "+t.getMessage(),t);
							}
							
							if(certificatoCheck instanceof java.security.cert.X509Certificate &&
								cert.equals(((java.security.cert.X509Certificate)certificatoCheck),true) &&
								(ConsoleOperationType.ADD.equals(consoleOperationType) || !idServizioApplicativoSubjectIssuerCheck.equals(id))
							){
								throw new ProtocolException("Il certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL_MSG_ERROR+" risulta già assegnato all'applicativo '"+idServizioApplicativoSubjectIssuerCheck.getNome()+"'");
							}
							
						}
					}
					
				}
				
				if(changeBinary) {
					try {
						if(cert!=null && cert.getSubject()!=null) {
							StringProperty subjectItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID);
							subjectItemValue.setValue(cert.getSubject().toString());
							
							StringProperty issuerItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID);
							if(cert.getIssuer()!=null) {
								issuerItemValue.setValue(cert.getIssuer().toString());
							}
							else {
								if(issuerItemValue!=null) {
									issuerItemValue.setValue(null);
								}
							}
						}
					}catch(Exception e) {
						// errore sollevato in validazione
					}
				}
				
			}
			
			// Audience Risposta
			AbstractConsoleItem<?> audienceRispostaItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID
							);
			if(audienceRispostaItem!=null) {
				StringProperty audienceRispostaItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID);
				if(audienceRispostaItemValue!=null && audienceRispostaItemValue.getValue()!=null && !"".equals(audienceRispostaItemValue.getValue())) {
					try {
						String labelSicurezzaMessaggioAudienceItem = esterno ? 
								ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL : 
								ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL;
						InputValidationUtils.validateTextAreaInput(audienceRispostaItemValue.getValue(), 
								labelSicurezzaMessaggioAudienceItem);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// Public X5U URL
			if(!esterno) {
				
				AbstractConsoleItem<?> x5uUrlItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
								ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID
								);
				if(x5uUrlItem!=null) {
					StringProperty x5uUrlItemValue = 
							(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID);
					if(x5uUrlItemValue!=null && x5uUrlItemValue.getValue()!=null && !"".equals(x5uUrlItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(x5uUrlItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
				}
			}
			
			// Keystore Path
			AbstractConsoleItem<?> keystorePathItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID
							);
			if(keystorePathItem!=null) {
				StringProperty keystorePathItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
				if(keystorePathItemValue!=null && keystorePathItemValue.getValue()!=null && !"".equals(keystorePathItemValue.getValue())) {
					try {
						String keystoreType = null;
						StringProperty keystoreTypeItemValue = 
								(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
						if(keystoreTypeItemValue!=null && keystoreTypeItemValue.getValue()!=null) {
							keystoreType = keystoreTypeItemValue.getValue();
						}
						
						InputValidationUtils.validateTextAreaInput(keystorePathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_LABEL +" - "+
								((ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_KEY_PAIR.equals(keystoreType))? ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PRIVATE_KEY_LABEL : ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_LABEL));
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// Keystore Path 'PublicKey'
			AbstractConsoleItem<?> keystorePathPublicKeyItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID
							);
			if(keystorePathPublicKeyItem!=null) {
				StringProperty keystorePathPublicKeyItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID);
				if(keystorePathPublicKeyItemValue!=null && keystorePathPublicKeyItemValue.getValue()!=null && !"".equals(keystorePathPublicKeyItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(keystorePathPublicKeyItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_LABEL +" - "+
								ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			String tokenPolicyName = null;
			if(!esterno) {
				
				BooleanProperty booleanModeItemTokenValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_ID);
				if(booleanModeItemTokenValue!=null && booleanModeItemTokenValue.getValue()!=null && booleanModeItemTokenValue.getValue().booleanValue()) {
					
					StringProperty tokenPolicyItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID);
					if(tokenPolicyItemValue==null || tokenPolicyItemValue.getValue()==null || StringUtils.isEmpty(tokenPolicyItemValue.getValue()) || 
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(tokenPolicyItemValue.getValue())) {
						/** NON e' OBBLIGATORIA!
						//throw new ProtocolException("Deve essere selezionata una "+ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_LABEL);*/
					}
					else {
						tokenPolicyName = tokenPolicyItemValue.getValue();
					}
					
					// Altrimenti la select list è valorizzata con quelli esistenti
					if(tokenPolicyName!=null) {
						GenericProperties gp = null;
						try {
							gp = configIntegrationReader.getTokenPolicyValidazione(tokenPolicyName);
						}
						catch(RegistryNotFound notFound) {
							// ignore
						}
						catch(RegistryException e) {
							throw new ProtocolException(ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_LABEL+" indicata '"+tokenPolicyName+"' non esiste? (errore: "+e.getMessage()+")",e);
						}
						if(gp==null) {
							throw new ProtocolException(ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_LABEL+" indicata '"+tokenPolicyName+"' non esiste");
						}
					}
										
					StringProperty tokenClientIdItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
					if(tokenClientIdItemValue==null || tokenClientIdItemValue.getValue()==null || StringUtils.isEmpty(tokenClientIdItemValue.getValue())) {
						throw new ProtocolException("Deve essere indicato un "+ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_LABEL);
					}
					
					boolean tokenWithHttpsEnabled = false;
					// basta un protocollo che lo supporta per doverli cercare anche con la funzionalita' abilitata
					for(IProtocolFactory<?> protocolFactory: ProtocolFactoryManager.getInstance().getProtocolFactories().values()) {
						if(protocolFactory.createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken()) {
							tokenWithHttpsEnabled = true;
							break;
						}
					}
					
					ServizioApplicativo sa = null;
					if(tokenPolicyName!=null) {
						try {
							sa = configIntegrationReader.getServizioApplicativoByCredenzialiToken(tokenPolicyName, tokenClientIdItemValue.getValue(), tokenWithHttpsEnabled);
							boolean alreadyExists = false;
							if(sa!=null) {
								if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
									alreadyExists=true;
								}
								else {
									IDServizioApplicativo idSAFind = new IDServizioApplicativo();
									idSAFind.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
									idSAFind.setNome(sa.getNome());
									if(!id.equals(idSAFind)) {
										alreadyExists=true;
									}
								}
							}
							if(alreadyExists) {
								String labelSoggetto = NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
								throw new ProtocolException("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già l'identificativo client indicato");
							}
						}catch(RegistryNotFound notFound) {
							// ignore
						}
						catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
				}
				
				if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
					
					BooleanProperty booleanModeItemSicurezzaMessaggioValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_ID);
					boolean sicurezzaMessaggio = false;
					if(booleanModeItemSicurezzaMessaggioValue!=null && booleanModeItemSicurezzaMessaggioValue.getValue()!=null) {
						sicurezzaMessaggio = booleanModeItemSicurezzaMessaggioValue.getValue();
					}
					
					ServizioApplicativo sa = null;
					try {
						sa = configIntegrationReader.getServizioApplicativo(id);
					}catch(Exception t) {
						// ignore
					}
					if(sa!=null) {
						String oldTokenPolicyName = null;
						String oldCnSubject = null;
						/**String oldCnIssuer = null;*/
						if(sa.sizeProtocolPropertyList()>0) {
							for (org.openspcoop2.core.config.ProtocolProperty pp : sa.getProtocolPropertyList()) {
								if(ModIConsoleCostanti.MODIPA_SICUREZZA_TOKEN_POLICY_ID.equals(pp.getName())) {
									oldTokenPolicyName = pp.getValue();
								}
								else if(ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID.equals(pp.getName())) {
									oldCnSubject = pp.getValue();
								}
								/**else if(ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID.equals(pp.getName())) {
									oldCnIssuer = pp.getValue();
								}*/
							}
						}
						
						StringBuilder sbWarningChange = new StringBuilder();
						if(oldTokenPolicyName!=null && StringUtils.isNotEmpty(oldTokenPolicyName) && !"-".equals(oldTokenPolicyName)) {
							if(tokenPolicyName==null || StringUtils.isEmpty(tokenPolicyName)) {
								sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede la token policy '"+oldTokenPolicyName+"' eliminata.");
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
							}
							else if(!oldTokenPolicyName.equals(tokenPolicyName)) {
								sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede la precedente token policy '"+oldTokenPolicyName+"' modificata in '"+tokenPolicyName+"'.");
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
							}
						}
						
						if(oldCnSubject!=null && StringUtils.isNotEmpty(oldCnSubject) &&
							!sicurezzaMessaggio) {
							if(sbWarningChange.length()>0) {
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede il certificato eliminato ("+oldCnSubject+").");
							sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
						}
						
						/**
						boolean certWarning = false;
						if(oldCnSubject!=null && StringUtils.isNotEmpty(oldCnSubject)) {
							if(cert==null || cert.getSubject()==null) {
								if(sbWarningChange.length()>0) {
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede il certificato eliminato ("+oldCnSubject+").");
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
								certWarning = true;
							}
							else if(!oldCnSubject.equals(cert.getSubject().toString())) {
								if(sbWarningChange.length()>0) {
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede il precedente certificato ("+oldCnSubject+").");
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
								certWarning = true;
							}
						}
						
						if(!certWarning && oldCnIssuer!=null && StringUtils.isNotEmpty(oldCnIssuer)) {
							if(cert==null || cert.getIssuer()==null) {
								if(sbWarningChange.length()>0) {
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede il certificato eliminato (subject["+oldCnSubject+"] issuer["+oldCnIssuer+"]).");
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
							}
							else if(!oldCnIssuer.equals(cert.getIssuer().toString())) {
								if(sbWarningChange.length()>0) {
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
									sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								sbWarningChange.append("L'applicativo potrebbe essere stato associato ad una erogazione o fruizione che richiede il precedente certificato (subject["+oldCnSubject+"] issuer["+oldCnIssuer+"]).");
								sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								sbWarningChange.append(VERIFICARE_CONFIG_ASSOCIAZIONE_APPLICATIVO);
							}
						}
						*/
						
						if(sbWarningChange.length()>0) {
							try {
								consoleHelper.setMessage(sbWarningChange.toString(), true, "warn");
							}catch(Exception t) {
								// ignore
							}
						}
					}
					
				}
			}
		}
	}
}
