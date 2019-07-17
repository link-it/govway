/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.services.connector;

/**
 * ConnectorCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectorCostanti {

	public final static String SEPARATOR_CODE = "-";
	
	public final static String KEYWORD_METHOD_HTTP = "@METHOD@";
	public final static String MESSAGE_METHOD_HTTP_NOT_SUPPORTED = "Method HTTP "+KEYWORD_METHOD_HTTP+" non supportato";
	
	public final static String KEYWORD_SERVICE_BINDING = "@SERVICE_BINDING@";
	public final static String MESSAGE_SERVICE_BINDING_NOT_SUPPORTED = "ServiceBinding "+KEYWORD_SERVICE_BINDING+" non supportato";

	// ID: 7 cifre (parlante)
	public final static String ID_ERRORE_GENERICO = "0000000";
	// Altri codici definiti in org.openspcoop2.protocol.engine.constants.IDService
	
	
	// CODE: 4 cifre
	
	public final static String CODE_PROTOCOL_NOT_SUPPORTED = "0001";
	
	public final static String CODE_HTTP_METHOD_GET_UNSUPPORTED = "0011";
	public final static String CODE_HTTP_METHOD_POST_UNSUPPORTED = "0012";
	public final static String CODE_HTTP_METHOD_PUT_UNSUPPORTED = "0013";
	public final static String CODE_HTTP_METHOD_HEAD_UNSUPPORTED = "0014";
	public final static String CODE_HTTP_METHOD_DELETE_UNSUPPORTED = "0015";
	public final static String CODE_HTTP_METHOD_OPTIONS_UNSUPPORTED = "0016";
	public final static String CODE_HTTP_METHOD_TRACE_UNSUPPORTED = "0017";
	public final static String CODE_HTTP_METHOD_PATCH_UNSUPPORTED = "0018";
	public final static String CODE_HTTP_METHOD_LINK_UNSUPPORTED = "0019";
	public final static String CODE_HTTP_METHOD_UNLINK_UNSUPPORTED = "0020";
	
	public final static String CODE_WSDL_UNSUPPORTED = "0021";
	public final static String CODE_WSDL_NOT_DEFINED = "0022";
	
	public final static String CODE_ENGINE_FILTER = "0031";

	public final static String CODE_FUNCTION_UNSUPPORTED = "0041";
	
	public final static String CODE_SERVICE_BINDING_SOAP_UNSUPPORTED = "0051";
	public final static String CODE_SERVICE_BINDING_REST_UNSUPPORTED = "0052";
	
}
