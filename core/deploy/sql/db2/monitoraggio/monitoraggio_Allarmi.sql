-- ALLARMI

CREATE TABLE allarmi
(
	-- Informazioni generiche
	nome VARCHAR(275) NOT NULL,
	alias VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	tipo VARCHAR(255) NOT NULL,
	tipo_allarme VARCHAR(255) NOT NULL DEFAULT 'ATTIVO',
	-- Stato allarme (0=OK, 1=Warning, 2=Allarme)
	stato_precedente INT NOT NULL,
	stato INT NOT NULL,
	stato_dettaglio CLOB,
	-- Tempi di creazione/esecuzione allarmi
	lasttimestamp_create TIMESTAMP NOT NULL,
	lasttimestamp_update TIMESTAMP,
	-- Informazioni generali (0=false/1=true)
	enabled INT NOT NULL,
	acknowledged INT NOT NULL,
	periodo_tipo VARCHAR(255),
	periodo INT,
	mail_invia INT,
	-- Informazioni sull'invio di e-mail
	mail_invia_warning INT,
	mail_destinatari CLOB,
	mail_subject VARCHAR(255),
	mail_body CLOB,
	script_invoke INT,
	-- Informazioni sull'invocazione di script esterni
	script_invoke_warning INT,
	script_command CLOB,
	script_args CLOB,
	-- Filtro
	filtro_enabled SMALLINT NOT NULL DEFAULT 0,
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
	filtro_azione CLOB,
	-- Raggruppamento
	group_enabled SMALLINT NOT NULL DEFAULT 0,
	group_ruolo SMALLINT NOT NULL DEFAULT 0,
	group_protocollo SMALLINT NOT NULL DEFAULT 0,
	group_fruitore SMALLINT NOT NULL DEFAULT 0,
	group_sa_fruitore SMALLINT NOT NULL DEFAULT 0,
	group_id_autenticato SMALLINT NOT NULL DEFAULT 0,
	group_token CLOB,
	group_erogatore SMALLINT NOT NULL DEFAULT 0,
	group_servizio SMALLINT NOT NULL DEFAULT 0,
	group_azione SMALLINT NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
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
	param_value CLOB,
	-- fk/pk columns
	chk_param_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
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
	stato_dettaglio CLOB,
	acknowledged INT NOT NULL,
	timestamp_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	utente VARCHAR(255),
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	id_allarme BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_history_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_history PRIMARY KEY (id)
);

-- index
CREATE INDEX index_allarmi_history_1 ON allarmi_history (id_allarme,timestamp_update DESC);
CREATE INDEX index_allarmi_history_2 ON allarmi_history (timestamp_update);



CREATE TABLE allarmi_notifiche
(
	data_notifica TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	old_stato INT NOT NULL,
	old_stato_dettaglio CLOB,
	nuovo_stato INT NOT NULL,
	nuovo_stato_dettaglio CLOB,
	history_entry CLOB,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	id_allarme BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_notifiche_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_notifiche PRIMARY KEY (id)
);

-- index
CREATE INDEX index_allarmi_notifiche_1 ON allarmi_notifiche (data_notifica ASC);


