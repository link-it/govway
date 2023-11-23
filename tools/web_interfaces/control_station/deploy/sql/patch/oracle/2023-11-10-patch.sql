-- ALTER TABLE soggetti MODIFY descrizione CLOB;
ALTER TABLE soggetti ADD temp CLOB;
UPDATE soggetti SET temp=descrizione;
ALTER TABLE soggetti DROP COLUMN descrizione;
ALTER TABLE soggetti RENAME COLUMN temp TO descrizione;

ALTER TABLE soggetti ADD utente_richiedente VARCHAR2(255);
ALTER TABLE soggetti ADD data_creazione TIMESTAMP;
ALTER TABLE soggetti ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE soggetti ADD data_ultima_modifica TIMESTAMP;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;
