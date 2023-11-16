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

