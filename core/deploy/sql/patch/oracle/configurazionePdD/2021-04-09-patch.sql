-- **** Nodi Runtime ****

CREATE SEQUENCE seq_nodi_runtime MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE nodi_runtime
(
	hostname VARCHAR2(4000) NOT NULL,
	gruppo VARCHAR2(4000) NOT NULL,
	data_registrazione TIMESTAMP NOT NULL,
	data_refresh TIMESTAMP NOT NULL,
	id_numerico NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname),
	CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico),
	-- fk/pk keys constraints
	CONSTRAINT pk_nodi_runtime PRIMARY KEY (id)
);

CREATE TRIGGER trg_nodi_runtime
BEFORE
insert on nodi_runtime
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_nodi_runtime.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


