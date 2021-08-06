/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.controllo_traffico.beans.JMXConstants;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.jmx.CostantiJMX;



/**
 * Classe dove sono fornite costanti e metodi utilizzati dai vari moduli della porta di dominio 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiPdD {
   

    /* ********  F I E L D S    S T A T I C    P U B L I C  ******** */

    /** Versione beta, es: "b1" */
    public static final String OPENSPCOOP2_BETA = Costanti.OPENSPCOOP2_BETA;
    /** Versione di OpenSPCoop */
    public static final String OPENSPCOOP2_VERSION = Costanti.OPENSPCOOP2_VERSION;
    /** Versione di OpenSPCoop */
    public static final String OPENSPCOOP2_PRODUCT = Costanti.OPENSPCOOP2_PRODUCT;
    /** Versione di OpenSPCoop (User-Agent) */
    public static final String OPENSPCOOP2_PRODUCT_VERSION = Costanti.OPENSPCOOP2_PRODUCT_VERSION;
    /** Details */
    public static final String OPENSPCOOP2_DETAILS = Costanti.OPENSPCOOP2_DETAILS;
    /** Copyright */
	public final static String OPENSPCOOP2_COPYRIGHT = Costanti.OPENSPCOOP2_COPYRIGHT;
	 /** License */
	public final static String OPENSPCOOP2_LICENSE = Costanti.OPENSPCOOP2_LICENSE;
	
	

    /** Stringa contenente un codice di porta di dominio 'standard' da utilizzare in caso si generano errori 
	prima della lettura del vero codice della porta di dominio dalla configurazione di OpenSPCoop. */
    public final static String OPENSPCOOP2 = org.openspcoop2.utils.Costanti.OPENSPCOOP2;
    
    public final static String OPENSPCOOP2_LOCAL_HOME = org.openspcoop2.utils.Costanti.OPENSPCOOP2_LOCAL_HOME;
    
    public final static String OPENSPCOOP2_LOOKUP = org.openspcoop2.utils.Costanti.OPENSPCOOP2_LOOKUP;
    
    public final static String OPENSPCOOP2_LOADER = "OPENSPCOOP2_LOADER";
    public final static String OPENSPCOOP2_LOADER_PROPERTIES = "OPENSPCOOP2_LOADER_PROPERTIES";
    
    public final static String OPENSPCOOP2_PROPERTIES_LOCAL_PATH = "govway_local.properties";
    public final static String OPENSPCOOP2_PROPERTIES = "OPENSPCOOP2_PROPERTIES";
    
    public final static String OPENSPCOOP2_CLASSNAME_LOCAL_PATH = "govway_local.classRegistry.properties";
    public final static String OPENSPCOOP2_CLASSNAME_PROPERTIES = "OPENSPCOOP2_CLASSREGISTRY_PROPERTIES";
    
    public final static String OPENSPCOOP2_PDD_LOCAL_PATH = "govway_local.pdd.properties";
    public final static String OPENSPCOOP2_PDD_PROPERTIES = "OPENSPCOOP2_PDD_PROPERTIES";
    
    public final static String OPENSPCOOP2_MSGDIAGNOSTICI_LOCAL_PATH = "govway_local.msgDiagnostici.properties";
    public final static String OPENSPCOOP2_MSGDIAGNOSTICI_PROPERTIES = "OPENSPCOOP2_MSGDIAGNOSTICI_PROPERTIES";
       
    public final static String OPENSPCOOP2_LOGGER_LOCAL_PATH = "govway_local.log4j2.properties";
    public final static String OPENSPCOOP2_LOGGER_PROPERTIES = "OPENSPCOOP2_LOGGER_PROPERTIES";
    
    public final static String OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO = "IDPROTOCOLLO";
    private final static String OPENSPCOOP2_LOGGER_PROTOCOL_LOCAL_PATH = "govway_local.protocol_"+OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO+".log4j2.properties";
    public static String getOpenspcoop2LoggerProtocolLocalPath(String protocol) {
		return OPENSPCOOP2_LOGGER_PROTOCOL_LOCAL_PATH.replace(OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO, protocol);
	}
	private final static String OPENSPCOOP2_LOGGER_PROTOCOL_PROPERTIES = "OPENSPCOOP2_LOGGER_PROTOCOL_"+OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO+"_PROPERTIES";
    public static String getOpenspcoop2LoggerProtocolProperties(String protocol) {
		return OPENSPCOOP2_LOGGER_PROTOCOL_PROPERTIES.replace(OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO, protocol);
	}
    private final static String OPENSPCOOP2_LOGGER_FACTORY_NAME = "govway."+OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO;
    public static String getOpenspcoop2LoggerFactoryName(String protocol) {
		return OPENSPCOOP2_LOGGER_FACTORY_NAME.replace(OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO, protocol);
	}
     
	public final static String OPENSPCOOP2_CACHE_DEFAULT_PROPERTIES_NAME = "govway.jcs.properties";
    public final static String OPENSPCOOP2_CACHE_LOCAL_PATH = "govway_local.jcs.properties";
    public final static String OPENSPCOOP2_CACHE_PROPERTIES = "OPENSPCOOP2_CACHE_PROPERTIES";
    
    public final static String OPENSPCOOP2_CONFIG_LOCAL_PATH = "govway_local.config.properties";
    public final static String OPENSPCOOP2_CONFIG_PROPERTIES = "OPENSPCOOP2_CONFIG_PROPERTIES";
    
    /** Costante per l'indicazione dell'imbustamento con Attachments */
    public final static String IMBUSTAMENTO_ATTACHMENT = "attach";
    /** Costante per l'indicazione del mime type utilizzato per l'imbustamento con Attachments */
    public final static String IMBUSTAMENTO_MIME_TYPE = "mime-type";
   
    /** Costante che indica una busta in ingresso */
    public final static String TRACCIAMENTO_IN = "IN";
    /** Costante che indica una busta in uscita */
    public final static String TRACCIAMENTO_OUT = "OUT";
    
    public final static String TIPOLOGIA_INSTALLER = CostantiConfigurazione.GENERIC_PROPERTIES_TIPOLOGIA_INSTALLER;
    
    /**
     * Gestione Preflight Request in corso su SOAP
     */
    public final static String CORS_PREFLIGHT_REQUEST_SOAP = "CORS_PREFLIGHT_REQUEST_SOAP";
    
    /** Nome Porta Invocata originale */
    public final static String NOME_PORTA_INVOCATA = "NOME_PORTA_INVOCATA";
    
    /** Response Caching */
    public final static String RESPONSE_CACHE_REQUEST_DIGEST = "RESPONSE_CACHE_REQUEST_DIGEST";
    /** Response Caching */
    public final static String RESPONSE_CACHE_REQUEST_DIGEST_DEFAULT_ALGORITHM = "MD5";
    
    /** Dati di integresso del messaggio */
    public final static String DATA_ACCETTAZIONE_RICHIESTA = "CONTEXT_DATA_ACCETTAZIONE_RICHIESTA";
    public final static String DATA_INGRESSO_RICHIESTA = "CONTEXT_DATA_INGRESSO_RICHIESTA";
    
    /** Variabile che indica la configurazione di dump */
    public final static String DUMP_CONFIG = "DumpConfig";
    public final static String DUMP_RICHIESTA_EFFETTUATO = "DumpRichiestaEffettuato";
    
    /** Configurazione soglia per salvataggio in ram */
    public static final int DUMP_BINARIO_THRESHOLD = 1048576; // 1MB (1024*1024)
    
    /** Configurazione soglia per salvataggio in ram */
    public static final int DUMP_NON_REALTIME_THRESHOLD = 61440;
    /** Modalita di dump non realtime */
    public static final String DUMP_NON_REALTIME_MODE_DB = "db";
    public static final String DUMP_NON_REALTIME_MODE_FILE_SYSTEM = "fs";
    public static final String DUMP_NON_REALTIME_MODE_AUTO = "auto";

    /** Variabile che indica un soggetto anonimo */
    public final static String SOGGETTO_ANONIMO = "Anonimo";
    /** Variabile che indica un servizio applicativo anonimo */
    public final static String SERVIZIO_APPLICATIVO_ANONIMO = "Anonimo";
        	    
    /** Costante che indica l'header di integrazione per WS*/
    public final static String HEADER_INTEGRAZIONE = "HEADER_INTEGRAZIONE";
    /** Costante che indica la gestione dell'errore per WS*/
    public final static String GESTIONE_ESITO = "GESTIONE_ESITO";
    /** Costante che indica il servizio in gestione per WS*/
    public final static String GESTIONE_ESITO_MODULO_FUNZIONALE = "GESTIONE_ESITO_MODULO_FUNZIONALE";
    
    /** PddContext in Header HTTP */
	public final static String OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP = "OPENSPCOOP2_CONTEXT_HEADER_HTTP";
	
	public final static String OPENSPCOOP2_PDD_CONTEXT_NUMERO_TENTATIVI_RICONSEGNA_UPDATED = "NUMERO_TENTATIVI_RICONSEGNA_UPDATED";
	        
    /** Costante che indica il valore dell'header di integrazione: tipo mittente*/
    public final static String HEADER_INTEGRAZIONE_TIPO_MITTENTE = "tipoMittente";
    /** Costante che indica il valore dell'header di integrazione: mittente*/
    public final static String HEADER_INTEGRAZIONE_MITTENTE = "mittente";
    /** Costante che indica il valore dell'header di integrazione: tipo destinatario*/
    public final static String HEADER_INTEGRAZIONE_TIPO_DESTINATARIO = "tipoDestinatario";
    /** Costante che indica il valore dell'header di integrazione: destinatario*/
    public final static String HEADER_INTEGRAZIONE_DESTINATARIO = "destinatario";
    /** Costante che indica il valore dell'header di integrazione: tipo servizio*/
    public final static String HEADER_INTEGRAZIONE_TIPO_SERVIZIO = "tipoServizio";
    /** Costante che indica il valore dell'header di integrazione: servizio*/
    public final static String HEADER_INTEGRAZIONE_SERVIZIO = "servizio";
    /** Costante che indica il valore dell'header di integrazione: versioneServizio*/
    public final static String HEADER_INTEGRAZIONE_VERSIONE_SERVIZIO = "versioneServizio";
    /** Costante che indica il valore dell'header di integrazione: azione*/
    public final static String HEADER_INTEGRAZIONE_AZIONE = "azione";
    /** Costante che indica il valore dell'header di integrazione: identificativo*/
    public final static String HEADER_INTEGRAZIONE_ID_MESSAGGIO = "identificativo";
    /** Costante che indica il valore dell'header di integrazione: riferimentoMessaggio*/
    public final static String HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO = "riferimentoMessaggio";
    /** Costante che indica il valore dell'header di integrazione: idApplicativo*/
    public final static String HEADER_INTEGRAZIONE_ID_APPLICATIVO = "idApplicativo";
    /** Costante che indica il valore dell'header di integrazione: idCollaborazione*/
    public final static String HEADER_INTEGRAZIONE_COLLABORAZIONE = "idCollaborazione";
    /** Costante che indica il valore dell'header di integrazione: servizioApplicativo*/
    public final static String HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO = "servizioApplicativo";
    /** Costante che indica il valore dell'header di integrazione: identificativo di transazione*/
    public final static String HEADER_INTEGRAZIONE_ID_TRANSAZIONE = "idTransazione";
    
    /** Costante che indica l'invio delle informazioni di versionamento */
    public final static String HEADER_INTEGRAZIONE_PROTOCOL_INFO = "protocolInfo";
    
    /** Costante che indica l'invio delle informazioni di versionamento */
    public final static String HEADER_INTEGRAZIONE_INFO = "info";
    public final static String HEADER_INTEGRAZIONE_USER_AGENT = "userAgent";
    /** Costante che indica il valore dell'header X che indica la versione della PdD*/
    public final static String HEADER_HTTP_X_PDD = "GovWay-Version";
    /** Costante che indica il valore dell'header X che indica il dettaglio della versione della PdD*/
    public final static String HEADER_HTTP_X_PDD_DETAILS = "GovWay-Version-Info";
    
    /** Costante che indica il valore dell'header X che indica la versione della PdD*/
    public final static String URL_BASED_PDD = "version";
    /** Costante che indica il valore dell'header X che indica il dettaglio della versione della PdD*/
    public final static String URL_BASED_PDD_DETAILS = "version_info";
    
    /** Costante che indica il valore dell'attributo dell'header di integrazione SOAP proprietario di OpenSPCoop che indica la versione della PdD*/
    public final static String HEADER_INTEGRAZIONE_SOAP_PDD_VERSION = "version";
    /** Costante che indica il valore dell'attributo dell'header di integrazione SOAP proprietario di OpenSPCoop che indica il dettaglio della versione della PdD*/
    public final static String HEADER_INTEGRAZIONE_SOAP_PDD_DETAILS = "versionInfo";

    
    /** Costante che indica il valore dell'header di integrazione per i token*/
    public final static String HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME = "processTime";
    public final static String HEADER_INTEGRAZIONE_TOKEN_ISSUER = "issuer";
    public final static String HEADER_INTEGRAZIONE_TOKEN_SUBJECT = "subject";
    public final static String HEADER_INTEGRAZIONE_TOKEN_USERNAME = "username";
    public final static String HEADER_INTEGRAZIONE_TOKEN_AUDIENCE = "audience";
    public final static String HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID = "clientId";
    public final static String HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT = "issuedAt";
    public final static String HEADER_INTEGRAZIONE_TOKEN_EXPIRED = "expired";
    public final static String HEADER_INTEGRAZIONE_TOKEN_NBF = "nbf";
    public final static String HEADER_INTEGRAZIONE_TOKEN_ROLES = "roles";
    public final static String HEADER_INTEGRAZIONE_TOKEN_SCOPES = "scopes";
    public final static String HEADER_INTEGRAZIONE_TOKEN_FULL_NAME = "fullName";
    public final static String HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME = "firstName";
    public final static String HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME = "middleName";
    public final static String HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME = "familyName";
    public final static String HEADER_INTEGRAZIONE_TOKEN_EMAIL = "email";
    
    
    /** Attesa attiva di default effettuata per ottenere un lock, in millisecondi */
    public final static long GET_LOCK_ATTESA_ATTIVA = 90 * 1000; // 2 minuti
    /** Intervallo maggiore per frequenza di check per ottenere un lock, in millisecondi */
    public final static int GET_LOCK_CHECK_INTERVAL = 50;
    
    /** Attesa attiva di default effettuata per messaggi gia in processamento, in millisecondi */
    public final static long MSG_GIA_IN_PROCESSAMENTO_ATTESA_ATTIVA = 90 * 1000; // 2 minuti
    /** Intervallo maggiore per frequenza di check per messaggi gia in processamento, in millisecondi */
    public final static int MSG_GIA_IN_PROCESSAMENTO_CHECK_INTERVAL = 500;
  
    
    /** Attesa attiva di default effettuata dal TransactionManager, in millisecondi */
    public final static long TRANSACTION_MANAGER_ATTESA_ATTIVA = 120 * 1000; // 2 minuti
    /** Intervallo maggiore per frequenza di check nell'attesa attiva effettuata dal TransactionManager, in millisecondi */
    public final static int TRANSACTION_MANAGER_CHECK_INTERVAL = 10;
    /** Intervallo di check su DB effettuata dal TransactionManager, in caso di cache attiva */
    public final static int TRANSACTION_MANAGER_CHECK_DB_INTERVAL = 25;
    
    
    /** Attesa attiva di default effettuata dal timer, in millisecondi */
    public final static long TIMER_EJB_ATTESA_ATTIVA = 60 * 1000; // 1 minuto
    /** Intervallo maggiore per frequenza di check nell'attesa attiva effettuata dal Timer EJB millisecondi */
    public final static int TIMER_EJB_CHECK_INTERVAL = 200; 
    
    /** Intervallo per riconsegna messaggi verso ConsegnaContenutiApplicativi */
    public final static int TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRESA_IN_CONSEGNA_MAX_LIFE = 1800; 
    public final static String TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_MESSAGGI_SPEDIRE = "CONTENUTI_APPLICATIVI_MESSAGGI_SPEDIRE";
    public final static String TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_CODA_DEFAULT = CostantiConfigurazione.CODA_DEFAULT;
    public final static String TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRIORITA_DEFAULT = CostantiConfigurazione.PRIORITA_DEFAULT;
    
    /** Tempi di attesa dello startup per le richieste in ingresso */
    public final static int WAIT_STARTUP_TIMEOUT_SECONDS = 90; 
    public final static int WAIT_STARTUP_CHECK_INTERVAL_MS = 1000; 
    
    /** Tempi di gestione dei lock da parte del Timer */
    public final static int TIMER_LOCK_MAX_LIFE = -1; 
    public final static int TIMER_LOCK_IDLE_TIME = -1; 
    

    /** Identifica il timeout di ricezione sulla connesione jms (5 minuti) */
	public final static long NODE_RECEIVER_ATTESA_ATTIVA = 5 * 60 * 1000;
	/** Intervallo di checl sulla coda in ricezione */
	public final static int NODE_RECEIVER_CHECK_INTERVAL = 10;
	/** Intervallo di check su DB effettuata dal NodeReceiver, in caso di cache attiva */
    public final static int NODE_RECEIVER_CHECK_DB_INTERVAL = 25;
    
    /** Intervallo di check per risposta asincrona */
	public final static int RISPOSTA_ASINCRONA_CHECK_INTERVAL = 500;
	/** Timeout per risposta asincrona: 90 secondi */
    public final static long RISPOSTA_ASINCRONA_ATTESA_ATTIVA = 90000;
   
    /** Return code in gestione CORS */
   	public final static int GESTIONE_CORS_RETURN_CODE = 200;
     
    /** Timeout per la istanziazione della connessione: inoltro buste */
    public static final int CONNETTORE_CONNECTION_TIMEOUT_INOLTRO_BUSTE = 10000;
    /** Timeout per la istanziazione della connessione: consegna contenuti applicativi */
    public static final int CONNETTORE_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI = 10000;
    /** Timeout per la lettura dalla connessione: inoltro buste */
    public static final int CONNETTORE_READ_CONNECTION_TIMEOUT_INOLTRO_BUSTE = 180000;
    /** Timeout per la lettura dalla connessione: consegna contenuti applicativi */
    public static final int CONNETTORE_READ_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI = 120000;
    /** Timeout in millisecondi massimi durante il quale una connessione viene mantenuta aperta dalla PdD: inoltro buste */
    public static final int CONNETTORE_CONNECTION_LIFE_INOLTRO_BUSTE = 1800000;
    /** Timeout in millisecondi massimi durante il quale una connessione viene mantenuta aperta dalla PdD: consegna contenuti applicativi */
    public static final int CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI = 1800000;
    
    /** Dimensione della cache */
    public static final int CONNETTORE_HTTP_URL_HTTPS_CACHE_SIZE = 10000;
    
    /** Configurazione default per refresh cluster dinamico */
    public static final int CLUSTER_DINAMICO_REFRESH_SECONDS = 60;
    
    
    /** Dominio utilizzato per le risorse JMX */
    public static final String JMX_DOMINIO = CostantiJMX.JMX_DOMINIO;
    /** Tipo utilizzato per le risorse JMX */
    public static final String JMX_TYPE = CostantiJMX.JMX_TYPE;
    /** MBean per la Configurazione */
    public static final String JMX_CONFIGURAZIONE_PDD = "ConfigurazionePdD";
    /** MBean per il Registro dei Servizi */
    public static final String JMX_REGISTRO_SERVIZI = "AccessoRegistroServizi";
    /** MBean per il Monitoraggio delle Risorse */
    public static final String JMX_MONITORAGGIO_RISORSE = "MonitoraggioRisorse";
    /** MBean per l'Autorizzazione */
    public static final String JMX_AUTORIZZAZIONE = "DatiAutorizzazione";
    /** MBean per l'Autenticazione */
    public static final String JMX_AUTENTICAZIONE = "DatiAutenticazione";
    /** MBean per la gestione dei token */
    public static final String JMX_TOKEN = "GestioneToken";
    /** MBean per il caching delle risposte */
    public static final String JMX_RESPONSE_CACHING = "ResponseCaching";
    /** MBean per il caching dei keystore */
    public static final String JMX_KEYSTORE_CACHING = "Keystore";
    /** MBean per la gestione della consegna agli applicativi */
    public static final String JMX_LOAD_BALANCER = "LoadBalancer";
    /** MBean per il repository dei messaggi */
    public static final String JMX_REPOSITORY_MESSAGGI = "RepositoryMessaggi";
    /** MBean per lo stato dei servizi PdD */
    public static final String JMX_STATO_SERVIZI_PDD = "StatoServiziPdD";
    /** MBean per le info statistiche */
    public static final String JMX_INFORMAZIONI_STATISTICHE_PDD = "StatistichePdD";
    /** MBean per le system properties */
    public static final String JMX_SYSTEM_PROPERTIES_PDD = "SystemPropertiesPdD";
    /** MBean per la configurazione di sistema */
    public static final String JMX_CONFIGURAZIONE_SISTEMA = "ConfigurazioneSistema";
    /** MBean per il controllo del traffico */
    public static final String JMX_CONTROLLO_TRAFFICO = JMXConstants.JMX_NAME;
    
    
    /** Limit messaggi processati dai Gestori */
    public static final int LIMIT_MESSAGGI_GESTORI = 50;
    
    
    /** Tipo WSDL */
    public final static String SCHEMA_XSD = "Schema xsd dei messaggi";
    public final static String WSDL_CONCETTUALE = "Wsdl concettuale";
    public final static String WSDL_FRUITORE = "Wsdl fruitore";
    public final static String WSDL_EROGATORE = "Wsdl erogatore";
    
    public final static String BUSTA_RISPOSTA = "BustaProprietaHeaderIntegrazione";
    
    
    
    public final static String CHECK_STATO_PDD_METHOD_NAME = "methodName";
    public final static String CHECK_STATO_PDD_ATTRIBUTE_NAME = "attributeName";
    public final static String CHECK_STATO_PDD_ATTRIBUTE_VALUE = "attributeValue";
    public final static String CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE = "attributeBooleanValue";
    public final static String CHECK_STATO_PDD_RESOURCE_NAME = "resourceName";
    public final static String CHECK_STATO_PDD_PARAM_VALUE = "paramValue";
    
    
    
	public final static String CONNETTORE_REQUEST_URL = "CONNETTORE_REQUEST_URL";
	public final static String CONNETTORE_REQUEST_METHOD = "CONNETTORE_REQUEST_METHOD";
	public final static String CONNETTORE_REQUEST_PREFIX_METHOD = "[";
	public final static String CONNETTORE_REQUEST_SUFFIX_METHOD = "]";
	public final static String CONNETTORE_REQUEST_SEPARATOR = " ";
	public final static String getConnettoreRequest(String url, String method) {
		return CONNETTORE_REQUEST_PREFIX_METHOD+method+CONNETTORE_REQUEST_SUFFIX_METHOD+CONNETTORE_REQUEST_SEPARATOR+url;
	}
	public final static String readUrlFromConnettoreRequest(String request) {
		String splitSeparator = CONNETTORE_REQUEST_SUFFIX_METHOD+CONNETTORE_REQUEST_SEPARATOR;
		int indexOf = request.indexOf(splitSeparator);
		if(indexOf>0 && request.length()>(indexOf+splitSeparator.length())) {
			return request.substring(indexOf+splitSeparator.length());
		}
		return null;
	}
	public final static String readMethodFromConnettoreRequest(String request) {
		String splitSeparator = CONNETTORE_REQUEST_SUFFIX_METHOD+CONNETTORE_REQUEST_SEPARATOR;
		int indexOf = request.indexOf(splitSeparator);
		if(indexOf>0) {
			String s = request.substring(0,indexOf);
			if(s.startsWith(CONNETTORE_REQUEST_PREFIX_METHOD)) {
				return s.substring(CONNETTORE_REQUEST_PREFIX_METHOD.length());
			}
		}
		return null;
	}
    
    
       
    
    /* Proprieta di una busta: renderle reperibili TUTTE quando si è letta una busta, in ogni servizio/modulo */
    /* Fare una nota che dica che queste proprietà sono disponibili sempre, non appena è stata letta/costruita una busta */

    /** Costante che identifica una keyword per rappresentare il protocollo */
    public final static String KEY_PROTOCOLLO = "@PROTOCOLLO@";
    /** Costante che identifica una keyword per rappresentare i tipi di soggetti supportati dal protocollo */
    public final static String KEY_PROTOCOLLO_TIPI_SOGGETTI = "@PROTOCOLLO_TIPI_SOGGETTI@";
    /** Costante che identifica una keyword per rappresentare i tipi di servizio supportati dal protocollo */
    public final static String KEY_PROTOCOLLO_TIPI_SERVIZI = "@PROTOCOLLO_TIPI_SERVIZI@";
    
    /** Costante che identifica una keyword per rappresentare l'identificativo della richiesta */
    public final static String KEY_ID_MESSAGGIO_RICHIESTA = "@ID_MESSAGGIO_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare l'identificativo della risposta */
    public final static String KEY_ID_MESSAGGIO_RISPOSTA = "@ID_MESSAGGIO_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare il profilo di collaborazione */
    public final static String KEY_PROFILO_COLLABORAZIONE = "@PROFILO_COLLABORAZIONE@";
    /** Costante che identifica una keyword per rappresentare il riferimento messaggio */
    public final static String KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA = "@RIFERIMENTO_MESSAGGIO_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare il riferimento messaggio */
    public final static String KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA = "@RIFERIMENTO_MESSAGGIO_RISPOSTA@";
        
    /** Costante che identifica una keyword per rappresentare il tipo mittente di una busta richiesta */
    public final static String KEY_TIPO_MITTENTE_BUSTA_RICHIESTA = "@TIPO_MITTENTE_BUSTA_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare il mittente di una busta richiesta */
    public final static String KEY_MITTENTE_BUSTA_RICHIESTA = "@MITTENTE_BUSTA_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare il tipo destinatario di una busta richiesta */
    public final static String KEY_TIPO_DESTINATARIO_BUSTA_RICHIESTA = "@TIPO_DESTINATARIO_BUSTA_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare il destinatario di una busta richiesta */
    public final static String KEY_DESTINATARIO_BUSTA_RICHIESTA = "@DESTINATARIO_BUSTA_RICHIESTA@";	
    /** Costante che identifica una keyword per rappresentare il tipo servizio di una busta richiesta */
    public final static String KEY_TIPO_SERVIZIO_BUSTA_RICHIESTA = "@TIPO_SERVIZIO_BUSTA_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare il servizio di una busta richiesta */
    public final static String KEY_SERVIZIO_BUSTA_RICHIESTA = "@SERVIZIO_BUSTA_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare la versione del servizio di una busta richiesta */
    public final static String KEY_VERSIONE_SERVIZIO_BUSTA_RICHIESTA = "@VERSIONE_SERVIZIO_BUSTA_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare il servizio di una busta azione */
    public final static String KEY_AZIONE_BUSTA_RICHIESTA = "@AZIONE_BUSTA_RICHIESTA@";
    /** Costante che identifica l'indirizzo telematico del mittente della richiesta */
    public final static String KEY_INDIRIZZO_TELEMATICO_MITTENTE_RICHIESTA = "@INDIRIZZO_TELEMATICO_MITTENTE_RICHIESTA@";
    /** Costante che identifica l'indirizzo telematico del destinatario della richiesta */
    public final static String KEY_INDIRIZZO_TELEMATICO_DESTINATARIO_RICHIESTA = "@INDIRIZZO_TELEMATICO_DESTINATARIO_RICHIESTA@";
    /** Costante che identifica una keyword per rappresentare la scadenza */
    public final static String KEY_SCADENZA_BUSTA_RICHIESTA = "@SCADENZA_BUSTA_RICHIESTA@";
    
    /** Costante che identifica una keyword per rappresentare il tipo mittente di una busta risposta */
    public final static String KEY_TIPO_MITTENTE_BUSTA_RISPOSTA = "@TIPO_MITTENTE_BUSTA_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare il mittente di una busta risposta */
    public final static String KEY_MITTENTE_BUSTA_RISPOSTA = "@MITTENTE_BUSTA_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare il tipo destinatario di una busta risposta */
    public final static String KEY_TIPO_DESTINATARIO_BUSTA_RISPOSTA = "@TIPO_DESTINATARIO_BUSTA_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare il destinatario di una busta risposta */
    public final static String KEY_DESTINATARIO_BUSTA_RISPOSTA = "@DESTINATARIO_BUSTA_RISPOSTA@";	
    /** Costante che identifica una keyword per rappresentare il tipo servizio di una busta risposta */
    public final static String KEY_TIPO_SERVIZIO_BUSTA_RISPOSTA = "@TIPO_SERVIZIO_BUSTA_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare il servizio di una busta risposta */
    public final static String KEY_SERVIZIO_BUSTA_RISPOSTA = "@SERVIZIO_BUSTA_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare la versione del servizio di una busta risposta */
    public final static String KEY_VERSIONE_SERVIZIO_BUSTA_RISPOSTA = "@VERSIONE_SERVIZIO_BUSTA_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare il servizio di una busta azione */
    public final static String KEY_AZIONE_BUSTA_RISPOSTA = "@AZIONE_BUSTA_RISPOSTA@";
    /** Costante che identifica l'indirizzo telematico del mittente della risposta */
    public final static String KEY_INDIRIZZO_TELEMATICO_MITTENTE_RISPOSTA = "@INDIRIZZO_TELEMATICO_MITTENTE_RISPOSTA@";
    /** Costante che identifica l'indirizzo telematico del destinatario della risposta */
    public final static String KEY_INDIRIZZO_TELEMATICO_DESTINATARIO_RISPOSTA = "@INDIRIZZO_TELEMATICO_DESTINATARIO_RISPOSTA@";
    /** Costante che identifica una keyword per rappresentare la scadenza */
    public final static String KEY_SCADENZA_BUSTA_RISPOSTA = "@SCADENZA_BUSTA_RISPOSTA@";

    /** Costante che indica la sequenza presente in busta  */
    public final static String KEY_URI_ACCORDO_PARTE_SPECIFICA = "@URI_ACCORDO_PARTE_SPECIFICA@";
    
    /** Costante che indica la sequenza presente in busta  */
    public final static String KEY_SEQUENZA = "@SEQUENZA@";
    
    /** Costante che identifica una keyword per rappresentare il servizio applicativo fruitore */
    public final static String KEY_SA_FRUITORE = "@SERVIZIO_APPLICATIVO_FRUITORE@";
    /** Costante che identifica una keyword per rappresentare il servizio applicativo erogatore */
    public final static String KEY_SA_EROGATORE = "@SERVIZIO_APPLICATIVO_EROGATORE@";
    
    /** Costante che identifica una keyword per rappresentare l'identificativo della correlazione applicativa */
    public final static String KEY_ID_CORRELAZIONE_APPLICATIVA = "@ID_CORRELAZIONE_APPLICATIVA@";
    
    /** Costante che identifica una keyword per rappresentare l'identificativo della correlazione asincrona */
    public final static String KEY_ID_CORRELAZIONE_ASINCRONA = "@ID_CORRELAZIONE_ASINCRONA@";
    
    /** Costante che identifica un errore di processamento generico */
    public final static String KEY_ERRORE_PROCESSAMENTO = "@ERRORE_PROCESSAMENTO@";
    
    /** Costante che fornisce indicazione sul tipo di errore, permette di localizzare l'errore all'interno del modulo funzionale */
    public final static String KEY_POSIZIONE_ERRORE = "@POSIZIONE_ERRORE@";
    
    /** Costante che indica il proprietario di un messaggio  */
    public final static String KEY_PROPRIETARIO_MESSAGGIO = "@PROPRIETARIO_MESSAGGIO@";
    
    /** Costante che identifica un identificativo del messaggio processato dal TransactionManager */
    public final static String KEY_ID_MESSAGGIO_TRANSACTION_MANAGER = "@ID_MESSAGGIO_TRANSACTION_MANAGER@";
    
    /** Costante che identifica un header http */
    public final static String KEY_HTTP_HEADER = "@HEADER_HTTP@";
    
    /** Costante che identifica il namespace del messaggio soap ricevuto */
    public final static String KEY_SOAP_ENVELOPE_NAMESPACE = "@SOAP_NAMESPACE@";
    
    /** Costante che identifica la versione SOAP del messaggio ricevuto */
    public final static String KEY_SOAP_VERSION = "@SOAP_VERSION@";
    
    /** Costante che identifica il namespace del messaggio soap atteso rispetto alla versione soap */
    public final static String KEY_SOAP_ENVELOPE_NAMESPACE_ATTESO = "@SOAP_NAMESPACE_ATTESO@";
    
    /** Costante che identifica i content types attesi per la versione soap */
    public final static String KEY_CONTENT_TYPES_ATTESI = "@CONTENT_TYPES_ATTESI@";
    
    /** Costante che identifica un tipo di messaggio Errore */
    public final static String KEY_TIPO_MESSAGGIO_BUSTA_ERRORE = "@TIPO_ERRORE_BUSTA@";
    public final static String TIPO_MESSAGGIO_BUSTA_ERRORE_PROCESSAMENTO = "Processamento";
    public final static String TIPO_MESSAGGIO_BUSTA_ERRORE_INTESTAZIONE = "Intestazione";
    
    /** Costante che identifica il behaviour personalizzato */
    public final static String KEY_TIPO_BEHAVIOUR = "@TIPO_BEHAVIOUR@";
    /** Costante che descrive il behaviour personalizzato */
    public final static String KEY_DESCRIZIONE_BEHAVIOUR = "@DESCRIZIONE_BEHAVIOUR@";
    
    /** Costante che identifica il tipo di selettore per l'indentificazione dinamica dei connettori */
    public final static String KEY_TIPO_SELETTORE = "@TIPO_SELETTORE@";
    /** Costante che identifica il patter utilizzato dal selettore per l'indentificazione dinamica dei connettori */
    public final static String KEY_PATTERN_SELETTORE = "@PATTERN_SELETTORE@";
    /** Costante che identifica la condizione estratta per l'indentificazione dinamica dei connettori */
    public final static String KEY_CONDIZIONE_CONNETTORE =  "@CONDIZIONE_CONNETTORE@";
    /** Costante che identifica il nome di un connettore */
    public final static String KEY_NOME_CONNETTORE = "@NOME_CONNETTORE@";
    /** Costante che identifica la condizione estratta per lo sticky */
    public final static String KEY_CONDIZIONE_STICKY =  "@CONDIZIONE_STICKY@";
    
    
    /* Proprietà specifiche per modulo funzionale */
    
    /** Costante che identifica una keyword per rappresentare l'identificativo da inoltrare */
    public final static String KEY_ID_MESSAGGIO_DA_INOLTRARE = "@ID_MESSAGGIO_DA_INOLTRARE@";
    /** Costante che identifica una keyword per rappresentare l'identificativo da eliminare */
    public final static String KEY_ID_MESSAGGIO_DA_ELIMINARE = "@ID_MESSAGGIO_DA_ELIMINARE@";
    /** Costante che identifica una keyword per rappresentare un tipo di messaggio INBOX/OUTBOX */
    public final static String KEY_TIPO_MESSAGGIO = "@TIPO_MESSAGGIO@";
    /** Costante che identifica una keyword per rappresentare la scadenza di un messaggio */
    public final static String KEY_SCADENZA_MESSAGGIO = "@SCADENZA_MESSAGGIO@";
    /** Costante che identifica una keyword per rappresentare le credenziali utilizzate dal servizio applicativo fruitore */
    public final static String KEY_CREDENZIALI_SA_FRUITORE = "@CREDENZIALI_SA_FRUITORE@";
    /** Costante che identifica una keyword per rappresentare le credenziali utilizzate dal mittente */
    public final static String KEY_CREDENZIALI = "@CREDENZIALI@";
    /** Costante che identifica una keyword per rappresentare la porta delegata */
    public final static String KEY_PORTA_DELEGATA = "@PORTA_DELEGATA@";
    /** Costante che identifica una keyword per rappresentare la porta applicativa */
    public final static String KEY_PORTA_APPLICATIVA = "@PORTA_APPLICATIVA@";
    /** Costante che identifica una keyword per rappresentare la tipologia della risposta applicativa ritornata (es. ricevuta asincrona simmetrica) */
    public final static String KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA = "@TIPOLOGIA_RISPOSTA_APPLICATIVA@";
    /** Costante che identifica una keyword per rappresentare un timeout */
    public final static String KEY_TIMEOUT = "@TIMEOUT@";
    /** Costante che identifica una keyword per rappresentare un limit */
    public final static String KEY_LIMIT = "@LIMIT@";
    /** Costante che identifica una keyword per rappresentare un tipo di statistica */
    public final static String KEY_TIPO_STATISTICA = "@TIPO_STATISTICA@";
    /** Costante che identifica una keyword per rappresentare un intervallo temporale */
    public final static String KEY_TEMPO_GENERAZIONE = "@TEMPO_GENERAZIONE@";
    /** Costante che identifica il tipo di autorizzazione */
    public final static String KEY_TIPO_AUTORIZZAZIONE = "@TIPO_AUTORIZZAZIONE@";
    /** Costante che identifica il tipo di autorizzazione buste */
    public final static String KEY_TIPO_AUTORIZZAZIONE_CONTENUTO = "@TIPO_AUTORIZZAZIONE_CONTENUTO@";
    /** Costante che identifica il tipo di autorizzazione buste */
    public final static String KEY_TIPO_AUTORIZZAZIONE_BUSTE = "@TIPO_AUTORIZZAZIONE_BUSTE@";
    /** Costante che identifica il tipo di autenticazione */
    public final static String KEY_TIPO_AUTENTICAZIONE = "@TIPO_AUTENTICAZIONE@";
    /** Costante che identifica la policy di gestione token */
    public final static String KEY_TOKEN_POLICY_GESTIONE = "@TOKEN_POLICY_GESTIONE@";
    /** Costante che identifica il tipo di token */
    public final static String KEY_TOKEN_TIPO = "@TOKEN_TIPO@";
    /** Costante che identifica le azioni intraprese sul token */
    public final static String KEY_TOKEN_POLICY_AZIONI = "@TOKEN_POLICY_AZIONI@";
    /** Costante che identifica la posizione del token */
    public final static String KEY_TOKEN_POSIZIONE = "@TOKEN_POSIZIONE@";
    /** Costante che identifica il token */
    public final static String KEY_TOKEN = "@TOKEN@";
    /** Costante che identifica le informazioni sul token */
    public final static String KEY_TOKEN_INFO = "@TOKEN_INFO@";
    /** Costante che identifica l'endpoint del servizio di introspection */
    public final static String KEY_TOKEN_ENDPOINT_SERVIZIO_INTROSPECTION = "@TOKEN_ENDPOINT_SERVIZIO_INTROSPECTION@";
    /** Costante che identifica l'endpoint del servizio di userInfo */
    public final static String KEY_TOKEN_ENDPOINT_SERVIZIO_USER_INFO = "@TOKEN_ENDPOINT_SERVIZIO_USER_INFO@";
    /** Costante che identifica le informazioni di autenticazione controllate nel token */
    public final static String KEY_TOKEN_AUTHN_CHECK = "@TOKEN_AUTHN_CHECK@";
    /** Costante che identifica il nome del servizio di attribute authority */
    public final static String KEY_ATTRIBUTE_AUTHORITY_NAME = "@ATTRIBUTE_AUTHORITY_NAME@";
    /** Costante che identifica l'endpoint del servizio di attribute authority */
    public final static String KEY_ATTRIBUTE_AUTHORITY_ENDPOINT = "@ATTRIBUTE_AUTHORIY_ENDPOINT@";
    /** Costante che identifica gli attributi recuperati */
    public final static String KEY_ATTRIBUTES = "@ATTRIBUTES@";
    /** Costante che identifica il tipo di validazione contenuti */
    public final static String KEY_TIPO_VALIDAZIONE_CONTENUTI = "@TIPO_VALIDAZIONE_CONTENUTI@";
    /** Costante che identifica dettagli ulteriori sulla validazione contenuti */
    public final static String KEY_DETAILS_VALIDAZIONE_CONTENUTI = "@DETAILS_VALIDAZIONE_CONTENUTI@";    
    /** Costante che identifica la tipologia di processamento MTOM della richiesta */
    public final static String KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA = "@TIPO_PROCESSAMENTO_MTOM_RICHIESTA@";
    /** Costante che identifica la tipologia di processamento MTOM della risposta */
    public final static String KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA = "@TIPO_PROCESSAMENTO_MTOM_RISPOSTA@";
    /** Costante che identifica il tipo di sicurezza sul messaggio applicato alla richiesta */
    public final static String KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA = "@TIPO_SICUREZZA_MESSAGGIO_RICHIESTA@";
    /** Costante che identifica il tipo di sicurezza sul messaggio applicato alla risposta */
    public final static String KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA = "@TIPO_SICUREZZA_MESSAGGIO_RISPOSTA@";    
    /** Costante che identifica la trasformazione applicata alla richiesta */
    public final static String KEY_TIPO_TRASFORMAZIONE_RICHIESTA = "@TIPO_TRASFORMAZIONE_RICHIESTA@";
    /** Costante che identifica la trasformazione applicata alla risposta */
    public final static String KEY_TIPO_TRASFORMAZIONE_RISPOSTA = "@TIPO_TRASFORMAZIONE_RISPOSTA@";       
    /** Costante che identifica il tipo di messaggio (normale, ricevuta asincrona...) */
    public final static String KEY_TIPO_MESSAGGIO_BUSTA = "@TIPO_MESSAGGIO_BUSTA@"; 
    /** Costante che identifica tipo/nome mittente e tipo/nome destinatario erogatore del servizio da autorizzare, calcolato in base al tipo di messaggio ricevuto.  */
    public final static String KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE = "@MITTENTE_E_SERVIZIO_DA_AUTORIZZARE@"; 
    /** Costante che identifica un messaggio contenente le credenziali del mittente.  */
    public final static String KEY_CREDENZIALI_MITTENTE_MSG = "@CREDENZIALI_MITTENTE_MSG@"; 
    /** Costante che identifica un messaggio contenente  l'identità del servizio applicativo fruitore */
    public final static String KEY_SA_FRUITORE_MSG = "@SERVIZIO_APPLICATIVO_FRUITORE_MSG@";
    /** Costante che identifica un messaggio contenente il subject portato nell'header MessageSecurity  */
    public final static String KEY_SUBJECT_MESSAGE_SECURITY_MSG = "@SUBJECT_MESSAGE_SECURITY_MSG@"; 
    /** Costante che identifica il tipo di una ricevuta asincrona */
    public final static String KEY_TIPO_RICEVUTA_ASINCRONA = "@TIPO_RICEVUTA_ASINCRONA@";
    /** Costante che indica le eccezioni riscontrate dalla validazione della busta, o portate in un msg di protocollo errore */
    public final static String KEY_ECCEZIONI = "@ECCEZIONI@";
    /** Costante che indica il numero eccezioni riscontrate da una validazione della busta, o numero eccezioni portate in un msg di protocollo errore*/
    public final static String KEY_NUMERO_ECCEZIONI = "@NUMERO_ECCEZIONI@";
    /** Costante che indica le eccezioni riscontrate dalla validazione della busta, o portate in un msg di protocollo errore */
    public final static String KEY_OPENSPCOOP2_DETAILS = "@OPENSPCOOP2_DETAILS@";
    /** Costante che indica l'identificativo di un riscontro */
    public final static String KEY_ID_BUSTA_RISCONTRATA = "@ID_BUSTA_RISCONTRATA@";
    /** Costante che indica la data in cui una busta è stata riscontrata */
    public final static String KEY_DATA_RISCONTRO = "@DATA_RISCONTRO@";
    /** Costante che indica la sequenza attesa per la consegna */
    public final static String KEY_SEQUENZA_ATTESA = "@SEQUENZA_ATTESA@";
    /** Costante che indica il tipo di connettore utilizzato nella consegna */
    public final static String KEY_TIPO_CONNETTORE = "@TIPO_CONNETTORE@";
    /** Costante che indica url/codaJMS/risorsa verso cui la porta sta consegnando il messaggio */
    public final static String KEY_LOCATION = "@LOCATION@";
    /** Costante che indica il codice della consegna (es. http, codice di trasporto) */
    public final static String KEY_CODICE_CONSEGNA = "@CODICE_CONSEGNA@";
    /** Costante che indica l'eventuale motivo di errore riscontrato durante la consegna del messaggio */
    public final static String KEY_ERRORE_CONSEGNA = "@ERRORE_CONSEGNA@";
    /** Costante che indica l'eventuale destinatario fisico (next hop) a cui viene trasmesso la busta (es. Router) */
    public final static String KEY_DESTINATARIO_TRASMISSIONE = "@DESTINATARIO_TRASMISSIONE@";
    /** Costante che indica la descrizione degli elementi principali che formano un soap fault ricevuto */
    public final static String KEY_SOAP_FAULT = "@SOAP_FAULT@";
    /** Costante che indica la descrizione degli elementi principali che formano un problem detail ricevuto */
    public final static String KEY_REST_PROBLEM = "@REST_PROBLEM@";
    /** Costante (true/false) indica se la funzionalità è stata richiesta nell'accordo*/
    public final static String KEY_FUNZIONALITA_COLLABORAZIONE = "@FUNZIONALITA_COLLABORAZIONE@";
    /** Costante (true/false) indica se la funzionalità è stata richiesta nell'accordo*/
    public final static String KEY_FUNZIONALITA_CONFERMA_RICEZIONE = "@FUNZIONALITA_CONFERMA_RICEZIONE@";
    /** Costante (true/false) indica se la funzionalità è stata richiesta nell'accordo*/
    public final static String KEY_FUNZIONALITA_FILTRO_DUPLICATI = "@FUNZIONALITA_FILTRO_DUPLICATI@";
    /** Costante che indica il tipo di scenario gestito */
    public final static String KEY_SCENARIO_COOPERAZIONE_GESTITO = "@SCENARIO_COOPERAZIONE_GESTITO@";
    /** Costante che indica informazioni sul servizio da fruire */
    public final static String KEY_INFO_SERVIZIO_BUSTA = "@INFO_SERVIZIO_BUSTA@";
    /** Costante che indica il tipo di operazione invocata attraverso il servizio di IntegrationManager */
    public final static String KEY_TIPO_OPERAZIONE_IM = "@TIPO_OPERAZIONE_IM@";
    /** Costante che indica i parametri di una operazione invocata attraverso il servizio di IntegrationManager */
    public final static String KEY_PARAMETRI_OPERAZIONE_IM = "@PARAMETRI_OPERAZIONE_IM@";
    /** Costante che indica il messaggio richiesto tramite una operazione invocata attraverso il servizio di IntegrationManager */
    public final static String KEY_IDMESSAGGIO_OPERAZIONE_IM = "@ID_MESSAGGIO_OPERAZIONE_IM@";
    /** Costante che indica il tipo di header di integrazione */
    public final static String KEY_TIPO_HEADER_INTEGRAZIONE = "@TIPO_HEADER_INTEGRAZIONE@";
    public final static String KEY_TIPI_INTEGRAZIONE = "@TIPI_INTEGRAZIONE@";
    /** Costante che indica il tipo di gestore credenziali */
    public final static String KEY_TIPO_GESTORE_CREDENZIALI = "@TIPO_GESTORE_CREDENZIALI@";
    /** Costante che indica le credenziali ottenute via gestore delle credenziali */
    public final static String KEY_NUOVE_CREDENZIALI = "@NUOVE_CREDENZIALI@";
    /** Costante che indica l'identità assunta dal gestore delle credenziali (es. Gateway Ministero Interno) */
    public final static String KEY_IDENTITA_GESTORE_CREDENZIALI = "@IDENTITA_GESTORE_CREDENZIALI@";
    /** Costante che indica il tipo di validazione */
    public final static String KEY_TIPO_VALIDAZIONE_BUSTA = "@TIPO_VALIDAZIONE_BUSTA@";

    /** Costante che indica il Tipo di appender utilizzato per il tracciamento */
    public final static String KEY_TRACCIAMENTO_PERSONALIZZATO = "@TIPO_TRACCIAMENTO_PERSONALIZZATO@";
    /** Costante che indica l' errore che non ha permesso il tracciamento */
    public final static String KEY_TRACCIAMENTO_ERRORE = "@ERRORE_TRACCIAMENTO@";
    /** Costante che indica il tipo di traccia */
    public final static String KEY_TRACCIA_TIPO = "@TIPO_TRACCIA@";
    /** Costante che indica la traccia */
    public final static String KEY_TRACCIA = "@TRACCIA@";
   
    /** Costante che identifica una keyword per la causale di un lock */
    public final static String KEY_LOCK_CAUSALE = "@CAUSA_LOCK@";
    /** Costante che identifica una keyword per l'identificativo del modulo che ha acquisito un lock */
    public final static String KEY_LOCK_ID_MODULO = "@ID_MODULO_PROPRIETARIO_LOCK@";
    
    /** Costante che identifica una keyword per rappresentare l'identificatore del TimerGestoreRiscontriRicevute */
    public final static String KEY_TIMER_GESTORE_RISCONTRI_RICEVUTE = "@ID_TIMER_GESTORE_RISCONTRI_RICEVUTE@";
    
    /** Costante che identifica una keyword per rappresentare l'identificatore del TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI = "@ID_TIMER_GESTORE_MESSAGGI@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi completati in INBOX trovati da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_INBOX_COMPLETATI = "@NUMERO_MESSAGGI_COMPLETATI_INBOX@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi completati in OUTBOX trovati da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_OUTBOX_COMPLETATI = "@NUMERO_MESSAGGI_COMPLETATI_OUTBOX@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi scaduti in INBOX trovati da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_INBOX_SCADUTI = "@NUMERO_MESSAGGI_SCADUTI_INBOX@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi scaduti in OUTBOX trovati da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_OUTBOX_SCADUTI = "@NUMERO_MESSAGGI_SCADUTI_OUTBOX@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi non gestiti dal servizio PD trovati da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_NON_GESTITI_RICEZIONE_CONTENUTI_APPLICATIVI = "@NUMERO_MESSAGGI_NON_GESTITI_RICEZIONE_CONTENUTI_APPLICATIVI@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi non gestiti dal servizio PA trovati da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_NON_GESTITI_RICEZIONE_BUSTE = "@NUMERO_MESSAGGI_NON_GESTITI_RICEZIONE_BUSTE@";
    /** Costante che identifica una keyword per rappresentare il numero di correlazioni appplicative scadute trovate da eliminare dal TimerGestoreMessaggi */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_NUM_CORRELAZIONI_APPLICATIVE_SCADUTE = "@NUMERO_CORRELAZIONI_APPLICATIVE@";
    /** Costante che identifica una keyword per rappresentare il tipo di ricerca effettuato dal TimerGestoreMessaggi per trovare i messaggi da eliminare */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE = "@TIPO_RICERCA@";
    /** Costante che identifica una keyword per rappresentare se la ricerca effettuato dal TimerGestoreMessaggi per trovare i messaggi da eliminare è con riferimento o meno (true/false) */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO = "@RICERCA_TRAMITE_RIFERIMENTO@";
    /** Costante che identifica una keyword per rappresentare la coda JMS in ricezione su cui sta filtrando il TimerGestoreMessaggi per trovare i messaggi da eliminare */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE = "@CODA_JMS_RICEZIONE@";
    /** Costante che identifica una keyword per rappresentare la tipologia di connessione (http/jms PD/PA id) ancora aperta */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_TIPO_CONNESSIONE = "@TIPO_CONNESSIONE@";
    /** Costante che identifica una keyword per rappresentare la data di creazione della connessione ancora aperta */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_DATA_CREAZIONE_CONNESSIONE = "@DATA_CREAZIONE_CONNESSIONE@";
    
    /** Costante che identifica una keyword per rappresentare l'identificatore di un Timer */
    public final static String KEY_TIMER = "@ID_TIMER@";
    
    /** Costante che identifica una keyword per rappresentare il numero di messaggi inoltrati */
    public final static String KEY_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI_NUMERO_MESSAGGI_INOLTRATI = "@NUMERO_MESSAGGI_INOLTRATI@";
    
    /** Costante che identifica una keyword per rappresentare l'identificatore del TimerGestoreMessaggiAnomali */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI = "@ID_TIMER_GESTORE_MESSAGGI_INCONSISTENTI@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi inconsistenti in INBOX trovati da eliminare dal Timer */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI_NUM_MSG_INBOX = "@NUMERO_MESSAGGI_INCONSISTENTI_INBOX@";
    /** Costante che identifica una keyword per rappresentare il numero di messaggi inconsistenti in OUTBOX trovati da eliminare dal Timer */
    public final static String KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI_NUM_MSG_OUTBOX = "@NUMERO_MESSAGGI_INCONSISTENTI_OUTBOX@";
    
    /** Costante che identifica una keyword per rappresentare l'identificatore del TimerGestoreRepositoryBuste */
    public final static String KEY_TIMER_GESTORE_REPOSITORY_BUSTE = "@ID_TIMER_GESTORE_REPOSITORY_BUSTE@";
    /** Costante che identifica una keyword per rappresentare il numero di buste in INBOX trovati da eliminare dal Timer */
    public final static String KEY_TIMER_GESTORE_REPOSITORY_BUSTE_NUM_MSG_INBOX = "@NUMERO_BUSTE_INBOX@";
    /** Costante che identifica una keyword per rappresentare il numero di buste in OUTBOX trovati da eliminare dal Timer */
    public final static String KEY_TIMER_GESTORE_REPOSITORY_BUSTE_NUM_MSG_OUTBOX = "@NUMERO_BUSTE_OUTBOX@";
    
    /** Costante che identifica una keyword per rappresentare l'identificatore del TimerMonitoraggioRisorse */
    public final static String KEY_TIMER_MONITORAGGIO_RISORSE = "@ID_TIMER_MONITORAGGIO_RISORSE@";
    /** Costante che identifica una keyword per rappresentare una risorsa non disponibile rilevata dal TimerMonitoraggioRisorse */
    public final static String KEY_RISORSA_NON_DISPONIBILE = "@RISORSA_NON_DISPONIBILE@";
    /** Costante che identifica una keyword per rappresentare l'identificatore del TimerThreshold */
    public final static String KEY_TIMER_THRESHOLD = "@ID_TIMER_THRESHOLD@";
    /** Costante che identifica una keyword per rappresentare l'identificatore del tipo di controllo Threshold */
    public final static String KEY_TIMER_THRESHOLD_TIPO = "@TIPO_THRESHOLD@";
    
    /** Costante che identifica una keyword per rappresentare la versione della porta */
    public final static String KEY_VERSIONE_PORTA = "@VERSIONE@";
    /** Costante che identifica una keyword per rappresentare il tempo di avvio della porta */
    public final static String KEY_TEMPO_AVVIO = "@TEMPO_DI_AVVIO@";
    
    /** Costante che identifica una keyword per rappresentare dettagli */
    public final static String KEY_DETAILS = "@DETAILS@";
    
    /** Costante che identifica una keyword per rappresentare l'informazione in cache */
    private final static String KEY_INFO_IN_CACHE = "@INFO_IN_CACHE@";
    private final static String KEY_INFO_IN_CACHE_VALUE = " (in cache)";
    public final static String KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE = "RESULT_AUTHN_IN_CACHE";
    public final static String KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE = "RESULT_AUTHZ_IN_CACHE";
    public final static String KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI = "RESULT_AUTHZ_CONTENUTI_IN_CACHE";
    public final static void addKeywordInCache(MsgDiagnostico msgDiag, boolean isEsitoPresenteInCache, PdDContext pddContext, String ID_FUNCTION) {
    	if(isEsitoPresenteInCache){
			msgDiag.addKeyword(CostantiPdD.KEY_INFO_IN_CACHE, CostantiPdD.KEY_INFO_IN_CACHE_VALUE);
		}else{
			msgDiag.addKeyword(CostantiPdD.KEY_INFO_IN_CACHE, "");
		}
    	pddContext.addObject(ID_FUNCTION, isEsitoPresenteInCache);
    }
   
    
    
    public final static String OPTIONS_MESSAGE_FACTORY = "MessageEngine";
    
    
    public final static String PREFIX_HTTP_STATUS_CODE_IN = "In=";
    public final static String PREFIX_HTTP_STATUS_CODE_OUT = "Out=";
    public final static String PREFIX_API = "API=";
    
    
    public final static String PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO = "gw-";
    public final static String SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO = "-";
    
    
    public final static String PREFIX_TIMEOUT_REQUEST = org.openspcoop2.core.constants.Costanti.PREFIX_TIMEOUT_REQUEST;
    public final static String PREFIX_TIMEOUT_RESPONSE = org.openspcoop2.core.constants.Costanti.PREFIX_TIMEOUT_RESPONSE;
    
    public final static String PREFIX_LIMITED_REQUEST = org.openspcoop2.core.constants.Costanti.PREFIX_LIMITED_REQUEST;
    public final static String PREFIX_LIMITED_RESPONSE = org.openspcoop2.core.constants.Costanti.PREFIX_LIMITED_RESPONSE;
    
    
    public final static String CONNETTORE_MULTIPLO_SELEZIONATO = "CONNETTORE_MULTIPLO_SELEZIONATO";
}





