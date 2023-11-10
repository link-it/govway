ALTER TABLE accordi MODIFY COLUMN descrizione MEDIUMTEXT;
ALTER TABLE accordi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE accordi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;

