-- **** Connettori ****

CREATE SEQUENCE seq_connettori MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

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
	debug NUMBER,
	-- 1/0 (true/false) indica se il connettore e' gestito tramite le proprieta' custom
	custom NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori PRIMARY KEY (id)
);


ALTER TABLE connettori MODIFY debug DEFAULT 0;
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
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	id_connettore NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name,value),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_custom_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_custom PRIMARY KEY (id)
);

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
	nome_connettore VARCHAR(255) NOT NULL,
	-- location del file properties
	path VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_CONNETTORI_PROP ON connettori_properties (nome_connettore);
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


