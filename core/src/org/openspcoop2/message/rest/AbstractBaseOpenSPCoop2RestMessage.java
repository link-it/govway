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

package org.openspcoop2.message.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * AbstractBaseOpenSPCoop2SoapMessage_impl
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseOpenSPCoop2RestMessage<T> extends AbstractBaseOpenSPCoop2Message implements OpenSPCoop2RestMessage<T> {

	protected CountingInputStream countingInputStream; 
	protected String contentType;
	protected String contentTypeCharsetName = Charset.UTF_8.getValue();
	
	protected T content;
	protected boolean hasContent = false;
	
	
	/* Costruttore */
	
	protected AbstractBaseOpenSPCoop2RestMessage(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		this.hasContent = false;
	}
	
	protected AbstractBaseOpenSPCoop2RestMessage(OpenSPCoop2MessageFactory messageFactory, InputStream isParam,String contentType) throws MessageException {
		super(messageFactory);
		try{
			this.contentType = contentType;
			if(contentType!=null){
				String ch = ContentTypeUtilities.readCharsetFromContentType(contentType);
				if(ch!=null){
					this.contentTypeCharsetName = ch;
				}
			}
			if(isParam!=null){
				
				// check se esiste del contenuto nello stream, lo stream può essere diverso da null però vuoto.
				byte[] b = new byte[1];
				if(isParam.read(b) == -1) {
					// stream vuoto
					this.hasContent = false;
				} else {
					InputStream seq = new SequenceInputStream(new ByteArrayInputStream(b),isParam);
					this.countingInputStream = new CountingInputStream(seq);
					this.hasContent = true;
				}

			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	/* Metodi richiesti da chi implementa questa classe base */
	
	protected abstract T buildContent() throws MessageException;
	protected abstract String buildContentAsString() throws MessageException;
	protected abstract void serializeContent(OutputStream os, boolean consume) throws MessageException;
	

	
	
	/* Elementi REST */
	
	private synchronized void initializeContent() throws MessageException{
		if(this.hasContent){
			if(this.content==null){
				try {
					this.content = this.buildContent();
				}catch(Throwable t) {
					MessageUtils.registerParseException(this, t, true);
					throw new MessageException(t.getMessage(),t);
				}
			}
		}
	}
	
	@Override
	public boolean hasContent() throws MessageException,MessageNotSupportedException{
		return this.hasContent;
	}
	
	@Override
	public T getContent() throws MessageException,MessageNotSupportedException{
		if(this.hasContent){
			if(this.content==null){
				this.initializeContent();
			}
		}
		return this.content; // può tornare null
	}
	
	@Override
	public String getContentAsString() throws MessageException,MessageNotSupportedException{
		try{
			if(this.hasContent){
				if(this.content==null){
					this.initializeContent();
				}
				return this.buildContentAsString();
			}
			return null;
		}catch(MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateContent(T content) throws MessageException, MessageNotSupportedException{
		this.content = content;
		if(this.content!=null) {
			this.hasContent = true;
		}
		else {
			this.hasContent = false;
			this.contentType = null;
		}
	}
	
	@Override
	public boolean isProblemDetailsForHttpApis_RFC7807() throws MessageException,MessageNotSupportedException {
		return false;
	}
	
	
	/* ContentType */
	
	@Override
	public void updateContentType() throws MessageException {
		// nop;
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	
	/* WriteTo e Save */
	
	@Override
	public void writeTo(OutputStream os, boolean consume) throws MessageException {
		try{
			if(this.hasContent){
				
				if(!consume && this.content==null) {
					this.initializeContent(); // per poi entrare nel ramo sotto serializeContent
				}
			
				CountingOutputStream cos = new CountingOutputStream(os);
				if(this.content!=null){
					this.serializeContent(cos, consume);
				}
				else{
					Utilities.copy(this.countingInputStream, cos);
					this.countingInputStream.close();
				}
				this.outgoingsize = cos.getByteCount();
			}
		}
		catch(MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void saveChanges() throws MessageException{
		// nop;
	}
	
	@Override
	public boolean saveRequired(){
		return false;
	}
	
	
	/* Content Length */
	
	@Override
	public long getIncomingMessageContentLength() {
		if(this.countingInputStream!=null) {
			return this.countingInputStream.getByteCount();
		}
		else {
			return super.getIncomingMessageContentLength();
		}
	}	
	

}
