CREATE SEQUENCE seq_pa_authz_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_authz_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_authz_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_authz_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_authz_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_AUTHZ_PROP ON pa_authz_properties (id_porta);
CREATE TRIGGER trg_pa_authz_properties
BEFORE
insert on pa_authz_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_authz_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_authzc_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_authzc_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_authzc_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_authzc_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_authzc_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_AUTHZC_PROP ON pa_authzc_properties (id_porta);
CREATE TRIGGER trg_pa_authzc_properties
BEFORE
insert on pa_authzc_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_authzc_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


CREATE SEQUENCE seq_pd_authz_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_authz_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_authz_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authz_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authz_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTHZ_PROP ON pd_authz_properties (id_porta);
CREATE TRIGGER trg_pd_authz_properties
BEFORE
insert on pd_authz_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_authz_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_authzc_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_authzc_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_authzc_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authzc_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authzc_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTHZC_PROP ON pd_authzc_properties (id_porta);
CREATE TRIGGER trg_pd_authzc_properties
BEFORE
insert on pd_authzc_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_authzc_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


