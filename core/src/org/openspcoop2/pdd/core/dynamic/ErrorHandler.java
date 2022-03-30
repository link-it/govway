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

package org.openspcoop2.pdd.core.dynamic;

import java.util.List;
import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * ErrorHandler
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorHandler {

	public ErrorHandler() {} // per funzionalità dove la gestione dell'errore non serve
	public ErrorHandler(AbstractErrorGenerator errorGenerator, IntegrationFunctionError functionErrorDefault, Context context) {
		this.errorGenerator = errorGenerator;
		this.functionErrorDefault = functionErrorDefault;
		this.context = context;
	}

	private AbstractErrorGenerator errorGenerator;
	private IntegrationFunctionError functionErrorDefault;
	private Context context;
	
	private boolean error = false;
	private String detail = null;
	private ErrorMessage message = null;
	private OpenSPCoop2Message op2Message = null;
	private IntegrationFunctionError op2IntegrationFunctionError;
	
		
	public String getDetail() {
		return this.detail;
	}
	public boolean isError() {
		return this.error;
	}

	public ErrorMessage getMessage() {
		return this.message;
	}
	public OpenSPCoop2Message getOp2Message() {
		return this.op2Message;
	}
	public IntegrationFunctionError getOp2IntegrationFunctionError() {
		return this.op2IntegrationFunctionError;
	}
	
	public void setMessage(String detail, int responseCode) {
		this._setMessage(detail, null, null, responseCode+"", null);
	}
	public void setMessage(String detail, int responseCode, Map<String, String> headers) {
		this._setMessage(detail, null, null, responseCode+"", TransportUtils.convertToMapListValues(headers));
	}
	public void setMessageWithHeaders(String detail, int responseCode, Map<String, List<String>> headers) {
		this._setMessage(detail, null, null, responseCode+"", headers);
	}
	public void setMessage(String detail, String responseCode) {
		this._setMessage(detail, null, null, responseCode, null);
	}
	public void setMessage(String detail, String responseCode, Map<String, String> headers) {
		this._setMessage(detail, null, null, responseCode, TransportUtils.convertToMapListValues(headers));
	}
	public void setMessageWithHeaders(String detail, String responseCode, Map<String, List<String>> headers) {
		this._setMessage(detail, null, null, responseCode, headers);
	}
	
	public void setMessage(String detail, String content, String contentType, int responseCode) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode+"", null);
	}
	public void setMessage(String detail, String content, String contentType, int responseCode, Map<String, String> headers) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode+"", TransportUtils.convertToMapListValues(headers));
	}
	public void setMessageWithHeaders(String detail, String content, String contentType, int responseCode, Map<String, List<String>> headers) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode+"", headers);
	}
	public void setMessage(String detail, String content, String contentType, String responseCode) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode, null);
	}
	public void setMessage(String detail, String content, String contentType, String responseCode, Map<String, String> headers) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode, TransportUtils.convertToMapListValues(headers));
	}
	public void setMessageWithHeaders(String detail, String content, String contentType, String responseCode, Map<String, List<String>> headers) {
		this._setMessage(detail, content.getBytes(), contentType, responseCode, headers);
	}
	
	public void setMessage(String detail, byte[] content, String contentType, int responseCode) {
		this._setMessage(detail, content, contentType, responseCode+"", null);
	}
	public void setMessage(String detail, byte[] content, String contentType, int responseCode, Map<String, String> headers) {
		this._setMessage(detail, content, contentType, responseCode+"", TransportUtils.convertToMapListValues(headers));
	}
	public void setMessageWithHeaders(String detail, byte[] content, String contentType, int responseCode, Map<String, List<String>> headers) {
		this._setMessage(detail, content, contentType, responseCode+"", headers);
	}
	public void setMessage(String detail, byte[] content, String contentType, String responseCode) {
		this._setMessage(detail, content, contentType, responseCode, null);
	}
	public void setMessage(String detail, byte[] content, String contentType, String responseCode, Map<String, String> headers) {
		this._setMessage(detail, content, contentType, responseCode, TransportUtils.convertToMapListValues(headers));
	}
	public void setMessageWithHeaders(String detail, byte[] content, String contentType, String responseCode, Map<String, List<String>> headers) {
		this._setMessage(detail, content, contentType, responseCode, headers);
	}
	
	public void setError(String detail) throws DynamicException {
		this.setError(detail, this.functionErrorDefault);
	}
	public void setError(String detail, String integrationFunctionError) throws DynamicException {
		this.setError(detail, IntegrationFunctionError.valueOf(integrationFunctionError));
	}
	public void setError(String detail, IntegrationFunctionError integrationFunctionError) throws DynamicException {
		if(this.errorGenerator==null) {
			throw new DynamicException("Funzionalità non supportata");
		}
		this.detail = detail;
		this.op2Message = this.errorGenerator.buildFault(detail, this.context, integrationFunctionError);
		this.op2IntegrationFunctionError = integrationFunctionError;
		this.error = true;
	}
	
	private void _setMessage(String detail, byte[] content, String contentType, String responseCode, Map<String, List<String>> headers) {	
		ErrorMessage messageError = new ErrorMessage();
		messageError.setContent(content);
		messageError.setContentType(contentType);
		messageError.setResponseCode(responseCode);
		messageError.setHeadersValues(headers);
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
