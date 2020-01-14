/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa;


import org.openspcoop2.protocol.basic.BasicFactory;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.modipa.builder.ModIBustaBuilder;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.config.ModIProtocolConfiguration;
import org.openspcoop2.protocol.modipa.config.ModIProtocolManager;
import org.openspcoop2.protocol.modipa.config.ModIProtocolVersionManager;
import org.openspcoop2.protocol.modipa.properties.ModIDynamicConfiguration;
import org.openspcoop2.protocol.modipa.tracciamento.ModITracciaSerializer;
import org.openspcoop2.protocol.modipa.validator.ModIValidazioneSemantica;
import org.openspcoop2.protocol.modipa.validator.ModIValidazioneSintattica;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.slf4j.Logger;


/**
 * Factory del protocollo ModI
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIFactory extends BasicFactory<AbstractModISecurityToken<?>> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4246311511752079753L;

	/* ** INIT ** */
	
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		super.init(log,protocol,configPdD,manifest);
		ModIProperties.initialize(configPdD.getConfigurationDir(),log);
		ModIProperties properties = ModIProperties.getInstance();
		properties.validaConfigurazione(configPdD.getLoader());
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public ModIBustaBuilder createBustaBuilder(IState state) throws ProtocolException {
		return new ModIBustaBuilder(this,state);
	}

		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public ModIValidazioneSintattica createValidazioneSintattica(IState state)
			throws ProtocolException {
		return new ModIValidazioneSintattica(this,state);
	}
	
	@Override
	public IValidazioneSemantica createValidazioneSemantica(IState state) throws ProtocolException {
		return new ModIValidazioneSemantica(this, state);
	}

	
	/* ** TRACCE ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer createTracciaSerializer()  throws ProtocolException {
		return new ModITracciaSerializer(this);
	}

	
	/* ** CONFIG ** */
	
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return new ModIProtocolManager(this);
	}
	
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		return new ModIProtocolVersionManager(this,version);
	}

	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return new ModIProtocolConfiguration(this);
	}

	
	/* ** CONSOLE ** */
	
	@Override
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException{
		return new ModIDynamicConfiguration(this);
	}
	
}
