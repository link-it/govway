-- **** Regitro Plugins ****

CREATE SEQUENCE seq_registro_plugins MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE registro_plugins
(
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	descrizione VARCHAR2(255),
	data TIMESTAMP NOT NULL,
	compatibilita CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_registro_plugins_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registro_plugins PRIMARY KEY (id)
);


ALTER TABLE registro_plugins MODIFY data DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_registro_plugins
BEFORE
insert on registro_plugins
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_registro_plugins.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_registro_plug_jar MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE registro_plug_jar
(
	id_plugin NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	sorgente VARCHAR2(255) NOT NULL,
	contenuto BLOB,
	url VARCHAR2(4000),
	dir VARCHAR2(4000),
	data TIMESTAMP NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_registro_plug_jar_1 UNIQUE (id_plugin,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_registro_plug_jar_1 FOREIGN KEY (id_plugin) REFERENCES registro_plugins(id),
	CONSTRAINT pk_registro_plug_jar PRIMARY KEY (id)
);

-- index
CREATE INDEX index_registro_plug_jar_1 ON registro_plug_jar (id_plugin);

ALTER TABLE registro_plug_jar MODIFY data DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_registro_plug_jar
BEFORE
insert on registro_plug_jar
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_registro_plug_jar.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


-- Aggiunto filtro per tag sul controllo del traffico
ALTER TABLE ct_active_policy ADD filtro_tag VARCHAR(255);

