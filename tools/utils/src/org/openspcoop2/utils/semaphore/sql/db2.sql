-- **** Semaphore ****

CREATE TABLE OP2_SEMAPHORE
(
	node_id VARCHAR(255),
	creation_time TIMESTAMP,
	update_time TIMESTAMP,
	details CLOB,
	applicative_id VARCHAR(255),
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT uniq_semaphore_1 UNIQUE (applicative_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_semaphore_1 ON OP2_SEMAPHORE (applicative_id);


