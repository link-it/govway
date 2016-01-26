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



package org.openspcoop2.core.constants;

/**
 * CostantiDB
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class CostantiDB {
	    

    /** TABELLE*/
	
	/*ID GENERATOR*/
	public static final String TABELLA_ID_AS_LONG = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_AS_LONG;
	public static final String TABELLA_ID_RELATIVO_AS_LONG = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_RELATIVO_AS_LONG;
	public static final String TABELLA_ID_AS_STRING = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_AS_STRING;
	public static final String TABELLA_ID_RELATIVO_AS_STRING = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_RELATIVO_AS_STRING;
	
	public static final String TABELLA_ID_COLONNA_COUNTER = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_COLONNA_COUNTER;
	public static final String TABELLA_ID_COLONNA_PROTOCOLLO = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_COLONNA_PROTOCOLLO;
	public static final String TABELLA_ID_COLONNA_PROGRESSIVO = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_COLONNA_PROGRESSIVO;
	public static final String TABELLA_ID_COLONNA_INFO_ASSOCIATA = org.openspcoop2.utils.id.serial.Constants.TABELLA_ID_COLONNA_INFO_ASSOCIATA;
	
    /*COMMONS*/
    public static final String CONNETTORI = "connettori";
    public static final String CONNETTORI_CUSTOM = "connettori_custom";
    public static final String SOGGETTI = "soggetti";
    public static final String SOGGETTI_PDD = "soggetti_pdd";
    public static final String TIPI = "tipi";
    
    /*REGSERV*/
    public static final String SERVIZI = "servizi";
    public static final String SERVIZI_FRUITORI = "servizi_fruitori";
    public static final String SERVIZI_AZIONI = "servizi_azioni";
    public static final String ACCORDI = "accordi";
    public static final String ACCORDI_AZIONI = "accordi_azioni";
    public static final String PORT_TYPE = "port_type";
    public static final String PORT_TYPE_AZIONI = "port_type_azioni";
    public static final String PORT_TYPE_AZIONI_OPERATION_MESSAGES = "operation_messages";
    public static final String CONNETTORI_PROPERTIES = "connettori_properties";
    public static final String DOCUMENTI = "documenti";
    public static final String ACCORDI_SERVIZI_COMPOSTO = "acc_serv_composti";
    public static final String ACCORDI_SERVIZI_COMPONENTI = "acc_serv_componenti";
    public static final String ACCORDI_COOPERAZIONE = "accordi_cooperazione";
    public static final String ACCORDI_COOPERAZIONE_PARTECIPANTI = "accordi_coop_partecipanti";
    
    /*CONFIG*/
    public static final String SERVIZI_APPLICATIVI = "servizi_applicativi";
    public static final String PORTE_APPLICATIVE = "porte_applicative";
    public static final String PORTE_APPLICATIVE_SA = "porte_applicative_sa";
    public static final String PORTE_APPLICATIVE_PROP = "pa_properties";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST = "pa_ws_request";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE = "pa_ws_response";
    public static final String PORTE_APPLICATIVE_MTOM_REQUEST = "pa_mtom_request";
    public static final String PORTE_APPLICATIVE_MTOM_RESPONSE = "pa_mtom_response";
    public static final String PORTE_APPLICATIVE_CORRELAZIONE = "pa_correlazione";
    public static final String PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA = "pa_correlazione_risposta";
    public static final String PORTE_DELEGATE = "porte_delegate";
    public static final String PORTE_DELEGATE_SA = "porte_delegate_sa";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST = "pd_ws_request";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE = "pd_ws_response";
    public static final String PORTE_DELEGATE_MTOM_REQUEST = "pd_mtom_request";
    public static final String PORTE_DELEGATE_MTOM_RESPONSE = "pd_mtom_response";
    public static final String PORTE_DELEGATE_CORRELAZIONE = "pd_correlazione";
    public static final String PORTE_DELEGATE_CORRELAZIONE_RISPOSTA = "pd_correlazione_risposta";
    public static final String REGISTRI = "registri";
    public static final String SERVIZI_PDD = "servizi_pdd";
    public static final String SERVIZI_PDD_FILTRI = "servizi_pdd_filtri";
    public static final String SYSTEM_PROPERTIES_PDD = "pdd_sys_props";
    public static final String CONFIGURAZIONE = "configurazione";
    public static final String ROUTING = "routing";
    public static final String GESTIONE_ERRORE = "gestione_errore";
    public static final String GESTIONE_ERRORE_TRASPORTO = "gestione_errore_trasporto";
    public static final String GESTIONE_ERRORE_SOAP = "gestione_errore_soap";
    
    public static final String MSG_DIAGN_APPENDER = "msgdiag_appender";
    public final static String MSG_DIAGN_APPENDER_COLUMN_ID = "id";
    public final static String MSG_DIAGN_APPENDER_SEQUENCE = "seq_msgdiag_appender";
    public final static String MSG_DIAGN_APPENDER_TABLE_FOR_ID = "msgdiag_appender_init_seq";
    public static final String MSG_DIAGN_APPENDER_PROP = "msgdiag_appender_prop";
    
    public static final String MSG_DIAGN_DS = "msgdiag_ds";
    public final static String MSG_DIAGN_DS_COLUMN_ID = "id";
    public final static String MSG_DIAGN_DS_SEQUENCE = "seq_msgdiag_ds";
    public final static String MSG_DIAGN_DS_TABLE_FOR_ID = "msgdiag_ds_init_seq";
    public static final String MSG_DIAGN_DS_PROP = "msgdiag_ds_prop";
    
    public static final String TRACCIAMENTO_APPENDER = "tracce_appender";
    public final static String TRACCIAMENTO_APPENDER_COLUMN_ID = "id";
    public final static String TRACCIAMENTO_APPENDER_SEQUENCE = "seq_tracce_appender";
    public final static String TRACCIAMENTO_APPENDER_TABLE_FOR_ID = "tracce_appender_init_seq";
    public static final String TRACCIAMENTO_APPENDER_PROP = "tracce_appender_prop";
    
    public static final String TRACCIAMENTO_DS = "tracce_ds";
    public final static String TRACCIAMENTO_DS_COLUMN_ID = "id";
    public final static String TRACCIAMENTO_DS_SEQUENCE = "seq_tracce_ds";
    public final static String TRACCIAMENTO_DS_TABLE_FOR_ID = "tracce_ds_init_seq";
    public static final String TRACCIAMENTO_DS_PROP = "tracce_ds_prop";
    
    /*DIAGNOSTICA*/
    public final static String MSG_DIAGNOSTICI = "msgdiagnostici";
    public final static String MSG_DIAGNOSTICI_SEQUENCE = "seq_msgdiagnostici";
    public final static String MSG_DIAGNOSTICI_TABLE_FOR_ID = "msgdiagnostici_init_seq";
    
    /*DIAGNOSTICA CORRELAZIONE*/
    public final static String MSG_DIAGNOSTICI_CORRELAZIONE = "msgdiag_correlazione";
    public final static String MSG_DIAGNOSTICI_CORRELAZIONE_SEQUENCE = "seq_msgdiag_correlazione";
    public final static String MSG_DIAGNOSTICI_CORRELAZIONE_TABLE_FOR_ID = "msgdiag_correlazione_init_seq";
    public final static String MSG_DIAGNOSTICI_CORRELAZIONE_SA = "msgdiag_correlazione_sa";
    public final static String MSG_CORR_INDEX = "MSG_CORR_INDEX";
    
    /*TRACCIAMENTO*/
    public static final String TRACCE = "tracce";
    public static final String TRACCE_RISCONTRI = "tracce_riscontri";
    public static final String TRACCE_ECCEZIONI = "tracce_eccezioni";
    public static final String TRACCE_TRASMISSIONI = "tracce_trasmissioni";
    public static final String TRACCE_ALLEGATI = "tracce_allegati";
    public static final String TRACCE_EXT_INFO = "tracce_ext_protocol_info";
    public final static String TRACCE_COLUMN_ID = "id";
    public final static String TRACCE_SEQUENCE = "seq_tracce";
    public final static String TRACCE_TABLE_FOR_ID = "tracce_init_seq";
    
    /*PddConsole*/
    public static final String PDD = "pdd";
    public static final String USERS = "users";
    public static final String POLITICHE_SICUREZZA = "politiche_sicurezza";
    public static final String RUOLI	= "ruoli";
    public static final String RUOLI_SA = "ruoli_sa";
    
    /*DB INFO*/
    public static final String DB_INFO = "db_info";
    
    public static final int CREATE = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    
    public final static int TRUE = 1;
    public final static int FALSE = 0;
    
    
    /*COLONNE TABELLA TRACCE*/
    // dati generali
    public static final String TRACCE_COLUMN_GDO = "gdo";
    public static final String TRACCE_COLUMN_GDO_INT = "gdo_int";
    public static final String TRACCE_COLUMN_PDD_CODICE = "pdd_codice";
    public static final String TRACCE_COLUMN_PDD_TIPO_SOGGETTO = "pdd_tipo_soggetto";
    public static final String TRACCE_COLUMN_PDD_NOME_SOGGETTO = "pdd_nome_soggetto";
    public static final String TRACCE_COLUMN_PDD_RUOLO = "pdd_ruolo";
    public static final String TRACCE_COLUMN_TIPO_MESSAGGIO = "tipo_messaggio";
    public static final String TRACCE_COLUMN_ESITO_ELABORAZIONE = "esito_elaborazione";
    public static final String TRACCE_COLUMN_DETTAGLIO_ESITO_ELABORAZIONE = "dettaglio_esito_elaborazione";
    // dati mittente
    public static final String TRACCE_COLUMN_MITTENTE_TIPO = "tipo_mittente";
    public static final String TRACCE_COLUMN_MITTENTE_NOME = "mittente";
    public static final String TRACCE_COLUMN_MITTENTE_IDPORTA = "idporta_mittente";
    public static final String TRACCE_COLUMN_MITTENTE_INDIRIZZO = "indirizzo_mittente";
    // dati destinatario
    public static final String TRACCE_COLUMN_DESTINATARIO_TIPO = "tipo_destinatario";
    public static final String TRACCE_COLUMN_DESTINATARIO_NOME = "destinatario";
    public static final String TRACCE_COLUMN_DESTINATARIO_IDPORTA = "idporta_destinatario";
    public static final String TRACCE_COLUMN_DESTINATARIO_INDIRIZZO = "indirizzo_destinatario";
    // profilo collaborazione
    public static final String TRACCE_COLUMN_PROFILO_COLLABORAZIONE = "profilo_collaborazione";
    public static final String TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT = "profilo_collaborazione_meta";
    // servizio
    public static final String TRACCE_COLUMN_SERVIZIO_TIPO = "tipo_servizio";
    public static final String TRACCE_COLUMN_SERVIZIO_NOME = "servizio";
    public static final String TRACCE_COLUMN_SERVIZIO_VERSIONE = "versione_servizio";
    public static final String TRACCE_COLUMN_SERVIZIO_CORRELATO_TIPO = "tipo_servizio_correlato";
    public static final String TRACCE_COLUMN_SERVIZIO_CORRELATO_NOME = "servizio_correlato";
    // collaborazione
    public static final String TRACCE_COLUMN_COLLABORAZIONE = "collaborazione";
    // azione
    public static final String TRACCE_COLUMN_AZIONE = "azione";
    // identificativi
    public static final String TRACCE_COLUMN_ID_MESSAGGIO = "id_messaggio";
    public static final String TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO = "rif_messaggio";
    // ora registrazione
    public static final String TRACCE_COLUMN_ORA_REGISTRAZIONE = "ora_registrazione";
    public static final String TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO = "tipo_ora_reg";
    public static final String TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT = "tipo_ora_reg_meta";
    public static final String TRACCE_COLUMN_SCADENZA = "scadenza";
    // trasmissione
    public static final String TRACCE_COLUMN_INOLTRO = "inoltro";
    public static final String TRACCE_COLUMN_INOLTRO_SDK_CONSTANT = "inoltro_meta";
    public static final String TRACCE_COLUMN_CONFERMA_RICEZIONE = "conferma_ricezione";
    public static final String TRACCE_COLUMN_SEQUENZA = "sequenza";
    // integrazione
    public static final String TRACCE_COLUMN_LOCATION = "location";
    public static final String TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA = "correlazione_applicativa";
    public static final String TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA = "correlazione_risposta";
    public static final String TRACCE_COLUMN_SA_FRUITORE = "sa_fruitore";
    public static final String TRACCE_COLUMN_SA_EROGATORE = "sa_erogatore";
    // protocollo
    public static final String TRACCE_COLUMN_PROTOCOLLO = "protocollo";
    // isArrivedi
    public static final String TRACCE_COLUMN_IS_ARRIVED = "is_arrived";
    // SOAP
    public static final String TRACCE_COLUMN_SOAP = "soap_element";
    public static final String TRACCE_COLUMN_DIGEST = "digest";

    /*COLONNE TABELLA TRACCE RISCONTRI*/
    public static final String TRACCE_RISCONTRI_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO = "riscontro";
    public static final String TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE = "ora_registrazione";
    public static final String TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO = "tipo_ora_reg";
    public static final String TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT = "tipo_ora_reg_meta";
    
    /*COLONNE TABELLA TRACCE TRASMISSIONI */
    public static final String TRACCE_TRASMISSIONI_COLUMN_ID_TRACCIA = "idtraccia";
    // origine
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORIGINE = "origine";
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORIGINE_TIPO = "tipo_origine";
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORIGINE_INDIRIZZO = "indirizzo_origine";
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORIGINE_IDPORTA = "idporta_origine";
    // destinazione
    public static final String TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE = "destinazione";
    public static final String TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_TIPO = "tipo_destinazione";
    public static final String TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_INDIRIZZO = "indirizzo_destinazione";
    public static final String TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_IDPORTA = "idporta_destinazione";
    // ora registrazione
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE = "ora_registrazione";
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO = "tipo_ora_reg";
    public static final String TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT = "tipo_ora_reg_meta";

    /*COLONNE TABELLA TRACCE ECCEZIONI */
    public static final String TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA = "contesto_codifica";
    public static final String TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA_SDK_CONSTANT = "contesto_codifica_meta";
    public static final String TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE = "codice_eccezione";
    public static final String TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SDK_CONSTANT = "codice_eccezione_meta";
    public static final String TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SUBCOD_SDK_CONSTANT = "subcodice_eccezione_meta";
    public static final String TRACCE_ECCEZIONI_COLUMN_RILEVANZA = "rilevanza";
    public static final String TRACCE_ECCEZIONI_COLUMN_RILEVANZA_SDK_CONSTANT = "rilevanza_meta";
    public static final String TRACCE_ECCEZIONI_COLUMN_POSIZIONE = "posizione";
    public static final String TRACCE_ECCEZIONI_COLUMN_DESCRIZIONE = "descrizione";
    
    /*COLONNE TABELLA TRACCE ALLEGATI */
    public static final String TRACCE_ALLEGATI_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_ALLEGATI_COLUMN_CONTENT_ID = "content_id";
    public static final String TRACCE_ALLEGATI_COLUMN_CONTENT_LOCATION = "content_location";
    public static final String TRACCE_ALLEGATI_COLUMN_CONTENT_TYPE = "content_type";
    public static final String TRACCE_ALLEGATI_COLUMN_DIGEST = "digest";
    
    /*COLONNE TABELLA TRACCE EXT PROTOCOL INFO */
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME = "name";
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE = "value";


    /*COLONNE TABELLA MSGDIAGNOSTICI */
    public static final String MSG_DIAGNOSTICI_COLUMN_GDO = "gdo";
    public static final String MSG_DIAGNOSTICI_COLUMN_PDD_CODICE = "pdd_codice";
    public static final String MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO = "pdd_tipo_soggetto";
    public static final String MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO = "pdd_nome_soggetto";
    public static final String MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE = "idfunzione";
    public static final String MSG_DIAGNOSTICI_COLUMN_SEVERITA = "severita";
    public static final String MSG_DIAGNOSTICI_COLUMN_MESSAGGIO = "messaggio";
    public static final String MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO = "idmessaggio";
    public static final String MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA = "idmessaggio_risposta";
    public static final String MSG_DIAGNOSTICI_COLUMN_CODICE = "codice";
    public static final String MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO = "protocollo";
    public static final String MSG_DIAGNOSTICI_COLUMN_ID = "id";
    
    /*COLONNE TABELLA MSG_DIAGNOSTICI_CORRELAZIONE */
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_IDMESSAGGIO = "idmessaggio";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_PDD_CODICE = "pdd_codice";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_PDD_TIPO_SOGGETTO = "pdd_tipo_soggetto";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_PDD_NOME_SOGGETTO = "pdd_nome_soggetto";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_GDO = "gdo";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_PORTA = "porta";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_DELEGATA = "delegata";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_TIPO_FRUITORE = "tipo_fruitore";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_FRUITORE = "fruitore";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_TIPO_EROGATORE = "tipo_erogatore";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_EROGATORE = "erogatore";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_TIPO_SERVIZIO = "tipo_servizio";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_SERVIZIO = "servizio";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_VERSIONE_SERVIZIO = "versione_servizio";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_AZIONE = "azione";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID_CORRELAZIONE_APPLICATIVA_RICHIESTA = "id_correlazione_applicativa";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID_CORRELAZIONE_APPLICATIVA_RISPOSTA = "id_correlazione_risposta";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_PROTOCOLLO = "protocollo";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID = "id";

    /*COLONNE TABELLA MSG_DIAGNOSTICI_CORRELAZIONE_SA */
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_SA_COLUMN_ID_CORRELAZIONE = "id_correlazione";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_SA_COLUMN_SERVIZIO_APPLICATIVO = "servizio_applicativo";

    
    /**
     * PROPRIETA CONNETTORE
     */

    //tipi connettori
    public static final String CONNETTORE_TIPO_HTTP = TipiConnettore.HTTP.getNome();
    public static final String CONNETTORE_TIPO_HTTPS = TipiConnettore.HTTPS.getNome();
    public static final String CONNETTORE_TIPO_JMS = TipiConnettore.JMS.getNome();
    public static final String CONNETTORE_TIPO_DISABILITATO = TipiConnettore.DISABILITATO.getNome();
    
    public static final String CONNETTORE_DEBUG = CostantiConnettori.CONNETTORE_DEBUG;

    public static final String CONNETTORE_HTTP_LOCATION = CostantiConnettori.CONNETTORE_LOCATION;

    public static final String CONNETTORE_JMS_NOME = CostantiConnettori.CONNETTORE_LOCATION; //il nome del connettore sarebbe la proprieta location
    public static final String CONNETTORE_JMS_TIPO = CostantiConnettori.CONNETTORE_JMS_TIPO;
    public static final String CONNETTORE_USER = CostantiConnettori.CONNETTORE_USERNAME;
    public static final String CONNETTORE_PWD = CostantiConnettori.CONNETTORE_PASSWORD;
    public static final String CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL=CostantiConnettori.CONNETTORE_JMS_CONTEXT_PREFIX+"java.naming.factory.initial";
    public static final String CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG=CostantiConnettori.CONNETTORE_JMS_CONTEXT_PREFIX+"java.naming.factory.url.pkgs";
    public static final String CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL=CostantiConnettori.CONNETTORE_JMS_CONTEXT_PREFIX+"java.naming.provider.url";
    public static final String CONNETTORE_JMS_CONNECTION_FACTORY=CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY;
    public static final String CONNETTORE_JMS_SEND_AS=CostantiConnettori.CONNETTORE_JMS_SEND_AS;
    
    public static final String CONNETTORE_HTTPS_LOCATION = CostantiConnettori.CONNETTORE_LOCATION;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_LOCATION = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD;
    public static final String CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_TYPE = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE;
    public static final String CONNETTORE_HTTPS_KEY_STORE_LOCATION = CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION;
    public static final String CONNETTORE_HTTPS_KEY_STORE_PASSWORD = CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD;
    public static final String CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM;
    public static final String CONNETTORE_HTTPS_KEY_STORE_TYPE = CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE;
    public static final String CONNETTORE_HTTPS_KEY_PASSWORD = CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD;
    public static final String CONNETTORE_HTTPS_HOSTNAME_VERIFIER = CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER;
    public static final String CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER = CostantiConnettori.CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER;
    public static final String CONNETTORE_HTTPS_SSL_TYPE = CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE;
	
    public static final String COMPONENTE_SERVIZIO_PD = "pd";
    public static final String COMPONENTE_SERVIZIO_PA = "pa";
    public static final String COMPONENTE_SERVIZIO_IM = "im";
    
    public static final String TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD = "abilitazione";
    public static final String TIPO_FILTRO_DISABILITAZIONE_SERVIZIO_PDD = "disabilitazione";
    
}