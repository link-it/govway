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
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;

/**
 * MimeMultipartContent
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultipartContent {

	public static boolean BUILD_LAZY = true;
	
	private MimeMultipart mimeMultipart;
	private byte[] content;
	private String contentType;
	
	public MultipartContent(InputStream is, String contentType) throws MessageException {
		try{
			this.contentType = contentType;
			if(BUILD_LAZY) {
				this.content = Utilities.getAsByteArray(is);
			}
			else {
				this.mimeMultipart = new MimeMultipart(is, contentType);
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public void buildMimeMultipart() throws MessageException {
		if(this.mimeMultipart==null) {
			try{
				this.mimeMultipart = new MimeMultipart(new ByteArrayInputStream(this.content), this.contentType);
				this.content = null;
			}catch(Exception e){
				throw new MessageException(e.getMessage(),e);
			}
		}
	}
	public MimeMultipart getMimeMultipart() throws MessageException {
		if(this.mimeMultipart==null) {
			buildMimeMultipart();
		}
		return this.mimeMultipart;
	}
//	public byte[] getContent() {
//		return this.content;
//	}
	
	public void writeTo(OutputStream os) throws MessageException {
		try{
			if(this.mimeMultipart!=null) {
				this.mimeMultipart.writeTo(os);
			}
			else if(this.content!=null) {
				os.write(this.content);
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
}
