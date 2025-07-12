CREATE SEQUENCE seq_transazioni_info MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

ALTER TABLE transazioni_info ADD id NUMBER;
UPDATE transazioni_info SET id = seq_transazioni_info.NEXTVAL;
ALTER TABLE transazioni_info MODIFY id NUMBER NOT NULL;
ALTER TABLE transazioni_info ADD CONSTRAINT pk_transazioni_info PRIMARY KEY (id);

CREATE TRIGGER trg_transazioni_info
BEFORE
insert on transazioni_info
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_transazioni_info.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


