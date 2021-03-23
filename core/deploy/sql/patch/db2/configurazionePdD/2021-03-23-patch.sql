
CREATE INDEX INDEX_SA_RUOLI ON sa_ruoli (id_servizio_applicativo);



CREATE TABLE sa_credenziali
(
	id_servizio_applicativo BIGINT NOT NULL,
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate BLOB,
	cert_strict_verification INT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_credenziali_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_CRED ON sa_credenziali (id_servizio_applicativo);


