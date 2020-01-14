/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.ws.server;

import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterDumpMessaggio;
import java.util.List;


import org.openspcoop2.core.transazioni.DumpMessaggio;

import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotImplementedException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * DumpMessaggioSearch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", name = "DumpMessaggio")
public interface DumpMessaggioSearch {
	/**
	 * It allows you to retrieve all objects of type DumpMessaggio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type DumpMessaggio
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllDumpMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllDumpMessaggio")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<DumpMessaggio> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterDumpMessaggio filter
	) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type DumpMessaggio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type DumpMessaggio
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotFoundException_Exception
	 * @throws TransazioniMultipleResultException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindDumpMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindDumpMessaggio")
    @javax.jws.WebResult(name = "dumpMessaggio", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public DumpMessaggio find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterDumpMessaggio filter
	) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type DumpMessaggio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type DumpMessaggio
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.CountDumpMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.CountDumpMessaggio")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterDumpMessaggio filter
	) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type DumpMessaggio identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type DumpMessaggio
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotFoundException_Exception
	 * @throws TransazioniMultipleResultException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.GetDumpMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.GetDumpMessaggio")
    @javax.jws.WebResult(name = "dumpMessaggio", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public DumpMessaggio get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		org.openspcoop2.core.transazioni.IdDumpMessaggio id
	) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object DumpMessaggio identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object DumpMessaggio identified by the id parameter. 
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniMultipleResultException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.ExistsDumpMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.ExistsDumpMessaggio")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		org.openspcoop2.core.transazioni.IdDumpMessaggio id
	) throws TransazioniServiceException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.transazioni.IdDumpMessaggio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.transazioni.IdDumpMessaggio
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllIdsDumpMessaggioResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllIdsDumpMessaggio")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.transazioni.IdDumpMessaggio> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterDumpMessaggio filter
	) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	

}