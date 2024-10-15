-- **** Utenti ****

CREATE TABLE users
(
	login VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_password TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	check_data_password INT NOT NULL DEFAULT 1,
	tipo_interfaccia VARCHAR(255) NOT NULL,
	interfaccia_completa INT,
	permessi VARCHAR(255) NOT NULL,
	protocolli TEXT,
	protocollo_pddconsole VARCHAR(255),
	protocollo_pddmonitor VARCHAR(255),
	soggetto_pddconsole VARCHAR(255),
	soggetto_pddmonitor VARCHAR(255),
	soggetti_all INT,
	servizi_all INT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_users_1 UNIQUE (login),
	-- fk/pk keys constraints
	CONSTRAINT pk_users PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE users_stati
(
	oggetto VARCHAR(255) NOT NULL,
	stato MEDIUMTEXT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_stati_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_stati PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE users_password
(
	password VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_password TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_password_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_password PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




CREATE TABLE users_soggetti
(
	id_utente BIGINT NOT NULL,
	id_soggetto BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_users_soggetti_1 UNIQUE (id_utente,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_soggetti_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_users_soggetti_2 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_soggetti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_users_soggetti_1 ON users_soggetti (id_utente,id_soggetto);



CREATE TABLE users_servizi
(
	id_utente BIGINT NOT NULL,
	id_servizio BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_users_servizi_1 UNIQUE (id_utente,id_servizio),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_servizi_1 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT fk_users_servizi_2 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_servizi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_users_servizi_1 ON users_servizi (id_utente,id_servizio);



CREATE TABLE users_ricerche
(
	id_utente BIGINT NOT NULL,
	label VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_creazione TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	modulo VARCHAR(255) NOT NULL,
	modalita_ricerca VARCHAR(255) NOT NULL,
	visibilita VARCHAR(255) NOT NULL,
	ricerca MEDIUMTEXT NOT NULL,
	protocollo VARCHAR(255),
	soggetto VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_users_ricerche_1 UNIQUE (id_utente,label,modulo,modalita_ricerca),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_ricerche_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_ricerche PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_users_ricerche_1 ON users_ricerche (id_utente,label,modulo,modalita_ricerca);


