ALTER TABLE statistiche ADD id BIGINT;

WITH CTE AS (
    SELECT tipo, ROW_NUMBER() OVER (ORDER BY tipo) AS new_id
    FROM statistiche
)
UPDATE s
SET id = cte.new_id
FROM statistiche s
INNER JOIN CTE cte ON s.tipo = cte.tipo;

ALTER TABLE statistiche ALTER COLUMN id BIGINT NOT NULL;

ALTER TABLE statistiche DROP CONSTRAINT unique_statistiche_1;
ALTER TABLE statistiche DROP CONSTRAINT chk_statistiche_1;

CREATE TABLE statistiche_new
(
	tipo VARCHAR(255) NOT NULL,
	data_ultima_generazione DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili')),
	-- unique constraints
	CONSTRAINT unique_statistiche_1 UNIQUE (tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche PRIMARY KEY (id)
);

SET IDENTITY_INSERT statistiche_new ON;
INSERT INTO statistiche_new (id, tipo, data_ultima_generazione)
SELECT id, tipo, data_ultima_generazione FROM statistiche;
SET IDENTITY_INSERT statistiche_new OFF;

DROP TABLE statistiche;

EXEC sp_rename 'statistiche_new', 'statistiche';



ALTER TABLE transazioni_info ADD id BIGINT;

WITH CTE AS (
    SELECT tipo, ROW_NUMBER() OVER (ORDER BY tipo) AS new_id
    FROM transazioni_info
)
UPDATE s
SET id = cte.new_id
FROM transazioni_info s
INNER JOIN CTE cte ON s.tipo = cte.tipo;

ALTER TABLE transazioni_info ALTER COLUMN id BIGINT NOT NULL;

ALTER TABLE transazioni_info DROP CONSTRAINT unique_transazioni_info_1;

CREATE TABLE transazioni_info_new
(
	tipo VARCHAR(255) NOT NULL,
	data DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_transazioni_info_1 UNIQUE (tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_info PRIMARY KEY (id)
);


SET IDENTITY_INSERT transazioni_info_new ON;
INSERT INTO transazioni_info_new (id, tipo, data)
SELECT id, tipo, data FROM transazioni_info;
SET IDENTITY_INSERT transazioni_info_new OFF;

DROP TABLE transazioni_info;

EXEC sp_rename 'transazioni_info_new', 'transazioni_info';
