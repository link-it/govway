-- **** Connettori ****

CREATE TABLE connettori
(
	-- (disabilitato,http,jms)
	endpointtype VARCHAR(255) NOT NULL,
	nome_connettore VARCHAR(2000) NOT NULL,
	-- url nel caso http
	url VARCHAR(4000),
	-- nel caso di http indicazione se usare chunking
	transfer_mode VARCHAR(255),
	transfer_mode_chunk_size INT,
	-- nel caso di http indicazione se seguire il redirect o meno
	redirect_mode VARCHAR(255),
	redirect_max_hop INT,
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
	-- 1/0 (true/false) abilita l'utilizzo di un proxy tramite il connettore
	proxy INT DEFAULT 0,
	proxy_type VARCHAR(255),
	proxy_hostname VARCHAR(255),
	proxy_port VARCHAR(255),
	proxy_username VARCHAR(255),
	proxy_password VARCHAR(255),
	-- Indicazione sull'intervallo massimo di tempo necessario per instaurare una connessione (intervallo in ms)
	connection_timeout INT,
	-- Indicazione sull'intervallo massimo di tempo che può occorrere prima di ricevere una risposta (intervallo in ms)
	read_timeout INT,
	-- Indicazione sull'intervallo massimo di tempo medio atteso prima di ricevere una risposta (intervallo in ms)
	avg_response_time INT,
	-- 1/0 (true/false) indica se il connettore e' gestito tramite le proprieta' custom
	custom INT DEFAULT 0,
	-- Gestione Token
	token_policy VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_connettori_1 ON connettori (nome_connettore);



CREATE TABLE connettori_custom
(
	name VARCHAR(255) NOT NULL,
	value VARCHAR(4000) NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name,value),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_custom_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_custom PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conn_custom_1 ON connettori_custom (id_connettore);



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
CREATE UNIQUE INDEX index_connettori_properties_1 ON connettori_properties (nome_connettore);


