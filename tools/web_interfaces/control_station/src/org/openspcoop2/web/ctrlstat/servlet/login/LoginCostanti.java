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
package org.openspcoop2.web.ctrlstat.servlet.login;

import java.util.Vector;

/**
 * LoginCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoginCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_LOGIN = "login";
	
	public final static String OBJECT_NAME_LOGIN_AS_SU = "loginAsSu";
	
	public final static String OBJECT_NAME_LOGOUT = "logout";
	
	public final static String OBJECT_NAME_MESSAGE_PAGE = "messagePage";
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_LOGIN = OBJECT_NAME_LOGIN+".do";
	public final static Vector<String> SERVLET_LOGIN = new Vector<String>();
	static{
		SERVLET_LOGIN.add(SERVLET_NAME_LOGIN);
	}
	
	public final static String SERVLET_NAME_LOGIN_AS_SU = OBJECT_NAME_LOGIN_AS_SU+".do";
	public final static Vector<String> SERVLET_LOGIN_AS_SU = new Vector<String>();
	static{
		SERVLET_LOGIN_AS_SU.add(SERVLET_NAME_LOGIN_AS_SU);
	}
	
	public final static String SERVLET_NAME_LOGOUT = OBJECT_NAME_LOGOUT+".do";
	public final static Vector<String> SERVLET_LOGOUT = new Vector<String>();
	static{
		SERVLET_LOGOUT.add(SERVLET_NAME_LOGOUT);
	}
	
	public final static String SERVLET_NAME_MESSAGE_PAGE = OBJECT_NAME_MESSAGE_PAGE+".do";
	public final static Vector<String> SERVLET_MESSAGE_PAGE = new Vector<String>();
	static{
		SERVLET_MESSAGE_PAGE.add(SERVLET_NAME_MESSAGE_PAGE);
	}
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_LOGIN = "Login";
	public final static String LABEL_LOGOUT = "Logout";
	public final static String LABEL_LOGIN_ATTUALMENTE_CONNESSO = "Utente: ";
	public final static String LABEL_LOGIN_ATTUALMENTE_CONNESSO_WITH_PARAM = "Utente: <b>{0}</b>";
	public final static String LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO = "Login effettuato con successo";
	public final static String LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO = "Logout effettuato con successo";
	public final static String LABEL_LOGIN_SESSIONE_SCADUTA = "La sessione &egrave; scaduta. Effettuare il login";
	public final static String LABEL_LOGIN_AUTORIZZAZIONE_NEGATA = "Autorizzazione negata";
	public final static String LABEL_LOGIN_ERRORE = "L'ultima operazione effettuata ha provocato un errore che ha reso l'interfaccia non utilizzabile.<BR><BR>Effettuare nuovamente il login";
	public final static String LABEL_ERRORE = "Errore";
	public final static String LABEL_CONSOLE_RIPRISTINATA = "Console ripristinata con successo.";
	
	/* LABEL MENU' AZIONI UTENTE */
	
	public final static String LABEL_MENU_UTENTE_MODALITA_AVANZATA = "Modalit&agrave; avanzata";
	public final static String LABEL_MENU_UTENTE_MODALITA_STANDARD = "Modalit&agrave; standard";
	public final static String ICONA_MENU_UTENTE_CHECKED = "checkbox-checked-white";
	public final static String ICONA_MENU_UTENTE_UNCHECKED = "checkbox-unchecked-white";
	public final static String LABEL_MENU_UTENTE_INFORMAZIONI = "Informazioni";
	public final static String LABEL_MENU_UTENTE_PROFILO_UTENTE = "Profilo Utente";
	public final static String LABEL_MENU_UTENTE_LOGOUT = "Logout";
	
	public final static String LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": {0}";
	public final static String LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM = "Soggetto: {0}";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_LOGIN_LOGIN = "login";
	public final static String PARAMETRO_LOGIN_PASSWORD = "password";
	
	
	
	/* OTHER */
	
	public final static String LOGIN_JSP = "/jsplib/login.jsp";
	public final static String INFO_JSP ="/jsplib/info.jsp";
	
	/* EVENTO PRESSIONE TASTO LOGIN */
	public final static String LOGIN_JS_FUNCTION = "CheckDati();return false;";

	public static final String MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE = "L'utente non &egrave; abilitato ad utilizzare la console"; 
	public static final String MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO = "L'utente non &egrave; abilitato ad utilizzare la console: configurazione incompleta"; 
}
