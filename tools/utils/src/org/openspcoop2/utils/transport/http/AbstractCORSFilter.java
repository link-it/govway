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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;

/**
 * AbstractCORSFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCORSFilter implements javax.servlet.Filter {

	public final static String CORS_REQUEST_TYPE = "CORSRequestType";
	
	protected abstract CORSFilterConfiguration getConfig() throws IOException;
	protected abstract Logger getLog();

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest servletReq, ServletResponse servletRes, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) servletReq;
		HttpServletResponse res = (HttpServletResponse) servletRes;

		this.doCORS(req, res);

		chain.doFilter(servletReq, servletRes);
	}

	public void doCORS(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		Logger log = this.getLog();
		
		CORSRequestType requestType = getRequestType(req, log);
		
		this.doCORS(req, res, requestType);
	}
	
	public void doCORS(HttpServletRequest req, HttpServletResponse res, CORSRequestType requestType) throws IOException {

		// https://www.w3.org/TR/cors/#resource-processing-model
		
		/* 
		 * Chapter 6:
		 * 
		 * The resource sharing policy described by this specification is bound to a particular resource. 
		 * For the purposes of this section each resource is bound to the following:
		 * - A list of origins consisting of zero or more origins that are allowed access to the resource.
		 *   This can include the origin of the resource itself though be aware that requests to cross-origin resources can be redirected back to the resource.
		 *   
		 * - A list of methods consisting of zero or more methods that are supported by the resource.
		 *
		 * - A list of headers consisting of zero or more header field names that are supported by the resource.
		 * 
		 * - A list of exposed headers consisting of zero or more header field names of headers other than the simple response headers that the resource might use and can be exposed.
		 *
		 * - A supports credentials flag that indicates whether the resource supports user credentials in the request. It is true when the resource does and false otherwise.
		 **/
		CORSFilterConfiguration config = this.getConfig();
		
		Logger log = this.getLog();

		req.setAttribute(CORS_REQUEST_TYPE, requestType);
		
		if(CORSRequestType.INVALID.equals(requestType)) {
			if(config.throwExceptionIfInvalid || config.terminateIfInvalid) {
				String msgError = "CORSE Configuration error: the request is invalid";
				log.error(msgError);
				if(config.throwExceptionIfInvalid) {
					throw new IOException(msgError);
				}
				else {
					return;
				}
			}
		}
		
		
		
		// Di seguito sono state implementate le logiche che riguardano le sezioni:
		// * 6.1 Simple Cross-Origin Request, Actual Request, and Redirects
		// * 6.2 Preflight Request

		
		// 6.1.1 e 6.2.1
		// If the Origin header is not present terminate this set of steps. The request is outside the scope of this specification.
		
		String accessControlRequestOrigin = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN);
		if(accessControlRequestOrigin==null) {
			accessControlRequestOrigin = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN.toLowerCase());
		}
		if(accessControlRequestOrigin==null) {
			accessControlRequestOrigin = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN.toUpperCase());
		}
		if(accessControlRequestOrigin==null) {
			if(config.throwExceptionIfNotFoundOrigin || config.terminateIfNotFoundOrigin) {
				String msgError = "CORSE Configuration error: the request hasn't "+HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN+" header";
				log.error(msgError);
				if(config.throwExceptionIfNotFoundOrigin) {
					throw new IOException(msgError);
				}
				else {
					return;
				}
			}
		}
		
		
		// ** allowOrigin **
		// 6.1.2 e 6.2.2
		// If the value of the Origin header is not a case-sensitive match for any of the values in list of origins, 
		// do not set any additional headers and terminate this set of steps.
		// NOTE: Always matching is acceptable since the list of origins can be unbounded.
		// NOTE [only for preflight request] The Origin header can only contain a single origin as the user agent will not follow redirects
		boolean allowOriginStar = false;
		if(accessControlRequestOrigin!=null) {
			if(config.allowRequestOrigin!=null && config.allowRequestOrigin) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, accessControlRequestOrigin);
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE);
			}
			else {
				boolean foundOrigin = false;
				if(!config.allowOrigins.isEmpty()) {
					for (String originCheck : config.allowOrigins) {
						//if(accessControlRequestOrigin.equalsIgnoreCase(originCheck)) {
						if(accessControlRequestOrigin.equals(originCheck)) {
							foundOrigin = true;
							break;
						}
					}
				}
								
				if(foundOrigin) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, accessControlRequestOrigin);
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE);
				}
				else if(config.allowAllOrigin!=null && config.allowAllOrigin) {
					if(config.allowCredentials!=null && config.allowCredentials && !config.throwExceptionIfAllowCredentialAndAllowOrigin) {
						// Vedi sotto 6.1.3 e 6.2.7, nota 'The string "*" cannot be used for a resource that supports credentials'
						res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, accessControlRequestOrigin);
						res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE);
					}
					else {
						res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
						allowOriginStar = true;
					}
				}
				else {
					if(config.throwExceptionIfNotMatchOrigin || config.terminateIfNotMatchOrigin) {
						String msgError = "CORSE Configuration error: the request has an "+HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN+" header, a response header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' is required";
						log.error(msgError);
						if(config.throwExceptionIfNotMatchOrigin) {
							throw new IOException(msgError);
						}
						else {
							return;
						}
					}
					else {
						// non produco alcun header allow, sarà il browser a riconoscere che non e' abilitato
					}
				}
			}
		}
		else {
			// se throwExceptionIfNotFoundOrigin=true o terminateIfNotFoundOrigin=true non si arriva a questo else
			if(config.allowAllOrigin!=null && config.allowAllOrigin) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
				allowOriginStar = true;
			}
		}
		
		
		// ** allowCredentials **
		// 6.1.3 e 6.2.7
		// If the resource supports credentials add a single Access-Control-Allow-Origin header,
		// with the value of the Origin header as value, and add a single Access-Control-Allow-Credentials header with the case-sensitive string "true" as value.
		//
		// Otherwise, add a single Access-Control-Allow-Origin header, with either the value of the Origin header or the string "*" as value.
		//
		// NOTE: The string "*" cannot be used for a resource that supports credentials.
		if(config.allowCredentials!=null && config.allowCredentials) {
			
			if(allowOriginStar && config.throwExceptionIfAllowCredentialAndAllowOrigin) {
				String msgError = "CORSE Configuration error: the response header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"=*' cannot be used for a resource that supports credentials ("+HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS+"=true)";
				log.error(msgError);
				throw new IOException(msgError);
			}
			
			res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		}
		
		
		// ** Expose-Headers **
		// 6.1.4
		// If the list of exposed headers is not empty add one or more Access-Control-Expose-Headers headers, with as values the header field names given in the list of exposed headers.
		// NOTE: By not adding the appropriate headers resource can also clear the preflight result cache of all entries where origin is a case-sensitive match for the value of the Origin header and url is a case-sensitive match for the URL of the resource.
		if(!config.exposeHeaders.isEmpty()) {
			res.addHeader(HttpConstants.ACCESS_CONTROL_EXPOSE_HEADERS, this.convertList(config.exposeHeaders));
		}
		

		
		if(CORSRequestType.PRE_FLIGHT.equals(requestType)) {
				
			// ** allowMethods check presenza **
			// 6.3.3
			// Let method be the value as result of parsing the Access-Control-Request-Method header.
			// If there is no Access-Control-Request-Method header or if parsing failed, do not set any additional headers and terminate this set of steps. 
			// The request is outside the scope of this specification.
			String accessControlRequestMethod = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD);
			if(accessControlRequestMethod==null) {
				accessControlRequestMethod = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD.toLowerCase());
			}
			if(accessControlRequestMethod==null) {
				accessControlRequestMethod = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD.toUpperCase());
			}
			if(accessControlRequestMethod==null) {
				if(config.throwExceptionIfNotFoundRequestMethod || config.terminateIfNotFoundRequestMethod) {
					String msgError = "CORSE Configuration error: the request hasn't an "+HttpConstants.ACCESS_CONTROL_REQUEST_METHOD+" header";
					log.error(msgError);
					if(config.throwExceptionIfNotFoundRequestMethod) {
						throw new IOException(msgError);
					}
					else {
						return;
					}
				}
				else {
					// non produco alcun header method, sarà il browser a riconoscere che non e' abilitato
				}
			}
			
			 
			// ** allowHeaders check presenza **
			// 6.3.4
			// Let header field-names be the values as result of parsing the Access-Control-Request-Headers headers.
			// If there are no Access-Control-Request-Headers headers let header field-names be the empty list.
			// If parsing failed do not set any additional headers and terminate this set of steps. The request is outside the scope of this specification.
			List<String> accessControlRequestHeadersList = new ArrayList<>();
			String accessControlRequestHeaders = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS);
			if(accessControlRequestHeaders==null) {
				accessControlRequestHeaders = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS.toLowerCase());
			}
			if(accessControlRequestHeaders==null) {
				accessControlRequestHeaders = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS.toUpperCase());
			}
			if(accessControlRequestHeaders!=null && !StringUtils.isEmpty(accessControlRequestHeaders.trim())) {
				accessControlRequestHeaders = accessControlRequestHeaders.trim();
				if(accessControlRequestHeaders.contains(",")) {
					try {
						String [] tmp = accessControlRequestHeaders.split(",");
						for (String hdr : tmp) {
							accessControlRequestHeadersList.add(hdr.trim());
						}
					}catch(Exception e) {
						if(config.throwExceptionIfNotFoundRequestHeaders || config.terminateIfNotFoundRequestHeaders) {
							String msgError = "CORSE Configuration error: the request has an uncorrect "+HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS+" header";
							log.error(msgError,e);
							if(config.throwExceptionIfNotFoundRequestHeaders) {
								throw new IOException(msgError,e);
							}
							else {
								return;
							}
						}
					}
				}
				else {
					accessControlRequestHeadersList.add(accessControlRequestHeaders.trim());
				}
			}
			
			// ** allowMethods **
			// 6.3.5
			// If method is not a case-sensitive match for any of the values in list of methods do not set any additional headers and terminate this set of steps.
			// NOTE: Always matching is acceptable since the list of methods can be unbounded.
			if(accessControlRequestMethod!=null) {
				
				boolean foundMethod = false;
				if(!config.allowMethods.isEmpty()) {
					for (String methodCheck : config.allowMethods) {
						//if(accessControlRequestMethod.equalsIgnoreCase(methodCheck)) {
						if(accessControlRequestMethod.equals(methodCheck)) {
							foundMethod = true;
							break;
						}
					}
				}

				// 6.3.9
				// If method is a simple method this step may be skipped.
				// Add one or more Access-Control-Allow-Methods headers consisting of (a subset of) the list of methods.
				// NOTE: If a method is a simple method it does not need to be listed, but this is not prohibited.
				// NOTE: Since the list of methods can be unbounded, simply returning the method indicated by Access-Control-Request-Method (if supported) can be enough.
				if(foundMethod) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, this.convertList(config.allowMethods));
				}
				else if(config.allowRequestMethod!=null && config.allowRequestMethod) {
					// utilizzabile per debug
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, accessControlRequestMethod); 
				}
				else {
					if(config.isThrowExceptionIfNotMatchRequestMethod() || config.terminateIfNotMatchRequestMethod) {
						String msgError = "CORSE Configuration error: the request has an "+HttpConstants.ACCESS_CONTROL_REQUEST_METHOD+" header not permitted";
						log.error(msgError);
						if(config.isThrowExceptionIfNotMatchRequestMethod()) {
							throw new IOException(msgError);
						}
						else {
							return;
						}
					}
					else {
						// non produco alcun header, sarà il browser a riconoscere che non e' abilitato
					}
				}

			}
			else {
				// se throwExceptionIfNotFoundRequestMethod=true o terminateIfNotFoundRequestMethod=true non si arriva a questo else
				if(!config.allowMethods.isEmpty()) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, this.convertList(config.allowMethods));
				}
			}


			// ** allowHeaders **
			// 6.3.6
			// If any of the header field-names is not a ASCII case-insensitive match for any of the values in list of headers do not set any additional headers and terminate this set of steps.
			// NOTE: Always matching is acceptable since the list of headers can be unbounded.
			if(accessControlRequestHeadersList.size()>0) {
				
				List<String> headerNotMatch = new ArrayList<>();
				for (String headerRequest : accessControlRequestHeadersList) {
					
					boolean foundHeader = false;
					if(!config.allowHeaders.isEmpty()) {
						for (String headerCheck : config.allowHeaders) {
							if(headerRequest.equalsIgnoreCase(headerCheck)) { // E' volutamente ASCII case-insensitive match da specifica
							//if(headerRequest.equals(headerCheck)) {
								foundHeader = true;
								break;
							}
						}
					}
					if(!foundHeader) {
						headerNotMatch.add(headerRequest);
					}
					
				}
				
				// 6.3.10
				// If each of the header field-names is a simple header and none is Content-Type, this step may be skipped.
				// Add one or more Access-Control-Allow-Headers headers consisting of (a subset of) the list of headers.
				// NOTE: If a header field name is a simple header and is not Content-Type, it is not required to be listed. Content-Type is to be listed as only a subset of its values makes it qualify as simple header.
				// NOTE: Since the list of headers can be unbounded, simply returning supported headers from Access-Control-Allow-Headers can be enough.
				if(headerNotMatch.isEmpty()) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, this.convertList(config.allowHeaders));
					if(config.generateAllowHeader) {
						res.addHeader(HttpConstants.ALLOW_HEADERS, this.convertList(config.allowHeaders));
					}
				}
				else if(config.allowRequestHeader!=null && config.allowRequestHeader) {
					// utilizzabile per debug
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
					if(config.generateAllowHeader) {
						res.addHeader(HttpConstants.ALLOW_HEADERS, accessControlRequestHeaders);
					}
				}
				else {
					if(config.isThrowExceptionIfNotMatchRequestHeaders() || config.terminateIfNotMatchRequestHeaders) {
						String msgError = "CORSE Configuration error: the request has an header defined in "+HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS+" not permitted: "+headerNotMatch.toString();
						log.error(msgError);
						if(config.isThrowExceptionIfNotMatchRequestHeaders()) {
							throw new IOException(msgError);
						}
						else {
							return;
						}
					}
					else {
						// non produco alcun header, sarà il browser a riconoscere che non e' abilitato
					}
				}
			}
			else {
				// se throwExceptionIfNotFoundRequestHeaders=true o terminateIfNotFoundRequestHeaders=true non si arriva a questo else
				if(!config.allowHeaders.isEmpty()) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, this.convertList(config.allowHeaders));
					if(config.generateAllowHeader) {
						res.addHeader(HttpConstants.ALLOW_HEADERS, this.convertList(config.allowHeaders));
					}
				}
			}
	
		
			// ** MaxAge **
			// 6.3.8
			// Optionally add a single Access-Control-Max-Age header with as value the amount of seconds the user agent is allowed to cache the result of the request.
			if(config.cachingAccessControl_maxAgeSeconds!=null) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE, config.cachingAccessControl_maxAgeSeconds.intValue()+"");
			}
			else if(config.cachingAccessControl_disable!=null && config.cachingAccessControl_disable) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE, HttpConstants.ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE);
			}
			
		}


	}

	
	
	private String convertList(List<String> l) {
		StringBuilder sb = new StringBuilder();
		for (String s : l) {
			if(sb.length()>0) {
				sb.append(", ");
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public CORSRequestType getRequestType(final HttpServletRequest request, Logger log) {
        CORSRequestType requestType = CORSRequestType.INVALID;
        if (request == null) {
            throw new IllegalArgumentException(
                    "HttpServletRequest object is null");
        }
        String accessControlRequestOrigin = request.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN);
		if(accessControlRequestOrigin==null) {
			accessControlRequestOrigin = request.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN.toLowerCase());
		}
		if(accessControlRequestOrigin==null) {
			accessControlRequestOrigin = request.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN.toUpperCase());
		}
        // Section 6.1.1 and Section 6.2.1
        if (accessControlRequestOrigin != null) {
            if (accessControlRequestOrigin.isEmpty()) {
            	log.error("Header '"+HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN+"' is empty");
                requestType = CORSRequestType.INVALID;
            } else if (!isValidOrigin(accessControlRequestOrigin)) {
            	log.error("Header '"+HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN+"' with uncorrect origin value");
                requestType = CORSRequestType.INVALID;
            } else {
                String method = request.getMethod();
                HttpRequestMethod requestMethod = null;
                try{
                	if(method==null) {
                		throw new Exception("Request method is null");
                	}
                	requestMethod = HttpRequestMethod.valueOf(method);
                }catch(Exception e) {
                	log.error("Method unknown '"+method+"': "+e.getMessage(),e);
                    requestType = CORSRequestType.INVALID;
                }
                if (HttpRequestMethod.OPTIONS.equals(requestMethod)) {
                	String accessControlRequestMethod = request.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD);
        			if(accessControlRequestMethod==null) {
        				accessControlRequestMethod = request.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD.toLowerCase());
        			}
        			if(accessControlRequestMethod==null) {
        				accessControlRequestMethod = request.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD.toUpperCase());
        			}
                    if (accessControlRequestMethod != null && !StringUtils.isEmpty(accessControlRequestMethod)) {
                        requestType = CORSRequestType.PRE_FLIGHT;
                    } else if (accessControlRequestMethod != null
                            && StringUtils.isEmpty(accessControlRequestMethod)) {
                    	log.error("Header '"+HttpConstants.ACCESS_CONTROL_REQUEST_METHOD+"' with empty value");
                        requestType = CORSRequestType.INVALID;
                    } else {
                        requestType = CORSRequestType.ACTUAL;
                    }
                } else if (HttpRequestMethod.HEAD.equals(requestMethod) || HttpRequestMethod.GET.equals(requestMethod)) {
                    requestType = CORSRequestType.SIMPLE;
                } else if (HttpRequestMethod.POST.equals(requestMethod)) {
                    String contentType = request.getContentType();
                    if (contentType != null) {
                        contentType = contentType.toLowerCase().trim();
                        if (HttpConstants.ACCESS_CONTROL_SIMPLE_REQUEST_CONTENT_TYPES.contains(contentType)) {
                            requestType = CORSRequestType.SIMPLE;
                        } else {
                            requestType = CORSRequestType.ACTUAL;
                        }
                    }
                } else {
                    requestType = CORSRequestType.ACTUAL;
                }
            }
        } else {
        	log.error("Header '"+HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN+"' not defined");
            requestType = CORSRequestType.INVALID;
        }

        return requestType;
    }

    private static boolean isValidOrigin(String origin) {
    	
    	// RFC6454 chapter 4
        // "null" is a valid origin
        if ("null".equals(origin)) {
            return true;
        }
    	//  If uri-scheme is "file", the implementation MAY return an implementation-defined value.
        if (origin.startsWith("file://")) {
            return true;
        }        
    	// Checks for encoded characters. Helps prevent CRLF injection.
        if (origin.contains("%")) {
            return false;
        }
        URI originURI;
        try {
            originURI = new URI(origin);
        } catch (URISyntaxException e) {
            return false;
        }
        // If scheme for URI is null, return false. Return true otherwise.
        return originURI.getScheme() != null;

    }

}


