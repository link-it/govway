-- **** Scope ****

CREATE SEQUENCE seq_scope start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE scope
(
	nome VARCHAR(255) NOT NULL,
	descrizione TEXT,
	tipologia VARCHAR(255),
	nome_esterno VARCHAR(255),
	contesto_utilizzo VARCHAR(255) NOT NULL DEFAULT 'qualsiasi',
	superuser VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	utente_richiedente VARCHAR(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_scope') NOT NULL,
	-- check constraints
	CONSTRAINT chk_scope_1 CHECK (contesto_utilizzo IN ('portaDelegata','portaApplicativa','qualsiasi')),
	-- unique constraints
	CONSTRAINT unique_scope_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_scope PRIMARY KEY (id)
);



