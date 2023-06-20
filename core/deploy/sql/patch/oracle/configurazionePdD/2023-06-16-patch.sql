
CREATE SEQUENCE seq_nodi_runtime_operations MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE nodi_runtime_operations
(
	descrizione VARCHAR2(4000) NOT NULL,
	operazione VARCHAR2(4000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime_operations PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_NODI_OP ON nodi_runtime_operations (data_registrazione);
CREATE TRIGGER trg_nodi_runtime_operations
BEFORE
insert on nodi_runtime_operations
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_nodi_runtime_operations.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


