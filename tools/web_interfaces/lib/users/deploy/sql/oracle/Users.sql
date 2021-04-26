-- **** Utenti ****

CREATE SEQUENCE seq_users MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users
(
	login VARCHAR2(255) NOT NULL,
	password VARCHAR2(255) NOT NULL,
	data_password TIMESTAMP NOT NULL,
	check_data_password NUMBER NOT NULL,
	tipo_interfaccia VARCHAR2(255) NOT NULL,
	interfaccia_completa NUMBER,
	permessi VARCHAR2(255) NOT NULL,
	protocolli CLOB,
	protocollo_pddconsole VARCHAR2(255),
	protocollo_pddmonitor VARCHAR2(255),
	soggetto_pddconsole VARCHAR2(255),
	soggetto_pddmonitor VARCHAR2(255),
	soggetti_all NUMBER,
	servizi_all NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_1 UNIQUE (login),
	-- fk/pk keys constraints
	CONSTRAINT pk_users PRIMARY KEY (id)
);


ALTER TABLE users MODIFY data_password DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users MODIFY check_data_password DEFAULT 1;

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



CREATE SEQUENCE seq_users_stati MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users_stati
(
	oggetto VARCHAR2(255) NOT NULL,
	stato CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_utente NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_users_stati_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_stati PRIMARY KEY (id)
);

CREATE TRIGGER trg_users_stati
BEFORE
insert on users_stati
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_users_stati.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



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



CREATE SEQUENCE seq_users_soggetti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users_soggetti
(
	id_utente NUMBER NOT NULL,
	id_soggetto NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_soggetti_1 UNIQUE (id_utente,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_soggetti_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_users_soggetti_2 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_soggetti PRIMARY KEY (id)
);

CREATE TRIGGER trg_users_soggetti
BEFORE
insert on users_soggetti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_users_soggetti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_users_servizi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users_servizi
(
	id_utente NUMBER NOT NULL,
	id_servizio NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_servizi_1 UNIQUE (id_utente,id_servizio),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_servizi_1 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT fk_users_servizi_2 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_servizi PRIMARY KEY (id)
);

CREATE TRIGGER trg_users_servizi
BEFORE
insert on users_servizi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_users_servizi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


