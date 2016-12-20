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

package org.openspcoop2.protocol.as4;


import org.openspcoop2.protocol.basic.BasicEmptyRawContentFactory;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.as4.builder.AS4BustaBuilder;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.config.AS4ProtocolConfiguration;
import org.openspcoop2.protocol.as4.config.AS4ProtocolManager;
import org.openspcoop2.protocol.as4.config.AS4ProtocolVersionManager;
import org.openspcoop2.protocol.as4.validator.AS4ValidazioneSintattica;
import org.slf4j.Logger;


/**
 * Factory del protocollo AS4
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12362 $, $Date: 2016-11-21 15:44:14 +0100 (Mon, 21 Nov 2016) $
 */
public class AS4Factory extends BasicEmptyRawContentFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246311511752079753L;

	/* ** INIT ** */
	
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		super.init(log,protocol,configPdD,manifest);
		AS4Properties.initialize(configPdD.getConfigurationDir(),log);
		AS4Properties properties = AS4Properties.getInstance(log);
		properties.validaConfigurazione(configPdD.getLoader());
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public AS4BustaBuilder createBustaBuilder() throws ProtocolException {
		return new AS4BustaBuilder(this);
	}

		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public AS4ValidazioneSintattica createValidazioneSintattica()
			throws ProtocolException {
		return new AS4ValidazioneSintattica(this);
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

	
}
