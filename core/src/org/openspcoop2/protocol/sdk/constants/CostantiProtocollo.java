/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.sdk.constants;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Costanti del Protocollo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiProtocollo {
    
	/** Esiti properties */
    public final static String OPENSPCOOP2_ESITI_LOCAL_PATH = "esiti_local.properties";
    public final static String OPENSPCOOP2_ESITI_PROPERTIES = "OPENSPCOOP2_ESITI_PROPERTIES";
    
    /** Context */
    public final static String ESITO_TRANSACTION_CONTEXT_STANDARD = "standard";
    public final static String ESITO_TRANSACTION_CONTEXT_SISTEMA = "sistema";
    
    /** Definisce un tipo di FaultCode (errore nell'intestazione) */
    public static final String FAULT_CODE_CLIENT = "Client";
    /** Definisce un tipo di FaultCode (errore nel processamento) */
    public static final String FAULT_CODE_SERVER = "Server";
    public static final String FAULT_CODE_SERVER_USEREXCEPTION = "Server.userException";
    public static final String FAULT_CODE_SERVER_CONTENT_TYPE_UNSUPPORTED = "Server.contentTypeUnsupported";
    public static final String FAULT_CODE_MUSTUNDERSTAND = "MustUnderstand";
    public static final String FAULT_CODE_VERSION_MISMATCH = "VersionMismatch";
     
    /** Definisce le costanti che contengono informazioni per retro-compatibilita' */
	public final static String BACKWARD_COMPATIBILITY_ACTOR = "BACKWARD_COMPATIBILITY_ACTOR";
	public final static String BACKWARD_COMPATIBILITY_PREFIX_FAULT_CODE = "BACKWARD_COMPATIBILITY_PREFIX_FAULT_CODE";
    
    /** String che rappresenta il messaggio per un qualsiasi errore di processamento: SistemaNonDisponibile*/
    public final static String SISTEMA_NON_DISPONIBILE = "Sistema non disponibile";
    /** Keyword per indicare 'Porta di Dominio non disponibile' */
    public final static String KEYWORDPDD_NON_DISPONIBILE = "@NOMEPDD@";
    public final static String PDD_NON_DISPONIBILE = "Porta di Dominio del soggetto "+CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE+" non disponibile";
    public final static String SERVIZIO_APPLICATIVO_NON_DISPONIBILE = "Servizio Applicativo non disponibile";
    
    /** Definisce una richiesta da registrare in un tracciamento */
    public static final String TRACCIAMENTO_RICHIESTA = "Richiesta";
    /** Definisce una risposta da registrare in un tracciamento */
    public static final String TRACCIAMENTO_RISPOSTA = "Risposta";
    /** Definisce una risposta da registrare in un tracciamento */
    public static final String TRACCIAMENTO_OPERAZIONE_INTEGRATION_MANAGER = "IntegrationManager";
    
    public static final String ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE = "elemento presente più volte nell'header";

    /** Tipo di identificativo: default */
    public final static String IDENTIFICATIVO_SERIALE_DB = "db";
    /** Tipo di identificativo: mysql */
    public final static String IDENTIFICATIVO_SERIALE_MYSQL = TipiDatabase.MYSQL.toString();
    /** Tipo di identificativo: static */
    public final static String IDENTIFICATIVO_SERIALE_STATIC = "static";
        
    
	// Costanti Key per i Messaggi di Errore Integrazione
    
    public final static String KEY_ERRORE_INTEGRAZIONE_PORTA_DELEGATA_PARAMETRI = "@ERRORE_INTEGRAZIONE_PD_PARAMETRI@";
    public final static String KEY_ERRORE_INTEGRAZIONE_PORTA_DELEGATA_LOCATION = "@ERRORE_INTEGRAZIONE_PD_LOCATION@";
    public final static String KEY_ERRORE_INTEGRAZIONE_PORTA_DELEGATA_URL_INVOCAZIONE = "@ERRORE_INTEGRAZIONE_PD_URL_INVOCAZIONE@";
    public final static String KEY_ERRORE_INTEGRAZIONE_PORTA_DELEGATA_SERVIZIO_APPLICATIVO = "@ERRORE_INTEGRAZIONE_PD_SERVIZIO_APPLICATIVO@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_USERNAME = "@ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_USERNAME@";
    public final static String KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_PASSWORD = "@ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_PASSWORD@";
    public final static String KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_SUBJECT = "@ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_SUBJECT@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_IDENTIFICAZIONE_DINAMICA_PARAMETRO = "@ERRORE_INTEGRAZIONE_IDENTIFICAZIONE_DINAMICA_PARAMETRO@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO = "@ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO = "@ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_TIPO_WSDL = "@ERRORE_INTEGRAZIONE_TIPO_WSDL@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_MUST_UNDERSTAND_HEADERS = "@ERRORE_INTEGRAZIONE_MUST_UNDERSTAND_HEADER@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_SOAP_VERSION = "@ERRORE_INTEGRAZIONE_SOAP_VERSION@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_TROVATO = "@ERRORE_INTEGRAZIONE_CONTENT_TYPE_TROVATO@";
    public final static String KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI = "@ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_TROVATO = "@ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_TROVATO@";
    public final static String KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_SUPPORTATI = "@ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_SUPPORTATI@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_ID_BUSTA = "@ERRORE_INTEGRAZIONE_ID_BUSTA@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_CONNETTORE_ERRORE_PDD = CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE;
    
    public final static String KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE = "@ERRORE_INTEGRAZIONE_ECCEZIONE@";
    public final static String KEY_ERRORE_INTEGRAZIONE_TIPO_GESTORE_CREDENZIALI = "@ERRORE_INTEGRAZIONE_TIPO_GESTORE@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_PROTOCOL = "@ERRORE_INTEGRAZIONE_PROTOCOLLO@";
    
    public final static String KEY_ERRORE_INTEGRAZIONE_TIPO = "@ERRORE_INTEGRAZIONE_TIPO@";
    public final static String KEY_ERRORE_INTEGRAZIONE_NOME = "@ERRORE_INTEGRAZIONE_NOME@";
    public final static String KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI = "@ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI@";
	
}
