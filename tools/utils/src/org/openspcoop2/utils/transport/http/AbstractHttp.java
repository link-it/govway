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

package org.openspcoop2.utils.transport.http;

import java.util.Hashtable;
import java.util.Map;

/**
 * AbstractHttp
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractHttp {

	private String contentType;
	private byte[] content;
	private Map<String, String> headers = new Hashtable<String,String>();
	
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public void addHeader(String key,String value){
		this.headers.put(key, value);
	}
	public Map<String, String> getHeaders() {
		return this.headers;
	}
	public String getHeader(String header) {
		String v = this.headers.get(header);
		if(v==null){
			v = this.headers.get(header.toLowerCase());
		}
		if(v==null){
			v = this.headers.get(header.toUpperCase());
		}
		return v;
	}
	
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
