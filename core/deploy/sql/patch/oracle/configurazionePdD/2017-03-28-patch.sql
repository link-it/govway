-- Ruoli

ALTER TABLE porte_applicative ADD autenticazione VARCHAR(255);
ALTER TABLE porte_applicative ADD autenticazione_opzionale VARCHAR(255);
ALTER TABLE porte_applicative ADD autorizzazione VARCHAR(255);
UPDATE porte_applicative set autorizzazione='authenticated';

ALTER TABLE porte_applicative ADD ruoli_match VARCHAR(255); 

CREATE SEQUENCE seq_pa_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_ruoli
(
	id_porta NUMBER NOT NULL,
	ruolo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_ruoli
BEFORE
insert on pa_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


ALTER TABLE porte_delegate ADD autenticazione_opzionale VARCHAR(255);

UPDATE porte_delegate set autorizzazione='authenticated' WHERE autorizzazione='openspcoop';

ALTER TABLE porte_delegate ADD ruoli_match VARCHAR(255);

CREATE SEQUENCE seq_pd_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_ruoli
(
	id_porta NUMBER NOT NULL,
	ruolo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_ruoli
BEFORE
insert on pd_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


ALTER TABLE servizi_applicativi DROP COLUMN subjectrisp;
ALTER TABLE servizi_applicativi DROP COLUMN subjectinv;

CREATE SEQUENCE seq_sa_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE sa_ruoli
(
	id_servizio_applicativo NUMBER NOT NULL,
	ruolo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_sa_ruoli_1 UNIQUE (id_servizio_applicativo,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_ruoli_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_sa_ruoli
BEFORE
insert on sa_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_sa_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- config

ALTER TABLE configurazione ADD authn_statocache VARCHAR(255);
ALTER TABLE configurazione ADD authn_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD authn_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD authn_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD authn_lifecache VARCHAR(255);

UPDATE configurazione set authn_statocache='abilitato';
UPDATE configurazione set authn_dimensionecache=5000;
UPDATE configurazione set authn_algoritmocache='lru';
UPDATE configurazione set authn_lifecache='7200';

