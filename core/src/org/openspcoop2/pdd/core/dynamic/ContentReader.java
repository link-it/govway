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

package org.openspcoop2.pdd.core.dynamic;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.DumpRestMessageUtils;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.slf4j.Logger;

/**
 * ContentReader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentReader {

	protected static final String FUNZIONALITA_RICHIEDE_SOAP = "Funzionalità utilizzabile solamente con service binding soap";
	protected static final String FUNZIONALITA_RICHIEDE_JSON = "Funzionalità richiede un messaggio JSON";
	protected static final String FUNZIONALITA_RICHIEDE_XML = "Funzionalità richiede un messaggio XML";
	
	protected Logger log;
	
	protected OpenSPCoop2Message message;
	
	protected Context pddContext;

	public ContentReader(OpenSPCoop2Message message, Context pddContext, Logger log) {
		this.message = message; 
		this.pddContext = pddContext;
		this.log = log;
	}


	public String getApplicationMessageId() {
		boolean request = this.message==null || 
				this.message.getMessageRole()==null || 
				MessageRole.NONE.equals(this.message.getMessageRole()) || 
				MessageRole.REQUEST.equals(this.message.getMessageRole());
		if(request) {
			if(this.pddContext!=null && this.pddContext.containsKey(GestoreCorrelazioneApplicativa.CONTEXT_CORRELAZIONE_APPLICATIVA_RICHIESTA)) {
				return (String) this.pddContext.get(GestoreCorrelazioneApplicativa.CONTEXT_CORRELAZIONE_APPLICATIVA_RICHIESTA);
			}
		}
		else {
			if(this.pddContext!=null && this.pddContext.containsKey(GestoreCorrelazioneApplicativa.CONTEXT_CORRELAZIONE_APPLICATIVA_RISPOSTA)) {
				return (String) this.pddContext.get(GestoreCorrelazioneApplicativa.CONTEXT_CORRELAZIONE_APPLICATIVA_RISPOSTA);
			}
		}
		return null;
	}
	
	
	public OpenSPCoop2Message getMessage() {
		return this.message; // questo metodo consente di attuare trasformazione più avanzate agendo sull'oggetto
	}

	
	public boolean isSoap() {
		if(this.message==null) {
			return false;
		}
		return ServiceBinding.SOAP.equals(this.message.getServiceBinding());
	}
	public boolean isSoap11() {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.SOAP_11.equals(this.message.getMessageType());
	}
	public boolean isSoap12() {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.SOAP_12.equals(this.message.getMessageType());
	}
	
	public boolean isRest() {
		if(this.message==null) {
			return false;
		}
		return ServiceBinding.REST.equals(this.message.getServiceBinding());
	}
	public boolean isRestXml() {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.XML.equals(this.message.getMessageType());
	}
	public boolean isRestJson() {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.JSON.equals(this.message.getMessageType());
	}
	public boolean isRestMultipart() {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.MIME_MULTIPART.equals(this.message.getMessageType());
	}
	public boolean isRestBinary() {
		if(this.message==null || this.message.getMessageType()==null) {
			return false;
		}
		return MessageType.BINARY.equals(this.message.getMessageType());
	}
	
	
	public boolean isHasContent() throws DynamicException {
		// per invocazioni dinamiche
		return this.hasContent(); 
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
		}catch(Exception t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public byte[] getContent() throws DynamicException {
		byte [] content = null;
		if(!this.hasContent()) {
			content = null;
		}
		else {
			try {
				if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					this.message.castAsSoap().writeTo(bout, false);
					bout.flush();
					bout.close();
					content = bout.toByteArray();
				}
				else {
					/** non viene preservato il charset, es il test org.openspcoop2.pdd.core.trasformazioni.Test fallisce: Test [zip-json] (charset: ISO-8859-1) ... nei caratteri strani
					return this.message.castAsRest().getContentAsByteArray(); */
					content = this.message.castAsRest().getContentAsString().getBytes();
				}
			}catch(Exception t) {
				throw new DynamicException(t.getMessage(),t);
			}
		}
		return content;
	}
	public String getContentAsString() throws DynamicException {
		if(!this.hasContent()) {
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
		}catch(Exception t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}

	public String getContentBase64Digest(String algorithm) throws DynamicException{
		return getContentDigest(algorithm, DigestEncoding.BASE64, false);
	}
	public String getContentBase64Digest(String algorithm, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentBase64Digest(algorithm, v);
	}
	public String getContentBase64Digest(String algorithm, boolean rfc3230) throws DynamicException{
		return getContentDigest(algorithm, DigestEncoding.BASE64, rfc3230);
	}
	
	public String getContentHexDigest(String algorithm) throws DynamicException{
		return getContentDigest(algorithm, DigestEncoding.HEX, false);
	}
	public String getContentHexDigest(String algorithm, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentHexDigest(algorithm, v);
	}
	public String getContentHexDigest(String algorithm, boolean rfc3230) throws DynamicException{
		return getContentDigest(algorithm, DigestEncoding.HEX, rfc3230);
	}
	
	public String getContentDigest(String algorithm, String digestEncodingParam) throws DynamicException{
		return getContentDigest(algorithm, digestEncodingParam, false);
	}
	public String getContentDigest(String algorithm, DigestEncoding digestEncoding) throws DynamicException{
		return getContentDigest(algorithm, digestEncoding, false);
	}
	public String getContentDigest(String algorithm, String digestEncodingParam, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentDigest(algorithm, digestEncodingParam, v);
	}
	public String getContentDigest(String algorithm, String digestEncodingParam,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws DynamicException{
		DigestEncoding digestEncoding = null;
		try {
			digestEncoding = DigestEncoding.valueOf(digestEncodingParam);
		}catch(Exception t) {
			throw new DynamicException("DigestEncoding '"+digestEncodingParam+"' unsupported");
		}
		return getContentDigest(algorithm, digestEncoding, rfc3230);
	}
	public String getContentDigest(String algorithm, DigestEncoding digestEncoding, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentDigest(algorithm, digestEncoding, v);
	}
	public String getContentDigest(String algorithm, DigestEncoding digestEncoding,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws DynamicException{
		byte[] content = getContent();
		if(content==null) {
			throw new DynamicException("Content null");
		}
		try {
			return org.openspcoop2.utils.digest.DigestUtils.getDigestValue(content, algorithm, digestEncoding, rfc3230);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public byte[] getContentSoapBody() throws DynamicException {
		byte [] content = null;
		if(!this.hasContent()) {
			content = null;
		}
		else {
			try {
				if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
					if(this.message.castAsSoap().getSOAPPart()==null || this.message.castAsSoap().getSOAPPart().getEnvelope()==null) {
						throw new DynamicException("Messaggio senza una busta SOAP");
					}
					content = TunnelSoapUtils.sbustamentoSOAPEnvelope(this.message.getFactory(), this.message.castAsSoap().getSOAPPart().getEnvelope(), false);
				}
				else {
					throw new DynamicException(FUNZIONALITA_RICHIEDE_SOAP);
				}
			}catch(Exception t) {
				throw new DynamicException(t.getMessage(),t);
			}
		}
		return content;
	}
	
	public String getContentSoapBodyBase64Digest(String algorithm) throws DynamicException{
		return getContentSoapBodyDigest(algorithm, DigestEncoding.BASE64, false);
	}
	public String getContentSoapBodyBase64Digest(String algorithm, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentSoapBodyBase64Digest(algorithm, v);
	}
	public String getContentSoapBodyBase64Digest(String algorithm, boolean rfc3230) throws DynamicException{
		return getContentSoapBodyDigest(algorithm, DigestEncoding.BASE64, rfc3230);
	}
	
	public String getContentSoapBodyHexDigest(String algorithm) throws DynamicException{
		return getContentSoapBodyDigest(algorithm, DigestEncoding.HEX, false);
	}
	public String getContentSoapBodyHexDigest(String algorithm, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentSoapBodyHexDigest(algorithm, v);
	}
	public String getContentSoapBodyHexDigest(String algorithm, boolean rfc3230) throws DynamicException{
		return getContentSoapBodyDigest(algorithm, DigestEncoding.HEX, rfc3230);
	}
	
	public String getContentSoapBodyDigest(String algorithm, String digestEncodingParam) throws DynamicException{
		return getContentSoapBodyDigest(algorithm, digestEncodingParam, false);
	}
	public String getContentSoapBodyDigest(String algorithm, DigestEncoding digestEncoding) throws DynamicException{
		return getContentSoapBodyDigest(algorithm, digestEncoding, false);
	}
	public String getContentSoapBodyDigest(String algorithm, String digestEncodingParam, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentSoapBodyDigest(algorithm, digestEncodingParam, v);
	}
	public String getContentSoapBodyDigest(String algorithm, String digestEncodingParam,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws DynamicException{
		DigestEncoding digestEncoding = null;
		try {
			digestEncoding = DigestEncoding.valueOf(digestEncodingParam);
		}catch(Exception t) {
			throw new DynamicException("DigestEncoding '"+digestEncodingParam+"' unsupported");
		}
		return getContentSoapBodyDigest(algorithm, digestEncoding, rfc3230);
	}
	public String getContentSoapBodyDigest(String algorithm, DigestEncoding digestEncoding, String rfc3230) throws DynamicException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new DynamicException(this.buildUncorrectBooleaValueMessage(rfc3230, e),e);
		}
		return getContentSoapBodyDigest(algorithm, digestEncoding, v);
	}
	public String getContentSoapBodyDigest(String algorithm, DigestEncoding digestEncoding,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws DynamicException{
		byte[] content = getContentSoapBody();
		if(content==null) {
			throw new DynamicException("Content null");
		}
		try {
			return org.openspcoop2.utils.digest.DigestUtils.getDigestValue(content, algorithm, digestEncoding, rfc3230);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	private String buildUncorrectBooleaValueMessage(String rfc3230, Exception e) {
		return "Uncorrect boolean value '"+rfc3230+"': "+e.getMessage();
	}
	
	
	
	public boolean isSoapFault() throws DynamicException {
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				return this.message.castAsSoap().hasSOAPFault();
			}
			else {
				throw new DynamicException(FUNZIONALITA_RICHIEDE_SOAP);
			}
		}catch(Exception t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public boolean isSoapBodyEmpty() throws DynamicException {
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				return this.message.castAsSoap().isSOAPBodyEmpty();
			}
			else {
				throw new DynamicException(FUNZIONALITA_RICHIEDE_SOAP);
			}
		}catch(Exception t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public boolean isSoapWithAttachments() throws DynamicException {
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				return this.message.castAsSoap().hasAttachments();
			}
			else {
				throw new DynamicException(FUNZIONALITA_RICHIEDE_SOAP);
			}
		}catch(Exception t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public OpenSPCoop2MessageSoapStreamReader getSoapReader() throws DynamicException {
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				return this.message.castAsSoap().getSoapReader();
			}
			else {
				throw new DynamicException(FUNZIONALITA_RICHIEDE_SOAP);
			}
		}catch(Exception t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	
	
	public DumpMessaggio getPart() throws DynamicException {
		// per invocazioni dinamiche
		return this.dumpMessage();
	}
	public DumpMessaggio getDumpMessage() throws DynamicException {
		// per invocazioni dinamiche
		return this.dumpMessage();
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
			}catch(Exception t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
			this.dumpMessaggioInit = true;
		}
	}

	
}

