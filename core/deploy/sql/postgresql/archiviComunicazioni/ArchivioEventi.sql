-- PLUGINS PER GLI EVENTI

CREATE SEQUENCE seq_notifiche_eventi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE notifiche_eventi
(
	tipo VARCHAR(100) NOT NULL,
	codice VARCHAR(100) NOT NULL,
	severita INT NOT NULL,
	ora_registrazione TIMESTAMP NOT NULL,
	descrizione VARCHAR(255),
	id_transazione VARCHAR(255),
	id_configurazione VARCHAR(255),
	configurazione TEXT,
	cluster_id VARCHAR(100),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_notifiche_eventi') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_notifiche_eventi PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_EVENTI ON notifiche_eventi (ora_registrazione DESC,severita,tipo,codice,id_configurazione,cluster_id);


