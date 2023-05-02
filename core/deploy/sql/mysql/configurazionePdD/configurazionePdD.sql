-- **** Configurazione ****

CREATE TABLE registri
(
	nome VARCHAR(255) NOT NULL,
	location VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	utente VARCHAR(255),
	password VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_registri_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registri PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




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
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_routing PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE config_cache_regole
(
	status_min INT,
	status_max INT,
	fault INT DEFAULT 0,
	cache_seconds INT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_config_cache_regole PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




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
	tracciamento_esiti VARCHAR(255),
	-- Transazione
	transazioni_tempi VARCHAR(255),
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
	cors_allow_origins TEXT,
	cors_allow_headers TEXT,
	cors_allow_methods TEXT,
	cors_allow_expose_headers TEXT,
	-- Response caching
	response_cache_stato VARCHAR(255),
	response_cache_seconds INT,
	response_cache_max_msg_size BIGINT,
	response_cache_hash_url VARCHAR(255),
	response_cache_hash_query VARCHAR(255),
	response_cache_hash_query_list TEXT,
	response_cache_hash_headers VARCHAR(255),
	response_cache_hash_hdr_list TEXT,
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
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_configurazione PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** URLInvocazione ****

CREATE TABLE config_url_invocazione
(
	base_url VARCHAR(255) NOT NULL,
	base_url_fruizione VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_invocazione PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE config_url_regole
(
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	descrizione TEXT,
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
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_config_url_regole_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_regole PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_config_url_regole_1 ON config_url_regole (nome);



-- **** Messaggi diagnostici Appender ****

CREATE TABLE msgdiag_appender
(
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_appender PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE msgdiag_appender_prop
(
	id_appender BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_msgdiag_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES msgdiag_appender(id),
	CONSTRAINT pk_msgdiag_appender_prop PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** Tracciamento Appender ****

CREATE TABLE tracce_appender
(
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_appender PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE tracce_appender_prop
(
	id_appender BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_tracce_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES tracce_appender(id),
	CONSTRAINT pk_tracce_appender_prop PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** Dump Appender ****

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
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_config PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX index_dump_config_1 ON dump_config (proprietario);



CREATE TABLE dump_config_regola
(
	payload VARCHAR(255) NOT NULL,
	payload_parsing VARCHAR(255) NOT NULL,
	body VARCHAR(255) NOT NULL,
	attachments VARCHAR(255) NOT NULL,
	headers VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_config_regola PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE dump_appender
(
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_appender PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE dump_appender_prop
(
	id_appender BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_dump_app_prop_1 UNIQUE (id_appender,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_appender_prop_1 FOREIGN KEY (id_appender) REFERENCES dump_appender(id),
	CONSTRAINT pk_dump_appender_prop PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** Datasources dove reperire i messaggi diagnostici salvati ****

CREATE TABLE msgdiag_ds
(
	nome VARCHAR(255) NOT NULL,
	nome_jndi VARCHAR(255) NOT NULL,
	tipo_database VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_msgdiag_ds_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_ds PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE msgdiag_ds_prop
(
	id_prop BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_msgdiag_ds_prop_1 UNIQUE (id_prop,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_ds_prop_1 FOREIGN KEY (id_prop) REFERENCES msgdiag_ds(id),
	CONSTRAINT pk_msgdiag_ds_prop PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** Datasources dove reperire le tracce salvate ****

CREATE TABLE tracce_ds
(
	nome VARCHAR(255) NOT NULL,
	nome_jndi VARCHAR(255) NOT NULL,
	tipo_database VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_tracce_ds_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_ds PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE tracce_ds_prop
(
	id_prop BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_tracce_ds_prop_1 UNIQUE (id_prop,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_ds_prop_1 FOREIGN KEY (id_prop) REFERENCES tracce_ds(id),
	CONSTRAINT pk_tracce_ds_prop PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** Stato dei servizi attivi sulla Porta di Dominio ****

CREATE TABLE servizi_pdd
(
	componente VARCHAR(255) NOT NULL,
	-- Stato dei servizi attivi sulla Porta di Dominio
	stato INT DEFAULT 1,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_servizi_pdd_1 UNIQUE (componente),
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_pdd PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_servizi_pdd_1 ON servizi_pdd (componente);



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
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_servizi_pdd_filtri_1 CHECK (tipo_filtro IN ('abilitazione','disabilitazione')),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_pdd_filtri_1 FOREIGN KEY (id_servizio_pdd) REFERENCES servizi_pdd(id),
	CONSTRAINT pk_servizi_pdd_filtri PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** PddSystemProperties ****

CREATE TABLE pdd_sys_props
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pdd_sys_props_1 UNIQUE (nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd_sys_props PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pdd_sys_props_1 ON pdd_sys_props (nome,valore);



-- **** Proprieta Generiche ****

CREATE TABLE generic_properties
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_generic_properties_1 UNIQUE (tipologia,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_generic_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_generic_properties_1 ON generic_properties (tipologia,nome);



CREATE TABLE generic_property
(
	id_props BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_generic_property_1 UNIQUE (id_props,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_generic_property_1 FOREIGN KEY (id_props) REFERENCES generic_properties(id),
	CONSTRAINT pk_generic_property PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX index_generic_property_1 ON generic_property (id_props);



-- **** Canali ****

CREATE TABLE canali_configurazione
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canale_default INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_canali_configurazione_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_configurazione PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE canali_nodi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canali VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_canali_nodi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_nodi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- **** Regitro Plugins ****

CREATE TABLE registro_plugins
(
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	descrizione VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	compatibilita TEXT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_registro_plugins_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registro_plugins PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_registro_plugins_1 ON registro_plugins (nome);



CREATE TABLE registro_plug_jar
(
	id_plugin BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	sorgente VARCHAR(255) NOT NULL,
	contenuto LONGBLOB,
	url VARCHAR(4000),
	dir VARCHAR(4000),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_registro_plug_jar_1 UNIQUE (id_plugin,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_registro_plug_jar_1 FOREIGN KEY (id_plugin) REFERENCES registro_plugins(id),
	CONSTRAINT pk_registro_plug_jar PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX index_registro_plug_jar_1 ON registro_plug_jar (id_plugin);



-- **** Handlers ****

CREATE TABLE config_handlers
(
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_config_handlers_1 UNIQUE (tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_handlers PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_config_handlers_1 ON config_handlers (tipologia,tipo);



-- **** Nodi Runtime ****

CREATE TABLE nodi_runtime
(
	hostname VARCHAR(3000) NOT NULL,
	gruppo VARCHAR(3000) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_refresh TIMESTAMP(3) NOT NULL DEFAULT 0,
	id_numerico INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_nodi_runtime_1 ON nodi_runtime (hostname);
CREATE UNIQUE INDEX index_nodi_runtime_2 ON nodi_runtime (gruppo,id_numerico);



-- **** Keystore ****

CREATE TABLE remote_store
(
	nome VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_remote_store_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_remote_store PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_remote_store_1 ON remote_store (nome);



CREATE TABLE remote_store_key
(
	id_remote_store BIGINT NOT NULL,
	kid VARCHAR(255) NOT NULL,
	key LONGBLOB NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_remote_store_key_1 UNIQUE (id_remote_store,kid),
	-- fk/pk keys constraints
	CONSTRAINT fk_remote_store_key_1 FOREIGN KEY (id_remote_store) REFERENCES remote_store(id),
	CONSTRAINT pk_remote_store_key PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_remote_store_key_1 ON remote_store_key (id_remote_store,kid);


