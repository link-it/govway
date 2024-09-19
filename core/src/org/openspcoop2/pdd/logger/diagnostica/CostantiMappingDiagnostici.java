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

package org.openspcoop2.pdd.logger.diagnostica;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.security.message.constants.SecurityConstants;

import org.openspcoop2.pdd.logger.record.CostantiDati;
import org.openspcoop2.pdd.logger.record.InfoDato;

/**     
 * CostantiMappingDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiMappingDiagnostici {
	
	private CostantiMappingDiagnostici() {}

	public static final int MAX_DIAGNOSTIC_LIST_ROW_1 = 15;
	public static final int MAX_DIAGNOSTIC_LIST_ROW_2 = 30; // è un TEXT questa colonna
	public static final int MAX_DIAGNOSTIC_LIST_SUM = MAX_DIAGNOSTIC_LIST_ROW_1+MAX_DIAGNOSTIC_LIST_ROW_2;
	
	/**public static void setMAX_DIAGNOSTIC_LIST_ROW_2(int p) {
		CostantiMappingDiagnostici.MAX_DIAGNOSTIC_LIST_ROW_2 = p;
		CostantiMappingDiagnostici.MAX_DIAGNOSTIC_LIST_SUM =  MAX_DIAGNOSTIC_LIST_ROW_1+MAX_DIAGNOSTIC_LIST_ROW_2;
	}*/
	
	public static final String DIAGNOSTIC_ORIGINAL_POSITION = "DIAGNOSTIC_ORIGINAL_POSITION";
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE;
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_VALUE = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_VALUE;
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR;
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR;
	
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR;
	
	public static final char NON_PRESENTE = CostantiDati.NON_PRESENTE.charAt(0);
	
	public static final String SEPARATOR = CostantiDati.SEPARATOR;
	
	/**
	 * Posizione 1 (1 carattere):
  		- (-) Non presente
  		- (N) indica che la transazione ha emesso diagnostici non ricostruibili
  		- (R) indica che la transazione ha emesso diagnostici non ricostruibili
	 **/
	public static final InfoDato DIAGNOSTICI_EMESSI = new InfoDato(0, 
			"Indicazione se la transazione ha emesso diagnostici ricostruibili (R), non ricostruibile (N) o non ha emesso alcuna traccia (-)");
	public static final char DIAGNOSTICI_EMESSI_RICOSTRUIBILI = 'R';
	public static final char DIAGNOSTICI_EMESSI_NON_RICOSTRUIBILI = 'N';
	
	public static final InfoDato DIAGNOSTICI_EMESSI_NON_RICOSTRUIBILI_MOTIVO = new InfoDato(1, 
			"Motivo che indica come mai i diagnostici non sono ricostruibili");
	
	/**
	 * 	Posizione 2 (8 caratteri)
	  - Data del primo diagnostico nel formato 'AAAAMMDD'
	 */
	public static final InfoDato DIAGNOSTICI_EMISSIONE_FIRST_DATE = new InfoDato(1, 
			"Indica la data del primo messaggio diagnostico emesso nel formato AAAAMMDD");
	
	/**
	 * 	Posizione 3 (max 30 caratteri o -)
	  - tipoAutorizzazioneBusta
	 */
	public static final InfoDato TIPO_AUTORIZZAZIONE = new InfoDato(2, 
			"Indica il tipo di autorizzazione utilizzato dalla PdD");
	
	/**
	 * 	Posizione 4 (max 20 caratteri o -)
	  - codice di trasporto della richiesta
	 */
	public static final InfoDato CODICE_TRASPORTO_RICHIESTA = new InfoDato(3, 
			"Indica il codice di trasporto ottenuto durante la fase di consegna della richiesta");
	
	/**
	 * 	Posizione 5 (max 20 caratteri o -)
	  - codice di trasporto della risposta
	 */
	public static final InfoDato CODICE_TRASPORTO_RISPOSTA = new InfoDato(4, 
			"Indica il codice di trasporto ottenuto durante la fase di consegna della risposta");
	
	/**
	 * 	Posizione 6 (max 20 caratteri o -)
	  - tipo di connettore
	 */
	public static final InfoDato TIPO_CONNETTORE = new InfoDato(5, 
			"Indica il tipo di connettore utilizzato per la consegna del messaggio di richiesta");
		
	/**
	 * 	Posizione 7 (max 10 caratteri o -)
	  - max threads threshold
	 */
	public static final InfoDato MAX_THREADS_THRESHOLD = new InfoDato(6, 
			"Indica il numero massimo di richieste simultanee");
	
	/**
	 * 	Posizione 8 (max 2 caratteri o -)
	  - threshold che attiva il controllo del traffico (in percentuale)
	 */
	public static final InfoDato CONTROLLO_TRAFFICO_THRESHOLD = new InfoDato(7, 
			"Indica la % di richieste simultanee rispetto al numero massimo, per attivare il controllo del traffico");
	
	/**
	 * 	Posizione 9 (max 10 caratteri o -)
	  - active max threads
	 */
	public static final InfoDato ACTIVE_THREADS = new InfoDato(8, 
			"Indica il numero di threads attivi");
	
	/**
	 * 	Posizione 10 (max 4 caratteri o -)
	  - numero di policy configurate
	 */
	public static final InfoDato NUMERO_POLICY_CONFIGURATE = new InfoDato(9, 
			"Indica il numero di policy attivate");
	
	/**
	 * 	Posizione 11 (max 4 caratteri o -)
	  - numero di policy configurate però disabilitate
	 */
	public static final InfoDato NUMERO_POLICY_DISABILITATE = new InfoDato(10, 
			"Indica il numero di policy attivate con stato disabilitate");
	
	/**
	 * 	Posizione 12 (max 4 caratteri o -)
	  - numero di policy configurate però che non rispettano il filtro per i dati della transazione in essere
	 */
	public static final InfoDato NUMERO_POLICY_FILTRATE = new InfoDato(11, 
			"Indica il numero di policy configurate però che non rispettano il filtro per i dati della transazione in essere");
	
	/**
	 * 	Posizione 13 (max 4 caratteri o -)
	  - numero di policy configurate che non possono essere applicate per le condizioni del sistema
	 */
	public static final InfoDato NUMERO_POLICY_NON_APPLICATE = new InfoDato(12, 
			"Indica il numero di policy configurate che non possono essere applicate per le condizioni del sistema");
	
	/**
	 * 	Posizione 14 (max 4 caratteri o -)
	  - numero di policy configurate che soddisfano i valori di soglia
	 */
	public static final InfoDato NUMERO_POLICY_RISPETTATE= new InfoDato(13, 
			"Indica il numero di policy configurate che soddisfano i valori di soglia");
	
	/**
	 * 	Posizione 15 (max 4 caratteri o -)
	  - numero di policy configurate che violano i valori di soglia
	 */
	public static final InfoDato NUMERO_POLICY_VIOLATE= new InfoDato(14, 
			"Indica il numero di policy configurate che violano i valori di soglia");
	
	/**
	 * 	Posizione 16 (max 4 caratteri o -)
	  - numero di policy configurate che violano i valori di soglia (warningOnly)
	 */
	public static final InfoDato NUMERO_POLICY_VIOLATE_WARNING_ONLY= new InfoDato(15, 
			"Indica il numero di policy configurate che violano i valori di soglia (warningOnly)");
	
	/**
	 * 	Posizione 17 (max 4 caratteri o -)
	  - numero di policy configurate la cui verifica ha provocato un errore
	 */
	public static final InfoDato NUMERO_POLICY_IN_ERRORE= new InfoDato(16, 
			"Indica il numero di policy configurate la cui verifica ha provocato un errore");
	
	/**
	 * 	Posizione 18 (max 20 caratteri o -)
	  - tipoAutenticazione
	 */
	public static final InfoDato TIPO_AUTENTICAZIONE = new InfoDato(17, 
			"Indica il tipo di autenticazione utilizzato dalla PdD");
	
	/**
	 * 	Posizione 19 (max 20 caratteri o -)
	  - tipoAutorizzazioneContenuti
	 */
	public static final InfoDato TIPO_AUTORIZZAZIONE_CONTENUTI = new InfoDato(18, 
			"Indica il tipo di autorizzazione contenuti utilizzato dalla PdD");
	
	/**
	 * 	Posizione 20 (max 3 caratteri o -)
	  - tipoValidazioneContenuti
	 */
	
	public static final char TIPO_VALIDAZIONE_CONTENUTI_OPENSPCOOP = 'O';
	public static final char TIPO_VALIDAZIONE_CONTENUTI_INTERFACE = 'I';
	public static final char TIPO_VALIDAZIONE_CONTENUTI_XSD = 'X';
	public static final char TIPO_VALIDAZIONE_CONTENUTI_WARN = 'W';
	public static final char TIPO_VALIDAZIONE_CONTENUTI_MTOM = 'M';
	
	public static final InfoDato TIPO_VALIDAZIONE_CONTENUTI = new InfoDato(19, 
			"Indica il tipo di validazione contenuti utilizzato dalla PdD");
	
	/**
	 * 	Posizione 21 (max 1 caratteri o -)
	  - tipoProcessamentoMtomRichiesta
	 */
	
	public static final char TIPO_MTOM_PROCESSAMENTO_DISABLE = 'D';
	public static final char TIPO_MTOM_PROCESSAMENTO_PACKAGING = 'P';
	public static final char TIPO_MTOM_PROCESSAMENTO_UNPACKAGING = 'U';
	public static final char TIPO_MTOM_PROCESSAMENTO_VERIFY = 'V';
	
	public static final InfoDato TIPO_PROCESSAMENTO_MTOM_RICHIESTA = new InfoDato(20, 
			"Indica il tipo di processamento mtom della richiesta utilizzato dalla PdD");
	
	/**
	 * 	Posizione 22 (max 1 caratteri o -)
	  - tipoProcessamentoMtomRisposta
	 */
	
	public static final InfoDato TIPO_PROCESSAMENTO_MTOM_RISPOSTA = new InfoDato(21, 
			"Indica il tipo di processamento mtom della risposta utilizzato dalla PdD");
	
	/**
	 * 	Posizione 23 (max 6 caratteri o -)
	  - tipoProcessamentoMessageSecurityRichiesta
	 */
	
	public static final char TIPO_SECURITY_ENGINE_WSS4J = 'W';
	public static final char TIPO_SECURITY_ENGINE_SOAPBOX = 'S';
	public static final char TIPO_SECURITY_ENGINE_DSS = 'D';
	public static final char TIPO_SECURITY_ENGINE_JOSE = 'J';
	public static final char TIPO_SECURITY_ENGINE_XML = 'X';
	
	private static final Map<String, String> MAP_SECURITY_ACTION = new HashMap<>();
	public static Map<String, String> getMapSecurityAction() {
		return MAP_SECURITY_ACTION;
	}
	static {
		// deve essere 1 carattere
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_NO_SECURITY, "1");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_USERNAME_TOKEN_SIGNATURE, "2");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_USERNAME_TOKEN, "3");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_USERNAME_TOKEN_NO_PASSWORD, "4");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED, "5");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_SAML_TOKEN_SIGNED, "6");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_SIGNATURE, "7");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_ENCRYPTION, "8");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_TIMESTAMP, "9");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_SIGNATURE_DERIVED, "a");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_ENCRYPTION_DERIVED, "b");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_SIGNATURE_WITH_KERBEROS_TOKEN, "c");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_ENCRYPTION_WITH_KERBEROS_TOKEN, "d");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_KERBEROS_TOKEN, "e");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_CUSTOM_TOKEN, "f");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_ENCRYPT_OLD, "g");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_DECRYPTION, "h");
		MAP_SECURITY_ACTION.put(SecurityConstants.ACTION_DECRYPT_OLD, "i");
	}
	
	public static final InfoDato TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RICHIESTA = new InfoDato(22, 
			"Indica il tipo di processamento message security della richiesta utilizzato dalla PdD");
	
	/**
	 * 	Posizione 24 (max 6 caratteri o -)
	  - tipoProcessamentoMessageSecurityRisposta
	 */
	public static final InfoDato TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RISPOSTA = new InfoDato(23, 
			"Indica il tipo di processamento message security della risposta utilizzato dalla PdD");
	
	/**
	 * 	Posizione 25 (max 1 caratteri o -)
	  - autenticazioneInCache
	 */
	public static final InfoDato AUTENTICAZIONE_IN_CACHE = new InfoDato(24, 
			"Indica se il risultato dell'autenticazione è stato prelevato dalla cache o meno");
	
	/**
	 * 	Posizione 26 (max 1 caratteri o -)
	  - autenticazioneInCache
	 */
	public static final InfoDato AUTORIZZAZIONE_IN_CACHE = new InfoDato(25, 
			"Indica se il risultato dell'autorizzazione è stato prelevato dalla cache o meno");
	
	/**
	 * 	Posizione 27 (max 1 caratteri o -)
	  - autenticazioneInCache
	 */
	
	public static final InfoDato AUTORIZZAZIONE_CONTENUTI_IN_CACHE = new InfoDato(26, 
			"Indica se il risultato dell'autorizzazione dei contenuti è stato prelevato dalla cache o meno");
	
	/**
	 * 	Posizione 28 (max 40 caratteri o -)
	  - nome della token policy
	 */
	public static final InfoDato TOKEN_POLICY = new InfoDato(27, 
			"Indica il nome della token policy");
	
	/**
	 * 	Posizione 29 (max 3 caratteri o -)
	  - tipo di operazioni sul token
	 */
	public static final char GESTIONE_TOKEN_VALIDATION_ACTION_NONE = 'N';
	public static final char GESTIONE_TOKEN_VALIDATION_ACTION_JWT = 'J';
	public static final char GESTIONE_TOKEN_VALIDATION_ACTION_INTROSPECTION = 'I';
	public static final char GESTIONE_TOKEN_VALIDATION_ACTION_USER_INFO = 'U';
	public static final InfoDato TOKEN_POLICY_ACTIONS = new InfoDato(28, 
			"Azioni effettuate sul token");
	
	/**
	 * 	Posizione 30 (max 5 caratteri o -)
	  - tipo di operazioni sul token
	 */
	public static final char AUTENTICAZIONE_TOKEN_ISSUER = 'I';
	public static final char AUTENTICAZIONE_TOKEN_SUBJECT = 'S';
	public static final char AUTENTICAZIONE_TOKEN_CLIENTID = 'C';
	public static final char AUTENTICAZIONE_TOKEN_USERNAME = 'U';
	public static final char AUTENTICAZIONE_TOKEN_EMAIL = 'E';
	public static final InfoDato TOKEN_POLICY_AUTENTCAZIONE = new InfoDato(29, 
			"Azioni di autenticazione effettuate sul token");
	
	/**
	 * 	Posizione 31 (max 1 caratteri o -)
	  - responseFromCache
	 */
	public static final InfoDato RESPONSE_FROM_CACHE = new InfoDato(30, 
			"Indica se la risposta e' stata prelevata dalla cache delle risposte");

	/**
	 * 	Posizione 32 (max 6 caratteri o -)
	  - tipoTrasformazioneRichiesta
	 */
	
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_SOAP = "S";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_REST = "R";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_METHOD = "M";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_PATH = "P";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_HEADERS = "H";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_QUERY_PARAMETERS = "Q";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_RETURN_CODE = "C";
	public static final String TIPO_TRASFORMAZIONE_NESSUNA = "N";
	
	private static final Map<String, String> MAP_TIPI_CONVERSIONE = new HashMap<>();
	public static Map<String, String> getMapTipiConversione() {
		return MAP_TIPI_CONVERSIONE;
	}
	static {
		// Deve essere un carattere (diverso da quelli sopra S,R,M,P,H,Q,C,N)
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.EMPTY.getValue(), "1");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.TEMPLATE.getValue(), "2");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.FREEMARKER_TEMPLATE.getValue(), "3");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.getValue(), "4");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.VELOCITY_TEMPLATE.getValue(), "5");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.VELOCITY_TEMPLATE_ZIP.getValue(), "6");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.XSLT.getValue(), "7");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.ZIP.getValue(), "8");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.TGZ.getValue(), "9");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.TAR.getValue(), "0");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.getValue(), "F");
		MAP_TIPI_CONVERSIONE.put(TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.getValue(), "V");
	}
	
	public static final InfoDato TIPO_TRASFORMAZIONE_RICHIESTA = new InfoDato(31, 
			"Indica il tipo di trasformazione della richiesta utilizzato dalla PdD");
	
	/**
	 * 	Posizione 33 (max 6 caratteri o -)
	  - tipoTrasformazioneRichiesta
	 */
	public static final InfoDato TIPO_TRASFORMAZIONE_RISPOSTA = new InfoDato(32, 
			"Indica il tipo di trasformazione della risposta utilizzato dalla PdD");
	
	
	/**
	 * 	Posizione 34 (max 1 caratteri o -)
	  - autenticazioneTokenInCache
	 */
	public static final InfoDato AUTENTICAZIONE_TOKEN_IN_CACHE = new InfoDato(33, 
			"Indica se il risultato dell'autenticazione token è stato prelevato dalla cache o meno");
	
	/**
	 * 	Posizione 35 (max 1 caratteri o -)
	  - dettaglioAutenticazioneFallita
	 */
	
	public static final String TIPO_AUTENTICAZIONE_FALLITA_MOTIVAZIONE_CREDENZIALI_NON_FORNITE = "N";
	public static final String TIPO_AUTENTICAZIONE_FALLITA_MOTIVAZIONE_CREDENZIALI_FORNITE_NON_CORRETE = "E";
		
	public static final InfoDato AUTENTICAZIONE_FALLITA_MOTIVAZIONE = new InfoDato(34, 
			"Indica la tipologia di errore di autenticazione");
	
	/**
	 * 	Posizione 36 (max 1 caratteri o -)
	  - modiTokenAuthorizationInCache
	 */
	public static final InfoDato MODI_TOKEN_AUTHORIZATION_IN_CACHE = new InfoDato(35, 
			"Indica se il token ModI Authorization è stato prelevato dalla cache o meno");
	
	/**
	 * 	Posizione 37 (max 1 caratteri o -)
	  - modiTokenIntegrityInCache
	 */
	public static final InfoDato MODI_TOKEN_INTEGRITY_IN_CACHE = new InfoDato(36, 
			"Indica se il token ModI Integrity è stato prelevato dalla cache o meno");
	
	/**
	 * 	Posizione 38 (max 1 caratteri o -)
	  - modiTokenAuditInCache
	 */
	public static final InfoDato MODI_TOKEN_AUDIT_IN_CACHE = new InfoDato(37, 
			"Indica se il token ModI Audit è stato prelevato dalla cache o meno");
	
	
	
	// Costanti condivise
	
	public static final String IN_CACHE_TRUE = "T";
	public static final String IN_CACHE_FALSE = "F";
	
	// Attuale somma dei caratteri:
	// (1-3)     1 +  8 + 30 + 
	// (4-6)    20 + 20 + 20 +
	// (7-9)    10 +  2 + 10 +
	// (10-12)   4 +  4 +  4 +
	// (13-15)   4 +  4 +  4 +
	// (16-18)   4 +  4 + 10 +
	// (19-20)  20 + 3 +
	// (21-23)   1 +  1 + 6 +
	// (24)      6 +
	// (25-27)   1 + 1 + 1 +
	// (28-31)  40 + 3 + 5 + 1 +
	// (32-33)   6 + 6 +
	// (34)      1 +
	// (35)      1
	// (36-38)   3
	// risultato: 269 + LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE spazi = 307
	
	public static final int LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE_PRECEDENTE_INTRODOTTO_33_34 = 33;
	public static final int LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE_PRECEDENTE_INTRODOTTO_35_36_37 = 35;
	public static final int LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE = 38; // incrementare ogni volta si aggiunge una nuova info

}
