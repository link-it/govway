package org.openspcoop2.pdd.core.token;

import java.util.Properties;

import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpConstants;

public class WWWAuthenticateGenerator {
	
	public static OpenSPCoop2Message buildErrorMessage(ErrorCode errorCode, String realm, boolean genericError, String error, String ... scope) {
		
		OpenSPCoop2Message errorMessage = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.BINARY, MessageRole.FAULT);
		ForcedResponseMessage forcedResponseMessage = new ForcedResponseMessage();
		forcedResponseMessage.setContent(null); // vuoto
		forcedResponseMessage.setContentType(null); // vuoto
		StringBuffer bf = new StringBuffer(HttpConstants.AUTHORIZATION_PREFIX_BEARER);
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
			forcedResponseMessage.setResponseCode("400");
			if(genericError) {
				bf.append("The request is missing a required token parameter");
			}
			break;
		case invalid_token:
			forcedResponseMessage.setResponseCode("401");
			if(genericError) {
				bf.append("Token invalid");
			}
			break;
		case insufficient_scope:
			forcedResponseMessage.setResponseCode("403");
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
		
		forcedResponseMessage.setHeaders(new Properties());
		forcedResponseMessage.getHeaders().put(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, bf.toString());
		errorMessage.forceResponse(forcedResponseMessage);
		
		return errorMessage;
	}
	
	
}

enum ErrorCode {
	
	 invalid_request,
//     The request is missing a required parameter, includes an
//     unsupported parameter or parameter value, repeats the same
//     parameter, uses more than one method for including an access
//     token, or is otherwise malformed.  The resource server SHOULD
//     respond with the HTTP 400 (Bad Request) status code.

	 invalid_token,
//     The access token provided is expired, revoked, malformed, or
//     invalid for other reasons.  The resource SHOULD respond with
//     the HTTP 401 (Unauthorized) status code.  The client MAY
//     request a new access token and retry the protected resource
//     request.

	 insufficient_scope;
//     The request requires higher privileges than provided by the
//     access token.  The resource server SHOULD respond with the HTTP
//     403 (Forbidden) status code and MAY include the "scope"
//     attribute with the scope necessary to access the protected
//     resource.
}
