ALTER TABLE porte_applicative ADD id_sa_default NUMBER;

ALTER TABLE porte_applicative_sa ADD connettore_nome VARCHAR2(255);
ALTER TABLE porte_applicative_sa ADD connettore_descrizione VARCHAR2(4000);
ALTER TABLE porte_applicative_sa ADD connettore_stato VARCHAR2(255);
ALTER TABLE porte_applicative_sa ADD connettore_filtri CLOB;

ALTER TABLE servizi_applicativi ADD tipo VARCHAR2(255);
update servizi_applicativi set tipo='client' where tipologia_fruizione<>'disabilitato';

ALTER TABLE servizi_applicativi ADD as_client NUMBER;

CREATE SEQUENCE seq_pa_sa_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_sa_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_sa_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_sa_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative_sa(id),
	CONSTRAINT pk_pa_sa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_PROP ON pa_sa_properties (id_porta);
CREATE TRIGGER trg_pa_sa_properties
BEFORE
insert on pa_sa_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_sa_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_behaviour_props MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_behaviour_props
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_behaviour_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_behaviour_props_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_behaviour_props PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_BEHAVIOUR_PROP ON pa_behaviour_props (id_porta);
CREATE TRIGGER trg_pa_behaviour_props
BEFORE
insert on pa_behaviour_props
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_behaviour_props.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




ALTER TABLE configurazione ADD consegna_statocache VARCHAR2(255);
ALTER TABLE configurazione ADD consegna_dimensionecache VARCHAR2(255);
ALTER TABLE configurazione ADD consegna_algoritmocache VARCHAR2(255);
ALTER TABLE configurazione ADD consegna_idlecache VARCHAR2(255);
ALTER TABLE configurazione ADD consegna_lifecache VARCHAR2(255);

UPDATE configurazione set consegna_statocache='abilitato';
UPDATE configurazione set consegna_dimensionecache='10000';
UPDATE configurazione set consegna_algoritmocache='lru';
UPDATE configurazione set consegna_lifecache='-1';
