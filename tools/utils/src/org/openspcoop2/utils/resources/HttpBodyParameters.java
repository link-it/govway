/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.resources;

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
	
	public HttpBodyParameters(String httpMethod, String contentType) throws UtilsException{
		
		String m = httpMethod;
		if(m!=null){
			m = m.trim();
		}
		
		if("OPTIONS".equalsIgnoreCase(m)){
			// If the OPTIONS request includes an entity-body then the media type MUST be indicated by a Content-Type field. 
			if(contentType!=null && !"".equals(contentType)){
				//this.doOutput = true; // non supportato da HttpUrlConnection, throw 'HTTP method OPTIONS doesn't support output'
				this.doOutput = false;
			}
			else{
				this.doOutput = false;
			}
			// body is not defined by this specification, but might be defined by future extensions to HTTP.
			this.doInput = true;
		}
		else if("GET".equalsIgnoreCase(m)){
			this.doOutput = false;
			this.doInput = true;	
		}
		else if("HEAD".equalsIgnoreCase(m)){
			// The HEAD method is identical to GET except that the server MUST NOT return a message-body in the response.
			this.doOutput = false;
			this.doInput = false;	
		}
		else if("POST".equalsIgnoreCase(m)){
			this.doOutput = true;
			this.doInput = true;	
		}
		else if("PUT".equalsIgnoreCase(m)){
			this.doOutput = true;
			this.doInput = true;	
		}
		else if("DELETE".equalsIgnoreCase(m)){
			this.doOutput = false;
			this.doInput = true;	
		}
		else if("TRACE".equalsIgnoreCase(m)){
			// A TRACE request MUST NOT include an entity
			this.doOutput = false; 
			this.doInput = true;	
		}
		else{
			throw new UtilsException("HttpMethod ["+httpMethod+"] unsupported");
		}
		
	}

	public boolean isDoOutput() {
		return this.doOutput;
	}

	public boolean isDoInput() {
		return this.doInput;
	}

}
