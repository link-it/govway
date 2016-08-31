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
package org.openspcoop2.core.config.ws.server;

import org.openspcoop2.core.config.ws.server.filter.SearchFilterServizioApplicativo;
import java.util.List;

import org.openspcoop2.core.config.ws.server.beans.UseInfo;

import org.openspcoop2.core.config.ServizioApplicativo;

import org.openspcoop2.core.config.ws.server.exception.ConfigServiceException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotFoundException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotImplementedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * ServizioApplicativoAll
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/config/management", name = "ServizioApplicativo")
public interface ServizioApplicativoAll {
	/**
	 * It allows you to retrieve all objects of type ServizioApplicativo that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type ServizioApplicativo
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.FindAllServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.FindAllServizioApplicativo")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<ServizioApplicativo> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		SearchFilterServizioApplicativo filter
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type ServizioApplicativo that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type ServizioApplicativo
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotFoundException_Exception
	 * @throws ConfigMultipleResultException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.FindServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.FindServizioApplicativo")
    @javax.jws.WebResult(name = "servizioApplicativo", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public ServizioApplicativo find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		SearchFilterServizioApplicativo filter
	) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type ServizioApplicativo that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type ServizioApplicativo
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.CountServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.CountServizioApplicativo")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		SearchFilterServizioApplicativo filter
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type ServizioApplicativo identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type ServizioApplicativo
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotFoundException_Exception
	 * @throws ConfigMultipleResultException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.GetServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.GetServizioApplicativo")
    @javax.jws.WebResult(name = "servizioApplicativo", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public ServizioApplicativo get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		org.openspcoop2.core.config.IdServizioApplicativo id
	) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object ServizioApplicativo identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object ServizioApplicativo identified by the id parameter. 
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigMultipleResultException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.ExistsServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.ExistsServizioApplicativo")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		org.openspcoop2.core.config.IdServizioApplicativo id
	) throws ConfigServiceException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.config.IdServizioApplicativo that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.config.IdServizioApplicativo
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.FindAllIdsServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.FindAllIdsServizioApplicativo")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.config.IdServizioApplicativo> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		SearchFilterServizioApplicativo filter
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Indicates the use of the object (identified by parameter) by other components
	 *
	 * @return Indicates the use of the object (identified by parameter) by other components
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotFoundException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "inUseResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.UseInfoServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "inUse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.UseInfoServizioApplicativo")
    @javax.jws.WebResult(name = "inUse", targetNamespace = "http://www.openspcoop2.org/core/config/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="inUse",operationName="inUse")
	public UseInfo inUse(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		org.openspcoop2.core.config.IdServizioApplicativo id
	) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Create the object described by the provided parameter
	 *
	 * @param servizioApplicativo object
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "createResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.CreateServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "create", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.CreateServizioApplicativo")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="create",operationName="create")
	public void create(
		@javax.jws.WebParam(name = "servizio-applicativo", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		ServizioApplicativo servizioApplicativo
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;
	
	/**
	 * Update the object instance identified by the id parameter, using the provided object description.
	 *
	 * @param oldId object id
	 * @param servizioApplicativo object
	 * @throws ConfigServiceException_Exception
 	 * @throws ConfigNotFoundException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "updateResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.UpdateServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "update", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.UpdateServizioApplicativo")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="update",operationName="update")
    public void update(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/config/management")
    	org.openspcoop2.core.config.IdServizioApplicativo oldId,
    	@javax.jws.WebParam(name = "servizio-applicativo", targetNamespace = "http://www.openspcoop2.org/core/config/management")
	    ServizioApplicativo servizioApplicativo
    ) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Update the object istance identified by the id parameter or create a new object, using the provided object description.
	 *
	 * @param oldId object id
	 * @param servizioApplicativo object
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "updateOrCreateResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.UpdateOrCreateServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "updateOrCreate", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.UpdateOrCreateServizioApplicativo")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="updateOrCreate",operationName="updateOrCreate")
	public void updateOrCreate(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		org.openspcoop2.core.config.IdServizioApplicativo oldId, 
		@javax.jws.WebParam(name = "servizio-applicativo", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		ServizioApplicativo servizioApplicativo
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Delete the object instance identified by the id parameter.
	 *
	 * @param id object id
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteByIdResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteByIdServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteById", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteByIdServizioApplicativo")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteById",operationName="deleteById")
	public void deleteById(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		org.openspcoop2.core.config.IdServizioApplicativo id
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Delete all object instances
	 *
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteAllServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAll", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteAllServizioApplicativo")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/core/config/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAll",operationName="deleteAll")
	public long deleteAll() throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;

	/**
	 * Delete all object instances matching the filter parameter
	 *
	 * @param filter Filter
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllByFilterResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteAllByFilterServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllByFilter", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteAllByFilterServizioApplicativo")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/core/config/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAllByFilter",operationName="deleteAllByFilter")
	public long deleteAllByFilter(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		SearchFilterServizioApplicativo filter
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;
	
	/**
	 * Delete the object instance identified by the provided object description.
	 *
	 * @param servizioApplicativo object
	 * @throws ConfigServiceException_Exception
	 * @throws ConfigNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteResponse", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteServizioApplicativoResponse")
    @javax.xml.ws.RequestWrapper(localName = "delete", targetNamespace = "http://www.openspcoop2.org/core/config/management", className = "org.openspcoop2.core.config.ws.wrapped.DeleteServizioApplicativo")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="delete",operationName="delete")
	public void delete(
		@javax.jws.WebParam(name = "servizio-applicativo", targetNamespace = "http://www.openspcoop2.org/core/config/management")
		ServizioApplicativo servizioApplicativo
	) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception;
}