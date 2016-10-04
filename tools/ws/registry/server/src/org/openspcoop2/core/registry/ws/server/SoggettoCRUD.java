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



import org.openspcoop2.core.registry.Soggetto;

import org.openspcoop2.core.registry.ws.server.exception.RegistryServiceException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotFoundException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotImplementedException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * SoggettoCRUD
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/registry/management", name = "Soggetto")
public interface SoggettoCRUD {

	/**
	 * Create the object described by the provided parameter
	 *
	 * @param soggetto object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "createResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CreateSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "create", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.CreateSoggetto")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="create",operationName="create")
	public void create(
		@javax.jws.WebParam(name = "soggetto", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		Soggetto soggetto
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
	
	/**
	 * Update the object instance identified by the id parameter, using the provided object description.
	 *
	 * @param oldId object id
	 * @param soggetto object
	 * @throws RegistryServiceException_Exception
 	 * @throws RegistryNotFoundException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "updateResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "update", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateSoggetto")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="update",operationName="update")
    public void update(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
    	org.openspcoop2.core.registry.IdSoggetto oldId,
    	@javax.jws.WebParam(name = "soggetto", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	    Soggetto soggetto
    ) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Update the object istance identified by the id parameter or create a new object, using the provided object description.
	 *
	 * @param oldId object id
	 * @param soggetto object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "updateOrCreateResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateOrCreateSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "updateOrCreate", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.UpdateOrCreateSoggetto")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="updateOrCreate",operationName="updateOrCreate")
	public void updateOrCreate(
		@javax.jws.WebParam(name = "oldId", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdSoggetto oldId, 
		@javax.jws.WebParam(name = "soggetto", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		Soggetto soggetto
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete the object instance identified by the id parameter.
	 *
	 * @param id object id
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */		
	@javax.xml.ws.ResponseWrapper(localName = "deleteByIdResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteByIdSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteById", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteByIdSoggetto")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteById",operationName="deleteById")
	public void deleteById(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		org.openspcoop2.core.registry.IdSoggetto id
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;

	/**
	 * Delete all object instances
	 *
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAll", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllSoggetto")
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
	@javax.xml.ws.ResponseWrapper(localName = "deleteAllByFilterResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllByFilterSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllByFilter", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteAllByFilterSoggetto")
    @javax.jws.WebResult(name = "deletedItems", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="deleteAllByFilter",operationName="deleteAllByFilter")
	public long deleteAllByFilter(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		SearchFilterSoggetto filter
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
	
	/**
	 * Delete the object instance identified by the provided object description.
	 *
	 * @param soggetto object
	 * @throws RegistryServiceException_Exception
	 * @throws RegistryNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "deleteResponse", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteSoggettoResponse")
    @javax.xml.ws.RequestWrapper(localName = "delete", targetNamespace = "http://www.openspcoop2.org/core/registry/management", className = "org.openspcoop2.core.registry.ws.wrapped.DeleteSoggetto")
	@javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="delete",operationName="delete")
	public void delete(
		@javax.jws.WebParam(name = "soggetto", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
		Soggetto soggetto
	) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception;
}