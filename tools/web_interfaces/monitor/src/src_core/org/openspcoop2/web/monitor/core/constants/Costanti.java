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
package org.openspcoop2.web.monitor.core.constants;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Costanti
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Costanti {
	
	private Costanti() {}

	public static final String LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": {0}";
	public static final String LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM = "Soggetto: {0}";
	
	public static final String LABEL_PARAMETRO_SOGGETTI_OPERATIVI = "Soggetti Operativi";
	public static final String LABEL_PARAMETRO_SOGGETTO_OPERATIVO = "Soggetto Operativo";
	public static final String LABEL_PARAMETRO_SOGGETTO_COMPACT = "Soggetto Operativo";
	public static final String LABEL_PARAMETRO_SOGGETTI_COMPACT = "Soggetti Operativi";
	public static final String LABEL_PROFILO = "Profilo";
	
	public static final String LABEL_PARAMETRO_MODALITA_ALL = "Tutti";
	public static final String VALUE_PARAMETRO_MODALITA_ALL = "qualsiasi";
	
	public static final String ICONS_BASE = "/images/tema_link/" ;
	
	public static final String ICONA_MENU_UTENTE_CHECKED = ICONS_BASE +"checkbox_checked_white.png";
	public static final String ICONA_MENU_UTENTE_UNCHECKED = ICONS_BASE + "checkbox_unchecked_white.png";
	
	public static final String DATASOURCE = "dataSource";
	
	public static final String TIPODATABASE ="tipoDatabase";
	
	public static final String SHOWSQL ="showSql";
	
	public static final String NON_SELEZIONATO = "--";
	
	public static final String SEPARATORE_TIPO_NOME = "/";
	public static final String SEPARATORE_VERSIONE = ":";
	public static final String SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA = " (";
	public static final String SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA = ")";
	
	public static final String LABEL_PARAMETRO_RISORSA = "Risorsa";
	public static final String LABEL_PARAMETRO_AZIONE = "Azione";
	
	public static final String LABEL_INFORMAZIONE_NON_DISPONIBILE = "Informazione non disponibile";
	public static final String LABEL_INFORMAZIONE_NON_PIU_PRESENTE = "Informazione non piu' presente";
	
	public static final String LABEL_EROGAZIONE_FRUIZIONE = "Erogazione/Fruizione";
	public static final String LABEL_FRUIZIONE = "Fruizione";
	public static final String LABEL_EROGAZIONE = "Erogazione";
	
	
	public static final String PERIODO_ULTIMA_SETTIMANA = "Ultima settimana";
	public static final String PERIODO_PERSONALIZZATO = "Personalizzato";
	public static final String PERIODO_ULTIMO_ANNO = "Ultimo anno";
	public static final String PERIODO_IERI = "Ieri";
	public static final String PERIODO_ULTIME_24_ORE = "Ultime 24 ore";
	public static final String PERIODO_ULTIME_12_ORE = "Ultime 12 ore";
	public static final String PERIODO_ULTIMA_ORA = "Ultima ora";
	public static final String PERIODO_ULTIMO_MESE = "Ultimo mese";
	public static final String PERIODO_LIVE = "Live";
	
	public static final String TIPO_RICERCA_IM = "im";
	public static final String TIPO_RICERCA_SPCOOP = "spcoop";
	
	public static final String VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO = "tokenInfo";
	public static final String VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO = "identificativoAutenticato";
	public static final String VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO = "applicativo";
	public static final String VALUE_TIPO_RICONOSCIMENTO_SOGGETTO = "soggetto";
	public static final String VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP = "ip";
	
	public static final String VALUE_CREDENZIALE_TRASPORTO_PREFIX = "trasporto";
	
	public static final String VALUE_CLIENT_ADDRESS_SOCKET = "socket";
	public static final String VALUE_CLIENT_ADDRESS_TRASPORTO = "transport";
	
	public static final String ICONA_ANDAMENTO_TEMPORALE = "timelapse";
	public static final String ICONA_ID_APPLICATIVO = "apps";
	public static final String ICONA_ID_MESSAGGIO = "message";
	public static final String ICONA_ID_TRANSAZIONE = "swap_horiz";
	public static final String ICONA_INFO_WHITE = "info_outline";

	public static final String LABEL_OPENSPCOOP2_WEB = "https://govway.org";
	
	public static final Integer SELECT_ITEM_VALORE_MASSIMO_ENTRIES = Integer.valueOf(1000);
	
	public static final List<SelectItem> SELECT_ITEM_ENTRIES = new ArrayList<>();
	static {
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(25), "25 Entries"));  
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(75), "75 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(125), "125 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(250), "250 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(500), "500 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(SELECT_ITEM_VALORE_MASSIMO_ENTRIES, "1000 Entries"));
	}
	
	public static final String SELECT_ITEM_VALORE_MASSIMO_ENTRIES_LABEL_KEY = "commons.search.selezionaNumeroMassimoEntries.label";
	public static final String SELEZIONATI_PRIMI_X_ELEMENTI_LABEL_KEY = "commons.search.selezionatiPrimiElementi.label";
	
	
	
	/* costanti properties file messages_it.properties*/ 
	
	public static final String DATA_LABEL_KEY = "commons.data.label";
	public static final String ESITO_LABEL_KEY = "commons.esito.label";
	public static final String DESCRIZIONE_LABEL_KEY = "commons.descrizione.label";
	public static final String AZIONE_LABEL_KEY = "commons.azione.label";
	public static final String SERVIZIO_APPLICATIVO_LABEL_KEY = "commons.applicativo.label";
	public static final String SERVIZIO_APPLICATIVO_TOKEN_LABEL_KEY = "commons.applicativoToken.label";
	public static final String TAG_LABEL_KEY = "commons.tag.label";
	public static final String API_LABEL_KEY = "commons.api.label";
	public static final String SERVIZIO_LABEL_KEY = "commons.servizio.label";
	public static final String SOGGETTO_LABEL_KEY = "commons.soggetto.label";
	public static final String SOGGETTO_LOCALE_LABEL_KEY = "commons.soggettoLocale.label";
	public static final String SOGGETTO_REMOTO_LABEL_KEY = "commons.soggettoRemoto.label";
	public static final String NOME_LABEL_KEY = "commons.nome.label";
	public static final String RICHIESTA_LABEL_KEY = "commons.richiesta.label";
	public static final String EROGATORE_LABEL_KEY = "commons.erogatore.label";
	public static final String FRUITORE_LABEL_KEY = "commons.fruitore.label";
	public static final String TOKEN_INFO_KEY = "commons.tokenInfo.label";
	public static final String TOKEN_CLIENT_ID_KEY = "commons.tokenInfo.clientID.label";
	public static final String TOKEN_CLIENT_ID_PDND_ORGANIZZAZIONE_KEY = "commons.tokenInfo.clientID.pdnd.label";
	public static final String TOKEN_ISSUER_KEY = "commons.tokenInfo.issuer.label";
	public static final String TOKEN_SUBJECT_KEY = "commons.tokenInfo.subject.label";
	public static final String TOKEN_USERNAME_KEY = "commons.tokenInfo.username.label";
	public static final String TOKEN_EMAIL_KEY = "commons.tokenInfo.email.label";
	public static final String IDENTIFICATIVO_AUTENTICATO_KEY = "commons.identificativoAutenticato.label";
	public static final String INDIRIZZO_IP_KEY = "commons.indirizzoIP.label";
	public static final String IDENTIFICAZIONE_TOKEN_KEY = "commons.identificazione.token.label";
	public static final String IDENTIFICAZIONE_TRASPORTO_KEY = "commons.identificazione.trasporto.label";
	public static final String AUTENTICAZIONE_KEY = "commons.autenticazione.label";
	
	public static final String TOKEN_CLIENT_ID_PDND_EXTERNAL_ID_KEY = "commons.tokenInfo.clientID.pdnd.externalId.label";
	public static final String TOKEN_CLIENT_ID_PDND_ORGANIZATION_ID_KEY = "commons.tokenInfo.clientID.pdnd.id.label";
	public static final String TOKEN_CLIENT_ID_PDND_CATEGORIA_KEY = "commons.tokenInfo.clientID.pdnd.categoria.label";
	
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_KEY = "commons.search.applicativo.defaultLabel";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_SOGGETTO_LOCALE_KEY = "commons.search.applicativo.defaultLabelNoSoggettoLocal";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_IDENTIFICAZIONE_NO_SOGGETTO_LOCALE_KEY = "commons.search.applicativo.defaultLabelNoIdentificazioneNoSoggettoLocal";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_SOGGETTO_FRUITORE_KEY = "commons.search.applicativo.defaultLabelNoSoggettoFruitore";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_IDENTIFICAZIONE_NO_SOGGETTO_FRUITORE_KEY = "commons.search.applicativo.defaultLabelNoIdentificazioneNoSoggettoFruitore";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_IDENTIFICAZIONE_KEY = "commons.search.applicativo.defaultLabelNoIdentificazione";
	
	public static final String SEARCH_MISSING_PARAMETERS_SOGGETTO_LABEL_KEY = "commons.search.missing_parameters.soggetto";
	public static final String SEARCH_MISSING_PARAMETERS_IDENTIFICAZIONE_LABEL_KEY = "commons.search.missing_parameters.identificazione";
	public static final String SEARCH_MISSING_PARAMETERS_APPLICATIVO_LABEL_KEY = "commons.search.missing_parameters.applicativo";
	public static final String SEARCH_MISSING_PARAMETERS_AUTENTICAZIONE_LABEL_KEY = "commons.search.missing_parameters.autenticazione";
	public static final String SEARCH_MISSING_PARAMETERS_ID_LABEL_KEY = "commons.search.missing_parameters.id";
	public static final String SEARCH_MISSING_PARAMETERS_SSL_SUBJECT_LABEL_KEY = "commons.search.missing_parameters.ssl.subject";
	public static final String SEARCH_MISSING_PARAMETERS_INDIRIZZO_IP_LABEL_KEY = "commons.search.missing_parameters.indirizzoIP";
	public static final String SEARCH_MISSING_PARAMETERS_CLAIM_LABEL_KEY = "commons.search.missing_parameters.claim";
	public static final String SEARCH_MISSING_PARAMETERS_VALORE_LABEL_KEY = "commons.search.missing_parameters.valore";
	public static final String SEARCH_MISSING_PARAMETERS_TIPO_LABEL_KEY = "commons.search.missing_parameters.tipo";
	public static final String SEARCH_MISSING_PARAMETERS_3D_INFO_LABEL_KEY = "commons.search.missing_parameters.3dInfo";
	
	public static final String SEARCH_TOKEN_ISSUER = "transazioni.search.sezioneFiltroRicercaLibera.tokenInfo.issuer.label";
	public static final String SEARCH_TOKEN_CLIENT_ID = "transazioni.search.sezioneFiltroRicercaLibera.tokenInfo.clientID.label";
	public static final String SEARCH_TOKEN_SUBJECT = "transazioni.search.sezioneFiltroRicercaLibera.tokenInfo.subject.label";
	public static final String SEARCH_TOKEN_USERNAME = "transazioni.search.sezioneFiltroRicercaLibera.tokenInfo.username.label";
	public static final String SEARCH_TOKEN_EMAIL = "transazioni.search.sezioneFiltroRicercaLibera.tokenInfo.email.label";
	
	public static final String SEARCH_PDND_PREFIX_ORGANIZATION_NAME = "transazioni.search.sezioneFiltroRicercaLibera.pdndPrefix.organization.name.label";
	public static final String SEARCH_PDND_ORGANIZATION_NAME = "transazioni.search.sezioneFiltroRicercaLibera.pdnd.organization.name.label";
	
	public static final String SALVA_RICERCA_MISSING_PARAMETER_LABEL_LABEL_KEY = "commons.salvaRicerca.missing_parameter.label";
	public static final String SALVA_RICERCA_MISSING_PARAMETER_DESCRIZIONE_LABEL_KEY = "commons.salvaRicerca.missing_parameter.descrizione";
	public static final String SALVA_RICERCA_MISSING_PARAMETER_VISIBILITA_LABEL_KEY = "commons.salvaRicerca.missing_parameter.visibilita";
	
	public static final String SALVA_RICERCA_INVALID_PARAMETER_LABEL_DIMENSIONE_NON_VALIDA_LABEL_KEY = "commons.salvaRicerca.invalid_parameter.label.dimensioneNonValida";
	public static final String SALVA_RICERCA_INVALID_PARAMETER_DESCRIZIONE_DIMENSIONE_NON_VALIDA_LABEL_KEY = "commons.salvaRicerca.invalid_parameter.descrizione.dimensioneNonValida";
	
	public static final String RICERCHE_UTENTE_LINK_CON_COUNT_LABEL_KEY = "commons.ricercheUtente.linkConCount";
	
	public static final String RICERCHE_UTENTE_MODULO_TRANSAZIONI_LABEL_KEY = "commons.ricercheUtente.modulo.transazioni.label";
	public static final String RICERCHE_UTENTE_MODULO_STATISTICHE_LABEL_KEY = "commons.ricercheUtente.modulo.statistiche.label";
	public static final String RICERCHE_UTENTE_MODULO_STATISTICHE_PERSONALIZZATE_LABEL_KEY = "commons.ricercheUtente.modulo.statistichePersonalizzate.label";
	public static final String RICERCHE_UTENTE_MODULO_CONFIGURAZIONI_LABEL_KEY= "commons.ricercheUtente.modulo.configurazioni.label";
	public static final String RICERCHE_UTENTE_MODULO_EVENTI_LABEL_KEY = "commons.ricercheUtente.modulo.eventi.label";
	public static final String RICERCHE_UTENTE_MODULO_ALLARMI_LABEL_KEY = "commons.ricercheUtente.modulo.allarmi.label";
	public static final String RICERCHE_UTENTE_VISIBILITA_PRIVATA_LABEL_KEY = "commons.salvaRicerca.modal.privata";
	public static final String RICERCHE_UTENTE_VISIBILITA_PUBBLICA_LABEL_KEY = "commons.salvaRicerca.modal.pubblica";
	public static final String RICERCHE_UTENTE_FORM_FIELD_FILE_LABEL_KEY = "commons.ricercheUtente.importaRicerche.label";
	
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_LABEL_KEY = "transazioni.search.tipoRicerca.temporale.label";
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITTENTE_LABEL_KEY = "transazioni.search.tipoRicerca.mittente.label";
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_LABEL_KEY = "transazioni.search.tipoRicerca.id.label";
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY = "transazioni.search.tipoRicerca.temporale.ricercaTemporale.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_LABEL_KEY = "transazioni.search.tipoRicerca.temporale.ricercaLibera.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_LABEL_KEY = "transazioni.search.tipoRicerca.mittente.ricercaTokenInfo.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_LABEL_KEY = "transazioni.search.tipoRicerca.mittente.ricercaSoggetto.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_LABEL_KEY = "transazioni.search.tipoRicerca.mittente.ricercaApplicativo.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY = "transazioni.search.tipoRicerca.mittente.ricercaIdentificativoAutenticato.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_LABEL_KEY = "transazioni.search.tipoRicerca.mittente.ricercaIndirizzoIP.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LABEL_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_LABEL_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.lvl2.ricercaBase.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_LABEL_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.lvl2.ricercaLibera.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_LABEL_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoMessaggio.label"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_LABEL_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoTransazione.label"; 

	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_DISTRIBUZIONE_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.distribuzione.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.personalizzata.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.identificativoAutenticato.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.indirizzoIP.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.applicativo.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.errori.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.azione.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.servizio.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.soggettoLocale.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.soggettoRemoto.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.esiti.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.temporale.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.mittente.label";
	
	/* Home page utente*/
	
	/* */
	public static final String OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE =  NomiTabelle.WELCOME_SCREEN.toString(); 
	public static final String OGGETTO_STATO_UTENTE_HOME_PAGE = "HOME_PAGE";
	
	public static final String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI_KEY = "utenti.profilo.homePage.transazioni";
	public static final String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE_KEY = "utenti.profilo.homePage.statistiche";
	
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO_KEY = "utenti.profilo.intervalloTemporaleHomePage.noGrafico";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultime24ore";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultimi7giorni";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultimi30giorni";
	public static final String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultimoAnno";
	
	public static final String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI = "transazioni";
	public static final String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE = "summary";
	
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO = "--";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE = "Ultime 24 ore";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI = "Ultimi 7 giorni";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI = "Ultimi 30 giorni";
	public static final String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO = "Ultimo anno";
	
	
	/* CSRF */
	
	public static final String SESSION_ATTRIBUTE_CSRF_TOKEN =  "csrf";
	public static final String PARAMETRO_CSRF_TOKEN = "_csrf";
	public static final String MESSAGGIO_ERRORE_CSRF_TOKEN_NON_VALIDO = "Controllo validità CSRF non superato, l'operazione non verrà eseguita.";
	// Identificativi tasti salvataggio
	public static final String ID_BUTTON_SALVA_PROFILO = "salvaProfiloBtn";
	public static final String ID_BUTTON_SALVA_PASSWORD = "modificaPwd";
	
	public static final String OPERAZIONE_NON_DISPONIBILE = "Operazione non disponibile";
	public static final String ERRORE_GENERICO = "ERRORE_GENERICO";
	
	public static final String OPERAZIONE_NON_CONSENTITA = "Operazione non consentita";

	/* NOMI PARAMETRI */
	
	public static final String USER_AGENT_HEADER_NAME = "User-Agent";
	
	/* COSTANTI NOMI FILE PROPERTIES */
	
	public static final String APP_CACHE_PROPERTIES_PATH = "monitor.jcs.properties";
	public static final String APP_CACHE_PROPERTIES_LOCAL_PATH = "monitor_local.jcs.properties";
	public static final String APP_CACHE_PROPERTIES = "MONITOR_CACHE_PROPERTIES";
	
	public static final String APP_LOG_PROPERTIES_PATH = "monitor.log4j2.properties";
	public static final String APP_LOG_PROPERTIES_LOCAL_PATH = "monitor_local.log4j2.properties";
	public static final String APP_LOG_PROPERTIES = "MONITOR_LOG_PROPERTIES";
	
	public static final String APP_PROPERTIES_PATH = "monitor.properties";
	public static final String APP_PROPERTIES_LOCAL_PATH = "monitor_local.properties";
	public static final String APP_PROPERTIES = "MONITOR_PROPERTIES";
	
	/* RICERCHE */

	public static final String VALUE_VISIBILITA_RICERCA_UTENTE_PRIVATA = "privata";
	public static final String VALUE_VISIBILITA_RICERCA_UTENTE_PUBBLICA = "pubblica";
	
	public static final String PARAMETER_IDS = "ids";
	public static final String PARAMETER_IS_ALL = "isAll";
	
	public static final String PARAMETER_IDS_ORIGINALI = "idsOriginali";
	public static final String PARAMETER_IS_ALL_ORIGINALE = "isAllOriginale";
	
	public static final String RICERCHE_EXPORTER_SERVLET_NAME = "ricercheExporter";
	
	public static final String[] SEARCH_FORM_FIELDS_DA_NON_SALVARE= {
			// AbstractCoreSearchForm
			"numeroPagine",
			"pageSize",
			"currentPage",
			"currentSearchSize",
			"start",
			"limit",
			"restoreSearch",
			"totalCount",
			"useCount",
			"executeQuery",
			"aggiornamentoDatiAbilitato",
			"visualizzaSelezioneDimensionePagina",
			"visualizzaFiltroAperto",
			"detached",
			"wrappedKeys",
			// AbstractDateSearchForm
			"lastPeriodo",
			"periodo",
			"periodoDefault",
			"dataInizio",
			"dataFine",
			"dataRicerca",
			"useDataRicerca",
			"ricercaUtente",
			// BaseSearchForm
			"log",
			"visualizzaIdCluster",
			"visualizzaIdClusterAsSelectList",
			"listIdCluster",
			"listLabelIdCluster",
			"visualizzaCanali",
			"listCanali",
			"mapCanaleToNodi",
			"defaultTipologiaRicerca",
			"_tipologiaRicercaEntrambiEnabled",
			"escludiRichiesteScartate",
			"attivoIntegrationManagerMessageBox",
			"labelTipoNomeMittente",
			"labelTipoNomeDestinatario",
			"labelTipoNomeTrafficoPerSoggetto",
			"labelTipoNomeSoggettoLocale",
			"labelApi",
			"labelNomeServizio",
			"labelNomeAzione",
			"ricerchePersonalizzate",
			"ricercaSelezionata",
			"statistichePersonalizzate",
			"statisticaSelezionata",
			"ricerchePersonalizzateAttive",
			"statistichePersonalizzateAttive",
			"protocolli",
			"checkSoggettoPddMonitor",
			"filtro",
			"intervalloRefresh",
			"tempoMassimoRefreshLive",
			"user",
			"sortOrder",
			"sortField",
			"sortOrders",
			TIPODATABASE,
			"isSearchFormEsitoConsegnaMultiplaEnabled",
			// da tutti i form
			"elencoFieldsRicercaDaIgnorare",
			"isCloned",
			"protocolCloned",
			"tipoNomeSoggettoLocaleCloned",
	};
	
}
