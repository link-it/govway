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
package org.openspcoop2.protocol.engine.utils;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ModIPDNDClientConfig;
import org.openspcoop2.protocol.sdk.ModIPDNDOrganizationConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.slf4j.Logger;

/**
 * ModITestUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModITestUtils {

	// lo scopo è verificare che i metodi esistano
	
	public static void main(String[] args) throws ProtocolException {
		verify();
	}
	
	public static void verify() throws ProtocolException {
		
		Logger log = LoggerWrapperFactory.getLogger(ModITestUtils.class);
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
		ConfigurazionePdD configPdD = new ConfigurazionePdD();
		configPdD.setLog(log);
		ProtocolFactoryManager.initializeSingleProtocol(log, configPdD, Costanti.MODIPA_PROTOCOL_NAME);
		
		testModIProperties(log);
		
		getHeaderModI(log);
		
		isTokenOAuthUseJtiIntegrityAsMessageId(log);
		
		getAPIPDNDClientConfig(log);
		getAPIPDNDOrganizationConfig(log);

		buildSignalHubPushIdAPI(log);
		isSignalHubEnabled(log);
		isTracingPDNDEnabled(log);
		
		getRemoteStoreConfig(log);
		getRemoteKeyType(log);
		
		getSicurezzaMessaggioCertificatiTrustStore(log);
		getSicurezzaMessaggioSslTrustStore(log);
		getSicurezzaMessaggioCertificatiKeyStore(log);
	}

	private static void testModIProperties(Logger log) throws ProtocolException {
		log.info("test ModIProperties ...");
		Object o = ModIUtils.getModiProperties();
		if(o == null) {
			throw new ProtocolException("ModiIProperties undefined");
		}
		if(!o.getClass().getName().equals(ModIUtils.CLASS_MODIPA_PROPERTIES)) {
			throw new ProtocolException("ModiIProperties class wrong, expected ["+(ModIUtils.CLASS_MODIPA_PROPERTIES+"] found ["+o.getClass().getName()+"]"));
		}
		log.info("test ModIProperties ok");
	}
	
	private static void getHeaderModI(Logger log) throws ProtocolException {
		log.info("test getHeaderModI ...");
		String hdr = ModIUtils.getHeaderModI();
		if(hdr == null) {
			throw new ProtocolException("ModiIProperties.getHeaderModI is null");
		}
		log.info("test getHeaderModI ok");
	}
	
	private static void isTokenOAuthUseJtiIntegrityAsMessageId(Logger log) throws ProtocolException {
		log.info("test isTokenOAuthUseJtiIntegrityAsMessageId ...");
		ModIUtils.isTokenOAuthUseJtiIntegrityAsMessageId();
		log.info("test isTokenOAuthUseJtiIntegrityAsMessageId ok");
	}
	
	private static void getAPIPDNDClientConfig(Logger log) throws ProtocolException {
		log.info("test getAPIPDNDClientConfig ...");
		ModIPDNDClientConfig c = ModIUtils.getAPIPDNDClientConfig();
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDClientConfig is null");
		}
		c = ModIUtils.getAPIPDNDClientConfig(log);
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDClientConfig(log) is null");
		}
		String details = "{\"id\":\"1\"}";
		c = ModIUtils.getAPIPDNDClientConfig(details);
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDClientConfig is null");
		}
		if(!details.equals(c.getDetails())) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDClientConfig.details different");
		}
		c = ModIUtils.getAPIPDNDClientConfig(details, log);
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDClientConfig(log) is null");
		}
		if(!details.equals(c.getDetails())) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDClientConfig(log).details different");
		}
		log.info("test getAPIPDNDClientConfig ok");
	}
	
	private static void getAPIPDNDOrganizationConfig(Logger log) throws ProtocolException {
		log.info("test getAPIPDNDOrganizationConfig ...");
		ModIPDNDOrganizationConfig c = ModIUtils.getAPIPDNDOrganizationConfig();
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDOrganizationConfig is null");
		}
		c = ModIUtils.getAPIPDNDOrganizationConfig(log);
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDOrganizationConfig(log) is null");
		}
		String details = "{\"id\":\"1\"}";
		c = ModIUtils.getAPIPDNDOrganizationConfig(details);
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDOrganizationConfig is null");
		}
		if(!details.equals(c.getDetails())) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDOrganizationConfig.details different");
		}
		c = ModIUtils.getAPIPDNDOrganizationConfig(details, log);
		if(c==null) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDOrganizationConfig(log) is null");
		}
		if(!details.equals(c.getDetails())) {
			throw new ProtocolException("ModiIProperties.getAPIPDNDOrganizationConfig(log).details different");
		}
		log.info("test getAPIPDNDOrganizationConfig ok");
	}
	
	private static void buildSignalHubPushIdAPI(Logger log) throws ProtocolException {
		log.info("test buildSignalHubPushIdAPI ...");
		IDAccordo idAccordo = ModIUtils.buildSignalHubPushIdAPI(new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, "Test"));
		if(idAccordo == null) {
			throw new ProtocolException("ModiIProperties.buildSignalHubPushIdAPI is null");
		}
		log.info("test buildSignalHubPushIdAPI ok");
	}
	
	private static void isSignalHubEnabled(Logger log) throws ProtocolException {
		log.info("test isSignalHubEnabled ...");
		ModIUtils.isSignalHubEnabled();
		log.info("test isSignalHubEnabled ok");
	}
	
	private static void isTracingPDNDEnabled(Logger log) throws ProtocolException {
		log.info("test isTracingPDNDEnabled ...");
		ModIUtils.isTracingPDNDEnabled();
		log.info("test isTracingPDNDEnabled ok");
	}
	
	private static void getRemoteStoreConfig(Logger log) throws ProtocolException {
		log.info("test getRemoteStoreConfig ...");
		List<RemoteStoreConfig> l = ModIUtils.getRemoteStoreConfig();
		if(l == null) {
			throw new ProtocolException("ModiIProperties.getRemoteStoreConfig is null");
		}
		log.info("test getRemoteStoreConfig ok");
	}
	
	private static void getRemoteKeyType(Logger log) throws ProtocolException {
		log.info("test getRemoteKeyType ...");
		ModIUtils.getRemoteKeyType("notExists");
		log.info("test getRemoteKeyType ok");
	}
	
	private static void getSicurezzaMessaggioCertificatiTrustStore(Logger log) throws ProtocolException {
		log.info("test getSicurezzaMessaggioCertificatiTrustStore ...");
		KeystoreParams kp = ModIUtils.getSicurezzaMessaggioCertificatiTrustStore();
		if(kp == null) {
			throw new ProtocolException("ModiIProperties.getSicurezzaMessaggioCertificatiTrustStore is null");
		}
		log.info("test getSicurezzaMessaggioCertificatiTrustStore ok");
	}
	
	private static void getSicurezzaMessaggioSslTrustStore(Logger log) throws ProtocolException {
		log.info("test getSicurezzaMessaggioSslTrustStore ...");
		/**KeystoreParams kp =*/ 
		ModIUtils.getSicurezzaMessaggioSslTrustStore();
		/** per default è vuotoif(kp == null) {
			throw new ProtocolException("ModiIProperties.getSicurezzaMessaggioSslTrustStore is null");
		}*/
		log.info("test getSicurezzaMessaggioSslTrustStore ok");
	}
	
	private static void getSicurezzaMessaggioCertificatiKeyStore(Logger log) throws ProtocolException {
		log.info("test getSicurezzaMessaggioCertificatiKeyStore ...");
		KeystoreParams kp = ModIUtils.getSicurezzaMessaggioCertificatiKeyStore();
		if(kp == null) {
			throw new ProtocolException("ModiIProperties.getSicurezzaMessaggioCertificatiKeyStore is null");
		}
		log.info("test getSicurezzaMessaggioCertificatiKeyStore ok");
	}
}
