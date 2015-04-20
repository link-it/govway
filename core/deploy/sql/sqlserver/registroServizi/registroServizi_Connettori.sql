-- **** Connettori ****

CREATE TABLE connettori
(
	-- (disabilitato,http,jms)
	endpointtype VARCHAR(255) NOT NULL,
	nome_connettore VARCHAR(255) NOT NULL,
	-- url nel caso http
	url VARCHAR(255),
	-- nome coda jms
	nome VARCHAR(255),
	-- tipo coda jms (queue,topic)
	tipo VARCHAR(255),
	-- utente di una connessione jms
	utente VARCHAR(255),
	-- password per una connessione jms
	password VARCHAR(255),
	-- context property: initial_content
	initcont VARCHAR(255),
	-- context property: url_pkg
	urlpkg VARCHAR(255),
	-- context property: provider_url
	provurl VARCHAR(255),
	-- ConnectionFactory JMS
	connection_factory VARCHAR(255),
	-- Messaggio JMS inviato come text/byte message
	send_as VARCHAR(255),
	-- 1/0 (true/false) abilita il debug tramite il connettore
	debug INT DEFAULT 0,
	-- 1/0 (true/false) indica se il connettore e' gestito tramite le proprieta' custom
	custom INT DEFAULT 0,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori PRIMARY KEY (id)
);

-- index
CREATE INDEX index_connettori_1 ON connettori (nome_connettore);



CREATE TABLE connettori_custom
(
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name,value),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_custom_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_custom PRIMARY KEY (id)
);




CREATE TABLE connettori_properties
(
	-- nome connettore personalizzato attraverso file properties
	nome_connettore VARCHAR(255) NOT NULL,
	-- location del file properties
	path VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_connettori_properties_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX index_connettori_properties_1 ON connettori_properties (nome_connettore);


