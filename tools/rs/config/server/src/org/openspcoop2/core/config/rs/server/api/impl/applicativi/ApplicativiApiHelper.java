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
package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.rs.server.api.impl.ApiKeyInfo;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ConnettoreAPIHelper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.ApplicativoItem;
import org.openspcoop2.core.config.rs.server.model.ApplicativoServer;
import org.openspcoop2.core.config.rs.server.model.ApplicativoServerItem;
import org.openspcoop2.core.config.rs.server.model.AuthenticationApiKey;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.BaseCredenziali;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfBaseCredenzialiCredenziali;
import org.openspcoop2.core.config.rs.server.model.Proprieta4000;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.pdd.core.autenticazione.ApiKey;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ApplicativiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApplicativiApiHelper {
	
	public static void validateCredentials(OneOfBaseCredenzialiCredenziali creds, boolean dominioInterno, boolean profiloModi) throws Exception {
		if(creds!=null && creds instanceof AuthenticationHttps) {
			AuthenticationHttps https = (AuthenticationHttps) creds;
			if(https.getToken()!=null && (dominioInterno || !profiloModi)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile definire anche una configurazione 'token' alle credenziali di tipo '"+creds.getModalitaAccesso()+"' se l'applicativo non è di profilo ModI e di dominio esterno");
			}
		}
	}

	public static void overrideSAParameters(HttpRequestWrapper wrap, ConsoleHelper consoleHelper, ServizioApplicativo sa, Applicativo applicativo,
			ApiKeyInfo apiKeyInfo, boolean updateKey) {
		overrideSAParameters(wrap, consoleHelper, sa, applicativo.getCredenziali(),
				apiKeyInfo, updateKey);
	}
	public static void overrideSAParameters(HttpRequestWrapper wrap, ConsoleHelper consoleHelper, ServizioApplicativo sa, OneOfBaseCredenzialiCredenziali credenzialiApiRest,
			ApiKeyInfo apiKeyInfo, boolean updateKey) {
		Credenziali credenziali = sa.getInvocazionePorta().getCredenziali(0);
		
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME, sa.getNome());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT, ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP);
		
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE, credenziali.getTipo().toString());
		if(CredenzialeTipo.SSL.equals(credenziali.getTipo())){
			if(credenziali.getTokenPolicy()!=null && StringUtils.isNotEmpty(credenziali.getTokenPolicy())){
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE,  ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH);
			}
		}
		
		if(credenzialiApiRest!=null) {
			
			Helper.overrideAuthParams(wrap, consoleHelper, credenzialiApiRest,
					apiKeyInfo, updateKey);
		}
	}
    
	public static void overrideSAParametersApplicativoServer(HttpRequestWrapper wrap, ConsoleHelper consoleHelper, ServizioApplicativo sa, boolean updateKey) {
//		Credenziali credenziali = sa.getInvocazionePorta().getCredenziali(0);
		
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME, sa.getNome());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT, ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP);
		
//		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE, credenziali.getTipo().toString());
		
	}
    
	
	public static ServizioApplicativo applicativoToServizioApplicativo(
			ApplicativoServer applicativo,
			String tipoProtocollo,
			String soggetto,
			ControlStationCore stationCore) throws UtilsException, DriverControlStationException, ProtocolException, DriverRegistroServiziException, DriverRegistroServiziNotFound {

		ServerProperties serverProperties = ServerProperties.getInstance();
	
		if(stationCore!=null) {
			// nop
		}
		
		ControlStationCore core = new ControlStationCore(true, serverProperties.getConfDirectory(),tipoProtocollo); 
		SoggettiCore soggettiCore = new SoggettiCore(core);	

		String tipoSoggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipoProtocollo);
		IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto,soggetto);
		Soggetto soggettoRegistro = soggettiCore.getSoggettoRegistro(idSoggetto);
		
	
		//soggettoRegistro.get
	    ServizioApplicativo sa = new ServizioApplicativo();
	
	    sa.setDescrizione(applicativo.getDescrizione());
	    
	    sa.setNome(applicativo.getNome());
	    sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
	    sa.setTipo(CostantiConfigurazione.SERVER);
		sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());	
		
	    //Inseriamo il soggetto del registro locale
	    sa.setIdSoggetto(soggettoRegistro.getId());
	    sa.setNomeSoggettoProprietario(soggettoRegistro.getNome());
	    sa.setTipoSoggettoProprietario(soggettoRegistro.getTipo());	    
	    
	    // *** risposta asinc ***
		InvocazioneCredenziali credenzialiInvocazione = new InvocazioneCredenziali();
		credenzialiInvocazione.setUser("");
		credenzialiInvocazione.setPassword("");
		
		RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
		rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		rispostaAsinc.setCredenziali(credenzialiInvocazione);
		rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
	
		sa.setRispostaAsincrona(rispostaAsinc);
		
		InvocazioneServizio invServizio = new InvocazioneServizio();
		invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		invServizio.setCredenziali(credenzialiInvocazione);
		invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
		
		invServizio.setConnettore(new Connettore());
		
		sa.setInvocazioneServizio(invServizio);
		
		// *** Invocazione Porta ***
		InvocazionePorta invocazionePorta = new InvocazionePorta();
		//		List<Credenziali> credenziali = credenzialiFromAuth(applicativo.getCredenziali(), keyInfo);
//
//		invocazionePorta.getCredenzialiList().add(credenziali);
		
	    //Imposto i ruoli
//		FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
//		filtroRuoli.setContesto(RuoloContesto.QUALSIASI); // gli applicativi possono essere usati anche nelle erogazioni.
//		filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
//	
//		List<String> allRuoli = stationCore.getAllRuoli(filtroRuoli);
//		final ServizioApplicativoRuoli ruoli = invocazionePorta.getRuoli() == null ? new ServizioApplicativoRuoli() : invocazionePorta.getRuoli();
//		
//		if (applicativo.getRuoli() != null) {
//			applicativo.getRuoli().forEach( nome -> {
//				
//				if (!allRuoli.contains(nome)) {
//					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo di nome " + nome + " non è presente o assegnabile al servizio applicativo.");
//				}
//				Ruolo r = new Ruolo();
//				r.setNome(nome);
//				ruoli.addRuolo(r);
//			});
//		}
//		invocazionePorta.setRuoli(ruoli);
			
		final String fault = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP;
		
		InvocazionePortaGestioneErrore ipge = new InvocazionePortaGestioneErrore();
		ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
		invocazionePorta.setGestioneErrore(ipge);
		
		invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(""));

		sa.setInvocazionePorta(invocazionePorta);
		
		// *** proprieta ***
		if(applicativo.getProprieta()!=null && !applicativo.getProprieta().isEmpty()) {
			for (Proprieta4000 proprieta : applicativo.getProprieta()) {
				org.openspcoop2.core.config.Proprieta pConfig = new org.openspcoop2.core.config.Proprieta();
				pConfig.setNome(proprieta.getNome());
				pConfig.setValore(proprieta.getValore());
				sa.addProprieta(pConfig);
			}
		}
		
	    return sa;

	}
    
	
	public static ServizioApplicativo applicativoToServizioApplicativo(
			Applicativo applicativo,
			String tipoProtocollo,
			String soggetto,
			ControlStationCore stationCore, 
			ApiKeyInfo keyInfo) throws UtilsException, DriverControlStationException, ProtocolException, DriverRegistroServiziException, DriverRegistroServiziNotFound, IllegalAccessException, InvocationTargetException, InstantiationException {

		ServerProperties serverProperties = ServerProperties.getInstance();
	
		
		ControlStationCore core = new ControlStationCore(true, serverProperties.getConfDirectory(),tipoProtocollo); 
		SoggettiCore soggettiCore = new SoggettiCore(core);	

		String tipoSoggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipoProtocollo);
		IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto,soggetto);
		Soggetto soggettoRegistro = soggettiCore.getSoggettoRegistro(idSoggetto);
		
	
		//soggettoRegistro.get
	    ServizioApplicativo sa = new ServizioApplicativo();
	
	    sa.setDescrizione(applicativo.getDescrizione());
	    
	    sa.setNome(applicativo.getNome());
	    sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
	    sa.setTipo(CostantiConfigurazione.CLIENT);
		sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());	
		
	    //Inseriamo il soggetto del registro locale
	    sa.setIdSoggetto(soggettoRegistro.getId());
	    sa.setNomeSoggettoProprietario(soggettoRegistro.getNome());
	    sa.setTipoSoggettoProprietario(soggettoRegistro.getTipo());	    
	    
	    // *** risposta asinc ***
		InvocazioneCredenziali credenzialiInvocazione = new InvocazioneCredenziali();
		credenzialiInvocazione.setUser("");
		credenzialiInvocazione.setPassword("");
		
		RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
		rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		rispostaAsinc.setCredenziali(credenzialiInvocazione);
		rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
	
		sa.setRispostaAsincrona(rispostaAsinc);
		
		InvocazioneServizio invServizio = new InvocazioneServizio();
		invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		invServizio.setCredenziali(credenzialiInvocazione);
		invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
		
		sa.setInvocazioneServizio(invServizio);
		
		// *** Invocazione Porta ***
		InvocazionePorta invocazionePorta = new InvocazionePorta();
		List<Credenziali> credenziali = credenzialiFromAuth(applicativo.getCredenziali(), keyInfo);

		invocazionePorta.getCredenzialiList().addAll(credenziali);
		
	    //Imposto i ruoli
		FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
		filtroRuoli.setContesto(RuoloContesto.QUALSIASI); // gli applicativi possono essere usati anche nelle erogazioni.
		filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
	
		List<String> allRuoli = stationCore.getAllRuoli(filtroRuoli);
		final ServizioApplicativoRuoli ruoli = invocazionePorta.getRuoli() == null ? new ServizioApplicativoRuoli() : invocazionePorta.getRuoli();
		
		if (applicativo.getRuoli() != null) {
			applicativo.getRuoli().forEach( nome -> {
				
				if (!allRuoli.contains(nome)) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo di nome " + nome + " non è presente o assegnabile al servizio applicativo.");
				}
				Ruolo r = new Ruolo();
				r.setNome(nome);
				ruoli.addRuolo(r);
			});
		}
		invocazionePorta.setRuoli(ruoli);
			
		final String fault = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP;
		
		InvocazionePortaGestioneErrore ipge = new InvocazionePortaGestioneErrore();
		ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
		invocazionePorta.setGestioneErrore(ipge);
		
		invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(""));

		sa.setInvocazionePorta(invocazionePorta);
		
		// *** proprieta ***
		if(applicativo.getProprieta()!=null && !applicativo.getProprieta().isEmpty()) {
			for (Proprieta4000 proprieta : applicativo.getProprieta()) {
				org.openspcoop2.core.config.Proprieta pConfig = new org.openspcoop2.core.config.Proprieta();
				pConfig.setNome(proprieta.getNome());
				pConfig.setValore(proprieta.getValore());
				sa.addProprieta(pConfig);
			}
		}
		
	    return sa;
	}
	
	
	public static final Applicativo servizioApplicativoToApplicativo(ServizioApplicativo sa) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Applicativo ret = new Applicativo();
		
		ret.setNome(sa.getNome());

		ret.setDescrizione(sa.getDescrizione());
		
		InvocazionePorta invPorta = sa.getInvocazionePorta();
		if (invPorta != null) {
			ServizioApplicativoRuoli ruoli = invPorta.getRuoli();
			if (ruoli != null) {
				ret.setRuoli(ruoli.getRuoloList().stream().map( ruolo -> ruolo.getNome().toString() ).collect(Collectors.toList()));
			}
		}
	
		if(invPorta!=null) {
			ret.setCredenziali(authFromCredenziali(invPorta.getCredenzialiList()));
		}
			
		if(sa.sizeProprietaList()>0) {
			for (org.openspcoop2.core.config.Proprieta proprieta : sa.getProprietaList()) {
				Proprieta4000 p = new Proprieta4000();
				p.setNome(proprieta.getNome());
				p.setValore(proprieta.getValore());
				if(ret.getProprieta()==null) {
					ret.setProprieta(new ArrayList<Proprieta4000>());
				}
				ret.addProprietaItem(p);
			}
		}
		
		return ret;
	}
	
	public static final ApplicativoServer servizioApplicativoToApplicativoServer(ServizioApplicativo sa) throws Exception {
		ApplicativoServer ret = new ApplicativoServer();
		
		ret.setNome(sa.getNome());

		ret.setDescrizione(sa.getDescrizione());
		
		ret.setConnettore(ConnettoreAPIHelper.getConnettoreApplicativoServer(sa));
			
		if(sa.sizeProprietaList()>0) {
			for (org.openspcoop2.core.config.Proprieta proprieta : sa.getProprietaList()) {
				Proprieta4000 p = new Proprieta4000();
				p.setNome(proprieta.getNome());
				p.setValore(proprieta.getValore());
				if(ret.getProprieta()==null) {
					ret.setProprieta(new ArrayList<Proprieta4000>());
				}
				ret.addProprietaItem(p);
			}
		}
		
		return ret;
	}
	
	public static Map<String, AbstractProperty<?>> getProtocolPropertiesMap(ServizioApplicativo sa, ApplicativiEnv env) throws Exception {

		ProtocolProperties prop = getProtocolProperties(sa, env);
		Map<String, AbstractProperty<?>> p = new HashMap<>();

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}
		
		return p;
	}

	public static ProtocolProperties getProtocolProperties(ServizioApplicativo sa, ApplicativiEnv env) throws Exception {
		ConsoleConfiguration consoleConf = getConsoleConfiguration(sa, env);

		ProtocolProperties prop = env.saHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesConfig(prop, sa.getProtocolPropertyList(), ConsoleOperationType.CHANGE);
		return prop;
	}

	public static ConsoleConfiguration getConsoleConfiguration(ServizioApplicativo sa, ApplicativiEnv env) throws Exception {
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		IDServizioApplicativo idSA = new IDServizioApplicativo();
		idSA.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
		idSA.setNome(sa.getNome());
		
		return consoleDynamicConfiguration.getDynamicConfigServizioApplicativo(ConsoleOperationType.ADD, env.saHelper, 
				registryReader, configRegistryReader, idSA);

	}

	public static ProtocolProperties getProtocolProperties(Applicativo body, ProfiloEnum profilo, ServizioApplicativo sa, ApplicativiEnv env) throws Exception {


		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliveryApplicativiApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPAApplicativiApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiApplicativiApiHelper.getProtocolProperties(body, sa, env);
		case SPCOOP:
			return SPCoopApplicativiApiHelper.getProtocolProperties(body);
		}
		return null;
	}


	public static void populateProtocolInfo(ServizioApplicativo sa, Applicativo ret, ApplicativiEnv env, ProfiloEnum profilo) throws Exception {
		
		if(profilo != null) {
			switch(profilo) {
			case APIGATEWAY: 
				break;
			case EDELIVERY: EDeliveryApplicativiApiHelper.populateProtocolInfo(sa, env, ret);
				break;
			case FATTURAPA: FatturaPAApplicativiApiHelper.populateProtocolInfo(sa, env, ret);
				break;
			case MODI: 
			case MODIPA: ModiApplicativiApiHelper.populateProtocolInfo(sa, env, ret);
				break;
			case SPCOOP: SPCoopApplicativiApiHelper.populateProtocolInfo(sa, env, ret);
				break;
			default:
				break;}
		}

	}
	// Rationale: Questa funzione è bene che sia unchecked. Se non esplicitamente catturata infatti, comporterà in ogni caso lato API
	// la segnalazione di un errore interno.
	//
	// Il fatto che abbiamo creato nel sistema un oggetto di tipo ServizioApplicativo ritenuto corretto (il parametro)
	// ma per il quale non si riesce più a recuperare, mmmh.
	//	+ Il tipo del soggetto propietario
	//	+ Il tipo del protocollo associato al tipo del soggeto
	//  + Il servizio protocolli stesso
	//
	// Denotano un errore interno.
	//
	// è una funzione che effettua un mapping da un tipo all'altro e che potrà essere utilizzata liberamente negli stream.
	
	public static final ApplicativoItem servizioApplicativoToApplicativoItem(ServizioApplicativo sa) {
		ApplicativoItem ret = new ApplicativoItem();
		
		ret.setNome(sa.getNome());
		
		String tipo_protocollo = null;
		
		try {
			tipo_protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(sa.getTipoSoggettoProprietario());
		} catch (ProtocolException e) {
		
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}		
		
		ret.setProfilo(BaseHelper.profiloFromTipoProtocollo.get(tipo_protocollo));
		ret.setSoggetto(sa.getNomeSoggettoProprietario());
		
		ServizioApplicativoRuoli saRoles = null;
		if(sa.getInvocazionePorta()!=null) {
			saRoles = sa.getInvocazionePorta().getRuoli();
		}
		if (saRoles == null) {
			ret.setCountRuoli(0);
		} 
		else {
			ret.setCountRuoli(saRoles.sizeRuoloList());
		}
		
		return ret;
	}
	
	public static final ApplicativoServerItem servizioApplicativoToApplicativoServerItem(ServizioApplicativo sa) {
		ApplicativoServerItem ret = new ApplicativoServerItem();
		
		ret.setNome(sa.getNome());
		
		String tipo_protocollo = null;
		
		try {
			tipo_protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(sa.getTipoSoggettoProprietario());
		} catch (ProtocolException e) {
		
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}		
		
		ret.setProfilo(BaseHelper.profiloFromTipoProtocollo.get(tipo_protocollo));
		ret.setSoggetto(sa.getNomeSoggettoProprietario());
		
		return ret;
	}
	
	
	
	public static final IDSoggetto getIDSoggetto(String nome, String tipo_protocollo) throws ProtocolException {
		String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipo_protocollo);
		return  new IDSoggetto(tipo_soggetto,nome);
	}
	
	public static final Soggetto getSoggetto(String nome, ProfiloEnum modalita, SoggettiCore soggettiCore) throws DriverRegistroServiziException, DriverRegistroServiziNotFound, ProtocolException {
		return soggettiCore.getSoggettoRegistro(getIDSoggetto(nome,BaseHelper.tipoProtocolloFromProfilo.get(modalita)));

	}
			
	
	/**
	 * Swagger passa le credenziali come una linkedHashMap.
	 * Dobbiamo trasformarla nei relativi oggetti di autenticazione.
	 * 
	 * TODO: Rimuovere questa versione e utilizzare quella in RestApiHelper.
	 * 
	 * @param applicativo
	 * @return
	 * @throws Exception
	 */
	public static OneOfBaseCredenzialiCredenziali translateCredenzialiApplicativo(Applicativo applicativo, boolean create) {
		if(applicativo.getCredenziali()==null) {
			return null;
		}
		return translateCredenzialiApplicativo(applicativo.getCredenziali(), create);
	}
	public static OneOfBaseCredenzialiCredenziali translateCredenzialiApplicativo(BaseCredenziali baseCredenziali, boolean create) {
		if(baseCredenziali==null) {
			return null;
		}
		return translateCredenzialiApplicativo(baseCredenziali.getCredenziali(), create);
	}
	public static OneOfBaseCredenzialiCredenziali translateCredenzialiApplicativo(OneOfBaseCredenzialiCredenziali c, boolean create) {
		OneOfBaseCredenzialiCredenziali creds = null;
		
		if(c==null || c.getModalitaAccesso()==null) {
			return null;
		}
		
		String  tipoauthSA = Helper.tipoAuthSAFromModalita.get(c.getModalitaAccesso().toString());
		
		if (tipoauthSA == null)
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo modalità accesso sconosciuto: " + c.getModalitaAccesso());
		
		if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) ||
				tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL) ||
				tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY) ||
				tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL) ||
				tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN)) {
			creds = Helper.translateCredenziali(c, create);
		}
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
			creds = null;
		}
		else {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo autenticazione sconosciuto: " + tipoauthSA);
		}

		return creds;
	}
	
	
	public static ApiKeyInfo createApiKey(OneOfBaseCredenzialiCredenziali creds, IDServizioApplicativo idSA, ServiziApplicativiCore saCore, String protocollo) throws Exception {
		if(creds!=null && ModalitaAccessoEnum.API_KEY.equals(creds.getModalitaAccesso())) {
			AuthenticationApiKey apiKey = (AuthenticationApiKey) creds;
			boolean appId = Helper.isAppId(apiKey.isAppId());
			ApiKeyInfo keyInfo = new ApiKeyInfo();
			keyInfo.setMultipleApiKeys(appId);
			ApiKey apiKeyGenerated = null;
			if(appId) {
				apiKeyGenerated = saCore.newMultipleApiKey();
				keyInfo.setAppId(saCore.toAppId(protocollo, idSA, appId));
			}
			else {
				keyInfo.setAppId(saCore.toAppId(protocollo, idSA, appId));
				apiKeyGenerated = saCore.newApiKey(protocollo, idSA);
			}
			keyInfo.setPassword(apiKeyGenerated.getPassword());
			keyInfo.setApiKey(apiKeyGenerated.getApiKey());
			return keyInfo;
		}
		return null;
	}
	public static ApiKeyInfo getApiKey(ServizioApplicativo sa, boolean setApiKey) throws Exception {
		if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0) {
			Credenziali c = sa.getInvocazionePorta().getCredenziali(0);
			if(CredenzialeTipo.APIKEY.equals(c.getTipo())){
				ApiKeyInfo keyInfo = new ApiKeyInfo();
				keyInfo.setMultipleApiKeys(c.isAppId());
				keyInfo.setAppId(c.getUser());
				keyInfo.setPassword(c.getPassword());
				keyInfo.setCifrata(c.isCertificateStrictVerification());
				if(setApiKey) {
					if(c.isAppId()) {
						keyInfo.setApiKey(ApiKeyUtilities.encodeMultipleApiKey(c.getPassword()));
					}
					else {
						keyInfo.setApiKey(ApiKeyUtilities.encodeApiKey(c.getUser(), c.getPassword()));
					}
				}
				return keyInfo;
			}
		}
		return null;
	}
	
	/**
	 * Trasforma le credenziali di autenticazione di un servizio applicativo nelle credenziali
	 * di un'InvocazionePorta.
	 * @throws UtilsException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	public static List<Credenziali> credenzialiFromAuth(OneOfBaseCredenzialiCredenziali cred, ApiKeyInfo keyInfo) throws IllegalAccessException, InvocationTargetException, InstantiationException, UtilsException {
		
		List<Credenziali> credenziali = null;
		
		if(cred!=null) {
		
			ModalitaAccessoEnum modalitaAccesso = cred.getModalitaAccesso();
			if(modalitaAccesso == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Modalità di accesso delle credenziali non indicata");
			}
			
			String tipoauthSA = Helper.tipoAuthSAFromModalita.get(modalitaAccesso.toString());
			
			
			if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
				credenziali = new ArrayList<Credenziali>();
				Credenziali cre = new Credenziali();
				cre.setTipo(null);
				credenziali.add(cre);
			}
			else {
			
				credenziali = Helper.apiCredenzialiToGovwayCred(
						cred,
						modalitaAccesso,
						Credenziali.class,
						org.openspcoop2.core.config.constants.CredenzialeTipo.class,
						keyInfo
						);	
			
			}
			
		}
		
		return credenziali;
	}
	
	/**
	 * Trasforma le credenziali per un'InvocazionePorta nelle credenziali conservate in un applicativo.
	 * 
	 * @param cred
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static OneOfBaseCredenzialiCredenziali authFromCredenziali(List<Credenziali> cred) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		return Helper.govwayCredenzialiToApi(
				cred,
				Credenziali.class,
				org.openspcoop2.core.config.constants.CredenzialeTipo.class);

	}
	
	
	public static void checkApplicativoName(String nome) throws Exception {

		if (nome.equals(""))		
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Dati incompleti. E' necessario indicare: " + ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME
			);
					
		if (nome.indexOf(" ") != -1 || nome.indexOf('\"') != -1) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non inserire spazi o doppi apici nei campi di testo");
		}
		
		checkIntegrationEntityName(nome);
		checkLength255(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
	}
	
	
	public static boolean isApplicativoDuplicato(ServizioApplicativo sa, ServiziApplicativiCore saCore) throws DriverConfigurazioneException {

		IDServizioApplicativo idSA = new IDServizioApplicativo();
		IDSoggetto idSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
		idSA.setIdSoggettoProprietario(idSoggetto);
		idSA.setNome(sa.getNome());
		
		return saCore.existsServizioApplicativo(idSA);
	}
	
	
	public static void checkIntegrationEntityName(String name) throws Exception{
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.' '/'
		if (!RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-\\._/A-Za-z0-9]*$")) {
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Il campo '" + 
					 ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME + 
					 "' può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-'");
		}
	}
	
	
	public static void checkLength255(String value, String object) {
		checkLength(value, object, -1, 255);
	}
	
	
	public static void checkLength(String value, String object, int minLength, int maxLength) {
		
		boolean error = false;
		
		if(minLength>0) {
			if(value==null || value.length()<minLength) {
				error = true;
			}
		}
		if(maxLength>0) {
			if(value!=null && value.length()>maxLength) {
				error = true;
			}
		}
		
		if (error) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"L'informazione fornita nel campo " +
					object + 
					" deve essere compresa fa " + Integer.toString(minLength) + " e " + Integer.toString(maxLength) +" caratteri"
			);
		}
			
	}

	
	public static final void checkNoDuplicateCred(
			ServizioApplicativo sa,
			List<ServizioApplicativo> saConflicts,
			SoggettiCore soggettiCore,
			TipoOperazione tipoOperazione ) throws DriverRegistroServiziNotFound, DriverRegistroServiziException 
	{		
		Soggetto soggettoToCheck = soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
		String portaDominio = soggettoToCheck.getPortaDominio();
		
		for (int i = 0; i < saConflicts.size(); i++) {
			ServizioApplicativo saConflict = saConflicts.get(i);

			// controllo se soggetto appartiene a nal diversi, in tal caso e' possibile avere stesse credenziali
			Soggetto tmpSoggettoProprietarioSa = soggettiCore.getSoggettoRegistro(saConflict.getIdSoggetto());
			if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
				continue;
			
			if (tipoOperazione == TipoOperazione.CHANGE && sa.getNome().equals(saConflict.getNome()) && (sa.getIdSoggetto().longValue() == saConflict.getIdSoggetto().longValue())) {
				continue;
			}
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Esistono gia' altri servizi applicativi che possiedono le credenziali indicate.");
		}
		
	}
	
	public static final ServizioApplicativo getServizioApplicativo(String nome, String soggetto, String tipo_protocollo, ServiziApplicativiCore saCore) throws ProtocolException, DriverConfigurazioneException  { 
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(soggetto, tipo_protocollo));
		idServizioApplicativo.setNome(nome);
		
		return saCore.getServizioApplicativo(idServizioApplicativo);
	}


	public static List<IDPortaDelegata> getIdPorteDelegate(ServizioApplicativo oldSa, PorteDelegateCore pCore) throws DriverConfigurazioneException {
		FiltroRicercaPorteDelegate filtro = new FiltroRicercaPorteDelegate();
		filtro.setTipoSoggetto(oldSa.getTipoSoggettoProprietario());
		filtro.setNomeSoggetto(oldSa.getNomeSoggettoProprietario());
		filtro.setNomeServizioApplicativo(oldSa.getNome());
		return pCore.getAllIdPorteDelegate(filtro);
		
	}


	public static void checkServizioApplicativoInUso(ServizioApplicativo oldSa, SoggettiCore soggetiCore) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		org.openspcoop2.core.config.Soggetto oldSogg = soggetiCore.getSoggetto(oldSa.getIdSoggetto());
		String nomeProv = oldSogg.getTipo() + "/" + oldSogg.getNome();

		boolean servizioApplicativoInUso = false;
		
		for (int i = 0; i < oldSogg.sizePortaDelegataList(); i++) {
			PortaDelegata pde = oldSogg.getPortaDelegata(i);
			for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
				PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
				if (oldSa.getNome().equals(tmpSA.getNome())) {
					servizioApplicativoInUso = true;
					break;
				}
			}
			if (servizioApplicativoInUso)
				break;
		}

		if (!servizioApplicativoInUso) {
			for (int i = 0; i < oldSogg.sizePortaApplicativaList(); i++) {
				PortaApplicativa pa = oldSogg.getPortaApplicativa(i);
				for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
					PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(j);
					if (oldSa.getNome().equals(tmpSA.getNome())) {
						servizioApplicativoInUso = true;
						break;
					}
				}
				if (servizioApplicativoInUso)
					break;
			}
		}
		
		if (servizioApplicativoInUso) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il Servizio Applicativo " + oldSa.getNome() + "è già stato associato ad alcune porte delegate e/o applicative del Soggetto " + nomeProv + ". Se si desidera modificare il Soggetto è necessario eliminare prima tutte le occorrenze del Servizio Applicativo");
		}
	}

	public static void validateProperties(ApplicativiEnv env, ProtocolProperties protocolProperties,
			ServizioApplicativo sa,
			ConsoleOperationType operationType) throws Exception {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(sa, env);

				env.saHelper.validaProtocolProperties(consoleConf, operationType, protocolProperties);
				
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
				idSA.setNome(sa.getNome());
				
				IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
				IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);
				consoleDynamicConfiguration.updateDynamicConfigServizioApplicativo(consoleConf, operationType, env.saHelper, protocolProperties, 
						registryReader, configRegistryReader, idSA);
				
				consoleDynamicConfiguration.validateDynamicConfigServizioApplicativo(consoleConf, operationType, env.saHelper, protocolProperties, 
						registryReader, configRegistryReader, idSA);  
				
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
		}
		
	}
	
}
