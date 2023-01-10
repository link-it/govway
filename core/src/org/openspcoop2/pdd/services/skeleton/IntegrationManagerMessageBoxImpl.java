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

package org.openspcoop2.pdd.services.skeleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * IntegrationManager service
 *
 *
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(name = "IntegrationManager",
		serviceName = "MessageBoxService",
		portName = "MessageBox",
		targetNamespace = "http://services.pdd.openspcoop2.org",
		endpointInterface = "org.openspcoop2.pdd.services.skeleton.IntegrationManagerMessageBoxInterface")
public class IntegrationManagerMessageBoxImpl extends IntegrationManager{

	@javax.annotation.Resource
	javax.xml.ws.WebServiceContext wsc;
	
	@Override
	@javax.jws.WebMethod
	protected HttpServletRequest getHttpServletRequest() throws IntegrationManagerException{
		
		javax.servlet.http.HttpServletRequest req =  null;
		
		javax.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		req = 
			(javax.servlet.http.HttpServletRequest) msgContext.get(javax.xml.ws.handler.MessageContext.SERVLET_REQUEST);
		
		
		// Il path info e' nella forma /govway/protocol/IntegrationManager
		String pathinfo = (String) msgContext.get(javax.xml.ws.handler.MessageContext.PATH_INFO);
		
		IntegrationManagerUtility.readAndSetProtocol(req, pathinfo);
		
		return req;
	}

	@Override
	protected HttpServletResponse getHttpServletResponse()
			throws IntegrationManagerException {
		
		javax.servlet.http.HttpServletResponse res =  null;
		
		javax.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		res = 
			(javax.servlet.http.HttpServletResponse) msgContext.get(javax.xml.ws.handler.MessageContext.SERVLET_RESPONSE);
		
		return res;
	}
	
	
}
