-- **** Audit Appenders ****

CREATE SEQUENCE seq_audit_operations MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE audit_operations
(
	tipo_operazione VARCHAR2(255) NOT NULL,
	-- non definito in caso di LOGIN/LOGOUT
	tipo VARCHAR2(255),
	-- non definito in caso di LOGIN/LOGOUT
	object_id CLOB,
	object_old_id CLOB,
	utente VARCHAR2(255) NOT NULL,
	stato VARCHAR2(255) NOT NULL,
	-- Dettaglio oggetto in gestione (Rappresentazione tramite JSON o XML format)
	object_details CLOB,
	-- Class, serve eventualmente per riottenere l'oggetto dal dettaglio
	-- non definito in caso di LOGIN/LOGOUT
	object_class VARCHAR2(255),
	-- Eventuale messaggio di errore
	error CLOB,
	time_request TIMESTAMP NOT NULL,
	time_execute TIMESTAMP NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_audit_operations_1 CHECK (tipo_operazione IN ('LOGIN','LOGOUT','ADD','CHANGE','DEL')),
	CONSTRAINT chk_audit_operations_2 CHECK (stato IN ('requesting','error','completed')),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX audit_filter_time ON audit_operations (time_request);
CREATE INDEX audit_filter ON audit_operations (tipo_operazione,tipo,utente,stato);
CREATE TRIGGER trg_audit_operations
BEFORE
insert on audit_operations
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_audit_operations.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_audit_binaries MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE audit_binaries
(
	binary_id VARCHAR2(255) NOT NULL,
	checksum NUMBER NOT NULL,
	id_audit_operation NUMBER NOT NULL,
	time_rec TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_audit_binaries_1 UNIQUE (binary_id,id_audit_operation),
	-- fk/pk keys constraints
	CONSTRAINT fk_audit_binaries_1 FOREIGN KEY (id_audit_operation) REFERENCES audit_operations(id),
	CONSTRAINT pk_audit_binaries PRIMARY KEY (id)
);


ALTER TABLE audit_binaries MODIFY time_rec DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_audit_binaries
BEFORE
insert on audit_binaries
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_audit_binaries.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


