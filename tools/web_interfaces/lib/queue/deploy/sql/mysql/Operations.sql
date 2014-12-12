-- **** Operations ****

CREATE TABLE operations
(
	operation VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	superuser VARCHAR(255) NOT NULL,
	hostname VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	details TEXT,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	timereq TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	timexecute TIMESTAMP(3) NOT NULL DEFAULT 0,
	deleted INT NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_operations PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX operations_superuser ON operations (superuser);
CREATE INDEX operations_precedenti ON operations (id,deleted,hostname,timereq);



CREATE TABLE parameters
(
	id_operations BIGINT NOT NULL,
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_parameters_1 FOREIGN KEY (id_operations) REFERENCES operations(id),
	CONSTRAINT pk_parameters PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX parameters_index ON parameters (name,value);


