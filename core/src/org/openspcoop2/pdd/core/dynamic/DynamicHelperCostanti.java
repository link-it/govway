/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
	
	private DynamicHelperCostanti() {}

	private static final String DOPPIO_A_CAPO_HTML = "<BR/><BR/>";
	
	
	// ****** TEMPLATE VARI (TRASFORMAZIONI)
	
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PREFIX = "Il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway.";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SUFFIX = "Le espressioni utilizzabili sono:";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO = LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PREFIX+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SUFFIX;
	
	public static final String LABEL_CONFIGURAZIONE_INFO_TEMPLATE = "Il template fornito può contenere parti dinamiche risolte a runtime dal Gateway.<br/>Le espressioni utilizzabili sono:";
	
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI = "È possiile accedere ai seguenti oggetti resi disponibili a runtime dal Gateway:";
	
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER = "Il file fornito deve rispettare la sintassi del template engine 'Freemarker'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER_ZIP = "Il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Freemarker'.<br/>"+
			"GovWay richiede la presenza, all'interno dell'archivio zip, di un file indice che deve possedere il nome '"+org.openspcoop2.pdd.core.dynamic.Costanti.ZIP_INDEX_ENTRY_FREEMARKER+"'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY = "Il file fornito deve rispettare la sintassi del template engine 'Velocity'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY_ZIP = "Il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Velocity'.<br/>"+
			"GovWay richiede la presenza, all'interno dell'archivio zip, di un file indice che deve possedere il nome '"+org.openspcoop2.pdd.core.dynamic.Costanti.ZIP_INDEX_ENTRY_VELOCITY+"'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
		
	public static final String LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS = "Il file fornito deve contenere proprietà indicate come nome=valore in ogni linea.<BR/>"+
		"Il nome della proprietà corrisponde all'entry name all'interno dell'archivio (es. dir/subDir/entryName1).<BR/>"+
		"Il valore della proprietà corrisponde al contenuto dell'entry.<BR/>"+
		org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
		"È possibile selezionare parti del messaggio, per associarle come contenuto dell'entry, utilizzando le seguenti espressioni dinamiche risolte a runtime dal Gateway:";

	public static final String LABEL_CONFIGURAZIONE_CLAIMS = LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PREFIX+
			DOPPIO_A_CAPO_HTML+StringEscapeUtils.escapeHtml("Un valore definito come parte dinamica tramite la sintassi ${..}, la cui risoluzione non è possibile a runtime, comporta un fallimento della richiesta in corso. Per una aggiunta del claim condizionata al riuscimento della risoluzione dinamica, senza far terminare la richiesta in errore in caso di mancata risoluzione, è possibile usare la forma '?{..}'.")+
			DOPPIO_A_CAPO_HTML+StringEscapeUtils.escapeHtml("Fornendo un valore che inizia e termina con le parentesi graffe si definisce un oggetto json, come ad esempio:<BR/> claimTest={\"prova\":\"valoreProva\", \"prova2\":\"${header:X-Example}\"}")+
			DOPPIO_A_CAPO_HTML+StringEscapeUtils.escapeHtml("Se il valore inizia e termina con le parentesi quadre si definisce invece un array json, come ad esempio:<BR/> claimTest=[\"valoreProva\", \"valoreProva2\", \"${header:X-Example}\"]")+
			DOPPIO_A_CAPO_HTML+StringEscapeUtils.escapeHtml("Per definire tipi primitivi json (boolean,int,long,float,double) è necessario attuare un cast come ad esempio:<BR/> claimTest=cast(true as boolean) oppure claimTest=cast(${header:X-Example} as long)")+
	        DOPPIO_A_CAPO_HTML+StringEscapeUtils.escapeHtml("Per convertire una lista json di tipi primitivi in lista di stringhe è possibile attuare un cast come ad esempio:<BR/> claimTest=cast([1,2,3] as string array)")+
	        DOPPIO_A_CAPO_HTML+LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SUFFIX;
	
	public static final String LABEL_CONFIGURAZIONE_PURPOSE_ID = StringEscapeUtils.escapeHtml("Il purposeId può essere configurato attraverso differenti modalità.")+
			DOPPIO_A_CAPO_HTML+
			StringEscapeUtils.escapeHtml("Staticamente nella token policy dove viene indicato direttamente il valore; questa modalità richiede la registrazione di una token policy per ogni finalità.")+
			DOPPIO_A_CAPO_HTML+
			StringEscapeUtils.escapeHtml("Dinamicamente indicando nella token policy un valore che indirizza proprietà degli oggetti del registro consentento di supportare differenti scenari:")+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
			StringEscapeUtils.escapeHtml("- 1-1 con la fruizione: utilizzando il valore '${config:purposeId}' viene richiesta la registrazione della proprietà 'purposeId' nella fruizione dove viene utilizzata la policy.")+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
			StringEscapeUtils.escapeHtml("- 1-1 con l'applicativo fruitore: utilizzando il valore '${clientApplicationConfig}' viene richiesta la registrazione della proprietà 'purposeId' nell'applicativo fruitore.")+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
			StringEscapeUtils.escapeHtml("- N applicativi fruitore censiti sulla fruzione: utilizzando il valore '${dynamicConfig:apiSearchByClientApplication(purposeId)}' è possibile registrare molteplici proprietà '&lt;clientApplicationName&gt;.purposeId' sulla fruizione, una per ogni applicativo fruitore il cui nome va indicato come prefisso della proprietà stessa. È possibile utilizzare la proprietà senza prefisso come finalità di default.")+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
			StringEscapeUtils.escapeHtml("- N fruizioni censite sull’applicativo fruitore: utilizzando il valore '${dynamicConfig:clientApplicationSearch(purposeId)}' è possibile registrare molteplici proprietà '&lt;nomeApiImpl&gt;.v&lt;nomeApiImpl&gt;.purposeId' sull’applicativo, una per ogni fruizione di API che l’applicativo fruisce indicando il nome come prefisso della proprietà. È possibile utilizzare la proprietà senza prefisso come finalità di default.")+
			DOPPIO_A_CAPO_HTML+
			StringEscapeUtils.escapeHtml("Contenere parti dinamiche risolte a runtime dal Gateway.")+LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SUFFIX;
			
	
	// ****** CONTENT-TYPE (TRASFORMAZIONI, MODI)
	
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE = "Lista di Content-Type per i quali la regola di trasformazione verrà utilizzata; di seguito i formati utilizzabili:";
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_EMPTY = "<b>empty</b>: valore speciale che rappresenta una richiesta senza Content-Type"; 
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_1 = "<b>type/subtype</b>: indicazione puntuale di un Content-Type"; 
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_2 = "<b>type/*</b>: hanno un match tutti i Content-Type appartenenti al tipo indicato"; 
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_3 = "<b>*/*+xml</b>: hanno un match tutti i Content-Type che terminano con '+xml'";
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_4 = "<b>regexpType/regexpSubType</b>: hanno un match tutti i Content-Type che soddisfano le espressioni regolari indicate"; 
	public static final List<String> LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI = new ArrayList<>();
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
	
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TRANSACTION_ID_OBJECT+"</b>: identificativo UUID della transazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TRANSACTION_ID+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+"</b>: data di elaborazione del messaggio ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DATE_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+"</b>: headers http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER_VALUES = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+"</b>: headers http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_VALUES_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+"</b>: parametri della url ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_QUERY_PARAMETER_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY_VALUES = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+"</b>: parametri della url ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_QUERY_PARAMETER_VALUES_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+"</b>: parametri della form ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_FORM_PARAMETER_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM_VALUES = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+"</b>: parametri della form ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_FORM_PARAMETER_VALUES_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+"</b>: informazioni generiche del profilo ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+"</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni da messaggi XML tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni da messaggi JSON tramite espressioni JSONPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_JSON_PATH+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_URL = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dalla url tramite espressioni regolari ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_URL_REGEXP+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_REQUEST = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUEST+"</b>: permette di accedere al contenuto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE_CONTENT+")";
	/** public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CTX_OBJECT+"</b>: permette di accedere al contesto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CTX_OBJECT_HTML_ESCAPED+")"); */
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CTX_OBJECT+"</b>: permette di accedere al contesto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CTX_OBJECT_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_INTEGRATION = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+"</b>: permette di accedere ai claims di un token di integrazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_INTEGRATION+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+"</b>: permette di accedere ai claims di un token ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_CLIENT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+"</b>: identità dell'applicativo client identificato tramite il clientId presente nel token ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_TOKEN+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+"</b>: permette di accedere agli attributi recuperati tramite Attribute Authority ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ATTRIBUTES+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+"</b>: permette di accedere ai dati della richiesta http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+"</b>: permette di accedere alle informazioni relative ai certificati ed ai security token presenti nella richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SECURITY_TOKEN+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_API_IMPL_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate per l'API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_API_IMPL_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nell'applicativo client ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nel soggetto fruitore("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nel soggetto erogatore ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nell'applicativo client identificato tramite il clientId presente nel token ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_DYNAMIC_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DYNAMIC_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà degli attori coinvolti nella richiesta: api, applicativi, soggetti ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DYNAMIC_CONFIG_PROPERTY+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTACHMENTS = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTACHMENTS_OBJECT+"</b>: consente di ottenere gli allegati registrati sull'API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_ATTACHMENTS_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_SYSTEM = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+"</b>: permette di accedere alle proprietà di sistema indicate nella configurazione generale ("+org.openspcoop2.pdd.core.dynamic.Costanti.getTypeSystemProperty()+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+"</b>: permette di accedere alle variabili di sistema ("+org.openspcoop2.pdd.core.dynamic.Costanti.getTypeEnvProperty()+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_JAVA = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+"</b>: permette di accedere alle variabili della jvm ("+org.openspcoop2.pdd.core.dynamic.Costanti.getTypeJavaProperty()+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV_JAVA = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_JAVA_PROPERTY+"</b>: permette di recuperare una variabile cercandola prima come variabile di sistema e se non presente come variabile della jvm ("+org.openspcoop2.pdd.core.dynamic.Costanti.getTypeEnvJavaProperty()+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ERROR_HANDLER_OBJECT+"</b>: permette di generare risposte personalizzate che segnalano l'impossibilità di proseguire la trasformazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ERROR_HANDLER_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_LOAD_STATIC+"</b>: permette di definire classi (es. class[\"org.apache.commons.lang.StringUtils\"] ).");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_NEW_INSTANCE+"</b>: permette di istanziare una classe (es. new(\"java.lang.StringBuilder\",\"Commento Iniziale\") ).");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_LOAD_STATIC+"</b>: permette di definire classi (es. class.forName(\"org.apache.commons.lang.StringUtils\") ).");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_NEW_INSTANCE+"</b>: permette di istanziare una classe (es. new.instance(\"java.lang.StringBuilder\",\"Commento Iniziale\") ).");
	
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_DATA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: data di elaborazione del messaggio di risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DATE_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: headers http della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER_VALUES ="<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_RESPONSE_VALUES+"</b>: headers http della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_VALUES_HTML_ESCAPED+")";
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio di risposta tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio XML di risposta tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio JSON di risposta tramite espressioni JSONPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_JSON_PATH+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_INTEGRATION = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: permette di accedere ai claims di un token di integrazione presente nella risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_INTEGRATION+")"); 
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_RESPONSE+"</b>: permette di accedere al contenuto della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE_CONTENT+")";
	
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI = new ArrayList<>();
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
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_CLIENT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_INTEGRATION);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_DYNAMIC_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTACHMENTS);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_REQUEST);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JAVA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV_JAVA);
	}
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
	}
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH); // elimino json path
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		/** if(forceNoSecToken || !modi || fruizione) { */
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE = new ArrayList<>();
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
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_CLIENT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_INTEGRATION);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_INTEGRATION);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_DYNAMIC_PROPERTY_CONFIG);
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
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ENV_JAVA);
	}
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP); // elimino xpath su soap per la risposta
	}
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH); // elimino xpath su xml per la risposta
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH); // elimino json path
		deleteField(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH); // elimino json path per la risposta
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_OBJECT_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY;
		}
	}

	
	
	
	
	// ******** TRASFORMAZIONI (HEADER HTTP, PARAMETRI DELLA URL, TRASFORMAZIONE-REST, TEMPLATE GOVWAY)
	// ******** CONNETTORE FILE (requestFile, requestHeaderFile, responseFile, responseHeaderFile)
	// ******** LOAD BALANCER (CONDIZIONE TEMPLATE GOVWAY per sticky)
	// ******** CONNETTORI MULTIPLI CON CONSEGNA CONDIZIONALE (TEMPLATE GOVWAY)
	// ******** CONNETTORI MULTIPLI CON CONSEGNA CONDIZIONALE, regole specifiche (TEMPLATE GOVWAY)
	// ******** MODI CORNICE SICUREZZA
	
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TRANSACTION_ID_VALUE+"}</b>: identificativo UUID della transazione";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+":FORMAT}</b>: data di elaborazione del messaggio; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+":yyyyMMdd_HHmmssSSS})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+":NAME}</b>: valori presenti negli header http che possiedono il nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della url con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+":NAME}</b>: valore associato al parametro della form con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della form con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":FIELD}</b>: permette di utilizzare informazioni generiche del profilo; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+"' (es. per il mittente usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":mittente})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+":NAME}</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia (es. identificativo SDI). Il valore 'NAME' indica il nome della proprietà da utilizzare";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_REQUEST = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUEST+":FIELD}</b>: permette di accedere al contenuto della richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE_READER+"' (es. per ottenere il digest dell'attachment usare ${request:part.attachmentByIndex(0).contentBase64Digest(SHA-256)})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_INTEGRATION = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+":FIELD}</b>: permette di accedere ai claim di un token di integrazione; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_INTEGRATION+"' (es. per ottenere il valore del claim 'claimCustom' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+":info[claimCustom]})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":FIELD}</b>: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+"' (es. per ottenere il valore del claim 'sub' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":sub})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_CLIENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+":FIELD}</b>: identità dell'applicativo client identificato tramite il clientId presente nel token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_TOKEN+"' (es. per ottenere il nome dell'applicativo usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+":nome})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":FIELD}</b>: permette di accedere agli attributi recuperati tramite Attribute Authority; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ATTRIBUTES+"' (es. per ottenere il valore dell'attributo 'attr1' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[attr1]}, se configurata solamente 1 A.A., altrimenti usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[nomeAttributeAuthority][attr1]} )";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+":FIELD}</b>: permette di accedere alle informazioni relative ai certificati ed ai security token presenti nella richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SECURITY_TOKEN+"' (es. per accedere al CN del certificato presente nel token ModI 'Authorization' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+":authorization.certificate.subject.info(CN)})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_API_IMPL_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'API, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto fruitore, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto erogatore, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DYNAMIC_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DYNAMIC_CONFIG_PROPERTY+":FIELD}</b>: permette di accedere alle proprietà degli attori coinvolti nella richiesta (api, applicativi, soggetti); il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DYNAMIC_CONFIG_PROPERTY+"'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SYSTEM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+":NAME}</b>: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile java con nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema o java con nome 'NAME'; la variabile viene cercata prima come variabile di sistema e se non presente come variabile della jvm";
		
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_DATA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":FORMAT}</b>: data di elaborazione del messaggio di risposta; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":yyyyMMdd_HHmmssSSS})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":NAME}</b>: valore presente nell'header http della risposta che possiede il nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_RESPONSE_VALUES+":NAME}</b>: valori presenti negli header http della risposta che possiedono il nome 'NAME'";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione XPath applicata sulla risposta"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione XPath applicata su una risposta XML"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione JSONPath applicata su una risposta JSON"; 
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_INTEGRATION = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":FIELD}</b>: permette di accedere ai claim di un token di integrazione presente nella risposta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_INTEGRATION+"' (es. per ottenere il valore del claim 'claimCustom' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":info[claimCustom]})";
	public static final String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_RESPONSE+":FIELD}</b>: permette di accedere al contenuto della risposta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE_READER+"' (es. per ottenere il digest dell'attachment usare ${response:part.attachmentByIndex(0).contentBase64Digest(SHA-256)})";
	
	
	public static final List<String> LABEL_CONFIGURAZIONE_INFO_ALL_VALORI = new ArrayList<>();
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
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_CLIENT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_INTEGRATION);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DYNAMIC_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_REQUEST);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JAVA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV_JAVA);
	}
	
	// NOTA: usato anche in ModIConsoleCostanti
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI;
		}
	}
	
	// NOTA: usato anche in ModIConsoleCostanti
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH); // elimino json path
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI = new ArrayList<>();
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
		deleteField(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_REQUEST); // elimino request
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI;
		}
	}

	public static final List<String> LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE = new ArrayList<>();
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
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_CLIENT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_INTEGRATION);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_INTEGRATION);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DYNAMIC_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY_VALUES);
//		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_FORM_VALUES);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SYSTEM);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JAVA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ENV_JAVA);
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP); // elimino xpath su soap per la risposta
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		//if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE;
		}
	}
	
	private static final List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE);
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH); // elimino xpath su xml
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH); // elimino xpath su xml per la risposta
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH); // elimino json path
		deleteField(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE,LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH); // elimino json path per la risposta
	}
	public static List<String> getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE(boolean modi, boolean fruizione, boolean forceNoSecToken) {
		///if(forceNoSecToken || !modi || fruizione) {
		if(forceNoSecToken) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
			deleteField(l, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE;
		}
	}
	
	
	
	
	
	// ********* COMPRESS (TRASFORMAZIONI)
	
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_CONTENT+"}</b>: payload della richiesta";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_CONTENT+"}</b>: payload della risposta";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_PREFIX+"index"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una richiesta multipart alla posizione indicata dall'intero 'index'";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_PREFIX+"index"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una risposta multipart alla posizione indicata dall'intero 'index'";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_BY_ID_PREFIX+"id"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una richiesta multipart che possiede il "+HttpConstants.CONTENT_ID+" indicato";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_BY_ID_PREFIX+"id"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una risposta multipart che possiede il "+HttpConstants.CONTENT_ID+" indicato";
	
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_ENVELOPE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ENVELOPE+"}</b>: soap envelope della richiesta";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_ENVELOPE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ENVELOPE+"}</b>: soap envelope della risposta";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_BODY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_BODY+"}</b>: contenuto del soap body della richiesta";
	public static final String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_BODY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_BODY+"}</b>: contenuto del soap body della risposta";
	
	public static final List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public static final List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	public static final List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_ENVELOPE);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_BODY);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
	}
	
	public static final List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_ENVELOPE);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_BODY);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
	}
	
	
	
	
	
	
	// ******* CONTROLLO DEGLI ACCESSI - AUTORIZZAZIONE TOKEN CLAIMS
	
	public static final String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_NOTE = "Indicare per riga i claims richiesti (nome=valore); visualizzare 'info' per maggiori dettagli";
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX = "Indicare per riga i claims richiesti nel token nella forma 'nome=valore'.<br/><br/>";
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ATTRIBUTE_PREFIX = "Per verificare un attributo indicarlo con il prefisso 'attribute.' nella forma 'attribute.nome=valore'.<br/><br/>";
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_AA_ATTRIBUTE_PREFIX = "Per verificare un attributo prelevato da un authority indicarlo con i prefissi 'aa.' e 'attribute.' nella forma 'aa.nomeAuthority.attribute.nomeAttributo=valore'.<br/><br/>";
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX =
			"Ogni valore può essere fornito in una delle seguenti modalità:<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_ANY_VALUE+"</b> : indica qualsiasi valore non nullo.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_UNDEFINED+"</b> : la risorsa indicata non deve esistere o non deve essere valorizzata.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : la regola è soddisfatta se l'intero valore del claim ha un match rispetto all'espressione regolare EXPR indicata. È possibile utilizzare anche la versione "+CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+" che consente di attuare una negazione della condizione.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore del claim. Come per la precedente esiste anche la versione "+CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+".<br/>"+
			"- <b>valore</b> : indica esattamente il valore (case sensitive) che deve possedere il claim; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito.<br/>"+
			"- <b>valore1,..,valoreN</b> : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+" o "+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore1,...,valoreN"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+"</b> : simile alle precedenti regole consente di attuare una verifica <b>case insensitive</b>.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_NOT_PREFIX+"valore"+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+" o "+CostantiAutorizzazione.AUTHZ_NOT_PREFIX+"valore1,...,valoreN"+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+"</b> : simile alle precedenti regole consente di indicare esattamente i valori (case sensitive) che <b>non</b> deve possedere il claim. È possibile utilizzarla anche in combinazione con il controllo case-insensitive: "+
			CostantiAutorizzazione.AUTHZ_NOT_PREFIX+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+" o "+
			CostantiAutorizzazione.AUTHZ_NOT_PREFIX+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore1,...,valoreN"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+"</b><br/>"+
			"<br/>Le espressioni utilizzabili come parti dinamiche, risolte a runtime dal gateway, sono:";	
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_NO_ATTRIBUTE_AUTHORITY = LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX + 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX;
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SINGLE_ATTRIBUTE_AUTHORITY = LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX+
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ATTRIBUTE_PREFIX + 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX;
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_MULTI_ATTRIBUTE_AUTHORITY = LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PREFIX+ 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_AA_ATTRIBUTE_PREFIX + 
			LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SUFFIX;
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+":NAME}</b>: valori presenti negli header http che possiedono il nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della url con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+":NAME}</b>: valore associato al parametro della form con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della form con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata sul messaggio"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url di invocazione"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_INTEGRATION = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+":FIELD}</b>: permette di accedere ai claim di un token di integrazione; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_INTEGRATION+"' (es. per ottenere il valore del claim 'claimCustom' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+":info[claimCustom]})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TOKEN_CLIENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+":FIELD}</b>: identità dell'applicativo client identificato tramite il clientId presente nel token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_TOKEN+"' (es. per ottenere il nome dell'applicativo usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+":nome})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":FIELD}</b>: permette di utilizzare informazioni generiche del profilo; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+"' (es. per il mittente usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":mittente})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PROPERTY_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+":NAME}</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia (es. identificativo SDI). Il valore 'NAME' indica il nome della proprietà da utilizzare";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SECURITY_TOKEN = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+":FIELD}</b>: permette di accedere alle informazioni relative ai certificati ed ai security token presenti nella richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SECURITY_TOKEN+"' (es. per accedere al CN del certificato presente nel token ModI 'Authorization' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+":authorization.certificate.subject.info(CN)})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_API_IMPL_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'API, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto fruitore, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto erogatore, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_DYNAMIC_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DYNAMIC_CONFIG_PROPERTY+":FIELD}</b>: permette di accedere alle proprietà degli attori coinvolti nella richiesta (api, applicativi, soggetti); il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DYNAMIC_CONFIG_PROPERTY+"'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SYSTEM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+":NAME}</b>: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile java con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema o java con nome 'NAME'; la variabile viene cercata prima come variabile di sistema e se non presente come variabile della jvm";
	
	private static final List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JSONPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TOKEN_CLIENT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PROPERTY_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SECURITY_TOKEN);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_INTEGRATION);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_DYNAMIC_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JAVA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV_JAVA);
	}
	public static List<String> getLABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI(boolean modi, boolean fruizione) {
		if(!modi || fruizione) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI);
			if(fruizione) {
				deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PROPERTY_BUSTA);
			}
			//deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI;
		}
	}
	
	private static final List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH_SOAP);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TOKEN_CLIENT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PROPERTY_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SECURITY_TOKEN);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_INTEGRATION);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_DYNAMIC_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JAVA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_ENV_JAVA);
	}
	public static List<String> getLABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI(boolean modi, boolean fruizione) {
		if(!modi || fruizione) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI);
			if(fruizione) {
				deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_PROPERTY_BUSTA);
			}
			//deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI;
		}
	}
	
	
	
	
	// ******* CONTROLLO DEGLI ACCESSI - AUTORIZZAZIONE CONTENUTI
	
	public static final String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CONTENUTI_NOTE = "Indicare per riga i controlli richiesti (risorsa=valore); visualizzare 'info' per maggiori dettagli";
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI= "Indicare per riga i controlli di autorizzazione richiesti nella forma (risorsa=valore).<br/><br/>"+
			"Una risorsa identifica un header, una parte dell'url o del messaggio, un claim del token o un principal etc.<br/>"+
			"Per identificare una risorsa sono utilizzabili le espressioni dinamiche descritte nell'ultima parte di questo box informazioni.<br/><br/>"+
			"Ogni valore può essere fornito in una delle seguenti modalità:<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_ANY_VALUE+"</b> : indica qualsiasi valore non nullo.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_UNDEFINED+"</b> : la risorsa indicata non deve esistere o non deve essere valorizzata.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : la regola è soddisfatta se il valore della risorsa ha un match completo rispetto all'espressione regolare EXPR indicata. È possibile utilizzare anche la versione "+CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+" che consente di attuare una negazione della condizione.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore della risorsa. Come per la precedente esiste anche la versione "+CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+".<br/>"+
			"- <b>valore</b> : indica esattamente il valore (case sensitive) che deve possedere la risorsa; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito<br/>"+
			"- <b>valore1,..,valoreN</b> : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+" o "+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore1,...,valoreN"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+"</b> : simile alle precedenti regole consente di attuare una verifica <b>case insensitive</b>.<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_NOT_PREFIX+"valore"+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+" o "+CostantiAutorizzazione.AUTHZ_NOT_PREFIX+"valore1,...,valoreN"+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+"</b> : simile alle precedenti regole consente di indicare esattamente i valori (case sensitive) che <b>non</b> deve possedere la risorsa. È possibile utilizzarla anche in combinazione con il controllo case-insensitive: "+
			CostantiAutorizzazione.AUTHZ_NOT_PREFIX+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+" o "+
			CostantiAutorizzazione.AUTHZ_NOT_PREFIX+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX+"valore1,...,valoreN"+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX+"</b><br/>"+
			"<br/><b>Identificazione delle risorse tramite espressioni dinamiche</b><br/><br/>Le espressioni utilizzabili, risolte a runtime dal gateway, sono:";
	
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER_VALUES+":NAME}</b>: valori presenti negli header http che possiedono il nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della url con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER+":NAME}</b>: valore associato al parametro della form con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM_VALUES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_FORM_PARAMETER_VALUES+":NAME}</b>: valori associati ai parametri della form con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata sul messaggio"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url di invocazione"; 
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_INTEGRATION = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+":FIELD}</b>: permette di accedere ai claim di un token di integrazione; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_INTEGRATION+"' (es. per ottenere il valore del claim 'claimCustom' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_INTEGRATION+":info[claimCustom]})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":FIELD}</b>: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+"' (es. per ottenere il valore del claim 'sub' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":sub})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_CLIENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+":FIELD}</b>: identità dell'applicativo client identificato tramite il clientId presente nel token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_APPLICATIVO_TOKEN+"' (es. per ottenere il nome dell'applicativo usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN+":nome})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_ATTRIBUTES = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":FIELD}</b>: permette di accedere agli attributi recuperati tramite Attribute Authority; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ATTRIBUTES+"' (es. per ottenere il valore dell'attributo 'attr1' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[attr1]}, se configurata solamente 1 A.A., altrimenti usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ATTRIBUTES+":attributes[nomeAttributeAuthority][attr1]} )";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":FIELD}</b>: permette di utilizzare informazioni generiche del profilo; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+"' (es. per il mittente usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":mittente})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_PROPERTY_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+":NAME}</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia (es. identificativo SDI). Il valore 'NAME' indica il nome della proprietà da utilizzare";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SECURITY_TOKEN = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+":FIELD}</b>: permette di accedere alle informazioni relative ai certificati ed ai security token presenti nella richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_SECURITY_TOKEN+"' (es. per accedere al CN del certificato presente nel token ModI 'Authorization' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SECURITY_TOKEN+":authorization.certificate.subject.info(CN)})";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_API_IMPL_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_API_IMPL_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'API, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_FRUITORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto fruitore, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_EROGATORE_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto erogatore, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alla proprietà, configurata nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_DYNAMIC_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DYNAMIC_CONFIG_PROPERTY+":FIELD}</b>: permette di accedere alle proprietà degli attori coinvolti nella richiesta (api, applicativi, soggetti); il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DYNAMIC_CONFIG_PROPERTY+"'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SYSTEM = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SYSTEM_PROPERTY+":NAME}</b>: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile java con nome 'NAME'";
	public static final String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV_JAVA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ENV_JAVA_PROPERTY+":NAME}</b>: valore associato alla variabile di sistema o java con nome 'NAME'; la variabile viene cercata prima come variabile di sistema e se non presente come variabile della jvm";
		
	private static final List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_JSONPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_CLIENT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_ATTRIBUTES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_PROPERTY_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SECURITY_TOKEN);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_INTEGRATION);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_DYNAMIC_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_JAVA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV_JAVA);
	}
	public static List<String> getLABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI(boolean modi, boolean fruizione) {
		if(!modi || fruizione) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI);
			if(fruizione) {
				deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_PROPERTY_BUSTA);
			}
			//deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI;
		}
	}
	
	private static final List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH_SOAP);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_CLIENT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_ATTRIBUTES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_PROPERTY_BUSTA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SECURITY_TOKEN);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_INTEGRATION);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_API_IMPL_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_FRUITORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_EROGATORE_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_PROPERTY_CONFIG);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_DYNAMIC_PROPERTY_CONFIG);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY_VALUES);
//		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_FORM_VALUES);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SYSTEM);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_JAVA);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_ENV_JAVA);
	}
	public static List<String> getLABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI(boolean modi, boolean fruizione) {
		if(!modi || fruizione) {
			List<String> l = new ArrayList<>();
			l.addAll(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI);
			if(fruizione) {
				deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_PROPERTY_BUSTA);
			}
			//deleteField(l, LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SECURITY_TOKEN);
			return l;
		}
		else {
			return LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI;
		}
	}
	
	
	
	
	
	// ******* ATTRIBUTE AUTHORITY (TEMPLATE VELOCITY, TEMPLATE FREEMARKER)
	
	public static final String LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI_REQUIRED_ATTRIBUTE = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+"</b>: "+
			"permette di conoscere gli attributi che devono essere richiesti, indicati nella configurazione specifica di una erogazione o fruizione di API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_REQUIRED_ATTRIBUTES+")"); 
	
	public static final List<String> LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI.add(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI_REQUIRED_ATTRIBUTE);
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_ATTRIBUTES); // elimino attributes 
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER); // elimino error handler
	}
	
	// ******* ATTRIBUTE AUTHORITY (TEMPLATE e ALTRE VOCI)
		
	public static final String LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_REQUIRED_ATTRIBUTE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+":METHOD}</b>: "+
			"permette di conoscere gli attributi che devono essere richiesti, indicati nella configurazione specifica di una erogazione o fruizione di API; il valore 'METHOD' fornito deve rappresentare un metodo valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_REQUIRED_ATTRIBUTES+"'"+
			" (es. per ottenere la lista degli attributi in un formato utilizzabile all'interno di un array json usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+":jsonList()} oppure ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES+":formatList(&quot;,&quot;)})";
	
	public static final List<String> LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
		deleteField(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ATTRIBUTES); // elimino attributes 
	}
	
	public static final List<String> LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES.add(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_REQUIRED_ATTRIBUTE);
		LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES.addAll(LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI);
	}
	
	
	
	
	
	
	// ******* NEGOZIAZIONE TOKEN
	
	public static final String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_ISSUER = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il claim 'iss' verrà valorizzato con il nome del soggetto associato al dominio di gestione della richiesta";
	public static final String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_SUBJECT = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il claim 'sub' verrà valorizzato con il medesimo valore associato al Client ID";
	public static final String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_IDENTIFIER = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il claim 'jti' verrà valorizzato con un UUID generato da GovWay";
	public static final String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_FORM_PARAMETRO_CLIENT_ID = LABEL_CONFIGURAZIONE_INFO_TRASPORTO.replace(":", " indicate di seguito.")+"<BR/>Se non viene fornito un valore il parametro verrà valorizzato con il medesimo valore associato al Client ID definito nel payload del JWT";
	
	public static final String LABEL_CONFIGURAZIONE_NEGOZIAZIONE_VALORE_NON_DEFINITO = "<b>"+org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED+"</b>: indica di non generare il claim";
	
	public static final List<String> LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI, LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP); // elimino xpath su soap
	}
	
	public static final List<String> LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO.add(LABEL_CONFIGURAZIONE_NEGOZIAZIONE_VALORE_NON_DEFINITO);
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
	}
	
	public static final List<String> LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_OBJECT_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_OBJECT_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		deleteField(LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_OBJECT_VALORI, LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP); // elimino xpath su soap
	}
	
	
	
	
	
	
	// ******* MODI - REST EROGAZIONE/FRUIZIONE CLAIMS
	
	public static final String NOT_GENERATE = "${notGenerate}";
	
	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE = "Indicare per riga i claims (nome=valore); visualizzare 'info' per maggiori dettagli";

	private static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_PREFIX= 
			"Indicare per riga i claims da aggiungere nel payload del JWT, nella forma 'nome=valore'.<br/><br/>";
	private static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS_TEMPLATE = "CLAIMS";
	private static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS = 
			"<b>Attenzione</b>: non è consentito indicare i seguenti claims: "+LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS_TEMPLATE+"<br/><br/>";
	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_NOT_GENERATED = 
			"È possibile utilizzare la keyword '"+NOT_GENERATE+"' come valore dei claims 'iss', 'sub' o 'client_id' per non far generare il claim all'interno del jwt payload.<br/><br/>";
	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_REQUEST = 
			"Se vengono indicati i claims 'iss' o 'sub' i valori forniti sovrascriveranno le impostazioni di default del prodotto.<br/>"+
				LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_NOT_GENERATED;
	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_RESPONSE = 
			"Se vengono indicati i claims 'iss' o 'sub' o 'aud' o 'client_id' i valori forniti sovrascriveranno le impostazioni di default del prodotto.<br/>"+
				LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_NOT_GENERATED;

	public static final String getLABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO(boolean request, List<String> denyClaims) {
		StringBuilder sb = new StringBuilder(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_PREFIX);
		sb.append(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS.
				replace(LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DENY_CLAIMS_TEMPLATE, denyClaims.toString()));
		sb.append(request ? LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_REQUEST :
			LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_DEFAULT_CLAIMS_RESPONSE);
		sb.append(LABEL_CONFIGURAZIONE_CLAIMS);
		return sb.toString();
	}
	
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_REQUEST.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_INFO_VALORI_RESPONSE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	
	
	// ******* MODI - REST EROGAZIONE/FRUIZIONE AUDIENCE

	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INFO = LABEL_CONFIGURAZIONE_INFO_TRASPORTO;
	
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_REQUEST = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_REQUEST.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_REQUEST = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_REQUEST.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
	}
	
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_RESPONSE = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_RESPONSE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_RESPONSE = new ArrayList<>();
	static {
		LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_RESPONSE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
	}
	
	public static final String LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_INFO = LABEL_CONFIGURAZIONE_INFO_TRASPORTO;
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_REST_INFO_VALORI_REQUEST = LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE_INFO_VALORI_REQUEST;
	public static final List<String> LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SOAP_INFO_VALORI_REQUEST = LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_AUDIENCE_INFO_VALORI_REQUEST;

	
	
	// ******* PLUGIN
	
	public static final String PLUGIN_CLASSNAME = "ClassName";
	
	public static final String PLUGIN_CLASSNAME_INFO_SINGOLA = "Il plugin deve implementare l'interfaccia: ";
	public static final String PLUGIN_CLASSNAME_INFO_MULTIPLA_AND = "Il plugin deve implementare tutte le seguenti interfacce: ";
	public static final String PLUGIN_CLASSNAME_INFO_MULTIPLA_OR = "Il plugin deve implementare una delle seguenti interfacce: ";
	
	
	
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
