ALTER TABLE users ADD data_password TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
UPDATE users SET data_password = CURRENT_TIMESTAMP;
ALTER TABLE users MODIFY (data_password NOT NULL);

ALTER TABLE users ADD check_data_password NUMBER DEFAULT 1;
UPDATE users SET check_data_password = 1;
ALTER TABLE users MODIFY (check_data_password NOT NULL);

CREATE SEQUENCE seq_users_password MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users_password
(
	password VARCHAR2(255) NOT NULL,
	data_password TIMESTAMP NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_utente NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_password_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_password PRIMARY KEY (id)
);

CREATE TRIGGER trg_users_password
BEFORE
insert on users_password
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_users_password.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


