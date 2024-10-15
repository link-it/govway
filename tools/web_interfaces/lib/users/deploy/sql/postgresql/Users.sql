-- **** Utenti ****

CREATE SEQUENCE seq_users start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users
(
	login VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	data_password TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
	id BIGINT DEFAULT nextval('seq_users') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_1 UNIQUE (login),
	-- fk/pk keys constraints
	CONSTRAINT pk_users PRIMARY KEY (id)
);




CREATE SEQUENCE seq_users_stati start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users_stati
(
	oggetto VARCHAR(255) NOT NULL,
	stato TEXT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_users_stati') NOT NULL,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_stati_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_stati PRIMARY KEY (id)
);




CREATE SEQUENCE seq_users_password start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users_password
(
	password VARCHAR(255) NOT NULL,
	data_password TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_users_password') NOT NULL,
	id_utente BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_password_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_password PRIMARY KEY (id)
);




CREATE SEQUENCE seq_users_soggetti start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users_soggetti
(
	id_utente BIGINT NOT NULL,
	id_soggetto BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_users_soggetti') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_soggetti_1 UNIQUE (id_utente,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_soggetti_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_users_soggetti_2 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_soggetti PRIMARY KEY (id)
);




CREATE SEQUENCE seq_users_servizi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users_servizi
(
	id_utente BIGINT NOT NULL,
	id_servizio BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_users_servizi') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_servizi_1 UNIQUE (id_utente,id_servizio),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_servizi_1 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT fk_users_servizi_2 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_servizi PRIMARY KEY (id)
);




CREATE SEQUENCE seq_users_ricerche start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE users_ricerche
(
	id_utente BIGINT NOT NULL,
	label VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000) NOT NULL,
	data_creazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modulo VARCHAR(255) NOT NULL,
	modalita_ricerca VARCHAR(255) NOT NULL,
	visibilita VARCHAR(255) NOT NULL,
	ricerca TEXT NOT NULL,
	protocollo VARCHAR(255),
	soggetto VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_users_ricerche') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_ricerche_1 UNIQUE (id_utente,label,modulo,modalita_ricerca),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_ricerche_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_ricerche PRIMARY KEY (id)
);



