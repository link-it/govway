-- **** Porte Applicative ****

CREATE TABLE porte_applicative
(
	nome_porta VARCHAR(4000) NOT NULL,
	descrizione VARCHAR(255),
	-- Soggetto Virtuale
	id_soggetto_virtuale BIGINT,
	tipo_soggetto_virtuale VARCHAR(255),
	nome_soggetto_virtuale VARCHAR(255),
	-- Servizio
	id_servizio BIGINT,
	tipo_servizio VARCHAR(255) NOT NULL,
	servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL DEFAULT 1,
	id_accordo BIGINT,
	id_port_type BIGINT,
	-- azione
	azione VARCHAR(255),
	mode_azione VARCHAR(255),
	pattern_azione VARCHAR(255),
	nome_porta_delegante_azione VARCHAR(255),
	-- abilitato/disabilitato
	force_interface_based_azione VARCHAR(255),
	-- disable/packaging/unpackaging/verify
	mtom_request_mode VARCHAR(255),
	-- disable/packaging/unpackaging/verify
	mtom_response_mode VARCHAR(255),
	-- abilitato/disabilitato (se abilitato le WSSproperties sono presenti nelle tabelle ...._security_request/response)
	security VARCHAR(255),
	-- abilitato/disabilitato
	security_mtom_req VARCHAR(255),
	-- abilitato/disabilitato
	security_mtom_res VARCHAR(255),
	security_request_mode VARCHAR(255),
	security_response_mode VARCHAR(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_sim VARCHAR(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_asim VARCHAR(255),
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR(255),
	-- wsdl/interface/xsd
	validazione_contenuti_tipo VARCHAR(255),
	-- abilitato/disabilitato
	validazione_contenuti_mtom VARCHAR(255),
	-- lista di tipi separati dalla virgola
	integrazione VARCHAR(255),
	-- scadenza correlazione applicativa
	scadenza_correlazione_appl VARCHAR(255),
	-- abilitato/disabilitato
	allega_body VARCHAR(255),
	-- abilitato/disabilitato
	scarta_body VARCHAR(255),
	-- abilitato/disabilitato
	gestione_manifest VARCHAR(255),
	-- abilitato/disabilitato
	stateless VARCHAR(255),
	behaviour VARCHAR(255),
	-- Controllo Accessi
	autenticazione VARCHAR(255),
	-- abilitato/disabilitato
	autenticazione_opzionale VARCHAR(255),
	-- Gestione Token
	token_policy VARCHAR(255),
	token_opzionale VARCHAR(255),
	token_validazione VARCHAR(255),
	token_introspection VARCHAR(255),
	token_user_info VARCHAR(255),
	token_forward VARCHAR(255),
	token_options VARCHAR(4000),
	token_authn_issuer VARCHAR(255),
	token_authn_client_id VARCHAR(255),
	token_authn_subject VARCHAR(255),
	token_authn_username VARCHAR(255),
	token_authn_email VARCHAR(255),
	-- Autorizzazione
	autorizzazione VARCHAR(255),
	autorizzazione_xacml VARCHAR(max),
	autorizzazione_contenuto VARCHAR(255),
	-- all/any
	ruoli_match VARCHAR(255),
	scope_stato VARCHAR(255),
	-- all/any
	scope_match VARCHAR(255),
	-- abilitato/disabilitato
	ricerca_porta_azione_delegata VARCHAR(255),
	-- Livello Log Messaggi Diagnostici
	msg_diag_severita VARCHAR(255),
	tracciamento_esiti VARCHAR(255),
	-- abilitato/disabilitato
	stato VARCHAR(255),
	-- proprietario porta applicativa
	id_soggetto BIGINT NOT NULL,
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_porte_applicative_1 UNIQUE (nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_applicative PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_porte_applicative_1 ON porte_applicative (nome_porta);



CREATE TABLE porte_applicative_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_porte_applicative_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_applicative_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_porte_applicative_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA ON porte_applicative_sa (id_porta);



CREATE TABLE pa_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_pa_properties_1 UNIQUE (id_porta,nome,valore),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_PROP ON pa_properties (id_porta);



CREATE TABLE pa_mtom_request
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	pattern VARCHAR(max) NOT NULL,
	content_type VARCHAR(255),
	required INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_mtom_request_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_mtom_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_MTOMTREQ ON pa_mtom_request (id_porta);



CREATE TABLE pa_mtom_response
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	pattern VARCHAR(max) NOT NULL,
	content_type VARCHAR(255),
	required INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_mtom_response_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_mtom_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_MTOMTRES ON pa_mtom_response (id_porta);



CREATE TABLE pa_security_request
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_security_request_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_security_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_WSSREQ ON pa_security_request (id_porta);



CREATE TABLE pa_security_response
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_security_response_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_security_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_WSSRES ON pa_security_response (id_porta);



CREATE TABLE pa_correlazione
(
	id_porta BIGINT NOT NULL,
	nome_elemento VARCHAR(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR(255),
	-- pattern utilizzato solo per content/url based
	pattern VARCHAR(max),
	-- blocca/accetta
	identificazione_fallita VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_correlazione_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_correlazione PRIMARY KEY (id)
);




CREATE TABLE pa_correlazione_risposta
(
	id_porta BIGINT NOT NULL,
	nome_elemento VARCHAR(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR(255),
	-- pattern utilizzato solo per content/url based
	pattern VARCHAR(max),
	-- blocca/accetta
	identificazione_fallita VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_correlazione_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_correlazione_risposta PRIMARY KEY (id)
);




CREATE TABLE pa_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_pa_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ruoli PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_pa_ruoli_1 ON pa_ruoli (id_porta,ruolo);



CREATE TABLE pa_scope
(
	id_porta BIGINT NOT NULL,
	scope VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_pa_scope_1 UNIQUE (id_porta,scope),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_scope_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_scope PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_pa_scope_1 ON pa_scope (id_porta,scope);



CREATE TABLE pa_soggetti
(
	id_porta BIGINT NOT NULL,
	tipo_soggetto VARCHAR(255) NOT NULL,
	nome_soggetto VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_pa_soggetti_1 UNIQUE (id_porta,tipo_soggetto,nome_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_soggetti_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_soggetti PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_pa_soggetti_1 ON pa_soggetti (id_porta,tipo_soggetto,nome_soggetto);



CREATE TABLE porte_applicative_sa_auth
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_pa_sa_auth_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_applicative_sa_auth_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_applicative_sa_auth_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_porte_applicative_sa_auth PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_AUTH ON porte_applicative_sa_auth (id_porta);



CREATE TABLE pa_azioni
(
	id_porta BIGINT NOT NULL,
	azione VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_pa_azioni_1 UNIQUE (id_porta,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_azioni_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_azioni PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_pa_azioni_1 ON pa_azioni (id_porta,azione);


