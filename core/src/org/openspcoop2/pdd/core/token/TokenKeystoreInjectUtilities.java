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


package org.openspcoop2.pdd.core.token;

import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.pdd.core.connettori.ConnettoreBase;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseWithResponse;
import org.openspcoop2.pdd.core.controllo_traffico.PolicyTimeoutConfig;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutConfigurationUtils;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutContextParam;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaReadTimeout;
import org.openspcoop2.pdd.core.controllo_traffico.TimeoutNotifier;
import org.openspcoop2.pdd.core.controllo_traffico.TimeoutNotifierType;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.jose.JOSECostanti;
import org.openspcoop2.security.message.jose.JOSEUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**     
 * TokenKeystoreInjectUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenKeystoreInjectUtilities {
	
	private Logger log;
	private RequestInfo requestInfo;
	private IProtocolFactory<?> protocolFactory;
	private Context context;
	private IState state;
	private Busta busta;
	
	private boolean portaDelegata;
	private PortaDelegata pd;
	private PortaApplicativa pa;
	
	private PolicyTimeoutConfig policyTimeoutConfig;
	private int connectionTimeout = -1;
	private boolean connectionTimeoutConfigurazioneGlobale = true;
	private int readConnectionTimeout = -1;
	private boolean readConnectionTimeoutConfigurazioneGlobale = true;
	
	public TokenKeystoreInjectUtilities(Logger log, RequestInfo requestInfo,IProtocolFactory<?> protocolFactory,Context context,IState state,Busta busta) {
		this.log = log;
		this.requestInfo = requestInfo;
		this.protocolFactory = protocolFactory;
		this.context = context;
		this.state = state;
		this.busta = busta;
	}
		
	public void initTokenPolicyValidazioneJwt(String nomePolicy,boolean portaDelegata, PortaDelegata pd, PortaApplicativa pa, Properties p) {
		this.policyTimeoutConfig = new PolicyTimeoutConfig();
		this.policyTimeoutConfig.setPolicyValidazioneJwt(nomePolicy);
		init(portaDelegata, pd, pa, p);
	}
	public void initAttributeAuthorityValidazioneRispostaJwt(String nomePolicy,boolean portaDelegata, PortaDelegata pd, PortaApplicativa pa, Properties p) {
		this.policyTimeoutConfig = new PolicyTimeoutConfig();
		this.policyTimeoutConfig.setAttributeAuthorityResponseJwt(nomePolicy);
		init(portaDelegata, pd, pa, p);
	}
	private void init(boolean portaDelegata, PortaDelegata pd, PortaApplicativa pa, Properties p) {
		this.portaDelegata = portaDelegata;
		this.pd = pd;
		this.pa = pa;
		
		String trustStoreSslConnectionTimeoutPropertyName =  JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_CONNECTION_TIMEOUT;
		String trustStoreSslReadTimeoutPropertyName =  JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_READ_TIMEOUT;
		String trustStoreSslConnectionTimeoutProperty =  p.getProperty(trustStoreSslConnectionTimeoutPropertyName);
		String trustStoreSslReadTimeoutProperty =  p.getProperty(trustStoreSslReadTimeoutPropertyName);
		if(trustStoreSslConnectionTimeoutProperty!=null && trustStoreSslReadTimeoutProperty!=null) {
			this.connectionTimeout = Integer.valueOf(trustStoreSslConnectionTimeoutProperty);
			this.readConnectionTimeout = Integer.valueOf(trustStoreSslReadTimeoutProperty);
			this.connectionTimeoutConfigurazioneGlobale = false;
			this.readConnectionTimeoutConfigurazioneGlobale = false;
		}
		else {
			this.connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			this.readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
		}
	}
	
	public void inject(Properties p) throws SecurityException {
		
		try {
			boolean throwError = true;
			Map<String,Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(this.busta, this.requestInfo, this.context, this.log);
			JOSEUtils.injectKeystore(this.requestInfo, dynamicMap, p, this.log, throwError); // serve per leggere il keystore dalla cache
		}catch(SecurityException e) {
			
			String msgErrore = ConnettoreBase.readConnectionExceptionMessageFromException(e);
			
			this.processConnectionTimeoutException(this.connectionTimeout, this.connectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			this.processReadTimeoutException(this.readConnectionTimeout, this.readConnectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			throw e;
		}
		
	}
	
    private void processReadTimeoutException(int timeout, boolean configurazioneGlobale, Exception e, String message) {
    	try {
	    	if(timeout>0 && ConnettoreBaseWithResponse.containsReadTimeoutException(e, message)) {
	      		TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.WAIT_RESPONSE);
	    		notifier.notify(timeout);
	    	}
    	}catch(Exception error) {
    		if(this.log!=null) {
    			this.log.error("Errore avvenuto durante la registrazione dell'evento di read timeout: "+error.getMessage(),error);
    		}
    	}
    }
    
    private void processConnectionTimeoutException(int timeout, boolean configurazioneGlobale, Exception e, String message) {
    	try {
	    	if(timeout>0 && ConnettoreBaseWithResponse.containsConnectionTimeoutException(e, message)) {
	      		TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.CONNECTION);
	    		notifier.notify(timeout);
	    	}
    	}catch(Exception error) {
    		if(this.log!=null) {
    			this.log.error("Errore avvenuto durante la registrazione dell'evento di connection timeout: "+error.getMessage(),error);
    		}
    	}
    }
	
    private TimeoutNotifier getTimeoutNotifier(int timeout, boolean configurazioneGlobale, TimeoutNotifierType type) throws DriverConfigurazioneException, ProtocolException {
		SogliaReadTimeout soglia = null;
		if(!this.portaDelegata) {
			soglia = (this.pa!=null) ? 
					ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, configurazioneGlobale, this.pa, null, this.policyTimeoutConfig,
							new ReadTimeoutContextParam(this.requestInfo, this.protocolFactory, this.context, this.state)) : 
						ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, false, this.protocolFactory);
		}
		else {
			soglia = (this.pd!=null) ? 
					ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, configurazioneGlobale, this.pd, this.policyTimeoutConfig,
							new ReadTimeoutContextParam(this.requestInfo, this.protocolFactory, this.context, this.state)) : 
						ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, true, this.protocolFactory);
		}
		boolean saveInContext = !(this.policyTimeoutConfig!=null && 
									(this.policyTimeoutConfig.getAttributeAuthority()!=null || this.policyTimeoutConfig.getAttributeAuthorityResponseJwt()!=null)
								);
		return new TimeoutNotifier(this.context, this.protocolFactory, 
				soglia, type, this.log, saveInContext);
	}
}
