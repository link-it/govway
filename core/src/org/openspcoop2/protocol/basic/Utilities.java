/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**	
 * Utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {

	public static String toString(OpenSPCoop2MessageFactory messageFactory, SOAPElement soapElement,boolean consume)
			throws ProtocolException {
		if(soapElement == null){
			throw new ProtocolException("Conversione in element non riuscita");
		}
		try{
			MessageXMLUtils xmlUtils = MessageXMLUtils.getInstance(messageFactory);
			String xml = OpenSPCoop2MessageFactory.getAsString(messageFactory, soapElement,consume);
			if(xml==null){
				xml = xmlUtils.toString(soapElement,true);
				if(xml==null){
					throw new Exception("Conversione in stringa non riuscita");
				}
			}
			return xml;
		}catch(ProtocolException pe){
			throw pe;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	public static byte[] toByteArray(OpenSPCoop2MessageFactory messageFactory, SOAPElement soapElement,boolean consume)
			throws ProtocolException {
		if(soapElement == null){
			throw new ProtocolException("Conversione in element non riuscita");
		}
		try{
			MessageXMLUtils xmlUtils = MessageXMLUtils.getInstance(messageFactory);
			byte[] xml = OpenSPCoop2MessageFactory.getAsByte(messageFactory, soapElement,consume);
			if(xml==null){
				xml = xmlUtils.toByteArray(soapElement,true);
				if(xml==null){
					throw new Exception("Conversione in bytes non riuscita");
				}
			}
			return xml;
		}catch(ProtocolException pe){
			throw pe;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}


	
	public static ServiceBinding convert(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding){
		switch (serviceBinding) {
		case SOAP:
			return ServiceBinding.SOAP;
		case REST:
			return ServiceBinding.REST;
		}
		return null;
	}
	public static ServiceBinding convert(org.openspcoop2.protocol.manifest.constants.ServiceBinding serviceBinding){
		switch (serviceBinding) {
		case SOAP:
			return ServiceBinding.SOAP;
		case REST:
			return ServiceBinding.REST;
		}
		return null;
	}
}
