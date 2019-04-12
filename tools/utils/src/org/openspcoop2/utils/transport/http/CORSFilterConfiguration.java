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
package org.openspcoop2.utils.transport.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * CORSFilterConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CORSFilterConfiguration {

	// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/

	protected boolean errorAsDebug = true;
	
	// indicazione se una richiesta invalida deve cmq essere continuata ad essere gestita (e pilotata con le opzioni seguenti)
	protected boolean throwExceptionIfInvalid = false;
	protected boolean terminateIfInvalid = true; 
	
	// indica un utilizzo errato del cors come descritto nel capitolo 6.1.1 e 6.2.1 (https://www.w3.org/TR/cors/#resource-processing-model)
	// If the Origin header is not present terminate this set of steps. The request is outside the scope of this specification.
	protected boolean throwExceptionIfNotFoundOrigin = false;
	protected boolean terminateIfNotFoundOrigin = false;
	
	// indica un utilizzo errato del cors come descritto nel capitolo 6.1.2 e 6.2.2 (https://www.w3.org/TR/cors/#resource-processing-model)
	// If the value of the Origin header is not a case-sensitive match for any of the values in list of origins, 
	// do not set any additional headers and terminate this set of steps.
	protected boolean throwExceptionIfNotMatchOrigin = false;
	protected boolean terminateIfNotMatchOrigin = false;

	// indica una configurazione errata come descritto nel capitolo 6.1.3 e 6.2.7 (https://www.w3.org/TR/cors/#resource-processing-model)
	// The string "*" (Access-Control-Allow-Origin) cannot be used for a resource that supports credentials (Access-Control-Allow-Credentials=true).
	protected boolean throwExceptionIfAllowCredentialAndAllowOrigin = false;

	// indica un utilizzo errato del cors come descritto nel capitolo 6.3.3 (https://www.w3.org/TR/cors/#resource-processing-model)
	// If there is no Access-Control-Request-Method header or if parsing failed, do not set any additional headers and terminate this set of steps. 
	// The request is outside the scope of this specification.
	protected boolean throwExceptionIfNotFoundRequestMethod = false;
	protected boolean terminateIfNotFoundRequestMethod = false;
	
	// indica un utilizzo errato del cors come descritto nel capitolo 6.3.5 (https://www.w3.org/TR/cors/#resource-processing-model)
	// If method is not a case-sensitive match for any of the values in list of methods do not set any additional headers and terminate this set of steps.
	protected boolean throwExceptionIfNotMatchRequestMethod = false;
	protected boolean terminateIfNotMatchRequestMethod = false;
	
	// indica se produrre la lista dei metodi supportati, anche se la richiesta non presenta un Access-Control-Request-Method header o il valore presente non è permesso.
	// Se non produco alcun header in risposta, sarà il browser a riconoscere che non e' abilitato, cosi come se ritorna una lista non contenente il metodo richiesto
	protected boolean generateListAllowIfNotMatchRequestMethod = true;
	
	// indica un utilizzo errato del cors come descritto nel capitolo 6.3.4 (https://www.w3.org/TR/cors/#resource-processing-model)
	// If parsing failed do not set any additional headers and terminate this set of steps.
	// The request is outside the scope of this specification.
	protected boolean throwExceptionIfNotFoundRequestHeaders = false;
	protected boolean terminateIfNotFoundRequestHeaders = false;
	
	// indica un utilizzo errato del cors come descritto nel capitolo 6.3.6 (https://www.w3.org/TR/cors/#resource-processing-model)
	// If any of the header field-names is not a ASCII case-insensitive match for any of the values in list of headers do not set any additional headers and terminate this set of steps.
	protected boolean throwExceptionIfNotMatchRequestHeaders = false;
	protected boolean terminateIfNotMatchRequestHeaders = false;
	
	// indica se produrre la lista degli headers supportati, anche se la richiesta non presenta un Access-Control-Request-Header header o un degli header presenti non è permesso.
	// Se non produco alcun header in risposta, sarà il browser a riconoscere che non e' abilitato, cosi come se ritorna una lista non contenente l'header richiesto
	protected boolean generateListAllowIfNotMatchRequestHeaders = true;
	
	
	
	/*
	 * The Access-Control-Allow-Credentials response header indicates whether or not the response to the request can be exposed to the page. 
	 * It can be exposed when the true value is returned.
	 * Credentials are cookies, authorization headers or TLS client certificates.
	 * When used as part of a response to a preflight request, this indicates whether or not the actual request can be made using credentials. 
	 * Note that simple GET requests are not preflighted, and so if a request is made for a resource with credentials, 
	 * if this header is not returned with the resource, the response is ignored by the browser and not returned to web content.
	 *
	 * NOTE: The only valid value for this header is true (case-sensitive). If you don't need credentials, omit this header entirely (rather than setting its value to false).
	 **/
	protected Boolean allowCredentials = null;

	/*
	 * The Access-Control-Request-Headers request header is used when issuing a preflight request to let the server know which HTTP headers will be used when the actual request is made.
	 **/
	protected Boolean allowRequestHeader = null;
	/*
	 * The Access-Control-Allow-Headers response header is used in response to a preflight request which includes the Access-Control-Request-Headers to indicate which HTTP headers can be used during the actual request.
	 * The simple headers, Accept, Accept-Language, Content-Language, Content-Type (but only with a MIME type of its parsed value (ignoring parameters) of either application/x-www-form-urlencoded, multipart/form-data, or text/plain), 
	 * are always available and don't need to be listed by this header.
	 * 
	 * This header is required if the request has an Access-Control-Request-Headers header.
	 **/
	protected List<String> allowHeaders = new ArrayList<>(); 
	
	/*
	 * Oltre all'header Access-Control-Allow-Headers response header viene generato anche l'header Allow con medesimi valori
	 */
	protected boolean generateAllowHeader = false;
	
	/*
	 * The Access-Control-Request-Method request header is used when issuing a preflight request to let the server know which HTTP method will be used when the actual request is made. 
	 * This header is necessary as the preflight request is always an OPTIONS and doesn't use the same method as the actual request.
	 **/
	protected Boolean allowRequestMethod = null;
	/*
	 * The Access-Control-Allow-Methods response header specifies the method or methods allowed when accessing the resource in response to a preflight request.
	 **/
	protected List<String> allowMethods = new ArrayList<>(); 
	

	/*
	 * The Access-Control-Allow-Origin response header indicates whether the response can be shared with requesting code from the given origin.
	 * 
	 * Access-Control-Allow-Origin: <origin>
	 * Specifies an origin. Only a single origin can be specified.
	 * 
	 * !! CORS and caching !! If the server sends a response with an Access-Control-Allow-Origin value that is an explicit origin (rather than the "*" wildcard), 
	 * then the response should also include a Vary response header with the value Origin to indicate to browsers that server responses can differ based 
	 * on the value of the Origin request header.
	 * 
	 * example request:
	 * Origin: https://developer.mozilla.org
	 * 
	 * example response:
	 * Access-Control-Allow-Origin: https://developer.mozilla.org
	 * Vary: Origin
	 **/
	protected Boolean allowRequestOrigin = null;
	/*
	 * Access-Control-Allow-Origin: *
	 * 
	 * For requests without credentials, the literal value "*" can be specified, as a wildcard; 
	 * the value tells browsers to allow requesting code from any origin to access the resource. 
	 * Attempting to use the wildcard with credentials will result in an error.
	 **/
	protected Boolean allowAllOrigin = null;
	/*
	 * The Access-Control-Allow-Origin enabled.
	 **/
	protected List<String> allowOrigins = new ArrayList<>(); 


	/*
	 * The Access-Control-Expose-Headers response header indicates which headers can be exposed as part of the response by listing their names.
	 * By default, only the 6 simple response headers are exposed: Cache-Control, Content-Language, Content-Type, Expires, Last-Modified, Pragma
	 * If you want clients to be able to access other headers, you have to list them using the Access-Control-Expose-Headers header.
	 **/
	protected List<String> exposeHeaders = new ArrayList<>();
	
	/*
	 * The Access-Control-Max-Age response header indicates how long the results of a preflight request 
	 * (that is the information contained in the Access-Control-Allow-Methods and Access-Control-Allow-Headers headers) can be cached.
	 * 
	 * Maximum number of seconds the results can be cached. Firefox caps this at 24 hours (86400 seconds) and Chromium at 10 minutes (600 seconds). 
	 * Chromium also specifies a default value of 5 seconds. 
	 **/
	protected Integer cachingAccessControl_maxAgeSeconds = null;
	
	/*
	 * A value of -1 will disable caching, requiring a preflight OPTIONS check for all calls.
	 **/
	protected Boolean cachingAccessControl_disable = null;


	
	public void init(Properties p) {
						
		/*
		 * cors.errorAsDebug=true/false
		 * 
		 * cors.throwExceptionIfInvalid=true/false
		 * cors.terminateIfInvalid=true/false
		 * 
		 * cors.throwExceptionIfNotFoundOrigin=true/false
		 * cors.terminateIfNotFoundOrigin=true/false
		 * cors.throwExceptionIfNotMatchOrigin=true/false
		 * cors.terminateIfNotMatchOrigin=true/false
		 * 
		 * cors.throwExceptionIfAllowCredentialAndAllowOrigin=true/false
		 * 
		 * cors.throwExceptionIfNotFoundRequestMethod=true/false
		 * cors.terminateIfNotFoundRequestMethod=true/false
		 * cors.throwExceptionIfNotMatchRequestMethod=true/false
		 * cors.terminateIfNotMatchRequestMethod=true/false
		 * cors.generateListAllowIfNotMatchRequestMethod=true/false
		 * 
		 * cors.throwExceptionIfNotFoundRequestHeaders=true/false
		 * cors.terminateIfNotFoundRequestHeaders=true/false
		 * cors.throwExceptionIfNotMatchRequestHeaders=true/false
		 * cors.terminateIfNotMatchRequestHeaders=true/false
		 * cors.generateListAllowIfNotMatchRequestHeaders=true/false
		 * 
		 * 
		 * cors.allowCredentials=true/false
		 * 
		 * cors.allowRequestHeaders=true/false
		 * cors.allowHeaders=HDR1,...,HDRN
		 * 
		 * cors.allowRequestMethod=true/false
		 * cors.allowMethods=METHOD1,...,METHODN
		 * 
		 * cors.allowRequestOrigin=true/false
		 * cors.allowAllOrigin=true/false
		 * cors.allowOrigins=http://origin1, ... ,http://originN
		 * 
		 * cors.exposeHeaders=HDR1,...,HDRN
		 * 
		 * cors.maxAge.cacheDisable=true/false
		 * cors.maxAge.seconds=
		 * 
		 **/
		
		String tmp = p.getProperty("cors.errorAsDebug");
		if(tmp!=null) {
			this.errorAsDebug = "true".equalsIgnoreCase(tmp.trim());
		}
		
		tmp = p.getProperty("cors.throwExceptionIfInvalid");
		if(tmp!=null) {
			this.throwExceptionIfInvalid = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfInvalid");
		if(tmp!=null) {
			this.terminateIfInvalid = "true".equalsIgnoreCase(tmp.trim());
		}	
		
		tmp = p.getProperty("cors.throwExceptionIfNotFoundOrigin");
		if(tmp!=null) {
			this.throwExceptionIfNotFoundOrigin = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfNotFoundOrigin");
		if(tmp!=null) {
			this.terminateIfNotFoundOrigin = "true".equalsIgnoreCase(tmp.trim());
		}		
		tmp = p.getProperty("cors.throwExceptionIfNotMatchOrigin");
		if(tmp!=null) {
			this.throwExceptionIfNotMatchOrigin = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfNotMatchOrigin");
		if(tmp!=null) {
			this.terminateIfNotMatchOrigin = "true".equalsIgnoreCase(tmp.trim());
		}
		
		tmp = p.getProperty("cors.throwExceptionIfAllowCredentialAndAllowOrigin");
		if(tmp!=null) {
			this.throwExceptionIfAllowCredentialAndAllowOrigin = "true".equalsIgnoreCase(tmp.trim());
		}
			
		tmp = p.getProperty("cors.throwExceptionIfNotFoundRequestMethod");
		if(tmp!=null) {
			this.throwExceptionIfNotFoundRequestMethod = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfNotFoundRequestMethod");
		if(tmp!=null) {
			this.terminateIfNotFoundRequestMethod = "true".equalsIgnoreCase(tmp.trim());
		}		
		tmp = p.getProperty("cors.throwExceptionIfNotMatchRequestMethod");
		if(tmp!=null) {
			this.throwExceptionIfNotMatchRequestMethod = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfNotMatchRequestMethod");
		if(tmp!=null) {
			this.terminateIfNotMatchRequestMethod = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.generateListAllowIfNotMatchRequestMethod");
		if(tmp!=null) {
			this.generateListAllowIfNotMatchRequestMethod = "true".equalsIgnoreCase(tmp.trim());
		}
		
		tmp = p.getProperty("cors.throwExceptionIfNotFoundRequestHeaders");
		if(tmp!=null) {
			this.throwExceptionIfNotFoundRequestHeaders = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfNotFoundRequestHeaders");
		if(tmp!=null) {
			this.terminateIfNotFoundRequestHeaders = "true".equalsIgnoreCase(tmp.trim());
		}		
		tmp = p.getProperty("cors.throwExceptionIfNotMatchRequestHeaders");
		if(tmp!=null) {
			this.throwExceptionIfNotMatchRequestHeaders = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.terminateIfNotMatchRequestHeaders");
		if(tmp!=null) {
			this.terminateIfNotMatchRequestHeaders = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.generateListAllowIfNotMatchRequestHeaders");
		if(tmp!=null) {
			this.generateListAllowIfNotMatchRequestHeaders = "true".equalsIgnoreCase(tmp.trim());
		}

		
		tmp = p.getProperty("cors.allowCredentials");
		if(tmp!=null) {
			this.allowCredentials = "true".equalsIgnoreCase(tmp.trim());
		}
		
		tmp = p.getProperty("cors.allowRequestHeaders");
		if(tmp!=null) {
			this.allowRequestHeader = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.allowHeaders");
		if(tmp!=null) {
			String [] tmpList = tmp.trim().split(",");
			for (String v : tmpList) {
				this.allowHeaders.add(v.trim());
			}
		}
		
		tmp = p.getProperty("cors.allowRequestMethod");
		if(tmp!=null) {
			this.allowRequestMethod = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.allowMethods");
		if(tmp!=null) {
			String [] tmpList = tmp.trim().split(",");
			for (String v : tmpList) {
				this.allowMethods.add(v.trim());
			}
		}
		
		tmp = p.getProperty("cors.allowRequestOrigin");
		if(tmp!=null) {
			this.allowRequestOrigin = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.allowAllOrigin");
		if(tmp!=null) {
			this.allowAllOrigin = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.allowOrigins");
		if(tmp!=null) {
			String [] tmpList = tmp.trim().split(",");
			for (String v : tmpList) {
				this.allowOrigins.add(v.trim());
			}
		}
		
		tmp = p.getProperty("cors.exposeHeaders");
		if(tmp!=null) {
			String [] tmpList = tmp.trim().split(",");
			for (String v : tmpList) {
				this.exposeHeaders.add(v.trim());
			}
		}
		
		tmp = p.getProperty("cors.maxAge.cacheDisable");
		if(tmp!=null) {
			this.cachingAccessControl_disable = "true".equalsIgnoreCase(tmp.trim());
		}
		tmp = p.getProperty("cors.maxAge.seconds");
		if(tmp!=null) {
			this.cachingAccessControl_maxAgeSeconds = Integer.valueOf(tmp.trim());
		}
		
	}

	public boolean isErrorAsDebug() {
		return this.errorAsDebug;
	}
	public void setErrorAsDebug(boolean errorAsDebug) {
		this.errorAsDebug = errorAsDebug;
	}

	public boolean isThrowExceptionIfInvalid() {
		return this.throwExceptionIfInvalid;
	}
	public void setThrowExceptionIfInvalid(boolean throwExceptionIfInvalid) {
		this.throwExceptionIfInvalid = throwExceptionIfInvalid;
	}

	public boolean isTerminateIfInvalid() {
		return this.terminateIfInvalid;
	}
	public void setTerminateIfInvalid(boolean terminateIfInvalid) {
		this.terminateIfInvalid = terminateIfInvalid;
	}

	
	public boolean isThrowExceptionIfNotFoundOrigin() {
		return this.throwExceptionIfNotFoundOrigin;
	}
	public void setThrowExceptionIfNotFoundOrigin(boolean throwExceptionIfNotFoundOrigin) {
		this.throwExceptionIfNotFoundOrigin = throwExceptionIfNotFoundOrigin;
	}

	public boolean isTerminateIfNotFoundOrigin() {
		return this.terminateIfNotFoundOrigin;
	}
	public void setTerminateIfNotFoundOrigin(boolean terminateIfNotFoundOrigin) {
		this.terminateIfNotFoundOrigin = terminateIfNotFoundOrigin;
	}


	public boolean isThrowExceptionIfNotMatchOrigin() {
		return this.throwExceptionIfNotMatchOrigin;
	}
	public void setThrowExceptionIfNotMatchOrigin(boolean throwExceptionIfNotMatchOrigin) {
		this.throwExceptionIfNotMatchOrigin = throwExceptionIfNotMatchOrigin;
	}

	public boolean isTerminateIfNotMatchOrigin() {
		return this.terminateIfNotMatchOrigin;
	}
	public void setTerminateIfNotMatchOrigin(boolean terminateIfNotMatchOrigin) {
		this.terminateIfNotMatchOrigin = terminateIfNotMatchOrigin;
	}


	
	public boolean isThrowExceptionIfAllowCredentialAndAllowOrigin() {
		return this.throwExceptionIfAllowCredentialAndAllowOrigin;
	}
	public void setThrowExceptionIfAllowCredentialAndAllowOrigin(boolean throwExceptionIfAllowCredentialAndAllowOrigin) {
		this.throwExceptionIfAllowCredentialAndAllowOrigin = throwExceptionIfAllowCredentialAndAllowOrigin;
	}



	public boolean isThrowExceptionIfNotFoundRequestMethod() {
		return this.throwExceptionIfNotFoundRequestMethod;
	}
	public void setThrowExceptionIfNotFoundRequestMethod(boolean throwExceptionIfNotFoundRequestMethod) {
		this.throwExceptionIfNotFoundRequestMethod = throwExceptionIfNotFoundRequestMethod;
	}

	public boolean isTerminateIfNotFoundRequestMethod() {
		return this.terminateIfNotFoundRequestMethod;
	}
	public void setTerminateIfNotFoundRequestMethod(boolean terminateIfNotFoundRequestMethod) {
		this.terminateIfNotFoundRequestMethod = terminateIfNotFoundRequestMethod;
	}


	public boolean isThrowExceptionIfNotMatchRequestMethod() {
		return this.throwExceptionIfNotMatchRequestMethod;
	}
	public void setThrowExceptionIfNotMatchRequestMethod(boolean throwExceptionIfNotMatchRequestMethod) {
		this.throwExceptionIfNotMatchRequestMethod = throwExceptionIfNotMatchRequestMethod;
	}

	public boolean isTerminateIfNotMatchRequestMethod() {
		return this.terminateIfNotMatchRequestMethod;
	}
	public void setTerminateIfNotMatchRequestMethod(boolean terminateIfNotMatchRequestMethod) {
		this.terminateIfNotMatchRequestMethod = terminateIfNotMatchRequestMethod;
	}

	
	public boolean isGenerateListAllowIfNotMatchRequestMethod() {
		return this.generateListAllowIfNotMatchRequestMethod;
	}
	public void setGenerateListAllowIfNotMatchRequestMethod(boolean generateListAllowIfNotMatchRequestMethod) {
		this.generateListAllowIfNotMatchRequestMethod = generateListAllowIfNotMatchRequestMethod;
	}

	
	public boolean isThrowExceptionIfNotFoundRequestHeaders() {
		return this.throwExceptionIfNotFoundRequestHeaders;
	}
	public void setThrowExceptionIfNotFoundRequestHeaders(boolean throwExceptionIfNotFoundRequestHeaders) {
		this.throwExceptionIfNotFoundRequestHeaders = throwExceptionIfNotFoundRequestHeaders;
	}

	public boolean isTerminateIfNotFoundRequestHeaders() {
		return this.terminateIfNotFoundRequestHeaders;
	}
	public void setTerminateIfNotFoundRequestHeaders(boolean terminateIfNotFoundRequestHeaders) {
		this.terminateIfNotFoundRequestHeaders = terminateIfNotFoundRequestHeaders;
	}


	public boolean isThrowExceptionIfNotMatchRequestHeaders() {
		return this.throwExceptionIfNotMatchRequestHeaders;
	}
	public void setThrowExceptionIfNotMatchRequestHeaders(boolean throwExceptionIfNotMatchRequestHeaders) {
		this.throwExceptionIfNotMatchRequestHeaders = throwExceptionIfNotMatchRequestHeaders;
	}

	public boolean isTerminateIfNotMatchRequestHeaders() {
		return this.terminateIfNotMatchRequestHeaders;
	}
	public void setTerminateIfNotMatchRequestHeaders(boolean terminateIfNotMatchRequestHeaders) {
		this.terminateIfNotMatchRequestHeaders = terminateIfNotMatchRequestHeaders;
	}
	
	
	public boolean isGenerateListAllowIfNotMatchRequestHeaders() {
		return this.generateListAllowIfNotMatchRequestHeaders;
	}
	public void setGenerateListAllowIfNotMatchRequestHeaders(boolean generateListAllowIfNotMatchRequestHeaders) {
		this.generateListAllowIfNotMatchRequestHeaders = generateListAllowIfNotMatchRequestHeaders;
	}
	

	
	public Boolean getAllowCredentials() {
		return this.allowCredentials;
	}
	public void setAllowCredentials(Boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}
	
	public Boolean getAllowRequestHeader() {
		return this.allowRequestHeader;
	}
	public void setAllowRequestHeader(Boolean allowRequestHeader) {
		this.allowRequestHeader = allowRequestHeader;
	}
	
	public List<String> getAllowHeaders() {
		return this.allowHeaders;
	}
	public void addAllowHeader(String header) {
		this.allowHeaders.add(header);
	}
	
	public boolean isGenerateAllowHeader() {
		return this.generateAllowHeader;
	}
	public void setGenerateAllowHeader(boolean generateAllowHeader) {
		this.generateAllowHeader = generateAllowHeader;
	}
	
	public Boolean getAllowRequestMethod() {
		return this.allowRequestMethod;
	}
	public void setAllowRequestMethod(Boolean allowRequestMethod) {
		this.allowRequestMethod = allowRequestMethod;
	}
	
	public List<String> getAllowMethods() {
		return this.allowMethods;
	}
	public void addAllowMethod(String method) {
		this.allowMethods.add(method);
	}
	
	public Boolean getAllowRequestOrigin() {
		return this.allowRequestOrigin;
	}
	public void setAllowRequestOrigin(Boolean allowRequestOrigin) {
		this.allowRequestOrigin = allowRequestOrigin;
	}
	
	public Boolean getAllowAllOrigin() {
		return this.allowAllOrigin;
	}
	public void setAllowAllOrigin(Boolean allowAllOrigin) {
		this.allowAllOrigin = allowAllOrigin;
	}
	
	public List<String> getAllowOrigins() {
		return this.allowOrigins;
	}
	public void addAllowOrigin(String origin) {
		this.allowOrigins.add(origin);
	}
	
	public List<String> getExposeHeaders() {
		return this.exposeHeaders;
	}
	public void addExposeHeader(String header) {
		this.exposeHeaders.add(header);
	}
	
	public Integer getCachingAccessControl_maxAgeSeconds() {
		return this.cachingAccessControl_maxAgeSeconds;
	}
	public void setCachingAccessControl_maxAgeSeconds(Integer cachingAccessControl_maxAgeSeconds) {
		this.cachingAccessControl_maxAgeSeconds = cachingAccessControl_maxAgeSeconds;
	}
	
	public Boolean getCachingAccessControl_disable() {
		return this.cachingAccessControl_disable;
	}
	public void setCachingAccessControl_disable(Boolean cachingAccessControl_disable) {
		this.cachingAccessControl_disable = cachingAccessControl_disable;
	}
	
}
