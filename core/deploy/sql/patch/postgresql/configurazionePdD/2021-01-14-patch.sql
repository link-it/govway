ALTER TABLE porte_delegate ALTER COLUMN integrazione TYPE VARCHAR(4000);
ALTER TABLE porte_applicative ALTER COLUMN integrazione TYPE VARCHAR(4000);

CREATE SEQUENCE seq_config_handlers start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE config_handlers
(
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_config_handlers') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_config_handlers_1 UNIQUE (tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_handlers PRIMARY KEY (id)
);


CREATE SEQUENCE seq_pd_handlers start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_handlers
(
	id_porta BIGINT NOT NULL,
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_handlers') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_handlers PRIMARY KEY (id)
);


CREATE SEQUENCE seq_pa_handlers start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_handlers
(
	id_porta BIGINT NOT NULL,
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_handlers') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_handlers PRIMARY KEY (id)
);


