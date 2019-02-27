/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config.rs.server.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
public class WrapperFormFile implements FormFile {

	private String contentType;
	private String fileName;
	private byte[] content;
	
	public WrapperFormFile(String fileName, byte[] content) throws UtilsException {
		this(getContentType(fileName),fileName,content);
	}
	public WrapperFormFile(String contentType, String fileName, byte[] content) {
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
			}catch(Exception e){}
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
