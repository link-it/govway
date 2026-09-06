DROP INDEX audit_object_id ON audit_operations;

DROP INDEX audit_object_old_id ON audit_operations;

DROP INDEX audit_filter ON audit_operations;
CREATE INDEX audit_filter ON audit_operations (tipo_operazione,tipo,utente,stato);
