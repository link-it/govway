-- **** Soggetti ****

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR(255) NOT NULL,
	tipo_soggetto VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	identificativo_porta VARCHAR(255),
	-- 1/0 (true/false) svolge attivita di router
	is_router INT DEFAULT 0,
	id_connettore BIGINT NOT NULL,
	superuser VARCHAR(255),
	server VARCHAR(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	profilo VARCHAR(255),
	codice_ipa VARCHAR(255) NOT NULL,
	pd_url_prefix_rewriter VARCHAR(255),
	pa_url_prefix_rewriter VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	CONSTRAINT unique_soggetti_2 UNIQUE (codice_ipa),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
);

-- index
CREATE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);
CREATE INDEX index_soggetti_2 ON soggetti (codice_ipa);


