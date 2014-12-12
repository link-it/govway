-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX MSG_CORR_APP;
-- DROP INDEX MSG_CORR_GDO;
-- DROP INDEX MSG_CORR_INDEX;
-- DROP INDEX MSG_DIAG_GDO;
-- DROP INDEX MSG_DIAG_ID;
DROP TRIGGER trg_msgdiag_correlazione;
DROP TRIGGER trg_msgdiagnostici;
DROP TABLE msgdiag_correlazione_sa;
DROP TABLE msgdiag_correlazione;
DROP TABLE msgdiagnostici;
DROP SEQUENCE seq_msgdiag_correlazione;
DROP SEQUENCE seq_msgdiagnostici;
