/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver;

/**
 * Costanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti  {
	
	public static final String MULTI_DELIVER_CONNETTORE_API = "md_api_connector"; 
	
	public static final String MULTI_DELIVER_NOTIFICHE_BY_ESITO = "md_notifiche_by_esito"; // true/false
	
	public static final String MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK = "md_notifiche_by_esito_ok"; // true/false
	public static final String MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT = "md_notifiche_by_esito_fault"; // true/false
	public static final String MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA = "md_notifiche_by_esito_errori_consegna"; // true/false
	public static final String MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO = "md_notifiche_by_esito_processamento"; // true/false
	public static final String MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE = "md_notifiche_by_esito_scartate"; // true/false
	
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_CADENZA = "md_notifiche_err_cadenza"; // integer
	
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_VALUE_CONSEGNA_COMPLETATA = "ok";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_VALUE_CONSEGNA_FALLITA = "ko";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_VALUE_CODICI_CONSEGNA_COMPLETATA = "ok_codeList";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_VALUE_INTERVALLO_CONSEGNA_COMPLETATA = "ok_codeInterval";
	
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX = "md_notifiche_err_trasporto_2xx";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST = "md_notifiche_err_trasporto_2xx_codes";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_LEFT_INTERVAL = "md_notifiche_err_trasporto_2xx_left";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_RIGHT_INTERVAL = "md_notifiche_err_trasporto_2xx_right";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX = "md_notifiche_err_trasporto_3xx";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST = "md_notifiche_err_trasporto_3xx_codes";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_LEFT_INTERVAL = "md_notifiche_err_trasporto_3xx_left";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_RIGHT_INTERVAL = "md_notifiche_err_trasporto_3xx_right";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX = "md_notifiche_err_trasporto_4xx";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST = "md_notifiche_err_trasporto_4xx_codes";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_LEFT_INTERVAL = "md_notifiche_err_trasporto_4xx_left";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_RIGHT_INTERVAL = "md_notifiche_err_trasporto_4xx_right";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX = "md_notifiche_err_trasporto_5xx";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST = "md_notifiche_err_trasporto_5xx_codes";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_LEFT_INTERVAL = "md_notifiche_err_trasporto_5xx_left";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_RIGHT_INTERVAL = "md_notifiche_err_trasporto_5xx_right";
	
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_VALUE_CONSEGNA_COMPLETATA = "ok";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_VALUE_CONSEGNA_FALLITA = "ko";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_VALUE_CODICI_CONSEGNA_COMPLETATA = "custom";
	
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT = "md_notifiche_err_fault";
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_CODE = "md_notifiche_err_fault_code"; // status in problem
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_ACTOR = "md_notifiche_err_fault_actor"; // type in problem
	public static final String MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_MESSAGE = "md_notifiche_err_fault_message"; // claims in problem
	
	
}
