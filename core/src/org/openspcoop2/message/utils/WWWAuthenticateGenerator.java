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

package org.openspcoop2.message.utils;

import java.util.HashMap;

import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**     
 * WWWAuthenticateGenerator
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WWWAuthenticateGenerator {
	
	public static OpenSPCoop2Message buildErrorMessage(WWWAuthenticateErrorCode errorCode, String realm, boolean genericError, String error, String ... scope) {
		
		OpenSPCoop2Message errorMessage = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createEmptyMessage(MessageType.BINARY, MessageRole.FAULT);
		ForcedResponseMessage forcedResponseMessage = new ForcedResponseMessage();
		forcedResponseMessage.setContent(null); // vuoto
		forcedResponseMessage.setContentType(null); // vuoto
		forcedResponseMessage.setResponseCode(getReturnCode(errorCode)+"");	
		forcedResponseMessage.setHeaders(new HashMap<String, String>());
		String headerValue = buildHeaderValue(errorCode, realm, genericError, error, scope);
		forcedResponseMessage.getHeaders().put(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, headerValue);
		errorMessage.forceResponse(forcedResponseMessage);
		
		return errorMessage;
	}
	
	public static int getReturnCode(WWWAuthenticateErrorCode errorCode) {
		switch (errorCode) {
		case invalid_request:
			return 400;
		case invalid_token:
			return 401;
		case insufficient_scope:
			return 403;
		}
		return 500;
	}
	
	public static String buildHeaderValue(WWWAuthenticateErrorCode errorCode, String realm, boolean genericError, String error, String ... scope) {
		
		StringBuilder bf = new StringBuilder(HttpConstants.AUTHORIZATION_PREFIX_BEARER);
		bf.append("realm=\"");
		bf.append(realm);
		bf.append("\", error=\"");
		bf.append(errorCode.name());
		bf.append("\", error_description=\"");
		if(genericError==false) {
			bf.append(error);
		}
		switch (errorCode) {
		case invalid_request:
			if(genericError) {
				bf.append("The request is missing a required token parameter");
			}
			break;
		case invalid_token:
			if(genericError) {
				bf.append("Token invalid");
			}
			break;
		case insufficient_scope:
			if(genericError) {
				bf.append("The request requires higher privileges than provided by the access token");
			}
			break;
		}
		bf.append("\"");
		if(scope!=null && scope.length>0) {
			bf.append(", scope=\"");
			for (int i = 0; i < scope.length; i++) {
				if(i>0) {
					bf.append(",");
				}
				bf.append(scope[i]);	
			}
			bf.append("\"");
		}
		
		return bf.toString();
	}
	
	public static String buildBasicHeaderValue(String realm) {
		
		StringBuilder bf = new StringBuilder(HttpConstants.AUTHORIZATION_PREFIX_BASIC);
		bf.append("realm=\"");
		bf.append(realm);
		bf.append("\"");
		return bf.toString();
	}
	
}

