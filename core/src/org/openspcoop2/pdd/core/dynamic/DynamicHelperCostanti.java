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

package org.openspcoop2.pdd.core.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * HelperCostanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicHelperCostanti {

	
	// ****** TEMPLATE VARI (TRASFORMAZIONI)
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO = "Il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway.<br/>Le espressioni utilizzabili sono:";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TEMPLATE = "Il template fornito può contenere parti dinamiche risolte a runtime dal Gateway.<br/>Le espressioni utilizzabili sono:";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI = "È possiile accedere ai seguenti oggetti resi disponibili a runtime dal Gateway:";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER = "Il file fornito deve rispettare la sintassi del template engine 'Freemarker'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER_ZIP = "Il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Freemarker'.<br/>"+
			"GovWay richiede la presenza, all'interno dell'archivio zip, di un file indice che deve possedere il nome '"+org.openspcoop2.pdd.core.dynamic.Costanti.ZIP_INDEX_ENTRY_FREEMARKER+"'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY = "Il file fornito deve rispettare la sintassi del template engine 'Velocity'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY_ZIP = "Il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Velocity'.<br/>"+
			"GovWay richiede la presenza, all'interno dell'archivio zip, di un file indice che deve possedere il nome '"+org.openspcoop2.pdd.core.dynamic.Costanti.ZIP_INDEX_ENTRY_VELOCITY+"'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
		
	public final static String LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS = "Il file fornito deve contenere proprietà indicate come nome=valore in ogni linea.<BR/>"+
		"Il nome della proprietà corrisponde all'entry name all'interno dell'archivio (es. dir/subDir/entryName1).<BR/>"+
		"Il valore della proprietà corrisponde al contenuto dell'entry.<BR/>"+
		org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
		"È possibile selezionare parti del messaggio, per associarle come contenuto dell'entry, utilizzando le seguenti espressioni dinamiche risolte a runtime dal Gateway:";
		
	
	// ****** CONTENT-TYPE (TRASFORMAZIONI, MODI)
	
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE = "Lista di Content-Type per i quali la regola di trasformazione verrà utilizzata; di seguito i formati utilizzabili:";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_EMPTY = "<b>empty</b>: valore speciale che rappresenta una richiesta senza Content-Type"; 
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_1 = "<b>type/subtype</b>: indicazione puntuale di un Content-Type"; 
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_2 = "<b>type/*</b>: hanno un match tutti i Content-Type appartenenti al tipo indicato"; 
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_3 = "<b>*/*+xml</b>: hanno un match tutti i Content-Type che terminano con '+xml'";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_4 = "<b>regexpType/regexpSubType</b>: hanno un match tutti i Content-Type che soddisfano le espressioni regolari indicate"; 
	public final static List<String> LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_1);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_2);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_3);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_4);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_EMPTY);
	}
	
	
	
	
	// ****** TRASFORMAZIONI (TEMPLATE VELOCITY, TEMPLATE FREEMARKER)
	// ******** LOAD BALANCER (CONDIZIONE TEMPLATE VELOCITY o FREEMARKER per sticky)
	// ******** CONNETTORI MULTIPLI CON CONSEGNA CONDIZIONALE (TEMPLATE VELOCITY o FREEMARKER)
	// ******** CONNETTORI MULTIPLI CON CONSEGNA CONDIZIONALE, regole specifiche (TEMPLATE VELOCITY o FREEMARKER)
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TRANSACTION_ID_OBJECT+"</b>: identificativo UUID della transazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TRANSACTION_ID+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+"</b>: data di elaborazione del messaggio ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DATE_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+"</b>: headers http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER_VALUES = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+"</b>: headers http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_VALUES_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+"</b>: parametri della url ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_QUERY_PARAMETER_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY_VALUES = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+"</b>: parametri della url ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_QUERY_PARAMETER_VALUES_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+"</b>: parametri della form ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_FORM_PARAMETER_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM_VALUES = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+"</b>: parametri della form ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_FORM_PARAMETER_VALUES_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+"</b>: informazioni generiche del profilo ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+"</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_PROPERTY_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni da messaggi XML tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni da messaggi JSON tramite espressioni JSONPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_JSON_PATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_URL = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dalla url tramite espressioni regolari ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_URL_REGEXP+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_REQUEST = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUEST+"</b>: permette di accedere al contenuto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE+")";
	//public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CTX_OBJECT+"</b>: permette di accedere al contesto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CTX_OBJECT_HTML_ESCAPED+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CTX_OBJECT+"</b>: permette di accedere al contesto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CTX_OBJECT_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+"</b>: permette di accedere ai claims di un token ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+"</b>: permette di accedere agli attributi recuperati tramite Attribute Authority ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ATTRIBUTES+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+"</b>: permette di accedere ai dati della richiesta http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_API_IMPL_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate per l'API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_API_IMPL_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nell'applicativo client ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nel soggetto fruitore("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nel soggetto erogatore ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTACHMENTS = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTACHMENTS_OBJECT+"</b>: consente di ottenere gli allegati registrati sull'API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_ATTACHMENTS_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SYSTEM = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+"</b>: permette di accedere alle proprietà di sistema indicate nella configurazione generale ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_SYSTEM_PROPERTY+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+"</b>: permette di accedere alle variabili di sistema ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_ENV_PROPERTY+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_JAVA = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+"</b>: permette di accedere alle variabili della jvm ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_JAVA_PROPERTY+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ERROR_HANDLER_OBJECT+"</b>: permette di generare risposte personalizzate che segnalano l'impossibilità di proseguire la trasformazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ERROR_HANDLER_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_LOAD_STATIC+"</b>: permette di definire classi (es. class[\"org.apache.commons.lang.StringUtils\"] ).");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_NEW_INSTANCE+"</b>: permette di istanziare una classe (es. new(\"java.lang.StringBuilder\",\"Commento Iniziale\") ).");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_LOAD_STATIC+"</b>: permette di definire classi (es. class.forName(\"org.apache.commons.lang.StringUtils\") ).");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_NEW_INSTANCE+"</b>: permette di istanziare una classe (es. new.instance(\"java.lang.StringBuilder\",\"Commento Iniziale\") ).");
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_DATA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: data di elaborazione del messaggio di risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DATE_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: headers http della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER_VALUES ="<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_RESPONSE_VALUES+"</b>: headers http della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_VALUES_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio di risposta tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio XML di risposta tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio JSON di risposta tramite espressioni JSONPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_JSON_PATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_RESPONSE+"</b>: permette di accedere al contenuto della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE+")";
	
	
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_URL);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTACHMENTS);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_REQUEST);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JAVA);
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH); // elimino json path
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_URL);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_DATA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTACHMENTS);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JAVA);
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP); // elimino xpath su soap per la risposta
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH); // elimino xpath su xml per la risposta
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH); // elimino json path
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH); // elimino json path per la risposta
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}

	
	
	
	
	// ******** TRASFORMAZIONI (HEADER HTTP, PARAMETRI DELLA URL, TRASFORMAZIONE-REST, TEMPLATE GOVWAY)
	// ******** CONNETTORE FILE (requestFile, requestHeaderFile, responseFile, responseHeaderFile)
	// ******** LOAD BALANCER (CONDIZIONE TEMPLATE GOVWAY per sticky)
	// ******** CONNETTORI MULTIPLI CON CONSEGNA CONDIZIONALE (TEMPLATE GOVWAY)
	// ******** CONNETTORI MULTIPLI CON CONSEGNA CONDIZIONALE, regole specifiche (TEMPLATE GOVWAY)
	// ******** MODI CORNICE SICUREZZA
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TRANSACTION_ID_VALUE+"}</b>: identificativo UUID della transazione";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+":FORMAT}</b>: data di elaborazione del messaggio; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+":yyyyMMdd_HHmmssSSS})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+":NAME}</b>: valori presenti negli header http che possiedono il nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della url con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+":NAME}</b>: valore associato al parametro della form con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della form con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":FIELD}</b>: permette di utilizzare informazioni generiche del profilo; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+"' (es. per il mittente usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":mittente})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+":NAME}</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia (es. identificativo SDI). Il valore 'NAME' indica il nome della proprietà da utilizzare";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":FIELD}</b>: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+"' (es. per ottenere il valore del claim 'sub' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":sub})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":FIELD}</b>: permette di accedere agli attributi recuperati tramite Attribute Authority; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ATTRIBUTES+"' (es. per ottenere il valore dell'attributo 'attr1' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[attr1]}, se configurata solamente 1 A.A., altrimenti usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[nomeAttributeAuthority][attr1]} )";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_API_IMPL_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'API, con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo, con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto fruitore, con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto erogatore, con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SYSTEM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+":NAME}</b>: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile java con nome 'NAME'";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_DATA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":FORMAT}</b>: data di elaborazione del messaggio di risposta; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":yyyyMMdd_HHmmssSSS})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":NAME}</b>: valore presente nell'header http della risposta che possiede il nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_RESPONSE_VALUES+":NAME}</b>: valori presenti negli header http della risposta che possiedono il nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione XPath applicata sulla risposta"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione XPath applicata su una risposta XML"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione JSONPath applicata su una risposta JSON"; 
	
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_ALL_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JAVA);
	}
	// NOTA: usato anche in ModIConsoleCostanti
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
	}
	// NOTA: usato anche in ModIConsoleCostanti
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH); // elimino json path
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM); // elimino form
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH); // elimino json path
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL); // elimino espressione regolare
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES); // elimino header values
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES); // elimino url values
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES); // elimino form values
	}

	public final static List<String> LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_DATA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JAVA);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP); // elimino xpath su soap per la risposta
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH); // elimino xpath su xml per la risposta
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH); // elimino json path
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH); // elimino json path per la risposta
	}
	
	
	
	
	
	// ********* COMPRESS (TRASFORMAZIONI)
	
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_CONTENT+"}</b>: payload della richiesta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_CONTENT+"}</b>: payload della risposta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_PREFIX+"index"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una richiesta multipart alla posizione indicata dall'intero 'index'";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_PREFIX+"index"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una risposta multipart alla posizione indicata dall'intero 'index'";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_BY_ID_PREFIX+"id"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una richiesta multipart che possiede il "+HttpConstants.CONTENT_ID+" indicato";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_BY_ID_PREFIX+"id"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una risposta multipart che possiede il "+HttpConstants.CONTENT_ID+" indicato";
	
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_ENVELOPE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ENVELOPE+"}</b>: soap envelope della richiesta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_ENVELOPE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ENVELOPE+"}</b>: soap envelope della risposta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_BODY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_BODY+"}</b>: contenuto del soap body della richiesta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_BODY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_BODY+"}</b>: contenuto del soap body della risposta";
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_ENVELOPE);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_BODY);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_ENVELOPE);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_BODY);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
	}
	
	
	
	
	
	
	// ******* CONTROLLO DEGLI ACCESSI - AUTORIZZAZIONE TOKEN CLAIMS
	
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_NOTE = "Indicare per riga i claims richiesti (nome=valore); visualizzare 'info' per maggiori dettagli";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX = "Indicare per riga i claims richiesti nel token nella forma 'nome=valore'.<br/><br/>";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ATTRIBUTE_PREFIX = "Per verificare un attributo indicarlo con il prefisso 'attribute.' nella forma 'attribute.nome=valore'.<br/><br/>";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_AA_ATTRIBUTE_PREFIX = "Per verificare un attributo prelevato da un authority indicarlo con i prefissi 'aa.' e 'attribute.' nella forma 'aa.nomeAuthority.attribute.nomeAttributo=valore'.<br/><br/>";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX =
			"Ogni valore può essere fornito in una delle seguenti modalità:<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_ANY_VALUE+"</b> : indica qualsiasi valore non nullo<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_UNDEFINED+"</b> : la risorsa indicata non deve esistere o non deve essere valorizzata<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : la regola è soddisfatta se l'intero valore del claim ha un match rispetto all'espressione regolare EXPR indicata<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore del claim<br/>"+
			"- <b>valore</b> : indica esattamente il valore (case sensitive) che deve possedere il claim; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito<br/>"+
			"- <b>valore1,..,valoreN</b> : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche<br/>"+
			"<br/>Le espressioni utilizzabili come parti dinamiche, risolte a runtime dal gateway, sono:";
		
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_NO_ATTRIBUTE_AUTHORITY = LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX + 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX;
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SINGLE_ATTRIBUTE_AUTHORITY = LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX+
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ATTRIBUTE_PREFIX + 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX;
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_MULTI_ATTRIBUTE_AUTHORITY = LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX+ 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_AA_ATTRIBUTE_PREFIX + 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX;
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+":NAME}</b>: valori presenti negli header http che possiedono il nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della url con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+":NAME}</b>: valore associato al parametro della form con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della form con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata sul messaggio"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url di invocazione"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_API_IMPL_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'API, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto fruitore, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto erogatore, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SYSTEM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+":NAME}</b>: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile java con nome 'NAME'";
		
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JSONPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JAVA);
	}
	
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH_SOAP);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JAVA);
	}
	
	
	
	
	// ******* CONTROLLO DEGLI ACCESSI - AUTORIZZAZIONE CONTENUTI
	
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CONTENUTI_NOTE = "Indicare per riga i controlli richiesti (risorsa=valore); visualizzare 'info' per maggiori dettagli";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI= "Indicare per riga i controlli di autorizzazione richiesti nella forma (risorsa=valore).<br/><br/>"+
			"Una risorsa identifica un header, una parte dell'url o del messaggio, un claim del token o un principal etc.<br/>"+
			"Per identificare una risorsa sono utilizzabili le espressioni dinamiche descritte nell'ultima parte di questo box informazioni.<br/><br/>"+
			"Ogni valore può essere fornito in una delle seguenti modalità:<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_ANY_VALUE+"</b> : indica qualsiasi valore non nullo<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_UNDEFINED+"</b> : la risorsa indicata non deve esistere o non deve essere valorizzata<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : la regola è soddisfatta se il valore della risorsa ha un match completo rispetto all'espressione regolare EXPR indicata<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore della risorsa<br/>"+
			"- <b>valore</b> : indica esattamente il valore (case sensitive) che deve possedere la risorsa; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito<br/>"+
			"- <b>valore1,..,valoreN</b> : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche<br/>"+
			"<br/><b>Identificazione delle risorse tramite espressioni dinamiche</b><br/><br/>Le espressioni utilizzabili, risolte a runtime dal gateway, sono:";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+":NAME}</b>: valori presenti negli header http che possiedono il nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della url con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+":NAME}</b>: valore associato al parametro della form con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della form con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata sul messaggio"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url di invocazione"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":FIELD}</b>: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+"' (es. per ottenere il valore del claim 'sub' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":sub})";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_ATTRIBUTES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":FIELD}</b>: permette di accedere agli attributi recuperati tramite Attribute Authority; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ATTRIBUTES+"' (es. per ottenere il valore dell'attributo 'attr1' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[attr1]}, se configurata solamente 1 A.A., altrimenti usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[nomeAttributeAuthority][attr1]} )";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_API_IMPL_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'API, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto fruitore, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto erogatore, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SYSTEM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+":NAME}</b>: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile java con nome 'NAME'";
		
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_JSONPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_ATTRIBUTES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_JAVA);
	}
	
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH_SOAP);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_ATTRIBUTES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_JAVA);
	}
	
	
	
	
	
	
	// ******* ATTRIBUTE AUTHORITY (TEMPLATE VELOCITY, TEMPLATE FREEMARKER)
	
	public final static String LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI_REQUIRED_ATTRIBUTE = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+"</b>: "+
			"permette di conoscere gli attributi che devono essere richiesti, indicati nella configurazione specifica di una erogazione o fruizione di API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_REQUIRED_ATTRIBUTES+")"); 
	
	public final static List<String> LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI.add(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI_REQUIRED_ATTRIBUTE);
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES); // elimino attributes 
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER); // elimino error handler
	}
	
	// ******* ATTRIBUTE AUTHORITY (TEMPLATE e ALTRE VOCI)
		
	public final static String LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_REQUIRED_ATTRIBUTE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+":METHOD}</b>: "+
			"permette di conoscere gli attributi che devono essere richiesti, indicati nella configurazione specifica di una erogazione o fruizione di API; il valore 'METHOD' fornito deve rappresentare un metodo valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_REQUIRED_ATTRIBUTES+"'"+
			" (es. per ottenere la lista degli attributi in un formato utilizzabile all'interno di un array json usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+":jsonList()} oppure ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+":formatList(&quot;,&quot;)})";
	
	public final static List<String> LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES); // elimino attributes 
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES.add(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_REQUIRED_ATTRIBUTE);
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES.addAll(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI);
	}
	
	
	
	
	
	
	// ******* NEGOZIAZIONE TOKEN
	
	public final static String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_ISSUER = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il claim 'iss' verrà valorizzato con il nome del soggetto associato al dominio di gestione della richiesta";
	public final static String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_SUBJECT = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il claim 'sub' verrà valorizzato con il medesimo valore associato al Client ID";
	public final static String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_IDENTIFIER = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il claim 'jti' verrà valorizzato con un UUID generato da GovWay";
	public final static String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_FORM_PARAMETRO_CLIENT_ID = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il parametro verrà valorizzato con il medesimo valore associato al Client ID definito nel payload del JWT";
	public final static String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_CLAIMS = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+
			"<BR/><BR/>"+StringEscapeUtils.escapeHtml("Fornendo un valore che inizia e termina con le parentesi graffe si definisce un oggetto json, come ad esempio:<BR/> claimTest={\"prova\":\"valoreProva\", \"prova2\":\"${header:X-Example}\"}")+
			"<BR/><BR/>"+StringEscapeUtils.escapeHtml("Se il valore inizia e termina con le parentesi quadre si definisce invece un array json, come ad esempio:<BR/> claimTest=[\"valoreProva\", \"valoreProva2\", \"${header:X-Example}\"]");
	
	public final static String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_VALORE_NON_DEFINITO = "<b>"+org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED+"</b>: indica di non generare il claim";
	
	public final static List<String> LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO.add(LABEL_CONFIGURAZIONE_NEGOZIAZIONE_VALORE_NON_DEFINITO);
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
	}
	
	
	
	
	
	
	
	// ******* MODI - REST EROGAZIONE/FRUIZIONE CLAIMS
	
	public final static String NOT_GENERATE = "${notGenerate}";
	
	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE = "Indicare per riga i claims (nome=valore); visualizzare 'info' per maggiori dettagli";

	private final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_PREFIX= 
			"Indicare per riga i claims da aggiungere nel payload del JWT, nella forma 'nome=valore'.<br/><br/>";
	private final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS_TEMPLATE = "CLAIMS";
	private final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS = 
			"<b>Attenzione</b>: non è consentito indicare i seguenti claims: "+LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS_TEMPLATE+"<br/><br/>";
	public final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_NOT_GENERATED = 
			"È possibile utilizzare la keyword '"+NOT_GENERATE+"' come valore dei claims 'iss', 'sub' o 'client_id' per non far generare il claim all'interno del jwt payload.<br/><br/>";
	public final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_REQUEST = 
			"Se vengono indicati i claims 'iss' o 'sub' i valori forniti sovrascriveranno le impostazioni di default del prodotto.<br/>"+
				LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_NOT_GENERATED;
	public final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_RESPONSE = 
			"Se vengono indicati i claims 'iss' o 'sub' o 'aud' o 'client_id' i valori forniti sovrascriveranno le impostazioni di default del prodotto.<br/>"+
				LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_NOT_GENERATED;
	private final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_SUFFIX = 
			"Il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway.<br/>Le espressioni utilizzabili sono:"; // usato anche sotto per audience
	
	public final static String getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(boolean request, boolean corniceSicurezza, List<String> denyClaims) {
		StringBuilder sb = new StringBuilder(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_PREFIX);
		sb.append(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS.
				replace(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS_TEMPLATE, denyClaims.toString()));
		//if(!corniceSicurezza) {
		sb.append(request ? LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_REQUEST :
			LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_RESPONSE);
		//}
		sb.append(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_SUFFIX);
		return sb.toString();
	}
	
	public final static List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public final static List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	
	
	// ******* MODI - REST EROGAZIONE/FRUIZIONE AUDIENCE

	public final static String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INFO = LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_SUFFIX;
	
	public final static List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_REQUEST = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_REQUEST.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public final static List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_REQUEST = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_REQUEST.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
	}
	
	public final static List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_RESPONSE = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_RESPONSE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	public final static List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_RESPONSE = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_RESPONSE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
	}
	
	
	
	
	
	// ****** UTILITIES
	
	private static void deleteField(List<String> list, String field) {
		if(list!=null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				String v = list.get(i);
				if(v.equals(field)) {
					list.remove(i);
					break;
				}
			}
		}
	}
	@SuppressWarnings("unused")
	private static void replaceField(List<String> list, String field, String newField) {
		if(list!=null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				String v = list.get(i);
				if(v.equals(field)) {
					list.remove(i);
					list.add(i, newField);
					break;
				}
			}
		}
	}
}
