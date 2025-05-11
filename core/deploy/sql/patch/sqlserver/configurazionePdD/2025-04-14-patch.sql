
-- **** Digests Servizi Params ****

CREATE TABLE servizi_digest_params
(
	id_servizio BIGINT NOT NULL,
	serial_number BIGINT,
	data_registrazione DATETIME2 NOT NULL,
	seed VARCHAR(4000) NOT NULL,
	algorithm VARCHAR(32) NOT NULL,
	lifetime INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_digest_params PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_REF_SERVIZI ON servizi_digest_params (id_servizio);

-- semaphore
INSERT INTO OP2_SEMAPHORE (applicative_id) VALUES ('ServiziDigestParamsUpdate');
