/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.protocol.spcoop;


import org.slf4j.Logger;
import org.openspcoop2.protocol.basic.BasicFactory;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.builder.IEsitoBuilder;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.diagnostica.IXMLDiagnosticoBuilder;
import org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneAccordi;
import org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica;
import org.openspcoop2.protocol.spcoop.archive.SPCoopArchive;
import org.openspcoop2.protocol.spcoop.builder.SPCoopBustaBuilder;
import org.openspcoop2.protocol.spcoop.builder.SPCoopErroreApplicativoBuilder;
import org.openspcoop2.protocol.spcoop.builder.SPCoopEsitoBuilder;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.protocol.spcoop.config.SPCoopProtocolConfiguration;
import org.openspcoop2.protocol.spcoop.config.SPCoopProtocolManager;
import org.openspcoop2.protocol.spcoop.config.SPCoopProtocolVersionManager;
import org.openspcoop2.protocol.spcoop.config.SPCoopTraduttore;
import org.openspcoop2.protocol.spcoop.diagnostica.SPCoopXMLDiagnosticoBuilder;
import org.openspcoop2.protocol.spcoop.tracciamento.SPCoopXMLTracciaBuilder;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidatoreErrori;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneAccordi;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneConSchema;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneDocumenti;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSemantica;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSintattica;


/**
 * Factory del protocollo SPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopFactory extends BasicFactory {

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
	
	
	/* ** INFO SERVIZIO ** */
	
	//public String getProtocol();
	//public Logger getLogger();
	//public ConfigurazionePdD getConfigurazionePdD();
	//public Openspcoop2 getManifest();
	// ereditato da BasicFactory
	
	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public IBustaBuilder createBustaBuilder() throws ProtocolException {
		return new SPCoopBustaBuilder(this);
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
	public IValidatoreErrori createValidatoreErrori() throws ProtocolException {
		return new SPCoopValidatoreErrori(this);
	}
	
	@Override
	public IValidazioneSintattica createValidazioneSintattica()
			throws ProtocolException {
		return new SPCoopValidazioneSintattica(this);
	}

	@Override
	public IValidazioneSemantica createValidazioneSemantica()
			throws ProtocolException {
		return new SPCoopValidazioneSemantica(this);
	}
	
	@Override
	public IValidazioneConSchema createValidazioneConSchema()
			throws ProtocolException {
		return new SPCoopValidazioneConSchema(this);
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
	
	//public IDriverMSGDiagnostici createDriverMSGDiagnostici() throws ProtocolException;
	//public IMsgDiagnosticoOpenSPCoopAppender createMsgDiagnosticoOpenSPCoopAppender() throws ProtocolException;
	// ereditato da BasicFactory
	
	@Override
	public IXMLDiagnosticoBuilder createXMLDiagnosticoBuilder()
			throws ProtocolException {
		return new SPCoopXMLDiagnosticoBuilder(this);
	}
	
	
	/* ** TRACCE ** */
	
	//public IDriverTracciamento createDriverTracciamento() throws ProtocolException;
	//public ITracciamentoOpenSPCoopAppender createTracciamentoOpenSPCoopAppender() throws ProtocolException;
	// ereditato da BasicFactory
	
	@Override
	public IXMLTracciaBuilder createXMLTracciaBuilder() throws ProtocolException {
		return new SPCoopXMLTracciaBuilder(this);
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
