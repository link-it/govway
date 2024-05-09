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

package org.openspcoop2.web.lib.mvc;

/**
 * Costanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}

	/** SISTEMA NON DISPONIBILE */
	
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE = "Il sistema &egrave; momentaneamente indisponibile.";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME_PRE = "Torna alla ";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME = "home";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME_POST = ".";
	
	/** LABEL */
	
	public static final String LABEL_VISUALIZZA = "visualizza";
	public static final String LABEL_NON_DISPONIBILE = "non disp.";
	
	
	/** Multipart */
	public static final String MULTIPART = "multipart/form-data";
	public static final String MULTIPART_FILENAME = "filename";
	public static final String MULTIPART_START = "-----";
	public static final String MULTIPART_BEGIN = "BEGIN";
	public static final String MULTIPART_END = "END";
	
	
	/** PARAMETER */
	
	public static final String PARAMETER_NAME_OBJECTS_FOR_REMOVE = "obj";
	public static final String PARAMETER_NAME_OBJECTS_FOR_REMOVE_TYPE = "obj_t";
	public static final String PARAMETER_NAME_MSG_ERROR_EXPORT = "errorExport";
	
	public static final String PARAMETER_NAME_SEARCH_LIST_DONE= "_searchDone";
	public static final String PARAMETER_NAME_ID_DATI= "iddati";
	public static final String PARAMETER_NAME_PARAMS = "params";
	
	
	/** PARAMETER ATTRIBUTE */
	
	public static final String REQUEST_ATTIBUTE_PARAMS = "params";
	
	
	/** SEARCH */
	
	public static final String SEARCH_ENABLED ="on";
	public static final String SEARCH_DISABLED ="off";
	
	public static final String SEARCH_PAGE_SIZE = "pageSize";
	public static final String SEARCH_INDEX = "index";
	public static final String SEARCH = "search";
	
	public static final String SEARCH_PARAMETER_NAME = "search";
	public static final Integer SEARCH_PARAMETER_DEFAULT_SIZE = 50;
	public static final String SEARCH_PARAMETER_NOTE = "Attenzione! &Egrave; attualmente impostato il filtro di ricerca con la stringa ''${0}''";
	public static final String SEARCH_PARAMETER_NAME_FAKE_NAME = "__fake__search__";
	
	/** POSTBACK FUNCTION */
	
	public static final String POSTBACK_ELEMENT_NAME = "postBackElementName"; //nome dell'element che ha scaturito il postBack
	public static final String POSTBACK_FUNCTION = "postBack()";
	public static final String POSTBACK_FUNCTION_WITH_PARAMETER_START = "postBack('";
	public static final String POSTBACK_FUNCTION_WITH_PARAMETER_END = "')";
	public static final String POSTBACK_VIA_POST_FUNCTION_PREFIX = "postVersion_";
	
	/** IN USO FUNCTION */
	
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL = "url";
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO = "idOggetto";
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO = "tipoOggetto";
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA = "tipoRisposta";
	
	public static final String VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT= "text";
	public static final String VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_JSON = "json";
	
	/** CACHE MANAGER */
	public static final String PARAMETRO_RESET_CACHE_TIPO_OGGETTO = PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO;
	
	/** VERIFICA CERTIFICATI */
	public static final String PARAMETRO_VERIFICA_CERTIFICATI_TIPO_OGGETTO = PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO;
	
	/** FILTER */
	
	public static final String PARAMETRO_FILTER_NAME = "filterName_";
	public static final String PARAMETRO_FILTER_VALUE = "filterValue_";
	
	
	/** CHECK BOX */
	
	public static final String CHECK_BOX_ENABLED = "yes";
	public static final String CHECK_BOX_ENABLED_TRUE = "true";
	public static final String CHECK_BOX_ENABLED_ABILITATO = "abilitato";
	public static final String CHECK_BOX_DISABLED = "no";
	public static final String CHECK_BOX_DISABLED_FALSE = "false";
	public static final String CHECK_BOX_WARN = "warn";
	public static final String CHECK_BOX_CONFIG_ENABLE = "config_enable";
	public static final String CHECK_BOX_CONFIG_WARNING = "config_warning";
	public static final String CHECK_BOX_CONFIG_ERROR = "config_error";
	public static final String CHECK_BOX_CONFIG_DISABLE = "config_disable";
	
	
	/** PAGE DATA TITLE */
	
	public static final String PAGE_DATA_TITLE_LABEL_AGGIUNGI = "Aggiungi"; // Utilizzato solo dalle utility interne
	//public static final String PAGE_DATA_TITLE_LABEL_ELENCO = "Elenco"; // DEPRECATO
	public static final String PAGE_DATA_TITLE_LABEL_VISUALIZZA = "Visualizza";
	public static final String PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA = "Risultati ricerca";
	public static final String PAGE_DATA_TITLE_LABEL_ELIMINA = "Elimina";
	public static final String PAGE_DATA_TITLE_LABEL_MODIFICA = "Modifica";
	public static final String PAGE_DATA_TITLE_LABEL_REGISTRO = "Registro";
	
	
	/** STRUTS-FORWARD */
	
	public static final String STRUTS_FORWARD_FORM = "Form";
	public static final String STRUTS_FORWARD_CONFIRM_FORM = "ConfirmForm";
	public static final String STRUTS_FORWARD_OK = "Ok";
	public static final String STRUTS_FORWARD_CHECK_ERROR = "CheckError";	
	public static final String STRUTS_FORWARD_ERRORE_GENERALE = "Error";
	
	public static final String STRUTS_ACTION_SUFFIX_ADD = "Add.do";
	public static final String STRUTS_ACTION_SUFFIX_CHANGE = "Change.do";
	public static final String STRUTS_ACTION_SUFFIX_DELETE = "Del.do";
	public static final String STRUTS_ACTION_SUFFIX_LIST = "List.do";

	
	
	/** SESSION */
	
	public static final String SESSION_ATTRIBUTE_TAB_KEY_PREFIX = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_ATTRIBUTO_TAB_SESSION_KEY_PREFIX;
	public static final String SESSION_ATTRIBUTE_TAB_KEYS_MAP ="_tabKeysMap_";
	public static final String SESSION_ATTRIBUTE_TAB_MAP_CREATION_DATE = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "creation_date";
	
	protected static final String SESSION_ATTRIBUTE_LOGIN = "Login";
	public static final String SESSION_ATTRIBUTE_PAGE_DATA_REDIRECT = "PageData";
	protected static final String SESSION_ATTRIBUTE_USER = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "User";
	public static final String SESSION_ATTRIBUTE_GENERAL_DATA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "GeneralData";
	public static final String SESSION_ATTRIBUTE_PAGE_DATA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + SESSION_ATTRIBUTE_PAGE_DATA_REDIRECT;
	public static final String SESSION_ATTRIBUTE_RICERCA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "Ricerca";
	public static final String SESSION_ATTRIBUTE_RISULTATI_LISTA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "RisultatiLista";
	protected static final String SESSION_ATTRIBUTE_CONTA_LISTE = "contaListe";
	public static final String SESSION_ATTRIBUTE_LIST_ELEMENT = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "ListElement";
	public static final String SESSION_ATTRIBUTE_ID_DATI = PARAMETER_NAME_ID_DATI;
	protected static final String SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED = org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED;
	protected static final String SESSION_ATTRIBUTE_VALUE_SOGGETTO_VIRTUALE_YES ="yes";
	protected static final String SESSION_ATTRIBUTE_GESTIONE_WSBL ="GestioneWSBL";
	protected static final String SESSION_ATTRIBUTE_CONFIGURAZIONI_PERSONALIZZATE ="ConfigurazioniPersonalizzate";
	public static final String SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX = "oldConfProp_";
	
	
	
	
	/** COOKIES **/
	
	public static final String COOKIE_NAME_JSESSIONID = "JSESSIONID_GW_CONSOLE";
	public static final String COOKIE_ATTRIBUTE_SAME_SITE_NAME = "SameSite";
	public static final String COOKIE_ATTRIBUTE_SAME_SITE_VALUE_LAX = "Lax";
	public static final String COOKIE_ATTRIBUTE_SAME_SITE_VALUE_STRICT = "Strict";
	public static final String COOKIE_ATTRIBUTE_SAME_SITE_VALUE_NONE = "None";
	public static final String COOKIE_SECURE_ATTRIBUTE_NAME="Secure";
	
	/** CSFR **/
	
	public static final String SESSION_ATTRIBUTE_CSRF_TOKEN =  SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "csrf";
	public static final String PARAMETRO_CSRF_TOKEN = "_csrf";
	public static final String MESSAGGIO_ERRORE_CSRF_TOKEN_NON_VALIDO = "Controllo validit&agrave; CSRF non superato, l'operazione non verr&agrave; eseguita."; 

	
	/** GESTIONE LOGIN PRINCIPAL **/
	
	public static final String PRINCIPAL_ERROR_MSG = "principalErrorMsg";
	public static final String MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_PRINCIPAL_ASSENTE = "Impossibile leggere le informazioni dell'utenza. Principal assente.";
	public static final String MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_ERRORE_INTERNO = "Si &egrave; verificato un errore durante il login, impossibile autenticare l''utente {0}.";
	public static final String MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_TROVATO = "Il sistema non riesce ad autenticare l''utente {0}: Utente non registrato.";
	public static final String MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_VALIDO = "Il sistema non riesce ad autenticare l''utente {0}: {1}.";
	public static final String MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_SESSIONE_SCADUTA = "Il sistema non riesce ad autenticare l''utente {0}. La sessione &egrave; scaduta.";
	public static final String MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_AUTORIZZATO = "Il sistema non riesce ad autenticare l''utente {0}: {1}.";
	
	/** DATA ELEMENT **/
	
	public static final String DATA_ELEMENT_VIEW_NAME = "view";
	public static final String DATA_ELEMENT_EDIT_MODE_NAME = "edit-mode";
	public static final String DATA_ELEMENT_EDIT_MODE_DISABLE_NAME = "view-noeditbutton";
	public static final String DATA_ELEMENT_DISABLE_ONLY_BUTTON = "view-nobutton";
	
	public static final String DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK = "in_progress_postback";
	public static final String DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS = "in_progress";
	public static final String DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END = "end";
	
	public static final DataElement DATA_ELEMENT_HIDDENT_EDIT_MODE_END = new DataElement();
	static{
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setLabel(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setValue(DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setType(DataElementType.HIDDEN);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setName(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
	}
	
	public static final Parameter PARAMETER_EDIT_MODE_END = new Parameter
			(DATA_ELEMENT_EDIT_MODE_NAME, DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END);

	
	public static final DataElement DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS = new DataElement();
	static{
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setLabel(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setValue(DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setType(DataElementType.HIDDEN);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setName(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
	}
	
	/** FUNZIONE ESEGUI / ANNULLA  CONFERMA **/
	
	public static final String PARAMETRO_ACTION_CONFIRM = "actionConfirm";
	public static final String PARAMETRO_IS_POSTBACK = "isPostback";
	public static final String PARAMETRO_AGGIORNA_RICERCA = "aggiornaRicerca";
	public static final String PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE = "rmElFromCache";
	public static final String PARAMETRO_AZIONE = "azione";
	
	public static final String LABEL_MONITOR_BUTTON_OK = "Ok";
	public static final String LABEL_MONITOR_BUTTON_CONFERMA = "Conferma";
	public static final String LABEL_MONITOR_BUTTON_ANNULLA = "Annulla";
	public static final String LABEL_MONITOR_BUTTON_INVIA = "Salva";
	public static final String LABEL_MONITOR_BUTTON_FILTRA = "Filtra";
	public static final String LABEL_MONITOR_BUTTON_RIPULISCI = "Ripulisci";
	public static final String LABEL_MONITOR_BUTTON_GENERA = "Genera";
	public static final String LABEL_MONITOR_BUTTON_CHIUDI = "Chiudi";
	
	public static final String TOOLTIP_MONITOR_BUTTON_GENERA_PWD = "Genera Password";
		
	public static final String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX = "EseguiConferma(";
	public static final String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX = ")";
	
	public static final String LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX = "AnnullaConferma("; 
	public static final String LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX = ")"; 
	
	public static final String PARAMETRO_ACTION_CONFIRM_VALUE_OK = "ok";
	public static final String PARAMETRO_ACTION_CONFIRM_VALUE_NO = "no";
	
	public static final String HTML_MODAL_SPAN_PREFIX = "<p class=\"contenutoModal\"><span>";
	public static final String HTML_MODAL_SPAN_SUFFIX = "</span></p>";
	
	
	public static final String CUSTOM_JS_FUNCTION_INPUT_NUMBER_VALIDATION = "customInputNumberChangeEventHandler";
	
	public static final String JS_FUNCTION_VISUALIZZA_AJAX_STATUS = "visualizzaAjaxStatus();";
	public static final String JS_FUNCTION_NASCONDI_AJAX_STATUS = "nascondiAjaxStatus();";
	
	public static final String JS_FUNCTION_GO_TO_PREFIX = "goToLocation('"; 
	public static final String JS_FUNCTION_GO_TO_SUFFIX = "')"; 
	
	/** PARAMETRI BINARI **/
	
	public static final String PARAMETER_FILENAME_PREFIX = "__fn__";  
	public static final String PARAMETER_FILEID_PREFIX = "__fd__";
	public static final String PARAMETER_FILENAME_REMOVE_PLACEHOLDER = "__fr__";  
	
	/** PARAMETRI MESSAGE PAGE **/
	
	public static final String PARAMETER_MESSAGE_TEXT ="mpText";
	public static final String PARAMETER_MESSAGE_TITLE ="mpTitle";
	public static final String PARAMETER_MESSAGE_TYPE ="mpType";
	public static final String PARAMETER_MESSAGE_BREADCRUMB ="mpBC";
	
	/** PARAMETRI TAB_ID **/
	
	public static final String PARAMETER_TAB_KEY ="__tabKey__";
	public static final String PARAMETER_PREV_TAB_KEY ="__prevTabKey__";
	
	/** PARAMETRI LOCK **/
	
	public static final String PARAMETER_LOCK_PREFIX = "__lk__";
	public static final String PARAMETER_LOCK_DEFAULT_VALUE = "********";
	public static final String TITOLO_FINESTRA_MODALE_DOWNLOAD_MESSAGE_WARNING = "Attenzione";
	public static final String TITOLO_FINESTRA_MODALE_VISUALIZZA_MESSAGE_WARNING = "Attenzione";
	public static final String TITOLO_FINESTRA_MODALE_COPIA_MESSAGE_WARNING = "Attenzione";
	
	/** PAGE DATA TITLE */
	
	public static final MessageType MESSAGE_TYPE_ERROR = MessageType.ERROR;
	public static final MessageType MESSAGE_TYPE_ERROR_SINTETICO = MessageType.ERROR_SINTETICO;
	public static final MessageType MESSAGE_TYPE_INFO = MessageType.INFO;
	public static final MessageType MESSAGE_TYPE_INFO_SINTETICO = MessageType.INFO_SINTETICO;
	public static final MessageType MESSAGE_TYPE_CONFIRM = MessageType.CONFIRM;
	public static final MessageType MESSAGE_TYPE_WARN = MessageType.WARN;
	public static final MessageType MESSAGE_TYPE_WARN_SINTETICO = MessageType.WARN_SINTETICO;
	
	public static final String MESSAGE_TYPE_ERROR_TITLE = "Messaggio di Errore";
	public static final String MESSAGE_TYPE_WARN_TITLE = "Attenzione";
	public static final String MESSAGE_TYPE_INFO_TITLE = "Messaggio Informativo";
	public static final String MESSAGE_TYPE_CONFIRM_TITLE = "Conferma Operazione";
	public static final String MESSAGE_TYPE_DIALOG_TITLE = "Finestra di Dialogo";
	
	/** CLASSI DEFAULT CSS ELEMENTI INPUT */
	public static final String INPUT_LONG_CSS_CLASS = "inputLinkLong";
	public static final String INPUT_PWD_CHIARO_CSS_CLASS = "inputLinkLongPwdChiaro";
	public static final String INPUT_CSS_CLASS = "inputLink";
	public static final String LABEL_MEDIUM_CSS_CLASS = "labelMedium";
	public static final String LABEL_LONG_CSS_CLASS = "labelLong";
	public static final String INPUT_TEXT_DEFAULT_CSS_CLASS = "spanNoEdit";
	public static final String INPUT_TEXT_BOLD_CSS_CLASS = "spanNoEdit-bold";
	public static final String INPUT_TEXT_BOLD_RED_CSS_CLASS = "spanNoEdit-bold-red";
	public static final String INPUT_TEXT_DEFAULT_CSS_CLASS_FONT_SIZE_16 = "spanNoEdit16";	
	
	public static final String LABEL_TITOLO_SEZIONE_DEFAULT = "Filtri di Ricerca";
	
	public static final String TOOLTIP_VISUALIZZA_FILTRI_RICERCA = "Visualizza Filtri di Ricerca";
	public static final String TOOLTIP_NASCONDI_FILTRI_RICERCA = "Nascondi Filtri di Ricerca";
	public static final String TOOLTIP_VISUALIZZA_FIELDSET = "Visualizza Sezione";
	public static final String TOOLTIP_NASCONDI_FIELDSET = "Nascondi Sezione";
	public static final String TOOLTIP_VISUALIZZA_SUBTITLE = "Visualizza Sezione";
	public static final String TOOLTIP_NASCONDI_SUBTITLE = "Nascondi Sezione";
	public static final String TOOLTIP_ICONA_COPIA = "Copia {0}";
	public static final String TOOLTIP_ELIMINA_FILE = "Rimuovi {0}";
	
	public static final String TOOLTIP_VISUALIZZA_SEZIONE_FILTRI_RICERCA = "Visualizza Sezione";
	public static final String TOOLTIP_NASCONDI_SEZIONE_FILTRI_RICERCA = "Nascondi Sezione";
	
	/** ICONE
	 * 
	 *  Le icone devono essere definite come stringhe contenenti il codice unicode del carattere da visualizzare
	 *  al posto di utilizzare le legature che non sono supportate da tutti i browser.
	 *  Ulteriori dettagli in:
	 *  
	 *  https://developers.google.com/fonts/docs/material_icons?hl=it#using_the_icons_in_html
	 * 
	 * 	per ogni icona indicare il codice unicode e nel commento il nome dell'icona per agevolare la ricerca nel repo icone 
	 * 
	 * **/
	
	public static final String ICON_VISUALIZZA_SEZIONE_FILTRI_RICERCA = "&#xE5CF;"; // "expand_more"
	public static final String ICON_NASCONDI_SEZIONE_FILTRI_RICERCA = "&#xE5CE;"; //"expand_less"
	public static final String ICON_VISUALIZZA_FIELDSET = "&#xE5CF;"; // "expand_more"
	public static final String ICON_NASCONDI_FIELDSET = "&#xE5CE;"; //"expand_less"
	public static final String ICON_VISUALIZZA_SUBTITLE = "&#xE5CF;"; // "expand_more"
	public static final String ICON_NASCONDI_SUBTITLE = "&#xE5CE;"; //"expand_less"
	
	public static final String ICON_ELIMINA_FILE = "&#xE872;"; //"delete"
	
	public static final String INFO_BUTTON_ICON = "&#xE88E;"; // info 
	public static final String INFO_BUTTON_ICON_WHITE = "&#xE88F;"; // info_outline
	public static final String ICON_DIALOG_HEADER = "&#xE8B2;"; // report_problem
	public static final String ICON_COPY = "&#xE14D;"; // "content_copy" 
	public static final String ICON_USO = "&#xE880;"; // "find_in_page" //"location_searching"
	public static final String ICON_USO_INFO = INFO_BUTTON_ICON; //"info"
	
	public static final String ICON_PERSON = "&#xE7FD;"; // "person"
	public static final String ICON_UPDATE = "&#xE923;"; // "update"
	public static final String ICON_SCHEDULE = "&#xE8B5;"; // "schedule"
	
	public static final String ICONA_RESET_CACHE_ELEMENTO = "&#xE863;"; // autorenew
	public static final String ICONA_RESET_CACHE_ELEMENTO_TOOLTIP = "Rimuovi dalla cache";
	public static final String ICONA_RESET_CACHE_ELEMENTO_TOOLTIP_CON_PARAMETRO = "Rimuovi {0} dalla cache";
	
	public static final String ICONA_VERIFICA = "&#xE8BE;"; // settings_ethernet
	public static final String ICONA_VERIFICA_TOOLTIP = "Verifica";
	public static final String ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO = "Verifica {0}";

	public static final String ICONA_VERIFICA_CONNETTIVITA_TOOLTIP = "Verifica connettività";
	public static final String ICONA_VERIFICA_CONNETTIVITA_TOOLTIP_CON_PARAMETRO = "Verifica connettività {0}";
	
	public static final String ICONA_VERIFICA_CERTIFICATI = "&#xEA17;"; // policy
	public static final String ICONA_VERIFICA_CERTIFICATI_TOOLTIP = "Verifica certificati";
	public static final String ICONA_VERIFICA_CERTIFICATI_TOOLTIP_CON_PARAMETRO = "Verifica certificati {0}";
	
	public static final String ICONA_VISUALIZZA_RUNTIME_ALLARME = Costanti.ICON_USO;
	public static final String ICONA_VISUALIZZA_RUNTIME_ALLARME_TOOLTIP = "Visualizza runtime";
	
	public static final String ICONA_AGGIORNA_RICERCA = "&#xE863;"; // autorenew
	public static final String ICONA_AGGIORNA_RICERCA_TOOLTIP = "Aggiorna Ricerca";
	
	public static final String ICONA_MENU_AZIONI_BUTTON = "&#xE5D4;"; // more_vert
	public static final String ICONA_MENU_AZIONI_BUTTON_TOOLTIP = ""; // "Visualizza azioni disponibili"
	
	public static final String ICONA_EDIT = "&#xE3C9;"; // "edit"
	public static final String ICONA_EDIT_TOOLTIP = "Modifica";
	
	public static final String ICON_LOCK = "&#xE897;"; // lock
	public static final String ICON_LOCK_OPEN = "&#xE898;"; // lock_open
	
	public static final String ICON_VISIBILITY = "&#xE8F4;"; // visibility
	public static final String ICON_VISIBILITY_OFF = "&#xE8F5;"; // visibility_off
	
	public static final String ICON_SEARCH = "&#xE8B6;"; // search
	
	public static final String ICON_SUPERVISOR_ACCOUNT = "&#xE8D3;"; // supervisor_account
	
	public static final String ICON_CHEVRON_RIGHT = "&#xE5CC;"; // chevron_right
	public static final String ICON_CHEVRON_LEFT = "&#xE5CB;"; // chevron_left
	
	public static final String ICONA_FRECCIA_SU = "&#xE316;"; //keyboard_arrow_up
	public static final String ICONA_FRECCIA_GIU = "&#xE313;"; // keyboard_arrow_down
	public static final String ICONA_PLACEHOLDER = "&#160;&#160;&#160;&#160;&#160;"; // 5 spazi
	
	public static final String ICONA_CONTINUE = "&#xE5DB;"; // arrow_downward
	public static final String ICONA_BREAK = "&#xE5CD;"; // close

	public static final String ICONA_ALARM_ACTIVE = "&#xE855;"; // alarm
	public static final String ICONA_ALARM_PASSIVE = "&#xE857;"; // alarm_off
	
	public static final String ICONA_SCHEDULE_ACTIVE = "&#xe889;"; // history
	public static final String ICONA_SCHEDULE_PASSIVE = "&#xf17d;"; // history_toggle_off
	
	public static final String ICONA_DESCRIZIONE = "&#xE873;"; //description
	public static final String ICONA_VISUALIZZA = "&#xE89E;"; // open_in_new
	public static final String ICONA_SETTINGS = "&#xE8B8;"; // settings
	public static final String ICONA_ELENCO = "&#xE0EE;";// "list_alt"
	public static final String ICONA_UPGRADE = "&#xE3CA;"; // exposure 
	public static final String ICONA_TOGGLE_ON = "&#xE9F6;"; //"toggle_on"
	public static final String ICONA_TOGGLE_OFF = "&#xE9F5;"; //"toggle_off"
	
	// Icone del set material symbols
	
	public static final String ICONA_COPY_LOCK_TOOLTIP = "Copia informazione cifrata";
	
	public static final String ICON_VISIBILITY_LOCK = "&#xF653;"; // visibility_lock
	public static final String ICONA_VISIBILITY_LOCK_TOOLTIP = "Visualizza informazione cifrata";
	
	
	public static final int LUNGHEZZA_LABEL_TABS = 30;
	public static final int LUNGHEZZA_RIGA_TESTO_TABELLA = 150;
	
	// Indica il numero delle possibili classi CSS per i tag dei gruppi, modificare questo valore se si vuole modificare il numero delle classi disponibili
	public static final Integer NUMERO_GRUPPI_CSS = 30;
	
	public static final String SA_TIPO_DEFAULT_VALUE = "clientORserver";	
	
	/* CSP */
	public static final String REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE = "_csp_random_nonce";
	
	/* JSON */
	public static final String CHAR_APERTURA_JSON = "{";
	public static final String CHAR_CHIUSURA_JSON = "}";
	public static final String CHAR_QUOTA_JSON = "\"";
	public static final String CHAR_VIRGOLA_JSON = ",";
	public static final String CHAR_DUE_PUNTI_JSON = ":";
	public static final String NULL_VALUE_JSON = "null";
	public static final String KEY_ESITO_JSON = "esito";
	public static final String KEY_DETTAGLIO_ESITO_JSON = "dettaglioEsito";
}