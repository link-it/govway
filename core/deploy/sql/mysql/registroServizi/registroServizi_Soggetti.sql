-- **** Soggetti ****

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR(255) NOT NULL,
	tipo_soggetto VARCHAR(255) NOT NULL,
	descrizione MEDIUMTEXT,
	identificativo_porta VARCHAR(255),
	-- 1/0 (true/false) Indicazione se il soggetto svolge è quello di default per il protocollo
	is_default INT DEFAULT 0,
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
	tipoauth VARCHAR(255),
	utente VARCHAR(2800),
	password VARCHAR(255),
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate MEDIUMBLOB,
	cert_strict_verification INT,
	utente_richiedente VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_creazione TIMESTAMP(3) DEFAULT 0,
	utente_ultima_modifica VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultima_modifica TIMESTAMP(3) DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	CONSTRAINT unique_soggetti_2 UNIQUE (codice_ipa),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);
CREATE UNIQUE INDEX index_soggetti_2 ON soggetti (codice_ipa);



CREATE TABLE soggetti_ruoli
(
	id_soggetto BIGINT NOT NULL,
	id_ruolo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_soggetti_ruoli_1 UNIQUE (id_soggetto,id_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_ruoli_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_soggetti_ruoli_2 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT pk_soggetti_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_soggetti_ruoli_1 ON soggetti_ruoli (id_soggetto,id_ruolo);
CREATE INDEX INDEX_SOGGETTI_RUOLI ON soggetti_ruoli (id_soggetto);



CREATE TABLE soggetti_credenziali
(
	id_soggetto BIGINT NOT NULL,
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate MEDIUMBLOB,
	cert_strict_verification INT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_credenziali_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_credenziali PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_SOGGETTI_CRED ON soggetti_credenziali (id_soggetto);



CREATE TABLE soggetti_properties
(
	id_soggetto BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_soggetti_props_1 UNIQUE (id_soggetto,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_properties_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_SOGGETTI_PROP ON soggetti_properties (id_soggetto);


