ALTER TABLE accordi ALTER COLUMN descrizione VARCHAR(max);

ALTER TABLE accordi ADD utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD data_creazione DATETIME2;
ALTER TABLE accordi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD data_ultima_modifica DATETIME2;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;


ALTER TABLE servizi ALTER COLUMN descrizione VARCHAR(max);

ALTER TABLE servizi ADD utente_richiedente VARCHAR(255);
ALTER TABLE servizi ADD data_creazione DATETIME2;
ALTER TABLE servizi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi ADD data_ultima_modifica DATETIME2;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ALTER COLUMN descrizione VARCHAR(max);

ALTER TABLE servizi_fruitori ADD utente_richiedente VARCHAR(255);
ALTER TABLE servizi_fruitori ADD data_creazione DATETIME2;
ALTER TABLE servizi_fruitori ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_fruitori ADD data_ultima_modifica DATETIME2;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;

