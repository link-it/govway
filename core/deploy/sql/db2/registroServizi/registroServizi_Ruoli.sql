-- **** Ruoli ****

CREATE TABLE ruoli
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000),
	tipologia VARCHAR(255) NOT NULL DEFAULT 'qualsiasi',
	nome_esterno VARCHAR(255),
	contesto_utilizzo VARCHAR(255) NOT NULL DEFAULT 'qualsiasi',
	superuser VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	utente_richiedente VARCHAR(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_ruoli_1 CHECK (tipologia IN ('interno','esterno','qualsiasi')),
	CONSTRAINT chk_ruoli_2 CHECK (contesto_utilizzo IN ('portaDelegata','portaApplicativa','qualsiasi')),
	-- unique constraints
	CONSTRAINT unique_ruoli_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_ruoli PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_ruoli_1 ON ruoli (nome);


