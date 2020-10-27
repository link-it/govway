ALTER TABLE porte_delegate ADD canale VARCHAR(20);
ALTER TABLE porte_applicative ADD canale VARCHAR(20);
ALTER TABLE configurazione ADD canali_stato VARCHAR(255);

CREATE TABLE canali_configurazione
(
	nome VARCHAR(20) NOT NULL,
	descrizione VARCHAR(255),
	canale_default INT NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_canali_configurazione_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_configurazione PRIMARY KEY (id)
);




CREATE TABLE canali_nodi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canali VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_canali_nodi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_nodi PRIMARY KEY (id)
);



CREATE INDEX index_porte_delegate_3 ON porte_delegate (canale);
CREATE INDEX index_porte_applicative_3 ON porte_applicative (canale);

CREATE INDEX index_mapping_fruizione_pd_2 ON mapping_fruizione_pd (id_porta);
CREATE INDEX index_mapping_erogazione_pa_2 ON mapping_erogazione_pa (id_porta);

