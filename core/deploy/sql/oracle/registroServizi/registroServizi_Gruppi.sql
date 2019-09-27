-- **** Gruppi ****

CREATE SEQUENCE seq_gruppi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE gruppi
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	service_binding VARCHAR2(255),
	superuser VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_gruppi_1 CHECK (service_binding IN ('soap','rest')),
	-- unique constraints
	CONSTRAINT unique_gruppi_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gruppi PRIMARY KEY (id)
);


ALTER TABLE gruppi MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_gruppi
BEFORE
insert on gruppi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_gruppi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


