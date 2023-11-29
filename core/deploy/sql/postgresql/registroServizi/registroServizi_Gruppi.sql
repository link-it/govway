-- **** Gruppi ****

CREATE SEQUENCE seq_gruppi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE gruppi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000),
	service_binding VARCHAR(255),
	superuser VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	utente_richiedente VARCHAR(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_gruppi') NOT NULL,
	-- check constraints
	CONSTRAINT chk_gruppi_1 CHECK (service_binding IN ('soap','rest')),
	-- unique constraints
	CONSTRAINT unique_gruppi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gruppi PRIMARY KEY (id)
);



