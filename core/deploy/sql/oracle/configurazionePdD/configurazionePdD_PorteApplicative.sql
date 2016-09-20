-- **** Porte Applicative ****

CREATE SEQUENCE seq_porte_applicative MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_applicative
(
	nome_porta VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	-- Soggetto Virtuale
	id_soggetto_virtuale NUMBER,
	tipo_soggetto_virtuale VARCHAR2(255),
	nome_soggetto_virtuale VARCHAR2(255),
	-- Servizio
	id_servizio NUMBER,
	tipo_servizio VARCHAR2(255),
	servizio VARCHAR2(255),
	id_accordo NUMBER,
	id_port_type NUMBER,
	-- azione
	azione VARCHAR2(255),
	-- disable/packaging/unpackaging/verify
	mtom_request_mode VARCHAR2(255),
	-- disable/packaging/unpackaging/verify
	mtom_response_mode VARCHAR2(255),
	-- abilitato/disabilitato (se abilitato le WSSproperties sono presenti nelle tabelle ...._ws_request/response)
	ws_security VARCHAR2(255),
	-- abilitato/disabilitato
	ws_security_mtom_req VARCHAR2(255),
	-- abilitato/disabilitato
	ws_security_mtom_res VARCHAR2(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_sim VARCHAR2(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_asim VARCHAR2(255),
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR2(255),
	-- wsdl/openspcoop/xsd
	validazione_contenuti_tipo VARCHAR2(255),
	-- abilitato/disabilitato
	validazione_contenuti_mtom VARCHAR2(255),
	-- lista di tipi separati dalla virgola
	integrazione VARCHAR2(255),
	-- scadenza correlazione applicativa
	scadenza_correlazione_appl VARCHAR2(255),
	-- abilitato/disabilitato
	allega_body VARCHAR2(255),
	-- abilitato/disabilitato
	scarta_body VARCHAR2(255),
	-- abilitato/disabilitato
	gestione_manifest VARCHAR2(255),
	-- abilitato/disabilitato
	stateless VARCHAR2(255),
	behaviour VARCHAR2(255),
	autorizzazione_contenuto VARCHAR2(255),
	-- proprietario porta applicativa
	id_soggetto NUMBER NOT NULL,
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_applicative_1 UNIQUE (id_soggetto,nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_applicative PRIMARY KEY (id)
);


ALTER TABLE porte_applicative MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_porte_applicative
BEFORE
insert on porte_applicative
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_applicative.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_porte_applicative_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_applicative_sa
(
	id_porta NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_applicative_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_applicative_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_porte_applicative_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA ON porte_applicative_sa (id_porta);
CREATE TRIGGER trg_porte_applicative_sa
BEFORE
insert on porte_applicative_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_applicative_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pa_properties_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_PROP ON pa_properties (id_porta);
CREATE TRIGGER trg_pa_properties
BEFORE
insert on pa_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_mtom_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_mtom_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR2(255),
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
	nome VARCHAR2(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR2(255),
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



CREATE SEQUENCE seq_pa_ws_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_ws_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ws_request_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ws_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_WSSREQ ON pa_ws_request (id_porta);
CREATE TRIGGER trg_pa_ws_request
BEFORE
insert on pa_ws_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_ws_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_ws_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_ws_response
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ws_response_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ws_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_WSSRES ON pa_ws_response (id_porta);
CREATE TRIGGER trg_pa_ws_response
BEFORE
insert on pa_ws_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_ws_response.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_correlazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_correlazione
(
	id_porta NUMBER NOT NULL,
	nome_elemento VARCHAR2(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR2(255),
	-- pattern utilizzato solo per content/url based
	pattern CLOB,
	-- blocca/accetta
	identificazione_fallita VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_correlazione_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_correlazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_correlazione
BEFORE
insert on pa_correlazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_correlazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pa_correlazione_risposta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pa_correlazione_risposta
(
	id_porta NUMBER NOT NULL,
	nome_elemento VARCHAR2(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR2(255),
	-- pattern utilizzato solo per content/url based
	pattern CLOB,
	-- blocca/accetta
	identificazione_fallita VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_correlazione_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_correlazione_risposta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pa_correlazione_risposta
BEFORE
insert on pa_correlazione_risposta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pa_correlazione_risposta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


