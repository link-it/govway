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


-- ALTER TABLE servizi MODIFY descrizione CLOB;
ALTER TABLE servizi ADD temp CLOB;
UPDATE servizi SET temp=descrizione;
ALTER TABLE servizi DROP COLUMN descrizione;
ALTER TABLE servizi RENAME COLUMN temp TO descrizione;

ALTER TABLE servizi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE servizi ADD data_creazione TIMESTAMP;
ALTER TABLE servizi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE servizi ADD data_ultima_modifica TIMESTAMP;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ADD descrizione CLOB;
ALTER TABLE servizi_fruitori ADD utente_richiedente VARCHAR2(255);
ALTER TABLE servizi_fruitori ADD data_creazione TIMESTAMP;
ALTER TABLE servizi_fruitori ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE servizi_fruitori ADD data_ultima_modifica TIMESTAMP;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;


-- ALTER TABLE soggetti MODIFY descrizione CLOB;
ALTER TABLE soggetti ADD temp CLOB;
UPDATE soggetti SET temp=descrizione;
ALTER TABLE soggetti DROP COLUMN descrizione;
ALTER TABLE soggetti RENAME COLUMN temp TO descrizione;

ALTER TABLE soggetti ADD utente_richiedente VARCHAR2(255);
ALTER TABLE soggetti ADD data_creazione TIMESTAMP;
ALTER TABLE soggetti ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE soggetti ADD data_ultima_modifica TIMESTAMP;
UPDATE soggetti SET utente_richiedente=superuser;
UPDATE soggetti SET data_creazione=ora_registrazione;


-- ALTER TABLE scope MODIFY descrizione CLOB;
ALTER TABLE scope ADD temp CLOB;
UPDATE scope SET temp=descrizione;
ALTER TABLE scope DROP COLUMN descrizione;
ALTER TABLE scope RENAME COLUMN temp TO descrizione;

ALTER TABLE scope ADD utente_richiedente VARCHAR2(255);
ALTER TABLE scope ADD data_creazione TIMESTAMP;
ALTER TABLE scope ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE scope ADD data_ultima_modifica TIMESTAMP;
UPDATE scope SET utente_richiedente=superuser;
UPDATE scope SET data_creazione=ora_registrazione;


-- ALTER TABLE ruoli MODIFY descrizione CLOB;
ALTER TABLE ruoli ADD temp CLOB;
UPDATE ruoli SET temp=descrizione;
ALTER TABLE ruoli DROP COLUMN descrizione;
ALTER TABLE ruoli RENAME COLUMN temp TO descrizione;

ALTER TABLE ruoli ADD utente_richiedente VARCHAR2(255);
ALTER TABLE ruoli ADD data_creazione TIMESTAMP;
ALTER TABLE ruoli ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE ruoli ADD data_ultima_modifica TIMESTAMP;
UPDATE ruoli SET utente_richiedente=superuser;
UPDATE ruoli SET data_creazione=ora_registrazione;


-- ALTER TABLE gruppi MODIFY descrizione CLOB;
ALTER TABLE gruppi ADD temp CLOB;
UPDATE gruppi SET temp=descrizione;
ALTER TABLE gruppi DROP COLUMN descrizione;
ALTER TABLE gruppi RENAME COLUMN temp TO descrizione;

ALTER TABLE gruppi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE gruppi ADD data_creazione TIMESTAMP;
ALTER TABLE gruppi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE gruppi ADD data_ultima_modifica TIMESTAMP;
UPDATE gruppi SET utente_richiedente=superuser;
UPDATE gruppi SET data_creazione=ora_registrazione;

