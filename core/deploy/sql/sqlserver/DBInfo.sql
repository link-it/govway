CREATE TABLE db_info
(
	major_version INT NOT NULL,
	minor_version INT NOT NULL,
	notes VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_db_info PRIMARY KEY (id)
);



