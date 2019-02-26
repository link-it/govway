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
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * AbstractCORSFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCORSFilter implements javax.servlet.Filter {

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

		CORSFilterConfiguration config = this.getConfig();

		Logger log = this.getLog();
		
		// allowCredentials
		if(config.allowCredentials!=null && config.allowCredentials) {
			res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		}

		// allowHeaders
		String accessControlRequestHeaders = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS);
		if(accessControlRequestHeaders==null) {
			accessControlRequestHeaders = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS.toLowerCase());
		}
		if(accessControlRequestHeaders==null) {
			accessControlRequestHeaders = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS.toUpperCase());
		}
		if(accessControlRequestHeaders!=null) {
			if(config.allowRequestHeader!=null && config.allowRequestHeader) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
				if(config.generateAllowHeader) {
					res.addHeader(HttpConstants.ALLOW_HEADERS, accessControlRequestHeaders);
				}
			}
			else if(!config.allowHeaders.isEmpty()) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, this.convertList(config.allowHeaders));
				if(config.generateAllowHeader) {
					res.addHeader(HttpConstants.ALLOW_HEADERS, this.convertList(config.allowHeaders));
				}
			}
			else {
				if(config.throwExceptionIfNotFoundConfig) {
					String msgError = "CORSE Configuration error: the request has an "+HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS+" header, a response header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' is required";
					log.error(msgError);
					throw new IOException(msgError);
				}
				else {
					// non produco alcun header allow, sarà il browser a riconoscere che non e' abilitato
				}
			}
		}
		else {
			if(!config.allowHeaders.isEmpty()) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, this.convertList(config.allowHeaders));
				if(config.generateAllowHeader) {
					res.addHeader(HttpConstants.ALLOW_HEADERS, this.convertList(config.allowHeaders));
				}
			}
		}

		// allowMethods
		String accessControlRequestMethod = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD);
		if(accessControlRequestMethod==null) {
			accessControlRequestMethod = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD.toLowerCase());
		}
		if(accessControlRequestMethod==null) {
			accessControlRequestMethod = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD.toUpperCase());
		}
		if(accessControlRequestMethod!=null) {
			if(config.allowRequestMethod!=null && config.allowRequestMethod) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, accessControlRequestMethod);
			}
			else if(!config.allowHeaders.isEmpty()) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, this.convertList(config.allowMethods));
			}
			else {
				if(config.throwExceptionIfNotFoundConfig) {
					String msgError = "CORSE Configuration error: the request has an "+HttpConstants.ACCESS_CONTROL_REQUEST_METHOD+" header, a response header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' is required";
					log.error(msgError);
					throw new IOException(msgError);
				}
				else {
					// non produco alcun header allow, sarà il browser a riconoscere che non e' abilitato
				}
			}
		}
		else {
			if(!config.allowHeaders.isEmpty()) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, this.convertList(config.allowMethods));
			}
		}

		// allowOrigin
		String accessControlRequestOrigin = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN);
		if(accessControlRequestOrigin==null) {
			accessControlRequestOrigin = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN.toLowerCase());
		}
		if(accessControlRequestOrigin==null) {
			accessControlRequestOrigin = req.getHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN.toUpperCase());
		}
		if(accessControlRequestOrigin!=null) {
			if(config.allowRequestOrigin!=null && config.allowRequestOrigin) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, accessControlRequestOrigin);
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE);
			}
			else {
				boolean foundOrigin = false;
				if(!config.allowOrigins.isEmpty()) {
					for (String originCheck : config.allowOrigins) {
						if(accessControlRequestOrigin.equalsIgnoreCase(originCheck)) {
							foundOrigin = true;
						}
					}
				}
				if(foundOrigin) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, accessControlRequestOrigin);
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE);
				}
				else if(config.allowAllOrigin!=null && config.allowAllOrigin) {
					res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
				}
				else {
					if(config.throwExceptionIfNotFoundConfig) {
						String msgError = "CORSE Configuration error: the request has an "+HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN+" header, a response header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' is required";
						log.error(msgError);
						throw new IOException(msgError);
					}
					else {
						// non produco alcun header allow, sarà il browser a riconoscere che non e' abilitato
					}
				}
			}
		}
		else {
			if(config.allowAllOrigin!=null && config.allowAllOrigin) {
				res.addHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
			}
		}

		// Expose-Headers
		if(!config.exposeHeaders.isEmpty()) {
			res.addHeader(HttpConstants.ACCESS_CONTROL_EXPOSE_HEADERS, this.convertList(config.exposeHeaders));
		}

		// MaxAge
		if(config.cachingAccessControl_maxAgeSeconds!=null) {
			res.addHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE, config.cachingAccessControl_maxAgeSeconds.intValue()+"");
		}
		else if(config.cachingAccessControl_disable!=null && config.cachingAccessControl_disable) {
			res.addHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE, HttpConstants.ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE);
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

}


