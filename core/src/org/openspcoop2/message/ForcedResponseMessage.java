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

package org.openspcoop2.message;


import java.util.Map;

import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ForcedResponseMessage
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ForcedResponseMessage {
	
	public ForcedResponseMessage() {}
	public ForcedResponseMessage(AbstractBaseOpenSPCoop2Message msg) {
		if(msg.forceTransportHeaders!=null && !msg.forceTransportHeaders.isEmpty()) {
			this.headers = msg.forceTransportHeaders;
		}
	}
	
	private byte[] content;
	private String contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
	private Map<String, String> headers;
	private String responseCode;
	
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Map<String, String> getHeaders() {
		return this.headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public String getResponseCode() {
		return this.responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
}
