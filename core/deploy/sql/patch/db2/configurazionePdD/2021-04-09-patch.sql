-- **** Nodi Runtime ****

CREATE TABLE nodi_runtime
(
	hostname VARCHAR(4000) NOT NULL,
	gruppo VARCHAR(4000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_refresh TIMESTAMP NOT NULL,
	id_numerico INT NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_nodi_runtime_1 ON nodi_runtime (hostname);
CREATE UNIQUE INDEX index_nodi_runtime_2 ON nodi_runtime (gruppo,id_numerico);
