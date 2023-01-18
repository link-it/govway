/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * CostantiLabel
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class CostantiLabel {
	    

    public static final String TRASPARENTE_PROTOCOL_NAME = "trasparente";
    public static final String TRASPARENTE_PROTOCOL_LABEL = "API Gateway";
    public static final MapKey<String> TRASPARENTE_PROTOCOL_MAP_KEY = Map.newMapKey(TRASPARENTE_PROTOCOL_NAME);
    
    public static final String SPCOOP_PROTOCOL_NAME = "spcoop";
    public static final String SPCOOP_PROTOCOL_LABEL = "SPCoop";
    public static final MapKey<String> SPCOOP_PROTOCOL_MAP_KEY = Map.newMapKey(SPCOOP_PROTOCOL_NAME);
    
    public static final String MODIPA_PROTOCOL_NAME = "modipa";
    public static final String MODIPA_PROTOCOL_LABEL = "ModI";
    public static final MapKey<String> MODIPA_PROTOCOL_MAP_KEY = Map.newMapKey(MODIPA_PROTOCOL_NAME);
    
    public static final String SDI_PROTOCOL_NAME = "sdi";
    public static final String SDI_PROTOCOL_LABEL = "Fatturazione Elettronica";
    public static final MapKey<String> SDI_PROTOCOL_MAP_KEY = Map.newMapKey(SDI_PROTOCOL_NAME);
    
    public static final String AS4_PROTOCOL_NAME = "as4";
    public static final String AS4_PROTOCOL_LABEL = "eDelivery";
    public static final MapKey<String> AS4_PROTOCOL_MAP_KEY = Map.newMapKey(AS4_PROTOCOL_NAME);
    
    public static final String NO_PROTOCOL_NAME = "__noprotocol";
    public static final MapKey<String> NO_PROTOCOL_MAP_KEY = Map.newMapKey(NO_PROTOCOL_NAME);
	
    
    /**
     * PROPRIETA STORE
     */
    
	public final static String STORE_HSM = "HSM";
	public final static String STORE_CARICATO_BASEDATI = "Archivio caricato";
	public final static String KEYSTORE = "KeyStore";
	public final static String TRUSTSTORE = "TrustStore";
	public final static String CRL = "CRL";
	public final static String CRLs = "CRLs";
	public final static String OCSP_POLICY = "OCSP Policy";
	public final static String KEY_ALIAS = "Key Alias";
	public final static String CERTIFICATE_ALIAS = "Certificate Alias";
	public final static String ALIAS = "Alias";
	
	/**
     * PROPRIETA CERTIFICATI
     */
	
	public final static String CERTIFICATE_SUBJECT = "Subject";
	public final static String CERTIFICATE_ISSUER = "Issuer";
	public final static String CERTIFICATE_SELF_SIGNED = "Self Signed";
	public final static String CERTIFICATE_NOT_BEFORE = "Not Before";
	public final static String CERTIFICATE_NOT_AFTER = "Not After";
	public final static String CERTIFICATE_SERIAL_NUMBER = "Serial Number";
	public final static String CERTIFICATE_SERIAL_NUMBER_HEX = "Serial Number (Hex)";
	
	
    /**
     * PROPRIETA CREDENZIALI
     */
    
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_USERNAME = "Utente";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "Password";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "Subject";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_ISSUER = "Issuer";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR = "Multiple API Keys";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_APP_ID = "App ID";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_API_KEY = "Api Key";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "UserId";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_DESCR = "Credenziali Token";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_WITH_HTTPS = "Abilitato";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY = "Token Policy";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY_VALIDAZIONE = "Token Policy di Validazione";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID = "Identificativo";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH = "Token ClientId";
	public final static String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID = "Key Id (kid) del Certificato";
   	
    /**
     * PROPRIETA CONNETTORE
     */
    
	public final static String LABEL_CONNETTORE = "Connettore";
	public final static String LABEL_TIPO_CONNETTORE = "Tipo";
	public final static String LABEL_SERVER = "Applicativo Server";
	public final static String LABEL_CONNETTORE_ENDPOINT = "Endpoint";
	
	public final static String CONNETTORE_HSM = STORE_HSM;
		
	public final static String LABEL_CONNETTORE_HTTPS_HOST_VERIFY = "Verifica Hostname";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP = "Autenticazione Http";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_BEARER = "Autenticazione Bearer";
	public final static String LABEL_CONNETTORE_BEARER_TOKEN = "Token";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN = "Autenticazione Token";
	public final static String LABEL_CONNETTORE_TOKEN_POLICY = "Policy";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS = "Autenticazione Https";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUST_ALL_CERTS = "Trust all certificates";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE = "Auth Server - TrustStore";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLs = "Auth Server - CRLs";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_OCSP_POLICY = "Auth Server - OCSP Policy";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE = "Auth Client - KeyStore";
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS = "Auth Client - Key Alias";
	
	public final static String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY = "Proxy";
	
	public final static String LABEL_CONNETTORE_JMS_NOME_CODA = "Nome";
	public final static String LABEL_CONNETTORE_JMS_USERNAME = "Utente";
	
	public static final String LABEL_INPUT_FILE_HEADER = "InputFile (Header)";
	public static final String LABEL_INPUT_FILE = "InputFile";
	public static final String LABEL_OUTPUT_FILE_HEADER = "OutputFile (Header)";
	public static final String LABEL_OUTPUT_FILE = "OutputFile";
    
	public final static String LABEL_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT = "Connection Timeout";
	public final static String LABEL_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT = "Read Timeout";
	
	public final static String LABEL_VERIFICA_CONNETTORE_EFFETTUATO_CON_SUCCESSO = "Test di connettività effettuato con successo";
	public final static String LABEL_VERIFICA_CONNETTORE_FALLITA = "Test di connettività fallito: ";
	
	public final static String LABEL_VERIFICA_CERTIFICATI_EFFETTUATA_CON_SUCCESSO = "Tutti i certificati configurati risultano validi";
	public final static String LABEL_VERIFICA_CERTIFICATI_WARNING = "Identificati certificati che necessitano di un aggiornamento: ";
	public final static String LABEL_VERIFICA_CERTIFICATI_FALLITA = "Identificati certificati non validi: ";
	
		
	
    /**
     * PROPRIETA MODI
     */
	
	public static final String MODIPA_LABEL_DEFAULT = "Default";
    public static final String MODIPA_LABEL_RIDEFINISCI = "Ridefinito";
    
	public static final String MODIPA_API_PROFILO_CANALE_LABEL = "Sicurezza Canale";
	public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01 = "ID_AUTH_CHANNEL_01";
	public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02 = "ID_AUTH_CHANNEL_02";
		
	public static final String MODIPA_SICUREZZA_CHOICE_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL = "Authorization ModI";
	public static final String MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL = "Authorization PDND";
	public static final String MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL = "Authorization OAuth";
	public static final String MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_PDND_LABEL = MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL+" + Integrity";
	public static final String MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_OAUTH_LABEL = MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL+" + Integrity";
	
	public static final String MODIPA_SICUREZZA_MESSAGGIO_FIRMA_APPLICATIVO_SUBTITLE_LABEL = "Certificato";
	public static final String MODIPA_SICUREZZA_TOKEN_FIRMA_APPLICATIVO_SUBTITLE_LABEL = "Identificativo registrato sull'Authorization Server";
	public static final String MODIPA_SICUREZZA_TOKEN_FIRMA_APPLICATIVO_SUBTITLE_LABEL_PDND = "ClientId registrato sulla PDND";
	
	public static final String MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL = "Authorization OAuth";
	
	public static final String MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01 = "ID_AUTH_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST = "ID_AUTH_REST_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP = "ID_AUTH_SOAP_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02 = "ID_AUTH_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST = "ID_AUTH_REST_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP = "ID_AUTH_SOAP_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301 = "INTEGRITY_01 con ID_AUTH_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST = "INTEGRITY_REST_01 con ID_AUTH_REST_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP = "INTEGRITY_SOAP_01 con ID_AUTH_SOAP_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302 = "INTEGRITY_01 con ID_AUTH_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST = "INTEGRITY_REST_01 con ID_AUTH_REST_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP = "INTEGRITY_SOAP_01 con ID_AUTH_SOAP_02";
	
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL = "Header HTTP del Token";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA = "Letto da property";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION = HttpConstants.AUTHORIZATION+" "+HttpConstants.AUTHENTICATION_BEARER;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA + " + "+ HttpConstants.AUTHORIZATION+" "+HttpConstants.AUTHENTICATION_BEARER; 
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA + " + "+ HttpConstants.AUTHORIZATION+" "+HttpConstants.AUTHENTICATION_BEARER +" anche nella risposta";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM_HEADER_NAME = "Custom-JWT-Signature";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM_HEADER_NAME;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM_HEADER_NAME + " + "+ HttpConstants.AUTHORIZATION+" "+HttpConstants.AUTHENTICATION_BEARER; 
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM_HEADER_NAME + " + "+ HttpConstants.AUTHORIZATION+" "+HttpConstants.AUTHENTICATION_BEARER +" anche nella risposta";
    
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_LABEL = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM_HEADER_NAME;
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_LABEL = "Gestione Integrità";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_LABEL_STANDARD = "Standard (INTEGRITY_REST_01)";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_LABEL_CUSTOM = "Personalizzata";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_HEADER_NAME_LABEL = "Header HTTP Integrity";
    
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_LABEL = "Algoritmo";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL = "Codifica Digest";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_BASE64 = "Base64"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_HEX = "Hex"; 
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL = "Algoritmo";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL = "Forma Canonica XML";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_10 = "Canonical XML 1.0";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_11 = "Canonical XML 1.1";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_EXCLUSIVE_C14N_10 = "Exclusive XML Canonicalization 1.0";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL = "Riferimento X.509";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_LABEL = "Certificate Chain";
	
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL = "Riferimento X.509";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_BINARY_SECURITY_TOKEN = "Binary Security Token";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_SECURITY_TOKEN_REFERENCE = "Issuer-Serial Security Token Reference";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_X509 = "X509 Key Identifier";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_THUMBPRINT = "Thumbprint Key Identifier";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_SKI = "SKI Key Identifier";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_LABEL = "Certificate Chain";

    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_LABEL = "Includi Signature Token";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TTL_LABEL = "Time to Live";
	
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL = "Digest Richiesta";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL = "Informazioni Utente";
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL = "Applicabilità";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI = "Richiesta e Risposta";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA = "Richiesta";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA = "Risposta";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS = "Richiesta e Risposta (con firma degli allegati)";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS = "Richiesta (con firma degli allegati)";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS = "Risposta (con firma degli allegati)";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PERSONALIZZATO = "Personalizza criteri di applicabilità";
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PARAMETRI_RISPOSTA = "Parametri della Risposta";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INTEGRITY_REST_LABEL = "Integrity"; 
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL = "Audience"; 
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL = "WSAddressing To";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL = "Verifica Audience";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL = "Verifica WSAddressing To";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL = "Identificativo Client";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL_FILTRO_RICERCA = MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL+" ClientId";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL = "Reply Audience/WSA-To";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_WSATO_LABEL = "Audience/WSA-To";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TEMPLATE_HEADER_AGID = "TEMPLATE";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL = "Claims";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL = "Claims 'Authorization'";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL = "Claims '"+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TEMPLATE_HEADER_AGID+"'";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL = "KeyStore";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_PATH_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" Path";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_X509_SUBJECT_MODE_LABEL = "X.509 Subject";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_SUBJECT_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" "+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_X509_SUBJECT_MODE_LABEL;

    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL = "HTTP Headers da firmare";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL = "SOAP Headers da firmare";

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL = "TrustStore SSL";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL = "TrustStore Certificati";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT = MODIPA_LABEL_DEFAULT;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI = MODIPA_LABEL_RIDEFINISCI;
    
	public static final String MODIPA_STORE_PATH_LABEL = "Path";
	public static final String MODIPA_STORE_TYPE_LABEL = "Tipo";
	public static final String MODIPA_TRUSTSTORE_CRLS_LABEL = "CRL File(s)";
	public static final String MODIPA_KEY_ALIAS_LABEL = "Alias Chiave Privata";
	public static final String MODIPA_TRUSTSTORE_OCSP_LABEL = "OCSP Policy";
}
