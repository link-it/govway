-- **** Connettori ****

CREATE SEQUENCE seq_connettori MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE connettori
(
	-- (disabilitato,http,jms)
	endpointtype VARCHAR2(255) NOT NULL,
	nome_connettore VARCHAR2(2000) NOT NULL,
	-- url nel caso http
	url VARCHAR2(4000),
	-- nel caso di http indicazione se usare chunking
	transfer_mode VARCHAR2(255),
	transfer_mode_chunk_size NUMBER,
	-- nel caso di http indicazione se seguire il redirect o meno
	redirect_mode VARCHAR2(255),
	redirect_max_hop NUMBER,
	-- nome coda jms
	nome VARCHAR2(255),
	-- tipo coda jms (queue,topic)
	tipo VARCHAR2(255),
	-- utente di una connessione jms
	utente VARCHAR2(255),
	-- password per una connessione jms
	password VARCHAR2(255),
	enc_password CLOB,
	-- context property: initial_content
	initcont VARCHAR2(255),
	-- context property: url_pkg
	urlpkg VARCHAR2(255),
	-- context property: provider_url
	provurl VARCHAR2(255),
	-- ConnectionFactory JMS
	connection_factory VARCHAR2(255),
	-- Messaggio JMS inviato come text/byte message
	send_as VARCHAR2(255),
	-- 1/0 (true/false) abilita il debug tramite il connettore
	debug NUMBER,
	-- 1/0 (true/false) abilita l'utilizzo di un proxy tramite il connettore
	proxy NUMBER,
	proxy_type VARCHAR2(255),
	proxy_hostname VARCHAR2(255),
	proxy_port VARCHAR2(255),
	proxy_username VARCHAR2(255),
	proxy_password VARCHAR2(255),
	enc_proxy_password CLOB,
	-- Indicazione sull'intervallo massimo di tempo necessario per instaurare una connessione (intervallo in ms)
	connection_timeout NUMBER,
	-- Indicazione sull'intervallo massimo di tempo che può occorrere prima di ricevere una risposta (intervallo in ms)
	read_timeout NUMBER,
	-- Indicazione sull'intervallo massimo di tempo medio atteso prima di ricevere una risposta (intervallo in ms)
	avg_response_time NUMBER,
	-- 1/0 (true/false) indica se il connettore e' gestito tramite le proprieta' custom
	custom NUMBER,
	-- Gestione Token
	token_policy VARCHAR2(255),
	api_key CLOB,
	api_key_header VARCHAR2(255),
	app_id CLOB,
	app_id_header VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori PRIMARY KEY (id)
);


ALTER TABLE connettori MODIFY debug DEFAULT 0;
ALTER TABLE connettori MODIFY proxy DEFAULT 0;
ALTER TABLE connettori MODIFY custom DEFAULT 0;

CREATE TRIGGER trg_connettori
BEFORE
insert on connettori
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_connettori.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_connettori_custom MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE connettori_custom
(
	name VARCHAR2(255) NOT NULL,
	value VARCHAR2(4000) NOT NULL,
	enc_value CLOB,
	id_connettore NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_custom_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_custom PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conn_custom_1 ON connettori_custom (id_connettore);
CREATE TRIGGER trg_connettori_custom
BEFORE
insert on connettori_custom
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_connettori_custom.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_connettori_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE connettori_properties
(
	-- nome connettore personalizzato attraverso file properties
	nome_connettore VARCHAR2(255) NOT NULL,
	-- location del file properties
	path VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_connettori_properties_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori_properties PRIMARY KEY (id)
);

CREATE TRIGGER trg_connettori_properties
BEFORE
insert on connettori_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_connettori_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


