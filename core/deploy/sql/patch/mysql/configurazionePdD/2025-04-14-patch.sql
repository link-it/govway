
-- **** Digests Servizi Params ****

CREATE TABLE servizi_digest_params
(
	id_servizio BIGINT NOT NULL,
	serial_number BIGINT,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	seed VARCHAR(4000) NOT NULL,
	algorithm VARCHAR(32) NOT NULL,
	lifetime INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_digest_params PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_REF_SERVIZI ON servizi_digest_params (id_servizio);

-- semaphore
INSERT INTO OP2_SEMAPHORE (applicative_id) VALUES ('ServiziDigestParamsUpdate');
