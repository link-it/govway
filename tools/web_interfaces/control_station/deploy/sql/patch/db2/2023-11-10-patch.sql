-- ALTER TABLE soggetti ALTER COLUMN descrizione SET DATA TYPE CLOB;
ALTER TABLE soggetti ADD temp CLOB;
UPDATE soggetti SET temp=descrizione;
ALTER TABLE soggetti DROP COLUMN descrizione;
ALTER TABLE soggetti RENAME COLUMN temp TO descrizione; 
CALL SYSPROC.ADMIN_CMD ('REORG TABLE soggetti') ;

ALTER TABLE soggetti ADD utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD data_creazione TIMESTAMP;
ALTER TABLE soggetti ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD data_ultima_modifica TIMESTAMP;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;
