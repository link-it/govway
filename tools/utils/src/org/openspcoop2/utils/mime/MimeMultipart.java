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

package org.openspcoop2.utils.mime;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * MimeMultipartContent
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MimeMultipart {
	
	private javax.mail.internet.MimeMultipart mimeMultipart = null;
	
	
	public MimeMultipart(InputStream is, String contentType) throws UtilsException{
		try{
			javax.activation.DataSource ds = new ByteArrayDataSource(is, contentType);
			this.mimeMultipart = new javax.mail.internet.MimeMultipart(ds);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	/* Attachments REST */
	
	public void addBodyPart(BodyPart bodyPart) throws UtilsException{
		try{
			this.mimeMultipart.addBodyPart(bodyPart);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addBodyPart(BodyPart bodyPart, int index) throws UtilsException{
		try{
			this.mimeMultipart.addBodyPart(bodyPart, index);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public BodyPart createBodyPart(java.io.InputStream is) throws UtilsException{
		try{
			return new MimeBodyPart(is);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public BodyPart createBodyPart(InternetHeaders headers, byte[] content) throws UtilsException{
		try{
			return new MimeBodyPart(headers,content);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public int countBodyParts() throws UtilsException{
		try{
			return this.mimeMultipart.getCount();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public BodyPart getBodyPart(int index) throws UtilsException{
		try{
			return this.mimeMultipart.getBodyPart(index);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public BodyPart getBodyPart(String contentID) throws UtilsException{
		try{
			return this.mimeMultipart.getBodyPart(contentID);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public void removeBodyPart(int index) throws UtilsException{
		try{
			this.mimeMultipart.removeBodyPart(index);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void removeBodyPart(String contentID) throws UtilsException{
		try{
			BodyPart bodyPart = this.getBodyPart(contentID);
			if(bodyPart!=null){
				this.mimeMultipart.removeBodyPart(bodyPart);
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void removeBodyPart(BodyPart bodyPart) throws UtilsException{
		try{
			this.mimeMultipart.removeBodyPart(bodyPart);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public void writeTo(OutputStream os) throws UtilsException{
		try{
			this.mimeMultipart.writeTo(os);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String getContentID(BodyPart bodyPart) throws UtilsException{
		try{
			Enumeration<?> enHdr = bodyPart.getAllHeaders();
			while (enHdr.hasMoreElements()) {
				String header = (String) enHdr.nextElement();
				if(HttpConstants.CONTENT_ID.equals(header)){
					return bodyPart.getHeader(header)[0];
				}	
				else if(HttpConstants.CONTENT_ID.toLowerCase().equals(header.toLowerCase())){
					return bodyPart.getHeader(header)[0];
				}	
				else if(HttpConstants.CONTENT_ID.toUpperCase().equals(header.toUpperCase())){
					return bodyPart.getHeader(header)[0];
				}
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String getContentLocation(BodyPart bodyPart) throws UtilsException{
		try{
			Enumeration<?> enHdr = bodyPart.getAllHeaders();
			while (enHdr.hasMoreElements()) {
				String header = (String) enHdr.nextElement();
				if(HttpConstants.CONTENT_LOCATION.equals(header)){
					return bodyPart.getHeader(header)[0];
				}	
				else if(HttpConstants.CONTENT_LOCATION.toLowerCase().equals(header.toLowerCase())){
					return bodyPart.getHeader(header)[0];
				}	
				else if(HttpConstants.CONTENT_LOCATION.toUpperCase().equals(header.toUpperCase())){
					return bodyPart.getHeader(header)[0];
				}
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
