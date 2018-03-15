/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
	
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return new AS4ProtocolManager(this);
	}
	
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		return new AS4ProtocolVersionManager(this,version);
	}

	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return new AS4ProtocolConfiguration(this);
	}

	
	/* ** CONSOLE ** */
	
	@Override
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException{
		return new AS4DynamicConfiguration(this);
	}
}
