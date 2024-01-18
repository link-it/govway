/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.transport.TransportUtils;

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
	private Map<String, List<String>> headers = new HashMap<>();
	
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	private InputStream contentStream;
	public InputStream getContentStream() {
		return this.contentStream;
	}
	public void setContentStream(InputStream contentStream) {
		this.contentStream = contentStream;
	}
	
	public void addHeader(String key,String value){
		List<String> l = this.headers.remove(key);
		if(l==null) {
			l = new ArrayList<>();
		}
		l.add(value);
		this.headers.put(key, l);
	}
	public void addHeader(String key,List<String> values){
		List<String> l = this.headers.remove(key);
		if(l==null) {
			l = new ArrayList<>();
		}
		l.addAll(values);
		this.headers.put(key, l);
	}
	
	@Deprecated
	public Map<String, String> getHeaders() {
		return TransportUtils.convertToMapSingleValue(this.headers);
	}
	public Map<String, List<String>> getHeadersValues() {
		return this.headers;
	}
	
	@Deprecated
	public String getHeader(String header) {
		return TransportUtils.getObjectAsString(this.headers, header);
	}
	public String getHeader_compactMultipleValues(String name){
		return TransportUtils.getObjectAsString(this.headers, name);
	}
	public String getHeaderFirstValue(String name){
		List<String> l = getHeaderValues(name);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public List<String> getHeaderValues(String header) {
		return TransportUtils.getRawObject(this.headers, header);
	}
	
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
