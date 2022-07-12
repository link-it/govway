/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.AzioniUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.SOAPHeader;
import org.openspcoop2.protocol.modipa.validator.AbstractModIValidazioneSintatticaCommons;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
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
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPortTypeAzioni;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaRisorse;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.properties.PropertiesUtilities;

/**
 * ModIDynamicConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfiguration extends BasicDynamicConfiguration implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {

	private ModIProperties modiProperties = null;

	public ModIDynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
		this.modiProperties = ModIProperties.getInstance();
	}

	

	/*** APPLICATIVI */
	
	@Override
	public ConsoleConfiguration getDynamicConfigServizioApplicativo(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		boolean esterno = false;
		try {
			String dominio = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO);
			if(dominio==null || "".equals(dominio)) {
				if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
					Soggetto soggetto = registryReader.getSoggetto(id.getIdSoggettoProprietario());
					if(soggetto.getPortaDominio()==null || "".equals(soggetto.getPortaDominio())) {
						dominio = PddTipologia.ESTERNO.toString();
					}
					else {
						List<String> pddOperative = null;
						try {
							pddOperative = registryReader.findIdPorteDominio(true);
						}catch(RegistryNotFound notFound) {}
						if(pddOperative==null || pddOperative.isEmpty() || !pddOperative.contains(soggetto.getPortaDominio())) {
							dominio = PddTipologia.ESTERNO.toString();	
						}
						else {
							dominio = PddTipologia.OPERATIVO.toString();
						}
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
			isClient = (client==null) || ("".equals(client)) || (CostantiConfigurazione.CLIENT.toString().equals(client)) || (CostantiConfigurazione.CLIENT_OR_SERVER.toString().equals(client));
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(id);
				isClient = CostantiConfigurazione.CLIENT.toString().equals(sa.getTipo()) || sa.isUseAsClient();
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
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_ID, 
						ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL);
				configuration.addConsoleItem(subTitolo );
				
				BooleanConsoleItem booleanConsoleItem = 
						(BooleanConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, ConsoleItemType.CHECKBOX,
								ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_ID, ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_LABEL);
				booleanConsoleItem.setDefaultValue(false);
				booleanConsoleItem.setReloadOnChange(true);
				configuration.addConsoleItem(booleanConsoleItem);
				
				this.addKeystoreConfig(configuration, false, true, true);
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
						
			return configuration;
		}
		else {
			return super.getDynamicConfigServizioApplicativo(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id);
		}
		
	}

	@Override
	public void updateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader,
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
			isClient = (client==null) || ("".equals(client)) || (CostantiConfigurazione.CLIENT.toString().equals(client)) || (CostantiConfigurazione.CLIENT_OR_SERVER.toString().equals(client));
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(id);
				isClient = CostantiConfigurazione.CLIENT.toString().equals(sa.getTipo()) || sa.isUseAsClient();
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		
		if(!esterno && isClient) {
			
			BooleanProperty booleanModeItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_ID);
			if(booleanModeItemValue!=null && booleanModeItemValue.getValue()!=null && booleanModeItemValue.getValue()) {
				boolean hideSceltaArchivioFilePath = false;
				ConfigurazioneMultitenant configurazioneMultitenant = null;
				try {
					configurazioneMultitenant = configIntegrationReader.getConfigurazioneMultitenant();
				}
				catch(RegistryNotFound notFound) {}
				catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				/*
				if(configurazioneMultitenant!=null &&
						StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
					hideSceltaArchivioFilePath = true;
				}
				FIX: visualizzo sempre: ho aggiunto un commento. Altrimenti se poi uno modifica la configurazione multitenat, gli applicativi gia' configurati con modalita 'path' vanno in errore
				*/			
				boolean addHiddenSubjectIssuer = true;
				this.updateKeystoreConfig(consoleConfiguration, properties, false, 
						hideSceltaArchivioFilePath, addHiddenSubjectIssuer,
						true, configurazioneMultitenant);
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
				
				AbstractConsoleItem<?> profiloSicurezzaMessaggioAudienceItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID);
				profiloSicurezzaMessaggioAudienceItem.setType(ConsoleItemType.HIDDEN);
				
				AbstractConsoleItem<?> profiloSicurezzaMessaggioX5UItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID);
				profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.HIDDEN);
			}
		
		}
		
	}

	@Override
	public void validateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader,
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
			isClient = (client==null) || ("".equals(client)) || (CostantiConfigurazione.CLIENT.toString().equals(client)) || (CostantiConfigurazione.CLIENT_OR_SERVER.toString().equals(client));
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(id);
				isClient = CostantiConfigurazione.CLIENT.toString().equals(sa.getTipo()) || sa.isUseAsClient();
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
	
			if(!esterno) {
			
				CertificateInfo cert = null;
				
				if(verifyKeystoreConfig) {
					// NOTA: se si attiva anche la validazione durante il change binary, poi non si riesce a modificarlo poiche' la password o l'alis, o qualche parametro non è compatibile con il nuovo archivio.
					
					try {
						cert = ModIDynamicConfiguration.readKeystoreConfig(properties, false);
					}catch(Throwable e) {
						throw new ProtocolException("Verificare i parametri indicati per il keystore in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+": "+e.getMessage(),e);
					}
				}
				else if(verifyCertificateConfig) {
					try {
						cert = ModIDynamicConfiguration.readKeystoreConfig(properties, true);
					}catch(Throwable e) {
						throw new ProtocolException("Verificare il certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+": "+e.getMessage(),e);
					}
					
					if(cert!=null && cert.getSubject()!=null) {
						
						FiltroRicercaServiziApplicativi filtro = AbstractModIValidazioneSintatticaCommons.createFilter(cert.getSubject().toString(), 
								cert.getIssuer().toString());
						
						List<IDServizioApplicativo> list = null;
						try {
							list = configIntegrationReader.findIdServiziApplicativi(filtro);
						}catch(RegistryNotFound notFound) {}
						catch(Throwable t) {
							throw new ProtocolException("Errore non atteso durante la verfica del certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+": "+t.getMessage(),t);
						}
						if(list!=null) {
							for (IDServizioApplicativo idServizioApplicativoSubjectIssuerCheck : list) {
								// Possono esistere piu' sil che hanno un CN con subject e issuer.
								
								java.security.cert.Certificate certificatoCheck = null;
								try {
									ServizioApplicativo sa = configIntegrationReader.getServizioApplicativo(idServizioApplicativoSubjectIssuerCheck);
									certificatoCheck = AbstractModIValidazioneSintatticaCommons.readServizioApplicativoByCertificate(sa);
								}catch(Throwable t) {
									throw new ProtocolException("Errore non atteso durante la verfica del certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+": "+t.getMessage(),t);
								}
								
								if(certificatoCheck!=null) {
									//if(certificate.equals(certificatoCheck.getCertificate(),true)) {
									if(certificatoCheck instanceof java.security.cert.X509Certificate) {
										if(cert.equals(((java.security.cert.X509Certificate)certificatoCheck),true)) {
											if(ConsoleOperationType.ADD.equals(consoleOperationType) || !idServizioApplicativoSubjectIssuerCheck.equals(id)) {
												throw new ProtocolException("Il certificato caricato in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+" risulta già assegnato all'applicativo '"+idServizioApplicativoSubjectIssuerCheck.getNome()+"'");
											}
											
										}
									}
								}
								
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
								issuerItemValue.setValue(null);
							}
						}
					}catch(Throwable e) {
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
				if(audienceRispostaItemValue.getValue()!=null && !"".equals(audienceRispostaItemValue.getValue())) {
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
					if(x5uUrlItemValue.getValue()!=null && !"".equals(x5uUrlItemValue.getValue())) {
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
				if(keystorePathItemValue.getValue()!=null && !"".equals(keystorePathItemValue.getValue())) {
					try {
						InputValidationUtils.validateTextAreaInput(keystorePathItemValue.getValue(), 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_LABEL +" - "+
								ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_LABEL);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
			}
			
		}
	}

	
	
	
	
	/*** API */
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
					
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_API_ID, 
				ModIConsoleCostanti.MODIPA_API_LABEL);
		configuration.addConsoleItem(titolo );
		
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_PROFILO_CANALE_ID, 
				ModIConsoleCostanti.MODIPA_API_PROFILO_CANALE_LABEL));
		
		StringConsoleItem profiloSicurezzaCanaleItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL);
		profiloSicurezzaCanaleItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NEW,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01);
		profiloSicurezzaCanaleItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NEW,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02);
		profiloSicurezzaCanaleItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE);
		if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NOTE);
		}
		profiloSicurezzaCanaleItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaCanaleItem);
		
		boolean rest = this.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
		addProfiloSicurezzaMessaggio(configuration, rest, false);
		
		return configuration;
	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
			throws ProtocolException {
		
		updateProfiloSicurezzaCanale(consoleConfiguration, properties, registryReader);
		
		boolean rest = this.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
		updateProfiloSicurezzaMessaggio(consoleConfiguration, consoleHelper, properties, registryReader, rest, false);
		
	}
		
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException{
		
		boolean rest = this.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
		boolean permettiModificaNomeAccordo = true;
		// Lascio il codice se servisse, ma è stato aggiunto la gestione sull'update dell'API
		if(!permettiModificaNomeAccordo && ConsoleOperationType.CHANGE.equals(consoleOperationType) && id!=null) {
			try {
				String apiGestioneParziale = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APC_API_GESTIONE_PARZIALE);
				if(Costanti.CONSOLE_VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI.equals(apiGestioneParziale)) {
					String nome = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APC_NOME);
					String versioneS = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APC_VERSIONE);
					int versione = -1;
					try {
						versione = Integer.valueOf(versioneS);
					}catch(Exception e) {}
					if(nome!=null && versione>0) {
						if(!id.getNome().equals(nome) || id.getVersione().intValue()!=versione) {
							
							AccordoServizioParteComune as = registryReader.getAccordoServizioParteComune(id);
							if(ServiceBinding.REST.equals(as.getServiceBinding())){
								
								FiltroRicercaRisorse filtro = new FiltroRicercaRisorse();
								ProtocolProperties protocolPropertiesResources = new ProtocolProperties();
								protocolPropertiesResources.addProperty(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID, IDAccordoFactory.getInstance().getUriFromIDAccordo(id));
								filtro.setProtocolPropertiesRisorsa(protocolPropertiesResources);
								List<IDResource> list = null;
								try {
									list = registryReader.findIdResourceAccordo(filtro);
								}catch(RegistryNotFound notFound) {}
								if(list!=null && !list.isEmpty()) {
									// ne dovrebbe esistere solo una.
									IDResource idR = list.get(0);
									String uriAPI = NamingUtils.getLabelAccordoServizioParteComune(idR.getIdAccordo());
									Resource resource = registryReader.getResourceAccordo(idR);
									String labelR = NamingUtils.getLabelResource(resource);
									throw new Exception("Non è possibile modificare le informazioni generali dell'API poichè riferita dalla risorsa '"+labelR+"' dell'API '"+uriAPI+"' (Profilo non bloccante PUSH)");
								}
								
							}
							else {
							
								FiltroRicercaPortTypeAzioni filtro = new FiltroRicercaPortTypeAzioni();
								ProtocolProperties protocolPropertiesAzioni = new ProtocolProperties();
								protocolPropertiesAzioni.addProperty(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID, IDAccordoFactory.getInstance().getUriFromIDAccordo(id));
								filtro.setProtocolPropertiesAzione(protocolPropertiesAzioni);
								List<IDPortTypeAzione> list = null;
								try {
									list = registryReader.findIdAzionePortType(filtro);
								}catch(RegistryNotFound notFound) {}
								if(list!=null && !list.isEmpty()) {
									// ne dovrebbe esistere solo una.
									IDPortTypeAzione idA = list.get(0);
									String uriAPI = NamingUtils.getLabelAccordoServizioParteComune(idA.getIdPortType().getIdAccordo());
									throw new Exception("Non è possibile modificare le informazioni generali dell'API poichè riferita dall'azione '"+idA.getNome()+"' del Servizio '"+idA.getIdPortType().getNome()+"' nell'API '"+uriAPI+"' (Profilo non bloccante PUSH)");
								}
								
							}
						}
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
		}
		
		validateProfiloSicurezzaMessaggio(consoleConfiguration, consoleHelper, properties, registryReader, rest, false);
	}
	
	private boolean isApiRest(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper,
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException {
		boolean rest = true;
		AccordoServizioParteComune aspc = null;
		if(ConsoleOperationType.CHANGE.equals(consoleOperationType) && id!=null) {
			try {
				aspc = registryReader.getAccordoServizioParteComune(id, false);
			}catch(RegistryNotFound notFound) {}
			catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		if(aspc!=null) {
			rest = ServiceBinding.REST.equals(aspc.getServiceBinding());
		}
		else {
			try {
				String serviceBinding = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SERVICE_BINDING);
				rest = !ServiceBinding.SOAP.name().equals(serviceBinding);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return rest;
	}
	
	
	
	
	/*** OPERAZIONI SOAP */
	
	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id) throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_AZIONE_ID, 
				ModIConsoleCostanti.MODIPA_AZIONE_LABEL);
		configuration.addConsoleItem(titolo );
		
		addProfiloInterazione(configuration, false, null);
		
		addProfiloSicurezzaMessaggio(configuration, false, true);
		
		return configuration;
		
	}

	@Override
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
			throws ProtocolException {
		
		updateProfiloInterazione(consoleConfiguration, consoleOperationType, properties, registryReader, id.getIdPortType().getIdAccordo(), id.getIdPortType().getNome(), id.getNome(), false, null);
		
		updateProfiloSicurezzaMessaggio(consoleConfiguration, consoleHelper, properties, registryReader, false, true);
		
	}

	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
			throws ProtocolException {

		validateProfiloInterazione(consoleConfiguration, properties, registryReader, id.getIdPortType().getIdAccordo(), id.getIdPortType().getNome(), id.getNome(), false);
		
		validateProfiloSicurezzaMessaggio(consoleConfiguration, consoleHelper, properties, registryReader, false, true);
	}


	
	
	/*** RISORSE REST */
	
	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path) throws ProtocolException {

		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_AZIONE_ID, 
				ModIConsoleCostanti.MODIPA_AZIONE_LABEL);
		configuration.addConsoleItem(titolo );
		
		addProfiloInterazione(configuration, true, httpMethod);
		
		addProfiloSicurezzaMessaggio(configuration, true, true);
		
		return configuration;
		
	}

	@Override
	public void updateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path)
			throws ProtocolException {
		
		updateProfiloInterazione(consoleConfiguration, consoleOperationType, properties, registryReader, id.getIdAccordo(), null, id.getNome(), true, httpMethod);
		
		updateProfiloSicurezzaMessaggio(consoleConfiguration, consoleHelper, properties, registryReader, true, true);
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path)
			throws ProtocolException {
		
		validateProfiloInterazione(consoleConfiguration, properties, registryReader, id.getIdAccordo(), null, id.getNome(), true);
		
		validateProfiloSicurezzaMessaggio(consoleConfiguration, consoleHelper, properties, registryReader, true, true);
	}
	
	
	
	
	
	
	
	/** EROGAZIONI / FRUIZIONI **/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizio id) throws ProtocolException {
		
		ConsoleConfiguration configuration = getDynamicConfigParteSpecifica(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id, false);
		if(configuration!=null) {
			return configuration;
		}
		return super.getDynamicConfigAccordoServizioParteSpecifica(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id);
		
	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		
		boolean operazioneGestita = updateDynamicConfigParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, id, registryReader, false);
		if(!operazioneGestita) {
			super.updateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
			return;
		}
		
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		
		boolean operazioneGestita = this.validateDynamicConfigParteSpecifica(consoleConfiguration, consoleHelper, properties, id, registryReader, false);
		if(!operazioneGestita) {
			super.validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
			return;
		}
		
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		
		ConsoleConfiguration configuration = getDynamicConfigParteSpecifica(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id.getIdServizio(), true);
		if(configuration!=null) {
			return configuration;
		}
		return super.getDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleOperationType, consoleHelper, registryReader,
				configIntegrationReader, id);
		
	}

	@Override
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id)
			throws ProtocolException {
		
		boolean operazioneGestita = updateDynamicConfigParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, id.getIdServizio(), registryReader, true);
		if(!operazioneGestita) {
			super.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType,
					consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}

	@Override
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id)
			throws ProtocolException {
	
		boolean operazioneGestita = this.validateDynamicConfigParteSpecifica(consoleConfiguration, consoleHelper, properties, id.getIdServizio(), registryReader, true);
		if(!operazioneGestita) {
			super.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType,
					consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
	private ConsoleConfiguration getDynamicConfigParteSpecifica(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizio id, boolean fruizioni) throws ProtocolException {
		
		if(consoleHelper.isModalitaCompleta()) {
			return null;
		}
		if(ConsoleOperationType.DEL.equals(consoleOperationType)) {
			return null;
		}
		if(!isMascheraGestioneFruizioneOrErogazione(consoleHelper)) {
			return null;
		}
		boolean casoSpecialeModificaNomeFruizione = !fruizioni && this.isMascheraGestioneFruizione(consoleHelper);
		
		// Identificazione API
		AccordoServizioParteComune api = null;
		String portType = null; 
		try {
			if(id!=null && id.getUriAccordoServizioParteComune()!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(id.getUriAccordoServizioParteComune());
				api = registryReader.getAccordoServizioParteComune(idAccordo, false);
			}
			
			portType = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE);
			if((portType==null || "".equals(portType)) && id!=null) {
				portType = id.getPortType();
			}
			
		}catch(Exception e) {
			throw new ProtocolException("Lettura API fallita: "+e.getMessage(),e);
		}
		if(api==null) {
			return null;
		}
		
		// Identificazione se è richiesta la sicurezza
		if(isSicurezzaMessaggioRequired(api, portType)) {
		
			ConsoleConfiguration configuration = new ConsoleConfiguration();
				
			boolean rest = ServiceBinding.REST.equals(api.getServiceBinding());
			boolean digest = isProfiloSicurezzaMessaggioConIntegrita(api, portType);
			boolean corniceSicurezza = isProfiloSicurezzaMessaggioCorniceSicurezza(api, portType);
			boolean headerDuplicati = false;
			if(rest) {
				headerDuplicati = isProfiloSicurezzaMessaggioConHeaderDuplicati(api, portType);
			}
			
			addSicurezzaMessaggio(configuration, rest, fruizioni, true, casoSpecialeModificaNomeFruizione, digest, corniceSicurezza, headerDuplicati);
			addSicurezzaMessaggio(configuration, rest, fruizioni, false, casoSpecialeModificaNomeFruizione, digest, corniceSicurezza, headerDuplicati);
			
			return configuration;
			
		}
		
		return null;
		
	}
	
	private boolean updateDynamicConfigParteSpecifica(ConsoleConfiguration consoleConfiguration,
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
		boolean casoSpecialeModificaNomeFruizione = !fruizioni && this.isMascheraGestioneFruizione(consoleHelper);
		
		// Identificazione API
		AccordoServizioParteComune api = null;
		String portType = null; 
		try {
			if(id!=null && id.getUriAccordoServizioParteComune()!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(id.getUriAccordoServizioParteComune());
				api = registryReader.getAccordoServizioParteComune(idAccordo, false);
			}
			
			portType = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE);
			if((portType==null || "".equals(portType)) && id!=null) {
				portType = id.getPortType();
			}
			
		}catch(Exception e) {
			throw new ProtocolException("Lettura API fallita: "+e.getMessage(),e);
		}
		if(api==null) {
			return false;
		}
		
		// Identificazione se è richiesta la sicurezza
		if(isSicurezzaMessaggioRequired(api, portType)) {
		
			boolean rest = ServiceBinding.REST.equals(api.getServiceBinding());
			boolean corniceSicurezza = isProfiloSicurezzaMessaggioCorniceSicurezza(api, portType);
			boolean headerDuplicati = false;
			if(rest) {
				headerDuplicati = isProfiloSicurezzaMessaggioConHeaderDuplicati(api, portType);
			}
			
			this.updateSicurezzaMessaggio(consoleConfiguration, properties, rest, fruizioni, true, casoSpecialeModificaNomeFruizione, corniceSicurezza, headerDuplicati, consoleHelper);
			this.updateSicurezzaMessaggio(consoleConfiguration, properties, rest, fruizioni, false, casoSpecialeModificaNomeFruizione, corniceSicurezza, headerDuplicati, consoleHelper);	
			
		}
		
		return true;
	}
	
	private boolean validateDynamicConfigParteSpecifica(ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties, IDServizio id,
			IRegistryReader registryReader, boolean fruizioni) throws ProtocolException {
		
		if(!isMascheraGestioneFruizioneOrErogazione(consoleHelper)) {
			return false;
		}
		boolean casoSpecialeModificaNomeFruizione = !fruizioni && this.isMascheraGestioneFruizione(consoleHelper);
		if(casoSpecialeModificaNomeFruizione) {
			return false;
		}
		
		AccordoServizioParteComune api = null;
		String portType = null; 
		try {
			if(id!=null && id.getUriAccordoServizioParteComune()!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(id.getUriAccordoServizioParteComune());
				api = registryReader.getAccordoServizioParteComune(idAccordo, false);
			}
			
			portType = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE);
			if((portType==null || "".equals(portType)) && id!=null) {
				portType = id.getPortType();
			}
			
		}catch(Exception e) {
			throw new ProtocolException("Lettura API fallita: "+e.getMessage(),e);
		}
		if(api==null) {
			return false;
		}
		
		// Identificazione se è richiesta la sicurezza
		if(isSicurezzaMessaggioRequired(api, portType)) {
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHttpHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID
							);
			if(profiloSicurezzaMessaggioHttpHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioHttpHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID);
				if(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue())) {
					try {
						String [] hdrObbligatori = this.modiProperties.getRestSecurityTokenSignedHeaders();
						if(hdrObbligatori!=null && hdrObbligatori.length>0) {
							
							String [] hdrImpostati = profiloSicurezzaMessaggioHttpHeadersItemValue.getValue().split(",");
							if(hdrImpostati==null || hdrImpostati.length<=0) {
								throw new  Exception("Nessun header indicato");
							}
							
							for (String hdrObbligatorio : hdrObbligatori) {
								boolean found = false;
								for (String hdrImpostato : hdrImpostati) {
									if(hdrImpostato.toLowerCase().equals(hdrObbligatorio.toLowerCase())) {
										found = true;
										break;
									}
								}
								if(!found) {
									throw new  Exception("Header obbligatorio '"+hdrObbligatorio+"' non indicato");
								}
							}
							
						}
					}catch(Exception e) {
						throw new ProtocolException("Verificare quanto indicato in "+ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL+": "+e.getMessage(),e);
					}
				}
				else {
					throw new ProtocolException("Verificare quanto indicato in "+ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL+": nessun header indicato");
				}
				
			}
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioSoapHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID
							);
			if(profiloSicurezzaMessaggioSoapHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioSoapHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID);
				if(profiloSicurezzaMessaggioSoapHeadersItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioSoapHeadersItemValue.getValue())) {
					try {
						String sValue = profiloSicurezzaMessaggioSoapHeadersItemValue.getValue();
						SOAPHeader.parse(sValue);
					}catch(Exception e) {
						throw new ProtocolException("Verificare quanto indicato in "+ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL+": "+e.getMessage(),e);
					}
				}
				
			}
			
			if(!fruizioni) {
				try {
					ModIDynamicConfiguration.readKeystoreConfig(properties, false);
				}catch(Throwable e) {
					throw new ProtocolException("Verificare i parametri indicati per il keystore in "+ModIConsoleCostanti.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL+": "+e.getMessage(),e);
				}
			}
			
			boolean rest = ServiceBinding.REST.equals(api.getServiceBinding());
			boolean digest = isProfiloSicurezzaMessaggioConIntegrita(api, portType);
			boolean corniceSicurezza = isProfiloSicurezzaMessaggioCorniceSicurezza(api, portType);
			boolean headerDuplicati = false;
			if(rest) {
				headerDuplicati = isProfiloSicurezzaMessaggioConHeaderDuplicati(api, portType);
			}
			boolean requestCalcolatoSuInfoFruizioni = fruizioni;
						
			// Claims
			if(rest) {
				String idProperty = (fruizioni ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				if(profiloSicurezzaMessaggioRestJwtClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtClaimsItemValue.getValue()!=null) {
					Properties claims = PropertiesUtilities.convertTextToProperties(profiloSicurezzaMessaggioRestJwtClaimsItemValue.getValue());
					checkClaims(claims, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL, requestCalcolatoSuInfoFruizioni, digest, corniceSicurezza);
				}
			}
		
			// Header Duplicati
			if(rest && headerDuplicati) {
				String idProperty = (fruizioni ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				if(profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue.getValue()!=null) {
					Properties claims = PropertiesUtilities.convertTextToProperties(profiloSicurezzaMessaggioRestJwtAuthorizationClaimsItemValue.getValue());
					checkClaims(claims, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL, requestCalcolatoSuInfoFruizioni, digest, corniceSicurezza);
				}
				
				idProperty = (fruizioni ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RICHIESTA_ID : ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RISPOSTA_ID);
				StringProperty profiloSicurezzaMessaggioRestJwtModiClaimsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idProperty);
				if(profiloSicurezzaMessaggioRestJwtModiClaimsItemValue!=null && profiloSicurezzaMessaggioRestJwtModiClaimsItemValue.getValue()!=null) {
					Properties claims = PropertiesUtilities.convertTextToProperties(profiloSicurezzaMessaggioRestJwtModiClaimsItemValue.getValue());
					checkClaims(claims, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL.
							replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI()), 
							requestCalcolatoSuInfoFruizioni, digest, corniceSicurezza);
				}
			}
			
			
			// X5U URL
			if(rest) {
				String idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X5U_URL_ID;
				if(idUrl!=null) {
					AbstractConsoleItem<?> profiloSicurezzaMessaggioRestUrlItem = 	
							ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
									idUrl
									);
					if(profiloSicurezzaMessaggioRestUrlItem!=null) {
						
						StringProperty profiloSicurezzaMessaggioRestUrlItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idUrl);
						if(profiloSicurezzaMessaggioRestUrlItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioRestUrlItemValue.getValue())) {
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
				if(profiloSicurezzaMessaggioAudienceItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceItemValue.getValue())) {
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
					if(profiloSicurezzaMessaggioAudienceItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceItemValue.getValue())) {
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
				if(profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioCorniceSicurezzaCodiceEnteItemValue.getValue())) {
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
				if(profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioCorniceSicurezzaUserIdItemValue.getValue())) {
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
				if(profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioCorniceSicurezzaIPUserItemValue.getValue())) {
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
					if(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
									replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI())
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
					if(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
									replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI())
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
					if(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue())) {
						try {
							InputValidationUtils.validateTextAreaInput(profiloSicurezzaMessaggioAudienceIntegrityItemValue.getValue(), 
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL.
									replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI())
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
				if(keystorePathItemValue.getValue()!=null && !"".equals(keystorePathItemValue.getValue())) {
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
				if(truststorePathItemValue.getValue()!=null && !"".equals(truststorePathItemValue.getValue())) {
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
				if(truststoreCRLPathItemValue.getValue()!=null && !"".equals(truststoreCRLPathItemValue.getValue())) {
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
				if(truststorePathItemValue.getValue()!=null && !"".equals(truststorePathItemValue.getValue())) {
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
				if(truststoreCRLPathItemValue.getValue()!=null && !"".equals(truststoreCRLPathItemValue.getValue())) {
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
	
	private void checkClaims(Properties claims, String elemento, boolean request, boolean digest, boolean corniceSicurezza) throws ProtocolException {
		List<String> denyClaims = null;
		String claimNameClientId = null;
		try {
			denyClaims = this.modiProperties.getUsedRestSecurityClaims(request, digest, corniceSicurezza);
			claimNameClientId = this.modiProperties.getRestSecurityTokenClaimsClientIdHeader();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(claims!=null && !claims.isEmpty()) {
			for (Object oClaim : claims.keySet()) {
				if(oClaim !=null && oClaim instanceof String) {
					String claim = (String) oClaim;
					String value = claims.getProperty(claim);
					
					if(value!=null &&  DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(value.trim())) {
						if(claim.equalsIgnoreCase(claimNameClientId) || 
								(claim.equalsIgnoreCase(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER) && !corniceSicurezza) ||
								(claim.equalsIgnoreCase(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT) && !corniceSicurezza)
							) {
							continue;
						}
						else {
							throw new ProtocolException("Keyword '"+DynamicHelperCostanti.NOT_GENERATE+"' non utilizzabile nel claim '"+claim+"', indicato nel campo "+elemento+"");
						}
					}
					if(denyClaims.contains(claim) || denyClaims.contains(claim.toLowerCase())) {
						throw new ProtocolException("Il claim '"+claim+"', indicato nel campo "+elemento+", non può essere configurato");
					}
					if(value==null || StringUtils.isEmpty(value)) {
						throw new ProtocolException("Claim '"+claim+"', indicato nel campo "+elemento+", non valorizzato");
					}
				}
			}
		}
	}
	
	private boolean isMascheraGestioneFruizioneOrErogazione(IConsoleHelper consoleHelper) {
		boolean gestioneFruitori = isMascheraGestioneFruizione(consoleHelper);
		boolean gestioneErogatori = isMascheraGestioneErogazione(consoleHelper);
		if(!gestioneErogatori && !gestioneFruitori) {
			return false;
		}
		return true;
	}
	
	private boolean isMascheraGestioneErogazione(IConsoleHelper consoleHelper) {
		Object obj = consoleHelper.getSession().getAttribute(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE);
		String tipologia = null;
		if(obj != null) {
			tipologia = (String) obj;
		} else {
			try {
				String p = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM);
				if(p!=null) {
					tipologia = p;
				}
			}catch(Exception e) {}
		}
		if(tipologia!=null) {
			if(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				return true;
			}
		}
		return false;
	}
	private boolean isMascheraGestioneFruizione(IConsoleHelper consoleHelper) {
		Object obj = consoleHelper.getSession().getAttribute(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE);
		String tipologia = null;
		if(obj != null) {
			tipologia = (String) obj;
		} else {
			try {
				String p = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM);
				if(p!=null) {
					tipologia = p;
				}
			}catch(Exception e) {}
		}
		if(tipologia!=null) {
			if(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				return true;
			}
		}
		return false;
	}
	
	
	/*** UTILITIES */
		
	private void addProfiloInterazione(ConsoleConfiguration configuration, boolean rest, String httpMethod) throws ProtocolException {
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_ID, 
				ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_LABEL));
		
		ModIProfiliInterazioneRESTConfig config = null;
		if(rest) {
			config = new ModIProfiliInterazioneRESTConfig(this.modiProperties, httpMethod, null);
		}
	
		StringConsoleItem profiloInterazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL);
		boolean addBloccante = true;
		boolean addNonBloccantePush = true;
		boolean addNonBloccantePull = true;
		if(rest) {
			addBloccante = config.isCompatibileBloccante();
			addNonBloccantePush = config.isCompatibileNonBloccantePush();
			addNonBloccantePull = config.isCompatibileNonBloccantePull();
		}
		if(rest) {
			profiloInterazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_CRUD,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		}
		if(addBloccante) {
			profiloInterazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_BLOCCANTE,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
		}
		if(addNonBloccantePush || addNonBloccantePull) {
			profiloInterazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_NON_BLOCCANTE,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
		}
		profiloInterazioneItem.setDefaultValue(rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE);
		profiloInterazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneItem);
		
		StringConsoleItem profiloInterazioneItemReadOnly = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID_INUSE_READONLY, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL);
		configuration.addConsoleItem(profiloInterazioneItemReadOnly);
		
		StringConsoleItem profiloInterazioneAsincronaItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL);
		profiloInterazioneAsincronaItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE);
		profiloInterazioneAsincronaItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaItem);
		
		StringConsoleItem profiloInterazioneAsincronaItemReadOnly = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID_INUSE_READONLY, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL);
		configuration.addConsoleItem(profiloInterazioneAsincronaItemReadOnly);
		
		StringConsoleItem profiloInterazioneAsincronaRelazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL);
		profiloInterazioneAsincronaRelazioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE);
		profiloInterazioneAsincronaRelazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaRelazioneItem);
		
		StringConsoleItem profiloInterazioneAsincronaRelazioneItemReadOnly = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID_INUSE_READONLY, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL);
		configuration.addConsoleItem(profiloInterazioneAsincronaRelazioneItemReadOnly);
		
		StringConsoleItem profiloInterazioneAsincronaCorrelataApiItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID, 
				"");
		profiloInterazioneAsincronaCorrelataApiItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		profiloInterazioneAsincronaCorrelataApiItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaCorrelataApiItem);
		
		StringConsoleItem profiloInterazioneAsincronaCorrelataServizioItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID, 
				"");
		profiloInterazioneAsincronaCorrelataServizioItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		profiloInterazioneAsincronaCorrelataServizioItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaCorrelataServizioItem);
		
		StringConsoleItem profiloInterazioneAsincronaCorrelataAzioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID, 
				"");
		profiloInterazioneAsincronaCorrelataAzioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		profiloInterazioneAsincronaCorrelataAzioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaCorrelataAzioneItem);
		
	}
	
	private void updateProfiloInterazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties,
			IRegistryReader registryReader, IDAccordo idAccordo, String idPortType, String idAzione, boolean rest, String httpMethod) throws ProtocolException {
		
		AbstractConsoleItem<?> profiloInterazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaRelazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaCorrelataApiItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaCorrelataServizioItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaCorrelataAzioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID);
		
		ModIProfiliInterazioneRESTConfig config = null;
		boolean inUse = false;
		if(rest) {
			Resource resource = null;
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				try {
					IDResource id = new IDResource();
					id.setIdAccordo(idAccordo);
					id.setNome(idAzione);
					resource = registryReader.getResourceAccordo(id);
					
					inUse = registryReader.inUso(id);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(), e);
				}
			}
			config = new ModIProfiliInterazioneRESTConfig(this.modiProperties, httpMethod, resource);
		}
		else {
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				try {
					IDPortType idPT = new IDPortType();
					idPT.setIdAccordo(idAccordo);
					idPT.setNome(idPortType);
					
					IDPortTypeAzione id = new IDPortTypeAzione();
					id.setIdPortType(idPT);
					id.setNome(idAzione);
					
					inUse = registryReader.inUso(id);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(), e);
				}
			}
		}
		
		boolean addBloccante = true;
		boolean addNonBloccantePush = true;
		boolean addNonBloccantePull = true;
		if(rest) {
			addBloccante = config.isCompatibileBloccante();
			addNonBloccantePush = config.isCompatibileNonBloccantePush();
			addNonBloccantePull = config.isCompatibileNonBloccantePull();
		}
		
		boolean allHidden = true;
		StringProperty profiloInterazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
		String profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
		if(profiloInterazioneItemValue!=null && profiloInterazioneItemValue.getValue()!=null && !"".equals(profiloInterazioneItemValue.getValue())) {
			profiloInterazione = profiloInterazioneItemValue.getValue();
		}	
		
		if(!addBloccante) {
			profiloInterazioneItem.removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_BLOCCANTE);
		}
		if(!addNonBloccantePush && !addNonBloccantePull) {
			profiloInterazioneItem.removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_NON_BLOCCANTE);
		}
		
		if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE.equals(profiloInterazione)) {
			if(!addBloccante) {
				profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
			}
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(profiloInterazione)) {
			if(!addNonBloccantePush && !addNonBloccantePull) {
				profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
			}
		}
		
		if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(profiloInterazione)) {
			
			allHidden = false;
			
			profiloInterazioneAsincronaItem.setType(ConsoleItemType.SELECT);
			if(addNonBloccantePush) {
				((StringConsoleItem)profiloInterazioneAsincronaItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL_PUSH,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH);
			}
			if(addNonBloccantePull) {
				((StringConsoleItem)profiloInterazioneAsincronaItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL_PULL,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL);
			}
			
			StringProperty profiloInterazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
			String interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE;
			if(profiloInterazioneAsincronaItemValue!=null && profiloInterazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloInterazioneAsincronaItemValue.getValue())) {
				interazioneMode = profiloInterazioneAsincronaItemValue.getValue();
				// verifico compatibilita
				if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode)) {
					if(!addNonBloccantePush) {
						interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL;
					}
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(interazioneMode)) {
					if(!addNonBloccantePull) {
						interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH;
					}
				}
			}		
			else {
				// verifico compatibilita default
				if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode)) {
					if(!addNonBloccantePush) {
						interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL;
					}
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(interazioneMode)) {
					if(!addNonBloccantePull) {
						interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH;
					}
				}
				if(profiloInterazioneAsincronaItemValue!=null) {
					profiloInterazioneAsincronaItemValue.setValue(interazioneMode); // imposto il default
				}
			}
			boolean push = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode);
			
			boolean addRichiesta = true;
			boolean addRichiestaStato = true;
			boolean addRisposta = true;
			if(push) {
				addRichiestaStato = false;
				if(rest) {
					addRichiesta = config.isCompatibileNonBloccantePushRequest();
					addRisposta = config.isCompatibileNonBloccantePushResponse();
				}
			}
			else {
				if(rest) {
					addRichiesta = config.isCompatibileNonBloccantePullRequest();
					addRichiestaStato = config.isCompatibileNonBloccantePullRequestState();
					addRisposta = config.isCompatibileNonBloccantePullResponse();
				}
			}
			
			profiloInterazioneAsincronaRelazioneItem.setType(ConsoleItemType.SELECT);
			if(addRichiesta) {
				((StringConsoleItem)profiloInterazioneAsincronaRelazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA);
			}
			if(addRichiestaStato) {
				((StringConsoleItem)profiloInterazioneAsincronaRelazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA_STATO,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO);
			}
			if(addRisposta) {
				((StringConsoleItem)profiloInterazioneAsincronaRelazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RISPOSTA,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
			}
			
			StringProperty profiloRelazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
			String relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE;
			if(profiloRelazioneAsincronaItemValue!=null && profiloRelazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloRelazioneAsincronaItemValue.getValue())) {
				relazioneMode = profiloRelazioneAsincronaItemValue.getValue();
			}	
			if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(relazioneMode) && !addRichiesta) {
				if(addRisposta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA;
				}
				else if(addRichiestaStato) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO;
				}
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(relazioneMode) && !addRichiestaStato) {
				if(addRichiesta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA;
				}
				else if(addRisposta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA;
				}
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(relazioneMode) && !addRisposta) {
				if(addRichiesta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA;
				}
				else if(addRichiestaStato) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO;
				}
			}
			
			boolean request = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(relazioneMode);			
			if(request) {
				profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.HIDDEN);
				profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
				profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.HIDDEN);
			}
			else {
				try {
										
					if(push) {
						// *** PUSH ***
						
						profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.SELECT);
						((StringConsoleItem)profiloInterazioneAsincronaCorrelataApiItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
						profiloInterazioneAsincronaCorrelataApiItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_API_LABEL);
												
						profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
						profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.HIDDEN);
												
						// listo le altre API
						List<IDAccordo> list = null;
						try {
							FiltroRicercaAccordi filtro = new FiltroRicercaAccordi();
							filtro.setServiceBinding(idPortType!=null ? ServiceBinding.SOAP : ServiceBinding.REST);
							filtro.setSoggetto(new IDSoggetto(this.protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault(), null));
							list = registryReader.findIdAccordiServizioParteComune(filtro);
						}catch(RegistryNotFound notFound) {}
						if(list!=null && !list.isEmpty()) {
							for (IDAccordo idAccordoTrovato : list) {
								String uri = IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoTrovato);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataApiItem).addLabelValue(NamingUtils.getLabelAccordoServizioParteComune(idAccordoTrovato),uri);
							}
						}
						
						StringProperty apiValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID);
						if(apiValue!=null && apiValue.getValue()!=null && !"".equals(apiValue.getValue()) &&
								!ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(apiValue.getValue())) {
							String uriAPI = apiValue.getValue();
							IDAccordo idAccordoSelected = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAPI);
						
							// Utility
							AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
							asps.setTipoSoggettoErogatore(idAccordoSelected.getSoggettoReferente().getTipo());
							asps.setNomeSoggettoErogatore(idAccordoSelected.getSoggettoReferente().getNome());
							asps.setNome(idAccordoSelected.getNome());
							asps.setVersione(idAccordoSelected.getVersione());
							AccordoServizioParteComune aspcNormale = registryReader.getAccordoServizioParteComune(idAccordoSelected,false);
							AccordoServizioParteComuneSintetico aspcSelected = new AccordoServizioParteComuneSintetico(aspcNormale);
							boolean addTrattinoSelezioneNonEffettuata = true;
							boolean throwException = true;
							
							AccordoServizioParteComune aspc = null;
							try {
								aspc = registryReader.getAccordoServizioParteComune(idAccordoSelected);
							}catch(RegistryNotFound notFound) {}
							
							if(idPortType!=null) {
								// SOAP
								profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.SELECT);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataServizioItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
								profiloInterazioneAsincronaCorrelataServizioItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_SERVIZIO_LABEL);
								
								if(aspc!=null && aspc.sizePortTypeList()>0) {
									for (PortType pt : aspc.getPortTypeList()) {
										if(pt.getNome().equals(idPortType)) {
											continue;
										}
										((StringConsoleItem)profiloInterazioneAsincronaCorrelataServizioItem).addLabelValue(pt.getNome(), pt.getNome());
									}
								}
								
								StringProperty ptValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID);
								if(ptValue!=null && ptValue.getValue()!=null && !"".equals(ptValue.getValue()) &&
										!ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(ptValue.getValue())) {
								
									profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.SELECT);
									((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
									profiloInterazioneAsincronaCorrelataAzioneItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_AZIONE_LABEL);
																		
									asps.setPortType(ptValue.getValue());
																		
									// listo le altre azioni
									List<String> filtraAzioniUtilizzate = new ArrayList<>();
									filtraAzioniUtilizzate.add(idAzione);
									Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspcSelected, addTrattinoSelezioneNonEffettuata, throwException, 
											filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, this.log); 
									if(azioni!=null && !azioni.isEmpty()) {
										for (String azioneId : azioni.keySet()) {
											String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(aspc, ptValue.getValue(), azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
											if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
												continue;
											}
											String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(aspc, ptValue.getValue(), azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
											if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(tmpRuolo)) {
												continue;
											}
											String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(aspc, ptValue.getValue(), azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
											if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
												continue;
											}
											String azioneLabel = azioni.get(azioneId);
											((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(azioneLabel,azioneId);
										}
									}
								}
							}
							else {
								// REST
								
								profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.SELECT);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
								profiloInterazioneAsincronaCorrelataAzioneItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_RISORSA_LABEL);
								
								// listo le altre azioni
								List<String> filtraAzioniUtilizzate = new ArrayList<>();
								filtraAzioniUtilizzate.add(idAzione);
								Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspcSelected, addTrattinoSelezioneNonEffettuata, throwException, 
										filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, this.log); 
								if(azioni!=null && !azioni.isEmpty()) {
									for (String azioneId : azioni.keySet()) {
										String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(aspc, null, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
										if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
											continue;
										}
										String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(aspc, null, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
										if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(tmpRuolo)) {
											continue;
										}
										String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(aspc, null, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
										if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
											continue;
										}
										String azioneLabel = azioni.get(azioneId);
										((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(azioneLabel,azioneId);
									}
								}
							}
						}		
					}
					else {
						// *** PULL ***
						
						profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.HIDDEN);
						profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
						
						profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.SELECT);
						((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
						profiloInterazioneAsincronaCorrelataAzioneItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_LABEL);
						
						// Utility
						AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
						asps.setTipoSoggettoErogatore(idAccordo.getSoggettoReferente().getTipo());
						asps.setNomeSoggettoErogatore(idAccordo.getSoggettoReferente().getNome());
						asps.setNome(idAccordo.getNome());
						asps.setVersione(idAccordo.getVersione());
						asps.setPortType(idPortType);
						AccordoServizioParteComune aspcNormale = registryReader.getAccordoServizioParteComune(idAccordo,false);
						AccordoServizioParteComuneSintetico aspc = new AccordoServizioParteComuneSintetico(aspcNormale);
						boolean addTrattinoSelezioneNonEffettuata = true;
						boolean throwException = true;
						
						// listo le altre azioni
						List<String> filtraAzioniUtilizzate = new ArrayList<>();
						filtraAzioniUtilizzate.add(idAzione);
						Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspc, addTrattinoSelezioneNonEffettuata, throwException, 
								filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, this.log); 
						if(azioni!=null && !azioni.isEmpty()) {
							for (String azioneId : azioni.keySet()) {
								String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(aspcNormale, idPortType, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
								if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
									continue;
								}
								String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(aspcNormale, idPortType, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
								if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(tmpRuolo)) {
									continue;
								}
								String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(aspcNormale, idPortType, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
								if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
									continue;
								}
								String azioneLabel = azioni.get(azioneId);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(azioneLabel,azioneId);
							}
						}

					}
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
		}
		
		if(allHidden) {
			profiloInterazioneAsincronaItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaRelazioneItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.HIDDEN);
		}
		else if(inUse) {
			
			setLabelInUse(consoleConfiguration, properties, profiloInterazioneItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID_INUSE_READONLY);
			
			setLabelInUse(consoleConfiguration, properties, profiloInterazioneAsincronaItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID_INUSE_READONLY);
			
			setLabelInUse(consoleConfiguration, properties, profiloInterazioneAsincronaRelazioneItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID_INUSE_READONLY);

		}
		
	}

	private void setLabelInUse(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			AbstractConsoleItem<?> item, String id, String idReadOnly) throws ProtocolException {
		if(!ConsoleItemType.HIDDEN.equals(item.getType())){
			
			StringProperty itemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
						
			item.setType(ConsoleItemType.HIDDEN);
			AbstractConsoleItem<?> itemReadOnly = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idReadOnly);
			itemReadOnly.setType(ConsoleItemType.TEXT);
			String label = null;
			String labelDefault = null;
			if(item instanceof StringConsoleItem) {
				StringConsoleItem sci = (StringConsoleItem) item;
				TreeMap<String,String> map = sci.getMapLabelValues();
				if(map!=null && !map.isEmpty()) {
					for (String l : map.keySet()) {
						String v = map.get(l);
						if(v!=null && v.equals(itemValue.getValue())) {
							label = l;
						}
						if(v!=null && v.equals(item.getDefaultValue())) {
							labelDefault = l;
						}
					}
				}
			}

			StringProperty itemValueReadOnly = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idReadOnly);
			if(label!=null) {
				itemValueReadOnly.setValue(label);
			}
			else {
				itemValueReadOnly.setValue(labelDefault);
			}
		}
	}
		
	private void validateProfiloInterazione(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IRegistryReader registryReader, IDAccordo idAccordo, String idPortType, String idAzione, boolean rest) throws ProtocolException {
	
		StringProperty profiloInterazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
		String profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
		if(profiloInterazioneItemValue!=null && profiloInterazioneItemValue.getValue()!=null && !"".equals(profiloInterazioneItemValue.getValue())) {
			profiloInterazione = profiloInterazioneItemValue.getValue();
		}	
		if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(profiloInterazione)) {
			
			StringProperty profiloInterazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
			String interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE;
			if(profiloInterazioneAsincronaItemValue!=null && profiloInterazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloInterazioneAsincronaItemValue.getValue())) {
				interazioneMode = profiloInterazioneAsincronaItemValue.getValue();
			}		
			boolean push = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode);
			
			StringProperty profiloRelazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
			String relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE;
			if(profiloRelazioneAsincronaItemValue!=null && profiloRelazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloRelazioneAsincronaItemValue.getValue())) {
				relazioneMode = profiloRelazioneAsincronaItemValue.getValue();
			}		
			boolean request = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(relazioneMode);
			
			if(!request) {
				
				String labelRelazione = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RISPOSTA;
				if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(relazioneMode)) {
					labelRelazione = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA_STATO;	
				}
				
				if(push) {
					// *** PUSH ***
					
					StringProperty apiItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID);
					if(apiItemValue==null || apiItemValue.getValue()==null || "".equals(apiItemValue.getValue()) ||
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(apiItemValue.getValue())) {
						throw new ProtocolException("Il profilo non bloccante 'PUSH', relazione '"+labelRelazione+"', richiede che sia perfezionata una correlazione verso una API che implementa il servizio di risposta");
					}
					
					if(idPortType!=null) {
						StringProperty ptItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID);
						if(ptItemValue==null || ptItemValue.getValue()==null || "".equals(ptItemValue.getValue()) ||
								ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(ptItemValue.getValue())) {
							throw new ProtocolException("Il profilo non bloccante 'PUSH', relazione '"+labelRelazione+"', richiede che sia perfezionata una correlazione verso un servizio di risposta");
						}
					}
					
					StringProperty azioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID);
					if(azioneItemValue==null || azioneItemValue.getValue()==null || "".equals(azioneItemValue.getValue()) ||
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(azioneItemValue.getValue())) {
						String az = null;
						if(idPortType!=null) {
							az = "un'azione";
						}
						else {
							az = "una risorsa";
						}
						throw new ProtocolException("Il profilo non bloccante 'PUSH', relazione '"+labelRelazione+"', richiede che sia perfezionata una correlazione verso "+az+" con relazione 'Richiesta'");
					}
				}
				else {
					// *** PULL ***
					StringProperty azioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID);
					if(azioneItemValue==null || azioneItemValue.getValue()==null || "".equals(azioneItemValue.getValue()) ||
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(azioneItemValue.getValue())) {
						String az = null;
						if(idPortType!=null) {
							az = "un'azione";
						}
						else {
							az = "una risorsa";
						}
						throw new ProtocolException("Il profilo non bloccante 'PULL', relazione '"+labelRelazione+"', richiede che sia perfezionata una correlazione verso "+az+" con relazione 'Richiesta'");
					}
				}
				
			}
		}
		
	}
	
	private void updateProfiloSicurezzaCanale(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IRegistryReader registryReader) throws ProtocolException {
				
		AbstractConsoleItem<?> profiloSicurezzaCanaleItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_ID);
		StringProperty sicurezzaCanaleItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_ID);
		
		String value = sicurezzaCanaleItemValue!=null ? sicurezzaCanaleItemValue.getValue() : null;
		if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01.equals(value)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02.equals(value)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NOTE);
		}

	}
	
	private void addProfiloSicurezzaMessaggio(ConsoleConfiguration configuration, boolean rest, boolean action) throws ProtocolException {
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_ID, 
				ModIConsoleCostanti.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL));
		
		String labelSelezione = ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL;
		
		boolean corniceSicurezza = this.modiProperties.isSicurezzaMessaggio_corniceSicurezza_enabled();
		
		if(action) {
			
			labelSelezione = "";
			
			StringConsoleItem modeItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_ID, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL);
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_LABEL_RIDEFINISCI,
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
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01);
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02);
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301);
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302);
		}
		else {
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01);
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02);
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NEW,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301);
			profiloSicurezzaMessaggioItem.addLabelValue(this.modiProperties.isModIVersioneBozza() ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NEW,
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
		//if(corniceSicurezza) {
		profiloSicurezzaMessaggioItem.setReloadOnChange(true);
		//}
		configuration.addConsoleItem(profiloSicurezzaMessaggioItem);
		
		if(rest) {
			
			// !! Nel caso di 2 header, quello integrity viene prodotto solo se c'è un payload o uno degli header indicati da firmare.
			StringConsoleItem profiloSicurezzaMessaggioHeaderItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL);
			profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA.
					replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()),
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA);
			profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION);
			if(isProfiloSicurezza03(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
				profiloSicurezzaMessaggioHeaderItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE);
				
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA.
						replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()),
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA);
				profiloSicurezzaMessaggioHeaderItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.
						replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()),
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE);
			}
			else if(isProfiloSicurezza01(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) || isProfiloSicurezza02(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
				profiloSicurezzaMessaggioHeaderItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE);
			}
			else {
				profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.HIDDEN);
			}
			configuration.addConsoleItem(profiloSicurezzaMessaggioHeaderItem);
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
				!isProfiloSicurezza03(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE) ) {
			profiloSicurezzaMessaggioConfigurazioneItem.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaMessaggioConfigurazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaMessaggioConfigurazioneItem);
				
		BooleanConsoleItem profiloSicurezzaMessaggioRequestDigest = (BooleanConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
				ConsoleItemType.CHECKBOX,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL);
		if(!isProfiloSicurezza03(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE)) {
			profiloSicurezzaMessaggioRequestDigest.setType(ConsoleItemType.HIDDEN);
		}
		profiloSicurezzaMessaggioRequestDigest.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_DEFAULT);
		ConsoleItemInfo c_digest = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL);
		c_digest.setHeaderBody(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_REST_INFO_HEADER : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_SOAP_INFO_HEADER );
		profiloSicurezzaMessaggioRequestDigest.setInfo(c_digest);
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
			ConsoleItemInfo c_corniceSicurezza = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL);
			c_corniceSicurezza.setHeaderBody(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_HEADER : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_HEADER );
			c_corniceSicurezza.setListBody(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_VALORI : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_VALORI );
			profiloSicurezzaMessaggioCorniceSicurezza.setInfo(c_corniceSicurezza);
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
		
	private boolean isProfiloSicurezza01(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(value);
	}
	
	private boolean isProfiloSicurezza02(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(value);
	}
	
	private boolean isProfiloSicurezza03(String value) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(value)
				||
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(value);
	}
	
	private ConsoleItemInfo buildConsoleItemInfoSicurezzaContentType(String label) {
		
		try {
			ConsoleItemInfo c = new ConsoleItemInfo(label);
			
			c.setHeaderBody(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_O_RISPOSTA_CONTENT_TYPE_MODE_ID_INFO_CONTENT_TYPE);
			
			c.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI);
			
			return c;
			
		}catch(Throwable t) {
			try {
				LoggerWrapperFactory.getLogger("govwayConsole.core").error(t.getMessage(),t);
			}catch(Throwable tInternal) {
				System.err.println("ERRORE: "+t.getMessage());
				t.printStackTrace(System.err);
			}
			return null;
		}
		
	}
	
	private void updateProfiloSicurezzaMessaggio(ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, boolean rest, boolean action) throws ProtocolException {
		
		boolean corniceSicurezza = this.modiProperties.isSicurezzaMessaggio_corniceSicurezza_enabled();
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
				profiloSicurezzaMessaggioItem.setType(ConsoleItemType.HIDDEN);
				profiloSicurezzaMessaggioItemValue.setValue(null);
			}
			
		}
		else {
		
			profiloSicurezzaMessaggioItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
				
			profiloSicurezzaMessaggioItemModifyNote = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
						
		}
		if(profiloSicurezzaMessaggioItemModifyNote!=null) {
			String v = profiloSicurezzaMessaggioItemValue!=null ? profiloSicurezzaMessaggioItemValue.getValue() : null;
			if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(v)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(v)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(v)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NOTE);
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(v)) {
				profiloSicurezzaMessaggioItemModifyNote.setNote(rest ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NOTE : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NOTE);
			}
		}
		
		boolean isSicurezza01 = false;
		boolean isSicurezza02 = false;
		boolean isSicurezza03 = false;
		if(profiloSicurezzaMessaggioItemValue!=null && profiloSicurezzaMessaggioItemValue.getValue()!=null) {
			isSicurezza01 = isProfiloSicurezza01(profiloSicurezzaMessaggioItemValue.getValue());
			isSicurezza02 = isProfiloSicurezza02(profiloSicurezzaMessaggioItemValue.getValue());
			isSicurezza03 = isProfiloSicurezza03(profiloSicurezzaMessaggioItemValue.getValue());
		}
		
		if(rest) {
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHeaderItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID);
			if(isSicurezza01 || isSicurezza02 || isSicurezza03) {
				profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.SELECT);
				StringProperty profiloSicurezzaMessaggioHeaderItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID);
				String postBackElementName = null;
				try {
					postBackElementName = consoleHelper.getPostBackElementName();
				}catch(Exception e) {
					// ignoro
					//throw new ProtocolException(e.getMessage(),e);
				}
				if(profiloSicurezzaMessaggioHeaderItemValue!=null && ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID.equals(postBackElementName)){
					profiloSicurezzaMessaggioHeaderItemValue.setValue(null);
				}
				if(isSicurezza03) {
					if(profiloSicurezzaMessaggioHeaderItemValue!=null &&
						(profiloSicurezzaMessaggioHeaderItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaMessaggioHeaderItemValue.getValue()))
					) {
						profiloSicurezzaMessaggioHeaderItemValue.setValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE);
					}
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE);
					
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA.
							replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()),
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA);
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.
							replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()),
							ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE);
				}
				else if(isSicurezza01 || isSicurezza02) {
					if(profiloSicurezzaMessaggioHeaderItemValue!=null &&
							(profiloSicurezzaMessaggioHeaderItemValue.getValue()==null || StringUtils.isEmpty(profiloSicurezzaMessaggioHeaderItemValue.getValue()))
						) {
						profiloSicurezzaMessaggioHeaderItemValue.setValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE);
					}
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE);
					
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA.
							replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()));
					((StringConsoleItem)profiloSicurezzaMessaggioHeaderItem).removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.
							replace(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, this.modiProperties.getRestSecurityTokenHeaderModI()));
				}
			}
			else {
				profiloSicurezzaMessaggioHeaderItem.setType(ConsoleItemType.HIDDEN);
				if(!ridefinito) {
					StringProperty profiloSicurezzaMessaggioHeaderItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID);
					profiloSicurezzaMessaggioHeaderItemValue.setValue(null);
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
				
				if(profiloSicurezzaMessaggioConfigurazioneItemValue!=null) {
					if(profiloSicurezzaMessaggioConfigurazioneItemValue.getValue()!=null && !StringUtils.isEmpty(profiloSicurezzaMessaggioConfigurazioneItemValue.getValue())) {
						String v = profiloSicurezzaMessaggioConfigurazioneItemValue.getValue();
						if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(v) ||
								ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS.equals(v) ||
								ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(v) ) {
							profiloSicurezzaMessaggioConfigurazioneItemValue.setValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE);
						}
					}
				}
			}
		}
		
		// parametri che serviranno a nascondere requestDigest e infoUtente
		boolean sicurezzaSullaRichiesta = false;
		boolean sicurezzaSullaRisposta = false;
		
		
		boolean configurazionePersonalizzata = false;
		if(isSicurezza01 || isSicurezza02 || isSicurezza03) {
			profiloSicurezzaConfigurazioneItem.setType(ConsoleItemType.SELECT);
			
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
			if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO.equals(secValue)) {
				sicurezzaSullaRichiesta = true;
			}
			else if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO.equals(secValue)) {
				sicurezzaSullaRichiesta = true; // per una qualche richiesta sarà abilitato
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
			if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO.equals(secValue)) {
				sicurezzaSullaRisposta = true;
			}
			else if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO.equals(secValue)) {
				sicurezzaSullaRisposta = true; // per una qualche risposta sarà abilitato
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
				profiloSicurezzaMessaggioCorniceSicurezzaItem.setType(ConsoleItemType.HIDDEN);
				profiloSicurezzaMessaggioCorniceSicurezzaItemValue.setValue(null);
			}
		}
		
		AbstractConsoleItem<?> profiloSicurezzaMessaggioRequestDigestItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID);
		BooleanProperty profiloSicurezzaMessaggioRequestDigestItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID);
		
		if(isSicurezza03 && sicurezzaSullaRichiesta && sicurezzaSullaRisposta) {
			profiloSicurezzaMessaggioRequestDigestItem.setType(ConsoleItemType.CHECKBOX);
		}
		else {
			profiloSicurezzaMessaggioRequestDigestItem.setType(ConsoleItemType.HIDDEN);
			profiloSicurezzaMessaggioRequestDigestItemValue.setValue(null);
		}
	}
	
	private void validateProfiloSicurezzaMessaggio(ConsoleConfiguration consoleConfiguration, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, boolean rest, boolean action) throws ProtocolException {
		
		if(rest) {
			StringProperty profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
			if(profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue!=null && profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.getValue()!=null && 
					StringUtils.isNotEmpty(profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.getValue())) {
				String v = profiloSicurezzaRispostaConfigurazioneReturnCodeItemValue.getValue();
				List<String> codici = new ArrayList<String>();
				if(v.contains(",")) {
					String [] tmp = v.split(",");
					for (int i = 0; i < tmp.length; i++) {
						codici.add(tmp[i].trim());
					}
				}
				else {
					codici.add(v.trim());
				}
				if(codici!=null && !codici.isEmpty()) {
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
								int codiceInt = Integer.valueOf(codice);
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
	
	private List<String> getProfiloSicurezzaMessaggio(AccordoServizioParteComune api, String portType) {
		return this._getPropertySicurezzaMessaggio(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID, api, portType, false);
	}
	private boolean isProfiloSicurezzaMessaggioConIntegrita(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getProfiloSicurezzaMessaggio(api, portType);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	private boolean isProfiloSicurezzaMessaggioCorniceSicurezza(AccordoServizioParteComune api, String portType) {
		List<String> tmp = this._getPropertySicurezzaMessaggio(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_ID, api, portType, true);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String v : tmp) {
				if(v!=null && "true".equals(v)) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean isProfiloSicurezzaMessaggioConHeaderDuplicati(AccordoServizioParteComune api, String portType) {
		List<String> tmp = _getPropertySicurezzaMessaggio(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA.equals(profiloSicurezzaMessaggio) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	private List<String> _getPropertySicurezzaMessaggio(String propertyName, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		return RegistroServiziUtils.fillPropertyProtocollo(propertyName, api, portType, booleanValue);
	}
	
	private boolean isSicurezzaMessaggioRequired(AccordoServizioParteComune api, String portType) {
		
		List<String> apiValues = this.getProfiloSicurezzaMessaggio(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String apiValue : apiValues) {
				if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(apiValue) ||
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(apiValue)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void addSicurezzaMessaggio(ConsoleConfiguration configuration, boolean rest, boolean fruizione, boolean request, 
			boolean casoSpecialeModificaNomeFruizione, boolean digest, boolean corniceSicurezza, boolean headerDuplicati) throws ProtocolException {
		
		boolean requiredValue = casoSpecialeModificaNomeFruizione ? false : true;
		
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
			addConfigurazioneFirmaRest(configuration, fruizione, request, digest, requiredValue);
		}
		else {
			addConfigurazioneFirmaSoap(configuration, fruizione, request, digest, requiredValue);
		}
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			// truststore per i certificati
			this.addTrustStoreCertificatiConfig_choice(configuration, false);
			
			if(rest) {
				// ssl per le url (x5u)
				this.addTrustStoreSSLConfig_choice(configuration, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE);
			}
		}
		
		if(!fruizione && !request) {
			// keystore
			this.addTrustStoreKeystoreConfig_choice(configuration);
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
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT);
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_RIDEFINISCI,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_RIDEFINISCI);
			modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_DEFAULT_VALUE);
			modeItem.setReloadOnChange(true);
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
				profiloSicurezzaMessaggioAudienceItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_FRUIZIONE_NOTE);
				profiloSicurezzaMessaggioAudienceItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_DEFAULT);
				profiloSicurezzaMessaggioAudienceItem.setReloadOnChange(true);
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
		if(rest && 
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
				info.setHeaderBody(DynamicHelperCostanti.getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(request, corniceSicurezza, 
						this.modiProperties.getUsedRestSecurityClaims(request, digest, corniceSicurezza)));
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
		
		if(corniceSicurezza && fruizione && request) {
			
			SubtitleConsoleItem subtitleItem = (SubtitleConsoleItem) ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_RICHIESTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_RICHIESTA_LABEL);
			subtitleItem.setCloseable(true);
			subtitleItem.setLastItemId(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_ID);
			configuration.addConsoleItem(subtitleItem);
		
			StringConsoleItem modeCodiceEnteItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL);
			((StringConsoleItem)modeCodiceEnteItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
			((StringConsoleItem)modeCodiceEnteItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
			modeCodiceEnteItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_DEFAULT_VALUE);
			modeCodiceEnteItem.setUseDefaultValueForCloseableSection(true);
			modeCodiceEnteItem.setReloadOnChange(true);
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
			((StringConsoleItem)modeUserItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
			((StringConsoleItem)modeUserItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
			modeUserItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_VALUE);
			modeUserItem.setUseDefaultValueForCloseableSection(true);
			modeUserItem.setReloadOnChange(true);
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
			((StringConsoleItem)modeIPUserItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
			((StringConsoleItem)modeIPUserItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
			modeIPUserItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_VALUE);
			modeIPUserItem.setUseDefaultValueForCloseableSection(true);
			modeIPUserItem.setReloadOnChange(true);
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
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI())); 
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
			((StringConsoleItem)jtiItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL_SAME,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_SAME);
			((StringConsoleItem)jtiItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL_DIFFERENT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DIFFERENT);
			jtiItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DEFAULT);
			jtiItem.setUseDefaultValueForCloseableSection(true);
			jtiItem.setReloadOnChange(true);
			configuration.addConsoleItem(jtiItem);
			
			StringConsoleItem jtiAsIdMessaggioItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					request ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RICHIESTA_ID : 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RISPOSTA_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL);
			((StringConsoleItem)jtiAsIdMessaggioItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION);
			((StringConsoleItem)jtiAsIdMessaggioItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL_MODI.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI()),
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
				((StringConsoleItem)audItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_SAME,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
				((StringConsoleItem)audItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_DIFFERENT,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
				audItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT);
				audItem.setUseDefaultValueForCloseableSection(true);
				audItem.setReloadOnChange(true);
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
				infoAuthorization.setHeaderBody(DynamicHelperCostanti.getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(request, corniceSicurezza, 
						this.modiProperties.getUsedRestSecurityClaims(request, digest, corniceSicurezza)));
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
						replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI()));
			modiClaimsItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_NOTE);
			ConsoleItemInfo infoModi = new ConsoleItemInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI()));
			try {
				infoModi.setHeaderBody(DynamicHelperCostanti.getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(request, corniceSicurezza, 
						this.modiProperties.getUsedRestSecurityClaims(request, digest, corniceSicurezza)));
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
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI()));
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
			((StringConsoleItem)jtiItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_AUTHORIZATION,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION);
			((StringConsoleItem)jtiItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_MODI.
					replace(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID, this.modiProperties.getRestSecurityTokenHeaderModI()),
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
			((StringConsoleItem)audItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_SAME,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
			((StringConsoleItem)audItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_DIFFERENT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
			audItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT);
			audItem.setUseDefaultValueForCloseableSection(true);
			audItem.setReloadOnChange(true);
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
		
		
		// TrustStore
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			
			// truststore per i certificati
			this.addTrustStoreConfig_subSection(configuration, false, false);
			
			if(rest) {
			
				// ssl per le url (x5u)
				this.addTrustStoreConfig_subSection(configuration, true, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE);
			
			}
			
		}
		
		
		// KeyStore
		
		if(!fruizione && !request) {
			this.addKeystoreConfig(configuration, true, false, requiredValue);
		}
		
	}
	
	private ConsoleItemInfo buildConsoleItemInfoCorniceSicurezza(String intestazione, boolean rest) {
		
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
			
		}catch(Throwable t) {
			try {
				LoggerWrapperFactory.getLogger("govwayConsole.core").error(t.getMessage(),t);
			}catch(Throwable tInternal) {
				System.err.println("ERRORE: "+t.getMessage());
				t.printStackTrace(System.err);
			}
			return null;
		}
		
	}
	
	private void addConfigurazioneFirmaRest(ConsoleConfiguration configuration, boolean fruizione, boolean request, boolean digest, boolean requiredValue) throws ProtocolException {
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
				profiloSicurezzaMessaggioDigestEncodingItem.setDefaultValue(this.modiProperties.getRestSecurityTokenDigestDefaultEncoding().name());
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			profiloSicurezzaMessaggioDigestEncodingItem.setType(ConsoleItemType.HIDDEN);
			configuration.addConsoleItem(profiloSicurezzaMessaggioDigestEncodingItem);
		}
		
		if(digest) {
			if( (request && fruizione) || (!request && !fruizione) ) {

				StringConsoleItem profiloSicurezzaMessaggioHttpHeadersItem = (StringConsoleItem) 
						ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
						ConsoleItemType.TAGS,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID, 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL);
				//profiloSicurezzaMessaggioHttpHeadersItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_NOTE);
				//profiloSicurezzaMessaggioHttpHeadersItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_DEFAULT_VALUE);
				try {
					profiloSicurezzaMessaggioHttpHeadersItem.setDefaultValue(this.modiProperties.getRestSecurityTokenSignedHeadersAsString());
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				profiloSicurezzaMessaggioHttpHeadersItem.setRequired(true);
				configuration.addConsoleItem(profiloSicurezzaMessaggioHttpHeadersItem);
			}
		}
		
		if(!request) {
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
			profiloSicurezzaMessaggioRifX509AsRequestItem.setReloadOnChange(true);
			configuration.addConsoleItem(profiloSicurezzaMessaggioRifX509AsRequestItem);
		}
		
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
		profiloSicurezzaMessaggioRifX509Item.setReloadOnChange(true);
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
		
		String idUrl = null;
		if(fruizione && request) {
			// Deprecato, spostato su SA
			//idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X5U_URL_ID;
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
			configuration.addConsoleItem(profiloSicurezzaMessaggioX5UItem);
		}

	}
	
	private void addConfigurazioneFirmaSoap(ConsoleConfiguration configuration, boolean fruizione, boolean request, boolean digest, boolean requiredValue) throws ProtocolException {
		
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
			if(this.modiProperties.isModIVersioneBozza()) {
				profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_224,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_224);
			}
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_256,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_256);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_384,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_384);
			profiloSicurezzaMessaggioAlgItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_512,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_512);
			if(this.modiProperties.isModIVersioneBozza()) {
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
		
		
		if(digest) {
			if( (request && fruizione) || (!request && !fruizione) ) {

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
			profiloSicurezzaMessaggioRifX509Item.setReloadOnChange(true);
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
	
	private void updateSicurezzaMessaggio(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, 
			boolean rest, boolean fruizione, boolean request, boolean casoSpecialeModificaNomeFruizione,
			boolean corniceSicurezza, boolean headerDuplicati,
			IConsoleHelper consoleHelper) throws ProtocolException {
		
		boolean requiredValue = casoSpecialeModificaNomeFruizione ? false : true;
		
		if(fruizione && !request) {
			
			String idPropertyAud = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_ID;
			String idPropertyAudValue = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_ID;
			
			BooleanProperty profiloSicurezzaMessaggioRestAudItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAud);
			StringProperty profiloSicurezzaMessaggioRestAudValueItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idPropertyAudValue);
			AbstractConsoleItem<?> profiloSicurezzaMessaggioRestAudValueItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idPropertyAudValue);
			if(profiloSicurezzaMessaggioRestAudValueItem!=null) {
				if(profiloSicurezzaMessaggioRestAudItemValue!=null && profiloSicurezzaMessaggioRestAudItemValue.getValue()!=null && profiloSicurezzaMessaggioRestAudItemValue.getValue()) {
					profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.TEXT_AREA);
				}
				else {
					profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
					profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
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
					show = this.modiProperties.isRestSecurityTokenDigestEncodingChoice();
					if(show && consoleHelper.isModalitaStandard()) {
						show = false;
					}
					if(!show) {
						if(actualValue!=null && !actualValue.equals(this.modiProperties.getRestSecurityTokenDigestDefaultEncoding())) {
							show=true;
						}
					}
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				if(show) {
					profiloSicurezzaMessaggioDigestEncodingItem.setType(ConsoleItemType.SELECT);
				}
				else {
					profiloSicurezzaMessaggioDigestEncodingItem.setType(ConsoleItemType.HIDDEN);
					if(profiloSicurezzaMessaggioDigestEncodingItemValue!=null) {
						profiloSicurezzaMessaggioDigestEncodingItemValue.setValue(null);
					}
				}
				
			}
			
					
			boolean x5uRichiesta = false;
			boolean x5cRichiesta = false;
			StringProperty profiloSicurezzaMessaggioRequestRifX509ItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_ID);
			if(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioRequestRifX509ItemValue.getValue())) {
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
				
				if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE.equals(profiloSicurezzaMessaggioRifX509AsRequestItemValue.getValue())){
					profiloSicurezzaMessaggioResponseX5UItem.setType(ConsoleItemType.MULTI_SELECT);
					((StringConsoleItem)profiloSicurezzaMessaggioResponseX5UItem).setRows(3);
					
					StringProperty profiloSicurezzaMessaggioResponseRifX509ItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_ID);
					if(profiloSicurezzaMessaggioResponseRifX509ItemValue.getValue()!=null && !"".equals(profiloSicurezzaMessaggioResponseRifX509ItemValue.getValue())) {
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
				// Deprecato, spostato su SA
				//idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X5U_URL_ID;
			}
			else if(!fruizione && !request) {
				idUrl = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X5U_URL_ID;
			}
			if(idUrl!=null) {
				AbstractConsoleItem<?> profiloSicurezzaMessaggioX5UItem = 	
						ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idUrl);
				if(profiloSicurezzaMessaggioX5UItem!=null) {
					if(x5url) {
						profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.TEXT_AREA);
						profiloSicurezzaMessaggioX5UItem.setRequired(requiredValue);
					}
					else {
						profiloSicurezzaMessaggioX5UItem.setRequired(false);
						profiloSicurezzaMessaggioX5UItem.setType(ConsoleItemType.HIDDEN);
					}
				}
			}
			
			
			
			AbstractConsoleItem<?> profiloSicurezzaMessaggioHttpHeadersItem = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID
							);
			if(profiloSicurezzaMessaggioHttpHeadersItem!=null) {
				
				StringProperty profiloSicurezzaMessaggioHttpHeadersItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID);
				if(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue()==null || "".equals(profiloSicurezzaMessaggioHttpHeadersItemValue.getValue())) {
					try {
						profiloSicurezzaMessaggioHttpHeadersItemValue.setValue(this.modiProperties.getRestSecurityTokenSignedHeadersAsString());
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
						if(profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue==null || profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue.getValue()==null) {
							profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_DEFAULT);
						}
					}
					else {
						profiloSicurezzaMessaggioRestJtiAsIdMessaggioItem.setType(ConsoleItemType.HIDDEN);
						profiloSicurezzaMessaggioRestJtiAsIdMessaggioItemValue.setValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_DEFAULT);
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
						profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
						profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
						profiloSicurezzaMessaggioRestAudValueItem.setRequired(false);
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
						profiloSicurezzaMessaggioRestAudValueItem.setType(ConsoleItemType.HIDDEN);
						profiloSicurezzaMessaggioRestAudValueItemValue.setValue(null);
						profiloSicurezzaMessaggioRestAudValueItem.setRequired(false);
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
				if(profiloSicurezzaMessaggioSoapHeadersItemValue.getValue()==null || "".equals(profiloSicurezzaMessaggioSoapHeadersItemValue.getValue())) {
					if(consoleHelper.isModalitaStandard()) {
						profiloSicurezzaMessaggioSoapHeadersItem.setType(ConsoleItemType.HIDDEN);
					}
				}
				
			}
		}
		
		// Cornice Sicurezza
		
		if(corniceSicurezza && fruizione && request) {
		
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
				selectCodiceEnteItemValue.setValue(null);
			}
			
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
				userModeItem.getInfo().setListBody(new ArrayList<String>());
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
				selectUserItemValue.setValue(null);
			}
			
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
				ipUserModeItem.getInfo().setListBody(new ArrayList<String>());
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
				selectIPUserItemValue.setValue(null);
			}
			
		}
		
		// TrustStore
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			
			// truststore per i certificati
			this.updateTrustConfig(consoleConfiguration, properties, false, false, requiredValue);
			
			if(rest) {
			
				// ssl per le url (x5u)
				this.updateTrustConfig(consoleConfiguration, properties, true, x5url, requiredValue);
			
			}
		}
		
		// KeyStore
		
		if(!fruizione && !request) {
			
			boolean hideSceltaArchivioFilePath = false;
			boolean addHiddenSubjectIssuer = false;
			this.updateKeystoreConfig(consoleConfiguration, properties, true, 
					hideSceltaArchivioFilePath, addHiddenSubjectIssuer, 
					requiredValue, null);
		}
		
	}
	
	
	
	private void addKeystoreConfig(ConsoleConfiguration configuration, boolean checkRidefinisci, boolean addHiddenSubjectIssuer, boolean requiredValue) throws ProtocolException {
		
		if(checkRidefinisci) {
			BaseConsoleItem subTitleItem = 
					ProtocolPropertiesFactory.newSubTitleItem(
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_ID, 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_LABEL);
			subTitleItem.setType(ConsoleItemType.HIDDEN);
			configuration.addConsoleItem(subTitleItem);
		}
		
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID, 
				ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL);
		modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_DEFAULT_VALUE);
		modeItem.setReloadOnChange(true);
		configuration.addConsoleItem(modeItem);
		
		AbstractConsoleItem<?> archiveItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_LABEL);
		((BinaryConsoleItem)archiveItem).setShowContent(false);
		((BinaryConsoleItem)archiveItem).setReadOnly(false);
		((BinaryConsoleItem)archiveItem).setRequired(true);
		((BinaryConsoleItem)archiveItem).setNoteUpdate(ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_NOTE_UPDATE);
		configuration.addConsoleItem(archiveItem);
		
		AbstractConsoleItem<?> pathItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_LABEL);
		configuration.addConsoleItem(pathItem);
		
		StringConsoleItem typeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID, 
				ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL);
		typeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_DEFAULT_VALUE);
		configuration.addConsoleItem(typeItem);
		
		AbstractConsoleItem<?> keystorePasswordItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_LABEL);
		keystorePasswordItem.setRequired(requiredValue);
		configuration.addConsoleItem(keystorePasswordItem);
		
		AbstractConsoleItem<?> keyAliasItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID, 
						ModIConsoleCostanti.MODIPA_KEY_ALIAS_LABEL);
		keyAliasItem.setRequired(requiredValue);
		configuration.addConsoleItem(keyAliasItem);
		
		AbstractConsoleItem<?> keyPasswordItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID, 
						ModIConsoleCostanti.MODIPA_KEY_PASSWORD_LABEL);
		keyPasswordItem.setRequired(requiredValue);
		configuration.addConsoleItem(keyPasswordItem);

		AbstractConsoleItem<?> certificateItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_LABEL);
		((BinaryConsoleItem)certificateItem).setShowContent(false);
		((BinaryConsoleItem)certificateItem).setReadOnly(false);
		((BinaryConsoleItem)certificateItem).setRequired(false);
		configuration.addConsoleItem(certificateItem);
		
		if(addHiddenSubjectIssuer) {
			
			AbstractConsoleItem<?> cnSubjectItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.HIDDEN,
							ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID, 
							ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID);
			configuration.addConsoleItem(cnSubjectItem);
			
			AbstractConsoleItem<?> cnIssuerItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.HIDDEN,
							ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID, 
							ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID);
			configuration.addConsoleItem(cnIssuerItem);
			
		}
	}
	
	private void updateKeystoreConfig(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, boolean checkRidefinisci, 
			boolean hideSceltaArchivioFilePath, boolean addHiddenSubjectIssuer,
			boolean requiredValue, ConfigurazioneMultitenant configurazioneMultitenant) throws ProtocolException {
		
		boolean ridefinisci = true;
		if(checkRidefinisci) {
			StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_ID);
			if(selectModeItemValue==null) {
				ridefinisci = false;	
			}
			else {
				ridefinisci = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectModeItemValue.getValue());
			}
		
			BaseConsoleItem subTitleItem = 	
					ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_ID);
			if(subTitleItem!=null) {
				if(ridefinisci) {
					subTitleItem.setType(ConsoleItemType.SUBTITLE);	
				}
				else {
					subTitleItem.setType(ConsoleItemType.HIDDEN);	
				}
			}
		}
		
		AbstractConsoleItem<?> modeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
		if(ridefinisci && !hideSceltaArchivioFilePath) {
			modeItem.setType(ConsoleItemType.SELECT);
		}
		else {
			modeItem.setType(ConsoleItemType.HIDDEN);
		}
		((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL_ARCHIVE,
				ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
		((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL_PATH,
				ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH);
		if(HSMUtils.existsTIPOLOGIE_KEYSTORE_HSM(false, false)) {
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL_HSM,
					ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM);
		}
		
		StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
		AbstractConsoleItem<?> archiveItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
		AbstractConsoleItem<?> pathItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
		AbstractConsoleItem<?> certificateItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
		
		boolean permitCertificate = false;
		boolean hsm = false;
		String modalita = ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_DEFAULT_VALUE;
		if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
			modalita = selectModeItemValue.getValue();
		}
		if(ridefinisci) {
			if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(modalita)) {
				
				permitCertificate = true;
				
				hsm = true;
				
				archiveItem.setType(ConsoleItemType.HIDDEN);
				archiveItem.setRequired(false);
				BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
				if(archiveItemValue!=null) {
					archiveItemValue.setValue(null);
					archiveItemValue.setFileName(null);
					archiveItemValue.setClearContent(true);
				}
				
				pathItem.setType(ConsoleItemType.HIDDEN);
				pathItem.setRequired(false);
				StringProperty pathItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
				if(pathItemItemValue!=null) {
					pathItemItemValue.setValue(null);
				}
				
				if(configurazioneMultitenant!=null &&
						StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
					modeItem.setNote(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_NOTE_PATH);
				}
				else {
					modeItem.setNote(null);
				}
			}
			else if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
				
				archiveItem.setType(ConsoleItemType.FILE);
				archiveItem.setRequired(requiredValue);
				if(addHiddenSubjectIssuer) {
					((BinaryConsoleItem)archiveItem).setReadOnly(true);
				}
				
				pathItem.setType(ConsoleItemType.HIDDEN);
				pathItem.setRequired(false);
				StringProperty pathItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
				if(pathItemItemValue!=null) {
					pathItemItemValue.setValue(null);
				}
				
				modeItem.setNote(null);
			}
			else {
				
				permitCertificate = true;
				
				archiveItem.setType(ConsoleItemType.HIDDEN);
				archiveItem.setRequired(false);
				BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
				if(archiveItemValue!=null) {
					archiveItemValue.setValue(null);
					archiveItemValue.setFileName(null);
					archiveItemValue.setClearContent(true);
				}
				
				pathItem.setType(ConsoleItemType.TEXT_AREA);
				((StringConsoleItem)pathItem).setRows(3);
				pathItem.setRequired(requiredValue);
				
				if(configurazioneMultitenant!=null &&
						StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
					modeItem.setNote(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_NOTE_PATH);
				}
				else {
					modeItem.setNote(null);
				}
			}
			
			if(certificateItem!=null) {
				if(addHiddenSubjectIssuer // ha senso solo per l'applicativo per ovviare al problema di riconoscimento multi-tenant
						&& permitCertificate) {
					certificateItem.setType(ConsoleItemType.FILE);
					certificateItem.setRequired(false);
					((BinaryConsoleItem)archiveItem).setReadOnly(true);
					
					BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
					if(certificateItemValue!=null && certificateItemValue.getValue()!=null && certificateItemValue.getValue().length>0) {
						modeItem.setNote(null);
					}
				}
				else {
					certificateItem.setType(ConsoleItemType.HIDDEN);
					certificateItem.setRequired(false);
					BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
					if(certificateItemValue!=null) {
						certificateItemValue.setValue(null);
						certificateItemValue.setFileName(null);
						certificateItemValue.setClearContent(true);
					}
				}
			}
			
			if(addHiddenSubjectIssuer) {
				
				try {
					CertificateInfo cert = ModIDynamicConfiguration.readKeystoreConfig(properties, permitCertificate);
					if(cert!=null && cert.getSubject()!=null) {
						StringProperty subjectItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID);
						subjectItemValue.setValue(cert.getSubject().toString());
						
						StringProperty issuerItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID);
						if(cert.getIssuer()!=null) {
							issuerItemValue.setValue(cert.getIssuer().toString());
						}
						else {
							issuerItemValue.setValue(null);
						}
					}
				}catch(Throwable e) {
					// errore sollevato in validazione
				}
				
			}
		}
		else {
			pathItem.setType(ConsoleItemType.HIDDEN);
			pathItem.setRequired(false);
			archiveItem.setType(ConsoleItemType.HIDDEN);
			archiveItem.setRequired(false);
			if(certificateItem!=null) {
				certificateItem.setType(ConsoleItemType.HIDDEN);
				certificateItem.setRequired(false);
			}
		}
		
		AbstractConsoleItem<?> typeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
		if(hsm) {
			List<String> l = new ArrayList<String>();
			HSMUtils.fillTIPOLOGIE_KEYSTORE(false, false, l);
			if(l!=null && !l.isEmpty()) {
				for (String hsmType : l) {
					((StringConsoleItem)typeItem).addLabelValue(hsmType, hsmType);
				}
			}
		}	
		else {
			((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL_JKS,
					ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL_PKCS12,
					ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12);
		}
		if(ridefinisci) {
			typeItem.setType(ConsoleItemType.SELECT);
		}
		else {
			typeItem.setType(ConsoleItemType.HIDDEN);
		}
		
		AbstractConsoleItem<?> keystorePasswordItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
		if(ridefinisci && !hsm) {
			keystorePasswordItem.setType(ConsoleItemType.TEXT_EDIT);
		}
		else {
			keystorePasswordItem.setType(ConsoleItemType.HIDDEN);
		}
		
		AbstractConsoleItem<?> keyAliasItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID);
		if(ridefinisci) {
			keyAliasItem.setType(ConsoleItemType.TEXT_EDIT);
		}
		else {
			keyAliasItem.setType(ConsoleItemType.HIDDEN);
		}
		
		AbstractConsoleItem<?> keyPasswordItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
		if(ridefinisci && (!hsm || HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD)) {
			keyPasswordItem.setType(ConsoleItemType.TEXT_EDIT);
		}
		else {
			keyPasswordItem.setType(ConsoleItemType.HIDDEN);
		}
	}
	
	public static CertificateInfo readKeystoreConfig(ProtocolProperties properties, boolean onlyCert) throws Exception {
		
		Certificate cert = null;
			
		StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
		String modalita = ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_DEFAULT_VALUE;
		if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
			modalita = selectModeItemValue.getValue();
		}
		
		if(onlyCert) {
			
			if(!ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
				
				BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
				if(certificateItemValue!=null && certificateItemValue.getValue()!=null && !"".equals(certificateItemValue.getValue())) {
					byte [] certificate = certificateItemValue.getValue();
					cert = ArchiveLoader.load(certificate);
				}
				
			}
			else {
				// deve essere gestito invocando questo metodo con onlyCert
			}
			
		}
		else {
					
			StringProperty keystoreTypeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
			StringProperty keystorePasswordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
			StringProperty keyAliasItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID);
			StringProperty keyPasswordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
			if(keystoreTypeItemValue!=null && keystorePasswordItemValue!=null && keyAliasItemValue!=null && keyPasswordItemValue!=null) {
				String type = keystoreTypeItemValue.getValue();
				ArchiveType archiveType = null;
				if(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS.equals(type)) {
					archiveType = ArchiveType.JKS;
				}
				else {
					archiveType = ArchiveType.PKCS12;
				}
				
				byte [] archive = null;
				if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
					BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
					if(archiveItemValue!=null && archiveItemValue.getValue()!=null && !"".equals(archiveItemValue.getValue())) {
						archive = archiveItemValue.getValue();
						cert = ArchiveLoader.load(archiveType, archive, keyAliasItemValue.getValue(), keystorePasswordItemValue.getValue());
					}
				}
				else {
					// Il PATH o HSM indicato non e' disponibile nella macchina dove gira la console.
	//				StringProperty pathItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
	//				if(pathItemValue!=null && pathItemValue.getValue()!=null && !"".equals(pathItemValue.getValue())) {
	//					archive = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(pathItemValue.getValue());
	//					cert = ArchiveLoader.load(archiveType, archive, keyAliasItemValue.getValue(), keystorePasswordItemValue.getValue());
	//				}
				}
				
				// Verifico chiave privata
				if(archive!=null) {
					KeyStore ks = KeyStore.getInstance(archiveType.name());
					ks.load(new ByteArrayInputStream(archive), keystorePasswordItemValue.getValue().toCharArray());
					ks.getKey(keyAliasItemValue.getValue(), keyPasswordItemValue.getValue().toCharArray());
				}
			}
		}
		
		if(cert!=null) {
			return cert.getCertificate();
		}
		return null;
	}
	
	
	private void addTrustStoreSSLConfig_choice(ConsoleConfiguration configuration, boolean x5u) throws ProtocolException {
		_addTrustStoreConfig_choice(configuration, true, false, false, x5u);
	}
	private void addTrustStoreCertificatiConfig_choice(ConsoleConfiguration configuration, boolean x5u) throws ProtocolException {
		_addTrustStoreConfig_choice(configuration, false, true, false, x5u);
	}
	private void addTrustStoreKeystoreConfig_choice(ConsoleConfiguration configuration) throws ProtocolException {
		_addTrustStoreConfig_choice(configuration, false, false, true, false);
	}
	private void _addTrustStoreConfig_choice(ConsoleConfiguration configuration, boolean ssl, boolean truststore, boolean keystore, boolean x5u) throws ProtocolException {
		
		String id = null;
		if(ssl) {
			id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID; 
		}
		else if(truststore) {
			id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_ID;
		}
		else {
			id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_ID;
		}
		
		String label = null;
		if(ssl) {
			label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL; 
		}
		else if(truststore) {
			label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL;
		}
		else {
			label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
		}
		
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				id, 
				label);
		((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_DEFAULT_VALUE);
		modeItem.setReloadOnChange(true);
		if(ssl && !x5u) {
			modeItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(modeItem);
		
	}
	private void addTrustStoreConfig_subSection(ConsoleConfiguration configuration, boolean ssl, boolean x5u) throws ProtocolException {
		
		BaseConsoleItem subTitleItem = 
				ProtocolPropertiesFactory.newSubTitleItem(
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_LABEL);
		subTitleItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(subTitleItem);
				
		StringConsoleItem typeItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL);
		typeItem.setDefaultValue(ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS
									:
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS);
		configuration.addConsoleItem(typeItem);
		
		AbstractConsoleItem<?> pathItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_LABEL);
		configuration.addConsoleItem(pathItem);
		
		StringConsoleItem passwordItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_LABEL);
		configuration.addConsoleItem(passwordItem);
		
		StringConsoleItem crlsItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID
								:
							  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_LABEL
								:
							  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_LABEL		);
		crlsItem.setRequired(false);
		crlsItem.setRows(2);
		crlsItem.setNote(ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_NOTE
					:
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_NOTE);
		configuration.addConsoleItem(crlsItem);
		
	}
	
	private void updateTrustConfig(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, boolean ssl, boolean x5u, boolean requiredValue) throws ProtocolException {

		
		if(ssl) {
			StringConsoleItem modeItem = (StringConsoleItem) 
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID);
			if(x5u) {
				modeItem.setType(ConsoleItemType.SELECT);
				((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT,
						ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
				((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI,
						ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
			}	
			else {
				modeItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		
		
		
		StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID
						: 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_ID);
		boolean ridefinisci = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectModeItemValue.getValue());
		

		BaseConsoleItem subTitleItem = 	
				ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_ID
								: 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_ID);
		subTitleItem.setType(ridefinisci ? ConsoleItemType.SUBTITLE : ConsoleItemType.HIDDEN);
		
				
		boolean hsm = false;
		AbstractConsoleItem<?> typeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID);
		typeItem.setType(ridefinisci ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		if(ridefinisci) {
			if(ssl) {
				((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_LABEL_JKS,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS);
			}
			else {
				((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL_JKS,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS);
			}
			if(HSMUtils.existsTIPOLOGIE_KEYSTORE_HSM(true, false)) {
				List<String> l = new ArrayList<String>();
				HSMUtils.fillTIPOLOGIE_KEYSTORE(true, false, l);
				if(l!=null && !l.isEmpty()) {
					typeItem.setReloadOnChange(true);
					for (String hsmType : l) {
						((StringConsoleItem)typeItem).addLabelValue(hsmType, hsmType);
					}
				}
			}
			else {
				typeItem.setReloadOnChange(false);
			}
			
			StringProperty typeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
					ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID);
			if(typeItemValue!=null && typeItemValue.getValue()!=null) {
				hsm = HSMUtils.isKeystoreHSM(typeItemValue.getValue());
			}
		}
		
		AbstractConsoleItem<?> pathItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID);
		pathItem.setType(ridefinisci && !hsm ? ConsoleItemType.TEXT_AREA : ConsoleItemType.HIDDEN);
		((StringConsoleItem)pathItem).setRows(3);
		if(ridefinisci && !hsm)
			pathItem.setRequired(requiredValue);
		else 
			pathItem.setRequired(false);
		
		AbstractConsoleItem<?> passwordItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_ID);
		passwordItem.setType((ridefinisci && !hsm) ? ConsoleItemType.TEXT_EDIT : ConsoleItemType.HIDDEN);
		if(ridefinisci && !hsm)
			passwordItem.setRequired(requiredValue);
		else 
			passwordItem.setRequired(false);
		
		AbstractConsoleItem<?> crlsItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID
							:
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID	);
		crlsItem.setType(ridefinisci ? ConsoleItemType.TEXT_AREA : ConsoleItemType.HIDDEN);

	}
}
