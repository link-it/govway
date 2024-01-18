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

package org.openspcoop2.message.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestBinaryMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi binari
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_binary_impl extends AbstractBaseOpenSPCoop2RestMessage<BinaryContent> implements OpenSPCoop2RestBinaryMessage {

	public OpenSPCoop2Message_binary_impl(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		//this.supportReadOnly = false; // il contenuto e' gia un binary.
		this.supportReadOnly = true; // in modo da sfruttare il buffer
	}
	public OpenSPCoop2Message_binary_impl(OpenSPCoop2MessageFactory messageFactory, InputStream is,String contentType) throws MessageException {
		super(messageFactory, is, contentType);
		//this.supportReadOnly = false; // il contenuto e' gia un binary.
		this.supportReadOnly = true; // in modo da sfruttare il buffer
	}
	
	@Override
	protected BinaryContent buildContent() throws MessageException{
		try{
			return new BinaryContent(this._getInputStream(), this.contentType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally {
			try {
				this._getInputStream().close();
			}catch(Exception eClose) {
				// close
			}
		}
	}
	@Override
	protected BinaryContent buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException{
		return new BinaryContent(contentBuffer, this.contentType);
	}

	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			return Utilities.getAsString(new ByteArrayInputStream(this.content.getContent()), this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	@Override
	protected byte[] buildContentAsByteArray() throws MessageException{
		return this.content.getContent();
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			this.content.writeTo(os, consume);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	public void updateContent(byte[] content) throws MessageException, MessageNotSupportedException {
		if(this.content!=null && this.content.contentBuffer!=null) {
			this.content.contentBuffer.clearResources();
			this.content = null;
		}
		this.content = new BinaryContent(new ByteArrayInputStream(content), this.contentType);
	}
	
}
