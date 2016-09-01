/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.monitor.ws.server.impl;

import org.openspcoop2.pdd.monitor.ws.server.StatoPddAll;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorServiceException_Exception;

/**     
 * StatoPddAllImpl_PortSoap12
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(name = "StatoPdd",
		serviceName = "StatoPddSoap12Service",
		portName = "StatoPddPortSoap12",
		targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management",
		endpointInterface = "org.openspcoop2.pdd.monitor.ws.server.StatoPddAll")
public class StatoPddAllImpl_PortSoap12 extends StatoPddImpl implements StatoPddAll {

	@javax.annotation.Resource
	javax.xml.ws.WebServiceContext wsc;
	
	@Override
	@javax.jws.WebMethod
	protected javax.servlet.http.HttpServletRequest getHttpServletRequest() throws MonitorServiceException_Exception{
		
		javax.servlet.http.HttpServletRequest req =  null;
		
		javax.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		req = 
			(javax.servlet.http.HttpServletRequest) msgContext.get(javax.xml.ws.handler.MessageContext.SERVLET_REQUEST);
		
		return req;
	}

	@Override
	protected javax.servlet.http.HttpServletResponse getHttpServletResponse()
			throws MonitorServiceException_Exception {
		
		javax.servlet.http.HttpServletResponse res =  null;
		
		javax.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		res = 
			(javax.servlet.http.HttpServletResponse) msgContext.get(javax.xml.ws.handler.MessageContext.SERVLET_RESPONSE);
		
		return res;
	}
}