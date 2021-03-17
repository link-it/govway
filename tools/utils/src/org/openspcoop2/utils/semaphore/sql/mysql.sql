-- **** Semaphore ****

CREATE TABLE OP2_SEMAPHORE
(
	node_id VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	creation_time TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	update_time TIMESTAMP(3) DEFAULT 0,
	details TEXT,
	applicative_id VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_semaphore_1 UNIQUE (applicative_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX idx_semaphore_1 ON OP2_SEMAPHORE (applicative_id);


