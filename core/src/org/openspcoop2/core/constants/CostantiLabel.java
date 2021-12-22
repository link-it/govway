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



package org.openspcoop2.core.constants;

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
    
    public static final String SPCOOP_PROTOCOL_NAME = "spcoop";
    public static final String SPCOOP_PROTOCOL_LABEL = "SPCoop";
    
    public static final String MODIPA_PROTOCOL_NAME = "modipa";
    public static final String MODIPA_PROTOCOL_LABEL = "ModI";
    
    public static final String SDI_PROTOCOL_NAME = "sdi";
    public static final String SDI_PROTOCOL_LABEL = "Fatturazione Elettronica";
    
    public static final String AS4_PROTOCOL_NAME = "as4";
    public static final String AS4_PROTOCOL_LABEL = "eDelivery";
	
   	
    /**
     * PROPRIETA MODI
     */
	
	public static final String MODIPA_LABEL_DEFAULT = "Default";
    public static final String MODIPA_LABEL_RIDEFINISCI = "Ridefinito";
    
	public static final String MODIPA_API_PROFILO_CANALE_LABEL = "Sicurezza Canale";
	public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01 = "ID_AUTH_CHANNEL_01";
	public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02 = "ID_AUTH_CHANNEL_02";
	
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
   
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_LABEL = "Algoritmo";
	
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
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INTEGRITY_REST_LABEL = "Integrity"; 
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL = "Audience"; 
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL = "WSAddressing To";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL = "Verifica Audience";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL = "Verifica WSAddressing To";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL = "Identificativo Client";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL = "Reply Audience/WSA-To";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_WSATO_LABEL = "Audience/WSA-To";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TEMPLATE_HEADER_AGID = "TEMPLATE";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL = "Claims";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL = "Claims 'Authorization'";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL = "Claims '"+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TEMPLATE_HEADER_AGID+"'";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL = "KeyStore";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_PATH_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" Path";
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_SUBJECT_MODE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL+" X.509 Subject";

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
}
