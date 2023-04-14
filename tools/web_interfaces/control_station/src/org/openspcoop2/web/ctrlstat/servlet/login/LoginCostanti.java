/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.login;

import java.util.List;
import java.util.ArrayList;

/**
 * LoginCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoginCostanti {
	
	private LoginCostanti() {}
	

	/* OBJECT NAME */
	
	public static final String OBJECT_NAME_LOGIN = "login";
	
	public static final String OBJECT_NAME_LOGIN_AS_SU = "loginAsSu";
	
	public static final String OBJECT_NAME_LOGOUT = "logout";
	
	public static final String OBJECT_NAME_MESSAGE_PAGE = "messagePage";
	public static final String OBJECT_NAME_LOGIN_MESSAGE_PAGE = "loginMessagePage";
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_LOGIN = OBJECT_NAME_LOGIN+".do";
	private static final List<String> SERVLET_LOGIN = new ArrayList<>();
	public static List<String> getServletLogin() {
		return SERVLET_LOGIN;
	}
	static{
		SERVLET_LOGIN.add(SERVLET_NAME_LOGIN);
	}
	
	public static final String SERVLET_NAME_LOGIN_AS_SU = OBJECT_NAME_LOGIN_AS_SU+".do";
	private static final List<String> SERVLET_LOGIN_AS_SU = new ArrayList<>();
	public static List<String> getServletLoginAsSu() {
		return SERVLET_LOGIN_AS_SU;
	}
	static{
		SERVLET_LOGIN_AS_SU.add(SERVLET_NAME_LOGIN_AS_SU);
	}
	
	public static final String SERVLET_NAME_LOGOUT = OBJECT_NAME_LOGOUT+".do";
	private static final List<String> SERVLET_LOGOUT = new ArrayList<>();
	public static List<String> getServletLogout() {
		return SERVLET_LOGOUT;
	}
	static{
		SERVLET_LOGOUT.add(SERVLET_NAME_LOGOUT);
	}
	
	public static final String SERVLET_NAME_LOGIN_MESSAGE_PAGE = OBJECT_NAME_LOGIN_MESSAGE_PAGE+".do";
	public static final String SERVLET_NAME_MESSAGE_PAGE = OBJECT_NAME_MESSAGE_PAGE+".do";
	
	private static final List<String> SERVLET_MESSAGE_PAGE = new ArrayList<>();
	public static List<String> getServletMessagePage() {
		return SERVLET_MESSAGE_PAGE;
	}
	static{
		SERVLET_MESSAGE_PAGE.add(SERVLET_NAME_MESSAGE_PAGE);
		SERVLET_MESSAGE_PAGE.add(SERVLET_NAME_LOGIN_MESSAGE_PAGE);
	}
	
	
	/* LABEL GENERALI */
	
	public static final String LABEL_LOGIN = "Login";
	public static final String LABEL_USERNAME = "Username";
	public static final String LABEL_LOGOUT = "Logout";
	public static final String LABEL_LOGIN_ATTUALMENTE_CONNESSO = "Utente: ";
	public static final String LABEL_LOGIN_ATTUALMENTE_CONNESSO_WITH_PARAM = "Utente: <b>{0}</b>";
	public static final String LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO = "Login effettuato con successo";
	public static final String LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO = "Logout effettuato con successo";
	public static final String LABEL_LOGIN_SESSIONE_SCADUTA = "La sessione &egrave; scaduta. Effettuare il login";
	public static final String LABEL_LOGIN_AUTORIZZAZIONE_NEGATA = "Autorizzazione negata";
	public static final String LABEL_LOGIN_ERRORE = "L'ultima operazione effettuata ha provocato un errore che ha reso l'interfaccia non utilizzabile.<BR><BR>Effettuare nuovamente il login";
	public static final String LABEL_ERRORE = "Errore";
	public static final String LABEL_CONSOLE_RIPRISTINATA = "Console ripristinata con successo.";
	public static final String LABEL_LOGIN_PASSWORD_AGGIORNATA_CON_SUCCESSO = "Password aggiornata con successo";
	
	/* LABEL MENU' AZIONI UTENTE */
	
	public static final String LABEL_MENU_UTENTE_MODALITA_AVANZATA = "Modalit&agrave; avanzata";
	public static final String LABEL_MENU_UTENTE_MODALITA_STANDARD = "Modalit&agrave; standard";
	public static final String ICONA_MENU_UTENTE_CHECKED = "checkbox-checked-white";
	public static final String ICONA_MENU_UTENTE_UNCHECKED = "checkbox-unchecked-white";
	public static final String LABEL_MENU_UTENTE_INFORMAZIONI = "Informazioni";
	public static final String LABEL_MENU_UTENTE_PROFILO_UTENTE = "Profilo Utente";
	public static final String LABEL_MENU_UTENTE_LOGOUT = "Logout";
	
	public static final String LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": {0}";
	public static final String LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM = "Soggetto: {0}";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_LOGIN_LOGIN = "login";
	public static final String PARAMETRO_LOGIN_PASSWORD = "password";
	public static final String PARAMETRO_LOGIN_DESTINAZIONE = "dest";
	
	/* ATTRIBUTI */
	public static final String ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA = "changePwdScaduta";
	
	/* OTHER */
	
	public static final String LOGIN_JSP = "/jsplib/login.jsp";
	public static final String INFO_JSP ="/jsplib/info.jsp";
	
	public static final String LOGIN_AS_JSP ="/public/loginAS.jsp";
	public static final String LOGOUT_AS_JSP ="/public/logoutAS.jsp";
	public static final String LOGIN_FAILURE_JSP ="/public/loginFailure.jsp";
	
	/* EVENTO PRESSIONE TASTO LOGIN */
	public static final String LOGIN_JS_FUNCTION = "CheckDati();return false;";

	public static final String MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE = "L'utente non &egrave; abilitato ad utilizzare la console"; 
	public static final String MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO = "L'utente non &egrave; abilitato ad utilizzare la console: configurazione incompleta"; 
	public static final String MESSAGGIO_INFO_CONTROLLO_CONSISTENZA_DATI_IN_CORSO =	"<b>Attenzione</b>: è in esecuzione un controllo sulla consistenza dei dati; attendere il completamento dell'operazione";
}
