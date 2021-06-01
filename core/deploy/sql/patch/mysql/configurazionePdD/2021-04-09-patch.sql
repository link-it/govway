-- **** Nodi Runtime ****

CREATE TABLE nodi_runtime
(
	hostname VARCHAR(3000) NOT NULL,
	gruppo VARCHAR(3000) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_refresh TIMESTAMP(3) NOT NULL DEFAULT 0,
	id_numerico INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_nodi_runtime_1 ON nodi_runtime (hostname);
CREATE UNIQUE INDEX index_nodi_runtime_2 ON nodi_runtime (gruppo,id_numerico);


