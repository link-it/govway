ALTER TABLE porte_applicative ADD validazione_contenuti_soapa VARCHAR2(255);
ALTER TABLE porte_applicative ADD validazione_contenuti_json VARCHAR2(255);
ALTER TABLE porte_applicative ADD validazione_contenuti_pat_and VARCHAR2(255);
ALTER TABLE porte_applicative ADD validazione_contenuti_pat_not VARCHAR2(255);

ALTER TABLE porte_delegate ADD validazione_contenuti_soapa VARCHAR2(255);
ALTER TABLE porte_delegate ADD validazione_contenuti_json VARCHAR2(255);
ALTER TABLE porte_delegate ADD validazione_contenuti_pat_and VARCHAR2(255);
ALTER TABLE porte_delegate ADD validazione_contenuti_pat_not VARCHAR2(255);





CREATE SEQUENCE seq_pa_val_pattern MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_val_pattern
(
	id_configurazione NUMBER NOT NULL,
	tipo_validazione VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	regola CLOB NOT NULL,
	-- abilitato/disabilitato
	pattern_and VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_val_pattern_1 UNIQUE (tipo_validazione,id_configurazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pa_val_pattern PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_val_pattern
BEFORE
insert on pa_val_pattern
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_val_pattern.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_val_richiesta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_val_richiesta
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR2(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR2(255),
	-- abilitato/disabilitato
	mtom VARCHAR2(255),
	-- abilitato/disabilitato
	soapaction VARCHAR2(255),
	-- Nome interfaccia json
	json VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR2(255),
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_val_richiesta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pa_val_richiesta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_val_richiesta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_val_richiesta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_val_richiesta
BEFORE
insert on pa_val_richiesta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_val_richiesta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_val_risposta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_val_risposta
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR2(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR2(255),
	-- abilitato/disabilitato
	mtom VARCHAR2(255),
	-- Nome interfaccia json
	json VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR2(255),
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	applicabilita_status_min NUMBER,
	applicabilita_status_max NUMBER,
	applicabilita_problem_detail VARCHAR2(255),
	applicabilita_empty_response VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_val_risposta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pa_val_risposta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_val_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_val_risposta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_val_risposta
BEFORE
insert on pa_val_risposta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_val_risposta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pd_val_pattern MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_val_pattern
(
	id_configurazione NUMBER NOT NULL,
	tipo_validazione VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	regola CLOB NOT NULL,
	-- abilitato/disabilitato
	pattern_and VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_val_pattern_1 UNIQUE (tipo_validazione,id_configurazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pd_val_pattern PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_val_pattern
BEFORE
insert on pd_val_pattern
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_val_pattern.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_val_richiesta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_val_richiesta
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR2(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR2(255),
	-- abilitato/disabilitato
	mtom VARCHAR2(255),
	-- abilitato/disabilitato
	soapaction VARCHAR2(255),
	-- Nome interfaccia json
	json VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR2(255),
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_val_richiesta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pd_val_richiesta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_val_richiesta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_val_richiesta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_val_richiesta
BEFORE
insert on pd_val_richiesta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_val_richiesta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_val_risposta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_val_risposta
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR2(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR2(255),
	-- abilitato/disabilitato
	mtom VARCHAR2(255),
	-- Nome interfaccia json
	json VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR2(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR2(255),
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	applicabilita_status_min NUMBER,
	applicabilita_status_max NUMBER,
	applicabilita_problem_detail VARCHAR2(255),
	applicabilita_empty_response VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_val_risposta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pd_val_risposta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_val_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_val_risposta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_val_risposta
BEFORE
insert on pd_val_risposta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_val_risposta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



