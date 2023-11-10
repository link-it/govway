-- ALTER TABLE accordi MODIFY descrizione CLOB;
ALTER TABLE accordi ADD temp CLOB;
UPDATE accordi SET temp=descrizione;
ALTER TABLE accordi DROP COLUMN descrizione;
ALTER TABLE accordi RENAME COLUMN temp TO descrizione;

ALTER TABLE accordi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE accordi ADD data_creazione TIMESTAMP;
ALTER TABLE accordi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE accordi ADD data_ultima_modifica TIMESTAMP;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;

