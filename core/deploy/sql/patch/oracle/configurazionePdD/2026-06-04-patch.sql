CREATE SEQUENCE seq_connettori_llm_binding MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE connettori_llm_binding
(
	nome_binding VARCHAR2(64) NOT NULL,
	id_connettore NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_conn_llm_bind_1 UNIQUE (id_connettore,nome_binding),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_llm_binding_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_llm_binding PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conn_llm_bind_1 ON connettori_llm_binding (id_connettore);
CREATE TRIGGER trg_connettori_llm_binding
BEFORE
insert on connettori_llm_binding
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_connettori_llm_binding.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_connettori_llm MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE connettori_llm
(
	id_connettore_principale NUMBER NOT NULL,
	id_connettore NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_conn_llm_1 UNIQUE (id_connettore_principale,id_connettore),
	-- fk/pk keys constraints
	CONSTRAINT fk_connettori_llm_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_connettori_llm_2 FOREIGN KEY (id_connettore_principale) REFERENCES connettori(id),
	CONSTRAINT pk_connettori_llm PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conn_llm_1 ON connettori_llm (id_connettore_principale);
CREATE TRIGGER trg_connettori_llm
BEFORE
insert on connettori_llm
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_connettori_llm.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/
