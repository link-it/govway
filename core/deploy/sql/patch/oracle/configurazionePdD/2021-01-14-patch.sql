ALTER TABLE porte_delegate MODIFY integrazione VARCHAR2(4000);
ALTER TABLE porte_applicative MODIFY integrazione VARCHAR2(4000);


CREATE SEQUENCE seq_config_handlers MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_handlers
(
	tipologia VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_config_handlers_1 UNIQUE (tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_handlers PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_handlers
BEFORE
insert on config_handlers
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_handlers.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pd_handlers MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_handlers
(
	id_porta NUMBER NOT NULL,
	tipologia VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_handlers PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_handlers
BEFORE
insert on pd_handlers
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_handlers.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pa_handlers MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_handlers
(
	id_porta NUMBER NOT NULL,
	tipologia VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_handlers PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_handlers
BEFORE
insert on pa_handlers
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_handlers.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


