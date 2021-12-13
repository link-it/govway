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

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestBinaryMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi binari
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_binary_impl extends AbstractBaseOpenSPCoop2RestMessage<byte[]> implements OpenSPCoop2RestBinaryMessage {

	public OpenSPCoop2Message_binary_impl(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		this.supportReadOnly = false; // il contenuto e' gia un binary.
	}
	public OpenSPCoop2Message_binary_impl(OpenSPCoop2MessageFactory messageFactory, InputStream is,String contentType) throws MessageException {
		super(messageFactory, is, contentType);
		this.supportReadOnly = false; // il contenuto e' gia un binary.
	}
	
	@Override
	protected byte[] buildContent() throws MessageException{
		try{
			return Utilities.getAsByteArray(this.countingInputStream);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally {
			try {
				this.countingInputStream.close();
			}catch(Exception eClose) {}
		}
	}
	@Override
	protected byte[] buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException{
		return contentBuffer.toByteArray();
	}

	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			return Utilities.getAsString(new ByteArrayInputStream(this.content), this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	@Override
	protected byte[] buildContentAsByteArray() throws MessageException{
		return this.content;
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			os.write(this.content);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
}
