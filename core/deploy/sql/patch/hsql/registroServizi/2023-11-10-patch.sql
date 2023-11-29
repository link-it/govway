ALTER TABLE accordi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE accordi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE accordi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;


ALTER TABLE servizi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE servizi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ADD COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi_fruitori ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi_fruitori ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE servizi_fruitori ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_fruitori ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;


ALTER TABLE soggetti ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE soggetti ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE soggetti ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;


ALTER TABLE scope ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE scope ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE scope ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE scope ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE scope ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE scope SET utente_richiedente=superuser;
UPDATE scope SET data_creazione=ora_registrazione;


ALTER TABLE ruoli ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE ruoli ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE ruoli ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE ruoli ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE ruoli ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE ruoli SET utente_richiedente=superuser;
UPDATE ruoli SET data_creazione=ora_registrazione;


ALTER TABLE gruppi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE gruppi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE gruppi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE gruppi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE gruppi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE gruppi SET utente_richiedente=superuser;
UPDATE gruppi SET data_creazione=ora_registrazione;

