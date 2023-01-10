/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriInfo;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.ILogger;
import org.openspcoop2.utils.service.logger.ServiceLogger;
import org.springframework.security.core.Authentication;

/**
 * IContext
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IContext {

	public HttpServletRequest getServletRequest();
	public HttpServletResponse getServletResponse();
	
	public UriInfo getUriInfo();
	
	public String getTransactionId();
	
	public String getClassName();
	public String getMethodName();
	public String getRestPath();
	
	public org.openspcoop2.utils.logger.beans.context.core.AbstractContext getApplicationContext();
	public ILogger getApplicationLogger() throws UtilsException;
	
	public ServiceLogger getLogger();
	
	public Authentication getAuthentication();
	
}
