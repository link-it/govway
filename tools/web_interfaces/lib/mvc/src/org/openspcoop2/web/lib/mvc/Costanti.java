/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
	public final static String PARAMETER_NAME_MSG_ERROR_EXPORT = "errorExport";
	
	/** PARAMETER ATTRIBUTE */
	
	public final static String REQUEST_ATTIBUTE_PARAMS = "params";
	
	
	/** SEARCH */
	
	public final static String SEARCH_ENABLED ="on";
	public final static String SEARCH_DISABLED ="off";
	
	public final static String SEARCH_PAGE_SIZE = "pageSize";
	public final static String SEARCH_INDEX = "index";
	public final static String SEARCH = "search";
	
	
	/** POSTBACK FUNCTION */
	
	public final static String POSTBACK_ELEMENT_NAME = "postBackElementName"; //nome dell'element che ha scaturito il postBack
	public final static String POSTBACK_FUNCTION = "postBack()";
	public final static String POSTBACK_FUNCTION_WITH_PARAMETER_START = "postBack('";
	public final static String POSTBACK_FUNCTION_WITH_PARAMETER_END = "')";
	public final static String POSTBACK_VIA_POST_FUNCTION_PREFIX = "postVersion_";
	
	
	/** CHECK BOX */
	
	public final static String CHECK_BOX_ENABLED = "yes";
	public final static String CHECK_BOX_ENABLED_TRUE = "true";
	public final static String CHECK_BOX_ENABLED_ABILITATO = "abilitato";
	public final static String CHECK_BOX_DISABLED = "no";
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
	
	protected final static String SESSION_ATTRIBUTE_LOGIN = "Login";
	protected final static String SESSION_ATTRIBUTE_USER = "User";
	protected final static String SESSION_ATTRIBUTE_GENERAL_DATA = "GeneralData";
	protected final static String SESSION_ATTRIBUTE_PAGE_DATA = "PageData";
	public final static String SESSION_ATTRIBUTE_RICERCA = "Ricerca";
	protected final static String SESSION_ATTRIBUTE_CONTA_LISTE = "contaListe";
	protected final static String SESSION_ATTRIBUTE_LIST_ELEMENT ="ListElement";
	protected final static String SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED = org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED;
	protected final static String SESSION_ATTRIBUTE_VALUE_SOGGETTO_VIRTUALE_YES ="yes";
	protected final static String SESSION_ATTRIBUTE_GESTIONE_WSBL ="GestioneWSBL";
	protected final static String SESSION_ATTRIBUTE_CONFIGURAZIONI_PERSONALIZZATE ="ConfigurazioniPersonalizzate";
	public final static String SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX = "oldConfProp_";
	
	/** COOKIES **/
	
	public static final String COOKIE_NAME_JSESSIONID = "JSESSIONID";
	
	
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
	
	/** PARAMETRI MESSAGE PAGE **/
	
	public static final String PARAMETER_MESSAGE_TEXT ="mpText";
	public static final String PARAMETER_MESSAGE_TITLE ="mpTitle";
	public static final String PARAMETER_MESSAGE_TYPE ="mpType";
	public static final String PARAMETER_MESSAGE_BREADCRUMB ="mpBC";
	
	/** PAGE DATA TITLE */
	
	public final static MessageType MESSAGE_TYPE_ERROR = MessageType.ERROR;
	public final static MessageType MESSAGE_TYPE_ERROR_SINTETICO = MessageType.ERROR_SINTETICO;
	public final static MessageType MESSAGE_TYPE_INFO = MessageType.INFO;
	public final static MessageType MESSAGE_TYPE_INFO_SINTETICO = MessageType.INFO_SINTETICO;
	public final static MessageType MESSAGE_TYPE_CONFIRM = MessageType.CONFIRM;
	
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
	
	public final static String LABEL_TITOLO_SEZIONE_DEFAULT = "Filtri di Ricerca";
	
	public final static String TOOLTIP_VISUALIZZA_FILTRI_RICERCA = "Visualizza Filtri di Ricerca";
	public final static String TOOLTIP_NASCONDI_FILTRI_RICERCA = "Nascondi Filtri di Ricerca";
	public final static String TOOLTIP_ICONA_COPIA = "Copia {0}";
	
	public final static String INFO_BUTTON_ICON = "&#xE88E;";
	public final static String INFO_BUTTON_ICON_WHITE = "&#xE88F;";
	public final static String ICON_DIALOG_HEADER = "&#xE8B2;";
	public final static String ICON_COPY = "file_copy";
	
	public static final int LUNGHEZZA_LABEL_TABS = 30;
	public static final int LUNGHEZZA_RIGA_TESTO_TABELLA = 150;
	
	// Indica il numero delle possibili classi CSS per i tag dei gruppi, modificare questo valore se si vuole modificare il numero delle classi disponibili
	public final static Integer NUMERO_GRUPPI_CSS = 30;
	
	public final static String SA_TIPO_DEFAULT_VALUE = "clientORserver";	
}
