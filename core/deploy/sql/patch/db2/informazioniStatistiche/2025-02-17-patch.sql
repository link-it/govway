ALTER TABLE statistiche DROP CONSTRAINT unique_statistiche_1;
ALTER TABLE statistiche DROP CONSTRAINT chk_statistiche_1;
CREATE TABLE statistiche_new
(
	tipo VARCHAR(255) NOT NULL,
	data_ultima_generazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili')),
	-- unique constraints
	CONSTRAINT unique_statistiche_1 UNIQUE (tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche PRIMARY KEY (id)
);
INSERT INTO statistiche_new (tipo, data_ultima_generazione) SELECT tipo, data_ultima_generazione FROM statistiche;
DROP TABLE statistiche;
RENAME TABLE statistiche_new TO statistiche;
