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

package org.openspcoop2.protocol.sdi;


import javax.xml.soap.SOAPElement;

import org.openspcoop2.protocol.basic.BasicFactory;
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
	}
	

	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public SDIBustaBuilder createBustaBuilder() throws ProtocolException {
		return new SDIBustaBuilder(this);
	}
	
		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public SDIValidazioneSintattica createValidazioneSintattica()
			throws ProtocolException {
		return new SDIValidazioneSintattica(this);
	}

	@Override
	public IValidazioneSemantica createValidazioneSemantica()
			throws ProtocolException {
		return new SDIValidazioneSemantica(this);
	}
	
	@Override
	public IValidazioneConSchema createValidazioneConSchema()
			throws ProtocolException {
		return new SDIValidazioneConSchema(this);
	}
	
	
	/* ** CONFIG ** */
	
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return new SDIProtocolManager(this);
	}
	
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		return new SDIProtocolVersionManager(this,version);
	}

	@Override
	public ITraduttore createTraduttore() throws ProtocolException {
		return new SDITraduttore(this);
	}
	
	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return new SDIProtocolConfiguration(this);
	}

	
}
