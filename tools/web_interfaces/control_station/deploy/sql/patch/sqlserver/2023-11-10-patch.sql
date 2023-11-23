ALTER TABLE soggetti ALTER COLUMN descrizione VARCHAR(max);

ALTER TABLE soggetti ADD utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD data_creazione DATETIME2;
ALTER TABLE soggetti ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD data_ultima_modifica DATETIME2;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;

