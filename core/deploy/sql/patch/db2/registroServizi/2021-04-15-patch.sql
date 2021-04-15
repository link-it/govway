
CREATE TABLE soggetti_properties
(
	id_soggetto BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT uniq_soggetti_props_1 UNIQUE (id_soggetto,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_properties_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_PROP ON soggetti_properties (id_soggetto);


