
-- **** Digests Servizi Params ****

CREATE SEQUENCE seq_servizi_digest_params start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE servizi_digest_params
(
	id_servizio BIGINT NOT NULL,
	serial_number BIGINT,
	data_registrazione TIMESTAMP NOT NULL,
	seed VARCHAR(1024) NOT NULL,
	algorithm VARCHAR(32) NOT NULL,
	lifetime INT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_servizi_digest_params') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_digest_params PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_REF_SERVIZI ON servizi_digest_params (id_servizio);

-- semaphore
INSERT INTO OP2_SEMAPHORE (applicative_id) VALUES ('ServiziDigestParamsUpdate');
