-- PLUGINS PER LE RICERCHE PERSONALIZZATE

CREATE TABLE ricerche_personalizzate
(
	id VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL,
	label VARCHAR(255) NOT NULL,
	-- fk/pk columns
	pid BIGINT AUTO_INCREMENT,
	id_configurazione BIGINT NOT NULL,
	id_plugin BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_ricerche_pers_1 UNIQUE (id_configurazione,id),
	CONSTRAINT uniq_ricerche_pers_2 UNIQUE (id_configurazione,id_plugin),
	-- fk/pk keys constraints
	CONSTRAINT fk_ricerche_personalizzate_1 FOREIGN KEY (id_configurazione) REFERENCES plugins_conf_azioni(id) ON DELETE CASCADE,
	CONSTRAINT fk_ricerche_personalizzate_2 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_ricerche_personalizzate PRIMARY KEY (pid)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX idx_ricerche_pers_1 ON ricerche_personalizzate (id_configurazione,id);
CREATE UNIQUE INDEX idx_ricerche_pers_2 ON ricerche_personalizzate (id_configurazione,id_plugin);


