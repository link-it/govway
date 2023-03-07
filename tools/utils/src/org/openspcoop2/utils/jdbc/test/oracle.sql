CREATE TABLE prova_bytes (
     descrizione VARCHAR(255) NOT NULL,
	 contenuto BLOB NOT NULL,
	 contenuto_vuoto BLOB
);
		 
CREATE SEQUENCE seq_prova MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;
CREATE TABLE prova (
     descrizione VARCHAR(255) NOT NULL,
	 id NUMBER,
     CONSTRAINT pk_prova PRIMARY KEY (id)
);
CREATE TRIGGER trg_prova
BEFORE
insert on prova
for each row
begin
	IF (:new.id IS NULL) THEN
		SELECT seq_prova.nextval INTO :new.id
			FROM DUAL;
	END IF;
end;
/

