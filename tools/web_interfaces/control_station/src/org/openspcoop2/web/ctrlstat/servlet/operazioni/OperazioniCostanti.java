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
package org.openspcoop2.web.ctrlstat.servlet.operazioni;

import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * OperazioniCostanti
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioniCostanti {
	
	private OperazioniCostanti() {}
	

	/* OBJECT NAME */

	public static final String OBJECT_NAME_OPERAZIONI = "operazioni";
	
	public static final ForwardParams TIPO_OPERAZIONE_OPERAZIONI = ForwardParams.OTHER("");
	public static final ForwardParams TIPO_OPERAZIONE_DETAIL = ForwardParams.OTHER("Detail");

	/* SERVLET NAME */
	public static final String SERVLET_NAME_OPERAZIONI = OBJECT_NAME_OPERAZIONI+".do";
	
	public static final String SERVLET_NAME_OPERAZIONI_DELETE = OBJECT_NAME_OPERAZIONI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_OPERAZIONI_LIST = OBJECT_NAME_OPERAZIONI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_OPERAZIONI = new ArrayList<>();
	public static List<String> getServletOperazioni() {
		return SERVLET_OPERAZIONI;
	}
	static{
		SERVLET_OPERAZIONI.add(SERVLET_NAME_OPERAZIONI);
		SERVLET_OPERAZIONI.add(SERVLET_NAME_OPERAZIONI_DELETE);
		SERVLET_OPERAZIONI.add(SERVLET_NAME_OPERAZIONI_LIST);
	}

	/* ATTRIBUTI SESSIONE */
	
	public static final String SESSION_ATTRIBUTE_FORM_BEAN = "formBean";
	public static final String SESSION_ATTRIBUTE_FILTER_SEARCH = "filterSearch";
	
	/* LABEL GENERALI */
	
	public static final String LABEL_OPERAZIONI = "Coda Configurazioni";
	public static final String LABEL_OPERAZIONI_CONFIGURAZIONI = "Configurazioni";
	public static final String LABEL_OPERAZIONI_RISULTATI_RICERCA = "Risultati ricerca";
	public static final String LABEL_OPERAZIONI_PARAMETRO_ERRORE = "Errore";
	public static final String LABEL_OPERAZIONI_RICHIESTA_IL = "Richiesta il ";
	public static final String LABEL_OPERAZIONI_DETTAGLI = "Dettagli";
	public static final String LABEL_PARAMETRI = "Parametri";
	public static final String LABEL_OPERAZIONI_ESEGUITA_IL = "Eseguita il";
	public static final String LABEL_OPERAZIONI_ELIMINATA_IL = "Eliminata il";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_OPERAZIONI_OPERAZIONE = "op";
	public static final String PARAMETRO_OPERAZIONI_ID = "idOp";
	public static final String PARAMETRO_OPERAZIONI_UTENTE = "user";
	public static final String PARAMETRO_OPERAZIONI_METHOD = "method";
	public static final String PARAMETRO_OPERAZIONI_PAGE_SIZE = "pageSize";
	public static final String PARAMETRO_OPERAZIONI_ACTION = "action";
	public static final String PARAMETRO_OPERAZIONI_NEW_SEARCH = "newSearch";
	public static final String PARAMETRO_OPERAZIONI_PAR = "par";
	public static final String PARAMETRO_OPERAZIONI_WAIT_TIME = "waitTime";
	public static final String PARAMETRO_OPERAZIONI_TEMPO_ESECUZIONE = "timexecute";
	public static final String PARAMETRO_OPERAZIONI_ERRORE = "errore";
	
	
	/*  LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE = "Tipo";
	public static final String LABEL_PARAMETRO_OPERAZIONI_UTENTE = "Utente";
	
	public static final String LABEL_PARAMETRO_OPERAZIONI_UTENTE_ALL = "--";
	
	public static final String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA = "In Coda";
	public static final String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE = "Eseguite";
	public static final String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA = "In Attesa";
	public static final String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE = "Invalide";
	public static final String LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE = "Fallite";
	
	private static final String[] LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_LIST = {
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA,
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE,
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA, 
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE, 
		LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE
	};
	public static String[] getLabelParametroOperazioniOperazioneList() {
		return LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_LIST;
	}


	public static final String LABEL_PARAMETRO_OPERAZIONI_WAITING_TIME = "Waiting Time";
	public static final String LABEL_PARAMETRO_OPERAZIONI_NUOVA_PASSWORD = "NuovaPassword";
	public static final String LABEL_PARAMETRO_OPERAZIONI_PASSWORD = "Password";
	
	
	/* LABEL COLONNE TABELLA SCHERMATA LIST */
	
	public static final String LABEL_PARAMETRO_OPERAZIONI_ID = "Id";
	public static final String LABEL_PARAMETRO_OPERAZIONI_HOST = "Coda";
	public static final String LABEL_PARAMETRO_OPERAZIONI_DATA_RICHIESTA = "Data di Richiesta";
	public static final String LABEL_PARAMETRO_OPERAZIONI_DATA_ESECUZIONE = "Data di Esecuzione";
	public static final String LABEL_PARAMETRO_OPERAZIONI_ELIMINATA = "Dettagli";
	public static final String LABEL_PARAMETRO_OPERAZIONI_ELIMINAZIONE_OPERATORE = "Eliminata dall'utente";
	
	
	/*LABEL PULSANTI */

	public static final String LABEL_OPERAZIONI_BUTTON_OK = "Ok";
	public static final String LABEL_OPERAZIONI_BUTTON_ESEGUI_OPERAZIONE_1 = "EseguiOp(1)";
	public static final String LABEL_OPERAZIONI_BUTTON_ANNULLA = "Annulla";
	public static final String LABEL_OPERAZIONI_BUTTON_ANNULLA_1 = "Annulla(1)"; 
		
	/* VALORI DEFAULT PARAMETRI*/
	
	public static final String DEFAULT_VALUE_PARAMETRO_OPERAZIONI_WAIT_TIME_WAIT = "WAIT";
	
	public static final String PARAMETRO_OPERAZIONI_UTENTE_ALL = "*";
	
	public static final String PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA = "coda";
	public static final String PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE = "eseguite";
	public static final String PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA = "attesa";
	public static final String PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE = "invalide";
	public static final String PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE = "fallite";
	
	private static final String[] PARAMETRO_OPERAZIONI_OPERAZIONE_LIST = {
		PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA,
		PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE,
		PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA, 
		PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE, 
		PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE
	};
	public static String[] getParametroOperazioniOperazioneList() {
		return PARAMETRO_OPERAZIONI_OPERAZIONE_LIST;
	}


	public static final String DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS = "details";
	public static final String DEFAULT_VALUE_FORM_BEAN_METHOD_FORM = "form";
	
	public static final String DEFAULT_VALUE_FALSE = "false";

	
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
