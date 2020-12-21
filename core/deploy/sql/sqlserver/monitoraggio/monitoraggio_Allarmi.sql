-- ALLARMI

CREATE TABLE allarmi
(
	-- Informazioni generiche
	nome VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	tipo_allarme VARCHAR(255) NOT NULL DEFAULT 'ATTIVO',
	-- Stato allarme (0=OK, 1=Warning, 2=Allarme)
	stato_precedente INT NOT NULL,
	stato INT NOT NULL,
	stato_dettaglio VARCHAR(max),
	-- Tempi di creazione/esecuzione allarmi
	lasttimestamp_create DATETIME2 NOT NULL,
	lasttimestamp_update DATETIME2,
	-- Informazioni generali (0=false/1=true)
	enabled INT NOT NULL,
	acknowledged INT NOT NULL,
	periodo_tipo VARCHAR(255),
	periodo INT,
	-- Informazioni sull'invio di e-mail
	mail_ack_mode INT,
	mail_invia_warning INT,
	mail_invia_alert INT,
	mail_destinatari VARCHAR(max),
	mail_subject VARCHAR(255),
	mail_body VARCHAR(max),
	-- Informazioni sull'invocazione di script esterni
	script_ack_mode INT,
	script_invoke_warning INT,
	script_invoke_alert INT,
	script_command VARCHAR(max),
	script_args VARCHAR(max),
	-- Filtro
	filtro_enabled BIT NOT NULL DEFAULT 'false',
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
	filtro_azione VARCHAR(max),
	-- Raggruppamento
	group_enabled BIT NOT NULL DEFAULT 'false',
	group_ruolo BIT NOT NULL DEFAULT 'false',
	group_protocollo BIT NOT NULL DEFAULT 'false',
	group_fruitore BIT NOT NULL DEFAULT 'false',
	group_sa_fruitore BIT NOT NULL DEFAULT 'false',
	group_id_autenticato BIT NOT NULL DEFAULT 'false',
	group_token VARCHAR(max),
	group_erogatore BIT NOT NULL DEFAULT 'false',
	group_servizio BIT NOT NULL DEFAULT 'false',
	group_azione BIT NOT NULL DEFAULT 'false',
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_allarmi_1 CHECK (tipo_allarme IN ('ATTIVO','PASSIVO')),
	CONSTRAINT chk_allarmi_2 CHECK (filtro_ruolo IN ('delegata','applicativa','entrambi')),
	-- unique constraints
	CONSTRAINT uniq_allarmi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_allarmi PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_allarmi_1 ON allarmi (nome);
CREATE INDEX idx_allarmi_2 ON allarmi (filtro_ruolo,filtro_porta);



CREATE TABLE allarmi_parametri
(
	param_id VARCHAR(255) NOT NULL,
	param_value VARCHAR(max),
	-- fk/pk columns
	chk_param_id BIGINT IDENTITY,
	id_allarme BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_allarmi_param_1 UNIQUE (id_allarme,param_id),
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_parametri_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_parametri PRIMARY KEY (chk_param_id)
);

-- index
CREATE UNIQUE INDEX idx_allarmi_param_1 ON allarmi_parametri (id_allarme,param_id);



CREATE TABLE allarmi_history
(
	enabled INT NOT NULL,
	stato INT NOT NULL,
	stato_dettaglio VARCHAR(max),
	acknowledged INT NOT NULL,
	timestamp_update DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
	utente VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_allarme BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_history_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_history PRIMARY KEY (id)
);

-- index
CREATE INDEX index_allarmi_history_1 ON allarmi_history (id_allarme,timestamp_update DESC);


