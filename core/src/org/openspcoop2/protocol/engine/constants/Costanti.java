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



package org.openspcoop2.protocol.engine.constants;


/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica, 
 * utilizzate dalle classi del package org.openspcoop.engine .
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Costanti {
   
	/* ******** Costanti pubbliche PdD ************* */
	
	/** default protocol name */
	public final static String DEFAULT_PROTOCOL = "basic";
	

	
	
    /* ********  F I E L D S    S T A T I C    P U B L I C  ******** */
	
 
    
    /** Tabella per il salvataggio delle buste inviate/ricevute */
    public static final String REPOSITORY = "REPOSITORY_BUSTE";
    public static final String REPOSITORY_INDEX_SCADENZA_SEARCH = "REP_BUSTE_SEARCH";
    public static final String REPOSITORY_INDEX_TIPO_SEARCH = "REP_BUSTE_SEARCH_TIPO";
	/* Colonne per Repository Buste */
    public static final String REPOSITORY_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String REPOSITORY_COLUMN_TIPO_MESSAGGIO = "TIPO";
    public static final String REPOSITORY_COLUMN_PROTOCOLLO = "PROTOCOLLO";
    public static final String REPOSITORY_COLUMN_RIFERIMENTO_MESSAGGIO = "RIFERIMENTO_MESSAGGIO";
    public static final String REPOSITORY_COLUMN_DUPLICATI = "DUPLICATI";
    public static final String REPOSITORY_COLUMN_USE_HISTORY = "HISTORY";
    public static final String REPOSITORY_COLUMN_USE_PROFILO = "PROFILO";
    public static final String REPOSITORY_COLUMN_USE_PDD = "PDD";
    public static final String REPOSITORY_COLUMN_USE_REPOSITORY_ACCESS = "REPOSITORY_ACCESS";
    
    /** Tabella per il salvataggio delle buste inviate/ricevute: Eccezioni */
    public static final String  LISTA_ECCEZIONI = "LISTA_ECCEZIONI";
	/* Colonne per Lista Eccezioni */
    public static final String LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO = "TIPO";
    
    /** Tabella per il salvataggio delle buste inviate/ricevute: Trasmissioni */
    public static final String  LISTA_TRASMISSIONI = "LISTA_TRASMISSIONI";
    /* Colonne per Lista Trasmissioni */
    public static final String LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO = "TIPO";
    
    /** Tabella per il salvataggio delle buste inviate/ricevute: Riscontri */
    public static final String  LISTA_RISCONTRI = "LISTA_RISCONTRI";
    /* Colonne per Lista Riscontri */
    public static final String LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO = "TIPO";
    
    /** Tabella per il salvataggio delle informazioni extra */
    public static final String  LISTA_EXT_INFO = "LISTA_EXT_PROTOCOL_INFO";
    /* Colonne per Lista ExtInfo */
    public static final String LISTA_EXT_INFO_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String LISTA_EXT_INFO_COLUMN_TIPO_MESSAGGIO = "TIPO";
    
    /** Tabella per la generazione del numero seriale utilizzato in */
    public static final String ID_COUNTER  = "ID_BUSTA";
   
    /** Tabella per il profilo asincrono */
    public static final String PROFILO_ASINCRONO = "ASINCRONO";
	/* Colonne per ProfiloAsincrono */
    public static final String PROFILO_ASINCRONO_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO = "TIPO";
    public static final String PROFILO_ASINCRONO_COLUMN_RICEVUTA_ASINCRONA = "RICEVUTA_ASINCRONA";
    public static final String PROFILO_ASINCRONO_COLUMN_RICEVUTA_APPLICATIVA = "RICEVUTA_APPLICATIVA";
    
    /** Tabella per la fase di ricezione riscontro */
    public static final String RISCONTRI_DA_RICEVERE  = "RISCONTRI_DA_RICEVERE";
    /* Colonne per Riscontri */
    public static final String RISCONTRI_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    
    /** Tabella per la fase di invio riscontro */
    public static final String RISCONTRI_DA_INVIARE  = "RISCONTRI_DA_INVIARE";
    
    /** Tabella per la fase di ricezione in ordine */
    public static final String SEQUENZA_DA_RICEVERE  = "SEQUENZA_DA_RICEVERE";
    /* Colonne per Sequenza da Ricevere */
    public static final String SEQUENZA_DA_RICEVERE_COLUMN_ID_COLLABORAZIONE = "ID_COLLABORAZIONE";
    
    /** Tabella per la fase di invio in ordine */
    public static final String SEQUENZA_DA_INVIARE  = "SEQUENZA_DA_INVIARE";
    /* Colonne per Sequenza da Inviare */
    public static final String SEQUENZA_DA_INVIARE_COLUMN_ID_COLLABORAZIONE = "ID_COLLABORAZIONE";
    
    /** Massimo numero assumibile dal numero seriale utilizzato in una sequenza. */
    public static final int MAX_VALUE_SEQUENZA_COUNTER = 9999999;
    
    /** Cartella INBOX, contenente messaggi ricevuti */
	public static final String INBOX = "INBOX";
	/** Cartella OUTBOX, contenente messaggi inviati */
	public static final String OUTBOX = "OUTBOX";
	    
	
    /** Attesa attiva di default effettuata per la gestione del livello serializable nel DB, in millisecondi */
    public final static long GESTIONE_SERIALIZABLE_ATTESA_ATTIVA = 60 * 1000; // 1 minut0
    /** Intervallo maggiore per frequenza di check nell'attesa attiva effettuata per la gestione del livello serializable nel DB, in millisecondi */
    public final static int GESTIONE_SERIALIZABLE_CHECK_INTERVAL = 100; 
	
    /** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO = "OneWay_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO = "Sincrono_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO = "AsincronoSimmetrico_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA = "AsincronoSimmetrico_ConsegnaRisposta";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO = "AsincronoAsimmetrico_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_ASIMMETRICO_POLLING = "AsincronoAsimmetrico_Polling";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI = "ConsegnaContenutiApplicativi";

    
    
    
}





