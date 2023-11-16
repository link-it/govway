-- **** Documenti ****

CREATE TABLE documenti
(
	ruolo VARCHAR(255) NOT NULL,
	-- tipo (es. xsd,xml...)
	tipo VARCHAR(255) NOT NULL,
	-- nome documento
	nome VARCHAR(255) NOT NULL,
	-- contenuto documento
	contenuto VARBINARY(MAX) NOT NULL,
	-- idOggettoProprietarioDocumento
	id_proprietario BIGINT NOT NULL,
	-- tipoProprietario
	tipo_proprietario VARCHAR(255) NOT NULL,
	ora_registrazione DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_documenti_1 CHECK (ruolo IN ('allegato','specificaSemiformale','specificaLivelloServizio','specificaSicurezza','specificaCoordinamento')),
	CONSTRAINT chk_documenti_2 CHECK (tipo_proprietario IN ('accordoServizio','accordoCooperazione','servizio')),
	-- unique constraints
	CONSTRAINT unique_documenti_1 UNIQUE (ruolo,tipo,nome,id_proprietario,tipo_proprietario),
	-- fk/pk keys constraints
	CONSTRAINT pk_documenti PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_documenti_1 ON documenti (ruolo,tipo,nome,id_proprietario,tipo_proprietario);
CREATE INDEX INDEX_DOC_SEARCH ON documenti (id_proprietario);



-- **** Accordi di Servizio Parte Comune ****

CREATE TABLE accordi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(max),
	service_binding VARCHAR(255) NOT NULL,
	message_type VARCHAR(255),
	profilo_collaborazione VARCHAR(255),
	formato_specifica VARCHAR(255),
	wsdl_definitorio VARCHAR(max),
	wsdl_concettuale VARCHAR(max),
	wsdl_logico_erogatore VARCHAR(max),
	wsdl_logico_fruitore VARCHAR(max),
	spec_conv_concettuale VARCHAR(max),
	spec_conv_erogatore VARCHAR(max),
	spec_conv_fruitore VARCHAR(max),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	id_riferimento_richiesta VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	superuser VARCHAR(255),
	-- id del soggetto referente
	id_referente BIGINT DEFAULT 0,
	versione INT DEFAULT 1,
	-- 1/0 (vero/falso) indica se questo accordo di servizio e' utilizzabile in mancanza di azioni associate
	utilizzo_senza_azione INT DEFAULT 1,
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	canale VARCHAR(255),
	utente_richiedente VARCHAR(255),
	data_creazione DATETIME2,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica DATETIME2,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_accordi_1 CHECK (service_binding IN ('soap','rest')),
	CONSTRAINT chk_accordi_2 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_accordi_1 UNIQUE (nome,id_referente,versione),
	-- fk/pk keys constraints
	CONSTRAINT pk_accordi PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_accordi_1 ON accordi (nome,id_referente,versione);
CREATE INDEX index_accordi_2 ON accordi (canale);



CREATE TABLE accordi_azioni
(
	id_accordo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	profilo_collaborazione VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	id_riferimento_richiesta VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	correlata VARCHAR(255),
	-- ridefinito/default
	profilo_azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_accordi_azioni_1 UNIQUE (id_accordo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_azioni_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_accordi_azioni PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_accordi_azioni_1 ON accordi_azioni (id_accordo,nome);



CREATE TABLE port_type
(
	id_accordo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	profilo_collaborazione VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	id_riferimento_richiesta VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	-- ridefinito/default
	profilo_pt VARCHAR(255),
	-- document/RPC
	soap_style VARCHAR(255),
	message_type VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_port_type_1 UNIQUE (id_accordo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_port_type_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_port_type PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_port_type_1 ON port_type (id_accordo,nome);



CREATE TABLE port_type_azioni
(
	id_port_type BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	profilo_collaborazione VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	id_riferimento_richiesta VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	correlata_servizio VARCHAR(255),
	correlata VARCHAR(255),
	-- ridefinito/default
	profilo_pt_azione VARCHAR(255),
	-- document/rpc
	soap_style VARCHAR(255),
	soap_action VARCHAR(255),
	-- literal/encoded
	soap_use_msg_input VARCHAR(255),
	-- namespace utilizzato per RPC
	soap_namespace_msg_input VARCHAR(255),
	-- literal/encoded
	soap_use_msg_output VARCHAR(255),
	-- namespace utilizzato per RPC
	soap_namespace_msg_output VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_port_type_azioni_1 UNIQUE (id_port_type,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_port_type_azioni_1 FOREIGN KEY (id_port_type) REFERENCES port_type(id),
	CONSTRAINT pk_port_type_azioni PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_port_type_azioni_1 ON port_type_azioni (id_port_type,nome);



CREATE TABLE operation_messages
(
	id_port_type_azione BIGINT NOT NULL,
	-- true(1)/false(0), true indica un input-message, false un output-message
	input_message INT DEFAULT 1,
	name VARCHAR(255) NOT NULL,
	element_name VARCHAR(255),
	element_namespace VARCHAR(255),
	type_name VARCHAR(255),
	type_namespace VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_operation_messages_1 FOREIGN KEY (id_port_type_azione) REFERENCES port_type_azioni(id),
	CONSTRAINT pk_operation_messages PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_OP_MESSAGES ON operation_messages (id_port_type_azione,input_message);



CREATE TABLE api_resources
(
	id_accordo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	http_method VARCHAR(255) NOT NULL,
	path VARCHAR(255) NOT NULL,
	message_type VARCHAR(255),
	message_type_request VARCHAR(255),
	message_type_response VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	id_riferimento_richiesta VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	-- ridefinito/default
	profilo_azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_api_resources_1 UNIQUE (id_accordo,nome),
	CONSTRAINT unique_api_resources_2 UNIQUE (id_accordo,http_method,path),
	-- fk/pk keys constraints
	CONSTRAINT fk_api_resources_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_api_resources PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_api_resources_1 ON api_resources (id_accordo,nome);
CREATE UNIQUE INDEX index_api_resources_2 ON api_resources (id_accordo,http_method,path);



CREATE TABLE api_resources_response
(
	id_resource BIGINT NOT NULL,
	descrizione VARCHAR(255),
	status INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_api_resp_1 UNIQUE (id_resource,status),
	-- fk/pk keys constraints
	CONSTRAINT fk_api_resources_response_1 FOREIGN KEY (id_resource) REFERENCES api_resources(id),
	CONSTRAINT pk_api_resources_response PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_api_resp_1 ON api_resources_response (id_resource,status);



CREATE TABLE api_resources_media
(
	id_resource_media BIGINT,
	id_resource_response_media BIGINT,
	media_type VARCHAR(255) NOT NULL,
	message_type VARCHAR(255),
	nome VARCHAR(255),
	descrizione VARCHAR(255),
	tipo VARCHAR(255),
	xml_tipo VARCHAR(255),
	xml_name VARCHAR(255),
	xml_namespace VARCHAR(255),
	json_type VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_api_resources_media_1 FOREIGN KEY (id_resource_response_media) REFERENCES api_resources_response(id),
	CONSTRAINT fk_api_resources_media_2 FOREIGN KEY (id_resource_media) REFERENCES api_resources(id),
	CONSTRAINT pk_api_resources_media PRIMARY KEY (id)
);




CREATE TABLE api_resources_parameter
(
	id_resource_parameter BIGINT,
	id_resource_response_par BIGINT,
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	tipo_parametro VARCHAR(255) NOT NULL,
	required BIT NOT NULL DEFAULT 'false',
	tipo VARCHAR(255) NOT NULL,
	restrizioni VARCHAR(max),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_api_resources_parameter_1 FOREIGN KEY (id_resource_response_par) REFERENCES api_resources_response(id),
	CONSTRAINT fk_api_resources_parameter_2 FOREIGN KEY (id_resource_parameter) REFERENCES api_resources(id),
	CONSTRAINT pk_api_resources_parameter PRIMARY KEY (id)
);




CREATE TABLE accordi_gruppi
(
	id_accordo BIGINT NOT NULL,
	id_gruppo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_acc_gruppi_1 UNIQUE (id_accordo,id_gruppo),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_gruppi_1 FOREIGN KEY (id_gruppo) REFERENCES gruppi(id),
	CONSTRAINT fk_accordi_gruppi_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_accordi_gruppi PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_acc_gruppi_1 ON accordi_gruppi (id_accordo,id_gruppo);



-- **** Accordi di Cooperazione ****

CREATE TABLE accordi_cooperazione
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- id del soggetto referente
	id_referente BIGINT DEFAULT 0,
	versione INT DEFAULT 1,
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	superuser VARCHAR(255),
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_accordi_cooperazione_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_accordi_cooperazione_1 UNIQUE (nome,id_referente,versione),
	-- fk/pk keys constraints
	CONSTRAINT pk_accordi_cooperazione PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_accordi_cooperazione_1 ON accordi_cooperazione (nome,id_referente,versione);



CREATE TABLE accordi_coop_partecipanti
(
	id_accordo_cooperazione BIGINT NOT NULL,
	id_soggetto BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_acc_coop_part_1 UNIQUE (id_accordo_cooperazione,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_coop_partecipanti_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_accordi_coop_partecipanti_2 FOREIGN KEY (id_accordo_cooperazione) REFERENCES accordi_cooperazione(id),
	CONSTRAINT pk_accordi_coop_partecipanti PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_AC_COOP_PAR ON accordi_coop_partecipanti (id_accordo_cooperazione);
CREATE INDEX INDEX_AC_SOG ON accordi_coop_partecipanti (id_soggetto);



-- **** Accordi di Servizio Parte Specifica ****

CREATE TABLE servizi
(
	nome_servizio VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(255) NOT NULL,
	versione_servizio INT DEFAULT 1,
	id_soggetto BIGINT NOT NULL,
	id_accordo BIGINT NOT NULL,
	servizio_correlato VARCHAR(255),
	id_connettore BIGINT NOT NULL,
	wsdl_implementativo_erogatore VARCHAR(max),
	wsdl_implementativo_fruitore VARCHAR(max),
	superuser VARCHAR(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	port_type VARCHAR(255),
	profilo VARCHAR(255),
	descrizione VARCHAR(max),
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	message_type VARCHAR(255),
	utente_richiedente VARCHAR(255),
	data_creazione DATETIME2,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica DATETIME2,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_servizi_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_servizi_1 UNIQUE (id_soggetto,tipo_servizio,nome_servizio,versione_servizio),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_servizi_3 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_servizi PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_servizi_1 ON servizi (id_soggetto,tipo_servizio,nome_servizio,versione_servizio);
CREATE INDEX INDEX_SERV_ACC ON servizi (id_accordo);
CREATE INDEX INDEX_SERV_SOG ON servizi (id_soggetto);



CREATE TABLE servizi_azioni
(
	id_servizio BIGINT NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_azioni_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_azioni_2 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_servizi_azioni PRIMARY KEY (id)
);

-- index
CREATE INDEX index_servizi_azioni_1 ON servizi_azioni (id_servizio);



CREATE TABLE servizi_azione
(
	nome_azione VARCHAR(255) NOT NULL,
	id_servizio_azioni BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_servizi_azione_1 UNIQUE (nome_azione,id_servizio_azioni),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_azione_1 FOREIGN KEY (id_servizio_azioni) REFERENCES servizi_azioni(id),
	CONSTRAINT pk_servizi_azione PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_servizi_azione_1 ON servizi_azione (nome_azione,id_servizio_azioni);



CREATE TABLE servizi_fruitori
(
	id_servizio BIGINT NOT NULL,
	id_soggetto BIGINT NOT NULL,
	id_connettore BIGINT NOT NULL,
	wsdl_implementativo_erogatore VARCHAR(max),
	wsdl_implementativo_fruitore VARCHAR(max),
	ora_registrazione DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	descrizione VARCHAR(max),
	utente_richiedente VARCHAR(255),
	data_creazione DATETIME2,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica DATETIME2,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_servizi_fruitori_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_servizi_fruitori_1 UNIQUE (id_servizio,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_fruitori_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_fruitori_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_servizi_fruitori_3 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_servizi_fruitori PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_servizi_fruitori_1 ON servizi_fruitori (id_servizio,id_soggetto);
CREATE INDEX INDEX_SERV_FRU_SOG ON servizi_fruitori (id_soggetto);



CREATE TABLE servizi_fruitori_azioni
(
	id_fruizione BIGINT NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_fruitori_azioni_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_fruitori_azioni_2 FOREIGN KEY (id_fruizione) REFERENCES servizi_fruitori(id),
	CONSTRAINT pk_servizi_fruitori_azioni PRIMARY KEY (id)
);

-- index
CREATE INDEX index_serv_fru_azioni_1 ON servizi_fruitori_azioni (id_fruizione);



CREATE TABLE servizi_fruitori_azione
(
	nome_azione VARCHAR(255) NOT NULL,
	id_fruizione_azioni BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_serv_fru_azione_1 UNIQUE (nome_azione,id_fruizione_azioni),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_fruitori_azione_1 FOREIGN KEY (id_fruizione_azioni) REFERENCES servizi_fruitori_azioni(id),
	CONSTRAINT pk_servizi_fruitori_azione PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_serv_fru_azione_1 ON servizi_fruitori_azione (nome_azione,id_fruizione_azioni);



-- **** Accordi di Servizio Composti ****

CREATE TABLE acc_serv_composti
(
	id_accordo BIGINT NOT NULL,
	id_accordo_cooperazione BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_acc_serv_composti_1 UNIQUE (id_accordo),
	-- fk/pk keys constraints
	CONSTRAINT fk_acc_serv_composti_1 FOREIGN KEY (id_accordo_cooperazione) REFERENCES accordi_cooperazione(id),
	CONSTRAINT fk_acc_serv_composti_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_acc_serv_composti PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_acc_serv_composti_1 ON acc_serv_composti (id_accordo);
CREATE INDEX INDEX_AC_SC ON acc_serv_composti (id_accordo_cooperazione);



CREATE TABLE acc_serv_componenti
(
	id_servizio_composto BIGINT NOT NULL,
	id_servizio_componente BIGINT NOT NULL,
	azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT fk_acc_serv_componenti_1 FOREIGN KEY (id_servizio_composto) REFERENCES acc_serv_composti(id),
	CONSTRAINT fk_acc_serv_componenti_2 FOREIGN KEY (id_servizio_componente) REFERENCES servizi(id),
	CONSTRAINT pk_acc_serv_componenti PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_AC_SC_SC ON acc_serv_componenti (id_servizio_composto);
CREATE INDEX INDEX_AC_SC_SERV ON acc_serv_componenti (id_servizio_componente);


