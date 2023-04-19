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

package org.openspcoop2.protocol.sdk.constants;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
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
    public static final String OPENSPCOOP2_ESITI_LOCAL_PATH = "esiti_local.properties";
    public static final String OPENSPCOOP2_ESITI_PROPERTIES = "OPENSPCOOP2_ESITI_PROPERTIES";
    
	/** Errori properties */
    public static final String OPENSPCOOP2_ERRORI_LOCAL_PATH = "errori_local.properties";
    public static final String OPENSPCOOP2_ERRORI_PROPERTIES = "OPENSPCOOP2_ERRORI_PROPERTIES";
    public static final String OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE = "HTTP_CODE";
    public static final String OPENSPCOOP2_ERRORI_TEMPLATE_ERROR_CODE = "ERROR_CODE";
    public static final String OPENSPCOOP2_ERRORI_TEMPLATE_ERROR_TYPE = "ERROR_TYPE";
    
    /** Context */
    public static final String ESITO_TRANSACTION_CONTEXT_STANDARD = "standard";
    public static final String ESITO_TRANSACTION_CONTEXT_SISTEMA = "sistema";
    
    /** Definisce un tipo di FaultCode (errore nell'intestazione) */
    public static final String FAULT_CODE_CLIENT = "Client";
    /** Definisce un tipo di FaultCode (errore nel processamento) */
    public static final String FAULT_CODE_SERVER = "Server";
    public static final String FAULT_CODE_SERVER_USEREXCEPTION = "Server.userException";
    public static final String FAULT_CODE_SERVER_CONTENT_TYPE_UNSUPPORTED = "Server.contentTypeUnsupported";
    public static final String FAULT_CODE_MUSTUNDERSTAND = "MustUnderstand";
    public static final String FAULT_CODE_VERSION_MISMATCH = "VersionMismatch";
     
    /** Definisce le costanti che contengono informazioni per retro-compatibilita' */
	public static final MapKey<String> BACKWARD_COMPATIBILITY_ACTOR = Map.newMapKey("BACKWARD_COMPATIBILITY_ACTOR");
	public static final MapKey<String> BACKWARD_COMPATIBILITY_PREFIX_FAULT_CODE = Map.newMapKey("BACKWARD_COMPATIBILITY_PREFIX_FAULT_CODE");
	
	public static final MapKey<String> CUSTOM_ACTOR = Map.newMapKey("CUSTOM_ACTOR");
    
    /** String che rappresenta il messaggio per un qualsiasi errore di processamento: SistemaNonDisponibile*/
    public static final String SISTEMA_NON_DISPONIBILE = "Sistema non disponibile";
    /** Keyword per indicare 'Gateway non disponibile' */
    public static final String KEYWORDPDD_NON_DISPONIBILE = "@NOMEPDD@";
    public static final String PDD_NON_DISPONIBILE = "Servizio erogato dal Soggetto "+CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE+" non disponibile";
    public static final String SERVIZIO_APPLICATIVO_NON_DISPONIBILE = "Servizio Applicativo non disponibile";
    
    /** Definisce una richiesta da registrare in un tracciamento */
    public static final String TRACCIAMENTO_RICHIESTA = "Richiesta";
    /** Definisce una risposta da registrare in un tracciamento */
    public static final String TRACCIAMENTO_RISPOSTA = "Risposta";
    /** Definisce una risposta da registrare in un tracciamento */
    public static final String TRACCIAMENTO_OPERAZIONE_INTEGRATION_MANAGER = "IntegrationManager";
    
    public static final String ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE = "elemento presente più volte nell'header";

    /** Tipo di identificativo: default */
    public static final String IDENTIFICATIVO_SERIALE_DB = "db";
    /** Tipo di identificativo: mysql */
    public static final String IDENTIFICATIVO_SERIALE_MYSQL = TipiDatabase.MYSQL.toString();
    /** Tipo di identificativo: static */
    public static final String IDENTIFICATIVO_SERIALE_STATIC = "static";
    /** Tipo di identificativo: dynamic */
    public static final String IDENTIFICATIVO_SERIALE_DYNAMIC = "dynamic";
        
    public static final String PREFISSO_AUTENTICAZIONE_FALLITA = "Autenticazione fallita, ";
    /** Messaggio di credenziali non fornite */
    public static final String CREDENZIALI_NON_FORNITE = "credenziali non fornite";
    public static final String CREDENZIALI_FORNITE_NON_CORRETTE =  "credenziali fornite non corrette";
    
    
	// Costanti Key per i Messaggi di Errore Integrazione
    
    public static final String KEY_ERRORE_INTEGRAZIONE_PORTA_PARAMETRI = "@ERRORE_INTEGRAZIONE_PD_PARAMETRI@";
    public static final String KEY_ERRORE_INTEGRAZIONE_PORTA_LOCATION = "@ERRORE_INTEGRAZIONE_PD_LOCATION@";
    public static final String KEY_ERRORE_INTEGRAZIONE_PORTA_URL_INVOCAZIONE = "@ERRORE_INTEGRAZIONE_PD_URL_INVOCAZIONE@";
    public static final String KEY_ERRORE_INTEGRAZIONE_PORTA_SERVIZIO_APPLICATIVO = "@ERRORE_INTEGRAZIONE_PD_SERVIZIO_APPLICATIVO@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_USERNAME = "@ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_USERNAME@";
    public static final String KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_PASSWORD = "@ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_PASSWORD@";
    public static final String KEY_ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_SUBJECT = "@ERRORE_INTEGRAZIONE_AUTENTICAZIONE_CREDENZIALE_SUBJECT@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO = "@ERRORE_INTEGRAZIONE_IDENTITA_SERVIZIO_APPLICATIVO@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO = "@ERRORE_INTEGRAZIONE_TIPO_MESSAGGIO@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA = "@ERRORE_INTEGRAZIONE_TIPO_INTERFACCIA@";
    public static final String KEY_ERRORE_INTEGRAZIONE_VALIDAZIONE_ERROR_MSG = "@ERRORE_INTEGRAZIONE_VALIDAZIONE_ERROR_MSG@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_MUST_UNDERSTAND_HEADERS = "@ERRORE_INTEGRAZIONE_MUST_UNDERSTAND_HEADER@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_SOAP_VERSION = "@ERRORE_INTEGRAZIONE_SOAP_VERSION@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_TROVATO = "@ERRORE_INTEGRAZIONE_CONTENT_TYPE_TROVATO@";
    public static final String KEY_ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI = "@ERRORE_INTEGRAZIONE_CONTENT_TYPE_SUPPORTATI@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_TROVATO = "@ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_TROVATO@";
    public static final String KEY_ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_SUPPORTATI = "@ERRORE_INTEGRAZIONE_SOAP_NAMESPACE_SUPPORTATI@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_ID_BUSTA = "@ERRORE_INTEGRAZIONE_ID_BUSTA@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_CONNETTORE_ERRORE_PDD = CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE;
    
    public static final String KEY_ERRORE_INTEGRAZIONE_MSG_ECCEZIONE = "@ERRORE_INTEGRAZIONE_ECCEZIONE@";
    public static final String KEY_ERRORE_INTEGRAZIONE_TIPO_GESTORE_CREDENZIALI = "@ERRORE_INTEGRAZIONE_TIPO_GESTORE@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_PROTOCOL = "@ERRORE_INTEGRAZIONE_PROTOCOLLO@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_TIPO = "@ERRORE_INTEGRAZIONE_TIPO@";
    public static final String KEY_ERRORE_INTEGRAZIONE_NOME = "@ERRORE_INTEGRAZIONE_NOME@";
    public static final String KEY_ERRORE_INTEGRAZIONE_VERSIONE = "@ERRORE_INTEGRAZIONE_VERSIONE@";
    public static final String KEY_ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI = "@ERRORE_INTEGRAZIONE_TIPI_SUPPORTATI@";
    
    public static final String KEY_ERRORE_INTEGRAZIONE_OGGETTO_DIVERSO_TRA_BUSTA_E_PA = "@ERRORE_INTEGRAZIONE_OGGETTO_DIVERSO_TRA_BUSTA_E_PA@";
    public static final String KEY_ERRORE_INTEGRAZIONE_DATO_BUSTA = "@ERRORE_INTEGRAZIONE_DATO_BUSTA@";
    public static final String KEY_ERRORE_INTEGRAZIONE_DATO_PA = "@ERRORE_INTEGRAZIONE_DATO_PA@";
    
    public static final String KEY_ERRORE_CUSTOM = "@ERRORE_CUSTOM@";
	
}
