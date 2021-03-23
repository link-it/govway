
CREATE INDEX INDEX_SA_RUOLI ON sa_ruoli (id_servizio_applicativo);


CREATE SEQUENCE seq_sa_credenziali start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE sa_credenziali
(
	id_servizio_applicativo BIGINT NOT NULL,
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate BYTEA,
	cert_strict_verification INT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_sa_credenziali') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_credenziali_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_CRED ON sa_credenziali (id_servizio_applicativo);
