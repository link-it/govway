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

package org.openspcoop2.protocol.basic.validator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.utils.digest.IDigestReader;

/**
 * ValidazioneSemantica
 * 
 * @author Nardi Lorenzo (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ValidazioneSemantica implements
		org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica {

	protected IProtocolFactory protocolFactory;
	protected Logger log;
		
	public ValidazioneSemantica(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}

	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	@Override
	public boolean validazioneID(String id, IDSoggetto dominio,
			ProprietaValidazione proprietaValidazione) {
		return true;
	}

	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, IState state,
			ProprietaValidazione tipoValidazione, RuoloBusta tipoBusta)
			throws ProtocolException {
		return new ValidazioneSemanticaResult(null, null, null, null, null);
	}

	@Override
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg,SOAPEnvelope soapEnvelope,SOAPElement protocolHeader) throws ProtocolException{
		return null;
	}
	@Override
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg) throws ProtocolException{
		return null;
	}
}
