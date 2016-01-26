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
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_LOGIN = "Login";
	public final static String LABEL_LOGOUT = "Logout";
	public final static String LABEL_LOGIN_ATTUALMENTE_CONNESSO = "Utente: ";
	public final static String LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO = "Login effettuato con successo";
	public final static String LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO = "Logout effettuato con successo";
	public final static String LABEL_LOGIN_SESSIONE_SCADUTA = "La sessione &egrave; scaduta.<BR>Effettuare il login";
	public final static String LABEL_LOGIN_AUTORIZZAZIONE_NEGATA = "Autorizzazione negata";
	public final static String LABEL_LOGIN_ERRORE = "L'ultima operazione effettuata ha provocato un errore che ha reso l'interfaccia non utilizzabile.<BR><BR>Effettuare nuovamente il login";
	
	
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_LOGIN_LOGIN = "login";
	public final static String PARAMETRO_LOGIN_PASSWORD = "password";
	
	
	
	/* OTHER */
	
	public final static String LOGIN_JSP = "/jsplib/login.jsp";
	public final static String INFO_JSP ="/jsplib/info.jsp";
}
