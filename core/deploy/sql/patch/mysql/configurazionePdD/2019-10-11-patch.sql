ALTER TABLE porte_applicative ADD COLUMN id_sa_default BIGINT;

ALTER TABLE porte_applicative_sa ADD COLUMN connettore_nome VARCHAR(255);
ALTER TABLE porte_applicative_sa ADD COLUMN connettore_descrizione VARCHAR(4000);
ALTER TABLE porte_applicative_sa ADD COLUMN connettore_stato VARCHAR(255);
ALTER TABLE porte_applicative_sa ADD COLUMN connettore_filtri TEXT;

ALTER TABLE servizi_applicativi ADD COLUMN tipo VARCHAR(255);
update servizi_applicativi set tipo='client' where tipologia_fruizione<>'disabilitato';

CREATE TABLE pa_sa_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pa_sa_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_sa_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative_sa(id),
	CONSTRAINT pk_pa_sa_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PA_SA_PROP ON pa_sa_properties (id_porta);



CREATE TABLE pa_behaviour_props
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pa_behaviour_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_behaviour_props_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_behaviour_props PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PA_BEHAVIOUR_PROP ON pa_behaviour_props (id_porta);


