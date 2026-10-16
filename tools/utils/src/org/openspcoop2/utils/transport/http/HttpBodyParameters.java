/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.UtilsException;

/**
 * HttpBodyParameters
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpBodyParameters {

	private boolean doOutput = false;
	private boolean doInput = false;
	
	public HttpBodyParameters(HttpRequestMethod httpMethod, String contentType) throws UtilsException{
		

		switch (httpMethod) {
		
			// standard
		
			case OPTIONS:
				// RFC 9110 9.3.7: A client that generates an OPTIONS request containing content MUST send a valid Content-Type header field describing the representation media type.
				// Il contenuto viene quindi inoltrato solamente in presenza del Content-Type (vedi controllo sottostante)
				this.doOutput = true;
				// body is not defined by this specification, but might be defined by future extensions to HTTP.
				this.doInput = true;
				break;

			case GET:
				this.doOutput = false;
				this.doInput = true;	
				break;

			case HEAD:
				// The HEAD method is identical to GET except that the server MUST NOT return a message-body in the response.
				this.doOutput = false;
				this.doInput = false;	
				break;
				
			case POST:
				this.doOutput = true;
				this.doInput = true;
				break;
				
			case PUT:
				this.doOutput = true;
				this.doInput = true;
				break;
				
			case DELETE:
				this.doOutput = false;
				this.doInput = true;
				break;
				
			case TRACE:
				// A TRACE request MUST NOT include an entity
				this.doOutput = false; 
				this.doInput = true;
				break;

			// additional: https://tools.ietf.org/html/rfc2068#section-19.6.1
				
			case PATCH:
				// The PATCH method is similar to PUT
				this.doOutput = true;
				this.doInput = true;
				break;
				
			case LINK:
				// The PATCH method is similar to PUT
				this.doOutput = true;
				this.doInput = true;
				break;
				
			case UNLINK:
				// The PATCH method is similar to PUT
				this.doOutput = true;
				this.doInput = true;
				break;
				
			default:
				throw new UtilsException("HttpMethod ["+httpMethod+"] unsupported");
		}
		
		if(contentType==null || "".equals(contentType)){
			this.doOutput = false;
		}
		
	}

	public boolean isDoOutput() {
		return this.doOutput;
	}

	/**
	 * Metodi per i quali la specifica HTTP non definisce una semantica del contenuto della richiesta
	 * (RFC 9110 9.3.1 GET, 9.3.2 HEAD, 9.3.5 DELETE): il contenuto non viene inoltrato per default.
	 */
	public static boolean isContentWithoutDefinedSemantics(HttpRequestMethod httpMethod) {
		return HttpRequestMethod.GET.equals(httpMethod) ||
				HttpRequestMethod.HEAD.equals(httpMethod) ||
				HttpRequestMethod.DELETE.equals(httpMethod);
	}

	public boolean isDoInput() {
		return this.doInput;
	}

}
