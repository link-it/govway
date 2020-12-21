-- CONTROLLO TRAFFICO

CREATE SEQUENCE seq_ct_config MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ct_config
(
	-- Numero Massimo Richieste Simultanee
	max_threads_enabled NUMBER NOT NULL,
	max_threads_warning_only NUMBER NOT NULL,
	max_threads NUMBER NOT NULL,
	max_threads_tipo_errore VARCHAR2(255) NOT NULL,
	max_threads_includi_errore NUMBER NOT NULL,
	-- Controllo della Congestione
	cc_enabled NUMBER NOT NULL,
	cc_threshold NUMBER,
	-- Tempi di Risposta Fruizione
	pd_connection_timeout NUMBER,
	pd_read_timeout NUMBER,
	pd_avg_time NUMBER,
	-- Tempi di Risposta Erogazione
	pa_connection_timeout NUMBER,
	pa_read_timeout NUMBER,
	pa_avg_time NUMBER,
	-- Rate Limiting
	rt_tipo_errore VARCHAR2(255) NOT NULL,
	rt_includi_errore NUMBER NOT NULL,
	-- Cache
	cache NUMBER NOT NULL,
	cache_size NUMBER,
	cache_algorithm VARCHAR2(255),
	cache_idle_time NUMBER,
	cache_life_time NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_ct_config_1 CHECK (cache_algorithm IN ('LRU','MRU')),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_config PRIMARY KEY (id)
);


ALTER TABLE ct_config MODIFY max_threads_enabled DEFAULT 1;
ALTER TABLE ct_config MODIFY max_threads_warning_only DEFAULT 0;
ALTER TABLE ct_config MODIFY max_threads_tipo_errore DEFAULT 'fault';
ALTER TABLE ct_config MODIFY max_threads_includi_errore DEFAULT 1;
ALTER TABLE ct_config MODIFY cc_enabled DEFAULT 0;
ALTER TABLE ct_config MODIFY rt_tipo_errore DEFAULT 'fault';
ALTER TABLE ct_config MODIFY rt_includi_errore DEFAULT 1;
ALTER TABLE ct_config MODIFY cache DEFAULT 1;

CREATE TRIGGER trg_ct_config
BEFORE
insert on ct_config
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ct_config.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_ct_config_policy MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ct_config_policy
(
	-- Dati Generali
	policy_id VARCHAR2(255) NOT NULL,
	policy_built_in NUMBER NOT NULL,
	rt_descrizione CLOB NOT NULL,
	rt_risorsa VARCHAR2(255) NOT NULL,
	-- Valori di Soglia
	rt_simultanee NUMBER NOT NULL,
	rt_valore NUMBER,
	rt_bytes_type VARCHAR2(255),
	rt_latency_type VARCHAR2(255),
	rt_modalita_controllo VARCHAR2(255),
	rt_interval_type_real VARCHAR2(255),
	rt_interval_type_stat VARCHAR2(255),
	rt_interval NUMBER,
	rt_finestra VARCHAR2(255),
	-- Applicabilità
	rt_applicabilita VARCHAR2(255) NOT NULL,
	-- Applicabilità con Congestione in Corso
	rt_applicabilita_con_cc NUMBER NOT NULL,
	-- Applicabilità con Degrado Prestazionale
	rt_applicabilita_degrado NUMBER NOT NULL,
	degrato_modalita_controllo VARCHAR2(255),
	degrado_avg_interval_type_real VARCHAR2(255),
	degrado_avg_interval_type_stat VARCHAR2(255),
	degrado_avg_interval NUMBER,
	degrado_avg_finestra VARCHAR2(255),
	degrado_avg_latency_type VARCHAR2(255),
	-- Applicabilità con Stato Allarme
	rt_applicabilita_allarme NUMBER NOT NULL,
	allarme_nome VARCHAR2(255),
	allarme_stato NUMBER,
	allarme_not_stato NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
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


ALTER TABLE ct_config_policy MODIFY policy_built_in DEFAULT 0;
ALTER TABLE ct_config_policy MODIFY rt_simultanee DEFAULT 0;
ALTER TABLE ct_config_policy MODIFY rt_applicabilita DEFAULT 'sempre';
ALTER TABLE ct_config_policy MODIFY rt_applicabilita_con_cc DEFAULT 0;
ALTER TABLE ct_config_policy MODIFY rt_applicabilita_degrado DEFAULT 0;
ALTER TABLE ct_config_policy MODIFY rt_applicabilita_allarme DEFAULT 0;
ALTER TABLE ct_config_policy MODIFY allarme_not_stato DEFAULT 0;

CREATE TRIGGER trg_ct_config_policy
BEFORE
insert on ct_config_policy
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ct_config_policy.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_ct_active_policy MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ct_active_policy
(
	-- Dati Generali
	active_policy_id VARCHAR2(255) NOT NULL,
	policy_alias VARCHAR2(255),
	policy_update_time TIMESTAMP NOT NULL,
	policy_posizione NUMBER NOT NULL,
	policy_continue NUMBER NOT NULL,
	policy_id VARCHAR2(255) NOT NULL,
	policy_enabled NUMBER NOT NULL,
	policy_warning NUMBER NOT NULL,
	-- Valori di Soglia
	policy_redefined NUMBER NOT NULL,
	policy_valore NUMBER,
	-- Filtro
	filtro_enabled NUMBER NOT NULL,
	filtro_protocollo VARCHAR2(255),
	filtro_ruolo VARCHAR2(255),
	filtro_porta VARCHAR2(2000),
	filtro_tipo_fruitore VARCHAR2(255),
	filtro_nome_fruitore VARCHAR2(255),
	filtro_ruolo_fruitore VARCHAR2(255),
	filtro_sa_fruitore VARCHAR2(255),
	filtro_tipo_erogatore VARCHAR2(255),
	filtro_nome_erogatore VARCHAR2(255),
	filtro_ruolo_erogatore VARCHAR2(255),
	filtro_sa_erogatore VARCHAR2(255),
	filtro_tag VARCHAR2(255),
	filtro_tipo_servizio VARCHAR2(255),
	filtro_nome_servizio VARCHAR2(255),
	filtro_versione_servizio NUMBER,
	filtro_azione CLOB,
	-- Filtro per Chiave Applicativa
	filtro_key_enabled NUMBER NOT NULL,
	filtro_key_type VARCHAR2(255),
	filtro_key_name CLOB,
	filtro_key_value CLOB,
	-- Raggruppamento
	group_enabled NUMBER NOT NULL,
	group_ruolo NUMBER NOT NULL,
	group_protocollo NUMBER NOT NULL,
	group_fruitore NUMBER NOT NULL,
	group_sa_fruitore NUMBER NOT NULL,
	group_id_autenticato NUMBER NOT NULL,
	group_token CLOB,
	group_erogatore NUMBER NOT NULL,
	group_sa_erogatore NUMBER NOT NULL,
	group_servizio NUMBER NOT NULL,
	group_azione NUMBER NOT NULL,
	-- Raggruppamento per Chiave Applicativa
	group_key_enabled NUMBER NOT NULL,
	group_key_type VARCHAR2(255),
	group_key_name CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_cong_att_policy_1 CHECK (filtro_ruolo IN ('delegata','applicativa','entrambi')),
	-- unique constraints
	CONSTRAINT uniq_cong_att_policy_1 UNIQUE (active_policy_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_active_policy PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_cong_att_policy_1 ON ct_active_policy (filtro_ruolo,filtro_porta);

ALTER TABLE ct_active_policy MODIFY policy_continue DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY policy_warning DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY filtro_enabled DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY filtro_key_enabled DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_enabled DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_ruolo DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_protocollo DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_fruitore DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_sa_fruitore DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_id_autenticato DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_erogatore DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_sa_erogatore DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_servizio DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_azione DEFAULT 0;
ALTER TABLE ct_active_policy MODIFY group_key_enabled DEFAULT 0;

CREATE TRIGGER trg_ct_active_policy
BEFORE
insert on ct_active_policy
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ct_active_policy.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


