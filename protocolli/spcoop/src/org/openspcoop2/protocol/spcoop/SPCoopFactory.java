/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.protocol.spcoop;


import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.protocol.basic.BasicFactory;
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
	}
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public SPCoopBustaBuilder createBustaBuilder(IState state) throws ProtocolException {
		return new SPCoopBustaBuilder(this,state);
	}

	@Override
	public IErroreApplicativoBuilder createErroreApplicativoBuilder()
			throws ProtocolException {
		return new SPCoopErroreApplicativoBuilder(this);
	}
	
	@Override
	public IEsitoBuilder createEsitoBuilder() throws ProtocolException {
		return new SPCoopEsitoBuilder(this);
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
		return new SPCoopArchive(this);
	}
	
	
	
	/* ** CONFIG ** */
	
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return new SPCoopProtocolManager(this);
	}
	
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		return new SPCoopProtocolVersionManager(this,version);
	}

	@Override
	public ITraduttore createTraduttore() throws ProtocolException {
		return new SPCoopTraduttore(this);
	}
	
	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return new SPCoopProtocolConfiguration(this);
	}

	
}
