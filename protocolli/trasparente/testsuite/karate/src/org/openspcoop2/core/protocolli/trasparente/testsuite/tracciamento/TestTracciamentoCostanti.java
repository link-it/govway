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
package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento;

/**
* TestTracciamentoCostanti
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TestTracciamentoCostanti {

	private TestTracciamentoCostanti() {}
	
	public static final String HTTP_TOKEN = "GovWay-TestSuite-Token";
	
	public static final int SLEEP_BEFORE_CHECK_RESOURCES = 2000;
	public static final int SLEEP_AFTER_CHECK_RESOURCES = SLEEP_BEFORE_CHECK_RESOURCES + 500;
	
	public static final String RISORSA_4FASI = "fasi4";
	public static final String RISORSA_DEFAULT = "default";
	public static final String RISORSA_ABILITATO = "abilitato";
	public static final String RISORSA_DISABILITATO = "disabilitato";
	public static final String RISORSA_FAULT = "fault";
	public static final String RISORSA_INFO = "info";
	public static final String RISORSA_INFO_INVERTITE = "infoInvertite";
	public static final String RISORSA_IN_REQUEST = "inRequest";
	public static final String RISORSA_OUT_REQUEST = "outRequest";
	public static final String RISORSA_OUT_RESPONSE = "outResponse";
	public static final String RISORSA_IN_REQUEST_OUT_REQUEST = "inRequest-outRequest";
	public static final String RISORSA_IN_REQUEST_OUT_RESPONSE = "inRequest-outResponse";
	public static final String RISORSA_OUT_REQUEST_OUT_RESPONSE = "outRequest-outResponse";
	
	public static final String ERRORE_DATABASE_FASE_IN_REQUEST = "Errore durante il tracciamento 'database' (fase: Richiesta ricevuta): Test Manually Exception generated in phase 'IN_REQUEST'";
	public static final String ERRORE_DATABASE_FASE_OUT_REQUEST = "Errore durante il tracciamento 'database' (fase: Richiesta in consegna): Test Manually Exception generated in phase 'OUT_REQUEST'";
	public static final String ERRORE_DATABASE_FASE_OUT_RESPONSE = "Errore durante il tracciamento 'database' (fase: Risposta in consegna): Test Manually Exception generated in phase 'OUT_RESPONSE'";
	
	public static final String DETAIL = "{\"type\":\"https://httpstatuses.com/500\",\"title\":\"Internal Server Error\",\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
	public static final String DETAIL_MESSAGGIO = "Ricevuto un Problem Detail (RFC 7807) in seguito all'invio del messaggio applicativo: "+DETAIL;
	
	
}
