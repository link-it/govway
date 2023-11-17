-- **** Servizi Applicativi ****

CREATE SEQUENCE seq_servizi_applicativi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_applicativi
(
	nome VARCHAR2(2000) NOT NULL,
	tipo VARCHAR2(255),
	as_client NUMBER,
	descrizione CLOB,
	-- * Risposta Asincrona *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentorisp NUMBER,
	getmsgrisp VARCHAR2(255),
	-- FOREIGN KEY (id_gestione_errore_risp) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per la risposta asincrona
	id_gestione_errore_risp NUMBER,
	tipoauthrisp VARCHAR2(255),
	utenterisp VARCHAR2(255),
	passwordrisp VARCHAR2(255),
	invio_x_rif_risp VARCHAR2(255),
	risposta_x_rif_risp VARCHAR2(255),
	id_connettore_risp NUMBER NOT NULL,
	sbustamento_protocol_info_risp NUMBER,
	-- * Invocazione Servizio *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentoinv NUMBER,
	getmsginv VARCHAR2(255),
	-- FOREIGN KEY (id_gestione_errore_inv) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per l'invocazione servizio
	id_gestione_errore_inv NUMBER,
	tipoauthinv VARCHAR2(255),
	utenteinv VARCHAR2(255),
	passwordinv VARCHAR2(255),
	invio_x_rif_inv VARCHAR2(255),
	risposta_x_rif_inv VARCHAR2(255),
	id_connettore_inv NUMBER NOT NULL,
	sbustamento_protocol_info_inv NUMBER,
	-- * SoggettoErogatore *
	id_soggetto NUMBER NOT NULL,
	-- * Invocazione Porta *
	fault VARCHAR2(255),
	fault_actor VARCHAR2(255),
	generic_fault_code VARCHAR2(255),
	prefix_fault_code VARCHAR2(255),
	tipoauth VARCHAR2(255),
	utente VARCHAR2(2800),
	password VARCHAR2(255),
	subject VARCHAR2(2800),
	cn_subject VARCHAR2(255),
	issuer VARCHAR2(2800),
	cn_issuer VARCHAR2(255),
	certificate BLOB,
	cert_strict_verification NUMBER,
	token_policy VARCHAR2(255),
	invio_x_rif VARCHAR2(255),
	sbustamento_protocol_info NUMBER,
	tipologia_fruizione VARCHAR2(255),
	tipologia_erogazione VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	utente_richiedente VARCHAR2(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR2(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_servizi_applicativi_1 UNIQUE (nome,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_applicativi_1 FOREIGN KEY (id_connettore_inv) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_applicativi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_servizi_applicativi PRIMARY KEY (id)
);

-- index
CREATE INDEX index_servizi_applicativi_1 ON servizi_applicativi (id_soggetto);

ALTER TABLE servizi_applicativi MODIFY sbustamentorisp DEFAULT 0;
ALTER TABLE servizi_applicativi MODIFY sbustamento_protocol_info_risp DEFAULT 1;
ALTER TABLE servizi_applicativi MODIFY sbustamentoinv DEFAULT 0;
ALTER TABLE servizi_applicativi MODIFY sbustamento_protocol_info_inv DEFAULT 1;
ALTER TABLE servizi_applicativi MODIFY sbustamento_protocol_info DEFAULT 1;
ALTER TABLE servizi_applicativi MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_servizi_applicativi
BEFORE
insert on servizi_applicativi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_applicativi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



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

-- index
CREATE INDEX INDEX_SA_RUOLI ON sa_ruoli (id_servizio_applicativo);
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



CREATE SEQUENCE seq_sa_credenziali MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE sa_credenziali
(
	id_servizio_applicativo NUMBER NOT NULL,
	subject VARCHAR2(2800),
	cn_subject VARCHAR2(255),
	issuer VARCHAR2(2800),
	cn_issuer VARCHAR2(255),
	certificate BLOB,
	cert_strict_verification NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_credenziali_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_CRED ON sa_credenziali (id_servizio_applicativo);
CREATE TRIGGER trg_sa_credenziali
BEFORE
insert on sa_credenziali
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_sa_credenziali.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_sa_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE sa_properties
(
	id_servizio_applicativo NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(4000) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_sa_properties_1 UNIQUE (id_servizio_applicativo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_properties_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_PROP ON sa_properties (id_servizio_applicativo);
CREATE TRIGGER trg_sa_properties
BEFORE
insert on sa_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_sa_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


