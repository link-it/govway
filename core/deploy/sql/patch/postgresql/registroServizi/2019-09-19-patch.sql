-- **** Gruppi ****

CREATE SEQUENCE seq_gruppi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE gruppi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	service_binding VARCHAR(255),
	superuser VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_gruppi') NOT NULL,
	-- check constraints
	CONSTRAINT chk_gruppi_1 CHECK (service_binding IN ('soap','rest')),
	-- unique constraints
	CONSTRAINT unique_gruppi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gruppi PRIMARY KEY (id)
);



CREATE SEQUENCE seq_accordi_gruppi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE accordi_gruppi
(
	id_accordo BIGINT NOT NULL,
	id_gruppo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_accordi_gruppi') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_acc_gruppi_1 UNIQUE (id_accordo,id_gruppo),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_gruppi_1 FOREIGN KEY (id_gruppo) REFERENCES gruppi(id),
	CONSTRAINT fk_accordi_gruppi_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_accordi_gruppi PRIMARY KEY (id)
);



