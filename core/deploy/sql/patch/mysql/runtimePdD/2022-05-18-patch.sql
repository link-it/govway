
-- **** Controllo Traffico ****

CREATE TABLE ct_map
(
	map_key VARCHAR(300) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	map_update_time TIMESTAMP(3) NOT NULL DEFAULT 0,
	map_value MEDIUMBLOB NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_ct_map_1 UNIQUE (map_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_map PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_ct_map_1 ON ct_map (map_key);

