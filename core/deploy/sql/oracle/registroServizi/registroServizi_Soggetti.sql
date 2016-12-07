-- **** Soggetti ****

CREATE SEQUENCE seq_soggetti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR2(255) NOT NULL,
	tipo_soggetto VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	identificativo_porta VARCHAR2(255),
	-- 1/0 (true/false) svolge attivita di router
	is_router NUMBER,
	id_connettore NUMBER NOT NULL,
	superuser VARCHAR2(255),
	server VARCHAR2(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato NUMBER,
	ora_registrazione TIMESTAMP,
	profilo VARCHAR2(255),
	codice_ipa VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	CONSTRAINT unique_soggetti_2 UNIQUE (codice_ipa),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
);


ALTER TABLE soggetti MODIFY is_router DEFAULT 0;
ALTER TABLE soggetti MODIFY privato DEFAULT 0;
ALTER TABLE soggetti MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_soggetti
BEFORE
insert on soggetti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


