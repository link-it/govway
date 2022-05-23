
-- **** Controllo Traffico ****

CREATE SEQUENCE seq_ct_map MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE ct_map
(
	map_key VARCHAR2(300) NOT NULL,
	map_update_time TIMESTAMP NOT NULL,
	map_value BLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_ct_map_1 UNIQUE (map_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_map PRIMARY KEY (id)
);

CREATE TRIGGER trg_ct_map
BEFORE
insert on ct_map
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ct_map.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/

