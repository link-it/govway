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

package org.openspcoop2.message.soap;

import java.util.List;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPPart;

import org.apache.wss4j.common.WSS4JConstants;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.w3c.dom.Attr;

/**
 * Libreria contenente metodi utili per la gestione WSSecurity.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class WSSecurityUtils {

	public static SOAPElement getSecurityHeader(SOAPPart soapPart, MessageType msgType, String actor, boolean throwExceptionIfFoundMoreSecurityHeader) throws MessageException, MessageNotSupportedException {
        
		SOAPHeader hdr = null;
		try {
			hdr = soapPart!=null && soapPart.getEnvelope()!=null ?  soapPart.getEnvelope().getHeader() : null;
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
		if (hdr == null) {
            return null;
        }
		
		boolean soap12 = MessageType.SOAP_12.equals(msgType);
		
        String actorLocal = WSS4JConstants.ATTR_ACTOR;
        String soapNamespace = WSS4JConstants.URI_SOAP11_ENV;
        if (soap12) {
            actorLocal = WSS4JConstants.ATTR_ROLE;
            soapNamespace = WSS4JConstants.URI_SOAP12_ENV;
        }

        //
        // Iterate through the security headers
        //
        SOAPElement foundSecurityHeader = null;
        List<SOAPElement> childs = SoapUtils.getNotEmptyChildSOAPElement(hdr);
        if(childs!=null && !childs.isEmpty()) {
        	for (SOAPElement currentChild : childs) {
        		if (WSS4JConstants.WSSE_LN.equals(currentChild.getLocalName())
                        && (WSS4JConstants.WSSE_NS.equals(currentChild.getNamespaceURI())
                            || WSS4JConstants.OLD_WSSE_NS.equals(currentChild.getNamespaceURI()))) {

                        Attr attr = currentChild.getAttributeNodeNS(soapNamespace, actorLocal);
                        String hActor = (attr != null) ? attr.getValue() : null;

                        if (isActorEqual(actor, hActor)) {
                            if (foundSecurityHeader != null) {
                            	throw new MessageException("Two or more security headers have the same actor name: '"+actor+"'");
                            }
                            foundSecurityHeader = currentChild;
                            if(!throwExceptionIfFoundMoreSecurityHeader) {
                            	break; // un header trovato
                            }
                        }
                    }
			}
        }
        
        return foundSecurityHeader;
    }
	
	private static boolean isActorEqual(String actor, String hActor) {
        if ((hActor == null || hActor.length() == 0)
            && (actor == null || actor.length() == 0)) {
            return true;
        }

        if (hActor != null && actor != null && hActor.equalsIgnoreCase(actor)) {
            return true;
        }

        return false;
    }
	
}
