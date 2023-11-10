ALTER TABLE accordi ALTER COLUMN descrizione VARCHAR(max);

ALTER TABLE accordi ADD utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD data_creazione DATETIME2;
ALTER TABLE accordi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD data_ultima_modifica DATETIME2;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;

