ALTER TABLE servizi_applicativi ADD COLUMN token_policy VARCHAR(255);

ALTER TABLE porte_applicative ADD COLUMN token_sa_stato VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN token_ruoli_stato VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN token_ruoli_match VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN token_ruoli_tipologia VARCHAR(255);

ALTER TABLE porte_delegate ADD COLUMN token_sa_stato VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN token_ruoli_stato VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN token_ruoli_match VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN token_ruoli_tipologia VARCHAR(255);

CREATE TABLE pd_token_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_token_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_token_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pd_token_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_token_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_TOKEN_SA ON pd_token_sa (id_porta);



CREATE TABLE pd_token_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_token_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_token_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_token_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_token_ruoli_1 ON pd_token_ruoli (id_porta,ruolo);


CREATE TABLE pa_token_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pa_token_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_token_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pa_token_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_token_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PA_TOKEN_SA ON pa_token_sa (id_porta);



CREATE TABLE pa_token_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pa_token_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_token_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_token_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pa_token_ruoli_1 ON pa_token_ruoli (id_porta,ruolo);


