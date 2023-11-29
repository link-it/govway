ALTER TABLE soggetti MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE soggetti ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE soggetti ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;

