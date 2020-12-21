-- **** Regitro Plugins ****

CREATE TABLE registro_plugins
(
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	descrizione VARCHAR(255),
	data DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
	compatibilita VARCHAR(max),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_registro_plugins_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_registro_plugins PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_registro_plugins_1 ON registro_plugins (nome);



CREATE TABLE registro_plug_jar
(
	id_plugin BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	sorgente VARCHAR(255) NOT NULL,
	contenuto VARBINARY(MAX),
	url VARCHAR(4000),
	dir VARCHAR(4000),
	data DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_registro_plug_jar_1 UNIQUE (id_plugin,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_registro_plug_jar_1 FOREIGN KEY (id_plugin) REFERENCES registro_plugins(id),
	CONSTRAINT pk_registro_plug_jar PRIMARY KEY (id)
);

-- index
CREATE INDEX index_registro_plug_jar_1 ON registro_plug_jar (id_plugin);

-- Aggiunto filtro per tag sul controllo del traffico
ALTER TABLE ct_active_policy ADD filtro_tag VARCHAR(255);

