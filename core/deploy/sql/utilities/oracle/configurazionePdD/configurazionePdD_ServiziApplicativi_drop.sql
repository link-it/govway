-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX INDEX_SA_CRED;
-- DROP INDEX INDEX_SA_RUOLI;
-- DROP INDEX index_servizi_applicativi_1;
DROP TRIGGER trg_sa_credenziali;
DROP TRIGGER trg_sa_ruoli;
DROP TRIGGER trg_servizi_applicativi;
DROP TABLE sa_credenziali;
DROP TABLE sa_ruoli;
DROP TABLE servizi_applicativi;
DROP SEQUENCE seq_sa_credenziali;
DROP SEQUENCE seq_sa_ruoli;
DROP SEQUENCE seq_servizi_applicativi;
