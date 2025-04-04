-- **** Audit Appenders ****

CREATE TABLE audit_operations
(
	tipo_operazione VARCHAR(255) NOT NULL,
	-- non definito in caso di LOGIN/LOGOUT
	tipo VARCHAR(255),
	-- non definito in caso di LOGIN/LOGOUT
	object_id VARCHAR(2000),
	object_old_id VARCHAR(2000),
	utente VARCHAR(255) NOT NULL,
	stato VARCHAR(255) NOT NULL,
	-- Dettaglio oggetto in gestione (Rappresentazione tramite JSON o XML format)
	object_details MEDIUMTEXT,
	-- Class, serve eventualmente per riottenere l'oggetto dal dettaglio
	-- non definito in caso di LOGIN/LOGOUT
	object_class VARCHAR(255),
	-- Eventuale messaggio di errore
	error TEXT,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	time_request TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	time_execute TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_audit_operations_1 CHECK (tipo_operazione IN ('LOGIN','LOGOUT','ADD','CHANGE','DEL')),
	CONSTRAINT chk_audit_operations_2 CHECK (stato IN ('requesting','error','completed')),
	-- fk/pk keys constraints
	CONSTRAINT pk_audit_operations PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX audit_filter_time ON audit_operations (time_request DESC);
CREATE INDEX audit_object_id ON audit_operations (object_id);
CREATE INDEX audit_object_old_id ON audit_operations (object_old_id);
CREATE INDEX audit_filter ON audit_operations (tipo_operazione,tipo,object_id,utente,stato);



CREATE TABLE audit_binaries
(
	binary_id VARCHAR(255) NOT NULL,
	checksum BIGINT NOT NULL,
	id_audit_operation BIGINT NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	time_rec TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_audit_binaries_1 UNIQUE (binary_id,id_audit_operation),
	-- fk/pk keys constraints
	CONSTRAINT fk_audit_binaries_1 FOREIGN KEY (id_audit_operation) REFERENCES audit_operations(id),
	CONSTRAINT pk_audit_binaries PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_audit_binaries_1 ON audit_binaries (binary_id,id_audit_operation);


