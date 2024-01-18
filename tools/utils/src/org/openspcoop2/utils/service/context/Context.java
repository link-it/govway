/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.service.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.UriInfo;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.ILogger;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContext;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransactionWithClient;
import org.openspcoop2.utils.logger.beans.context.core.Operation;
import org.openspcoop2.utils.logger.beans.context.core.Service;
import org.openspcoop2.utils.service.logger.ServiceLogger;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Context
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Context implements IContext {

	
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	
	private UriInfo uriInfo;
	
	private String transactionId;
	
	private String className;
	private String methodName;
	private String restPath;
	
	private AbstractContext applicationContext;
	private ILogger applicationLogger;
	
	private ServiceLogger logger; // core
	
	private Authentication authentication;
	
	private boolean loggerPrefixEnabled;
	
	
	public Context(ILogger applicationLogger, boolean loggerPrefixEnabled) throws UtilsException {
		this.applicationLogger = applicationLogger;
		this.applicationContext = (AbstractContext) this.applicationLogger.getContext();
		this.transactionId = this.applicationContext.getIdTransaction();
		if(this.transactionId==null) {
			throw new UtilsException("TransactionId undefined");
		}
		this.loggerPrefixEnabled = loggerPrefixEnabled;
	}
	
	public void update(HttpServletRequest servletRequest, HttpServletResponse servletResponse, UriInfo uriInfo, int level, Logger log) {
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		this.uriInfo = uriInfo;
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		if(stackTrace!=null && stackTrace.length>=(level+1)) {
			this.className = stackTrace[level].getClassName();
			this.methodName = stackTrace[level].getMethodName();
		}
		this.logger = new ServiceLogger(this.transactionId, this.methodName, this.className, log, this.loggerPrefixEnabled);
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		
		AbstractTransactionWithClient transaction = (AbstractTransactionWithClient) this.applicationContext.getTransaction();
		
		if(this.className!=null) {
			if(transaction.getService()==null) {
				transaction.setService(new Service());
			}
			if(transaction.getService().getName()==null) {
				if(this.className.lastIndexOf(".")>0) {
					transaction.getService().setName(this.className.substring(this.className.lastIndexOf(".")+1, this.className.length()));
				}
				else {
					transaction.getService().setName(this.className);
				}
			}
		}
		if(this.methodName!=null) {
			if(transaction.getOperation()==null) {
				transaction.setOperation(new Operation());
			}
			transaction.getOperation().setName(this.methodName);
		}
	
	}
	
	public void setRestPath(String restPath) {
		this.restPath = restPath;
	}
	
	@Override
	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}
	
	@Override
	public HttpServletResponse getServletResponse() {
		return this.servletResponse;
	}
	
	@Override
	public UriInfo getUriInfo() {
		return this.uriInfo;
	}
	
	@Override
	public String getTransactionId() {
		return this.transactionId;
	}
	
	@Override
	public String getClassName() {
		return this.className;
	}
	@Override
	public String getMethodName() {
		return this.methodName;
	}
	@Override
	public String getRestPath() {
		return this.restPath;
	}
	
	@Override
	public AbstractContext getApplicationContext(){
		return this.applicationContext;
	}
	@Override
	public ILogger getApplicationLogger() throws UtilsException {
		return this.applicationLogger;
	}
	
	@Override
	public ServiceLogger getLogger() {
		return this.logger;
	}
	
	@Override
	public Authentication getAuthentication() {
		return this.authentication;
	}
	
}
