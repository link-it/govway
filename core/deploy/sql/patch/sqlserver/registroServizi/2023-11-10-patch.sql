ALTER TABLE accordi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE accordi ADD utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD data_creazione DATETIME2;
ALTER TABLE accordi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD data_ultima_modifica DATETIME2;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;


ALTER TABLE servizi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi ADD utente_richiedente VARCHAR(255);
ALTER TABLE servizi ADD data_creazione DATETIME2;
ALTER TABLE servizi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi ADD data_ultima_modifica DATETIME2;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi_fruitori ADD utente_richiedente VARCHAR(255);
ALTER TABLE servizi_fruitori ADD data_creazione DATETIME2;
ALTER TABLE servizi_fruitori ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_fruitori ADD data_ultima_modifica DATETIME2;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;


ALTER TABLE soggetti ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE soggetti ADD utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD data_creazione DATETIME2;
ALTER TABLE soggetti ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD data_ultima_modifica DATETIME2;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;


ALTER TABLE scope ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE scope ADD utente_richiedente VARCHAR(255);
ALTER TABLE scope ADD data_creazione DATETIME2;
ALTER TABLE scope ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE scope ADD data_ultima_modifica DATETIME2;
UPDATE scope SET utente_richiedente=superuser;
UPDATE scope SET data_creazione=ora_registrazione;


ALTER TABLE ruoli ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE ruoli ADD utente_richiedente VARCHAR(255);
ALTER TABLE ruoli ADD data_creazione DATETIME2;
ALTER TABLE ruoli ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE ruoli ADD data_ultima_modifica DATETIME2;
UPDATE ruoli SET utente_richiedente=superuser;
UPDATE ruoli SET data_creazione=ora_registrazione;


ALTER TABLE gruppi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE gruppi ADD utente_richiedente VARCHAR(255);
ALTER TABLE gruppi ADD data_creazione DATETIME2;
ALTER TABLE gruppi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE gruppi ADD data_ultima_modifica DATETIME2;
UPDATE gruppi SET utente_richiedente=superuser;
UPDATE gruppi SET data_creazione=ora_registrazione;

