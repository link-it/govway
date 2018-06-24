/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.message;


import java.util.Properties;

import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ForcedResponseMessage
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ForcedResponseMessage {
	
	private byte[] content;
	private String contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
	private Properties headers;
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
	public Properties getHeaders() {
		return this.headers;
	}
	public void setHeaders(Properties headers) {
		this.headers = headers;
	}
	public String getResponseCode() {
		return this.responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
}
