-- **** Gruppi ****

CREATE TABLE gruppi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000),
	service_binding VARCHAR(255),
	superuser VARCHAR(255),
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	utente_richiedente VARCHAR(255),
	data_creazione DATETIME2,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica DATETIME2,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_gruppi_1 CHECK (service_binding IN ('soap','rest')),
	-- unique constraints
	CONSTRAINT unique_gruppi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gruppi PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_gruppi_1 ON gruppi (nome);


