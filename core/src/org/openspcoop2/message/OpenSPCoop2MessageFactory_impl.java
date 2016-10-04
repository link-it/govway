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

package org.openspcoop2.message;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Factory per la costruzione di messaggi OpenSPCoop2Message. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2MessageFactory_impl extends OpenSPCoop2MessageFactory {

	private static Logger logger = LoggerWrapperFactory.getLogger(OpenSPCoop2MessageFactory_impl.class);
	
	@Override
	public OpenSPCoop2Message _createMessage(SOAPVersion versioneSoap,SOAPMessage msg) {
		OpenSPCoop2Message omsg = null;
		if(SOAPVersion.SOAP11.equals(versioneSoap)){
			omsg = new OpenSPCoop2Message_11_impl(msg);
		} else {
			omsg = new OpenSPCoop2Message_12_impl(msg);
		}
		return omsg;
	}
	
	@Override
	public OpenSPCoop2Message _createMessage(SOAPVersion versioneSoap) {
		OpenSPCoop2Message msg = null;
		if(SOAPVersion.SOAP11.equals(versioneSoap)){
	        msg = new OpenSPCoop2Message_11_impl();
	        msg.setContentType(Costanti.CONTENT_TYPE_SOAP_1_1);
		} else {
			msg = new OpenSPCoop2Message_12_impl();
			msg.setContentType(Costanti.CONTENT_TYPE_SOAP_1_2);
		}
        return msg;
	}

	@Override
	protected OpenSPCoop2Message _createMessage(MimeHeaders mhs, InputStream is, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, long overhead) throws SOAPException, IOException {
		if ((mhs == null) || (getContentType(mhs) == null)) {
            mhs = new MimeHeaders();
            mhs.setHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_1);
		}
		
		OpenSPCoop2Message msg;
		
		String contentType = getContentType(mhs);
		SOAPVersion soapVersion = SOAPVersion.getVersioneSoap(logger, contentType, true);
		
		if(SOAPVersion.SOAP12.equals(soapVersion)){
			msg = new OpenSPCoop2Message_12_impl(mhs, is, fileCacheEnable, attachmentRepoDir, fileThreshold, overhead);
			((OpenSPCoop2Message_12_impl) msg).setLazyAttachments(false);
		} else {
			msg = new OpenSPCoop2Message_11_impl(mhs, is, fileCacheEnable, attachmentRepoDir, fileThreshold, overhead);
			((OpenSPCoop2Message_11_impl) msg).setLazyAttachments(false);
		}
		
        return msg;
	}

	@Override
	protected synchronized void initSoapFactory() {
		try{
            if(OpenSPCoop2MessageFactory.soapFactory11==null || OpenSPCoop2MessageFactory.soapFactory12==null){
            	OpenSPCoop2MessageFactory.soapFactory11 = new com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl();
            	OpenSPCoop2MessageFactory.soapFactory12 = new com.sun.xml.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl();
            }
        }catch(Exception e){
                System.out.println("ERRORE: "+e.getMessage());
        }
	}

	@Override
	protected synchronized void initSoapMessageFactory() throws SOAPException {
		if(OpenSPCoop2MessageFactory.soapMessageFactory==null){
            OpenSPCoop2MessageFactory.soapMessageFactory = new com.sun.xml.messaging.saaj.soap.MessageFactoryImpl();
		}
	}

	@Override
	public String getDocumentBuilderFactoryClass() {
		return com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl.class.getName();
	}

	@Override
	public SOAPConnectionFactory getSOAPConnectionFactory()
			throws SOAPException {
		return new com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory();
	}

	@Override
	public String getEncryptedDataHeaderBlockClass() {
		return com.sun.xml.wss.core.EncryptedDataHeaderBlock.class.getName();
	}

	@Override
	public String getProcessPartialEncryptedMessageClass() {
		return "org.openspcoop2.security.message.soapbox.ProcessPartialEncryptedMessage";
	}

	@Override
	public String getSignPartialMessageProcessorClass() {
		return "org.openspcoop2.security.message.soapbox.SignPartialMessageProcessor";
	}

    @Override
	public Element convertoForXPathSearch(Element contenutoAsElement){
    	return contenutoAsElement;
    }
    
    @Override
	public void normalizeDocument(Document document){
    	document.normalizeDocument();
    }
	
}
