ALTER TABLE audit_operations ADD COLUMN temp VARCHAR(4000); 
UPDATE audit_operations SET temp=object_id; 
ALTER TABLE audit_operations DROP COLUMN object_id; 
ALTER TABLE audit_operations CHANGE COLUMN temp object_id VARCHAR(4000);

ALTER TABLE audit_operations ADD COLUMN temp VARCHAR(4000); 
UPDATE audit_operations SET temp=object_old_id;      
ALTER TABLE audit_operations DROP COLUMN object_old_id;      
ALTER TABLE audit_operations CHANGE COLUMN temp object_old_id VARCHAR(4000);

DROP INDEX audit_filter_time;
CREATE INDEX audit_filter_time ON audit_operations (time_request DESC);

DROP INDEX audit_filter;
CREATE INDEX audit_filter ON audit_operations (tipo_operazione,tipo,object_id,utente,stato);

CREATE INDEX audit_object_id ON audit_operations (object_id);
CREATE INDEX audit_object_old_id ON audit_operations (object_old_id);
 
DROP INDEX index_audit_binaries_1;
CREATE UNIQUE INDEX index_audit_binaries_1 ON audit_binaries (binary_id,id_audit_operation);

