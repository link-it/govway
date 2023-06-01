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



package org.openspcoop2.core.constants;

import org.openspcoop2.utils.certificate.KeystoreType;

/**
 * CostantiDB
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class CostantiDB {
	
	private CostantiDB() {}
	    

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
	
	public static final String CONDITION_IS_NOT_NULL = "is not null";
	public static final String CONDITION_IS_NULL = "is null";
	public static final String CONDITION_AND = " AND ";
	public static final String CONDITION_OR = " OR ";
	
    /*COMMONS*/
	public static final String COLUMN_ALIAS_COUNT = "count";
    public static final String COLUMN_ID = "id";
    public static final String CONNETTORI = "connettori";
    public static final String CONNETTORI_CUSTOM = "connettori_custom";
    public static final String SOGGETTI = "soggetti";
    public static final String SOGGETTI_PDD = "soggetti_pdd";
    public static final String SOGGETTI_COLUMN_TIPO_SOGGETTO = "tipo_soggetto";
    public static final String SOGGETTI_COLUMN_NOME_SOGGETTO = "nome_soggetto";
    public static final String SOGGETTI_COLUMN_DESCRIZIONE = "descrizione";
    public static final String SOGGETTI_COLUMN_IDENTIFICATIVO_PORTA = "identificativo_porta";
    public static final String SOGGETTI_COLUMN_SERVER = "server";
    public static final String SOGGETTI_COLUMN_ROUTER = "is_router";
    public static final String SOGGETTI_COLUMN_DEFAULT = "is_default";
    public static final String SOGGETTI_COLUMN_TIPO_SOGGETTO_VIRTUALE = "tipo_soggetto_virtuale";
    public static final String SOGGETTI_COLUMN_NOME_SOGGETTO_VIRTUALE = "nome_soggetto_virtuale";
    public static final String SOGGETTI_COLUMN_SUPERUSER = "superuser";
    
    /*REGSERV*/
    public static final String PROTOCOL_PROPERTIES = "protocol_properties";
    public static final String PDD = "pdd";
    public static final String PDD_COLUMN_NOME = "nome";
    public static final String PDD_COLUMN_TIPO = "tipo";
    public static final String GRUPPI	= "gruppi";
    public static final String GRUPPI_COLUMN_NOME = "nome";
    public static final String RUOLI	= "ruoli";
    public static final String SCOPE	= "scope";
    public static final String SOGGETTI_RUOLI = "soggetti_ruoli";
    public static final String SOGGETTI_CREDENZIALI = "soggetti_credenziali";
    public static final String SOGGETTI_PROPS = "soggetti_properties";
    public static final String SERVIZI = "servizi";
    public static final String SERVIZI_COLUMN_ID_ACCORDO_REF = "id_accordo";
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
    public static final String ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF = "id_accordo";
    public static final String ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF = "id_gruppo";
    public static final String CONNETTORI_PROPERTIES = "connettori_properties";
    public static final String DOCUMENTI = "documenti";
    public static final String ACCORDI_SERVIZI_COMPOSTO = "acc_serv_composti";
    public static final String ACCORDI_SERVIZI_COMPONENTI = "acc_serv_componenti";
    public static final String ACCORDI_COOPERAZIONE = "accordi_cooperazione";
    public static final String ACCORDI_COOPERAZIONE_PARTECIPANTI = "accordi_coop_partecipanti";
    
    /*CONFIG*/
    public static final String SERVIZI_APPLICATIVI = "servizi_applicativi";
    public static final String SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO = "id_soggetto";
    public static final String SERVIZI_APPLICATIVI_COLUMN_NOME = "nome";
    public static final String SERVIZI_APPLICATIVI_COLUMN_TIPO = "tipo";
    public static final String SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE = "tipologia_fruizione";
    public static final String SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE = "tipologia_erogazione";
    public static final String SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY = "token_policy";
    public static final String SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT = "as_client";
    public static final String SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH = "tipoauth";
    public static final String SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT = "cn_subject";
    public static final String SERVIZI_APPLICATIVI_COLUMN_SUBJECT = "subject";
    public static final String SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER = "cn_issuer";
    public static final String SERVIZI_APPLICATIVI_COLUMN_ISSUER = "issuer";
    public static final String SERVIZI_APPLICATIVI_COLUMN_UTENTE = "utente";
    public static final String SERVIZI_APPLICATIVI_RUOLI = "sa_ruoli";
    public static final String SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF = "id_servizio_applicativo";
    public static final String SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO = "ruolo";
    public static final String SERVIZI_APPLICATIVI_CREDENZIALI = "sa_credenziali";
    public static final String SERVIZI_APPLICATIVI_PROPS = "sa_properties";
    public static final String SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME = "nome";
    public static final String SERVIZI_APPLICATIVI_PROPS_ID_SERVIZIO_APPLICATIVO_REF = "id_servizio_applicativo";
    public static final String PORTE_APPLICATIVE = "porte_applicative";
    public static final String PORTE_APPLICATIVE_SA = "porte_applicative_sa";
    public static final String PORTE_APPLICATIVE_SA_PROPS = "pa_sa_properties";
    public static final String PORTE_APPLICATIVE_BEHAVIOUR_PROPS = "pa_behaviour_props";
    public static final String PORTE_APPLICATIVE_AUTENTICAZIONE_PROP = "pa_auth_properties";
    public static final String PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP = "pa_authz_properties";
    public static final String PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP = "pa_authzc_properties";
    public static final String PORTE_APPLICATIVE_RATE_LIMITING_PROP = "pa_ct_properties";
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
    public static final String PORTE_APPLICATIVE_HANDLERS = "pa_handlers";
    public static final String PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY = "pa_aa";
    public static final String PORTE_APPLICATIVE_TOKEN_SA = "pa_token_sa";
    public static final String PORTE_APPLICATIVE_TOKEN_RUOLI = "pa_token_ruoli";
    public static final String PORTE_DELEGATE = "porte_delegate";
    public static final String PORTE_DELEGATE_SA = "porte_delegate_sa";
    public static final String PORTE_DELEGATE_AUTENTICAZIONE_PROP = "pd_auth_properties";
    public static final String PORTE_DELEGATE_AUTORIZZAZIONE_PROP = "pd_authz_properties";
    public static final String PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP = "pd_authzc_properties";
    public static final String PORTE_DELEGATE_RATE_LIMITING_PROP = "pd_ct_properties";
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
    public static final String PORTE_DELEGATE_HANDLERS = "pd_handlers";
    public static final String PORTE_DELEGATE_ATTRIBUTE_AUTHORITY = "pd_aa";
    public static final String PORTE_DELEGATE_TOKEN_SA = "pd_token_sa";
    public static final String PORTE_DELEGATE_TOKEN_RUOLI = "pd_token_ruoli";
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
    public static final String CONFIGURAZIONE_HANDLERS = "config_handlers";

    public static final String SUPERUSER_COLUMN = "superuser";
    
    public static final String PORTA_COLUMN_ID_REF = "id_porta";
    public static final String PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF = "id_servizio_applicativo";
    public static final String PORTA_COLUMN_ID_SOGGETTO_REF = "id_soggetto";
    public static final String PORTA_COLUMN_ID_SERVIZIO_REF = "id_servizio";
    public static final String PORTA_COLUMN_TIPO_SOGGETTO_EROGATORE = "tipo_soggetto_erogatore";
    public static final String PORTA_COLUMN_NOME_SOGGETTO_EROGATORE = "nome_soggetto_erogatore";
    public static final String PORTA_COLUMN_TIPO_SERVIZIO = "tipo_servizio";
    public static final String PORTA_COLUMN_NOME_SERVIZIO = "nome_servizio";
    public static final String PORTA_COLUMN_SERVIZIO = "servizio";
    public static final String PORTA_COLUMN_VERSIONE_SERVIZIO = "versione_servizio";
    public static final String PORTA_COLUMN_NOME_PORTA = "nome_porta";
    
    public static final String TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE =  "id_trasformazione";
    public static final String TRASFORMAZIONI_COLUMN_POSIZIONE = "posizione";
    public static final String TRASFORMAZIONI_COLUMN_APPLICABILITA_CT = "applicabilita_ct";
    public static final String TRASFORMAZIONI_COLUMN_APPLICABILITA_PATTERN = "applicabilita_pattern";
    public static final String TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE = "soap_envelope";
    public static final String TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_AS_ATTACH ="soap_envelope_as_attach";
    public static final String TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TIPO = "soap_envelope_tipo";
    public static final String TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TEMPLATE = "soap_envelope_template";
    public static final String TRASFORMAZIONI_COLUMN_TIPO_SOGGETTO = "tipo_soggetto";
    public static final String TRASFORMAZIONI_COLUMN_NOME_SOGGETTO = "nome_soggetto";
    public static final String TRASFORMAZIONI_COLUMN_VALORE = "valore";
    public static final String TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA = "identificazione_fallita";
    
    public static final String CONFIG_NODI_RUNTIME = "nodi_runtime";
    
    public static final String CONFIG_URL_INVOCAZIONE = "config_url_invocazione";
    public static final String CONFIG_URL_REGOLE = "config_url_regole";
    
    public static final String CONFIG_GENERIC_PROPERTIES = "generic_properties";
    public static final String CONFIG_GENERIC_PROPERTIES_COLUMN_ID = "id";
    public static final String CONFIG_GENERIC_PROPERTIES_SEQUENCE = "seq_generic_properties";
    public static final String CONFIG_GENERIC_PROPERTIES_TABLE_FOR_ID = "generic_properties_init_seq";
    public static final String CONFIG_GENERIC_PROPERTY = "generic_property";
    
    public static final String CONTROLLO_TRAFFICO_CONFIG = "ct_config";
    public static final String CONTROLLO_TRAFFICO_CONFIG_RATE_LIMITING_PROPERTIES = "ct_rt_props";
    public static final String CONTROLLO_TRAFFICO_CONFIG_POLICY = "ct_config_policy";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY = "ct_active_policy";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_TIPO_FRUITORE = "filtro_tipo_fruitore";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_NOME_FRUITORE = "filtro_nome_fruitore";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_SA_FRUITORE = "filtro_sa_fruitore";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_PORTA = "filtro_porta";
    public static final String CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_RUOLO = "filtro_ruolo";
    
    public static final String REGISTRO_PLUGINS = "registro_plugins";
    public static final String REGISTRO_PLUGINS_COLUMN_ID = "id";
    public static final String REGISTRO_PLUGINS_SEQUENCE = "seq_registro_plugins";
    public static final String REGISTRO_PLUGINS_TABLE_FOR_ID = "registro_plugins_init_seq";
    public static final String REGISTRO_PLUGINS_ARCHIVE = "registro_plug_jar";
    
    public static final String REGISTRO_CLASSI = "plugins";
    public static final String REGISTRO_CLASSI_COMPATIBILITA_SERVIZIO = "plugins_servizi_comp";
    public static final String REGISTRO_CLASSI_COMPATIBILITA_AZIONE = "plugins_azioni_comp";
    public static final String REGISTRO_CLASSI_COMPATIBILITA_PROPRIETA = "plugins_props_comp";
    
    public static final String REMOTE_STORE = "remote_store";
    public static final String REMOTE_STORE_COLUMN_ID = "id";
    public static final String REMOTE_STORE_SEQUENCE = "seq_remote_store";
    public static final String REMOTE_STORE_TABLE_FOR_ID = "remote_store_init_seq";
    
    public static final String REMOTE_STORE_KEY = "remote_store_key";
    
    private static boolean allarmiEnabled = false;
    public static boolean isAllarmiEnabled() {
		return allarmiEnabled;
	}
	public static void setAllarmiEnabled(boolean allarmiEnabled) {
		CostantiDB.allarmiEnabled = allarmiEnabled;
	}
	public static final String ALLARMI = "allarmi";
    public static final String ALLARMI_PARAMETRI = "allarmi_parametri";
    public static final String ALLARMI_HISTORY = "allarmi_history";
    public static final String ALLARMI_NOTIFICHE = "allarmi_notifiche";
    
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

    public static final String OLD_BACKWARD_COMPATIBILITY_DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG = "config";
    public static final String DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD = "configpd";
    public static final String DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA = "configpa";
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
	
	 /*STATISTICHE*/
    public static final String STATISTICHE = "statistiche";
    public static final String STATISTICHE_ORARIE = "statistiche_orarie";
    public static final String STATISTICHE_ORARIE_CONTENUTI = "stat_orarie_contenuti";
    public static final String STATISTICHE_GIORNALIERE = "statistiche_giornaliere";
    public static final String STATISTICHE_GIORNALIERE_CONTENUTI = "stat_giorni_contenuti";
    public static final String STATISTICHE_SETTIMANALI = "statistiche_settimanali";
    public static final String STATISTICHE_SETTIMANALI_CONTENUTI = "stat_settimane_contenuti";
    public static final String STATISTICHE_MENSILI = "statistiche_mensili";
    public static final String STATISTICHE_MENSILI_CONTENUTI = "stat_mensili_contenuti";
    
    public static final int STATISTICHE_STATO_RECORD_VALIDO = 1; // finito anche in patch sql
    public static final int STATISTICHE_STATO_RECORD_ANCORA_VALIDO_IN_FASE_DI_AGGIORNAMENTO = 2;
    public static final int STATISTICHE_STATO_RECORD_IN_AGGIORNAMENTO = 0;
    public static final int STATISTICHE_STATO_RECORD_ELIMINATO = -2;
	
	public static final String TRANSAZIONI_EXTENDED_INFO = "transazione_extended_info";
	
	public static final String TRANSAZIONI_ESITI = "transazioni_esiti";
	public static final String TRANSAZIONI_CLASSE_ESITI = "transazioni_classe_esiti";
	
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
    public static final String USERS_PASSWORD = "users_password";
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
     * PROPRIETA SICUREZZA
     */

    public static final String KEY_IDENTIFIER_BST_DIRECT_REFERENCE = "DirectReference";
    public static final String KEY_IDENTIFIER_ISSUER_SERIAL = "IssuerSerial";
    public static final String KEY_IDENTIFIER_X509 = "X509KeyIdentifier";
    public static final String KEY_IDENTIFIER_SKI = "SKIKeyIdentifier";
    public static final String KEY_IDENTIFIER_EMBEDDED_KEY_NAME = "EmbeddedKeyName";
    public static final String KEY_IDENTIFIER_THUMBPRINT = "Thumbprint";
    public static final String KEY_IDENTIFIER_ENCRYPTED_KEY_SHA1 = "EncryptedKeySHA1";
    
    public static final String INCLUSIVE_C14N_10_OMITS_COMMENTS_URI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String INCLUSIVE_C14N_10_WITH_COMMENTS_URI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String INCLUSIVE_C14N_11_OMITS_COMMENTS_URI = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String INCLUSIVE_C14N_11_WITH_COMMENTS_URI = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    public static final String EXCLUSIVE_C14N_10_OMITS_COMMENTS_URI = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String EXCLUSIVE_C14N_10_WITH_COMMENTS_URI = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    
    public static final String KEYSTORE_TYPE_KEY_PAIR = KeystoreType.KEY_PAIR.getNome();
    public static final String KEYSTORE_TYPE_PUBLIC_KEY = KeystoreType.PUBLIC_KEY.getNome();   
    public static final String KEYSTORE_TYPE_JWK = KeystoreType.JWK_SET.getNome();  
    public static final String KEYSTORE_TYPE_JKS = KeystoreType.JKS.getNome();    
    public static final String KEYSTORE_TYPE_PKCS12 = KeystoreType.PKCS12.getNome();
    
    
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
    public static final String CONNETTORE_HTTPS_TRUST_STORE_CRLS = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs;
    public static final String CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY = CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY;
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
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR;
	public static final String CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_MODE = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ;
	public static final String CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME = CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME;
	
	
    /**
     * PROPRIETA MODI
     */
	
	public static final String MODIPA_VALUE_UNDEFINED = "-";
	
	public static final String MODIPA_PROFILO_INTERAZIONE = "modipaInteractionProfile";
	public static final String MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD = "crud";
	public static final String MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE = "bloccante";
	public static final String MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE = "nonBloccante";
	
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA = "modipaInteractionAsyncProfile";
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH = "PUSH";
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL = "PULL";
	
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO = "modipaInteractionAsyncRole";
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA = "Richiesta";
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO = "RichiestaStato";
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA = "Risposta";
	
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA = "modipaInteractionAsyncApiRequest";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA = "modipaInteractionAsyncServiceRequest";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA = "modipaInteractionAsyncActionRequest";
	
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE = "modipaSecurityChannelProfile";
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01 = "idac01";
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02 = "idac02";
    
	public static final String MODIPA_SICUREZZA_MESSAGGIO = "modipaSecurityMessage";
	
	public static final String MODIPA_SICUREZZA_TOKEN = "modipaSecurityToken";
	
	public static final String MODIPA_SICUREZZA_TOKEN_POLICY = "modipaSecurityTokenPolicy";
	
	public static final String MODIPA_SICUREZZA_TOKEN_CLIENT_ID = "modipaSecurityTokenClientId";
	
	public static final String MODIPA_SICUREZZA_TOKEN_KID_ID = "modipaSecurityTokenKID";
	
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE = "modipaSecurityMessageProfileActionMode";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE = "modipaSecurityMessageConfig";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI = "entrambi";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA = "richiesta";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA = "risposta";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS = "entrambi_attachments";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS = "richiesta_attachments";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS = "risposta_attachments";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO = "custom";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO = "modipaSecurityMessageProfile";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01 = "idam01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02 = "idam02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301 = "idam0301";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302 = "idam0302";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401 = "idam0401";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402 = "idam0402";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH = "modipaSecurityMessageSorgenteToken";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE = "locale";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND = "pdnd";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH = "oauth";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER = "modipaSecurityMessageHeaderName";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA = "modipa";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION = "authorization";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE = "authorization_modipa";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA = "autonlyreq_modipa";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM = "custom";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE = "authorization_custom";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM = "autonlyreq_custom";
   
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM = "modipaSecurityMessageHeaderCustomName";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE = "modipaSecurityMessageHeaderSignatureMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_VALUE_STANDARD = "standard";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_VALUE_CUSTOM = "custom";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_AGID_SIGNATURE_HEADER_NAME = "modipaSecurityMessageHeaderSignatureHdrName";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA = "modipaSecurityMessageCorniceSicurezza";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN = "modipaSecurityMessageCorniceSicurezzaPattern";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01 = "audit01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02 = "audit02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD = "old";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA = "modipaSecurityMessageCorniceSicurezzaSchema";

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE = "modipaSecurityMessageCorniceSicurezzaOpzionale";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST = "modipaSecurityMessageRequestDigest";

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG = "modipaSecurityMessageRestRequestAlg";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG = "modipaSecurityMessageRestResponseAlg";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_DIGEST_ENCODING = "modipaSecurityMessageRestRequestDigestEncoding";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_DIGEST_ENCODING = "modipaSecurityMessageRestResponseDigestEncoding";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X509_VALUE_X5URL = "modipaSecurityMessageRestRequestX509Url";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL = "modipaSecurityMessageRestRequestX509Url";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL = "modipaSecurityMessageRestResponseX509Url";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG = "modipaSecurityMessageSoapRequestAlg";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG = "modipaSecurityMessageSoapResponseAlg";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_CANONICALIZATION_ALG = "modipaSecurityMessageSoapRequestCanonicalizationAlg";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG = "modipaSecurityMessageSoapResponseCanonicalizationAlg";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE = "modipaKeystoreCertMode";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE = "modipaKeystoreFruizioneMode";
    
    public static final String MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO = "modipaSecurityOauthId";
    public static final String MODIPA_PROFILO_SICUREZZA_OAUTH_KID = "modipaSecurityOauthKid";
    public static final String MODIPA_PROFILO_SICUREZZA_OAUTH_KEYSTORE = "modipaSecurityOauthKeystore";
    
    public static final String MODIPA_KEYSTORE_MODE = "modipaKeystoreMode";
    public static final String MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE = "archive";
    public static final String MODIPA_KEYSTORE_MODE_VALUE_PATH = "path";
    public static final String MODIPA_KEYSTORE_MODE_VALUE_HSM = "hsm";
    
    public static final String MODIPA_KEYSTORE_TYPE = "modipaKeystoreType";
    
    public static final String MODIPA_KEYSTORE_PATH = "modipaKeystorePath";
    
    public static final String MODIPA_KEYSTORE_PATH_PUBLIC_KEY = "modipaKeystorePathPublicKey";
    
    public static final String MODIPA_KEYSTORE_KEY_ALGORITHM = "modipaKeystoreKeyPairAlgo";
    
    public static final String MODIPA_KEYSTORE_PASSWORD = "modipaKeystorePassword";
    
    public static final String MODIPA_KEYSTORE_ARCHIVE = "modipaKeystoreArchive";
    public static final String MODIPA_KEYSTORE_CERTIFICATE = "modipaKeystoreCertificate";
    
    public static final String MODIPA_KEY_ALIAS = "modipaKeyAlias";
    public static final String MODIPA_KEY_PASSWORD = "modipaKeyPassword";
    
    public static final String MODIPA_KEY_CN_SUBJECT = "modipaKeyCNSubject";
    public static final String MODIPA_KEY_CN_ISSUER = "modipaKeyCNIssuer";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE = "modipaTruststoreMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE = "modipaTruststoreType";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH = "modipaTruststorePath";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS = "modipaTruststoreCRLs";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY = "modipaTruststoreOCSP";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD = "modipaTruststorePassword";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE = "modipaSslTruststoreMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE = "modipaSslTruststoreType";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH = "modipaSslTruststorePath";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS = "modipaSslTruststoreCRLs";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY = "modipaSslTruststoreOCSP";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD = "modipaSslTruststorePassword";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT = "modipaSecurityMessageRequestIatMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT = "modipaSecurityMessageResponseIatMode";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS = "modipaSecurityMessageRequestIatTtl";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS = "modipaSecurityMessageResponseIatTtl";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED = "modipaSecurityMessageRequestExp";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED = "modipaSecurityMessageResponseExp";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE = "modipaSecurityMessageRequestAud";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE = "modipaSecurityMessageResponseAud";

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE = "modipaSecurityMessageResponseAudExpected";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_JWT_CLAIMS = "modipaSecurityMessageRequestClaims";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_JWT_CLAIMS = "modipaSecurityMessageResponseClaims";
    
    public static final String MODIPA_VALUE_SAME = "same";
    public static final String MODIPA_VALUE_DIFFERENT = "different";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI = "modipaSecurityMessageRequestJti";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI = "modipaSecurityMessageResponseJti";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_SAME = MODIPA_VALUE_SAME;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_DIFFERENT = MODIPA_VALUE_DIFFERENT;

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO = "modipaSecurityMessageRequestJtiIdMsg";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO = "modipaSecurityMessageResponseJtiIdMsg";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION = "auth";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI = "modi";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE = "modipaSecurityMessageRequestIntegrityAudMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE = "modipaSecurityMessageResponseIntegrityAudMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME = MODIPA_VALUE_SAME;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT = MODIPA_VALUE_DIFFERENT;

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY = "modipaSecurityMessageRequestIntegrityAud";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY = "modipaSecurityMessageResponseIntegrityAud";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE = "modipaSecurityMessageRequestAuditAudMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTE_AUDIT_AUDIENCE_VALUE_SAME = MODIPA_VALUE_SAME;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE_VALUE_DIFFERENT = MODIPA_VALUE_DIFFERENT;

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE_CUSTOM_AUDIT = "modipaSecurityMessageRequestAuditAud";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION = "modipaSecurityMessageRequestClaimsAuth";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION = "modipaSecurityMessageResponseClaimsAuth";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI = "modipaSecurityMessageRequestClaimsModi";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI = "modipaSecurityMessageResponseClaimsModi";

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI = "modipaSecurityMessageRequestDuplicateJti";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI = "modipaSecurityMessageResponseDuplicateJti";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION = "auth";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI = "modi";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST = "modipaSecurityMessageHttpHeaders";

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP = "modipaSecurityMessageSoapHeaders";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_PREFIX = "modipaSecurityMessageCorniceSicurezzaSchemaMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_PREFIX = "modipaSecurityMessageCorniceSicurezzaSchemaValue";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE = "modipaSecurityMessageCorniceSicurezzaCodiceEnteMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE = "modipaSecurityMessageCorniceSicurezzaCodiceEnte";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE = "modipaSecurityMessageCorniceSicurezzaUserMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER = "modipaSecurityMessageCorniceSicurezzaUser";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE = "modipaSecurityMessageCorniceSicurezzaIPUserMode";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER = "modipaSecurityMessageCorniceSicurezzaIPUser";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509 = "modipaSecurityMessageRestRequestX509Cert";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509 = "modipaSecurityMessageRestResponseX509Cert";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U = "x5u";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C = "x5c";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T = "x5t";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509 = "modipaSecurityMessageSoapRequestX509Cert";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509 = "modipaSecurityMessageSoapResponseX509Cert";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN = "modipaSecurityMessageRestRequestX509CertUseCertificateChain";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN = "modipaSecurityMessageRestResponseX509CertUseCertificateChain";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN = "modipaSecurityMessageSoapRequestX509CertUseCertificateChain";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN = "modipaSecurityMessageSoapResponseX509CertUseCertificateChain";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN = "modipaSecurityMessageSoapRequestX509CertIncludeSignatureToken";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN = "modipaSecurityMessageSoapResponseX509CertIncludeSignatureToken";
    
    public static final String MODIPA_PROFILO_UNDEFINED = "-";
    public static final String MODIPA_PROFILO_DEFAULT = "default";
    public static final String MODIPA_PROFILO_RIDEFINISCI = "ridefinisci";
    
    public static final String MODIPA_KEYSTORE_FRUIZIONE_APPLICATIVO = "applicativo";
    public static final String MODIPA_KEYSTORE_FRUIZIONE = "fruizione";
    
    
    /**
     * STATO FUNZIONALITA
     */
    
    public static final String STATO_FUNZIONALITA_ABILITATO = "abilitato";
    public static final String STATO_FUNZIONALITA_DISABILITATO = "disabilitato";
    
	
    public static final String COMPONENTE_SERVIZIO_PD = "pd";
    public static final String COMPONENTE_SERVIZIO_PA = "pa";
    public static final String COMPONENTE_SERVIZIO_IM = "im";
    
    public static final String TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD = "abilitazione";
    public static final String TIPO_FILTRO_DISABILITAZIONE_SERVIZIO_PDD = "disabilitazione";
    
    public static final String API_RESOURCE_HTTP_METHOD_ALL_VALUE = "ALL";
    public static final String API_RESOURCE_PATH_ALL_VALUE = "*";
    public static final String API_RESOURCE_DETAIL_REQUEST = "REQUEST";
    public static final String API_RESOURCE_DETAIL_RESPONSE = "RESPONSE";
/**    public static final int API_RESOURCE_DETAIL_STATUS_UNDEFINED = -1; */
    
    public static final String ISSUER_APIKEY = "apiKey";
    public static final String ISSUER_APIKEY_APPID = "apiKey_appId";
    public static String getIssuerApiKey(boolean appId) {
    	return appId ? ISSUER_APIKEY_APPID : ISSUER_APIKEY;
    }
    public static boolean isAPPID(String issuer) {
    	return ISSUER_APIKEY_APPID.equals(issuer);
    }
    
    public static final String HANDLER_PRE_IN = "PreIn";
    public static final String HANDLER_IN = "In";
    public static final String HANDLER_IN_PROTOCOL = "InProtocol";
    public static final String HANDLER_OUT = "Out";
    public static final String HANDLER_POST_OUT = "PostOut";
    public static final String HANDLER_REQUEST_SUFFIX = "Request";
    public static final String HANDLER_RESPONSE_SUFFIX = "Response";
    
    public static final String HANDLER_INIT = "Init";
    public static final String HANDLER_EXIT = "Exit";
    public static final String HANDLER_INTEGRATION_MANAGER_REQUEST = "IntegrationManagerRequest";
    public static final String HANDLER_INTEGRATION_MANAGER_RESPONSE = "IntegrationManagerResponse";
}
