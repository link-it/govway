-- **** Audit Appenders ****

CREATE SEQUENCE seq_audit_operations start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE audit_operations
(
	tipo_operazione VARCHAR(255) NOT NULL,
	-- non definito in caso di LOGIN/LOGOUT
	tipo VARCHAR(255),
	-- non definito in caso di LOGIN/LOGOUT
	object_id TEXT,
	object_old_id TEXT,
	utente VARCHAR(255) NOT NULL,
	stato VARCHAR(255) NOT NULL,
	-- Dettaglio oggetto in gestione (Rappresentazione tramite JSON o XML format)
	object_details TEXT,
	-- Class, serve eventualmente per riottenere l'oggetto dal dettaglio
	-- non definito in caso di LOGIN/LOGOUT
	object_class VARCHAR(255),
	-- Eventuale messaggio di errore
	error TEXT,
	time_request TIMESTAMP NOT NULL,
	time_execute TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_audit_operations') NOT NULL,
	-- check constraints
	CONSTRAINT chk_audit_operations_1 CHECK (tipo_operazione IN ('LOGIN','LOGOUT','ADD','CHANGE','DEL')),
	CONSTRAINT chk_audit_operations_2 CHECK (stato IN ('requesting','error','completed')),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX audit_filter_time ON audit_operations (time_request);
CREATE INDEX audit_filter ON audit_operations (tipo_operazione,tipo,utente,stato);



CREATE SEQUENCE seq_audit_binaries start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE audit_binaries
(
	binary_id VARCHAR(255) NOT NULL,
	checksum BIGINT NOT NULL,
	id_audit_operation BIGINT NOT NULL,
	time_rec TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_audit_binaries') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_audit_binaries_1 UNIQUE (binary_id,id_audit_operation),
	-- fk/pk keys constraints
	CONSTRAINT fk_audit_binaries_1 FOREIGN KEY (id_audit_operation) REFERENCES audit_operations(id),
	CONSTRAINT pk_audit_binaries PRIMARY KEY (id)
);



