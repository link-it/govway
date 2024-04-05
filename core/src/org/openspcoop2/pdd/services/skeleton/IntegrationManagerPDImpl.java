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

package org.openspcoop2.pdd.services.skeleton;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * IntegrationManager service
 *
 *
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@jakarta.jws.WebService(name = "IntegrationManager",
		serviceName = "PDService",
		portName = "PD",
		targetNamespace = "http://services.pdd.openspcoop2.org",
		endpointInterface = "org.openspcoop2.pdd.services.skeleton.IntegrationManagerPDInterface")
public class IntegrationManagerPDImpl extends IntegrationManager{

	@jakarta.annotation.Resource
	jakarta.xml.ws.WebServiceContext wsc;

	
	@Override
	@jakarta.jws.WebMethod
	protected HttpServletRequest getHttpServletRequest() throws IntegrationManagerException{
		
		jakarta.servlet.http.HttpServletRequest req =  null;
		
		jakarta.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		req = 
			(jakarta.servlet.http.HttpServletRequest) msgContext.get(jakarta.xml.ws.handler.MessageContext.SERVLET_REQUEST);
		
		
		// Il path info e' nella forma /govway/protocol/IntegrationManager
		String pathinfo = (String) msgContext.get(jakarta.xml.ws.handler.MessageContext.PATH_INFO);
		
		IntegrationManagerUtility.readAndSetProtocol(req, pathinfo);
		
		return req;
	}

	@Override
	protected HttpServletResponse getHttpServletResponse()
			throws IntegrationManagerException {
		
		jakarta.servlet.http.HttpServletResponse res =  null;
		
		jakarta.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		res = 
			(jakarta.servlet.http.HttpServletResponse) msgContext.get(jakarta.xml.ws.handler.MessageContext.SERVLET_RESPONSE);
		
		return res;
	}
	
	
}
