-- **** Semaphore ****

CREATE SEQUENCE seq_OP2_SEMAPHORE MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE OP2_SEMAPHORE
(
	node_id VARCHAR2(255),
	creation_time TIMESTAMP,
	update_time TIMESTAMP,
	details CLOB,
	applicative_id VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_OP2_SEMAPHORE PRIMARY KEY (id)
);

CREATE TRIGGER trg_OP2_SEMAPHORE
BEFORE
insert on OP2_SEMAPHORE
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_OP2_SEMAPHORE.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


