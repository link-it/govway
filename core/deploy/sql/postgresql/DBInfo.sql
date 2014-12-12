CREATE SEQUENCE seq_db_info start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE db_info
(
	major_version INT NOT NULL,
	minor_version INT NOT NULL,
	notes VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_db_info') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_db_info PRIMARY KEY (id)
);



