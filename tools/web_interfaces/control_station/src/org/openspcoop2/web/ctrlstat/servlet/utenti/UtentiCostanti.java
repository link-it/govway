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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * UtentiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiCostanti {
	
	private UtentiCostanti() {}

	/* OBJECT NAME */
	
	public static final String OBJECT_NAME_UTENTI = "utenti";
	
	public static final String OBJECT_NAME_UTENTE = "utente";
	
	public static final String OBJECT_NAME_UTENTI_SERVIZI = "utentiServizi";
	
	public static final String OBJECT_NAME_UTENTI_SOGGETTI = "utentiSoggetti";
	
	public static final String OBJECT_NAME_UTENTE_PASSWORD = "utentePassword";
	
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_UTENTI_ADD = OBJECT_NAME_UTENTI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_UTENTI_CHANGE = OBJECT_NAME_UTENTI+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_UTENTI_DELETE = OBJECT_NAME_UTENTI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_UTENTI_LIST = OBJECT_NAME_UTENTI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_UTENTI = new ArrayList<>();
	public static List<String> getServletUtenti() {
		return SERVLET_UTENTI;
	}
	static{
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_ADD);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_CHANGE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_DELETE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_LIST);
	}
	
	public static final String SERVLET_NAME_UTENTE_CHANGE = OBJECT_NAME_UTENTE+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_UTENTE_PASSWORD_CHANGE = OBJECT_NAME_UTENTE_PASSWORD+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	private static final List<String> SERVLET_UTENTE = new ArrayList<>();
	public static List<String> getServletUtente() {
		return SERVLET_UTENTE;
	}
	static{
		SERVLET_UTENTE.add(SERVLET_NAME_UTENTE_CHANGE);
		SERVLET_UTENTE.add(SERVLET_NAME_UTENTE_PASSWORD_CHANGE);
	}
	
	public static final String SERVLET_NAME_UTENTI_SERVIZI_ADD = OBJECT_NAME_UTENTI_SERVIZI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_UTENTI_SERVIZI_DELETE = OBJECT_NAME_UTENTI_SERVIZI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_UTENTI_SERVIZI_LIST = OBJECT_NAME_UTENTI_SERVIZI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_UTENTI_SERVIZI = new ArrayList<>();
	public static List<String> getServletUtentiServizi() {
		return SERVLET_UTENTI_SERVIZI;
	}
	static{
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SERVIZI_ADD);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SERVIZI_DELETE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SERVIZI_LIST);
	}
	
	public static final String SERVLET_NAME_UTENTI_SOGGETTI_ADD = OBJECT_NAME_UTENTI_SOGGETTI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_UTENTI_SOGGETTI_DELETE = OBJECT_NAME_UTENTI_SOGGETTI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_UTENTI_SOGGETTI_LIST = OBJECT_NAME_UTENTI_SOGGETTI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_UTENTI_SOGGETTI = new ArrayList<>();
	public static List<String> getServletUtentiSoggetti() {
		return SERVLET_UTENTI_SOGGETTI;
	}
	static{
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SOGGETTI_ADD);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SOGGETTI_DELETE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SOGGETTI_LIST);
	}
	

	/* STRUTS FORWARD */
	
	public static final String STRUTS_FORWARD_INFO = "Info";
	public static final String STRUTS_FORWARD_PERMESSI_OK = "PermessiOk";
	
	
	/* */
	public static final String OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE = "WELCOME_SCREEN"; 
	public static final String OGGETTO_STATO_UTENTE_HOME_PAGE = "HOME_PAGE";
	
	
	/* LABEL GENERALI */
	
	public static final String LABEL_UTENTI = "Utenti";
	public static final String LABEL_INFO_UTENTE = "Utente";
	public static final String LABEL_UTENTE = "Profilo Utente";
	public static final String LABEL_PROFILO_UTENTE = "Profilo Utente";
	public static final String LABEL_PROFILO = "Profilo";
	public static final String LABEL_INFORMAZIONI_UTENTE = "Informazioni Utente";
	public static final String LABEL_PASSWORD = "Password";
	public static final String LABEL_MODALITA_INTERFACCIA = "Modalit&agrave; Interfaccia";
	public static final String LABEL_MODALITA_GATEWAY = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE;
	public static final String LABEL_MODALITA_GATEWAY_COMPACT = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public static final String LABEL_PERMESSI_GESTIONE = "Permessi di Gestione";
	public static final String LABEL_CAMBIA_IDENTITA = "Cambia identità";
	public static final String LABEL_ACCEDI = "Accedi";
	public static final String LABEL_MODIFICA = "Modifica";
	public static final String LABEL_ATTENZIONE = "Attenzione";
	public static final String LABEL_VISIBILITA_DATI_GOVWAY_MONITOR = "Visibilità dati tramite govwayMonitor";
	public static final String LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_CREATE_NOTE = "Attenzione: procedere con la registrazione dei soggetti visibili tramite govwayMonitor per l'utente appena creato";
	public static final String LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_UPDATE_NOTE = "Per completare la configurazione dell'utente definire i soggetti visibili tramite govwayMonitor";
	public static final String LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_CREATE_NOTE = "Attenzione: procedere con la registrazione delle API visibili tramite govwayMonitor per l'utente appena creato";
	public static final String LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_UPDATE_NOTE = "Per completare la configurazione dell'utente definire le API visibili tramite govwayMonitor";
	public static final String LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_TUTTI = "Tutti";
	public static final String LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_TUTTI = "Tutte";
	public static final String LABEL_CONFIGURAZIONE_PDD_MONITOR_MONITORAGGIO_REPORTISTICA = "L'utente è abilitato al Monitoraggio e alla Reportistica";
	public static final String LABEL_CONFIGURAZIONE_PDD_MONITOR_MONITORAGGIO = "L'utente è abilitato al Monitoraggio";
	public static final String LABEL_CONFIGURAZIONE_PDD_MONITOR_REPORTISTICA = "L'utente è abilitato alla Reportistica";
	public static final String LABEL_SUFFIX_RESTRIZIONE_SOGGETTI = " dei seguenti soggetti:";
	public static final String LABEL_SUFFIX_RESTRIZIONE_API = " delle seguenti API:";
	public static final String LABEL_UTENTI_SERVIZI = "API";
	public static final String LABEL_UTENTI_SOGGETTI = "Soggetti";
	public static final String LABEL_UTENTI_SOGGETTI_DISPONIBILI_ESAURITI = "Non esistono altri soggetti associabili all'utente";
	public static final String LABEL_UTENTI_SOGGETTI_DISPONIBILI_ESAURITI_PER_LA_MODALITA_XX = "Non esistono altri soggetti del "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE.toLowerCase()+" {0} associabili all''utente";
	public static final String LABEL_UTENTI_SERVIZI_DISPONIBILI_ESAURITI = "Non esistono altri servizi associabili all'utente";
	public static final String LABEL_UTENTI_SERVIZI_DISPONIBILI_ESAURITI_PER_LA_MODALITA_XX = "Non esistono altri servizi del "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE.toLowerCase()+" {0} associabili all''utente";
	public static final String LABEL_PROFILO_UTENTE_CONSOLE_GESTIONE = "govwayConsole";
	public static final String LABEL_PROFILO_UTENTE_CONSOLE_MONITOR = "govwayMonitor";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_UTENTI_FIRST = "first";
	public static final String PARAMETRO_UTENTI_USERNAME = "nomesu";
	public static final String PARAMETRO_UTENTI_PW = "pwsu";
	public static final String PARAMETRO_UTENTI_CONFERMA_PW = "confpwsu";
	public static final String PARAMETRO_UTENTI_CHANGE_PW = "changepwd";
	public static final String PARAMETRO_UTENTI_TIPO_GUI = "tipo_gui";
	public static final String PARAMETRO_UTENTI_IS_SERVIZI = "isServizi";
	public static final String PARAMETRO_UTENTI_IS_DIAGNOSTICA = "isDiagnostica";
	public static final String PARAMETRO_UTENTI_IS_REPORTISTICA = "isReportistica";
	public static final String PARAMETRO_UTENTI_IS_SISTEMA = "isSistema";
	public static final String PARAMETRO_UTENTI_IS_MESSAGGI = "isMessaggi";
	public static final String PARAMETRO_UTENTI_IS_UTENTI = "isUtenti";
	public static final String PARAMETRO_UTENTI_IS_AUDITING = "isAuditing";
	public static final String PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE = "isAccordiCooperazione";
	public static final String PARAMETRO_UTENTI_SINGLE_SU_SERVIZI = "singleSuS";
	public static final String PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE = "singleSuAC";
	public static final String PARAMETRO_UTENTI_MODALITA_PREFIX = "mod_";
	public static final String PARAMETRO_UTENTI_PROTOCOLLO = "protocollo";
	public static final String PARAMETRO_UTENTI_ABILITAZIONI_SOGGETTI_ALL = "isSoggettiAll";
	public static final String PARAMETRO_UTENTI_ABILITAZIONI_SERVIZI_ALL = "isServiziAll";
	public static final String PARAMETRO_UTENTI_SCADENZA = "scadenza";
	
	public static final String PARAMETRO_UTENTE_LOGIN = "login";
	public static final String PARAMETRO_UTENTE_PW = "password";
	public static final String PARAMETRO_UTENTE_VECCHIA_PW = "oldpw";
	public static final String PARAMETRO_UTENTE_NUOVA_PW = "newpw";
	public static final String PARAMETRO_UTENTE_CONFERMA_NUOVA_PW = "confpw";
	public static final String PARAMETRO_UTENTE_CHANGE_PW = "changepwd";
	public static final String PARAMETRO_UTENTE_TIPO_GUI = "tipo_gui";
	public static final String PARAMETRO_UTENTE_CHANGE_GUI = "change_gui";
	public static final String PARAMETRO_UTENTE_ESEGUI = "changePw";
	public static final String PARAMETRO_UTENTE_PROTOCOLLO = "protocollo_";
	public static final String PARAMETRO_UTENTE_TIPO_MODALITA = "tipo_mod";
	public static final String PARAMETRO_UTENTE_TIPO_MODALITA_LIST = "tipo_mod_list";
	public static final String PARAMETRO_UTENTE_CHANGE_MODALITA = "change_mod";
	public static final String PARAMETRO_UTENTI_SOGGETTO = "soggetto";
	public static final String PARAMETRO_UTENTI_SERVIZIO = "servizio";
	public static final String PARAMETRO_UTENTE_CHANGE_SOGGETTO = "change_sog";
	public static final String PARAMETRO_UTENTE_ID_SOGGETTO = "id_sog";
	public static final String PARAMETRO_UTENTE_ID_SOGGETTO_LIST = "label_id_list";
	public static final String PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR = "tipo_mod_mon";
	public static final String PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR = "id_sog_mon";
	
	public static final String PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO = "hp_mon";
	public static final String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI = "transazioni";
	public static final String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE = "summary";
	
	private static final String [] VALUES_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO = { 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI,
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE
	};
	public static String[] getValuesParametroUtentiHomePageMonitoraggio() {
		return VALUES_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO;
	}
	public static final String PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO = "int_temp_mon";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO = "--";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE = "Ultime 24 ore";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI = "Ultimi 7 giorni";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI = "Ultimi 30 giorni";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO = "Ultimo anno";
	
	private static final String[]  VALUES_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO = {
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO
	};
	public static String[] getValuesParametroUtentiIntervalloTemporaleHomePageMonitoraggio() {
		return VALUES_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO;
	}
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_UTENTI_USERNAME = "Nome";
	public static final String LABEL_PARAMETRO_UTENTI_PW = "Password";
	public static final String LABEL_PARAMETRO_UTENTI_CONFERMA_PW = "Conferma Password";
	public static final String LABEL_PARAMETRO_UTENTI_CHANGE_PW = "Nuova Password";
	public static final String LABEL_PARAMETRO_UTENTI_TIPO_GUI =  "Tipo";
	public static final String LABEL_PARAMETRO_UTENTI_REGISTRO_SUBSECTION = "Registro";
	public static final String LABEL_PARAMETRO_UTENTI_IS_SERVIZI = "Gestione API [S]";
	public static final String LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE = "Accordi Cooperazione [P]";
	public static final String LABEL_PARAMETRO_UTENTI_STRUMENTI_SUBSECTION = "Strumenti";
	public static final String LABEL_PARAMETRO_UTENTI_IS_MESSAGGI = "Coda Messaggi [M]";
	public static final String LABEL_PARAMETRO_UTENTI_IS_AUDITING = "Auditing [A]";
	public static final String LABEL_PARAMETRO_UTENTI_CONFIGURAZIONE_SUBSECTION = "Configurazione";
	public static final String LABEL_PARAMETRO_UTENTI_IS_SISTEMA = "Configurazione Generale [C]";
	public static final String LABEL_PARAMETRO_UTENTI_IS_UTENTI = "Utenti [U]";
	public static final String LABEL_PARAMETRO_UTENTI_GOVWAY_MONITOR_SUBSECTION = "GovWay Monitor";
	public static final String LABEL_PARAMETRO_UTENTI_IS_DIAGNOSTICA = "Monitoraggio [D]";
	public static final String LABEL_PARAMETRO_UTENTI_IS_REPORTISTICA = "Reportistica [R]";
	public static final String LABEL_PARAMETRO_UTENTI_SCADENZA = "Scadenza";
	public static final String LABEL_NOTA_UTENTI_SCADENZA_ADD = "La password scade dopo {0} giorni";
	public static final String LABEL_NOTA_UTENTI_SCADENZA_CHANGE = "La password scade fra {0} giorni";
	public static final String LABEL_UTENTI_SCADENZA_PW_SCADUTA = "Password scaduta";
	public static final String LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO = "Home Page";
	public static final String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI = "Ricerca Transazioni";
	public static final String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE = "Report Statistico";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO = "Intervallo Temporale";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO = "No Grafico";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE = "Ultime 24 ore";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI = "Ultimi 7 giorni";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI = "Ultimi 30 giorni";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO = "Ultimo anno";

	public static final String LABEL_PARAMETRO_UTENTE_VECCHIA_PW = "Vecchia";
	public static final String LABEL_PARAMETRO_UTENTE_CONFERMA_NUOVA_PW = "Conferma Nuova";
	public static final String LABEL_PARAMETRO_UTENTE_NUOVA_PW = "Nuova";
	
	public static final String LABEL_PARAMETRO_UTENTI_SOGGETTO = "Soggetto";
	public static final String LABEL_PARAMETRO_UTENTI_SOGGETTO_EROGATORE = "Soggetto Erogatore";
	public static final String LABEL_PARAMETRO_UTENTI_SERVIZIO = "API";
	public static final String LABEL_PARAMETRO_UTENTI_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	
	public static final String LABEL_UTENTE_PERMESSI = "Utente";
	private static final String LABEL_UTENTE_PERMESSI_PREFIX_NOTE = "Utente a cui verranno assegnati gli oggetti creati con il permesso '";
	public static final String LABEL_UTENTE_PERMESSI_SERVIZI_NOTE = LABEL_UTENTE_PERMESSI_PREFIX_NOTE + LABEL_PARAMETRO_UTENTI_IS_SERVIZI+"'";
	public static final String LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE_NOTE = LABEL_UTENTE_PERMESSI_PREFIX_NOTE + LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE+"'";
	
	public static final String LABEL_PARAMETRO_MODALITA_ALL = "Tutti";
	
	public static final String LABEL_PARAMETRO_SOGGETTI_OPERATIVI = "Soggetti Operativi";
	public static final String LABEL_PARAMETRO_SOGGETTO_OPERATIVO = "Soggetto Operativo";
	public static final String LABEL_PARAMETRO_SOGGETTO_COMPACT = "Soggetto Operativo";
	public static final String LABEL_PARAMETRO_SOGGETTI_COMPACT = "Soggetti Operativi";
	
	/* VALORI PARAMETRI */
	public static final String VALORE_PARAMETRO_MODALITA_ALL = "all";
	public static final String VALORE_PARAMETRO_PW_MODALITA_NO_LOGIN_APPLICATION = "changeit";
	
	/* MESSAGGI ERRORE */
	public static final String MESSAGGIO_ERRORE_NOME_SOGGETTO_OBBLIGATORIO = "Selezionare un soggetto";
	public static final String MESSAGGIO_ERRORE_NOME_SERVIZIO_OBBLIGATORIO = "Selezionare un servizio";
	public static final String MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA = "La password scelta non deve corrispondere ad una precedente password";
	public static final String MESSAGGIO_ERRORE_PASSWORD_NUOVE_DIFFERENTI = "Le password inserite nei campi Nuova e Conferma Nuova non corrispondono";
	
}
