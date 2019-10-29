ALTER TABLE porte_applicative ADD id_sa_default BIGINT;

ALTER TABLE porte_applicative_sa ADD connettore_nome VARCHAR(255);
ALTER TABLE porte_applicative_sa ADD connettore_descrizione VARCHAR(4000);
ALTER TABLE porte_applicative_sa ADD connettore_stato VARCHAR(255);
ALTER TABLE porte_applicative_sa ADD connettore_filtri VARCHAR(max);

ALTER TABLE servizi_applicativi ADD tipo VARCHAR(255);
update servizi_applicativi set tipo='client' where tipologia_fruizione<>'disabilitato';

ALTER TABLE servizi_applicativi ADD COLUMN as_client INT;

CREATE TABLE pa_sa_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_pa_sa_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_sa_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative_sa(id),
	CONSTRAINT pk_pa_sa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_PROP ON pa_sa_properties (id_porta);



CREATE TABLE pa_behaviour_props
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_pa_behaviour_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_behaviour_props_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_behaviour_props PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_BEHAVIOUR_PROP ON pa_behaviour_props (id_porta);


ALTER TABLE configurazione ADD consegna_statocache VARCHAR(255);
ALTER TABLE configurazione ADD consegna_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD consegna_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD consegna_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD consegna_lifecache VARCHAR(255);

UPDATE configurazione set consegna_statocache='abilitato';
UPDATE configurazione set consegna_dimensionecache='10000';
UPDATE configurazione set consegna_algoritmocache='lru';
UPDATE configurazione set consegna_lifecache='-1';
