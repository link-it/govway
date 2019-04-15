

CREATE SEQUENCE seq_pd_auth_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_auth_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_auth_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_auth_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_auth_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTH_PROP ON pd_auth_properties (id_porta);
CREATE TRIGGER trg_pd_auth_properties
BEFORE
insert on pd_auth_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_auth_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_auth_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_auth_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_auth_props_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_auth_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_auth_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_AUTH_PROP ON pa_auth_properties (id_porta);
CREATE TRIGGER trg_pa_auth_properties
BEFORE
insert on pa_auth_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_auth_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/





CREATE SEQUENCE seq_config_cache_regole MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_cache_regole
(
	status_min NUMBER,
	status_max NUMBER,
	fault NUMBER,
	cache_seconds NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_config_cache_regole PRIMARY KEY (id)
);


ALTER TABLE config_cache_regole MODIFY fault DEFAULT 0;

CREATE TRIGGER trg_config_cache_regole
BEFORE
insert on config_cache_regole
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_cache_regole.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_cache_regole MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_cache_regole
(
	id_porta NUMBER NOT NULL,
	status_min NUMBER,
	status_max NUMBER,
	fault NUMBER,
	cache_seconds NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_cache_regole_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_cache_regole PRIMARY KEY (id)
);


ALTER TABLE pd_cache_regole MODIFY fault DEFAULT 0;

CREATE TRIGGER trg_pd_cache_regole
BEFORE
insert on pd_cache_regole
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_cache_regole.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pa_cache_regole MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_cache_regole
(
	id_porta NUMBER NOT NULL,
	status_min NUMBER,
	status_max NUMBER,
	fault NUMBER,
	cache_seconds NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_cache_regole_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_cache_regole PRIMARY KEY (id)
);


ALTER TABLE pa_cache_regole MODIFY fault DEFAULT 0;

CREATE TRIGGER trg_pa_cache_regole
BEFORE
insert on pa_cache_regole
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_cache_regole.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



ALTER TABLE configurazione ADD response_cache_hash_hdr_list CLOB; 
ALTER TABLE configurazione ADD response_cache_control_nocache NUMBER;
ALTER TABLE configurazione ADD response_cache_control_maxage NUMBER;
ALTER TABLE configurazione ADD response_cache_control_nostore NUMBER;

ALTER TABLE porte_applicative ADD response_cache_hash_hdr_list CLOB; 
ALTER TABLE porte_applicative ADD response_cache_control_nocache NUMBER;
ALTER TABLE porte_applicative ADD response_cache_control_maxage NUMBER;
ALTER TABLE porte_applicative ADD response_cache_control_nostore NUMBER;

ALTER TABLE porte_delegate ADD response_cache_hash_hdr_list CLOB; 
ALTER TABLE porte_delegate ADD response_cache_control_nocache NUMBER;
ALTER TABLE porte_delegate ADD response_cache_control_maxage NUMBER;
ALTER TABLE porte_delegate ADD response_cache_control_nostore NUMBER;




CREATE SEQUENCE seq_pd_transform MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	req_conversione_enabled NUMBER NOT NULL,
	req_conversione_tipo VARCHAR2(255),
	req_conversione_template BLOB,
	req_content_type VARCHAR2(255),
	rest_transformation NUMBER NOT NULL,
	rest_method VARCHAR2(255),
	rest_path VARCHAR2(255),
	soap_transformation NUMBER NOT NULL,
	soap_version VARCHAR2(255),
	soap_action VARCHAR2(255),
	soap_envelope NUMBER NOT NULL,
	soap_envelope_as_attach NUMBER NOT NULL,
	soap_envelope_tipo VARCHAR2(255),
	soap_envelope_template BLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_transform_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pd_transform_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_transform PRIMARY KEY (id)
);


ALTER TABLE pd_transform MODIFY req_conversione_enabled DEFAULT 0;
ALTER TABLE pd_transform MODIFY rest_transformation DEFAULT 0;
ALTER TABLE pd_transform MODIFY soap_transformation DEFAULT 0;
ALTER TABLE pd_transform MODIFY soap_envelope DEFAULT 0;
ALTER TABLE pd_transform MODIFY soap_envelope_as_attach DEFAULT 0;

CREATE TRIGGER trg_pd_transform
BEFORE
insert on pd_transform
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_transform.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


CREATE SEQUENCE seq_pd_transform_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform_sa
(
	id_trasformazione NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_transform_sa_1 UNIQUE (id_trasformazione,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pd_transform_sa_2 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_sa PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_transform_sa
BEFORE
insert on pd_transform_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_transform_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_transform_hdr MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform_hdr
(
	id_trasformazione NUMBER NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_hdr_1 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_hdr PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pd_trasf_hdr_1 ON pd_transform_hdr (id_trasformazione);
CREATE TRIGGER trg_pd_transform_hdr
BEFORE
insert on pd_transform_hdr
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_transform_hdr.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_transform_url MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform_url
(
	id_trasformazione NUMBER NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_url_1 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_url PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pd_trasf_url_1 ON pd_transform_url (id_trasformazione);
CREATE TRIGGER trg_pd_transform_url
BEFORE
insert on pd_transform_url
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_transform_url.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_transform_risp MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform_risp
(
	id_trasformazione NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	applicabilita_status_min NUMBER,
	applicabilita_status_max NUMBER,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	conversione_enabled NUMBER NOT NULL,
	conversione_tipo VARCHAR2(255),
	conversione_template BLOB,
	content_type VARCHAR2(255),
	return_code NUMBER,
	soap_envelope NUMBER NOT NULL,
	soap_envelope_as_attach NUMBER NOT NULL,
	soap_envelope_tipo VARCHAR2(255),
	soap_envelope_template BLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_trasf_resp_1 UNIQUE (id_trasformazione,nome),
	CONSTRAINT uniq_pd_trasf_resp_2 UNIQUE (id_trasformazione,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_risp_1 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_risp PRIMARY KEY (id)
);


ALTER TABLE pd_transform_risp MODIFY conversione_enabled DEFAULT 0;
ALTER TABLE pd_transform_risp MODIFY soap_envelope DEFAULT 0;
ALTER TABLE pd_transform_risp MODIFY soap_envelope_as_attach DEFAULT 0;

CREATE TRIGGER trg_pd_transform_risp
BEFORE
insert on pd_transform_risp
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_transform_risp.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_transform_risp_hdr MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform_risp_hdr
(
	id_transform_risp NUMBER NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_risp_hdr_1 FOREIGN KEY (id_transform_risp) REFERENCES pd_transform_risp(id),
	CONSTRAINT pk_pd_transform_risp_hdr PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pd_trasf_hdr_resp_1 ON pd_transform_risp_hdr (id_transform_risp);
CREATE TRIGGER trg_pd_transform_risp_hdr
BEFORE
insert on pd_transform_risp_hdr
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_transform_risp_hdr.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




CREATE SEQUENCE seq_pa_transform MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	req_conversione_enabled NUMBER NOT NULL,
	req_conversione_tipo VARCHAR2(255),
	req_conversione_template BLOB,
	req_content_type VARCHAR2(255),
	rest_transformation NUMBER NOT NULL,
	rest_method VARCHAR2(255),
	rest_path VARCHAR2(255),
	soap_transformation NUMBER NOT NULL,
	soap_version VARCHAR2(255),
	soap_action VARCHAR2(255),
	soap_envelope NUMBER NOT NULL,
	soap_envelope_as_attach NUMBER NOT NULL,
	soap_envelope_tipo VARCHAR2(255),
	soap_envelope_template BLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_transform_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pa_transform_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_transform PRIMARY KEY (id)
);


ALTER TABLE pa_transform MODIFY req_conversione_enabled DEFAULT 0;
ALTER TABLE pa_transform MODIFY rest_transformation DEFAULT 0;
ALTER TABLE pa_transform MODIFY soap_transformation DEFAULT 0;
ALTER TABLE pa_transform MODIFY soap_envelope DEFAULT 0;
ALTER TABLE pa_transform MODIFY soap_envelope_as_attach DEFAULT 0;

CREATE TRIGGER trg_pa_transform
BEFORE
insert on pa_transform
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


CREATE SEQUENCE seq_pa_transform_soggetti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform_soggetti
(
	id_trasformazione NUMBER NOT NULL,
	tipo_soggetto VARCHAR2(255) NOT NULL,
	nome_soggetto VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_transform_soggetti_1 UNIQUE (id_trasformazione,tipo_soggetto,nome_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_soggetti_1 FOREIGN KEY (id_trasformazione) REFERENCES pa_transform(id),
	CONSTRAINT pk_pa_transform_soggetti PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_transform_soggetti
BEFORE
insert on pa_transform_soggetti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform_soggetti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_transform_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform_sa
(
	id_trasformazione NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_transform_sa_1 UNIQUE (id_trasformazione,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pa_transform_sa_2 FOREIGN KEY (id_trasformazione) REFERENCES pa_transform(id),
	CONSTRAINT pk_pa_transform_sa PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_transform_sa
BEFORE
insert on pa_transform_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


CREATE SEQUENCE seq_pa_transform_hdr MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform_hdr
(
	id_trasformazione NUMBER NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_hdr_1 FOREIGN KEY (id_trasformazione) REFERENCES pa_transform(id),
	CONSTRAINT pk_pa_transform_hdr PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pa_trasf_hdr_1 ON pa_transform_hdr (id_trasformazione);
CREATE TRIGGER trg_pa_transform_hdr
BEFORE
insert on pa_transform_hdr
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform_hdr.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_transform_url MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform_url
(
	id_trasformazione NUMBER NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_url_1 FOREIGN KEY (id_trasformazione) REFERENCES pa_transform(id),
	CONSTRAINT pk_pa_transform_url PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pa_trasf_url_1 ON pa_transform_url (id_trasformazione);
CREATE TRIGGER trg_pa_transform_url
BEFORE
insert on pa_transform_url
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform_url.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_transform_risp MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform_risp
(
	id_trasformazione NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	applicabilita_status_min NUMBER,
	applicabilita_status_max NUMBER,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	conversione_enabled NUMBER NOT NULL,
	conversione_tipo VARCHAR2(255),
	conversione_template BLOB,
	content_type VARCHAR2(255),
	return_code NUMBER,
	soap_envelope NUMBER NOT NULL,
	soap_envelope_as_attach NUMBER NOT NULL,
	soap_envelope_tipo VARCHAR2(255),
	soap_envelope_template BLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_trasf_resp_1 UNIQUE (id_trasformazione,nome),
	CONSTRAINT uniq_pa_trasf_resp_2 UNIQUE (id_trasformazione,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_risp_1 FOREIGN KEY (id_trasformazione) REFERENCES pa_transform(id),
	CONSTRAINT pk_pa_transform_risp PRIMARY KEY (id)
);


ALTER TABLE pa_transform_risp MODIFY conversione_enabled DEFAULT 0;
ALTER TABLE pa_transform_risp MODIFY soap_envelope DEFAULT 0;
ALTER TABLE pa_transform_risp MODIFY soap_envelope_as_attach DEFAULT 0;

CREATE TRIGGER trg_pa_transform_risp
BEFORE
insert on pa_transform_risp
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform_risp.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_transform_risp_hdr MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_transform_risp_hdr
(
	id_transform_risp NUMBER NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_transform_risp_hdr_1 FOREIGN KEY (id_transform_risp) REFERENCES pa_transform_risp(id),
	CONSTRAINT pk_pa_transform_risp_hdr PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pa_trasf_hdr_resp_1 ON pa_transform_risp_hdr (id_transform_risp);
CREATE TRIGGER trg_pa_transform_risp_hdr
BEFORE
insert on pa_transform_risp_hdr
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_transform_risp_hdr.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/





-- Inizializzo per avere nuove visualizzazioni
delete from users_stati ;

