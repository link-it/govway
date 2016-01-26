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
package org.openspcoop2.web.ctrlstat.servlet.operazioni;

import java.util.Vector;

import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * OperazioniCostanti
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioniCostanti {

	/* OBJECT NAME */

	public final static String OBJECT_NAME_OPERAZIONI = "operazioni";
	
	public final static ForwardParams TIPO_OPERAZIONE_OPERAZIONI = ForwardParams.OTHER("");
	public final static ForwardParams TIPO_OPERAZIONE_DETAIL = ForwardParams.OTHER("Detail");

	/* SERVLET NAME */
	public final static String SERVLET_NAME_OPERAZIONI = OBJECT_NAME_OPERAZIONI+".do";
	
	public final static String SERVLET_NAME_OPERAZIONI_DELETE = OBJECT_NAME_OPERAZIONI+"Del.do";
	public final static String SERVLET_NAME_OPERAZIONI_LIST = OBJECT_NAME_OPERAZIONI+"List.do";
	public final static Vector<String> SERVLET_OPERAZIONI = new Vector<String>();
	static{
		SERVLET_OPERAZIONI.add(SERVLET_NAME_OPERAZIONI);
		SERVLET_OPERAZIONI.add(SERVLET_NAME_OPERAZIONI_DELETE);
		SERVLET_OPERAZIONI.add(SERVLET_NAME_OPERAZIONI_LIST);
	}

	/* ATTRIBUTI SESSIONE */
	
	public final static String SESSION_ATTRIBUTE_FORM_BEAN = "formBean";
	public final static String SESSION_ATTRIBUTE_FILTER_SEARCH = "filterSearch";
	
	/* LABEL GENERALI */
	
	public final static String LABEL_OPERAZIONI = "Coda Configurazioni";
	public final static String LABEL_OPERAZIONI_RISULTATI_RICERCA = "Risultati ricerca";
	public static final String LABEL_OPERAZIONI_PARAMETRO_ERRORE = "Errore";
	public static final String LABEL_OPERAZIONI_RICHIESTA_IL = "Richiesta il ";
	public static final String LABEL_OPERAZIONI_DETTAGLI = "Dettagli";
	public static final String LABEL_PARAMETRI = "Parametri";
	public static final String LABEL_OPERAZIONI_ESEGUITA_IL = "Eseguita il";
	public static final String LABEL_OPERAZIONI_ELIMINATA_IL = "Eliminata il";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_OPERAZIONI_OPERAZIONE = "op";
	public final static String PARAMETRO_OPERAZIONI_ID = "idOp";
	public final static String PARAMETRO_OPERAZIONI_UTENTE = "user";
	public final static String PARAMETRO_OPERAZIONI_METHOD = "method";
	public final static String PARAMETRO_OPERAZIONI_PAGE_SIZE = "pageSize";
	public final static String PARAMETRO_OPERAZIONI_ACTION = "action";
	public final static String PARAMETRO_OPERAZIONI_NEW_SEARCH = "newSearch";
	public static final String PARAMETRO_OPERAZIONI_PAR = "par";
	public static final String PARAMETRO_OPERAZIONI_WAIT_TIME = "waitTime";
	public static final String PARAMETRO_OPERAZIONI_TEMPO_ESECUZIONE = "timexecute";
	public static final String PARAMETRO_OPERAZIONI_ERRORE = "errore";
	
	
	/*  LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE = "Tipo";
	public final static String LABEL_PARAMETRO_OPERAZIONI_UTENTE = "Utente";
	
	public final static String LABEL_PARAMETRO_OPERAZIONI_UTENTE_ALL = "--";
	
	public final static String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA = "In Coda";
	public final static String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE = "Eseguite";
	public final static String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA = "In Attesa";
	public final static String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE = "Invalide";
	public final static String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE = "Fallite";
	
	public final static String[] LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_LIST = {
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA,
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE,
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA, 
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE, 
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE
	};
	
	public static final String LABEL_PARAMETRO_OPERAZIONI_WAITING_TIME = "Waiting Time";
	public static final String LABEL_PARAMETRO_OPERAZIONI_NUOVA_PASSWORD = "NuovaPassword";
	public static final String LABEL_PARAMETRO_OPERAZIONI_PASSWORD = "Password";
	
	
	/* LABEL COLONNE TABELLA SCHERMATA LIST */
	
	public final static String LABEL_PARAMETRO_OPERAZIONI_ID = "Id";
	public final static String LABEL_PARAMETRO_OPERAZIONI_HOST = "Coda";
	public final static String LABEL_PARAMETRO_OPERAZIONI_DATA_RICHIESTA = "Data di Richiesta";
	public final static String LABEL_PARAMETRO_OPERAZIONI_DATA_ESECUZIONE = "Data di Esecuzione";
	public final static String LABEL_PARAMETRO_OPERAZIONI_ELIMINATA = "Dettagli";
	public final static String LABEL_PARAMETRO_OPERAZIONI_ELIMINAZIONE_OPERATORE = "Eliminata dall'utente";
	
	
	/*LABEL PULSANTI */

	public final static String LABEL_OPERAZIONI_BUTTON_OK = "Ok";
	public final static String LABEL_OPERAZIONI_BUTTON_ESEGUI_OPERAZIONE_1 = "EseguiOp(1)";
	public final static String LABEL_OPERAZIONI_BUTTON_ANNULLA = "Annulla";
	public final static String LABEL_OPERAZIONI_BUTTON_ANNULLA_1 = "Annulla(1)"; 
		
	/* VALORI DEFAULT PARAMETRI*/
	
	public static final String DEFAULT_VALUE_PARAMETRO_OPERAZIONI_WAIT_TIME_WAIT = "WAIT";
	
	public final static String PARAMETRO_OPERAZIONI_UTENTE_ALL = "*";
	
	public final static String PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA = "coda";
	public final static String PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE = "eseguite";
	public final static String PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA = "attesa";
	public final static String PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE = "invalide";
	public final static String PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE = "fallite";
	
	public final static String[] PARAMETRO_OPERAZIONI_OPERAZIONE_LIST = {
		PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA,
		PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE,
		PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA, 
		PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE, 
		PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE
	};
	
	
	public final static String DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS = "details";
	public final static String DEFAULT_VALUE_FORM_BEAN_METHOD_FORM = "form";
	
	public final static String DEFAULT_VALUE_FALSE = "false";

	
	public static final String ERRORE_TIPO_OPERAZIONE_SCONOSCIUTO = "Tipo selezionato sconosciuto. Selezionare un tipo tra: "
			+ LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA + ", "
			+ LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE + ", "
			+ LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA + ", "
			+ LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE + "o "
			+ LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE + "." ;
	
	
	public static String getTipoOperazioneLabelFromValue(String op){
		for (int i = 0; i < PARAMETRO_OPERAZIONI_OPERAZIONE_LIST.length; i++) {
			if(PARAMETRO_OPERAZIONI_OPERAZIONE_LIST[i].equals(op))
				return LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_LIST[i];
		}
		
		return "";
	}
}
