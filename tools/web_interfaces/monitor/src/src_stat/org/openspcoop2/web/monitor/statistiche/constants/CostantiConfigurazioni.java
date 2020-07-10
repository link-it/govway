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
package org.openspcoop2.web.monitor.statistiche.constants;

/**
 * CostantiConfigurazioni
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CostantiConfigurazioni {
	
	// Export 
	public static final String PARAMETER_IDS = "ids";
	public static final String PARAMETER_IS_ALL = "isAll";
	public static final String PARAMETER_RUOLO = "ruolo";
	
	public static final String PARAMETER_IDS_ORIGINALI = "idsOriginali";
	public static final String PARAMETER_IS_ALL_ORIGINALE = "isAllOriginale";
	public static final String PARAMETER_RUOLO_ORIGINALE = "ruoloOriginale";
	
	public static final String CONFIGURAZIONI_EXPORTER_SERVLET_NAME = "configurazioniexporter";

	public static final String NON_SELEZIONATO = "--"; 
	public static final String FRUIZIONE_VALUE = "fruizione"; 
	public static final String EROGAZIONE_VALUE = "erogazione"; 
	
	
	public static final String LABEL_INFORMAZIONI_SERVIZI = "Informazioni Servizi";
	public static final String LABEL_INFORMAZIONI_GENERALI = "Informazioni Generali";
	public static final String LABEL_SERVIZI = "API";
	public static final String LABEL_SOGGETTI = "Soggetti";
	public static final String LABEL_REGISTRO = "Registro";
	
	public static final String LABEL_INFORMAZIONI_SOGGETTO = "API del Soggetto {0}";
	public static final String LABEL_INFORMAZIONI_SERVIZIO = "API {0}";
	
	// Label Informazioni generali

	public static final String CONF_UTENTI_LABEL = "Utenti";
	public static final String CONF_SOGGETTI_OPERATIVI_LABEL = "Soggetti 'Operativi'";
	public static final String CONF_SOGGETTI_ESTERNI_LABEL = "Soggetti 'Esterni'";
	public static final String CONF_PORTE_DI_DOMINIO_LABEL = "Porte di Dominio";
	public static final String CONF_SERVIZI_APPLICATIVI_LABEL = "Applicativi";

	// Label informazioni Servizi

	public static final String CONF_AZIONI_LABEL = "Azioni";	
	public static final String CONF_ASPC_LABEL = "API";
	public static final String CONF_ASPS_LABEL = "Erogazioni";
	public static final String CONF_FRUIZIONI_SERVIZIO_LABEL = "Fruizioni";

	// Label Integrazione applicativi

	public static final String CONF_PORTE_DELEGATE_LABEL = "Porte Delegate";
	public static final String CONF_PORTE_APPLICATIVE_LABEL = "Porte Applicative";
	public static final String CONF_SERVIZI_APPLICATIVI_FRUITORE_LABEL = "Applicativo con ruolo Fruitore";
	public static final String CONF_SERVIZI_APPLICATIVI_EROGATORE_LABEL = "Applicativo con ruolo Erogatore";
	
	
	public static final String LABEL_DOTS = ":";
	public static final String VALUE_USER = "user";
	public static final String VALUE_APP_ID = "appId";
	public static final String VALUE_SUBJECT = "subject";
	public static final String VALUE_PRINCIPAL = "principal";
	public static final String LABEL_SOGGETTI_AUTORIZZATI = "Soggetti Autorizzati";
	public static final String LABEL_RUOLI_SOGGETTI_AUTORIZZATI = "Ruoli o Soggetti Autorizzati";
	public static final String LABEL_RUOLI_ALL = "tutti";
	public static final String LABEL_RUOLI_ANY = "almeno uno";
	public static final String LABEL_SERVIZIO = "API";
	public static final String LABEL_EROGATORE = "Erogatore";
	public static final String LABEL_NOME_PORTA_APPLICATIVA = "Nome Porta Applicativa";
	public static final String LABEL_EXPRESSIONE_REGOLARE = "Expressione Regolare";
	public static final String LABEL_EXPRESSIONE_X_PATH = "Expressione XPath";
	public static final String LABEL_IDENTIFICAZIONE_AZIONE = "Modalita' Identificazione Azione";
	public static final String LABEL_PATTERN = "Pattern";
	public static final String LABEL_FORCE_INTERFACE_BASED = "Force Interface Based";
	public static final String LABEL_URL_DI_BASE = "URL di Base";
	public static final String LABEL_AZIONI = "Azioni";
	public static final String LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE = "Utilizzo del Servizio senza azione";
	public static final String LABEL_URL_DI_INVOCAZIONE = "URL di Invocazione";
	public static final String VALUE_FONTE_QUALISIASI = "fonte: qualisiasi";
	public static final String VALUE_FONTE_INTERNA = "fonte: interna";
	public static final String VALUE_FONTE_ESTERNA = "fonte: esterna";
	public static final String LABEL_RUOLI = "Ruoli";
	public static final String LABEL_APPLICATIVI_AUTORIZZATI = "Applicativi Autorizzati";
	public static final String LABEL_APPLICATIVI_RUOLI_AUTORIZZATI = "Ruoli o Applicativi Autorizzati";
	public static final String LABEL_OPZIONALE = "Opzionale";
	public static final String LABEL_STATO = "Stato";
	public static final String LABEL_DEBUG = "Debug";
	public static final String LABEL_INPUT_FILE_HEADER = "InputFile (Header)";
	public static final String LABEL_INPUT_FILE = "InputFile";
	public static final String LABEL_OUTPUT_FILE_HEADER = "OutputFile (Header)";
	public static final String LABEL_OUTPUT_FILE = "OutputFile";
	public static final String LABEL_USERNAME = "Username";
	public static final String LABEL_NOME = "Nome";
	public static final String LABEL_CONNECTION_FACTORY = "Connection Factory";
	public static final String LABEL_SEND_AS = "SendAs";
	public static final String LABEL_PROXY_USERNAME = "Proxy Username";
	public static final String LABEL_PROXY = "Proxy";
	public static final String LABEL_KEY_STORE = "KeyStore";
	public static final String LABEL_CLIENT_CERTIFICATE = "Client Certificate";
	public static final String LABEL_SERVIZIO_APPLICATIVO = "Applicativo";
	public static final String LABEL_MESSAGE_BOX = "MessageBox";
	public static final String LABEL_SBUSTAMENTO_SOAP = "Sbustamento SOAP";
	public static final String LABEL_SBUSTAMENTO_PROTOCOLLO = "Sbustamento Protocollo";
	public static final String LABEL_TIPO = "Tipo";
	public static final String LABEL_TRUST_STORE = "TrustStore";
	public static final String LABEL_HOSTNAME_VERIFIER = "Hostname Verifier";
	public static final String LABEL_SSL_TYPE = "SSL Type";
	public static final String LABEL_HTTP_BASIC_USERNAME = "HttpBasic Username";
	public static final String LABEL_URL = "URL";
	public static final String LABEL_AZIONE = "Azione";
	public static final String LABEL_AZIONE_RISORSA = "Azione/Risorsa";
	public static final String LABEL_MODALITA_INOLTRO = "Modalità Inoltro";
	public static final String SEPARATORE_TIPONOME = "/";
	public static final String LABEL_AZIONE_STAR = "default"; //"*";
	public static final String LABEL_ASPC = "API";
	public static final String LABEL_PORT_TYPE = "PortType";
	public static final String LABEL_TIPO_CODA_JMS = "Tipo JMS";
	public static final String LABEL_MODALITA = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO;
	
	public static final String LABEL_TOKEN_STATO = "Token (Stato)";
	public static final String LABEL_TOKEN_OPZIONALE = "Token (Opzionale)";
	public static final String LABEL_TOKEN_POLICY = "Token (Policy)";
	public static final String LABEL_TOKEN_VALIDAZIONE_INPUT = "Token (Validazione JWT)";
	public static final String LABEL_TOKEN_INTROSPECTION = "Token (Introspection)";
	public static final String LABEL_TOKEN_USER_INFO = "Token (UserInfo)";
	public static final String LABEL_TOKEN_FORWARD = "Token (Token Forward) ";
	
	// label Colonne CSV
	public static final String PREFIX_COLONNA = "col_";
	
	public static final String LABEL_FRUITORE = "Fruitore";
	public static final String LABEL_PORTA_DELEGATA = "Porta Delegata";
	public static final String LABEL_PORTA_APPLICATIVA = "Porta Applicativa";
	public static final String LABEL_ESPRESSIONE = "Espressione";
	public static final String LABEL_AUTENTICAZIONE_STATO = "Autenticazione (Stato)";
	public static final String LABEL_AUTENTICAZIONE_OPZIONALE = "Autenticazione (Opzionale)";
	public static final String LABEL_AUTENTICAZIONE_TOKEN_ISSUER = "Autenticazione (Token Issuer)";
	public static final String LABEL_AUTENTICAZIONE_TOKEN_CLIENT_ID = "Autenticazione (Token ClientID)";
	public static final String LABEL_AUTENTICAZIONE_TOKEN_SUBJECT = "Autenticazione (Token Subject)";
	public static final String LABEL_AUTENTICAZIONE_TOKEN_USERNAME = "Autenticazione (Token Username)";
	public static final String LABEL_AUTENTICAZIONE_TOKEN_EMAIL = "Autenticazione (Token eMail)";
	public static final String LABEL_AUTORIZZAZIONE_STATO = "Autorizzazione (Stato)";
	public static final String LABEL_AUTORIZZAZIONE_RUOLI_STATO = "Autorizzazione (Ruoli)";
	public static final String LABEL_AUTORIZZAZIONE_TOKEN_CLAIMS = "Autorizzazione (Token Claims)";
	public static final String LABEL_AUTORIZZAZIONE_APPLICATIVI_AUTORIZZATI_STATO = "Autorizzazione (Applicativi Autorizzati)";
	public static final String LABEL_AUTORIZZAZIONE_SOGGETTI_AUTORIZZATI_STATO = "Autorizzazione (Soggetti Autorizzati)";
	public static final String LABEL_RUOLI_RICHIESTI = "Ruoli Richiesti";
	public static final String LABEL_AUTORIZZAZIONE_SCOPE_STATO = "Autorizzazione (Scope)";
	public static final String LABEL_SCOPE_RICHIESTI = "Scope Richiesti";
	public static final String LABEL_SCOPE_FORNITI = "Scope";
	public static final String LABEL_CONNETTORE_TIPO = "Connettore (Tipo)";
	public static final String LABEL_CONNETTORE_ENDPOINT = "Connettore (Endpoint)";
	public static final String LABEL_CONNETTORE_DEBUG = "Connettore (Debug)";
	public static final String LABEL_CONNETTORE_USERNAME = "Connettore (Username)";
	public static final String LABEL_CONNETTORE_PROXY_ENDPOINT = "Connettore (Proxy Endpoint)";
	public static final String LABEL_CONNETTORE_SSL_TYPE = "Connettore (SSL Type)";
	public static final String LABEL_CONNETTORE_ALTRE_CONFIGURAZIONI = "Connettore (Altre Configurazioni)";
	public static final String LABEL_CONNETTORE_PROXY_USERNAME = "Connettore (Proxy Username)";
	public static final String LABEL_CONNETTORE_HTTPS_KEY_STORE_LOCATION = "Connettore (KeyStore Location)";
	public static final String LABEL_CONNETTORE_HTTPS_TRUST_STORE_LOCATION = "Connettore (TrustStore Location)";
	public static final String LABEL_CONNETTORE_CLIENT_CERTIFICATE = "Connettore (Client Certificate)";
	public static final String LABEL_CONNETTORE_HOSTNAME_VERIFIER = "Connettore (Hostname Verifier)";
	public static final String LABEL_CONNETTORE_TRUST_STORE = "Connettore (TrustStore)";
	public static final String LABEL_CONNETTORE_KEY_STORE = "Connettore (KeyStore)";
	public static final String LABEL_VALIDAZIONE_STATO = "Validazione (Stato)";
	public static final String LABEL_VALIDAZIONE_TIPO = "Validazione (Tipo)";
	public static final String LABEL_VALIDAZIONE_MTOM = "Validazione (Accetta MTOM)";
	
	public static final String LABEL_SICUREZZA_MESSAGGIO_STATO = "Sicurezza Messaggio (Stato)";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RICHIESTA = "Schema Sicurezza (Richiesta)";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RISPOSTA = "Schema Sicurezza (Risposta)";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO = "Nessuno";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE = "Configurazione Manuale";
	public static final String VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT = "default";
	
	public static final String LABEL_MTOM_RICHIESTA = "MTOM (Richiesta)";
	public static final String LABEL_MTOM_RISPOSTA = "MTOM (Risposta)";
	
	public static final String LABEL_CORRELAZIONE_APPLICATIVA_RICHIESTA = "Correlazione Applicativa (Richiesta)";
	public static final String LABEL_CORRELAZIONE_APPLICATIVA_RISPOSTA = "Correlazione Applicativa (Risposta)";
	
	// Valori Mode delle Porte
	public static final String VALUE_PORTE_DELEGATED_BY = "delegatedBy";
	
}

