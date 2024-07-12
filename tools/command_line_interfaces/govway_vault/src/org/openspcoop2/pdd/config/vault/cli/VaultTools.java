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

package org.openspcoop2.pdd.config.vault.cli;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.security.ProviderUtils;
import org.slf4j.Logger;

/**
 *  VaultTools
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VaultTools {

	private static Logger logCore = LoggerWrapperFactory.getLogger(VaultTools.class);
	public static Logger getLogCore() {
		return logCore;
	}
	public static void logCoreDebug(String msg) {
		logCore.debug(msg);
	}
	public static void logCoreInfo(String msg) {
		logCore.info(msg);
	}
	public static void logCoreError(String msg, Exception e) {
		logCore.error(msg,e);
	}
	private static Logger logOutput = LoggerWrapperFactory.getLogger(VaultTools.class);
	public static void logOutput(String msg) {
		logOutput.info(msg);
	}
	
	public static void main(String[] args) throws CoreException {
		
		VaultOperationType opType = null;
		VaultUpdateConfig updateConfig = null;
		VaultEncDecConfig encDecConfig = null;
		String[] argsConfig = null;
		String utilizzoErrato = null;
		try {
		
			// Logger
			initLogger();
			logCore=LoggerWrapperFactory.getLogger("govway_vault.core");	
			logOutput=LoggerWrapperFactory.getLogger("govway_vault.output");	
			
			
			logCoreDebug("Raccolta parametri in corso...");
									
			// args
			utilizzoErrato = "Usage error: VaultTools <operationType> <options>\n"+
					"- <operationType>: "+VaultOperationType.UPDATE_CONFIG.getValue()+","+VaultOperationType.ENCRYPT.getValue()+","+VaultOperationType.DECRYPT.getValue()+"\n"+
					"- <options>: \n"+
					"\t- "+VaultOperationType.UPDATE_CONFIG.getValue()+": "+VaultUpdateConfig.getUsage()+"\n"+
					"\t- "+VaultOperationType.ENCRYPT.getValue()+": "+VaultEncDecConfig.getUsage()+"\n"+
					"\t- "+VaultOperationType.DECRYPT.getValue()+": "+VaultEncDecConfig.getUsage()+"";
			
		}
		catch(Exception t) {
			if(logCore!=null) {
				logCore.error(t.getMessage(),t);
			}
			throw new CoreException(t.getMessage(),t);
		}
			
		if(args.length<1 || args[0]==null) {
			throw new CoreException(utilizzoErrato);
		}
		
		try {
			opType = parseOperationType(utilizzoErrato, args);
			argsConfig = new String[args.length - 1];
	        System.arraycopy(args, 1, argsConfig, 0, args.length - 1);
		}
		catch(Exception t) {
			if(logCore!=null) {
				logCore.error(t.getMessage(),t);
			}
			throw new CoreException(t.getMessage(),t);
		}
		
		switch (opType) {
		case UPDATE_CONFIG:
			utilizzoErrato = "Usage error: update "+VaultUpdateConfig.getUsage();			
			updateConfig = new VaultUpdateConfig(argsConfig, utilizzoErrato);
			break;
		case ENCRYPT:
			utilizzoErrato = "Usage error: encrypt "+VaultEncDecConfig.getUsage();			
			encDecConfig = new VaultEncDecConfig(argsConfig, utilizzoErrato, true);
			break;
		case DECRYPT:
			utilizzoErrato = "Usage error: decrypt "+VaultEncDecConfig.getUsage();			
			encDecConfig = new VaultEncDecConfig(argsConfig, utilizzoErrato, false);
			break;
		}
		
		try {
			process(updateConfig, encDecConfig);
		}
		catch(Exception t) {
			if(logCore!=null) {
				logCore.error(t.getMessage(),t);
			}
			throw new CoreException(t.getMessage(),t);
		}

	}
	
	private static void process(VaultUpdateConfig updateConfig, VaultEncDecConfig encDecConfig) throws CoreException {
		logCoreDebug("Raccolta parametri terminata");
		
		// properties
		VaultProperties vaultProperties = VaultProperties.getInstance();
		/**String confDir = null;*/ // non sembra servire
		String protocolloDefault = vaultProperties.getProtocolloDefault();
		
		// Map (environment)
		initMap(vaultProperties);
		
		// Load Security Provider
		if(vaultProperties.isSecurityLoadBouncyCastleProvider()) {
			initBouncyCastle();
		}
		
		// inizializzo HSM Manager
		initHsm(vaultProperties);
		
		// inizializzo BYOK Manager
		BYOKManager byokManager = initBYOK(vaultProperties);
		
		// Secrets (environment)
		initSecrets(vaultProperties, byokManager);
		
		// Init GovWay
		logCoreDebug("Inizializzazione risorse libreria in corso...");
		
		initProtocolFactory(protocolloDefault);
		
		initExtendedInfoManager();
		
		logCoreDebug("Inizializzazione risorse libreria terminata");
		
		// Validazione configurazioni
		if(updateConfig!=null) {
			
			logCoreInfo("Aggiornamento informazioni sensibili in corso ...");
			
			updateConfig.validate(byokManager);
			VaultUpdateConfigUtilities utils = new VaultUpdateConfigUtilities(updateConfig);
			utils.process();
			
			logCoreInfo("Aggiornamento informazioni sensibili completato");
		}
		else if(encDecConfig!=null) {
			
			String op = encDecConfig.isEncode() ? "Cifratura" : "Decrifratura";
			logCoreInfo(op+" in corso ...");
			
			encDecConfig.validate(byokManager);
			VaultEncDecUtilities utils = new VaultEncDecUtilities(encDecConfig);
			utils.process();
			
			logCoreInfo(op+" completata");
		}
	}

	private static void initLogger() throws CoreException {
		Properties propertiesLog4j = new Properties();
		try (InputStream inPropLog4j = VaultTools.class.getResourceAsStream("/govway_vault.cli.log4j2.properties");){
			propertiesLog4j.load(inPropLog4j);
			LoggerWrapperFactory.setLogConfiguration(propertiesLog4j);
		} catch(java.lang.Exception e) {
			throw new CoreException("Impossibile leggere i dati dal file 'govway_vault.cli.log4j2.properties': "+e.getMessage());
		} 
	}
	private static VaultOperationType parseOperationType(String utilizzoErrato,String [] args) throws CoreException{
		VaultOperationType opType = null;
		try {
			opType = VaultOperationType.toEnumConstant(args[0].trim(), true);
		}catch(Exception e) {
			throw new CoreException(utilizzoErrato+"\nIl tipo di operazione indicato ("+args[0].trim()+") non è gestito, valori ammessi: "+
					VaultOperationType.UPDATE_CONFIG.getValue()+","+VaultOperationType.ENCRYPT.getValue()+","+VaultOperationType.DECRYPT.getValue());
		}
		return opType;
	}
	private static void initBouncyCastle() throws CoreException {
		try{
			ProviderUtils.addBouncyCastleAfterSun(true);
			logCoreInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	private static void initMap(VaultProperties loaderProperties) throws CoreException {
		try {
			String mapConfig = loaderProperties.getEnvMapConfig();
			if(StringUtils.isNotEmpty(mapConfig)) {
				logCoreInfo("Inizializzazione environment in corso...");
				MapProperties.initialize(logCore, mapConfig, loaderProperties.isEnvMapConfigRequired());
				MapProperties mapProperties = MapProperties.getInstance();
				mapProperties.initEnvironment();
				String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
						"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
						"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
						"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
				logCoreInfo(msgInit);
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione dell'ambiente",e);
		}
	}
	private static void initHsm(VaultProperties loaderProperties) throws CoreException {
		// inizializzo HSM Manager
		try {
			String hsmConfig = loaderProperties.getHSMConfigurazione();
			if(StringUtils.isNotEmpty(hsmConfig)) {
				logCoreInfo("Inizializzazione HSM in corso...");
				File f = new File(hsmConfig);
				HSMManager.init(f, loaderProperties.isHSMRequired(), logCore, false);
				HSMUtils.setHsmConfigurableKeyPassword(loaderProperties.isHSMKeyPasswordConfigurable());
				logCoreInfo("Inizializzazione HSM effettuata con successo");
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione del manager HSM",e);
		}
	}
	private static BYOKManager initBYOK(VaultProperties loaderProperties) throws CoreException {
		BYOKManager byokManager = null;
		try {
			String byokConfig = loaderProperties.getBYOKConfigurazione();
			if(StringUtils.isNotEmpty(byokConfig)) {
				logCoreInfo("Inizializzazione BYOK in corso...");
				File f = new File(byokConfig);
				BYOKManager.init(f, loaderProperties.isBYOKRequired(), logCore);
				byokManager = BYOKManager.getInstance();
				String msgInit = "Gestore BYOK inizializzato;"+
						"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
						"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
						"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription();
				logCoreInfo(msgInit);
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione del manager BYOK",e);
		}
		return byokManager;
	}
	private static void initSecrets(VaultProperties loaderProperties, BYOKManager byokManager) throws CoreException {
		try {
			String secretsConfig = loaderProperties.getBYOKEnvSecretsConfig();
			if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
				logCoreInfo("Inizializzazione secrets in corso...");
				
				Map<String, Object> dynamicMap = new HashMap<>();
				DynamicInfo dynamicInfo = new  DynamicInfo();
				DynamicUtils.fillDynamicMap(logCore, dynamicMap, dynamicInfo);
				
				BYOKMapProperties.initialize(logCore, secretsConfig, loaderProperties.isBYOKEnvSecretsConfigRequired(), 
						true, 
						dynamicMap, true);
				BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
				secretsProperties.initEnvironment();
				String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
						"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
				logCoreInfo(msgInit);
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione dell'ambiente (secrets)",e);
		}	
	}
	private static ConfigurazionePdD initProtocolFactory(String protocolloDefault) throws CoreException {
		ConfigurazionePdD configPdD = null;
		try {
			configPdD = new ConfigurazionePdD();
			configPdD.setAttesaAttivaJDBC(-1);
			configPdD.setCheckIntervalJDBC(-1);
			configPdD.setLoader(new org.openspcoop2.utils.resources.Loader(VaultTools.class.getClassLoader()));
			configPdD.setLog(logCore);
			ProtocolFactoryManager.initialize(logCore, configPdD,
					protocolloDefault);
		} catch (Exception e) {
			throw new CoreException("Errore (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
		}
		return configPdD;
	}
	private static void initExtendedInfoManager() throws CoreException {
		try{
			ExtendedInfoManager.initialize(new org.openspcoop2.utils.resources.Loader(VaultTools.class.getClassLoader()), null, null, null);
		}catch(Exception e){
			throw new CoreException("Inizializzazione [ExtendedInfoManager] fallita",e);
		}
	}

	private static void doError(String msg,Exception e) throws CoreException {
		String msgErrore = msg+": " + e.getMessage();
		logCoreError(msgErrore,e);
		throw new CoreException(msgErrore,e);
	}
	
}
