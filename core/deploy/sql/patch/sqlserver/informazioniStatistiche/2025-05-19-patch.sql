
-- STATISTICHE TRACING PDND

CREATE TABLE statistiche_pdnd_tracing
(
	data_tracciamento DATETIME2 NOT NULL,
	data_registrazione DATETIME2,
	pdd_codice VARCHAR(255) NOT NULL,
	csv VARBINARY(MAX),
	method VARCHAR(20),
	stato_pdnd VARCHAR(20) NOT NULL DEFAULT 'WAITING',
	tentativi_pubblicazione INT NOT NULL DEFAULT 0,
	stato VARCHAR(20),
	tracing_id VARCHAR(36),
	error_details VARCHAR(255),
	history INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_statistiche_pdnd_tracing_1 CHECK (method IN ('REPLACE','RECOVER','SUBMIT')),
	CONSTRAINT chk_statistiche_pdnd_tracing_2 CHECK (stato_pdnd IN ('WAITING','PENDING','OK','ERROR')),
	CONSTRAINT chk_statistiche_pdnd_tracing_3 CHECK (stato IN ('PUBLISHED','FAILED')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_pdnd_tracing PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PDND_TRACING_ACTIVE ON statistiche_pdnd_tracing (pdd_codice,data_tracciamento DESC,history);

-- constraint
ALTER TABLE statistiche DROP CONSTRAINT chk_statistiche_1;
ALTER TABLE statistiche ADD CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili','PdndGenerazioneTracciamento','PdndPubblicazioneTracciamento'));

