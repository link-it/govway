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
package org.openspcoop2.core.diagnostica.ws.server;

import org.openspcoop2.core.diagnostica.ws.server.filter.SearchFilterInformazioniProtocolloTransazione;
import java.util.List;


import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;

import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaServiceException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotFoundException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaMultipleResultException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotImplementedException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * InformazioniProtocolloTransazioneSearch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "InformazioniProtocolloTransazione")
public interface InformazioniProtocolloTransazioneSearch {
	/**
	 * It allows you to retrieve all objects of type InformazioniProtocolloTransazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type InformazioniProtocolloTransazione
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindAllInformazioniProtocolloTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindAllInformazioniProtocolloTransazione")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<InformazioniProtocolloTransazione> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		SearchFilterInformazioniProtocolloTransazione filter
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type InformazioniProtocolloTransazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return object of type InformazioniProtocolloTransazione
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotFoundException_Exception
	 * @throws DiagnosticaMultipleResultException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindInformazioniProtocolloTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "find", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindInformazioniProtocolloTransazione")
    @javax.jws.WebResult(name = "informazioniProtocolloTransazione", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="find",operationName="find")
	public InformazioniProtocolloTransazione find(
	    @javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		SearchFilterInformazioniProtocolloTransazione filter
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotFoundException_Exception,DiagnosticaMultipleResultException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	/**
	 * It allows you to count all objects of type InformazioniProtocolloTransazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type InformazioniProtocolloTransazione
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.CountInformazioniProtocolloTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.CountInformazioniProtocolloTransazione")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		SearchFilterInformazioniProtocolloTransazione filter
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve the object of type InformazioniProtocolloTransazione identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return object of type InformazioniProtocolloTransazione
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotFoundException_Exception
	 * @throws DiagnosticaMultipleResultException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "getResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.GetInformazioniProtocolloTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "get", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.GetInformazioniProtocolloTransazione")
    @javax.jws.WebResult(name = "informazioniProtocolloTransazione", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="get",operationName="get")
	public InformazioniProtocolloTransazione get(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione id
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotFoundException_Exception,DiagnosticaMultipleResultException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	/**
	 * Indicates the existence of the instance of the object InformazioniProtocolloTransazione identified by the id parameter.
	 *
	 * @param id Object Id
	 * @return Indicates the existence of the instance of the object InformazioniProtocolloTransazione identified by the id parameter. 
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaMultipleResultException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */	
	@javax.xml.ws.ResponseWrapper(localName = "existsResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.ExistsInformazioniProtocolloTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "exists", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.ExistsInformazioniProtocolloTransazione")
    @javax.jws.WebResult(name = "exists", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="exists",operationName="exists")
	public boolean exists(
		@javax.jws.WebParam(name = "id", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione id
	) throws DiagnosticaServiceException_Exception,DiagnosticaMultipleResultException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	/**
	 * It allows you to retrieve all object Ids of type org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List object ids of type org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllIdsResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindAllIdsInformazioniProtocolloTransazioneResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAllIds", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindAllIdsInformazioniProtocolloTransazione")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAllIds",operationName="findAllIds")
	public List<org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione> findAllIds(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		SearchFilterInformazioniProtocolloTransazione filter
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	

}