-- **** Gruppi ****

CREATE TABLE gruppi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	service_binding VARCHAR(255),
	superuser VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_gruppi_1 CHECK (service_binding IN ('soap','rest')),
	-- unique constraints
	CONSTRAINT unique_gruppi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gruppi PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_gruppi_1 ON gruppi (nome);


