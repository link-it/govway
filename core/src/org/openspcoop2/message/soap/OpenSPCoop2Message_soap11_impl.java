/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPHeader;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi SOAP 11
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_soap11_impl extends AbstractOpenSPCoop2Message_soap_impl<OpenSPCoop2Message_saaj_11_impl> {
	
	public OpenSPCoop2Message_soap11_impl(OpenSPCoop2MessageFactory messageFactory, MimeHeaders mhs, InputStream is, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		super(messageFactory, mhs, is, overhead, soapStreamReader);
	}
	
	@Override
	protected OpenSPCoop2Message_saaj_11_impl buildContent() throws MessageException{
		try{
			return buildContent(this._getInputStream());
		}finally{
			try{
				this._getInputStream().close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	@Override
	protected OpenSPCoop2Message_saaj_11_impl buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException{
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

	protected OpenSPCoop2Message_saaj_11_impl buildContent(InputStream is) throws MessageException{
		try{
//			System.out.println("BUILD 11");
			
//			byte[] b = org.openspcoop2.utils.Utilities.getAsByteArray(is);
//			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
//			bout.write(b);
//			bout.flush();
//			bout.close();
//			System.out.println("AAAAAAA: '"+bout.toString()+"'");
//			is = new java.io.ByteArrayInputStream(b);
			
			OpenSPCoop2Message_saaj_11_impl msg = new OpenSPCoop2Message_saaj_11_impl(this.messageFactory, this.mhs, is);
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
	
}
