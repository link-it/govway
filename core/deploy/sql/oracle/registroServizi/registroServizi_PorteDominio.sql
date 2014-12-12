-- **** Porte di Dominio ****

CREATE SEQUENCE seq_pdd MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pdd
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	implementazione VARCHAR(255),
	subject VARCHAR(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR(255),
	ora_registrazione TIMESTAMP,
	superuser VARCHAR(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pdd_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd PRIMARY KEY (id)
);


ALTER TABLE pdd MODIFY implementazione DEFAULT 'standard';
ALTER TABLE pdd MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_pdd
BEFORE
insert on pdd
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pdd.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


