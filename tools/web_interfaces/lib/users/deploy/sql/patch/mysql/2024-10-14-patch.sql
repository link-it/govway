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
CREATE UNIQUE INDEX index_users_ricerche_1 ON users_ricerche (id_utente,label);


