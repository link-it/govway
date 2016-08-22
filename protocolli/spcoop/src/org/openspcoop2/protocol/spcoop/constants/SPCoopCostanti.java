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



package org.openspcoop2.protocol.spcoop.constants;


/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica eGov, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SPCoopCostanti {
   
	public static final String SERVIZIO_SPC = "SPC";

    public final static String OPENSPCOOP2_LOCAL_HOME = "OPENSPCOOP2_HOME";
	
    public final static String SPCOOP_PROPERTIES_LOCAL_PATH = "spcoop_local.properties";
    public final static String SPCOOP_PROPERTIES = "SPCOOP_PROPERTIES";
	
    /** Profilo di gestione della Busta eGov: busta eGov v1.1 */
    public static final String PROFILO_CNIPA_BUSTA_EGOV_11 = "eGov1.1";
    /** Profilo di gestione della Busta eGov: linee guida v1.1 */
    public static final String PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11 = "eGov1.1-lineeGuida1.1";
    
    /** Definisce l'actor utilizzato nelle buste eGov */
    public static final String ACTOR_EGOV = "http://www.cnipa.it/eGov_it/portadominio";
    /** Definisce il namespace utilizzato nelle buste eGov */
    public static final String NAMESPACE_EGOV = "http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/";
    /** Definisce il prefix utilizzato nelle buste eGov */
    public static final String PREFIX_EGOV = "eGov_IT";
    /** Definisce il local name utilizzato nelle buste eGov */
    public static final String LOCAL_NAME_EGOV = "Intestazione";

    /** Definisce il namespace utilizzato per buste di eccezione */
    public static final String NAMESPACE_ECCEZIONE_APPLICATIVA_EGOV = "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/";
    
    /** Definisce il local name del manifest eGov */
    public static final String LOCAL_NAME_MANIFEST_EGOV_DESCRIZIONE_MESSAGGIO = "DescrizioneMessaggio";
    
    /** Definisce il local name dell'attachments che contenenva un body vuoto */
    public static final String LOCAL_NAME_MANIFEST_EGOV_EMPTY_BODY = "SoapBodyEmpty";
    /** Definisce il namespace utilizzato nelle buste eGov, per indicare un soapBody vuoto indirizzato dal manifest eGov */
    public static final String NAMESPACE_MANIFEST_EGOV_EMPTY_BODY = "http://www.openspcoop2.org/spcoop/manifest/body";
    public static final String XML_MANIFEST_EGOV_EMPTY_BODY = "<"+PREFIX_EGOV+":"+LOCAL_NAME_MANIFEST_EGOV_EMPTY_BODY+" xmlns:"+PREFIX_EGOV+"=\""+NAMESPACE_MANIFEST_EGOV_EMPTY_BODY+"\" />";
    
    /** Massimo numero assumibile dal numero seriale utilizzato in un Identificativo eGov. */
    public static final int MAX_VALUE_ID_EGOV_COUNTER = 9999999;
    /** Massimo numero assumibile dal numero seriale utilizzato in un Identificativo eGov, se il prefisso e' di lunghezza 1. */
    public static final int MAX_VALUE_ID_EGOV_COUNTER_PREFIX_1 = 999999;
    /** Massimo numero assumibile dal numero seriale utilizzato in un Identificativo eGov, se il prefisso e' di lunghezza 2. */
    public static final int MAX_VALUE_ID_EGOV_COUNTER_PREFIX_2 = 99999;
    public static final int CIFRE_SERIALI_ID_EGOV = 7;
   
    /** Tipo di identificativo egov: default */
    public final static String IDENTIFICATIVO_EGOV_SERIALE_DB = "db";
    /** Tipo di identificativo egov: mysql */
    public final static String IDENTIFICATIVO_EGOV_SERIALE_MYSQL = "mysql";
    /** Tipo di identificativo egov: static */
    public final static String IDENTIFICATIVO_EGOV_SERIALE_STATIC = "static";
    
    /** Attesa attiva di default effettuata per la gestione del livello serializable nel DB, in millisecondi */
    public final static long GESTIONE_SERIALIZABLE_ATTESA_ATTIVA = 60 * 1000; // 1 minut
    /** Intervallo maggiore per frequenza di check nell'attesa attiva effettuata per la gestione del livello serializable nel DB, in millisecondi */
    public final static int GESTIONE_SERIALIZABLE_CHECK_INTERVAL = 100; 

    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_INTEGRAZIONE = "EccezioneIntegrazione";
    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_VALIDAZIONE_BUSTA_SPCOOP = "EccezioneBusta";
    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_PROCESSAMENTO_SPCOOP = "EccezioneProcessamento";
    
    /** Definisce un tipo di FaultCode (errore nell'intestazione eGov) */
    public static final String FAULT_CODE_VALIDAZIONE_SPCOOP = "Client";
    /** Definisce un tipo di FaultCode (errore nel processamento) */
    public static final String FAULT_CODE_PROCESSAMENTO_SPCOOP = "Server";
    	
    /** Definisce un tipo di FaultString (errore nel processamento) */
    public static final String FAULT_STRING_PROCESSAMENTO_SPCOOP =  "EGOV_IT_300 - Errore nel processamento del messaggio SPCoop";
    public static final String FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE =  "Errore nel processamento del messaggio SPCoop";
    /** Definisce un tipo di FaultString (errore nell'intestazione eGov) */
    public static final String FAULT_STRING_VALIDAZIONE_SPCOOP =  "EGOV_IT_001 - Formato Busta non corretto";

    /** Definisce un tipo di FaultString (errore nel processamento) */
    public static final String FAULT_STRING_PROCESSAMENTO =  "EGOV_IT_300 - Errore nel processamento del messaggio SPCoop";
    public static final String FAULT_STRING_PROCESSAMENTO_SENZA_CODICE =  "Errore nel processamento del messaggio SPCoop";
    /** Definisce un tipo di FaultString (errore nell'intestazione eGov) */
    public static final String FAULT_STRING_VALIDAZIONE =  "EGOV_IT_001 - Formato Busta non corretto";
    																
    /** Profilo di Collaborazione : MessaggioSingolo OneWay. */
    public static final String PROFILO_COLLABORAZIONE_ONEWAY = "EGOV_IT_MessaggioSingoloOneWay";
    /** Profilo di Collaborazione : Servizio Sincrono. */
    public static final String PROFILO_COLLABORAZIONE_SINCRONO = "EGOV_IT_ServizioSincrono";
    /** Profilo di Collaborazione : Servizio Asincrono Simmetrico. */
    public static final String PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = "EGOV_IT_ServizioAsincronoSimmetrico";
    /** Profilo di Collaborazione : Servizio Asincrono Asimmetrico. */
    public static final String PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = "EGOV_IT_ServizioAsincronoAsimmetrico";
    public static final String PROFILO_COLLABORAZIONE_SCONOSCIUTO = "SCONOSCIUTO";
    
    
    /** Profilo di Trasmissione, attributo inoltro : Al piu' una volta. */
    public static final String PROFILO_TRASMISSIONE_SENZA_DUPLICATI = "EGOV_IT_ALPIUUNAVOLTA";
    /** Profilo di Trasmissione, attributo inoltro : Piu' una volta. */
    public static final String PROFILO_TRASMISSIONE_CON_DUPLICATI = "EGOV_IT_PIUDIUNAVOLTA";

    public static final String PROFILO_TRASMISSIONE_SCONOSCIUTO = "SCONOSCIUTO";
	/** Ora di creazione di un tracciamento. Il token 'locale' indica il tempo locale
    non sincronizzato da sistema che lo imposta*/
   public static final String TIPO_TEMPO_LOCALE = "EGOV_IT_Locale";
   /** Ora di creazione di un tracciamento. Il token 'spc' indica il tempo sincronizzato di rete*/
   public static final String TIPO_TEMPO_SPC = "EGOV_IT_SPC";
    
   public static final String TIPO_TEMPO_SCONOSCIUTO = "SCONOSCIUTO";
   
   public static final String ECCEZIONE_RILEVANZA_LIEVE = "LIEVE";
   public static final String ECCEZIONE_RILEVANZA_INFO = "INFO";
   public static final String ECCEZIONE_RILEVANZA_GRAVE = "GRAVE";
   public static final String ECCEZIONE_RILEVANZA_SCONOSCIUTO = "SCONOSCIUTO";
   
   /** ContestoCodifica di un Messaggio SPCoop Errore di Validazione. */    
   public static final String CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE = "ErroreIntestazioneMessaggioSPCoop";
   /** ContestoCodifica di un Messaggio SPCoop Errore di Validazione. */    
   public static final String CONTESTO_CODIFICA_ECCEZIONE_PROCESSAMENTO = "ErroreProcessamentoMessaggioSPCoop";
   
   /** Definisce una richiesta nel Manifest */
   public static final String ATTACHMENTS_MANIFEST_RICHIESTA = "Richiesta";
   /** Definisce una risposta da registrare in un Manifest */
   public static final String ATTACHMENTS_MANIFEST_RISPOSTA = "Risposta";
   /** Definisce un allegato da registrare in un Manifest */
   public static final String ATTACHMENTS_MANIFEST_ALLEGATO = "Allegato";
   public static final String MANIFEST_KEY_ROLE_RICHIESTA = "@RICHIESTA@";
   public static final String MANIFEST_KEY_ROLE_RISPOSTA = "@RISPOSTA@";
}





