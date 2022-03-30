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

package org.openspcoop2.message.soap;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi SOAP 12
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_soap12_impl extends AbstractOpenSPCoop2Message_soap_impl<OpenSPCoop2Message_saaj_12_impl> {

	public OpenSPCoop2Message_soap12_impl(OpenSPCoop2MessageFactory messageFactory, MimeHeaders mhs, InputStream is, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		super(messageFactory, mhs, is, overhead, soapStreamReader);
	}
	
	@Override
	protected OpenSPCoop2Message_saaj_12_impl buildContent() throws MessageException{
		try{
			return buildContent(this.countingInputStream);
		}finally{
			try{
				this.countingInputStream.close();
			}catch(Exception eClose){}
		}
	}
	@Override
	protected OpenSPCoop2Message_saaj_12_impl buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException{
		try{
			if(contentBuffer.isSerializedOnFileSystem()) {
				try(InputStream is = new FileInputStream(contentBuffer.getSerializedFile())){
					return buildContent(is);
				}
			}
			else {
				try(InputStream is = new ByteArrayInputStream(contentBuffer.toByteArray())){
					return buildContent(is);
				}
			}
		}
		catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	protected OpenSPCoop2Message_saaj_12_impl buildContent(InputStream is) throws MessageException{
		try{
			//System.out.println("BUILD 12");
			OpenSPCoop2Message_saaj_12_impl msg = new OpenSPCoop2Message_saaj_12_impl(this.messageFactory, this.mhs, is);
			msg.initialize(this.overhead);
			msg.copyResourceFrom(this.soapCore);
			msg.setMessageRole(this.messageRole);
			msg.setMessageType(this.messageType);
			
			// Verifica struttura (in AbstractBaseOpenSPCoop2MessageDynamicContent verrà collezionato l'errore di parsing)
			// Servono tutti e 3 i comandi per far leggere tutto lo stream
			// Se si levano alcuni test falliscono
			SOAPHeader hdr = msg.getSOAPHeader(); 
			SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
			msg.countAttachments();
			
			this.addSoapHeaderModifiedIntoSoapReader(hdr, envelope);
			
			return msg;
		}catch(Throwable t){
			throw SoapUtils.buildMessageException("Unable to create envelope from given source: ",t);
		}
	}

	@Override
	protected String _getContentType() {
		String ct = super._getContentType();
		try {
			ContentType cType = new ContentType(ct);
			String soapActionValue = this.getSoapAction();
			if(soapActionValue!=null && StringUtils.isNotEmpty(soapActionValue)){
				String pSoapAction = cType.getParameter(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION);
				//System.out.println("PARAM["+pSoapAction+"] presente["+soapActionValue+"]");
				if(soapActionValue.equals(pSoapAction)) {
					//System.out.println("EQUALS");
					return ct;
				}
				else {
					if(soapActionValue.startsWith("\"") && soapActionValue.length()>1) {
						soapActionValue = soapActionValue.substring(1);
					}
					if(soapActionValue.endsWith("\"") && soapActionValue.length()>1) {
						soapActionValue = soapActionValue.substring(0,soapActionValue.length()-1);
					}
					if(StringUtils.isEmpty(soapActionValue)) {
						return ct; // dopo l'eliminazione degli "
					}
					if(pSoapAction!=null){
						cType.getParameterList().remove(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION);
					}
					if(this.contentTypeParamaters!=null && this.contentTypeParamaters.containsKey(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION)){
						this.contentTypeParamaters.remove(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION);
					}
					this.contentTypeParamaters.put(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION, soapActionValue);
					//System.out.println("NEW '"+soapActionValue+"'");
					return ContentTypeUtilities.buildContentType(cType.toString(),this.contentTypeParamaters);
				}
			}
			else {
				return ct;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ct;
		}
	}
}
