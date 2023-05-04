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

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.utils.SOAPHeader;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.properties.PropertiesUtilities;

/**
 * ModIDynamicConfigurationAccordiParteSpecificaUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationAccordiParteSpecificaUtilities {
	
	private ModIDynamicConfigurationAccordiParteSpecificaUtilities() {}

	private static String getErrorLetturaAPIFallita(Exception e) {
		return "Lettura API fallita: "+e.getMessage();
	}
	static ConsoleConfiguration getDynamicConfigParteSpecifica(ModIProperties modiProperties,
			ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizio id, IDSoggetto idFruitore, boolean fruizioni) throws ProtocolException {
		
		if(consoleHelper.isModalitaCompleta()) {
			return null;
		}
		if(ConsoleOperationType.DEL.equals(consoleOperationType)) {
			return null;
		}
		if(!isMascheraGestioneFruizioneOrErogazione(consoleHelper)) {
			return null;
		}
		boolean casoSpecialeModificaNomeFruizione = !fruizioni && isMascheraGestioneFruizione(consoleHelper);
		
		// Identificazione API
		AccordoServizioParteComune api = null;
		String portType = null; 
		try {
			if(id!=null && id.getUriAccordoServizioParteComune()!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(id.getUriAccordoServizioParteComune());
				api = registryReader.getAccordoServizioParteComune(idAccordo, false, false);
			}
			
			portType = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE);
			if((portType==null || "".equals(portType)) && id!=null) {
				portType = id.getPortType();
			}
			
		}catch(Exception e) {
			throw new ProtocolException(getErrorLetturaAPIFallita(e),e);
		}
		if(api==null) {
			return null;
		}
		
		boolean rest = ServiceBinding.REST.equals(api.getServiceBinding());
		ConsoleConfiguration configuration = new ConsoleConfiguration();
				
		boolean corniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioCorniceSicurezza(api, portType);
		String patternDatiCorniceSicurezza = null;
		String schemaDatiCorniceSicurezza = null;
		if(corniceSicurezza) {
			patternDatiCorniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaPattern(api, portType);
			if(patternDatiCorniceSicurezza==null) {
				// backward compatibility
				patternDatiCorniceSicurezza = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD;
			}
			if(!ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)) {
				schemaDatiCorniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, portType);
			}
		}
		
		// Identificazione se è richiesta la sicurezza
		if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isSicurezzaMessaggioRequired(api, portType)) {
		
			boolean digest = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioConIntegrita(api, portType);
			
			boolean headerDuplicati = false;
			boolean riferimentoX509 = false;
			if(rest) {
				headerDuplicati = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioConHeaderDuplicati(api, portType);
				riferimentoX509 = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isSicurezzaMessaggioRiferimentoX509Required(api, portType);
			}
			
			if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioApplicabileRichiesta(api, portType, true)) {
				ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.addSicurezzaMessaggio(modiProperties,
					configuration, rest, fruizioni, true, casoSpecialeModificaNomeFruizione, digest, 
					patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza,
					headerDuplicati,
					consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id, idFruitore,
					riferimentoX509,
					false);
			}
			if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioApplicabileRisposta(api, portType, true)) {
				ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.addSicurezzaMessaggio(modiProperties,
					configuration, rest, fruizioni, false, casoSpecialeModificaNomeFruizione, digest, 
					patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza,
					headerDuplicati,
					consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id, idFruitore,
					riferimentoX509,
					false);
			}
			
			return configuration;
			
		}
		else {
			
			// Sicurezza Audit
			
			if( corniceSicurezza && !ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza) &&
				schemaDatiCorniceSicurezza!=null) {
					
				boolean forceRest = true;
				
				ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.addSicurezzaMessaggio(modiProperties,
						configuration, forceRest, fruizioni, true, casoSpecialeModificaNomeFruizione, 
						false,  // digest
						patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza,
						false, // headerDuplicati
						consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id, idFruitore,
						false, // riferimentoX509
						true // audit
						); 
				
				return configuration; // NOTA: contiene già i dati richiesti in sicurezza OAuth
				
			}
			
			
			// Sicurezza OAuth
			
			if( fruizioni ) {
				boolean sicurezzaMessaggioNonPresente = false;
				boolean tokenSignedJWT = ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.addSicurezzaTokenSignedJWT(rest,
						configuration,
						consoleOperationType, consoleHelper, 
						registryReader, configIntegrationReader, 
						id, idFruitore,
						sicurezzaMessaggioNonPresente);
				
				if(tokenSignedJWT) {
					/**this.addKeStoreConfigOAuth_choice(configuration);*/
					ModIDynamicConfigurationKeystoreUtilities.addTrustStoreKeystoreFruizioneOAuthConfigChoice(configuration);
					
					/**boolean requiredValue = casoSpecialeModificaNomeFruizione ? false : true;*/
					boolean requiredValue = true;
					ModIDynamicConfigurationKeystoreUtilities.addKeystoreConfig(configuration, true, false, requiredValue);
				
					return configuration;
				}
			}
			
			
		}
		
		return null;
		
	}
	
	static boolean updateDynamicConfigParteSpecifica(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IDServizio id, IRegistryReader registryReader, boolean fruizioni) throws ProtocolException {
		if(consoleHelper.isModalitaCompleta()) {
			return false;
		}
		if(ConsoleOperationType.DEL.equals(consoleOperationType)) {
			return false;
		}
		if(!isMascheraGestioneFruizioneOrErogazione(consoleHelper)) {
			return false;
		}
		boolean casoSpecialeModificaNomeFruizione = !fruizioni && isMascheraGestioneFruizione(consoleHelper);
		
		// Identificazione API
		AccordoServizioParteComune api = null;
		String portType = null; 
		try {
			if(id!=null && id.getUriAccordoServizioParteComune()!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(id.getUriAccordoServizioParteComune());
				api = registryReader.getAccordoServizioParteComune(idAccordo, false, false);
			}
			
			portType = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE);
			if((portType==null || "".equals(portType)) && id!=null) {
				portType = id.getPortType();
			}
			
		}catch(Exception e) {
			throw new ProtocolException(getErrorLetturaAPIFallita(e),e);
		}
		if(api==null) {
			return false;
		}
		boolean rest = ServiceBinding.REST.equals(api.getServiceBinding());
		
		boolean corniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioCorniceSicurezza(api, portType);
		String patternDatiCorniceSicurezza = null;
		String schemaDatiCorniceSicurezza = null;
		if(corniceSicurezza) {
			patternDatiCorniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaPattern(api, portType);
			if(patternDatiCorniceSicurezza==null) {
				// backward compatibility
				patternDatiCorniceSicurezza = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD;
			}
			if(!ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)) {
				schemaDatiCorniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, portType);
			}
		}
		
		// Identificazione se è richiesta la sicurezza
		if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isSicurezzaMessaggioRequired(api, portType)) {
					
			boolean headerDuplicati = false;
			if(rest) {
				headerDuplicati = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioConHeaderDuplicati(api, portType);
			}
			
			boolean kidMode = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isSicurezzaMessaggioKidModeSupported(api, portType);
			
			if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioApplicabileRichiesta(api, portType, true)) {
				ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.updateSicurezzaMessaggio(modiProperties,
					consoleConfiguration, properties, rest, fruizioni, true, casoSpecialeModificaNomeFruizione, 
					patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza,
					headerDuplicati, consoleHelper,
					kidMode);
			}
			if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioApplicabileRisposta(api, portType, true)) {
				ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.updateSicurezzaMessaggio(modiProperties,
					consoleConfiguration, properties, rest, fruizioni, false, casoSpecialeModificaNomeFruizione, 
					patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza,
					headerDuplicati, consoleHelper,
					kidMode);
			}
			
		}
		else {
			
			// Sicurezza Audit
			
			if( corniceSicurezza && !ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza) &&
					schemaDatiCorniceSicurezza!=null) {
					
				boolean forceRest = true;
				
				boolean kidMode = true;
				
				ModIDynamicConfigurationAccordiParteSpecificaSicurezzaMessaggioUtilities.updateSicurezzaMessaggio(modiProperties,
						consoleConfiguration, properties, forceRest, fruizioni, true, casoSpecialeModificaNomeFruizione, 
						patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza,
						false, // headerDuplicati
						consoleHelper,
						kidMode);
				
			}
			
			
			// Sicurezza OAuth
			
			if(fruizioni) {
				
				/**boolean requiredValue = casoSpecialeModificaNomeFruizione ? false : true;*/
				boolean requiredValue = true;
				
				boolean hideSceltaArchivioFilePath = false;
				boolean addHiddenSubjectIssuer = false;
				
				/**boolean checkRidefinisciOauth = true;*/
				boolean checkRidefinisciOauth = false;
				
				rest = true; // forzo comportamento come fosse REST poichè si tratta della configurazione necessaria per generare token JWT
				
				ModIDynamicConfigurationKeystoreUtilities.updateKeystoreConfig(consoleConfiguration, properties, true, checkRidefinisciOauth,
						hideSceltaArchivioFilePath, addHiddenSubjectIssuer, 
						requiredValue, null,
						rest);
			}
			
		}
		
		return true;
	}
	
	private static final String PREFIX_VERIFICATO_QUANTO_INDICATO_IN = "Verificare quanto indicato in ";
	
	static boolean validateDynamicConfigParteSpecifica(ModIProperties modiProperties,
			ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties, IDServizio id,
			IRegistryReader registryReader, boolean fruizioni) throws ProtocolException {
		
		if(!isMascheraGestioneFruizioneOrErogazione(consoleHelper)) {
			return false;
		}
		boolean casoSpecialeModificaNomeFruizione = !fruizioni && isMascheraGestioneFruizione(consoleHelper);
		if(casoSpecialeModificaNomeFruizione) {
			return false;
		}
		
		AccordoServizioParteComune api = null;
		String portType = null; 
		try {
			if(id!=null && id.getUriAccordoServizioParteComune()!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(id.getUriAccordoServizioParteComune());
				api = registryReader.getAccordoServizioParteComune(idAccordo, false, false);
			}
			
			portType = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE);
			if((portType==null || "".equals(portType)) && id!=null) {
				portType = id.getPortType();
			}
			
		}catch(Exception e) {
			throw new ProtocolException(getErrorLetturaAPIFallita(e),e);
		}
		if(api==null) {
			return false;
		}
		
		
		boolean corniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioCorniceSicurezza(api, portType);
		String patternDatiCorniceSicurezza = null;
		String schemaDatiCorniceSicurezza = null;
		if(corniceSicurezza) {
			patternDatiCorniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaPattern(api, portType);
			if(patternDatiCorniceSicurezza==null) {
				// backward compatibility
				patternDatiCorniceSicurezza = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD;
			}
			if(!ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza)) {
				schemaDatiCorniceSicurezza = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, portType);
			}
		}
		
		
		// Sicurezza Audit
		boolean sicurezzaAudit = false;
		if( fruizioni  &&
				corniceSicurezza && !ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza) &&
				schemaDatiCorniceSicurezza!=null) {
			sicurezzaAudit = true;
		}
		
		
		// Identificazione se è richiesta la sicurezza
		if(ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isSicurezzaMessaggioRequired(api, portType) || sicurezzaAudit) {
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHttpHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID
							);
			if(profiloSicurezzaMessaggioHttpHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioHttpHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID);
				if(profiloSicurezzaMessaggioHttpHeadersItemValue!=null && profiloSicurezzaMessaggioHttpHeadersItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue())) {
					try {
						String [] hdrObbligatori = modiProperties.getRestSecurityTokenSignedHeaders();
						if(hdrObbligatori!=null && hdrObbligatori.length>0) {
							
							String [] hdrImpostati = profiloSicurezzaMessaggioHttpHeadersItemValue.getValue().split(",");
							if(hdrImpostati==null || hdrImpostati.length<=0) {
								throw new ProtocolException("Nessun header indicato");
							}
							
							for (String hdrObbligatorio : hdrObbligatori) {
								boolean found = false;
								for (String hdrImpostato : hdrImpostati) {
									if(hdrImpostato.equalsIgnoreCase(hdrObbligatorio)) {
										found = true;
										break;
									}
								}
								if(!found) {
									throw new ProtocolException("Header obbligatorio '"+hdrObbligatorio+"' non indicato");
								}
							}
							
						}
					}catch(Exception e) {
						throw new ProtocolException(PREFIX_VERIFICATO_QUANTO_INDICATO_IN+ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL+": "+e.getMessage(),e);
					}
				}
				else {
					throw new ProtocolException(PREFIX_VERIFICATO_QUANTO_INDICATO_IN+ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL+": nessun header indicato");
				}
				
			}
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioSoapHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID
							);
			if(profiloSicurezzaMessaggioSoapHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioSoapHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID);
				if(profiloSicurezzaMessaggioSoapHeadersItemValue!=null && profiloSicurezzaMessaggioSoapHeadersItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioSoapHeadersItemValue.getValue())) {
					try {
						String sValue = profiloSicurezzaMessaggioSoapHeadersItemValue.getValue();
						SOAPHeader.parse(sValue);
					}catch(Exception e) {
						throw new ProtocolException(PREFIX_VERIFICATO_QUANTO_INDICATO_IN+ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL+": "+e.getMessage(),e);
					}
				}
				
			}
			
			if(!fruizioni) {
				try {
					ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(properties, false);
				}catch(Exception e) {
					throw new ProtocolException("Verificare i parametri indicati per il keystore in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+": "+e.getMessage(),e);
				}
			}
			
			boolean rest = ServiceBinding.REST.equals(api.getServiceBinding());
			boolean digest = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioConIntegrita(api, portType);
			boolean corniceSicurezzaLegacy = false;
			if(corniceSicurezza) {
				corniceSicurezzaLegacy = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternDatiCorniceSicurezza);
			}
			boolean headerDuplicati = false;
			if(rest) {
				headerDuplicati = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.isProfiloSicurezzaMessaggioConHeaderDuplicati(api, portType);
			}
			boolean requestCalcolatoSuInfoFruizioni = fruizioni;
						
			// Claims
			if(rest) {
				String idProperty = (fruizioni ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				if(profiloSicurezzaMessaggioRestJwtClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtClaimsItemValue.getValue()!=null) {
					Properties claims = PropertiesUtilities.convertTextToProperties(profiloSicurezzaMessaggioRestJwtClaimsItemValue.getValue());
					checkClaims(modiProperties, claims, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL, requestCalcolatoSuInfoFruizioni, digest, corniceSicurezzaLegacy);
				}
			}
		
			// Header Duplicati
			if(rest && headerDuplicati) {
				String idProperty = (fruizioni ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				if(profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue.getValue()!=null) {
					Properties claims = PropertiesUtilities.convertTextToProperties(profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue.getValue());
					checkClaims(modiProperties, claims, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL, requestCalcolatoSuInfoFruizioni, digest, corniceSicurezzaLegacy);
				}
				
				idProperty = (fruizioni ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtModiClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				if(profiloSicurezzaMessaggioRestJwtModiClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtModiClaimsItemValue.getValue()!=null) {
					Properties claims = PropertiesUtilities.convertTextToProperties(profiloSicurezzaMessaggioRestJwtModiClaimsItemValue.getValue());
					checkClaims(modiProperties, claims, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL.
							replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI()), 
							requestCalcolatoSuInfoFruizioni, digest, corniceSicurezzaLegacy);
				}
			}
			
			
			// X5U URL
			if(rest) {
				
				String idUrl = null;
				if(fruizioni) {
					idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X5U_URL_ID;
				}
				else if(!fruizioni) {
					idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X5U_URL_ID;
				}
				if(idUrl!=null) {
					AbstractConsoleItem<?> profiloSicurezzaMessaggioRestUrlItem = 	
							ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
									idUrl
									);
					if(profiloSicurezzaMessaggioRestUrlItem!=null) {
						
						StringProperty profiloSicurezzaMessaggioRestUrlItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idUrl);
						if(profiloSicurezzaMessaggioRestUrlItemValue!=null && profiloSicurezzaMessaggioRestUrlItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioRestUrlItemValue.getValue())) {
							try {
								InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioRestUrlItemValue.getValue(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
							}catch(Exception e) {
								throw new ProtocolException(e.getMessage(),e);
							}
						}
						
					}
				}
			}
			
			// Audit
			String idAudit = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_ID;
			String labelAudit = rest ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL :  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL;
			AbstractConsoleItem<?> profiloSicurezzaMessaggioAudienceItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							idAudit
							);
			if(profiloSicurezzaMessaggioAudienceItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioAudienceItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idAudit);
				if(profiloSicurezzaMessaggioAudienceItemValue!=null && profiloSicurezzaMessaggioAudienceItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceItemValue.getValue(), labelAudit);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				
			}
			
			// Audit Risposta
			if(fruizioni) {
				idAudit = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_ID;
				labelAudit = rest ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL :  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL;
				profiloSicurezzaMessaggioAudienceItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
								idAudit
								);
				if(profiloSicurezzaMessaggioAudienceItem!=null) {
					
					StringProperty profiloSicurezzaMessaggioAudienceItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idAudit);
					if(profiloSicurezzaMessaggioAudienceItemValue!=null && profiloSicurezzaMessaggioAudienceItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceItemValue.getValue(), labelAudit);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
					
				}
			}

			// Cornice Sicurezza - CodiceEnte
			AbstractConsoleItem<?> profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_ID
							);
			if(profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItem!=null) {
				StringProperty profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_ID);
				if(profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue!=null && profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL,
								true); // possono essere presenti spazi all'interno
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// Cornice Sicurezza - UserId
			AbstractConsoleItem<?> profiloSicurezzaMessaggioCorniceSicurezzaUserIdItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_ID
							);
			if(profiloSicurezzaMessaggioCorniceSicurezzaUserIdItem!=null) {
				StringProperty profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_ID);
				if(profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue!=null && profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL,
								true); // possono essere presenti spazi all'interno
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// Cornice Sicurezza - IPUser
			AbstractConsoleItem<?> profiloSicurezzaMessaggioCorniceSicurezzaIPUserItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_ID
							);
			if(profiloSicurezzaMessaggioCorniceSicurezzaIPUserItem!=null) {
				StringProperty profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_ID);
				if(profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue!=null && profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL,
								true); // possono essere presenti spazi all'interno
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// Audience Integrity
			if(fruizioni) {
				AbstractConsoleItem<?> profiloSicurezzaMessaggioAudienceIntegrityItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID
								);
				if(profiloSicurezzaMessaggioAudienceIntegrityItem!=null) {
					StringProperty profiloSicurezzaMessaggioAudienceIntegrityItemValue = 
							(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID);
					if(profiloSicurezzaMessaggioAudienceIntegrityItemValue!=null && profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
									replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI())
									+ " - "+
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
				}
			}
			// Header Duplicati
			if(rest && headerDuplicati) {
				String idAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID;
				AbstractConsoleItem<?> profiloSicurezzaMessaggioAudienceIntegrityItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
								idAud
								);
				if(profiloSicurezzaMessaggioAudienceIntegrityItem!=null) {
					StringProperty profiloSicurezzaMessaggioAudienceIntegrityItemValue = 
							(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idAud);
					if(profiloSicurezzaMessaggioAudienceIntegrityItemValue!=null && profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
									replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI())
									+ " - "+
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
				}
				
				idAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RISPOSTA_ID;
				profiloSicurezzaMessaggioAudienceIntegrityItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
								idAud
								);
				if(profiloSicurezzaMessaggioAudienceIntegrityItem!=null) {
					StringProperty profiloSicurezzaMessaggioAudienceIntegrityItemValue = 
							(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idAud);
					if(profiloSicurezzaMessaggioAudienceIntegrityItemValue!=null && profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
									replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, modiProperties.getRestSecurityTokenHeaderModI())
									+ " - "+
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL);
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
						InputValidationUtils.validateTextAreaInput(keystorePathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_LABEL +" - "+
								ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// TrustStore Path
			AbstractConsoleItem<?> truststorePathItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID
							);
			if(truststorePathItem!=null) {
				StringProperty truststorePathItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID);
				if(truststorePathItemValue!=null && truststorePathItemValue.getValue()!=null && !"".equals(truststorePathItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(truststorePathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_LABEL +" - "+
										ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// TrustStore CRL
			AbstractConsoleItem<?> truststoreCRLPathItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID
							);
			if(truststoreCRLPathItem!=null) {
				StringProperty truststoreCRLPathItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID);
				if(truststoreCRLPathItemValue!=null && truststoreCRLPathItemValue.getValue()!=null && !"".equals(truststoreCRLPathItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(truststoreCRLPathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_LABEL +" - "+
										ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
			// TrustStore Path (SSL)
			truststorePathItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID
							);
			if(truststorePathItem!=null) {
				StringProperty truststorePathItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID);
				if(truststorePathItemValue!=null && truststorePathItemValue.getValue()!=null && !"".equals(truststorePathItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(truststorePathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_LABEL +" - "+
										ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
						
			// TrustStore CRL (SSL)
			truststoreCRLPathItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID
							);
			if(truststoreCRLPathItem!=null) {
				StringProperty truststoreCRLPathItemValue = 
						(StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID);
				if(truststoreCRLPathItemValue!=null && truststoreCRLPathItemValue.getValue()!=null && !"".equals(truststoreCRLPathItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(truststoreCRLPathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_LABEL +" - "+
										ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}

		}
		
		return true;
	}
	
	private static void checkClaims(ModIProperties modiProperties,
			Properties claims, String elemento, boolean request, boolean digest, boolean corniceSicurezzaLegacy) throws ProtocolException {
		List<String> denyClaims = null;
		String claimNameClientId = null;
		try {
			denyClaims = modiProperties.getUsedRestSecurityClaims(request, digest);
			claimNameClientId = modiProperties.getRestSecurityTokenClaimsClientIdHeader();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(claims!=null && !claims.isEmpty()) {
			for (Object oClaim : claims.keySet()) {
				if(oClaim instanceof String) {
					String claim = (String) oClaim;
					String value = claims.getProperty(claim);
					
					String debugS = "'"+claim+"', indicato nel campo "+elemento;
					
					if(value!=null &&  DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(value.trim())) {
						if(claim.equalsIgnoreCase(claimNameClientId) || 
								(claim.equalsIgnoreCase(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER) && !corniceSicurezzaLegacy) ||
								(claim.equalsIgnoreCase(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT) && !corniceSicurezzaLegacy)
							) {
							continue;
						}
						else {
							throw new ProtocolException("Keyword '"+DynamicHelperCostanti.NOT_GENERATE+"' non utilizzabile nel claim "+debugS);
						}
					}
					if(denyClaims.contains(claim) || denyClaims.contains(claim.toLowerCase())) {
						throw new ProtocolException("Il claim "+debugS+", non può essere configurato");
					}
					if(value==null || StringUtils.isEmpty(value)) {
						throw new ProtocolException("Claim "+debugS+", non valorizzato");
					}
				}
			}
		}
	}
	
	private static boolean isMascheraGestioneFruizioneOrErogazione(IConsoleHelper consoleHelper) {
		boolean gestioneFruitori = isMascheraGestioneFruizione(consoleHelper);
		boolean gestioneErogatori = isMascheraGestioneErogazione(consoleHelper);
		return gestioneErogatori || gestioneFruitori;
	}
	
	private static boolean isMascheraGestioneErogazione(IConsoleHelper consoleHelper) {
		String tipologia = consoleHelper.getAttributeFromSession(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE, String.class);
		if(tipologia == null) {
			try {
				String p = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM);
				if(p!=null) {
					tipologia = p;
				}
			}catch(Exception e) {
				// ignore
			}
		}
		return tipologia!=null &&
			Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia);
	}
	private static boolean isMascheraGestioneFruizione(IConsoleHelper consoleHelper) {
		String tipologia = consoleHelper.getAttributeFromSession(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE, String.class);
		if(tipologia == null) {
			try {
				String p = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM);
				if(p!=null) {
					tipologia = p;
				}
			}catch(Exception e) {
				// ignore
			}
		}
		return tipologia!=null &&
			Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia);
	}
}
