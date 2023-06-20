CREATE SEQUENCE seq_nodi_runtime_operations start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE nodi_runtime_operations
(
	descrizione VARCHAR(4000) NOT NULL,
	operazione VARCHAR(4000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_nodi_runtime_operations') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_NODI_OP ON nodi_runtime_operations (data_registrazione);

