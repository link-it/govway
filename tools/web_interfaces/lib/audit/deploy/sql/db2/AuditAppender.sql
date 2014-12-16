-- **** Audit Appenders ****

CREATE TABLE audit_operations
(
	tipo_operazione VARCHAR(255) NOT NULL,
	-- non definito in caso di LOGIN/LOGOUT
	tipo VARCHAR(255),
	-- non definito in caso di LOGIN/LOGOUT
	object_id CLOB,
	object_old_id CLOB,
	utente VARCHAR(255) NOT NULL,
	stato VARCHAR(255) NOT NULL,
	-- Dettaglio oggetto in gestione (Rappresentazione tramite JSON o XML format)
	object_details CLOB,
	-- Class, serve eventualmente per riottenere l'oggetto dal dettaglio
	-- non definito in caso di LOGIN/LOGOUT
	object_class VARCHAR(255),
	-- Eventuale messaggio di errore
	error CLOB,
	time_request TIMESTAMP NOT NULL,
	time_execute TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_audit_operations_1 CHECK (tipo_operazione IN ('LOGIN','LOGOUT','ADD','CHANGE','DEL')),
	CONSTRAINT chk_audit_operations_2 CHECK (stato IN ('requesting','error','completed')),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX audit_filter_time ON audit_operations (time_request);
CREATE INDEX audit_filter ON audit_operations (tipo_operazione,tipo,utente,stato);



CREATE TABLE audit_binaries
(
	binary_id VARCHAR(255) NOT NULL,
	checksum BIGINT NOT NULL,
	id_audit_operation BIGINT NOT NULL,
	time_rec TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_audit_binaries_1 UNIQUE (binary_id,id_audit_operation),
	-- fk/pk keys constraints
	CONSTRAINT fk_audit_binaries_1 FOREIGN KEY (id_audit_operation) REFERENCES audit_operations(id),
	CONSTRAINT pk_audit_binaries PRIMARY KEY (id)
);

-- index
CREATE INDEX index_audit_binaries_1 ON audit_binaries (binary_id,id_audit_operation);


