package org.openspcoop2.utils.resources;

import org.openspcoop2.utils.UtilsException;

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
