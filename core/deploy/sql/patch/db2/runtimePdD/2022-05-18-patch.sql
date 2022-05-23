-- **** Controllo Traffico ****

CREATE TABLE ct_map
(
	map_key VARCHAR(300) NOT NULL,
	map_update_time TIMESTAMP NOT NULL,
	map_value BLOB NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_ct_map_1 UNIQUE (map_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_map PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_ct_map_1 ON ct_map (map_key);

