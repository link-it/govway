-- **** Porte Delegate ****

CREATE SEQUENCE seq_porte_delegate MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_delegate
(
	nome_porta VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	-- la location sovrascrive il nome di una porta delegata, nella sua url di invocazione
	location VARCHAR2(255),
	-- * Soggetto Erogatore *
	-- tipo/nome per le modalita static
	-- tipo/pattern per la modalita contentBased/urlBased
	-- id utilizzato in caso di registryInput
	id_soggetto_erogatore NUMBER,
	tipo_soggetto_erogatore VARCHAR2(255),
	nome_soggetto_erogatore VARCHAR2(255),
	mode_soggetto_erogatore VARCHAR2(255),
	pattern_soggetto_erogatore VARCHAR2(255),
	-- * Servizio *
	-- tipo/nome per le modalita static
	-- tipo/pattern per la modalita contentBased/urlBased
	-- id utilizzato in caso di registryInput
	id_servizio NUMBER,
	tipo_servizio VARCHAR2(255),
	nome_servizio VARCHAR2(255),
	mode_servizio VARCHAR2(255),
	pattern_servizio VARCHAR2(255),
	id_accordo NUMBER,
	id_port_type NUMBER,
	-- * Azione *
	-- tipo/nome per le modalita static
	-- tipo/pattern per la modalita contentBased/urlBased
	-- id utilizzato in caso di registryInput
	id_azione NUMBER,
	nome_azione VARCHAR2(255),
	mode_azione VARCHAR2(255),
	pattern_azione VARCHAR2(255),
	-- abilitato/disabilitato
	force_wsdl_based_azione VARCHAR2(255),
	-- * Configurazione *
	autenticazione VARCHAR2(255),
	autorizzazione VARCHAR2(255),
	autorizzazione_contenuto VARCHAR2(255),
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
	-- abilitato/disabilitato
	local_forward VARCHAR2(255),
	-- proprietario porta delegata (Soggetto fruitore)
	id_soggetto NUMBER NOT NULL,
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_1 UNIQUE (id_soggetto,nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_delegate PRIMARY KEY (id)
);


ALTER TABLE porte_delegate MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_porte_delegate
BEFORE
insert on porte_delegate
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_delegate.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_porte_delegate_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_delegate_sa
(
	id_porta NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_delegate_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_porte_delegate_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_SA ON porte_delegate_sa (id_porta);
CREATE TRIGGER trg_porte_delegate_sa
BEFORE
insert on porte_delegate_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_delegate_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_mtom_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_mtom_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR2(255),
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
	nome VARCHAR2(255) NOT NULL,
	pattern CLOB NOT NULL,
	content_type VARCHAR2(255),
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



CREATE SEQUENCE seq_pd_ws_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_ws_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ws_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ws_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_WSSREQ ON pd_ws_request (id_porta);
CREATE TRIGGER trg_pd_ws_request
BEFORE
insert on pd_ws_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_ws_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_ws_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_ws_response
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ws_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ws_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_WSSRES ON pd_ws_response (id_porta);
CREATE TRIGGER trg_pd_ws_response
BEFORE
insert on pd_ws_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_ws_response.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_correlazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_correlazione
(
	id_porta NUMBER NOT NULL,
	nome_elemento VARCHAR2(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR2(255),
	-- pattern utilizzato solo per content/url based
	pattern CLOB,
	-- blocca/accetta
	identificazione_fallita VARCHAR2(255),
	-- abilitato/disabilitato
	riuso_id VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_correlazione_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_correlazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_correlazione
BEFORE
insert on pd_correlazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_correlazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_correlazione_risposta MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_correlazione_risposta
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
	CONSTRAINT fk_pd_correlazione_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_correlazione_risposta PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_correlazione_risposta
BEFORE
insert on pd_correlazione_risposta
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_correlazione_risposta.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


