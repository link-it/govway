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

package org.openspcoop2.pdd.core.batch;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.SystemDate;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.slf4j.Logger;

/**
* Generator
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Generator {

	private static final String LOGGER_PREFIX = "govway.batch.runtime_repository.";
	
	public static void main(String[] args) throws UtilsException {

		StringBuilder bf = new StringBuilder();
		String [] tipi = TipoRuntimeRepository.toStringArray();
		if(tipi!=null) {
			for (String t : tipi) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append(t);
			}
		}
		String usage = "\n\nUse: generator.sh tipo\n\ttipo: "+bf.toString();
		
		if(args.length<=0) {
			throw new UtilsException("ERROR: tipo gestore non fornito"+usage);
		}
		
		String tipo = args[0].trim();
		TipoRuntimeRepository tipoGestoreArg = null;
		try {
			tipoGestoreArg = TipoRuntimeRepository.toEnumConstant(tipo, true);
		}catch(Exception e) {
			throw new UtilsException("ERROR: tipo gestore fornito ("+tipo+") sconosciuto"+usage);
		}

		List<TipoRuntimeRepository> listTipiDaProcessare = new ArrayList<>();
		if(TipoRuntimeRepository.ALL.equals(tipoGestoreArg)) {
			listTipiDaProcessare.add(TipoRuntimeRepository.MESSAGGI);
			listTipiDaProcessare.add(TipoRuntimeRepository.BUSTE);
		}
		else {
			listTipiDaProcessare.add(tipoGestoreArg);
		}
		
		try{
			Properties props = new Properties();
			try(FileInputStream fis = new FileInputStream(Generator.class.getResource("/batch-runtime-repository.log4j2.properties").getFile())){
				props.load(fis);
				LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
				LoggerWrapperFactory.setLogConfiguration(props);
			}
		}catch(Exception e) {
			throw new UtilsException("Impostazione logging fallita: "+e.getMessage());
		}
		
		GeneratorProperties generatorProperties = GeneratorProperties.getInstance();
		
		// Inizializzazione log di default
		Logger logStartup = null;
		TipoRuntimeRepository tipoGestoreDefault = null;
		if(listTipiDaProcessare!=null && !listTipiDaProcessare.isEmpty()) {
			tipoGestoreDefault = listTipiDaProcessare.get(0);
		}
		else {
			tipoGestoreDefault = TipoRuntimeRepository.MESSAGGI;
		}
		if(generatorProperties.isMessaggiDebug()) {
			logStartup = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+tipoGestoreDefault.getValue());
		}
		else {
			logStartup = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+tipoGestoreDefault.getValue()+".error");
		}
		
		// Map (environment)
		try {
			String mapConfig = generatorProperties.getEnvMapConfig();
			if(StringUtils.isNotEmpty(mapConfig)) {
				logStartup.info("Inizializzazione environment in corso...");
				MapProperties.initialize(logStartup, mapConfig, generatorProperties.isEnvMapConfigRequired());
				MapProperties mapProperties = MapProperties.getInstance();
				mapProperties.initEnvironment();
				String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
						"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
						"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
						"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
				logStartup.info(msgInit);
			}
		} catch (Exception e) {
			doError(logStartup, "Errore durante l'inizializzazione dell'ambiente",e);
		}
		
		// Load Security Provider
		logStartup.info("Inizializzazione security provider...");
		try {
			if(generatorProperties.isSecurityLoadBouncyCastleProvider()) {
				ProviderUtils.addBouncyCastleAfterSun(true);
				logStartup.info("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
			}
		} catch (Exception e) {
			doError(logStartup, "Errore durante l'inizializzazione dei security provider",e);
		}
		logStartup.info("Inizializzazione security provider effettuata con successo");
		
		// inizializzo HSM Manager
		try {
			String hsmConfig = generatorProperties.getHSMConfigurazione();
			if(StringUtils.isNotEmpty(hsmConfig)) {
				logStartup.info("Inizializzazione HSM in corso...");
				File f = new File(hsmConfig);
				HSMManager.init(f, generatorProperties.isHSMRequired(), logStartup, false);
				HSMUtils.setHsmConfigurableKeyPassword(generatorProperties.isHSMKeyPasswordConfigurable());
				logStartup.info("Inizializzazione HSM effettuata con successo");
			}
		} catch (Exception e) {
			doError(logStartup, "Errore durante l'inizializzazione del manager HSM",e);
		}
		
		// inizializzo BYOK Manager
		BYOKManager byokManager = null;
		try {
			String byokConfig = generatorProperties.getBYOKConfigurazione();
			if(StringUtils.isNotEmpty(byokConfig)) {
				logStartup.info("Inizializzazione BYOK in corso...");
				File f = new File(byokConfig);
				BYOKManager.init(f, generatorProperties.isBYOKRequired(), logStartup);
				byokManager = BYOKManager.getInstance();
				
				logStartup.info("Inizializzazione BYOK effettuata con successo");
			}
		} catch (Exception e) {
			doError(logStartup, "Errore durante l'inizializzazione del manager BYOK",e);
		}
		
		// Secrets (environment)
		try {
			String secretsConfig = generatorProperties.getBYOKEnvSecretsConfig();
			if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
				logStartup.info("Inizializzazione secrets in corso...");
				String securityPolicy = BYOKManager.getSecurityEngineGovWayInstance();
				String securityRemotePolicy = BYOKManager.getSecurityRemoteEngineGovWayInstance();
				
				Map<String, Object> dynamicMap = new HashMap<>();
				DynamicInfo dynamicInfo = new  DynamicInfo();
				DynamicUtils.fillDynamicMap(logStartup, dynamicMap, dynamicInfo);

				BYOKMapProperties.initialize(logStartup, secretsConfig, generatorProperties.isBYOKEnvSecretsConfigRequired(), 
						securityPolicy, securityRemotePolicy, 
						dynamicMap, true);
				BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
				secretsProperties.initEnvironment();
				String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
						"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
				logStartup.info(msgInit);
			}
		} catch (Exception e) {
			doError(logStartup, "Errore durante l'inizializzazione dell'ambiente (secrets)",e);
		}		
		
		// Inizializza restanti proprietà
		generatorProperties.initProperties();
		
		int refreshConnection = generatorProperties.getRefreshConnessione();
		int scadenzaMessaggi = generatorProperties.getScadenzaMessaggiMinuti();
		
		String tipoRepositoryBuste = generatorProperties.getRepositoryBuste();
		boolean useDataRegistrazione = generatorProperties.isUseDataRegistrazione();
		
		boolean debug = generatorProperties.isMessaggiDebug();
		boolean logQuery = generatorProperties.isMessaggiLogQuery();
		
		int finestraSecondi = generatorProperties.getMessaggiFinestraSecondi();
		
		for (TipoRuntimeRepository tipoGestore : listTipiDaProcessare) {
					
			Logger logCore = null;
			Logger logSql = null;
			if(debug) {
				logCore = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+tipoGestore.getValue());
			}
			else {
				logCore = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+tipoGestore.getValue()+".error");
			}
			if(logQuery) {
				logSql = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+tipoGestore.getValue()+".sql");
			}
			else {
				logSql = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+tipoGestore.getValue()+".sql.error");
			}
			
			try {
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new Loader(Generator.class.getClassLoader()));
				configPdD.setLog(logCore);
				ProtocolFactoryManager.initialize(logCore, configPdD,
						generatorProperties.getProtocolloDefault());
				
				DateManager.initializeDataManager(SystemDate.class.getName(), new Properties(), LoggerWrapperFactory.getLogger(Generator.class));
				
			} catch (Exception e) {
				throw new UtilsException("Errore durante la gestione del repository '"+tipoGestore+"' (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
			}
			
			try{
				switch (tipoGestore) {
				case MESSAGGI:{
					GestoreMessaggi gestore = new GestoreMessaggi(debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, scadenzaMessaggi, tipoRepositoryBuste);
					gestore.process();
					break;
				}
				case BUSTE:{
					GestoreBuste gestore = new GestoreBuste(debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, tipoRepositoryBuste, useDataRegistrazione);
					gestore.process();
					break;
				}
				case ALL:{
					throw new UtilsException("Tipo '"+tipoGestore+"' non supportato");
				}
				}
			}catch(Exception e){
				throw new UtilsException("Errore durante la gestione del repository '"+tipoGestore+"': "+e.getMessage(),e);
			}
		
		}
		
	}

	private static void doError(Logger logCore,String msg,Exception e) throws UtilsException {
		String msgErrore = msg+": " + e.getMessage();
		logCore.error(msgErrore,e);
		throw new UtilsException(msgErrore,e);
	}
}
