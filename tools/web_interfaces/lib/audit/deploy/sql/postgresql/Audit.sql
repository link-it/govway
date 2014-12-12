-- **** Audit Configuration ****

CREATE SEQUENCE seq_audit_conf start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE audit_conf
(
	audit_engine INT NOT NULL DEFAULT 0,
	enabled INT NOT NULL DEFAULT 0,
	dump INT NOT NULL DEFAULT 0,
	dump_format VARCHAR(255) NOT NULL DEFAULT 'JSON',
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_audit_conf') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_conf PRIMARY KEY (id)
);




CREATE SEQUENCE seq_audit_filters start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE audit_filters
(
	-- Filter
	username VARCHAR(255),
	tipo_operazione VARCHAR(255),
	tipo VARCHAR(255),
	stato VARCHAR(255),
	-- Filtri basati su contenuto utilizzabili solo se il dump della configurazione generale e' abilitato
	dump_search VARCHAR(255),
	-- 1(espressione regolare)/0(semplice stringa da ricercare)
	dump_expr INT NOT NULL DEFAULT 0,
	-- Action
	enabled INT NOT NULL DEFAULT 0,
	dump INT NOT NULL DEFAULT 0,
	-- Tempo di registrazione
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_audit_filters') NOT NULL,
	-- check constraints
	CONSTRAINT chk_audit_filters_1 CHECK (tipo_operazione IN ('ADD','CHANGE','DEL')),
	CONSTRAINT chk_audit_filters_2 CHECK (stato IN ('requesting','error','completed')),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_filters PRIMARY KEY (id)
);




CREATE SEQUENCE seq_audit_appender start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE audit_appender
(
	name VARCHAR(255) NOT NULL,
	class VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_audit_appender') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_audit_appender_1 UNIQUE (name),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_appender PRIMARY KEY (id)
);




CREATE SEQUENCE seq_audit_appender_prop start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE audit_appender_prop
(
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	id_audit_appender BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_audit_appender_prop') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_audit_appender_prop_1 FOREIGN KEY (id_audit_appender) REFERENCES audit_appender(id),
	CONSTRAINT pk_audit_appender_prop PRIMARY KEY (id)
);



