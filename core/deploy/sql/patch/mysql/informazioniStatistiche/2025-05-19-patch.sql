
-- STATISTICHE TRACING PDND

CREATE TABLE statistiche_pdnd_tracing
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_tracciamento TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_pubblicazione TIMESTAMP(3) DEFAULT 0,
	pdd_codice VARCHAR(255) NOT NULL,
	csv MEDIUMBLOB,
	method VARCHAR(20),
	stato_pdnd VARCHAR(20) NOT NULL DEFAULT 'WAITING',
	tentativi_pubblicazione INT NOT NULL DEFAULT 0,
	force_publish BOOLEAN NOT NULL DEFAULT false,
	stato VARCHAR(20),
	tracing_id VARCHAR(36),
	error_details TEXT,
	history INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_pdnd_tracing_1 CHECK (method IN ('REPLACE','RECOVER','SUBMIT')),
	CONSTRAINT chk_statistiche_pdnd_tracing_2 CHECK (stato_pdnd IN ('WAITING','PENDING','OK','ERROR')),
	CONSTRAINT chk_statistiche_pdnd_tracing_3 CHECK (stato IN ('PUBLISHED','FAILED')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_pdnd_tracing PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PDND_TRACING_ACTIVE ON statistiche_pdnd_tracing (pdd_codice,data_tracciamento DESC,history);

-- constraint
-- necessario con MySQL ≥ 8.0.16: ALTER TABLE statistiche DROP CONSTRAINT chk_statistiche_1;
ALTER TABLE statistiche ADD CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili','PdndGenerazioneTracciamento','PdndPubblicazioneTracciamento'));
