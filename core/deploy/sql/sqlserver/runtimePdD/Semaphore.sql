-- **** Semaphore ****

CREATE TABLE OP2_SEMAPHORE
(
	node_id VARCHAR(255),
	creation_time DATETIME2,
	update_time DATETIME2,
	details VARCHAR(max),
	applicative_id VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
);



