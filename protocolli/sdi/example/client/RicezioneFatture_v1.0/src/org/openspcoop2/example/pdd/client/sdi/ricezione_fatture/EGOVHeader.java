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
package org.openspcoop2.example.pdd.client.sdi.ricezione_fatture;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.openspcoop2.utils.xml.XMLUtils;

/**
 * EGOVHeader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EGOVHeader implements SOAPHandler<SOAPMessageContext> {

	public byte[] header;
	public EGOVHeader(byte[] header){
		this.header = header;
	}
	
    @Override
	public Set<QName> getHeaders() {
        return new TreeSet<QName>();
    }

    @Override
	public boolean handleMessage(SOAPMessageContext context) {
    	
    	Boolean outboundProperty = 
            (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        
        if (outboundProperty.booleanValue()) {
            try {
                SOAPEnvelope envelope = context.getMessage()
                        .getSOAPPart().getEnvelope();
                SOAPFactory factory = SOAPFactory.newInstance();
                SOAPElement egov = factory.createElement(XMLUtils.getInstance().newElement(this.header));
                SOAPHeader header = envelope.getHeader();
                if(header==null){
                	header = envelope.addHeader();
                }
                header.addChildElement(egov);
                
            } catch (Exception e) {
                System.out.println("Exception in handler: " + e);
            }
        } else {
            // inbound
        }
        return true;
    }

    @Override
	public boolean handleFault(SOAPMessageContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
	public void close(MessageContext context) {
        //
    }
}
