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
package org.openspcoop2.example.pdd.server.sdi.ricevi_file;

import java.io.File;
import java.io.FileInputStream;
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

import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;

/**
 * EGOVHeader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EGOVHeader implements SOAPHandler<SOAPMessageContext> {

	public String headerFileName;
	public EGOVHeader(){
		
		try{
		
			java.util.Properties reader = new java.util.Properties();
			try{  
				reader.load(new FileInputStream("Server.properties")); 
			}catch(java.io.IOException e) {
				System.err.println("ERROR : "+e.toString());
				return;
			}
			
			this.headerFileName = reader.getProperty("busta");
			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public String getEGovHeader(){
		
		try{
					
			if(this.headerFileName!=null){
				java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat ("yyyy-MM-dd");
				java.text.SimpleDateFormat timeformat = new java.text.SimpleDateFormat ("HH:mm:ss.SSS");
				java.text.SimpleDateFormat dateidformat = new java.text.SimpleDateFormat ("yyyy-MM-dd");
				java.text.SimpleDateFormat timeidformat = new java.text.SimpleDateFormat ("HH:mm");
				String data = FileSystemUtilities.readFile(new File(this.headerFileName));
				data = data.replaceAll("@ID-DATE@", dateidformat.format(new java.util.Date()));
				data = data.replaceAll("@ID-TIME@", timeidformat.format(new java.util.Date()));
				data = data.replaceAll("@DATE@", dateformat.format(new java.util.Date()));
				data = data.replaceAll("@TIME@", timeformat.format(new java.util.Date()));
				return data;
			}
			else{
				return null;
			}
			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
    @Override
	public Set<QName> getHeaders() {
        return new TreeSet<QName>();
    }

    private String idEgov = null;
    
    @Override
	public boolean handleMessage(SOAPMessageContext context) {
    	
    	
    	Boolean outboundProperty = 
            (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	
    	if (outboundProperty.booleanValue()==false && this.headerFileName!=null) {
    		
    		try {
	    		SOAPEnvelope envelope = context.getMessage()
	                    .getSOAPPart().getEnvelope();
	            SOAPHeader header = envelope.getHeader();
	            XPathExpressionEngine engine = new XPathExpressionEngine();
	            DynamicNamespaceContext dnc = org.openspcoop2.message.DynamicNamespaceContextFactory.getInstance().getNamespaceContext(header);
	            this.idEgov = engine.getStringMatchPattern(header, dnc, "//{http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/}Identificatore/text()");
	            System.out.println("IDEGOV: "+this.idEgov);
    		 } catch (Exception e) {
                 System.out.println("Exception in handler: " + e);
             }
    	}
    	
        if (outboundProperty.booleanValue() && this.headerFileName!=null) {
            try {
            	
            	SOAPEnvelope envelope = context.getMessage()
                        .getSOAPPart().getEnvelope();
                SOAPFactory factory = SOAPFactory.newInstance();
                
                String data = this.getEGovHeader().replaceAll("@RIF_MSG@", this.idEgov);
                
                SOAPElement egov = factory.createElement(XMLUtils.getInstance().newElement(data.getBytes()));
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
