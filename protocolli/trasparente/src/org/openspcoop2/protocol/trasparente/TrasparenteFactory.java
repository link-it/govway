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

package org.openspcoop2.protocol.trasparente;


import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.protocol.basic.BasicEmptyRawContentFactory;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.trasparente.properties.TrasparenteTestsuiteDynamicConfiguration;
import org.openspcoop2.protocol.trasparente.builder.TrasparenteBustaBuilder;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProperties;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProtocolConfiguration;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProtocolManager;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProtocolVersionManager;
import org.openspcoop2.protocol.trasparente.validator.TrasparenteValidazioneSintattica;
import org.slf4j.Logger;


/**
 * Factory del protocollo Trasparente
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteFactory extends BasicEmptyRawContentFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246311511752079753L;
	
	/* ** INIT ** */
	
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		super.init(log,protocol,configPdD,manifest);
		TrasparenteProperties.initialize(configPdD.getConfigurationDir(),log);
		TrasparenteProperties properties = TrasparenteProperties.getInstance();
		properties.validaConfigurazione(configPdD.getLoader());
		
		BasicStaticInstanceConfig staticInstanceConfig = properties.getStaticInstanceConfig();
		super.initStaticInstance(staticInstanceConfig);
		if(staticInstanceConfig!=null) {
			if(staticInstanceConfig.isStaticConfig()) {
				staticInstanceProtocolManager = new TrasparenteProtocolManager(this);
				staticInstanceProtocolVersionManager = new HashMap<String, IProtocolVersionManager>();
				staticInstanceProtocolConfiguration = new TrasparenteProtocolConfiguration(this);
			}
		}
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public TrasparenteBustaBuilder createBustaBuilder(IState state) throws ProtocolException {
		return new TrasparenteBustaBuilder(this,state);
	}

		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public TrasparenteValidazioneSintattica createValidazioneSintattica(IState state)
			throws ProtocolException {
		return new TrasparenteValidazioneSintattica(this,state);
	}


	
	/* ** CONFIG ** */
	
	private static IProtocolManager staticInstanceProtocolManager = null;
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return staticInstanceProtocolManager!=null ? staticInstanceProtocolManager : new TrasparenteProtocolManager(this);
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
			return new TrasparenteProtocolVersionManager(this,version);
		}
	}
	private synchronized void initProtocolVersionManager(String version) throws ProtocolException {
		if(!staticInstanceProtocolVersionManager.containsKey(version)) {
			staticInstanceProtocolVersionManager.put(version, new TrasparenteProtocolVersionManager(this,version));
		}
	}

	private static IProtocolConfiguration staticInstanceProtocolConfiguration = null;
	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return staticInstanceProtocolConfiguration!=null ? staticInstanceProtocolConfiguration : new TrasparenteProtocolConfiguration(this);
	}

	
	/* ** CONSOLE ** */
	
	@Override
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException{
		if(TrasparenteProperties.getInstance().isUtilizzaTestSuiteProtocolProperties())
			return new TrasparenteTestsuiteDynamicConfiguration(this);
		else return super.createDynamicConfigurationConsole();
	}
	
}
