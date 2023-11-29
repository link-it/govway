-- **** Soggetti ****

CREATE SEQUENCE seq_soggetti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti
(
	nome_soggetto VARCHAR2(255) NOT NULL,
	tipo_soggetto VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(4000),
	identificativo_porta VARCHAR2(255),
	-- 1/0 (true/false) Indicazione se il soggetto svolge è quello di default per il protocollo
	is_default NUMBER,
	-- 1/0 (true/false) svolge attivita di router
	is_router NUMBER,
	id_connettore NUMBER NOT NULL,
	superuser VARCHAR2(255),
	server VARCHAR2(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato NUMBER,
	ora_registrazione TIMESTAMP,
	profilo VARCHAR2(255),
	codice_ipa VARCHAR2(255) NOT NULL,
	tipoauth VARCHAR2(255),
	utente VARCHAR2(2800),
	password VARCHAR2(255),
	subject VARCHAR2(2800),
	cn_subject VARCHAR2(255),
	issuer VARCHAR2(2800),
	cn_issuer VARCHAR2(255),
	certificate BLOB,
	cert_strict_verification NUMBER,
	utente_richiedente VARCHAR2(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR2(255),
	data_ultima_modifica TIMESTAMP,
	pd_url_prefix_rewriter VARCHAR2(255),
	pa_url_prefix_rewriter VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_soggetti_1 UNIQUE (nome_soggetto,tipo_soggetto),
	CONSTRAINT unique_soggetti_2 UNIQUE (codice_ipa),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT pk_soggetti PRIMARY KEY (id)
);


ALTER TABLE soggetti MODIFY is_default DEFAULT 0;
ALTER TABLE soggetti MODIFY is_router DEFAULT 0;
ALTER TABLE soggetti MODIFY privato DEFAULT 0;
ALTER TABLE soggetti MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_soggetti
BEFORE
insert on soggetti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_soggetti_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_ruoli
(
	id_soggetto NUMBER NOT NULL,
	id_ruolo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_soggetti_ruoli_1 UNIQUE (id_soggetto,id_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_ruoli_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_soggetti_ruoli_2 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT pk_soggetti_ruoli PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_RUOLI ON soggetti_ruoli (id_soggetto);
CREATE TRIGGER trg_soggetti_ruoli
BEFORE
insert on soggetti_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_soggetti_credenziali MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_credenziali
(
	id_soggetto NUMBER NOT NULL,
	subject VARCHAR2(2800),
	cn_subject VARCHAR2(255),
	issuer VARCHAR2(2800),
	cn_issuer VARCHAR2(255),
	certificate BLOB,
	cert_strict_verification NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_credenziali_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_CRED ON soggetti_credenziali (id_soggetto);
CREATE TRIGGER trg_soggetti_credenziali
BEFORE
insert on soggetti_credenziali
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_credenziali.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_soggetti_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_properties
(
	id_soggetto NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(4000) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_soggetti_props_1 UNIQUE (id_soggetto,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_properties_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_PROP ON soggetti_properties (id_soggetto);
CREATE TRIGGER trg_soggetti_properties
BEFORE
insert on soggetti_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


