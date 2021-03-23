
CREATE INDEX INDEX_SOGGETTI_RUOLI ON soggetti_ruoli (id_soggetto);



CREATE SEQUENCE seq_soggetti_credenziali start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE soggetti_credenziali
(
	id_soggetto BIGINT NOT NULL,
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate BYTEA,
	cert_strict_verification INT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_soggetti_credenziali') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_credenziali_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_CRED ON soggetti_credenziali (id_soggetto);


