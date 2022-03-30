-- **** Porte Delegate ****

CREATE SEQUENCE seq_porte_delegate MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_delegate
(
	nome_porta VARCHAR2(2000) NOT NULL,
	descrizione VARCHAR2(255),
	-- * Soggetto Erogatore *
	-- tipo/nome per le modalita static
	-- id utilizzato in caso di registryInput
	id_soggetto_erogatore NUMBER,
	tipo_soggetto_erogatore VARCHAR2(255) NOT NULL,
	nome_soggetto_erogatore VARCHAR2(255) NOT NULL,
	-- * Servizio *
	-- tipo/nome/versione per le modalita static
	-- id utilizzato in caso di registryInput
	id_servizio NUMBER,
	tipo_servizio VARCHAR2(255) NOT NULL,
	nome_servizio VARCHAR2(255) NOT NULL,
	versione_servizio NUMBER NOT NULL,
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
	nome_porta_delegante_azione VARCHAR2(255),
	-- abilitato/disabilitato
	force_interface_based_azione VARCHAR2(255),
	-- Controllo Accessi
	autenticazione VARCHAR2(255),
	-- abilitato/disabilitato
	autenticazione_opzionale VARCHAR2(255),
	-- Gestione Token
	token_policy VARCHAR2(255),
	token_opzionale VARCHAR2(255),
	token_validazione VARCHAR2(255),
	token_introspection VARCHAR2(255),
	token_user_info VARCHAR2(255),
	token_forward VARCHAR2(255),
	token_options VARCHAR2(4000),
	token_authn_issuer VARCHAR2(255),
	token_authn_client_id VARCHAR2(255),
	token_authn_subject VARCHAR2(255),
	token_authn_username VARCHAR2(255),
	token_authn_email VARCHAR2(255),
	-- Autorizzazione
	autorizzazione VARCHAR2(255),
	autorizzazione_xacml CLOB,
	autorizzazione_contenuto VARCHAR2(255),
	-- disable/packaging/unpackaging/verify
	mtom_request_mode VARCHAR2(255),
	-- disable/packaging/unpackaging/verify
	mtom_response_mode VARCHAR2(255),
	-- abilitato/disabilitato (se abilitato le WSSproperties sono presenti nelle tabelle ...._security_request/response)
	security VARCHAR2(255),
	-- abilitato/disabilitato
	security_mtom_req VARCHAR2(255),
	-- abilitato/disabilitato
	security_mtom_res VARCHAR2(255),
	security_request_mode VARCHAR2(255),
	security_response_mode VARCHAR2(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_sim VARCHAR2(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_asim VARCHAR2(255),
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR2(255),
	-- wsdl/interface/xsd
	validazione_contenuti_tipo VARCHAR2(255),
	-- abilitato/disabilitato
	validazione_contenuti_mtom VARCHAR2(255),
	-- lista di tipi separati dalla virgola
	integrazione VARCHAR2(4000),
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
	-- Nome della PortaApplicativa
	local_forward_pa VARCHAR2(255),
	-- all/any
	ruoli_match VARCHAR2(255),
	scope_stato VARCHAR2(255),
	-- all/any
	scope_match VARCHAR2(255),
	-- abilitato/disabilitato
	ricerca_porta_azione_delegata VARCHAR2(255),
	-- Livello Log Messaggi Diagnostici
	msg_diag_severita VARCHAR2(255),
	tracciamento_esiti VARCHAR2(255),
	-- Gestione CORS
	cors_stato VARCHAR2(255),
	cors_tipo VARCHAR2(255),
	cors_all_allow_origins VARCHAR2(255),
	cors_all_allow_methods VARCHAR2(255),
	cors_all_allow_headers VARCHAR2(255),
	cors_allow_credentials VARCHAR2(255),
	cors_allow_max_age NUMBER,
	cors_allow_max_age_seconds NUMBER,
	cors_allow_origins CLOB,
	cors_allow_headers CLOB,
	cors_allow_methods CLOB,
	cors_allow_expose_headers CLOB,
	-- Response caching
	response_cache_stato VARCHAR2(255),
	response_cache_seconds NUMBER,
	response_cache_max_msg_size NUMBER,
	response_cache_hash_url VARCHAR2(255),
	response_cache_hash_query VARCHAR2(255),
	response_cache_hash_query_list CLOB,
	response_cache_hash_headers VARCHAR2(255),
	response_cache_hash_hdr_list CLOB,
	response_cache_hash_payload VARCHAR2(255),
	response_cache_control_nocache NUMBER,
	response_cache_control_maxage NUMBER,
	response_cache_control_nostore NUMBER,
	-- Stato della porta: abilitato/disabilitato
	stato VARCHAR2(255),
	-- proprietario porta delegata (Soggetto fruitore)
	id_soggetto NUMBER NOT NULL,
	ora_registrazione TIMESTAMP,
	options VARCHAR2(4000),
	canale VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_1 UNIQUE (nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_delegate PRIMARY KEY (id)
);

-- index
CREATE INDEX index_porte_delegate_1 ON porte_delegate (id_soggetto);
CREATE INDEX index_porte_delegate_2 ON porte_delegate (canale);

ALTER TABLE porte_delegate MODIFY versione_servizio DEFAULT 1;
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



CREATE SEQUENCE seq_pd_auth_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_auth_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_auth_props_1 UNIQUE (id_porta,nome),
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



CREATE SEQUENCE seq_pd_authz_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_authz_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_authz_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authz_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authz_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTHZ_PROP ON pd_authz_properties (id_porta);
CREATE TRIGGER trg_pd_authz_properties
BEFORE
insert on pd_authz_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_authz_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_authzc_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_authzc_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_authzc_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authzc_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authzc_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_AUTHZC_PROP ON pd_authzc_properties (id_porta);
CREATE TRIGGER trg_pd_authzc_properties
BEFORE
insert on pd_authzc_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_authzc_properties.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_properties MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_properties
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_pd_properties_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_PROP ON pd_properties (id_porta);
CREATE TRIGGER trg_pd_properties
BEFORE
insert on pd_properties
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_properties.nextval INTO :new.id
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



CREATE SEQUENCE seq_pd_security_request MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_security_request
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_security_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_security_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_WSSREQ ON pd_security_request (id_porta);
CREATE TRIGGER trg_pd_security_request
BEFORE
insert on pd_security_request
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_security_request.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_security_response MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_security_response
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	valore CLOB NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_security_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_security_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_WSSRES ON pd_security_response (id_porta);
CREATE TRIGGER trg_pd_security_response
BEFORE
insert on pd_security_response
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_security_response.nextval INTO :new.id
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

-- index
CREATE INDEX INDEX_PD_CORR_REQ ON pd_correlazione (id_porta);
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

-- index
CREATE INDEX INDEX_PD_CORR_RES ON pd_correlazione_risposta (id_porta);
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



CREATE SEQUENCE seq_pd_scope MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_scope
(
	id_porta NUMBER NOT NULL,
	scope VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_scope_1 UNIQUE (id_porta,scope),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_scope_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_scope PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_scope
BEFORE
insert on pd_scope
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_scope.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_azioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_azioni
(
	id_porta NUMBER NOT NULL,
	azione VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_azioni_1 UNIQUE (id_porta,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_azioni_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_azioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_azioni
BEFORE
insert on pd_azioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_azioni.nextval INTO :new.id
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

-- index
CREATE INDEX INDEX_PD_CACHE ON pd_cache_regole (id_porta);

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



CREATE SEQUENCE seq_pd_transform MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_transform
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	applicabilita_azioni CLOB,
	applicabilita_ct CLOB,
	applicabilita_pattern CLOB,
	req_conversione_enabled NUMBER NOT NULL,
	req_conversione_tipo VARCHAR2(255),
	req_conversione_template BLOB,
	req_content_type VARCHAR2(255),
	rest_transformation NUMBER NOT NULL,
	rest_method VARCHAR2(255),
	rest_path VARCHAR2(4000),
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
	identificazione_fallita VARCHAR2(255),
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
	identificazione_fallita VARCHAR2(255),
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
	return_code VARCHAR2(255),
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
	identificazione_fallita VARCHAR2(255),
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



CREATE SEQUENCE seq_pd_handlers MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_handlers
(
	id_porta NUMBER NOT NULL,
	tipologia VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_handlers PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_handlers
BEFORE
insert on pd_handlers
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_handlers.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_pd_aa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pd_aa
(
	id_porta NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	attributi CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_aa_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_aa_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_aa PRIMARY KEY (id)
);

CREATE TRIGGER trg_pd_aa
BEFORE
insert on pd_aa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pd_aa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


