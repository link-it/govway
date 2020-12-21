-- ALLARMI

CREATE SEQUENCE seq_allarmi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE allarmi
(
	-- Informazioni generiche
	nome VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	tipo_allarme VARCHAR2(255) NOT NULL,
	-- Stato allarme (0=OK, 1=Warning, 2=Allarme)
	stato_precedente NUMBER NOT NULL,
	stato NUMBER NOT NULL,
	stato_dettaglio CLOB,
	-- Tempi di creazione/esecuzione allarmi
	lasttimestamp_create TIMESTAMP NOT NULL,
	lasttimestamp_update TIMESTAMP,
	-- Informazioni generali (0=false/1=true)
	enabled NUMBER NOT NULL,
	acknowledged NUMBER NOT NULL,
	periodo_tipo VARCHAR2(255),
	periodo NUMBER,
	-- Informazioni sull'invio di e-mail
	mail_ack_mode NUMBER,
	mail_invia_warning NUMBER,
	mail_invia_alert NUMBER,
	mail_destinatari CLOB,
	mail_subject VARCHAR2(255),
	mail_body CLOB,
	-- Informazioni sull'invocazione di script esterni
	script_ack_mode NUMBER,
	script_invoke_warning NUMBER,
	script_invoke_alert NUMBER,
	script_command CLOB,
	script_args CLOB,
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
	filtro_tag VARCHAR2(255),
	filtro_tipo_servizio VARCHAR2(255),
	filtro_nome_servizio VARCHAR2(255),
	filtro_versione_servizio NUMBER,
	filtro_azione CLOB,
	-- Raggruppamento
	group_enabled NUMBER NOT NULL,
	group_ruolo NUMBER NOT NULL,
	group_protocollo NUMBER NOT NULL,
	group_fruitore NUMBER NOT NULL,
	group_sa_fruitore NUMBER NOT NULL,
	group_id_autenticato NUMBER NOT NULL,
	group_token CLOB,
	group_erogatore NUMBER NOT NULL,
	group_servizio NUMBER NOT NULL,
	group_azione NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
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

ALTER TABLE allarmi MODIFY tipo_allarme DEFAULT 'ATTIVO';
ALTER TABLE allarmi MODIFY filtro_enabled DEFAULT 0;
ALTER TABLE allarmi MODIFY group_enabled DEFAULT 0;
ALTER TABLE allarmi MODIFY group_ruolo DEFAULT 0;
ALTER TABLE allarmi MODIFY group_protocollo DEFAULT 0;
ALTER TABLE allarmi MODIFY group_fruitore DEFAULT 0;
ALTER TABLE allarmi MODIFY group_sa_fruitore DEFAULT 0;
ALTER TABLE allarmi MODIFY group_id_autenticato DEFAULT 0;
ALTER TABLE allarmi MODIFY group_erogatore DEFAULT 0;
ALTER TABLE allarmi MODIFY group_servizio DEFAULT 0;
ALTER TABLE allarmi MODIFY group_azione DEFAULT 0;

CREATE TRIGGER trg_allarmi
BEFORE
insert on allarmi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_allarmi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_allarmi_parametri MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE allarmi_parametri
(
	param_id VARCHAR2(255) NOT NULL,
	param_value CLOB,
	-- fk/pk columns
	chk_param_id NUMBER NOT NULL,
	id_allarme NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_allarmi_param_1 UNIQUE (id_allarme,param_id),
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_parametri_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_parametri PRIMARY KEY (chk_param_id)
);

CREATE TRIGGER trg_allarmi_parametri
BEFORE
insert on allarmi_parametri
for each row
begin
   IF (:new.chk_param_id IS NULL) THEN
      SELECT seq_allarmi_parametri.nextval INTO :new.chk_param_id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_allarmi_history MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE allarmi_history
(
	enabled NUMBER NOT NULL,
	stato NUMBER NOT NULL,
	stato_dettaglio CLOB,
	acknowledged NUMBER NOT NULL,
	timestamp_update TIMESTAMP NOT NULL,
	utente VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_allarme NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_allarmi_history_1 FOREIGN KEY (id_allarme) REFERENCES allarmi(id) ON DELETE CASCADE,
	CONSTRAINT pk_allarmi_history PRIMARY KEY (id)
);

-- index
CREATE INDEX index_allarmi_history_1 ON allarmi_history (id_allarme,timestamp_update DESC);

ALTER TABLE allarmi_history MODIFY timestamp_update DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_allarmi_history
BEFORE
insert on allarmi_history
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_allarmi_history.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


