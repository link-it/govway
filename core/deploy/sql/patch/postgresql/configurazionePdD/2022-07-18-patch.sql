ALTER TABLE servizi_applicativi ADD COLUMN token_policy VARCHAR(255);

ALTER TABLE porte_applicative ADD COLUMN token_sa_stato VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN token_ruoli_stato VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN token_ruoli_match VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN token_ruoli_tipologia VARCHAR(255);

ALTER TABLE porte_delegate ADD COLUMN token_sa_stato VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN token_ruoli_stato VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN token_ruoli_match VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN token_ruoli_tipologia VARCHAR(255);

CREATE SEQUENCE seq_pd_token_sa start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_token_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_token_sa') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_token_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_token_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pd_token_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_token_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_TOKEN_SA ON pd_token_sa (id_porta);



CREATE SEQUENCE seq_pd_token_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_token_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_token_ruoli') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_token_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_token_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_token_ruoli PRIMARY KEY (id)
);


CREATE SEQUENCE seq_pa_token_sa start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_token_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_token_sa') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_token_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_token_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pa_token_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_token_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_TOKEN_SA ON pa_token_sa (id_porta);



CREATE SEQUENCE seq_pa_token_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_token_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_token_ruoli') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_token_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_token_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_token_ruoli PRIMARY KEY (id)
);



