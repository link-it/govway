-- **** Connettori Gestione Errore ****

CREATE SEQUENCE seq_gestione_errore MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE gestione_errore
(
	-- accetta/rispedisci
	comportamento_default VARCHAR2(255),
	cadenza_rispedizione VARCHAR2(255),
	nome VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_gestione_errore_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gestione_errore PRIMARY KEY (id)
);

CREATE TRIGGER trg_gestione_errore
BEFORE
insert on gestione_errore
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_gestione_errore.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_gestione_errore_trasporto MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE gestione_errore_trasporto
(
	id_gestione_errore NUMBER NOT NULL,
	valore_massimo NUMBER,
	valore_minimo NUMBER,
	-- accetta/rispedisci
	comportamento VARCHAR2(255),
	cadenza_rispedizione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_gestione_errore_trasporto_1 FOREIGN KEY (id_gestione_errore) REFERENCES gestione_errore(id),
	CONSTRAINT pk_gestione_errore_trasporto PRIMARY KEY (id)
);

CREATE TRIGGER trg_gestione_errore_trasporto
BEFORE
insert on gestione_errore_trasporto
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_gestione_errore_trasporto.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_gestione_errore_soap MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE gestione_errore_soap
(
	id_gestione_errore NUMBER NOT NULL,
	fault_actor VARCHAR2(255),
	fault_code VARCHAR2(255),
	fault_string VARCHAR2(255),
	-- accetta/rispedisci
	comportamento VARCHAR2(255),
	cadenza_rispedizione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_gestione_errore_soap_1 FOREIGN KEY (id_gestione_errore) REFERENCES gestione_errore(id),
	CONSTRAINT pk_gestione_errore_soap PRIMARY KEY (id)
);

CREATE TRIGGER trg_gestione_errore_soap
BEFORE
insert on gestione_errore_soap
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_gestione_errore_soap.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


