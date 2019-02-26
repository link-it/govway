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
	
	protected boolean throwExceptionIfNotFoundConfig = false;
	
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
		 * cors.throwExceptionIfNotFoundConfig=true/false
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
		
		String tmp = p.getProperty("cors.throwExceptionIfNotFoundConfig");
		if(tmp!=null) {
			this.throwExceptionIfNotFoundConfig = "true".equalsIgnoreCase(tmp.trim());
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
	
	public boolean isThrowExceptionIfNotFoundConfig() {
		return this.throwExceptionIfNotFoundConfig;
	}
	public void setThrowExceptionIfNotFoundConfig(boolean throwExceptionIfNotFoundConfig) {
		this.throwExceptionIfNotFoundConfig = throwExceptionIfNotFoundConfig;
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
