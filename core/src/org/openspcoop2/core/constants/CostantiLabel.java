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

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * CostantiLabel
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class CostantiLabel {
	
	private CostantiLabel() {}
	    

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
    
	public static final String STORE_HSM = "HSM";
	public static final String STORE_CARICATO_BASEDATI = "Archivio caricato";
	public static final String KEYSTORE = "KeyStore";
	public static final String TRUSTSTORE = "TrustStore";
	public static final String CRL = "CRL";
	public static final String CRLS = "CRLs";
	public static final String OCSP_POLICY = "OCSP Policy";
	public static final String KEY_ALIAS = "Key Alias";
	public static final String CERTIFICATE_ALIAS = "Certificate Alias";
	public static final String ALIAS = "Alias";
	public static final String KEY_PAIR = "Key Pair";
	public static final String PRIVATE_KEY = "Private Key";
	public static final String PUBLIC_KEY = "Public Key";
	public static final String JWKS = "JWKs";
	
    public static final String KEYSTORE_TYPE_KEY_PAIR = KeystoreType.KEY_PAIR.getLabel();
    public static final String KEYSTORE_TYPE_PUBLIC_KEY = KeystoreType.PUBLIC_KEY.getLabel();
    public static final String KEYSTORE_TYPE_JWK = KeystoreType.JWK_SET.getLabel();
    public static final String KEYSTORE_TYPE_JKS = KeystoreType.JKS.getLabel();
    public static final String KEYSTORE_TYPE_PKCS12 = KeystoreType.PKCS12.getLabel();
	
	/**
     * PROPRIETA CERTIFICATI
     */
	
	public static final String CERTIFICATE_SUBJECT = "Subject";
	public static final String CERTIFICATE_ISSUER = "Issuer";
	public static final String CERTIFICATE_SELF_SIGNED = "Self Signed";
	public static final String CERTIFICATE_NOT_BEFORE = "Not Before";
	public static final String CERTIFICATE_NOT_AFTER = "Not After";
	public static final String CERTIFICATE_SERIAL_NUMBER = "Serial Number";
	public static final String CERTIFICATE_SERIAL_NUMBER_HEX = "Serial Number (Hex)";
	
	
    /**
     * PROPRIETA CREDENZIALI
     */
    
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_USERNAME = "Utente";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_PASSWORD = "Password";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_SUBJECT = "Subject";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_ISSUER = "Issuer";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR = "Multiple API Keys";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_APP_ID = "App ID";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_API_KEY = "Api Key";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL = "UserId";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_DESCR = "Credenziali Token";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_WITH_HTTPS = "Abilitato";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY = "Token Policy";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY_VALIDAZIONE = "Token Policy di Validazione";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID = "Identificativo";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH = "Token ClientId";
	public static final String LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID = "Key Id (kid) del Certificato";
   	
    /**
     * PROPRIETA CONFIGURAZIONE
     */
	
	public static final String LABEL_CONFIGURAZIONE_STATO = "Stato";
	public static final String LABEL_CONFIGURAZIONE_TIPO_AUTENTICAZIONE_TOKEN = "Autenticazione Token";
	public static final String LABEL_CONFIGURAZIONE_TIPO_AUTENTICAZIONE_TRASPORTO = "Autenticazione Trasporto";
	public static final String LABEL_CONFIGURAZIONE_TIPO_AUTENTICAZIONE_CANALE = "Autenticazione Canale";
	public static final String LABEL_CONFIGURAZIONE_RATE_LIMITING = "Rate Limiting";
	public static final String LABEL_CONFIGURAZIONE_VALIDAZIONE = "Validazione";
	public static final String LABEL_CONFIGURAZIONE_CACHE_RISPOSTA = "Caching Risposta";
	public static final String LABEL_CONFIGURAZIONE_MESSAGE_SECURITY = "Sicurezza Messaggio";
	public static final String LABEL_CONFIGURAZIONE_MTOM = "MTOM";
	public static final String LABEL_CONFIGURAZIONE_TRASFORMAZIONE = "Trasformazione";
	public static final String LABEL_CONFIGURAZIONE_TRANSAZIONI = "Tracciamento";
	public static final String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA= "Correlazione Applicativa";
	public static final String LABEL_CONFIGURAZIONE_TIPO_DUMP = "Registrazione Messaggi";
	public static final String LABEL_CONFIGURAZIONE_CORS= "Gestione CORS";
	public static final String LABEL_CONFIGURAZIONE_CORS_ORIGIN= "Allow Origins";
	
    /**
     * PROPRIETA CONNETTORE
     */
    
	public static final String LABEL_CONNETTORE = "Connettore";
	public static final String LABEL_TIPO_CONNETTORE = "Tipo";
	public static final String LABEL_SERVER = "Applicativo Server";
	public static final String LABEL_CONNETTORE_ENDPOINT = "Endpoint";
	
	public static final String CONNETTORE_HSM = STORE_HSM;
		
	public static final String LABEL_CONNETTORE_HTTPS_HOST_VERIFY = "Verifica Hostname";
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP = "Autenticazione Http";
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_BEARER = "Autenticazione Bearer";
	public static final String LABEL_CONNETTORE_BEARER_TOKEN = "Token";
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN = "Autenticazione Token";
	public static final String LABEL_CONNETTORE_TOKEN_POLICY = "Policy";
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS = "Autenticazione Https";
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUST_ALL_CERTS = "Trust all certificates";
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE = "Auth Server - TrustStore";
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLS = "Auth Server - CRLs";
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_OCSP_POLICY = "Auth Server - OCSP Policy";
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE = "Auth Client - KeyStore";
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS = "Auth Client - Key Alias";
	
	public static final String LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY = "Proxy";
	
	public static final String LABEL_CONNETTORE_JMS_NOME_CODA = "Nome";
	public static final String LABEL_CONNETTORE_JMS_USERNAME = "Utente";
	
	public static final String LABEL_INPUT_FILE_HEADER = "InputFile (Header)";
	public static final String LABEL_INPUT_FILE = "InputFile";
	public static final String LABEL_OUTPUT_FILE_HEADER = "OutputFile (Header)";
	public static final String LABEL_OUTPUT_FILE = "OutputFile";
    
	public static final String LABEL_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT = "Connection Timeout";
	public static final String LABEL_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT = "Read Timeout";
	
	public static final String LABEL_VERIFICA_CONNETTORE_EFFETTUATO_CON_SUCCESSO = "Test di connettività effettuato con successo";
	public static final String LABEL_VERIFICA_CONNETTORE_FALLITA = "Test di connettività fallito: ";
	
	public static final String LABEL_VERIFICA_CERTIFICATI_EFFETTUATA_CON_SUCCESSO = "Tutti i certificati configurati risultano validi";
	public static final String LABEL_VERIFICA_CERTIFICATI_WARNING = "Identificati certificati che necessitano di un aggiornamento: ";
	public static final String LABEL_VERIFICA_CERTIFICATI_FALLITA = "Identificati certificati non validi: ";
	
		
	
    /**
     * PROPRIETA MODI
     */
	
	public static final String MODIPA_LABEL_UNDEFINED = "-";
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
	
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401 = "INTEGRITY_02 con ID_AUTH_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST = "INTEGRITY_REST_02 con ID_AUTH_REST_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402 = "INTEGRITY_02 con ID_AUTH_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST = "INTEGRITY_REST_02 con ID_AUTH_REST_02";
	
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH = "Generazione Token";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LOCALE = MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL;
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_PDND = MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL;
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_OAUTH = MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL;
	
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
	
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_MODE_LABEL = "";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_MODE_LABEL_PAYLOAD_HTTP = "Presente solo con payload HTTP";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_MODE_LABEL_ALWAYS = "Presente per tutte le risorse";
    
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
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL = "URL (x5u)";
	
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
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL = "Informazioni Audit";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE_LABEL = "Audit Audience";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL = "Pattern";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01 = "AUDIT_REST_01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02 = "AUDIT_REST_02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD = "AUDIT_LEGACY";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_LABEL = "Schema Dati";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_AUDIT_LABEL = "Schema Dati Audit";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE = "Opzionale";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE = "Codice Ente";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER = "UserID Utente";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER = "Indirizzo IP Utente";
    
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
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL = "KeyStore";
    
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_APPLICATIVO = "Definito nell'applicativo";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE = "Definito nella fruizione";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_TOKEN_POLICY = "Definito nella token policy";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL = "KeyStore";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_PATH_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" Path";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_X509_SUBJECT_MODE_LABEL = "X.509 Subject";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_X509_ISSUER_MODE_LABEL = "X.509 Issuer";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_SUBJECT_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" "+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_X509_SUBJECT_MODE_LABEL;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_ISSUER_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" "+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_X509_ISSUER_MODE_LABEL;

    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL = "HTTP Headers da firmare";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL = "SOAP Headers da firmare";

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL = "TrustStore SSL";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL = "TrustStore Certificati";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_UNDEFINED = MODIPA_LABEL_UNDEFINED;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT = MODIPA_LABEL_DEFAULT;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI = MODIPA_LABEL_RIDEFINISCI;
    
	public static final String MODIPA_STORE_PATH_LABEL = "Path";
	public static final String MODIPA_STORE_TYPE_LABEL = "Tipo";
	public static final String MODIPA_STORE_PATH_PRIVATE_KEY_LABEL = "Chiave Privata";
	public static final String MODIPA_STORE_PATH_PUBLIC_KEY_LABEL = "Chiave Pubblica";
	public static final String MODIPA_STORE_ALGORITHM_ID_LABEL = "";
	public static final String MODIPA_TRUSTSTORE_CRLS_LABEL = "CRL File(s)";
	public static final String MODIPA_KEY_ALIAS_LABEL = "Alias Chiave Privata";
	public static final String MODIPA_TRUSTSTORE_OCSP_LABEL = "OCSP Policy";
}
