
CREATE SEQUENCE seq_pa_aa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_aa
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	attributi CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_aa_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_aa_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_aa PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_aa
BEFORE
insert on pa_aa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_aa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_aa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_aa
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	attributi CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_aa_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_aa_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_aa PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_aa
BEFORE
insert on pd_aa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_aa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


