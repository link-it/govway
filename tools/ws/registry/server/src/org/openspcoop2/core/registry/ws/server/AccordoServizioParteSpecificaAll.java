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

import org.openspcoop2.core.registry.ws.server.filter.SearchFilterAccordoServizioParteSpecifica;
import java.util.List;

import org.openspcoop2.core.registry.ws.server.beans.UseInfo;

import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;

import org.openspcoop2.core.registry.ws.server.exception.RegistryServiceException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotFoundException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotImplementedException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * AccordoServizioParteSpecificaAll
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/registry/management", name = "AccordoServizioParteSpecifica")
public interface AccordoServizioParteSpecificaAll {
	/**
	 * It allows you to retrieve all objects of type AccordoServizioParteSpecifica that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type AccordoServizioParteSpecifica
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<AccordoServizioParteSpecifica> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterAccordoServizioParteSpecifica filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type AccordoServizioParteSpecifica that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type AccordoServizioParteSpecifica
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "accordoServizioParteSpecifica", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public AccordoServizioParteSpecifica find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterAccordoServizioParteSpecifica filter
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type AccordoServizioParteSpecifica that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type AccordoServizioParteSpecifica
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CountAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CountAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterAccordoServizioParteSpecifica filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type AccordoServizioParteSpecifica identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type AccordoServizioParteSpecifica
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.GetAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.GetAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "accordoServizioParteSpecifica", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public AccordoServizioParteSpecifica get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica id
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object AccordoServizioParteSpecifica identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object AccordoServizioParteSpecifica identified by the id parameter. 
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryMultipleResultException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.ExistsAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.ExistsAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica id
	) throws RegistryServiceException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllIdsAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.FindAllIdsAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterAccordoServizioParteSpecifica filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Indicates the use of the object (identified by parameter) by other components
	 *
	 * @return Indicates the use of the object (identified by parameter) by other components
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "inUseResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UseInfoAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "inUse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UseInfoAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "inUse", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="inUse",operationName="inUse")
	public UseInfo inUse(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica id
	) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Create the object described by the provided parameter
	 *
	 * @param accordoServizioParteSpecifica object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "createResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CreateAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "create", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CreateAccordoServizioParteSpecifica")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="create",operationName="create")
	public void create(
		@javax.jws.WebParam(name = "accordo-servizio-parte-specifica", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		AccordoServizioParteSpecifica accordoServizioParteSpecifica
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
	
	/**
	 * Update the object instance identified by the id parameter, using the provided object description.
	 *
	 * @param oldId object id
	 * @param accordoServizioParteSpecifica object
	 * @throws RegistryServiceException_Exception
 	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "updateResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "update", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateAccordoServizioParteSpecifica")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="update",operationName="update")
    public void update(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    	org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica oldId,
    	@javax.jws.WebParam(name = "accordo-servizio-parte-specifica", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	    AccordoServizioParteSpecifica accordoServizioParteSpecifica
    ) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Update the object istance identified by the id parameter or create a new object, using the provided object description.
	 *
	 * @param oldId object id
	 * @param accordoServizioParteSpecifica object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "updateOrCreateResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateOrCreateAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "updateOrCreate", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateOrCreateAccordoServizioParteSpecifica")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="updateOrCreate",operationName="updateOrCreate")
	public void updateOrCreate(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica oldId, 
		@javax.jws.WebParam(name = "accordo-servizio-parte-specifica", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		AccordoServizioParteSpecifica accordoServizioParteSpecifica
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete the object instance identified by the id parameter.
	 *
	 * @param id object id
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteByIdResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteByIdAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteById", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteByIdAccordoServizioParteSpecifica")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteById",operationName="deleteById")
	public void deleteById(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica id
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete all object instances
	 *
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAll", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllAccordoServizioParteSpecifica")
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
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllByFilterResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllByFilterAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllByFilter", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllByFilterAccordoServizioParteSpecifica")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAllByFilter",operationName="deleteAllByFilter")
	public long deleteAllByFilter(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterAccordoServizioParteSpecifica filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
	
	/**
	 * Delete the object instance identified by the provided object description.
	 *
	 * @param accordoServizioParteSpecifica object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAccordoServizioParteSpecificaResponse")
    @javax.xml.ws.RequestWrapper(localName = "delete", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAccordoServizioParteSpecifica")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="delete",operationName="delete")
	public void delete(
		@javax.jws.WebParam(name = "accordo-servizio-parte-specifica", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		AccordoServizioParteSpecifica accordoServizioParteSpecifica
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
}