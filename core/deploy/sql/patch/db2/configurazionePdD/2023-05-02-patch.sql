CREATE TABLE remote_store
(
	nome VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_remote_store_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_remote_store PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_remote_store_1 ON remote_store (nome);



CREATE TABLE remote_store_key
(
	id_remote_store BIGINT NOT NULL,
	kid VARCHAR(255) NOT NULL,
	key BLOB NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_aggiornamento TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_remote_store_key_1 UNIQUE (id_remote_store,kid),
	-- fk/pk keys constraints
	CONSTRAINT fk_remote_store_key_1 FOREIGN KEY (id_remote_store) REFERENCES remote_store(id),
	CONSTRAINT pk_remote_store_key PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_remote_store_key_1 ON remote_store_key (id_remote_store,kid);
CREATE INDEX REMOTE_STORE_UPDATE ON remote_store_key (data_aggiornamento);
CREATE INDEX REMOTE_STORE_CREATE ON remote_store_key (data_registrazione);


