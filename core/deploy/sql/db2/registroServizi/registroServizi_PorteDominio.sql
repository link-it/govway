-- **** Porte di Dominio ****

CREATE TABLE pdd
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	implementazione VARCHAR(255) DEFAULT 'standard',
	subject VARCHAR(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	superuser VARCHAR(255),
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_pdd_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd PRIMARY KEY (id)
);

-- index
CREATE INDEX index_pdd_1 ON pdd (nome);


