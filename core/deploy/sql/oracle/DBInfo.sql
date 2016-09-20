CREATE SEQUENCE seq_db_info MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE db_info
(
	major_version NUMBER NOT NULL,
	minor_version NUMBER NOT NULL,
	notes VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_db_info PRIMARY KEY (id)
);

CREATE TRIGGER trg_db_info
BEFORE
insert on db_info
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_db_info.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


