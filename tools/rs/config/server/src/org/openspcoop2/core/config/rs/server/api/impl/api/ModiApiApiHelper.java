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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiModI;
import org.openspcoop2.core.config.rs.server.model.ApiModIAzioneSoap;
import org.openspcoop2.core.config.rs.server.model.ApiModIPatternInterazioneCorrelazioneRest;
import org.openspcoop2.core.config.rs.server.model.ApiModIPatternInterazioneCorrelazioneSoap;
import org.openspcoop2.core.config.rs.server.model.ApiModIPatternInterazioneRest;
import org.openspcoop2.core.config.rs.server.model.ApiModIPatternInterazioneSoap;
import org.openspcoop2.core.config.rs.server.model.ApiModIRisorsaRest;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaCanale;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioApplicabilitaCustom;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioOperazione;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioOperazioneRidefinito;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.ModIPatternInterazioneEnum;
import org.openspcoop2.core.config.rs.server.model.ModIPatternInterazioneFunzioneEnum;
import org.openspcoop2.core.config.rs.server.model.ModIPatternInterazioneTipoEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaCanaleEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaCustomEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioOperazioneEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestHeaderEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.protocol.engine.utils.AzioniUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.properties.ModIProfiliInterazioneRESTConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * ApiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ModiApiApiHelper {



	public static void populateApiAzioneWithProtocolInfo(AccordoServizioParteComune as, Operation az, ApiEnv env, ApiAzione ret) throws Exception {

		Map<String, AbstractProperty<?>> p = new HashMap<>();
		Optional<PortType> portType = as.getPortTypeList().stream().filter(pt -> pt.getId().equals(az.getIdPortType())).findAny();
				
		if(!portType.isPresent()) {
			throw FaultCode.ERRORE_INTERNO.toException("Port type non trovato");
		}
		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
		IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
		IDPortType idPortType = new IDPortType();
		idPortType.setIdAccordo(idAccordoFromAccordo);
		idPortType.setNome(portType.get().getNome());
		idPortTypeAzione.setIdPortType(idPortType);
		idPortTypeAzione.setNome(az.getNome());
		ConsoleConfiguration consoleConf = ApiApiHelper.getConsoleConfiguration(env, idPortTypeAzione);

		ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, az.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}

		ApiModIAzioneSoap apimodi = new ApiModIAzioneSoap();
		if(p!= null) {

			ApiModIPatternInterazioneSoap interazione = new ApiModIPatternInterazioneSoap();

			if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD)) {
				interazione.setPattern(ModIPatternInterazioneEnum.CRUD);
			} else if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE)) {
				interazione.setPattern(ModIPatternInterazioneEnum.BLOCCANTE);
			} else if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE)) {

				ModIPatternInterazioneTipoEnum tipo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL) ?
						ModIPatternInterazioneTipoEnum.PULL : ModIPatternInterazioneTipoEnum.PUSH;
				
				interazione.setTipo(tipo);

				ModIPatternInterazioneFunzioneEnum funzione = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA) ?
						ModIPatternInterazioneFunzioneEnum.RICHIESTA: ModIPatternInterazioneFunzioneEnum.RISPOSTA;
				
				interazione.setFunzione(funzione);
				
				if(funzione.equals(ModIPatternInterazioneFunzioneEnum.RISPOSTA)) {
					interazione.setAzioneCorrelata(getAzioneCorrelata(p, env));
				}
				
				interazione.setPattern(ModIPatternInterazioneEnum.NON_BLOCCANTE);
			}

			apimodi.setInterazione(interazione);


			if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, true).equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
				ApiModISicurezzaMessaggioOperazione op = new ApiModISicurezzaMessaggioOperazione();
				op.setStato(ModISicurezzaMessaggioOperazioneEnum.API);
				apimodi.setSicurezzaMessaggio(op);
			} else {

				ApiModISicurezzaMessaggioOperazioneRidefinito rid = new ApiModISicurezzaMessaggioOperazioneRidefinito();
				rid.setStato(ModISicurezzaMessaggioOperazioneEnum.RIDEFINITO);
				rid.setConfigurazione(getSicurezzaMessaggio(p, true));

				apimodi.setSicurezzaMessaggio(rid);
			}

		}

		ret.setModi(apimodi);

	}


	public static void populateApiRisorsaWithProtocolInfo(AccordoServizioParteComune as, Resource res, ApiEnv env, ApiRisorsa ret) throws Exception {

		Map<String, AbstractProperty<?>> p = new HashMap<>();
		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
		IDResource idres = new IDResource();
		idres.setIdAccordo(idAccordoFromAccordo);
		idres.setNome(res.getNome());
		ConsoleConfiguration consoleConf = ApiApiHelper.getConsoleConfiguration(env, idres, res.getMethod() != null ? res.getMethod().toString() : null, res.getPath());

		ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, res.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}

		ApiModIRisorsaRest apimodi = new ApiModIRisorsaRest();
		if(p!= null) {


			ApiModIPatternInterazioneRest interazione = new ApiModIPatternInterazioneRest();

			if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD)) {
				interazione.setPattern(ModIPatternInterazioneEnum.CRUD);
			} else if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE)) {
				interazione.setPattern(ModIPatternInterazioneEnum.BLOCCANTE);
			} else if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE)) {

				ModIPatternInterazioneTipoEnum tipo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL) ?
						ModIPatternInterazioneTipoEnum.PULL : ModIPatternInterazioneTipoEnum.PUSH;
				
				interazione.setTipo(tipo);

				ModIPatternInterazioneFunzioneEnum funzione = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA) ?
						ModIPatternInterazioneFunzioneEnum.RICHIESTA: ModIPatternInterazioneFunzioneEnum.RISPOSTA;
				
				interazione.setFunzione(funzione);
				
				if(funzione.equals(ModIPatternInterazioneFunzioneEnum.RISPOSTA)) {
					interazione.setRisorsaCorrelata(getRisorsaCorrelata(p, env));
				}
				
				interazione.setPattern(ModIPatternInterazioneEnum.NON_BLOCCANTE);
			}

			apimodi.setInterazione(interazione);


			if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, true).equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
				ApiModISicurezzaMessaggioOperazione op = new ApiModISicurezzaMessaggioOperazione();
				op.setStato(ModISicurezzaMessaggioOperazioneEnum.API);
				apimodi.setSicurezzaMessaggio(op);
			} else {

				ApiModISicurezzaMessaggioOperazioneRidefinito rid = new ApiModISicurezzaMessaggioOperazioneRidefinito();
				rid.setStato(ModISicurezzaMessaggioOperazioneEnum.RIDEFINITO);

				rid.setConfigurazione(getSicurezzaMessaggio(p, false));
				apimodi.setSicurezzaMessaggio(rid);
			}

		}

		ret.setModi(apimodi);

	}

	private static ApiModIPatternInterazioneCorrelazioneRest getRisorsaCorrelata(Map<String, AbstractProperty<?>> p, ApiEnv env) throws Exception {
		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromUri(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, true));
		IDResource id = new IDResource();
		id.setIdAccordo(idAccordoFromAccordo);
		id.setNome(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, true));
		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory);
		Resource resource = registryReader.getResourceAccordo(id);

		ApiModIPatternInterazioneCorrelazioneRest risorsaCorrelata = new ApiModIPatternInterazioneCorrelazioneRest();
		risorsaCorrelata.setApiNome(idAccordoFromAccordo.getNome());
		risorsaCorrelata.setApiVersione(idAccordoFromAccordo.getVersione());
		
		HttpMethodEnum method = HttpMethodEnum.QUALSIASI;			
		
		if(resource.getMethod()!=null) {
			switch(resource.getMethod()) {
			case DELETE: method = HttpMethodEnum.DELETE;
				break;
			case GET: method = HttpMethodEnum.GET;
				break;
			case HEAD: method = HttpMethodEnum.HEAD;
				break;
			case LINK: method = HttpMethodEnum.LINK;
				break;
			case OPTIONS: method = HttpMethodEnum.OPTIONS;
				break;
			case PATCH: method = HttpMethodEnum.PATCH;
				break;
			case POST: method = HttpMethodEnum.POST;
				break;
			case PUT: method = HttpMethodEnum.PUT;
				break;
			case TRACE: method = HttpMethodEnum.TRACE;
				break;
			case UNLINK: method = HttpMethodEnum.UNLINK;
				break;}
		}
		
		risorsaCorrelata.setRisorsaHttpMethod(method);
		risorsaCorrelata.setRisorsaPath(resource.getPath());
		
		return risorsaCorrelata;
	}

	private static ApiModIPatternInterazioneCorrelazioneSoap getAzioneCorrelata(Map<String, AbstractProperty<?>> p, ApiEnv env) throws Exception {

		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromUri(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, true));

		ApiModIPatternInterazioneCorrelazioneSoap risorsaCorrelata = new ApiModIPatternInterazioneCorrelazioneSoap();
		risorsaCorrelata.setApiNome(idAccordoFromAccordo.getNome());
		risorsaCorrelata.setApiVersione(idAccordoFromAccordo.getVersione());
		
		risorsaCorrelata.setServizio(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA, true));
		risorsaCorrelata.setAzione(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, true));
		
		return risorsaCorrelata;
	}


	public static ApiModI getApiModI(AccordoServizioParteComune as, ProfiloEnum profilo, ApiEnv env) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		Map<String, AbstractProperty<?>> p = new HashMap<>();
		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
		ConsoleConfiguration consoleConf = ApiApiHelper.getConsoleConfiguration(env, idAccordoFromAccordo);

		ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, as.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}


		ApiModI apimodi = new ApiModI();
		if(p!= null) {
			ApiModISicurezzaCanale sicurezzaCanale = new ApiModISicurezzaCanale();

			String sicurezzaCanalePatternString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE, true);

			sicurezzaCanale.setPattern(sicurezzaCanalePatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01) ? ModISicurezzaCanaleEnum.AUTH01:ModISicurezzaCanaleEnum.AUTH02);
			apimodi.setSicurezzaCanale(sicurezzaCanale);

			apimodi.setSicurezzaMessaggio(getSicurezzaMessaggio(p, as.getServiceBinding().equals(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP)));

		}

		return apimodi;
	}

	private static ApiModISicurezzaMessaggio getSicurezzaMessaggio(Map<String, AbstractProperty<?>> p, boolean isSOAP) throws Exception {
		ApiModISicurezzaMessaggio sicurezzaMessaggio = new ApiModISicurezzaMessaggio();

		String sicurezzaMessaggioPatternString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, true);

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

		
		if(profiloSicurezzaMessaggioPattern!=null && !profiloSicurezzaMessaggioPattern.equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {
			if(isSOAP) {
				populateSicurezzaMessaggioSOAP(p, sicurezzaMessaggio);
			} else {
				populateSicurezzaMessaggioREST(p, sicurezzaMessaggio);
			}

			sicurezzaMessaggio.setDigestRichiesta(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
			sicurezzaMessaggio.setInformazioniUtente(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

		} else {
			sicurezzaMessaggio.setInformazioniUtente(false);
			sicurezzaMessaggio.setDigestRichiesta(false);
			sicurezzaMessaggio.setSoapFirmaAllegati(false);
		}
		
		return sicurezzaMessaggio;

	}

	private static void populateSicurezzaMessaggioSOAP(Map<String, AbstractProperty<?>> p, ApiModISicurezzaMessaggio sicurezzaMessaggio) throws Exception {
		ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

		String sicurezzaMessaggioApplicabilitaString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

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
		
	}

	private static void populateSicurezzaMessaggioREST(Map<String, AbstractProperty<?>> p, ApiModISicurezzaMessaggio sicurezzaMessaggio) throws Exception {
		ModISicurezzaMessaggioRestHeaderEnum restHeader = null;
		String restHeaderString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, true);

		boolean custom = false;
		if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID;
		} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.BEARER;
		} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID_BEARER_REQUEST;
		} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID_BEARER;
		} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.CUSTOM;
			custom = true;
		} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.CUSTOM_BEARER_REQUEST;
			custom = true;
		} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE)) {
			restHeader = ModISicurezzaMessaggioRestHeaderEnum.CUSTOM_BEARER;
			custom = true;
		}
		sicurezzaMessaggio.setRestHeader(restHeader);
		
		if(custom) {
			String restHeaderCustomString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM, true);
			sicurezzaMessaggio.setRestHeaderCustom(restHeaderCustomString);
		}

		ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

		String sicurezzaMessaggioApplicabilitaString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

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
			String applicabilitaCustomRichiestaString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, true);
			ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRichiesta = null;

			if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO)) {
				applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
			} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO)) {
				applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
			} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO)) {
				applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;
				appCustom.setRichiestaContentType(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, true));
			} 

			appCustom.setRichiesta(applicabilitaCustomRichiesta);


			String applicabilitaCustomRispostaString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, true);
			ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRisposta = null;

			if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO)) {
				applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
			} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO)) {
				applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
			} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO)) {
				applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;

				appCustom.setRispostaContentType(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, true));
				appCustom.setRispostaCodice(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, true));
			} 

			appCustom.setRisposta(applicabilitaCustomRisposta);

			sicurezzaMessaggio.setApplicabilitaCustom(appCustom);
		}
		
		sicurezzaMessaggio.setApplicabilita(applicabilita);


	}

	public static ProtocolProperties getProtocolProperties(ApiAzione body, AccordoServizioParteComune as, Operation op, ApiEnv env) throws Exception {
		return getSOAPModiProtocolProperties(body.getModi(), as, op, env);
	}


	public static ProtocolProperties getProtocolProperties(ApiRisorsa body, Resource res, ApiEnv env) throws Exception {
		return getRESTModiProtocolProperties(body.getModi(), res, env);
	}

	public static ProtocolProperties getRESTModiProtocolProperties(ApiModIRisorsaRest modi, Resource res, ApiEnv env) throws Exception {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}
		
		ModIProfiliInterazioneRESTConfig config = null;
		try {
			config = new ModIProfiliInterazioneRESTConfig(ModIProperties.getInstance(),  res.getMethod() != null ? res.getMethod().toString() : null, res);
		} catch (ProtocolException e) {
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}

		ProtocolProperties p = new ProtocolProperties();

		if(modi.getInterazione().getPattern().equals(ModIPatternInterazioneEnum.BLOCCANTE)) {
			if(!config.isCompatibileBloccante()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
			} 

			p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);

		} else if(modi.getInterazione().getPattern().equals(ModIPatternInterazioneEnum.NON_BLOCCANTE)) {

			boolean isPull = modi.getInterazione().getTipo().equals(ModIPatternInterazioneTipoEnum.PULL);
			boolean isRichiesta = modi.getInterazione().getFunzione().equals(ModIPatternInterazioneFunzioneEnum.RICHIESTA);
			
			if(isPull) {
				if(!config.isCompatibileNonBloccantePull()) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
				} 

				p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL);
				
				if(isRichiesta) {
					if(!config.isCompatibileNonBloccantePullRequest()) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
					} 
					
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA);
				} else {
					if(!config.isCompatibileNonBloccantePullResponse()) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
					} 
					
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
				}
			} else {
				if(!config.isCompatibileNonBloccantePush()) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
				} 
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH);
				
				
				if(isRichiesta) {
					if(!config.isCompatibileNonBloccantePushRequest()) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
					} 
					
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA);
				} else {
					if(!config.isCompatibileNonBloccantePushResponse()) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern "+modi.getInterazione().getPattern()+" non compatibile");
					} 
					
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
				}
				
			}

			if(!isRichiesta) {
				addRisorsaCorrelata(modi.getInterazione().getRisorsaCorrelata(), env, p, isPull);
			}

			p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
			
		} else if(modi.getInterazione().getPattern().equals(ModIPatternInterazioneEnum.CRUD)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		}


		if(modi.getSicurezzaMessaggio().getStato().equals(ModISicurezzaMessaggioOperazioneEnum.API)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			ApiModISicurezzaMessaggioOperazioneRidefinito rid = ((ApiModISicurezzaMessaggioOperazioneRidefinito)modi.getSicurezzaMessaggio());

			if(!rid.getConfigurazione().getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {
				getRESTProperties(rid.getConfigurazione(), p);
			}
		}

		return p;
	}


	private static void addRisorsaCorrelata(ApiModIPatternInterazioneCorrelazioneRest corr, ApiEnv env, ProtocolProperties p, boolean isPull) throws Exception {

		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromValues(corr.getApiNome(), env.idSoggetto.getTipo(),env.idSoggetto.getNome(),corr.getApiVersione());
		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory);
		AccordoServizioParteComune accordoServizioFull = env.apcCore.getAccordoServizioFull(idAccordoFromAccordo);
		Resource found = null;
		
		HttpMethod method = null;
		
		switch(corr.getRisorsaHttpMethod()) {
		case DELETE: method = HttpMethod.DELETE;
			break;
		case GET:method = HttpMethod.GET;
			break;
		case HEAD:method = HttpMethod.HEAD;
			break;
		case LINK:method = HttpMethod.LINK;
			break;
		case OPTIONS:method = HttpMethod.OPTIONS;
			break;
		case PATCH:method = HttpMethod.PATCH;
			break;
		case POST:method = HttpMethod.POST;
			break;
		case PUT:method = HttpMethod.PUT;
			break;
		case TRACE: method = HttpMethod.TRACE;
			break;
		case UNLINK:method = HttpMethod.UNLINK;
			break;
		default:
			break;
		}
		for(Resource r: accordoServizioFull.getResourceList()) {
			if(sameResource(r, method, corr.getRisorsaPath())) {
				found = r;
			}
		}
		
		if(found == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Azione non trovata");				
		}
		
		String nomeRisorsa = found.getNome();
		checkAzione(idAccordoFromAccordo, null, nomeRisorsa, isPull, registryReader);

		p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, idAccordoFromAccordo.toString());
		p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, nomeRisorsa);
		p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED);

	}


	private static boolean sameResource(Resource r, HttpMethod method, String path) {
		
		boolean equalsMethod = false;
		if(r.getMethod() == null) {
			equalsMethod = method == null;
		} else {
			equalsMethod = r.getMethod().equals(method);
		}
		
		return equalsMethod && r.getPath().equals(path);
	}


	public static ProtocolProperties getSOAPModiProtocolProperties(ApiModIAzioneSoap modi, AccordoServizioParteComune as, Operation op, ApiEnv env) throws Exception {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(modi.getInterazione().getPattern().equals(ModIPatternInterazioneEnum.BLOCCANTE)) {

			if(modi.getInterazione().getTipo()!= null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile specificare interazione.tipo con pattern " + modi.getInterazione().getPattern());
			}

			if(modi.getInterazione().getFunzione()!= null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile specificare interazione.funzione con pattern " + modi.getInterazione().getPattern());
			}
			
			if(modi.getInterazione().getAzioneCorrelata()!=null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile specificare interazione.azione_correlata con funzione " + modi.getInterazione().getFunzione());
			}

			p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);

		} else if(modi.getInterazione().getPattern().equals(ModIPatternInterazioneEnum.NON_BLOCCANTE)) {
			
			boolean isPull = modi.getInterazione().getTipo().equals(ModIPatternInterazioneTipoEnum.PULL);
			boolean isRichiesta = modi.getInterazione().getFunzione().equals(ModIPatternInterazioneFunzioneEnum.RICHIESTA);
			
			if(isPull) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL);
				
				if(isRichiesta) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA);
				} else {
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
				}
			} else {
				p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH);
				
				
				if(isRichiesta) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA);
				} else {
					p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
				}
			}

			if(!isRichiesta) {
				addAzioneCorrelata(modi.getInterazione().getAzioneCorrelata(), env, p, isPull);
			}

			p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
			
		} else if(modi.getInterazione().getPattern().equals(ModIPatternInterazioneEnum.CRUD)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Pattern interazione CRUD non permesso per servizi SOAP");
		}


		if(modi.getSicurezzaMessaggio().getStato().equals(ModISicurezzaMessaggioOperazioneEnum.API)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
		} else {
			ApiModISicurezzaMessaggioOperazioneRidefinito rid = ((ApiModISicurezzaMessaggioOperazioneRidefinito)modi.getSicurezzaMessaggio());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			getSOAPProperties(rid.getConfigurazione(), p);

		}

		return p;
	}

	private static void addAzioneCorrelata(ApiModIPatternInterazioneCorrelazioneSoap azioneCorrelata, ApiEnv env,
			ProtocolProperties p, boolean isPull) throws Exception {
		
		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromValues(azioneCorrelata.getApiNome(), env.idSoggetto.getTipo(),env.idSoggetto.getNome(),azioneCorrelata.getApiVersione());
		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory);
		
		checkAzione(idAccordoFromAccordo, azioneCorrelata.getServizio(), azioneCorrelata.getAzione(), isPull, registryReader);

		p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, idAccordoFromAccordo.toString());
		p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA, azioneCorrelata.getServizio());
		p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, azioneCorrelata.getAzione());

	}


	private static void checkAzione(IDAccordo idAccordoFromAccordo, String servizio, String azione, boolean isPull, IRegistryReader registryReader) throws Exception {
		
		AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
		asps.setTipoSoggettoErogatore(idAccordoFromAccordo.getSoggettoReferente().getTipo());
		asps.setNomeSoggettoErogatore(idAccordoFromAccordo.getSoggettoReferente().getNome());
		asps.setNome(idAccordoFromAccordo.getNome());
		asps.setVersione(idAccordoFromAccordo.getVersione());
		asps.setPortType(servizio);
		AccordoServizioParteComune as = registryReader.getAccordoServizioParteComune(idAccordoFromAccordo,false,false);

		AccordoServizioParteComuneSintetico aspc = new AccordoServizioParteComuneSintetico(as);

		Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspc, false, false, 
		new ArrayList<>(), ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, org.slf4j.LoggerFactory.getLogger(ApiApiServiceImpl.class));
		
		
		
		if(azioni!=null && !azioni.isEmpty()) {
			
			for (String azioneId : azioni.keySet()) {
				String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(as, servizio, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
				if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
					continue;
				}
				String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(as, servizio, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
				if(isPull != ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(tmpRuolo)) {
					continue;
				}
				String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(as, servizio, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
				if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
					continue;
				}
				
				if(azioneId.equals(azione)) {
					return;
				}
			}
		}

		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Azione non correlabile");				
		
	}


	public static ProtocolProperties getProtocolProperties(Api body) {
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

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties(modi.getSicurezzaMessaggio(), p);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties(modi.getSicurezzaMessaggio(), p);
		}


		return p;
	}


	private static void getRESTProperties(ApiModISicurezzaMessaggio sicurezzaMessaggio, ProtocolProperties p) {
	
		String profiloSicurezzaMessaggio = null;

		switch(sicurezzaMessaggio.getPattern()) {
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


		if(sicurezzaMessaggio.isSoapFirmaAllegati()!= null && 
				sicurezzaMessaggio.isSoapFirmaAllegati()) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con servizio di tipo REST");
		}

		if(!sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

			if(sicurezzaMessaggio.getRestHeader() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern " + sicurezzaMessaggio.getPattern());
			}

			boolean integrity = sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01) || 
					sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02);


			boolean applicabilitaInfoUtente = false;
			boolean applicabilitaDigest = false;


			String headerHTTPREST = "";
			boolean custom = false;
			switch(sicurezzaMessaggio.getRestHeader()) {
				case AGID: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA;
				break;
				case BEARER: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION;
				break;
				case AGID_BEARER_REQUEST: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA;
				break;
				case AGID_BEARER: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE;
				break;
				case CUSTOM: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM;
				custom=true;
				break;
				case CUSTOM_BEARER_REQUEST: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM;
				custom=true;
				break;
				case CUSTOM_BEARER: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE;
				custom=true;
				break;
			}
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, headerHTTPREST);
			
			if(custom && sicurezzaMessaggio.getRestHeaderCustom()!=null) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM, sicurezzaMessaggio.getRestHeaderCustom());
			}


			String applicabilita = "";


			if(sicurezzaMessaggio.getApplicabilita() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita deve essere specificato");
			}
			
			if(sicurezzaMessaggio.getApplicabilita().equals(ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM)) {

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO);

				if(sicurezzaMessaggio.getApplicabilitaCustom() == null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}

				ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = sicurezzaMessaggio.getApplicabilitaCustom();

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

				applicabilitaDigest = true;
				applicabilitaInfoUtente= true;

				if((sicurezzaMessaggio.isInformazioniUtente() != null && sicurezzaMessaggio.isInformazioniUtente())) {

					if(integrity && applicabilitaInfoUtente) {
						cornicePropValue =  sicurezzaMessaggio.isInformazioniUtente();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

				if((sicurezzaMessaggio.isDigestRichiesta() != null && sicurezzaMessaggio.isDigestRichiesta())) {

					if(integrity && applicabilitaDigest) {
						digestPropValue = sicurezzaMessaggio.isDigestRichiesta();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

			} else {

				if(sicurezzaMessaggio.getApplicabilitaCustom() != null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom non deve essere specificato con servizio di tipo REST e applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}

				switch(sicurezzaMessaggio.getApplicabilita()) {
				case CUSTOM: //gestito nell'altro ramo if
					break;
				case QUALSIASI: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI; applicabilitaDigest = true; applicabilitaInfoUtente= true;
				break;
				case RICHIESTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; applicabilitaInfoUtente= true;
				break;
				case RISPOSTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
				break;
				}

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);

				if((sicurezzaMessaggio.isInformazioniUtente() != null && sicurezzaMessaggio.isInformazioniUtente())) {

					if(integrity && applicabilitaInfoUtente) {
						cornicePropValue =  sicurezzaMessaggio.isInformazioniUtente();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

				if((sicurezzaMessaggio.isDigestRichiesta() != null && sicurezzaMessaggio.isDigestRichiesta())) {

					if(integrity && applicabilitaDigest) {
						digestPropValue = sicurezzaMessaggio.isDigestRichiesta();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

			}
			p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
			p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));

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


	private static void getSOAPProperties(ApiModISicurezzaMessaggio sicurezzaMessaggio, ProtocolProperties p) {
		
		String profiloSicurezzaMessaggio = null;

		switch(sicurezzaMessaggio.getPattern()) {
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

		if(sicurezzaMessaggio.getRestHeader() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP");
		}


		if(!sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

			String applicabilita = "";

			boolean integrity = sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01) || 
					sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02);

			if(sicurezzaMessaggio.isSoapFirmaAllegati() && !integrity) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con pattern " + sicurezzaMessaggio.getPattern());
			}

			boolean applicabilitaInfoUtente = false;
			boolean applicabilitaDigest = false;

			if(sicurezzaMessaggio.getApplicabilita()!=null) {
				switch(sicurezzaMessaggio.getApplicabilita()) {
				case CUSTOM: throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP");
				case QUALSIASI: applicabilita = sicurezzaMessaggio.isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI; applicabilitaInfoUtente = true; applicabilitaDigest = true;
				break;
				case RICHIESTA: applicabilita = sicurezzaMessaggio.isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; applicabilitaInfoUtente = true;
				break;
				case RISPOSTA: applicabilita = sicurezzaMessaggio.isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
				break;
				}
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
			} else {
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_DEFAULT);
				applicabilitaInfoUtente = true;
				applicabilitaDigest = true;
			}

			if((sicurezzaMessaggio.isInformazioniUtente() != null && sicurezzaMessaggio.isInformazioniUtente())) {

				if(integrity && applicabilitaInfoUtente) {
					cornicePropValue =  sicurezzaMessaggio.isInformazioniUtente();
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}
			}

			if((sicurezzaMessaggio.isDigestRichiesta() != null && sicurezzaMessaggio.isDigestRichiesta())) {

				if(integrity && applicabilitaDigest) {
					digestPropValue = sicurezzaMessaggio.isDigestRichiesta();
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
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

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));
	}
}
