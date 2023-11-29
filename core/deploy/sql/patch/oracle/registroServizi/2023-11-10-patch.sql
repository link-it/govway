ALTER TABLE accordi MODIFY descrizione VARCHAR2(4000);
ALTER TABLE accordi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE accordi ADD data_creazione TIMESTAMP;
ALTER TABLE accordi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE accordi ADD data_ultima_modifica TIMESTAMP;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;


ALTER TABLE servizi MODIFY descrizione VARCHAR2(4000);
ALTER TABLE servizi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE servizi ADD data_creazione TIMESTAMP;
ALTER TABLE servizi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE servizi ADD data_ultima_modifica TIMESTAMP;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ADD descrizione VARCHAR2(4000);
ALTER TABLE servizi_fruitori ADD utente_richiedente VARCHAR2(255);
ALTER TABLE servizi_fruitori ADD data_creazione TIMESTAMP;
ALTER TABLE servizi_fruitori ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE servizi_fruitori ADD data_ultima_modifica TIMESTAMP;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;


ALTER TABLE soggetti MODIFY descrizione VARCHAR2(4000);
ALTER TABLE soggetti ADD utente_richiedente VARCHAR2(255);
ALTER TABLE soggetti ADD data_creazione TIMESTAMP;
ALTER TABLE soggetti ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE soggetti ADD data_ultima_modifica TIMESTAMP;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;


ALTER TABLE scope MODIFY descrizione VARCHAR2(4000);
ALTER TABLE scope ADD utente_richiedente VARCHAR2(255);
ALTER TABLE scope ADD data_creazione TIMESTAMP;
ALTER TABLE scope ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE scope ADD data_ultima_modifica TIMESTAMP;
UPDATE scope SET utente_richiedente=superuser;
UPDATE scope SET data_creazione=ora_registrazione;


ALTER TABLE ruoli MODIFY descrizione VARCHAR2(4000);
ALTER TABLE ruoli ADD utente_richiedente VARCHAR2(255);
ALTER TABLE ruoli ADD data_creazione TIMESTAMP;
ALTER TABLE ruoli ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE ruoli ADD data_ultima_modifica TIMESTAMP;
UPDATE ruoli SET utente_richiedente=superuser;
UPDATE ruoli SET data_creazione=ora_registrazione;


ALTER TABLE gruppi MODIFY descrizione VARCHAR2(4000);
ALTER TABLE gruppi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE gruppi ADD data_creazione TIMESTAMP;
ALTER TABLE gruppi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE gruppi ADD data_ultima_modifica TIMESTAMP;
UPDATE gruppi SET utente_richiedente=superuser;
UPDATE gruppi SET data_creazione=ora_registrazione;

