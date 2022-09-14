ALTER TABLE servizi_applicativi ADD token_policy VARCHAR2(255);

ALTER TABLE porte_applicative ADD token_sa_stato VARCHAR2(255);
ALTER TABLE porte_applicative ADD token_ruoli_stato VARCHAR2(255);
ALTER TABLE porte_applicative ADD token_ruoli_match VARCHAR2(255);
ALTER TABLE porte_applicative ADD token_ruoli_tipologia VARCHAR2(255);

ALTER TABLE porte_delegate ADD token_sa_stato VARCHAR2(255);
ALTER TABLE porte_delegate ADD token_ruoli_stato VARCHAR2(255);
ALTER TABLE porte_delegate ADD token_ruoli_match VARCHAR2(255);
ALTER TABLE porte_delegate ADD token_ruoli_tipologia VARCHAR2(255);

CREATE SEQUENCE seq_pd_token_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_token_sa
(
	id_porta NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_token_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_token_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pd_token_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_token_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_TOKEN_SA ON pd_token_sa (id_porta);
CREATE TRIGGER trg_pd_token_sa
BEFORE
insert on pd_token_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_token_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_token_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_token_ruoli
(
	id_porta NUMBER NOT NULL,
	ruolo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_token_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_token_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_token_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_token_ruoli
BEFORE
insert on pd_token_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_token_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_token_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_token_sa
(
	id_porta NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_token_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_token_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pa_token_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_token_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_TOKEN_SA ON pa_token_sa (id_porta);
CREATE TRIGGER trg_pa_token_sa
BEFORE
insert on pa_token_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_token_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_token_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_token_ruoli
(
	id_porta NUMBER NOT NULL,
	ruolo VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_token_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_token_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_token_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_token_ruoli
BEFORE
insert on pa_token_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_token_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


