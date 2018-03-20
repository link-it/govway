/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Vector;

import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
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

	public final static String LABEL_CONNETTORE = "Connettore";
	public final static String LABEL_CONNETTORE_ABILITATO = "Abilitato";
	public final static String LABEL_CONNETTORE_PROXY = "Proxy";
	public final static String LABEL_CONNETTORE_HTTP = "Autenticazione Http";
	public final static String LABEL_CONNETTORE_HTTPS = "Autenticazione Https";
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
	
	public final static String PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE = "tipoauthInv";
	public final static String PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME = "utenteInv";
	public final static String PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "passwordInv";
	
	public final static String PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE = "tipoauthCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME = "utenteCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "passwordCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "subjectCredenziali";
	public final static String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "principalCredenziali";
	
	public final static String PARAMETRO_CONNETTORE_PROXY_ENABLED = "connettore_proxy_enabled";
	public final static String PARAMETRO_CONNETTORE_PROXY_HOSTNAME = "connettore_proxy_host";
	public final static String PARAMETRO_CONNETTORE_PROXY_PORT = "connettore_proxy_port";
	public final static String PARAMETRO_CONNETTORE_PROXY_USERNAME = "connettore_proxy_username";
	public final static String PARAMETRO_CONNETTORE_PROXY_PASSWORD = "connettore_proxy_password";
	
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE = "opzioni_avanzate";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE = "transfer_mode";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE = "transfer_chunk_size";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE = "redirect_mode";
	public final static String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP = "redirect_max_hop";
	
	public final static String PARAMETRO_CONNETTORE_JMS_NOME_CODA = "nome";
	public final static String PARAMETRO_CONNETTORE_JMS_TIPO_CODA = "tipo";
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
	public final static String PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "httpsalgoritmo";
	public final static String PARAMETRO_CONNETTORE_HTTPS_STATO = "httpsstato";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE = "httpskeystore";
	public final static String PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "httpspwdprivatekeytrust";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "httpspathkey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "httpstipokey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "httpspwdkey";
	public final static String PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "httpspwdprivatekey";
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
	
	
	/* LABEL PARAMETRI */

	public final static String LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "Tipo Personalizzato";
	public final static String LABEL_PARAMETRO_CONNETTORE_DEBUG = "Debug";
	public final static String LABEL_PARAMETRO_CONNETTORE_URL = "Endpoint";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROFILO =  "Versione Protocollo";
	
	public final static String AUTENTICAZIONE_TIPO_NESSUNA = "nessuna";
	public final static String AUTENTICAZIONE_TIPO_BASIC = CostantiConfigurazione.CREDENZIALE_BASIC.toString();
	public final static String AUTENTICAZIONE_TIPO_SSL = CostantiConfigurazione.CREDENZIALE_SSL.toString();
	public final static String AUTENTICAZIONE_TIPO_PRINCIPAL = CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString();
	public final static String LABEL_AUTENTICAZIONE_TIPO_BASIC = CostantiConfigurazione.LABEL_CREDENZIALE_BASIC;
	public final static String LABEL_AUTENTICAZIONE_TIPO_SSL = CostantiConfigurazione.LABEL_CREDENZIALE_SSL;
	public final static String LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL = CostantiConfigurazione.LABEL_CREDENZIALE_PRINCIPAL;
	public final static String DEFAULT_AUTENTICAZIONE_TIPO = AUTENTICAZIONE_TIPO_NESSUNA;
	
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME = "Utente";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "Subject";
	public final static String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "UserId";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME = "Hostname";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_PORT = "Porta";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME = "Username";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD = "Password";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE = LABEL_CONNETTORE_OPZIONI_AVANZATE;
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE = "Modalità Data Transfer";
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE = "Chunk Length (Bytes)";
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE = "Gestione Redirect";
	public final static String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP = "Max Numero di Redirect";
	
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
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD ="Password";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "Algoritmo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO = "Abilitato";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE ="Dati Accesso al KeyStore";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "Password Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "Path";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "Password Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "Algoritmo";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME = "File";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS = "File Headers";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "AutoCreate Parent Dir";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME = "Overwrite If Exists";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE = "Generazione";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME = "File";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS = "File Headers";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ = "Delete After Read";
	public final static String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "WaitTime ifNotExists (ms)";
		
	public final static String LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE = "Valore";
	
	public final static String LABEL_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO = "ridefinito";
	public final static String LABEL_PARAMETRO_MODALITA_CONNETTORE_DEFAULT = "default";
	public final static String LABEL_PARAMETRO_CONNETTORE_MODALITA = "Modalit&agrave;";
	
	
	/* DEFAULT VALUE PARAMETRI */

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
}
