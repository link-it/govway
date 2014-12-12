-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX parameters_index;
-- DROP INDEX operations_precedenti;
-- DROP INDEX operations_superuser;
DROP TRIGGER trg_parameters;
DROP TRIGGER trg_operations;
DROP TABLE parameters;
DROP TABLE operations;
DROP SEQUENCE seq_parameters;
DROP SEQUENCE seq_operations;
