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
package org.openspcoop2.core.statistiche.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.core.commons.PropertiesEnvUtils;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.slf4j.Logger;

/**
 * StatisticheServletListener
 *
 * Listener per l'inizializzazione e la distruzione del servizio di generazione statistiche.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheServletListener implements ServletContextListener {

	private static final String LOGGER_PREFIX = "govway.statistiche.server";
	private static final String LOG4J_PROPERTIES = "/statistiche-server.log4j2.properties";
	private static final String LOG4J_PROPERTIES_LOCAL = "/statistiche-server_local.log4j2.properties";
	private static final String LOG4J_PROPERTIES_LOCAL_VARIABLE = "STATISTICHE_SERVER_LOG_PROPERTIES";

	private Logger logCore = null;
	private StatisticheServerExecutor executor = null;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		try {
			// Inizializzazione logging
			initLogging();

			this.logCore.info("Inizializzazione servizio statistiche in corso...");

			// Inizializzazione properties
			StatisticheServerProperties serverProperties = StatisticheServerProperties.getInstance(this.logCore);

			// Map (environment)
			initMapProperties(serverProperties);

			// Load Security Provider
			initSecurityProvider(serverProperties);

			// HSM Manager
			initHSMManager(serverProperties);

			// BYOK Manager
			BYOKManager byokManager = initBYOKManager(serverProperties);

			// Secrets (environment)
			initSecrets(serverProperties, byokManager);

			// Inizializza restanti proprietà
			serverProperties.initProperties();

			// Inizializza Protocol Factory Manager
			initProtocolFactoryManager(serverProperties);

			// Aggiorna logger se debug abilitato
			if(serverProperties.isStatisticheGenerazioneDebug()) {
				this.logCore = LoggerWrapperFactory.getLogger(LOGGER_PREFIX);
			}

			// Avvia esecutor
			this.executor = new StatisticheServerExecutor(serverProperties);
			this.executor.start();

			this.logCore.info("Servizio statistiche inizializzato correttamente");

		} catch (Exception e) {
			String msg = "Errore durante l'inizializzazione del servizio statistiche: " + e.getMessage();
			if(this.logCore != null)
				this.logCore.error(msg, e);
			throw new IllegalStateException("Inizializzazione servizio statistiche fallita", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(this.logCore != null) {
			this.logCore.info("Arresto servizio statistiche in corso...");
		}

		try {
			if(this.executor != null) {
				this.executor.stop();
			}

			if(this.logCore != null) {
				this.logCore.info("Servizio statistiche arrestato correttamente");
			}
		} catch (Exception e) {
			String msg = "Errore durante l'arresto del servizio statistiche: " + e.getMessage();
			if(this.logCore != null) {
				this.logCore.error(msg, e);
			}
		}
	}


	private void initLogging() throws IllegalStateException, IOException, UtilsException {
		// Carica properties di base dal classpath
		Properties props = new Properties();
		try(InputStream is = StatisticheServletListener.class.getResourceAsStream(LOG4J_PROPERTIES)) {
			if(is == null) {
				throw new IllegalStateException("File '"+LOG4J_PROPERTIES+"' not found in classpath");
			}
			props.load(is);
		}

		// Cerca file locale di override (stesso ordine di ricerca delle properties)
		// 1. Variabile sistema/java STATISTICHE_SERVER_LOG_PROPERTIES
		// 2. OPENSPCOOP2_LOCAL_HOME/statistiche-server.log4j2.local.properties
		// 3. confDirectory/statistiche-server.log4j2.local.properties
		CollectionProperties localProps = PropertiesUtilities.searchLocalImplementation(
			Costanti.OPENSPCOOP2_LOCAL_HOME,
			LoggerWrapperFactory.getLogger(StatisticheServletListener.class),
			LOG4J_PROPERTIES_LOCAL_VARIABLE,
			LOG4J_PROPERTIES_LOCAL,
			null, // confDirectory non ancora disponibile, sarà usato OPENSPCOOP2_LOCAL_HOME
			true
		);

		// Sovrascrivi con properties locali se trovate
		if(localProps != null && localProps.size() > 0) {
			java.util.Enumeration<?> en = localProps.propertyNames();
			while(en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = localProps.get(key);
				if(value != null) {
					props.setProperty(key, value);
				}
			}
		}

		PropertiesEnvUtils.resolveGovWayEnvVariables(props);
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		LoggerWrapperFactory.setLogConfiguration(props);
		this.logCore = LoggerWrapperFactory.getLogger(LOGGER_PREFIX + ".error");
	}

	private void initMapProperties(StatisticheServerProperties serverProperties) throws UtilsException {
		String mapConfig = serverProperties.getEnvMapConfig();
		if(StringUtils.isNotEmpty(mapConfig)) {
			this.logCore.info("Inizializzazione environment in corso...");
			MapProperties.initialize(this.logCore, mapConfig, serverProperties.isEnvMapConfigRequired());
			MapProperties mapProperties = MapProperties.getInstance();
			mapProperties.initEnvironment();
			String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
					"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
					"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
					"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
					"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
					"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
			this.logCore.info(msgInit);
		}
	}

	private void initSecurityProvider(StatisticheServerProperties serverProperties) throws UtilsException  {
		this.logCore.info("Inizializzazione security provider...");
		if(serverProperties.isSecurityLoadBouncyCastleProvider()) {
			ProviderUtils.addBouncyCastleAfterSun(true);
			this.logCore.info("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
		}
		this.logCore.info("Inizializzazione security provider effettuata con successo");
	}

	private void initHSMManager(StatisticheServerProperties serverProperties) throws UtilsException {
		String hsmConfig = serverProperties.getHSMConfigurazione();
		if(StringUtils.isNotEmpty(hsmConfig)) {
			this.logCore.info("Inizializzazione HSM in corso...");
			File f = new File(hsmConfig);
			HSMManager.init(f, serverProperties.isHSMRequired(), this.logCore, false);
			HSMUtils.setHsmConfigurableKeyPassword(serverProperties.isHSMKeyPasswordConfigurable());
			this.logCore.info("Inizializzazione HSM effettuata con successo");
		}
	}

	private BYOKManager initBYOKManager(StatisticheServerProperties serverProperties) throws UtilsException {
		BYOKManager byokManager = null;
		String byokConfig = serverProperties.getBYOKConfigurazione();
		if(StringUtils.isNotEmpty(byokConfig)) {
			this.logCore.info("Inizializzazione BYOK in corso...");
			File f = new File(byokConfig);
			BYOKManager.init(f, serverProperties.isBYOKRequired(), this.logCore);
			byokManager = BYOKManager.getInstance();
			String msgInit = "Gestore BYOK inizializzato;"+
					"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
					"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
					"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription();
			this.logCore.info(msgInit);
		}
		return byokManager;
	}

	private void initSecrets(StatisticheServerProperties serverProperties, BYOKManager byokManager) throws UtilsException {
		String secretsConfig = serverProperties.getBYOKEnvSecretsConfig();
		if(byokManager != null && StringUtils.isNotEmpty(secretsConfig)) {
			this.logCore.info("Inizializzazione secrets in corso...");

			Map<String, Object> dynamicMap = new HashMap<>();
			DynamicInfo dynamicInfo = new DynamicInfo();
			DynamicUtils.fillDynamicMap(this.logCore, dynamicMap, dynamicInfo);

			BYOKMapProperties.initialize(this.logCore, secretsConfig, serverProperties.isBYOKEnvSecretsConfigRequired(),
					true,
					dynamicMap, true);
			BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
			secretsProperties.initEnvironment();
			String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
					"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
					"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
					"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
			this.logCore.info(msgInit);
		}
	}

	private void initProtocolFactoryManager(StatisticheServerProperties serverProperties) throws ProtocolException {
		this.logCore.info("Inizializzazione ProtocolFactoryManager in corso...");
		ConfigurazionePdD configPdD = new ConfigurazionePdD();
		configPdD.setAttesaAttivaJDBC(-1);
		configPdD.setCheckIntervalJDBC(-1);
		configPdD.setLoader(new Loader(StatisticheServletListener.class.getClassLoader()));
		configPdD.setLog(this.logCore);
		ProtocolFactoryManager.initialize(this.logCore, configPdD,
				serverProperties.getProtocolloDefault());
		this.logCore.info("Inizializzazione ProtocolFactoryManager effettuata con successo");
	}
}
