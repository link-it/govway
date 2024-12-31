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

package org.openspcoop2.core.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * CostantiConnettori
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiConnettori {
	
	private CostantiConnettori() {}
	
	
	/** COMMONS PROPERTIES */
	
	public static final String CONNETTORE_LOCATION = "location";
	public static final String CONNETTORE_DEBUG = "debug";
    public static final String CONNETTORE_USERNAME = "user";
    public static final String CONNETTORE_PASSWORD = "password";
    public static final String CONNETTORE_BEARER_TOKEN = "bearerToken";
    public static final String CONNETTORE_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String CONNETTORE_CONNECTION_TIMEOUT_GLOBALE = "connectionTimeoutGlobale";
    public static final String CONNETTORE_READ_CONNECTION_TIMEOUT = "readConnectionTimeout";
    public static final String CONNETTORE_READ_CONNECTION_TIMEOUT_GLOBALE = "readConnectionTimeoutGlobale";
    public static final String CONNETTORE_TEMPO_MEDIO_RISPOSTA = "avgResponseTime";
    public static final String CONNETTORE_TOKEN_POLICY = "tokenPolicy";
	
    
    /** EXTENDED PROPERTIES */
    
    public static final String CONNETTORE_EXTENDED_PREFIX = "-#ext#-";
    
    
    /** HTTP PROPERTIES */
	
    public static final String CONNETTORE_HTTP_PROXY_TYPE = "proxyType";
    public static final String CONNETTORE_HTTP_PROXY_HOSTNAME = "proxyHostname";
    public static final String CONNETTORE_HTTP_PROXY_PORT = "proxyPort";
    public static final String CONNETTORE_HTTP_PROXY_USERNAME = "proxyUsername";
    public static final String CONNETTORE_HTTP_PROXY_PASSWORD = "proxyPassword";

    public static final String CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP = TipiConnettore.HTTP.getNome();
    public static final String CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS = TipiConnettore.HTTPS.getNome();
    
    public static final String CONNETTORE_HTTP_REDIRECT_FOLLOW = "followRedirects";
    public static final String CONNETTORE_HTTP_REDIRECT_MAX_HOP = "maxHopRedirect";
    public static final String CONNETTORE_HTTP_REDIRECT_NUMBER = "numberRedirect";
    public static final String CONNETTORE_HTTP_REDIRECT_ROUTE = "routeRedirect";
    
    public static final String CONNETTORE_HTTP_DATA_TRANSFER_MODE = "dataTransferMode";
    public static final String CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE = "transferChunkSize";
    
    public static final String CONNETTORE_APIKEY_HEADER = "apiKeyHeader";
    public static final String CONNETTORE_APIKEY = "apiKey";
    public static final String CONNETTORE_APIKEY_APPID_HEADER = "appIdHeader";
    public static final String CONNETTORE_APIKEY_APPID = "appId";
    public static final String DEFAULT_HEADER_API_KEY = "X-API-KEY";
    public static final String DEFAULT_HEADER_APP_ID = "X-APP-ID";
    
	/** JMS PROPERTIES */
	
    public static final String CONNETTORE_JMS_TIPO = "tipo";
    public static final String CONNETTORE_JMS_CONTEXT_PREFIX="context-";
    public static final String CONNETTORE_JMS_POOL_PREFIX="pool-";
    public static final String CONNETTORE_JMS_LOOKUP_DESTINATION_PREFIX="lookupDestination-";
    public static final String CONNETTORE_JMS_CONNECTION_FACTORY="connection-factory";
    public static final String CONNETTORE_JMS_SEND_AS="send-as";
    public static final String CONNETTORE_JMS_LOCATIONS_CACHE = "locations-cache";
    public static final String CONNETTORE_JMS_ACKNOWLEDGE_MODE = "acknowledgeMode";
    
    public static final String CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO = "#Servizio"; 
    public static final String CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO = "#TipoServizio";
    public static final String CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE = "#Azione";
    
    public static final String CONNETTORE_JMS_TIPO_QUEUE = "queue";
    public static final String CONNETTORE_JMS_TIPO_TOPIC = "topic";
	
    public static final String CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE = "TextMessage";
    public static final String CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE = "BytesMessage";
	
    public static final String CONNETTORE_JMS_LOCATIONS_CACHE_ABILITATA = "abilitata";
    public static final String CONNETTORE_JMS_LOCATIONS_CACHE_DISABILITATA = "disabilitata";
    
    
    /** HTTPS PROPERTIES */
    
    public static final String CONNETTORE_HTTPS_TRUST_ALL_CERTS = "trustAllCerts";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "trustStoreLocation";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = "trustStorePassword";
    public static final String CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM = "trustManagementAlgorithm";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_TYPE = "trustStoreType";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_CRLS = "trustStoreCRLs";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY = "trustStoreOCSPPolicy";
    public static final String CONNETTORE_HTTPS_KEY_STORE_LOCATION = "keyStoreLocation";
    public static final String CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "keyStorePassword";
    public static final String CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM = "keyManagementAlgorithm";
    public static final String CONNETTORE_HTTPS_KEY_STORE_TYPE = "keyStoreType";
    public static final String CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY = "keyStoreBYOKPolicy";
    public static final String CONNETTORE_HTTPS_KEY_PASSWORD = "keyPassword";
    public static final String CONNETTORE_HTTPS_KEY_ALIAS = "keyAlias";
    public static final String CONNETTORE_HTTPS_HOSTNAME_VERIFIER = "hostnameVerifier";
    public static final String CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER = "classNameHostnameVerifier";
    public static final String CONNETTORE_HTTPS_SSL_TYPE = "sslType";
    public static final String CONNETTORE_HTTPS_SECURE_RANDOM = "secureRandom";
    public static final String CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM = "secureRandomAlgorithm";

    
    public static final String CONNETTORE_HTTPS_SSL_TYPE_DEFAULT_VALUE=SSLUtilities.getSafeDefaultProtocol();
	
    
    
    /** FILE PROPERTIES */
    
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE = "outputFile";
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS = "outputFilePermissions";
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS = "outputFileHeaders";
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS = "outputFileHeadersPermissions";
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "outputFileAutoCreateParentDirectory";
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE = "outputFileOverwriteIfExists";
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_MODE = "response";
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE = "inputFile";
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS = "inputFileHeaders";
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ = "inputFileDeleteAfterRead";
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "inputFileWaitTimeIfNotExists";
    
    
    
    /** DIRECT VM PROPERTIES */
    
    public static final String CONNETTORE_DIRECT_VM_PROTOCOL = "protocol";
    public static final String CONNETTORE_DIRECT_VM_CONTEXT = "context";
    public static final String CONNETTORE_DIRECT_VM_PDD_CONTEXT_PRESERVE = "contextPreserve";
    public static final String CONNETTORE_DIRECT_VM_PD = "pd";
    public static final String CONNETTORE_DIRECT_VM_PA = "pa";
    
    
    
    /** STRESSTEST PROPERTIES */
    
    public static final String CONNETTORE_STRESS_TEST_SLEEP = "sleep";
    public static final String CONNETTORE_STRESS_TEST_SLEEP_MAX = "sleepMax";
    public static final String CONNETTORE_STRESS_TEST_SLEEP_MIN = "sleepMin";
    public static final String CONNETTORE_STRESS_TEST_HEADER_APPLICATIVO = "addHeader";
    
    
    /** NULLECHO PROPERTIES */
    
    public static final String CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE = "generaTrasmissione";
    public static final String CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_INVERTITA = "generaTrasmissioneInvertita";
    public static final String CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_ANDATA_RITORNO = "generaTrasmissioneAndataRitorno";
    
    
    /** STATUS PROPERTIES */ 
    public static final String CONNETTORE_STATUS_RESPONSE_TYPE = "statusResponseType";
    public static final String CONNETTORE_STATUS_TEST_CONNECTIVITY = "testConnectivity";
    public static final String CONNETTORE_STATUS_PERIOD = "period";
    public static final String CONNETTORE_STATUS_PERIOD_VALUE = "periodValue";
    public static final String CONNETTORE_STATUS_STAT_LIFETIME = "statLifetime";
    
    /** BYOK PROPERTIES */
    
    private static List<String> confidentials = new ArrayList<>();
    public static List<String> getConfidentials() {
		return confidentials;
	}
	static {
		confidentials.add(CONNETTORE_PASSWORD);
    	confidentials.add(CONNETTORE_BEARER_TOKEN);
    	confidentials.add(CONNETTORE_APIKEY);
    	confidentials.add(CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
    	confidentials.add(CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
    	confidentials.add(CONNETTORE_HTTPS_KEY_PASSWORD);
    	confidentials.add(CONNETTORE_HTTP_PROXY_PASSWORD);
	}
    
    public static boolean isConfidential(String nome) {
    	return confidentials.contains(nome);
    }
}
