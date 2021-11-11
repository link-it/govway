/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.as4;


import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.protocol.as4.archive.AS4Archive;
import org.openspcoop2.protocol.as4.builder.AS4BustaBuilder;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.config.AS4ProtocolConfiguration;
import org.openspcoop2.protocol.as4.config.AS4ProtocolManager;
import org.openspcoop2.protocol.as4.config.AS4ProtocolVersionManager;
import org.openspcoop2.protocol.as4.properties.AS4DynamicConfiguration;
import org.openspcoop2.protocol.as4.validator.AS4ValidazioneSintattica;
import org.openspcoop2.protocol.basic.BasicFactory;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.state.IState;
import org.slf4j.Logger;


/**
 * Factory del protocollo AS4
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4Factory extends BasicFactory<SOAPElement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246311511752079753L;

	/* ** INIT ** */
	
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		super.init(log,protocol,configPdD,manifest);
		AS4Properties.initialize(configPdD.getConfigurationDir(),log);
		AS4Properties properties = AS4Properties.getInstance();
		properties.validaConfigurazione(configPdD.getLoader());
		
		BasicStaticInstanceConfig staticInstanceConfig = properties.getStaticInstanceConfig();
		super.initStaticInstance(staticInstanceConfig);
		if(staticInstanceConfig!=null) {
			if(staticInstanceConfig.isStaticConfig()) {
				staticInstanceProtocolManager = new AS4ProtocolManager(this);
				staticInstanceProtocolVersionManager = new HashMap<String, IProtocolVersionManager>();
				staticInstanceProtocolConfiguration = new AS4ProtocolConfiguration(this);
			}
		}
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public AS4BustaBuilder createBustaBuilder(IState state) throws ProtocolException {
		return new AS4BustaBuilder(this, state);
	}

		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public AS4ValidazioneSintattica createValidazioneSintattica(IState state)
			throws ProtocolException {
		return new AS4ValidazioneSintattica(this, state);
	}
	
	
	/* ** ARCHIVE ** */
	
	@Override
	public IArchive createArchive() throws ProtocolException{
		return new AS4Archive(this);
	}


	
	/* ** CONFIG ** */
	
	private static IProtocolManager staticInstanceProtocolManager = null;
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return staticInstanceProtocolManager!=null ? staticInstanceProtocolManager : new AS4ProtocolManager(this);
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
			return new AS4ProtocolVersionManager(this,version);
		}
	}
	private synchronized void initProtocolVersionManager(String version) throws ProtocolException {
		if(!staticInstanceProtocolVersionManager.containsKey(version)) {
			staticInstanceProtocolVersionManager.put(version, new AS4ProtocolVersionManager(this,version));
		}
	}

	private static IProtocolConfiguration staticInstanceProtocolConfiguration = null;
	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return staticInstanceProtocolConfiguration!=null ? staticInstanceProtocolConfiguration : new AS4ProtocolConfiguration(this);
	}

	
	/* ** CONSOLE ** */
	
	@Override
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException{
		return new AS4DynamicConfiguration(this);
	}
}
