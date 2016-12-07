-- **** Protocol Properties ****

CREATE TABLE protocol_properties
(
	-- tipoProprietario
	tipo_proprietario VARCHAR(255) NOT NULL,
	-- idOggettoProprietarioDocumento
	id_proprietario BIGINT NOT NULL,
	-- nome property
	name VARCHAR(255) NOT NULL,
	-- valore come stringa
	value_string VARCHAR(4000),
	-- valore come numero
	value_number BIGINT,
	-- valore true o false
	value_boolean INT,
	-- valore binario
	value_binary MEDIUMBLOB,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_protocol_properties_1 UNIQUE (tipo_proprietario,id_proprietario,name),
	-- fk/pk keys constraints
	CONSTRAINT pk_protocol_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_protocol_properties_1 ON protocol_properties (tipo_proprietario,id_proprietario,name);


