
CREATE TABLE pa_aa
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	attributi TEXT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pa_aa_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_aa_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_aa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pa_aa_1 ON pa_aa (id_porta,nome);


CREATE TABLE pd_aa
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	attributi TEXT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_aa_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_aa_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_aa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_aa_1 ON pd_aa (id_porta,nome);


