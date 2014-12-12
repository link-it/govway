-- **** Ruoli ****

CREATE SEQUENCE seq_ruoli_sa start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE ruoli_sa
(
	id_accordo BIGINT NOT NULL,
	servizio_correlato INT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_ruoli_sa') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_ruoli_sa_1 UNIQUE (id_accordo,servizio_correlato,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_ruoli_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_ruoli_sa_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_ruoli_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_RUOLI ON ruoli_sa (id_accordo,servizio_correlato);
CREATE INDEX INDEX_RUOLI_SA ON ruoli_sa (id_servizio_applicativo);


