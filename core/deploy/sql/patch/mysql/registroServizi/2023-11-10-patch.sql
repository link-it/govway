ALTER TABLE accordi MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE accordi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE accordi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;


ALTER TABLE servizi MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE servizi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ADD COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi_fruitori ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi_fruitori ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE servizi_fruitori ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_fruitori ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;


ALTER TABLE soggetti MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE soggetti ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE soggetti ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;


ALTER TABLE scope MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE scope ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE scope ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE scope ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE scope ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE scope SET utente_richiedente=superuser;
UPDATE scope SET data_creazione=ora_registrazione;


ALTER TABLE ruoli MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE ruoli ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE ruoli ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE ruoli ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE ruoli ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE ruoli SET utente_richiedente=superuser;
UPDATE ruoli SET data_creazione=ora_registrazione;


ALTER TABLE gruppi MODIFY COLUMN descrizione VARCHAR(4000);
ALTER TABLE gruppi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE gruppi ADD COLUMN data_creazione TIMESTAMP(3) DEFAULT 0;
ALTER TABLE gruppi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE gruppi ADD COLUMN data_ultima_modifica TIMESTAMP(3) DEFAULT 0;
UPDATE gruppi SET utente_richiedente=superuser;
UPDATE gruppi SET data_creazione=ora_registrazione;

