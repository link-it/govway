/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.tracciamento;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.apache.log4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Element;

/**
 * XMLTracciaBuilder
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLTracciaBuilder implements org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder {
	
	protected Logger log;
	protected IProtocolFactory factory;
	protected OpenSPCoop2MessageFactory fac = null;
	protected AbstractXMLUtils xmlUtils;
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}
	
	public XMLTracciaBuilder(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.factory = factory;
		this.fac = OpenSPCoop2MessageFactory.getMessageFactory();
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
	}

	@Override
	public SOAPElement toElement(Traccia tracciaObject)
			throws ProtocolException {
		try{			
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = tracciaObject.getTraccia();
			
			// xml
			if(tracciaBase.getBustaXml()==null){
				if(tracciaObject.getBustaAsByteArray()!=null)
					tracciaBase.setBustaXml(new String(tracciaObject.getBustaAsByteArray()));
				else if(tracciaObject.getBustaAsElement()!=null){
					OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11);
					tracciaBase.setBustaXml(msg.getAsString(tracciaObject.getBustaAsElement(), false));
				}
			}
			
			byte[]xmlTraccia = org.openspcoop2.core.tracciamento.utils.XMLUtils.generateTraccia(tracciaBase);
			Element elementTraccia = this.xmlUtils.newElement(xmlTraccia);
			
			SOAPFactory sf = SoapUtils.getSoapFactory(SOAPVersion.SOAP11);
			SOAPElement traccia =  sf.createElement(elementTraccia);
					
			return  traccia;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
			throw new ProtocolException("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
		}
	}

	@Override
	public String toString(Traccia traccia) throws ProtocolException {
		SOAPElement tracciamento = this.toElement(traccia);	
		return Utilities.toString(this.log, tracciamento);
	}

	@Override
	public byte[] toByteArray(Traccia traccia) throws ProtocolException {
		SOAPElement tracciamento = this.toElement(traccia);	
		return Utilities.toByteArray(this.log, tracciamento);
	}

}