-- **** Mapping con Porte ****

CREATE SEQUENCE seq_mapping_fruizione_pd MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE mapping_fruizione_pd
(
	id_fruizione NUMBER NOT NULL,
	id_porta NUMBER NOT NULL,
	is_default NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_fruizione_pd_1 FOREIGN KEY (id_fruizione) REFERENCES servizi_fruitori(id),
	CONSTRAINT fk_mapping_fruizione_pd_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_mapping_fruizione_pd PRIMARY KEY (id)
);

-- index
CREATE INDEX index_mapping_fruizione_pd_1 ON mapping_fruizione_pd (id_fruizione);
CREATE TRIGGER trg_mapping_fruizione_pd
BEFORE
insert on mapping_fruizione_pd
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_mapping_fruizione_pd.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_mapping_erogazione_pa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE mapping_erogazione_pa
(
	id_erogazione NUMBER NOT NULL,
	id_porta NUMBER NOT NULL,
	is_default NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_erogazione_pa_1 FOREIGN KEY (id_erogazione) REFERENCES servizi(id),
	CONSTRAINT fk_mapping_erogazione_pa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_mapping_erogazione_pa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_mapping_erogazione_pa_1 ON mapping_erogazione_pa (id_erogazione);
CREATE TRIGGER trg_mapping_erogazione_pa
BEFORE
insert on mapping_erogazione_pa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_mapping_erogazione_pa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


