-- ALTER TABLE porte_applicative ALTER COLUMN descrizione SET DATA TYPE CLOB;
ALTER TABLE porte_applicative ADD temp CLOB;
UPDATE porte_applicative SET temp=descrizione;
ALTER TABLE porte_applicative DROP COLUMN descrizione;
ALTER TABLE porte_applicative RENAME COLUMN temp TO descrizione; 
CALL SYSPROC.ADMIN_CMD ('REORG TABLE porte_applicative') ;

ALTER TABLE porte_applicative ADD utente_richiedente VARCHAR(255);
ALTER TABLE porte_applicative ADD data_creazione TIMESTAMP;
ALTER TABLE porte_applicative ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_applicative ADD data_ultima_modifica TIMESTAMP;
UPDATE porte_applicative pa SET utente_richiedente=(select superuser from servizi s where s.id=pa.id_servizio) where pa.id_servizio is not null AND pa.id_servizio>0;
UPDATE porte_applicative SET data_creazione=ora_registrazione;


-- ALTER TABLE porte_delegate ALTER COLUMN descrizione SET DATA TYPE CLOB;
ALTER TABLE porte_delegate ADD temp CLOB;
UPDATE porte_delegate SET temp=descrizione;
ALTER TABLE porte_delegate DROP COLUMN descrizione;
ALTER TABLE porte_delegate RENAME COLUMN temp TO descrizione; 
CALL SYSPROC.ADMIN_CMD ('REORG TABLE porte_delegate') ;

ALTER TABLE porte_delegate ADD utente_richiedente VARCHAR(255);
ALTER TABLE porte_delegate ADD data_creazione TIMESTAMP;
ALTER TABLE porte_delegate ADD utente_ultima_modifica VARCHAR(255);
ALTER TABLE porte_delegate ADD data_ultima_modifica TIMESTAMP;
UPDATE porte_delegate pd SET utente_richiedente=(select superuser from servizi s where s.id=pd.id_servizio) where pd.id_servizio is not null AND pd.id_servizio>0;
UPDATE porte_delegate SET data_creazione=ora_registrazione;

