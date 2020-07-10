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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TransferLengthModes;


/**
 * ConnettoriCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriCostanti {

	/* OBJECT NAME */

	public final static String OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES = "connettoriCustomProprieta";

	/* SERVLET NAME */

	public final static String SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_ADD = OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES
			+ "Add.do";
	public final static String SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_DELETE = OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES
			+ "Del.do";
	public final static String SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST = OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES
			+ "List.do";
	public final static Vector<String> SERVLET_CONNETTORI_CUSTOM_PROPERTIES = new Vector<String>();
	static {
		SERVLET_CONNETTORI_CUSTOM_PROPERTIES.add(SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_ADD);
		SERVLET_CONNETTORI_CUSTOM_PROPERTIES.add(SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_DELETE);
		SERVLET_CONNETTORI_CUSTOM_PROPERTIES.add(SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST);
	}
	
	/* LABEL GENERALI */

	public final static String LABEL_SERVER = "Applicativo Server";
	public final static String LABEL_CONNETTORE = "Connettore";
	public final static String LABEL_DOWNLOAD_CERTIFICATI_SERVER = "Download Certificati Server";
	public final static String LABEL_CONNETTORE_ABILITATO = "Abilitato";
	public final static String LABEL_CONNETTORE_PROXY = "Proxy";
	public final static String LABEL_CONNETTORE_HTTP = "Autenticazione Http";
	public final static String LABEL_CONNETTORE_BEARER = "Autenticazione Token";
	public final static String LABEL_CONNETTORE_HTTPS = "Autenticazione Https";
	public final static String LABEL_CONNETTORE_TEMPI_RISPOSTA = "Tempi Risposta";
	public final static String LABEL_CONNETTORE_OPZIONI_AVANZATE = "Opzioni Avanzate";
	public final static String LABEL_CONNETTORE_PROPRIETA = "Propriet&agrave;";
	public final static String LABEL_CONNETTORE_AUTENTICAZIONE = "Autenticazione Https";
	public final static String LABEL_CONNETTORE_AUTENTICAZIONE_SERVER = "Autenticazione Server";
	public final static String LABEL_CONNETTORE_AUTENTICAZIONE_CLIENT = "Autenticazione Client";
	public final static String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CODA = "Dati Configurazione Coda";
	public final static String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONNESIONE = "Dati Configurazione Connessione";
	public final static String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONTESTO_JNDI = "Contesto JNDI";
	public final static String LABEL_CONNETTORE_REQUEST_OUTPUT = "Richiesta";
	public final static String LABEL_CONNETTORE_RESPONSE_INPUT = "Risposta";
	public final static String LABEL_CONNETTORE_CUSTOM = "Personalizzato";
	public final static String LABEL_PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER = CostantiControlStation.LABEL_PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER;
	public final static String LABEL_PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER = CostantiControlStation.LABEL_PARAMETRO_ID_APPLICATIVO_SERVER;

	/* PARAMETRI */

	public final static String PARAMETRO_CONNETTORE_ID = "id";	
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "endpointtype";
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP = "endpointtype_http";
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS = "endpointtype_https";
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK = "endpointtype_ckb";
	public final static String PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "tipoconn";
	public final static String PARAMETRO_CONNETTORE_DEBUG = "connettore_debug";
	public final static String PARAMETRO_CONNETTORE_URL = "url";
//	public final static String PARAMETRO_CONNETTORE_PROVIDER = "provider";
	public final static String PARAMETRO_CONNETTORE_PROFILO = "profilo";
	public final static String PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER = CostantiControlStation.PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER;
	public final static String PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER = CostantiControlStation.PARAMETRO_ID_APPLICATIVO_SERVER;
	
	public final static String PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE = "tipoauthInv";
	public final static String PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME = "utenteInv";
	public final static String PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "passwordInv";
	
	public final static String PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE = "tipoauthCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME = "utenteCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "passwordCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "subjectCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER = "subjectIssuer";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS = "apiKeyMultipleCred";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY = "apiKeyCred";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID = "appIdCred";
	public final static boolean PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE = false;
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "principalCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL = "confSSLCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO = "confSSLCredTipoArch";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO = "confSSLCredFileCert";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD= "confSSLCredFileCertPwd";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO = "confSSLCredAliasCert";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT = "confSSLCredAliasCertSub";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER = "confSSLCredAliasCertIss";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE = "confSSLCredAliasCertType";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION = "confSSLCredAliasCertVers";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER = "confSSLCredAliasCertSN";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED = "confSSLCredAliasCertSS";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE = "confSSLCredAliasCertNB";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER = "confSSLCredAliasCertNA";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI = "confSSLCredVerifTutti";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED = "confSSLManSS";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP = "confSSLCredWizStep";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_DOWNLOAD= "confSSLCredFileCertLink";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_MODIFICA = "confSSLCredFileModificaCertLink";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD = "changepwd";
	
	public final static String PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO = "connettore_token_policy_stat";
	public final static String PARAMETRO_CONNETTORE_TOKEN_POLICY = "connettore_token_policy";
	
	public final static String PARAMETRO_CONNETTORE_PROXY_ENABLED = "connettore_proxy_enabled";
	public final static String PARAMETRO_CONNETTORE_PROXY_HOSTNAME = "connettore_proxy_host";
	public final static String PARAMETRO_CONNETTORE_PROXY_PORT = "connettore_proxy_port";
	public final static String PARAMETRO_CONNETTORE_PROXY_USERNAME = "connettore_proxy_username";
	public final static String PARAMETRO_CONNETTORE_PROXY_PASSWORD = "connettore_proxy_password";
	
	public final static String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE = "connettore_tempi_redefine";
	public final static String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT = "connettore_tempi_ct";
	public final static String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT = "connettore_tempi_rt";
	public final static String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA = "connettore_tempi_avg";
	
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE = "opzioni_avanzate";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE = "transfer_mode";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE = "transfer_chunk_size";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE = "redirect_mode";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP = "redirect_max_hop";
	
	public final static String PARAMETRO_CONNETTORE_JMS_NOME_CODA = "nomeJms";
	public final static String PARAMETRO_CONNETTORE_JMS_TIPO_CODA = "tipoJms";
	public final static String PARAMETRO_CONNETTORE_JMS_USERNAME = "userJms";
	public final static String PARAMETRO_CONNETTORE_JMS_PASSWORD = "passwordJms";
	public final static String PARAMETRO_CONNETTORE_JMS_INIT_CTX = "initcont";
	public final static String PARAMETRO_CONNETTORE_JMS_URL_PKG = "urlpgk";
	public final static String PARAMETRO_CONNETTORE_JMS_PROVIDER_URL = "provurl";
	public final static String PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY = "connfact";
	public final static String PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS = "sendas";
		
	public final static String PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH = "clientAuth";
	public final static String PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE = "stato";
	public final static String PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE ="httpstipologia";
	public final static String PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY = "httpshostverify";
	public final static String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "httpspath";
	public final static String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE = "httpstipo";
	public final static String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = "httpspwd";
	public final static String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL = "httpscrl";
	public final static String PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "httpsalgoritmo";
	public final static String PARAMETRO_CONNETTORE_HTTPS_STATO = "httpsstato";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE = "httpskeystore";
	public final static String PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "httpspwdprivatekeytrust";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "httpspathkey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "httpstipokey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "httpspwdkey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "httpspwdprivatekey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE = "httpsaliasprivatekey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "httpsalgoritmokey";
	
	public final static String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME = "requestOutputFileName";
	public final static String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS = "requestOutputFileNameHeaders";
	public final static String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "requestOutputDirectoryAutoCreate";
	public final static String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME = "requestOutputOverwriteFileName";
	public final static String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE = "responseInputMode";
	public final static String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME = "responseInputFileName";
	public final static String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS = "responseInputFileNameHeaders";
	public final static String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ = "responseInputFileNameDelete";
	public final static String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "responseInputWaitTime";
	
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID = "id";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_MY_ID = "myId";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_SERVLET = "servlet";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO = "nomeprov";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO = "tipoprov";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO = "nomeservizio";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO = "tiposervizio";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_VERSIONE_SERVIZIO = "versioneservizio";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_CORRELATO = "correlato";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE = "idSoggErogatore";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO = "nomeservizioApplicativo";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO = "idsil";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME = "nome";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_VALORE = "valore";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO = "tipoAccordo";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_PROVIDER = "provider";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA = "proprietaConnettoreCustom";
	public final static String PARAMETRO_CONNETTORE_MODALITA = "modCon";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA = CostantiControlStation.PARAMETRO_ID_PORTA;
	
	
	public final static String PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX = "__SA";
	
	
	/* LABEL PARAMETRI */

	public final static int LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE = CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE;
	
	public final static String LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "Tipo Personalizzato";
	public final static String LABEL_PARAMETRO_CONNETTORE_DEBUG = "Debug";
	public final static String LABEL_PARAMETRO_CONNETTORE_URL = "Endpoint";
	public final static int LABEL_PARAMETRO_CONNETTORE_URL_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_PROFILO =  "Versione Protocollo";
	
	public final static String AUTENTICAZIONE_TIPO_NESSUNA = "nessuna";
	public final static String AUTENTICAZIONE_TIPO_BASIC = CostantiConfigurazione.CREDENZIALE_BASIC.toString();
	public final static String AUTENTICAZIONE_TIPO_APIKEY = CostantiConfigurazione.CREDENZIALE_APIKEY.toString();
	public final static String AUTENTICAZIONE_TIPO_SSL = CostantiConfigurazione.CREDENZIALE_SSL.toString();
	public final static String AUTENTICAZIONE_TIPO_PRINCIPAL = CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString();
	public final static String LABEL_AUTENTICAZIONE_TIPO_BASIC = CostantiConfigurazione.LABEL_CREDENZIALE_BASIC;
	public final static String LABEL_AUTENTICAZIONE_TIPO_APIKEY = CostantiConfigurazione.LABEL_CREDENZIALE_APIKEY;
	public final static String LABEL_AUTENTICAZIONE_TIPO_SSL = CostantiConfigurazione.LABEL_CREDENZIALE_SSL;
	public final static String LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL = CostantiConfigurazione.LABEL_CREDENZIALE_PRINCIPAL;
	public final static String DEFAULT_AUTENTICAZIONE_TIPO = AUTENTICAZIONE_TIPO_NESSUNA;
	
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME = "Utente";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "Subject";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER = "Issuer";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR = "Multiple API Keys";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS = "App ID";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_EMPTY_LABEL = ""; //"App ID";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID = "App ID";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY = "Api Key";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "UserId";

	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_PASSWORD = "Nuova "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_API_KEY = "Nuova "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY;
	public final static int API_KEY_ROWS = 5;
	
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_PASSWORD = "Modifica "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_API_KEY = "Aggiorna "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY;
	
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME = "Hostname";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_PORT = "Porta";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME = "Username";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD = "Password";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE = "Ridefinisci Tempi Risposta";
	public final static String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT = "Connection Timeout";
	public final static String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT = "Read Timeout";
	public final static String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA = "Tempo Medio di Risposta";
	public final static String LABEL_PARAMETRO_CONNETTORE_TEMPI_MILLISECONDI_NOTE = "Indicazione in millisecondi (ms)";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE = LABEL_CONNETTORE_OPZIONI_AVANZATE;
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE = "Modalità Data Transfer";
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE = "Chunk Length (Bytes)";
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE = "Gestione Redirect";
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP = "Max Numero di Redirect";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_TOKEN_POLICY = "Policy";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA = "Nome";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_CODA = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME = "Utente";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX = "Initial Context Factory";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG = "Url Pgk Prefixes";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL = "Provider Url";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY = "Connection Factory";
	public final static String LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS = "Send As";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_URL = "Endpoint";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH = "Client-Auth";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE = "Tipologia";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY = "HostnameVerifier";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "Path";
	public final static int LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD ="Password";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL = "CRL File(s)";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL_NOTE = "Elencare più file separandoli con la ','";
	public final static int LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL_SIZE = 2;
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "Algoritmo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO = "Abilitato";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE ="Dati Accesso al KeyStore";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "Password Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "Path";
	public final static int LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "Password Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE = "Alias Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "Algoritmo";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME = "File";
	public final static int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS = "File Headers";
	public final static int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "AutoCreate Parent Dir";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME = "Overwrite If Exists";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE = "Generazione";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME = "File";
	public final static int LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS = "File Headers";
	public final static int LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ = "Delete After Read";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "WaitTime ifNotExists (ms)";
		
	public final static String LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE = "Valore";
	
	public final static String LABEL_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO = "Connettore ridefinito per il gruppo";
	public final static String LABEL_PARAMETRO_MODALITA_CONNETTORE_DEFAULT = "Utilizza connettore del gruppo '"+Costanti.MAPPING_DESCRIZIONE_DEFAULT+"'";
	public final static String LABEL_PARAMETRO_CONNETTORE_MODALITA = "Modalit&agrave;";
	
	public final static String LABEL_CONFIGURAZIONE_SSL_TITLE_CONFIGURAZIONE = "Configurazione";
	public final static String LABEL_CONFIGURAZIONE_SSL_TITLE_INFORMAZIONI_CERTIFICATO = "Certificato";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO = "Formato";//"Tipo Archivio";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_JKS = "Se il keystore contiene pi&ugrave; certificati verr&agrave; richiesto di sceglierne uno attraverso la selezione del corrispettivo 'alias'";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER = "&Egrave; possibile caricare un certificato in uno dei seguenti formati:";
	public final static List<String> LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES = new ArrayList<>();
	static {
		LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES.add("<b>PEM</b>: "+StringEscapeUtils.escapeHtml("file ASCII con codifica Base64 che contiene \"-----BEGIN CERTIFICATE-----\" all'inizio e \"-----END CERTIFICATE-----\" alla fine"));
		LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES.add("<b>DER</b>: versione binaria del formato PEM");
	}
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO = "Certificato";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_ARCHIVIO = "Archivio";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_DOWNLOAD = "Download";//"Download Certificato";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_CAMBIA_ = "Cambia ";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO = "Alias";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL = "Modalità";//"Sorgente Dati";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_UPLOAD_CERTIFICATO = "Upload Archivio";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_CONFIGURAZIONE_MANUALE = "Configurazione Manuale";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT = "Subject";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER = "Issuer";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE = "Type";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION = "Version";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER = "Serial Number";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED = "Self Signed";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE = "Not Before";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER = "Not After";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI = "Verifica tutti i campi";
	public final static String NOTE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI = "Attenzione questa opzione richiede l'aggiornamento del certificato a scadenza";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED = "Self Signed";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS="<br/><br/><b>Nota:</b> Il certiticato selezionato possiede un serial number diverso da quello del certificato già in uso. Abilita l'opzione '"+
			ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI+"' per consentire la distinzione tra i due certificati.<br>";

	
	public final static String LABEL_BUTTON_INVIA_CARICA_CERTIFICATO = "Carica Certificato";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_CONNECTION_TIMEOUT = LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT;
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP = "Autenticazione Http";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP_USERNAME = LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME;
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP_PASSWORD = LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN = "Autenticazione Token";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS = "Autenticazione Https";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_SSL_TYPE = LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE;
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_HOSTNAME_VERIFIER = LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY;
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE = "Auth Server - TrustStore";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLs = "Auth Server - CRLs";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE = "Auth Client - KeyStore";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS = "Auth Client - Key Alias";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY = "Proxy";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_HOSTNAME = LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME;
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_PORT = LABEL_PARAMETRO_CONNETTORE_PROXY_PORT;
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_USERNAME = LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME;
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_PASSWORD = LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD;
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String LUNGHEZZA_SUBJECT_MAX = "2800";
	
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD_ARCHIVI = "-1";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD = "0";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO = "1";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO = "2";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK = "3";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO = "4";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE = "5";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER = "6";
	
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO = "uploadCert";
	public final static String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE = "confMan";

	public final static String VALUE_PARAMETRO_MODALITA_CONNETTORE_DEFAULT = "default";
	public final static String VALUE_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO = "ridefinito";
	
	public final static  String[] TIPI_CODE_JMS = { "queue", "topic" };
	
	public final static String DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT = "default";
	public final static String DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI = "ridefinisci";
	public final static String[] DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODES = {DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT
		, DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI };
	public final static String[] DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_LABEL_MODES = { "Usa valori del TrustStore", "Ridefinisci" };
	
	public final static String DEFAULT_CONNETTORE_HTTPS_TYPE = SSLUtilities.getSafeDefaultProtocol();
	
	public final static String DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE = "jks";
	public final static String[] TIPOLOGIE_KEYSTORE = { "jks", "pkcs12", "jceks", "bks", "uber", "gkr" };
	
	public final static String[] TIPO_SEND_AS = { "TextMessage", "BytesMessage" };
	
	public final static String DEFAULT_TIPO_DATA_TRANSFER = "default"; 
	public final static String[] TIPI_DATA_TRANSFER = { DEFAULT_TIPO_DATA_TRANSFER, TransferLengthModes.CONTENT_LENGTH.getNome() , TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome() };
	
	public final static String DEFAULT_GESTIONE_REDIRECT = "default"; 
	public final static String[] TIPI_GESTIONE_REDIRECT = { DEFAULT_GESTIONE_REDIRECT, CostantiConfigurazione.ABILITATO.getValue() , CostantiConfigurazione.DISABILITATO.getValue() , };
	
	public final static String DEFAULT_CONNETTORE_TYPE_CUSTOM = "custom";
	
	public final static  String[] TIPI_GESTIONE_RESPONSE_FILE = { CostantiConfigurazione.DISABILITATO.getValue(), CostantiConfigurazione.ABILITATO.getValue() };
	
	public final static  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VALUES = { ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO, ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE };
	public final static  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABELS = { ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_UPLOAD_CERTIFICATO, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_CONFIGURAZIONE_MANUALE };
	
	public final static  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_VALUES = { ArchiveType.CER.name(), ArchiveType.JKS.name(), ArchiveType.PKCS12.name()};
	public final static  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_LABELS = { ArchiveType.CER.name(), ArchiveType.JKS.name(), ArchiveType.PKCS12.name()};
	
	public final static String [] CREDENZIALI_CON_NESSUNA_VALUES = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL };
	public final static String [] CREDENZIALI_CON_NESSUNA_LABELS = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL };
	
	public final static String [] CREDENZIALI_VALUES = new String[] { 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL };
	public final static String [] CREDENZIALI_LABELS = new String[] { 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL};
}
