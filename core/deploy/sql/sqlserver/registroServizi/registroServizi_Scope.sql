-- **** Scope ****

CREATE TABLE scope
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000),
	tipologia VARCHAR(255),
	nome_esterno VARCHAR(255),
	contesto_utilizzo VARCHAR(255) NOT NULL DEFAULT 'qualsiasi',
	superuser VARCHAR(255),
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	utente_richiedente VARCHAR(255),
	data_creazione DATETIME2,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica DATETIME2,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_scope_1 CHECK (contesto_utilizzo IN ('portaDelegata','portaApplicativa','qualsiasi')),
	-- unique constraints
	CONSTRAINT unique_scope_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_scope PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_scope_1 ON scope (nome);


