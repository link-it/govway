/*
 * OpenSPCoop - Customizable API Gateway 
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

import org.openspcoop2.pdd.monitor.ws.server.filter.SearchFilterMessaggio;
import java.util.List;


import org.openspcoop2.pdd.monitor.Messaggio;

import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorServiceException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotImplementedException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * MessaggioAll
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", name = "Messaggio")
public interface MessaggioAll {
	/**
	 * It allows you to retrieve all objects of type Messaggio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type Messaggio
	 * @throws MonitorServiceException_Exception
	 * @throws MonitorNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.FindAllMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.FindAllMessaggio")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<Messaggio> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
		SearchFilterMessaggio filter
	) throws MonitorServiceException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception;

	

	/**
	 * It allows you to count all objects of type Messaggio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type Messaggio
	 * @throws MonitorServiceException_Exception
	 * @throws MonitorNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.CountMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.CountMessaggio")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
		SearchFilterMessaggio filter
	) throws MonitorServiceException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception;

	
	

	

	/**
	 * Delete all object instances matching the filter parameter
	 *
	 * @param filter Filter
	 * @throws MonitorServiceException_Exception
	 * @throws MonitorNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllByFilterResponse", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.DeleteAllByFilterMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllByFilter", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management", className = "org.openspcoop2.pdd.monitor.ws.wrapped.DeleteAllByFilterMessaggio")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAllByFilter",operationName="deleteAllByFilter")
	public long deleteAllByFilter(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/pdd/monitor/management")
		SearchFilterMessaggio filter
	) throws MonitorServiceException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception;
	
}