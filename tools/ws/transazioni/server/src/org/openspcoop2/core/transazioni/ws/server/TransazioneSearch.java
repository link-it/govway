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
package org.openspcoop2.core.transazioni.ws.server;

import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterTransazione;
import java.util.List;


import org.openspcoop2.core.transazioni.Transazione;

import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotImplementedException_Exception;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * TransazioneSearch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", name = "Transazione")
public interface TransazioneSearch {
	/**
	 * It allows you to retrieve all objects of type Transazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type Transazione
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllTransazione")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<Transazione> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterTransazione filter
	) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type Transazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type Transazione
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotFoundException_Exception
	 * @throws TransazioniMultipleResultException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindTransazione")
    @javax.jws.WebResult(name = "transazione", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public Transazione find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterTransazione filter
	) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type Transazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type Transazione
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.CountTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.CountTransazione")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterTransazione filter
	) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type Transazione identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type Transazione
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotFoundException_Exception
	 * @throws TransazioniMultipleResultException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.GetTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.GetTransazione")
    @javax.jws.WebResult(name = "transazione", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public Transazione get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		java.lang.String id
	) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object Transazione identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object Transazione identified by the id parameter. 
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniMultipleResultException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.ExistsTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.ExistsTransazione")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		java.lang.String id
	) throws TransazioniServiceException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type java.lang.String that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type java.lang.String
	 * @throws TransazioniServiceException_Exception
	 * @throws TransazioniNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllIdsTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management", className = "org.openspcoop2.core.transazioni.ws.wrapped.FindAllIdsTransazione")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<java.lang.String> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
		SearchFilterTransazione filter
	) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception;

	

}