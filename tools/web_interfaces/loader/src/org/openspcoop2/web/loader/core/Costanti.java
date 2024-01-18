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


package org.openspcoop2.web.loader.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * Costanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Costanti {

	public static final String OPENSPCOOP2_LOADER_LOCAL_PATH = "loader_local.properties";
    public static final String OPENSPCOOP2_LOADER_PROPERTIES = "OPENSPCOOP2_LOADER_PROPERTIES";
    
    public static final String OPENSPCOOP2_DATASOURCE_LOCAL_PATH = "loader_local.datasource.properties";
    public static final String OPENSPCOOP2_DATASOURCE_PROPERTIES = "OPENSPCOOP2_LOADER_DATASOURCE_PROPERTIES";
	
    public static final String OPENSPCOOP2_LOGGER_LOCAL_PATH = "loader_local.log4j2.properties";
    public static final String OPENSPCOOP2_LOGGER_PROPERTIES = "OPENSPCOOP2_LOADER_LOG_PROPERTIES";
	
	/* OTHER */
    
	public static final String IMAGES_DIR = "images";
	public static final String CSS_DIR = "css";
	public static final String JS_DIR = "js";
	public static final String FONTS_DIR = "fonts";
	public static final String LOGIN_JSP = "/jsplib/login.jsp";
	public static final String INFO_JSP ="/jsplib/info.jsp";
	
	public static final String SCHEMA_REGISTRO_SERVIZI = "registroServizi.xsd";
	public static final String SCHEMA_CONFIGURAZIONE_PDD = "config.xsd";
    
    
    /* OBJECT NAME */
	
	public static final String OBJECT_NAME_LOGIN = "login";
	
	public static final String OBJECT_NAME_LOGOUT = "logout";
	
	public static final String OBJECT_NAME_MESSAGE_PAGE = "messagePage";
	
	public static final String OBJECT_NAME_ARCHIVI_IMPORTA_XML = "importaXml";
	public static final ForwardParams TIPO_OPERAZIONE_ARCHIVI_IMPORTA_XML = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_ARCHIVI_ELIMINAZIONE_XML = "eliminazioneXml";
	public static final ForwardParams TIPO_OPERAZIONE_ARCHIVI_ELIMINAZIONE_XML = ForwardParams.OTHER("");
	
	
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_LOGIN = OBJECT_NAME_LOGIN+".do";
	public static final List<String> SERVLET_LOGIN = new ArrayList<>();
	static{
		SERVLET_LOGIN.add(SERVLET_NAME_LOGIN);
	}
	
	public static final String SERVLET_NAME_LOGOUT = OBJECT_NAME_LOGOUT+".do";
	public static final List<String> SERVLET_LOGOUT = new ArrayList<>();
	static{
		SERVLET_LOGOUT.add(SERVLET_NAME_LOGOUT);
	}
	
	public static final String SERVLET_NAME_ARCHIVI_IMPORTA_XML = OBJECT_NAME_ARCHIVI_IMPORTA_XML+".do";
	public static final String SERVLET_NAME_ARCHIVI_ELIMINAZIONE_XML = OBJECT_NAME_ARCHIVI_ELIMINAZIONE_XML+".do";
	public static final List<String> SERVLET_ARCHIVI = new ArrayList<>();
	static{
		SERVLET_ARCHIVI.add(SERVLET_NAME_ARCHIVI_IMPORTA_XML);
	}
	
	public static final String SERVLET_NAME_MESSAGE_PAGE = OBJECT_NAME_MESSAGE_PAGE+".do";
	public static final List<String> SERVLET_MESSAGE_PAGE = new ArrayList<>();
	static{
		SERVLET_MESSAGE_PAGE.add(SERVLET_NAME_MESSAGE_PAGE);
	}
	
	/* LABEL GENERALI */
	
	public static final String LABEL_LINKIT_WEB = "https://link.it";
	public static final String LABEL_OPENSPCOOP2_WEB = "https://govway.org";
	
	public static final String LABEL_CONFIGURAZIONI_XML = "Strumenti";
	public static final String LABEL_TIPOLOGIA_XML = "Tipologia";
	public static final String LABEL_IMPORTA = "Importa";
	public static final String LABEL_ELIMINA = "Elimina";
	public static final String LABEL_FILE_NON_VALIDO = "File non valido: ";
	public static final String LABEL_IMPORTAZIONE_EFFETTUATA_CORRETTAMENTE ="L'importazione dei dati, contenuti nel file xml, è avvenuta con successo";
	public static final String LABEL_ELIMINAZIONE_EFFETTUATA_CORRETTAMENTE ="L'eliminazione delle configurazioni, contenute nel file xml, è avvenuta con successo";
	
	public static final String LABEL_MENU_UTENTE_INFORMAZIONI = "Informazioni";
	public static final String LABEL_LOGIN = "Login";
	public static final String LABEL_LOGOUT = "Logout";
	public static final String LABEL_PASSWORD = "Password";
	public static final String LABEL_LOGIN_ATTUALMENTE_CONNESSO = "Utente: ";
	public static final String LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO = "Login effettuato con successo";
	public static final String LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO = "Logout effettuato con successo";
	public static final String LABEL_LOGIN_SESSIONE_SCADUTA = "La sessione &egrave; scaduta. Effettuare il login";
	public static final String LABEL_LOGIN_AUTORIZZAZIONE_NEGATA = "Autorizzazione negata";
	public static final String LABEL_LOGIN_ERRORE = "L'ultima operazione effettuata ha provocato un errore che ha reso l'interfaccia non utilizzabile.<BR><BR>Effettuare nuovamente il login";
	public static final String LABEL_LOGIN_DATI_INCOMPLETI_LOGIN = "Dati incompleti. E' necessario indicare un utente";
	public static final String LABEL_LOGIN_DATI_INCOMPLETI_PASSWORD = "Dati incompleti. E' necessario indicare una Password";
	public static final String LABEL_LOGIN_ERRATO = "L'utente fornito non esiste";
	public static final String LABEL_LOGIN_CON_PW_ERRATO = "Le credenziali fornite non sono corrette";
	public static final String LABEL_LOGIN_PERMESSI_NON_SUFFICENTI = "L'utente non possiede i diritti per creare configurazioni";
	public static final String LABEL_CONSOLE_RIPRISTINATA = "Console ripristinata con successo.";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_UTENTE_LOGIN = "login";
	public static final String PARAMETRO_UTENTE_PASSWORD = "password";
	
	public static final String PARAMETRO_ARCHIVI_TIPO_XML = "tipoxml";
	public static final String PARAMETRO_ARCHIVI_FILE = "theFile";
	

	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_ARCHIVI_FILE = "File";
	
	/** PARAMETRI MESSAGE PAGE **/
	
	public static final String PARAMETER_MESSAGE_TEXT = org.openspcoop2.web.lib.mvc.Costanti.PARAMETER_MESSAGE_TEXT;
	public static final String PARAMETER_MESSAGE_TITLE = org.openspcoop2.web.lib.mvc.Costanti.PARAMETER_MESSAGE_TITLE;
	public static final String PARAMETER_MESSAGE_TYPE = org.openspcoop2.web.lib.mvc.Costanti.PARAMETER_MESSAGE_TYPE;
	public static final String PARAMETER_MESSAGE_BREADCRUMB = org.openspcoop2.web.lib.mvc.Costanti.PARAMETER_MESSAGE_BREADCRUMB;
	
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE = org.openspcoop2.web.lib.mvc.Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE;
	
	/* VALORI */
	
	public static final String TIPOLOGIA_XML_REGISTRO_SERVIZI = "registroServizi";
	public static final String TIPOLOGIA_XML_CONFIGURAZIONE_PDD = "configurazionePdD";
	
	public static final String PDD_TIPOLOGIA_ESTERNA = PddTipologia.ESTERNO.toString();
	public static final String PDD_TIPOLOGIA_OPERATIVA = PddTipologia.OPERATIVO.toString();
}
