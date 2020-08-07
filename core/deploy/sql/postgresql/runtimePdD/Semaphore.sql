-- **** Semaphore ****

CREATE SEQUENCE seq_OP2_SEMAPHORE start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE OP2_SEMAPHORE
(
	node_id VARCHAR(255),
	creation_time TIMESTAMP,
	update_time TIMESTAMP,
	details TEXT,
	applicative_id VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_OP2_SEMAPHORE') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_semaphore_1 UNIQUE (applicative_id),
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
);



