CREATE SEQUENCE seq_users_ricerche MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE users_ricerche
(
	id_utente NUMBER NOT NULL,
	label VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(4000) NOT NULL,
	data_creazione TIMESTAMP NOT NULL,
	modulo VARCHAR2(255) NOT NULL,
	modalita_ricerca VARCHAR2(255) NOT NULL,
	visibilita VARCHAR2(255) NOT NULL,
	ricerca CLOB NOT NULL,
	protocollo VARCHAR2(255),
	soggetto VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_users_ricerche_1 UNIQUE (id_utente,label),
	-- fk/pk keys constraints
	CONSTRAINT fk_users_ricerche_1 FOREIGN KEY (id_utente) REFERENCES users(id),
	CONSTRAINT pk_users_ricerche PRIMARY KEY (id)
);


ALTER TABLE users_ricerche MODIFY data_creazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_users_ricerche
BEFORE
insert on users_ricerche
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_users_ricerche.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


