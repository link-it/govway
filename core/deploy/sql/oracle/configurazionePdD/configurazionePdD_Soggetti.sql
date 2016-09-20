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
	superuser VARCHAR2(255),
	pd_url_prefix_rewriter VARCHAR2(255),
	pa_url_prefix_rewriter VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
);


ALTER TABLE soggetti MODIFY is_router DEFAULT 0;
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


