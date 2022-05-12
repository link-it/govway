/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.mime.MimeMultipart;

/**
 * MimeMultipartContent
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultipartContent extends AbstractLazyContent<MimeMultipart> {

	public MultipartContent(InputStream is, String contentType) throws MessageException {
		try{
			if(BUILD_LAZY) {
				this.init(is, contentType);
			}
			else {
				this.init(new MimeMultipart(is, contentType));
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public MultipartContent(DumpByteArrayOutputStream contentBuffer, String contentType) throws MessageException {
		try{
			if(BUILD_LAZY) {
				this.init(contentBuffer, contentType);
			}
			else {
				if(contentBuffer.isSerializedOnFileSystem()) {
					try(InputStream is = new FileInputStream(contentBuffer.getSerializedFile())){
						this.init(new MimeMultipart(is, contentType));
					}
				}
				else {
					try(InputStream is = new ByteArrayInputStream(contentBuffer.toByteArray())){
						this.init(new MimeMultipart(is, contentType));
					}
				}
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public MimeMultipart buildContent(InputStream is) throws MessageException {
		try {
			return new MimeMultipart(is, this.contentType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	@Override
	public MimeMultipart buildContent(byte[] c) throws MessageException {
		try(InputStream is = new ByteArrayInputStream(c)){
			return new MimeMultipart(is, this.contentType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	@Override
	public void writeContentTo(OutputStream os, boolean consume) throws MessageException {
		try {
			this.content.writeTo(os);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public MimeMultipart getMimeMultipart() throws MessageException {
		return this.getContent();
	}
	
}
