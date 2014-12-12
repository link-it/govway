-- **** Operations ****

CREATE TABLE operations
(
	operation VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	superuser VARCHAR(255) NOT NULL,
	hostname VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	details VARCHAR(max),
	timereq DATETIME2 NOT NULL,
	timexecute DATETIME2 NOT NULL,
	deleted INT NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX operations_superuser ON operations (superuser);
CREATE INDEX operations_precedenti ON operations (id,deleted,hostname,timereq);



CREATE TABLE parameters
(
	id_operations BIGINT NOT NULL,
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_parameters_1 FOREIGN KEY (id_operations) REFERENCES operations(id),
	CONSTRAINT pk_parameters PRIMARY KEY (id)
);

-- index
CREATE INDEX parameters_index ON parameters (name,value);


