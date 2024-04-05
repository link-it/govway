ALTER TABLE porte_applicative ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE porte_applicative ADD utente_richiedente VARCHAR(255);
ALTER TABLE porte_applicative ADD data_creazione DATETIME2;
ALTER TABLE porte_applicative ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_applicative ADD data_ultima_modifica DATETIME2;
UPDATE porte_applicative SET utente_richiedente=(select superuser from servizi s where s.id=porte_applicative.id_servizio) where porte_applicative.id_servizio is not null AND porte_applicative.id_servizio>0;
UPDATE porte_applicative SET data_creazione=ora_registrazione;


ALTER TABLE porte_applicative_sa ADD utente_richiedente VARCHAR(255);
ALTER TABLE porte_applicative_sa ADD data_creazione DATETIME2;
ALTER TABLE porte_applicative_sa ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_applicative_sa ADD data_ultima_modifica DATETIME2;
UPDATE porte_applicative_sa SET utente_richiedente=(select utente_richiedente from porte_applicative pa where pa.id=porte_applicative_sa.id_porta AND pa.utente_richiedente is not null);
UPDATE porte_applicative_sa SET data_creazione=(select data_creazione from porte_applicative pa where pa.id=porte_applicative_sa.id_porta AND pa.data_creazione is not null);


ALTER TABLE porte_delegate ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE porte_delegate ADD utente_richiedente VARCHAR(255);
ALTER TABLE porte_delegate ADD data_creazione DATETIME2;
ALTER TABLE porte_delegate ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_delegate ADD data_ultima_modifica DATETIME2;
UPDATE porte_delegate SET utente_richiedente=(select superuser from servizi s where s.id=porte_delegate.id_servizio) where porte_delegate.id_servizio is not null AND porte_delegate.id_servizio>0;
UPDATE porte_delegate SET data_creazione=ora_registrazione;


ALTER TABLE servizi_applicativi ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE servizi_applicativi ADD utente_richiedente VARCHAR(255);
ALTER TABLE servizi_applicativi ADD data_creazione DATETIME2;
ALTER TABLE servizi_applicativi ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE servizi_applicativi ADD data_ultima_modifica DATETIME2;
UPDATE servizi_applicativi SET utente_richiedente=(select superuser from soggetti s where s.id=servizi_applicativi.id_soggetto) where servizi_applicativi.id_soggetto is not null AND servizi_applicativi.id_soggetto>0;
UPDATE servizi_applicativi SET data_creazione=ora_registrazione;


ALTER TABLE generic_properties ALTER COLUMN descrizione VARCHAR(4000);
ALTER TABLE generic_properties ADD utente_richiedente VARCHAR(255);
ALTER TABLE generic_properties ADD data_creazione DATETIME2;
ALTER TABLE generic_properties ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE generic_properties ADD data_ultima_modifica DATETIME2;
