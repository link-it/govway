-- **** Audit Configuration ****

CREATE SEQUENCE seq_audit_conf MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE audit_conf
(
	audit_engine NUMBER NOT NULL,
	enabled NUMBER NOT NULL,
	dump NUMBER NOT NULL,
	dump_format VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_conf PRIMARY KEY (id)
);


ALTER TABLE audit_conf MODIFY audit_engine DEFAULT 0;
ALTER TABLE audit_conf MODIFY enabled DEFAULT 0;
ALTER TABLE audit_conf MODIFY dump DEFAULT 0;
ALTER TABLE audit_conf MODIFY dump_format DEFAULT 'JSON';

CREATE TRIGGER trg_audit_conf
BEFORE
insert on audit_conf
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_audit_conf.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_audit_filters MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE audit_filters
(
	-- Filter
	username VARCHAR2(255),
	tipo_operazione VARCHAR2(255),
	tipo VARCHAR2(255),
	stato VARCHAR2(255),
	-- Filtri basati su contenuto utilizzabili solo se il dump della configurazione generale e' abilitato
	dump_search VARCHAR2(255),
	-- 1(espressione regolare)/0(semplice stringa da ricercare)
	dump_expr NUMBER NOT NULL,
	-- Action
	enabled NUMBER NOT NULL,
	dump NUMBER NOT NULL,
	-- Tempo di registrazione
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_audit_filters_1 CHECK (tipo_operazione IN ('ADD','CHANGE','DEL')),
	CONSTRAINT chk_audit_filters_2 CHECK (stato IN ('requesting','error','completed')),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_filters PRIMARY KEY (id)
);


ALTER TABLE audit_filters MODIFY dump_expr DEFAULT 0;
ALTER TABLE audit_filters MODIFY enabled DEFAULT 0;
ALTER TABLE audit_filters MODIFY dump DEFAULT 0;
ALTER TABLE audit_filters MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_audit_filters
BEFORE
insert on audit_filters
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_audit_filters.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_audit_appender MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE audit_appender
(
	name VARCHAR2(255) NOT NULL,
	class VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_audit_appender_1 UNIQUE (name),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_appender PRIMARY KEY (id)
);

CREATE TRIGGER trg_audit_appender
BEFORE
insert on audit_appender
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_audit_appender.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_audit_appender_prop MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE audit_appender_prop
(
	name VARCHAR2(255) NOT NULL,
	value VARCHAR2(255) NOT NULL,
	id_audit_appender NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_audit_appender_prop_1 FOREIGN KEY (id_audit_appender) REFERENCES audit_appender(id),
	CONSTRAINT pk_audit_appender_prop PRIMARY KEY (id)
);

CREATE TRIGGER trg_audit_appender_prop
BEFORE
insert on audit_appender_prop
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_audit_appender_prop.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


