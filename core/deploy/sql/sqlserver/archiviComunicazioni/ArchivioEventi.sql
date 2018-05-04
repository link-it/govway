-- PLUGINS PER GLI EVENTI

CREATE TABLE notifiche_eventi
(
	tipo VARCHAR(100) NOT NULL,
	codice VARCHAR(100) NOT NULL,
	severita INT NOT NULL,
	ora_registrazione DATETIME2 NOT NULL,
	descrizione VARCHAR(255),
	id_transazione VARCHAR(255),
	id_configurazione VARCHAR(255),
	configurazione VARCHAR(max),
	cluster_id VARCHAR(100),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_notifiche_eventi PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_EVENTI ON notifiche_eventi (ora_registrazione DESC,severita,tipo,codice,id_configurazione,cluster_id);


