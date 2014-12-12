-- **** Porte Delegate ****

CREATE TABLE porte_delegate
(
	nome_porta VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- la location sovrascrive il nome di una porta delegata, nella sua url di invocazione
	location VARCHAR(255),
	-- * Soggetto Erogatore *
	-- tipo/nome per le modalita static
	-- tipo/pattern per la modalita contentBased/urlBased
	-- id utilizzato in caso di registryInput
	id_soggetto_erogatore BIGINT,
	tipo_soggetto_erogatore VARCHAR(255),
	nome_soggetto_erogatore VARCHAR(255),
	mode_soggetto_erogatore VARCHAR(255),
	pattern_soggetto_erogatore VARCHAR(255),
	-- * Servizio *
	-- tipo/nome per le modalita static
	-- tipo/pattern per la modalita contentBased/urlBased
	-- id utilizzato in caso di registryInput
	id_servizio BIGINT,
	tipo_servizio VARCHAR(255),
	nome_servizio VARCHAR(255),
	mode_servizio VARCHAR(255),
	pattern_servizio VARCHAR(255),
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
	-- abilitato/disabilitato
	force_wsdl_based_azione VARCHAR(255),
	-- * Configurazione *
	autenticazione VARCHAR(255),
	autorizzazione VARCHAR(255),
	autorizzazione_contenuto VARCHAR(255),
	-- disable/packaging/unpackaging/verify
	mtom_request_mode VARCHAR(255),
	-- disable/packaging/unpackaging/verify
	mtom_response_mode VARCHAR(255),
	-- abilitato/disabilitato (se abilitato le WSSproperties sono presenti nelle tabelle ...._ws_request/response)
	ws_security VARCHAR(255),
	-- abilitato/disabilitato
	ws_security_mtom_req VARCHAR(255),
	-- abilitato/disabilitato
	ws_security_mtom_res VARCHAR(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_sim VARCHAR(255),
	-- abilitato/disabilitato
	ricevuta_asincrona_asim VARCHAR(255),
	-- abilitato/disabilitato/warningOnly
	validazione_contenuti_stato VARCHAR(255),
	-- wsdl/openspcoop/xsd
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
	-- abilitato/disabilitato
	local_forward VARCHAR(255),
	-- proprietario porta delegata (Soggetto fruitore)
	id_soggetto BIGINT NOT NULL,
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_1 UNIQUE (id_soggetto,nome_porta),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_porte_delegate PRIMARY KEY (id)
);

-- index
CREATE INDEX index_porte_delegate_1 ON porte_delegate (id_soggetto,nome_porta);



CREATE TABLE porte_delegate_sa
(
	id_porta BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_porte_delegate_sa_1 UNIQUE (id_porta,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_porte_delegate_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_porte_delegate_sa_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_porte_delegate_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_SA ON porte_delegate_sa (id_porta);



CREATE TABLE pd_mtom_request
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	pattern VARCHAR(max) NOT NULL,
	content_type VARCHAR(255),
	required INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_mtom_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_mtom_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_MTOMTREQ ON pd_mtom_request (id_porta);



CREATE TABLE pd_mtom_response
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	pattern VARCHAR(max) NOT NULL,
	content_type VARCHAR(255),
	required INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_mtom_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_mtom_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_MTOMTRES ON pd_mtom_response (id_porta);



CREATE TABLE pd_ws_request
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ws_request_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ws_request PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_WSSREQ ON pd_ws_request (id_porta);



CREATE TABLE pd_ws_response
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ws_response_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ws_response PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PD_WSSRES ON pd_ws_response (id_porta);



CREATE TABLE pd_correlazione
(
	id_porta BIGINT NOT NULL,
	nome_elemento VARCHAR(255),
	-- modalita di scelta user input, content-based, url-based, disabilitato
	mode_correlazione VARCHAR(255),
	-- pattern utilizzato solo per content/url based
	pattern VARCHAR(max),
	-- blocca/accetta
	identificazione_fallita VARCHAR(255),
	-- abilitato/disabilitato
	riuso_id VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_correlazione_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_correlazione PRIMARY KEY (id)
);




CREATE TABLE pd_correlazione_risposta
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
	CONSTRAINT fk_pd_correlazione_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_correlazione_risposta PRIMARY KEY (id)
);



