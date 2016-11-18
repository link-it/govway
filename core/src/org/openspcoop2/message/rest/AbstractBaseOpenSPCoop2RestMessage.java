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

package org.openspcoop2.message.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
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
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
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
	
	protected AbstractBaseOpenSPCoop2RestMessage(InputStream is,String contentType) throws MessageException {
		try{
			this.is = is;
			this.contentType = contentType;
			if(contentType!=null){
				String ch = ContentTypeUtilities.readCharsetFromContentType(contentType);
				if(ch!=null){
					this.contentTypeCharsetName = ch;
				}
			}
			if(is!=null){
				this.hasContent = true;
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
				if(this.content!=null){
					return this.buildContentAsString();
				}
				else{
					Utilities.getAsString(this.is, this.contentTypeCharsetName);
				}
			}
			return null;
		}catch(MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	/* Trasporto */

	
	@Override
	public OpenSPCoop2MessageProperties getForwardTransportHeader(List<String> whiteListHeader) throws MessageException{
		try{
			if(this.forwardTransportHeader.isInitialize()==false){
				
				Properties transportHeaders = null;
				if(this.transportRequestContext!=null){
					transportHeaders = this.transportRequestContext.getParametersTrasporto();
				}
				else if(this.transportResponseContext!=null){
					transportHeaders = this.transportResponseContext.getParametersTrasporto();
				}
				
				if(transportHeaders!=null && transportHeaders.size()>0){
					RestUtilities.initializeTransportHeaders(this.forwardTransportHeader, this.messageRole, transportHeaders, 
							whiteListHeader);
					this.forwardTransportHeader.setInitialize(true);
				}
			
			}
			return this.forwardTransportHeader;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
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
