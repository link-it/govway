-- PLUGINS PER LE STATISTICHE PERSONALIZZATE

CREATE TABLE stat_personalizzate
(
	id VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL,
	label VARCHAR(255) NOT NULL,
	-- fk/pk columns
	pid BIGINT AUTO_INCREMENT,
	id_configurazione BIGINT NOT NULL,
	id_plugin BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_stat_pers_1 UNIQUE (id_configurazione,id),
	CONSTRAINT uniq_stat_pers_2 UNIQUE (id_configurazione,id_plugin),
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_personalizzate_1 FOREIGN KEY (id_configurazione) REFERENCES plugins_conf_azioni(id) ON DELETE CASCADE,
	CONSTRAINT fk_stat_personalizzate_2 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_personalizzate PRIMARY KEY (pid)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX idx_stat_pers_1 ON stat_personalizzate (id_configurazione,id);
CREATE UNIQUE INDEX idx_stat_pers_2 ON stat_personalizzate (id_configurazione,id_plugin);


