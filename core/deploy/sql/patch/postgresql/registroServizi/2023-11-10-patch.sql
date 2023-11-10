ALTER TABLE accordi ALTER COLUMN descrizione TYPE TEXT;
ALTER TABLE accordi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE accordi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;

