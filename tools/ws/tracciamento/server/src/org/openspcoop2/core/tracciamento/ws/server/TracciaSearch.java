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
package org.openspcoop2.core.tracciamento.ws.server;

import org.openspcoop2.core.tracciamento.ws.server.filter.SearchFilterTraccia;
import java.util.List;


import org.openspcoop2.core.tracciamento.Traccia;

import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoServiceException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotFoundException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoMultipleResultException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotImplementedException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * TracciaSearch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", name = "Traccia")
public interface TracciaSearch {
	/**
	 * It allows you to retrieve all objects of type Traccia that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type Traccia
	 * @throws TracciamentoServiceException_Exception
	 * @throws TracciamentoNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.FindAllTracciaResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.FindAllTraccia")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<Traccia> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
		SearchFilterTraccia filter
	) throws TracciamentoServiceException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type Traccia that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type Traccia
	 * @throws TracciamentoServiceException_Exception
	 * @throws TracciamentoNotFoundException_Exception
	 * @throws TracciamentoMultipleResultException_Exception
	 * @throws TracciamentoNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.FindTracciaResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.FindTraccia")
    @javax.jws.WebResult(name = "traccia", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public Traccia find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
		SearchFilterTraccia filter
	) throws TracciamentoServiceException_Exception,TracciamentoNotFoundException_Exception,TracciamentoMultipleResultException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type Traccia that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type Traccia
	 * @throws TracciamentoServiceException_Exception
	 * @throws TracciamentoNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.CountTracciaResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.CountTraccia")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
		SearchFilterTraccia filter
	) throws TracciamentoServiceException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type Traccia identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type Traccia
	 * @throws TracciamentoServiceException_Exception
	 * @throws TracciamentoNotFoundException_Exception
	 * @throws TracciamentoMultipleResultException_Exception
	 * @throws TracciamentoNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.GetTracciaResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.GetTraccia")
    @javax.jws.WebResult(name = "traccia", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public Traccia get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
		org.openspcoop2.core.tracciamento.IdTraccia id
	) throws TracciamentoServiceException_Exception,TracciamentoNotFoundException_Exception,TracciamentoMultipleResultException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object Traccia identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object Traccia identified by the id parameter. 
	 * @throws TracciamentoServiceException_Exception
	 * @throws TracciamentoMultipleResultException_Exception
	 * @throws TracciamentoNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.ExistsTracciaResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.ExistsTraccia")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
		org.openspcoop2.core.tracciamento.IdTraccia id
	) throws TracciamentoServiceException_Exception,TracciamentoMultipleResultException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.tracciamento.IdTraccia that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.tracciamento.IdTraccia
	 * @throws TracciamentoServiceException_Exception
	 * @throws TracciamentoNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.FindAllIdsTracciaResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management", className = "org.openspcoop2.core.tracciamento.ws.wrapped.FindAllIdsTraccia")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.tracciamento.IdTraccia> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/tracciamento/management")
		SearchFilterTraccia filter
	) throws TracciamentoServiceException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception;

	

}