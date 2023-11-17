ALTER TABLE porte_applicative ALTER COLUMN descrizione LONGVARCHAR;
ALTER TABLE porte_applicative ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE porte_applicative ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE porte_applicative pa SET utente_richiedente=(select superuser from servizi s where s.id=pa.id_servizio) where pa.id_servizio is not null AND pa.id_servizio>0;
UPDATE porte_applicative SET data_creazione=ora_registrazione;


ALTER TABLE porte_delegate ALTER COLUMN descrizione LONGVARCHAR;
ALTER TABLE porte_delegate ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE porte_delegate ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE porte_delegate pd SET utente_richiedente=(select superuser from servizi s where s.id=pd.id_servizio) where pd.id_servizio is not null AND pd.id_servizio>0;
UPDATE porte_delegate SET data_creazione=ora_registrazione;


ALTER TABLE servizi_applicativi ALTER COLUMN descrizione LONGVARCHAR;
ALTER TABLE servizi_applicativi ADD COLUMN utente_richiedente VARCHAR(255);
ALTER TABLE servizi_applicativi ADD COLUMN data_creazione TIMESTAMP;
ALTER TABLE servizi_applicativi ADD COLUMN utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_applicativi ADD COLUMN data_ultima_modifica TIMESTAMP;
UPDATE servizi_applicativi sa SET utente_richiedente=(select superuser from soggetti s where s.id=sa.id_soggetto) where sa.id_soggetto is not null AND sa.id_soggetto>0;
UPDATE servizi_applicativi SET data_creazione=ora_registrazione;
