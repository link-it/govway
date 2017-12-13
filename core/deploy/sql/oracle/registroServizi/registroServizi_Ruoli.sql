-- **** Ruoli ****

CREATE SEQUENCE seq_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ruoli
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	tipologia VARCHAR2(255) NOT NULL,
	nome_esterno VARCHAR2(255),
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


