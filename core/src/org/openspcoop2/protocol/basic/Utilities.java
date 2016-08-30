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

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.xml.AbstractXMLUtils;

/**	
 * Utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {

	private static OpenSPCoop2MessageFactory msgFactory = OpenSPCoop2MessageFactory.getMessageFactory();
	private static AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
	
	public static String toString(Logger log,SOAPElement soapElement)
			throws ProtocolException {
		if(soapElement == null){
			throw new ProtocolException("Conversione in element non riuscita");
		}
		try{
			String xml = Utilities.msgFactory.createMessage(SOAPVersion.SOAP11).getAsString(soapElement,true);
			if(xml==null){
				xml = Utilities.xmlUtils.toString(soapElement,true);
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

	public static byte[] toByteArray(Logger log,SOAPElement soapElement)
			throws ProtocolException {
		if(soapElement == null){
			throw new ProtocolException("Conversione in element non riuscita");
		}
		try{
			byte[] xml = Utilities.msgFactory.createMessage(SOAPVersion.SOAP11).getAsByte(soapElement,true);
			if(xml==null){
				xml = Utilities.xmlUtils.toByteArray(soapElement,true);
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

	
}
