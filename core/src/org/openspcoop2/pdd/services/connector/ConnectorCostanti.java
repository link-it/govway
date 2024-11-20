/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
	
	private ConnectorCostanti() {}

	public static final String SEPARATOR_CODE = "-";
	
	public static final String KEYWORD_METHOD_HTTP = "@METHOD@";
	public static final String MESSAGE_METHOD_HTTP_NOT_SUPPORTED = "Method HTTP "+KEYWORD_METHOD_HTTP+" non supportato";
	
	public static final String KEYWORD_SERVICE_BINDING = "@SERVICE_BINDING@";
	public static final String MESSAGE_SERVICE_BINDING_NOT_SUPPORTED = "ServiceBinding "+KEYWORD_SERVICE_BINDING+" non supportato";

	public static final boolean ASYNC = true;
	public static final boolean SYNC = false;
	
	// ID: 7 cifre (parlante)
	public static final String ID_ERRORE_GENERICO = "0000000";
	// Altri codici definiti in org.openspcoop2.protocol.engine.constants.IDService
	
	
	// CODE: 4 cifre
	
	public static final String GOVWAY_NOT_INITIALIZED = "9999";
	
	public static final String CODE_PROTOCOL_NOT_SUPPORTED = "0001";
	
	public static final String CODE_HTTP_METHOD_GET_UNSUPPORTED = "0011";
	public static final String CODE_HTTP_METHOD_POST_UNSUPPORTED = "0012";
	public static final String CODE_HTTP_METHOD_PUT_UNSUPPORTED = "0013";
	public static final String CODE_HTTP_METHOD_HEAD_UNSUPPORTED = "0014";
	public static final String CODE_HTTP_METHOD_DELETE_UNSUPPORTED = "0015";
	public static final String CODE_HTTP_METHOD_OPTIONS_UNSUPPORTED = "0016";
	public static final String CODE_HTTP_METHOD_TRACE_UNSUPPORTED = "0017";
	public static final String CODE_HTTP_METHOD_PATCH_UNSUPPORTED = "0018";
	public static final String CODE_HTTP_METHOD_LINK_UNSUPPORTED = "0019";
	public static final String CODE_HTTP_METHOD_UNLINK_UNSUPPORTED = "0020";
	
	public static final String CODE_WSDL_UNSUPPORTED = "0021";
	public static final String CODE_WSDL_NOT_DEFINED = "0022";
	
	public static final String CODE_ENGINE_FILTER = "0031";

	public static final String CODE_FUNCTION_UNSUPPORTED = "0041";
	
	public static final String CODE_SERVICE_BINDING_SOAP_UNSUPPORTED = "0051";
	public static final String CODE_SERVICE_BINDING_REST_UNSUPPORTED = "0052";
	
}
