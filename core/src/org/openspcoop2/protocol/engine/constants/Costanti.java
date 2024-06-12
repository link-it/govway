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



package org.openspcoop2.protocol.engine.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;

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
	public static final String DEFAULT_PROTOCOL = "basic";
	

	
	
    /* ********  F I E L D S    S T A T I C    P U B L I C  ******** */
	
 
    
    /** Tabella per il salvataggio delle buste inviate/ricevute */
    public static final String REPOSITORY = "REPOSITORY_BUSTE";
    public static final String REPOSITORY_INDEX_SCADENZA_SEARCH = "REP_BUSTE_SEARCH";
    public static final String REPOSITORY_INDEX_TIPO_SEARCH = "REP_BUSTE_SEARCH_TIPO";
	/* Colonne per Repository Buste */
    public static final String REPOSITORY_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
    public static final String REPOSITORY_COLUMN_TIPO_MESSAGGIO = "TIPO";
    public static final String REPOSITORY_COLUMN_DATA_REGISTRAZIONE = "DATA_REGISTRAZIONE";
    public static final String REPOSITORY_COLUMN_SCADENZA = "SCADENZA_BUSTA";
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
    public static final long GESTIONE_SERIALIZABLE_ATTESA_ATTIVA = 60l * 1000l; // 1 minuto
    /** Intervallo maggiore per frequenza di check nell'attesa attiva effettuata per la gestione del livello serializable nel DB, in millisecondi */
    public static final int GESTIONE_SERIALIZABLE_CHECK_INTERVAL = 100; 
	
    /** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO = org.openspcoop2.core.constants.Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO = org.openspcoop2.core.constants.Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO = org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO;
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA = org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA;
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO = org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO;
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_ASIMMETRICO_POLLING = org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING;
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI = org.openspcoop2.core.constants.Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI;

    
    public static final String REQUISITI_INPUT_RACCOLTI = "[[InformationMissingRequisiti]]";
    
    public static final String TRASPARENTE_PROTOCOL_NAME = CostantiLabel.TRASPARENTE_PROTOCOL_NAME;
    public static final String TRASPARENTE_PROTOCOL_LABEL = CostantiLabel.TRASPARENTE_PROTOCOL_LABEL;
    
    public static final String SPCOOP_PROTOCOL_NAME = CostantiLabel.SPCOOP_PROTOCOL_NAME;
    public static final String SPCOOP_PROTOCOL_LABEL = CostantiLabel.SPCOOP_PROTOCOL_LABEL;
    
    public static final String MODIPA_PROTOCOL_NAME = CostantiLabel.MODIPA_PROTOCOL_NAME;
    public static final String MODIPA_PROTOCOL_LABEL = CostantiLabel.MODIPA_PROTOCOL_LABEL;
    private static List<String> tipiSoggettoModI = new ArrayList<>();
	static {
    	tipiSoggettoModI.add(CostantiLabel.MODIPA_PROTOCOL_NAME); // tipo soggetto uguale al protocollo
    }
	public static List<String> getTipiSoggettoModI() {
		return tipiSoggettoModI;
	}
    
    public static final String SDI_PROTOCOL_NAME = CostantiLabel.SDI_PROTOCOL_NAME;
    public static final String SDI_PROTOCOL_LABEL = CostantiLabel.SDI_PROTOCOL_LABEL;
    
    public static final String AS4_PROTOCOL_NAME = CostantiLabel.AS4_PROTOCOL_NAME;
    public static final String AS4_PROTOCOL_LABEL = CostantiLabel.AS4_PROTOCOL_LABEL;
    
    public static final String MODIPA_VALUE_UNDEFINED = CostantiDB.MODIPA_VALUE_UNDEFINED;
    
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA = CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA = CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA = CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA;
    
    
	public static final String CONSOLE_PARAMETRO_SOGGETTO_DOMINIO = "dominio";
	
	public static final String CONSOLE_PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA = "tipoSA";
	
	public static final String CONSOLE_PARAMETRO_PP_CHANGE_BINARY = "changeBinary";
	public static final String CONSOLE_PARAMETRO_PP_CHANGE_BINARY_VALUE_TRUE = "true";
	
	public static final String CONSOLE_PARAMETRO_SERVICE_BINDING = "serviceBinding";
	
	public static final String CONSOLE_PARAMETRO_APC_API_GESTIONE_PARZIALE = "apiGestioneParziale";
	public static final String CONSOLE_VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI = "apiInfoGenerali";
	
	public static final String CONSOLE_PARAMETRO_APC_NOME = "nome";
	public static final String CONSOLE_PARAMETRO_APC_VERSIONE = "versione";
	
	public static final String CONSOLE_PARAMETRO_APS_PORT_TYPE = "port_type";
	
	public static final String CONSOLE_ATTRIBUTO_TAB_SESSION_KEY_PREFIX ="_tabKey_";
	
	public static final String CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VIA_PARAM = Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE+"_VIA_PARAM";
	public static final String CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE = CONSOLE_ATTRIBUTO_TAB_SESSION_KEY_PREFIX + "tipologiaErogazione";
	public static final String CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE = "erogazione";
	public static final String CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE = "fruizione";
	public static final String CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_COMPLETA = "completa";
	
	public static final String CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO = "connettore_token_policy_stat";
	public static final String CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY = "connettore_token_policy";
	public static final String CONSOLE_PARAMETRO_CONNETTORE_TOKEN_POLICY_VIA_API = "connettore_token_policy_via_api";
	
	public static final String CONSOLE_PARAMETRO_EROGAZIONE_TOKEN_POLICY = "gtPolicy";
	
	public static final String CONSOLE_DEFAULT_VALUE_NON_SELEZIONATO = "-";
}





