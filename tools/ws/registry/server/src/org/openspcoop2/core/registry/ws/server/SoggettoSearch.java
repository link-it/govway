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
package org.openspcoop2.core.registry.ws.server;

import org.openspcoop2.core.registry.ws.server.filter.SearchFilterSoggetto;
import java.util.List;

import org.openspcoop2.core.registry.ws.server.beans.UseInfo;

import org.openspcoop2.core.registry.Soggetto;

import org.openspcoop2.core.registry.ws.server.exception.RegistryServiceException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotFoundException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotImplementedException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * SoggettoSearch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/registry/management", name = "Soggetto")
public interface SoggettoSearch {
	/**
	 * It allows you to retrieve all objects of type Soggetto that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type Soggetto
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllSoggetto")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<Soggetto> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterSoggetto filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type Soggetto that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type Soggetto
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindSoggetto")
    @javax.jws.WebResult(name = "soggetto", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public Soggetto find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterSoggetto filter
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type Soggetto that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type Soggetto
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CountSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CountSoggetto")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterSoggetto filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type Soggetto identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type Soggetto
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.GetSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.GetSoggetto")
    @javax.jws.WebResult(name = "soggetto", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public Soggetto get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdSoggetto id
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object Soggetto identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object Soggetto identified by the id parameter. 
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.ExistsSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.ExistsSoggetto")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdSoggetto id
	) throws RegistryServiceException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.registry.IdSoggetto that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.registry.IdSoggetto
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllIdsSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllIdsSoggetto")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.registry.IdSoggetto> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterSoggetto filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Indicates the use of the object (identified by parameter) by other components
	 *
	 * @return Indicates the use of the object (identified by parameter) by other components
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "inUseResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UseInfoSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "inUse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UseInfoSoggetto")
    @javax.jws.WebResult(name = "inUse", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="inUse",operationName="inUse")
	public UseInfo inUse(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdSoggetto id
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

}