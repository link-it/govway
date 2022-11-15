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
package org.openspcoop2.web.ctrlstat.core;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.struts.upload.FormFile;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * WrapperFormFile
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SerialiableFormFile implements FormFile, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String contentType;
	private String fileName;
	private byte[] content;
	
	public SerialiableFormFile(FormFile ff) throws UtilsException {
		try {
//			if(ff instanceof SerialiableFormFile) {
//				SerialiableFormFile sff = (SerialiableFormFile) ff;
//				this.contentType = sff.getContentType();
//				this.fileName = sff.getFileName();
//				this.content = sff.getFileData();
//			}
//			else {
			this.contentType = ff.getContentType();
			this.fileName = ff.getFileName();
			this.content = ff.getFileData();
			//ff.destroy();
//			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	// Costruttori utilizzati in RS API
	public SerialiableFormFile(String fileName, byte[] content) throws UtilsException {
		this(getContentType(fileName),fileName,content);
	}
	public SerialiableFormFile(String contentType, String fileName, byte[] content) {
		this.fileName = fileName;
		this.content = content;
		this.contentType = contentType;
	}
	
	private static String getContentType(String fileName) throws UtilsException {
		String mimeType = null;
		if(fileName.contains(".")){
			String ext = null;
			try{
				ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			}catch(Exception e){
				// ignore
			}
			MimeTypes mimeTypes = MimeTypes.getInstance();
			if(ext!=null && mimeTypes.existsExtension(ext)){
				mimeType = mimeTypes.getMimeType(ext);
				//System.out.println("CUSTOM ["+mimeType+"]");		
			}
			else{
				mimeType = HttpConstants.CONTENT_TYPE_X_DOWNLOAD;
			}
		}
		else{
			mimeType = HttpConstants.CONTENT_TYPE_X_DOWNLOAD;
		}
		return mimeType;
	}
	
	@Override
	public void destroy() {
		
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public byte[] getFileData() throws FileNotFoundException, IOException {
		return this.content;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public int getFileSize() {
		return this.content.length;
	}

	@Override
	public InputStream getInputStream() throws FileNotFoundException, IOException {
		return new ByteArrayInputStream(this.content);
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setFileSize(int arg0) {
		// nop;
	}

}
