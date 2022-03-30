/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.service;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.openspcoop2.utils.service.context.ContextThreadLocal;
import org.openspcoop2.utils.service.context.IContext;
import org.slf4j.Logger;

/**
 * BaseImpl
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseImpl {

	@Context 
	protected HttpServletRequest servletRequest;
	@Context 
	protected HttpServletResponse servletResponse;
	@Context
	protected UriInfo uriInfo;
	
	protected Logger log;
	
	public BaseImpl(Logger log){
		this.log = log;
	}
	
	protected synchronized IContext getContext() {
		IContext context = ContextThreadLocal.get();
		if(context instanceof org.openspcoop2.utils.service.context.Context) {
			((org.openspcoop2.utils.service.context.Context)context).update(this.servletRequest, this.servletResponse, this.uriInfo, 2, this.log);
			((org.openspcoop2.utils.service.context.Context)context).setRestPath(this.getPathFromRestMethod(context.getMethodName()));
		}
		return context;
	}

	private String getPathFromRestMethod(String methodName) {

        try {
        	Class<?> c = this.getClass();
        	Class<?> [] interfaces = c.getInterfaces();
        	if(interfaces==null || interfaces.length<=0) {
        		return null;
        	}
        	Class<?> cInterface = null;
        	for (int i = 0; i < interfaces.length; i++) {
        		if (interfaces[i] != null && interfaces[i].isAnnotationPresent(Path.class)) {
        			cInterface = interfaces[i];
        			break;
        		}
			}
        	if(cInterface==null) {
        		return null;
        	}
        	Method [] methods = cInterface.getMethods();
        	if(methods==null || methods.length<=0) {
        		return null;
        	}
        	Method method = null;
        	for (int i = 0; i < methods.length; i++) {
        		if (methods[i] != null && methods[i].getName().equals(methodName) && methods[i].isAnnotationPresent(Path.class)) {
        			method = methods[i];
        			break;
        		}
			}
        	if(method==null) {
        		return null;
        	}
        	Path path = method.getAnnotation(Path.class);
        	if(path==null) {
        		return null;
        	}
        	return path.value();
        } catch (Exception e) {
            this.log.error(e.getMessage(),e);
        }

        return null;
    }
	
}
