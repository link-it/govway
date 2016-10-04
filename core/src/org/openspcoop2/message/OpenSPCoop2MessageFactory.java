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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.Loader;
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

public abstract class OpenSPCoop2MessageFactory {
	public abstract String getEncryptedDataHeaderBlockClass();
	public abstract String getDocumentBuilderFactoryClass();
	public abstract String getProcessPartialEncryptedMessageClass();
	public abstract String getSignPartialMessageProcessorClass();
	private static Logger logger = LoggerWrapperFactory.getLogger(OpenSPCoop2MessageFactory.class);
	public static String messageFactoryImpl = org.openspcoop2.message.OpenSPCoop2MessageFactory_impl.class.getName();
	
	public static void setMessageFactoryImpl(String messageFactoryImpl) {
		if(messageFactoryImpl != null)
			OpenSPCoop2MessageFactory.messageFactoryImpl = messageFactoryImpl;
	}

	protected static OpenSPCoop2MessageFactory openspcoopMessageFactory = null;
	public static OpenSPCoop2MessageFactory getMessageFactory() {
		if(OpenSPCoop2MessageFactory.openspcoopMessageFactory == null)
			try { OpenSPCoop2MessageFactory.initMessageFactory(); } catch (Exception e) { throw new RuntimeException(e); }
		return OpenSPCoop2MessageFactory.openspcoopMessageFactory;
	}
	
	public static void initMessageFactory() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		initMessageFactory(false);
	}
	public static void initMessageFactory(boolean force) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(OpenSPCoop2MessageFactory.openspcoopMessageFactory==null || force){
			OpenSPCoop2MessageFactory.openspcoopMessageFactory = (OpenSPCoop2MessageFactory) Loader.getInstance().newInstance(OpenSPCoop2MessageFactory.messageFactoryImpl);
			//System.out.println("CREATOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO F("+force+") ["+OpenSPCoop2MessageFactory.openspcoopMessageFactory+"] ["+OpenSPCoop2MessageFactory.messageFactoryImpl+"]");
		}
//		else{
//			System.out.println("GIA ESISTEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE ["+OpenSPCoop2MessageFactory.openspcoopMessageFactory+"]");
//		}
	}
	
	protected static SOAPFactory soapFactory11 = null;
	protected static SOAPFactory soapFactory12 = null;
	public SOAPFactory getSoapFactory11(){
		if(OpenSPCoop2MessageFactory.soapFactory11==null){
			initSoapFactory();
		}
		return OpenSPCoop2MessageFactory.soapFactory11;
	}
	
	public SOAPFactory getSoapFactory12(){
		if(OpenSPCoop2MessageFactory.soapFactory12==null){
			initSoapFactory();
		}
		return OpenSPCoop2MessageFactory.soapFactory12;
	}
	protected abstract void initSoapFactory();
	
	
	protected static MessageFactory soapMessageFactory = null;
	public MessageFactory getSoapMessageFactory() throws SOAPException {
		if(OpenSPCoop2MessageFactory.soapMessageFactory==null){
			initSoapMessageFactory();
		}
		return OpenSPCoop2MessageFactory.soapMessageFactory;
	}
	protected abstract void initSoapMessageFactory() throws SOAPException; 
	
	protected final String getContentType(MimeHeaders headers)
    {
        String values[] = headers.getHeader(Costanti.CONTENT_TYPE);
        if(values == null)
            return null;
        else
            return values[0];
    }
	
	public abstract SOAPConnectionFactory getSOAPConnectionFactory() throws SOAPException;
	
	// Implementazione specifica per i SAAJ Vendors
	protected abstract OpenSPCoop2Message _createMessage(SOAPVersion versioneSoap);
	protected abstract OpenSPCoop2Message _createMessage(SOAPVersion versioneSoap,SOAPMessage msg);
	protected abstract OpenSPCoop2Message _createMessage(MimeHeaders mhs, InputStream is,  boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, long overhead) throws SOAPException, IOException;	
	
    public abstract Element convertoForXPathSearch(Element contenutoAsElement);
    
    public abstract void normalizeDocument(Document document);
    
	
	
	// ********** INTERNAL CREATE (chiamate dai metodi pubblici) *************
	
	private OpenSPCoop2MessageParseResult _internalCreateMessage(MimeHeaders mhs, InputStream is, NotifierInputStreamParams notifierInputStreamParams,
			boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, long overhead) {	
		
		OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
		try{
			InputStream nis = null;
			
			if(is==null){
				throw new Exception("Original InputStream undefined");
			}
			
			if(notifierInputStreamParams!=null){
				String [] headerContentType = null;
				if(mhs!=null){
					headerContentType = mhs.getHeader(Costanti.CONTENT_TYPE);
				}
				String contentType = null;
				if(headerContentType!=null && headerContentType.length>0){
					contentType = headerContentType[0];
				}
				else{
					contentType = Costanti.CONTENT_TYPE_SOAP_1_1;
				}
		
				nis = new NotifierInputStream(is,contentType,notifierInputStreamParams);
			}
			else{
				nis = is;
			}
			
			OpenSPCoop2Message op2Msg = this._createMessage(mhs, nis, fileCacheEnable, attachmentRepoDir, fileThreshold, overhead);
			if(op2Msg==null){
				throw new Exception("Create message failed");
			}
			if(notifierInputStreamParams!=null){
				op2Msg.setNotifierInputStream((NotifierInputStream)nis);
			}
			
			result.setMessage(op2Msg);
		}catch(Throwable t){
			result.setParseException(MessageUtils.buildParseException(t));
		}
		return result;
	}
	
	
	// ********** METODI PUBBLICI *************
		
	public OpenSPCoop2MessageParseResult createMessage(MimeHeaders mhs, InputStream is, NotifierInputStreamParams notifierInputStreamParams, 
			boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, long overhead) {	
		return _internalCreateMessage(mhs, is, notifierInputStreamParams,  fileCacheEnable, attachmentRepoDir, fileThreshold, overhead);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(HttpServletRequest req, NotifierInputStreamParams notifierInputStreamParams,  
			boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold) {
		return createMessage(req, notifierInputStreamParams, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(HttpServletRequest req, NotifierInputStreamParams notifierInputStreamParams, 
			boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, long overhead) {
		
		MimeHeaders mhs = null;
		InputStream is = null;
		try{
			
			mhs = new MimeHeaders();
			mhs.addHeader(Costanti.CONTENT_TYPE, req.getContentType());
			mhs.addHeader(Costanti.SOAP_ACTION, req.getHeader(Costanti.SOAP_ACTION));
			
			is = req.getInputStream();
			
			if(is==null){
				throw new Exception("Original InputStream undefined");
			}
			
		}catch(Throwable t){
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(MessageUtils.buildParseException(t));
			return result;
		}
		
		return _internalCreateMessage(mhs, is, notifierInputStreamParams,  fileCacheEnable, attachmentRepoDir, fileThreshold, overhead);
		
	}


	public OpenSPCoop2MessageParseResult createMessage(InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean isBodyStream, String contentTypeParam, String contentLocation,  boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold) {
		
		try{
			long diff = 0;
			
			if(messageInput==null){
				throw new Exception("Original InputStream undefined");
			}
			
			SOAPVersion soapVersion = SOAPVersion.getVersioneSoap(logger, contentTypeParam, true);
			
			if(isBodyStream){
				Vector<InputStream> streams = new Vector<InputStream> ();
				byte[] start = null, end = null;
				if(SOAPVersion.SOAP12.equals(soapVersion)){
					start = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body>".getBytes();
					end = "</SOAP-ENV:Body></SOAP-ENV:Envelope>".getBytes();
				} else {
					start = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>".getBytes();
					end = "</SOAP-ENV:Body></SOAP-ENV:Envelope>".getBytes();				
				}
				diff += start.length + end.length;
				streams.add(new ByteArrayInputStream(start));
				streams.add(messageInput);
				streams.add(new ByteArrayInputStream (end));
				messageInput = new SequenceInputStream (streams.elements());
			}
			MimeHeaders mhs = new MimeHeaders();
			mhs.addHeader(Costanti.CONTENT_TYPE, contentTypeParam);
			if(contentLocation != null) 
				mhs.addHeader(Costanti.CONTENT_LOCATION, contentLocation);
			
			OpenSPCoop2MessageParseResult result = _internalCreateMessage(mhs, messageInput, notifierInputStreamParams, fileCacheEnable, attachmentRepoDir, fileThreshold, diff);
						
			// Verifico la costruzione del messaggio SOAP
			if(result.getMessage()!=null){
				try{
					result.getMessage().getSOAPPart().getEnvelope();
				} catch (Throwable soapException) {
					result.setMessage(null);
					result.setParseException(MessageUtils.buildParseException(soapException));
				}
			}
			
			return result;
		}catch(Throwable t){
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(MessageUtils.buildParseException(t));
			return result;
		}
	}
	
	
	public OpenSPCoop2MessageParseResult createMessage(SOAPVersion versioneSoap, byte[] xml) {
		return this.createMessage(versioneSoap,xml,null);
	}
	public OpenSPCoop2MessageParseResult createMessage(SOAPVersion versioneSoap, byte[] xml,NotifierInputStreamParams notifierInputStreamParams) {
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(xml);
			MimeHeaders mhs = new MimeHeaders();
			if(versioneSoap.equals(SOAPVersion.SOAP12)){
				mhs.addHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_2);
			} else {
				mhs.addHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_1);
			}
			return _internalCreateMessage(mhs, bais, notifierInputStreamParams, false, null, null, 0);
		}catch(Throwable t){
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(MessageUtils.buildParseException(t));
			return result;
		}
	}
	
	public OpenSPCoop2MessageParseResult createMessage(SOAPVersion versioneSoap, String xml) {
		return this.createMessage(versioneSoap,xml,null);
	}
	public OpenSPCoop2MessageParseResult createMessage(SOAPVersion versioneSoap, String xml,NotifierInputStreamParams notifierInputStreamParams) {
		return createMessage(versioneSoap,xml.getBytes(),notifierInputStreamParams);
	}
	
	
	/*
	 * Messaggi
	 */
	
	public OpenSPCoop2Message createMessage(SOAPVersion versioneSoap){
		return this._createMessage(versioneSoap);
	}
	public OpenSPCoop2Message createMessage(SOAPVersion versioneSoap, SOAPMessage msg){
		return this._createMessage(versioneSoap,msg);
	}
	
	/*
	 * Messaggi vuoti
	 */
	
	public OpenSPCoop2Message createEmptySOAPMessage(SOAPVersion versioneSoap) {
		return this.createEmptySOAPMessage(versioneSoap,null);
	}
	public OpenSPCoop2Message createEmptySOAPMessage(SOAPVersion versioneSoap,NotifierInputStreamParams notifierInputStreamParams) {
		try{
			byte[] xml = null;
			MimeHeaders mhs = new MimeHeaders();
			if(versioneSoap.equals(SOAPVersion.SOAP12)){
				xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body/></SOAP-ENV:Envelope>".getBytes();
				mhs.addHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_2);
			} else {
				xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body/></SOAP-ENV:Envelope>".getBytes();
				mhs.addHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_1);
			}
			
			ByteArrayInputStream bais = new ByteArrayInputStream(xml);
			OpenSPCoop2MessageParseResult result =  _internalCreateMessage(mhs, bais, notifierInputStreamParams, false, null, null, xml.length);
			if(result.getParseException()!=null){
				// non dovrebbe succedere
				throw result.getParseException().getSourceException();
			}
			return result.getMessage();
		}
		catch(Throwable e){
			System.err.println("Exception non gestibile durante la creazione di un messaggio vuoto. " + e);
			e.printStackTrace(System.err);
		}
		return null;
		
	}
	
	/*
	 * Messaggi di Errore
	 */
	public OpenSPCoop2Message createFaultMessage(SOAPVersion versioneSoap, Throwable t) {
		return this.createFaultMessage(versioneSoap, t,null);
	}
	public OpenSPCoop2Message createFaultMessage(SOAPVersion versioneSoap, Throwable t,NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(versioneSoap, t.getMessage(),notifierInputStreamParams);
	}
	
	public OpenSPCoop2Message createFaultMessage(SOAPVersion versioneSoap, String errore) {
		return this.createFaultMessage(versioneSoap, errore,null);
	}
	public OpenSPCoop2Message createFaultMessage(SOAPVersion versioneSoap, String errore,NotifierInputStreamParams notifierInputStreamParams){
		try{
			String xml = null;
			MimeHeaders mhs = new MimeHeaders();
			if(versioneSoap.equals(SOAPVersion.SOAP12)){
				xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\">"
						+"<SOAP-ENV:Header/><SOAP-ENV:Body>"
						+"<SOAP-ENV:Fault>"
						+"<SOAP-ENV:Code><SOAP-ENV:Value>SOAP-ENV:Server</SOAP-ENV:Value></SOAP-ENV:Code>"
						+"<SOAP-ENV:Reason><SOAP-ENV:Text xml:lang=\"en-US\">" + errore + "</SOAP-ENV:Text></SOAP-ENV:Reason>"
						+"<SOAP-ENV:Role>"+org.openspcoop2.utils.Costanti.OPENSPCOOP2+"</SOAP-ENV:Role>"
						+"</SOAP-ENV:Fault>"
						+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
				mhs.addHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_2);
			} else {
				xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+"<SOAP-ENV:Header/><SOAP-ENV:Body>"
						+"<SOAP-ENV:Fault>"
						+"<faultcode>SOAP-ENV:Server</faultcode>"
						+"<faultstring>" + errore + "</faultstring>"
						+"<faultactor>"+org.openspcoop2.utils.Costanti.OPENSPCOOP2+"</faultactor>"
						+"</SOAP-ENV:Fault>"
						+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
				mhs.addHeader(Costanti.CONTENT_TYPE, Costanti.CONTENT_TYPE_SOAP_1_1);
			}
			
			//System.out.println("XML ["+versioneSoap+"] ["+xml+"]");
			
			byte[] xmlByte = xml.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(xmlByte);
			OpenSPCoop2MessageParseResult result =  _internalCreateMessage(mhs, bais, notifierInputStreamParams, false, null, null, xmlByte.length);
			if(result.getParseException()!=null){
				// non dovrebbe succedere
				throw result.getParseException().getSourceException();
			}
			return result.getMessage();
		}
		catch(Throwable e){
			System.err.println("Exception non gestibile durante la creazione di un SOAPFault. " + e);
			e.printStackTrace(System.err);
		}
		return null;
	}
	
	
	/*
	 * Utility per debugging. Prende uno Stream e lo porta in String
	 */
    public String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }
	

}
