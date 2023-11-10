-- ALTER TABLE accordi ALTER COLUMN descrizione SET DATA TYPE CLOB;
ALTER TABLE accordi ADD temp CLOB;
UPDATE accordi SET temp=descrizione;
ALTER TABLE accordi DROP COLUMN descrizione;
ALTER TABLE accordi RENAME COLUMN temp TO descrizione; 
CALL SYSPROC.ADMIN_CMD ('REORG TABLE accordi') ;

ALTER TABLE accordi ADD utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD data_creazione TIMESTAMP;
ALTER TABLE accordi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD data_ultima_modifica TIMESTAMP;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;

