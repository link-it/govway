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

package org.openspcoop2.protocol.basic.validator;

import java.util.Vector;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ValidazioneConSchema
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ValidazioneConSchema implements
		org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema {

	protected IProtocolFactory protocolFactory;
	protected Logger log;
		
	public ValidazioneConSchema(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}

	@Override
	public Vector<Eccezione> getEccezioniValidazione() {
		return new java.util.Vector<Eccezione>();
	}

	@Override
	public Vector<Eccezione> getEccezioniProcessamento() {
		return new java.util.Vector<Eccezione>();
	}

	@Override
	public void valida(OpenSPCoop2Message message,SOAPElement header, SOAPBody soapBody,
			boolean isErroreProcessamento,
			boolean isErroreIntestazione,
			boolean isMessaggioConAttachments, boolean validazioneManifestAttachments) throws ProtocolException {
		return;
	}

	@Override
	public boolean initialize() {
		return true;
	}

	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

}
