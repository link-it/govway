/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.costanti;

/**
 * CostantiControlStation
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class CostantiControlStation {
	
	public final static String DEFAULT_OPENSPCOOP2_PREFIX_LOCAL_PATH = "console";
	public final static String DEFAULT_OPENSPCOOP2_PROPERTIES_NAME = "CONSOLE";
	
	public final static String OPENSPCOOP2_PREFIX_LOCAL_PATH = "PREFIXNAMETEMPLATE";
	public final static String OPENSPCOOP2_PROPERTIES_NAME = "NAMETEMPLATE";
	
    public final static String _OPENSPCOOP2_CONSOLE_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.properties";
    public final static String _OPENSPCOOP2_CONSOLE_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_PROPERTIES";
    
    public final static String _OPENSPCOOP2_DATASOURCE_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.datasource.properties";
    public final static String _OPENSPCOOP2_DATASOURCE_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_DATASOURCE_PROPERTIES";
    
    public final static String _OPENSPCOOP2_REGISTRO_SERVIZI_REMOTO_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.registroServiziRemoto.properties";
    public final static String _OPENSPCOOP2_REGISTRO_SERVIZI_REMOTO_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_REGISTRO_SERVIZI_REMOTO_PROPERTIES";
	
    public final static String _OPENSPCOOP2_LOGGER_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.log4j2.properties";
    public final static String _OPENSPCOOP2_LOGGER_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_LOG_PROPERTIES";
    
	/** Logger */
	public static final String DRIVER_DB_PDD_CONSOLE_LOGGER = "DRIVER_DB_PDD_CONSOLE";

	/** Debug COSTANT STRING */
	public static final String DEBUG_STRING = "PDD_CONSOLE_DEBUG";
	
	/** PLACEHOLDER PROTOCOLLO in creazione automatica endpoint del soggetto */
	public static final String PLACEHOLDER_SOGGETTO_ENDPOINT_CREAZIONE_AUTOMATICA = "@protocol@";
	
	/** PLACEHOLDER PDD  */
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_IP_GESTIONE = "@IP_GESTIONE@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PORTA_GESTIONE = "@PORTA_GESTIONE@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PROTOCOLLO_GESTIONE = "@PROTOCOLLO_GESTIONE@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_IP_PUBBLICO = "@IP@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PORTA_PUBBLICA = "@PORTA@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PROTOCOLLO_PUBBLICO = "@PROTOCOLLO@";
	
	/** Intervallo in millisecondi per Filtro JMS effettuato dai Gestori */
	public static final int JMS_FILTER = 100;

	/** Sleep per simulare Algoritmo transazione XA */
	public static final int INTERVALLO_TRANSAZIONE_XA = 2000;

	/** Sleep per receive */
	public static final int INTERVALLO_RECEIVE = 10000;
	
	/** PERFORM OPERATION */
	public static final int PERFORM_OPERATION_CREATE = 0;
	public static final int PERFORM_OPERATION_UPDATE = 1;
	public static final int PERFORM_OPERATION_DELETE = 2;
	
	/** SCRIPT OPERATION */
	public static final String SCRIPT_PERFORM_OPERATION_CREATE = "add";
	public static final String SCRIPT_PERFORM_OPERATION_DELETE = "delete";
	
	/** SESSION ATTRIBUTE */
	public final static String SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO = "GestioneInfoProtocollo";
	public final static String SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI = "ShowAccordiAzioni";
	public final static String SESSION_PARAMETRO_VISUALIZZA_ACCORDI_PORT_TYPES = "ShowAccordiPortTypes";
	public final static String SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE = "ShowAccordiCooperazione";
	public final static String SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI = "SoggettoVirtuale";
	public final static String SESSION_PARAMETRO_MODALITA_INTERFACCIA = "ModalitaInterfaccia";
	public final static String SESSION_PARAMETRO_SINGLE_PDD = "singlePdD";
	public final static String SESSION_PARAMETRO_GENERAZIONE_AUTOMATICA_PORTE_DELEGATE = "generazioneAutomaticaPD";
	public final static String SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE = "ConfigurazioniPersonalizzate";
	public final static String SESSION_PARAMETRO_SAME_DB_WEBUI = "sameDBWebUI";
	public final static String SESSION_PARAMETRO_TIPO_DB = "tipoDB";
	
	
	/** LABEL GENERALI */
	public final static String LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE_COLUMN = "Non standard";
	public final static String LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE = "Configurazione non visualizzabile";
	public final static String LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE = "Attenzione: Configurazione non standard (Utilizzare l'interfaccia avanzata)";
	public final static String LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO = "Aggiornamento effettuato con successo";
	public final static String LABEL_STRUMENTI = "Strumenti";
	public final static String LABEL_LINKIT_WEB = "http://www.link.it";
	public final static String LABEL_OPENSPCOOP2 = "OpenSPCoop.org";
	public final static String LABEL_OPENSPCOOP2_WEB = "http://www.openspcoop.org";
	public final static String LABEL_PARAMETRO_ID = "Id";
	public final static String LABEL_PARAMETRO_ID_SOGGETTO = "IdSogg";
	public final static String LABEL_PARAMETRO_ID_PORTA = "IdPorta";
	public final static String LABEL_PARAMETRO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_VALORE = "Valore";
	public final static String LABEL_PARAMETRO_SERVIZIO_APPLICATIVO = "Servizio Applicativo";
	public final static String LABEL_PARAMETRO_STATO = "Stato";
	public final static String LABEL_PARAMETRO_MESSAGE_SECURITY = "Message-Security";
	public final static String LABEL_PARAMETRO_RICHIESTA = "Richiesta";
	public final static String LABEL_PARAMETRO_RISPOSTA = "Risposta";
	public final static String LABEL_PARAMETRO_OBBLIGATORIO = "Elemento Obbligatorio";
	public final static String LABEL_PARAMETRO_PATTERN = "Pattern";
	public final static String LABEL_PARAMETRO_CONTENT_TYPE = "Content-Type";
	public final static String LABEL_PARAMETRO_PARAMETRI = "Parametri";
	public final static String LABEL_PARAMETRO_APPLICA_MTOM = "Applica MTOM";
	public final static String LABEL_PARAMETRO_ACCETTA_MTOM = "Accetta MTOM";
	public final static String LABEL_PARAMETRO_RUOLO = "Nome";
	public final static String LABEL_PARAMETRO_RUOLO_TIPOLOGIA = "Fonte";
	public final static String LABEL_PARAMETRO_RUOLO_TIPOLOGIA_XACML_POLICY = "Fonte Ruoli";
	public final static String LABEL_PARAMETRO_RUOLO_MATCH = "Ruoli Richiesti";
	public final static String LABEL_PARAMETRO_RUOLO_MATCH_ALL = "tutti";
	public final static String LABEL_PARAMETRO_RUOLO_MATCH_ANY = "almeno uno";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI = "Controllo degli Accessi";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE = "Autenticazione";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE = "Autorizzazione";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI = "Autorizzazione Contenuti";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE = "Opzionale";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI = "Applicativi Autenticati";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI = "Soggetti Autenticati";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI = "Ruoli";
	public final static String LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI = "Tipo";
	public final static String LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA = "Correlazione Applicativa";
	public final static String LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RICHIESTA = "Regole";
	public final static String LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RISPOSTA = "Regole";
	public final static String LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_LABEL = "Scadenza (minuti)";
	public final static String LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_NOTE = "Definisce una scadenza per il riuso del solito ID Protocollo";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING = "Service Binding";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING_SOAP = "Soap";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING_REST = "Rest";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE = "Tipo Messaggio";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_DEFAULT = "Default";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_11 = "Soap-1.1";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_12 = "Soap-1.2";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_XML = "Xml";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_JSON = "Json";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_BINARY = "Binary";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART = "MIME-Multipart";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE = "Formato Specifica";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11 = "Wsdl 1.1";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_WADL = "Wadl";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2 = "Swagger 2.0";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3 = "Open API 3.0";
	
	/** PARAMETERS **/
	
	public final static String PARAMETRO_ID = "id";
	public final static String PARAMETRO_ID_SOGGETTO = "idsogg";
	public final static String PARAMETRO_ID_PORTA = "idPorta";
	public final static String PARAMETRO_NOME = "nome";
	public final static String PARAMETRO_SERVIZIO_APPLICATIVO = "servizioApplicativo";
	public final static String PARAMETRO_VALORE = "valore";
	public final static String PARAMETRO_MESSAGE_SECURITY = "messageSecurity";
	public final static String PARAMETRO_MTOM_RICHIESTA = "mtomReq";
	public final static String PARAMETRO_MTOM_RISPOSTA = "mtomRes";
	public final static String PARAMETRO_OBBLIGATORIO = "obbl";
	public final static String PARAMETRO_CONTENT_TYPE = "contentT";
	public final static String PARAMETRO_PATTERN = "pattern";
	public final static String PARAMETRO_APPLICA_MTOM_RICHIESTA = "applicaMTOMReq";
	public final static String PARAMETRO_APPLICA_MTOM_RISPOSTA = "applicaMTOMRes";
	public final static String PARAMETRO_USAIDSOGG = "usaidsogg";
	public final static String PARAMETRO_EXTENDED_FORM_ID = "extendedFormUniqueId";
	public final static String PARAMETRO_CONTENT_DISPOSITION = "Content-Disposition";
	public final static String PREFIX_CONTENT_DISPOSITION = "form-data; name=\"";
	public final static String SUFFIX_CONTENT_DISPOSITION = "\"";
	public final static String PREFIX_FILENAME = "filename=\"";
	public final static String SUFFIX_FILENAME = "\"";
	public final static String PARAMETRO_RUOLO = "ruolo";
	public final static String PARAMETRO_RUOLO_TIPOLOGIA = "ruoloTipologia";
	public final static String PARAMETRO_RUOLO_MATCH = "ruoloMatch";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE = "autenticazione";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM = "autenticazioneCustom";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE = "autenticazioneOpzionale";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE = "autorizzazione";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM = "autorizzazioneCustom";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE = "autorizzazioneAutenticazione";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI = "autorizzazioneRuoli";
	public final static String PARAMETRO_AUTORIZZAZIONE_CONTENUTI = "autorizzazioneContenuti";
	public final static String PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA = "scadcorr";
	public final static String PARAMETRO_APPLICA_MODIFICA = "applicaMod";
	
	public final static String PARAMETRO_SERVICE_BINDING = "serviceBinding";
	public final static String PARAMETRO_MESSAGE_TYPE = "messageType";
	public final static String PARAMETRO_INTERFACE_TYPE = "interfaceType";
	
	/** DEFAULT VALUES **/
	
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO = "abilitato";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_DISABILITATO = "disabilitato";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_REQUEST_FLOW = "Request Flow";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_RESPONSE_FLOW = "Response Flow";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_DISABLE = "disable";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_PACKAGING = "packaging";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_UNPACKAGING = "unpackaging";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_VERIFY = "verify";
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM =  "custom";
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM = "custom";
	
	public final static String DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP = "SOAP";
	public final static String DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST = "REST";
	
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_DEFAULT = "D";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_11 = "SOAP_11";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_12 = "SOAP_12";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_XML = "XML";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_JSON = "JSON";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_BINARY = "BINARY";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART = "MIME_MULTIPART";
	
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_WSDL_11 = "wsdl11";
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_WADL = "wadl";
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SWAGGER_2 = "swagger2";
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_OPEN_API_3 = "openApi3";
	
	public final static String LABEL_LIST_VALORE_NON_PRESENTE = "--";
	
	/** OTHER */
	public final static String IMAGES_DIR = "images";
	public final static String CSS_DIR = "css";
	public final static String JS_DIR = "js";
	public final static String FONTS_DIR = "fonts";
	public final static String OPERATIONS_DELIMITER = "\n--------------------------------------------\n\n";
	public final static String RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_JMX = "jmx";
	public final static String RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP = "openspcoop";
	
	
	/** COSTANTI FILE TEMPORANEI */
	public final static String TEMP_FILE_PREFIX = "__pddconsole__";
	public final static String TEMP_FILE_SUFFIX = ".tmp"; 
	
}
