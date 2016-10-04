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
	public final static String MESSAGE_METHOD_HTTP_NOT_SUPPORTED = "Method HTTP @METHOD@ non supportato";

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
	
	public final static String CODE_WSDL = "0021";
	
	public final static String CODE_ENGINE_FILTER = "0031";

	public final static String CODE_FUNCTION_UNSUPPORTED = "0041";
	
}
