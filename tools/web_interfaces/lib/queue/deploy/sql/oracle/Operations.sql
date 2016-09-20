-- **** Operations ****

CREATE SEQUENCE seq_operations MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE operations
(
	operation VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	superuser VARCHAR2(255) NOT NULL,
	hostname VARCHAR2(255) NOT NULL,
	status VARCHAR2(255) NOT NULL,
	details CLOB,
	timereq TIMESTAMP NOT NULL,
	timexecute TIMESTAMP NOT NULL,
	deleted NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX operations_superuser ON operations (superuser);
CREATE INDEX operations_precedenti ON operations (id,deleted,hostname,timereq);

ALTER TABLE operations MODIFY deleted DEFAULT 0;

CREATE TRIGGER trg_operations
BEFORE
insert on operations
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_operations.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_parameters MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE parameters
(
	id_operations NUMBER NOT NULL,
	name VARCHAR2(255) NOT NULL,
	value VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_parameters_1 FOREIGN KEY (id_operations) REFERENCES operations(id),
	CONSTRAINT pk_parameters PRIMARY KEY (id)
);

-- index
CREATE INDEX parameters_index ON parameters (name,value);
CREATE TRIGGER trg_parameters
BEFORE
insert on parameters
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_parameters.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


