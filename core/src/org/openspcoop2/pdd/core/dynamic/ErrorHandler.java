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

package org.openspcoop2.pdd.core.dynamic;

import java.util.Properties;

/**
 * ErrorHandler
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorHandler {

	private boolean error = false;
	private ErrorMessage message = null;
	private String detail = null;
	
	public String getDetail() {
		return this.detail;
	}
	public boolean isError() {
		return this.error;
	}

	public ErrorMessage getMessage() {
		return this.message;
	}
	
	public void setMessage(String detail, int responseCode) {
		this._setMessage(detail, null, null, responseCode+"", null);
	}
	public void setMessage(String detail, int responseCode, Properties headers) {
		this._setMessage(detail, null, null, responseCode+"", headers);
	}
	public void setMessage(String detail, String responseCode) {
		this._setMessage(detail, null, null, responseCode, null);
	}
	public void setMessage(String detail, String responseCode, Properties headers) {
		this._setMessage(detail, null, null, responseCode, headers);
	}
	
	public void setMessage(String detail, String content, String contentType, int responseCode) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode+"", null);
	}
	public void setMessage(String detail, String content, String contentType, int responseCode, Properties headers) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode+"", headers);
	}
	public void setMessage(String detail, String content, String contentType, String responseCode) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode, null);
	}
	public void setMessage(String detail, String content, String contentType, String responseCode, Properties headers) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode, headers);
	}
	
	public void setMessage(String detail, byte[] content, String contentType, int responseCode) {
		this._setMessage(detail, content, contentType, responseCode+"", null);
	}
	public void setMessage(String detail, byte[] content, String contentType, int responseCode, Properties headers) {
		this._setMessage(detail, content, contentType, responseCode+"", headers);
	}
	public void setMessage(String detail, byte[] content, String contentType, String responseCode) {
		this._setMessage(detail, content, contentType, responseCode, null);
	}
	public void setMessage(String detail, byte[] content, String contentType, String responseCode, Properties headers) {
		this._setMessage(detail, content, contentType, responseCode, headers);
	}
	
	private void _setMessage(String detail, byte[] content, String contentType, String responseCode, Properties headers) {	
		ErrorMessage messageError = new ErrorMessage();
		messageError.setContent(content);
		messageError.setContentType(contentType);
		messageError.setResponseCode(responseCode);
		messageError.setHeaders(headers);
		this.setMessage(detail, messageError);
	}
	public void setMessage(String detail, ErrorMessage message) {
		this.detail = detail;
		this.message = message;
		if(message!=null) {
			this.error = true;
		}
		else {
			this.error = false;
		}
	}
	
}
