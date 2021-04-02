/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoGenerico;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItem;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItemGenerico;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItemSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiCanale;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaRest;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaSoap;
import org.openspcoop2.core.config.rs.server.model.ApiItem;
import org.openspcoop2.core.config.rs.server.model.ApiModI;
import org.openspcoop2.core.config.rs.server.model.ApiModIAzioneSoap;
import org.openspcoop2.core.config.rs.server.model.ApiModIRisorsaRest;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaCanale;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioApplicabilitaCustom;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioOperazione;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioOperazioneRidefinito;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.CanaleEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoRestEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoSoapEnum;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaCanaleEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaCustomEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioOperazioneEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestHeaderEnum;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPI;
import org.openspcoop2.core.config.rs.server.model.StatoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSemiformaleEnum;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.archive.APIUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ApiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApiApiHelper {



	public static void validateProperties(ApiEnv env, ProtocolProperties protocolProperties, IDAccordo idAccordoFromAccordo)
			throws DriverConfigurazioneException {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idAccordoFromAccordo);

				env.apcHelper.validaProtocolProperties(consoleConf, ConsoleOperationType.ADD, protocolProperties);
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
		}
	}

	public static ConsoleConfiguration getConsoleConfiguration(ApiEnv env, IDAccordo idAccordoFromAccordo)
			throws ProtocolException, DriverConfigurazioneException {
		String protocolName = "modipa"; //TODO traduzione da profilo

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
		IConsoleDynamicConfiguration consoleDynamicConfiguration = protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(protocolFactory);

		ConsoleConfiguration consoleConf = consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(ConsoleOperationType.ADD, env.apcHelper, 
				registryReader, configRegistryReader, idAccordoFromAccordo);
		return consoleConf;
	}


	public static ApiModIAzioneSoap getApiAzioneModI(AccordoServizioParteComune as, Operation az, ProfiloEnum profilo,
			ApiEnv env) {

		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			return null; //TODO generalizzare 
		}


		Map<String, AbstractProperty<?>> p = new HashMap<>();
		try{
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idAccordoFromAccordo);

			ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, as.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

			for(int i =0; i < prop.sizeProperties(); i++) {
				p.put(prop.getIdProperty(i), prop.getProperty(i));
			}

			ApiModIAzioneSoap apimodi = new ApiModIAzioneSoap();
			if(p!= null) {

				//TODO interazione tipo funz azione


				if(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, true)) {
					ApiModISicurezzaMessaggioOperazione op = new ApiModISicurezzaMessaggioOperazione();
					op.setStato(ModISicurezzaMessaggioOperazioneEnum.API);
					apimodi.setSicurezzaMessaggio(op);
				} else {

					ApiModISicurezzaMessaggioOperazioneRidefinito rid = new ApiModISicurezzaMessaggioOperazioneRidefinito();
					rid.setStato(ModISicurezzaMessaggioOperazioneEnum.RIDEFINITO);

					ApiModISicurezzaMessaggio conf = new ApiModISicurezzaMessaggio();
					ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

					String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

					if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
						conf.setSoapFirmaAllegati(true);
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
						conf.setSoapFirmaAllegati(true);
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
						conf.setSoapFirmaAllegati(true);
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
					}
					conf.setApplicabilita(applicabilita);
					conf.setDigestRichiesta(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
					conf.setInformazioniUtente(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

					rid.setConfigurazione(conf);
					apimodi.setSicurezzaMessaggio(rid);
				}

			}

			return apimodi;

		}catch(Exception e){
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}


	}


	public static ApiModIRisorsaRest getApiRisorsaModI(AccordoServizioParteComune as, Resource res, ProfiloEnum profilo, ApiEnv env) {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			return null; //TODO generalizzare 
		}


		Map<String, AbstractProperty<?>> p = new HashMap<>();
		try{
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idAccordoFromAccordo);

			ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, as.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

			for(int i =0; i < prop.sizeProperties(); i++) {
				p.put(prop.getIdProperty(i), prop.getProperty(i));
			}

			ApiModIRisorsaRest apimodi = new ApiModIRisorsaRest();
			if(p!= null) {

				//TODO interazione tipo funz azione


				if(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, true)) {
					ApiModISicurezzaMessaggioOperazione op = new ApiModISicurezzaMessaggioOperazione();
					op.setStato(ModISicurezzaMessaggioOperazioneEnum.API);
					apimodi.setSicurezzaMessaggio(op);
				} else {

					ApiModISicurezzaMessaggioOperazioneRidefinito rid = new ApiModISicurezzaMessaggioOperazioneRidefinito();
					rid.setStato(ModISicurezzaMessaggioOperazioneEnum.RIDEFINITO);

					ApiModISicurezzaMessaggio conf = new ApiModISicurezzaMessaggio();

					ModISicurezzaMessaggioRestHeaderEnum restHeader = null;
					String restHeaderString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, true);

					if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA)) {
						restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID;
					} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION)) {
						restHeader = ModISicurezzaMessaggioRestHeaderEnum.BEARER;
					}
					conf.setRestHeader(restHeader);

					ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

					String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

					if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM;

						ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = new ApiModISicurezzaMessaggioApplicabilitaCustom();
						appCustom.setApplicabilita(applicabilita);


						String applicabilitaCustomRichiestaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, true);
						ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRichiesta = null;

						if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO)) {
							applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
						} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO)) {
							applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
						} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO)) {
							applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;
							appCustom.setRichiestaContentType(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
						} 

						appCustom.setRichiesta(applicabilitaCustomRichiesta);


						String applicabilitaCustomRispostaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, true);
						ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRisposta = null;

						if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO)) {
							applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
						} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO)) {
							applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
						} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO)) {
							applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;

							appCustom.setRispostaContentType(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
							appCustom.setRispostaCodice(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
						} 

						appCustom.setRisposta(applicabilitaCustomRisposta);

						conf.setApplicabilitaCustom(appCustom);
					}

					conf.setApplicabilita(applicabilita);

					conf.setDigestRichiesta(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
					conf.setInformazioniUtente(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

					rid.setConfigurazione(conf);
					apimodi.setSicurezzaMessaggio(rid);
				}

			}

			return apimodi;

		}catch(Exception e){
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}


	}

	public static ApiModI getApiModI(AccordoServizioParteComune as, ProfiloEnum profilo, ApiEnv env) {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		Map<String, AbstractProperty<?>> p = new HashMap<>();
		try{
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idAccordoFromAccordo);

			ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, as.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

			for(int i =0; i < prop.sizeProperties(); i++) {
				p.put(prop.getIdProperty(i), prop.getProperty(i));
			}


			ApiModI apimodi = new ApiModI();
			if(p!= null) {
				ApiModISicurezzaCanale sicurezzaCanale = new ApiModISicurezzaCanale();

				String sicurezzaCanalePatternString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE, true);

				sicurezzaCanale.setPattern(sicurezzaCanalePatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01) ? ModISicurezzaCanaleEnum.AUTH01:ModISicurezzaCanaleEnum.AUTH02);
				apimodi.setSicurezzaCanale(sicurezzaCanale);

				ApiModISicurezzaMessaggio sicurezzaMessaggio = new ApiModISicurezzaMessaggio();

				String sicurezzaMessaggioPatternString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, true);

				ModISicurezzaMessaggioEnum profiloSicurezzaMessaggioPattern = null;
				if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.AUTH01;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.AUTH02;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.DISABILITATO;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02;
				}
				sicurezzaMessaggio.setPattern(profiloSicurezzaMessaggioPattern);

				if(!profiloSicurezzaMessaggioPattern.equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

					boolean isSoap = as.getServiceBinding().equals(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP);

					if(isSoap) {
						ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

						String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

						if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
							sicurezzaMessaggio.setSoapFirmaAllegati(true);
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
							sicurezzaMessaggio.setSoapFirmaAllegati(true);
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
							sicurezzaMessaggio.setSoapFirmaAllegati(true);
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
						}
						sicurezzaMessaggio.setApplicabilita(applicabilita);
					} else {

						ModISicurezzaMessaggioRestHeaderEnum restHeader = null;
						String restHeaderString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, true);

						if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA)) {
							restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID;
						} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION)) {
							restHeader = ModISicurezzaMessaggioRestHeaderEnum.BEARER;
						}
						sicurezzaMessaggio.setRestHeader(restHeader);

						ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

						String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

						if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM;

							ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = new ApiModISicurezzaMessaggioApplicabilitaCustom();
							appCustom.setApplicabilita(applicabilita);


							String applicabilitaCustomRichiestaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, true);
							ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRichiesta = null;

							if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO)) {
								applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
							} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO)) {
								applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
							} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO)) {
								applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;
								appCustom.setRichiestaContentType(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
							} 

							appCustom.setRichiesta(applicabilitaCustomRichiesta);


							String applicabilitaCustomRispostaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, true);
							ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRisposta = null;

							if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO)) {
								applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
							} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO)) {
								applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
							} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO)) {
								applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;

								appCustom.setRispostaContentType(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
								appCustom.setRispostaCodice(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
							} 

							appCustom.setRisposta(applicabilitaCustomRisposta);

							sicurezzaMessaggio.setApplicabilitaCustom(appCustom);
						}

						sicurezzaMessaggio.setApplicabilita(applicabilita);


					}

					sicurezzaMessaggio.setDigestRichiesta(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
					sicurezzaMessaggio.setInformazioniUtente(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

				}

				apimodi.setSicurezzaMessaggio(sicurezzaMessaggio);

			}

			return apimodi;

		}catch(Exception e){
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}
	}


	private static Boolean getBooleanProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {
		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop instanceof BooleanProperty) {
			return ((BooleanProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una Boolean:" + prop.getClass().getName());
		}
	}


	private static String getStringProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop instanceof StringProperty) {
			return ((StringProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una StringProperty:" + prop.getClass().getName());
		}
	}

	private static AbstractProperty<?> getProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {
		if(p.containsKey(key)) {
			return p.get(key);
		} else {
			if(required) {
				throw new Exception("Property "+key+" non trovata");
			} else {
				return null;
			}
		}
	}

	public static ProtocolProperties getProtocolProperties(ApiAzione body, ProfiloEnum profilo) {
		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return getApiGatewayProtocolProperties(body);
		case EDELIVERY:
			return getEDeliveryProtocolProperties(body);
		case FATTURAPA:
			return getFatturaPAProtocolProperties(body);
		case MODI:
		case MODIPA:
			return getModiProtocolProperties(body);
		case SPCOOP:
			return getSPCoopProtocolProperties(body);
		}
		return null;
	}


	private static ProtocolProperties getFatturaPAProtocolProperties(ApiAzione body) {
		return null;
	}

	private static ProtocolProperties getSPCoopProtocolProperties(ApiAzione body) {
		return null;
	}

	private static ProtocolProperties getEDeliveryProtocolProperties(ApiAzione body) {
		return null;
	}

	private static ProtocolProperties getApiGatewayProtocolProperties(ApiAzione body) {
		return null;
	}

	private static ProtocolProperties getModiProtocolProperties(ApiAzione body) {
		return getSOAPModiProtocolProperties(body.getModi());
	}
	

	public static ProtocolProperties getProtocolProperties(ApiRisorsa body, ProfiloEnum profilo) {
		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return getApiGatewayProtocolProperties(body);
		case EDELIVERY:
			return getEDeliveryProtocolProperties(body);
		case FATTURAPA:
			return getFatturaPAProtocolProperties(body);
		case MODI:
		case MODIPA:
			return getModiProtocolProperties(body);
		case SPCOOP:
			return getSPCoopProtocolProperties(body);
		}
		return null;
	}


	private static ProtocolProperties getFatturaPAProtocolProperties(ApiRisorsa body) {
		return null;
	}

	private static ProtocolProperties getSPCoopProtocolProperties(ApiRisorsa body) {
		return null;
	}

	private static ProtocolProperties getEDeliveryProtocolProperties(ApiRisorsa body) {
		return null;
	}

	private static ProtocolProperties getApiGatewayProtocolProperties(ApiRisorsa body) {
		return null;
	}

	private static ProtocolProperties getModiProtocolProperties(ApiRisorsa body) {
		return getRESTModiProtocolProperties(body.getModi());
	}

	public static ProtocolProperties getRESTModiProtocolProperties(ApiModIRisorsaRest modi) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		String profiloSicurezzaMessaggio = null;

		switch(modi.getInterazione().getPattern()) {
		case BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
			break;
		case CRUD: p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		break;
		case NON_BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
			break;
		default:
			break;

		}

		//TODO interazione tipo funz azione
		
		boolean cornicePropValue = false;
		boolean digestPropValue = false;

		if(modi.getSicurezzaMessaggio().getStato().equals(ModISicurezzaMessaggioOperazioneEnum.API)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
		} else {
			ApiModISicurezzaMessaggioOperazioneRidefinito rid = ((ApiModISicurezzaMessaggioOperazioneRidefinito)modi.getSicurezzaMessaggio());

			switch(rid.getConfigurazione().getPattern()) {
			case AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
			break;
			case AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02;
			break;
			case DISABILITATO: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED;
			break;
			case INTEGRITY01_AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301;
			break;
			case INTEGRITY01_AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302;
			break;}


			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, profiloSicurezzaMessaggio);

			if(!rid.getConfigurazione().getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

				if(rid.getConfigurazione().getRestHeader() == null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern " + rid.getConfigurazione().getPattern());
				}


				String headerHTTPREST = "";
				switch(rid.getConfigurazione().getRestHeader()) {
				case AGID: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA;
				break;
				case BEARER: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION;
				break;}


				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, headerHTTPREST);


				String applicabilita = "";


				if(rid.getConfigurazione().getApplicabilita().equals(ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM)) {

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO);

					if(rid.getConfigurazione().getApplicabilitaCustom() == null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita " + rid.getConfigurazione().getApplicabilita());
					}

					ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = rid.getConfigurazione().getApplicabilitaCustom();

					String sicurezzaRichiesta = "";
					boolean requireContentTypeRequest = false;
					switch(appCustom.getRichiesta()) {
					case ABILITATO: sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO;
					break;
					case CUSTOM:sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO; requireContentTypeRequest = true;
					break;
					case DISABILITATO:sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO;
					break;}

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, sicurezzaRichiesta);

					if(appCustom.getRichiestaContentType()!=null) {
						if(!requireContentTypeRequest) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.richiesta_content_type non deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta " + appCustom.getRichiesta());
						}
						p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, appCustom.getRichiestaContentType());
					} else {
						if(requireContentTypeRequest) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.richiesta_content_type deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta " + appCustom.getRichiesta());
						}
					}

					String sicurezzaRisposta = "";
					boolean requireContentTypeAndReturnCodeResponse = false;
					switch(appCustom.getRisposta()) {
					case ABILITATO: sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO;
					break;
					case CUSTOM:sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO; requireContentTypeAndReturnCodeResponse = true;
					break;
					case DISABILITATO:sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO;
					break;}

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, sicurezzaRisposta);


					if(appCustom.getRispostaContentType()!=null && appCustom.getRispostaCodice()!=null) {
						if(!requireContentTypeAndReturnCodeResponse) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta " + appCustom.getRisposta());
						}
						p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, appCustom.getRispostaContentType());
						p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, appCustom.getRispostaCodice());
					} else {
						if(requireContentTypeAndReturnCodeResponse) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta " + appCustom.getRisposta());
						}
					}
				} else {

					if(rid.getConfigurazione().getApplicabilitaCustom() != null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom non deve essere specificato con servizio di tipo REST e applicabilita " + rid.getConfigurazione().getApplicabilita());
					}

					switch(rid.getConfigurazione().getApplicabilita()) {
					case CUSTOM: //gestito nell'altro ramo dell'if
						break;
					case QUALSIASI: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI;
					break;
					case RICHIESTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; 
					break;
					case RISPOSTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
					break;
					}

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
				}
			} else {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, "");

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
			}
		}

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));

		return p;
	}


	public static ProtocolProperties getSOAPModiProtocolProperties(ApiModIAzioneSoap modi) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		switch(modi.getInterazione().getPattern()) {
		case BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
			break;
		case CRUD: p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		break;
		case NON_BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
			break;
		default:
			break;

		}
		
		//TODO interazione tipo funz azione

		boolean cornicePropValue = false;
		boolean digestPropValue = false;

		if(modi.getSicurezzaMessaggio().getStato().equals(ModISicurezzaMessaggioOperazioneEnum.API)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
		} else {
			ApiModISicurezzaMessaggioOperazioneRidefinito rid = ((ApiModISicurezzaMessaggioOperazioneRidefinito)modi.getSicurezzaMessaggio());

			String applicabilita = "";

			boolean integritySOAP = rid.getConfigurazione().getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01) || 
					rid.getConfigurazione().getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02);

			if(rid.getConfigurazione().isSoapFirmaAllegati() && !integritySOAP) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con pattern " + rid.getConfigurazione().getPattern());
			}

			boolean applicabilitaInfoUtente = false;
			boolean applicabilitaDigest = false;

			if(rid.getConfigurazione().getApplicabilita()!=null) {
				switch(rid.getConfigurazione().getApplicabilita()) {
				case CUSTOM: throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP");
				case QUALSIASI: applicabilita = rid.getConfigurazione().isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI; applicabilitaInfoUtente = true; applicabilitaDigest = true;
				break;
				case RICHIESTA: applicabilita = rid.getConfigurazione().isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; applicabilitaInfoUtente = true;
				break;
				case RISPOSTA: applicabilita = rid.getConfigurazione().isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
				break;
				}
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
			} else {
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_DEFAULT);
				applicabilitaInfoUtente = true;
				applicabilitaDigest = true;
			}

			if((rid.getConfigurazione().isInformazioniUtente() != null && rid.getConfigurazione().isInformazioniUtente())) {

				if(integritySOAP && applicabilitaInfoUtente) {
					cornicePropValue =  rid.getConfigurazione().isInformazioniUtente();
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + rid.getConfigurazione().getPattern() + " o applicabilita " + rid.getConfigurazione().getApplicabilita());
				}
			}

			if((rid.getConfigurazione().isDigestRichiesta() != null && rid.getConfigurazione().isDigestRichiesta())) {

				if(integritySOAP && applicabilitaDigest) {
					digestPropValue = rid.getConfigurazione().isDigestRichiesta();
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + rid.getConfigurazione().getPattern() + " o applicabilita " + rid.getConfigurazione().getApplicabilita());
				}
			}

			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");

		}

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));
		return p;
	}

	public static ProtocolProperties getProtocolProperties(Api body, ProfiloEnum profilo) {


		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return getApiGatewayProtocolProperties(body);
		case EDELIVERY:
			return getEDeliveryProtocolProperties(body);
		case FATTURAPA:
			return getFatturaPAProtocolProperties(body);
		case MODI:
		case MODIPA:
			return getModiProtocolProperties(body);
		case SPCOOP:
			return getSPCoopProtocolProperties(body);
		}
		return null;
	}

	private static ProtocolProperties getFatturaPAProtocolProperties(Api body) {
		return null;
	}

	private static ProtocolProperties getSPCoopProtocolProperties(Api body) {
		return null;
	}

	private static ProtocolProperties getEDeliveryProtocolProperties(Api body) {
		return null;
	}

	private static ProtocolProperties getApiGatewayProtocolProperties(Api body) {
		return null;
	}

	private static ProtocolProperties getModiProtocolProperties(Api body) {
		return getModiProtocolProperties(body.getModi(), body.getTipoInterfaccia().getProtocollo());
	}

	public static ProtocolProperties updateModiProtocolProperties(AccordoServizioParteComune as, ProfiloEnum profilo, ApiModI modi) {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = as.getFormatoSpecifica().equals(FormatoSpecifica.WSDL_11) ? TipoApiEnum.SOAP: TipoApiEnum.REST;
		return getModiProtocolProperties(modi, protocollo);

	}

	private static ProtocolProperties getModiProtocolProperties(ApiModI modi, TipoApiEnum protocollo) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		String chan = modi.getSicurezzaCanale().getPattern().equals(ModISicurezzaCanaleEnum.AUTH01) ? ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01 : ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02;
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE, chan);

		String profiloSicurezzaMessaggio = null;

		switch(modi.getSicurezzaMessaggio().getPattern()) {
		case AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
		break;
		case AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02;
		break;
		case DISABILITATO: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED;
		break;
		case INTEGRITY01_AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301;
		break;
		case INTEGRITY01_AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302;
		break;}


		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, profiloSicurezzaMessaggio);

		boolean cornicePropValue = false;
		boolean digestPropValue = false;

		if(protocollo.equals(TipoApiEnum.SOAP)) {

			if(modi.getSicurezzaMessaggio().getRestHeader() != null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP");
			}


			if(!modi.getSicurezzaMessaggio().getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

				String applicabilita = "";

				boolean integritySOAP = modi.getSicurezzaMessaggio().getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01) || 
						modi.getSicurezzaMessaggio().getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02);

				if(modi.getSicurezzaMessaggio().isSoapFirmaAllegati() && !integritySOAP) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con pattern " + modi.getSicurezzaMessaggio().getPattern());
				}

				boolean applicabilitaInfoUtente = false;
				boolean applicabilitaDigest = false;

				if(modi.getSicurezzaMessaggio().getApplicabilita()!=null) {
					switch(modi.getSicurezzaMessaggio().getApplicabilita()) {
					case CUSTOM: throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP");
					case QUALSIASI: applicabilita = modi.getSicurezzaMessaggio().isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI; applicabilitaInfoUtente = true; applicabilitaDigest = true;
					break;
					case RICHIESTA: applicabilita = modi.getSicurezzaMessaggio().isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; applicabilitaInfoUtente = true;
					break;
					case RISPOSTA: applicabilita = modi.getSicurezzaMessaggio().isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
					break;
					}
					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
				} else {
					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_DEFAULT);
					applicabilitaInfoUtente = true;
					applicabilitaDigest = true;
				}

				if((modi.getSicurezzaMessaggio().isInformazioniUtente() != null && modi.getSicurezzaMessaggio().isInformazioniUtente())) {

					if(integritySOAP && applicabilitaInfoUtente) {
						cornicePropValue =  modi.getSicurezzaMessaggio().isInformazioniUtente();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + modi.getSicurezzaMessaggio().getPattern() + " o applicabilita " + modi.getSicurezzaMessaggio().getApplicabilita());
					}
				}

				if((modi.getSicurezzaMessaggio().isDigestRichiesta() != null && modi.getSicurezzaMessaggio().isDigestRichiesta())) {

					if(integritySOAP && applicabilitaDigest) {
						digestPropValue = modi.getSicurezzaMessaggio().isDigestRichiesta();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + modi.getSicurezzaMessaggio().getPattern() + " o applicabilita " + modi.getSicurezzaMessaggio().getApplicabilita());
					}
				}

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
			} else {
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, "");

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
			}

		} else if(protocollo.equals(TipoApiEnum.REST)) {

			if(modi.getSicurezzaMessaggio().isSoapFirmaAllegati()!= null && 
					modi.getSicurezzaMessaggio().isSoapFirmaAllegati()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con servizio di tipo REST");
			}

			if(modi.getSicurezzaMessaggio().isDigestRichiesta()!= null && 
					modi.getSicurezzaMessaggio().isDigestRichiesta()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con servizio di tipo REST");
			}

			if(modi.getSicurezzaMessaggio().isInformazioniUtente()!= null && 
					modi.getSicurezzaMessaggio().isInformazioniUtente()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con servizio di tipo REST");
			}


			if(!modi.getSicurezzaMessaggio().getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

				if(modi.getSicurezzaMessaggio().getRestHeader() == null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern " + modi.getSicurezzaMessaggio().getPattern());
				}


				String headerHTTPREST = "";
				switch(modi.getSicurezzaMessaggio().getRestHeader()) {
				case AGID: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA;
				break;
				case BEARER: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION;
				break;}


				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, headerHTTPREST);


				String applicabilita = "";


				if(modi.getSicurezzaMessaggio().getApplicabilita().equals(ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM)) {

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO);

					if(modi.getSicurezzaMessaggio().getApplicabilitaCustom() == null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita " + modi.getSicurezzaMessaggio().getApplicabilita());
					}

					ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = modi.getSicurezzaMessaggio().getApplicabilitaCustom();

					String sicurezzaRichiesta = "";
					boolean requireContentTypeRequest = false;
					switch(appCustom.getRichiesta()) {
					case ABILITATO: sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO;
					break;
					case CUSTOM:sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO; requireContentTypeRequest = true;
					break;
					case DISABILITATO:sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO;
					break;}

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, sicurezzaRichiesta);

					if(appCustom.getRichiestaContentType()!=null) {
						if(!requireContentTypeRequest) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.richiesta_content_type non deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta " + appCustom.getRichiesta());
						}
						p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, appCustom.getRichiestaContentType());
					} else {
						if(requireContentTypeRequest) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.richiesta_content_type deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta " + appCustom.getRichiesta());
						}
					}

					String sicurezzaRisposta = "";
					boolean requireContentTypeAndReturnCodeResponse = false;
					switch(appCustom.getRisposta()) {
					case ABILITATO: sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO;
					break;
					case CUSTOM:sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO; requireContentTypeAndReturnCodeResponse = true;
					break;
					case DISABILITATO:sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO;
					break;}

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, sicurezzaRisposta);


					if(appCustom.getRispostaContentType()!=null && appCustom.getRispostaCodice()!=null) {
						if(!requireContentTypeAndReturnCodeResponse) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta " + appCustom.getRisposta());
						}
						p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, appCustom.getRispostaContentType());
						p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, appCustom.getRispostaCodice());
					} else {
						if(requireContentTypeAndReturnCodeResponse) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta " + appCustom.getRisposta());
						}
					}
				} else {

					if(modi.getSicurezzaMessaggio().getApplicabilitaCustom() != null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom non deve essere specificato con servizio di tipo REST e applicabilita " + modi.getSicurezzaMessaggio().getApplicabilita());
					}

					switch(modi.getSicurezzaMessaggio().getApplicabilita()) {
					case CUSTOM: //gestito nell'altro ramo dell'if
						break;
					case QUALSIASI: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI;
					break;
					case RICHIESTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; 
					break;
					case RISPOSTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
					break;
					}

					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
				}
			} else {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, "");

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
			}
		}

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));

		return p;
	}


	public static final AccordoServizioParteComune accordoApiToRegistro(Api body, ApiEnv env) throws Exception {
		AccordoServizioParteComune as = new AccordoServizioParteComune(); 

		as.setNome(body.getNome());
		as.setDescrizione(body.getDescrizione());
		as.setProfiloCollaborazione(ProfiloCollaborazione.SINCRONO);

		// Quando sono in SPCoopSoap Specifico di tutti i vari wsdl\wsbl solo il wsdlserv, ovvero  AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE
		// Quando invece sono in modalità ApiGateway e passo una OpenApi, imposto il wsdlconcettuale
		String interfaccia = body.getInterfaccia() != null ? new String(body.getInterfaccia()) : null;

		// defaults
		// Questo codice e il controllo sotto non dovrebbero mai intervenire essendo tipo required
		if (env.profilo != ProfiloEnum.APIGATEWAY) {

			if (body.getTipoInterfaccia() == null) {
				ApiInterfacciaSoap iSoap = new ApiInterfacciaSoap();
				iSoap.setProtocollo(TipoApiEnum.SOAP);
				iSoap.setFormato(FormatoSoapEnum._1);
				body.setTipoInterfaccia(iSoap);	
			}
		}

		if ( env.profilo == ProfiloEnum.APIGATEWAY ) {

			if ( body.getTipoInterfaccia() == null ) {
				ApiInterfacciaRest iRest = new ApiInterfacciaRest();
				iRest.setProtocollo(TipoApiEnum.REST);
				iRest.setFormato(FormatoRestEnum.OPENAPI3_0);
				body.setTipoInterfaccia(iRest);	
			}

		}

		if ( body.getTipoInterfaccia() == null )	// Questo non può mai accadere, lo tengo perchè in futuro il codice sopra potrà cambiare.
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un tipo di interfaccia per la Api");

		switch (body.getTipoInterfaccia().getProtocollo()) {
		case REST:

			if(body.getTipoInterfaccia() instanceof ApiInterfacciaSoap) {
				ApiInterfacciaSoap soap = (ApiInterfacciaSoap) body.getTipoInterfaccia();
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+soap.getFormato()+") non è compatibile con il protocollo REST indicato");
			}
			else if(body.getTipoInterfaccia() instanceof ApiInterfacciaRest) {
				ApiInterfacciaRest iRest = (ApiInterfacciaRest) body.getTipoInterfaccia();

				as.setByteWsdlConcettuale(interfaccia != null && !interfaccia.trim().replaceAll("\n", "").equals("") ? interfaccia.trim().getBytes() : null);
				FormatoRestEnum formatoRest = iRest.getFormato();
				as.setFormatoSpecifica( BaseHelper.evalorElse( () -> 
				Enums.formatoSpecificaFromRest.get(formatoRest),
				FormatoSpecifica.OPEN_API_3
						)); 
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+body.getTipoInterfaccia().getClass().getName()+") risulta sconosciuto e non compatibile con il protocollo REST indicato");
			}

			break;
		case SOAP: 

			if(body.getTipoInterfaccia() instanceof ApiInterfacciaRest) {
				ApiInterfacciaRest rest = (ApiInterfacciaRest) body.getTipoInterfaccia();
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+rest.getFormato()+") non è compatibile con il protocollo SOAP indicato");
			}
			else if(body.getTipoInterfaccia() instanceof ApiInterfacciaSoap) {
				ApiInterfacciaSoap iSoap = (ApiInterfacciaSoap) body.getTipoInterfaccia();

				FormatoSoapEnum formatoSoap = iSoap.getFormato();
				as.setFormatoSpecifica( BaseHelper.evalorElse( 
						() -> Enums.formatoSpecificaFromSoap.get(formatoSoap), 
						FormatoSpecifica.WSDL_11 
						));
				as.setByteWsdlLogicoErogatore(interfaccia != null && !interfaccia.trim().replaceAll("\n", "").equals("") ? interfaccia.trim().getBytes() : null);	// Da commenti e audit, WSDL solo logico ed erogatore
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+body.getTipoInterfaccia().getClass().getName()+") risulta sconosciuto e non compatibile con il protocollo SOAP indicato");
			}

			break;
		}

		as.setServiceBinding(env.apcCore.fromMessageServiceBinding(Enums.serviceBindingFromTipo.get(body.getTipoInterfaccia().getProtocollo())));

		boolean facilityUnicoWSDL_interfacciaStandard = false;
		if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
			as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
			facilityUnicoWSDL_interfacciaStandard = true;
		}

		boolean filtroDuplicatiSupportato = env.stationCore.isFunzionalitaProtocolloSupportataDalProtocollo(
				env.tipo_protocollo, 
				env.apcCore.toMessageServiceBinding(as.getServiceBinding())
				, FunzionalitaProtocollo.FILTRO_DUPLICATI
				);

		as.setFiltroDuplicati(Helper.boolToStatoFunzionalita(filtroDuplicatiSupportato));
		as.setConfermaRicezione(StatoFunzionalita.DISABILITATO);
		as.setIdCollaborazione(StatoFunzionalita.DISABILITATO);
		as.setConsegnaInOrdine(StatoFunzionalita.DISABILITATO);
		as.setIdRiferimentoRichiesta(StatoFunzionalita.DISABILITATO);
		as.setUtilizzoSenzaAzione(true);	// Default a true.
		as.setPrivato(false);	// Da Audit è false.
		as.setStatoPackage(StatoType.FINALE.getValue());	// Come da Audit

		if (body.getVersione() != null)
			as.setVersione(body.getVersione());

		as.setSuperUser(env.userLogin);

		// Questo resta a null dal debug anche quando specifico un wsdl
		// as.setMessageType(apcCore.fromMessageMessageType(this.messageType));

		// Setto il soggetto referente, true in caso di "spcoop" "sdi", "as4"
		// Se non lo gestisco (caso "trasparente") allora è il sistema a legare l'applicativo con un soggetto di default.
		IdSoggetto idSoggReferente = new IdSoggetto();
		if (env.gestisciSoggettoReferente) {			
			idSoggReferente.setNome(Helper.getSoggettoOrDefault(body.getReferente(), env.profilo));
			idSoggReferente.setTipo(env.tipo_soggetto);
		}
		else {
			IDSoggetto idSogg = env.apcCore.getSoggettoOperativoDefault(env.userLogin, env.tipo_protocollo);
			idSoggReferente.setNome(idSogg.getNome());
			idSoggReferente.setTipo(idSogg.getTipo());
		}

		if (!env.soggettiCore.existsSoggetto(idSoggReferente.toIDSoggetto())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto referente " + idSoggReferente.toIDSoggetto().toString() + " non è presente nel registro");
		}

		idSoggReferente.setId(env.soggettiCore.getIdSoggetto(idSoggReferente.getNome(),idSoggReferente.getTipo()));

		as.setSoggettoReferente(idSoggReferente);

		// Canale
		if(env.gestioneCanali && body.getCanale()!=null) {
			if(!env.canali.contains(body.getCanale())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il canale fornito '" + body.getCanale() + "' non è presente nel registro");
			}
			as.setCanale(body.getCanale());
		}

		// Automapping
		ServerProperties properties = ServerProperties.getInstance();
		InterfaceType interfaceType = Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica());

		AccordiServizioParteComuneUtilities.mapppingAutomaticoInterfaccia(
				as,
				env.apcCore, 
				properties.isEnabledAutoMapping(),
				properties.isValidazioneDocumenti(),
				properties.isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes(),
				facilityUnicoWSDL_interfacciaStandard, 
				env.tipo_protocollo, interfaceType);


		return as;
	}


	public static final Documento apiAllegatoToDocumento(ApiAllegato body, AccordoServizioParteComune as, ApiEnv env) {

		Documento documento = new Documento();
		documento.setIdProprietarioDocumento(as.getId());

		RuoloAllegatoAPI ruoloAllegato = body.getAllegato().getRuolo();

		documento.setRuolo(Enums.ruoliDocumentoFromApi.get(ruoloAllegato).toString());

		switch (ruoloAllegato) {
		case ALLEGATO:

			if(! (body.getAllegato() instanceof ApiAllegatoGenerico)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiAllegatoGenerico allegatoGenerico = (ApiAllegatoGenerico) body.getAllegato();
			documento.setFile(allegatoGenerico.getNome());
			documento.setByteContenuto(allegatoGenerico.getDocumento());
			documento.setTipo( evalnull( () -> allegatoGenerico.getNome().substring( allegatoGenerico.getNome().lastIndexOf('.')+1, allegatoGenerico.getNome().length())) );
			break;

		case SPECIFICASEMIFORMALE:

			if(! (body.getAllegato() instanceof ApiAllegatoSpecificaSemiformale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiAllegatoSpecificaSemiformale allegatoSS = (ApiAllegatoSpecificaSemiformale) body.getAllegato();
			documento.setFile(allegatoSS.getNome());
			documento.setByteContenuto(allegatoSS.getDocumento());
			if(allegatoSS.getTipoSpecifica()==null) {
				documento.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()); // default
			}
			else {
				TipoSpecificaSemiformaleEnum tipoAllegato = (TipoSpecificaSemiformaleEnum) allegatoSS.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoSemiFormaleFromSpecifica.get(tipoAllegato) ).toString() );
			}
			break;
		}

		return documento;
	}


	public static final ApiAllegato documentoToApiAllegato(Documento d) {
		ApiAllegato ret = new ApiAllegato();

		RuoloAllegatoAPI ruoloAllegato = Enums.ruoliApiFromDocumento.get(Enum.valueOf(RuoliDocumento.class, d.getRuolo()));

		switch (ruoloAllegato) {
		case ALLEGATO:

			ApiAllegatoGenerico allegatoGenerico = new ApiAllegatoGenerico();
			allegatoGenerico.setRuolo(ruoloAllegato);
			allegatoGenerico.setDocumento(d.getByteContenuto());
			allegatoGenerico.setNome(d.getFile());
			ret.setAllegato(allegatoGenerico);
			break;

		case SPECIFICASEMIFORMALE:

			ApiAllegatoSpecificaSemiformale allegatoSS = new ApiAllegatoSpecificaSemiformale();
			allegatoSS.setRuolo(ruoloAllegato);
			allegatoSS.setDocumento(d.getByteContenuto());
			allegatoSS.setNome(d.getFile());
			TipiDocumentoSemiformale tipo = Enum.valueOf(TipiDocumentoSemiformale.class, d.getTipo());
			allegatoSS.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class)));
			ret.setAllegato(allegatoSS);
			break;
		}

		return ret;
	}

	public static final ApiAllegatoItem documentoToApiAllegatoItem(Documento d) {
		ApiAllegatoItem ret = new ApiAllegatoItem();

		RuoloAllegatoAPI ruoloAllegato = Enums.ruoliApiFromDocumento.get(Enum.valueOf(RuoliDocumento.class, d.getRuolo()));

		switch (ruoloAllegato) {
		case ALLEGATO:

			ApiAllegatoItemGenerico allegatoGenerico = new ApiAllegatoItemGenerico();
			allegatoGenerico.setRuolo(ruoloAllegato);
			allegatoGenerico.setNome(d.getFile());
			ret.setAllegato(allegatoGenerico);
			break;

		case SPECIFICASEMIFORMALE:

			ApiAllegatoItemSpecificaSemiformale allegatoSS = new ApiAllegatoItemSpecificaSemiformale();
			allegatoSS.setRuolo(ruoloAllegato);
			allegatoSS.setNome(d.getFile());
			TipiDocumentoSemiformale tipo = Enum.valueOf(TipiDocumentoSemiformale.class, d.getTipo());
			allegatoSS.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class)));
			ret.setAllegato(allegatoSS);
			break;
		}

		return ret;
	}

	public static final void updatePortType(ApiServizio body, PortType pt, ApiEnv env) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {

		pt.setNome(body.getNome());
		pt.setDescrizione(body.getDescrizione());

		boolean filtroDuplicatiSupportato = env.stationCore.isFunzionalitaProtocolloSupportataDalProtocollo(
				env.tipo_protocollo, 
				ServiceBinding.SOAP
				, FunzionalitaProtocollo.FILTRO_DUPLICATI
				);

		pt.setFiltroDuplicati(Helper.boolToStatoFunzionalita(filtroDuplicatiSupportato));	
		pt.setIdCollaborazione(Helper.boolToStatoFunzionalita(body.isIdCollaborazione()));
		pt.setIdRiferimentoRichiesta(Helper.boolToStatoFunzionalita(body.isRiferimentoIdRichiesta()));
		pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
		pt.setProfiloCollaborazione(Enums.profiloCollaborazioneFromApiEnum.get(body.getProfiloCollaborazione()));			
	}


	public static final void updateOperation(ApiAzione azione, PortType parent, Operation to_update) {

		to_update.setNome(azione.getNome());	

		if (azione.isProfiloRidefinito()) {
			to_update.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			to_update.setIdCollaborazione(Helper.boolToStatoFunzionalita(azione.isIdCollaborazione()));
			to_update.setIdRiferimentoRichiesta(Helper.boolToStatoFunzionalita(azione.isRiferimentoIdRichiesta()));
			to_update.setProfiloCollaborazione(Enums.profiloCollaborazioneFromApiEnum.get(azione.getProfiloCollaborazione()));
		}
		else {
			to_update.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
			to_update.setFiltroDuplicati(parent.getFiltroDuplicati());
			to_update.setConfermaRicezione(parent.getConfermaRicezione());
			to_update.setConsegnaInOrdine(parent.getConsegnaInOrdine());
			to_update.setIdCollaborazione(parent.getIdCollaborazione());
			to_update.setIdRiferimentoRichiesta(parent.getIdRiferimentoRichiesta());
			to_update.setProfiloCollaborazione(parent.getProfiloCollaborazione());
			to_update.setScadenza(parent.getScadenza());
		}		
	}


	public static final Operation apiAzioneToOperazione(ApiAzione azione, PortType parent) {
		final Operation ret = new Operation();
		updateOperation(azione, parent, ret);

		final String useOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE;
		final String namespaceWsdlOp = "";

		final BindingUse use = BindingUse.toEnumConstant(useOp);
		ret.setMessageInput(new Message());
		ret.getMessageInput().setSoapNamespace(namespaceWsdlOp);
		ret.getMessageInput().setUse(use);

		// Se il profilo non è oneWay dobbiamo specificare anche in mesaggio di i uscita, la comunicazione diventa a due vie.
		if (ret.getProfiloCollaborazione() != ProfiloCollaborazione.ONEWAY) {
			ret.setMessageOutput(new Message());
			ret.getMessageOutput().setSoapNamespace(namespaceWsdlOp);
			ret.getMessageOutput().setUse(use);
		}

		return ret;
	}

	public static final ApiAzione operazioneToApiAzione(Operation op) {
		ApiAzione ret = new ApiAzione();

		ret.setNome(op.getNome());
		ret.setIdCollaborazione(op.getIdCollaborazione() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setRiferimentoIdRichiesta(op.getIdRiferimentoRichiesta() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setProfiloCollaborazione(Enums.profiloCollaborazioneApiFromRegistro.get(op.getProfiloCollaborazione()));
		ret.setProfiloRidefinito(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(op.getProfAzione()) ? true : false);

		return ret;
	}


	public static final PortType apiServizioToRegistro(ApiServizio body, AccordoServizioParteComune parent, ApiEnv env) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {

		PortType ret = new PortType();

		updatePortType(body, ret, env);

		ret.setScadenza("");
		ret.setConfermaRicezione(parent.getConfermaRicezione());
		ret.setConsegnaInOrdine(parent.getConsegnaInOrdine());

		return ret;
	}




	public static final ApiServizio servizioRegistroToApi(PortType pt) {
		ApiServizio ret = new ApiServizio();
		ret.setNome(pt.getNome());
		ret.setDescrizione(pt.getDescrizione());
		ret.setIdCollaborazione(Helper.statoFunzionalitaToBool(pt.getIdCollaborazione()));
		ret.setRiferimentoIdRichiesta(Helper.statoFunzionalitaToBool(pt.getIdRiferimentoRichiesta()));
		ret.setProfiloCollaborazione(Enums.profiloCollaborazioneApiFromRegistro.get(pt.getProfiloCollaborazione()));

		return ret;
	}


	public static final void updateRisorsa(ApiRisorsa body, Resource res) {

		res.setNome(body.getNome());
		res.setDescrizione(body.getDescrizione());

		// 1. se il path non inizia per '/' aggiungo all'inizio della stringa
		String pathNormalizzato = body.getPath();

		if(pathNormalizzato !=null && !"".equals(pathNormalizzato)) {
			pathNormalizzato = pathNormalizzato.trim();
			if(!pathNormalizzato.startsWith("/"))
				pathNormalizzato = "/" + pathNormalizzato;
		}

		res.setPath(pathNormalizzato);

		res.setMethod(Helper.apiEnumToGovway(body.getHttpMethod(), HttpMethod.class));

		if (res.getMethod() != null)
			res.set_value_method(res.getMethod().toString());

		if (StringUtils.isEmpty(res.getNome()) && res.getMethod() != null)
			res.setNome(APIUtils.normalizeResourceName(res.getMethod(), pathNormalizzato));

		res.setIdCollaborazione( Helper.boolToStatoFunzionalita(body.isIdCollaborazione()));
		res.setIdRiferimentoRichiesta( Helper.boolToStatoFunzionalita(body.isRiferimentoIdRichiesta()));		
		res.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
	}

	// Todo prendiFiltroDuplicati e roba dal parent.
	public static final Resource apiRisorsaToRegistro(ApiRisorsa body, AccordoServizioParteComune parent) {
		Resource ret = new Resource();

		ret.setMessageType(null);
		ret.setRequestMessageType(null);
		ret.setResponseMessageType(null);
		ret.setFiltroDuplicati(parent.getFiltroDuplicati());
		ret.setConfermaRicezione(parent.getConfermaRicezione());
		ret.setIdCollaborazione(parent.getIdCollaborazione());
		ret.setIdRiferimentoRichiesta(parent.getIdRiferimentoRichiesta());
		ret.setConsegnaInOrdine(parent.getConsegnaInOrdine());
		ret.setScadenza(parent.getScadenza());		

		updateRisorsa(body,ret);

		return ret;
	}


	public static final ApiRisorsa risorsaRegistroToApi(Resource r) {
		ApiRisorsa ret = new ApiRisorsa();

		ret.setNome(r.getNome());
		ret.setDescrizione(r.getDescrizione());

		ret.setHttpMethod( HttpMethodEnum.valueOf(r.get_value_method()) == null 
				? HttpMethodEnum.QUALSIASI 
						: HttpMethodEnum.valueOf(r.get_value_method())
				);

		ret.setIdCollaborazione(r.getIdCollaborazione() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setRiferimentoIdRichiesta(r.getIdRiferimentoRichiesta() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setPath(r.getPath());

		return ret;
	}

	public static final ApiItem apiToItem(Api api, AccordoServizioParteComuneSintetico as, ApiEnv env) {
		ApiItem ret = new ApiItem();

		ret.setDescrizione(api.getDescrizione());
		ret.setTipoInterfaccia(api.getTipoInterfaccia());
		ret.setNome(api.getNome());
		ret.setProfilo(env.profilo);	// TODO: In multitenant questo va cambiato al profilo relativo al tip del soggetto referente dell'as
		ret.setSoggetto(api.getReferente());
		ret.setVersione(api.getVersione());
		ret.setReferente(api.getReferente());


		StatoApiEnum stato = null;
		String descrizioneStato = "";
		Search searchForCount = new Search(true);
		switch (ret.getTipoInterfaccia().getProtocollo()) {
		case REST:
			// caso REST: l'API e' abilitata se ha almeno una risorsa
			try {
				env.apcCore.accordiResourceList(as.getId().intValue(), searchForCount);
			} catch (Exception e) { throw new RuntimeException(e); }

			int numRisorse = searchForCount.getNumEntries(Liste.ACCORDI_API_RESOURCES);

			if(numRisorse > 0) {
				stato = StatoApiEnum.OK;
			}
			else {
				stato = StatoApiEnum.ERROR;
				descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP;
			}
			break;
		case SOAP:
		default:

			List<PortType> accordiPorttypeList = null;
			try {
				accordiPorttypeList = env.apcCore.accordiPorttypeList(as.getId().intValue(), searchForCount);
			} catch (Exception e) { throw new RuntimeException(e); }

			int numeroTotaleServizi = accordiPorttypeList.size();
			int numeroServiziAbilitati = 0;

			for (PortType portType : accordiPorttypeList) {
				if(portType.sizeAzioneList()>0) {
					numeroServiziAbilitati ++;
				}	
			}

			if(numeroTotaleServizi == 0) {
				stato = StatoApiEnum.ERROR;
				descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP;
			}
			else if(numeroServiziAbilitati==0) {
				stato = StatoApiEnum.ERROR;
				if(numeroTotaleServizi==1) {
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP;
				}
				else {
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_SENZA_AZIONE_TOOLTIP;
				}
			} else if(numeroServiziAbilitati == numeroTotaleServizi) {
				stato = StatoApiEnum.OK;
			} 
			else {
				stato = StatoApiEnum.WARN;
				descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP;
			}
			break;
		}

		ret.setStatoDescrizione(descrizioneStato);
		ret.setStato(stato);

		if(as.getGruppo()!=null &&  !as.getGruppo().isEmpty()) {
			ret.setTags(new ArrayList<String>());
			for (GruppoSintetico tag : as.getGruppo()) {
				ret.addTagsItem(tag.getNome());
			}
		}

		if(env.gestioneCanali) {
			ApiCanale canale = new ApiCanale();
			if(as.getCanale()!=null && !"".equals(as.getCanale())) {
				canale.setNome(as.getCanale());
				canale.setConfigurazione(CanaleEnum.API);
			}
			else {
				canale.setNome(env.canaleDefault);
				canale.setConfigurazione(CanaleEnum.DEFAULT);
			}
			ret.setCanale(canale);
		}

		return ret;

	}

	public static final Api accordoSpcRegistroToApi(AccordoServizioParteComuneSintetico as, SoggettiCore soggettiCore) {
		Api ret = new Api();

		ret.setNome(as.getNome());
		ret.setDescrizione(as.getDescrizione());

		switch (as.getServiceBinding()) {
		case REST:
			ApiInterfacciaRest iRest = new ApiInterfacciaRest();
			iRest.setProtocollo(TipoApiEnum.REST);
			iRest.setFormato(Enums.formatoRestFromSpecifica.get(as.getFormatoSpecifica()));
			ret.setTipoInterfaccia(iRest);
			ret.setInterfaccia(as.getByteWsdlConcettuale());
			break;
		case SOAP:
			ApiInterfacciaSoap iSoap = new ApiInterfacciaSoap();
			iSoap.setProtocollo(TipoApiEnum.SOAP);
			iSoap.setFormato(Enums.formatoSoapFromSpecifica.get(as.getFormatoSpecifica()));
			ret.setTipoInterfaccia(iSoap);
			ret.setInterfaccia(as.getByteWsdlLogicoErogatore());
			break;
		}

		ret.setVersione(as.getVersione());
		ret.setReferente(as.getSoggettoReferente().getNome());

		return ret;	
	}


	// Versione "deprecata" in favore di quella più generica nell'Helper
	public static final AccordoServizioParteComune getAccordoFull(String nome, Integer versione, ApiEnv env) throws CoreException {
		return Helper.getAccordoFull(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);
	}
	public static final AccordoServizioParteComuneSintetico getAccordoSintetico(String nome, Integer versione, ApiEnv env) throws CoreException {
		return Helper.getAccordoSintetico(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);
	}

}
