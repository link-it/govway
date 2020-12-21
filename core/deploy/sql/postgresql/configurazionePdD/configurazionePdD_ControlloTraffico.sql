-- CONTROLLO TRAFFICO

CREATE SEQUENCE seq_ct_config start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE ct_config
(
	-- Numero Massimo Richieste Simultanee
	max_threads_enabled BOOLEAN NOT NULL DEFAULT true,
	max_threads_warning_only BOOLEAN NOT NULL DEFAULT false,
	max_threads BIGINT NOT NULL,
	max_threads_tipo_errore VARCHAR(255) NOT NULL DEFAULT 'fault',
	max_threads_includi_errore BOOLEAN NOT NULL DEFAULT true,
	-- Controllo della Congestione
	cc_enabled BOOLEAN NOT NULL DEFAULT false,
	cc_threshold INT,
	-- Tempi di Risposta Fruizione
	pd_connection_timeout INT,
	pd_read_timeout INT,
	pd_avg_time INT,
	-- Tempi di Risposta Erogazione
	pa_connection_timeout INT,
	pa_read_timeout INT,
	pa_avg_time INT,
	-- Rate Limiting
	rt_tipo_errore VARCHAR(255) NOT NULL DEFAULT 'fault',
	rt_includi_errore BOOLEAN NOT NULL DEFAULT true,
	-- Cache
	cache BOOLEAN NOT NULL DEFAULT true,
	cache_size BIGINT,
	cache_algorithm VARCHAR(255),
	cache_idle_time BIGINT,
	cache_life_time BIGINT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_ct_config') NOT NULL,
	-- check constraints
	CONSTRAINT chk_ct_config_1 CHECK (cache_algorithm IN ('LRU','MRU')),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_config PRIMARY KEY (id)
);




CREATE SEQUENCE seq_ct_config_policy start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE ct_config_policy
(
	-- Dati Generali
	policy_id VARCHAR(255) NOT NULL,
	policy_built_in BOOLEAN NOT NULL DEFAULT false,
	rt_descrizione TEXT NOT NULL,
	rt_risorsa VARCHAR(255) NOT NULL,
	-- Valori di Soglia
	rt_simultanee BOOLEAN NOT NULL DEFAULT false,
	rt_valore BIGINT,
	rt_bytes_type VARCHAR(255),
	rt_latency_type VARCHAR(255),
	rt_modalita_controllo VARCHAR(255),
	rt_interval_type_real VARCHAR(255),
	rt_interval_type_stat VARCHAR(255),
	rt_interval INT,
	rt_finestra VARCHAR(255),
	-- Applicabilità
	rt_applicabilita VARCHAR(255) NOT NULL DEFAULT 'sempre',
	-- Applicabilità con Congestione in Corso
	rt_applicabilita_con_cc BOOLEAN NOT NULL DEFAULT false,
	-- Applicabilità con Degrado Prestazionale
	rt_applicabilita_degrado BOOLEAN NOT NULL DEFAULT false,
	degrato_modalita_controllo VARCHAR(255),
	degrado_avg_interval_type_real VARCHAR(255),
	degrado_avg_interval_type_stat VARCHAR(255),
	degrado_avg_interval INT,
	degrado_avg_finestra VARCHAR(255),
	degrado_avg_latency_type VARCHAR(255),
	-- Applicabilità con Stato Allarme
	rt_applicabilita_allarme BOOLEAN NOT NULL DEFAULT false,
	allarme_nome VARCHAR(255),
	allarme_stato INT,
	allarme_not_stato BOOLEAN NOT NULL DEFAULT false,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_ct_config_policy') NOT NULL,
	-- check constraints
	CONSTRAINT chk_cong_gen_policy_1 CHECK (rt_bytes_type IN ('complessiva','interna','esterna')),
	CONSTRAINT chk_cong_gen_policy_2 CHECK (rt_latency_type IN ('servizio','porta','totale')),
	CONSTRAINT chk_cong_gen_policy_3 CHECK (rt_modalita_controllo IN ('realtime','statistic')),
	CONSTRAINT chk_cong_gen_policy_4 CHECK (rt_interval_type_real IN ('secondi','minuti','orario','giornaliero')),
	CONSTRAINT chk_cong_gen_policy_5 CHECK (rt_interval_type_stat IN ('mensile','settimanale','giornaliero','orario')),
	CONSTRAINT chk_cong_gen_policy_6 CHECK (rt_finestra IN ('corrente','precedente','scorrevole')),
	CONSTRAINT chk_cong_gen_policy_7 CHECK (rt_applicabilita IN ('sempre','condizionale')),
	CONSTRAINT chk_cong_gen_policy_8 CHECK (degrato_modalita_controllo IN ('realtime','statistic')),
	CONSTRAINT chk_cong_gen_policy_9 CHECK (degrado_avg_interval_type_real IN ('secondi','minuti','orario','giornaliero')),
	CONSTRAINT chk_cong_gen_policy_10 CHECK (degrado_avg_interval_type_stat IN ('mensile','settimanale','giornaliero','orario')),
	CONSTRAINT chk_cong_gen_policy_11 CHECK (degrado_avg_finestra IN ('corrente','precedente','scorrevole')),
	CONSTRAINT chk_cong_gen_policy_12 CHECK (degrado_avg_latency_type IN ('servizio','porta','totale')),
	-- unique constraints
	CONSTRAINT uniq_cong_gen_policy_1 UNIQUE (policy_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_config_policy PRIMARY KEY (id)
);




CREATE SEQUENCE seq_ct_active_policy start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE ct_active_policy
(
	-- Dati Generali
	active_policy_id VARCHAR(255) NOT NULL,
	policy_alias VARCHAR(255),
	policy_update_time TIMESTAMP NOT NULL,
	policy_posizione INT NOT NULL,
	policy_continue BOOLEAN NOT NULL DEFAULT false,
	policy_id VARCHAR(255) NOT NULL,
	policy_enabled BOOLEAN NOT NULL,
	policy_warning BOOLEAN NOT NULL DEFAULT false,
	-- Valori di Soglia
	policy_redefined BOOLEAN NOT NULL,
	policy_valore BIGINT,
	-- Filtro
	filtro_enabled BOOLEAN NOT NULL DEFAULT false,
	filtro_protocollo VARCHAR(255),
	filtro_ruolo VARCHAR(255),
	filtro_porta VARCHAR(2000),
	filtro_tipo_fruitore VARCHAR(255),
	filtro_nome_fruitore VARCHAR(255),
	filtro_ruolo_fruitore VARCHAR(255),
	filtro_sa_fruitore VARCHAR(255),
	filtro_tipo_erogatore VARCHAR(255),
	filtro_nome_erogatore VARCHAR(255),
	filtro_ruolo_erogatore VARCHAR(255),
	filtro_sa_erogatore VARCHAR(255),
	filtro_tag VARCHAR(255),
	filtro_tipo_servizio VARCHAR(255),
	filtro_nome_servizio VARCHAR(255),
	filtro_versione_servizio INT,
	filtro_azione TEXT,
	-- Filtro per Chiave Applicativa
	filtro_key_enabled BOOLEAN NOT NULL DEFAULT false,
	filtro_key_type VARCHAR(255),
	filtro_key_name TEXT,
	filtro_key_value TEXT,
	-- Raggruppamento
	group_enabled BOOLEAN NOT NULL DEFAULT false,
	group_ruolo BOOLEAN NOT NULL DEFAULT false,
	group_protocollo BOOLEAN NOT NULL DEFAULT false,
	group_fruitore BOOLEAN NOT NULL DEFAULT false,
	group_sa_fruitore BOOLEAN NOT NULL DEFAULT false,
	group_id_autenticato BOOLEAN NOT NULL DEFAULT false,
	group_token TEXT,
	group_erogatore BOOLEAN NOT NULL DEFAULT false,
	group_sa_erogatore BOOLEAN NOT NULL DEFAULT false,
	group_servizio BOOLEAN NOT NULL DEFAULT false,
	group_azione BOOLEAN NOT NULL DEFAULT false,
	-- Raggruppamento per Chiave Applicativa
	group_key_enabled BOOLEAN NOT NULL DEFAULT false,
	group_key_type VARCHAR(255),
	group_key_name TEXT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_ct_active_policy') NOT NULL,
	-- check constraints
	CONSTRAINT chk_cong_att_policy_1 CHECK (filtro_ruolo IN ('delegata','applicativa','entrambi')),
	-- unique constraints
	CONSTRAINT uniq_cong_att_policy_1 UNIQUE (active_policy_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_active_policy PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_cong_att_policy_1 ON ct_active_policy (filtro_ruolo,filtro_porta);


