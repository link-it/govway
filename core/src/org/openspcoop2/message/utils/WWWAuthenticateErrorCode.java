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

package org.openspcoop2.message.utils;

/**     
 * WWWAuthenticateErrorCode
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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
