
CREATE SEQUENCE seq_soggetti_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_properties
(
	id_soggetto NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(4000) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_soggetti_props_1 UNIQUE (id_soggetto,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_properties_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_PROP ON soggetti_properties (id_soggetto);
CREATE TRIGGER trg_soggetti_properties
BEFORE
insert on soggetti_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


