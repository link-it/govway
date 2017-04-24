-- **** Ruoli ****

CREATE SEQUENCE seq_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ruoli
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	tipologia VARCHAR2(255) NOT NULL,
	contesto_utilizzo VARCHAR2(255) NOT NULL,
	superuser VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_ruoli_1 CHECK (tipologia IN ('interno','esterno','qualsiasi')),
	CONSTRAINT chk_ruoli_2 CHECK (contesto_utilizzo IN ('portaDelegata','portaApplicativa','qualsiasi')),
	-- unique constraints
	CONSTRAINT unique_ruoli_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_ruoli PRIMARY KEY (id)
);


ALTER TABLE ruoli MODIFY tipologia DEFAULT 'qualsiasi';
ALTER TABLE ruoli MODIFY contesto_utilizzo DEFAULT 'qualsiasi';
ALTER TABLE ruoli MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_ruoli
BEFORE
insert on ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




ALTER TABLE soggetti ADD tipoauth VARCHAR(255);
ALTER TABLE soggetti ADD utente VARCHAR(255);
ALTER TABLE soggetti ADD password VARCHAR(255);
ALTER TABLE soggetti ADD subject VARCHAR(255);


CREATE SEQUENCE seq_soggetti_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_ruoli
(
	id_soggetto NUMBER NOT NULL,
	id_ruolo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_soggetti_ruoli_1 UNIQUE (id_soggetto,id_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_ruoli_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_soggetti_ruoli_2 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT pk_soggetti_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_soggetti_ruoli
BEFORE
insert on soggetti_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



