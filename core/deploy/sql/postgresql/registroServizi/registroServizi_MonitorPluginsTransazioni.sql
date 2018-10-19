-- PERSONALIZZAZIONI/PLUGINS DELLE TRANSAZIONI

CREATE SEQUENCE seq_config_transazioni start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE config_transazioni
(
	enabled BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_config_transazioni') NOT NULL,
	id_conf_servizio_azione BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_trans_pers_1 UNIQUE (id_conf_servizio_azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_config_transazioni_1 FOREIGN KEY (id_conf_servizio_azione) REFERENCES plugins_conf_azioni(id),
	CONSTRAINT pk_config_transazioni PRIMARY KEY (id)
);




CREATE SEQUENCE seq_config_tran_plugins start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE config_tran_plugins
(
	id_conf_trans_plugin VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_config_tran_plugins') NOT NULL,
	id_configurazione_transazione BIGINT NOT NULL,
	id_plugin BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_conf_trans_plug_1 UNIQUE (id_configurazione_transazione,id_conf_trans_plugin),
	CONSTRAINT uniq_conf_trans_plug_2 UNIQUE (id_configurazione_transazione,id_plugin),
	-- fk/pk keys constraints
	CONSTRAINT fk_config_tran_plugins_1 FOREIGN KEY (id_configurazione_transazione) REFERENCES config_transazioni(id),
	CONSTRAINT fk_config_tran_plugins_2 FOREIGN KEY (id_plugin) REFERENCES plugins(id),
	CONSTRAINT pk_config_tran_plugins PRIMARY KEY (id)
);




CREATE SEQUENCE seq_configurazione_stati start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE configurazione_stati
(
	enabled BOOLEAN NOT NULL,
	nome VARCHAR(255) NOT NULL,
	tipo_controllo VARCHAR(255) NOT NULL,
	tipo_messaggio VARCHAR(255) NOT NULL,
	valore VARCHAR(255),
	xpath VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_configurazione_stati') NOT NULL,
	id_configurazione_transazione BIGINT NOT NULL,
	-- check constraints
	CONSTRAINT chk_configurazione_stati_1 CHECK (tipo_controllo IN ('Match','Equals','Exist')),
	CONSTRAINT chk_configurazione_stati_2 CHECK (tipo_messaggio IN ('Richiesta','Risposta')),
	-- unique constraints
	CONSTRAINT uniq_trans_pers_stati_1 UNIQUE (id_configurazione_transazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_configurazione_stati_1 FOREIGN KEY (id_configurazione_transazione) REFERENCES config_transazioni(id),
	CONSTRAINT pk_configurazione_stati PRIMARY KEY (id)
);




CREATE SEQUENCE seq_conf_risorse_contenuti start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE conf_risorse_contenuti
(
	abilita_anonimizzazione INT NOT NULL DEFAULT 0,
	abilita_compressione INT NOT NULL DEFAULT 0,
	tipo_compressione VARCHAR(255),
	carattere_maschera CHAR(1),
	num_char_maschera INT,
	posizionamento_maschera VARCHAR(255),
	tipo_mascheramento VARCHAR(255),
	enabled BOOLEAN NOT NULL,
	nome VARCHAR(255) NOT NULL,
	tipo_messaggio VARCHAR(255) NOT NULL,
	xpath VARCHAR(255) NOT NULL,
	stat_enabled BOOLEAN NOT NULL DEFAULT false,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_conf_risorse_contenuti') NOT NULL,
	id_conf_transazione BIGINT NOT NULL,
	id_configurazione_stato BIGINT,
	-- check constraints
	CONSTRAINT chk_conf_risorse_contenuti_1 CHECK (tipo_compressione IN ('Deflater','Gzip','Zip')),
	CONSTRAINT chk_conf_risorse_contenuti_2 CHECK (posizionamento_maschera IN ('Primi','Ultimi')),
	CONSTRAINT chk_conf_risorse_contenuti_3 CHECK (tipo_mascheramento IN ('Fisico','Logico')),
	CONSTRAINT chk_conf_risorse_contenuti_4 CHECK (tipo_messaggio IN ('Richiesta','Risposta')),
	-- unique constraints
	CONSTRAINT uniq_trans_pers_risorse_1 UNIQUE (id_conf_transazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_conf_risorse_contenuti_1 FOREIGN KEY (id_conf_transazione) REFERENCES config_transazioni(id),
	CONSTRAINT fk_conf_risorse_contenuti_2 FOREIGN KEY (id_configurazione_stato) REFERENCES configurazione_stati(id),
	CONSTRAINT pk_conf_risorse_contenuti PRIMARY KEY (id)
);



