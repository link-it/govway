
CREATE SEQUENCE seq_sa_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE sa_properties
(
	id_servizio_applicativo NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(4000) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_sa_properties_1 UNIQUE (id_servizio_applicativo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_properties_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_PROP ON sa_properties (id_servizio_applicativo);
CREATE TRIGGER trg_sa_properties
BEFORE
insert on sa_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_sa_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/

