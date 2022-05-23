-- **** Controllo Traffico ****

CREATE TABLE ct_map
(
	map_key VARCHAR(300) NOT NULL,
	map_update_time DATETIME2 NOT NULL,
	map_value VARBINARY(MAX) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_ct_map_1 UNIQUE (map_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_map PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_ct_map_1 ON ct_map (map_key);




