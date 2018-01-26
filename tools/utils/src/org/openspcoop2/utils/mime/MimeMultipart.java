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

package org.openspcoop2.utils.mime;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.openspcoop2.utils.UtilsException;


/**
 * MimeMultipartContent
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MimeMultipart {
	
	private javax.mail.internet.MimeMultipart mimeMultipart = null;
	
	
	public MimeMultipart() throws UtilsException{
		try{
			this.mimeMultipart = new javax.mail.internet.MimeMultipart();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public MimeMultipart(String subType) throws UtilsException{
		try{
			// multipart/<subType>
			this.mimeMultipart = new javax.mail.internet.MimeMultipart(subType);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
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
	
	public String getContentType() throws UtilsException{
		try{
			return this.mimeMultipart.getContentType();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private final static String CONTENT_ID = "Content-ID"; // Non e' utilizzabile HttpConstants per problemi di dipendenza di compilazione
	private final static String CONTENT_LOCATION = "Content-Location"; // Non e' utilizzabile HttpConstants per problemi di dipendenza di compilazione
	private final static String CONTENT_DISPOSITION = "Content-Disposition"; // Non e' utilizzabile HttpConstants per problemi di dipendenza di compilazione
	
	public String getContentID(BodyPart bodyPart) throws UtilsException{
		return this.getHeaderValue(CONTENT_ID, bodyPart);
	}
	
	public String getContentLocation(BodyPart bodyPart) throws UtilsException{
		return this.getHeaderValue(CONTENT_LOCATION, bodyPart);
	}
	
	public String getContentDisposition(BodyPart bodyPart) throws UtilsException{
		return this.getHeaderValue(CONTENT_DISPOSITION, bodyPart);
	}
	
	private String getHeaderValue(String headerName, BodyPart bodyPart) throws UtilsException{
		try{
			Enumeration<?> enHdr = bodyPart.getAllHeaders();
			while (enHdr.hasMoreElements()) {
				Object o = enHdr.nextElement();
				String header = null;
				javax.mail.Header mh = null;
				if(o instanceof String){
					header = (String) o;
					if(match(headerName, header)){
						return bodyPart.getHeader(header)[0];
					}
				}
				else if(o instanceof  javax.mail.Header){
					mh = ( javax.mail.Header) o;
					header = mh.getName();
					if(match(headerName, header)){
						return mh.getValue();
					}
				}
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private boolean match(String headerName, String check){
		return headerName.equals(check) || 
				headerName.toLowerCase().equals(check.toLowerCase()) || 
				headerName.toUpperCase().equals(check.toUpperCase()) ;
	}
}
