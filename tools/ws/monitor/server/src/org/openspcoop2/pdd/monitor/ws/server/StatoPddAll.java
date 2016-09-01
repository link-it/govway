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
package org.openspcoop2.pdd.monitor.ws.server;

import org.openspcoop2.pdd.monitor.ws.server.filter.SearchFilterStatoPdd;



import org.openspcoop2.pdd.monitor.StatoPdd;

import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorServiceException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotFoundException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorMultipleResultException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotImplementedException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * StatoPddAll
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "StatoPdd")
public interface StatoPddAll {
	

	/**
	 * It allows you to retrieve the object of type StatoPdd that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type StatoPdd
	 * @throws MonitorServiceException_Exception
	 * @throws MonitorNotFoundException_Exception
	 * @throws MonitorMultipleResultException_Exception
	 * @throws MonitorNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.FindStatoPddResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.FindStatoPdd")
    @javax.jws.WebResult(name = "statoPdd", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public StatoPdd find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
		SearchFilterStatoPdd filter
	) throws MonitorServiceException_Exception,MonitorNotFoundException_Exception,MonitorMultipleResultException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception;

	

	
	

	

	
	
}