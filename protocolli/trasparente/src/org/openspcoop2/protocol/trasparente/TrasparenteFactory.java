/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


import org.openspcoop2.protocol.basic.BasicEmptyRawContentFactory;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
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
		TrasparenteProperties properties = TrasparenteProperties.getInstance(log);
		properties.validaConfigurazione(configPdD.getLoader());
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public TrasparenteBustaBuilder createBustaBuilder() throws ProtocolException {
		return new TrasparenteBustaBuilder(this);
	}

		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public TrasparenteValidazioneSintattica createValidazioneSintattica()
			throws ProtocolException {
		return new TrasparenteValidazioneSintattica(this);
	}


	
	/* ** CONFIG ** */
	
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return new TrasparenteProtocolManager(this);
	}
	
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		return new TrasparenteProtocolVersionManager(this,version);
	}

	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return new TrasparenteProtocolConfiguration(this);
	}

	
	/* ** CONSOLE ** */
	
	@Override
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException{
		if(TrasparenteProperties.getInstance(getLogger()).isUtilizzaTestSuiteProtocolProperties())
			return new TrasparenteTestsuiteDynamicConfiguration(this);
		else return super.createDynamicConfigurationConsole();
	}
	
}
