-- PLUGINS

CREATE TABLE plugins
(
	tipo_plugin VARCHAR(255) NOT NULL,
	class_name VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	label VARCHAR(255) NOT NULL,
	stato BIT DEFAULT 'true',
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_plugins_1 UNIQUE (tipo_plugin,class_name),
	CONSTRAINT unique_plugins_2 UNIQUE (tipo_plugin,tipo),
	CONSTRAINT unique_plugins_3 UNIQUE (tipo_plugin,label),
	-- fk/pk keys constraints
	CONSTRAINT pk_plugins PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_plugins_1 ON plugins (tipo_plugin,class_name);
CREATE UNIQUE INDEX index_plugins_2 ON plugins (tipo_plugin,tipo);
CREATE UNIQUE INDEX index_plugins_3 ON plugins (tipo_plugin,label);



CREATE TABLE plugins_servizi_comp
(
	-- Accordo di Servizio
	uri_accordo VARCHAR(255),
	-- Nome del port-type
	servizio VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_plugin BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_servizi_comp_1 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_servizi_comp PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_plug_ser_com_1 ON plugins_servizi_comp (id_plugin);



CREATE TABLE plugins_azioni_comp
(
	azione VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_plugin_servizio_comp BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_plugins_azioni_comp_1 UNIQUE (id_plugin_servizio_comp,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_azioni_comp_1 FOREIGN KEY (id_plugin_servizio_comp) REFERENCES plugins_servizi_comp(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_azioni_comp PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_plugins_azioni_comp_1 ON plugins_azioni_comp (id_plugin_servizio_comp,azione);



CREATE TABLE plugins_props_comp
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_plugin BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_props_comp_1 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_props_comp PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_plug_prop_com_1 ON plugins_props_comp (id_plugin);



-- CONFIGURAZIONE DEI SERVIZI PER PERSONALIZZAZIONI

CREATE TABLE plugins_conf_servizi
(
	-- Accordo di Servizio
	accordo VARCHAR(255) NOT NULL,
	tipo_soggetto_referente VARCHAR(255) NOT NULL,
	nome_soggetto_referente VARCHAR(255) NOT NULL,
	versione INT NOT NULL DEFAULT 1,
	-- Nome del port-type
	servizio VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_plugins_conf_servizi PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conf_servizi_1 ON plugins_conf_servizi (accordo,tipo_soggetto_referente,nome_soggetto_referente,versione,servizio);



CREATE TABLE plugins_conf_azioni
(
	azione VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_config_servizio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_plugins_conf_azioni_1 UNIQUE (id_config_servizio,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_conf_azioni_1 FOREIGN KEY (id_config_servizio) REFERENCES plugins_conf_servizi(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_conf_azioni PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_plugins_conf_azioni_1 ON plugins_conf_azioni (id_config_servizio,azione);



-- FILTRO PER PERSONALIZZAZIONI

CREATE TABLE plugins_conf_filtri
(
	-- Dati Identificativi Regola
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- Regole di Filtraggio
	tipo_mittente VARCHAR(255),
	nome_mittente VARCHAR(255),
	idporta_mittente VARCHAR(255),
	tipo_destinatario VARCHAR(255),
	nome_destinatario VARCHAR(255),
	idporta_destinatario VARCHAR(255),
	tipo_servizio VARCHAR(255),
	nome_servizio VARCHAR(255),
	versione_servizio INT DEFAULT 1,
	azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_conf_filtri_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_plugins_conf_filtri PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_conf_filtri_1 ON plugins_conf_filtri (nome);


