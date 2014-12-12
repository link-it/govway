-- **** Ruoli ****

CREATE TABLE ruoli_sa
(
	id_accordo BIGINT NOT NULL,
	servizio_correlato INT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
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


