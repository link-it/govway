CREATE SEQUENCE seq_tracce MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;
CREATE TABLE tracce (
     descrizione VARCHAR(255) NOT NULL,
     gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update NUMBER,
		id NUMBER
);
CREATE TRIGGER trg_tracce
BEFORE
insert on tracce
for each row
begin
	IF (:new.id IS NULL) THEN
		SELECT seq_tracce.nextval INTO :new.id
			FROM DUAL;
	END IF;
end;
/

CREATE SEQUENCE seq_msgdiagnostici MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;
CREATE TABLE msgdiagnostici (
     descrizione VARCHAR(255) NOT NULL,
     gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     tipo_mittente VARCHAR(255) NOT NULL,
     mittente VARCHAR(255) NOT NULL,
     tipo_destinatario VARCHAR(255) NOT NULL,
     destinatario VARCHAR(255) NOT NULL,
     campo_vuoto VARCHAR(255),
     campo_update VARCHAR(255),
     campo_int_update NUMBER,
		id NUMBER
);
CREATE TRIGGER trg_msgdiagnostici
BEFORE
insert on msgdiagnostici
for each row
begin
	IF (:new.id IS NULL) THEN
		SELECT seq_msgdiagnostici.nextval INTO :new.id
			FROM DUAL;
	END IF;
end;
/
