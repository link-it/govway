ALTER TABLE soggetti ALTER COLUMN descrizione TYPE VARCHAR(4000);
ALTER TABLE soggetti ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE soggetti ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;

