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


package org.openspcoop2.message.utils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Allegato
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpAttachment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String contentId;
	private String contentLocation;
	private String contentType;
	private HashMap<String, String> headers = new HashMap<>();
	
	private String errorContentNotSerializable;
	private ByteArrayOutputStream content;
	private StringBuilder printableContent;
	
	public String getContentId() {
		return this.contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getContentLocation() {
		return this.contentLocation;
	}
	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getErrorContentNotSerializable() {
		return this.errorContentNotSerializable;
	}
	public void setErrorContentNotSerializable(String errorContentNotSerializable) {
		this.errorContentNotSerializable = errorContentNotSerializable;
	}
	public long getContentLength() {
		if(this.content!=null) {
			return this.content.size();
		}
		return 0;
	}
	public byte[] getContent() {
		if(this.content!=null) {
			return this.content.toByteArray();
		}
		return null;
	}
	private static String getTestoVisualizzabile(byte [] b,StringBuilder stringBuffer) {
		 try{
			 // 1024 = 1K
			 // Visualizzo al massimo 50K (per i log)
			 int max = 50 * 1024;
			 stringBuffer.append(org.openspcoop2.utils.Utilities.convertToPrintableText(b, max));
			 return null;	
		 }catch(Exception e){
			 return e.getMessage();
		 }

	 }
	public String getContentAsString(boolean ifPrintableContent) {
		if(this.content!=null) {
			if(ifPrintableContent) {
				if(this.printableContent!=null) {
					return this.printableContent.toString();
				}
				else {
					this.printableContent = new StringBuilder();
					String errore = getTestoVisualizzabile(this.content.toByteArray(),this.printableContent);
					if(errore!=null) {
						this.printableContent = new StringBuilder();
						this.printableContent.append(errore);
					}
					return this.printableContent.toString();
				}
			}
			else {
				return this.content.toString();
			}
		}
		return null;
	}
	public void setContent(ByteArrayOutputStream content) {
		this.content = content;
	}
	
	public HashMap<String, String> getHeaders() {
		return this.headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
}
