/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.DumpRestMessageUtils;
import org.openspcoop2.message.soap.AbstractBaseOpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.slf4j.Logger;

/**
 * ContentExtractor
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentExtractor {

	@SuppressWarnings("unused")
	private Logger log;
	
	private OpenSPCoop2Message message;

	public ContentExtractor(OpenSPCoop2Message message, Logger log) {
		this.message = message; 
		this.log = log;
	}


	public OpenSPCoop2Message getMessage() {
		return this.message; // questo metodo consente di attuare trasformazione più avanzate agendo sull'oggetto
	}


	public void addTransportHeader(String name, String value) {
		if(this.message!=null) {
			this.message.forceTransportHeader(name, value);
		}
	}
	public void removeTransportHeader(String name) {
		if(this.message!=null) {
			if(this.message.getTransportRequestContext()!=null) {
				this.message.getTransportRequestContext().removeParameterTrasporto(name);
			}
			else if(this.message.getTransportResponseContext()!=null) {
				this.message.getTransportResponseContext().removeParameterTrasporto(name);
			}
		}
	}
	
	public void addUrlProperty(String name, String value) {
		if(this.message!=null) {
			this.message.forceUrlProperty(name, value);
		}
	}
	public void removeUrlProperty(String name) {
		if(this.message!=null) {
			if(this.message.getTransportRequestContext()!=null) {
				this.message.getTransportRequestContext().removeParameterFormBased(name);
			}
		}
	}

	public void disableExceptionIfFoundMoreSecurityHeader() {
		if(this.message!=null && ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			if(this.message instanceof AbstractBaseOpenSPCoop2SoapMessage) {
				AbstractBaseOpenSPCoop2SoapMessage soapMsg = (AbstractBaseOpenSPCoop2SoapMessage) this.message;
				soapMsg.setThrowExceptionIfFoundMoreSecurityHeader(false);
			}
		}
	}
	public void addSoapHeader(String xml) throws DynamicException {
		this.addSoapHeader(xml.getBytes());
	}
	public void addSoapHeader(byte[] xml) throws DynamicException {
		if(this.message!=null && ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			
			try {
			
				OpenSPCoop2SoapMessage soapMsg = this.message.castAsSoap();
				
				SOAPHeader header = soapMsg.getSOAPHeader();
				if(header==null) {
					header = soapMsg.getSOAPPart().getEnvelope().addHeader();
				}
				
				SOAPElement soapElement = soapMsg.createSOAPElement(xml);
				
				header.addChildElement(soapElement);
				
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
		}
	}
	
	public boolean isSoap() throws DynamicException {
		if(this.message==null) {
			return false;
		}
		return ServiceBinding.SOAP.equals(this.message.getServiceBinding());
	}
	public boolean isSoap11() throws DynamicException {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.SOAP_11.equals(this.message.getMessageType());
	}
	public boolean isSoap12() throws DynamicException {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.SOAP_12.equals(this.message.getMessageType());
	}
	
	public boolean isRest() throws DynamicException {
		if(this.message==null) {
			return false;
		}
		return ServiceBinding.REST.equals(this.message.getServiceBinding());
	}
	public boolean isRestXml() throws DynamicException {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.XML.equals(this.message.getMessageType());
	}
	public boolean isRestJson() throws DynamicException {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.JSON.equals(this.message.getMessageType());
	}
	public boolean isRestMultipart() throws DynamicException {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.MIME_MULTIPART.equals(this.message.getMessageType());
	}
	public boolean isRestBinary() throws DynamicException {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.BINARY.equals(this.message.getMessageType());
	}
	
	
	public boolean hasContent() throws DynamicException {
		try {
			if(this.message==null) {
				return false;
			}
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				return this.message.castAsSoap().getSOAPPart()!=null && this.message.castAsSoap().getSOAPPart().getEnvelope()!=null;
			}
			else {
				return this.message.castAsRest().hasContent();
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public byte[] getContent() throws DynamicException {
		if(this.hasContent()==false) {
			return null;
		}
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				this.message.castAsSoap().writeTo(bout, false);
				bout.flush();
				bout.close();
				return bout.toByteArray();
			}
			else {
				return this.message.castAsRest().getContentAsString().getBytes();
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public String getContentAsString() throws DynamicException {
		if(this.hasContent()==false) {
			return null;
		}
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				this.message.castAsSoap().writeTo(bout, false);
				bout.flush();
				bout.close();
				return bout.toString();
			}
			else {
				return this.message.castAsRest().getContentAsString();
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}

	public byte[] getContentSoapBody() throws DynamicException {
		if(this.hasContent()==false) {
			return null;
		}
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				if(this.message.castAsSoap().getSOAPPart()==null || this.message.castAsSoap().getSOAPPart().getEnvelope()==null) {
					throw new Exception("Messaggio senza una busta SOAP");
				}
				return TunnelSoapUtils.sbustamentoSOAPEnvelope(this.message.getFactory(), this.message.castAsSoap().getSOAPPart().getEnvelope(), false);
			}
			else {
				throw new Exception("Funzionalità utilizzabile solamente con service binding soap");
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	
	public DumpMessaggio dumpMessage() throws DynamicException {
		if(this.dumpMessaggioInit==null) {
			this.initDump();
		}
		return this.dumpMessaggio;
	}
	
	
	private DumpMessaggio dumpMessaggio = null;
	private Boolean dumpMessaggioInit = null;
	private synchronized void initDump() throws DynamicException {
		if(this.dumpMessaggioInit==null) {
			
			try{
				if(this.hasContent()) {
	
					DumpMessaggioConfig config = new DumpMessaggioConfig();
					config.setDumpAttachments(true);
					config.setDumpBody(true);
					config.setDumpHeaders(false);
					config.setDumpMultipartHeaders(true);
					if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
						this.dumpMessaggio = DumpSoapMessageUtils.dumpMessage(this.message.castAsSoap(), config, true);
					}
					else {
						this.dumpMessaggio = DumpRestMessageUtils.dumpMessage(this.message.castAsRest(), config, true);
					}
				}
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
			this.dumpMessaggioInit = true;
		}
	}
	
	// Utility JSON
	
	public void prettyFormatJsonContent() throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().prettyFormatContent();
		}catch(Exception e) {
			throw new DynamicException("Operazione fallita: "+e.getMessage(),e);
		}
	}
	
	public void addSimpleJsonElement(String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addSimpleElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addSimpleJsonElement(String jsonPath, String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addSimpleElement(jsonPath, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addObjectJsonElement(String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addObjectElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addObjectJsonElement(String jsonPath, String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addObjectElement(jsonPath, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addArrayJsonElement(String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addArrayElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addArrayJsonElement(String jsonPath, String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addArrayElement(jsonPath, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void removeJsonField(String name) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().removeElement(name);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void removeJsonField(String jsonPath, String name) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().removeElement(jsonPath, name);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	
}

