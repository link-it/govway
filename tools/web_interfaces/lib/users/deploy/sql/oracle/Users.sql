-- **** Utenti ****

CREATE SEQUENCE seq_users MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users
(
	login VARCHAR2(255) NOT NULL,
	password VARCHAR2(255) NOT NULL,
	tipo_interfaccia VARCHAR2(255) NOT NULL,
	permessi VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_1 UNIQUE (login),
	-- fk/pk keys constraints
	CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TRIGGER trg_users
BEFORE
insert on users
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_users.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


