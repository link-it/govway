-- **** Semaphore ****

CREATE TABLE OP2_SEMAPHORE
(
	node_id VARCHAR(255),
	creation_time DATETIME2,
	update_time DATETIME2,
	details VARCHAR(max),
	applicative_id VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_semaphore_1 UNIQUE (applicative_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_semaphore_1 ON OP2_SEMAPHORE (applicative_id);


