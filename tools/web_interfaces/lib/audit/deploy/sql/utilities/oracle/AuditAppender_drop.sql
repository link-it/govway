-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX audit_filter;
-- DROP INDEX audit_filter_time;
DROP TRIGGER trg_audit_binaries;
DROP TRIGGER trg_audit_operations;
DROP TABLE audit_binaries;
DROP TABLE audit_operations;
DROP SEQUENCE seq_audit_binaries;
DROP SEQUENCE seq_audit_operations;
