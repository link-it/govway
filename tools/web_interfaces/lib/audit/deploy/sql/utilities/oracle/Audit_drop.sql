-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_audit_appender_prop;
DROP TRIGGER trg_audit_appender;
DROP TRIGGER trg_audit_filters;
DROP TRIGGER trg_audit_conf;
DROP TABLE audit_appender_prop;
DROP TABLE audit_appender;
DROP TABLE audit_filters;
DROP TABLE audit_conf;
DROP SEQUENCE seq_audit_appender_prop;
DROP SEQUENCE seq_audit_appender;
DROP SEQUENCE seq_audit_filters;
DROP SEQUENCE seq_audit_conf;
