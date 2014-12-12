ALTER TABLE configurazione ADD validazione_contenuti_mtom VARCHAR(255);

ALTER TABLE porte_delegate ADD mtom_request_mode VARCHAR(255);
ALTER TABLE porte_delegate ADD mtom_response_mode VARCHAR(255);
ALTER TABLE porte_delegate ADD ws_security_mtom_req VARCHAR(255);
ALTER TABLE porte_delegate ADD ws_security_mtom_res VARCHAR(255);
ALTER TABLE porte_delegate ADD validazione_contenuti_mtom VARCHAR(255);

CREATE SEQUENCE seq_pd_mtom_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_mtom_request
(
        id_porta NUMBER NOT NULL,
        nome VARCHAR(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR(255),
	required NUMBER NOT NULL,
        -- fk/pk columns
        id NUMBER NOT NULL,
        -- fk/pk keys constraints
        CONSTRAINT fk_pd_mtom_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
        CONSTRAINT pk_pd_mtom_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_MTOMTREQ ON pd_mtom_request (id_porta);
CREATE TRIGGER trg_pd_mtom_request
BEFORE
insert on pd_mtom_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_mtom_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


CREATE SEQUENCE seq_pd_mtom_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_mtom_response
(
        id_porta NUMBER NOT NULL,
        nome VARCHAR(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR(255),
	required NUMBER NOT NULL,
        -- fk/pk columns
        id NUMBER NOT NULL,
        -- fk/pk keys constraints
        CONSTRAINT fk_pd_mtom_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
        CONSTRAINT pk_pd_mtom_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_MTOMTRES ON pd_mtom_response (id_porta);
CREATE TRIGGER trg_pd_mtom_response
BEFORE
insert on pd_mtom_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_mtom_response.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




ALTER TABLE porte_applicative ADD mtom_request_mode VARCHAR(255);
ALTER TABLE porte_applicative ADD mtom_response_mode VARCHAR(255);
ALTER TABLE porte_applicative ADD ws_security_mtom_req VARCHAR(255);
ALTER TABLE porte_applicative ADD ws_security_mtom_res VARCHAR(255);
ALTER TABLE porte_applicative ADD validazione_contenuti_mtom VARCHAR(255);

CREATE SEQUENCE seq_pa_mtom_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_mtom_request
(
        id_porta NUMBER NOT NULL,
        nome VARCHAR(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR(255),
	required NUMBER NOT NULL,
        -- fk/pk columns
        id NUMBER NOT NULL,
        -- fk/pk keys constraints
        CONSTRAINT fk_pa_mtom_request_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
        CONSTRAINT pk_pa_mtom_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_MTOMTREQ ON pa_mtom_request (id_porta);
CREATE TRIGGER trg_pa_mtom_request
BEFORE
insert on pa_mtom_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_mtom_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pa_mtom_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_mtom_response
(
        id_porta NUMBER NOT NULL,
        nome VARCHAR(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR(255),
	required NUMBER NOT NULL,
        -- fk/pk columns
        id NUMBER NOT NULL,
        -- fk/pk keys constraints
        CONSTRAINT fk_pa_mtom_response_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
        CONSTRAINT pk_pa_mtom_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_MTOMTRES ON pa_mtom_response (id_porta);
CREATE TRIGGER trg_pa_mtom_response
BEFORE
insert on pa_mtom_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_mtom_response.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/





