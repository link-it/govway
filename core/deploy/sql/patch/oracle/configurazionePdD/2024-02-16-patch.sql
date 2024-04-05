ALTER TABLE configurazione ADD tracciamento_esiti_pd VARCHAR2(255);
UPDATE configurazione set tracciamento_esiti_pd=tracciamento_esiti;
ALTER TABLE configurazione ADD transazioni_tempi_pd VARCHAR2(255);
UPDATE configurazione set transazioni_tempi_pd=transazioni_tempi;
ALTER TABLE configurazione ADD transazioni_token_pd VARCHAR2(255);
UPDATE configurazione set transazioni_token_pd=transazioni_token;
update configurazione set transazioni_tempi_pd='disabilitato' where transazioni_tempi_pd is null;
update configurazione set transazioni_tempi='disabilitato' where transazioni_tempi is null;
update configurazione set transazioni_token_pd='abilitato' where transazioni_token_pd is null;
update configurazione set transazioni_token='abilitato' where transazioni_token is null;

ALTER TABLE porte_delegate ADD tracciamento_stato VARCHAR2(255);
UPDATE porte_delegate set tracciamento_stato='abilitato' WHERE tracciamento_esiti IS NOT NULL AND tracciamento_esiti <> '';
UPDATE porte_delegate set tracciamento_stato='disabilitato' WHERE tracciamento_esiti IS NULL OR tracciamento_esiti = '';
ALTER TABLE porte_delegate ADD transazioni_tempi VARCHAR2(255);
ALTER TABLE porte_delegate ADD transazioni_token VARCHAR2(255);

ALTER TABLE porte_applicative ADD tracciamento_stato VARCHAR2(255);
UPDATE porte_applicative set tracciamento_stato='abilitato' WHERE tracciamento_esiti IS NOT NULL AND tracciamento_esiti <> '';
UPDATE porte_applicative set tracciamento_stato='disabilitato' WHERE tracciamento_esiti IS NULL OR tracciamento_esiti = '';
ALTER TABLE porte_applicative ADD transazioni_tempi VARCHAR2(255);
ALTER TABLE porte_applicative ADD transazioni_token VARCHAR2(255);



CREATE SEQUENCE seq_tracce_config MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE tracce_config
(
	proprietario VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	id_proprietario NUMBER NOT NULL,
	stato VARCHAR2(255),
	filtro_esiti VARCHAR2(255),
	request_in VARCHAR2(255),
	request_out VARCHAR2(255),
	response_out VARCHAR2(255),
	response_out_complete VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_tracce_config_1 ON tracce_config (proprietario,tipo);
CREATE TRIGGER trg_tracce_config
BEFORE
insert on tracce_config
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_config.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_filetrace_config MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE filetrace_config
(
	proprietario VARCHAR2(255) NOT NULL,
	id_proprietario NUMBER NOT NULL,
	config VARCHAR2(255),
	dump_in_stato VARCHAR2(255),
	dump_in_stato_hdr VARCHAR2(255),
	dump_in_stato_body VARCHAR2(255),
	dump_out_stato VARCHAR2(255),
	dump_out_stato_hdr VARCHAR2(255),
	dump_out_stato_body VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_filetrace_config PRIMARY KEY (id)
);

-- index
CREATE INDEX index_filetrace_config_1 ON filetrace_config (proprietario);
CREATE TRIGGER trg_filetrace_config
BEFORE
insert on filetrace_config
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_filetrace_config.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



INSERT INTO tracce_config (proprietario,tipo,id_proprietario,stato,filtro_esiti,request_in,request_out,response_out,response_out_complete) VALUES ('configpa','filetrace',-1,'configurazioneEsterna','disabilitato','disabilitato','disabilitato','disabilitato','abilitato');
INSERT INTO tracce_config (proprietario,tipo,id_proprietario,stato,filtro_esiti,request_in,request_out,response_out,response_out_complete) VALUES ('configpd','filetrace',-1,'configurazioneEsterna','disabilitato','disabilitato','disabilitato','disabilitato','abilitato');


