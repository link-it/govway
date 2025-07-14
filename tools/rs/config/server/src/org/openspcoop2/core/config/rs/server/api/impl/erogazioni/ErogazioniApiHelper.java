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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.deserializeDefault;
import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;
import static org.openspcoop2.utils.service.beans.utils.ProfiloUtils.toProfilo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneToken;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneToken;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TracciamentoConfigurazione;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.StatoDescrizione;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.configurazione.ErogazioniConfEnv;
import org.openspcoop2.core.config.rs.server.api.impl.fruizioni.configurazione.FruizioniConfEnv;
import org.openspcoop2.core.config.rs.server.config.LoggerProperties;
import org.openspcoop2.core.config.rs.server.model.*;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneBasic;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.SerialiableFormFile;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeUtilities;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateUtilities;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.slf4j.Logger;


/**
 * ErogazioniApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErogazioniApiHelper {
	
	private ErogazioniApiHelper() {}
		
	
	public static void validateProperties(ErogazioniEnv env, ProtocolProperties protocolProperties, AccordoServizioParteSpecifica asps,
			ConsoleOperationType operationType)
			throws Exception {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(env, asps);

				env.apsHelper.validaProtocolProperties(consoleConf, ConsoleOperationType.ADD, protocolProperties);
				
				IDServizio oldIdAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione()); 
				oldIdAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
				oldIdAps.setPortType(asps.getPortType());
				
				IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
				IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);
				consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(consoleConf, operationType, env.apsHelper, protocolProperties, 
						registryReader, configRegistryReader, oldIdAps);
				
				consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(consoleConf, operationType, env.apsHelper, protocolProperties, 
						registryReader, configRegistryReader, oldIdAps);
				
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
		}
	}

	public static void validateProperties(ErogazioniEnv env, ProtocolProperties protocolProperties, IDFruizione id,
			ConsoleOperationType operationType)
			throws Exception {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(env, id);

				env.apsHelper.validaProtocolProperties(consoleConf, ConsoleOperationType.ADD, protocolProperties);
				
				IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
				IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);
				consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConf, operationType, env.apsHelper, protocolProperties, 
						registryReader, configRegistryReader, id);
				
				consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConf, operationType, env.apsHelper, protocolProperties, 
						registryReader, configRegistryReader, id);
				
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
		}
	}


	public static ConsoleConfiguration getConsoleConfiguration(ErogazioniEnv env, AccordoServizioParteSpecifica asps) throws Exception {
    	IDServizio oldIdAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione()); 
		oldIdAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
		oldIdAps.setPortType(asps.getPortType());

		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		env.requestWrapper.overrideParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM, Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE);
		
		return consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType.ADD, env.apsHelper, 
				registryReader, configRegistryReader, oldIdAps);

	}
	
	public static void updateConsoleConfiguration(ErogazioniEnv env, AccordoServizioParteSpecifica asps,
			ConsoleConfiguration consoleConf, ProtocolProperties prop) throws Exception {
    	IDServizio oldIdAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione()); 
		oldIdAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
		oldIdAps.setPortType(asps.getPortType());

		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		env.requestWrapper.overrideParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM, Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE);
		
		consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(consoleConf, ConsoleOperationType.CHANGE, env.apsHelper, prop,
				registryReader, configRegistryReader, oldIdAps);

	}

	public static ConsoleConfiguration getConsoleConfiguration(ErogazioniEnv env, IDFruizione id) throws Exception {
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		env.requestWrapper.overrideParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM, Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE);

		return consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleOperationType.ADD, env.apsHelper, registryReader, configRegistryReader, id);
	}
	
	public static void updateConsoleConfiguration(ErogazioniEnv env, IDFruizione id,
			ConsoleConfiguration consoleConf, ProtocolProperties prop) throws Exception {
    	
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		env.requestWrapper.overrideParameter(Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM, Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE);
		
		consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConf, ConsoleOperationType.CHANGE, env.apsHelper, prop,
				registryReader, configRegistryReader, id);

	}


	public static ProtocolProperties getProtocolProperties(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		ConsoleConfiguration consoleConf = getConsoleConfiguration(env, asps);

		ProtocolProperties prop = env.apsHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, asps.getProtocolPropertyList(), ConsoleOperationType.CHANGE);
		
		// update della configurazione
		// non sembra servire
		//updateConsoleConfiguration(env, asps, consoleConf, prop);
		
		return prop;
	}
	public static ProtocolProperties getProtocolProperties(IDFruizione id, Fruitore f, ErogazioniEnv env) throws Exception {
		ConsoleConfiguration consoleConf = getConsoleConfiguration(env, id);

		ProtocolProperties prop = env.apsHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, f.getProtocolPropertyList(), ConsoleOperationType.CHANGE);
		
		// update della configurazione 
		// non sembra servire
		//updateConsoleConfiguration(env, id, consoleConf, prop);
		
		return prop;
	}
	public static Map<String, AbstractProperty<?>> getProtocolPropertiesMap(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {

		ProtocolProperties prop = getProtocolProperties(asps, env);
		Map<String, AbstractProperty<?>> p = new HashMap<>();

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}
		
		return p;
	}
	
	public static Map<String, AbstractProperty<?>> getProtocolPropertiesMap(IDFruizione id, Fruitore f, ErogazioniEnv env) throws Exception {

		ProtocolProperties prop = getProtocolProperties(id, f, env);
		Map<String, AbstractProperty<?>> p = new HashMap<>();

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}
		
		return p;
	}
	
	public static ProtocolProperties getProtocolProperties(Erogazione body, ProfiloEnum profilo, AccordoServizioParteComune aspc, AccordoServizioParteSpecifica asps, ErogazioniEnv env, boolean required) throws Exception {


		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliveryErogazioniApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPAErogazioniApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiErogazioniApiHelper.getProtocolProperties(body, aspc, asps, env, required);
		case SPCOOP:
			return SPCoopErogazioniApiHelper.getProtocolProperties(body);
		}
		return null;
	}

	public static ProtocolProperties getProtocolProperties(Fruizione body, ProfiloEnum profilo, AccordoServizioParteSpecifica asps, ErogazioniEnv env, boolean required) throws Exception {


		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliveryErogazioniApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPAErogazioniApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiErogazioniApiHelper.getProtocolProperties(body, asps, env, required);
		case SPCOOP:
			return SPCoopErogazioniApiHelper.getProtocolProperties(body); 
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public static final <T> T deserializeModalitaConfGruppo(ModalitaConfigurazioneGruppoEnum discr, Object body) throws UtilsException, InstantiationException, IllegalAccessException {
		
		switch(discr) {
		case EREDITA: {
			GruppoEreditaConfigurazione conf = deserializeDefault(body, GruppoEreditaConfigurazione.class);
			if (StringUtils.isEmpty(conf.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(GruppoEreditaConfigurazione.class.getName()+": Indicare il campo obbligatorio 'nome'");
			}
			return (T) conf;
		}
		case NUOVA: {
			GruppoNuovaConfigurazione conf = deserializeDefault(body, GruppoNuovaConfigurazione.class);
			ErogazioniCheckNotNull.checkAutenticazione(conf);
			
			return (T) conf;
		}
		default: 
			return null;
		}
	}

	
	public static final void fillAps(
			AccordoServizioParteSpecifica specifico, 
			APIImpl impl,
			ErogazioniEnv env
		) throws ProtocolException {	
		
		specifico.setNome(impl.getApiNome());
		
		if(impl.getTipoServizio()!=null && StringUtils.isNotEmpty(impl.getTipoServizio())) {
			List<String> l = env.protocolFactoryMgr.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration().getTipiServizi(null);
			if(l!=null && !l.contains(impl.getTipoServizio())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo servizio indicato '"+impl.getTipoServizio()+"' non supportato dal profilo '"+env.profilo+"'");
			}
			specifico.setTipo(impl.getTipoServizio());
		}
		
		specifico.setVersione(impl.getApiVersione());
		specifico.setPortType(impl.getApiSoapServizio());	
	}
	
	public static final AccordoServizioParteSpecifica apiImplToAps(APIImpl impl, final Soggetto soggErogatore, AccordoServizioParteComuneSintetico as, ErogazioniEnv env) 
			throws DriverRegistroServiziException, ProtocolException {
		final AccordoServizioParteSpecifica ret = new AccordoServizioParteSpecifica();
				
		fillAps(ret, impl, env);
		
		// Questo per seguire la specifica della console, che durante la creazione di un servizio soap
		// vuole che il nome del'asps sia quello del servizio\port_type
		if (as.getServiceBinding() == ServiceBinding.SOAP) {
			ret.setNome(ret.getPortType());
		}
		
		if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo))
			ret.setVersione(as.getVersione());
		
		if(impl instanceof Erogazione) {
			Erogazione erogazione = (Erogazione) impl;
			if(erogazione.getErogazioneNome()!=null && !"".equals(erogazione.getErogazioneNome())) {
				ret.setNome(erogazione.getErogazioneNome());
			}
			if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo) &&
				erogazione.getErogazioneVersione()!=null && erogazione.getErogazioneVersione()>0) {
				ret.setVersione(erogazione.getErogazioneVersione());
			}
			ret.setDescrizione(erogazione.getDescrizione());
		}
		else if(impl instanceof Fruizione) {
			Fruizione fruizione = (Fruizione) impl;
			if(fruizione.getFruizioneNome()!=null && !"".equals(fruizione.getFruizioneNome())) {
				ret.setNome(fruizione.getFruizioneNome());
			}
			if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo) &&
				fruizione.getFruizioneVersione()!=null && fruizione.getFruizioneVersione()>0) {
				ret.setVersione(fruizione.getFruizioneVersione());
			}
			// descrizione deve essere impostata sul fruitore
		}

		ret.setIdSoggetto(soggErogatore.getId());
		ret.setTipoSoggettoErogatore(soggErogatore.getTipo());
		ret.setNomeSoggettoErogatore(soggErogatore.getNome());

		ret.setVersioneProtocollo(soggErogatore.getVersioneProtocollo());
		
		ret.setByteWsdlImplementativoErogatore(null);
		ret.setByteWsdlImplementativoFruitore(null);
		
     	ret.setIdAccordo(as.getId());	
		ret.setAccordoServizioParteComune(env.idAccordoFactory.getUriFromAccordo(as));
		ret.setTipologiaServizio(TipologiaServizio.NORMALE); // dal debug servcorr è "no"
		ret.setSuperUser(env.userLogin);
		ret.setPrivato(false);								// Come da debug.
		ret.setStatoPackage(StatoType.FINALE.getValue()); 
		ret.setConfigurazioneServizio(new ConfigurazioneServizio());
				 
		if (StringUtils.isEmpty(ret.getTipo())) {
			String tipoServizio = env.protocolFactoryMgr.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration().getTipoServizioDefault(Utilities.convert(as.getServiceBinding()));
			ret.setTipo( tipoServizio );
		}
				
		return ret;
	}
	
	
	public static final void serviziUpdateCheckData(AccordoServizioParteComuneSintetico as, AccordoServizioParteSpecifica asps, boolean isErogazione, ErogazioniEnv env) throws Exception {
		
		 // Determino i soggetti compatibili
        ConsoleSearch searchSoggetti = new ConsoleSearch(true);
		searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, env.tipo_protocollo);
		String[] soggettiCompatibili = env.soggettiCore.soggettiRegistroList(null, searchSoggetti).stream()
				.map( s -> s.getId().toString())
				.toArray(String[]::new);
		
		// Determino la lista Api
		String[] accordiList = AccordiServizioParteSpecificaUtilities.getListaIdAPI(
				env.tipo_protocollo,
				env.userLogin,
				env.apsCore, 
				env.apsHelper
			).stream()
    		.map( a -> a.getId().toString() )
    		.toArray(String[]::new);
		

		// Determino l'erogatore
		IdSoggetto erogatore = new IdSoggetto( new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
		erogatore.setId(asps.getIdSoggetto());
		
		org.openspcoop2.core.registry.Connettore connRegistro = asps.getConfigurazioneServizio().getConnettore();
		
		final String endpointtype = connRegistro.getTipo();
		final String endpoint_url = connRegistro.getProperties().get(CostantiDB.CONNETTORE_HTTP_LOCATION);
		
		// Recupero i Servizi Esposti dalla API 
        final String[] ptArray =  AccordiServizioParteSpecificaUtilities.getListaPortTypes(as, env.apsHelper)
        		.stream()
         		.map( p -> p.getNome() )
         		.toArray(String[]::new);
		
		final boolean accordoPrivato = as.getPrivato()!=null && as.getPrivato();

		final ConnettoreHttp connRest = new ConnettoreHTTPApiHelper().buildConnettore(connRegistro.getProperties(), connRegistro.getTipo());
		final ConnettoreConfigurazioneApiKey httpApiKey = connRest.getAutenticazioneApikey();
		boolean apiKey = (httpApiKey!=null && httpApiKey.getApiKey()!=null && StringUtils.isNotEmpty(httpApiKey.getApiKey()));
		final ConnettoreConfigurazioneHttps httpsConf 	 = connRest.getAutenticazioneHttps();

		final ConnettoreConfigurazioneHttpBasic httpConf	 = connRest.getAutenticazioneHttp();
		// Questa è la cosa diversa per i fruitori, Li invece abbiamo le credenziali direttamente nel connettore.
        final ConnettoreConfigurazioneHttpsClient httpsClient = httpsConf!=null ? evalnull( httpsConf::getClient ) : null;
      	final ConnettoreConfigurazioneHttpsServer httpsServer = httpsConf!=null ? evalnull( httpsConf::getServer ) : null;
      	final ConnettoreConfigurazioneProxy 	  proxy   	  = connRest.getProxy();
      	final ConnettoreConfigurazioneTimeout	  timeoutConf = connRest.getTempiRisposta();
      	final String tokenPolicy = connRest.getTokenPolicy(); 
      	final boolean autenticazioneToken = tokenPolicy!=null;
      	
        
 		final boolean httpsstato = httpsClient != null;
 		final boolean http_stato = connRest.getAutenticazioneHttp() != null;
 		final boolean proxy_enabled = connRest.getProxy() != null;
 		final boolean tempiRisposta_enabled = connRest.getTempiRisposta() != null;
 		
 		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
		

		final BinaryParameter xamlPolicy = new BinaryParameter();
		
		String erogazioneAutenticazione = null;
		boolean erogazioneAutenticazioneOpzionale = true;
		TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal = null;
		List<String> erogazioneAutenticazioneParametroList = null;
		String erogazioneAutorizzazione = null;
		boolean erogazioneAutorizzazioneAutenticati = false;
		boolean erogazioneAutorizzazioneRuoli = false;
		String erogazioneAutorizzazioneRuoliTipologia = null;
		String erogazioneAutorizzazioneRuoliMatch = null;

		String fruizioneAutenticazione = null;
		boolean fruizioneAutenticazioneOpzionale = true;
		TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal = null;
		List<String> fruizioneAutenticazioneParametroList = null;
		String fruizioneAutorizzazione = null;
		boolean fruizioneAutorizzazioneAutenticati = false;
		boolean fruizioneAutorizzazioneRuoli = false;
		String fruizioneAutorizzazioneRuoliTipologia = null;
		String fruizioneAutorizzazioneRuoliMatch = null;
		
		boolean gestioneCanaliEnabled = env.gestioneCanali;
		String canale = null;		
		
		if ( isErogazione ) {
			
			ServletUtils.setObjectIntoSession(env.requestWrapper, env.requestWrapper.getSession(),
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE,
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			
			final IDServizio idServizio = asps.getOldIDServizioForUpdate();
			final IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPA);
						
			erogazioneAutenticazione = pa.getAutenticazione();
			erogazioneAutenticazioneOpzionale = Helper.statoFunzionalitaConfToBool(pa.getAutenticazioneOpzionale());
			erogazioneAutenticazionePrincipal = env.paCore.getTipoAutenticazionePrincipal(pa.getProprietaAutenticazioneList());
			erogazioneAutenticazioneParametroList = env.paCore.getParametroAutenticazione(erogazioneAutenticazione, pa.getProprietaAutenticazioneList());
			
			erogazioneAutorizzazione = pa.getAutorizzazione();
			erogazioneAutorizzazioneAutenticati = TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione());
			erogazioneAutorizzazioneRuoli = TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione());
			erogazioneAutorizzazioneRuoliTipologia =  AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()).toString();
			erogazioneAutorizzazioneRuoliMatch = evalnull( () -> pa.getRuoli().getMatch().toString() );
			
			if (pa.getXacmlPolicy() != null) {							
		        xamlPolicy.setValue(pa.getXacmlPolicy().getBytes());
		        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
		     }
			
			canale = pa.getCanale();
		} else {
			
			ServletUtils.setObjectIntoSession(env.requestWrapper, env.requestWrapper.getSession(),
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE,
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			
			final IDServizio idServizio = asps.getOldIDServizioForUpdate();
			final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, env.idSoggetto.toIDSoggetto());
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);
					
			fruizioneAutenticazione = pd.getAutenticazione();
			fruizioneAutenticazioneOpzionale = Helper.statoFunzionalitaConfToBool(pd.getAutenticazioneOpzionale());
			fruizioneAutenticazionePrincipal = env.pdCore.getTipoAutenticazionePrincipal(pd.getProprietaAutenticazioneList());
			fruizioneAutenticazioneParametroList = env.pdCore.getParametroAutenticazione(fruizioneAutenticazione, pd.getProprietaAutenticazioneList());
			
			fruizioneAutorizzazione = pd.getAutorizzazione();
			fruizioneAutorizzazioneAutenticati = TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione());
			fruizioneAutorizzazioneRuoli =  TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione());
			fruizioneAutorizzazioneRuoliTipologia =  AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()).toString();
			fruizioneAutorizzazioneRuoliMatch = evalnull( () -> pd.getRuoli().getMatch().toString() );
			
			if (pd.getXacmlPolicy() != null) {							
				xamlPolicy.setValue(pd.getXacmlPolicy().getBytes());
		        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			}
			
			canale = pd.getCanale();
		}
		
		String canaleStato = null;
		if(canale == null) {
			canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
		} else {
			canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
		}
		
		
		final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype); // uso endpointtype per capire se è la prima volta che entro
				
		
		IDServizio oldId = asps.getOldIDServizioForUpdate();
				
        if (! env.apsHelper.serviziCheckData(            		
        		TipoOperazione.CHANGE,
        		soggettiCompatibili,		
        		accordiList, 		 		
        		oldId.getNome(),		//asps.getNome(),				// oldnome
        		oldId.getTipo(),		//asps.getTipo(),				// oldtipo
        		oldId.getVersione(),	//asps.getVersione(),			// oldversione
        		asps.getNome(),
        		asps.getTipo(),
        		erogatore.getId().toString(), 	// idSoggErogatore
        		erogatore.getNome(),
        		erogatore.getTipo(),
        		as.getId().toString(),
        		env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
        		"no",  //servcorr,
        		endpointtype, // endpointtype determinarlo dal connettore,
        		endpoint_url,
        		null, 	// nome JMS
        		null, 	// tipo JMS,
        		httpConf!=null ? evalnull( httpConf::getUsername ) : null,	
        		httpConf!=null ? evalnull( httpConf::getUsername ) : null, 
        		null,   // initcont JMS,
        		null,   // urlpgk JMS,
        		null,   // provurl JMS 
        		null,   // connfact JMS
        		null, 	// sendas JMS, 
        		new BinaryParameter(),		//  wsdlimpler
        		new BinaryParameter(),		//  wsdlimplfru
        		asps.getId().toString(), 	
        		asps.getVersioneProtocollo(),	//  Il profilo è la versione protocollo in caso spcoop, è quello del soggetto erogatore.
        		asps.getPortType(),
        		ptArray,
				accordoPrivato,
				false, 	// this.privato,
        		endpoint_url,	// httpsurl, 
        		evalnull( () ->  httpsConf.getTipologia().toString() ),				// I valori corrispondono con con org.openspcoop2.utils.transport.http.SSLConstants
        		BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),				// this.httpshostverify,
        		(httpsConf!=null ? !httpsConf.isTrustAllServerCerts() : ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS), // httpsTrustVerifyCert
        		httpsServer!=null ? evalnull( httpsServer::getTruststorePath ) : null,				// this.httpspath
				evalnull( () -> ConnettoreHTTPApiHelper.getTruststoreType(httpsServer) ),		// this.httpstipo,
				httpsServer!=null ? evalnull( httpsServer::getTruststorePassword ) : null,			// this.httpspwd,
				httpsServer!=null ? evalnull( httpsServer::getAlgoritmo ) : null,					// this.httpsalgoritmo
				httpsstato,	//
        		httpskeystore, 	
        		"", //  this.httpspwdprivatekeytrust
        		httpsClient!=null ? evalnull( httpsClient::getKeystorePath ) : null,					// httpspathkey
        		evalnull( () -> ConnettoreHTTPApiHelper.getKeystoreType(httpsClient) ),	 		// httpstipokey, coincide con ConnettoriCostanti.TIPOLOGIE_KEYSTORE
        		httpsClient!=null ? evalnull( httpsClient::getKeystorePassword ) : null, 	 		// httpspwdkey
        		httpsClient!=null ? evalnull( httpsClient::getKeyPassword ) : null,	 				// httpspwdprivatekey
        		httpsClient!=null ? evalnull( httpsClient::getAlgoritmo ) : null,					// httpsalgoritmokey
        		httpsClient!=null ? evalnull( httpsClient::getKeyAlias ) : null,					// httpsKeyAlias
        		httpsServer!=null ? evalnull( httpsServer::getTruststoreCrl ) : null,					// httpsTrustStoreCRLs
        		httpsServer!=null ? evalnull( httpsServer::getTruststoreOcspPolicy) : null,				// httpsTrustStoreOCSPPolicy
        		httpsClient!=null ? evalnull( httpsClient::getKeystoreByokPolicy) : null,				// httpsKeyStoreBYOKPolicy
        		null, 								// tipoconn Da debug = null.	
        		as.getVersione().toString(), 		// Versione aspc
        		false,								// validazioneDocumenti Da debug = false
        		null,								// Da Codice console
        		ServletUtils.boolToCheckBoxStatus(http_stato),	// "yes" se utilizziamo http.
        		ServletUtils.boolToCheckBoxStatus(proxy_enabled),			
        		proxy!=null ? evalnull( proxy::getHostname ) : null,
    			evalnull( () -> proxy.getPorta().toString() ),
    			proxy!=null ? evalnull( proxy::getUsername ) : null,
    			proxy!=null ? evalnull( proxy::getPassword ) : null,				
    			ServletUtils.boolToCheckBoxStatus(tempiRisposta_enabled), 
    			evalnull( () -> timeoutConf.getConnectionTimeout().toString()),		// this.tempiRisposta_connectionTimeout, 
    			evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()),  // this.tempiRisposta_readTimeout, 
    			evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),		// this.tempiRisposta_tempoMedioRisposta,
        		ServletUtils.boolToCheckBoxStatus(false),	// opzioniAvanzate 
				"", 	// this.transferMode, 
				"", 	// this.transferModeChunkSize, 
				"", 	// this.redirectMode, 
				"", 	// this.redirectMaxHop,
				null,   // this.httpImpl
        		null,	// requestOutputFileName,
        		null,	// requestOutputFileName_permissions,
        		null,	// requestOutputFileNameHeaders, 
        		null,	// requestOutputFileNameHeaders_permissions, 
        		null,	// requestOutputParentDirCreateIfNotExists, 
        		null,	// requestOutputOverwriteIfExists,
        		null,	// responseInputMode,
        		null,	// responseInputFileName,
        		null,	// responseInputFileNameHeaders,
        		null,	// responseInputDeleteAfterRead,
        		null,	// responseInputWaitTime,
        		null,	// erogazioneSoggetto, Come da codice console.
        		null,	// erogazioneRuolo non viene utilizzato.
        		erogazioneAutenticazione, // erogazioneAutenticazione
        		ServletUtils.boolToCheckBoxStatus(erogazioneAutenticazioneOpzionale),					// erogazioneAutenticazioneOpzionale
        		erogazioneAutenticazionePrincipal, // erogazioneAutenticazionePrincipal
        		erogazioneAutenticazioneParametroList, // erogazioneAutenticazioneParametroList
        		AutorizzazioneUtilities.convertToStato(erogazioneAutorizzazione), // erogazioneAutorizzazione										   					// erogazioneAutorizzazione QUESTO E' lo STATO dell'autorizzazione
        		ServletUtils.boolToCheckBoxStatus( erogazioneAutorizzazioneAutenticati ), 			// erogazioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( erogazioneAutorizzazioneRuoli ),				// erogazioneAutorizzazioneRuoli, 
        		erogazioneAutorizzazioneRuoliTipologia,				// erogazioneAutorizzazioneRuoliTipologia,
        		erogazioneAutorizzazioneRuoliMatch,  	// erogazioneAutorizzazioneRuoliMatch,  AllAnyEnum == RuoloTipoMatch
        		env.isSupportatoAutenticazioneSoggetti,	
        		isErogazione,													// generaPACheckSoggetto (Un'erogazione genera una porta applicativa)
        		listExtendedConnettore,
        		null,																	// fruizioneServizioApplicativo
        		null,																	// Ruolo fruizione, non viene utilizzato.
        		fruizioneAutenticazione, // fruizioneAutenticazione 
        		ServletUtils.boolToCheckBoxStatus(fruizioneAutenticazioneOpzionale), // fruizioneAutenticazioneOpzionale
        		fruizioneAutenticazionePrincipal, // fruizioneAutenticazionePrincipal
        		fruizioneAutenticazioneParametroList, // fruizioneAutenticazioneParametroList
        		AutorizzazioneUtilities.convertToStato(fruizioneAutorizzazione),		// fruizioneAutorizzazione 	
        		ServletUtils.boolToCheckBoxStatus( fruizioneAutorizzazioneAutenticati ),  				// fruizioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( fruizioneAutorizzazioneRuoli ), 					// fruizioneAutorizzazioneRuoli,
        		fruizioneAutorizzazioneRuoliTipologia, 		// fruizioneAutorizzazioneRuoliTipologia,
        		fruizioneAutorizzazioneRuoliMatch,		// fruizioneAutorizzazioneRuoliMatch,
        		env.tipo_protocollo, 
        		xamlPolicy, 																//allegatoXacmlPolicy,
        		"",
        		null,		// tipoFruitore 
        		null,	// nomeFruitore
        		autenticazioneToken, 
        		tokenPolicy,
        		
        		apiKey ? org.openspcoop2.web.lib.mvc.Costanti.CHECK_BOX_ENABLED : org.openspcoop2.web.lib.mvc.Costanti.CHECK_BOX_DISABLED, // autenticazioneApiKey
        		apiKey && 
        				env.erogazioniHelper.isAutenticazioneApiKeyUseOAS3Names(
        						httpApiKey!=null ? evalnull(httpApiKey::getApiKeyHeader) : null, 
        						httpApiKey!=null ? evalnull(httpApiKey::getAppIdHeader) : null
        		), // useOAS3Names
        		apiKey && 
        			env.erogazioniHelper.isAutenticazioneApiKeyUseAppId(
        					httpApiKey!=null ? evalnull(httpApiKey::getAppId) : null
        		), // useAppId
        		httpApiKey!=null ? evalnull( httpApiKey::getApiKeyHeader ) : null, // apiKeyHeader
        		httpApiKey!=null ? evalnull( httpApiKey::getApiKey ) : null, // apiKeyValue
        		httpApiKey!=null ? evalnull( httpApiKey::getAppIdHeader ) : null, // appIdHeader
        		httpApiKey!=null ? evalnull( httpApiKey::getAppId ) : null, // appIdValue		
        		
        		false, // erogazioneServizioApplicativoServerEnabled, 
    			null, // rogazioneServizioApplicativoServer,
    			canaleStato, canale, gestioneCanaliEnabled
        	)) {
        	throw FaultCode.RICHIESTA_NON_VALIDA.toException( StringEscapeUtils.unescapeHtml( env.pd.getMessage()) );
        }
		
		
	}
	
	public static final List<IDSoggettoDB> getSoggettiCompatibiliAutorizzazione( CredenzialeTipo tipoAutenticazione, Boolean appId, IdSoggetto erogatore, ErogazioniEnv env ) throws DriverRegistroServiziException, DriverConfigurazioneException {
		
		PddTipologia pddTipologiaSoggettoAutenticati = null;
		boolean gestioneErogatoriSoggettiAutenticatiEscludiSoggettoErogatore = false;
		
		
		if(env.apsCore.isMultitenant() && env.apsCore.getMultitenantSoggettiErogazioni()!=null) {
			switch (env.apsCore.getMultitenantSoggettiErogazioni()) {
				case SOLO_SOGGETTI_ESTERNI:
					pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
					break;
				case ESCLUDI_SOGGETTO_EROGATORE:
					gestioneErogatoriSoggettiAutenticatiEscludiSoggettoErogatore = true;
					break;
				case TUTTI:
					break;
				}
		}
		
		List<String> tipiSoggettiGestitiProtocollo = env.soggettiCore.getTipiSoggettiGestitiProtocollo(env.tipo_protocollo);
		
		// calcolo soggetti compatibili con tipi protocollo supportati dalla pa e credenziali indicate
		List<IDSoggettoDB> list = env.soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, null, tipoAutenticazione, appId, pddTipologiaSoggettoAutenticati);
		
		if( !list.isEmpty() && gestioneErogatoriSoggettiAutenticatiEscludiSoggettoErogatore ) {
			for (int i = 0; i < list.size(); i++) {
				IDSoggettoDB soggettoCheck = list.get(i);
				if(soggettoCheck.getTipo().equals(erogatore.getTipo()) && soggettoCheck.getNome().equals(erogatore.getNome())) {
					list.remove(i);
					break;
				}
			}
		}
		
		return list;
	}

	public static org.openspcoop2.utils.BooleanNullable getAutenticazioneOpzionale(Object authn) {
		if(authn!=null) {
			if(authn instanceof APIImplAutenticazioneBasic) {
				return new org.openspcoop2.utils.BooleanNullable(((APIImplAutenticazioneBasic)authn).isOpzionale());
			}
			else if(authn instanceof APIImplAutenticazioneHttps) {
				return new org.openspcoop2.utils.BooleanNullable(((APIImplAutenticazioneHttps)authn).isOpzionale());
			}
			else if(authn instanceof APIImplAutenticazionePrincipal) {
				return new org.openspcoop2.utils.BooleanNullable(((APIImplAutenticazionePrincipal)authn).isOpzionale());
			}
			else if(authn instanceof APIImplAutenticazioneApiKey) {
				return new org.openspcoop2.utils.BooleanNullable(((APIImplAutenticazioneApiKey)authn).isOpzionale());
			}
			else if(authn instanceof APIImplAutenticazioneCustom) {
				return new org.openspcoop2.utils.BooleanNullable(((APIImplAutenticazioneCustom)authn).isOpzionale());
			}
		}
		return org.openspcoop2.utils.BooleanNullable.NULL();
	}
	
	public static TipoAutenticazionePrincipal getTipoAutenticazionePrincipal(Object authn){
		if(authn instanceof APIImplAutenticazionePrincipal) {
			
			APIImplAutenticazionePrincipal authPrincipal = (APIImplAutenticazionePrincipal) authn;
			TipoAutenticazioneEnum tipo = authPrincipal.getTipo();
			
			if(TipoAutenticazioneEnum.PRINCIPAL.equals(tipo)) {
				try {
					return Enums.tipoAutenticazionePrincipalFromRest.get(authPrincipal.getTipoPrincipal());
				} catch (Exception e) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione principal non è correttamente definita: " + e.getMessage() );
				}
			}
		}
		return null;
	}

	public static List<String> getAutenticazioneParametroList(ErogazioniEnv env,TipoAutenticazioneEnum tipo, Object authn) {
		if(TipoAutenticazioneEnum.HTTP_BASIC.equals(tipo)) {
			
			APIImplAutenticazioneBasic authnBasic = null;
			if(authn!=null) {
				if(authn instanceof APIImplAutenticazioneBasic) {
					authnBasic = (APIImplAutenticazioneBasic) authn;
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+tipo+"' non è correttamente definita (trovata configurazione '"+authn.getClass().getName()+"')"  );
				}
			}
			
			if(authnBasic!=null) {
				
				List<Proprieta> listConfig = new ArrayList<>();
        		if(authnBasic.isForward()!=null) {
        			Proprieta propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION);
        			if(authnBasic.isForward().booleanValue()) {	
        				propertyAutenticazione.setValore(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_FALSE);
        			}
        			else {
        				propertyAutenticazione.setValore(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_TRUE);
        			}
        			listConfig.add(propertyAutenticazione);
        		}
        		
        		if(!listConfig.isEmpty()) {
        			return env.stationCore.getParametroAutenticazione(Enums.tipoAutenticazioneFromRest.get(tipo).toString(), listConfig);
        		}
			}
			
		}
		else if(TipoAutenticazioneEnum.PRINCIPAL.equals(tipo)) {
			
			APIImplAutenticazionePrincipal authnPrincipal = null;
			if(authn!=null) {
				if(authn instanceof APIImplAutenticazionePrincipal) {
					authnPrincipal = (APIImplAutenticazionePrincipal) authn;
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+tipo+"' non è correttamente definita (trovata configurazione '"+authn.getClass().getName()+"')"  );
				}
			}
			
			if(authnPrincipal!=null) {	
        		
        		TipoAutenticazionePrincipal autenticazionePrincipal = Enums.tipoAutenticazionePrincipalFromRest.get(authnPrincipal.getTipoPrincipal());
        		Proprieta propTipoAuthn = new Proprieta();
        		propTipoAuthn.setNome(ParametriAutenticazionePrincipal.TIPO_AUTENTICAZIONE);
        		propTipoAuthn.setValore(autenticazionePrincipal.getValue());
        		
        		List<Proprieta> listConfig = new ArrayList<>();
        		listConfig.add(propTipoAuthn);
        		switch (autenticazionePrincipal) {
				case CONTAINER:
				case INDIRIZZO_IP:
				case INDIRIZZO_IP_X_FORWARDED_FOR:
					break;
        		case HEADER:
					if(authnPrincipal.getNome()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il nome di un header http per l'autenticazione principal '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					Proprieta propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.NOME);
					propertyAutenticazione.setValore(authnPrincipal.getNome());
					listConfig.add(propertyAutenticazione);
					break;
				case FORM:
					if(authnPrincipal.getNome()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il nome di un parametro della url per l'autenticazione principal '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.NOME);
					propertyAutenticazione.setValore(authnPrincipal.getNome());
					listConfig.add(propertyAutenticazione);
					break;
				case URL:
					if(authnPrincipal.getPattern()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stata fornita una espressione regolare per l'autenticazione principal '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.PATTERN);
					propertyAutenticazione.setValore(authnPrincipal.getPattern());
					listConfig.add(propertyAutenticazione);
					break;
				case TOKEN:
					if(authnPrincipal.getToken()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il token claim, da cui estrarre il principal, per l'autenticazione '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					if(TipoAutenticazionePrincipalToken.CUSTOM.equals(authnPrincipal.getToken())) {
						if(authnPrincipal.getNome()==null) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il nome del token claim, da cui estrarre il principal, per l'autenticazione '"+authnPrincipal.getTipoPrincipal()+"' indicata");
						}
					}
					propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.TOKEN_CLAIM);
					switch (authnPrincipal.getToken()) {
					case SUBJECT:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_SUBJECT);	
						break;
					case CLIENTID:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CLIENT_ID);	
						break;
					case USERNAME:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_USERNAME);	
						break;
					case EMAIL:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_EMAIL);	
						break;
					case CUSTOM:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CUSTOM);	
						break;
					}
					listConfig.add(propertyAutenticazione);
					
					if(TipoAutenticazionePrincipalToken.CUSTOM.equals(authnPrincipal.getToken())) {
						propertyAutenticazione = new Proprieta();
						propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.NOME);
						propertyAutenticazione.setValore(authnPrincipal.getNome());
						listConfig.add(propertyAutenticazione);
					}
					
					break;
				}
        		
        		if(authnPrincipal.isForward()!=null) {
        			
        			Proprieta propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL);
        			if(authnPrincipal.isForward()) {
        				propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_FALSE);
        			}
        			else {
        				propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE);
        			}
        			listConfig.add(propertyAutenticazione);
        		}
        		
        		if(!listConfig.isEmpty()) {
        			return env.stationCore.getParametroAutenticazione(tipo.toString(), listConfig);
        		}
        	}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Autenticazione principal non correttamente formata");
			}
		}
		else if(TipoAutenticazioneEnum.API_KEY.equals(tipo)) {
			
			APIImplAutenticazioneApiKey authnApiKey = null;
			if(authn!=null) {
				if(authn instanceof APIImplAutenticazioneApiKey) {
					authnApiKey = (APIImplAutenticazioneApiKey) authn;
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+tipo+"' non è correttamente definita (trovata configurazione '"+authn.getClass().getName()+"')"  );
				}
			}
			
			if(authn!=null) {
				
				List<Proprieta> listConfig = new ArrayList<>();
				
				// posizione 0: appId
				_addProprieta(ParametriAutenticazioneApiKey.APP_ID, 
						authnApiKey.isAppId()!=null && authnApiKey.isAppId() ? ParametriAutenticazioneApiKey.APP_ID_TRUE : ParametriAutenticazioneApiKey.APP_ID_FALSE, 
								listConfig);

				// posizione 1: queryParameter
				if(authnApiKey.getPosizione()!=null && authnApiKey.getPosizione().isQueryParameter()!=null) {
					_addProprieta(ParametriAutenticazioneApiKey.QUERY_PARAMETER, 
							authnApiKey.getPosizione().isQueryParameter() ? 
									ParametriAutenticazioneApiKey.QUERY_PARAMETER_TRUE : ParametriAutenticazioneApiKey.QUERY_PARAMETER_FALSE, 
									listConfig);
				}
				else {
					APIImplAutenticazioneApiKeyPosizione defaultValues = new APIImplAutenticazioneApiKeyPosizione(); // uso i default
					_addProprieta(ParametriAutenticazioneApiKey.QUERY_PARAMETER, 
							defaultValues.isQueryParameter() ? 
									ParametriAutenticazioneApiKey.QUERY_PARAMETER_TRUE : ParametriAutenticazioneApiKey.QUERY_PARAMETER_FALSE, 
									listConfig);
				}
				
				// posizione 2: header
				if(authnApiKey.getPosizione()!=null && authnApiKey.getPosizione().isHeader()!=null) {
					_addProprieta(ParametriAutenticazioneApiKey.HEADER, 
							authnApiKey.getPosizione().isHeader() ? 
									ParametriAutenticazioneApiKey.HEADER_TRUE : ParametriAutenticazioneApiKey.HEADER_FALSE, 
									listConfig);				
				}
				else {
					APIImplAutenticazioneApiKeyPosizione defaultValues = new APIImplAutenticazioneApiKeyPosizione(); // uso i default
					_addProprieta(ParametriAutenticazioneApiKey.HEADER, 
							defaultValues.isHeader() ? 
									ParametriAutenticazioneApiKey.HEADER_TRUE : ParametriAutenticazioneApiKey.HEADER_FALSE, 
									listConfig);
				}
				
				// posizione 3: cookie
				if(authnApiKey.getPosizione()!=null && authnApiKey.getPosizione().isCookie()!=null) {
					_addProprieta(ParametriAutenticazioneApiKey.COOKIE, 
							authnApiKey.getPosizione().isCookie() ? 
									ParametriAutenticazioneApiKey.COOKIE_TRUE : ParametriAutenticazioneApiKey.COOKIE_FALSE, 
									listConfig);	
				}
				else {
					APIImplAutenticazioneApiKeyPosizione defaultValues = new APIImplAutenticazioneApiKeyPosizione(); // uso i default
					_addProprieta(ParametriAutenticazioneApiKey.COOKIE, 
							defaultValues.isCookie() ? 
									ParametriAutenticazioneApiKey.COOKIE_TRUE : ParametriAutenticazioneApiKey.COOKIE_FALSE, 
									listConfig);
				}
				
				// posizione 4: useOAS3Names
				boolean apiKeyNamesCustom = false;
				boolean appIdNamesCustom = false;
				if(authnApiKey.getApiKeyNomi()!=null) {
					if(StringUtils.isNotEmpty(authnApiKey.getApiKeyNomi().getQueryParameter()) ||
							StringUtils.isNotEmpty(authnApiKey.getApiKeyNomi().getHeader()) ||
							StringUtils.isNotEmpty(authnApiKey.getApiKeyNomi().getCookie())) {
						apiKeyNamesCustom = true;
					}
				}
				if(authnApiKey.getAppIdNomi()!=null) {
					if(StringUtils.isNotEmpty(authnApiKey.getAppIdNomi().getQueryParameter()) ||
							StringUtils.isNotEmpty(authnApiKey.getAppIdNomi().getHeader()) ||
							StringUtils.isNotEmpty(authnApiKey.getAppIdNomi().getCookie())) {
						appIdNamesCustom = true;
					}
				}
				_addProprieta(ParametriAutenticazioneApiKey.USE_OAS3_NAMES, 
						apiKeyNamesCustom || appIdNamesCustom  ? ParametriAutenticazioneApiKey.USE_OAS3_NAMES_FALSE : ParametriAutenticazioneApiKey.USE_OAS3_NAMES_TRUE, 
								listConfig);
				
				// posizione 5: cleanApiKey
				_addProprieta(ParametriAutenticazioneApiKey.CLEAN_API_KEY, 
						authnApiKey.isApiKeyForward()!=null && authnApiKey.isApiKeyForward() ? ParametriAutenticazioneApiKey.CLEAN_API_KEY_FALSE : ParametriAutenticazioneApiKey.CLEAN_API_KEY_TRUE, 
								listConfig);	
				
				// posizione 6: cleanAppId
				_addProprieta(ParametriAutenticazioneApiKey.CLEAN_APP_ID, 
						authnApiKey.isAppIdForward()!=null && authnApiKey.isAppIdForward() ? ParametriAutenticazioneApiKey.CLEAN_APP_ID_FALSE : ParametriAutenticazioneApiKey.CLEAN_APP_ID_TRUE, 
								listConfig);
				
				// posizione 7: queryParameterApiKey
				_addProprieta(ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_API_KEY, 
						apiKeyNamesCustom && StringUtils.isNotEmpty(authnApiKey.getApiKeyNomi().getQueryParameter()) ? authnApiKey.getApiKeyNomi().getQueryParameter() : ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_API_KEY, 
								listConfig);
				
				// posizione 8: headerApiKey
				_addProprieta(ParametriAutenticazioneApiKey.NOME_HEADER_API_KEY, 
						apiKeyNamesCustom && StringUtils.isNotEmpty(authnApiKey.getApiKeyNomi().getHeader()) ? authnApiKey.getApiKeyNomi().getHeader() : ParametriAutenticazioneApiKey.DEFAULT_HEADER_API_KEY, 
								listConfig);
				
				// posizione 9: cookieApiKey
				_addProprieta(ParametriAutenticazioneApiKey.NOME_COOKIE_API_KEY, 
						apiKeyNamesCustom && StringUtils.isNotEmpty(authnApiKey.getApiKeyNomi().getCookie()) ? authnApiKey.getApiKeyNomi().getCookie() : ParametriAutenticazioneApiKey.DEFAULT_COOKIE_API_KEY, 
								listConfig);
				
				// posizione 10: queryParameterAppId
				_addProprieta(ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_APP_ID, 
						appIdNamesCustom && StringUtils.isNotEmpty(authnApiKey.getAppIdNomi().getQueryParameter()) ? authnApiKey.getAppIdNomi().getQueryParameter() : ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_APP_ID, 
								listConfig);

				// posizione 11: headerAppId
				_addProprieta(ParametriAutenticazioneApiKey.NOME_HEADER_APP_ID, 
						appIdNamesCustom && StringUtils.isNotEmpty(authnApiKey.getAppIdNomi().getHeader()) ? authnApiKey.getAppIdNomi().getHeader() : ParametriAutenticazioneApiKey.DEFAULT_HEADER_APP_ID, 
								listConfig);

				// posizione 12: cookieAppId
				_addProprieta(ParametriAutenticazioneApiKey.NOME_COOKIE_APP_ID, 
						appIdNamesCustom && StringUtils.isNotEmpty(authnApiKey.getAppIdNomi().getCookie()) ? authnApiKey.getAppIdNomi().getCookie() : ParametriAutenticazioneApiKey.DEFAULT_COOKIE_APP_ID, 
								listConfig);

        		if(!listConfig.isEmpty()) {
        			return env.stationCore.getParametroAutenticazione(Enums.tipoAutenticazioneFromRest.get(tipo).toString(), listConfig);
        		}
			}
			
		}
		return null;
	}

	private static final void _addProprieta(String nome, String valore, List<Proprieta> listConfig) {
		Proprieta propertyAutenticazione = new Proprieta();
		propertyAutenticazione.setNome(nome);
		propertyAutenticazione.setValore(valore);
		listConfig.add(propertyAutenticazione);
	}
	
	public static final void serviziCheckData(	
			TipoOperazione tipoOp,
			ErogazioniEnv env,
			AccordoServizioParteComuneSintetico as,
			AccordoServizioParteSpecifica asps,
			Optional<IdSoggetto> fruitore,
			APIImpl impl

			) throws Exception {
		
		boolean generaPortaApplicativa = !fruitore.isPresent();
		IdSoggetto erogatore = new IdSoggetto( new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
		erogatore.setId(asps.getIdSoggetto());
		
		 if (impl == null) { 
			 impl = new APIImpl();
			 impl.setConnettore(new BaseConnettoreHttp());
		 }
		
		 boolean accordoPrivato = as.getPrivato()!=null && as.getPrivato();		
         
         List<PortTypeSintetico> ptList = AccordiServizioParteSpecificaUtilities.getListaPortTypes(as, env.apsHelper);
         
         String[] ptArray =  ptList.stream()
         		.map( p -> p.getNome() )
         		.toArray(String[]::new);
		
		 // Determino i soggetti compatibili
        ConsoleSearch searchSoggetti = new ConsoleSearch(true);
		searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, env.tipo_protocollo);
		boolean fruizioniEscludiSoggettoFruitore = false;
		
		// In caso di Fruizione i soggetti erogatori compatibili sono determinati dalla configurazione di GovWay (tutti, escluso erogatore, esterni)
		if ( fruitore.isPresent() ) {
			ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);
			PortaDelegataSoggettiErogatori confFruizioneErogatori = confCore.getConfigurazioneGenerale().getMultitenant().getFruizioneSceltaSoggettiErogatori();
			
			if (confFruizioneErogatori == PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI) {
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
			}
			else if ( confFruizioneErogatori == PortaDelegataSoggettiErogatori.ESCLUDI_SOGGETTO_FRUITORE ) {
				fruizioniEscludiSoggettoFruitore = true;
			}
			
		} // In caso di erogazione invece possiamo solo assegnare soggetti appartenenti a una porta di tipo operativo. 
		else {
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
		}
		
		List<Soggetto> listSoggetti = env.soggettiCore.soggettiRegistroList(null, searchSoggetti);
		
		final boolean escludiFruitore = fruizioniEscludiSoggettoFruitore;
		String[] soggettiCompatibili = listSoggetti.stream()
				.filter( s -> generaPortaApplicativa || ( !escludiFruitore || (s.getId().longValue() != fruitore.get().getId().longValue()) ) )
				.map( s -> s.getId().toString())
				.toArray(String[]::new);
		
		if (soggettiCompatibili.length == 0) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non ci sono soggetti compatibili per erogare il servizio");
		}
		
		
		// Determino la lista Api
		List<IDAccordoDB> listaIdAPI = AccordiServizioParteSpecificaUtilities.getListaIdAPI(
				env.tipo_protocollo,
				env.userLogin,
				env.apsCore, 
				env.apsHelper
			);
		
        String[] accordiList =  listaIdAPI.stream()
        		.map( a -> a.getId().toString() )
        		.toArray(String[]::new);
        
        final BaseConnettoreHttp conn = impl.getConnettore();
        final ConnettoreConfigurazioneApiKey httpApiKey = conn.getAutenticazioneApikey();
		boolean apiKey = (httpApiKey!=null && httpApiKey.getApiKey()!=null && StringUtils.isNotEmpty(httpApiKey.getApiKey()));
		final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
        final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();
        
	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    

    	final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype); // uso endpointtype per capire se è la prima volta che entro
	
        
        final ConnettoreConfigurazioneHttpsClient httpsClient = httpsConf!=null ? evalnull( httpsConf::getClient ) : null;
      	final ConnettoreConfigurazioneHttpsServer httpsServer = httpsConf!=null ? evalnull( httpsConf::getServer ) : null;
      	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
      	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
    	final String tokenPolicy = conn.getTokenPolicy(); 
      	final boolean autenticazioneToken = tokenPolicy!=null;

        
		final boolean httpsstato = httpsClient != null;
		final boolean http_stato = conn.getAutenticazioneHttp() != null;
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}

        // Autenticazione e autorizzazione sono opzionali nel json.
        final OneOfAPIImplAutorizzazione authz = impl.getAutorizzazione();
        final OneOfAPIImplAutenticazione authn = impl.getAutenticazione();
        
        // Vincolo rilasciato
        // Se sono in modalità SPCoop non posso specificare l'autenticazione
        /**if ( env.profilo == ProfiloEnum.SPCOOP && generaPortaApplicativa && authn != null ) {
        	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile specificare l'autenticazione per un servizio spcoop");
        }*/
        
        FonteEnum ruoliFonte = FonteEnum.QUALSIASI;
        String erogazioneRuolo = null;
        boolean isRichiedente = false;
        boolean isRuoli = false;
        String statoAutorizzazione = null;
        TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(authn!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(authn); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, authn.getTipo(), authn);
        }
        final BooleanNullable autenticazioneOpzionaleNullable = getAutenticazioneOpzionale(authn); // gestisce authn se null
        AllAnyEnum ruoliRichiesti = null;
        
        if ( generaPortaApplicativa && as.getServiceBinding() == ServiceBinding.SOAP && authz != null && authz.getTipo() != null ) {
	        switch ( authz.getTipo() ) {
	        case ABILITATO:
	        	
	        	if(authz instanceof APIImplAutorizzazioneAbilitataNew) {
	        	
	        		APIImplAutorizzazioneAbilitataNew authzAbilitata = (APIImplAutorizzazioneAbilitataNew) authz;
	        		
		        	if(authzAbilitata.getRuoliFonte()!=null) {
		        		ruoliFonte = authzAbilitata.getRuoliFonte();
		        	}
		         	erogazioneRuolo = authzAbilitata.getRuolo();
		         	isRichiedente = authzAbilitata.isRichiedente();
		         	isRuoli = authzAbilitata.isRuoli();
		         	statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
		         	ruoliRichiesti = authzAbilitata.getRuoliRichiesti();
	        	}
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
	        	break;
	        case XACML_POLICY:
	        	
	        	if(authz instanceof APIImplAutorizzazioneXACML) {
		        	
	        		APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
	        	
		        	if (authzXacml.getPolicy() == null) {
		        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
		        	}
		        	if(authzXacml.getRuoliFonte()!=null) {
		        		ruoliFonte = authzXacml.getRuoliFonte();
		        	}
		        	statoAutorizzazione = AutorizzazioneUtilities.STATO_XACML_POLICY;
	        	}
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
	        	break;
	        case DISABILITATO:
	        	statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
	        	break;
	        case CUSTOM:
		    	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autorizzazione '"+authz.getTipo()+"' non utilizzabile");
	        }
        }
        
        if (isRichiedente) {
        	
        	final APIImplAutorizzazioneAbilitataNew configAuthzFinal = (APIImplAutorizzazioneAbilitataNew) authz;
                    	
            // Se ho abilitata l'autorizzazione puntuale, devo aver anche abilitata l'autenticazione
        	if ( env.isSupportatoAutenticazioneSoggetti && 
        			(authn == null || authn.getTipo() == TipoAutenticazioneEnum.DISABILITATO)  &&
        			(!generaPortaApplicativa || !env.isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazione) 
        			){
        		throw FaultCode.RICHIESTA_NON_VALIDA.toException(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ABILITARE_AUTENTICAZIONE_PER_AUTORIZZAZIONE_PUNTUALE);
        	}
       	
        	if ( !StringUtils.isEmpty(configAuthzFinal.getSoggetto()) ) {
        		
        		CredenzialeTipo credTipo = evalnull( () -> Enums.credenzialeTipoFromTipoAutenticazione.get(authn.getTipo()) );
        		Boolean appId = null; // da fare APIKEY
        		Optional<String> soggettoCompatibile = getSoggettiCompatibiliAutorizzazione(credTipo, appId, env.idSoggetto, env)
        	        	.stream()
        	        	.map( IDSoggettoDB::getNome )
        	        	.filter( s -> s.equals( configAuthzFinal.getSoggetto() ) )
        	        	.findAny();
        	
        		//Se ho scelto un soggetto, questo deve esistere ed essere compatibile con il profilo di autenticazione
	        	if ( !soggettoCompatibile.isPresent() ) {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto " + configAuthzFinal.getSoggetto() + " scelto per l'autorizzazione puntuale non esiste o non è compatibile con la modalità di autenticazione scelta");
	        	}
        	}
        	
        }
        
        if (isRuoli) {
        	
        	final APIImplAutorizzazioneAbilitataNew configAuthzFinal = (APIImplAutorizzazioneAbilitataNew) authz;
            
        	if (!StringUtils.isEmpty(configAuthzFinal.getRuolo())) {
	        	RuoliCore ruoliCore = new RuoliCore(env.stationCore);
	        	// Il ruolo deve esistere
	        	org.openspcoop2.core.registry.Ruolo regRuolo = null;
				try {
					regRuolo = ruoliCore.getRuolo(configAuthzFinal.getRuolo());
				} catch (DriverConfigurazioneException e) {	
					// ignore
				}
				
				if (regRuolo == null) {
					throw FaultCode.NOT_FOUND.toException("Non esiste nessun ruolo con nome " + configAuthzFinal.getRuolo());
				}
        	}
        }
        
	
        final BinaryParameter xamlPolicy = new BinaryParameter();
        if(authz instanceof APIImplAutorizzazioneXACML) {
        	APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
        	xamlPolicy.setValue(authzXacml.getPolicy());
        }
        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
        
        
        StringBuilder inUsoMessage = new StringBuilder();
		
		if ( AccordiServizioParteSpecificaUtilities.alreadyExists(
				env.apsCore, 
				env.apsHelper, 
				erogatore.getId(), 
				env.idServizioFactory.getIDServizioFromAccordo(asps),
				env.idAccordoFactory.getUriFromAccordo(as),
				evalnull( () -> fruitore.get().getTipo() ),
				evalnull( () -> fruitore.get().getNome() ),
				env.tipo_protocollo,
				asps.getVersioneProtocollo(),
				asps.getPortType(),
				! generaPortaApplicativa,
				generaPortaApplicativa,
				inUsoMessage
			) ) {
			throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml( inUsoMessage.toString() ));
		}

		/**final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString( authz.getTipo(),
				isPuntuale,
				isRuoli,
				false,
				ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
				autorizzazione_tokenOptions,					// Questo è il token claims
				tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
			);*/
		
		boolean gestioneCanaliEnabled = env.gestioneCanali;
		String canale = null;
		if(gestioneCanaliEnabled) {
			if(impl instanceof Erogazione) {
				canale = ((Erogazione)impl).getCanale();
			}
			else if(impl instanceof Fruizione) {
				canale = ((Fruizione)impl).getCanale();
			}
			if(canale!=null &&
				!env.canali.contains(canale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il canale fornito '" + canale + "' non è presente nel registro");
			}
		}
		String canaleStato = null;
		if(canale == null) {
			canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
		} else {
			canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
		}
		
        if (! env.apsHelper.serviziCheckData(            		
        		tipoOp,
        		soggettiCompatibili,		
        		accordiList, 		 		
        		asps.getNome(),				// oldnome
        		asps.getTipo(),				// oldtipo
        		asps.getVersione(),			// oldversione
        		asps.getNome(),
        		asps.getTipo(),
        		erogatore.getId().toString(), 	// idSoggErogatore
        		erogatore.getNome(),
        		erogatore.getTipo(),
        		as.getId().toString(),
        		env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
        		"no",  //servcorr,
        		endpointtype, // endpointtype determinarlo dal connettore,
        		impl.getConnettore().getEndpoint(),
        		null, 	// nome JMS
        		null, 	// tipo JMS,
        		httpConf!=null ? evalnull( httpConf::getUsername ) : null,	
        		httpConf!=null ? evalnull( httpConf::getPassword ) : null, 
        		null,   // initcont JMS,
        		null,   // urlpgk JMS,
        		null,   // provurl JMS 
        		null,   // connfact JMS
        		null, 	// sendas JMS, 
        		new BinaryParameter(),		//  wsdlimpler
        		new BinaryParameter(),		//  wsdlimplfru
        		BaseHelper.evalorElse( () -> asps.getId().toString(), "0"), 	
        		asps.getVersioneProtocollo(),	//  Il profilo è la versione protocollo in caso spcoop, è quello del soggetto erogatore.
        		asps.getPortType(),
        		ptArray,
				accordoPrivato,
				false, 	// this.privato,
        		conn.getEndpoint(),	// httpsurl, 
        		evalnull( () ->  httpsConf.getTipologia().toString() ),				// I valori corrispondono con con org.openspcoop2.utils.transport.http.SSLConstants
        		BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),				// this.httpshostverify,
        		(httpsConf!=null ? !httpsConf.isTrustAllServerCerts() : ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS), // httpsTrustVerifyCert
        		httpsServer!=null ? evalnull( httpsServer::getTruststorePath ) : null,				// this.httpspath
				evalnull( () -> ConnettoreHTTPApiHelper.getTruststoreType(httpsServer) ),		// this.httpstipo,
				httpsServer!=null ? evalnull( httpsServer::getTruststorePassword ) : null,			// this.httpspwd,
				httpsServer!=null ? evalnull( httpsServer::getAlgoritmo ) : null,					// this.httpsalgoritmo
				httpsstato,	//
        		httpskeystore, 	
        		"", //  this.httpspwdprivatekeytrust
        		httpsClient!=null ? evalnull( httpsClient::getKeystorePath ) : null,					// httpspathkey
        		evalnull( () -> ConnettoreHTTPApiHelper.getKeystoreType(httpsClient) ),	 		// httpstipokey, coincide con ConnettoriCostanti.TIPOLOGIE_KEYSTORE
        		httpsClient!=null ? evalnull( httpsClient::getKeystorePassword ) : null, 	 		// httpspwdkey
        		httpsClient!=null ? evalnull( httpsClient::getKeyPassword ) : null,	 				// httpspwdprivatekey
        		httpsClient!=null ? evalnull( httpsClient::getAlgoritmo ) : null,					// httpsalgoritmokey
        		httpsClient!=null ? evalnull( httpsClient::getKeyAlias ) : null,					// httpsKeyAlias
        		httpsServer!=null ? evalnull( httpsServer::getTruststoreCrl ) : null,					// httpsTrustStoreCRLs
        		httpsServer!=null ? evalnull( httpsServer::getTruststoreOcspPolicy) : null,				// httpsTrustStoreOCSPPolicy
                httpsClient!=null ? evalnull( httpsClient::getKeystoreByokPolicy) : null,				// httpsKeyStoreBYOKPolicy
        		null, 								// tipoconn Da debug = null.	
        		asps.getVersione().toString(), //as.getVersione().toString(), 		// Versione aspc
        		false,								// validazioneDocumenti Da debug = false
        		null,								// Da Codice console
        		ServletUtils.boolToCheckBoxStatus(http_stato),	// "yes" se utilizziamo http.
        		ServletUtils.boolToCheckBoxStatus(proxy_enabled),			
        		proxy!=null ? evalnull( proxy::getHostname ) : null,
    			evalnull( () -> proxy.getPorta().toString() ),
    			proxy!=null ? evalnull( proxy::getUsername ) : null,
    			proxy!=null ? evalnull( proxy::getPassword ) : null,				
    			ServletUtils.boolToCheckBoxStatus(tempiRisposta_enabled), 
    			evalnull( () -> timeoutConf.getConnectionTimeout().toString()),		// this.tempiRisposta_connectionTimeout, 
    			evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()),  // this.tempiRisposta_readTimeout, 
    			evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),		// this.tempiRisposta_tempoMedioRisposta,
        		ServletUtils.boolToCheckBoxStatus(false),	// opzioniAvanzate 
				"", 	// this.transferMode, 
				"", 	// this.transferModeChunkSize, 
				"", 	// this.redirectMode, 
				"", 	// this.redirectMaxHop,
				null,   // this.httpImpl 
        		null,	// requestOutputFileName,
        		null,	// requestOutputFileName_permissions,
        		null,	// requestOutputFileNameHeaders, 
        		null,	// requestOutputFileNameHeaders_permissions, 
        		null,	// requestOutputParentDirCreateIfNotExists, 
        		null,	// requestOutputOverwriteIfExists,
        		null,	// responseInputMode,
        		null,	// responseInputFileName,
        		null,	// responseInputFileNameHeaders,
        		null,	// responseInputDeleteAfterRead,
        		null,	// responseInputWaitTime,
        		null,	// erogazioneSoggetto, Come da codice console.
        		erogazioneRuolo,	//, non viene utilizzato.
        		evalnull( () -> Enums.tipoAutenticazioneFromRest.get(authn.getTipo()).toString() ),		// erogazioneAutenticazione
        		evalnull( () -> ServletUtils.boolToCheckBoxStatus( autenticazioneOpzionaleNullable!=null ? autenticazioneOpzionaleNullable.getValue() : null ) ),					// erogazioneAutenticazioneOpzionale
        		autenticazionePrincipal, // erogazioneAutenticazionePrincipal
        		autenticazioneParametroList, // erogazioneAutenticazioneParametroList
        		statoAutorizzazione,					   	// erogazioneAutorizzazione QUESTO E' lo STATO dell'autorizzazione
        		ServletUtils.boolToCheckBoxStatus( isRichiedente ), 			// erogazioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( isRuoli ),				// erogazioneAutorizzazioneRuoli, 
                Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(),				// erogazioneAutorizzazioneRuoliTipologia,
                ruoliRichiesti!=null ? ruoliRichiesti.toString() : null,  	// erogazioneAutorizzazioneRuoliMatch,  AllAnyEnum == RuoloTipoMatch
        		env.isSupportatoAutenticazioneSoggetti,	
        		generaPortaApplicativa,													// generaPACheckSoggetto (Un'erogazione genera una porta applicativa)
        		listExtendedConnettore,
        		null,																	// fruizioneServizioApplicativo
        		null,																	// Ruolo fruizione, non viene utilizzato.
        		evalnull( () -> Enums.tipoAutenticazioneFromRest.get(authn.getTipo()).toString() ),  // fruizioneAutenticazione 
        		evalnull( () -> ServletUtils.boolToCheckBoxStatus( autenticazioneOpzionaleNullable!=null ? autenticazioneOpzionaleNullable.getValue() : null ) ), 			// fruizioneAutenticazioneOpzionale
        		autenticazionePrincipal, // fruizioneAutenticazionePrincipal
        		autenticazioneParametroList, // fruizioneAutenticazioneParametroList
        		statoAutorizzazione,											// fruizioneAutorizzazione 	
        		ServletUtils.boolToCheckBoxStatus( isRichiedente ), 				// fruizioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( isRuoli ), 					// fruizioneAutorizzazioneRuoli,
                Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(), 					// fruizioneAutorizzazioneRuoliTipologia,
                ruoliRichiesti!=null ? ruoliRichiesti.toString() : null, 		// fruizioneAutorizzazioneRuoliMatch,
        		env.tipo_protocollo, 
        		xamlPolicy, 																//allegatoXacmlPolicy,
        		"",
        		evalnull( () -> fruitore.get().getTipo() ),		// tipoFruitore 
        		evalnull( () -> fruitore.get().getNome() ),			// nomeFruitore
        		autenticazioneToken,
        		tokenPolicy,
        		
				apiKey ? org.openspcoop2.web.lib.mvc.Costanti.CHECK_BOX_ENABLED : org.openspcoop2.web.lib.mvc.Costanti.CHECK_BOX_DISABLED, // autenticazioneApiKey
				apiKey && 
        			env.erogazioniHelper.isAutenticazioneApiKeyUseOAS3Names(
        					httpApiKey!=null ? evalnull(httpApiKey::getApiKeyHeader) : null, 
        					httpApiKey!=null ? evalnull(httpApiKey::getAppIdHeader) : null
        		), // useOAS3Names
        		apiKey && 
        			env.erogazioniHelper.isAutenticazioneApiKeyUseAppId(
        					httpApiKey!=null ? evalnull(httpApiKey::getAppId) : null
        		), // useAppId
        		httpApiKey!=null ? evalnull( httpApiKey::getApiKeyHeader ) : null, // apiKeyHeader
        		httpApiKey!=null ? evalnull( httpApiKey::getApiKey ) : null, // apiKeyValue
        		httpApiKey!=null ? evalnull( httpApiKey::getAppIdHeader ) : null, // appIdHeader
        		httpApiKey!=null ? evalnull( httpApiKey::getAppId ) : null, // appIdValue	
        		
        		false, // erogazioneServizioApplicativoServerEnabled, 
    			null, // rogazioneServizioApplicativoServer
    			canaleStato, canale, gestioneCanaliEnabled
        	)) {
        	throw FaultCode.RICHIESTA_NON_VALIDA.toException( StringEscapeUtils.unescapeHtml( env.pd.getMessage()) );
        }
        
        
		
	}


	
	public static final org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(final ErogazioniEnv env, final BaseConnettoreHttp conn) throws Exception {
		final org.openspcoop2.core.registry.Connettore regConnettore = new org.openspcoop2.core.registry.Connettore();
		fillConnettoreRegistro(regConnettore, env, conn, "");
		return regConnettore;
	}

	
	public static final void fillConnettoreRegistro(
			final org.openspcoop2.core.registry.Connettore regConnettore,
			final ErogazioniEnv env,
			final BaseConnettoreHttp conn,
			final String oldConnT
			) throws Exception {
		
		
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
		final ConnettoreConfigurazioneApiKey httpApiKey = conn.getAutenticazioneApikey();
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

		final ConnettoreConfigurazioneHttpsClient httpsClient = httpsConf!=null ? evalnull( httpsConf::getClient ) : null;
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = httpsConf!=null ? evalnull( httpsConf::getServer ) : null;
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = ErogazioniCheckNotNull.getHttpskeystore(httpsClient);
        			    
	     
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnT,						// oldConnT
				"",						// tipoConn Personalizzato
				conn.getEndpoint(),		// this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				httpConf!=null ? evalnull( httpConf::getUsername ) : null,
				httpConf!=null ? evalnull( httpConf::getPassword ) : null,
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				(httpsConf!=null ? !httpsConf.isTrustAllServerCerts() : ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS), // httpsTrustVerifyCert
				httpsServer!=null ? evalnull( httpsServer::getTruststorePath ) : null,				// this.httpspath
				evalnull( () -> ConnettoreHTTPApiHelper.getTruststoreType(httpsServer) ),	// this.httpstipo,
				httpsServer!=null ? evalnull( httpsServer::getTruststorePassword ) : null,			// this.httpspwd,
				httpsServer!=null ? evalnull( httpsServer::getAlgoritmo ) : null,					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				httpsClient!=null ? evalnull( httpsClient::getKeystorePath ) : null,				// pathkey
				evalnull( () -> ConnettoreHTTPApiHelper.getKeystoreType(httpsClient) ), 		// this.httpstipokey
				httpsClient!=null ? evalnull( httpsClient::getKeystorePassword ) : null,			// this.httpspwdkey 
				httpsClient!=null ? evalnull( httpsClient::getKeyPassword ) : null,				// this.httpspwdprivatekey,  
				httpsClient!=null ? evalnull( httpsClient::getAlgoritmo ) : null,				// this.httpsalgoritmokey,
				httpsClient!=null ? evalnull( httpsClient::getKeyAlias ) : null,					// httpsKeyAlias
				httpsServer!=null ? evalnull( httpsServer::getTruststoreCrl ) : null,					// httpsTrustStoreCRLs
				httpsServer!=null ? evalnull( httpsServer::getTruststoreOcspPolicy) : null,				// httpsTrustStoreOCSPPolicy
		        httpsClient!=null ? evalnull( httpsClient::getKeystoreByokPolicy) : null,				// httpsKeyStoreBYOKPolicy
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				proxy!=null ? evalnull( proxy::getHostname ) : null,
				evalnull( () -> proxy.getPorta().toString() ),
				proxy!=null ? evalnull( proxy::getUsername ) : null,
				proxy!=null ? evalnull( proxy::getPassword ) : null,
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transferMode, 
				"", 	// this.transferModeChunkSize, 
				"", 	// this.redirectMode, 
				"", 	// this.redirectMaxHop,
				null,   // this.httpImpl
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileName_permissions,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputFileNameHeaders_permissions,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				tokenPolicy, 
				
				httpApiKey!=null ? evalnull( httpApiKey::getApiKeyHeader ) : null, 
				httpApiKey!=null ? evalnull( httpApiKey::getApiKey ) : null,
				httpApiKey!=null ? evalnull( httpApiKey::getAppIdHeader ) : null, 
				httpApiKey!=null ? evalnull( httpApiKey::getAppId ): null, 
				
				null, // connettoreStatusParams
				listExtendedConnettore);			
	}
	
	public static final void createAps(
			ErogazioniEnv env,
			AccordoServizioParteSpecifica asps,
			org.openspcoop2.core.registry.Connettore regConnettore,
			APIImpl impl,
			boolean alreadyExists,
			boolean generaPortaApplicativa) throws Exception
	{
		final boolean generaPortaDelegata = !generaPortaApplicativa;
		
		// defaults
		if (impl.getAutenticazione() == null) {
			APIImplAutenticazioneHttps https = new APIImplAutenticazioneHttps();
			https.setTipo(TipoAutenticazioneEnum.HTTPS);
			https.setOpzionale(false);
			impl.setAutenticazione(https);
		}
		
		final OneOfAPIImplAutorizzazione authz = impl.getAutorizzazione();
        final OneOfAPIImplAutenticazione authn = impl.getAutenticazione();
        
        AccordoServizioParteComuneSintetico as = null;
        if(asps!=null) {
        	as = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
        }

        FonteEnum ruoliFonte = FonteEnum.QUALSIASI;
        
        boolean isRichiedente = false;
        boolean isRuoli = false;
        String statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
        String soggettoAutenticato = null;
        AllAnyEnum ruoliRichiesti = null;
        String ruolo = null;
        
        TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(authn!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(authn); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, authn.getTipo(), authn);
        }
        final org.openspcoop2.utils.BooleanNullable autenticazioneOpzionaleNullable = getAutenticazioneOpzionale(authn); // gestisce authn se null
                
        if ( evalnull( () -> authz.getTipo() ) != null) {
		    switch (authz.getTipo()) {
		    case ABILITATO:	
		    	
		    	if(authz instanceof APIImplAutorizzazioneAbilitataNew) {
		    		
		    		APIImplAutorizzazioneAbilitataNew authzAbilitata = (APIImplAutorizzazioneAbilitataNew) authz;
		    		
			    	if(authzAbilitata.getRuoliFonte()!=null) {
		        		ruoliFonte = authzAbilitata.getRuoliFonte();
			    	}
		         	isRichiedente = authzAbilitata.isRichiedente();
		         	isRuoli = authzAbilitata.isRuoli();
		         	statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
		         	if ( authzAbilitata.getSoggetto() != null )
		         		soggettoAutenticato = new IDSoggetto(env.tipo_soggetto, authzAbilitata.getSoggetto()).toString();
		         	ruoliRichiesti = authzAbilitata.getRuoliRichiesti();
		         	ruolo = authzAbilitata.getRuolo();
			    }
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
	         	
		    	break;
		    case XACML_POLICY:
		    	
		    	if(authz instanceof APIImplAutorizzazioneXACML) {
		    		
		    		APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
		    	
			    	if (authzXacml.getPolicy() == null) {
			    		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
		        	}
			    	if(authzXacml.getRuoliFonte()!=null) {
		        		ruoliFonte = authzXacml.getRuoliFonte();
			    	}
			    	statoAutorizzazione = AutorizzazioneUtilities.STATO_XACML_POLICY;
		    	}
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
		    	break;
		    case DISABILITATO:
		    	statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
		    	break;
		    case CUSTOM:
		    	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autorizzazione '"+authz.getTipo()+"' non utilizzabile");
		    }
        }
        
    	
        
    	IDServizio idServizio = null;
    	if(asps!=null) {
    		idServizio = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione());
    	}
	               
        BinaryParameter xamlPolicy = new BinaryParameter();
        if(authz instanceof APIImplAutorizzazioneXACML) {
        	APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
        	xamlPolicy.setValue(authzXacml.getPolicy());
        }
        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
              
		boolean gestioneCanaliEnabled = env.gestioneCanali;
		String canale = null;
		if(gestioneCanaliEnabled) {
			if(impl instanceof Erogazione) {
				canale = ((Erogazione)impl).getCanale();
			}
			else if(impl instanceof Fruizione) {
				canale = ((Fruizione)impl).getCanale();
			}
			if(canale!=null) {
				if(!env.canali.contains(canale)) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il canale fornito '" + canale + "' non è presente nel registro");
				}
			}
		}
		String canaleStato = null;
		if(canale == null) {
			canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
		} else {
			canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
		}

		
		ProtocolProperties p = null;
		if(generaPortaApplicativa) {
			p = getProtocolProperties(asps, env);
		} else {
			IDFruizione idFruizione = new IDFruizione();
			
			IDServizio idAps = new IDServizio();

			if(asps!=null) {
				idAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
				idAps.setPortType(asps.getPortType());
			}

			idFruizione.setIdServizio(idAps);
			IDSoggetto idFruitore = new IDSoggetto(env.idSoggetto.getTipo(), env.idSoggetto.getNome());
			idFruizione.setIdFruitore(idFruitore);

			Fruitore fruitore = getFruitore(asps, env.idSoggetto.getNome());
			if(fruitore==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il fruitore '" + env.idSoggetto.getNome() + "' non risulta definito");
			}
			p = getProtocolProperties(idFruizione, fruitore, env);
		}
		
        AccordiServizioParteSpecificaUtilities.create(
        		asps,
        		alreadyExists, 
				idServizio,											// idServizio,
				env.idSoggetto.toIDSoggetto(),						// idFruitore,
				env.tipo_protocollo,								// this.tipoProtocollo,
				env.apcCore.toMessageServiceBinding(as.getServiceBinding()), 
				env.idSoggetto.getId(), 							// Questo è l'id del soggetto a cui associare la porta delegata\applicativa
				regConnettore, 
				generaPortaApplicativa,										// generaPortaApplicativa,
				generaPortaDelegata, 										// generaPortaDelegata
				evalnull( () -> Enums.tipoAutenticazioneFromRest.get(authn.getTipo()).toString() ),											// erogazioneAutenticazione
        		evalnull( () -> ServletUtils.boolToCheckBoxStatus( autenticazioneOpzionaleNullable!=null ? autenticazioneOpzionaleNullable.getValue() : null ) ), 														// erogazioneAutenticazioneOpzionale
        		autenticazionePrincipal, // erogazioneAutenticazionePrincipal
        		autenticazioneParametroList, // erogazioneAutenticazioneParametroList
        		statoAutorizzazione,	// autorizzazione, è lo STATO 	
				ServletUtils.boolToCheckBoxStatus( isRichiedente ),			// 	autorizzazioneAutenticati,
				ServletUtils.boolToCheckBoxStatus( isRuoli ),				//	autorizzazioneRuoli,
		    	Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(),				//	erogazioneAutorizzazioneRuoliTipologia
		    	ruoliRichiesti!=null ? ruoliRichiesti.toString() : null,			// 	autorizzazioneRuoliMatch
				null,	// servizioApplicativo Come da Debug, 
				ruolo,	// ruolo: E' il ruolo scelto nella label "Ruolo" 
		    	soggettoAutenticato,	// soggettoAutenticato TODO BISOGNA AGGIUNGERE IL CONTROLLO CHE IL SOGGETTO AUTENTICATO SIA NEL REGISTRO 
		    	null,   // 	autorizzazioneAutenticatiToken, 
		    	null,   // 	autorizzazioneRuoliToken, 
		    	null,   // 	autorizzazioneRuoliTipologiaToken, 
		    	null,   // 	autorizzazioneRuoliMatchToken,
				null,	// autorizzazione_tokenOptions, 
				null,	// autorizzazioneScope, 
				null,	// scope, 
				null,	// autorizzazioneScopeMatch,
				xamlPolicy,	// allegatoXacmlPolicy,
				StatoFunzionalita.DISABILITATO.toString(),	// gestioneToken
				null,	// gestioneTokenPolicy,
				null,	// gestioneTokenOpzionale, 
				null,	// gestioneTokenValidazioneInput, 
				null,	// gestioneTokenIntrospection, 
				null,	// gestioneTokenUserInfo, 
				null,	// gestioneTokenTokenForward, 
				null,	// autenticazioneTokenIssuer, 
				null,	// autenticazioneTokenClientId,
				null,	// autenticazioneTokenSubject, 
				null,	// autenticazioneTokenUsername, 
				null,	// autenticazioneTokenEMail, 
				p, 
				ConsoleOperationType.ADD, 
				env.apsCore, 
				env.erogazioniHelper,
        		null, // nomeSAServer TODO quando si aggiunge applicativo server
        		canaleStato, canale, gestioneCanaliEnabled,
        		null, // identificazioneAttributiStato, 
        		null, //String [] attributeAuthoritySelezionate
        		null // attributeAuthorityAttributi
			);

		
	}
	
	public static Fruitore getFruitore(AccordoServizioParteSpecifica asps, String nome) {
		
		if(asps!=null && asps.getFruitoreList()!=null) {
			for(Fruitore f: asps.getFruitoreList()) {
				if(f.getNome().equals(nome)) {
					return f;
				}
			}
		}
		return null;
	}

	/*public static final AccordoServizioParteSpecifica getServizio(String tipo, String nome, Integer versione, IDSoggetto idErogatore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		
		return getServizio(nome, versione, tipo, idErogatore, env);
	}*/
	
	
	public static final AccordoServizioParteSpecifica getServizio(String tipo, String nome, Integer versione, IDSoggetto idErogatore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		if ( tipo == null ) {
			tipo = env.protocolFactoryMgr._getServiceTypes().get(env.tipo_protocollo).get(0);
		} 		
        final IDServizio idAps = env.idServizioFactory.getIDServizioFromValues(tipo, nome, idErogatore, versione);
        
        return env.apsCore.getServizio(idAps, true);
	}
	
	
	public static final boolean isErogazione(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws DriverConfigurazioneException, DriverRegistroServiziException {
		List<IDPortaApplicativa> pApplicative = env.paCore.getIDPorteApplicativeAssociate(env.idServizioFactory.getIDServizioFromAccordo(asps));
		return pApplicative != null && !pApplicative.isEmpty();
	}
	
	public static final boolean isFruizione(AccordoServizioParteSpecifica asps, IDSoggetto idFruitore, ErogazioniEnv env) throws DriverConfigurazioneException, DriverRegistroServiziException {
		List<IDPortaDelegata> pDelegate = env.pdCore.getIDPorteDelegateAssociate(env.idServizioFactory.getIDServizioFromAccordo(asps), idFruitore);
		return pDelegate != null && !pDelegate.isEmpty();
	}
	
	
	
	
	
	public static final AccordoServizioParteSpecifica getServizioIfErogazione(String tipo, String nome, Integer versione, IDSoggetto idErogatore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		final AccordoServizioParteSpecifica ret = getServizio(tipo, nome, versione, idErogatore, env);
		
		try {
			if (!isErogazione(ret,env)) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		
		return ret;
	}
	
	
	public static final AccordoServizioParteSpecifica getServizioIfFruizione(String tipo, String nome, Integer versione, IDSoggetto idErogatore, IDSoggetto idFruitore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		final AccordoServizioParteSpecifica ret = getServizio(tipo, nome, versione, idErogatore, env);
		
		try {
			if (!isFruizione(ret,idFruitore,env))
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return ret;
	}
	
//	public static final AllegatoGenerico deserializeAllegatoGenerico(Object o) {
//		AllegatoGenerico ret = deserializeDefault(o, AllegatoGenerico.class);
//		if (StringUtils.isEmpty(ret.getNome()) || ret.getDocumento() == null) {
//			throw FaultCode.RICHIESTA_NON_VALIDA.toException(AllegatoGenerico.class.getName() + ": Indicare i campi obbligatori 'nome' e 'documento'");
//		}
//		return ret;
//	}
//	
//	public static final AllegatoSpecificaSemiformale deserializeAllegatoSpecificaSemiformale(Object o) {
//		AllegatoSpecificaSemiformale ret = deserializeDefault(o, AllegatoSpecificaSemiformale.class);
//		if (StringUtils.isEmpty(ret.getNome()) || ret.getDocumento() == null || ret.getTipo() == null) {
//			throw FaultCode.RICHIESTA_NON_VALIDA.toException(AllegatoSpecificaSemiformale.class.getName() + ": Indicare i campi obbligatori 'nome', 'documento' e 'tipo'");
//		}
//		return ret;
//	}
	
	
		
	public static final Documento implAllegatoToDocumento(ApiImplAllegato body, AccordoServizioParteSpecifica asps) throws InstantiationException, IllegalAccessException {
		
		Documento documento = new Documento();
		documento.setIdProprietarioDocumento(asps.getId());
		
		RuoloAllegatoAPIImpl ruoloAllegato = body.getAllegato().getRuolo();
		
		documento.setRuolo(RuoliDocumento.valueOf(
				Enums.ruoliDocumentoFromApiImpl
				.get( ruoloAllegato).toString() )
				.toString()
			);
		
		switch (ruoloAllegato) {
		case ALLEGATO:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoGenerico)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoGenerico allegatoGenerico = (ApiImplAllegatoGenerico) body.getAllegato();
			documento.setFile(allegatoGenerico.getNome());
			documento.setByteContenuto(allegatoGenerico.getDocumento());
			documento.setTipo( evalnull( () -> allegatoGenerico.getNome().substring( allegatoGenerico.getNome().lastIndexOf('.')+1, allegatoGenerico.getNome().length())) );
			break;

		case SPECIFICASEMIFORMALE:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoSpecificaSemiformale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoSpecificaSemiformale allegatoSS = (ApiImplAllegatoSpecificaSemiformale) body.getAllegato();
			documento.setFile(allegatoSS.getNome());
			documento.setByteContenuto(allegatoSS.getDocumento());
			if(ErogazioniCheckNotNull.isNotNullTipoSpecifica(allegatoSS, documento)) {
				TipoSpecificaSemiformaleEnum tipoAllegato = (TipoSpecificaSemiformaleEnum) allegatoSS.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoSemiFormaleFromSpecifica.get(tipoAllegato) ).toString() );
			}
			break;
			
		case SPECIFICALIVELLOSERVIZIO:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoSpecificaLivelloServizio)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoSpecificaLivelloServizio allegatoLS = (ApiImplAllegatoSpecificaLivelloServizio) body.getAllegato();
			documento.setFile(allegatoLS.getNome());
			documento.setByteContenuto(allegatoLS.getDocumento());
			if(ErogazioniCheckNotNull.isNotNullTipoSpecifica(allegatoLS, documento)) {
				TipoSpecificaLivelloServizioEnum tipoAllegato = (TipoSpecificaLivelloServizioEnum) allegatoLS.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoLivelloServizioFromSpecifica.get(tipoAllegato).toString()) );
			}
			break;
			
		case SPECIFICASICUREZZA:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoSpecificaSicurezza)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoSpecificaSicurezza allegatoSSec = (ApiImplAllegatoSpecificaSicurezza) body.getAllegato();
			documento.setFile(allegatoSSec.getNome());
			documento.setByteContenuto(allegatoSSec.getDocumento());
			if(ErogazioniCheckNotNull.isNotNullTipoSpecifica(allegatoSSec, documento)) {
				TipoSpecificaSicurezzaEnum tipoAllegato = (TipoSpecificaSicurezzaEnum) allegatoSSec.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoSicurezzaFromSpecifica.get(tipoAllegato).toString()) );
			}
			break;
		}

		return documento;
	}
	
	
	public static final ApiImplAllegato documentoToImplAllegato(Documento doc) {
		ApiImplAllegato ret = new ApiImplAllegato();
	    
		RuoloAllegatoAPIImpl ruoloAllegato = Enums.ruoliApiImplFromDocumento.get( RuoliDocumento.valueOf(doc.getRuolo()));
		
		switch (ruoloAllegato) {
		case ALLEGATO: {
			
			ApiImplAllegatoGenerico allegatoGenerico = new ApiImplAllegatoGenerico();
			allegatoGenerico.setRuolo(ruoloAllegato);
			allegatoGenerico.setDocumento(doc.getByteContenuto());
			allegatoGenerico.setNome(doc.getFile());
			ret.setAllegato(allegatoGenerico);
			break;
		}
		
		case SPECIFICASEMIFORMALE: {
			
			ApiImplAllegatoSpecificaSemiformale allegatoSS = new ApiImplAllegatoSpecificaSemiformale();
			allegatoSS.setRuolo(ruoloAllegato);
			allegatoSS.setDocumento(doc.getByteContenuto());
			allegatoSS.setNome(doc.getFile());
			TipiDocumentoSemiformale tipo = TipiDocumentoSemiformale.toEnumConstant(doc.getTipo());
			allegatoSS.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class)));
			ret.setAllegato(allegatoSS);
			break;
		}
			
		case SPECIFICALIVELLOSERVIZIO:{
			
			ApiImplAllegatoSpecificaLivelloServizio allegatoSL = new ApiImplAllegatoSpecificaLivelloServizio();
			allegatoSL.setRuolo(ruoloAllegato);
			allegatoSL.setDocumento(doc.getByteContenuto());
			allegatoSL.setNome(doc.getFile());
			TipiDocumentoLivelloServizio tipo = TipiDocumentoLivelloServizio.toEnumConstant(doc.getTipo());
			allegatoSL.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaLivelloServizioEnum.class)));
			ErogazioniCheckNotNull.documentoToImplAllegato(allegatoSL, tipo);
			ret.setAllegato(allegatoSL);
			break;
		}
		
		case SPECIFICASICUREZZA:{
			
			ApiImplAllegatoSpecificaSicurezza allegatoSSec = new ApiImplAllegatoSpecificaSicurezza();
			allegatoSSec.setRuolo(ruoloAllegato);
			allegatoSSec.setDocumento(doc.getByteContenuto());
			allegatoSSec.setNome(doc.getFile());
			TipiDocumentoSicurezza tipo = TipiDocumentoSicurezza.toEnumConstant(doc.getTipo());
			allegatoSSec.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSicurezzaEnum.class)));
			ErogazioniCheckNotNull.documentoToImplAllegato(allegatoSSec, tipo);
			ret.setAllegato(allegatoSSec);
			break;
		}
		
		}
	    
	    return ret;
		
	}
	
	public static final ApiImplAllegatoItem ImplAllegatoToItem(ApiImplAllegato allegato) {
		ApiImplAllegatoItem ret = new ApiImplAllegatoItem();
		
		RuoloAllegatoAPIImpl ruoloAllegato = allegato.getAllegato().getRuolo();
		
		switch (ruoloAllegato) {
		case ALLEGATO: {
			
			ApiImplAllegatoGenerico allegatoGenerico = (ApiImplAllegatoGenerico) allegato.getAllegato();
			
			ApiImplAllegatoItemGenerico allegatoGenericoItem = new ApiImplAllegatoItemGenerico();
			allegatoGenericoItem.setRuolo(ruoloAllegato);
			allegatoGenericoItem.setNome(allegatoGenerico.getNome());
			ret.setAllegato(allegatoGenericoItem);
			break;
		}
		
		case SPECIFICASEMIFORMALE: {
			
			ApiImplAllegatoSpecificaSemiformale allegatoSS = (ApiImplAllegatoSpecificaSemiformale) allegato.getAllegato();
			
			ApiImplAllegatoItemSpecificaSemiformale allegatoItemSS = new ApiImplAllegatoItemSpecificaSemiformale();
			allegatoItemSS.setRuolo(ruoloAllegato);
			allegatoItemSS.setNome(allegatoSS.getNome());
			allegatoItemSS.setTipoSpecifica(allegatoSS.getTipoSpecifica());
			ret.setAllegato(allegatoItemSS);
			break;
		}
			
		case SPECIFICALIVELLOSERVIZIO:{
			
			ApiImplAllegatoSpecificaLivelloServizio allegatoSL = (ApiImplAllegatoSpecificaLivelloServizio) allegato.getAllegato();
			
			ApiImplAllegatoItemSpecificaLivelloServizio allegatoItemSL = new ApiImplAllegatoItemSpecificaLivelloServizio();
			allegatoItemSL.setRuolo(ruoloAllegato);
			allegatoItemSL.setNome(allegatoSL.getNome());
			allegatoItemSL.setTipoSpecifica(allegatoSL.getTipoSpecifica());
			ret.setAllegato(allegatoItemSL);
			break;
		}
		
		case SPECIFICASICUREZZA:{
			
			ApiImplAllegatoSpecificaSicurezza allegatoSSec = (ApiImplAllegatoSpecificaSicurezza) allegato.getAllegato();
			
			ApiImplAllegatoItemSpecificaSicurezza allegatoItemSSec = new ApiImplAllegatoItemSpecificaSicurezza();
			allegatoItemSSec.setRuolo(ruoloAllegato);
			allegatoItemSSec.setNome(allegatoSSec.getNome());
			allegatoItemSSec.setTipoSpecifica(allegatoSSec.getTipoSpecifica());
			ret.setAllegato(allegatoItemSSec);
			break;
		}
		
		}

		return ret;
		
	}
	
	public static void createAllegatoAsps(ApiImplAllegato body, final ErogazioniEnv env,
			AccordoServizioParteSpecifica asps) 
					throws Exception {

		Documento documento = ErogazioniApiHelper.implAllegatoToDocumento(body, asps);
		
		SerialiableFormFile filewrap = new SerialiableFormFile(documento.getFile(), documento.getByteContenuto());
		env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, documento.getRuolo());			

		boolean documentoUnivocoIndipendentementeTipo = true;
		if (env.archiviCore.existsDocumento(documento,ProprietariDocumento.servizio,documentoUnivocoIndipendentementeTipo)) {
			throw FaultCode.CONFLITTO.toException("Allegato con nome " + documento.getFile() + " già presente per l'erogazione");
		}
		
		if (!env.apsHelper.serviziAllegatiCheckData(TipoOperazione.ADD,filewrap,documento,env.protocolFactory)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
		switch (body.getAllegato().getRuolo()) {
			case ALLEGATO:
				asps.addAllegato(documento);
				break;
			case SPECIFICASEMIFORMALE:
				asps.addSpecificaSemiformale(documento);
				break;
			case SPECIFICASICUREZZA:
				asps.addSpecificaSicurezza(documento);
				break;
			case SPECIFICALIVELLOSERVIZIO:
				asps.addSpecificaLivelloServizio(documento);
				break;
		}

		env.apsCore.performUpdateOperation(env.userLogin, false, asps);
	}
	
	public static final Optional<Documento> getDocumento(List<Documento> list, String name) {
		return list.stream().filter(d -> d.getFile().equals(name)).findFirst();
		
	}
	
	public static final Optional<Long> getIdDocumento(List<Documento> list, String name) {
		return list.stream().filter(d -> d.getFile().equals(name)).findFirst().map( d -> d.getId());
	}
	
	public static final <T> Stream<T> mergeStreams(Stream<T>[] streams) {
		Stream<T> tmp = streams[0];
		
		for(int i = 1; i< streams.length; i++) {
			tmp = Stream.concat(tmp, streams[i]);
		}
		
		return tmp;
	}
	
	public static final Optional<Long> getIdDocumento(String nomeAllegato, final AccordoServizioParteSpecifica asps) {
		Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(asps.getSpecificaSemiformaleList(), nomeAllegato);
		
		if (!idDoc.isPresent()) idDoc = getIdDocumento(asps.getSpecificaLivelloServizioList(), nomeAllegato);
		if (!idDoc.isPresent()) idDoc = getIdDocumento(asps.getAllegatoList(), nomeAllegato);
		if (!idDoc.isPresent()) idDoc = getIdDocumento(asps.getSpecificaSicurezzaList(), nomeAllegato);
		return idDoc;
	}
	
	
	public static void updateAllegatoAsps(ApiImplAllegato body, String nomeAllegato, ErogazioniEnv env, AccordoServizioParteSpecifica asps)
			throws Exception {
		Documento oldDoc = BaseHelper.supplyOrNotFound( () -> env.archiviCore.getDocumento(nomeAllegato, null, null, asps.getId(), false, ProprietariDocumento.servizio), "Allegato di nome " + nomeAllegato); 
		
		Documento newDoc = ErogazioniApiHelper.implAllegatoToDocumento(body, asps);
		newDoc.setId(oldDoc.getId());
		newDoc.setOraRegistrazione(new Date());
		
		if (! org.apache.commons.lang.StringUtils.equals(newDoc.getRuolo(), oldDoc.getRuolo())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il ruolo di un allegato");
		}

		SerialiableFormFile filewrap = new SerialiableFormFile(newDoc.getFile(), newDoc.getByteContenuto());
		env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, newDoc.getRuolo());
		
		if (!env.apsHelper.serviziAllegatiCheckData(TipoOperazione.CHANGE,filewrap,newDoc,env.protocolFactory)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
		AccordiServizioParteSpecificaUtilities.sostituisciDocumentoAsps(asps, oldDoc, newDoc);


		env.apsCore.performUpdateOperation(env.userLogin, false, asps);
	}
	
	public static final void updateInformazioniGenerali(ApiImplInformazioniGenerali body, ErogazioniEnv env, AccordoServizioParteSpecifica asps, boolean isErogazione) throws Exception{

		AccordoServizioParteComuneSintetico as = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());

		ServiceBinding tipoApi = as.getServiceBinding();
		
		IDServizio oldIDServizioForUpdate = env.idServizioFactory.getIDServizioFromAccordo(asps);
		asps.setOldIDServizioForUpdate(oldIDServizioForUpdate);
		
		asps.setNome(body.getNome());
		if (!StringUtils.isEmpty(body.getTipo())) {
			asps.setTipo(body.getTipo());
		}
		
		if (tipoApi == ServiceBinding.SOAP && !StringUtils.isEmpty(body.getApiSoapServizio())) {
			asps.setPortType(body.getApiSoapServizio());
		}
		
		serviziUpdateCheckData(as, asps, isErogazione, env);
						
		List<Object> oggettiDaAggiornare = AccordiServizioParteSpecificaUtilities.getOggettiDaAggiornare(asps, env.apsCore);
		
		// eseguo l'aggiornamento
		env.apsCore.performUpdateOperation(env.userLogin, false, oggettiDaAggiornare.toArray());
	}
	
	public static final void overrideFruizioneUrlInvocazione(final HttpRequestWrapper wrap, final IDSoggetto idErogatore,
			final IDServizio idServizio, final PortaDelegata pd, final PortaDelegataAzione pdAzione, boolean setPattern, long idAzione) {
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, pd.getId().toString());
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pd.getNome());			
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pd.getIdSoggetto().toString());	
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE, setPattern ? pdAzione.getPattern() : pdAzione.getNome());			// Azione è il contenuto del campo pattern o del campo nome, che vengono settati nel campo pattern.
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID, null);						// Come da debug
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE, pdAzione.getIdentificazione().toString() );
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD, null);
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE, "");
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID, idErogatore.toString());	// Questo è il nome (uri) del soggetto erogatore
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID, idServizio.toString());
		if(idAzione>0) {
			wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID, idAzione+"");
		}
	}
	
	public static final void fillApiImplViewItemWithErogazione(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps, final ApiImplViewItem toFill) {
		try {
			IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
			AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = env.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();

			String nomePortaDefault = null;
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				if(mappinErogazione.isDefault()) {
					nomePortaDefault = mappinErogazione.getIdPortaApplicativa().getNome();
				}
				listaPorteApplicativeAssociate.add(env.paCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
			}
			
			IDPortaApplicativa idPAdefault = new IDPortaApplicativa();
			idPAdefault.setNome(nomePortaDefault);
			PortaApplicativa paDefault = env.paCore.getPortaApplicativa(idPAdefault);

			int numeroAbilitate = 0;
			int numeroConfigurazioni = listaMappingErogazionePortaApplicativa.size();
			int numeroConfigurazioniSchedulingDisabilitato = -1;
			boolean allActionRedefined = false;
						
			if(listaMappingErogazionePortaApplicativa.size()>1) {
				List<String> azioniL = new ArrayList<>();
				Map<String,String> azioni = env.paCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<>());
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = env.erogazioniHelper.allActionsRedefinedMappingErogazione(azioniL, listaMappingErogazionePortaApplicativa);
			}

			boolean isRidefinito = false;
			for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
				boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
				if(statoPA) {
					if(!allActionRedefined || !paAssociata.getNome().equals(nomePortaDefault)) {
						numeroAbilitate ++;
					}
				}
				
				if(allActionRedefined && paAssociata.getNome().equals(nomePortaDefault)) {
					// se tutte le azioni sono state ridefinite non devo controllare la porta di default
					continue;
				}

				if(!paAssociata.getNome().equals(nomePortaDefault) && env.apsHelper.isConnettoreRidefinito(paDefault, paDefault.getServizioApplicativoList().get(0), paAssociata, 
						paAssociata.getServizioApplicativoList().get(0), paAssociata.getServizioApplicativoList())) {
					isRidefinito = true;
				}
				
				if(paAssociata.getBehaviour()!=null && paAssociata.sizeServizioApplicativoList()>0) {
					for (PortaApplicativaServizioApplicativo paSA : paAssociata.getServizioApplicativoList()) {
						if(paSA!=null && paSA.getDatiConnettore()!=null && 
								StatoFunzionalita.DISABILITATO.equals(paSA.getDatiConnettore().getScheduling())) {
							numeroConfigurazioniSchedulingDisabilitato++;
						}
					}
				}
			}
			
			StatoDescrizione stato = getStatoDescrizione(numeroAbilitate, allActionRedefined, numeroConfigurazioni, numeroConfigurazioniSchedulingDisabilitato );
			
			ApiCanale canale = ErogazioniApiHelper.toApiCanale(env, paDefault, apc, false);
			
			String urlConnettore = ConnettoreAPIHelper.getUrlConnettore(getServizioApplicativoErogazione(idServizio, env.saCore, env.paCore, env.paHelper), isRidefinito);
			fillApiImplViewItemWithAsps(
					env, 
					asps, 
					toFill, 
					getUrlInvocazioneErogazione(asps, env),
					urlConnettore,
					getGestioneCorsFromErogazione(asps, env),
					idServizio.getSoggettoErogatore().getNome(),
					stato.stato,
					stato.descrizione,
					canale
				);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
			
	}
	
	public static final StatoDescrizione getStatoDescrizione(int numeroAbilitate, boolean allActionRedefined, int numeroConfigurazioni, int numeroConfigurazioniSchedulingDisabilitato ) {
		String stato_descrizione;
		StatoApiEnum statoApi;
		
		StatoDescrizione ret = new StatoDescrizione();
		
		if(numeroAbilitate == 0) {
			statoApi = StatoApiEnum.ERROR;
			stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP;
		} else if( 
				(!allActionRedefined && numeroAbilitate == numeroConfigurazioni) 
				||
				(allActionRedefined && numeroAbilitate == (numeroConfigurazioni-1)) // escludo la regola che non viene usata poiche' tutte le azioni sono ridefinite 
				) {
			if(numeroConfigurazioniSchedulingDisabilitato>0) {
				statoApi = StatoApiEnum.WARN;
				stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONNETTORI_MULTIPLI_SCHEDULING_DISABILITATO_TOOLTIP;
			}
			else {
				statoApi = StatoApiEnum.OK;
				stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP; 
			}
		} else  {
			statoApi = StatoApiEnum.WARN;
			stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP;
		}
		
		ret.stato = statoApi;
		ret.descrizione = stato_descrizione;
		
		return ret;
	}
	
	
	public static final void fillApiImplViewItemWithFruizione(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps, final IdSoggetto fruitore, final ApiImplViewItem toFill) throws Exception {
		
		IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		long idFruizione = env.apsCore.getIdFruizioneAccordoServizioParteSpecifica(fruitore.toIDSoggetto(), idServizio);
		List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = env.apsCore.serviziFruitoriMappingList(idFruizione, fruitore.toIDSoggetto(), idServizio, null);	
		List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();

		String nomePortaDefault = null;
		boolean isRidefinito = false;

		for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
			if(mappingFruizione.isDefault()) {
				nomePortaDefault = mappingFruizione.getIdPortaDelegata().getNome();
			} else {
				
				PortaDelegata pd = env.pdCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata());

				List<String> listaAzioniPDAssociataMappingNonDefault = pd.getAzione().getAzioneDelegataList();
				String azioneConnettore =  null;
				if(listaAzioniPDAssociataMappingNonDefault!=null && listaAzioniPDAssociataMappingNonDefault.size()>0) {
					azioneConnettore = listaAzioniPDAssociataMappingNonDefault.get(0);
				}

				Optional<Fruitore> fruit = BaseHelper.findFirst(asps.getFruitoreList(),
						f -> f.getTipo().equals(mappingFruizione.getIdFruitore().getTipo()) && f.getNome().equals(mappingFruizione.getIdFruitore().getNome()));

				if(fruit.isPresent()) {
					if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
						for (ConfigurazioneServizioAzione check : fruit.get().getConfigurazioneAzioneList()) {
							if(check.getAzioneList().contains(azioneConnettore)) {
								isRidefinito = true;
							}
						}
					}
				}
			}
			listaPorteDelegateAssociate.add(env.pdCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
		}
		
		int numeroAbilitate = 0;
		int numeroConfigurazioni = listaMappingFruzionePortaDelegata.size();
		boolean allActionRedefined = false;
		 
		if(listaMappingFruzionePortaDelegata.size()>1) {
			List<String> azioniL = new ArrayList<>();
			Map<String,String> azioni = env.pdCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<>());
			if(azioni != null && azioni.size() > 0)
				azioniL.addAll(azioni.keySet());
			allActionRedefined = env.erogazioniHelper.allActionsRedefinedMappingFruizione(azioniL, listaMappingFruzionePortaDelegata);
		}
		
		for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
			boolean statoPD = pdAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
			if(statoPD) {
				if(!allActionRedefined || !pdAssociata.getNome().equals(nomePortaDefault)) {
					numeroAbilitate ++;
				}
			}
			
			if(allActionRedefined && pdAssociata.getNome().equals(nomePortaDefault)) {
				// se tutte le azioni sono state ridefinite non devo controllare la porta di default
				continue;
			}
			
			// aggiungere qua eventuali altri check
		}
		
		StatoDescrizione stato = getStatoDescrizione(numeroAbilitate, allActionRedefined, numeroConfigurazioni, -1);		
		
		IDPortaDelegata idPDdefault = new IDPortaDelegata();
		idPDdefault.setNome(nomePortaDefault);
		PortaDelegata pdDefault = env.pdCore.getPortaDelegata(idPDdefault);
		ApiCanale canale = ErogazioniApiHelper.toApiCanale(env, pdDefault, apc, false);
		
		try {
			String connettore = ConnettoreAPIHelper.getUrlConnettore(evalnull( () -> getConnettoreFruizione(asps, fruitore, env)), isRidefinito);
			fillApiImplViewItemWithAsps(
					env, 
					asps, 
					toFill, 
					getUrlInvocazioneFruizione(asps, fruitore.toIDSoggetto(), env),
					connettore,
					getGestioneCorsFromFruizione(asps, fruitore.toIDSoggetto(), env),
					fruitore.getNome(),
					stato.stato,
					stato.descrizione,
					canale
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	public static final void fillApiImplViewItemWithAsps(
			final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps,
			final ApiImplViewItem toFill, 
			final String urlInvocazione, 
			final String urlConnettore,
			final CorsConfigurazione gestioneCors,
			final String nomeSoggetto,
			final StatoApiEnum stato,
			final String statoDescrizione,
			final ApiCanale canale) {

		try {
			final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
			final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ProfiloEnum profilo = toProfilo(asps.getTipo());

			toFill.setTipoServizio(idServizio.getTipo());
			toFill.setNome(asps.getNome());
			toFill.setVersione(asps.getVersione());
			toFill.setSoggetto(nomeSoggetto);	// Questo nelle fruizioni è il fruitore, nelle erogazioni è l'erogatore
			toFill.setApiNome(aspc.getNome());
			toFill.setApiTipo(TipoApiEnum.valueOf(aspc.getServiceBinding().name()));
			toFill.setApiVersione(aspc.getVersione());
			toFill.setProfilo(Objects.requireNonNullElse(profilo, ProfiloEnum.APIGATEWAY));
			toFill.setConnettore(urlConnettore);		
			toFill.setApiSoapServizio(asps.getPortType());
			toFill.setGestioneCors(Helper.boolToStatoFunzionalita(gestioneCors != null).toString());
			toFill.setUrlInvocazione(urlInvocazione);
			
			if(aspc.getGruppo()!=null && !aspc.getGruppo().isEmpty()) {
				toFill.setApiTags(new ArrayList<>());
				for (GruppoSintetico tag : aspc.getGruppo()) {
					toFill.addApiTagsItem(tag.getNome());
				}
			}
			
			/*if(numeroAbilitate == 0) {
				de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE);
				de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP);
			} else if( 
					(!allActionRedefined && numeroAbilitate == numeroConfigurazioni) 
					||
					(allActionRedefined && numeroAbilitate == (numeroConfigurazioni-1)) // escludo la regola che non viene usata poiche' tutte le azioni sono ridefinite 
					) {
				de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE);
				de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP);
			} else  {
				de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE);
				de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP);
			}*/
			
			toFill.setStato(stato);
			toFill.setStatoDescrizione(statoDescrizione);
			toFill.setCanale(canale);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	
	public static final ErogazioneViewItem aspsToErogazioneViewItem(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps) {
		ErogazioneViewItem ret = new ErogazioneViewItem();
		fillApiImplViewItemWithErogazione(env, asps, ret);
		return ret;
	}
	
	
	public static final FruizioneViewItem aspsToFruizioneViewItem(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps, final IdSoggetto fruitore) {

		FruizioneViewItem ret = new FruizioneViewItem();
		try {
			fillApiImplViewItemWithFruizione(env, asps, fruitore, ret);
			
			final IDSoggetto idErogatore = env.idServizioFactory.getIDServizioFromAccordo(asps).getSoggettoErogatore();	
			ret.setErogatore(idErogatore.getNome());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	
	public static final void fillApiImplItemFromView(ApiImplViewItem impl, ApiImplItem toFill) {
		toFill.setApiNome(impl.getApiNome());
		toFill.setApiSoapServizio(impl.getApiSoapServizio());
		toFill.setApiTipo(impl.getApiTipo());
		toFill.setApiVersione(impl.getApiVersione());
		toFill.setNome(impl.getNome());
		toFill.setProfilo(impl.getProfilo());
		toFill.setSoggetto(impl.getSoggetto());
		toFill.setStato(impl.getStato());
		toFill.setStatoDescrizione(impl.getStatoDescrizione());
		toFill.setTipoServizio(impl.getTipoServizio());
		toFill.setVersione(impl.getVersione());
		toFill.setApiTags(impl.getApiTags());
		toFill.setCanale(impl.getCanale());
	}
	
	
	public static final FruizioneItem fruizioneViewItemToFruizioneItem(FruizioneViewItem fru) {
		FruizioneItem ret = new FruizioneItem();
		fillApiImplItemFromView(fru, ret);
		ret.setErogatore(fru.getErogatore());
		return ret;
	}
	
	
	public static final ErogazioneItem erogazioneViewItemToErogazioneItem(ErogazioneViewItem ero) {
		ErogazioneItem ret = new ErogazioneItem();
		fillApiImplItemFromView(ero, ret);
		
		return ret;		
	}
	
	
	
	public static final void deleteAllegato(String nomeAllegato, final ErogazioniEnv env, final AccordoServizioParteSpecifica asps)
			throws Exception {
		final Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(nomeAllegato, asps);
		
		if ( env.delete_404 && !idDoc.isPresent() ) {
			throw FaultCode.NOT_FOUND.toException("Allegato di nome " + nomeAllegato + " non presente."); 
		}
		
		if (idDoc.isPresent()) {
			AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecificaAllegati(asps, env.userLogin, env.apsCore, env.apsHelper, Arrays.asList(idDoc.get()));
		}
	}
	
	
	public static final CorsConfigurazione getGestioneCorsFromErogazione(AccordoServizioParteSpecifica asps, ErogazioniEnv env) 
			throws DriverConfigurazioneException, DriverRegistroServiziException, DriverConfigurazioneNotFound, CoreException  {
		
		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPA);
		
		return pa.getGestioneCors();
	}
	
	
	public static final CorsConfigurazione getGestioneCorsFromFruizione(AccordoServizioParteSpecifica asps, IDSoggetto fruitore, ErogazioniEnv env) 
			throws DriverRegistroServiziException, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, fruitore);
		final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);

		return pd.getGestioneCors();
	}
	
	
	public static final InvocazioneServizio getInvocazioneServizioErogazione(IDServizio idServizio, ServiziApplicativiCore saCore, PorteApplicativeCore paCore, PorteApplicativeHelper paHelper) 
			throws DriverConfigurazioneNotFound, DriverConfigurazioneException, CoreException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
	
		ServizioApplicativo sa = getServizioApplicativoErogazione(idServizio, saCore, paCore, paHelper);
		        		
		return sa.getInvocazioneServizio();
	}
	
	public static final ServizioApplicativo getServizioApplicativoErogazione(IDServizio idServizio, ServiziApplicativiCore saCore, PorteApplicativeCore paCore, PorteApplicativeHelper paHelper) 
			throws DriverConfigurazioneNotFound, DriverConfigurazioneException, CoreException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
	
		final IDPortaApplicativa idPA = paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		PortaApplicativa pa = paCore.getPortaApplicativa(idPA);
		String nomeSaDefault = idPA.getNome();
		if(pa.sizeServizioApplicativoList()>0) {
			for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
				if(paHelper.isConnettoreDefault(pasa)) {
					nomeSaDefault = pasa.getNome();
					break;
				}
			}
		}
		return saCore.getServizioApplicativo(saCore.getIdServizioApplicativo(idServizio.getSoggettoErogatore(), nomeSaDefault));
	}
	
	public static final InvocazioneServizio getInvocazioneServizioErogazioneGruppo(IdServizio idAsps, IDServizio idServizio, ErogazioniEnv env, String gruppo) 
			throws DriverConfigurazioneNotFound, DriverConfigurazioneException, CoreException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
	
		final ServizioApplicativo sa = getServizioApplicativo(idAsps, idServizio, env, gruppo);
		return sa.getInvocazioneServizio();

	}
	
	public static final ServizioApplicativo getServizioApplicativo(IdServizio idAsps, IDServizio idServizio, ErogazioniEnv env, String gruppo) 
			throws DriverConfigurazioneNotFound, DriverConfigurazioneException, CoreException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
	
		IDPortaApplicativa idPaDefault = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		PortaApplicativa paDefault = env.paCore.getPortaApplicativa(idPaDefault);

		final IDPortaApplicativa idPA = (gruppo!=null) ? BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPA(gruppo, idAsps, env.apsCore), "Gruppo per l'erogazione scelta") : idPaDefault;
		final PortaApplicativa pa = (gruppo!=null) ? env.paCore.getPortaApplicativa(idPA): paDefault;
		
		final ServizioApplicativo sa;
		
		boolean isConnettoreRidefinito = false;
		try {
			if(gruppo!=null) {
				isConnettoreRidefinito = env.apsHelper.isConnettoreRidefinito(paDefault, paDefault.getServizioApplicativoList().get(0), pa, 
						pa.getServizioApplicativoList().get(0), pa.getServizioApplicativoList());
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		if(gruppo != null && isConnettoreRidefinito) {
			sa = env.saCore.getServizioApplicativo(env.saCore.getIdServizioApplicativo(idServizio.getSoggettoErogatore(), pa.getServizioApplicativoList().get(0).getNome()));
		} else {
			sa = env.saCore.getServizioApplicativo(env.saCore.getIdServizioApplicativo(idServizio.getSoggettoErogatore(), paDefault.getServizioApplicativoList().get(0).getNome()));
		}
		
		return sa;

	}
	
	public static final org.openspcoop2.core.registry.Connettore getConnettoreFruizione(AccordoServizioParteSpecifica asps, IdSoggetto fruitore, ErogazioniEnv env) {
		
		return asps.getFruitoreList().stream()
			.filter( f -> {
				try {
					return (new IDSoggetto(f.getTipo(), f.getNome()).equals(fruitore.toIDSoggetto())) && (f.getConnettore() != null);
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
			})
			.map( f -> {
				org.openspcoop2.core.registry.Connettore conn = f.getConnettore();
				return conn;
			})
			.findFirst()
			.orElse(null);
					
	}
	
	
	public static final String getUrlInvocazioneErogazione(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		
		
		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPA);
		final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);
		
		Configurazione config = confCore.getConfigurazioneGenerale();
		
		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}
		
		List<String> tags = new ArrayList<>();
		if(aspc!=null && aspc.getGruppo()!=null && aspc.getGruppo().size()>0) {
			for (int i = 0; i < aspc.getGruppo().size(); i++) {
				tags.add(aspc.getGruppo().get(i).getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}
		String canale = null;
		if(config!=null) {
			canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, pa.getCanale());
		}
		
		org.openspcoop2.message.constants.ServiceBinding sbMessage = org.openspcoop2.message.constants.ServiceBinding.SOAP;
		if(aspc!=null) {
			sbMessage = (ServiceBinding.REST.equals(aspc.getServiceBinding())) ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP;
		}
		
		UrlInvocazioneAPI urlInvocazioneAPI = UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, env.protocolFactory, RuoloContesto.PORTA_APPLICATIVA, 
				sbMessage, 
				pa.getNome(), idServizio.getSoggettoErogatore(),
				tags, canale);		
		return urlInvocazioneAPI.getUrl();
		
	}
	
	public static final String getUrlInvocazioneFruizione(AccordoServizioParteSpecifica asps, IDSoggetto fruitore, ErogazioniEnv env) throws Exception {
		

		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, fruitore);
		final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);
		final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);

		Configurazione config = confCore.getConfigurazioneGenerale();
		
		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}
		
		List<String> tags = new ArrayList<>();
		if(aspc!=null && aspc.getGruppo()!=null && aspc.getGruppo().size()>0) {
			for (int i = 0; i < aspc.getGruppo().size(); i++) {
				tags.add(aspc.getGruppo().get(i).getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}
		String canale = null;
		if(config!=null) {
			canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, pd.getCanale());
		}
		
		org.openspcoop2.message.constants.ServiceBinding sbMessage = org.openspcoop2.message.constants.ServiceBinding.SOAP;
		if(aspc!=null) {
			sbMessage = (ServiceBinding.REST.equals(aspc.getServiceBinding())) ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP;
		}
		
		UrlInvocazioneAPI urlInvocazioneAPI = UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, env.protocolFactory, RuoloContesto.PORTA_DELEGATA, 
				sbMessage, 
				pd.getNome(), fruitore,
				tags, canale);		
		return urlInvocazioneAPI.getUrl();
		
	}
	
	public static final void updateTracciamento(RegistrazioneDiagnosticiConfigurazione body, PortaApplicativa pa, ErogazioniEnv env) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		String tracciamentoStato = null;
		if(pa.getTracciamento()!=null && pa.getTracciamento().getStato()!=null && StatoFunzionalita.ABILITATO.equals(pa.getTracciamento().getStato())) {
			tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
		}
		else {
			tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		}
		
		String statoDiagnostici = null;
		String severita = null;
		if(StatoDefaultRidefinitoEnum.RIDEFINITO.equals(body.getStato())) {
			statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			
			if(body.getSeverita()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Severità non indicata."); 
			}
			switch (body.getSeverita()) {
			case ALL:
				severita = org.openspcoop2.core.config.constants.Severita.ALL.getValue();
				break;
			case DEBUG_HIGH:
				severita = org.openspcoop2.core.config.constants.Severita.DEBUG_HIGH.getValue();
				break;
			case DEBUG_MEDIUM:
				severita = org.openspcoop2.core.config.constants.Severita.DEBUG_MEDIUM.getValue();
				break;
			case DEBUG_LOW:
				severita = org.openspcoop2.core.config.constants.Severita.DEBUG_LOW.getValue();
				break;
			case INFO_PROTOCOL:
				severita = org.openspcoop2.core.config.constants.Severita.INFO_PROTOCOL.getValue();
				break;
			case INFO_INTEGRATION:
				severita = org.openspcoop2.core.config.constants.Severita.INFO_INTEGRATION.getValue();
				break;
			case ERROR_PROTOCOL:
				severita = org.openspcoop2.core.config.constants.Severita.ERROR_PROTOCOL.getValue();
				break;
			case ERROR_INTEGRATION:
				severita = org.openspcoop2.core.config.constants.Severita.ERROR_INTEGRATION.getValue();
				break;
			case FATAL:
				severita = org.openspcoop2.core.config.constants.Severita.FATAL.getValue();
				break;
			case OFF:
				severita = org.openspcoop2.core.config.constants.Severita.OFF.getValue();
				break;
			}
		}
		else {
			statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			
			if(body.getSeverita()!=null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Severità non attesa."); 
			}
		}
		
		PorteApplicativeUtilities.initTracciamento(pa, env.paCore, null,
				tracciamentoStato, statoDiagnostici, severita);
	}
	
	public static final void updateTracciamento(RegistrazioneDiagnosticiConfigurazione body, PortaDelegata pd, ErogazioniEnv env) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		String tracciamentoStato = null;
		if(pd.getTracciamento()!=null && pd.getTracciamento().getStato()!=null && StatoFunzionalita.ABILITATO.equals(pd.getTracciamento().getStato())) {
			tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
		}
		else {
			tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		}
		
		String statoDiagnostici = null;
		String severita = null;
		if(StatoDefaultRidefinitoEnum.RIDEFINITO.equals(body.getStato())) {
			statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			
			if(body.getSeverita()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Severità non indicata."); 
			}
			switch (body.getSeverita()) {
			case ALL:
				severita = org.openspcoop2.core.config.constants.Severita.ALL.getValue();
				break;
			case DEBUG_HIGH:
				severita = org.openspcoop2.core.config.constants.Severita.DEBUG_HIGH.getValue();
				break;
			case DEBUG_MEDIUM:
				severita = org.openspcoop2.core.config.constants.Severita.DEBUG_MEDIUM.getValue();
				break;
			case DEBUG_LOW:
				severita = org.openspcoop2.core.config.constants.Severita.DEBUG_LOW.getValue();
				break;
			case INFO_PROTOCOL:
				severita = org.openspcoop2.core.config.constants.Severita.INFO_PROTOCOL.getValue();
				break;
			case INFO_INTEGRATION:
				severita = org.openspcoop2.core.config.constants.Severita.INFO_INTEGRATION.getValue();
				break;
			case ERROR_PROTOCOL:
				severita = org.openspcoop2.core.config.constants.Severita.ERROR_PROTOCOL.getValue();
				break;
			case ERROR_INTEGRATION:
				severita = org.openspcoop2.core.config.constants.Severita.ERROR_INTEGRATION.getValue();
				break;
			case FATAL:
				severita = org.openspcoop2.core.config.constants.Severita.FATAL.getValue();
				break;
			case OFF:
				severita = org.openspcoop2.core.config.constants.Severita.OFF.getValue();
				break;
			}
		}
		else {
			statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			
			if(body.getSeverita()!=null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Severità non attesa."); 
			}
		}
		
		PorteDelegateUtilities.initTracciamento(pd, env.pdCore, null,
				tracciamentoStato, statoDiagnostici, severita);
	}
	
	public static final void updateTracciamento(RegistrazioneTransazioniConfigurazione body, 
			Object porta, ErogazioniEnv environment) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, ProtocolException {
		
		PortaApplicativa portaApplicativa = null;
		PortaDelegata portaDelegata = null;
		boolean applicativa = false;
		if(porta instanceof PortaApplicativa) {
			portaApplicativa = (PortaApplicativa) porta;
			applicativa = true;
		}
		else if(porta instanceof PortaDelegata) {
			portaDelegata = (PortaDelegata) porta;
		}
		else {
			throw new ProtocolException("Oggetto '"+porta.getClass().getName()+"' non supportato");
		}
		
		String statoDiagnostici = null;
		if(applicativa) {
			if(portaApplicativa.getTracciamento()!=null && portaApplicativa.getTracciamento().getSeverita()!=null) {
				statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			}
			else {
				statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			}
		}
		else {
			if(portaDelegata.getTracciamento()!=null && portaDelegata.getTracciamento().getSeverita()!=null) {
				statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			}
			else {
				statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			}
		}
	
		String severita = null;
		if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(statoDiagnostici)) {
			// prendo quella di default
			if(applicativa && portaApplicativa.getTracciamento()!=null && portaApplicativa.getTracciamento().getSeverita()!=null) {
				severita = portaApplicativa.getTracciamento().getSeverita().getValue();
			}
			else if(portaDelegata.getTracciamento()!=null && portaDelegata.getTracciamento().getSeverita()!=null) {
				severita = portaDelegata.getTracciamento().getSeverita().getValue();	
			}
			
			if(severita==null) {
				Configurazione config = environment.configCore.getConfigurazioneGenerale();
				severita = config.getMessaggiDiagnostici()!=null && config.getMessaggiDiagnostici().getSeverita()!=null ? 
						config.getMessaggiDiagnostici().getSeverita().getValue() : 
						null;
			}
		}
		
		String tracciamentoStato = null;
		boolean ridefinito = false;
		if(body.getStato()!=null && StatoDefaultRidefinitoEnum.RIDEFINITO.equals(body.getStato())) {
			tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			ridefinito = true;
		}
		else {
			tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		}
		
		if(applicativa) {
			PorteApplicativeUtilities.initTracciamento(portaApplicativa, environment.paCore, null,
					tracciamentoStato, statoDiagnostici, severita);
		}
		else {
			PorteDelegateUtilities.initTracciamento(portaDelegata, environment.pdCore, null,
					tracciamentoStato, statoDiagnostici, severita);
		}
		
		String dbStato = null;
		String dbStatoReqIn = null;
		String dbStatoReqOut = null;
		String dbStatoResOut = null;
		String dbStatoResOutComplete = null;
		boolean dbFiltroEsiti = false;
		if(body.getDatabase()!=null) {
			if(!ridefinito) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Stato tracciamento su db consentito solo con stato ridefinito."); 
			}
			if(body.getDatabase().getStato()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Stato tracciamento su db non indicato."); 
			}
			String prefixDB = "Stato tracciamento su db '"+body.getDatabase().getStato()+"'";
			
			boolean personalizzato = false;
			switch (body.getDatabase().getStato()) {
			case ABILITATO:
				dbStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.ABILITATO.getValue();
				break;
			case DISABILITATO:
				dbStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.DISABILITATO.getValue();
				break;
			case CONFIGURAZIONEESTERNA:
				/**dbStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.CONFIGURAZIONE_ESTERNA.getValue();*/
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" non valido."); 
			case PERSONALIZZATO:
				dbStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.getValue();
				personalizzato = true;
				break;
			}
			if(body.getDatabase().getFiltroEsiti()!=null) {
				dbFiltroEsiti = TracciamentoTransazioniStatoFase.ABILITATO.equals(body.getDatabase().getFiltroEsiti());
			}
			if(body.getDatabase().getFasi()!=null) {
				if(personalizzato) {
					if(body.getDatabase().getFasi().getRichiestaIngresso()!=null) {
						switch (body.getDatabase().getFasi().getRichiestaIngresso()) {
						case BLOCCANTE:
							dbStatoReqIn = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.getValue();
							break;
						case NON_BLOCCANTE:
							dbStatoReqIn = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.getValue();
							break;
						case DISABILITATO:
							dbStatoReqIn = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.DISABILITATO.getValue();
							break;
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" richiede la definizione delle fasi; fase 'richiesta ingresso' non definita"); 
					}
					
					if(body.getDatabase().getFasi().getRichiestaUscita()!=null) {
						switch (body.getDatabase().getFasi().getRichiestaUscita()) {
						case BLOCCANTE:
							dbStatoReqOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.getValue();
							break;
						case NON_BLOCCANTE:
							dbStatoReqOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.getValue();
							break;
						case DISABILITATO:
							dbStatoReqOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.DISABILITATO.getValue();
							break;
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" richiede la definizione delle fasi; fase 'richiesta uscita' non definita"); 
					}
					
					if(body.getDatabase().getFasi().getRispostaUscita()!=null) {
						switch (body.getDatabase().getFasi().getRispostaUscita()) {
						case BLOCCANTE:
							dbStatoResOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.getValue();
							break;
						case NON_BLOCCANTE:
							dbStatoResOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.getValue();
							break;
						case DISABILITATO:
							dbStatoResOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.DISABILITATO.getValue();
							break;
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" richiede la definizione delle fasi; fase 'risposta uscita' non definita"); 
					}
					
					if(body.getDatabase().getFasi().getRispostaConsegnata()!=null) {
						if(TracciamentoTransazioniStatoFase.ABILITATO.equals(body.getDatabase().getFasi().getRispostaConsegnata())) {
							dbStatoResOutComplete = org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.getValue();
						}else {
							dbStatoResOutComplete = org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO.getValue();
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" richiede la definizione delle fasi; fase 'risposta consegnata' non definita"); 
					}
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" non prevede la definizione delle fasi."); 
				}
			}
			else {
				if(personalizzato) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixDB+" richiede la definizione delle fasi.");
				}
			}
		}
		
		String fsStato = null;
		String fsStatoReqIn = null;
		String fsStatoReqOut = null;
		String fsStatoResOut = null;
		String fsStatoResOutComplete = null;
		boolean fsFiltroEsiti = false;
		
		String fileTraceStato = null;
		String fileTraceConfigFile = null;
		String fileTraceClient = null;
		String fileTraceClientHdr = null;
		String fileTraceClientBody = null;
		String fileTraceServer = null;
		String fileTraceServerHdr = null;
		String fileTraceServerBody = null;
		
		if(body.getFiletrace()!=null) {
			
			if(!ridefinito) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Stato tracciamento su filetrace consentito solo con stato ridefinito."); 
			}
			
			if(body.getFiletrace().getStato()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Stato tracciamento su filetrace non indicato."); 
			}
			String prefixFS =  "Stato tracciamento su filetrace '"+body.getFiletrace().getStato()+"'";
			
			boolean abilitataConf = false;
			boolean personalizzato = false;
			switch (body.getFiletrace().getStato()) {
			case ABILITATO:
				fsStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.ABILITATO.getValue();
				abilitataConf = true;
				break;
			case DISABILITATO:
				fsStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.DISABILITATO.getValue();
				break;
			case CONFIGURAZIONEESTERNA:
				fsStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.CONFIGURAZIONE_ESTERNA.getValue();
				break;
			case PERSONALIZZATO:
				fsStato = org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.getValue();
				personalizzato = true;
				abilitataConf = true;
				break;
			}
			if(body.getFiletrace().getFiltroEsiti()!=null) {
				fsFiltroEsiti = TracciamentoTransazioniStatoFase.ABILITATO.equals(body.getFiletrace().getFiltroEsiti());
			}
			if(body.getFiletrace().getFasi()!=null) {
				if(personalizzato) {
					if(body.getFiletrace().getFasi().getRichiestaIngresso()!=null) {
						switch (body.getFiletrace().getFasi().getRichiestaIngresso()) {
						case BLOCCANTE:
							fsStatoReqIn = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.getValue();
							break;
						case NON_BLOCCANTE:
							fsStatoReqIn = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.getValue();
							break;
						case DISABILITATO:
							fsStatoReqIn = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.DISABILITATO.getValue();
							break;
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" richiede la definizione delle fasi; fase 'richiesta ingresso' non definita"); 
					}
					
					if(body.getFiletrace().getFasi().getRichiestaUscita()!=null) {
						switch (body.getFiletrace().getFasi().getRichiestaUscita()) {
						case BLOCCANTE:
							fsStatoReqOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.getValue();
							break;
						case NON_BLOCCANTE:
							fsStatoReqOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.getValue();
							break;
						case DISABILITATO:
							fsStatoReqOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.DISABILITATO.getValue();
							break;
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" richiede la definizione delle fasi; fase 'richiesta uscita' non definita"); 
					}
					
					if(body.getFiletrace().getFasi().getRispostaUscita()!=null) {
						switch (body.getFiletrace().getFasi().getRispostaUscita()) {
						case BLOCCANTE:
							fsStatoResOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.getValue();
							break;
						case NON_BLOCCANTE:
							fsStatoResOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.getValue();
							break;
						case DISABILITATO:
							fsStatoResOut = org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.DISABILITATO.getValue();
							break;
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" richiede la definizione delle fasi; fase 'risposta uscita' non definita"); 
					}
					
					if(body.getFiletrace().getFasi().getRispostaConsegnata()!=null) {
						if(TracciamentoTransazioniStatoFase.ABILITATO.equals(body.getFiletrace().getFasi().getRispostaConsegnata())) {
							fsStatoResOutComplete = org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.getValue();
						}else {
							fsStatoResOutComplete = org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO.getValue();
						}
					}
					else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" richiede la definizione delle fasi; fase 'risposta consegnata' non definita"); 
					}
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" non prevede la definizione delle fasi."); 
				}
			}
			else {
				if(personalizzato) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" richiede la definizione delle fasi.");
				}
			}
			
			if(abilitataConf) {
				if(body.getFiletrace().getConfigPath()!=null || 
						body.getFiletrace().getDumpClient()!=null ||
						body.getFiletrace().getDumpServer()!=null) {
					fileTraceStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
					
					fileTraceConfigFile = body.getFiletrace().getConfigPath();
					
					if(body.getFiletrace().getDumpClient()!=null) {
						fileTraceClient = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						
						if(body.getFiletrace().getDumpClient().isHeaders()!=null && body.getFiletrace().getDumpClient().isHeaders().booleanValue()) {
							fileTraceClientHdr = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						}
						else {
							fileTraceClientHdr = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						
						if(body.getFiletrace().getDumpClient().isPayload()!=null && body.getFiletrace().getDumpClient().isPayload().booleanValue()) {
							fileTraceClientBody = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						}
						else {
							fileTraceClientBody = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
					}
					else {
						fileTraceClient = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					
					if(body.getFiletrace().getDumpServer()!=null) {
						fileTraceServer = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						
						if(body.getFiletrace().getDumpServer().isHeaders()!=null && body.getFiletrace().getDumpServer().isHeaders().booleanValue()) {
							fileTraceServerHdr = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						}
						else {
							fileTraceServerHdr = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						
						if(body.getFiletrace().getDumpServer().isPayload()!=null && body.getFiletrace().getDumpServer().isPayload().booleanValue()) {
							fileTraceServerBody = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						}
						else {
							fileTraceServerBody = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
					}
					else {
						fileTraceServer = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
				}
				else {
					fileTraceStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
				}
			}
			else {
				if(body.getFiletrace().getConfigPath()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" non consente la definizione del path della configurazione"); 
				}
				if(body.getFiletrace().getDumpClient()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" non consente la definizione del buffer dei messaggi scambiati con il client"); 
				}
				if(body.getFiletrace().getDumpServer()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(prefixFS+" non consente la definizione del buffer dei messaggi scambiati con il server"); 
				}
				fileTraceStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			}
			
		}
		
		
		String nuovaConfigurazioneEsiti = null;
		if(body.getFiltro()!=null && body.getFiltro().getEsiti()!=null && !body.getFiltro().getEsiti().isEmpty()) {
			
			if(!ridefinito) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Filtro esiti consentito solo con stato ridefinito."); 
			}
			
			Logger log = LoggerWrapperFactory.getLogger(ErogazioniApiHelper.class);
			EsitiProperties esitiProperties = EsitiProperties.getInstance(log, environment.protocolFactory);
			List<Integer> esiti = esitiProperties.getEsitiCode();
			int codeErroreProtocollo = esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_PROTOCOLLO.name());
			int codeFaultPdd = esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_SERVER.name());
			esiti.add(codeErroreProtocollo);
			esiti.add(codeFaultPdd);
			StringBuilder sb = new StringBuilder();
			for (Integer esito : body.getFiltro().getEsiti()) {
				if(sb.length()>0) {
					sb.append(",");
				}
				sb.append(esito.intValue());
				if(!existsEsito(esito, esiti )) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'esito '"+esito+"' indicato nel filtro non è supportato"); 	
				}
			}
			nuovaConfigurazioneEsiti = sb.toString();
		}
		
		
		String transazioniTempiElaborazione = null;
		String transazioniToken = null;
		if(body.getInformazioni()!=null) {
			
			if(!ridefinito) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Definizione delle informazioni sulle transazioni consentita solo con stato ridefinito."); 
			}
			
			if(body.getInformazioni().getTempiElaborazione()!=null) {
				if(TracciamentoTransazioniStatoFase.ABILITATO.equals(body.getInformazioni().getTempiElaborazione())) {
					transazioniTempiElaborazione = StatoFunzionalita.ABILITATO.getValue();
				}
				else {
					transazioniTempiElaborazione = StatoFunzionalita.DISABILITATO.getValue();
				}
			}
			else {
				transazioniTempiElaborazione = StatoFunzionalita.DISABILITATO.getValue();
			}
			
			if(body.getInformazioni().getToken()!=null) {
				if(TracciamentoTransazioniStatoFase.ABILITATO.equals(body.getInformazioni().getToken())) {
					transazioniToken = StatoFunzionalita.ABILITATO.getValue();
				}
				else {
					transazioniToken = StatoFunzionalita.DISABILITATO.getValue();
				}
			}
			else {
				transazioniToken = StatoFunzionalita.DISABILITATO.getValue();
			}
		}				
		

		if(applicativa) {
			PorteApplicativeUtilities.setTracciamentoTransazioni(portaApplicativa, environment.paCore,
					dbStato,
					dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
					dbFiltroEsiti,
					fsStato,
					fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
					fsFiltroEsiti,
					nuovaConfigurazioneEsiti,
					transazioniTempiElaborazione, transazioniToken,
					fileTraceStato, fileTraceConfigFile,
					fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
					fileTraceServer, fileTraceServerHdr, fileTraceServerBody);
		}
		else {
			PorteDelegateUtilities.setTracciamentoTransazioni(portaDelegata, environment.pdCore,
					dbStato,
					dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
					dbFiltroEsiti,
					fsStato,
					fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
					fsFiltroEsiti,
					nuovaConfigurazioneEsiti,
					transazioniTempiElaborazione, transazioniToken,
					fileTraceStato, fileTraceConfigFile,
					fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
					fileTraceServer, fileTraceServerHdr, fileTraceServerBody);
		}
	}
	private static boolean existsEsito(Integer esito, List<Integer> esiti ) {
		if(esiti!=null && esito!=null) {
			for (Integer e : esiti) {
				if(e.intValue() == esito.intValue()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static final DumpConfigurazione buildDumpConfigurazione(RegistrazioneMessaggi dumpConf, boolean isErogazione, ErogazioniEnv env) throws Exception {
		final RegistrazioneMessaggiConfigurazione richiesta = dumpConf.getRichiesta();
		final RegistrazioneMessaggiConfigurazione risposta = dumpConf.getRisposta();

		final String statoDump = dumpConf.getStato().toString(); 
		final String statoDumpRichiesta 		 	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.isAbilitato() )).toString();
		final String statoDumpRisposta 				= Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.isAbilitato() )).toString();
		
		final String dumpRichiestaIngressoHeader	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isHeaders() )).toString();
		final String dumpRichiestaIngressoPayload   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isPayload() )).toString();
	    String dumpRichiestaIngressoPayloadParsing = Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRichiestaIngressoBody 		= Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRichiestaIngressoAttachments =  Helper.boolToStatoFunzionalitaConf(false).toString();
		if(richiesta!=null && richiesta.getIngresso()!=null && 
				richiesta.getIngresso().isPayload()!=null && richiesta.getIngresso().isPayload()) {
			dumpRichiestaIngressoPayloadParsing = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isPayloadParsing() )).toString();
			if(richiesta.getIngresso().isPayloadParsing()!=null && richiesta.getIngresso().isPayloadParsing()) {
				dumpRichiestaIngressoBody   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isBody() )).toString();
				dumpRichiestaIngressoAttachments   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isAttachments() )).toString();
			}
		}
		
		final String dumpRichiestaUscitaHeader	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isHeaders() )).toString();
		final String dumpRichiestaUscitaPayload   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isPayload() )).toString();
	    String dumpRichiestaUscitaPayloadParsing = Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRichiestaUscitaBody 		= Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRichiestaUscitaAttachments =  Helper.boolToStatoFunzionalitaConf(false).toString();
		if(richiesta!=null && richiesta.getUscita()!=null && 
				richiesta.getUscita().isPayload()!=null && richiesta.getUscita().isPayload()) {
			dumpRichiestaUscitaPayloadParsing = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isPayloadParsing() )).toString();
			if(richiesta.getUscita().isPayloadParsing()!=null && richiesta.getUscita().isPayloadParsing()) {
				dumpRichiestaUscitaBody   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isBody() )).toString();
				dumpRichiestaUscitaAttachments   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isAttachments() )).toString();
			}
		}
		
		final String dumpRispostaIngressoHeader	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isHeaders() )).toString();
		final String dumpRispostaIngressoPayload   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isPayload() )).toString();
	    String dumpRispostaIngressoPayloadParsing = Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRispostaIngressoBody 		= Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRispostaIngressoAttachments =  Helper.boolToStatoFunzionalitaConf(false).toString();
		if(risposta!=null && risposta.getIngresso()!=null && 
				risposta.getIngresso().isPayload()!=null && risposta.getIngresso().isPayload()) {
			dumpRispostaIngressoPayloadParsing = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isPayloadParsing() )).toString();
			if(risposta.getIngresso().isPayloadParsing()!=null && risposta.getIngresso().isPayloadParsing()) {
				dumpRispostaIngressoBody   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isBody() )).toString();
				dumpRispostaIngressoAttachments   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isAttachments() )).toString();
			}
		}
		
		final String dumpRispostaUscitaHeader	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isHeaders() )).toString();
		final String dumpRispostaUscitaPayload   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isPayload() )).toString();
	    String dumpRispostaUscitaPayloadParsing = Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRispostaUscitaBody 		= Helper.boolToStatoFunzionalitaConf(false).toString();
		String dumpRispostaUscitaAttachments =  Helper.boolToStatoFunzionalitaConf(false).toString();
		if(risposta!=null && risposta.getUscita()!=null && 
				risposta.getUscita().isPayload()!=null && risposta.getUscita().isPayload()) {
			dumpRispostaUscitaPayloadParsing = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isPayloadParsing() )).toString();
			if(risposta.getUscita().isPayloadParsing()!=null && risposta.getUscita().isPayloadParsing()) {
				dumpRispostaUscitaBody   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isBody() )).toString();
				dumpRispostaUscitaAttachments   = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isAttachments() )).toString();
			}
		}
		
		DumpConfigurazione ret = new DumpConfigurazione();
		
		// Caso porta applicativa
		if (isErogazione) {
			if (!env.paHelper.checkDataConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(), 
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ret = env.paHelper.getConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(),			// realtime, Come da debug
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments
				);
		} 
		else {	
			
			// Caso porta delegata
			
			if (!env.pdHelper.checkDataConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(),	// realtime 
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ret = env.pdHelper.getConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(),	// realtime, Come da debug 
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments
				);
		}
		
		return ret;
		
	}
	
	
	public static final List<MappingErogazionePortaApplicativa> getMappingGruppiPA(String nomeGruppo, IdServizio idAsps,  
			AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		
		return apsCore.mappingServiziPorteAppList(idAsps,idAsps.getId(), null).stream()
			.filter( m -> m.getDescrizione().equals(nomeGruppo) )
			.collect(Collectors.toList());
		
	}
	
	
	public static final List<MappingFruizionePortaDelegata> getMappingGruppiPD(
			String nomeGruppo,
			IDSoggetto idFruitore,
			IdServizio idAsps,  
			AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		
		return apsCore.serviziFruitoriMappingList(idFruitore, idAsps, null).stream()
			.filter( m -> m.getDescrizione().equals(nomeGruppo))
			.collect(Collectors.toList());
		
	}
	
	public static final IDPortaApplicativa getIDGruppoPA(String nome, IdServizio idAsps, AccordiServizioParteSpecificaCore apsCore) throws Exception {
		
		return getMappingGruppiPA(nome, idAsps, apsCore).stream()
				.map( m -> m.getIdPortaApplicativa() )
				.findFirst()
				.orElse(null);
		
	}
	
	
	public static final IDPortaApplicativa getIDGruppoPADefault(IdServizio idAsps, AccordiServizioParteSpecificaCore apsCore) throws Exception {
		return AccordiServizioParteSpecificaUtilities.getDefaultMappingPA(apsCore.mappingServiziPorteAppList(idAsps,idAsps.getId(), null))
				.getIdPortaApplicativa();
	}
	
	
	public static final IDPortaDelegata getIDGruppoPDDefault(IDSoggetto idFruitore,	IdServizio idAsps,	AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		return AccordiServizioParteSpecificaUtilities.getDefaultMappingPD(apsCore.serviziFruitoriMappingList(idFruitore,idAsps,null))
				.getIdPortaDelegata();
	}
	
	
	public static final IDPortaDelegata getIDGruppoPD(	
			String nome,
			IDSoggetto idFruitore,
			IdServizio idAsps,  
			AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		
		return getMappingGruppiPD(nome, idFruitore, idAsps, apsCore).stream()
				.map( m -> m.getIdPortaDelegata())
				.findFirst()
				.orElse(null);
	}
	
	
	public static final List<String> getAzioniOccupateErogazione(IdServizio idAsps, AccordiServizioParteSpecificaCore apsCore, PorteApplicativeCore paCore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		 return apsCore.mappingServiziPorteAppList(idAsps,idAsps.getId(), null).stream()
		 	.map ( m -> {
					try {
						return paCore.getPortaApplicativa(m.getIdPortaApplicativa());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
		 		})
		 	.filter( pa -> pa.getAzione() != null && pa.getAzione().getAzioneDelegataList() != null )
		 	.flatMap( pa -> pa.getAzione().getAzioneDelegataList().stream())
		 	.collect(Collectors.toList());

	}
	
	public static final List<String> getAzioniDisponibiliRateLimitingPortaApplicativa(IdServizio idAsps, PortaApplicativa pa) {
		return null;
	}
	
	public static final List<String> getAzioniDisponibiliRateLimitingPortaDelegata(PortaDelegata pd, ErogazioniEnv env) {
		if (pd.getAzione() != null && pd.getAzione().getAzioneDelegataList().size() > 0) {
			return pd.getAzione().getAzioneDelegataList();
		}
		
		return null;
		
		//env.paCore.getAzioni(asps, aspc, false, false, getAzioniOccupateFruizione()));
		/*List<String> azioniAll = this.confCore.getAzioni(env.tipo_protocollo, protocolliValue,
				pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
				pa.getServizio().getTipo(), pa.getServizio().getNome(), pa.getServizio().getVersione());*/
	}
	
	public static final List<String> getAzioniOccupateFruizione(IdServizio idAsps, IDSoggetto idFruitore, AccordiServizioParteSpecificaCore apsCore, PorteDelegateCore pdCore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		 return apsCore.serviziFruitoriMappingList(idFruitore, idAsps, null).stream()
		 	.map ( m -> {
					try {
						return pdCore.getPortaDelegata(m.getIdPortaDelegata());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
		 		})
		 	.filter( pa -> pa.getAzione() != null && pa.getAzione().getAzioneDelegataList() != null )
		 	.flatMap( pa -> pa.getAzione().getAzioneDelegataList().stream())
		 	.collect(Collectors.toList());

	}

	
	public static final List<String> getDescrizioniMappingPA(List<MappingErogazionePortaApplicativa> listaMappingErogazione) {
		return listaMappingErogazione.stream().map( m -> m.getDescrizione() ).collect(Collectors.toList() ); 
	}

	
	public static final List<String> getDescrizioniMappingPD(List<MappingFruizionePortaDelegata> listaMappingErogazione) {
		return listaMappingErogazione.stream().map( m -> m.getDescrizione() ).collect(Collectors.toList() ); 
	}



	public static final ListaApiImplAllegati findAllAllegati(String q, Integer limit, Integer offset, UriInfo uriInfo,
			final ErogazioniEnv env, final AccordoServizioParteSpecifica asps)
			throws DriverRegistroServiziException, InstantiationException, IllegalAccessException, CoreException {
		int idLista = Liste.SERVIZI_ALLEGATI;
		
		final ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
		final List<Documento> lista = env.apsCore.serviziAllegatiList(asps.getId().intValue(), ricerca);
		
		if ( env.findall_404 && lista.isEmpty() ) {
			throw FaultCode.NOT_FOUND.toException("Nessun allegato associato");
		}
		
		ListaApiImplAllegati ret = ListaUtils.costruisciListaPaginata(
				uriInfo,
				ricerca.getIndexIniziale(idLista),
				ricerca.getPageSize(idLista), 
				ricerca.getNumEntries(idLista), 
				ListaApiImplAllegati.class
			);
		
		lista.forEach( d-> 
			ret.addItemsItem( ImplAllegatoToItem(
					documentoToImplAllegato(d))
				)
		);
		return ret;
	}



	public static final ApiImplVersioneApiView aspsToApiImplVersioneApiView(final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		ApiImplVersioneApiView ret = new ApiImplVersioneApiView();
		
		AccordoServizioParteComuneSintetico api = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		ret.setApiNome(aspc.getNome());
		ret.setApiSoapServizio(asps.getPortType());
		ret.setApiVersione(aspc.getVersione());
		ret.setProfilo(env.profilo);
		ret.setSoggetto(aspc.getSoggettoReferente().getNome());
		ret.setTipoServizio(asps.getTipo());
		
		  // Lista di Accordi Compatibili
        List<AccordoServizioParteComune> asParteComuneCompatibili = env.apsCore.findAccordiParteComuneBySoggettoAndNome(
        		api.getNome(),
                new IDSoggetto(api.getSoggettoReferente().getTipo(), api.getSoggettoReferente().getNome())
        	);
        
      	ret.setVersioni(asParteComuneCompatibili.stream().map( a -> a.getVersione() ).collect(Collectors.toList())); 
		return ret;
	}



	public static final ApiImplInformazioniGeneraliView erogazioneToApiImplInformazioniGeneraliView(final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps) {
		ApiImplViewItem item = aspsToErogazioneViewItem(env, asps);

		ApiImplInformazioniGeneraliView ret = new ApiImplInformazioniGeneraliView();
		
		ret.setApiSoapServizio(item.getApiSoapServizio());
		ret.setNome(item.getNome());
		ret.setProfilo(item.getProfilo());
		ret.setTipo(item.getTipoServizio());
		return ret;
	}
	
	public static final ApiImplInformazioniGeneraliView fruizioneToApiImplInformazioniGeneraliView(final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps, IdSoggetto fruitore) {
		
		ApiImplViewItem item = aspsToFruizioneViewItem(env, asps, fruitore);

		ApiImplInformazioniGeneraliView ret = new ApiImplInformazioniGeneraliView();
		
		ret.setApiSoapServizio(item.getApiSoapServizio());
		ret.setNome(item.getNome());
		ret.setProfilo(item.getProfilo());
		ret.setTipo(item.getTipoServizio());
		return ret;
	}


	public static final CorsConfigurazione buildCorsConfigurazione(GestioneCors body, final ErogazioniEnv env,
			final CorsConfigurazione oldConf) throws Exception {
		
		
		final String statoCorsPorta = body.isRidefinito()
				? 		CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO
				:       CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		
		final GestioneCorsAccessControl c = body.getAccessControl();
		
		final String corsStato = Helper.boolToStatoFunzionalita( body.getTipo() != TipoGestioneCorsEnum.DISABILITATO ).toString();
		final TipoGestioneCORS corsTipo =  Enums.tipoGestioneCorsFromRest.get( body.getTipo() );
		final String allowOrigins = String.join(",", BaseHelper.evalorElse( () -> c.getAllowOrigins(), new ArrayList<>()) );
		final String allowHeaders = String.join(",", BaseHelper.evalorElse( () -> c.getAllowHeaders(), new ArrayList<>()) );
		final String exposeHeaders = String.join(",", BaseHelper.evalorElse( () -> c.getExposeHeaders(), new ArrayList<>()) );
		final String allowMethods = String.join(
				",",
				BaseHelper.evalorElse( () -> c.getAllowMethods(), new ArrayList<HttpMethodEnum>() )
					.stream().map( m -> m.name()).collect(Collectors.toList()) 
			);
	
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO, corsStato );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO, corsTipo.toString() );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS, ServletUtils.boolToCheckBoxStatus(c.isAllAllowOrigins()) );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS, allowOrigins );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS, ServletUtils.boolToCheckBoxStatus(c.isAllAllowHeaders()) );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS, allowHeaders );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS, ServletUtils.boolToCheckBoxStatus(c.isAllAllowMethods()) );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS, allowMethods );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS, exposeHeaders );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE, ServletUtils.boolToCheckBoxStatus(c.isMaxAge()) );
		if(c.isMaxAge()!=null && c.isMaxAge() && c.getMaxAgeSeconds()!=null) {
			env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS, c.getMaxAgeSeconds() );
		}
	
		if ( !env.paHelper.checkDataConfigurazioneCorsPorta(TipoOperazione.OTHER, true, statoCorsPorta) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
	
		return env.paHelper.getGestioneCors(
				body.getTipo() != TipoGestioneCorsEnum.DISABILITATO,
				corsTipo,  
				c.isAllAllowOrigins(), 
				c.isAllAllowHeaders(), 
				c.isAllAllowMethods(), 
				allowHeaders, 
				allowOrigins, 
				allowMethods, 
				c.isAllowCredentials(),
				exposeHeaders,
				c.isMaxAge()!=null ? c.isMaxAge() : false,
				c.getMaxAgeSeconds()!=null ? c.getMaxAgeSeconds() : -1
			);
	}
	
	
	public static final GestioneToken buildGestioneToken(ControlloAccessiGestioneToken body, Object porta, boolean isPortaDelegata, ConsoleHelper coHelper, ErogazioniEnv env) throws Exception {
				
		GestioneToken ret = new GestioneToken();
		
		ret.setPolicy( body.getPolicy() );
		ret.setTokenOpzionale( Helper.boolToStatoFunzionalitaConf(body.isTokenOpzionale()) ); 
		ret.setValidazione( StatoFunzionalitaConWarning.toEnumConstant(  evalnull(  () -> body.getValidazioneJwt().toString() )) );
		ret.setIntrospection( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getIntrospection().toString() )) );
		ret.setUserInfo( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getUserInfo().toString() )) );
		ret.setForward( Helper.boolToStatoFunzionalitaConf(body.isTokenForward()) ); 	
		
		return ret;
		
	}
	
	public static final void fillGestioneToken(GestioneToken ret, ControlloAccessiGestioneToken body) {
		ret.setPolicy( body.getPolicy() );
		ret.setTokenOpzionale( Helper.boolToStatoFunzionalitaConf(body.isTokenOpzionale()) ); 
		ret.setValidazione( StatoFunzionalitaConWarning.toEnumConstant(  evalnull(  () -> body.getValidazioneJwt().toString() )) );
		ret.setIntrospection( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getIntrospection().toString() )) );
		ret.setUserInfo( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getUserInfo().toString() )) );
		ret.setForward( Helper.boolToStatoFunzionalitaConf(body.isTokenForward()) ); 			
	}
	
	public static final RegistrazioneMessaggiConfigurazioneRegola fromDumpConfigurazioneRegola(DumpConfigurazioneRegola r) {
		if (r == null) return null;
		RegistrazioneMessaggiConfigurazioneRegola ret = new RegistrazioneMessaggiConfigurazioneRegola();
		
		ret.setPayload(Helper.statoFunzionalitaConfToBool( r.getPayload()) );
		ret.setHeaders(Helper.statoFunzionalitaConfToBool( r.getHeaders()) );
		if(ret.isPayload()!=null && ret.isPayload()) {
			ret.setPayloadParsing(Helper.statoFunzionalitaConfToBool( r.getPayloadParsing()) );
			if(ret.isPayloadParsing()!=null && ret.isPayloadParsing()) {
				ret.setBody(Helper.statoFunzionalitaConfToBool( r.getBody()) );
				ret.setAttachments(Helper.statoFunzionalitaConfToBool( r.getAttachments()) );
			}
		}
		
		return ret;
	}



	public static final ControlloAccessiAutenticazioneToken fromGestioneTokenAutenticazione(
			final GestioneTokenAutenticazione tokAut) {
		ControlloAccessiAutenticazioneToken token = new ControlloAccessiAutenticazioneToken();
		token.setClientId(Helper.statoFunzionalitaConfToBool( tokAut.getClientId() ));
		token.setEmail(Helper.statoFunzionalitaConfToBool( tokAut.getEmail() ));
		token.setIssuer(Helper.statoFunzionalitaConfToBool( tokAut.getIssuer() ));
		token.setSubject(Helper.statoFunzionalitaConfToBool( tokAut.getSubject() ));
		token.setUsername(Helper.statoFunzionalitaConfToBool(tokAut.getUsername() ));
		return token;
	}

	
	
	public static final RegistrazioneDiagnosticiConfigurazione fromDiagnosticiConfigurazione(final PortaTracciamento paTracciamento) {
		final RegistrazioneDiagnosticiConfigurazione ret = new RegistrazioneDiagnosticiConfigurazione();
		
		if (paTracciamento != null && paTracciamento.getSeverita()!=null) {
			ret.setStato(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			switch (paTracciamento.getSeverita()) {
			case ALL:
				ret.setSeverita(DiagnosticiSeverita.ALL);
				break;
			case DEBUG_HIGH:
				ret.setSeverita(DiagnosticiSeverita.DEBUG_HIGH);
				break;
			case DEBUG_MEDIUM:
				ret.setSeverita(DiagnosticiSeverita.DEBUG_MEDIUM);
				break;
			case DEBUG_LOW:
				ret.setSeverita(DiagnosticiSeverita.DEBUG_LOW);
				break;
			case INFO_PROTOCOL:
				ret.setSeverita(DiagnosticiSeverita.INFO_PROTOCOL);
				break;
			case INFO_INTEGRATION:
				ret.setSeverita(DiagnosticiSeverita.INFO_INTEGRATION);
				break;
			case ERROR_PROTOCOL:
				ret.setSeverita(DiagnosticiSeverita.ERROR_PROTOCOL);
				break;
			case ERROR_INTEGRATION:
				ret.setSeverita(DiagnosticiSeverita.ERROR_INTEGRATION);
				break;
			case FATAL:
				ret.setSeverita(DiagnosticiSeverita.FATAL);
				break;
			case OFF:
				ret.setSeverita(DiagnosticiSeverita.OFF);
				break;
			}
			
		}
		else {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
		}
		return ret;
	}
	
	
	public static final RegistrazioneTransazioniConfigurazione fromTransazioniConfigurazione(final PortaTracciamento paTracciamento, Environment env) throws ProtocolException {
		final RegistrazioneTransazioniConfigurazione ret = new RegistrazioneTransazioniConfigurazione();
		
		if (paTracciamento != null && 
				(
						paTracciamento.getDatabase()!=null || 
						paTracciamento.getFiletrace()!=null ||
						(
								paTracciamento.getEsiti()!=null && StringUtils.isNoneEmpty(paTracciamento.getEsiti()) &&
								(
										paTracciamento.getDatabase()!=null && StatoFunzionalita.ABILITATO.equals(paTracciamento.getDatabase().getFiltroEsiti())
										||
										paTracciamento.getFiletrace()!=null && StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletrace().getFiltroEsiti())
								)
						) 
						||
						paTracciamento.getFiletraceConfig()!=null ||
						paTracciamento.getTransazioni()!=null
						)
			) {
			ret.setStato(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			if(paTracciamento.getDatabase()!=null) {
				ret.setDatabase(fromTransazioniConfigurazioneBase(paTracciamento.getDatabase(), false));
			}
			if(paTracciamento.getFiletrace()!=null) {
				RegistrazioneTransazioniConfigurazioneFiletrace f = (RegistrazioneTransazioniConfigurazioneFiletrace) fromTransazioniConfigurazioneBase(paTracciamento.getFiletrace(), true);
				if(paTracciamento.getFiletraceConfig()!=null) {
					f.setConfigPath(paTracciamento.getFiletraceConfig().getConfig());
					if(paTracciamento.getFiletraceConfig().getDumpIn()!=null && StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletraceConfig().getDumpIn().getStato())) {
						f.setDumpClient(new RegistrazioneTransazioniConfigurazioneFiletraceMessaggio());
						f.getDumpClient().setHeaders(StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletraceConfig().getDumpIn().getHeader()));
						f.getDumpClient().setPayload(StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletraceConfig().getDumpIn().getPayload()));
					}
					if(paTracciamento.getFiletraceConfig().getDumpOut()!=null && StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletraceConfig().getDumpOut().getStato())) {
						f.setDumpServer(new RegistrazioneTransazioniConfigurazioneFiletraceMessaggio());
						f.getDumpServer().setHeaders(StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletraceConfig().getDumpOut().getHeader()));
						f.getDumpServer().setPayload(StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletraceConfig().getDumpOut().getPayload()));
					}
				}
				ret.setFiletrace(f);
			}
			if((paTracciamento.getEsiti()!=null && StringUtils.isNoneEmpty(paTracciamento.getEsiti())) &&
					(
							paTracciamento.getDatabase()!=null && StatoFunzionalita.ABILITATO.equals(paTracciamento.getDatabase().getFiltroEsiti())
							||
							paTracciamento.getFiletrace()!=null && StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiletrace().getFiltroEsiti())
					)
					) {
				Logger log = LoggerWrapperFactory.getLogger(ErogazioniApiHelper.class); 
				List<String> esiti  = EsitiConfigUtils.getRegistrazioneEsiti(paTracciamento.getEsiti(), log, new StringBuilder(), EsitiProperties.getInstance(log, env.protocolFactory));
				if(esiti!=null && !esiti.isEmpty()) {
					ret.setFiltro(new RegistrazioneTransazioniConfigurazioneFiltroEsiti());
					for (String e : esiti) {
						ret.getFiltro().addEsitiItem(Integer.valueOf(e));		
					}
				}
			}
			if(paTracciamento.getTransazioni()!=null) {
				ret.setInformazioni(new RegistrazioneTransazioniConfigurazioneInformazioniRegistrate());
				if(paTracciamento.getTransazioni().getTempiElaborazione()!=null) {
					if(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(paTracciamento.getTransazioni().getTempiElaborazione())) {
						ret.getInformazioni().setTempiElaborazione(TracciamentoTransazioniStatoFase.ABILITATO);						
					}
					else {
						ret.getInformazioni().setTempiElaborazione(TracciamentoTransazioniStatoFase.DISABILITATO);						
					}
				}
				else {
					ret.getInformazioni().setTempiElaborazione(TracciamentoTransazioniStatoFase.DISABILITATO);				
				}
				if(paTracciamento.getTransazioni().getToken()!=null) {
					if(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(paTracciamento.getTransazioni().getToken())) {
						ret.getInformazioni().setToken(TracciamentoTransazioniStatoFase.ABILITATO);						
					} else {
						ret.getInformazioni().setToken(TracciamentoTransazioniStatoFase.DISABILITATO);						
					}
				}
				else {
					ret.getInformazioni().setToken(TracciamentoTransazioniStatoFase.DISABILITATO);	
				}
			}
			
			// check finale
			if(ret.getDatabase()==null && ret.getFiletrace()==null && ret.getFiltro()==null && ret.getInformazioni()==null) {
				ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
			}
		}
		else {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
		}
		return ret;
	}
	private static final RegistrazioneTransazioniConfigurazioneBase fromTransazioniConfigurazioneBase(final TracciamentoConfigurazione paTracciamento, boolean fileTrace) {
		RegistrazioneTransazioniConfigurazioneBase c = null;
		
		if(paTracciamento.getStato()!=null) {
			
			c =  fileTrace ? new RegistrazioneTransazioniConfigurazioneFiletrace() : new RegistrazioneTransazioniConfigurazioneBase();
			
			switch (paTracciamento.getStato()) {
			case ABILITATO:
				c.setStato(TracciamentoTransazioniStato.ABILITATO);
				break;
			case DISABILITATO:
				c.setStato(TracciamentoTransazioniStato.DISABILITATO);
				break;
			case CONFIGURAZIONE_ESTERNA:
				c.setStato(TracciamentoTransazioniStato.CONFIGURAZIONEESTERNA);
				break;
			case PERSONALIZZATO:
				c.setStato(TracciamentoTransazioniStato.PERSONALIZZATO);
				
				c.setFasi(new RegistrazioneTransazioniConfigurazioneFasi());
				
				if(paTracciamento.getRequestIn()!=null) {
					switch (paTracciamento.getRequestIn()) {
						case ABILITATO:
							c.getFasi().setRichiestaIngresso(TracciamentoTransazioniStatoFaseBloccante.BLOCCANTE);
							break;
						case NON_BLOCCANTE:
							c.getFasi().setRichiestaIngresso(TracciamentoTransazioniStatoFaseBloccante.NON_BLOCCANTE);
							break;
						case DISABILITATO:
							c.getFasi().setRichiestaIngresso(TracciamentoTransazioniStatoFaseBloccante.DISABILITATO);
							break;
					}
				}
				else {
					c.getFasi().setRichiestaIngresso(TracciamentoTransazioniStatoFaseBloccante.DISABILITATO);
				}
				
				if(paTracciamento.getRequestOut()!=null) {
					switch (paTracciamento.getRequestOut()) {
						case ABILITATO:
							c.getFasi().setRichiestaUscita(TracciamentoTransazioniStatoFaseBloccante.BLOCCANTE);
							break;
						case NON_BLOCCANTE:
							c.getFasi().setRichiestaUscita(TracciamentoTransazioniStatoFaseBloccante.NON_BLOCCANTE);
							break;
						case DISABILITATO:
							c.getFasi().setRichiestaUscita(TracciamentoTransazioniStatoFaseBloccante.DISABILITATO);
							break;
					}
				}
				else {
					c.getFasi().setRichiestaUscita(TracciamentoTransazioniStatoFaseBloccante.DISABILITATO);
				}
				
				if(paTracciamento.getResponseOut()!=null) {
					switch (paTracciamento.getResponseOut()) {
						case ABILITATO:
							c.getFasi().setRispostaUscita(TracciamentoTransazioniStatoFaseBloccante.BLOCCANTE);
							break;
						case NON_BLOCCANTE:
							c.getFasi().setRispostaUscita(TracciamentoTransazioniStatoFaseBloccante.NON_BLOCCANTE);
							break;
						case DISABILITATO:
							c.getFasi().setRispostaUscita(TracciamentoTransazioniStatoFaseBloccante.DISABILITATO);
							break;
					}
				}
				else {
					c.getFasi().setRispostaUscita(TracciamentoTransazioniStatoFaseBloccante.DISABILITATO);
				}
				
				if(paTracciamento.getResponseOutComplete()!=null) {
					if(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(paTracciamento.getResponseOutComplete())) {
						c.getFasi().setRispostaConsegnata(TracciamentoTransazioniStatoFase.ABILITATO);
					}else {
						c.getFasi().setRispostaConsegnata(TracciamentoTransazioniStatoFase.DISABILITATO);
					}
				}
				else {
					c.getFasi().setRispostaConsegnata(TracciamentoTransazioniStatoFase.DISABILITATO);
				}
				
				break;
			}
						
			if(paTracciamento.getFiltroEsiti()!=null) {
				if(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(paTracciamento.getFiltroEsiti())) {
					c.setFiltroEsiti(TracciamentoTransazioniStatoFase.ABILITATO);
				}
				else{
					c.setFiltroEsiti(TracciamentoTransazioniStatoFase.DISABILITATO);
				}
			}
			
		}
		
		return c;
	}


	public static final RegistrazioneMessaggi fromDumpConfigurazione(final DumpConfigurazione paDump) {
		final RegistrazioneMessaggi ret = new RegistrazioneMessaggi();
		
		if (paDump != null) {
			ret.setStato(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			final RegistrazioneMessaggiConfigurazione richiesta = new RegistrazioneMessaggiConfigurazione();
			
			richiesta.setAbilitato( isDumpConfigurazioneAbilitato(paDump, false) );
			if ( richiesta.isAbilitato() ) {
				richiesta.setIngresso( fromDumpConfigurazioneRegola(paDump.getRichiestaIngresso()));
				richiesta.setUscita( fromDumpConfigurazioneRegola(paDump.getRichiestaUscita()));
			}
			
			ret.setRichiesta(richiesta);
							
			RegistrazioneMessaggiConfigurazione risposta = new RegistrazioneMessaggiConfigurazione();
			risposta.setAbilitato( isDumpConfigurazioneAbilitato(paDump,true) );
			if (risposta.isAbilitato()) {
				risposta.setIngresso( fromDumpConfigurazioneRegola( paDump.getRispostaIngresso()) );
				risposta.setUscita( fromDumpConfigurazioneRegola( paDump.getRispostaUscita()) );
			}
			
			ret.setRisposta(risposta);				
		}
		else {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
		}
		return ret;
	}



	public static final Validazione fromValidazioneContenutiApplicativi(final ValidazioneContenutiApplicativi vx) {
		final Validazione ret = new Validazione();
		
		if (vx == null) {
			ret.setStato(StatoFunzionalitaConWarningEnum.DISABILITATO);
		} else {

			ret.setMtom( Helper.statoFunzionalitaConfToBool(vx.getAcceptMtomMessage()) );
			ret.setStato( StatoFunzionalitaConWarningEnum.valueOf(vx.getStato().name()));
			ret.setTipo(  TipoValidazioneEnum.valueOf(vx.getTipo().name()) );
		}
		return ret;
	}


	public static final String readHeadersResponseCaching(List<String> headers) {
		if(headers==null || headers.isEmpty()) {
			return null;
		}
		StringBuilder  bf = new StringBuilder();
		for (String hdr : headers) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(hdr);
		}
		return bf.toString();
	}
	
	public static final List<ResponseCachingConfigurazioneRegola> getRegoleResponseCaching(List<CachingRispostaRegola> regole){
		if(regole==null || regole.isEmpty()) {
			return null;
		}
		List<ResponseCachingConfigurazioneRegola> returnList = new ArrayList<>();
		for (CachingRispostaRegola cachingRispostaRegola : regole) {
			ResponseCachingConfigurazioneRegola rule = new ResponseCachingConfigurazioneRegola();
			if(cachingRispostaRegola.getReturnCodeMin()!=null)
				rule.setReturnCodeMin(cachingRispostaRegola.getReturnCodeMin());
			if(cachingRispostaRegola.getReturnCodeMax()!=null)
				rule.setReturnCodeMax(cachingRispostaRegola.getReturnCodeMax());
			rule.setFault(cachingRispostaRegola.isFault());
			if(cachingRispostaRegola.getCacheTimeoutSeconds()!=null)
				rule.setCacheTimeoutSeconds(cachingRispostaRegola.getCacheTimeoutSeconds());
			returnList.add(rule);
		}
		return returnList;
	}

	public static final ResponseCachingConfigurazione buildResponseCachingConfigurazione(CachingRisposta body, PorteApplicativeHelper paHelper) {
		ResponseCachingConfigurazione newConfigurazione = null;
		if (body.getStato() == StatoDefaultRidefinitoEnum.DEFAULT) {
			
		}
		
		else if ( body.getStato() == StatoDefaultRidefinitoEnum.RIDEFINITO ) {
			newConfigurazione = paHelper.getResponseCaching(
					body.isAbilitato(),  // responseCachingEnabled
					body.getCacheTimeoutSeconds(), // responseCachingSeconds
					body.isMaxResponseSize(), // responseCachingMaxResponseSize
					body.getMaxResponseSizeKb(), // responseCachingMaxResponseSizeBytes
					body.isHashRequestUri(), // responseCachingDigestUrlInvocazione
					(readHeadersResponseCaching(body.getHashHeaders())!=null), // responseCachingDigestHeaders
					body.isHashPayload(), // responseCachingDigestPayload
					readHeadersResponseCaching(body.getHashHeaders()), // responseCachingDigestHeadersNomiHeaders
					convertToStatoFunzionalitaCacheDigestQueryParameter(body),//responseCachingDigestQueryParameter
					readHeadersResponseCaching(body.getHashQueryParamaters()), // responseCachingDigestNomiParametriQuery
					body.isControlNoCache(), // responseCachingCacheControlNoCache
					body.isControlMaxAge(), // responseCachingCacheControlMaxAge
					body.isControlNoStore(), // responseCachingCacheControlNoStore
					getRegoleResponseCaching(body.getRegole())// listaRegoleCachingConfigurazione
				);
		}
		return newConfigurazione;
	}
	
	private static final StatoFunzionalitaCacheDigestQueryParameter convertToStatoFunzionalitaCacheDigestQueryParameter(CachingRisposta body) {
		if(body!=null && body.isHashAllQueryParameters()!=null) {
			if(body.isHashAllQueryParameters()) {
				return StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
			}
			else {
				if(body.getHashQueryParamaters()!=null && !body.getHashQueryParamaters().isEmpty()) {
					return StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE;
				}
				else {
					return StatoFunzionalitaCacheDigestQueryParameter.DISABILITATO;
				}
			}
		}
		else {
			// default
			return StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
		}
	}
	
	public static final CachingRisposta buildCachingRisposta(ResponseCachingConfigurazione conf) {
		CachingRisposta ret = new CachingRisposta();
		
		if (conf == null) {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
			ret.setAbilitato(false);
			ret.setHashHeaders(null);
			ret.setHashPayload(null);
			ret.setHashRequestUri(null);
			ret.setHashAllQueryParameters(null);
			return ret;
		}
		
		ret.setAbilitato(conf.getStato() == StatoFunzionalita.ABILITATO);
		ret.setStato( conf.getStato() == StatoFunzionalita.ABILITATO
				? StatoDefaultRidefinitoEnum.RIDEFINITO
				: StatoDefaultRidefinitoEnum.DEFAULT
			);
		ret.setCacheTimeoutSeconds(conf.getCacheTimeoutSeconds());
		
		if (ret.isAbilitato()) {
			
			ret.setMaxResponseSize( conf.getMaxMessageSize() != null);
			ret.setMaxResponseSizeKb( conf.getMaxMessageSize() );
			
			ResponseCachingConfigurazioneHashGenerator hashInfo = conf.getHashGenerator();
			if(hashInfo.getQueryParameters()!=null) {
				switch (hashInfo.getQueryParameters()) {
				case ABILITATO:
					ret.setHashAllQueryParameters(true);
					break;
				case DISABILITATO:
					ret.setHashAllQueryParameters(false);
					break;
				case SELEZIONE_PUNTUALE:
					ret.setHashAllQueryParameters(false);
					if(hashInfo.sizeQueryParameterList()>0) {
						ret.setHashQueryParamaters(hashInfo.getQueryParameterList());
					}
					break;
				}
			}
			if(StatoFunzionalita.ABILITATO.equals(hashInfo.getHeaders())) {
				if(hashInfo.sizeHeaderList()>0) {
					ret.setHashHeaders(hashInfo.getHeaderList());
				}
			}
			ret.setHashPayload(Helper.statoFunzionalitaConfToBool(hashInfo.getPayload()));
			ret.setHashRequestUri(Helper.statoFunzionalitaConfToBool(hashInfo.getRequestUri()));
			
			if(conf.getControl()!=null) {
				ret.setControlNoCache(conf.getControl().isNoCache());
				ret.setControlMaxAge(conf.getControl().isMaxAge());
				ret.setControlNoStore(conf.getControl().isNoStore());
			}
			
			if(conf.sizeRegolaList()>0) {
				ret.setRegole(new ArrayList<CachingRispostaRegola>());
				
				for (ResponseCachingConfigurazioneRegola regola : conf.getRegolaList()) {
					CachingRispostaRegola cachingRispostaRegola = new CachingRispostaRegola();
					if(regola.getReturnCodeMin()!=null) {
						cachingRispostaRegola.setReturnCodeMin(regola.getReturnCodeMin());
					}
					if(regola.getReturnCodeMax()!=null) {
						cachingRispostaRegola.setReturnCodeMax(regola.getReturnCodeMax());
					}
					cachingRispostaRegola.setFault(regola.isFault());
					if(regola.getCacheTimeoutSeconds()!=null) {
						cachingRispostaRegola.setCacheTimeoutSeconds(regola.getCacheTimeoutSeconds());
					}
					ret.addRegoleItem(cachingRispostaRegola);
				}
			}
		}
		
		return ret;
	}


	
	@Deprecated	
	public static final RuoloTipologia ruoloTipologiaFromAutorizzazione(final String autorizzazione) {
		RuoloTipologia tipoRuoloFonte = null;
		if (TipoAutorizzazione.isRolesRequired(autorizzazione)) {
			if ( TipoAutorizzazione.isExternalRolesRequired(autorizzazione) )
				tipoRuoloFonte = RuoloTipologia.ESTERNO;
			else if ( TipoAutorizzazione.isInternalRolesRequired(autorizzazione))
				tipoRuoloFonte = RuoloTipologia.INTERNO;
			else
				tipoRuoloFonte = RuoloTipologia.QUALSIASI;				
		}
		return tipoRuoloFonte;
	}
	
	
	public static final boolean controlloAccessiCheckPD(FruizioniConfEnv env, PortaDelegata oldPd, PortaDelegata newPd) throws Exception {
		final BinaryParameter allegatoXacmlPolicy = new BinaryParameter();
		
		allegatoXacmlPolicy.setValue( BaseHelper.evalorElse(
				() -> newPd.getXacmlPolicy(),
				""
				).getBytes()
			);
		RuoloTipologia tipoRuoloFonte = AutorizzazioneUtilities.convertToRuoloTipologia(TipoAutorizzazione.toEnumConstant(newPd.getAutorizzazione()));
		String stato_autorizzazione = null;
		if (TipoAutorizzazione.toEnumConstant(newPd.getAutorizzazione()) != null) {
			stato_autorizzazione = AutorizzazioneUtilities.convertToStato(newPd.getAutorizzazione());
		}
		
		boolean tokenAbilitato = newPd.getGestioneToken() != null;
		boolean autorizzazioneScope = newPd.getScope() != null;
		
		if ( autorizzazioneScope && !tokenAbilitato ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per scope richiede una token policy abilitata");
		}
		
		TipoAutenticazionePrincipal	autenticazionePrincipal = env.pdCore.getTipoAutenticazionePrincipal(newPd.getProprietaAutenticazioneList());
		List<String> autenticazioneParametroList = env.pdCore.getParametroAutenticazione(newPd.getAutenticazione(), newPd.getProprietaAutenticazioneList());
        	
		String autorizzazioneContenutiStato = null;
		String autorizzazioneContenuti = newPd.getAutorizzazioneContenuto();
		String autorizzazioneContenutiProperties = null;
		if(autorizzazioneContenuti == null) {
			autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
		} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
			autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
			List<Proprieta> proprietaAutorizzazioneContenutoList = newPd.getProprietaAutorizzazioneContenutoList();
			StringBuilder sb = new StringBuilder();
			for (Proprieta proprieta : proprietaAutorizzazioneContenutoList) {
				if(sb.length() >0)
					sb.append("\n");
				
				sb.append(proprieta.getNome()).append("=").append(proprieta.getValore()); 
			}						
			
			autorizzazioneContenutiProperties = sb.toString();
		} else { // custom
			autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
		}
		
		String identificazioneAttributiStato = newPd.sizeAttributeAuthorityList()>0 ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
		String [] attributeAuthoritySelezionate = null; 
		String attributeAuthorityAttributi = null;
		if(newPd.sizeAttributeAuthorityList()>0) {
			List<String> l = new ArrayList<>();
			for (AttributeAuthority aa : newPd.getAttributeAuthorityList()) {
				l.add(aa.getNome());
			}
			attributeAuthoritySelezionate = l.toArray(new String[1]);
			attributeAuthorityAttributi = env.apsCore.buildAttributesStringFromAuthority(newPd.getAttributeAuthorityList());
		}
		
		String autorizzazioneAutenticatiToken = null;
		String autorizzazioneRuoliToken = null;
		if(newPd.getAutorizzazioneToken()!=null) {
			autorizzazioneAutenticatiToken = ServletUtils.boolToCheckBoxStatus(StatoFunzionalita.ABILITATO.equals(newPd.getAutorizzazioneToken().getAutorizzazioneApplicativi()));
			autorizzazioneRuoliToken = ServletUtils.boolToCheckBoxStatus(StatoFunzionalita.ABILITATO.equals(newPd.getAutorizzazioneToken().getAutorizzazioneRuoli()));
		}
		
		return env.paHelper.controlloAccessiCheck(
				TipoOperazione.OTHER, 
				newPd.getAutenticazione(),				// Autenticazione
				ServletUtils.boolToCheckBoxStatus( Helper.statoFunzionalitaConfToBool( newPd.getAutenticazioneOpzionale() ) ),		// Autenticazione Opzionale
				autenticazionePrincipal,
				autenticazioneParametroList,
				stato_autorizzazione,	 			
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isAuthenticationRequired(newPd.getAutorizzazione()) ), 
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isRolesRequired(newPd.getAutorizzazione()) ), 
				evalnull( () -> tipoRuoloFonte.toString() ), 
				evalnull( () -> newPd.getRuoli().getMatch().toString() ), 
				env.isSupportatoAutenticazioneSoggetti, 
				true,		// isPortaDelegata, 
				oldPd,
				evalnull( () ->
						newPd.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList())
					),
				Helper.boolToStatoFunzionalita(tokenAbilitato).getValue(),
				evalnull( () -> newPd.getGestioneToken().getPolicy() ), 
				evalnull( () -> newPd.getGestioneToken().getValidazione().toString() ), 
				evalnull( () -> newPd.getGestioneToken().getIntrospection().toString() ), 
				evalnull( () -> newPd.getGestioneToken().getUserInfo().toString() ),
				evalnull( () -> newPd.getGestioneToken().getForward().toString() ),
				autorizzazioneAutenticatiToken, 
				autorizzazioneRuoliToken, 
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( newPd.getGestioneToken().getOptions() != null ) ),
				evalnull( () -> newPd.getGestioneToken().getOptions() ),
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( autorizzazioneScope ) ),
				evalnull( () -> newPd.getScope().getMatch().toString() ),
				allegatoXacmlPolicy,
				autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
				env.tipo_protocollo,
				identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi);
	}



	public static final boolean controlloAccessiCheckPA(ErogazioniConfEnv env, PortaApplicativa oldPa, PortaApplicativa newPa)
			throws Exception {
		
		final BinaryParameter allegatoXacmlPolicy = new BinaryParameter();
		
		allegatoXacmlPolicy.setValue( BaseHelper.evalorElse(
				() -> newPa.getXacmlPolicy(),
				""
				).getBytes()
			);
		RuoloTipologia tipoRuoloFonte = AutorizzazioneUtilities.convertToRuoloTipologia(TipoAutorizzazione.toEnumConstant(newPa.getAutorizzazione()));
		String stato_autorizzazione = null;
		if (TipoAutorizzazione.toEnumConstant(newPa.getAutorizzazione()) != null) {
			stato_autorizzazione = AutorizzazioneUtilities.convertToStato(newPa.getAutorizzazione());
		}
		
		boolean tokenAbilitato = newPa.getGestioneToken() != null;
		boolean autorizzazioneScope = newPa.getScope() != null;
		
		if ( autorizzazioneScope && !tokenAbilitato ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per scope richiede una token policy abilitata");
		}
		
		TipoAutenticazionePrincipal	autenticazionePrincipal = env.paCore.getTipoAutenticazionePrincipal(newPa.getProprietaAutenticazioneList());
		List<String> autenticazioneParametroList = env.paCore.getParametroAutenticazione(newPa.getAutenticazione(), newPa.getProprietaAutenticazioneList());
		
		String autorizzazioneContenutiStato = null;
		String autorizzazioneContenuti = newPa.getAutorizzazioneContenuto();
		String autorizzazioneContenutiProperties = null;
		if(autorizzazioneContenuti == null) {
			autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
		} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
			autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
			List<Proprieta> proprietaAutorizzazioneContenutoList = newPa.getProprietaAutorizzazioneContenutoList();
			StringBuilder sb = new StringBuilder();
			for (Proprieta proprieta : proprietaAutorizzazioneContenutoList) {
				if(sb.length() >0)
					sb.append("\n");
				
				sb.append(proprieta.getNome()).append("=").append(proprieta.getValore()); 
			}						
			
			autorizzazioneContenutiProperties = sb.toString();
		} else { // custom
			autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
		}
		
		String identificazioneAttributiStato = newPa.sizeAttributeAuthorityList()>0 ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
		String [] attributeAuthoritySelezionate = null; 
		String attributeAuthorityAttributi = null;
		if(newPa.sizeAttributeAuthorityList()>0) {
			List<String> l = new ArrayList<>();
			for (AttributeAuthority aa : newPa.getAttributeAuthorityList()) {
				l.add(aa.getNome());
			}
			attributeAuthoritySelezionate = l.toArray(new String[1]);
			attributeAuthorityAttributi = env.apsCore.buildAttributesStringFromAuthority(newPa.getAttributeAuthorityList());
		}
		
		String autorizzazioneAutenticatiToken = null;
		String autorizzazioneRuoliToken = null;
		if(newPa.getAutorizzazioneToken()!=null) {
			autorizzazioneAutenticatiToken = ServletUtils.boolToCheckBoxStatus(StatoFunzionalita.ABILITATO.equals(newPa.getAutorizzazioneToken().getAutorizzazioneApplicativi()));
			autorizzazioneRuoliToken = ServletUtils.boolToCheckBoxStatus(StatoFunzionalita.ABILITATO.equals(newPa.getAutorizzazioneToken().getAutorizzazioneRuoli()));
		}
		
		return env.paHelper.controlloAccessiCheck(
				TipoOperazione.OTHER, 
				newPa.getAutenticazione(),				// Autenticazione
				ServletUtils.boolToCheckBoxStatus( Helper.statoFunzionalitaConfToBool( newPa.getAutenticazioneOpzionale() ) ),		// Autenticazione Opzionale
				autenticazionePrincipal,
				autenticazioneParametroList,
				stato_autorizzazione,	 			
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isAuthenticationRequired(newPa.getAutorizzazione()) ), 
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isRolesRequired(newPa.getAutorizzazione()) ), 
				evalnull( () -> tipoRuoloFonte.toString() ), 
				evalnull( () -> newPa.getRuoli().getMatch().toString() ), 
				env.isSupportatoAutenticazioneSoggetti, 
				false,		// isPortaDelegata, 
				oldPa,
				evalnull( () ->
						newPa.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList())
					),
				Helper.boolToStatoFunzionalita(tokenAbilitato).getValue(),	// gestioneToken
				evalnull( () -> newPa.getGestioneToken().getPolicy() ), 	// policy
				evalnull( () -> newPa.getGestioneToken().getValidazione().toString() ),	// validazioneInput 
				evalnull( () -> newPa.getGestioneToken().getIntrospection().toString() ), // introspection
				evalnull( () -> newPa.getGestioneToken().getUserInfo().toString() ),		// userInfo
				evalnull( () -> newPa.getGestioneToken().getForward().toString() ),		// forward
				autorizzazioneAutenticatiToken, 
				autorizzazioneRuoliToken, 
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( newPa.getGestioneToken().getOptions() != null ) ),	// autorizzazioneToken
				evalnull( () -> newPa.getGestioneToken().getOptions() ),
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( autorizzazioneScope ) ),
				evalnull( () -> newPa.getScope().getMatch().toString() ),
				allegatoXacmlPolicy,
				autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
				env.tipo_protocollo,
				identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi);
		
	}
	
	public static final void fillPortaDelegata(ControlloAccessiAutorizzazione body, final PortaDelegata newPd) {
		final OneOfControlloAccessiAutorizzazioneAutorizzazione authz = body.getAutorizzazione();
		
		newPd.setXacmlPolicy(null);
		
		switch (authz.getTipo()) {
		case DISABILITATO: {
			
			final String tipoAutorString = TipoAutorizzazione.DISABILITATO.toString();
			newPd.setRuoli(null);
			newPd.setScope(null);
			newPd.setAutorizzazione(tipoAutorString);
			break;
		}
		case ABILITATO: {
			
			if(! (authz instanceof APIImplAutorizzazioneAbilitata)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneAbilitata authzAbilitata = (APIImplAutorizzazioneAbilitata) authz;

			
			// defaults
			if ( authzAbilitata.isRuoli() && authzAbilitata.getRuoliFonte() == null ) {
				authzAbilitata.setRuoliFonte(FonteEnum.QUALSIASI);
			}
			
			final String autorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
			
			final String autorizzazioneAutenticati = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRichiedente());
			
			final String autorizzazioneRuoli = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRuoli());
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzAbilitata.getRuoliFonte());
			RuoloTipoMatch tipoRuoloMatch = null;
			if(authzAbilitata.getRuoliRichiesti()!=null) {
				tipoRuoloMatch = RuoloTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getRuoliRichiesti().toString()) );	// Gli enum coincidono
			}
			
			final String autorizzazioneTokenAutenticati = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isTokenRichiedente());
			
			final String autorizzazioneTokenRuoli = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isTokenRuoli());
			final org.openspcoop2.core.config.constants.RuoloTipologia tipoTokenRuoloFonte = Enums.ruoloTipologiaConfigFromRest.get(authzAbilitata.getTokenRuoliFonte());
			RuoloTipoMatch tipoTokenRuoloMatch = null;
			if(authzAbilitata.getTokenRuoliRichiesti()!=null) {
				tipoTokenRuoloMatch = RuoloTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getTokenRuoliRichiesti().toString()) );	// Gli enum coincidono
			}
			
			final String autorizzazioneScope = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isScope());
			final ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getScopeRichiesti().toString()) );
			
			String autorizzazione_tokenOptions = null;
			if(authzAbilitata.getTokenClaims()!=null && !authzAbilitata.getTokenClaims().isEmpty()) {
				autorizzazione_tokenOptions = String.join("\n", authzAbilitata.getTokenClaims());
			}
			
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione,
					ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),
					ServletUtils.isCheckBoxEnabled(autorizzazioneTokenAutenticati), ServletUtils.isCheckBoxEnabled(autorizzazioneTokenRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
					autorizzazione_tokenOptions,					// Questo è il token claims
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPd.setAutorizzazione(tipoAutorString);
			
			if ( authzAbilitata.isRuoli() ) {
				if ( newPd.getRuoli() == null) newPd.setRuoli(new AutorizzazioneRuoli());
				
				newPd.getRuoli().setMatch(tipoRuoloMatch);
			} else {
				newPd.setRuoli(null);
			}
			
			if(authzAbilitata.isTokenRichiedente() || authzAbilitata.isTokenRuoli()) {
				newPd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
				
				if(authzAbilitata.isTokenRichiedente()) {
					newPd.getAutorizzazioneToken().setAutorizzazioneApplicativi(StatoFunzionalita.ABILITATO);
				}
				
				if(authzAbilitata.isTokenRuoli()) {
					newPd.getAutorizzazioneToken().setAutorizzazioneRuoli(StatoFunzionalita.ABILITATO);
					newPd.getAutorizzazioneToken().setTipologiaRuoli(tipoTokenRuoloFonte);
					if(newPd.getAutorizzazioneToken().getRuoli()==null) {
						newPd.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
					}
					newPd.getAutorizzazioneToken().getRuoli().setMatch(tipoTokenRuoloMatch);
				}
			}
			
			if ( authzAbilitata.isScope() ) {
				if ( newPd.getScope() == null ) newPd.setScope(new AutorizzazioneScope());
				
				newPd.getScope().setMatch(scopeTipoMatch);
				newPd.getScope().setStato(StatoFunzionalita.ABILITATO);
			} else {
				newPd.setScope(null);
			}
			
			
			if ( authzAbilitata.isToken() ) {
				if ( newPd.getGestioneToken() == null ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun token configurato per l'erogazione");
				}
				newPd.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			}
			else {
				BaseHelper.runNull( () -> newPd.getGestioneToken().setOptions(null) );
			}
			break;
		}
		case XACML_POLICY: {
			
			if(! (authz instanceof APIImplAutorizzazioneXACML)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
			
			if (authzXacml.getPolicy() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
			}
			
			if (authzXacml.getRuoliFonte() == null)
				authzXacml.setRuoliFonte(FonteEnum.QUALSIASI);
			
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzXacml.getRuoliFonte());
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(TipoAutorizzazioneEnum.XACML_POLICY.toString(),
					false,false,
					false,false,
					false,
					"",
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPd.setAutorizzazione(tipoAutorString);
			newPd.setXacmlPolicy( evalnull( () -> new String(authzXacml.getPolicy())));				
			break;
		}
		case CUSTOM:
			
			if(! (authz instanceof APIImplAutorizzazioneCustom)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneCustom authzACustom = (APIImplAutorizzazioneCustom) authz;
			
			if (StringUtils.isEmpty(authzACustom.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(APIImplAutorizzazioneCustom.class.getName()+": Indicare il campo obbligatorio 'nome' ");
			}
			newPd.setAutorizzazione(authzACustom.getNome());
			break;
		}	
	}



	public static final void fillPortaApplicativa(ControlloAccessiAutorizzazione body, final PortaApplicativa newPa) {
		final OneOfControlloAccessiAutorizzazioneAutorizzazione authz = body.getAutorizzazione();
		
		newPa.setXacmlPolicy(null);

		switch (authz.getTipo()) {
		case DISABILITATO: {
			
			final String tipoAutorString = TipoAutorizzazione.DISABILITATO.toString();
			newPa.setSoggetti(null);
			newPa.setRuoli(null);
			newPa.setScope(null);
			newPa.setAutorizzazione(tipoAutorString);
			break;
		}
		
		
		case ABILITATO: {
			if(! (authz instanceof APIImplAutorizzazioneAbilitata)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneAbilitata authzAbilitata = (APIImplAutorizzazioneAbilitata) authz;
			
			// defaults
			if ( authzAbilitata.isRuoli() && authzAbilitata.getRuoliFonte() == null ) {
				authzAbilitata.setRuoliFonte(FonteEnum.QUALSIASI);
			}
			
			final String statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
			
			final String autorizzazioneAutenticati = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRichiedente());
			
			final String autorizzazioneRuoli = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRuoli());
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzAbilitata.getRuoliFonte());
			RuoloTipoMatch tipoRuoloMatch = null;
			if(authzAbilitata.getRuoliRichiesti()!=null) {
				tipoRuoloMatch = RuoloTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getRuoliRichiesti().toString()) );	// Gli enum coincidono
			}
			
			final String autorizzazioneTokenAutenticati = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isTokenRichiedente());
			
			final String autorizzazioneTokenRuoli = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isTokenRuoli());
			final org.openspcoop2.core.config.constants.RuoloTipologia tipoTokenRuoloFonte = Enums.ruoloTipologiaConfigFromRest.get(authzAbilitata.getTokenRuoliFonte());
			RuoloTipoMatch tipoTokenRuoloMatch = null;
			if(authzAbilitata.getTokenRuoliRichiesti()!=null) {
				tipoTokenRuoloMatch = RuoloTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getTokenRuoliRichiesti().toString()) );	// Gli enum coincidono
			}
			
			final String autorizzazioneScope = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isScope());
			final ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getScopeRichiesti().toString()) );
			
			String autorizzazione_tokenOptions = null;
			if(authzAbilitata.getTokenClaims()!=null && !authzAbilitata.getTokenClaims().isEmpty()) {
				autorizzazione_tokenOptions = String.join("\n", authzAbilitata.getTokenClaims());
			}
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(statoAutorizzazione,
					ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneTokenAutenticati), ServletUtils.isCheckBoxEnabled(autorizzazioneTokenRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
					autorizzazione_tokenOptions,					// Questo è il token claims
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPa.setAutorizzazione(tipoAutorString);
			
			if ( authzAbilitata.isRuoli() ) {
				if ( newPa.getRuoli() == null) newPa.setRuoli(new AutorizzazioneRuoli());
				
				newPa.getRuoli().setMatch(tipoRuoloMatch);
			} else {
				newPa.setRuoli(null);
			}
			
			if(authzAbilitata.isTokenRichiedente() || authzAbilitata.isTokenRuoli()) {
				newPa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken());
				
				if(authzAbilitata.isTokenRichiedente()) {
					newPa.getAutorizzazioneToken().setAutorizzazioneApplicativi(StatoFunzionalita.ABILITATO);
				}
				
				if(authzAbilitata.isTokenRuoli()) {
					newPa.getAutorizzazioneToken().setAutorizzazioneRuoli(StatoFunzionalita.ABILITATO);
					newPa.getAutorizzazioneToken().setTipologiaRuoli(tipoTokenRuoloFonte);
					if(newPa.getAutorizzazioneToken().getRuoli()==null) {
						newPa.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
					}
					newPa.getAutorizzazioneToken().getRuoli().setMatch(tipoTokenRuoloMatch);
				}
			} else if (!authzAbilitata.isTokenRichiedente().booleanValue() && !authzAbilitata.isTokenRuoli().booleanValue() && authzAbilitata.isToken().booleanValue()) {
				newPa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken());
			} else {
				newPa.setAutorizzazioneToken(null);
			}
			
			if ( authzAbilitata.isScope() ) {
				if ( newPa.getScope() == null ) newPa.setScope(new AutorizzazioneScope());
				
				newPa.getScope().setMatch(scopeTipoMatch);
				newPa.getScope().setStato(StatoFunzionalita.ABILITATO);
			} else {
				newPa.setScope(null);
			}
			
			
			if ( authzAbilitata.isToken() ) {
				if ( newPa.getGestioneToken() == null ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun token configurato per l'erogazione");
				}
				newPa.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			}
			else {
				BaseHelper.runNull( () -> newPa.getGestioneToken().setOptions(null) );
			}
			break;
		}
		case XACML_POLICY: {
			
			if(! (authz instanceof APIImplAutorizzazioneXACML)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
			
			if (authzXacml.getPolicy() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
			}
			
			if (authzXacml.getRuoliFonte() == null)
				authzXacml.setRuoliFonte(FonteEnum.QUALSIASI);
			
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzXacml.getRuoliFonte());
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(AutorizzazioneUtilities.STATO_XACML_POLICY,
					false,false,		
					false,false,		
					false,
					"",
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			
			newPa.setAutorizzazione(tipoAutorString);
			newPa.setXacmlPolicy( evalnull( () -> new String(authzXacml.getPolicy())));				
			break;
		}
		case CUSTOM:
			
			if(! (authz instanceof APIImplAutorizzazioneCustom)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneCustom authzACustom = (APIImplAutorizzazioneCustom) authz;
			
			if (StringUtils.isEmpty(authzACustom.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(APIImplAutorizzazioneCustom.class.getName()+": Indicare il campo obbligatorio 'nome' ");
			}
			newPa.setAutorizzazione(authzACustom.getNome());
			break;
		}
	}
	
	



	public static final TipoAutorizzazioneEnum getTipoAutorizzazione(final String tipo_autorizzazione_pa) {
		TipoAutorizzazioneEnum tipoAuthz = null;
		
		if (TipoAutorizzazione.toEnumConstant(tipo_autorizzazione_pa) == null) {
			tipoAuthz = TipoAutorizzazioneEnum.CUSTOM;
		} else {
			String stato_auth = AutorizzazioneUtilities.convertToStato(tipo_autorizzazione_pa);
			
			if (AutorizzazioneUtilities.STATO_ABILITATO.equals(stato_auth))
				tipoAuthz = TipoAutorizzazioneEnum.ABILITATO;
			
			else if ( AutorizzazioneUtilities.STATO_DISABILITATO.equals(stato_auth) )
				tipoAuthz = TipoAutorizzazioneEnum.DISABILITATO;
			
			else if ( AutorizzazioneUtilities.STATO_XACML_POLICY.equals(stato_auth) )
				tipoAuthz = TipoAutorizzazioneEnum.XACML_POLICY;
			else 
				throw FaultCode.ERRORE_INTERNO.toException("Stato autorizzazione " + stato_auth + " sconosciuto");
		}
		return tipoAuthz;
	}



	public static final ControlloAccessiAutorizzazioneView controlloAccessiAutorizzazioneFromPA(final PortaApplicativa pa) {
		ControlloAccessiAutorizzazioneView ret = new ControlloAccessiAutorizzazioneView();
		
		TipoAutorizzazioneEnum tipoAuthz = getTipoAutorizzazione(pa.getAutorizzazione());		
	
		OneOfControlloAccessiAutorizzazioneViewAutorizzazione retAuthz = null;
		
		switch ( tipoAuthz ) {
		case ABILITATO: {
			APIImplAutorizzazioneAbilitata authzAbilitata = new APIImplAutorizzazioneAbilitata();
			authzAbilitata.setTipo(tipoAuthz);
			retAuthz = authzAbilitata;
			
			authzAbilitata.setRichiedente(TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione()));
			
			authzAbilitata.setRuoli( TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()) );
			if (authzAbilitata.isRuoli()) {
			
				authzAbilitata.setRuoliRichiesti( evalnull(  () -> AllAnyEnum.fromValue( pa.getRuoli().getMatch().toString()) ) );
				authzAbilitata.setRuoliFonte( evalnull( () -> 
						Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()) )
					));
			}
			
			if(pa.getAutorizzazioneToken()!=null) {
				if(StatoFunzionalita.ABILITATO.equals(pa.getAutorizzazioneToken().getAutorizzazioneApplicativi())) {
					authzAbilitata.setTokenRichiedente(true);
				}
				
				if(StatoFunzionalita.ABILITATO.equals(pa.getAutorizzazioneToken().getAutorizzazioneRuoli())) {
					authzAbilitata.setTokenRuoli(true);
					
					authzAbilitata.setTokenRuoliRichiesti( evalnull(  () -> AllAnyEnum.fromValue( pa.getAutorizzazioneToken().getRuoli().getMatch().toString()) ) );
					if(pa.getAutorizzazioneToken().getTipologiaRuoli()!=null) {
						switch (pa.getAutorizzazioneToken().getTipologiaRuoli()) {
						case INTERNO:
							authzAbilitata.setTokenRuoliFonte(FonteEnum.REGISTRO);
							break;
						case ESTERNO:
							authzAbilitata.setTokenRuoliFonte(FonteEnum.ESTERNA);						
							break;
						case QUALSIASI:
							authzAbilitata.setTokenRuoliFonte(FonteEnum.QUALSIASI);
							break;
						}
					}
				}
			}
			
			authzAbilitata.setScope( pa.getScope() != null );
			authzAbilitata.setScopeRichiesti( evalnull( () -> AllAnyEnum.fromValue( pa.getScope().getMatch().getValue() )));
			
			authzAbilitata.setToken( (pa.getGestioneToken()!=null && pa.getGestioneToken().getOptions() != null) ? true : false);
			
			if(pa.getGestioneToken()!=null) {
				String pString = pa.getGestioneToken().getOptions();
				if(pString!=null) {
					List<String> proprieta = new ArrayList<>();
					String[] psplit = pString.split("\n");
					for(String pr: psplit) {
						proprieta.add(pr);
					}
					authzAbilitata.setTokenClaims(proprieta);
				}
			}
			
			break;
		}
		case XACML_POLICY: {
			APIImplAutorizzazioneXACMLView authzXacml = new APIImplAutorizzazioneXACMLView();
			authzXacml.setTipo(tipoAuthz);
			retAuthz = authzXacml;
			
			authzXacml.setRuoliFonte( evalnull( () -> 
					Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()) )
				));

			break;
		}
		case CUSTOM: {
			APIImplAutorizzazioneCustom authzCustom = new APIImplAutorizzazioneCustom();
			authzCustom.setTipo(tipoAuthz);
			retAuthz = authzCustom;
			
			authzCustom.setNome(pa.getAutorizzazione());

			break;
		}
		case DISABILITATO: {
			APIImplAutorizzazioneDisabilitata authzDisabilitata = new APIImplAutorizzazioneDisabilitata();
			authzDisabilitata.setTipo(tipoAuthz);
			retAuthz = authzDisabilitata;
			
			break;
		}
		}
		
		ret.setAutorizzazione(retAuthz);
		return ret;
	}
	
	public static final ControlloAccessiAutorizzazioneView controlloAccessiAutorizzazioneFromPD(final PortaDelegata pd) {
		ControlloAccessiAutorizzazioneView ret = new ControlloAccessiAutorizzazioneView();
		
		TipoAutorizzazioneEnum tipoAuthz = getTipoAutorizzazione(pd.getAutorizzazione());		
	
		OneOfControlloAccessiAutorizzazioneViewAutorizzazione retAuthz = null;
		
		switch ( tipoAuthz ) {
		case ABILITATO: {
			APIImplAutorizzazioneAbilitata authzAbilitata = new APIImplAutorizzazioneAbilitata();
			authzAbilitata.setTipo(tipoAuthz);
			retAuthz = authzAbilitata;
			
			authzAbilitata.setRichiedente( TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione()));
			
			
			authzAbilitata.setRuoli( TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione()) );
			if (authzAbilitata.isRuoli()) {
				authzAbilitata.setRuoliRichiesti( evalnull(  () -> AllAnyEnum.fromValue( pd.getRuoli().getMatch().toString()) ) );
				authzAbilitata.setRuoliFonte( evalnull( () -> 
						Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()) )
					));
			}
			
			if(pd.getAutorizzazioneToken()!=null) {
				if(StatoFunzionalita.ABILITATO.equals(pd.getAutorizzazioneToken().getAutorizzazioneApplicativi())) {
					authzAbilitata.setTokenRichiedente(true);
				}
				
				if(StatoFunzionalita.ABILITATO.equals(pd.getAutorizzazioneToken().getAutorizzazioneRuoli())) {
					authzAbilitata.setTokenRuoli(true);
					
					authzAbilitata.setTokenRuoliRichiesti( evalnull(  () -> AllAnyEnum.fromValue( pd.getAutorizzazioneToken().getRuoli().getMatch().toString()) ) );
					if(pd.getAutorizzazioneToken().getTipologiaRuoli()!=null) {
						switch (pd.getAutorizzazioneToken().getTipologiaRuoli()) {
						case INTERNO:
							authzAbilitata.setTokenRuoliFonte(FonteEnum.REGISTRO);
							break;
						case ESTERNO:
							authzAbilitata.setTokenRuoliFonte(FonteEnum.ESTERNA);						
							break;
						case QUALSIASI:
							authzAbilitata.setTokenRuoliFonte(FonteEnum.QUALSIASI);
							break;
						}
					}
				}
			}
			
			authzAbilitata.setScope( pd.getScope() != null );
			authzAbilitata.setScopeRichiesti( evalnull( () -> AllAnyEnum.fromValue( pd.getScope().getMatch().getValue() )));
			
			authzAbilitata.setToken( (pd.getGestioneToken()!=null && pd.getGestioneToken().getOptions() != null) ? true : false);
			
			if(pd.getGestioneToken()!=null) {
				String pString = pd.getGestioneToken().getOptions();
				if(pString!=null) {
					List<String> proprieta = new ArrayList<>();
					String[] psplit = pString.split("\n");
					for(String pr: psplit) {
						proprieta.add(pr);
					}
					authzAbilitata.setTokenClaims(proprieta);
				}
			}
				
			break;
		}
		case XACML_POLICY: {
			APIImplAutorizzazioneXACMLView authzXacml = new APIImplAutorizzazioneXACMLView();
			authzXacml.setTipo(tipoAuthz);
			retAuthz = authzXacml;
			
			authzXacml.setRuoliFonte( evalnull( () -> 
					Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()) )
				));

			break;
		}
		case CUSTOM: {
			APIImplAutorizzazioneCustom authzCustom = new APIImplAutorizzazioneCustom();
			authzCustom.setTipo(tipoAuthz);
			retAuthz = authzCustom;
			
			authzCustom.setNome(pd.getAutorizzazione());

			break;
		}
		case DISABILITATO: {
			APIImplAutorizzazioneDisabilitata authzDisabilitata = new APIImplAutorizzazioneDisabilitata();
			authzDisabilitata.setTipo(tipoAuthz);
			retAuthz = authzDisabilitata;
			
			break;
		}
		}
		
		ret.setAutorizzazione(retAuthz);
		return ret;
	}



	public static final void fillPortaApplicativa(final ErogazioniEnv env, ControlloAccessiAutenticazione body, final PortaApplicativa newPa) throws InstantiationException, IllegalAccessException {
		final OneOfControlloAccessiAutenticazioneAutenticazione auth = body.getAutenticazione();
		final BooleanNullable autenticazioneOpzionaleNullable = getAutenticazioneOpzionale(auth); // gestisce auth se null
		
		newPa.setAutenticazioneOpzionale( evalnull( () -> Helper.boolToStatoFunzionalitaConf(autenticazioneOpzionaleNullable!=null ? autenticazioneOpzionaleNullable.getValue() : null)) );
		newPa.setAutenticazione( evalnull( () -> Enums.tipoAutenticazioneFromRest.get(auth.getTipo()).toString()) );
		
        TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(auth!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(auth); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, auth.getTipo(), auth);
        }
		List<Proprieta> proprietaAutenticazione = env.paCore.convertToAutenticazioneProprieta(newPa.getAutenticazione(), autenticazionePrincipal, autenticazioneParametroList);
		newPa.setProprietaAutenticazioneList(proprietaAutenticazione);
		
		// Imposto l'autenticazione custom
		if ( evalnull( () -> auth.getTipo() ) == TipoAutenticazioneEnum.CUSTOM) {
			if(auth!=null && auth instanceof APIImplAutenticazioneCustom) {
				APIImplAutenticazioneCustom authnCustom = (APIImplAutenticazioneCustom) auth;
				newPa.setAutenticazione(authnCustom.getNome());
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+(auth!=null ? auth.getTipo() : "null?")+"' non è correttamente definita (trovata configurazione '"+
						(auth!=null ? auth.getClass().getName() : "null?")+"')"  );
			}
		}
		// Gestione Token
		final ControlloAccessiAutenticazioneToken gToken = body.getToken();
		if (gToken != null) {
			final boolean isGestioneToken = gToken.isClientId() || gToken.isEmail() || gToken.isIssuer() || gToken.isSubject() || gToken.isUsername();
			
			if (isGestioneToken) {
				if(newPa.getGestioneToken() == null)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La gestione token non è abilitata per il gruppo");
				
				if(newPa.getGestioneToken().getAutenticazione()==null) {
					newPa.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				
				newPa.getGestioneToken().getAutenticazione().setIssuer(Helper.boolToStatoFunzionalitaConf(gToken.isIssuer())); 
				newPa.getGestioneToken().getAutenticazione().setClientId(Helper.boolToStatoFunzionalitaConf(gToken.isClientId())); 
				newPa.getGestioneToken().getAutenticazione().setSubject(Helper.boolToStatoFunzionalitaConf(gToken.isSubject())); 
				newPa.getGestioneToken().getAutenticazione().setUsername(Helper.boolToStatoFunzionalitaConf(gToken.isUsername())); 
				newPa.getGestioneToken().getAutenticazione().setEmail(Helper.boolToStatoFunzionalitaConf(gToken.isEmail()));	
			}
		}
		
	}
	
	
	public static final void fillPortaDelegata(final ErogazioniEnv env, ControlloAccessiAutenticazione body, final PortaDelegata newPd) {
		final OneOfControlloAccessiAutenticazioneAutenticazione auth = body.getAutenticazione();
		final BooleanNullable autenticazioneOpzionaleNullable = getAutenticazioneOpzionale(auth); // gestisce auth se null
		
		newPd.setAutenticazioneOpzionale( evalnull( () -> Helper.boolToStatoFunzionalitaConf(autenticazioneOpzionaleNullable!=null ? autenticazioneOpzionaleNullable.getValue() : null)) );
		newPd.setAutenticazione( evalnull( () -> Enums.tipoAutenticazioneFromRest.get(auth.getTipo()).toString()) );
		
		TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(auth!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(auth); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, auth.getTipo(), auth);
        }
		List<Proprieta> proprietaAutenticazione = env.paCore.convertToAutenticazioneProprieta(newPd.getAutenticazione(), autenticazionePrincipal, autenticazioneParametroList);
		newPd.setProprietaAutenticazioneList(proprietaAutenticazione);
		
		if ( evalnull( () -> auth.getTipo() ) == TipoAutenticazioneEnum.CUSTOM) {
			if(auth!=null && auth instanceof APIImplAutenticazioneCustom) {
				APIImplAutenticazioneCustom authnCustom = (APIImplAutenticazioneCustom) auth;
				newPd.setAutenticazione(authnCustom.getNome());
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+(auth!=null ? auth.getTipo() : "null?")+"' non è correttamente definita (trovata configurazione '"+
						(auth!=null ? auth.getClass().getName() : "null?")+"')"  );
			}
		}
		// Gestione Token
		final ControlloAccessiAutenticazioneToken gToken = body.getToken();
		if (gToken != null) {
			final boolean isGestioneToken = gToken.isClientId() || gToken.isEmail() || gToken.isIssuer() || gToken.isSubject() || gToken.isUsername();
	
			if (isGestioneToken) {
				if(newPd.getGestioneToken() == null)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La gestione token non è abilitata per il gruppo");
				
				if(newPd.getGestioneToken().getAutenticazione()==null) {
					newPd.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				
				newPd.getGestioneToken().getAutenticazione().setIssuer(Helper.boolToStatoFunzionalitaConf(gToken.isIssuer())); 
				newPd.getGestioneToken().getAutenticazione().setClientId(Helper.boolToStatoFunzionalitaConf(gToken.isClientId())); 
				newPd.getGestioneToken().getAutenticazione().setSubject(Helper.boolToStatoFunzionalitaConf(gToken.isSubject())); 
				newPd.getGestioneToken().getAutenticazione().setUsername(Helper.boolToStatoFunzionalitaConf(gToken.isUsername())); 
				newPd.getGestioneToken().getAutenticazione().setEmail(Helper.boolToStatoFunzionalitaConf(gToken.isEmail()));
			}
		}
		
	}



	public static final IDSoggetto getIdReferente(APIImpl body, final ErogazioniEnv env)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		IDSoggetto idReferente = null;
		 
		if( env.apcCore.isSupportatoSoggettoReferente(env.tipo_protocollo) ){
			if ( body.getApiReferente() == null )
				body.setApiReferente(env.idSoggetto.getNome());
			
			idReferente = new IDSoggetto(env.tipo_soggetto,body.getApiReferente());
		}
	
		else {
			idReferente = env.soggettiCore.getSoggettoOperativoDefault(env.userLogin,env.tipo_protocollo);
		}
		return idReferente;
	}



	public static final GestioneCors convert(final CorsConfigurazione paConf) {
		final GestioneCors ret = new GestioneCors();
		
		if (paConf != null) {
			ret.setTipo( Enums.dualizeMap(Enums.tipoGestioneCorsFromRest).get(paConf.getTipo()));
			ret.setRidefinito(true);
			
			if ( ret.getTipo() == TipoGestioneCorsEnum.GATEWAY ) {

				GestioneCorsAccessControl opts = new GestioneCorsAccessControl();
				
				opts.setAllAllowOrigins( Helper.statoFunzionalitaConfToBool( paConf.getAccessControlAllAllowOrigins() ));
				opts.setAllAllowHeaders( Helper.statoFunzionalitaConfToBool( paConf.getAccessControlAllAllowHeaders() ));
				opts.setAllAllowMethods( Helper.statoFunzionalitaConfToBool( paConf.getAccessControlAllAllowMethods() ));
				opts.setAllowCredentials(  Helper.statoFunzionalitaConfToBool( paConf.getAccessControlAllowCredentials() ));
				opts.setAllowHeaders( evalnull( () -> paConf.getAccessControlAllowHeaders().getHeaderList()) );
				opts.setAllowMethods( evalnull( () -> 
						paConf.getAccessControlAllowMethods().getMethodList().stream().map(HttpMethodEnum::valueOf).collect(Collectors.toList())
						));
				opts.setAllowOrigins( evalnull( () -> paConf.getAccessControlAllowOrigins().getOriginList()));	
				opts.setExposeHeaders( evalnull( () -> paConf.getAccessControlExposeHeaders().getHeaderList()) );
				if(paConf.getAccessControlMaxAge()!=null) {
					opts.setMaxAge( true );
					opts.setMaxAgeSeconds(paConf.getAccessControlMaxAge());
				}
				
				ret.setAccessControl(opts);
			}
			

		}
		else {
			ret.setRidefinito(false);
		}
		return ret;
	}
	
	// CONVERSIONI POLICY RAGGRUPPAMENTO
	
	public static final RateLimitingPolicyGroupBy convert( AttivazionePolicyRaggruppamento src, RateLimitingPolicyGroupBy dest ) {
		
		dest.setAzione(src.isAzione());
		
		if(src.isServizioApplicativoFruitore() || src.isFruitore() || src.isIdentificativoAutenticato()) {
			dest.setRichiedente(true);
		}
		
		if(src.getToken()!=null) {
			String [] tmp = src.getToken().split(",");
			if(tmp!=null && tmp.length>0) {
				List<RateLimitingPolicyGroupByTokenClaimEnum> token = new ArrayList<>();
				for (int i = 0; i < tmp.length; i++) {
					TipoCredenzialeMittente tipo = null;
					try {
						tipo = TipoCredenzialeMittente.toEnumConstant(tmp[i], true);
					}catch(Exception e) {
						LoggerProperties.getLoggerCore().error(e.getMessage(),e);
					}
					if(tipo!=null) {
						switch (tipo) {
						case TOKEN_SUBJECT:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.SUBJECT);
							break;
						case TOKEN_ISSUER:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.ISSUER);
							break;
						case TOKEN_CLIENT_ID:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.CLIENT_ID);
							break;
						case TOKEN_USERNAME:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.USERNAME);
							break;
						case TOKEN_EMAIL:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.EMAIL);
							break;
							
						case PDND_ORGANIZATION_NAME:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.PDND_ORGANIZATION_NAME);
							break;
						case PDND_ORGANIZATION_EXTERNAL_ID:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.PDND_EXTERNAL_ID);
							break;
						case PDND_ORGANIZATION_CONSUMER_ID:
							token.add(RateLimitingPolicyGroupByTokenClaimEnum.PDND_CONSUMER_ID);
							break;
						default:
							break;
						}
					}
				}
				dest.setToken(token);
			}
		}
		
		dest.setChiaveNome(src.getInformazioneApplicativaNome());
		dest.setChiaveTipo(
				Enums.rateLimitingChiaveEnum.get( TipoFiltroApplicativo.toEnumConstant(src.getInformazioneApplicativaTipo()) )
			);
		
		return dest;
	}
	
	
	
	// CONVERSIONI POLICY FILTRO
	
	public static final RateLimitingPolicyFiltro  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltro dest ) {
		
		dest.setApplicativoFruitore( src.getServizioApplicativoFruitore() ); 
		
		if(src.getAzione()!=null && !"".equals(src.getAzione())) {
			String [] tmp = src.getAzione().split(",");
			if(tmp!=null && tmp.length>0) {
				List<String> azione = new ArrayList<>();
				for (int i = 0; i < tmp.length; i++) {
					azione.add(tmp[i]);
				}
				dest.setAzione(azione);
			}
		}
		
		dest.setRuoloRichiedente(src.getRuoloFruitore());
		
		if(src.getTokenClaims()!=null && !"".equals(src.getTokenClaims())) {
			List<String> proprieta = new ArrayList<>();
			String[] psplit = src.getTokenClaims().split("\n");
			for(String pr: psplit) {
				proprieta.add(pr);
			}
			dest.setTokenClaims(proprieta);
		}
		
		dest.setChiaveNome(src.getInformazioneApplicativaNome());
		dest.setChiaveTipo(
				Enums.rateLimitingChiaveEnum.get( TipoFiltroApplicativo.toEnumConstant(src.getInformazioneApplicativaTipo()) )
			);
				
		dest.setFiltroChiaveValore(src.getInformazioneApplicativaValore());
		
		return dest;
	}
	
	
	public static final RateLimitingPolicyFiltroErogazione  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltroErogazione dest ) {
		convert( src, (RateLimitingPolicyFiltro) dest);
		dest.setSoggettoFruitore(src.getNomeFruitore());
			
		return dest;
	}
	
	
	public static final RateLimitingPolicyFiltroFruizione  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltroFruizione dest ) {
		convert(src, (RateLimitingPolicyFiltro) dest);
		return dest;
	}
	
	
	// ATTIVAZIONE POLICY Conversion
	
	public static final RateLimitingPolicyFruizioneView convert ( AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyFruizioneView dest ) {
		
		convert( src, infoPolicy, (RateLimitingPolicyBase) dest );

		dest.setDescrizione(infoPolicy.getDescrizione());
		dest.setNome( PolicyUtilities.getNomeActivePolicy(src.getAlias(), src.getIdActivePolicy()));
				
		dest.setRaggruppamento(
				convert( src.getGroupBy(), new RateLimitingPolicyGroupBy() )
			);
	 
		
		dest.setFiltro(
				convert( src.getFiltro(), new RateLimitingPolicyFiltroFruizione() )
			);
		
		return dest;
	}

	
	
	public static final RateLimitingPolicyErogazioneView convert ( AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyErogazioneView dest ) {
		
		convert( src, infoPolicy, (RateLimitingPolicyBase) dest );

		dest.setDescrizione(infoPolicy.getDescrizione());
		dest.setNome( PolicyUtilities.getNomeActivePolicy(src.getAlias(), src.getIdActivePolicy()));
		
		dest.setRaggruppamento(
				convert( src.getGroupBy(), new RateLimitingPolicyGroupBy() )
			);
	 
		
		dest.setFiltro(
				convert( src.getFiltro(), new RateLimitingPolicyFiltroErogazione() )
			);
		
		return dest;
	}
	
	
	public static final RateLimitingPolicyBase convert (AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyBase dest ) {
	
		dest.setNome(src.getAlias());
		
		if ( src.isWarningOnly() )
			dest.setStato( StatoFunzionalitaConWarningEnum.WARNINGONLY );
		
		if ( src.isEnabled() )
			dest.setStato(StatoFunzionalitaConWarningEnum.ABILITATO );
		else
			dest.setStato(StatoFunzionalitaConWarningEnum.DISABILITATO );
		
		boolean dimensioneMessaggio = false;
		
		if(dest instanceof RateLimitingPolicyBaseConIdentificazione) {
			RateLimitingPolicyBaseConIdentificazione destIdentificazione = (RateLimitingPolicyBaseConIdentificazione) dest;
			
			if(infoPolicy.isBuiltIn()) {
				RateLimitingPolicyCriteri criteri = new RateLimitingPolicyCriteri();
				criteri.setIdentificazione(RateLimitingIdentificazionePolicyEnum.CRITERI);
				boolean intervallo = true;
				switch (infoPolicy.getTipoRisorsa()) {
				case NUMERO_RICHIESTE:
					if(infoPolicy.isCheckRichiesteSimultanee()) {
						criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_SIMULTANEE);
						intervallo = false;
					}
					else {
						criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE);
					}
					break;
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_OK);
					break;
				case NUMERO_RICHIESTE_FALLITE:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_FALLITE);
					break;
				case NUMERO_FAULT_APPLICATIVI:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_FAULT_APPLICATIVI);
					break;
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI);
					break;
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO_OFAULT_APPLICATIVI:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_OK_O_FAULT_APPLICATIVI);
					break;
				case DIMENSIONE_MASSIMA_MESSAGGIO:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.DIMENSIONE_MASSIMA);
					dimensioneMessaggio = true;
					break;
				case OCCUPAZIONE_BANDA:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.OCCUPAZIONE_BANDA);
					break;
				case TEMPO_MEDIO_RISPOSTA:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.TEMPO_MEDIO_RISPOSTA);
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.TEMPO_COMPLESSIVO_RISPOSTA);
					break;
				}
				if(intervallo) {
					switch (infoPolicy.getIntervalloUtilizzaRisorseRealtimeTipoPeriodo()) {
					case MINUTI:
						criteri.setIntervallo(RateLimitingCriteriIntervalloEnum.MINUTI);
						break;
					case ORARIO:
						criteri.setIntervallo(RateLimitingCriteriIntervalloEnum.ORARIO);
						break;
					case GIORNALIERO:
						criteri.setIntervallo(RateLimitingCriteriIntervalloEnum.GIORNALIERO);
						break;
					default:
						break;
					}
				}
				criteri.setCongestione(infoPolicy.isControlloCongestione());
				criteri.setDegrado(infoPolicy.isDegradoPrestazione());
				destIdentificazione.setConfigurazione(criteri);
			}
			else {
				RateLimitingPolicyIdentificativo id = new RateLimitingPolicyIdentificativo();
				id.setIdentificazione(RateLimitingIdentificazionePolicyEnum.POLICY);
				id.setPolicy(infoPolicy.getIdPolicy());
				
				if(TipoRisorsa.DIMENSIONE_MASSIMA_MESSAGGIO.equals(infoPolicy.getTipoRisorsa())) {
					dimensioneMessaggio = true;
				}
				
				destIdentificazione.setConfigurazione(id);
			}
		}

		
		
		dest.setSogliaRidefinita(src.isRidefinisci());
		
		if ( src.isRidefinisci()) {
			if(dimensioneMessaggio) {
				dest.setSogliaDimensioneRichiesta(src.getValore2().intValue());
				dest.setSogliaDimensioneRisposta(src.getValore().intValue());
			}
			else {
				dest.setSogliaValore(src.getValore().intValue());
			}
		}
		else {
			if(dimensioneMessaggio) {
				dest.setSogliaDimensioneRichiesta(infoPolicy.getValore2().intValue());
				dest.setSogliaDimensioneRisposta(infoPolicy.getValore().intValue());
			}
			else {
				dest.setSogliaValore(infoPolicy.getValore().intValue());
			}
		}
		
		
		return dest;
			
	}


	public static final void checkAzioniAdd( List<String> toAdd, List<String> occupate, List<String> presenti ) {
		
		for ( String azione : toAdd ) {
			
			if( occupate.contains(azione) ) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_GIA_PRESENTE));			
			}
			
			if( !presenti.contains(azione) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Azione " + azione + " non presente fra le azioni dell'accordo");
			}
		}
		
	}


	public static final String getDataElementModalita(RateLimitingIdentificazionePolicyEnum identificazione) {
		switch (identificazione) {
		case POLICY:
			return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CUSTOM;
		case CRITERI:
			return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN;
		}
		return null;
	}
	public static final String getDataElementModalita(boolean builtIt) {
		return builtIt ? getDataElementModalita(RateLimitingIdentificazionePolicyEnum.CRITERI) : getDataElementModalita(RateLimitingIdentificazionePolicyEnum.POLICY);
	}
	
	public static final String getIdPolicy(RateLimitingPolicyBaseConIdentificazione body, ConfigurazioneCore confCore, ConfigurazioneHelper confHelper) throws Exception {
		
		String idPolicy = null;
		switch (body.getConfigurazione().getIdentificazione()) {
		case POLICY:
			RateLimitingPolicyIdentificativo identificativo = new RateLimitingPolicyIdentificativo();
			identificativo = BaseHelper.deserialize(body.getConfigurazione(), RateLimitingPolicyIdentificativo.class);
        	idPolicy = identificativo.getPolicy();
			break;
		case CRITERI:
			RateLimitingPolicyCriteri criteri = new RateLimitingPolicyCriteri();
			criteri = BaseHelper.deserialize(body.getConfigurazione(), RateLimitingPolicyCriteri.class);
        	
        	String modalitaRisorsa = getDataElementModalitaRisorsa(criteri.getMetrica());
        	boolean modalitaSimultaneeEnabled = confHelper.isTipoRisorsaNumeroRichiesteSimultanee(modalitaRisorsa);
        	String modalitaEsiti = null;
        	String modalitaIntervallo = null;
        	if(criteri.getIntervallo()!=null) {
        		modalitaIntervallo = getDataElementModalitaIntervallo(criteri.getIntervallo());
        	}
        	boolean modalitaCongestioneEnabled = criteri.isCongestione()!=null ? criteri.isCongestione() : false;
        	boolean modalitaDegradoEnabled = criteri.isDegrado()!=null ? criteri.isDegrado() : false;
        	boolean modalitaErrorRateEnabled = false;
        	
        	List<InfoPolicy> infoPolicies = confCore.infoPolicyList(true);
        	List<InfoPolicy> idPoliciesSoddisfanoCriteri = new ArrayList<>();
        	confHelper.findPolicyBuiltIn(infoPolicies, idPoliciesSoddisfanoCriteri, 
        			modalitaRisorsa, modalitaEsiti, modalitaSimultaneeEnabled, modalitaIntervallo, 
        			modalitaCongestioneEnabled, modalitaDegradoEnabled, modalitaErrorRateEnabled);
        	if(idPoliciesSoddisfanoCriteri.size()>0) {
        		idPolicy = idPoliciesSoddisfanoCriteri.get(0).getIdPolicy();
        	}
			break;
		}
		
		return idPolicy;
	}
	
	public static String getDataElementModalitaRisorsa(RateLimitingCriteriMetricaEnum risorsa) {
		TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = null;
		switch (risorsa) {
		case NUMERO_RICHIESTE:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE;
			break;
		case NUMERO_RICHIESTE_SIMULTANEE:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE;
			break;
		case NUMERO_RICHIESTE_OK:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO;
			break;
		case NUMERO_RICHIESTE_FALLITE:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE;
			break;
		case NUMERO_FAULT_APPLICATIVI:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_FAULT_APPLICATIVI;
			break;
		case NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI;
			break;
		case NUMERO_RICHIESTE_OK_O_FAULT_APPLICATIVI:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO_OFAULT_APPLICATIVI;
			break;
		case DIMENSIONE_MASSIMA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.DIMENSIONE_MASSIMA_MESSAGGIO;
			break;
		case OCCUPAZIONE_BANDA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA;
			break;
		case TEMPO_MEDIO_RISPOSTA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA;
			break;
		case TEMPO_COMPLESSIVO_RISPOSTA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA;
			break;
		}
		return tipoRisorsaPolicyAttiva!=null ? tipoRisorsaPolicyAttiva.getValue() : null;
	}
	private static String getDataElementModalitaIntervallo(RateLimitingCriteriIntervalloEnum intervallo) {
    	String modalitaRisorsaEsiti = null;
		switch (intervallo) {
		case MINUTI:
			modalitaRisorsaEsiti = TipoPeriodoRealtime.MINUTI.getValue();
			break;
		case ORARIO:
			modalitaRisorsaEsiti = TipoPeriodoRealtime.ORARIO.getValue();
			break;
		case GIORNALIERO:
			modalitaRisorsaEsiti = TipoPeriodoRealtime.GIORNALIERO.getValue();
			break;
		}
		return modalitaRisorsaEsiti;
	}
	
	
	public static final void override(TipoRisorsa tipoRisorsa, String idPolicy, RateLimitingPolicyErogazione body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override (tipoRisorsa, (RateLimitingPolicyErogazione) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID, 
				idPolicy	
			);		
	}
	

	public static final void override(TipoRisorsa tipoRisorsa, String idPolicy, RateLimitingPolicyFruizione body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override (tipoRisorsa, (RateLimitingPolicyFruizione) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID, 
				idPolicy
			);
	}
	
	public static final void override( RateLimitingPolicyFiltro body, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;
		
		if(body.getAzione()!=null && !body.getAzione().isEmpty()) {
		
			wrap.overrideParameterValues(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE,
					body.getAzione().toArray(new String[1])
				);
		
		}
		
		wrap.overrideParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE, body.getApplicativoFruitore());
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE,
				evalnull( () -> body.getRuoloRichiedente() )  
			);
		
		String tokenClaims = null;
		if(body.getTokenClaims()!=null && !body.getTokenClaims().isEmpty()) {
			tokenClaims = String.join("\n", body.getTokenClaims());
		}
		if(tokenClaims!=null) {
			wrap.overrideParameter(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_TOKEN_CLAIMS,
					tokenClaims   
				);
		}
		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED,
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( body.getChiaveTipo() != null ))	// TOWAIT: mailandrea, non ho in rest un valore per la checkbox isFiltroAbilitato, quindi deduco il valore della checkbox così 
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO,
				evalnull( () -> Enums.tipoFiltroApplicativo.get( body.getChiaveTipo() ).toString())	
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME,
				evalnull( () -> body.getChiaveNome() )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE,
				evalnull( () -> body.getFiltroChiaveValore() ) 
			);	
	}
	
	private static final void override(	RateLimitingPolicyGroupBy body,  HttpRequestWrapper wrap,
			String protocollo, boolean applicativa)
	{
		if (body == null) return;

	
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED,
				body != null 
					?  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO
					:  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_DISABILITATO
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE,
				evalnull(  () -> ServletUtils.boolToCheckBoxStatus( body.isAzione() ) )
			);	
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RICHIEDENTE,
				evalnull(  () -> ServletUtils.boolToCheckBoxStatus( body.isRichiedente() ) )
			);
		
		if(body.getToken()!=null && !body.getToken().isEmpty()) {
			
			wrap.overrideParameter(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN,
					evalnull(  () -> ServletUtils.boolToCheckBoxStatus( true ) )
				);
			
			boolean modiPdnd = applicativa && org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(protocollo);
			
			List<String> values = new ArrayList<>();
			for (RateLimitingPolicyGroupByTokenClaimEnum tokenClaim : body.getToken()) {
				switch (tokenClaim) {
				case SUBJECT:
					values.add(TipoCredenzialeMittente.TOKEN_SUBJECT.getRawValue());
					break;
				case ISSUER:
					values.add(TipoCredenzialeMittente.TOKEN_ISSUER.getRawValue());
					break;
				case CLIENT_ID:
					values.add(TipoCredenzialeMittente.TOKEN_CLIENT_ID.getRawValue());
					break;
				case USERNAME:
					values.add(TipoCredenzialeMittente.TOKEN_USERNAME.getRawValue());
					break;
				case EMAIL:
					values.add(TipoCredenzialeMittente.TOKEN_EMAIL.getRawValue());
					break;
					
				case PDND_ORGANIZATION_NAME:
					if(!modiPdnd) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il criterio di raggruppamento per nome organizzazione PDND è utilizzabile solamente per erogazioni con profilo di interoperabilità ModI");
					}
					values.add(TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.getRawValue());
					break;	
				case PDND_EXTERNAL_ID:
					if(!modiPdnd) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il criterio di raggruppamento per identificativo esterno dell'organizzazione è utilizzabile solamente per erogazioni con profilo di interoperabilità ModI");
					}
					values.add(TipoCredenzialeMittente.PDND_ORGANIZATION_EXTERNAL_ID.getRawValue());
					break;	
				case PDND_CONSUMER_ID:
					if(!modiPdnd) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il criterio di raggruppamento per consumerId dell'organizzazione PDND è utilizzabile solamente per erogazioni con profilo di interoperabilità ModI");
					}
					values.add(TipoCredenzialeMittente.PDND_ORGANIZATION_CONSUMER_ID.getRawValue());
					break;	
				default:
					break;
				}
			}
			if(!values.isEmpty()) {
				wrap.overrideParameterValues(
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN_CLAIMS,
						values.toArray(new String[1])
					);
			}
		}		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED,
				evalnull(  () -> ServletUtils.boolToCheckBoxStatus( body.getChiaveTipo() != null ) )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO,
				evalnull( () -> Enums.tipoFiltroApplicativo.get(body.getChiaveTipo()).toString() )
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME,
				evalnull( () -> body.getChiaveNome() )
			); 
	}
	
	public static final void override(TipoRisorsa tipoRisorsa, RateLimitingPolicyErogazioneUpdate body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override(tipoRisorsa, (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
				RuoloPolicy.APPLICATIVA.toString() 
		);
		
		// Campi in più rispetto al padre:
		RateLimitingPolicyFiltroErogazione filtro = body.getFiltro();
		override( filtro, idPropietarioSa, wrap );
		
		if(filtro!=null) {
			final String filtroFruitore = evalnull(() -> filtro.getSoggettoFruitore()) != null   
					? new IDSoggetto(idPropietarioSa.getTipo(), filtro.getSoggettoFruitore()).toString()
					: null;
			
			wrap.overrideParameter(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE,
					filtroFruitore 
				);
		}
		
		RateLimitingPolicyGroupBy groupCriteria = body.getRaggruppamento();
		override( groupCriteria, wrap,
				protocollo, true);

	}
	public static final void override(TipoRisorsa tipoRisorsa, RateLimitingPolicyErogazione body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override(tipoRisorsa, (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
				RuoloPolicy.APPLICATIVA.toString() 
		);
		
		// Campi in più rispetto al padre:
		RateLimitingPolicyFiltroErogazione filtro = body.getFiltro();
		override( filtro, idPropietarioSa, wrap );
		
		if(filtro!=null) {
			final String filtroFruitore = evalnull(() -> filtro.getSoggettoFruitore()) != null   
					? new IDSoggetto(idPropietarioSa.getTipo(), filtro.getSoggettoFruitore()).toString()
					: null;
			
			wrap.overrideParameter(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE,
					filtroFruitore 
				);
		}
		
		RateLimitingPolicyGroupBy groupCriteria = body.getRaggruppamento();
		override( groupCriteria, wrap,
				protocollo, true );

	}
	
	
	
	public static final void override (TipoRisorsa tipoRisorsa,  RateLimitingPolicyFruizioneUpdate body,  String protocollo, IDSoggetto idPropietarioSa,  HttpRequestWrapper wrap ) {	// Questa è in comune alla update.
		if (body == null) return;

		override ( tipoRisorsa, (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		override ( body.getFiltro(), idPropietarioSa, wrap );
		override ( body.getRaggruppamento() , wrap,
				protocollo, false );
		
		wrap.overrideParameter(
			ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
			RuoloPolicy.DELEGATA.toString() 
		);
	}
	public static final void override (TipoRisorsa tipoRisorsa,  RateLimitingPolicyFruizione body,  String protocollo, IDSoggetto idPropietarioSa,  HttpRequestWrapper wrap ) {	// Questa è in comune alla update.
		if (body == null) return;

		override ( tipoRisorsa, (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		override ( body.getFiltro(), idPropietarioSa, wrap );
		override ( body.getRaggruppamento() , wrap,
				protocollo, false  );
		
		wrap.overrideParameter(
			ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
			RuoloPolicy.DELEGATA.toString() 
		);
		
	}
		
	public static final void override (TipoRisorsa tipoRisorsa, RateLimitingPolicyBase body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {	// Questa è in comune alla update.		
		if (body == null) return;

		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS,
				body.getNome()
			);		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED,
				body.getStato().toString()
				);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_RIDEFINISCI,
				ServletUtils.boolToCheckBoxStatus( body.isSogliaRidefinita() )
			);
		
		
		if(body.isSogliaRidefinita()) {
			if(TipoRisorsa.DIMENSIONE_MASSIMA_MESSAGGIO.equals(tipoRisorsa)) {
				
				if(body.getSogliaDimensioneRichiesta()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Soglia relativa alla dimensione della richiesta non fornita");
				}
				if(body.getSogliaDimensioneRisposta()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Soglia relativa alla dimensione della risposta non fornita");
				}
				if(body.getSogliaValore()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Valore di Soglia non utilizzabile con una policy di tipo '"+RateLimitingCriteriMetricaEnum.DIMENSIONE_MASSIMA.toString()+"'");
				}
				
				wrap.overrideParameter(
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VALORE_2, 
						evalnull( () -> body.getSogliaDimensioneRichiesta().toString() )
					);
				
				wrap.overrideParameter(
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VALORE, 
						evalnull( () -> body.getSogliaDimensioneRisposta().toString() )
					);
				
			}
			else {
			
				if(body.getSogliaDimensioneRichiesta()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Soglia relativa alla dimensione della richiesta non utilizzabile con una policy di tipo diverso da '"+RateLimitingCriteriMetricaEnum.DIMENSIONE_MASSIMA.toString()+"'");
				}
				if(body.getSogliaDimensioneRisposta()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Soglia relativa alla dimensione della risposta non utilizzabile con una policy di tipo diverso da '"+RateLimitingCriteriMetricaEnum.DIMENSIONE_MASSIMA.toString()+"'");
				}
				if(body.getSogliaValore()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Valore di Soglia non fornito");
				}
				
				wrap.overrideParameter(
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VALORE, 
						evalnull( () -> body.getSogliaValore().toString() )
					);
				
			}
		}
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED,
				StatoFunzionalita.ABILITATO.toString() 
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO,
				protocollo 
			);
	}


	public static final boolean correlazioneApplicativaRichiestaCheckData(
			TipoOperazione tipoOp,
			HttpRequestWrapper wrap,
			ConsoleHelper paHelper,
			boolean isDelegata,
			CorrelazioneApplicativaRichiesta body,
			Long idPorta,
			Long idCorrelazione,
			org.openspcoop2.message.constants.ServiceBinding serviceBinding
			
		) throws Exception {
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID, idPorta.toString() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ELEMENTO_XML, body.getElemento() );		// Campo elemento oppure "Qualsiasi"
		
		wrap.overrideParameter( 
				CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA, 
				CorrelazioneApplicativaRichiestaIdentificazione.valueOf(body.getIdentificazioneTipo().name()).toString()
			);
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_PATTERN, body.getIdentificazione() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID_CORRELAZIONE, evalnull( () -> idCorrelazione.toString() ) );					// Questo va impostato nella update.
		
		return paHelper.correlazioneApplicativaRichiestaCheckData(tipoOp,isDelegata,
				serviceBinding);
		
	}
	
	
	public static final CorrelazioneApplicativaElemento convert(CorrelazioneApplicativaRichiesta body) {
		
		CorrelazioneApplicativaElemento cae = new CorrelazioneApplicativaElemento();
		cae.setNome(body.getElemento());
		cae.setIdentificazione(CorrelazioneApplicativaRichiestaIdentificazione.valueOf(body.getIdentificazioneTipo().name()));
		
		if ( body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.URL_BASED 
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.HEADER_BASED 
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.CONTENT_BASED
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.TEMPLATE
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.FREEMARKER_TEMPLATE
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.VELOCITY_TEMPLATE) {
			
			cae.setPattern(body.getIdentificazione());
		}
		
		if ( body.getIdentificazioneTipo() != CorrelazioneApplicativaRichiestaEnum.DISABILITATO ) {
			
			cae.setIdentificazioneFallita(body.isGenerazioneErroreIdentificazioneFallita()
					? CorrelazioneApplicativaGestioneIdentificazioneFallita.BLOCCA 
					: CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				);
		}
		return cae;
	}
	
		
	public static final CorrelazioneApplicativaRispostaElemento convert(CorrelazioneApplicativaRisposta body) {

		CorrelazioneApplicativaRispostaElemento cae = new CorrelazioneApplicativaRispostaElemento();
		cae.setNome(body.getElemento());
		cae.setIdentificazione(CorrelazioneApplicativaRispostaIdentificazione.valueOf(body.getIdentificazioneTipo().name()));
		
		if ( 	 body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.HEADER_BASED 
			||   body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.CONTENT_BASED
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.TEMPLATE
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.FREEMARKER_TEMPLATE
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.VELOCITY_TEMPLATE	) {
			
			cae.setPattern(body.getIdentificazione());
		}
		
		if ( body.getIdentificazioneTipo() != CorrelazioneApplicativaRispostaEnum.DISABILITATO ) {
			
			cae.setIdentificazioneFallita(body.isGenerazioneErroreIdentificazioneFallita()
					? CorrelazioneApplicativaGestioneIdentificazioneFallita.BLOCCA 
					: CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				);
		}
		return cae;
	}
	
	
	public static final boolean correlazioneApplicativaRispostaCheckData(
			TipoOperazione tipoOp,
			HttpRequestWrapper wrap,
			ConsoleHelper paHelper,
			boolean isDelegata,
			CorrelazioneApplicativaRisposta body,
			Long idPorta,
			Long idCorrelazione,
			org.openspcoop2.message.constants.ServiceBinding serviceBinding
		) throws Exception {
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID, idPorta.toString() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ELEMENTO_XML, body.getElemento() );		// Campo elemento oppure "Qualsiasi"
		
		wrap.overrideParameter( 
				CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA, 
				CorrelazioneApplicativaRichiestaIdentificazione.valueOf(body.getIdentificazioneTipo().name()).toString()
			);
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_PATTERN, body.getIdentificazione() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID_CORRELAZIONE, evalnull( () -> idCorrelazione.toString() ));
	
		
		return paHelper.correlazioneApplicativaRichiestaCheckData(TipoOperazione.ADD,isDelegata,
				serviceBinding);
		
	}



	public static final CorrelazioneApplicativaRichiesta convert( CorrelazioneApplicativaElemento  src) {
		CorrelazioneApplicativaRichiesta ret = new CorrelazioneApplicativaRichiesta();
		
		ret.setElemento( StringUtils.isEmpty(src.getNome())
				? "*"
				: src.getNome()
			);
		
		ret.setGenerazioneErroreIdentificazioneFallita(src.getIdentificazioneFallita() == CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				? false
				: true
			);
		
		ret.setIdentificazione(src.getPattern());
		ret.setIdentificazioneTipo(CorrelazioneApplicativaRichiestaEnum.valueOf(src.getIdentificazione().name()));
		
		return ret;
	}
	
	
	public static final CorrelazioneApplicativaRisposta convert( CorrelazioneApplicativaRispostaElemento  src) {
		CorrelazioneApplicativaRisposta ret = new CorrelazioneApplicativaRisposta();
		
		ret.setElemento( StringUtils.isEmpty(src.getNome())
				? "*"
				: src.getNome()
			);
		
		ret.setGenerazioneErroreIdentificazioneFallita(src.getIdentificazioneFallita() == CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				? false
				: true
			);
		
		ret.setIdentificazione(src.getPattern());
		ret.setIdentificazioneTipo(CorrelazioneApplicativaRispostaEnum.valueOf(src.getIdentificazione().name()));
		
		return ret;
	}
	
	public static final boolean isDumpConfigurazioneAbilitato(DumpConfigurazione configurazione, boolean isRisposta) {
		boolean abilitato = false;
		
		if(configurazione == null)
			return false;
		
		if(isRisposta) {
			DumpConfigurazioneRegola rispostaIngresso = configurazione.getRispostaIngresso();
			
			if(rispostaIngresso != null) {
				if(rispostaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
			
			DumpConfigurazioneRegola rispostaUscita = configurazione.getRispostaUscita();
			
			if(rispostaUscita != null) {
				if(rispostaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
		} else {
			DumpConfigurazioneRegola richiestaIngresso = configurazione.getRichiestaIngresso();
			
			if(richiestaIngresso != null) {
				if(richiestaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
			
			DumpConfigurazioneRegola richiestaUscita = configurazione.getRichiestaUscita();
			
			if(richiestaUscita != null) {
				if(richiestaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
		}
		
		return abilitato;
	}
	
	
	public static final void attivazionePolicyCheckData(
			TipoOperazione tipoOperazione, 
			PortaApplicativa pa,
			AttivazionePolicy policy,
			InfoPolicy infoPolicy,  
			ErogazioniConfEnv env,
			org.openspcoop2.message.constants.ServiceBinding serviceBinding,
			String modalita) throws Exception  {

		final RuoloPolicy ruoloPorta = RuoloPolicy.APPLICATIVA;
		final String nomePorta = pa.getNome();
		// Controllo che l'azione scelta per il filtro sia supportata.
		boolean hasAzioni = pa.getAzione() != null && pa.getAzione().getAzioneDelegataList().size() > 0;
		List<String> azioniSupportate = hasAzioni 
				    ? pa.getAzione().getAzioneDelegataList()
					: env.confCore.getAzioni(
						env.asps,
						env.apcCore.getAccordoServizioSintetico(env.asps.getIdAccordo()), 
						false, 
						true, 
						ErogazioniApiHelper.getAzioniOccupateErogazione(env.idAsps, env.apsCore, env.paCore)
					);
		
		if(policy.getFiltro().getAzione() != null && !policy.getFiltro().getAzione().isEmpty()) {
			String [] tmp = policy.getFiltro().getAzione().split(",");
			if(tmp!=null && tmp.length>0) {
				for (String azCheck : tmp) {
					if ( !azioniSupportate.contains(azCheck)) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'azione " + azCheck + " non è assegnabile a una policy di rate limiting per il gruppo scelto. le azioni supportare sono: " + azioniSupportate.toString());
					}
				}
			}
		}
		
		if(policy.getFiltro().getRuoloFruitore()!=null) {
			
			FiltroRicercaRuoli filtroRicercaRuoli = new FiltroRicercaRuoli();
			filtroRicercaRuoli.setTipologia(RuoloTipologia.INTERNO);
			List<IDRuolo> listIdRuoli = env.ruoliCore.getAllIdRuoli(filtroRicercaRuoli);
			List<String> ruoli = new ArrayList<>();
			if(listIdRuoli!=null && !listIdRuoli.isEmpty()) {
				for (IDRuolo idRuolo : listIdRuoli) {
					ruoli.add(idRuolo.getNome());
				}
			}
			
			if ( !ruoli.contains(policy.getFiltro().getRuoloFruitore())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo " + policy.getFiltro().getRuoloFruitore() + " non esiste.");
			}
		}
		
		// Controllo che l'applicativo fruitore scelto per il filtro sia supportato.
		if ( policy.getFiltro().getServizioApplicativoFruitore() != null &&				
				!env.confCore.getServiziApplicativiFruitore(env.tipo_protocollo, null, env.idSoggetto.getTipo(), env.idSoggetto.getNome())
				.stream()
				.filter( id -> id.getNome().equals(policy.getFiltro().getServizioApplicativoFruitore()))
				.findAny().isPresent()
		) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il servizio applicativo fruitore " + policy.getFiltro().getServizioApplicativoFruitore() + " scelto non è assegnabile alla policy di rate limiting");
		}
		
		org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = env.confCore.getConfigurazioneControlloTraffico();
		if (! env.confHelper.attivazionePolicyCheckData(new StringBuilder(), tipoOperazione, configurazioneControlloTraffico, 
				policy,infoPolicy, ruoloPorta, nomePorta, serviceBinding, modalita) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
	
		// Controllo che il soggetto fruitore scelto per il filtro sia supportato, SOLO IN EROGAZIONI
		if ( policy.getFiltro().getTipoFruitore() != null && policy.getFiltro().getNomeFruitore() != null ) {
			List<IDSoggetto> soggettiSupportati = new ArrayList<IDSoggetto>();
			
			boolean multitenant = env.confCore.getConfigurazioneGenerale().getMultitenant().getStato() == StatoFunzionalita.ABILITATO;
			PddCore pddCore = new PddCore(env.stationCore);
			List<IDSoggetto> listSoggetti = env.confCore.getSoggetti(env.tipo_protocollo, null);
	
			for (IDSoggetto idSoggetto : listSoggetti) {
				if(!multitenant && policy.getFiltro().getRuoloPorta()!=null && 
						(policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.APPLICATIVA) || policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.DELEGATA))) {
					
					Soggetto s = env.soggettiCore.getSoggettoRegistro(idSoggetto);
					boolean isPddEsterna = pddCore.isPddEsterna(s.getPortaDominio());
					if( policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.APPLICATIVA)) {
						// devo prendere i soggetti esterni
						if(isPddEsterna) {
							soggettiSupportati.add(idSoggetto);
						}
					}
					else {
						//policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.DELEGATA);
						// devo prendere i soggetti interni
						if(!isPddEsterna) {
							soggettiSupportati.add(idSoggetto);
						}
					}
				}
				else {
					soggettiSupportati.add(idSoggetto);
				}
			}
			
			final IDSoggetto idSoggettoScelto = new IDSoggetto(policy.getFiltro().getTipoFruitore(), policy.getFiltro().getNomeFruitore());
			if ( !soggettiSupportati.contains(idSoggettoScelto) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto fruitore " + idSoggettoScelto.toString() + " scelto non è assegnabile alla policy di rate limiting.\n I soggetti supportati sono: " + soggettiSupportati.toString() ); 
			}
		}
	
		
		
	}


	public static ApiCanale toApiCanale(ErogazioniEnv env, PortaApplicativa pa, AccordoServizioParteComuneSintetico apc, boolean throwException) throws Exception{
		return _toApiCanale(env, pa.getCanale(), apc, throwException);
	}
	public static ApiCanale toApiCanale(ErogazioniEnv env, PortaDelegata pd, AccordoServizioParteComuneSintetico apc, boolean throwException) throws Exception{
		return _toApiCanale(env, pd.getCanale(), apc, throwException);
	}
	private static ApiCanale _toApiCanale(ErogazioniEnv env, String canalePorta, AccordoServizioParteComuneSintetico apc, boolean throwException) throws Exception{
		ApiCanale canale = null;
		if(env.gestioneCanali) {
			canale = new ApiCanale();
			if(canalePorta!=null && !"".equals(canalePorta)) {
				canale.setNome(canalePorta);
				canale.setConfigurazione(CanaleEnum.IMPLEMENTAZIONE_API);
			}
			else if(apc.getCanale()!=null && !"".equals(apc.getCanale())) {
				canale.setNome(apc.getCanale());
				canale.setConfigurazione(CanaleEnum.API);
			}
			else {
				canale.setNome(env.canaleDefault);
				canale.setConfigurazione(CanaleEnum.DEFAULT);
			}
		}
		else {
			if(throwException) {
				throw new Exception("Gestione dei canali non abilitata");
			}
		}
		return canale;
	}
	
	
	
	public static final void setFiltroApiImplementata(String uriApiImplementata, int idLista, ConsoleSearch ricerca, ErogazioniEnv env) throws Exception {
		//  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione
		String pattern1 = "^[a-z]{2,20}/[0-9A-Za-z]+:[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$";
		String pattern2 = "^[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$";
		try {
			IDAccordo idAccordo = null;
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			if(RegularExpressionEngine.isMatch(uriApiImplementata, pattern1)) {
				idAccordo = idAccordoFactory.getIDAccordoFromUri(uriApiImplementata);
			}
			else if(RegularExpressionEngine.isMatch(uriApiImplementata, pattern2)) {
				String uriCompleto = env.idSoggetto.getTipo()+"/"+env.idSoggetto.getNome()+":"+uriApiImplementata;
				idAccordo = idAccordoFactory.getIDAccordoFromUri(uriCompleto);
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La uri fornita '"+uriApiImplementata+"' non rispetta il formato atteso '"+pattern1+"|"+pattern2+"'");
			}
			ricerca.addFilter(idLista, Filtri.FILTRO_API, idAccordoFactory.getUriFromIDAccordo(idAccordo));
		}catch(RegExpNotFoundException e) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("La uri fornita '"+uriApiImplementata+"' non rispetta il formato atteso '"+pattern1+"|"+pattern2+"': "+e.getMessage());
		}
	}
	
	public static boolean isConnettoreApplicativoServer(IdServizio idAsps, IDServizio idServizioFromAccordo, ErogazioniEnv env,
			String gruppo) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, CoreException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return isConnettoreApplicativoServer(getServizioApplicativo(idAsps, idServizioFromAccordo, env, gruppo));
	}
	
	public static boolean isConnettoreApplicativoServer(ServizioApplicativo sa) {
		return sa.getTipo() != null && sa.getTipo().equals(CostantiConfigurazione.SERVER);
	}
	
	public static void addInfoTokenPolicyForModI(Connettore regConnettore, ErogazioniEnv env, 
			boolean checkEsistenzaTokenPolicy) throws Exception {
		if(regConnettore.getPropertyList()!=null && regConnettore.getPropertyList().size()>0) {
			for (Property p : regConnettore.getPropertyList()) {
				if(CostantiConnettori.CONNETTORE_TOKEN_POLICY.equals(p.getNome())){
					if(p.getValore()!=null && StringUtils.isNotEmpty(p.getValore())) {
						
						if(checkEsistenzaTokenPolicy) {
							try {
								env.configCore.getGenericProperties(p.getValore(), CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, false);
							}catch(DriverConfigurazioneNotFound notFound) {
								throw FaultCode.RICHIESTA_NON_VALIDA
									.toException("Token policy di negoziazione '"+p.getValore()+"' non esistente");
							}
							
						}
						
						env.requestWrapper.overrideParameter(Costanti.CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY_VIA_API, p.getValore());
					}
				}
			}
		}
	}
}
