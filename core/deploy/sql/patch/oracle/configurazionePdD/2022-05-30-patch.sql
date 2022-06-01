
CREATE SEQUENCE seq_ct_rt_props MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ct_rt_props
(
	rt_prop_name VARCHAR2(255) NOT NULL,
	rt_prop_value VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_rt_prop_policy_1 UNIQUE (rt_prop_name),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_rt_props PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_rt_prop_policy_1 ON ct_rt_props (rt_prop_value);
CREATE TRIGGER trg_ct_rt_props
BEFORE
insert on ct_rt_props
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ct_rt_props.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pa_ct_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_ct_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_ct_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ct_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ct_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pa_ct_props_1 ON pa_ct_properties (valore);
CREATE INDEX INDEX_PA_CT_PROP ON pa_ct_properties (id_porta);
CREATE TRIGGER trg_pa_ct_properties
BEFORE
insert on pa_ct_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_ct_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_ct_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_ct_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_ct_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ct_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ct_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pd_ct_props_1 ON pd_ct_properties (valore);
CREATE INDEX INDEX_PD_CT_PROP ON pd_ct_properties (id_porta);
CREATE TRIGGER trg_pd_ct_properties
BEFORE
insert on pd_ct_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_ct_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


