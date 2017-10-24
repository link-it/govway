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
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
);



