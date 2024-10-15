CREATE TABLE users_ricerche
(
	id_utente BIGINT NOT NULL,
	label VARCHAR(255) NOT NULL,
	descrizione VARCHAR(4000) NOT NULL,
	data_creazione DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modulo VARCHAR(255) NOT NULL,
	modalita_ricerca VARCHAR(255) NOT NULL,
	visibilita VARCHAR(255) NOT NULL,
	ricerca VARCHAR(max) NOT NULL,
	protocollo VARCHAR(255),
	soggetto VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_users_ricerche_1 UNIQUE (id_utente,label,modulo,modalita_ricerca),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_ricerche_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_ricerche PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_users_ricerche_1 ON users_ricerche (id_utente,label);
