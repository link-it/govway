-- **** Configurazione ****

CREATE SEQUENCE seq_registri MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE registri
(
	nome VARCHAR2(255) NOT NULL,
	location VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	utente VARCHAR2(255),
	password VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_registri_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registri PRIMARY KEY (id)
);

CREATE TRIGGER trg_registri
BEFORE
insert on registri
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_registri.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_routing MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE routing
(
	tipo VARCHAR2(255),
	nome VARCHAR2(255),
	-- registro/gateway
	tiporotta VARCHAR2(255) NOT NULL,
	tiposoggrotta VARCHAR2(255),
	nomesoggrotta VARCHAR2(255),
	-- foreign key per i registri(id)
	registrorotta NUMBER,
	is_default NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_routing PRIMARY KEY (id)
);

CREATE TRIGGER trg_routing
BEFORE
insert on routing
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_routing.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_config_cache_regole MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_cache_regole
(
	status_min NUMBER,
	status_max NUMBER,
	fault NUMBER,
	cache_seconds NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_config_cache_regole PRIMARY KEY (id)
);


ALTER TABLE config_cache_regole MODIFY fault DEFAULT 0;

CREATE TRIGGER trg_config_cache_regole
BEFORE
insert on config_cache_regole
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_cache_regole.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_configurazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE configurazione
(
	-- Cadenza inoltro Riscontri/BusteAsincrone
	cadenza_inoltro VARCHAR2(255) NOT NULL,
	-- Validazione Buste
	validazione_stato VARCHAR2(255),
	validazione_controllo VARCHAR2(255),
	validazione_profilo VARCHAR2(255),
	validazione_manifest VARCHAR2(255),
	-- Validazione Contenuti Applicativi
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR2(255),
	-- wsdl/interface/xsd
	validazione_contenuti_tipo VARCHAR2(255),
	-- abilitato/disabilitato
	validazione_contenuti_mtom VARCHAR2(255),
	-- Livello Log Messaggi Diagnostici
	msg_diag_severita VARCHAR2(255) NOT NULL,
	msg_diag_severita_log4j VARCHAR2(255) NOT NULL,
	-- Tracciamento Buste
	tracciamento_buste VARCHAR2(255),
	tracciamento_esiti VARCHAR2(255),
	-- Transazione
	transazioni_tempi VARCHAR2(255),
	transazioni_token VARCHAR2(255),
	-- Dump
	dump VARCHAR2(255),
	dump_bin_pd VARCHAR2(255),
	dump_bin_pa VARCHAR2(255),
	-- Autenticazione IntegrationManager
	auth_integration_manager VARCHAR2(255),
	-- Cache per l'accesso ai registri
	statocache VARCHAR2(255),
	dimensionecache VARCHAR2(255),
	algoritmocache VARCHAR2(255),
	idlecache VARCHAR2(255),
	lifecache VARCHAR2(255),
	-- Cache per l'accesso alla configurazione
	config_statocache VARCHAR2(255),
	config_dimensionecache VARCHAR2(255),
	config_algoritmocache VARCHAR2(255),
	config_idlecache VARCHAR2(255),
	config_lifecache VARCHAR2(255),
	-- Cache per l'accesso ai dati di autorizzazione
	auth_statocache VARCHAR2(255),
	auth_dimensionecache VARCHAR2(255),
	auth_algoritmocache VARCHAR2(255),
	auth_idlecache VARCHAR2(255),
	auth_lifecache VARCHAR2(255),
	-- Cache per l'accesso ai dati di autenticazione
	authn_statocache VARCHAR2(255),
	authn_dimensionecache VARCHAR2(255),
	authn_algoritmocache VARCHAR2(255),
	authn_idlecache VARCHAR2(255),
	authn_lifecache VARCHAR2(255),
	-- Cache per la gestione dei token
	token_statocache VARCHAR2(255),
	token_dimensionecache VARCHAR2(255),
	token_algoritmocache VARCHAR2(255),
	token_idlecache VARCHAR2(255),
	token_lifecache VARCHAR2(255),
	-- Cache per la gestione dei keystore
	keystore_statocache VARCHAR2(255),
	keystore_dimensionecache VARCHAR2(255),
	keystore_algoritmocache VARCHAR2(255),
	keystore_idlecache VARCHAR2(255),
	keystore_lifecache VARCHAR2(255),
	keystore_crl_lifecache VARCHAR2(255),
	-- connessione su cui vengono inviate le risposte
	-- reply: connessione esistente (es. http reply)
	-- new: nuova connessione
	mod_risposta VARCHAR2(255),
	-- Gestione dell'indirizzo
	indirizzo_telematico VARCHAR2(255),
	-- Gestione Manifest
	gestione_manifest VARCHAR2(255),
	-- Routing Table
	routing_enabled VARCHAR2(255) NOT NULL,
	-- Gestione errore di default per la Porta di Dominio
	-- FOREIGN KEY (id_ge_cooperazione) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore di default durante l'utilizzo di un connettore
	id_ge_cooperazione NUMBER,
	-- FOREIGN KEY (id_ge_integrazione) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore di default durante l'utilizzo di un connettore
	id_ge_integrazione NUMBER,
	-- Gestione MultiTenant
	multitenant_stato VARCHAR2(255),
	multitenant_fruizioni VARCHAR2(255),
	multitenant_erogazioni VARCHAR2(255),
	-- Gestione CORS
	cors_stato VARCHAR2(255),
	cors_tipo VARCHAR2(255),
	cors_all_allow_origins VARCHAR2(255),
	cors_allow_credentials VARCHAR2(255),
	cors_allow_max_age NUMBER,
	cors_allow_max_age_seconds NUMBER,
	cors_allow_origins CLOB,
	cors_allow_headers CLOB,
	cors_allow_methods CLOB,
	cors_allow_expose_headers CLOB,
	-- Response caching
	response_cache_stato VARCHAR2(255),
	response_cache_seconds NUMBER,
	response_cache_max_msg_size NUMBER,
	response_cache_hash_url VARCHAR2(255),
	response_cache_hash_query VARCHAR2(255),
	response_cache_hash_query_list CLOB,
	response_cache_hash_headers VARCHAR2(255),
	response_cache_hash_hdr_list CLOB,
	response_cache_hash_payload VARCHAR2(255),
	response_cache_control_nocache NUMBER,
	response_cache_control_maxage NUMBER,
	response_cache_control_nostore NUMBER,
	-- Cache per il response caching
	response_cache_statocache VARCHAR2(255),
	response_cache_dimensionecache VARCHAR2(255),
	response_cache_algoritmocache VARCHAR2(255),
	response_cache_idlecache VARCHAR2(255),
	response_cache_lifecache VARCHAR2(255),
	-- Cache per la consegna agli applicativi
	consegna_statocache VARCHAR2(255),
	consegna_dimensionecache VARCHAR2(255),
	consegna_algoritmocache VARCHAR2(255),
	consegna_idlecache VARCHAR2(255),
	consegna_lifecache VARCHAR2(255),
	-- Gestione CORS
	canali_stato VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_configurazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_configurazione
BEFORE
insert on configurazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_configurazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** URLInvocazione ****

CREATE SEQUENCE seq_config_url_invocazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_url_invocazione
(
	base_url VARCHAR2(255) NOT NULL,
	base_url_fruizione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_invocazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_url_invocazione
BEFORE
insert on config_url_invocazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_url_invocazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_config_url_regole MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_url_regole
(
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	descrizione CLOB,
	regexpr NUMBER NOT NULL,
	regola VARCHAR2(255) NOT NULL,
	contesto_esterno VARCHAR2(255) NOT NULL,
	base_url VARCHAR2(255),
	protocollo VARCHAR2(255),
	ruolo VARCHAR2(255),
	service_binding VARCHAR2(255),
	tipo_soggetto VARCHAR2(255),
	nome_soggetto VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_config_url_regole_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_regole PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_url_regole
BEFORE
insert on config_url_regole
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_url_regole.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Messaggi diagnostici Appender ****

CREATE SEQUENCE seq_msgdiag_appender MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE msgdiag_appender
(
	tipo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_appender PRIMARY KEY (id)
);

CREATE TRIGGER trg_msgdiag_appender
BEFORE
insert on msgdiag_appender
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_msgdiag_appender.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_msgdiag_appender_prop MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE msgdiag_appender_prop
(
	id_appender NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_msgdiag_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES msgdiag_appender(id),
	CONSTRAINT pk_msgdiag_appender_prop PRIMARY KEY (id)
);

CREATE TRIGGER trg_msgdiag_appender_prop
BEFORE
insert on msgdiag_appender_prop
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_msgdiag_appender_prop.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Tracciamento Appender ****

CREATE SEQUENCE seq_tracce_appender MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE tracce_appender
(
	tipo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_appender PRIMARY KEY (id)
);

CREATE TRIGGER trg_tracce_appender
BEFORE
insert on tracce_appender
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_appender.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_appender_prop MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE tracce_appender_prop
(
	id_appender NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_tracce_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES tracce_appender(id),
	CONSTRAINT pk_tracce_appender_prop PRIMARY KEY (id)
);

CREATE TRIGGER trg_tracce_appender_prop
BEFORE
insert on tracce_appender_prop
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_appender_prop.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Dump Appender ****

CREATE SEQUENCE seq_dump_config MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE dump_config
(
	proprietario VARCHAR2(255) NOT NULL,
	id_proprietario NUMBER NOT NULL,
	dump_realtime VARCHAR2(255),
	id_richiesta_ingresso NUMBER NOT NULL,
	id_richiesta_uscita NUMBER NOT NULL,
	id_risposta_ingresso NUMBER NOT NULL,
	id_risposta_uscita NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_config PRIMARY KEY (id)
);

CREATE TRIGGER trg_dump_config
BEFORE
insert on dump_config
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_dump_config.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_dump_config_regola MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE dump_config_regola
(
	body VARCHAR2(255) NOT NULL,
	attachments VARCHAR2(255) NOT NULL,
	headers VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_config_regola PRIMARY KEY (id)
);

CREATE TRIGGER trg_dump_config_regola
BEFORE
insert on dump_config_regola
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_dump_config_regola.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_dump_appender MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE dump_appender
(
	tipo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_appender PRIMARY KEY (id)
);

CREATE TRIGGER trg_dump_appender
BEFORE
insert on dump_appender
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_dump_appender.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_dump_appender_prop MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE dump_appender_prop
(
	id_appender NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_dump_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES dump_appender(id),
	CONSTRAINT pk_dump_appender_prop PRIMARY KEY (id)
);

CREATE TRIGGER trg_dump_appender_prop
BEFORE
insert on dump_appender_prop
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_dump_appender_prop.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Datasources dove reperire i messaggi diagnostici salvati ****

CREATE SEQUENCE seq_msgdiag_ds MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE msgdiag_ds
(
	nome VARCHAR2(255) NOT NULL,
	nome_jndi VARCHAR2(255) NOT NULL,
	tipo_database VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_msgdiag_ds_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_ds PRIMARY KEY (id)
);

CREATE TRIGGER trg_msgdiag_ds
BEFORE
insert on msgdiag_ds
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_msgdiag_ds.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_msgdiag_ds_prop MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE msgdiag_ds_prop
(
	id_prop NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_msgdiag_ds_prop_1 UNIQUE (id_prop,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_ds_prop_1 FOREIGN KEY (id_prop) REFERENCES msgdiag_ds(id),
	CONSTRAINT pk_msgdiag_ds_prop PRIMARY KEY (id)
);

CREATE TRIGGER trg_msgdiag_ds_prop
BEFORE
insert on msgdiag_ds_prop
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_msgdiag_ds_prop.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Datasources dove reperire le tracce salvate ****

CREATE SEQUENCE seq_tracce_ds MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE tracce_ds
(
	nome VARCHAR2(255) NOT NULL,
	nome_jndi VARCHAR2(255) NOT NULL,
	tipo_database VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_tracce_ds_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_ds PRIMARY KEY (id)
);

CREATE TRIGGER trg_tracce_ds
BEFORE
insert on tracce_ds
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_ds.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_ds_prop MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE tracce_ds_prop
(
	id_prop NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_tracce_ds_prop_1 UNIQUE (id_prop,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_ds_prop_1 FOREIGN KEY (id_prop) REFERENCES tracce_ds(id),
	CONSTRAINT pk_tracce_ds_prop PRIMARY KEY (id)
);

CREATE TRIGGER trg_tracce_ds_prop
BEFORE
insert on tracce_ds_prop
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_ds_prop.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Stato dei servizi attivi sulla Porta di Dominio ****

CREATE SEQUENCE seq_servizi_pdd MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_pdd
(
	componente VARCHAR2(255) NOT NULL,
	-- Stato dei servizi attivi sulla Porta di Dominio
	stato NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_servizi_pdd_1 UNIQUE (componente),
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_pdd PRIMARY KEY (id)
);


ALTER TABLE servizi_pdd MODIFY stato DEFAULT 1;

CREATE TRIGGER trg_servizi_pdd
BEFORE
insert on servizi_pdd
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_pdd.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_servizi_pdd_filtri MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_pdd_filtri
(
	id_servizio_pdd NUMBER NOT NULL,
	tipo_filtro VARCHAR2(255) NOT NULL,
	tipo_soggetto_fruitore VARCHAR2(255),
	soggetto_fruitore VARCHAR2(255),
	identificativo_porta_fruitore VARCHAR2(255),
	tipo_soggetto_erogatore VARCHAR2(255),
	soggetto_erogatore VARCHAR2(255),
	identificativo_porta_erogatore VARCHAR2(255),
	tipo_servizio VARCHAR2(255),
	servizio VARCHAR2(255),
	versione_servizio NUMBER,
	azione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_servizi_pdd_filtri_1 CHECK (tipo_filtro IN ('abilitazione','disabilitazione')),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_pdd_filtri_1 FOREIGN KEY (id_servizio_pdd) REFERENCES servizi_pdd(id),
	CONSTRAINT pk_servizi_pdd_filtri PRIMARY KEY (id)
);

CREATE TRIGGER trg_servizi_pdd_filtri
BEFORE
insert on servizi_pdd_filtri
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_pdd_filtri.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** PddSystemProperties ****

CREATE SEQUENCE seq_pdd_sys_props MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pdd_sys_props
(
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pdd_sys_props_1 UNIQUE (nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd_sys_props PRIMARY KEY (id)
);

CREATE TRIGGER trg_pdd_sys_props
BEFORE
insert on pdd_sys_props
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pdd_sys_props.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Proprieta Generiche ****

CREATE SEQUENCE seq_generic_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE generic_properties
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	tipologia VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_generic_properties_1 UNIQUE (tipologia,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_generic_properties PRIMARY KEY (id)
);

CREATE TRIGGER trg_generic_properties
BEFORE
insert on generic_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_generic_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_generic_property MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE generic_property
(
	id_props NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(4000) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_generic_property_1 UNIQUE (id_props,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_generic_property_1 FOREIGN KEY (id_props) REFERENCES generic_properties(id),
	CONSTRAINT pk_generic_property PRIMARY KEY (id)
);

-- index
CREATE INDEX index_generic_property_1 ON generic_property (id_props);
CREATE TRIGGER trg_generic_property
BEFORE
insert on generic_property
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_generic_property.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Canali ****

CREATE SEQUENCE seq_canali_configurazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE canali_configurazione
(
	nome VARCHAR2(20) NOT NULL,
	descrizione VARCHAR2(255),
	canale_default NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_canali_configurazione_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_configurazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_canali_configurazione
BEFORE
insert on canali_configurazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_canali_configurazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_canali_nodi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE canali_nodi
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	canali VARCHAR2(4000) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_canali_nodi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_nodi PRIMARY KEY (id)
);

CREATE TRIGGER trg_canali_nodi
BEFORE
insert on canali_nodi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_canali_nodi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


