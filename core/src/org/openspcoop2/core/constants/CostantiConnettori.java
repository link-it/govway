/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.core.constants;

import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * CostantiConnettori
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiConnettori {
	
	
	/** COMMONS PROPERTIES */
	
	public final static String CONNETTORE_LOCATION = "location";
	public final static String CONNETTORE_DEBUG = "debug";
    public static final String CONNETTORE_USERNAME = "user";
    public static final String CONNETTORE_PASSWORD = "password";
    public static final String CONNETTORE_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String CONNETTORE_READ_CONNECTION_TIMEOUT = "readConnectionTimeout";
    public static final String CONNETTORE_TEMPO_MEDIO_RISPOSTA = "avgResponseTime";
	
    
    /** EXTENDED PROPERTIES */
    
    public final static String CONNETTORE_EXTENDED_PREFIX = "-#ext#-";
    
    
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
    public static final String _CONNETTORE_HTTP_REDIRECT_NUMBER = "numberRedirect";
    public static final String _CONNETTORE_HTTP_REDIRECT_ROUTE = "routeRedirect";
    
    public static final String CONNETTORE_HTTP_DATA_TRANSFER_MODE = "dataTransferMode";
    public static final String CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE = "transferChunkSize";

    
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
    
    public static final String CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "trustStoreLocation";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = "trustStorePassword";
    public static final String CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = "trustManagementAlgorithm";
    public static final String CONNETTORE_HTTPS_TRUST_STORE_TYPE = "trustStoreType";
    public static final String CONNETTORE_HTTPS_KEY_STORE_LOCATION = "keyStoreLocation";
    public static final String CONNETTORE_HTTPS_KEY_STORE_PASSWORD = "keyStorePassword";
    public static final String CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = "keyManagementAlgorithm";
    public static final String CONNETTORE_HTTPS_KEY_STORE_TYPE = "keyStoreType";
    public static final String CONNETTORE_HTTPS_KEY_PASSWORD = "keyPassword";
    public static final String CONNETTORE_HTTPS_HOSTNAME_VERIFIER = "hostnameVerifier";
    public static final String CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER = "classNameHostnameVerifier";
    public static final String CONNETTORE_HTTPS_SSL_TYPE = "sslType";
    
    public static final String CONNETTORE_HTTPS_SSL_TYPE_DEFAULT_VALUE=SSLUtilities.getSafeDefaultProtocol();
	
    
    
    /** FILE PROPERTIES */
    
    public final static String _CONNETTORE_FILE_MAP_DATE_OBJECT = "date";
    public final static String _CONNETTORE_FILE_MAP_TRANSACTION_OBJECT = "transaction";
    public final static String _CONNETTORE_FILE_MAP_TRANSACTION_ID = "{transaction:id}";
    public final static String _CONNETTORE_FILE_MAP_BUSTA_OBJECT = "busta";
    public final static String _CONNETTORE_FILE_MAP_CTX_OBJECT = "context";
    public final static String _CONNETTORE_FILE_MAP_HEADER = "header";
    public final static String _CONNETTORE_FILE_MAP_QUERY_PARAMETER = "query";
    public final static String _CONNETTORE_FILE_MAP_BUSTA_PROPERTY = "property";
    
	public final static String CONNETTORE_FILE_REQUEST_OUTPUT_FILE = "outputFile";
	public final static String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS = "outputFileHeaders";
	public final static String CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = "outputFileAutoCreateParentDirectory";
	public final static String CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE = "outputFileOverwriteIfExists";
	public final static String CONNETTORE_FILE_RESPONSE_INPUT_MODE = "response";
	public final static String CONNETTORE_FILE_RESPONSE_INPUT_FILE = "inputFile";
	public final static String CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS = "inputFileHeaders";
	public final static String CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ = "inputFileDeleteAfterRead";
	public final static String CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = "inputFileWaitTimeIfNotExists";
    
    
    
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
    
    public final static String CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE = "generaTrasmissione";
    public final static String CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_INVERTITA = "generaTrasmissioneInvertita";
    public final static String CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_ANDATA_RITORNO = "generaTrasmissioneAndataRitorno";
}
