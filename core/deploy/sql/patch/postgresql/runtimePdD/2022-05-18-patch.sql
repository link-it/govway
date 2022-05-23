
-- **** Controllo Traffico ****

CREATE SEQUENCE seq_ct_map start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE ct_map
(
	map_key VARCHAR(300) NOT NULL,
	map_update_time TIMESTAMP NOT NULL,
	map_value BYTEA NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_ct_map') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_ct_map_1 UNIQUE (map_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_map PRIMARY KEY (id)
);


