-- **** Nodi Runtime ****

CREATE SEQUENCE seq_nodi_runtime start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE nodi_runtime
(
	hostname VARCHAR(4000) NOT NULL,
	gruppo VARCHAR(4000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_refresh TIMESTAMP NOT NULL,
	id_numerico INT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_nodi_runtime') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
);


