-- **** Protocol Properties ****

CREATE SEQUENCE seq_protocol_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE protocol_properties
(
	-- tipoProprietario
	tipo_proprietario VARCHAR2(255) NOT NULL,
	-- idOggettoProprietarioDocumento
	id_proprietario NUMBER NOT NULL,
	-- nome property
	name VARCHAR2(255) NOT NULL,
	-- valore come stringa
	value_string VARCHAR2(4000),
	-- valore come numero
	value_number NUMBER,
	-- valore true o false
	value_boolean NUMBER,
	-- valore binario
	value_binary BLOB,
	file_name VARCHAR2(4000),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_protocol_properties_1 UNIQUE (tipo_proprietario,id_proprietario,name),
	-- fk/pk keys constraints
	CONSTRAINT pk_protocol_properties PRIMARY KEY (id)
);

CREATE TRIGGER trg_protocol_properties
BEFORE
insert on protocol_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_protocol_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


