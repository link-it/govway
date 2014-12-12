-- **** Soggetti ****

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR(255) NOT NULL,
	tipo_soggetto VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	identificativo_porta VARCHAR(255),
	-- 1/0 (true/false) svolge attivita di router
	is_router INT DEFAULT 0,
	superuser VARCHAR(255),
	pd_url_prefix_rewriter VARCHAR(255),
	pa_url_prefix_rewriter VARCHAR(255),
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
);

-- index
CREATE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);


