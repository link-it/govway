/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.transport.TransportUtils;

/**
 * Messaggio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioMultipartInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4718160136521047108L;
		
	private String contentId;
	private String contentLocation;
	private String contentType;
	private Map<String, List<String>> headers = new HashMap<>();
	
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
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

	@Deprecated
	public Map<String, String> getHeaders() {
		return TransportUtils.convertToMapSingleValue(this.headers);
	}
	public Map<String, List<String>> getHeadersValues() {
		return this.headers;
	}

	@Deprecated
	public void setHeaders(Map<String, String> headers) {
		this.headers = TransportUtils.convertToMapListValues(headers);
	}
	public void setHeadersValues(Map<String, List<String>> headers) {
		this.headers = headers;
	}
	
}
