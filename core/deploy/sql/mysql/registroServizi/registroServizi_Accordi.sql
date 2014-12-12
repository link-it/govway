-- **** Documenti ****

CREATE TABLE documenti
(
	ruolo VARCHAR(255) NOT NULL,
	-- tipo (es. xsd,xml...)
	tipo VARCHAR(255) NOT NULL,
	-- nome documento
	nome VARCHAR(255) NOT NULL,
	-- contenuto documento
	contenuto MEDIUMBLOB NOT NULL,
	-- idOggettoProprietarioDocumento
	id_proprietario BIGINT NOT NULL,
	-- tipoProprietario
	tipo_proprietario VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_documenti_1 CHECK (ruolo IN ('allegato','specificaSemiformale','specificaLivelloServizio','specificaSicurezza','specificaCoordinamento')),
	CONSTRAINT chk_documenti_2 CHECK (tipo_proprietario IN ('accordoServizio','accordoCooperazione','servizio')),
	-- unique constraints
	CONSTRAINT unique_documenti_1 UNIQUE (ruolo,tipo,nome,id_proprietario,tipo_proprietario),
	-- fk/pk keys constraints
	CONSTRAINT pk_documenti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_documenti_1 ON documenti (ruolo,tipo,nome,id_proprietario,tipo_proprietario);
CREATE INDEX INDEX_DOC_SEARCH ON documenti (id_proprietario);



-- **** Accordi di Servizio Parte Comune ****

CREATE TABLE accordi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	profilo_collaborazione VARCHAR(255),
	wsdl_definitorio MEDIUMTEXT,
	wsdl_concettuale MEDIUMTEXT,
	wsdl_logico_erogatore MEDIUMTEXT,
	wsdl_logico_fruitore MEDIUMTEXT,
	spec_conv_concettuale MEDIUMTEXT,
	spec_conv_erogatore MEDIUMTEXT,
	spec_conv_fruitore MEDIUMTEXT,
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	superuser VARCHAR(255),
	-- id del soggetto referente
	id_referente BIGINT DEFAULT 0,
	versione VARCHAR(255) DEFAULT '',
	-- 1/0 (vero/falso) indica se questo accordo di servizio e' utilizzabile in mancanza di azioni associate
	utilizzo_senza_azione INT DEFAULT 1,
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_accordi_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_accordi_1 UNIQUE (nome,id_referente,versione),
	-- fk/pk keys constraints
	CONSTRAINT pk_accordi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_accordi_1 ON accordi (nome,id_referente,versione);



CREATE TABLE accordi_azioni
(
	id_accordo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	profilo_collaborazione VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	correlata VARCHAR(255),
	-- ridefinito/default
	profilo_azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_accordi_azioni_1 UNIQUE (id_accordo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_azioni_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_accordi_azioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_accordi_azioni_1 ON accordi_azioni (id_accordo,nome);



CREATE TABLE port_type
(
	id_accordo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	profilo_collaborazione VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
	consegna_in_ordine VARCHAR(255),
	scadenza VARCHAR(255),
	-- ridefinito/default
	profilo_pt VARCHAR(255),
	-- document/RPC
	soap_style VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_port_type_1 UNIQUE (id_accordo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_port_type_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_port_type PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_port_type_1 ON port_type (id_accordo,nome);



CREATE TABLE port_type_azioni
(
	id_port_type BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	profilo_collaborazione VARCHAR(255),
	filtro_duplicati VARCHAR(255),
	conferma_ricezione VARCHAR(255),
	identificativo_collaborazione VARCHAR(255),
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
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_port_type_azioni_1 UNIQUE (id_port_type,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_port_type_azioni_1 FOREIGN KEY (id_port_type) REFERENCES port_type(id),
	CONSTRAINT pk_port_type_azioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_port_type_azioni_1 ON port_type_azioni (id_port_type,nome);



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
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_operation_messages_1 FOREIGN KEY (id_port_type_azione) REFERENCES port_type_azioni(id),
	CONSTRAINT pk_operation_messages PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX INDEX_OP_MESSAGES ON operation_messages (id_port_type_azione,input_message);



-- **** Accordi di Cooperazione ****

CREATE TABLE accordi_cooperazione
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- id del soggetto referente
	id_referente BIGINT DEFAULT 0,
	versione VARCHAR(255) DEFAULT '',
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	superuser VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_accordi_cooperazione_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_accordi_cooperazione_1 UNIQUE (nome,versione),
	-- fk/pk keys constraints
	CONSTRAINT pk_accordi_cooperazione PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_accordi_cooperazione_1 ON accordi_cooperazione (nome,versione);



CREATE TABLE accordi_coop_partecipanti
(
	id_accordo_cooperazione BIGINT NOT NULL,
	id_soggetto BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_acc_coop_part_1 UNIQUE (id_accordo_cooperazione,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_coop_partecipanti_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_accordi_coop_partecipanti_2 FOREIGN KEY (id_accordo_cooperazione) REFERENCES accordi_cooperazione(id),
	CONSTRAINT pk_accordi_coop_partecipanti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX INDEX_AC_COOP_PAR ON accordi_coop_partecipanti (id_accordo_cooperazione);



-- **** Accordi di Servizio Parte Specifica ****

CREATE TABLE servizi
(
	nome_servizio VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(255) NOT NULL,
	id_soggetto BIGINT NOT NULL,
	id_accordo BIGINT NOT NULL,
	servizio_correlato VARCHAR(255),
	id_connettore BIGINT NOT NULL,
	wsdl_implementativo_erogatore MEDIUMTEXT,
	wsdl_implementativo_fruitore MEDIUMTEXT,
	superuser VARCHAR(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato INT DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	port_type VARCHAR(255),
	profilo VARCHAR(255),
	descrizione VARCHAR(255),
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	-- identificativi accordo di servizio parte specifica
	aps_nome VARCHAR(255),
	aps_versione VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_servizi_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_servizi_1 UNIQUE (nome_servizio,tipo_servizio,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_servizi_3 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_servizi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_servizi_1 ON servizi (nome_servizio,tipo_servizio,id_soggetto);
CREATE INDEX INDEX_APS ON servizi (aps_nome,aps_versione,id_soggetto);



CREATE TABLE servizi_fruitori
(
	id_servizio BIGINT NOT NULL,
	id_soggetto BIGINT NOT NULL,
	id_connettore BIGINT NOT NULL,
	wsdl_implementativo_erogatore MEDIUMTEXT,
	wsdl_implementativo_fruitore MEDIUMTEXT,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	profilo VARCHAR(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR(255),
	stato VARCHAR(255) NOT NULL DEFAULT 'finale',
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_servizi_fruitori_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_servizi_fruitori_1 UNIQUE (id_servizio,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_fruitori_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_fruitori_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_servizi_fruitori_3 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_servizi_fruitori PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_servizi_fruitori_1 ON servizi_fruitori (id_servizio,id_soggetto);



CREATE TABLE servizi_azioni
(
	nome_azione VARCHAR(255) NOT NULL,
	id_servizio BIGINT NOT NULL,
	id_connettore BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_servizi_azioni_1 UNIQUE (nome_azione,id_servizio),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_azioni_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_azioni_2 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_servizi_azioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_servizi_azioni_1 ON servizi_azioni (nome_azione,id_servizio);



-- **** Accordi di Servizio Composti ****

CREATE TABLE acc_serv_composti
(
	id_accordo BIGINT NOT NULL,
	id_accordo_cooperazione BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_acc_serv_composti_1 UNIQUE (id_accordo),
	-- fk/pk keys constraints
	CONSTRAINT fk_acc_serv_composti_1 FOREIGN KEY (id_accordo_cooperazione) REFERENCES accordi_cooperazione(id),
	CONSTRAINT fk_acc_serv_composti_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_acc_serv_composti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_acc_serv_composti_1 ON acc_serv_composti (id_accordo);
CREATE INDEX INDEX_AC_SC ON acc_serv_composti (id_accordo_cooperazione);



CREATE TABLE acc_serv_componenti
(
	id_servizio_composto BIGINT NOT NULL,
	id_servizio_componente BIGINT NOT NULL,
	azione VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_acc_serv_componenti_1 FOREIGN KEY (id_servizio_composto) REFERENCES acc_serv_composti(id),
	CONSTRAINT fk_acc_serv_componenti_2 FOREIGN KEY (id_servizio_componente) REFERENCES servizi(id),
	CONSTRAINT pk_acc_serv_componenti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX INDEX_AC_SC_SC ON acc_serv_componenti (id_servizio_composto);


