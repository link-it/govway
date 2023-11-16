ALTER TABLE accordi ALTER COLUMN descrizione LONGVARCHAR;
ALTER TABLE accordi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE accordi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE accordi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE accordi SET utente_richiedente=superuser;
UPDATE accordi SET data_creazione=ora_registrazione;


ALTER TABLE servizi ALTER COLUMN descrizione LONGVARCHAR;
ALTER TABLE servizi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE servizi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE servizi SET utente_richiedente=superuser;
UPDATE servizi SET data_creazione=ora_registrazione;


ALTER TABLE servizi_fruitori ADD COLUMN descrizione LONGVARCHAR;
ALTER TABLE servizi_fruitori ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi_fruitori ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE servizi_fruitori ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_fruitori ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE servizi_fruitori sf SET utente_richiedente=(select superuser from servizi s where s.id=sf.id_servizio);
UPDATE servizi_fruitori SET data_creazione=ora_registrazione;

