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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.Vector;

import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;

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
	
	public final static String OBJECT_NAME_UTENTI_SERVIZI = "utentiServizi";
	
	public final static String OBJECT_NAME_UTENTI_SOGGETTI = "utentiSoggetti";
	
	public final static String OBJECT_NAME_UTENTE_PASSWORD = "utentePassword";
	
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
	public final static String SERVLET_NAME_UTENTE_PASSWORD_CHANGE = OBJECT_NAME_UTENTE_PASSWORD+"Change.do";
	public final static Vector<String> SERVLET_UTENTE = new Vector<String>();
	static{
		SERVLET_UTENTE.add(SERVLET_NAME_UTENTE_CHANGE);
		SERVLET_UTENTE.add(SERVLET_NAME_UTENTE_PASSWORD_CHANGE);
	}
	
	public final static String SERVLET_NAME_UTENTI_SERVIZI_ADD = OBJECT_NAME_UTENTI_SERVIZI+"Add.do";
	public final static String SERVLET_NAME_UTENTI_SERVIZI_DELETE = OBJECT_NAME_UTENTI_SERVIZI+"Del.do";
	public final static String SERVLET_NAME_UTENTI_SERVIZI_LIST = OBJECT_NAME_UTENTI_SERVIZI+"List.do";
	public final static Vector<String> SERVLET_UTENTI_SERVIZI = new Vector<String>();
	static{
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SERVIZI_ADD);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SERVIZI_DELETE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SERVIZI_LIST);
	}
	
	public final static String SERVLET_NAME_UTENTI_SOGGETTI_ADD = OBJECT_NAME_UTENTI_SOGGETTI+"Add.do";
	public final static String SERVLET_NAME_UTENTI_SOGGETTI_DELETE = OBJECT_NAME_UTENTI_SOGGETTI+"Del.do";
	public final static String SERVLET_NAME_UTENTI_SOGGETTI_LIST = OBJECT_NAME_UTENTI_SOGGETTI+"List.do";
	public final static Vector<String> SERVLET_UTENTI_SOGGETTI = new Vector<String>();
	static{
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SOGGETTI_ADD);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SOGGETTI_DELETE);
		SERVLET_UTENTI.add(SERVLET_NAME_UTENTI_SOGGETTI_LIST);
	}
	

	/* STRUTS FORWARD */
	
	public final static String STRUTS_FORWARD_INFO = "Info";
	public final static String STRUTS_FORWARD_PERMESSI_OK = "PermessiOk";
	
	
	/* */
	public final static String OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE = "WELCOME_SCREEN"; 
	public final static String OGGETTO_STATO_UTENTE_HOME_PAGE = "HOME_PAGE";
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_UTENTI = "Utenti";
	public final static String LABEL_INFO_UTENTE = "Utente";
	public final static String LABEL_UTENTE = "Profilo Utente";
	public final static String LABEL_PROFILO_UTENTE = "Profilo Utente";
	public final static String LABEL_PROFILO = "Profilo";
	public final static String LABEL_INFORMAZIONI_UTENTE = "Informazioni Utente";
	public final static String LABEL_PASSWORD = "Password";
	public final static String LABEL_MODALITA_INTERFACCIA = "Modalit&agrave; Interfaccia";
	public final static String LABEL_MODALITA_GATEWAY = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE;
	public final static String LABEL_MODALITA_GATEWAY_COMPACT = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public final static String LABEL_PERMESSI_GESTIONE = "Permessi di Gestione";
	public final static String LABEL_CAMBIA_IDENTITA = "Cambia identità";
	public final static String LABEL_ACCEDI = "Accedi";
	public final static String LABEL_MODIFICA = "Modifica";
	public final static String LABEL_ATTENZIONE = "Attenzione";
	public final static String LABEL_VISIBILITA_DATI_GOVWAY_MONITOR = "Visibilità dati tramite govwayMonitor";
	public final static String LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_CREATE_NOTE = "Attenzione: procedere con la registrazione dei soggetti visibili tramite govwayMonitor per l'utente appena creato";
	public final static String LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_UPDATE_NOTE = "Per completare la configurazione dell'utente definire i soggetti visibili tramite govwayMonitor";
	public final static String LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_CREATE_NOTE = "Attenzione: procedere con la registrazione delle API visibili tramite govwayMonitor per l'utente appena creato";
	public final static String LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_UPDATE_NOTE = "Per completare la configurazione dell'utente definire le API visibili tramite govwayMonitor";
	public final static String LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_TUTTI = "Tutti";
	public final static String LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_TUTTI = "Tutte";
	public final static String LABEL_CONFIGURAZIONE_PDD_MONITOR_MONITORAGGIO_REPORTISTICA = "L'utente è abilitato al Monitoraggio e alla Reportistica";
	public final static String LABEL_CONFIGURAZIONE_PDD_MONITOR_MONITORAGGIO = "L'utente è abilitato al Monitoraggio";
	public final static String LABEL_CONFIGURAZIONE_PDD_MONITOR_REPORTISTICA = "L'utente è abilitato alla Reportistica";
	public final static String LABEL_SUFFIX_RESTRIZIONE_SOGGETTI = " dei seguenti soggetti:";
	public final static String LABEL_SUFFIX_RESTRIZIONE_API = " delle seguenti API:";
	public final static String LABEL_UTENTI_SERVIZI = "API";
	public final static String LABEL_UTENTI_SOGGETTI = "Soggetti";
	public final static String LABEL_UTENTI_SOGGETTI_DISPONIBILI_ESAURITI = "Non esistono altri soggetti associabili all'utente";
	public final static String LABEL_UTENTI_SOGGETTI_DISPONIBILI_ESAURITI_PER_LA_MODALITA_XX = "Non esistono altri soggetti del "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE.toLowerCase()+" {0} associabili all''utente";
	public final static String LABEL_UTENTI_SERVIZI_DISPONIBILI_ESAURITI = "Non esistono altri servizi associabili all'utente";
	public final static String LABEL_UTENTI_SERVIZI_DISPONIBILI_ESAURITI_PER_LA_MODALITA_XX = "Non esistono altri servizi del "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE.toLowerCase()+" {0} associabili all''utente";
	public final static String LABEL_PROFILO_UTENTE_CONSOLE_GESTIONE = "govwayConsole";
	public final static String LABEL_PROFILO_UTENTE_CONSOLE_MONITOR = "govwayMonitor";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_UTENTI_FIRST = "first";
	public final static String PARAMETRO_UTENTI_USERNAME = "nomesu";
	public final static String PARAMETRO_UTENTI_PW = "pwsu";
	public final static String PARAMETRO_UTENTI_CONFERMA_PW = "confpwsu";
	public final static String PARAMETRO_UTENTI_CHANGE_PW = "changepwd";
	public final static String PARAMETRO_UTENTI_TIPO_GUI = "tipo_gui";
	public final static String PARAMETRO_UTENTI_IS_SERVIZI = "isServizi";
	public final static String PARAMETRO_UTENTI_IS_DIAGNOSTICA = "isDiagnostica";
	public final static String PARAMETRO_UTENTI_IS_REPORTISTICA = "isReportistica";
	public final static String PARAMETRO_UTENTI_IS_SISTEMA = "isSistema";
	public final static String PARAMETRO_UTENTI_IS_MESSAGGI = "isMessaggi";
	public final static String PARAMETRO_UTENTI_IS_UTENTI = "isUtenti";
	public final static String PARAMETRO_UTENTI_IS_AUDITING = "isAuditing";
	public final static String PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE = "isAccordiCooperazione";
	public final static String PARAMETRO_UTENTI_SINGLE_SU_SERVIZI = "singleSuS";
	public final static String PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE = "singleSuAC";
	public final static String PARAMETRO_UTENTI_MODALITA_PREFIX = "mod_";
	public final static String PARAMETRO_UTENTI_PROTOCOLLO = "protocollo";
	public final static String PARAMETRO_UTENTI_ABILITAZIONI_SOGGETTI_ALL = "isSoggettiAll";
	public final static String PARAMETRO_UTENTI_ABILITAZIONI_SERVIZI_ALL = "isServiziAll";
	public final static String PARAMETRO_UTENTI_SCADENZA = "scadenza";
	
	public final static String PARAMETRO_UTENTE_LOGIN = "login";
	public final static String PARAMETRO_UTENTE_PW = "password";
	public final static String PARAMETRO_UTENTE_VECCHIA_PW = "oldpw";
	public final static String PARAMETRO_UTENTE_NUOVA_PW = "newpw";
	public final static String PARAMETRO_UTENTE_CONFERMA_NUOVA_PW = "confpw";
	public final static String PARAMETRO_UTENTE_CHANGE_PW = "changepwd";
	public final static String PARAMETRO_UTENTE_TIPO_GUI = "tipo_gui";
	public final static String PARAMETRO_UTENTE_CHANGE_GUI = "change_gui";
	public final static String PARAMETRO_UTENTE_ESEGUI = "changePw";
	public final static String PARAMETRO_UTENTE_PROTOCOLLO = "protocollo_";
	public final static String PARAMETRO_UTENTE_TIPO_MODALITA = "tipo_mod";
	public final static String PARAMETRO_UTENTE_TIPO_MODALITA_LIST = "tipo_mod_list";
	public final static String PARAMETRO_UTENTE_CHANGE_MODALITA = "change_mod";
	public final static String PARAMETRO_UTENTI_SOGGETTO = "soggetto";
	public final static String PARAMETRO_UTENTI_SERVIZIO = "servizio";
	public final static String PARAMETRO_UTENTE_CHANGE_SOGGETTO = "change_sog";
	public final static String PARAMETRO_UTENTE_ID_SOGGETTO = "id_sog";
	public final static String PARAMETRO_UTENTE_ID_SOGGETTO_LIST = "label_id_list";
	public final static String PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR = "tipo_mod_mon";
	public final static String PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR = "id_sog_mon";
	
	public final static String PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO = "hp_mon";
	public final static String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI = "transazioni";
	public final static String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE = "summary";
	
	public final static String [] VALUES_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO = { 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI,
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE
	};
	
	public final static String PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO = "int_temp_mon";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO = "--";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE = "Ultime 24 ore";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI = "Ultimi 7 giorni";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI = "Ultimi 30 giorni";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO = "Ultimo anno";
	
	public final static String[]  VALUES_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO = {
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI, 
			UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO
	};
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_UTENTI_USERNAME = "Nome";
	public final static String LABEL_PARAMETRO_UTENTI_PW = "Password";
	public final static String LABEL_PARAMETRO_UTENTI_CONFERMA_PW = "Conferma Password";
	public final static String LABEL_PARAMETRO_UTENTI_CHANGE_PW = "Nuova Password";
	public final static String LABEL_PARAMETRO_UTENTI_TIPO_GUI =  "Tipo";
	public final static String LABEL_PARAMETRO_UTENTI_REGISTRO_SUBSECTION = "Registro";
	public final static String LABEL_PARAMETRO_UTENTI_IS_SERVIZI = "Gestione API [S]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE = "Accordi Cooperazione [P]";
	public final static String LABEL_PARAMETRO_UTENTI_STRUMENTI_SUBSECTION = "Strumenti";
	public final static String LABEL_PARAMETRO_UTENTI_IS_MESSAGGI = "Coda Messaggi [M]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_AUDITING = "Auditing [A]";
	public final static String LABEL_PARAMETRO_UTENTI_CONFIGURAZIONE_SUBSECTION = "Configurazione";
	public final static String LABEL_PARAMETRO_UTENTI_IS_SISTEMA = "Configurazione Generale [C]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_UTENTI = "Utenti [U]";
	public final static String LABEL_PARAMETRO_UTENTI_GOVWAY_MONITOR_SUBSECTION = "GovWay Monitor";
	public final static String LABEL_PARAMETRO_UTENTI_IS_DIAGNOSTICA = "Monitoraggio [D]";
	public final static String LABEL_PARAMETRO_UTENTI_IS_REPORTISTICA = "Reportistica [R]";
	public final static String LABEL_PARAMETRO_UTENTI_SCADENZA = "Scadenza";
	public final static String LABEL_NOTA_UTENTI_SCADENZA_ADD = "La password scade dopo {0} giorni";
	public final static String LABEL_NOTA_UTENTI_SCADENZA_CHANGE = "La password scade fra {0} giorni";
	public final static String LABEL_UTENTI_SCADENZA_PW_SCADUTA = "Password scaduta";
	public final static String LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO = "Home Page";
	public final static String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI = "Ricerca Transazioni";
	public final static String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE = "Report Statistico";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO = "Intervallo Temporale";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO = "No Grafico";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE = "Ultime 24 ore";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI = "Ultimi 7 giorni";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI = "Ultimi 30 giorni";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO = "Ultimo anno";

	public final static String LABEL_PARAMETRO_UTENTE_VECCHIA_PW = "Vecchia";
	public final static String LABEL_PARAMETRO_UTENTE_CONFERMA_NUOVA_PW = "Conferma Nuova";
	public final static String LABEL_PARAMETRO_UTENTE_NUOVA_PW = "Nuova";
	
	public final static String LABEL_PARAMETRO_UTENTI_SOGGETTO = "Soggetto";
	public final static String LABEL_PARAMETRO_UTENTI_SOGGETTO_EROGATORE = "Soggetto Erogatore";
	public final static String LABEL_PARAMETRO_UTENTI_SERVIZIO = "API";
	public final static String LABEL_PARAMETRO_UTENTI_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	
	public final static String LABEL_UTENTE_PERMESSI = "Utente";
	private final static String LABEL_UTENTE_PERMESSI_PREFIX_NOTE = "Utente a cui verranno assegnati gli oggetti creati con il permesso '";
	public final static String LABEL_UTENTE_PERMESSI_SERVIZI_NOTE = LABEL_UTENTE_PERMESSI_PREFIX_NOTE + LABEL_PARAMETRO_UTENTI_IS_SERVIZI+"'";
	public final static String LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE_NOTE = LABEL_UTENTE_PERMESSI_PREFIX_NOTE + LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE+"'";
	
	public final static String LABEL_PARAMETRO_MODALITA_ALL = "Tutti";//CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String LABEL_PARAMETRO_SOGGETTI_OPERATIVI = "Soggetti Operativi";
	public final static String LABEL_PARAMETRO_SOGGETTO_OPERATIVO = "Soggetto Operativo";
	public final static String LABEL_PARAMETRO_SOGGETTO_COMPACT = "Soggetto Operativo";
	public final static String LABEL_PARAMETRO_SOGGETTI_COMPACT = "Soggetti Operativi";
	
	/* VALORI PARAMETRI */
	public final static String VALORE_PARAMETRO_MODALITA_ALL = "all";
	public final static String VALORE_PARAMETRO_PW_MODALITA_NO_LOGIN_APPLICATION = "changeit";
	
	/* MESSAGGI ERRORE */
	public final static String MESSAGGIO_ERRORE_NOME_SOGGETTO_OBBLIGATORIO = "Selezionare un soggetto";
	public final static String MESSAGGIO_ERRORE_NOME_SERVIZIO_OBBLIGATORIO = "Selezionare un servizio";
	public final static String MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA = "La password scelta non deve corrispondere ad una precedente password";
	public final static String MESSAGGIO_ERRORE_PASSWORD_NUOVE_DIFFERENTI = "Le password inserite nei campi Nuova e Conferma Nuova non corrispondono";
	
}
