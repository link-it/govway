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
package org.openspcoop2.core.diagnostica.ws.server;

import org.openspcoop2.core.diagnostica.ws.server.filter.SearchFilterMessaggioDiagnostico;
import java.util.List;


import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;

import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaServiceException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotImplementedException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotAuthorizedException_Exception;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**     
 * MessaggioDiagnosticoSearch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", name = "MessaggioDiagnostico")
public interface MessaggioDiagnosticoSearch {
	/**
	 * It allows you to retrieve all objects of type MessaggioDiagnostico that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return List objects of type MessaggioDiagnostico
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "findAllResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindAllMessaggioDiagnosticoResponse")
    @javax.xml.ws.RequestWrapper(localName = "findAll", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.FindAllMessaggioDiagnostico")
    @javax.jws.WebResult(name = "itemsFound", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="findAll",operationName="findAll")
	public List<MessaggioDiagnostico> findAll(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		SearchFilterMessaggioDiagnostico filter
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

	

	/**
	 * It allows you to count all objects of type MessaggioDiagnostico that matching the filter parameter
	 *
	 * @param filter Filter
	 * @return Count all objects of type MessaggioDiagnostico
	 * @throws DiagnosticaServiceException_Exception
	 * @throws DiagnosticaNotImplementedException_Exception
	 */
	@javax.xml.ws.ResponseWrapper(localName = "countResponse", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.CountMessaggioDiagnosticoResponse")
    @javax.xml.ws.RequestWrapper(localName = "count", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management", className = "org.openspcoop2.core.diagnostica.ws.wrapped.CountMessaggioDiagnostico")
    @javax.jws.WebResult(name = "countItems", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
    @javax.jws.WebMethod(action="count",operationName="count")
	public long count(
		@javax.jws.WebParam(name = "filter", targetNamespace = "http://www.openspcoop2.org/core/diagnostica/management")
		SearchFilterMessaggioDiagnostico filter
	) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception;

}