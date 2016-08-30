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

package org.openspcoop2.protocol.basic;


import org.slf4j.Logger;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.basic.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.basic.builder.EsitoBuilder;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.basic.config.BasicTraduttore;
import org.openspcoop2.protocol.basic.diagnostica.XMLDiagnosticoBuilder;
import org.openspcoop2.protocol.basic.tracciamento.XMLTracciaBuilder;
import org.openspcoop2.protocol.basic.validator.ValidatoreErrori;
import org.openspcoop2.protocol.basic.validator.ValidazioneAccordi;
import org.openspcoop2.protocol.basic.validator.ValidazioneConSchema;
import org.openspcoop2.protocol.basic.validator.ValidazioneDocumenti;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.diagnostica.IDriverMsgDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.IMsgDiagnosticoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.tracciamento.IDriverTracciamento;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciamentoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.validator.IValidazioneAccordi;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;


/**	
 * BasicFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BasicFactory implements IProtocolFactory {
	private static final long serialVersionUID = 1L;
	
	private String protocol;
	protected Logger log;
	private ConfigurazionePdD configPdD;
	private Openspcoop2 manifest;

	/* ** INIT ** */
		
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		this.protocol = protocol;
		this.log = log;
		this.configPdD = configPdD;
		this.manifest = manifest;
	}
		
	
	/* ** INFO SERVIZIO ** */
	
	@Override
	public String getProtocol() {
		return this.protocol;
	}
	
	@Override
	public Logger getLogger() {
		return this.log;
	}
	
	@Override
	public ConfigurazionePdD getConfigurazionePdD() {
		return this.configPdD;
	}
	
	@Override
	public Openspcoop2 getManifest() {
		return this.manifest;
	}

	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IBustaBuilder createBustaBuilder() throws ProtocolException {
		return new BustaBuilder(this);
	}

	@Override
	public org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder createErroreApplicativoBuilder()  throws ProtocolException {
		return new ErroreApplicativoBuilder(this);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IEsitoBuilder createEsitoBuilder()  throws ProtocolException{
		return new EsitoBuilder(this);
	}
	
	
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidatoreErrori createValidatoreErrori()  throws ProtocolException{
		return new ValidatoreErrori(this);
	}

	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica createValidazioneSintattica() throws ProtocolException {
		return new ValidazioneSintattica(this);
	}

	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica createValidazioneSemantica()  throws ProtocolException {
		return new ValidazioneSemantica(this);
	}

	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema createValidazioneConSchema()  throws ProtocolException{
		return new ValidazioneConSchema(this);
	}
	
	@Override
	public IValidazioneDocumenti createValidazioneDocumenti()
			throws ProtocolException {
		return new ValidazioneDocumenti(this);
	}

	@Override
	public IValidazioneAccordi createValidazioneAccordi()
			throws ProtocolException {
		return new ValidazioneAccordi(this);
	}
	
	

	/* ** DIAGNOSTICI ** */

	@Override
	public IDriverMsgDiagnostici createDriverMSGDiagnostici() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici(this);
	}
	
	@Override
	public IMsgDiagnosticoOpenSPCoopAppender createMsgDiagnosticoOpenSPCoopAppender() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.diagnostica.MsgDiagnosticoOpenSPCoopAppenderDB(this);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.diagnostica.IXMLDiagnosticoBuilder createXMLDiagnosticoBuilder()  throws ProtocolException {
		return new XMLDiagnosticoBuilder(this);
	}

	
	/* ** TRACCE ** */
	
	@Override
	public IDriverTracciamento createDriverTracciamento() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento(this);
	}
	
	@Override
	public ITracciamentoOpenSPCoopAppender createTracciamentoOpenSPCoopAppender() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.tracciamento.TracciamentoOpenSPCoopAppenderDB(this);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder createXMLTracciaBuilder()  throws ProtocolException {
		return new XMLTracciaBuilder(this);
	}
	
	
	
	/* ** ARCHIVE ** */
	
	@Override
	public IArchive createArchive() throws ProtocolException{
		return new BasicArchive(this);
	}
	
	
	/* ** CONFIG ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.config.ITraduttore createTraduttore()  throws ProtocolException{
		return new BasicTraduttore(this);
	}


}