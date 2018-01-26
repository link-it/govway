/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.mime.MimeMultipart;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi binari
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_mimeMultipart_impl extends AbstractBaseOpenSPCoop2RestMessage<MimeMultipart> implements OpenSPCoop2RestMimeMultipartMessage {

	public OpenSPCoop2Message_mimeMultipart_impl() {
		super();
	}
	public OpenSPCoop2Message_mimeMultipart_impl(InputStream is,String contentType) throws MessageException {
		super(is, contentType);
	}
	
	@Override
	protected MimeMultipart buildContent() throws MessageException{
		try{
			return new MimeMultipart(this.is, this.contentType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.serializeContent(bout, true);
			bout.flush();
			bout.close();
			return bout.toString(this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			this.content.writeTo(os);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
}
