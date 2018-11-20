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

package org.openspcoop2.utils.jaxrs.impl;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * ServiceContext
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceContext {

	private HttpServletRequest servletRequest;
	private UriInfo uriInfo;
	private String idOperazione;
	private String className;
	private String methodName;
	private ServiceLogger logger;
	private Authentication authentication;
	private String restPath;
	
	public ServiceContext(HttpServletRequest servletRequest, UriInfo uriInfo, int level, Logger log) {
		this.servletRequest = servletRequest;
		this.uriInfo = uriInfo;
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		if(stackTrace!=null && stackTrace.length>=(level+1)) {
			this.className = stackTrace[level].getClassName();
			this.methodName = stackTrace[level].getMethodName();
		}
		this.idOperazione = UUID.randomUUID().toString();
		this.logger = new ServiceLogger(this.idOperazione, this.methodName, this.className, log);
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
	}
	
	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}
	public UriInfo getUriInfo() {
		return this.uriInfo;
	}
	public String getIdOperazione() {
		return this.idOperazione;
	}
	public String getClassName() {
		return this.className;
	}
	public String getMethodName() {
		return this.methodName;
	}
	public ServiceLogger getLogger() {
		return this.logger;
	}
	public Authentication getAuthentication() {
		return this.authentication;
	}
	public String getRestPath() {
		return this.restPath;
	}
	void setRestPath(String restPath) {
		this.restPath = restPath;
	}
	
}
