ALTER TABLE porte_delegate ADD COLUMN canale VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN canale VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN canali_stato VARCHAR(255);


CREATE SEQUENCE seq_canali_configurazione start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE canali_configurazione
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canale_default INT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_canali_configurazione') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_canali_configurazione_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_configurazione PRIMARY KEY (id)
);




CREATE SEQUENCE seq_canali_nodi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE canali_nodi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	canali VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_canali_nodi') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_canali_nodi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_canali_nodi PRIMARY KEY (id)
);



CREATE INDEX index_porte_delegate_3 ON porte_delegate (canale);
CREATE INDEX index_porte_applicative_3 ON porte_applicative (canale);

CREATE INDEX index_mapping_fruizione_pd_2 ON mapping_fruizione_pd (id_porta);
CREATE INDEX index_mapping_erogazione_pa_2 ON mapping_erogazione_pa (id_porta);

