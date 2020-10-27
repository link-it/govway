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
    public static final String PROTOCOL_PROPERTIES = "protocol_properties";
    public static final String PDD = "pdd";
    public static final String GRUPPI	= "gruppi";
    public static final String RUOLI	= "ruoli";
    public static final String SCOPE	= "scope";
    public static final String SOGGETTI_RUOLI = "soggetti_ruoli";
    public static final String SERVIZI = "servizi";
    public static final String SERVIZI_AZIONI = "servizi_azioni";
    public static final String SERVIZI_AZIONE = "servizi_azione";
    public static final String SERVIZI_FRUITORI = "servizi_fruitori";
    public static final String SERVIZI_FRUITORI_AZIONI = "servizi_fruitori_azioni";
    public static final String SERVIZI_FRUITORI_AZIONE = "servizi_fruitori_azione";
    public static final String ACCORDI = "accordi";
    public static final String ACCORDI_AZIONI = "accordi_azioni";
    public static final String PORT_TYPE = "port_type";
    public static final String PORT_TYPE_AZIONI = "port_type_azioni";
    public static final String PORT_TYPE_AZIONI_OPERATION_MESSAGES = "operation_messages";
    public static final String API_RESOURCES = "api_resources";
    public static final String API_RESOURCES_RESPONSE = "api_resources_response";
    public static final String API_RESOURCES_MEDIA = "api_resources_media";
    public static final String API_RESOURCES_PARAMETER = "api_resources_parameter";
    public static final String ACCORDI_GRUPPI = "accordi_gruppi";
    public static final String CONNETTORI_PROPERTIES = "connettori_properties";
    public static final String DOCUMENTI = "documenti";
    public static final String ACCORDI_SERVIZI_COMPOSTO = "acc_serv_composti";
    public static final String ACCORDI_SERVIZI_COMPONENTI = "acc_serv_componenti";
    public static final String ACCORDI_COOPERAZIONE = "accordi_cooperazione";
    public static final String ACCORDI_COOPERAZIONE_PARTECIPANTI = "accordi_coop_partecipanti";
    
    /*CONFIG*/
    public static final String SERVIZI_APPLICATIVI = "servizi_applicativi";
    public static final String SERVIZI_APPLICATIVI_RUOLI = "sa_ruoli";
    public static final String PORTE_APPLICATIVE = "porte_applicative";
    public static final String PORTE_APPLICATIVE_SA = "porte_applicative_sa";
    public static final String PORTE_APPLICATIVE_SA_PROPS = "pa_sa_properties";
    public static final String PORTE_APPLICATIVE_BEHAVIOUR_PROPS = "pa_behaviour_props";
    public static final String PORTE_APPLICATIVE_AUTENTICAZIONE_PROP = "pa_auth_properties";
    public static final String PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP = "pa_authz_properties";
    public static final String PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP = "pa_authzc_properties";
    public static final String PORTE_APPLICATIVE_PROP = "pa_properties";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST = "pa_security_request";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE = "pa_security_response";
    public static final String PORTE_APPLICATIVE_MTOM_REQUEST = "pa_mtom_request";
    public static final String PORTE_APPLICATIVE_MTOM_RESPONSE = "pa_mtom_response";
    public static final String PORTE_APPLICATIVE_CORRELAZIONE = "pa_correlazione";
    public static final String PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA = "pa_correlazione_risposta";
    public static final String PORTE_APPLICATIVE_RUOLI = "pa_ruoli";
    public static final String PORTE_APPLICATIVE_SCOPE = "pa_scope";
    public static final String PORTE_APPLICATIVE_SOGGETTI = "pa_soggetti";
    public static final String PORTE_APPLICATIVE_SA_AUTORIZZATI = "porte_applicative_sa_auth";
    public static final String PORTE_APPLICATIVE_AZIONI = "pa_azioni";
    public static final String PORTE_APPLICATIVE_CACHE_REGOLE = "pa_cache_regole";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI = "pa_transform";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_COLUMN_ID = "id";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_SEQUENCE = "seq_pa_transform";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_TABLE_FOR_ID = "pa_transform_init_seq";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI = "pa_transform_soggetti";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_SA = "pa_transform_sa";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER = "pa_transform_hdr";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_URL = "pa_transform_url";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE = "pa_transform_risp";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_COLUMN_ID = "id";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_SEQUENCE = "seq_pa_transform_risp";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_TABLE_FOR_ID = "pa_transform_risp_init_seq";
    public static final String PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER = "pa_transform_risp_hdr";
    public static final String PORTE_DELEGATE = "porte_delegate";
    public static final String PORTE_DELEGATE_SA = "porte_delegate_sa";
    public static final String PORTE_DELEGATE_AUTENTICAZIONE_PROP = "pd_auth_properties";
    public static final String PORTE_DELEGATE_AUTORIZZAZIONE_PROP = "pd_authz_properties";
    public static final String PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP = "pd_authzc_properties";
    public static final String PORTE_DELEGATE_PROP = "pd_properties";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST = "pd_security_request";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE = "pd_security_response";
    public static final String PORTE_DELEGATE_MTOM_REQUEST = "pd_mtom_request";
    public static final String PORTE_DELEGATE_MTOM_RESPONSE = "pd_mtom_response";
    public static final String PORTE_DELEGATE_CORRELAZIONE = "pd_correlazione";
    public static final String PORTE_DELEGATE_CORRELAZIONE_RISPOSTA = "pd_correlazione_risposta";
    public static final String PORTE_DELEGATE_RUOLI = "pd_ruoli";
    public static final String PORTE_DELEGATE_SCOPE = "pd_scope";
    public static final String PORTE_DELEGATE_AZIONI = "pd_azioni";
    public static final String PORTE_DELEGATE_CACHE_REGOLE = "pd_cache_regole";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI = "pd_transform";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_COLUMN_ID = "id";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_SEQUENCE = "seq_pd_transform";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_TABLE_FOR_ID = "pd_transform_init_seq";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_SA = "pd_transform_sa";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_HEADER = "pd_transform_hdr";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_URL = "pd_transform_url";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE = "pd_transform_risp";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_COLUMN_ID = "id";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_SEQUENCE = "seq_pd_transform_risp";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_TABLE_FOR_ID = "pd_transform_risp_init_seq";
    public static final String PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER = "pd_transform_risp_hdr";
    public static final String REGISTRI = "registri";
    public static final String SERVIZI_PDD = "servizi_pdd";
    public static final String SERVIZI_PDD_FILTRI = "servizi_pdd_filtri";
    public static final String SYSTEM_PROPERTIES_PDD = "pdd_sys_props";
    public static final String CONFIGURAZIONE = "configurazione";
    public static final String CONFIGURAZIONE_CACHE_REGOLE = "config_cache_regole";
    public static final String ROUTING = "routing";
    public static final String GESTIONE_ERRORE = "gestione_errore";
    public static final String GESTIONE_ERRORE_TRASPORTO = "gestione_errore_trasporto";
    public static final String GESTIONE_ERRORE_SOAP = "gestione_errore_soap";
    public static final String CONFIGURAZIONE_CANALI = "canali_configurazione";
    public static final String CONFIGURAZIONE_CANALI_NODI = "canali_nodi";

    
    public static final String CONFIG_URL_INVOCAZIONE = "config_url_invocazione";
    public static final String CONFIG_URL_REGOLE = "config_url_regole";
    
    public static final String CONFIG_GENERIC_PROPERTIES = "generic_properties";
    public static final String CONFIG_GENERIC_PROPERTIES_COLUMN_ID = "id";
    public static final String CONFIG_GENERIC_PROPERTIES_SEQUENCE = "seq_generic_properties";
    public static final String CONFIG_GENERIC_PROPERTIES_TABLE_FOR_ID = "generic_properties_init_seq";
    public static final String CONFIG_GENERIC_PROPERTY = "generic_property";
    
    public static final String CONTROLLO_TRAFFICO_CONFIG = "ct_config";
    public static final String CONTROLLO_TRAFFICO_CONFIG_POLICY = "ct_config_policy";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY = "ct_active_policy";
    
    public static final String MSG_DIAGN_APPENDER = "msgdiag_appender";
    public static final String MSG_DIAGN_APPENDER_COLUMN_ID = "id";
    public static final String MSG_DIAGN_APPENDER_SEQUENCE = "seq_msgdiag_appender";
    public static final String MSG_DIAGN_APPENDER_TABLE_FOR_ID = "msgdiag_appender_init_seq";
    public static final String MSG_DIAGN_APPENDER_PROP = "msgdiag_appender_prop";
    
    public static final String MSG_DIAGN_DS = "msgdiag_ds";
    public static final String MSG_DIAGN_DS_COLUMN_ID = "id";
    public static final String MSG_DIAGN_DS_SEQUENCE = "seq_msgdiag_ds";
    public static final String MSG_DIAGN_DS_TABLE_FOR_ID = "msgdiag_ds_init_seq";
    public static final String MSG_DIAGN_DS_PROP = "msgdiag_ds_prop";
    
    public static final String TRACCIAMENTO_APPENDER = "tracce_appender";
    public static final String TRACCIAMENTO_APPENDER_COLUMN_ID = "id";
    public static final String TRACCIAMENTO_APPENDER_SEQUENCE = "seq_tracce_appender";
    public static final String TRACCIAMENTO_APPENDER_TABLE_FOR_ID = "tracce_appender_init_seq";
    public static final String TRACCIAMENTO_APPENDER_PROP = "tracce_appender_prop";
    
    // Utilizzabile anche per le transazioni
    public static final String TRACCIAMENTO_DS = "tracce_ds";
    public static final String TRACCIAMENTO_DS_COLUMN_ID = "id";
    public static final String TRACCIAMENTO_DS_SEQUENCE = "seq_tracce_ds";
    public static final String TRACCIAMENTO_DS_TABLE_FOR_ID = "tracce_ds_init_seq";
    public static final String TRACCIAMENTO_DS_PROP = "tracce_ds_prop";
    
    public static final String DUMP_APPENDER = "dump_appender";
    public static final String DUMP_APPENDER_COLUMN_ID = "id";
    public static final String DUMP_APPENDER_SEQUENCE = "seq_dump_appender";
    public static final String DUMP_APPENDER_TABLE_FOR_ID = "dump_appender_init_seq";
    
    public static final String DUMP_APPENDER_PROP = "dump_appender_prop";
    
    public static final String DUMP_CONFIGURAZIONE = "dump_config";
    
    public static final String DUMP_CONFIGURAZIONE_REGOLA = "dump_config_regola";
    public static final String DUMP_CONFIGURAZIONE_REGOLA_COLUMN_ID = "id";
    public static final String DUMP_CONFIGURAZIONE_REGOLA_SEQUENCE = "seq_dump_config_regola";
    public static final String DUMP_CONFIGURAZIONE_REGOLA_TABLE_FOR_ID = "dump_config_regola_init_seq";

    public static final String DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG = "config";
    public static final String DUMP_CONFIGURAZIONE_PROPRIETARIO_PD = "pd";
    public static final String DUMP_CONFIGURAZIONE_PROPRIETARIO_PA = "pa";
    
    /*DIAGNOSTICA*/
    public static final String MSG_DIAGNOSTICI = "msgdiagnostici";
    public static final String MSG_DIAGNOSTICI_SEQUENCE = "seq_msgdiagnostici";
    public static final String MSG_DIAGNOSTICI_TABLE_FOR_ID = "msgdiagnostici_init_seq";
    
    /*DIAGNOSTICA CORRELAZIONE*/
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE = "msgdiag_correlazione";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_SEQUENCE = "seq_msgdiag_correlazione";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_TABLE_FOR_ID = "msgdiag_correlazione_init_seq";
    public static final String MSG_DIAGNOSTICI_CORRELAZIONE_SA = "msgdiag_correlazione_sa";
    public static final String MSG_CORR_INDEX = "MSG_CORR_INDEX";
    
    /*TRACCIAMENTO*/
    public static final String TRACCE = "tracce";
    public static final String TRACCE_RISCONTRI = "tracce_riscontri";
    public static final String TRACCE_ECCEZIONI = "tracce_eccezioni";
    public static final String TRACCE_TRASMISSIONI = "tracce_trasmissioni";
    public static final String TRACCE_ALLEGATI = "tracce_allegati";
    public static final String TRACCE_EXT_INFO = "tracce_ext_protocol_info";
    public static final String TRACCE_COLUMN_ID = "id";
    public static final String TRACCE_SEQUENCE = "seq_tracce";
    public static final String TRACCE_TABLE_FOR_ID = "tracce_init_seq";
    
    /*TRANSAZIONI*/
    public static final String TRANSAZIONI = "transazioni";
	public static final String TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_1 = "INDEX_TR_FILTROD_REQ";
	public static final String TABLE_TRANSAZIONI_INDEX_FILTRO_RES_1 = "INDEX_TR_FILTROD_RES";
	public static final String TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_2 = "INDEX_TR_FILTROD_REQ_2";
	public static final String TABLE_TRANSAZIONI_INDEX_FILTRO_RES_2 = "INDEX_TR_FILTROD_RES_2";
	
	public static final String TRANSAZIONI_EXTENDED_INFO = "transazione_extended_info";
	
	 /*TRANSAZIONI_SERVER_APPLICATIVI*/
    public static final String TRANSAZIONI_APPLICATIVI_SERVER = "transazioni_sa";
    
    /*EVENTI*/
    public static final String DUMP_EVENTI = "notifiche_eventi";
    
    /*DUMP*/
    public static final String DUMP_MESSAGGI = "dump_messaggi";
    public static final String DUMP_MULTIPART_HEADER = "dump_multipart_header";
    public static final String DUMP_HEADER_TRASPORTO = "dump_header_trasporto";
    public static final String DUMP_ALLEGATI = "dump_allegati";
    public static final String DUMP_ALLEGATI_HEADER = "dump_header_allegato";
    public static final String DUMP_CONTENUTI = "dump_contenuti";
    
    /*PddConsole*/
    public static final String USERS = "users";
    public static final String USERS_STATI = "users_stati";
    public static final String USERS_SOGGETTI = "users_soggetti";
    public static final String USERS_SERVIZI = "users_servizi";
    public static final String MAPPING_FRUIZIONE_PD	= "mapping_fruizione_pd";
    public static final String MAPPING_EROGAZIONE_PA	= "mapping_erogazione_pa";
    
    /*DB INFO*/
    public static final String DB_INFO = "db_info";
    public static final String DB_INFO_CONSOLE = "db_info_console";
    
    // VALORI DI COMODO
    
    public static final int CREATE = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    
    
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
    public static final String TRACCE_COLUMN_SERVIZIO_CORRELATO_VERSIONE = "versione_servizio_correlato";
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
    // id_transazione
    public static final String TRACCE_COLUMN_ID_TRANSAZIONE = "id_transazione";
    // isArrivedi
    public static final String TRACCE_COLUMN_IS_ARRIVED = "is_arrived";
    // SOAP
    public static final String TRACCE_COLUMN_SOAP = "soap_element";
    public static final String TRACCE_COLUMN_DIGEST = "digest";

    /*COLONNE TABELLA TRACCE RISCONTRI*/
    public static final String TRACCE_RISCONTRI_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO = "riscontro";
    public static final String TRACCE_RISCONTRI_COLUMN_RICEVUTA = "ricevuta";
    public static final String TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE = "ora_registrazione";
    public static final String TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO = "tipo_ora_reg";
    public static final String TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT = "tipo_ora_reg_meta";
    public static final String TRACCE_RISCONTRI_COLUMN_GDO = "gdo";
    
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
    // gdo
    public static final String TRACCE_TRASMISSIONI_COLUMN_GDO = "gdo";

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
    public static final String TRACCE_ECCEZIONI_COLUMN_GDO = "gdo";
    
    /*COLONNE TABELLA TRACCE ALLEGATI */
    public static final String TRACCE_ALLEGATI_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_ALLEGATI_COLUMN_CONTENT_ID = "content_id";
    public static final String TRACCE_ALLEGATI_COLUMN_CONTENT_LOCATION = "content_location";
    public static final String TRACCE_ALLEGATI_COLUMN_CONTENT_TYPE = "content_type";
    public static final String TRACCE_ALLEGATI_COLUMN_DIGEST = "digest";
    public static final String TRACCE_ALLEGATI_COLUMN_GDO = "gdo";
    
    /*COLONNE TABELLA TRACCE EXT PROTOCOL INFO */
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA = "idtraccia";
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME = "name";
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE = "value";
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_EXT_VALUE = "ext_value";
    public static final String TRACCE_EXT_PROTOCOL_INFO_COLUMN_GDO = "gdo";


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
    public static final String MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE = "id_transazione";
    public static final String MSG_DIAGNOSTICI_COLUMN_APPLICATIVO = "applicativo";
    public static final String MSG_DIAGNOSTICI_COLUMN_ID = "id";
    
    /*COLONNE TABELLA PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST */
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_ID_PORTA = "id_porta";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME = "nome";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE = "valore";
        
    /*COLONNE TABELLA PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE */
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_ID_PORTA = "id_porta";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME = "nome";
    public static final String PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE = "valore";

    /*COLONNE TABELLA PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST */
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_ID_PORTA = "id_porta";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME = "nome";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE = "valore";
    
    /*COLONNE TABELLA PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE */
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_ID_PORTA = "id_porta";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME = "nome";
    public static final String PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE = "valore";
    
    /*COLONNE TABELLA CONFIG_GENERIC_PROPERTY */
    public static final String CONFIG_GENERIC_PROPERTY_COLUMN_ID_PROPS = "id_props";
    public static final String CONFIG_GENERIC_PROPERTY_COLUMN_NOME = "nome";
    public static final String CONFIG_GENERIC_PROPERTY_COLUMN_VALORE = "valore";
    
    
    /**
     * PROPRIETA CONNETTORE
     */

    //tipi connettori
    public static final String CONNETTORE_TIPO_HTTP = TipiConnettore.HTTP.getNome();
    public static final String CONNETTORE_TIPO_HTTPS = TipiConnettore.HTTPS.getNome();
    public static final String CONNETTORE_TIPO_JMS = TipiConnettore.JMS.getNome();
    public static final String CONNETTORE_TIPO_FILE = TipiConnettore.FILE.getNome();
    public static final String CONNETTORE_TIPO_DISABILITATO = TipiConnettore.DISABILITATO.getNome();
    
    public static final String CONNETTORE_DEBUG = CostantiConnettori.CONNETTORE_DEBUG;

    public static final String CONNETTORE_PROXY_TYPE = CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE;
    public static final String CONNETTORE_PROXY_HOSTNAME = CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME;
    public static final String CONNETTORE_PROXY_PORT = CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT;
    public static final String CONNETTORE_PROXY_USERNAME = CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME;
    public static final String CONNETTORE_PROXY_PASSWORD = CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD;
    
    public static final String CONNETTORE_CONNECTION_TIMEOUT = CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT;
    public static final String CONNETTORE_READ_CONNECTION_TIMEOUT = CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT;
    public static final String CONNETTORE_TEMPO_MEDIO_RISPOSTA = CostantiConnettori.CONNETTORE_TEMPO_MEDIO_RISPOSTA;
    
    public static final String CONNETTORE_TOKEN_POLICY = CostantiConnettori.CONNETTORE_TOKEN_POLICY;
    
    public static final String CONNETTORE_HTTP_LOCATION = CostantiConnettori.CONNETTORE_LOCATION;
    
    public static final String CONNETTORE_HTTP_DATA_TRANSFER_MODE = CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE;
    public static final String CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE = CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE;
    
    public static final String CONNETTORE_HTTP_REDIRECT_FOLLOW = CostantiConnettori.CONNETTORE_HTTP_REDIRECT_FOLLOW;
    public static final String CONNETTORE_HTTP_REDIRECT_MAX_HOP = CostantiConnettori.CONNETTORE_HTTP_REDIRECT_MAX_HOP;
        
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
    public static final String CONNETTORE_HTTPS_TRUST_ALL_CERTS = CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_LOCATION = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_PASSWORD = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD;
    public static final String CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM = CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_TYPE = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_CRLs = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs;
    public static final String CONNETTORE_HTTPS_KEY_STORE_LOCATION = CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION;
    public static final String CONNETTORE_HTTPS_KEY_STORE_PASSWORD = CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD;
    public static final String CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM = CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM;
    public static final String CONNETTORE_HTTPS_KEY_STORE_TYPE = CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE;
    public static final String CONNETTORE_HTTPS_KEY_PASSWORD = CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD;
    public static final String CONNETTORE_HTTPS_KEY_ALIAS = CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS;
    public static final String CONNETTORE_HTTPS_HOSTNAME_VERIFIER = CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER;
    public static final String CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER = CostantiConnettori.CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER;
    public static final String CONNETTORE_HTTPS_SSL_TYPE = CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE;
    public static final String CONNETTORE_HTTPS_SECURE_RANDOM = CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM;
    public static final String CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM = CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM;
    
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_MODE = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME;
	
    public static final String COMPONENTE_SERVIZIO_PD = "pd";
    public static final String COMPONENTE_SERVIZIO_PA = "pa";
    public static final String COMPONENTE_SERVIZIO_IM = "im";
    
    public static final String TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD = "abilitazione";
    public static final String TIPO_FILTRO_DISABILITAZIONE_SERVIZIO_PDD = "disabilitazione";
    
    public static final String API_RESOURCE_HTTP_METHOD_ALL_VALUE = "ALL";
    public static final String API_RESOURCE_PATH_ALL_VALUE = "*";
    public static final String API_RESOURCE_DETAIL_REQUEST = "REQUEST";
    public static final String API_RESOURCE_DETAIL_RESPONSE = "RESPONSE";
//    public static final int API_RESOURCE_DETAIL_STATUS_UNDEFINED = -1;
    
    public static final String ISSUER_APIKEY = "apiKey";
    public static final String ISSUER_APIKEY_APPID = "apiKey_appId";
    public static String getISSUER_APIKEY(boolean appId) {
    	return appId ? ISSUER_APIKEY_APPID : ISSUER_APIKEY;
    }
    public static boolean isAPPID(String issuer) {
    	return ISSUER_APIKEY_APPID.equals(issuer);
    }
}
