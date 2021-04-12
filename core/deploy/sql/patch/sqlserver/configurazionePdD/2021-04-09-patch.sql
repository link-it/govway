-- **** Nodi Runtime ****

CREATE TABLE nodi_runtime
(
	hostname VARCHAR(4000) NOT NULL,
	gruppo VARCHAR(4000) NOT NULL,
	data_registrazione DATETIME2 NOT NULL,
	data_refresh DATETIME2 NOT NULL,
	id_numerico INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_nodi_runtime_1 ON nodi_runtime (hostname);
CREATE UNIQUE INDEX index_nodi_runtime_2 ON nodi_runtime (gruppo,id_numerico);


