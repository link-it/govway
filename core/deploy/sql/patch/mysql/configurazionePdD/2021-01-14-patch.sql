ALTER TABLE porte_delegate MODIFY COLUMN integrazione VARCHAR(4000);
ALTER TABLE porte_applicative MODIFY COLUMN integrazione VARCHAR(4000);


CREATE TABLE config_handlers
(
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_config_handlers_1 UNIQUE (tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_handlers PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_config_handlers_1 ON config_handlers (tipologia,tipo);



CREATE TABLE pd_handlers
(
	id_porta BIGINT NOT NULL,
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_handlers PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_handlers_1 ON pd_handlers (id_porta,tipologia,tipo);


CREATE TABLE pa_handlers
(
	id_porta BIGINT NOT NULL,
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pa_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_handlers PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pa_handlers_1 ON pa_handlers (id_porta,tipologia,tipo);


