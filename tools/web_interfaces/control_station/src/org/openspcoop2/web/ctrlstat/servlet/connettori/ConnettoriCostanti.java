/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
	public final static String LABEL_CONNETTORE_HTTP = "Autenticazione Http";
	public final static String LABEL_CONNETTORE_HTTPS = "Autenticazione Https";
	public final static String LABEL_CONNETTORE_PROPRIETA = "Propriet&agrave;";
	public final static String LABEL_CONNETTORE_AUTENTICAZIONE = "Autenticazione Https";
	public final static String LABEL_CONNETTORE_AUTENTICAZIONE_SERVER = "Autenticazione Https Server";
	public final static String LABEL_CONNETTORE_AUTENTICAZIONE_CLIENT = "Autenticazione Https Client";
	public final static String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CODA = "Dati Configurazione Coda";
	public final static String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONNESIONE = "Dati Configurazione Connessione";
	public final static String LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONTESTO_JNDI = "Contesto JNDI";

	/* PARAMETRI */

	public final static String PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME = "utente";
	public final static String PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD = "password";
	
	public final static String PARAMETRO_CONNETTORE_ID = "id";	
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "endpointtype";
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP = "endpointtype_http";
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS = "endpointtype_https";
	public final static String PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK = "endpointtype_ckb";
	public final static String PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "tipoconn";
	public final static String PARAMETRO_CONNETTORE_DEBUG = "connettore_debug";
	public final static String PARAMETRO_CONNETTORE_URL = "url";
	public final static String PARAMETRO_CONNETTORE_PROVIDER = "provider";
	public final static String PARAMETRO_CONNETTORE_PROFILO = "profilo";
	
	public final static String PARAMETRO_CONNETTORE_JMS_NOME_CODA = "nome";
	public final static String PARAMETRO_CONNETTORE_JMS_TIPO_CODA = "tipo";
	public final static String PARAMETRO_CONNETTORE_JMS_USERNAME = "user";
	public final static String PARAMETRO_CONNETTORE_JMS_PASSWORD = "password";
	public final static String PARAMETRO_CONNETTORE_JMS_INIT_CTX = "initcont";
	public final static String PARAMETRO_CONNETTORE_JMS_URL_PKG = "urlpgk";
	public final static String PARAMETRO_CONNETTORE_JMS_PROVIDER_URL = "provurl";
	public final static String PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY = "connfact";
	public final static String PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS = "sendas";
	
	
	public final static String PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH = "clientAuth";
	public final static String PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE = "stato";
	public final static String PARAMETRO_CONNETTORE_HTTPS_URL = "httpsurl";
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
	
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID = "id";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_MY_ID = "myId";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_SERVLET = "servlet";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO = "nomeprov";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO = "tipoprov";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO = "nomeservizio";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO = "tiposervizio";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_CORRELATO = "correlato";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE = "idSoggErogatore";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO = "nomeservizioApplicativo";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO = "idsil";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_NOME = "nome";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_VALORE = "valore";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO = "tipoAccordo";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_ID_PROVIDER = "provider";
	public final static String PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA = "proprietaConnettoreCustom";
	
	
	/* LABEL PARAMETRI */

	public final static String LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO = "Tipo connettore personalizzato";
	public final static String LABEL_PARAMETRO_CONNETTORE_DEBUG = "Debug";
	public final static String LABEL_PARAMETRO_CONNETTORE_URL = "Endpoint";
	public final static String LABEL_PARAMETRO_CONNETTORE_PROFILO =  "Versione Protocollo";
	
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
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE ="Dati di accesso al KeyStore";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE = "Password Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "Path";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE = "Tipo";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE = "Password Chiave Privata";
	public final static String LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "Algoritmo";
	
	public final static String LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE = "Valore";
	
	/* DEFAULT VALUE PARAMETRI */

	public final static  String[] TIPI_CODE_JMS = { "queue", "topic" };
	
	public final static String DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT = "default";
	public final static String DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI = "ridefinisci";
	public final static String[] DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODES = {DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT
		, DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI };
	public final static String[] DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_LABEL_MODES = { "Usa valori del TrustStore", "Ridefinisci" };
	
	public final static String DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE = "SSLv3";
	public final static String[] TIPOLOGIE_HTTPS = { "SSL", "SSLv3", "TLS", "TLSv1" };
	
	public final static String DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE = "jks";
	public final static String[] TIPOLOGIE_KEYSTORE = { "jks", "pkcs12", "jceks", "bks", "uber", "gkr" };
	
	public final static String[] TIPO_SEND_AS = { "TextMessage", "BytesMessage" };
	
	public final static String DEFAULT_CONNETTORE_TYPE_CUSTOM = "custom";
}
