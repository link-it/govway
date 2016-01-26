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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.Vector;

/**
 * UtentiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_UTENTI = "utenti";
	
	public final static String OBJECT_NAME_UTENTE = "utente";
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_UTENTI_ADD = OBJECT_NAME_UTENTI+"Add.do";
	public final static String SERVLET_NAME_UTENTI_CHANGE = OBJECT_NAME_UTENTI+"Change.do";
	public final static String SERVLET_NAME_UTENTI_DELETE = OBJECT_NAME_UTENTI+"Del.do";
	public final static String SERVLET_NAME_UTENTI_LIST = OBJECT_NAME_UTENTI+"List.do";
	public final static Vector<String> SERVLET_UTENTI = new Vector<String>();
	static{
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_ADD);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_CHANGE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_DELETE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_LIST);
	}
	
	public final static String SERVLET_NAME_UTENTE_CHANGE = OBJECT_NAME_UTENTE+"Change.do";
	public final static Vector<String> SERVLET_UTENTE = new Vector<String>();
	static{
		SERVLET_UTENTE.add(SERVLET_NAME_UTENTE_CHANGE);
	}
	

	/* STRUTS FORWARD */
	
	public final static String STRUTS_FORWARD_INFO = "Info";
	public final static String STRUTS_FORWARD_PERMESSI_OK = "PermessiOk";
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_UTENTI = "Utenti";
	public final static String LABEL_UTENTE = "Profilo Utente";
	public final static String LABEL_INFORMAZIONI_UTENTE = "Informazioni Utente";
	public final static String LABEL_PASSWORD = "Password";
	public final static String LABEL_MODALITA_INTERFACCIA = "Modalità Interfaccia";
	public final static String LABEL_PERMESSI_GESTIONE = "Permessi di Gestione";
	public final static String LABEL_CAMBIA_IDENTITA = "Cambia identità";
	public final static String LABEL_ACCEDI = "Accedi";
	public final static String LABEL_MODIFICA = "Modifica";
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_UTENTI_USERNAME = "nomesu";
	public final static String PARAMETRO_UTENTI_PASSWORD = "pwsu";
	public final static String PARAMETRO_UTENTI_CONFERMA_PASSWORD = "confpwsu";
	public final static String PARAMETRO_UTENTI_CHANGE_PASSWORD = "changepwd";
	public final static String PARAMETRO_UTENTI_TIPO_GUI = "tipo_gui";
	public final static String PARAMETRO_UTENTI_IS_SERVIZI = "isServizi";
	public final static String PARAMETRO_UTENTI_IS_DIAGNOSTICA = "isDiagnostica";
	public final static String PARAMETRO_UTENTI_IS_SISTEMA = "isSistema";
	public final static String PARAMETRO_UTENTI_IS_MESSAGGI = "isMessaggi";
	public final static String PARAMETRO_UTENTI_IS_UTENTI = "isUtenti";
	public final static String PARAMETRO_UTENTI_IS_AUDITING = "isAuditing";
	public final static String PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE = "isAccordiCooperazione";
	public final static String PARAMETRO_UTENTI_SINGLE_SU_SERVIZI = "singleSuS";
	public final static String PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE = "singleSuAC";
	
	public final static String PARAMETRO_UTENTE_LOGIN = "login";
	public final static String PARAMETRO_UTENTE_PASSWORD = "password";
	public final static String PARAMETRO_UTENTE_VECCHIA_PASSWORD = "oldpw";
	public final static String PARAMETRO_UTENTE_NUOVA_PASSWORD = "newpw";
	public final static String PARAMETRO_UTENTE_CONFERMA_NUOVA_PASSWORD = "confpw";
	public final static String PARAMETRO_UTENTE_CHANGE_PASSWORD = "changepwd";
	public final static String PARAMETRO_UTENTE_TIPO_GUI = "tipo_gui";
	public final static String PARAMETRO_UTENTE_CHANGE_GUI = "change_gui";
	public final static String PARAMETRO_UTENTE_ESEGUI = "changePw";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_UTENTI_USERNAME = "Nome";
	public final static String LABEL_PARAMETRO_UTENTI_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD = "Conferma Password";
	public final static String LABEL_PARAMETRO_UTENTI_CHANGE_PASSWORD = "Nuova Password";
	public final static String LABEL_PARAMETRO_UTENTI_TIPO_GUI =  "Tipo";
	public final static String LABEL_PARAMETRO_UTENTI_IS_SERVIZI = "Servizi [S]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_DIAGNOSTICA = "Diagnostica [D]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_SISTEMA = "Sistema [C]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_MESSAGGI = "Coda Messaggi [M]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_UTENTI = "Utenti [U]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_AUDITING = "Auditing [A]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE = "Accordi Cooperazione [P]";

	public final static String LABEL_PARAMETRO_UTENTE_VECCHIA_PASSWORD = "Vecchia";
	public final static String LABEL_PARAMETRO_UTENTE_CONFERMA_NUOVA_PASSWORD = "Conferma Nuova";
	public final static String LABEL_PARAMETRO_UTENTE_NUOVA_PASSWORD = "Nuova";
	
	public final static String LABEL_UTENTE_PERMESSI_SERVIZI = "Utente con permessi " + LABEL_PARAMETRO_UTENTI_IS_SERVIZI;
	public final static String LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE = "Utente con permessi " + LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE;
	
	
}
