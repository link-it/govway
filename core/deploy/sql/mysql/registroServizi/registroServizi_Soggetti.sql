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



-- **** Soggetti ****

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR(255) NOT NULL,
	tipo_soggetto VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	identificativo_porta VARCHAR(255),
	-- 1/0 (true/false) svolge attivita di router
	is_router INT DEFAULT 0,
	id_connettore BIGINT NOT NULL,
	superuser VARCHAR(255),
	server VARCHAR(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	profilo VARCHAR(255),
	codice_ipa VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	CONSTRAINT unique_soggetti_2 UNIQUE (codice_ipa),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);
CREATE UNIQUE INDEX index_soggetti_2 ON soggetti (codice_ipa);


