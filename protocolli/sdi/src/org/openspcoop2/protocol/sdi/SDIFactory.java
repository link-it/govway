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

package org.openspcoop2.protocol.sdi;


import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.protocol.basic.BasicFactory;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdi.builder.SDIBustaBuilder;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.config.SDIProtocolConfiguration;
import org.openspcoop2.protocol.sdi.config.SDIProtocolManager;
import org.openspcoop2.protocol.sdi.config.SDIProtocolVersionManager;
import org.openspcoop2.protocol.sdi.config.SDITraduttore;
import org.openspcoop2.protocol.sdi.validator.SDIValidazioneConSchema;
import org.openspcoop2.protocol.sdi.validator.SDIValidazioneSemantica;
import org.openspcoop2.protocol.sdi.validator.SDIValidazioneSintattica;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.slf4j.Logger;


/**
 * Factory del protocollo SdI
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIFactory extends BasicFactory<SOAPElement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246311511752079753L;

	/* ** INIT ** */
	
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		super.init(log,protocol,configPdD,manifest);
		SDIProperties.initialize(configPdD.getConfigurationDir(),log);
		SDIProperties properties = SDIProperties.getInstance(log);
		properties.validaConfigurazione(configPdD.getLoader());
		
		BasicStaticInstanceConfig staticInstanceConfig = properties.getStaticInstanceConfig();
		super.initStaticInstance(staticInstanceConfig);
		if(staticInstanceConfig!=null) {
			if(staticInstanceConfig.isStaticConfig()) {
				staticInstanceProtocolManager = new SDIProtocolManager(this);
				staticInstanceProtocolVersionManager = new HashMap<>();
				staticInstanceTraduttore = new SDITraduttore(this);
				staticInstanceProtocolConfiguration = new SDIProtocolConfiguration(this);
			}
		}
	}
	

	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public SDIBustaBuilder createBustaBuilder(IState state) throws ProtocolException {
		return new SDIBustaBuilder(this,state);
	}
	
		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public SDIValidazioneSintattica createValidazioneSintattica(IState state)
			throws ProtocolException {
		return new SDIValidazioneSintattica(this, state);
	}

	@Override
	public IValidazioneSemantica createValidazioneSemantica(IState state)
			throws ProtocolException {
		return new SDIValidazioneSemantica(this, state);
	}
	
	@Override
	public IValidazioneConSchema createValidazioneConSchema(IState state)
			throws ProtocolException {
		return new SDIValidazioneConSchema(this, state);
	}
	
	
	/* ** CONFIG ** */
	
	private static IProtocolManager staticInstanceProtocolManager = null;
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return staticInstanceProtocolManager!=null ? staticInstanceProtocolManager : new SDIProtocolManager(this);
	}
	
	private static Map<String, IProtocolVersionManager> staticInstanceProtocolVersionManager = null;
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		if(staticInstanceProtocolVersionManager!=null) {
			if(!staticInstanceProtocolVersionManager.containsKey(version)) {
				initProtocolVersionManager(version);
			}
			return staticInstanceProtocolVersionManager.get(version);
		}
		else {
			return new SDIProtocolVersionManager(this,version);
		}
	}
	private synchronized void initProtocolVersionManager(String version) throws ProtocolException {
		if(!staticInstanceProtocolVersionManager.containsKey(version)) {
			staticInstanceProtocolVersionManager.put(version, new SDIProtocolVersionManager(this,version));
		}
	}

	private static ITraduttore staticInstanceTraduttore = null;
	@Override
	public ITraduttore createTraduttore() throws ProtocolException {
		return staticInstanceTraduttore!=null ? staticInstanceTraduttore : new SDITraduttore(this);
	}
	
	private static IProtocolConfiguration staticInstanceProtocolConfiguration = null;
	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return staticInstanceProtocolConfiguration!=null ? staticInstanceProtocolConfiguration : new SDIProtocolConfiguration(this);
	}

	
}
