package org.openspcoop2.message.utils;

public enum WWWAuthenticateErrorCode {

		 invalid_request,
//	     The request is missing a required parameter, includes an
//	     unsupported parameter or parameter value, repeats the same
//	     parameter, uses more than one method for including an access
//	     token, or is otherwise malformed.  The resource server SHOULD
//	     respond with the HTTP 400 (Bad Request) status code.

		 invalid_token,
//	     The access token provided is expired, revoked, malformed, or
//	     invalid for other reasons.  The resource SHOULD respond with
//	     the HTTP 401 (Unauthorized) status code.  The client MAY
//	     request a new access token and retry the protected resource
//	     request.

		 insufficient_scope;
//	     The request requires higher privileges than provided by the
//	     access token.  The resource server SHOULD respond with the HTTP
//	     403 (Forbidden) status code and MAY include the "scope"
//	     attribute with the scope necessary to access the protected
//	     resource.
	
}
