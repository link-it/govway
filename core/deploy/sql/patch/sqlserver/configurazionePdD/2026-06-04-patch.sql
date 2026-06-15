CREATE TABLE connettori_llm_binding
(
	nome_binding VARCHAR(64) NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_conn_llm_bind_1 UNIQUE (id_connettore,nome_binding),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_llm_binding_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_llm_binding PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conn_llm_bind_1 ON connettori_llm_binding (id_connettore);



CREATE TABLE connettori_llm
(
	id_connettore_principale BIGINT NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_conn_llm_1 UNIQUE (id_connettore_principale,id_connettore),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_llm_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_connettori_llm_2 FOREIGN KEY (id_connettore_principale) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conn_llm_1 ON connettori_llm (id_connettore_principale);
