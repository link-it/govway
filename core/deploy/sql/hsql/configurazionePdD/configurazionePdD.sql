-- **** Configurazione ****

CREATE SEQUENCE seq_registri AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE registri
(
	nome VARCHAR(255) NOT NULL,
	location VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	utente VARCHAR(255),
	password VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_registri_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registri PRIMARY KEY (id)
);

CREATE TABLE registri_init_seq (id BIGINT);
INSERT INTO registri_init_seq VALUES (NEXT VALUE FOR seq_registri);



CREATE SEQUENCE seq_routing AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE routing
(
	tipo VARCHAR(255),
	nome VARCHAR(255),
	-- registro/gateway
	tiporotta VARCHAR(255) NOT NULL,
	tiposoggrotta VARCHAR(255),
	nomesoggrotta VARCHAR(255),
	-- foreign key per i registri(id)
	registrorotta BIGINT,
	is_default BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_routing PRIMARY KEY (id)
);

CREATE TABLE routing_init_seq (id BIGINT);
INSERT INTO routing_init_seq VALUES (NEXT VALUE FOR seq_routing);



CREATE SEQUENCE seq_config_cache_regole AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE config_cache_regole
(
	status_min INT,
	status_max INT,
	fault INT,
	cache_seconds INT,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_cache_regole PRIMARY KEY (id)
);


ALTER TABLE config_cache_regole ALTER COLUMN fault SET DEFAULT 0;

CREATE TABLE config_cache_regole_init_seq (id BIGINT);
INSERT INTO config_cache_regole_init_seq VALUES (NEXT VALUE FOR seq_config_cache_regole);



CREATE SEQUENCE seq_configurazione AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE configurazione
(
	-- Cadenza inoltro Riscontri/BusteAsincrone
	cadenza_inoltro VARCHAR(255) NOT NULL,
	-- Validazione Buste
	validazione_stato VARCHAR(255),
	validazione_controllo VARCHAR(255),
	validazione_profilo VARCHAR(255),
	validazione_manifest VARCHAR(255),
	-- Validazione Contenuti Applicativi
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR(255),
	-- wsdl/interface/xsd
	validazione_contenuti_tipo VARCHAR(255),
	-- abilitato/disabilitato
	validazione_contenuti_mtom VARCHAR(255),
	-- Livello Log Messaggi Diagnostici
	msg_diag_severita VARCHAR(255) NOT NULL,
	msg_diag_severita_log4j VARCHAR(255) NOT NULL,
	-- Tracciamento Buste
	tracciamento_buste VARCHAR(255),
	tracciamento_esiti_pd VARCHAR(255),
	tracciamento_esiti VARCHAR(255),
	-- Transazione
	transazioni_tempi_pd VARCHAR(255),
	transazioni_tempi VARCHAR(255),
	transazioni_token_pd VARCHAR(255),
	transazioni_token VARCHAR(255),
	-- Dump
	dump VARCHAR(255),
	dump_bin_pd VARCHAR(255),
	dump_bin_pa VARCHAR(255),
	-- Autenticazione IntegrationManager
	auth_integration_manager VARCHAR(255),
	-- Cache per l'accesso ai registri
	statocache VARCHAR(255),
	dimensionecache VARCHAR(255),
	algoritmocache VARCHAR(255),
	idlecache VARCHAR(255),
	lifecache VARCHAR(255),
	-- Cache per l'accesso alla configurazione
	config_statocache VARCHAR(255),
	config_dimensionecache VARCHAR(255),
	config_algoritmocache VARCHAR(255),
	config_idlecache VARCHAR(255),
	config_lifecache VARCHAR(255),
	-- Cache per l'accesso ai dati di autorizzazione
	auth_statocache VARCHAR(255),
	auth_dimensionecache VARCHAR(255),
	auth_algoritmocache VARCHAR(255),
	auth_idlecache VARCHAR(255),
	auth_lifecache VARCHAR(255),
	-- Cache per l'accesso ai dati di autenticazione
	authn_statocache VARCHAR(255),
	authn_dimensionecache VARCHAR(255),
	authn_algoritmocache VARCHAR(255),
	authn_idlecache VARCHAR(255),
	authn_lifecache VARCHAR(255),
	-- Cache per la gestione dei token
	token_statocache VARCHAR(255),
	token_dimensionecache VARCHAR(255),
	token_algoritmocache VARCHAR(255),
	token_idlecache VARCHAR(255),
	token_lifecache VARCHAR(255),
	-- Cache per la gestione dei dati ottenuti da attribute authority
	aa_statocache VARCHAR(255),
	aa_dimensionecache VARCHAR(255),
	aa_algoritmocache VARCHAR(255),
	aa_idlecache VARCHAR(255),
	aa_lifecache VARCHAR(255),
	-- Cache per la gestione dei keystore
	keystore_statocache VARCHAR(255),
	keystore_dimensionecache VARCHAR(255),
	keystore_algoritmocache VARCHAR(255),
	keystore_idlecache VARCHAR(255),
	keystore_lifecache VARCHAR(255),
	keystore_crl_lifecache VARCHAR(255),
	-- connessione su cui vengono inviate le risposte
	-- reply: connessione esistente (es. http reply)
	-- new: nuova connessione
	mod_risposta VARCHAR(255),
	-- Gestione dell'indirizzo
	indirizzo_telematico VARCHAR(255),
	-- Gestione Manifest
	gestione_manifest VARCHAR(255),
	-- Routing Table
	routing_enabled VARCHAR(255) NOT NULL,
	-- Gestione errore di default per la Porta di Dominio
	-- FOREIGN KEY (id_ge_cooperazione) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore di default durante l'utilizzo di un connettore
	id_ge_cooperazione BIGINT,
	-- FOREIGN KEY (id_ge_integrazione) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore di default durante l'utilizzo di un connettore
	id_ge_integrazione BIGINT,
	-- Gestione MultiTenant
	multitenant_stato VARCHAR(255),
	multitenant_fruizioni VARCHAR(255),
	multitenant_erogazioni VARCHAR(255),
	-- Gestione CORS
	cors_stato VARCHAR(255),
	cors_tipo VARCHAR(255),
	cors_all_allow_origins VARCHAR(255),
	cors_all_allow_methods VARCHAR(255),
	cors_all_allow_headers VARCHAR(255),
	cors_allow_credentials VARCHAR(255),
	cors_allow_max_age INT,
	cors_allow_max_age_seconds INT,
	cors_allow_origins VARCHAR(65535),
	cors_allow_headers VARCHAR(65535),
	cors_allow_methods VARCHAR(65535),
	cors_allow_expose_headers VARCHAR(65535),
	-- Response caching
	response_cache_stato VARCHAR(255),
	response_cache_seconds INT,
	response_cache_max_msg_size BIGINT,
	response_cache_hash_url VARCHAR(255),
	response_cache_hash_query VARCHAR(255),
	response_cache_hash_query_list VARCHAR(65535),
	response_cache_hash_headers VARCHAR(255),
	response_cache_hash_hdr_list VARCHAR(65535),
	response_cache_hash_payload VARCHAR(255),
	response_cache_control_nocache INT,
	response_cache_control_maxage INT,
	response_cache_control_nostore INT,
	-- Cache per il response caching
	response_cache_statocache VARCHAR(255),
	response_cache_dimensionecache VARCHAR(255),
	response_cache_algoritmocache VARCHAR(255),
	response_cache_idlecache VARCHAR(255),
	response_cache_lifecache VARCHAR(255),
	-- Cache per la consegna agli applicativi
	consegna_statocache VARCHAR(255),
	consegna_dimensionecache VARCHAR(255),
	consegna_algoritmocache VARCHAR(255),
	consegna_idlecache VARCHAR(255),
	consegna_lifecache VARCHAR(255),
	-- Gestione CORS
	canali_stato VARCHAR(255),
	-- Cache per la gestione delle richieste
	dati_richieste_statocache VARCHAR(255),
	dati_richieste_dimensionecache VARCHAR(255),
	dati_richieste_algoritmocache VARCHAR(255),
	dati_richieste_idlecache VARCHAR(255),
	dati_richieste_lifecache VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_configurazione PRIMARY KEY (id)
);

CREATE TABLE configurazione_init_seq (id BIGINT);
INSERT INTO configurazione_init_seq VALUES (NEXT VALUE FOR seq_configurazione);



-- **** URLInvocazione ****

CREATE SEQUENCE seq_config_url_invocazione AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE config_url_invocazione
(
	base_url VARCHAR(255) NOT NULL,
	base_url_fruizione VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_invocazione PRIMARY KEY (id)
);

CREATE TABLE config_url_invocazione_init_seq (id BIGINT);
INSERT INTO config_url_invocazione_init_seq VALUES (NEXT VALUE FOR seq_config_url_invocazione);



CREATE SEQUENCE seq_config_url_regole AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE config_url_regole
(
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	descrizione VARCHAR(65535),
	regexpr INT NOT NULL,
	regola VARCHAR(255) NOT NULL,
	contesto_esterno VARCHAR(255) NOT NULL,
	base_url VARCHAR(255),
	protocollo VARCHAR(255),
	ruolo VARCHAR(255),
	service_binding VARCHAR(255),
	tipo_soggetto VARCHAR(255),
	nome_soggetto VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_config_url_regole_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_regole PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_config_url_regole_1 ON config_url_regole (nome);
CREATE TABLE config_url_regole_init_seq (id BIGINT);
INSERT INTO config_url_regole_init_seq VALUES (NEXT VALUE FOR seq_config_url_regole);



-- **** Messaggi diagnostici ****

CREATE SEQUENCE seq_msgdiag_appender AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE msgdiag_appender
(
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_appender PRIMARY KEY (id)
);

CREATE TABLE msgdiag_appender_init_seq (id BIGINT);
INSERT INTO msgdiag_appender_init_seq VALUES (NEXT VALUE FOR seq_msgdiag_appender);



CREATE SEQUENCE seq_msgdiag_appender_prop AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE msgdiag_appender_prop
(
	id_appender BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT uniq_msgdiag_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES msgdiag_appender(id),
	CONSTRAINT pk_msgdiag_appender_prop PRIMARY KEY (id)
);

CREATE TABLE msgdiag_appender_prop_init_seq (id BIGINT);
INSERT INTO msgdiag_appender_prop_init_seq VALUES (NEXT VALUE FOR seq_msgdiag_appender_prop);



-- **** Tracciamento ****

CREATE SEQUENCE seq_tracce_appender AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE tracce_appender
(
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_appender PRIMARY KEY (id)
);

CREATE TABLE tracce_appender_init_seq (id BIGINT);
INSERT INTO tracce_appender_init_seq VALUES (NEXT VALUE FOR seq_tracce_appender);



CREATE SEQUENCE seq_tracce_appender_prop AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE tracce_appender_prop
(
	id_appender BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT uniq_tracce_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES tracce_appender(id),
	CONSTRAINT pk_tracce_appender_prop PRIMARY KEY (id)
);

CREATE TABLE tracce_appender_prop_init_seq (id BIGINT);
INSERT INTO tracce_appender_prop_init_seq VALUES (NEXT VALUE FOR seq_tracce_appender_prop);



CREATE SEQUENCE seq_tracce_config AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE tracce_config
(
	proprietario VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	id_proprietario BIGINT NOT NULL,
	stato VARCHAR(255),
	filtro_esiti VARCHAR(255),
	request_in VARCHAR(255),
	request_out VARCHAR(255),
	response_out VARCHAR(255),
	response_out_complete VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_tracce_config_1 ON tracce_config (proprietario,tipo);
CREATE TABLE tracce_config_init_seq (id BIGINT);
INSERT INTO tracce_config_init_seq VALUES (NEXT VALUE FOR seq_tracce_config);



CREATE SEQUENCE seq_filetrace_config AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE filetrace_config
(
	proprietario VARCHAR(255) NOT NULL,
	id_proprietario BIGINT NOT NULL,
	config VARCHAR(255),
	dump_in_stato VARCHAR(255),
	dump_in_stato_hdr VARCHAR(255),
	dump_in_stato_body VARCHAR(255),
	dump_out_stato VARCHAR(255),
	dump_out_stato_hdr VARCHAR(255),
	dump_out_stato_body VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_filetrace_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_filetrace_config_1 ON filetrace_config (proprietario);
CREATE TABLE filetrace_config_init_seq (id BIGINT);
INSERT INTO filetrace_config_init_seq VALUES (NEXT VALUE FOR seq_filetrace_config);



-- **** Dump ****

CREATE SEQUENCE seq_dump_config AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE dump_config
(
	proprietario VARCHAR(255) NOT NULL,
	id_proprietario BIGINT NOT NULL,
	dump_realtime VARCHAR(255),
	id_richiesta_ingresso BIGINT NOT NULL,
	id_richiesta_uscita BIGINT NOT NULL,
	id_risposta_ingresso BIGINT NOT NULL,
	id_risposta_uscita BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_config_1 ON dump_config (proprietario);
CREATE TABLE dump_config_init_seq (id BIGINT);
INSERT INTO dump_config_init_seq VALUES (NEXT VALUE FOR seq_dump_config);



CREATE SEQUENCE seq_dump_config_regola AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE dump_config_regola
(
	payload VARCHAR(255) NOT NULL,
	payload_parsing VARCHAR(255) NOT NULL,
	body VARCHAR(255) NOT NULL,
	attachments VARCHAR(255) NOT NULL,
	headers VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_config_regola PRIMARY KEY (id)
);

CREATE TABLE dump_config_regola_init_seq (id BIGINT);
INSERT INTO dump_config_regola_init_seq VALUES (NEXT VALUE FOR seq_dump_config_regola);



CREATE SEQUENCE seq_dump_appender AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE dump_appender
(
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_appender PRIMARY KEY (id)
);

CREATE TABLE dump_appender_init_seq (id BIGINT);
INSERT INTO dump_appender_init_seq VALUES (NEXT VALUE FOR seq_dump_appender);



CREATE SEQUENCE seq_dump_appender_prop AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE dump_appender_prop
(
	id_appender BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT uniq_dump_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES dump_appender(id),
	CONSTRAINT pk_dump_appender_prop PRIMARY KEY (id)
);

CREATE TABLE dump_appender_prop_init_seq (id BIGINT);
INSERT INTO dump_appender_prop_init_seq VALUES (NEXT VALUE FOR seq_dump_appender_prop);



-- **** Datasources dove reperire i messaggi diagnostici salvati ****

CREATE SEQUENCE seq_msgdiag_ds AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE msgdiag_ds
(
	nome VARCHAR(255) NOT NULL,
	nome_jndi VARCHAR(255) NOT NULL,
	tipo_database VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_msgdiag_ds_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_ds PRIMARY KEY (id)
);

CREATE TABLE msgdiag_ds_init_seq (id BIGINT);
INSERT INTO msgdiag_ds_init_seq VALUES (NEXT VALUE FOR seq_msgdiag_ds);



CREATE SEQUENCE seq_msgdiag_ds_prop AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE msgdiag_ds_prop
(
	id_prop BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_msgdiag_ds_prop_1 UNIQUE (id_prop,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_ds_prop_1 FOREIGN KEY (id_prop) REFERENCES msgdiag_ds(id),
	CONSTRAINT pk_msgdiag_ds_prop PRIMARY KEY (id)
);

CREATE TABLE msgdiag_ds_prop_init_seq (id BIGINT);
INSERT INTO msgdiag_ds_prop_init_seq VALUES (NEXT VALUE FOR seq_msgdiag_ds_prop);



-- **** Datasources dove reperire le tracce salvate ****

CREATE SEQUENCE seq_tracce_ds AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE tracce_ds
(
	nome VARCHAR(255) NOT NULL,
	nome_jndi VARCHAR(255) NOT NULL,
	tipo_database VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_tracce_ds_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_ds PRIMARY KEY (id)
);

CREATE TABLE tracce_ds_init_seq (id BIGINT);
INSERT INTO tracce_ds_init_seq VALUES (NEXT VALUE FOR seq_tracce_ds);



CREATE SEQUENCE seq_tracce_ds_prop AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE tracce_ds_prop
(
	id_prop BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_tracce_ds_prop_1 UNIQUE (id_prop,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_ds_prop_1 FOREIGN KEY (id_prop) REFERENCES tracce_ds(id),
	CONSTRAINT pk_tracce_ds_prop PRIMARY KEY (id)
);

CREATE TABLE tracce_ds_prop_init_seq (id BIGINT);
INSERT INTO tracce_ds_prop_init_seq VALUES (NEXT VALUE FOR seq_tracce_ds_prop);



-- **** Stato dei servizi attivi sulla Porta di Dominio ****

CREATE SEQUENCE seq_servizi_pdd AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE servizi_pdd
(
	componente VARCHAR(255) NOT NULL,
	-- Stato dei servizi attivi sulla Porta di Dominio
	stato INT,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_servizi_pdd_1 UNIQUE (componente),
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_pdd PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_servizi_pdd_1 ON servizi_pdd (componente);

ALTER TABLE servizi_pdd ALTER COLUMN stato SET DEFAULT 1;

CREATE TABLE servizi_pdd_init_seq (id BIGINT);
INSERT INTO servizi_pdd_init_seq VALUES (NEXT VALUE FOR seq_servizi_pdd);



CREATE SEQUENCE seq_servizi_pdd_filtri AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE servizi_pdd_filtri
(
	id_servizio_pdd BIGINT NOT NULL,
	tipo_filtro VARCHAR(255) NOT NULL,
	tipo_soggetto_fruitore VARCHAR(255),
	soggetto_fruitore VARCHAR(255),
	identificativo_porta_fruitore VARCHAR(255),
	tipo_soggetto_erogatore VARCHAR(255),
	soggetto_erogatore VARCHAR(255),
	identificativo_porta_erogatore VARCHAR(255),
	tipo_servizio VARCHAR(255),
	servizio VARCHAR(255),
	versione_servizio INT,
	azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- check constraints
	CONSTRAINT chk_servizi_pdd_filtri_1 CHECK (tipo_filtro IN ('abilitazione','disabilitazione')),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_pdd_filtri_1 FOREIGN KEY (id_servizio_pdd) REFERENCES servizi_pdd(id),
	CONSTRAINT pk_servizi_pdd_filtri PRIMARY KEY (id)
);

CREATE TABLE servizi_pdd_filtri_init_seq (id BIGINT);
INSERT INTO servizi_pdd_filtri_init_seq VALUES (NEXT VALUE FOR seq_servizi_pdd_filtri);



-- **** PddSystemProperties ****

CREATE SEQUENCE seq_pdd_sys_props AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE pdd_sys_props
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	enc_value VARCHAR(65535),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_pdd_sys_props_1 UNIQUE (nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd_sys_props PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_pdd_sys_props_1 ON pdd_sys_props (nome,valore);
CREATE TABLE pdd_sys_props_init_seq (id BIGINT);
INSERT INTO pdd_sys_props_init_seq VALUES (NEXT VALUE FOR seq_pdd_sys_props);



-- **** Proprieta Generiche ****

CREATE SEQUENCE seq_generic_properties AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE generic_properties
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000),
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	utente_richiedente VARCHAR(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_generic_properties_1 UNIQUE (tipologia,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_generic_properties PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_generic_properties_1 ON generic_properties (tipologia,nome);
CREATE TABLE generic_properties_init_seq (id BIGINT);
INSERT INTO generic_properties_init_seq VALUES (NEXT VALUE FOR seq_generic_properties);



CREATE SEQUENCE seq_generic_property AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE generic_property
(
	id_props BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	enc_value VARCHAR(65535),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_generic_property_1 UNIQUE (id_props,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_generic_property_1 FOREIGN KEY (id_props) REFERENCES generic_properties(id),
	CONSTRAINT pk_generic_property PRIMARY KEY (id)
);

-- index
CREATE INDEX index_generic_property_1 ON generic_property (id_props);
CREATE TABLE generic_property_init_seq (id BIGINT);
INSERT INTO generic_property_init_seq VALUES (NEXT VALUE FOR seq_generic_property);



-- **** Canali ****

CREATE SEQUENCE seq_canali_configurazione AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE canali_configurazione
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canale_default INT NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_canali_configurazione_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_configurazione PRIMARY KEY (id)
);

CREATE TABLE canali_configurazione_init_seq (id BIGINT);
INSERT INTO canali_configurazione_init_seq VALUES (NEXT VALUE FOR seq_canali_configurazione);



CREATE SEQUENCE seq_canali_nodi AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE canali_nodi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canali VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_canali_nodi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_nodi PRIMARY KEY (id)
);

CREATE TABLE canali_nodi_init_seq (id BIGINT);
INSERT INTO canali_nodi_init_seq VALUES (NEXT VALUE FOR seq_canali_nodi);



-- **** Regitro Plugins ****

CREATE SEQUENCE seq_registro_plugins AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE registro_plugins
(
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	descrizione VARCHAR(255),
	data TIMESTAMP NOT NULL,
	compatibilita VARCHAR(65535),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_registro_plugins_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registro_plugins PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_registro_plugins_1 ON registro_plugins (nome);

ALTER TABLE registro_plugins ALTER COLUMN data SET DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE registro_plugins_init_seq (id BIGINT);
INSERT INTO registro_plugins_init_seq VALUES (NEXT VALUE FOR seq_registro_plugins);



CREATE SEQUENCE seq_registro_plug_jar AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE registro_plug_jar
(
	id_plugin BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	sorgente VARCHAR(255) NOT NULL,
	-- In hsql 2.x usare il tipo BLOB al posto di VARBINARY
	contenuto VARBINARY(1073741823),
	url VARCHAR(4000),
	dir VARCHAR(4000),
	data TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_registro_plug_jar_1 UNIQUE (id_plugin,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_registro_plug_jar_1 FOREIGN KEY (id_plugin) REFERENCES registro_plugins(id),
	CONSTRAINT pk_registro_plug_jar PRIMARY KEY (id)
);

-- index
CREATE INDEX index_registro_plug_jar_1 ON registro_plug_jar (id_plugin);

ALTER TABLE registro_plug_jar ALTER COLUMN data SET DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE registro_plug_jar_init_seq (id BIGINT);
INSERT INTO registro_plug_jar_init_seq VALUES (NEXT VALUE FOR seq_registro_plug_jar);



-- **** Handlers ****

CREATE SEQUENCE seq_config_handlers AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE config_handlers
(
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_config_handlers_1 UNIQUE (tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_handlers PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_config_handlers_1 ON config_handlers (tipologia,tipo);
CREATE TABLE config_handlers_init_seq (id BIGINT);
INSERT INTO config_handlers_init_seq VALUES (NEXT VALUE FOR seq_config_handlers);



-- **** Nodi Runtime ****

CREATE SEQUENCE seq_nodi_runtime AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE nodi_runtime
(
	hostname VARCHAR(3000) NOT NULL,
	gruppo VARCHAR(3000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_refresh TIMESTAMP NOT NULL,
	id_numerico INT NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_nodi_runtime_1 ON nodi_runtime (hostname);
CREATE UNIQUE INDEX index_nodi_runtime_2 ON nodi_runtime (gruppo,id_numerico);
CREATE TABLE nodi_runtime_init_seq (id BIGINT);
INSERT INTO nodi_runtime_init_seq VALUES (NEXT VALUE FOR seq_nodi_runtime);



CREATE SEQUENCE seq_nodi_runtime_operations AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE nodi_runtime_operations
(
	descrizione VARCHAR(4000) NOT NULL,
	operazione VARCHAR(4000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_NODI_OP ON nodi_runtime_operations (data_registrazione);
CREATE TABLE nodi_runtime_operations_init_seq (id BIGINT);
INSERT INTO nodi_runtime_operations_init_seq VALUES (NEXT VALUE FOR seq_nodi_runtime_operations);



-- **** Keystore ****

CREATE SEQUENCE seq_remote_store AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE remote_store
(
	nome VARCHAR(255) NOT NULL,
	data_aggiornamento TIMESTAMP NOT NULL,
	last_event VARCHAR(4000),
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_remote_store_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_remote_store PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_remote_store_1 ON remote_store (nome);
CREATE TABLE remote_store_init_seq (id BIGINT);
INSERT INTO remote_store_init_seq VALUES (NEXT VALUE FOR seq_remote_store);



CREATE SEQUENCE seq_remote_store_key AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE remote_store_key
(
	id_remote_store BIGINT NOT NULL,
	kid VARCHAR(255) NOT NULL,
	-- In hsql 2.x usare il tipo BLOB al posto di VARBINARY
	content_key VARBINARY(1073741823) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_aggiornamento TIMESTAMP NOT NULL,
	client_id VARCHAR(255),
	client_details VARCHAR(65535),
	organization_details VARCHAR(65535),
	client_data_aggiornamento TIMESTAMP,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT unique_remote_store_key_1 UNIQUE (id_remote_store,kid),
	-- fk/pk keys constraints
	CONSTRAINT fk_remote_store_key_1 FOREIGN KEY (id_remote_store) REFERENCES remote_store(id),
	CONSTRAINT pk_remote_store_key PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_remote_store_key_1 ON remote_store_key (id_remote_store,kid);
CREATE INDEX REMOTE_STORE_UPDATE ON remote_store_key (data_aggiornamento);
CREATE INDEX REMOTE_STORE_CREATE ON remote_store_key (data_registrazione);
CREATE TABLE remote_store_key_init_seq (id BIGINT);
INSERT INTO remote_store_key_init_seq VALUES (NEXT VALUE FOR seq_remote_store_key);



-- **** Digests Servizi Params ****

CREATE SEQUENCE seq_servizi_digest_params AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE servizi_digest_params
(
	id_servizio BIGINT NOT NULL,
	serial_number BIGINT,
	data_registrazione TIMESTAMP NOT NULL,
	seed VARCHAR(4000) NOT NULL,
	algorithm VARCHAR(32) NOT NULL,
	lifetime INT NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_digest_params PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_REF_SERVIZI ON servizi_digest_params (id_servizio);
CREATE TABLE servizi_digest_params_init_seq (id BIGINT);
INSERT INTO servizi_digest_params_init_seq VALUES (NEXT VALUE FOR seq_servizi_digest_params);


