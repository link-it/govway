CREATE SEQUENCE seq_remote_store MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE remote_store
(
	nome VARCHAR2(255) NOT NULL,
	data_aggiornamento TIMESTAMP NOT NULL,
	last_event VARCHAR2(4000),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_remote_store_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_remote_store PRIMARY KEY (id)
);

CREATE TRIGGER trg_remote_store
BEFORE
insert on remote_store
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_remote_store.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_remote_store_key MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE remote_store_key
(
	id_remote_store NUMBER NOT NULL,
	kid VARCHAR2(255) NOT NULL,
	content_key BLOB NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_aggiornamento TIMESTAMP NOT NULL,
	client_id VARCHAR2(255),
	client_details CLOB,
	organization_details CLOB,
	client_data_aggiornamento TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_remote_store_key_1 UNIQUE (id_remote_store,kid),
	-- fk/pk keys constraints
	CONSTRAINT fk_remote_store_key_1 FOREIGN KEY (id_remote_store) REFERENCES remote_store(id),
	CONSTRAINT pk_remote_store_key PRIMARY KEY (id)
);

-- index
CREATE INDEX REMOTE_STORE_UPDATE ON remote_store_key (data_aggiornamento);
CREATE INDEX REMOTE_STORE_CREATE ON remote_store_key (data_registrazione);
CREATE TRIGGER trg_remote_store_key
BEFORE
insert on remote_store_key
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_remote_store_key.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


