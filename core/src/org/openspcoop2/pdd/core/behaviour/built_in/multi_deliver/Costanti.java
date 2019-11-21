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
	
}
