/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * AbstractBaseOpenSPCoop2SoapMessage_impl
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseOpenSPCoop2RestMessage<T> extends AbstractBaseOpenSPCoop2Message implements OpenSPCoop2RestMessage<T> {

	protected InputStream is;
	protected String contentType;
	protected String contentTypeCharsetName = Charset.UTF_8.getValue();
	
	protected T content;
	protected boolean hasContent = false;
	
	
	/* Costruttore */
	
	protected AbstractBaseOpenSPCoop2RestMessage() {
		this.hasContent = false;
	}
	
	protected AbstractBaseOpenSPCoop2RestMessage(InputStream isParam,String contentType) throws MessageException {
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
					this.is = new SequenceInputStream(new ByteArrayInputStream(b),isParam);
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
				this.content = this.buildContent();
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
		}
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
				if(this.content!=null){
					this.serializeContent(os, consume);
				}
				else{
					Utilities.copy(this.is, os);
				}
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
	

}
