/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.soggetti;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.rs.server.api.impl.ApiKeyInfo;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.model.AuthenticationApiKey;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfBaseCredenzialiCredenziali;
import org.openspcoop2.core.config.rs.server.model.Proprieta4000OpzioneCifratura;
import org.openspcoop2.core.config.rs.server.model.Soggetto;
import org.openspcoop2.core.config.rs.server.model.SoggettoItem;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
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
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;

/**
 * SoggettiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettiApiHelper {
	
	private SoggettiApiHelper() {}

	public static void validateCredentials(OneOfBaseCredenzialiCredenziali creds) {
		if(creds!=null && ModalitaAccessoEnum.TOKEN.equals(creds.getModalitaAccesso())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile associare credenziali di tipo '"+creds.getModalitaAccesso()+"' ad un soggetto");
		}
	}
	
	public static ApiKeyInfo createApiKey(OneOfBaseCredenzialiCredenziali creds, IDSoggetto idSoggetto, SoggettiCore soggettiCore, String protocollo) throws DriverConfigurazioneException {
		if(creds!=null && ModalitaAccessoEnum.API_KEY.equals(creds.getModalitaAccesso())) {
			AuthenticationApiKey apiKey = (AuthenticationApiKey) creds;
			boolean appId = Helper.isAppId(apiKey.isAppId());
			ApiKeyInfo keyInfo = new ApiKeyInfo();
			keyInfo.setMultipleApiKeys(appId);
			ApiKey apiKeyGenerated = null;
			if(appId) {
				apiKeyGenerated = soggettiCore.newMultipleApiKey();
				keyInfo.setAppId(soggettiCore.toAppId(protocollo, idSoggetto, appId));
			}
			else {
				keyInfo.setAppId(soggettiCore.toAppId(protocollo, idSoggetto, appId));
				apiKeyGenerated = soggettiCore.newApiKey(protocollo, idSoggetto);
			}
			keyInfo.setPassword(apiKeyGenerated.getPassword());
			keyInfo.setApiKey(apiKeyGenerated.getApiKey());
			return keyInfo;
		}
		return null;
	}
	public static ApiKeyInfo getApiKey(org.openspcoop2.core.registry.Soggetto soggetto, boolean setApiKey) throws Exception {
		if(soggetto!=null && soggetto.sizeCredenzialiList()>0) {
			CredenzialiSoggetto c = soggetto.getCredenziali(0);
			if(org.openspcoop2.core.registry.constants.CredenzialeTipo.APIKEY.equals(c.getTipo())){
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
	
	public static final void overrideAuthParams(ConsoleHelper consoleHelper, Soggetto soggetto, HttpRequestWrapper wrap,
			ApiKeyInfo apiKeyInfo, boolean updateKey) {
		if(soggetto!=null) {
			overrideAuthParams(consoleHelper, soggetto.getCredenziali(), wrap,
					apiKeyInfo, updateKey);
		}
	}
	public static final void overrideAuthParams(ConsoleHelper consoleHelper, OneOfBaseCredenzialiCredenziali credenziali, HttpRequestWrapper wrap,
			ApiKeyInfo apiKeyInfo, boolean updateKey) {

		if(credenziali!=null && credenziali.getModalitaAccesso()!=null) {
			wrap.overrideParameter(
					ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE, 
					Helper.tipoAuthFromModalitaAccesso.get(credenziali.getModalitaAccesso())
			);
			Helper.overrideAuthParams(wrap, consoleHelper, credenziali,
					apiKeyInfo, updateKey);
		}
		
	}

	// In queste logiche non considero:
	//		+ isSinglePdd ( che è sempre true, serve a gestire più govway remoti per mezzo di una pdd operativa, in tal caso ogni soggetto deve averne una)
	
	// La tipologia (erogatore\fruitore) viene specificata solo nel caso in cui il soggetto sia esterno e serve a stabilire
	// le informazioni di autenticazione nel caso esso sia un fruitore, noi consideriamo tutti fruitori.
	
	
	public static void convert(Soggetto body, org.openspcoop2.core.registry.Soggetto ret, SoggettiEnv env, ApiKeyInfo keyInfo) throws DriverRegistroServiziException, DriverControlStationException, IllegalAccessException, InvocationTargetException, InstantiationException, UtilsException {
		
		ret.setNome(body.getNome());
		ret.setDescrizione(body.getDescrizione());		
	
		// Un soggetto esterno abbiamo detto che può averla, ma durante la creazione essa non viene specificata perciò è lasciata a null.
		// Un soggetto Interno DEVE avere una porta di dominio di tipo operativo, e gliene viene assegnata la prima trovata.
		if (body.getDominio() == DominioEnum.INTERNO) {
	
			if(!env.multitenant)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Per registrare un nuovo soggetto interno passare alla modalità multitenant");
			
			String nomePdd = env.pddCore.pddList(null, new ConsoleSearch(true)).stream()	
					.filter(pdd -> PddTipologia.OPERATIVO.toString().equals(pdd.getTipo()))
					.map( pdd -> pdd.getNome())
					.findFirst().orElse("");
			
			if (nomePdd.length() == 0) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna porta operativa da associare al soggetto interno");
			}
			ret.setPortaDominio(nomePdd);
		}
		
		if (env.soggettiCore.isSupportatoAutenticazioneSoggetti(env.tipo_protocollo) && body.getCredenziali() != null && body.getCredenziali().getModalitaAccesso() != null ) {
			try {
				List<CredenzialiSoggetto> credenziali = Helper.apiCredenzialiToGovwayCred(
							body.getCredenziali(),
							body.getCredenziali().getModalitaAccesso(),
							CredenzialiSoggetto.class,
							org.openspcoop2.core.registry.constants.CredenzialeTipo.class,
							keyInfo
				);		
				ret.getCredenzialiList().clear();
				ret.getCredenzialiList().addAll(credenziali);
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
	
		convertPropertyList(body, ret, env);
	}
	
	public static void convertPropertyList(Soggetto body, org.openspcoop2.core.registry.Soggetto ret, SoggettiEnv env) throws UtilsException {
		ret.getProprietaList().clear();
		if(body.getProprieta()!=null && !body.getProprieta().isEmpty()) {
			for (Proprieta4000OpzioneCifratura proprieta : body.getProprieta()) {
				org.openspcoop2.core.registry.Proprieta pRegistry = new org.openspcoop2.core.registry.Proprieta();
				pRegistry.setNome(proprieta.getNome());
				if(env.soggettiCore!=null && env.soggettiCore.getDriverBYOKUtilities()!=null && 
						proprieta.isEncrypted()!=null && proprieta.isEncrypted().booleanValue()) {
					pRegistry.setValore(env.soggettiCore.getDriverBYOKUtilities().wrap(proprieta.getValore()));
				}
				else {
					if(proprieta.getValore().length()>4000) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(CostantiControlStation.MESSAGGIO_ERRORE_VALORE_PROPRIETA_4000);
					}
					pRegistry.setValore(proprieta.getValore());
				}
				ret.addProprieta(pRegistry);
			}
		}
	}
	
	public static final org.openspcoop2.core.registry.Soggetto soggettoApiToRegistro(Soggetto body, SoggettiEnv env, ApiKeyInfo keyInfo) 
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverControlStationException, IllegalAccessException, InvocationTargetException, InstantiationException, UtilsException {
		
		final org.openspcoop2.core.registry.Soggetto ret = new org.openspcoop2.core.registry.Soggetto();
		convert(body, ret, env, keyInfo);
		
		final String protocollo = env.tipo_protocollo;
		final IDSoggetto idSoggetto = new IDSoggetto(env.tipo_soggetto,body.getNome());

		ret.setVersioneProtocollo(env.soggettiCore.getVersioneDefaultProtocollo(protocollo));
		ret.setIdentificativoPorta(env.soggettiCore.getIdentificativoPortaDefault(protocollo, idSoggetto));
		ret.setCodiceIpa(env.soggettiCore.getCodiceIPADefault(protocollo, idSoggetto, false));
		ret.setSuperUser(env.userLogin);
		ret.setTipo(env.tipo_soggetto);
		
		
		if ( body.getRuoli() != null ) {
			// I possibili ruoli da assegnare sono quelli con contesto porta_applicativa (erogazione) e tipologia interna.
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.PORTA_APPLICATIVA);
			filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			List<String> ruoliAmmessi = env.soggettiCore.getAllRuoli(filtroRuoli);
			
			RuoliSoggetto ruoliSoggetto = new RuoliSoggetto();
			
			body.getRuoli().forEach( rname -> {
				if (!ruoliAmmessi.contains(rname))
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non esiste alcun ruolo con nome " + rname + " da associare al soggetto.");
				RuoloSoggetto rs = new RuoloSoggetto();
				rs.setNome(rname);
				ruoliSoggetto.addRuolo(rs);
			});
			
			ret.setRuoli(ruoliSoggetto);
		}
		
		// Di base privato è false, ha a che fare con una checkbox che spunterà fuori solo in modalità completa e non in quella avanzata.
		ret.setPrivato(false);		
				
		final Connettore connettore = new Connettore();
		connettore.setTipo(CostantiDB.CONNETTORE_TIPO_DISABILITATO);
		ret.setConnettore(connettore);
		
		return ret;
	}
	
	public static final Soggetto soggettoRegistroToApi(org.openspcoop2.core.registry.Soggetto s, PddCore pddCore, SoggettiCore soggettiCore) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DriverConfigurazioneException {
		Soggetto ret = new Soggetto();
		ret.setNome(s.getNome());
		ret.setDescrizione(s.getDescrizione());
				
		ret.setDominio(DominioEnum.ESTERNO);
		try {
			if (s.getPortaDominio() != null) {
				PdDControlStation p = pddCore.getPdDControlStation(s.getPortaDominio());
				if (PddTipologia.OPERATIVO.toString().equals(p.getTipo()))
					ret.setDominio(DominioEnum.INTERNO);
			}
		} catch (Exception e) {
			throw new UtilsRuntimeException(e);
		}
		
		if(DominioEnum.ESTERNO.equals(ret.getDominio())) {
			ret.setCredenziali(Helper.govwayCredenzialiToApi(
					s.getCredenzialiList(),
					CredenzialiSoggetto.class,
					org.openspcoop2.core.registry.constants.CredenzialeTipo.class
				));
		}
		
		ret.setRuoli(soggettiCore.soggettiRuoliList(s.getId(), new ConsoleSearch()));
	
		if(s.sizeProprietaList()>0) {
			for (org.openspcoop2.core.registry.Proprieta proprieta : s.getProprietaList()) {
				Proprieta4000OpzioneCifratura p = new Proprieta4000OpzioneCifratura();
				p.setNome(proprieta.getNome());
				p.setValore(proprieta.getValore());
				p.setEncrypted(BYOKUtilities.isWrappedValue(p.getValore()));
				if(ret.getProprieta()==null) {
					ret.setProprieta(new ArrayList<>());
				}
				ret.addProprietaItem(p);
			}
		}
		
		return ret;
	}
	
	//String getTipoAuth
	
	public static final org.openspcoop2.core.config.Soggetto soggettoRegistroToConfig(org.openspcoop2.core.registry.Soggetto s, org.openspcoop2.core.config.Soggetto ret, boolean isRouter) {
		
		ret.setNome(s.getNome());
		ret.setTipo(s.getTipo());
		ret.setDescrizione(s.getDescrizione());
		ret.setIdentificativoPorta(s.getIdentificativoPorta());
		ret.setRouter(isRouter);
		ret.setSuperUser(s.getSuperUser());
		
		
		return ret;
	}


	public static SoggettoItem soggettoRegistroToItem(org.openspcoop2.core.registry.Soggetto s, PddCore pddCore, SoggettiCore soggettiCore) {
		SoggettoItem ret = new SoggettoItem();
		ret.setNome(s.getNome());
		ret.setDominio(DominioEnum.ESTERNO);
		
		try {
			
			if (s.getPortaDominio() != null) {
				PdDControlStation p = pddCore.getPdDControlStation(s.getPortaDominio());
				if (PddTipologia.OPERATIVO.toString().equals(p.getTipo()))
					ret.setDominio(DominioEnum.INTERNO);
			}
			
			// Recupero il numero di ruoli.
			ConsoleSearch searchForCount = new ConsoleSearch(true,1);
			soggettiCore.soggettiRuoliList(s.getId(),searchForCount);
			int numRuoli = searchForCount.getNumEntries(Liste.SOGGETTI_RUOLI);
			ret.setCountRuoli(numRuoli);
	
			String tipoProtocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(s.getTipo());
			ret.setProfilo(BaseHelper.profiloFromTipoProtocollo.get(tipoProtocollo));
		} 
		catch (Exception e) {
			throw new UtilsRuntimeException(e);
		}
		
		return ret;
	}

	
	public static ProtocolProperties getProtocolProperties(Soggetto body, ProfiloEnum profilo, org.openspcoop2.core.registry.Soggetto soggetto, SoggettiEnv env) throws Exception {


		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliverySoggettiApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPASoggettiApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiSoggettiApiHelper.getProtocolProperties(body);
		case SPCOOP:
			return SPCoopSoggettiApiHelper.getProtocolProperties(body);
		}
		return null;
	}


	public static void populateProtocolInfo(org.openspcoop2.core.registry.Soggetto soggetto, Soggetto ret, SoggettiEnv env, ProfiloEnum profilo) throws Exception {
		
		if(profilo != null) {
			switch(profilo) {
			case APIGATEWAY: 
				break;
			case EDELIVERY: EDeliverySoggettiApiHelper.populateProtocolInfo(soggetto, env, ret);
				break;
			case FATTURAPA: FatturaPASoggettiApiHelper.populateProtocolInfo(soggetto, env, ret);
				break;
			case MODI: 
			case MODIPA: ModiSoggettiApiHelper.populateProtocolInfo(soggetto, env, ret);
				break;
			case SPCOOP: SPCoopSoggettiApiHelper.populateProtocolInfo(soggetto, env, ret);
				break;
			default:
				break;}
		}

	}
	
	public static Map<String, AbstractProperty<?>> getProtocolPropertiesMap(org.openspcoop2.core.registry.Soggetto soggetto, SoggettiEnv env) throws DriverControlStationException, UtilsException, ProtocolException, DriverConfigurazioneException, ValidationException {

		ProtocolProperties prop = getProtocolProperties(soggetto, env);
		Map<String, AbstractProperty<?>> p = new HashMap<>();

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}
		
		return p;
	}
	
	public static ProtocolProperties getProtocolProperties(org.openspcoop2.core.registry.Soggetto soggetto, SoggettiEnv env) throws DriverControlStationException, UtilsException, ProtocolException, DriverConfigurazioneException, ValidationException {
		ConsoleConfiguration consoleConf = getConsoleConfiguration(soggetto, env);

		ProtocolProperties prop = env.soggettiHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, soggetto.getProtocolPropertyList(), ConsoleOperationType.CHANGE);
		return prop;
	}
	
	public static ConsoleConfiguration getConsoleConfiguration(org.openspcoop2.core.registry.Soggetto soggetto, SoggettiEnv env) throws ProtocolException, DriverConfigurazioneException {
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		IDSoggetto idSoggetto = new IDSoggetto(env.tipo_soggetto, soggetto.getNome());
		
		return consoleDynamicConfiguration.getDynamicConfigSoggetto(ConsoleOperationType.ADD, env.soggettiHelper, 
				registryReader, configRegistryReader, idSoggetto);

	}
}
