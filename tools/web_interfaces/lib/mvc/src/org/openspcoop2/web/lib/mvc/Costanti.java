/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

	/** SISTEMA NON DISPONIBILE */
	
	//public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE = "Il sistema &egrave; momentaneamente indisponibile.<BR/>Si prega di riprovare pi&ugrave; tardi.";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE = "Il sistema &egrave; momentaneamente indisponibile.";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME_PRE = "Torna alla ";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME = "home";
	public static final String MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME_POST = ".";
	
	/** LABEL */
	
	public final static String LABEL_VISUALIZZA = "visualizza";
	public final static String LABEL_NON_DISPONIBILE = "non disp.";
	
	
	/** Multipart */
	public static final String MULTIPART = "multipart/form-data";
	public static final String MULTIPART_FILENAME = "filename";
	public static final String MULTIPART_START = "-----";
	public static final String MULTIPART_BEGIN = "BEGIN";
	public static final String MULTIPART_END = "END";
	
	
	/** PARAMETER */
	
	public final static String PARAMETER_NAME_OBJECTS_FOR_REMOVE = "obj";
	public final static String PARAMETER_NAME_OBJECTS_FOR_REMOVE_TYPE = "obj_t";
	public final static String PARAMETER_NAME_MSG_ERROR_EXPORT = "errorExport";
	
	public final static String PARAMETER_NAME_SEARCH_LIST_DONE= "_searchDone";
	public final static String PARAMETER_NAME_ID_DATI= "iddati";
	public final static String PARAMETER_NAME_PARAMS = "params";
	
	
	/** PARAMETER ATTRIBUTE */
	
	public final static String REQUEST_ATTIBUTE_PARAMS = "params";
	
	
	/** SEARCH */
	
	public final static String SEARCH_ENABLED ="on";
	public final static String SEARCH_DISABLED ="off";
	
	public final static String SEARCH_PAGE_SIZE = "pageSize";
	public final static String SEARCH_INDEX = "index";
	public final static String SEARCH = "search";
	
	public final static String SEARCH_PARAMETER_NAME = "search";
	public final static Integer SEARCH_PARAMETER_DEFAULT_SIZE = 50;
	public final static String SEARCH_PARAMETER_NOTE = "Attenzione! &Egrave; attualmente impostato il filtro di ricerca con la stringa ''${0}''";
	public final static String SEARCH_PARAMETER_NAME_FAKE_NAME = "__fake__search__";
	
	/** POSTBACK FUNCTION */
	
	public final static String POSTBACK_ELEMENT_NAME = "postBackElementName"; //nome dell'element che ha scaturito il postBack
	public final static String POSTBACK_FUNCTION = "postBack()";
	public final static String POSTBACK_FUNCTION_WITH_PARAMETER_START = "postBack('";
	public final static String POSTBACK_FUNCTION_WITH_PARAMETER_END = "')";
	public final static String POSTBACK_VIA_POST_FUNCTION_PREFIX = "postVersion_";
	
	/** IN USO FUNCTION */
	
	public final static String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL = "url";
	public final static String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO = "idOggetto";
	public final static String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO = "tipoOggetto";
	public final static String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA = "tipoRisposta";
	
	public final static String VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT= "text";
	public final static String VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_JSON = "json";
	
	/** FILTER */
	
	public final static String PARAMETRO_FILTER_NAME = "filterName_";
	public final static String PARAMETRO_FILTER_VALUE = "filterValue_";
	
	
	/** CHECK BOX */
	
	public final static String CHECK_BOX_ENABLED = "yes";
	public final static String CHECK_BOX_ENABLED_TRUE = "true";
	public final static String CHECK_BOX_ENABLED_ABILITATO = "abilitato";
	public final static String CHECK_BOX_DISABLED = "no";
	public final static String CHECK_BOX_DISABLED_FALSE = "false";
	public final static String CHECK_BOX_WARN = "warn";
	public final static String CHECK_BOX_CONFIG_ENABLE = "config_enable";
	public final static String CHECK_BOX_CONFIG_WARNING = "config_warning";
	public final static String CHECK_BOX_CONFIG_ERROR = "config_error";
	public final static String CHECK_BOX_CONFIG_DISABLE = "config_disable";
	
	
	/** PAGE DATA TITLE */
	
	public final static String PAGE_DATA_TITLE_LABEL_AGGIUNGI = "Aggiungi"; // Utilizzato solo dalle utility interne
	//public final static String PAGE_DATA_TITLE_LABEL_ELENCO = "Elenco"; // DEPRECATO
	public final static String PAGE_DATA_TITLE_LABEL_VISUALIZZA = "Visualizza";
	public final static String PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA = "Risultati ricerca";
	public final static String PAGE_DATA_TITLE_LABEL_ELIMINA = "Elimina";
	public final static String PAGE_DATA_TITLE_LABEL_MODIFICA = "Modifica";
	public final static String PAGE_DATA_TITLE_LABEL_REGISTRO = "Registro";
	
	
	/** STRUTS-FORWARD */
	
	public final static String STRUTS_FORWARD_FORM = "Form";
	public final static String STRUTS_FORWARD_CONFIRM_FORM = "ConfirmForm";
	public final static String STRUTS_FORWARD_OK = "Ok";
	public final static String STRUTS_FORWARD_CHECK_ERROR = "CheckError";	
	public final static String STRUTS_FORWARD_ERRORE_GENERALE = "Error";

	
	
	/** SESSION */
	
	public static final String SESSION_ATTRIBUTE_TAB_KEY_PREFIX = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_ATTRIBUTO_TAB_SESSION_KEY_PREFIX;
	public static final String SESSION_ATTRIBUTE_TAB_KEYS_MAP ="_tabKeysMap_";
	public static final String SESSION_ATTRIBUTE_TAB_MAP_CREATION_DATE = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "creation_date";
	
	protected final static String SESSION_ATTRIBUTE_LOGIN = "Login";
	protected final static String SESSION_ATTRIBUTE_USER = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "User";
	public final static String SESSION_ATTRIBUTE_GENERAL_DATA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "GeneralData";
	public final static String SESSION_ATTRIBUTE_PAGE_DATA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "PageData";
	public final static String SESSION_ATTRIBUTE_RICERCA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "Ricerca";
	public final static String SESSION_ATTRIBUTE_RISULTATI_LISTA = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "RisultatiLista";
	protected final static String SESSION_ATTRIBUTE_CONTA_LISTE = "contaListe";
	public final static String SESSION_ATTRIBUTE_LIST_ELEMENT = SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "ListElement";
	public final static String SESSION_ATTRIBUTE_ID_DATI = PARAMETER_NAME_ID_DATI;
	protected final static String SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED = org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED;
	protected final static String SESSION_ATTRIBUTE_VALUE_SOGGETTO_VIRTUALE_YES ="yes";
	protected final static String SESSION_ATTRIBUTE_GESTIONE_WSBL ="GestioneWSBL";
	protected final static String SESSION_ATTRIBUTE_CONFIGURAZIONI_PERSONALIZZATE ="ConfigurazioniPersonalizzate";
	public final static String SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX = "oldConfProp_";
	public final static String SESSION_ATTRIBUTE_PAGE_DATA_REDIRECT = "PageData";
	
	
	
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
	
	public final static String DATA_ELEMENT_VIEW_NAME = "view";
	public final static String DATA_ELEMENT_EDIT_MODE_NAME = "edit-mode";
	public final static String DATA_ELEMENT_EDIT_MODE_DISABLE_NAME = "view-noeditbutton";
	public final static String DATA_ELEMENT_DISABLE_ONLY_BUTTON = "view-nobutton";
	
	public final static String DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK = "in_progress_postback";
	public final static String DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS = "in_progress";
	public final static String DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END = "end";
	
	public static DataElement DATA_ELEMENT_HIDDENT_EDIT_MODE_END = new DataElement();
	static{
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setLabel(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setValue(DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setType(DataElementType.HIDDEN);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_END.setName(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
	}
	
	public static Parameter PARAMETER_EDIT_MODE_END = new Parameter
			(DATA_ELEMENT_EDIT_MODE_NAME, DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END);

	
	public static DataElement DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS = new DataElement();
	static{
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setLabel(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setValue(DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setType(DataElementType.HIDDEN);
		DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS.setName(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
	}
	
	/** FUNZIONE ESEGUI / ANNULLA  CONFERMA **/
	
	public final static String PARAMETRO_ACTION_CONFIRM = "actionConfirm";
	public final static String PARAMETRO_IS_POSTBACK = "isPostback";
	public final static String PARAMETRO_AGGIORNA_RICERCA = "aggiornaRicerca";
	public final static String PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE = "rmElFromCache";
	public final static String PARAMETRO_AZIONE = "azione";
	
	public final static String LABEL_MONITOR_BUTTON_OK = "Ok";
	public final static String LABEL_MONITOR_BUTTON_CONFERMA = "Conferma";
	public final static String LABEL_MONITOR_BUTTON_ANNULLA = "Annulla";
	public final static String LABEL_MONITOR_BUTTON_INVIA = "Salva";
	public final static String LABEL_MONITOR_BUTTON_FILTRA = "Filtra";
	public final static String LABEL_MONITOR_BUTTON_RIPULISCI = "Ripulisci";
	public final static String LABEL_MONITOR_BUTTON_GENERA = "Genera";
	public final static String LABEL_MONITOR_BUTTON_CHIUDI = "Chiudi";
	
	public final static String TOOLTIP_MONITOR_BUTTON_GENERA_PWD = "Genera Password";
		
	public final static String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX = "EseguiConferma(";
	public final static String LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX = ")";
	
	public final static String LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX = "AnnullaConferma("; 
	public final static String LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX = ")"; 
	
	public final static String PARAMETRO_ACTION_CONFIRM_VALUE_OK = "ok";
	public final static String PARAMETRO_ACTION_CONFIRM_VALUE_NO = "no";
	
	public final static String HTML_MODAL_SPAN_PREFIX = "<p class=\"contenutoModal\"><span>";
	public final static String HTML_MODAL_SPAN_SUFFIX = "</span></p>";
	
	
	public final static String CUSTOM_JS_FUNCTION_INPUT_NUMBER_VALIDATION = "customInputNumberChangeEventHandler";
	
	public final static String JS_FUNCTION_VISUALIZZA_AJAX_STATUS = "visualizzaAjaxStatus();";
	public final static String JS_FUNCTION_NASCONDI_AJAX_STATUS = "nascondiAjaxStatus();";
	
	public final static String JS_FUNCTION_GO_TO_PREFIX = "goToLocation('"; 
	public final static String JS_FUNCTION_GO_TO_SUFFIX = "')"; 
	
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
	
	/** PAGE DATA TITLE */
	
	public final static MessageType MESSAGE_TYPE_ERROR = MessageType.ERROR;
	public final static MessageType MESSAGE_TYPE_ERROR_SINTETICO = MessageType.ERROR_SINTETICO;
	public final static MessageType MESSAGE_TYPE_INFO = MessageType.INFO;
	public final static MessageType MESSAGE_TYPE_INFO_SINTETICO = MessageType.INFO_SINTETICO;
	public final static MessageType MESSAGE_TYPE_CONFIRM = MessageType.CONFIRM;
	public final static MessageType MESSAGE_TYPE_WARN = MessageType.WARN;
	public final static MessageType MESSAGE_TYPE_WARN_SINTETICO = MessageType.WARN_SINTETICO;
	
	public final static String MESSAGE_TYPE_ERROR_TITLE = "Messaggio di Errore";
	public final static String MESSAGE_TYPE_WARN_TITLE = "Attenzione";
	public final static String MESSAGE_TYPE_INFO_TITLE = "Messaggio Informativo";
	public final static String MESSAGE_TYPE_CONFIRM_TITLE = "Conferma Operazione";
	public final static String MESSAGE_TYPE_DIALOG_TITLE = "Finestra di Dialogo";
	
	/** CLASSI DEFAULT CSS ELEMENTI INPUT */
	public final static String INPUT_LONG_CSS_CLASS = "inputLinkLong";
	public final static String INPUT_PWD_CHIARO_CSS_CLASS = "inputLinkLongPwdChiaro";
	public final static String INPUT_CSS_CLASS = "inputLink";
	public final static String LABEL_MEDIUM_CSS_CLASS = "labelMedium";
	public final static String LABEL_LONG_CSS_CLASS = "labelLong";
	public final static String INPUT_TEXT_DEFAULT_CSS_CLASS = "spanNoEdit";
	public final static String INPUT_TEXT_BOLD_CSS_CLASS = "spanNoEdit-bold";
	public final static String INPUT_TEXT_BOLD_RED_CSS_CLASS = "spanNoEdit-bold-red";
	public final static String INPUT_TEXT_DEFAULT_CSS_CLASS_FONT_SIZE_16 = "spanNoEdit16";	
	
	public final static String LABEL_TITOLO_SEZIONE_DEFAULT = "Filtri di Ricerca";
	
	public final static String TOOLTIP_VISUALIZZA_FILTRI_RICERCA = "Visualizza Filtri di Ricerca";
	public final static String TOOLTIP_NASCONDI_FILTRI_RICERCA = "Nascondi Filtri di Ricerca";
	public final static String TOOLTIP_VISUALIZZA_FIELDSET = "Visualizza Sezione";
	public final static String TOOLTIP_NASCONDI_FIELDSET = "Nascondi Sezione";
	public final static String TOOLTIP_VISUALIZZA_SUBTITLE = "Visualizza Sezione";
	public final static String TOOLTIP_NASCONDI_SUBTITLE = "Nascondi Sezione";
	public final static String TOOLTIP_ICONA_COPIA = "Copia {0}";
	public final static String TOOLTIP_ELIMINA_FILE = "Rimuovi {0}";
	
	public final static String TOOLTIP_VISUALIZZA_SEZIONE_FILTRI_RICERCA = "Visualizza Sezione";
	public final static String TOOLTIP_NASCONDI_SEZIONE_FILTRI_RICERCA = "Nascondi Sezione";
	
	public final static String ICON_VISUALIZZA_SEZIONE_FILTRI_RICERCA = "expand_more";
	public final static String ICON_NASCONDI_SEZIONE_FILTRI_RICERCA = "expand_less";
	public final static String ICON_VISUALIZZA__FIELDSET = "expand_more";
	public final static String ICON_NASCONDI__FIELDSET = "expand_less";
	public final static String ICON_VISUALIZZA_SUBTITLE = "expand_more";
	public final static String ICON_NASCONDI_SUBTITLE = "expand_less";
	
	public final static String ICON_ELIMINA_FILE = "delete";
	
	public final static String INFO_BUTTON_ICON = "&#xE88E;";
	public final static String INFO_BUTTON_ICON_WHITE = "&#xE88F;";
	public final static String ICON_DIALOG_HEADER = "&#xE8B2;";
	public final static String ICON_COPY = "file_copy";
	public final static String ICON_USO = "find_in_page";//"location_searching";
	public final static String ICON_USO_INFO = INFO_BUTTON_ICON; //"info";
	
	public final static String ICONA_RESET_CACHE_ELEMENTO = "&#xE863;";
	public final static String ICONA_RESET_CACHE_ELEMENTO_TOOLTIP = "Rimuovi dalla cache";
	public final static String ICONA_RESET_CACHE_ELEMENTO_TOOLTIP_CON_PARAMETRO = "Rimuovi {0} dalla cache";
	
	public final static String ICONA_VERIFICA = "&#xE8BE;";
	public final static String ICONA_VERIFICA_TOOLTIP = "Verifica";
	public final static String ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO = "Verifica {0}";

	public final static String ICONA_VERIFICA_CONNETTIVITA_TOOLTIP = "Verifica connettività";
	public final static String ICONA_VERIFICA_CONNETTIVITA_TOOLTIP_CON_PARAMETRO = "Verifica connettività {0}";
	
	public final static String ICONA_VERIFICA_CERTIFICATI = "&#xEA17;";
	public final static String ICONA_VERIFICA_CERTIFICATI_TOOLTIP = "Verifica certificati";
	public final static String ICONA_VERIFICA_CERTIFICATI_TOOLTIP_CON_PARAMETRO = "Verifica certificati {0}";
	
	public final static String ICONA_VISUALIZZA_RUNTIME_ALLARME = Costanti.ICON_USO;
	public final static String ICONA_VISUALIZZA_RUNTIME_ALLARME_TOOLTIP = "Visualizza runtime";
	
	public final static String ICONA_AGGIORNA_RICERCA = "&#xE863;";
	public final static String ICONA_AGGIORNA_RICERCA_TOOLTIP = "Aggiorna Ricerca";
	
	public final static String ICONA_MENU_AZIONI_BUTTON = "&#xE5D4;";
	public final static String ICONA_MENU_AZIONI_BUTTON_TOOLTIP = ""; // "Visualizza azioni disponibili";
	
	public static final int LUNGHEZZA_LABEL_TABS = 30;
	public static final int LUNGHEZZA_RIGA_TESTO_TABELLA = 150;
	
	// Indica il numero delle possibili classi CSS per i tag dei gruppi, modificare questo valore se si vuole modificare il numero delle classi disponibili
	public final static Integer NUMERO_GRUPPI_CSS = 30;
	
	public final static String SA_TIPO_DEFAULT_VALUE = "clientORserver";	
	
}
