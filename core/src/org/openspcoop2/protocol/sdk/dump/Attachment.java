/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.sdk.dump;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * Allegato
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Attachment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String contentId;
	private String contentLocation;
	private String contentType;
	
	private byte[] content;
	
	private Map<String, String> headers = new Hashtable<String, String>();
	
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
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
}
