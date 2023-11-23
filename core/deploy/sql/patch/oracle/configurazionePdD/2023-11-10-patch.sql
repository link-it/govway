-- ALTER TABLE porte_applicative MODIFY descrizione CLOB;
ALTER TABLE porte_applicative ADD temp CLOB;
UPDATE porte_applicative SET temp=descrizione;
ALTER TABLE porte_applicative DROP COLUMN descrizione;
ALTER TABLE porte_applicative RENAME COLUMN temp TO descrizione;

ALTER TABLE porte_applicative ADD utente_richiedente VARCHAR2(255);
ALTER TABLE porte_applicative ADD data_creazione TIMESTAMP;
ALTER TABLE porte_applicative ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE porte_applicative ADD data_ultima_modifica TIMESTAMP;
UPDATE porte_applicative pa SET utente_richiedente=(select superuser from servizi s where s.id=pa.id_servizio) where pa.id_servizio is not null AND pa.id_servizio>0;
UPDATE porte_applicative SET data_creazione=ora_registrazione;


ALTER TABLE porte_applicative_sa ADD utente_richiedente VARCHAR2(255);
ALTER TABLE porte_applicative_sa ADD data_creazione TIMESTAMP;
ALTER TABLE porte_applicative_sa ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE porte_applicative_sa ADD data_ultima_modifica TIMESTAMP;
UPDATE porte_applicative_sa pasa SET utente_richiedente=(select utente_richiedente from porte_applicative pa where pa.id=pasa.id_porta AND pa.utente_richiedente is not null);
UPDATE porte_applicative_sa pasa SET data_creazione=(select data_creazione from porte_applicative pa where pa.id=pasa.id_porta AND pa.data_creazione is not null);


-- ALTER TABLE porte_delegate MODIFY descrizione CLOB;
ALTER TABLE porte_delegate ADD temp CLOB;
UPDATE porte_delegate SET temp=descrizione;
ALTER TABLE porte_delegate DROP COLUMN descrizione;
ALTER TABLE porte_delegate RENAME COLUMN temp TO descrizione;

ALTER TABLE porte_delegate ADD utente_richiedente VARCHAR2(255);
ALTER TABLE porte_delegate ADD data_creazione TIMESTAMP;
ALTER TABLE porte_delegate ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE porte_delegate ADD data_ultima_modifica TIMESTAMP;
UPDATE porte_delegate pd SET utente_richiedente=(select superuser from servizi s where s.id=pd.id_servizio) where pd.id_servizio is not null AND pd.id_servizio>0;
UPDATE porte_delegate SET data_creazione=ora_registrazione;


-- ALTER TABLE servizi_applicativi MODIFY descrizione CLOB;
ALTER TABLE servizi_applicativi ADD temp CLOB;
UPDATE servizi_applicativi SET temp=descrizione;
ALTER TABLE servizi_applicativi DROP COLUMN descrizione;
ALTER TABLE servizi_applicativi RENAME COLUMN temp TO descrizione;

ALTER TABLE servizi_applicativi ADD utente_richiedente VARCHAR2(255);
ALTER TABLE servizi_applicativi ADD data_creazione TIMESTAMP;
ALTER TABLE servizi_applicativi ADD utente_ultima_modifica VARCHAR2(255);
ALTER TABLE servizi_applicativi ADD data_ultima_modifica TIMESTAMP;
UPDATE servizi_applicativi sa SET utente_richiedente=(select superuser from soggetti s where s.id=sa.id_soggetto) where sa.id_soggetto is not null AND sa.id_soggetto>0;
UPDATE servizi_applicativi SET data_creazione=ora_registrazione;
