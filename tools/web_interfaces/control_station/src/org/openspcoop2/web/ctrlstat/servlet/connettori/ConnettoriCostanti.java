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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.pdd.core.connettori.ConnettoreFILE;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;


/**
 * ConnettoriCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriCostanti {
	
	private ConnettoriCostanti() {}

	/* OBJECT NAME */

	public static final String OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES = "connettoriCustomProprieta";

	/* SERVLET NAME */

	public static final String SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_ADD = OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_DELETE = OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST = OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_CONNETTORI_CUSTOM_PROPERTIES = new ArrayList<>();
	public static List<String> getServletConnettoriCustomProperties() {
		return SERVLET_CONNETTORI_CUSTOM_PROPERTIES;
	}
	static {
		SERVLET_CONNETTORI_CUSTOM_PROPERTIES.add(SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_ADD);
		SERVLET_CONNETTORI_CUSTOM_PROPERTIES.add(SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_DELETE);
		SERVLET_CONNETTORI_CUSTOM_PROPERTIES.add(SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST);
	}
	
	/* LABEL GENERALI */

	public static final String LABEL_SERVER = CostantiLabel.LABEL_SERVER;
	public static final String LABEL_CONNETTORE = "Connettore";
	public static final String LABEL_DOWNLOAD_CERTIFICATI_SERVER = "Download Certificati Server";
	public static final String LABEL_CONNETTORE_ABILITATO = "Abilitato";
	public static final String LABEL_CONNETTORE_PROXY = "Proxy";
	public static final String LABEL_CONNETTORE_HTTP = "Autenticazione Http";
	public static final String LABEL_CONNETTORE_BEARER = "Autenticazione Token";
	public static final String LABEL_CONNETTORE_BEARER_MODI_PDND = "Negoziazione Token tramite PDND";
	public static final String LABEL_CONNETTORE_BEARER_MODI_OAUTH = "Negoziazione Token tramite Authorization Server OAuth";
	public static final String LABEL_CONNETTORE_HTTPS = "Autenticazione Https";
	public static final String LABEL_CONNETTORE_TEMPI_RISPOSTA = "Tempi Risposta";
	public static final String LABEL_CONNETTORE_OPZIONI_AVANZATE = "Opzioni Avanzate";
	public static final String LABEL_CONNETTORE_PROPRIETA = "Propriet&agrave;";
	public static final String LABEL_CONNETTORE_AUTENTICAZIONE = "Autenticazione Https";
	public static final String LABEL_CONNETTORE_AUTENTICAZIONE_SERVER = "Autenticazione Server";
	public static final String LABEL_CONNETTORE_AUTENTICAZIONE_CLIENT = "Autenticazione Client";
	public static final String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CODA = "Dati Configurazione Coda";
	public static final String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONNESIONE = "Dati Configurazione Connessione";
	public static final String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONTESTO_JNDI = "Contesto JNDI";
	public static final String LABEL_CONNETTORE_REQUEST_OUTPUT = "Richiesta";
	public static final String LABEL_CONNETTORE_RESPONSE_INPUT = "Risposta";
	public static final String LABEL_CONNETTORE_CUSTOM = "Personalizzato";
	public static final String LABEL_PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER = CostantiControlStation.LABEL_PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER;
	public static final String LABEL_PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER = CostantiControlStation.LABEL_PARAMETRO_ID_APPLICATIVO_SERVER;

	/* PARAMETRI */

	public static final String PARAMETRO_CONNETTORE_ID = "id";	
	public static final String PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "endpointtype";
	public static final String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP = "endpointtype_http";
	public static final String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS = "endpointtype_https";
	public static final String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK = "endpointtype_ckb";
	public static final String PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "tipoconn";
	public static final String PARAMETRO_CONNETTORE_DEBUG = "connettore_debug";
	public static final String PARAMETRO_CONNETTORE_URL = "url";
	public static final String PARAMETRO_CONNETTORE_PROFILO = "profilo";
	public static final String PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER = CostantiControlStation.PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER;
	public static final String PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER = CostantiControlStation.PARAMETRO_ID_APPLICATIVO_SERVER;
	
	public static final String PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE = "tipoauthInv";
	public static final String PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME = "utenteInv";
	public static final String PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "passwordInv";
	
	public static final String PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE = "tipoauthCredenziali";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME = "utenteCredenziali";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "passwordCredenziali";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "subjectCredenziali";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER = "subjectIssuer";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS = "apiKeyMultipleCred";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY = "apiKeyCred";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID = "appIdCred";
	public static final boolean PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE = false;
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "principalCredenziali";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY = "tokenPolicyCred";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID = "tokenClientIdCred";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL = "confSSLCredenziali";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO = "confSSLCredTipoArch";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO = "confSSLCredFileCert";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD= "confSSLCredFileCertPwd";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO = "confSSLCredAliasCert";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT = "confSSLCredAliasCertSub";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER = "confSSLCredAliasCertIss";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE = "confSSLCredAliasCertType";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION = "confSSLCredAliasCertVers";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER = "confSSLCredAliasCertSN";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER_HEX = "confSSLCredAliasCertSNhex";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED = "confSSLCredAliasCertSS";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE = "confSSLCredAliasCertNB";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER = "confSSLCredAliasCertNA";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI = "confSSLCredVerifTutti";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED = "confSSLManSS";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP = "confSSLCredWizStep";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_DOWNLOAD= "confSSLCredFileCertLink";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_MODIFICA = "confSSLCredFileModificaCertLink";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_PROMUOVI = "confSSLCredFileCertPromLink";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD = "changepwd";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CREDENZIALI_ID = "idCred";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PROMUOVI = "confSSLCredFileCertProm";
	public static final String PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_MULTI_AGGIORNA = "confSSLCredFileCertUpdate";
	
	public static final String PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO;
	public static final String PARAMETRO_CONNETTORE_TOKEN_POLICY = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY;
	
	public static final String PARAMETRO_CONNETTORE_PROXY_ENABLED = "connettore_proxy_enabled";
	public static final String PARAMETRO_CONNETTORE_PROXY_HOSTNAME = "connettore_proxy_host";
	public static final String PARAMETRO_CONNETTORE_PROXY_PORT = "connettore_proxy_port";
	public static final String PARAMETRO_CONNETTORE_PROXY_USERNAME = "connettore_proxy_username";
	public static final String PARAMETRO_CONNETTORE_PROXY_PASSWORD = "connettore_proxy_password";
	
	public static final String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE = "connettore_tempi_redefine";
	public static final String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT = "connettore_tempi_ct";
	public static final String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT = "connettore_tempi_rt";
	public static final String PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA = "connettore_tempi_avg";
	
	public static final String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE = "opzioni_avanzate";
	public static final String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE = "transfer_mode";
	public static final String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE = "transfer_chunk_size";
	public static final String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE = "redirect_mode";
	public static final String PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP = "redirect_max_hop";
	
	public static final String PARAMETRO_CONNETTORE_JMS_NOME_CODA = "nomeJms";
	public static final String PARAMETRO_CONNETTORE_JMS_TIPO_CODA = "tipoJms";
	public static final String PARAMETRO_CONNETTORE_JMS_USERNAME = "userJms";
	public static final String PARAMETRO_CONNETTORE_JMS_PASSWORD = "passwordJms";
	public static final String PARAMETRO_CONNETTORE_JMS_INIT_CTX = "initcont";
	public static final String PARAMETRO_CONNETTORE_JMS_URL_PKG = "urlpgk";
	public static final String PARAMETRO_CONNETTORE_JMS_PROVIDER_URL = "provurl";
	public static final String PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY = "connfact";
	public static final String PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS = "sendas";
		
	public static final String PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH = "clientAuth";
	public static final String PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE = "stato";
	public static final String PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE ="httpstipologia";
	public static final String PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY = "httpshostverify";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS = "httpstrustverify";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "httpspath";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE = "httpstipo";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = "httpspwd";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL = "httpscrl";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY = "httpsocsp";
	public static final String PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "httpsalgoritmo";
	public static final String PARAMETRO_CONNETTORE_HTTPS_STATO = "httpsstato";
	public static final String PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE = "httpskeystore";
	public static final String PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "httpspwdprivatekeytrust";
	public static final String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "httpspathkey";
	public static final String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "httpstipokey";
	public static final String PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "httpspwdkey";
	public static final String PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "httpspwdprivatekey";
	public static final String PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE = "httpsaliasprivatekey";
	public static final String PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "httpsalgoritmokey";
	
	public static final String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME = "requestOutputFileName";
	public static final String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS = "requestOutputFileNameP";
	public static final String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS = "requestOutputFileNameHeaders";
	public static final String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS = "requestOutputFileNameHeadersP";
	public static final String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "requestOutputDirectoryAutoCreate";
	public static final String PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME = "requestOutputOverwriteFileName";
	public static final String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE = "responseInputMode";
	public static final String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME = "responseInputFileName";
	public static final String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS = "responseInputFileNameHeaders";
	public static final String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ = "responseInputFileNameDelete";
	public static final String PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "responseInputWaitTime";
	
	public static final String PARAMETRO_CONNETTORE_CUSTOM_ID = "id";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_MY_ID = "myId";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_SERVLET = "servlet";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO = "nomeprov";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO = "tipoprov";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO = "nomeservizio";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO = "tiposervizio";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_VERSIONE_SERVIZIO = "versioneservizio";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_CORRELATO = "correlato";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE = "idSoggErogatore";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO = "nomeservizioApplicativo";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO = "idsil";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_NOME = "nome";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_VALORE = "valore";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO = "tipoAccordo";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_ID_PROVIDER = "provider";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA = "proprietaConnettoreCustom";
	public static final String PARAMETRO_CONNETTORE_MODALITA = "modCon";
	public static final String PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA = CostantiControlStation.PARAMETRO_ID_PORTA;
	
	
	public static final String PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX = "__SA";
	
	
	/* LABEL PARAMETRI */

	public static final int LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE = CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE;
	
	public static final String LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "Tipo";
	public static final String LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "Tipo Personalizzato";
	public static final String LABEL_PARAMETRO_CONNETTORE_DEBUG = "Debug";
	public static final String LABEL_PARAMETRO_CONNETTORE_DEBUG_INFO = "Se viene abilitata l'opzione, l'intera comunicazione viene registrata nel file "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_NOTE+". La registrazione comprende i payload dei messaggi scambiati, gli headers di trasporto, le informazioni sull'handshake tls, eventuali token oauth negoziati etc ... "; 
	public static final String LABEL_PARAMETRO_CONNETTORE_DEBUG_NODE = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_NOTE;
	public static final String LABEL_PARAMETRO_CONNETTORE_URL = "Endpoint";
	public static final int LABEL_PARAMETRO_CONNETTORE_URL_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_PROFILO =  "Versione Protocollo";
	
	public static final String AUTENTICAZIONE_TIPO_NESSUNA = "nessuna";
	public static final String AUTENTICAZIONE_TIPO_BASIC = CostantiConfigurazione.CREDENZIALE_BASIC.toString();
	public static final String AUTENTICAZIONE_TIPO_APIKEY = CostantiConfigurazione.CREDENZIALE_APIKEY.toString();
	public static final String AUTENTICAZIONE_TIPO_SSL = CostantiConfigurazione.CREDENZIALE_SSL.toString();
	public static final String AUTENTICAZIONE_TIPO_PRINCIPAL = CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString();
	public static final String AUTENTICAZIONE_TIPO_TOKEN = CostantiConfigurazione.CREDENZIALE_TOKEN.toString();
	public static final String AUTENTICAZIONE_TIPO_TOKEN_PDND = CostantiConfigurazione.CREDENZIALE_TOKEN.toString() + "-PDND";
	public static final String AUTENTICAZIONE_TIPO_TOKEN_OAUTH = CostantiConfigurazione.CREDENZIALE_TOKEN.toString() + "-OAUTH";
	public static final String AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND = AUTENTICAZIONE_TIPO_SSL + "-" + AUTENTICAZIONE_TIPO_TOKEN_PDND;
	public static final String AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH = AUTENTICAZIONE_TIPO_SSL + "-" + AUTENTICAZIONE_TIPO_TOKEN_OAUTH;
	public static final String LABEL_AUTENTICAZIONE_TIPO_BASIC = CostantiConfigurazione.LABEL_CREDENZIALE_BASIC;
	public static final String LABEL_AUTENTICAZIONE_TIPO_APIKEY = CostantiConfigurazione.LABEL_CREDENZIALE_APIKEY;
	public static final String LABEL_AUTENTICAZIONE_TIPO_SSL = CostantiConfigurazione.LABEL_CREDENZIALE_SSL;
	public static final String LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL = CostantiConfigurazione.LABEL_CREDENZIALE_PRINCIPAL;
	public static final String LABEL_AUTENTICAZIONE_TIPO_TOKEN = CostantiConfigurazione.LABEL_CREDENZIALE_TOKEN;
	public static final String DEFAULT_AUTENTICAZIONE_TIPO = AUTENTICAZIONE_TIPO_NESSUNA;
	
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_USERNAME;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_SUBJECT;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_ISSUER;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS = "App ID";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_EMPTY_LABEL = "";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_APP_ID;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_API_KEY;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_DESCR = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_DESCR;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_WITH_HTTPS = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_WITH_HTTPS;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID;	

	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_PASSWORD = "Nuova "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_API_KEY = "Nuova "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY;
	public static final int API_KEY_ROWS = 5;
	
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_PASSWORD = "Modifica "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_API_KEY = "Aggiorna "+LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY;
	
	public static final String LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME = "Hostname";
	public static final String LABEL_PARAMETRO_CONNETTORE_PROXY_PORT = "Porta";
	public static final String LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME = "Username";
	public static final String LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD = "Password";
	
	public static final String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE = "Ridefinisci Tempi Risposta";
	public static final String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT = CostantiLabel.LABEL_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT;
	public static final String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT = CostantiLabel.LABEL_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT;
	public static final String LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA = "Tempo Medio di Risposta";
	public static final String LABEL_PARAMETRO_CONNETTORE_TEMPI_MILLISECONDI_NOTE = "Indicazione in millisecondi (ms)";
	
	public static final String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE = LABEL_CONNETTORE_OPZIONI_AVANZATE;
	public static final String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE = "Modalità Data Transfer";
	public static final String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE = "Chunk Length (Bytes)";
	public static final String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE = "Gestione Redirect";
	public static final String LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP = "Max Numero di Redirect";
	
	public static final String LABEL_PARAMETRO_CONNETTORE_TOKEN_POLICY = CostantiLabel.LABEL_CONNETTORE_TOKEN_POLICY;
	
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA = CostantiLabel.LABEL_CONNETTORE_JMS_NOME_CODA;
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_CODA = "Tipo";
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME = CostantiLabel.LABEL_CONNETTORE_JMS_USERNAME;
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD = "Password";
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX = "Initial Context Factory";
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG = "Url Pgk Prefixes";
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL = "Provider Url";
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY = "Connection Factory";
	public static final String LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS = "Send As";
	
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_URL = LABEL_PARAMETRO_CONNETTORE_URL;
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH = "Client-Auth";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE = "Tipologia";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY = CostantiLabel.LABEL_CONNETTORE_HTTPS_HOST_VERIFY;
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS = "Verifica";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "Path";
	public static final int LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE = "Tipo";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD ="Password";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL = "CRL File(s)";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY = "OCSP Policy";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL_NOTE = "Elencare più file separandoli con la ','";
	public static final int LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL_SIZE = 2;
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "Algoritmo";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO = "Abilitato";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE ="Dati Accesso al KeyStore";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "Password Chiave Privata";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "Path";
	public static final int LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "Tipo";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "Password";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "Password Chiave Privata";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE = "Alias Chiave Privata";
	public static final String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "Algoritmo";
	
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME = "File";
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS = "File (Permessi)";
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS_INFO = "Consente di impostare i permessi del file tramite il seguente formato:<BR>- "+ConnettoreFILE.PERMESSI_FORMATO;
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS = 1;
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS_MAX = 3;
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS = "File Headers";
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS = "File Headers (Permessi)";
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE_PERMISSIONS = LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS;
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE_PERMISSIONS_MAX = LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS_MAX;
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "AutoCreate Parent Dir";
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME = "Overwrite If Exists";
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE = "Generazione";
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME = "File";
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS = "File Headers";
	public static final int LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS_SIZE = LABEL_PARAMETRO_CONNETTORE_TEXT_AREA_SIZE;
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ = "Delete After Read";
	public static final String LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "WaitTime ifNotExists (ms)";
		
	public static final String LABEL_INPUT_FILE_HEADER = CostantiLabel.LABEL_INPUT_FILE_HEADER;
	public static final String LABEL_INPUT_FILE = CostantiLabel.LABEL_INPUT_FILE;
	public static final String LABEL_OUTPUT_FILE_HEADER = CostantiLabel.LABEL_OUTPUT_FILE_HEADER;
	public static final String LABEL_OUTPUT_FILE = CostantiLabel.LABEL_OUTPUT_FILE;
	
	public static final String LABEL_SEZIONE_CONNETTORE_CUSTOM_PROPRIETA = "Proprietà";
	
	public static final String LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME = "Nome";
	public static final String LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE = "Valore";
	
	public static final String LABEL_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO = "Connettore ridefinito per il gruppo";
	public static final String LABEL_PARAMETRO_MODALITA_CONNETTORE_DEFAULT = "Utilizza connettore del gruppo '"+Costanti.MAPPING_DESCRIZIONE_DEFAULT+"'";
	public static final String LABEL_PARAMETRO_CONNETTORE_MODALITA = "Modalit&agrave;";
	
	public static final String LABEL_CONFIGURAZIONE_SSL_TITLE_CONFIGURAZIONE = "Configurazione";
	public static final String LABEL_CONFIGURAZIONE_SSL_TITLE_INFORMAZIONI_CERTIFICATO = "Certificato";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO = "Formato";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_JKS = "Se il keystore contiene pi&ugrave; certificati verr&agrave; richiesto di sceglierne uno attraverso la selezione del corrispettivo 'alias'";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER = "&Egrave; possibile caricare un certificato in uno dei seguenti formati:";
	public static final List<String> LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES = new ArrayList<>();
	static {
		LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES.add("<b>PEM</b>: "+StringEscapeUtils.escapeHtml("file ASCII con codifica Base64 che contiene \"-----BEGIN CERTIFICATE-----\" all'inizio e \"-----END CERTIFICATE-----\" alla fine"));
		LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES.add("<b>DER</b>: versione binaria del formato PEM");
	}
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO = "Certificato";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_ARCHIVIO = "Archivio";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_DOWNLOAD = "Download";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_CAMBIA_ = "Cambia ";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD = "Password";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO = "Alias";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL = "Modalità";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_UPLOAD_CERTIFICATO = "Upload Archivio";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_CONFIGURAZIONE_MANUALE = "Configurazione Manuale";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT = "Subject";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER = "Issuer";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE = "Type";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION = "Version";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER = "Serial Number";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER_HEX = "";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER_HEX_PREFIX = "(Hex) ";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED = "Self Signed";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE = "Not Before";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER = "Not After";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI = "Verifica";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI_ENABLE = "Certificato";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI_DISABLE = "Subject/Issuer";
	public static final String DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI = org.openspcoop2.web.lib.mvc.Costanti.CHECK_BOX_ENABLED;
	public static final String NOTE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_SOLO_SUBJECT_ISSUER = "L'identificazione avviene analizzando solamente i campi Subject e Issuer";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED = "Self Signed";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS="<br/><br/><b>Nota:</b> Il certiticato selezionato possiede un serial number diverso da quello del certificato già in uso. Abilita l'opzione '"+
			ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI+"' per consentire la distinzione tra i due certificati.<br>";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_AGGIUNGI = "Aggiungi Certificato";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_PROMUOVI = "Promuovi come Certificato Principale";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_PRINCIPALE = "Principale";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_ELENCO_CERTIFICATI = "Elenco Certificati";
	public static final String LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI = "Certificati";
	
	public static final String MESSAGGIO_NON_ELIMINABILE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_PRINCIPALE = "Certificato Principale non eliminabile";
	
	public static final String LABEL_BUTTON_INVIA_CARICA_CERTIFICATO = "Carica Certificato";
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_CONNECTION_TIMEOUT = LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_READ_TIMEOUT = LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT;
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP_USERNAME = LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP_PASSWORD = LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN;
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_SSL_TYPE = LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_HOSTNAME_VERIFIER = LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUST_ALL_CERTS = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUST_ALL_CERTS;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLS = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLS;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_OCSP_POLICY = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_OCSP_POLICY;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS;
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY = CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_HOSTNAME = LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_PORT = LABEL_PARAMETRO_CONNETTORE_PROXY_PORT;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_USERNAME = LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME;
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_PASSWORD = LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD;
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public static final String LUNGHEZZA_SUBJECT_MAX = "2800";
	
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD_ARCHIVI = "-1";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD = "0";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO = "1";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO = "2";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK = "3";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO = "4";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE = "5";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER = "6";
	
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO = "uploadCert";
	public static final String VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE = "confMan";

	public static final String VALUE_PARAMETRO_MODALITA_CONNETTORE_DEFAULT = "default";
	public static final String VALUE_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO = "ridefinito";
	
	public static final  String[] TIPI_CODE_JMS = { "queue", "topic" };
	
	public static final String DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT = "default";
	public static final String DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI = "ridefinisci";
	public static final String[] DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODES = {DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT
		, DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI };
	public static final String[] DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_LABEL_MODES = { "Usa valori del TrustStore", "Ridefinisci" };
	
	public static final String DEFAULT_CONNETTORE_HTTPS_TYPE = SSLUtilities.getSafeDefaultProtocol();
	
	public static final boolean DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS = true;
	
	public static final String DEFAULT_CONNETTORE_HTTPS_PATH_HSM_PREFIX = HSMUtils.KEYSTORE_HSM_PREFIX;
	public static final String DEFAULT_CONNETTORE_HTTPS_HSM_STORE_PASSWORD_UNDEFINED = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
	public static final String DEFAULT_CONNETTORE_HTTPS_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
	public static boolean DEFAULT_CONNETTORE_HTTPS_HSM_CONFIGURABLE_KEY_PASSWORD = HSMUtils.isHsmConfigurableKeyPassword();
	
	public static final String DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE = SecurityConstants.KEYSTORE_TYPE_JKS_VALUE;
	public static final String[] TIPOLOGIE_KEYSTORE_OLD = { SecurityConstants.KEYSTORE_TYPE_JKS_VALUE, 
			SecurityConstants.KEYSTORE_TYPE_PKCS12_VALUE, 
			SecurityConstants.KEYSTORE_TYPE_JCEKS_VALUE, 
			"bks", "uber", "gkr" };
	public static List<String> getTIPOLOGIE_KEYSTORE(boolean truststore, boolean label){
		// NOTA:far ricreare la lista ogni volta, poiche' poi viene modificata
		List<String> l = new ArrayList<>();
		l.add(label ? SecurityConstants.KEYSTORE_TYPE_JKS_LABEL : SecurityConstants.KEYSTORE_TYPE_JKS_VALUE);
		l.add(label ? SecurityConstants.KEYSTORE_TYPE_PKCS12_LABEL : SecurityConstants.KEYSTORE_TYPE_PKCS12_VALUE);
		HSMUtils.fillTipologieKeystore(truststore, false, l);
		return l;
	}
	public static boolean existsTIPOLOGIE_KEYSTORE_HSM(boolean truststore){
		return HSMUtils.existsTipologieKeystoreHSM(truststore, false);
	}
	
	
	public static final String[] TIPO_SEND_AS = { "TextMessage", "BytesMessage" };
	
	public static final String DEFAULT_TIPO_DATA_TRANSFER = "default"; 
	public static final String[] TIPI_DATA_TRANSFER = { DEFAULT_TIPO_DATA_TRANSFER, TransferLengthModes.CONTENT_LENGTH.getNome() , TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome() };
	
	public static final String DEFAULT_GESTIONE_REDIRECT = "default"; 
	public static final String[] TIPI_GESTIONE_REDIRECT = { DEFAULT_GESTIONE_REDIRECT, CostantiConfigurazione.ABILITATO.getValue() , CostantiConfigurazione.DISABILITATO.getValue() , };
	
	public static final String DEFAULT_CONNETTORE_TYPE_CUSTOM = "custom";
	
	public static final  String[] TIPI_GESTIONE_RESPONSE_FILE = { CostantiConfigurazione.DISABILITATO.getValue(), CostantiConfigurazione.ABILITATO.getValue() };
	
	public static final  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VALUES = { ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO, ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE };
	public static final  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABELS = { ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_UPLOAD_CERTIFICATO, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABEL_CONFIGURAZIONE_MANUALE };
	
	public static final  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_VALUES = { ArchiveType.CER.name(), ArchiveType.JKS.name(), ArchiveType.PKCS12.name()};
	public static final  String[] PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_LABELS = { ArchiveType.CER.name(), ArchiveType.JKS.name(), ArchiveType.PKCS12.name()};
	
	public static final String [] CREDENZIALI_CON_NESSUNA_VALUES = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL };
	public static final String [] CREDENZIALI_CON_NESSUNA_LABELS = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL };
	
	public static final String [] CREDENZIALI_VALUES = new String[] { 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL };
	public static final String [] CREDENZIALI_LABELS = new String[] { 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL};
	
	public static final String [] CREDENZIALI_CON_TOKEN_VALUES = new String[] { 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL, 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN };
	public static final String [] CREDENZIALI_CON_TOKEN_LABELS = new String[] { 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_BASIC, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_APIKEY, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_PRINCIPAL, 
			ConnettoriCostanti.LABEL_AUTENTICAZIONE_TIPO_TOKEN};

	public static final String [] CREDENZIALI_MODI_ESTERNO_VALUES = new String[] { 
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND,
			ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH};
	public static final String [] CREDENZIALI_MODI_ESTERNO_LABELS = new String[] { 
			CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL,
			CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL,
			CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL,
			CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_PDND_LABEL,
			CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_OAUTH_LABEL};
	
	public static final int NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI = 60;
	
	
	/* LABEL FILTRI RICERCA */
	public static final String LABEL_SUBTITLE_DATI_CONNETTORE = "Dati Connettore";
	public static final String NAME_SUBTITLE_DATI_CONNETTORE = "subtDatiConn";
	public static final String LABEL_FILTRO_TIPO_CONNETTORE = CostantiLabel.LABEL_TIPO_CONNETTORE;
	public static final String LABEL_FILTRO_TIPO_CONNETTORE_IM = "im-message-box";
	public static final String LABEL_FILTRO_CONNETTORE_TOKEN_POLICY = "Token Policy";
	public static final String LABEL_FILTRO_CONNETTORE_ENDPOINT = CostantiLabel.LABEL_CONNETTORE_ENDPOINT;
	public static final String LABEL_FILTRO_CONNETTORE_ENDPOINT_FILE = "File";
	public static final String LABEL_FILTRO_CONNETTORE_ENDPOINT_JMS = "Parametri Jms";
	public static final String LABEL_FILTRO_CONNETTORE_KEYSTORE = "Keystore";
	public static final String LABEL_FILTRO_CONNETTORE_DEBUG = "Debug";
	public static final String LABEL_FILTRO_CONNETTORE_MULTIPLO_NOME = "Nome";
	public static final String LABEL_FILTRO_CONNETTORE_MULTIPLO_FILTRO = "Filtro";
	
}
