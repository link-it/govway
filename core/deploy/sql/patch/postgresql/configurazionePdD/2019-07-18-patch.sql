CREATE SEQUENCE seq_pa_authz_properties start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_authz_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_authz_properties') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_authz_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_authz_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_authz_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_AUTHZ_PROP ON pa_authz_properties (id_porta);



CREATE SEQUENCE seq_pa_authzc_properties start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_authzc_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_authzc_properties') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_authzc_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_authzc_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_authzc_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_AUTHZC_PROP ON pa_authzc_properties (id_porta);


CREATE SEQUENCE seq_pd_authz_properties start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_authz_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_authz_properties') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_authz_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authz_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authz_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTHZ_PROP ON pd_authz_properties (id_porta);



CREATE SEQUENCE seq_pd_authzc_properties start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_authzc_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_authzc_properties') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_authzc_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authzc_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authzc_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTHZC_PROP ON pd_authzc_properties (id_porta);


