-- ALLARMI

CREATE SEQUENCE seq_allarmi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE allarmi
(
	-- Informazioni generiche
	nome VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	tipo_allarme VARCHAR(255) NOT NULL DEFAULT 'ATTIVO',
	-- Stato allarme (0=OK, 1=Warning, 2=Allarme)
	stato_precedente INT NOT NULL,
	stato INT NOT NULL,
	stato_dettaglio TEXT,
	-- Tempi di creazione/esecuzione allarmi
	lasttimestamp_create TIMESTAMP NOT NULL,
	lasttimestamp_update TIMESTAMP,
	-- Informazioni generali (0=false/1=true)
	enabled INT NOT NULL,
	acknowledged INT NOT NULL,
	periodo_tipo VARCHAR(255),
	periodo INT,
	-- Informazioni sull'invio di e-mail
	mail_ack_mode INT,
	mail_invia_warning INT,
	mail_invia_alert INT,
	mail_destinatari TEXT,
	mail_subject VARCHAR(255),
	mail_body TEXT,
	-- Informazioni sull'invocazione di script esterni
	script_ack_mode INT,
	script_invoke_warning INT,
	script_invoke_alert INT,
	script_command TEXT,
	script_args TEXT,
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
	filtro_tag VARCHAR(255),
	filtro_tipo_servizio VARCHAR(255),
	filtro_nome_servizio VARCHAR(255),
	filtro_versione_servizio INT,
	filtro_azione TEXT,
	-- Raggruppamento
	group_enabled BOOLEAN NOT NULL DEFAULT false,
	group_ruolo BOOLEAN NOT NULL DEFAULT false,
	group_protocollo BOOLEAN NOT NULL DEFAULT false,
	group_fruitore BOOLEAN NOT NULL DEFAULT false,
	group_sa_fruitore BOOLEAN NOT NULL DEFAULT false,
	group_id_autenticato BOOLEAN NOT NULL DEFAULT false,
	group_token TEXT,
	group_erogatore BOOLEAN NOT NULL DEFAULT false,
	group_servizio BOOLEAN NOT NULL DEFAULT false,
	group_azione BOOLEAN NOT NULL DEFAULT false,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_allarmi') NOT NULL,
	-- check constraints
	CONSTRAINT chk_allarmi_1 CHECK (tipo_allarme IN ('ATTIVO','PASSIVO')),
	CONSTRAINT chk_allarmi_2 CHECK (filtro_ruolo IN ('delegata','applicativa','entrambi')),
	-- unique constraints
	CONSTRAINT uniq_allarmi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_allarmi PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_allarmi_1 ON allarmi (filtro_ruolo,filtro_porta);



CREATE SEQUENCE seq_allarmi_parametri start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE allarmi_parametri
(
	param_id VARCHAR(255) NOT NULL,
	param_value TEXT,
	-- fk/pk columns
	chk_param_id BIGINT DEFAULT nextval('seq_allarmi_parametri') NOT NULL,
	id_allarme BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_allarmi_param_1 UNIQUE (id_allarme,param_id),
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_parametri_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_parametri PRIMARY KEY (chk_param_id)
);




CREATE SEQUENCE seq_allarmi_history start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE allarmi_history
(
	enabled INT NOT NULL,
	stato INT NOT NULL,
	stato_dettaglio TEXT,
	acknowledged INT NOT NULL,
	timestamp_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	utente VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_allarmi_history') NOT NULL,
	id_allarme BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_history_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_history PRIMARY KEY (id)
);

-- index
CREATE INDEX index_allarmi_history_1 ON allarmi_history (id_allarme,timestamp_update DESC);


