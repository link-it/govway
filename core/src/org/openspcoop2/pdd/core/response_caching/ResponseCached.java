/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.response_caching;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.context.HeaderParameters;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.context.StringParameter;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**     
 * ResponseCached
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCached implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String contentType;
	private byte[] message;
	private long messageLength;
	private byte[] context;
	private Date creazione;
	private Date scadenza;
	private String digest;
	private String uuid;
	
	public Date getCreazione() {
		return this.creazione;
	}
	public long getAgeInSeconds() {
		long now = DateManager.getTimeMillis();
		long creazione = this.creazione.getTime();
		long diff = now - creazione;
		return (diff / 1000l);
	}
	public void setCreazione(Date creazione) {
		this.creazione = creazione;
	}
	public Date getScadenza() {
		return this.scadenza;
	}
	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getMessage() {
		return this.message;
	}
	public void setMessage(byte[] message) {
		this.message = message;
	}
	public long getMessageLength() {
		return this.messageLength;
	}
	public void setMessageLength(long messageLength) {
		this.messageLength = messageLength;
	}
	public byte[] getContext() {
		return this.context;
	}
	public void setContext(byte[] context) {
		this.context = context;
	}
	public String getUuid() {
		return this.uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDigest() {
		return this.digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	
	public String print() throws UtilsException {
		StringBuilder bf = new StringBuilder();
		bf.append("UUID: ").append(this.uuid).append("\n\n");
		bf.append("Digest: ").append(this.digest).append("\n\n");
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		String creazione = dateformat.format(this.creazione).replace('_','T');
		bf.append("Creazione: ").append(creazione).append("\n\n");
		String scadenza = dateformat.format(this.scadenza).replace('_','T');
		bf.append("Scadenza: ").append(scadenza).append("\n\n");
		bf.append("Età (secondi): ").append(this.getAgeInSeconds()).append("\n\n");
		bf.append("Content-Type: ").append(this.contentType).append("\n\n");
		bf.append("Content-Length: ").append(this.messageLength).append("\n\n");
		bf.append("Context: \n").append(new String(this.context)).append("\n\n");
		// 1024 = 1K
		// Visualizzo al massimo 20K
		int max = 20 * 1024;
		String message =  Utilities.convertToPrintableText(this.message,max);
		bf.append("Messaggio: \n").append(message);
		return bf.toString();
	}
	
	public OpenSPCoop2Message toOpenSPCoop2Message(OpenSPCoop2MessageFactory messageFactory, AttachmentsProcessingMode attachmentProcessingMode, String headerCacheKey) throws Exception {
		
		// Lettura Message Context
		org.openspcoop2.message.context.utils.serializer.JaxbDeserializer jaxbDeserializer  = 
				new org.openspcoop2.message.context.utils.serializer.JaxbDeserializer();
		MessageContext msgContext = jaxbDeserializer.readMessageContext(this.context);
		
		if(msgContext.getMessageType()==null) {
			throw new MessageException("Message Type undefined in context serialized");
		}
		MessageType mt = MessageType.valueOf(msgContext.getMessageType());
		if(mt==null) {
			throw new MessageException("MessageType ["+msgContext.getMessageType()+"] unknown");
		}
		
		if(msgContext.getMessageRole()==null) {
			throw new MessageException("Message Role undefined in context serialized");
		}
		MessageRole mr = MessageRole.valueOf(msgContext.getMessageRole());
		if(mr==null) {
			throw new MessageException("MessageRole ["+msgContext.getMessageRole()+"] unknown");
		}
		
		if(headerCacheKey!=null && msgContext.getTransportResponseContext()!=null) {
			if(msgContext.getTransportResponseContext().getHeaderParameters()==null) {
				msgContext.getTransportResponseContext().setHeaderParameters(new HeaderParameters());
			}
			StringParameter headerParameter = new StringParameter();
			headerParameter.setNome(headerCacheKey);
			headerParameter.setBase(this.uuid);
			msgContext.getTransportResponseContext().getHeaderParameters().addHeaderParameter(headerParameter);
		}
		
		// CostruzioneMessaggio
		NotifierInputStreamParams notifierInputStreamParams = null; 
		OpenSPCoop2MessageParseResult pr = null;
		pr = messageFactory.createMessage(mt,mr,this.contentType,
				this.message,notifierInputStreamParams,
				attachmentProcessingMode);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		msg.readResourcesFrom(msgContext);
		return msg;
	}
	
	public static ResponseCached toResponseCached(OpenSPCoop2Message msg, int seconds) throws MessageException, IOException {
		ResponseCached rCached = new ResponseCached();
		
		long now = DateManager.getTimeMillis();
		long ms = (((long)seconds)*1000l);
		
		Date creazione = new Date( now );
		rCached.setCreazione(creazione);
		
		Date scadenza = new Date( now + ms );
		rCached.setScadenza(scadenza);
		
		// Save bytes message
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		msg.writeTo(bout,false);
		bout.flush();
		bout.close();
		rCached.setMessage(bout.toByteArray());
		rCached.setMessageLength(bout.size());
		
		// Save message context
		bout = new java.io.ByteArrayOutputStream();
		msg.serializeResourcesTo(bout);
		bout.flush();
		bout.close();
		rCached.setContext(bout.toByteArray());
		
		rCached.setContentType(msg.getContentType());
		
		return rCached;
	}
}
