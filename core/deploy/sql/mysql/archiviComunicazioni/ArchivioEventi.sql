-- PLUGINS PER GLI EVENTI

CREATE TABLE notifiche_eventi
(
	tipo VARCHAR(100) NOT NULL,
	codice VARCHAR(100) NOT NULL,
	severita INT NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	descrizione VARCHAR(255),
	id_transazione VARCHAR(255),
	id_configurazione VARCHAR(255),
	configurazione MEDIUMTEXT,
	cluster_id VARCHAR(100),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_notifiche_eventi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX INDEX_EVENTI ON notifiche_eventi (ora_registrazione DESC,severita,tipo,codice,id_configurazione,cluster_id);


