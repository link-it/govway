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

package org.openspcoop2.protocol.spcoop;


import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.protocol.basic.BasicFactory;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.builder.IEsitoBuilder;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneAccordi;
import org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.spcoop.archive.SPCoopArchive;
import org.openspcoop2.protocol.spcoop.builder.SPCoopBustaBuilder;
import org.openspcoop2.protocol.spcoop.builder.SPCoopErroreApplicativoBuilder;
import org.openspcoop2.protocol.spcoop.builder.SPCoopEsitoBuilder;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.protocol.spcoop.config.SPCoopProtocolConfiguration;
import org.openspcoop2.protocol.spcoop.config.SPCoopProtocolManager;
import org.openspcoop2.protocol.spcoop.config.SPCoopProtocolVersionManager;
import org.openspcoop2.protocol.spcoop.config.SPCoopTraduttore;
import org.openspcoop2.protocol.spcoop.diagnostica.SPCoopDiagnosticSerializer;
import org.openspcoop2.protocol.spcoop.tracciamento.SPCoopTracciaSerializer;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidatoreErrori;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneAccordi;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneConSchema;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneDocumenti;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSemantica;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSintattica;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;


/**
 * Factory del protocollo SPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopFactory extends BasicFactory<SOAPHeaderElement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246311511752079753L;

	/* ** INIT ** */
	
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		super.init(log,protocol,configPdD,manifest);
		SPCoopProperties.initialize(configPdD.getConfigurationDir(),log);
		SPCoopProperties properties = SPCoopProperties.getInstance(log);
		properties.validaConfigurazione(configPdD.getLoader());
		
		BasicStaticInstanceConfig staticInstanceConfig = properties.getStaticInstanceConfig();
		super.initStaticInstance(staticInstanceConfig);
		if(staticInstanceConfig!=null) {
			if(staticInstanceConfig.isStaticConfig()) {
				staticInstanceProtocolManager = new SPCoopProtocolManager(this);
				staticInstanceProtocolVersionManager = new HashMap<>();
				staticInstanceTraduttore = new SPCoopTraduttore(this);
				staticInstanceProtocolConfiguration = new SPCoopProtocolConfiguration(this);
			}
			if(staticInstanceConfig.isStaticErrorBuilder()) {
				staticInstanceErroreApplicativoBuilder = new SPCoopErroreApplicativoBuilder(this);
			}
			if(staticInstanceConfig.isStaticEsitoBuilder() && EsitiProperties.isInitializedProtocol(this.getProtocolMapKey())) {
				staticInstanceEsitoBuilder = new SPCoopEsitoBuilder(this);
			}
		}
	}
	@Override
	public void initStaticInstance() throws ProtocolException{
		super.initStaticInstance();
		if(this.staticInstanceConfig!=null && this.staticInstanceConfig.isStaticEsitoBuilder() && staticInstanceEsitoBuilder==null) {
			if(EsitiProperties.isInitializedProtocol(this.getProtocolMapKey())) {
				initStaticInstanceEsitoBuilder(this);
			}
		}
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public SPCoopBustaBuilder createBustaBuilder(IState state) throws ProtocolException {
		return new SPCoopBustaBuilder(this,state);
	}

	private static org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder staticInstanceErroreApplicativoBuilder = null;
	@Override
	public IErroreApplicativoBuilder createErroreApplicativoBuilder()
			throws ProtocolException {
		return staticInstanceErroreApplicativoBuilder!=null ? staticInstanceErroreApplicativoBuilder : new SPCoopErroreApplicativoBuilder(this);
	}
	
	private static org.openspcoop2.protocol.sdk.builder.IEsitoBuilder staticInstanceEsitoBuilder = null;
	private static synchronized void initStaticInstanceEsitoBuilder(SPCoopFactory spcoopFactory) throws ProtocolException {
		if(staticInstanceEsitoBuilder==null) {
			staticInstanceEsitoBuilder = new SPCoopEsitoBuilder(spcoopFactory);
		}
	}
	@Override
	public IEsitoBuilder createEsitoBuilder() throws ProtocolException {
		return staticInstanceEsitoBuilder!=null ? staticInstanceEsitoBuilder : new SPCoopEsitoBuilder(this);
	}
	
		
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public IValidatoreErrori createValidatoreErrori(IState state) throws ProtocolException {
		return new SPCoopValidatoreErrori(this,state);
	}
	
	@Override
	public SPCoopValidazioneSintattica createValidazioneSintattica(IState state)
			throws ProtocolException {
		return new SPCoopValidazioneSintattica(this,state);
	}

	@Override
	public IValidazioneSemantica createValidazioneSemantica(IState state)
			throws ProtocolException {
		return new SPCoopValidazioneSemantica(this,state);
	}
	
	@Override
	public IValidazioneConSchema createValidazioneConSchema(IState state)
			throws ProtocolException {
		return new SPCoopValidazioneConSchema(this,state);
	}

	@Override
	public IValidazioneDocumenti createValidazioneDocumenti()
			throws ProtocolException {
		return new SPCoopValidazioneDocumenti(this);
	}

	@Override
	public IValidazioneAccordi createValidazioneAccordi()
			throws ProtocolException {
		return new SPCoopValidazioneAccordi(this);
	}
	
	
	
	
	/* ** DIAGNOSTICI ** */
	
	@Override
	public IDiagnosticSerializer createDiagnosticSerializer()
			throws ProtocolException {
		return new SPCoopDiagnosticSerializer(this);
	}
	
	
	/* ** TRACCE ** */
	
	@Override
	public ITracciaSerializer createTracciaSerializer() throws ProtocolException {
		return new SPCoopTracciaSerializer(this);
	}
	
	
	/* ** ARCHIVE ** */
	
	@Override
	public IArchive createArchive() throws ProtocolException{
		return new SPCoopArchive(this.getLogger(), this);
	}
	
	
	
	/* ** CONFIG ** */
	
	private static IProtocolManager staticInstanceProtocolManager = null;
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return staticInstanceProtocolManager!=null ? staticInstanceProtocolManager : new SPCoopProtocolManager(this);
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
			return new SPCoopProtocolVersionManager(this,version);
		}
	}
	private synchronized void initProtocolVersionManager(String version) throws ProtocolException {
		if(!staticInstanceProtocolVersionManager.containsKey(version)) {
			staticInstanceProtocolVersionManager.put(version, new SPCoopProtocolVersionManager(this,version));
		}
	}

	private static ITraduttore staticInstanceTraduttore = null;
	@Override
	public ITraduttore createTraduttore() throws ProtocolException {
		return staticInstanceTraduttore!=null ? staticInstanceTraduttore : new SPCoopTraduttore(this);
	}
	
	private static IProtocolConfiguration staticInstanceProtocolConfiguration = null;
	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return staticInstanceProtocolConfiguration!=null ? staticInstanceProtocolConfiguration : new SPCoopProtocolConfiguration(this);
	}

	
}
