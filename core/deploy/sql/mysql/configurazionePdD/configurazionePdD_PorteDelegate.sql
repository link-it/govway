-- **** Porte Delegate ****

CREATE TABLE porte_delegate
(
	nome_porta VARCHAR(2000) NOT NULL,
	descrizione VARCHAR(255),
	-- * Soggetto Erogatore *
	-- tipo/nome per le modalita static
	-- id utilizzato in caso di registryInput
	id_soggetto_erogatore BIGINT,
	tipo_soggetto_erogatore VARCHAR(255) NOT NULL,
	nome_soggetto_erogatore VARCHAR(255) NOT NULL,
	-- * Servizio *
	-- tipo/nome/versione per le modalita static
	-- id utilizzato in caso di registryInput
	id_servizio BIGINT,
	tipo_servizio VARCHAR(255) NOT NULL,
	nome_servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL DEFAULT 1,
	id_accordo BIGINT,
	id_port_type BIGINT,
	-- * Azione *
	-- tipo/nome per le modalita static
	-- tipo/pattern per la modalita contentBased/urlBased
	-- id utilizzato in caso di registryInput
	id_azione BIGINT,
	nome_azione VARCHAR(255),
	mode_azione VARCHAR(255),
	pattern_azione VARCHAR(255),
	nome_porta_delegante_azione VARCHAR(255),
	-- abilitato/disabilitato
	force_interface_based_azione VARCHAR(255),
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
	autorizzazione_xacml MEDIUMTEXT,
	autorizzazione_contenuto VARCHAR(255),
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
	integrazione VARCHAR(4000),
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
	-- abilitato/disabilitato
	local_forward VARCHAR(255),
	-- Nome della PortaApplicativa
	local_forward_pa VARCHAR(255),
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
	-- Gestione CORS
	cors_stato VARCHAR(255),
	cors_tipo VARCHAR(255),
	cors_all_allow_origins VARCHAR(255),
	cors_all_allow_methods VARCHAR(255),
	cors_all_allow_headers VARCHAR(255),
	cors_allow_credentials VARCHAR(255),
	cors_allow_max_age INT,
	cors_allow_max_age_seconds INT,
	cors_allow_origins TEXT,
	cors_allow_headers TEXT,
	cors_allow_methods TEXT,
	cors_allow_expose_headers TEXT,
	-- Response caching
	response_cache_stato VARCHAR(255),
	response_cache_seconds INT,
	response_cache_max_msg_size BIGINT,
	response_cache_hash_url VARCHAR(255),
	response_cache_hash_query VARCHAR(255),
	response_cache_hash_query_list TEXT,
	response_cache_hash_headers VARCHAR(255),
	response_cache_hash_hdr_list TEXT,
	response_cache_hash_payload VARCHAR(255),
	response_cache_control_nocache INT,
	response_cache_control_maxage INT,
	response_cache_control_nostore INT,
	-- Stato della porta: abilitato/disabilitato
	stato VARCHAR(255),
	-- proprietario porta delegata (Soggetto fruitore)
	id_soggetto BIGINT NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	options VARCHAR(4000),
	canale VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_1 UNIQUE (nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_delegate PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_porte_delegate_1 ON porte_delegate (nome_porta);
CREATE INDEX index_porte_delegate_2 ON porte_delegate (id_soggetto);
CREATE INDEX index_porte_delegate_3 ON porte_delegate (canale);



CREATE TABLE porte_delegate_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_delegate_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_porte_delegate_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_SA ON porte_delegate_sa (id_porta);



CREATE TABLE pd_auth_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_auth_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_auth_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_auth_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_AUTH_PROP ON pd_auth_properties (id_porta);



CREATE TABLE pd_authz_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_authz_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authz_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authz_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_AUTHZ_PROP ON pd_authz_properties (id_porta);



CREATE TABLE pd_authzc_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_authzc_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_authzc_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_authzc_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_AUTHZC_PROP ON pd_authzc_properties (id_porta);



CREATE TABLE pd_ct_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_ct_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ct_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ct_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_pd_ct_props_1 ON pd_ct_properties (valore);
CREATE INDEX INDEX_PD_CT_PROP ON pd_ct_properties (id_porta);



CREATE TABLE pd_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_properties_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_PROP ON pd_properties (id_porta);



CREATE TABLE pd_mtom_request
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	pattern TEXT NOT NULL,
	content_type VARCHAR(255),
	required INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_mtom_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_mtom_request PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_MTOMTREQ ON pd_mtom_request (id_porta);



CREATE TABLE pd_mtom_response
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	pattern TEXT NOT NULL,
	content_type VARCHAR(255),
	required INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_mtom_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_mtom_response PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_MTOMTRES ON pd_mtom_response (id_porta);



CREATE TABLE pd_security_request
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore TEXT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_security_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_security_request PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_WSSREQ ON pd_security_request (id_porta);



CREATE TABLE pd_security_response
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore TEXT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_security_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_security_response PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_WSSRES ON pd_security_response (id_porta);



CREATE TABLE pd_correlazione
(
	id_porta BIGINT NOT NULL,
	nome_elemento VARCHAR(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR(255),
	-- pattern utilizzato solo per content/url based
	pattern TEXT,
	-- blocca/accetta
	identificazione_fallita VARCHAR(255),
	-- abilitato/disabilitato
	riuso_id VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_correlazione_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_correlazione PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_CORR_REQ ON pd_correlazione (id_porta);



CREATE TABLE pd_correlazione_risposta
(
	id_porta BIGINT NOT NULL,
	nome_elemento VARCHAR(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR(255),
	-- pattern utilizzato solo per content/url based
	pattern TEXT,
	-- blocca/accetta
	identificazione_fallita VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_correlazione_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_correlazione_risposta PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_CORR_RES ON pd_correlazione_risposta (id_porta);



CREATE TABLE pd_ruoli
(
	id_porta BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_ruoli_1 UNIQUE (id_porta,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ruoli_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_ruoli_1 ON pd_ruoli (id_porta,ruolo);



CREATE TABLE pd_scope
(
	id_porta BIGINT NOT NULL,
	scope VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_scope_1 UNIQUE (id_porta,scope),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_scope_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_scope PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_scope_1 ON pd_scope (id_porta,scope);



CREATE TABLE pd_azioni
(
	id_porta BIGINT NOT NULL,
	azione VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_azioni_1 UNIQUE (id_porta,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_azioni_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_azioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_azioni_1 ON pd_azioni (id_porta,azione);



CREATE TABLE pd_cache_regole
(
	id_porta BIGINT NOT NULL,
	status_min INT,
	status_max INT,
	fault INT DEFAULT 0,
	cache_seconds INT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_cache_regole_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_cache_regole PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PD_CACHE ON pd_cache_regole (id_porta);



CREATE TABLE pd_transform
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	applicabilita_azioni TEXT,
	applicabilita_ct TEXT,
	applicabilita_pattern TEXT,
	applicabilita_connettori TEXT,
	req_conversione_enabled INT NOT NULL DEFAULT 0,
	req_conversione_tipo VARCHAR(255),
	req_conversione_template LONGBLOB,
	req_content_type VARCHAR(255),
	rest_transformation INT NOT NULL DEFAULT 0,
	rest_method VARCHAR(255),
	rest_path VARCHAR(4000),
	soap_transformation INT NOT NULL DEFAULT 0,
	soap_version VARCHAR(255),
	soap_action VARCHAR(255),
	soap_envelope INT NOT NULL DEFAULT 0,
	soap_envelope_as_attach INT NOT NULL DEFAULT 0,
	soap_envelope_tipo VARCHAR(255),
	soap_envelope_template LONGBLOB,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_transform_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pd_transform_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_transform PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_transform_1 ON pd_transform (id_porta,nome);
CREATE UNIQUE INDEX index_pd_transform_2 ON pd_transform (id_porta,posizione);



CREATE TABLE pd_transform_sa
(
	id_trasformazione BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_transform_sa_1 UNIQUE (id_trasformazione,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_pd_transform_sa_2 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_transform_sa_1 ON pd_transform_sa (id_trasformazione,id_servizio_applicativo);



CREATE TABLE pd_transform_hdr
(
	id_trasformazione BIGINT NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore TEXT,
	identificazione_fallita VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_hdr_1 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_hdr PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_pd_trasf_hdr_1 ON pd_transform_hdr (id_trasformazione);



CREATE TABLE pd_transform_url
(
	id_trasformazione BIGINT NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore TEXT,
	identificazione_fallita VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_url_1 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_url PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_pd_trasf_url_1 ON pd_transform_url (id_trasformazione);



CREATE TABLE pd_transform_risp
(
	id_trasformazione BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	applicabilita_status_min INT,
	applicabilita_status_max INT,
	applicabilita_ct TEXT,
	applicabilita_pattern TEXT,
	conversione_enabled INT NOT NULL DEFAULT 0,
	conversione_tipo VARCHAR(255),
	conversione_template LONGBLOB,
	content_type VARCHAR(255),
	return_code VARCHAR(255),
	soap_envelope INT NOT NULL DEFAULT 0,
	soap_envelope_as_attach INT NOT NULL DEFAULT 0,
	soap_envelope_tipo VARCHAR(255),
	soap_envelope_template LONGBLOB,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pd_trasf_resp_1 UNIQUE (id_trasformazione,nome),
	CONSTRAINT uniq_pd_trasf_resp_2 UNIQUE (id_trasformazione,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_risp_1 FOREIGN KEY (id_trasformazione) REFERENCES pd_transform(id),
	CONSTRAINT pk_pd_transform_risp PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX idx_pd_trasf_resp_1 ON pd_transform_risp (id_trasformazione,nome);
CREATE UNIQUE INDEX idx_pd_trasf_resp_2 ON pd_transform_risp (id_trasformazione,posizione);



CREATE TABLE pd_transform_risp_hdr
(
	id_transform_risp BIGINT NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore TEXT,
	identificazione_fallita VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_transform_risp_hdr_1 FOREIGN KEY (id_transform_risp) REFERENCES pd_transform_risp(id),
	CONSTRAINT pk_pd_transform_risp_hdr PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_pd_trasf_hdr_resp_1 ON pd_transform_risp_hdr (id_transform_risp);



CREATE TABLE pd_handlers
(
	id_porta BIGINT NOT NULL,
	tipologia VARCHAR(255) NOT NULL,
	tipo VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	stato VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_handlers_1 UNIQUE (id_porta,tipologia,tipo),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_handlers_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_handlers PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_handlers_1 ON pd_handlers (id_porta,tipologia,tipo);



CREATE TABLE pd_aa
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	attributi TEXT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_pd_aa_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_aa_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_aa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_pd_aa_1 ON pd_aa (id_porta,nome);


