CREATE TABLE nodi_runtime_operations
(
	descrizione VARCHAR(4000) NOT NULL,
	operazione VARCHAR(4000) NOT NULL,
	data_registrazione DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_NODI_OP ON nodi_runtime_operations (data_registrazione);

