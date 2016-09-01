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
package org.openspcoop2.core.registry.ws.server;

import org.openspcoop2.core.registry.ws.server.filter.SearchFilterPortaDominio;
import java.util.List;

import org.openspcoop2.core.registry.ws.server.beans.UseInfo;

import org.openspcoop2.core.registry.PortaDominio;

import org.openspcoop2.core.registry.ws.server.exception.RegistryServiceException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotFoundException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotImplementedException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * PortaDominioAll
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/registry/management", name = "PortaDominio")
public interface PortaDominioAll {
	/**
	 * It allows you to retrieve all objects of type PortaDominio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type PortaDominio
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllPortaDominio")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<PortaDominio> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterPortaDominio filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type PortaDominio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type PortaDominio
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindPortaDominio")
    @javax.jws.WebResult(name = "portaDominio", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public PortaDominio find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterPortaDominio filter
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type PortaDominio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type PortaDominio
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CountPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CountPortaDominio")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterPortaDominio filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type PortaDominio identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type PortaDominio
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.GetPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.GetPortaDominio")
    @javax.jws.WebResult(name = "portaDominio", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public PortaDominio get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdPortaDominio id
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object PortaDominio identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object PortaDominio identified by the id parameter. 
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.ExistsPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.ExistsPortaDominio")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdPortaDominio id
	) throws RegistryServiceException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.registry.IdPortaDominio that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.registry.IdPortaDominio
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllIdsPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllIdsPortaDominio")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.registry.IdPortaDominio> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterPortaDominio filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Indicates the use of the object (identified by parameter) by other components
	 *
	 * @return Indicates the use of the object (identified by parameter) by other components
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "inUseResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UseInfoPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "inUse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UseInfoPortaDominio")
    @javax.jws.WebResult(name = "inUse", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="inUse",operationName="inUse")
	public UseInfo inUse(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdPortaDominio id
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Create the object described by the provided parameter
	 *
	 * @param portaDominio object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "createResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CreatePortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "create", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CreatePortaDominio")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="create",operationName="create")
	public void create(
		@javax.jws.WebParam(name = "porta-dominio", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		PortaDominio portaDominio
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
	
	/**
	 * Update the object instance identified by the id parameter, using the provided object description.
	 *
	 * @param oldId object id
	 * @param portaDominio object
	 * @throws RegistryServiceException_Exception
 	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "updateResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdatePortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "update", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdatePortaDominio")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="update",operationName="update")
    public void update(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    	org.openspcoop2.core.registry.IdPortaDominio oldId,
    	@javax.jws.WebParam(name = "porta-dominio", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	    PortaDominio portaDominio
    ) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Update the object istance identified by the id parameter or create a new object, using the provided object description.
	 *
	 * @param oldId object id
	 * @param portaDominio object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "updateOrCreateResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateOrCreatePortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "updateOrCreate", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateOrCreatePortaDominio")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="updateOrCreate",operationName="updateOrCreate")
	public void updateOrCreate(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdPortaDominio oldId, 
		@javax.jws.WebParam(name = "porta-dominio", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		PortaDominio portaDominio
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete the object instance identified by the id parameter.
	 *
	 * @param id object id
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteByIdResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteByIdPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteById", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteByIdPortaDominio")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteById",operationName="deleteById")
	public void deleteById(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdPortaDominio id
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete all object instances
	 *
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAll", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllPortaDominio")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAll",operationName="deleteAll")
	public long deleteAll() throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete all object instances matching the filter parameter
	 *
	 * @param filter Filter
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllByFilterResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllByFilterPortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllByFilter", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllByFilterPortaDominio")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAllByFilter",operationName="deleteAllByFilter")
	public long deleteAllByFilter(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterPortaDominio filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
	
	/**
	 * Delete the object instance identified by the provided object description.
	 *
	 * @param portaDominio object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeletePortaDominioResponse")
    @javax.xml.ws.RequestWrapper(localName = "delete", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeletePortaDominio")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="delete",operationName="delete")
	public void delete(
		@javax.jws.WebParam(name = "porta-dominio", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		PortaDominio portaDominio
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
}