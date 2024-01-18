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

package org.openspcoop2.pdd.core.controllo_traffico;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**     
 * CostantiControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiControlloTraffico {

	public static final MapKey<String> PDD_CONTEXT_PDD_CONGESTIONATA = Map.newMapKey("PDD_CONGESTIONATA");
	
	public static final MapKey<String> PDD_CONTEXT_POLICY_CONFIG_PORTA = Map.newMapKey("POLICY_CONFIG_PORTA");
	public static final MapKey<String> PDD_CONTEXT_POLICY_CONFIG_GLOBALE = Map.newMapKey("POLICY_CONFIG_GLOBALE");
	public static final MapKey<String> PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT = Map.newMapKey("POLICY_API_OR_GLOBAL_OR_DEFAULT");
	public static final String PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_API = "API";
	public static final String PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_GLOBAL = "GLOBAL";
	public static final String PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_DEFAULT = "DEFAULT";
	public static final MapKey<String> PDD_CONTEXT_LIST_GROUP_BY_CONDITION = Map.newMapKey("POLICY_GROUP_BY_CONDITION");
	public static final MapKey<String> PDD_CONTEXT_LIST_UNIQUE_ID_POLICY = Map.newMapKey("POLICY_IDS");
	public static final MapKey<String> PDD_CONTEXT_LIST_POLICY_APPLICABILE = Map.newMapKey("POLICY_APPLICABILE");
	public static final MapKey<String> PDD_CONTEXT_LIST_POLICY_VIOLATA = Map.newMapKey("POLICY_VIOLATA");
	public static final MapKey<String> PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO = Map.newMapKey("MAX_REQUEST_VIOLATED");
	public static final MapKey<String> PDD_CONTEXT_MAX_REQUEST_VIOLATED_EVENTO = Map.newMapKey("MAX_REQUEST_VIOLATED_EVENTO");
	public static final MapKey<String> PDD_CONTEXT_MAX_REQUEST_VIOLATED_URL_INVOCAZIONE = Map.newMapKey("MAX_REQUEST_VIOLATED_URL_INVOCAZIONE");
	public static final MapKey<String> PDD_CONTEXT_MAX_REQUEST_VIOLATED_CREDENZIALI = Map.newMapKey("MAX_REQUEST_VIOLATED_CREDENZIALI");
	public static final MapKey<String> PDD_CONTEXT_HEADER_RATE_LIMITING = Map.newMapKey("HEADER_RATE_LIMITING");
	
	public static final String PARAMETRO_CONTROLLO_TRAFFICO_REDEFINE_TEMPI_RISPOSTA = "CTRedefineTempiRisposta";
	public static final String PARAMETRO_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT = "CTConnectionTimeout";
	public static final String PARAMETRO_CONTROLLO_TRAFFICO_READ_TIMEOUT = "CTReadTimeout";
	public static final String PARAMETRO_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA = "CTTempoMedioRisposta";
	
	public static final String HTML_ERROR_TITLE_TEMPLATE = "TITLE"; 
	public static final String HTML_ERROR_MESSAGE_TEMPLATE = "MESSAGE"; 
	public static final String HTML_ERROR = 
		"<html>\n" + 
		"   <head>\n" +
		"      <title>"+HTML_ERROR_TITLE_TEMPLATE+"</title>\n" + 
		"   </head>\n" + 
		"   <body>\n" + 
		"      <h1>"+HTML_ERROR_TITLE_TEMPLATE+"</h1>\n" + 
		"      <p>"+HTML_ERROR_MESSAGE_TEMPLATE+"</p>\n" + 
		"   </body>\n" + 
		"</html>";
	
	public static final String HTML_429_TOO_MANY_REQUESTS_ERROR =  HTML_ERROR.replace(HTML_ERROR_TITLE_TEMPLATE, "Too Many Requests");
	public static final String HTML_429_LIMIT_EXCEEDED_ERROR =  HTML_ERROR.replace(HTML_ERROR_TITLE_TEMPLATE, "Limit Exceeded");
	public static final String HTML_503_ERROR =  HTML_ERROR.replace(HTML_ERROR_TITLE_TEMPLATE, "Service Unavailable");
	public static final String HTML_500_ERROR =  HTML_ERROR.replace(HTML_ERROR_TITLE_TEMPLATE, "Internal Server Error");
}
