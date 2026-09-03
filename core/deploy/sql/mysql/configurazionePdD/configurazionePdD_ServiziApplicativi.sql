-- **** Servizi Applicativi ****

CREATE TABLE servizi_applicativi
(
	nome VARCHAR(2000) NOT NULL,
	tipo VARCHAR(256),
	as_client INT,
	descrizione VARCHAR(4000),
	-- * Risposta Asincrona *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentorisp INT DEFAULT 0,
	getmsgrisp VARCHAR(256),
	-- FOREIGN KEY (id_gestione_errore_risp) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per la risposta asincrona
	id_gestione_errore_risp BIGINT,
	tipoauthrisp VARCHAR(256),
	utenterisp VARCHAR(256),
	passwordrisp VARCHAR(256),
	enc_passwordrisp TEXT,
	invio_x_rif_risp VARCHAR(256),
	risposta_x_rif_risp VARCHAR(256),
	id_connettore_risp BIGINT NOT NULL,
	sbustamento_protocol_info_risp INT DEFAULT 1,
	-- * Invocazione Servizio *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentoinv INT DEFAULT 0,
	getmsginv VARCHAR(256),
	-- FOREIGN KEY (id_gestione_errore_inv) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per l'invocazione servizio
	id_gestione_errore_inv BIGINT,
	tipoauthinv VARCHAR(256),
	utenteinv VARCHAR(256),
	passwordinv VARCHAR(256),
	enc_passwordinv TEXT,
	invio_x_rif_inv VARCHAR(256),
	risposta_x_rif_inv VARCHAR(256),
	id_connettore_inv BIGINT NOT NULL,
	sbustamento_protocol_info_inv INT DEFAULT 1,
	-- * SoggettoErogatore *
	id_soggetto BIGINT NOT NULL,
	-- * Invocazione Porta *
	fault VARCHAR(256),
	fault_actor VARCHAR(256),
	generic_fault_code VARCHAR(256),
	prefix_fault_code VARCHAR(256),
	tipoauth VARCHAR(256),
	utente VARCHAR(2800),
	password VARCHAR(256),
	subject VARCHAR(2800),
	cn_subject VARCHAR(256),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(256),
	certificate MEDIUMBLOB,
	cert_strict_verification INT,
	token_policy VARCHAR(256),
	invio_x_rif VARCHAR(256),
	sbustamento_protocol_info INT DEFAULT 1,
	tipologia_fruizione VARCHAR(256),
	tipologia_erogazione VARCHAR(256),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	utente_richiedente VARCHAR(256),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_creazione TIMESTAMP(3) DEFAULT 0,
	utente_ultima_modifica VARCHAR(256),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultima_modifica TIMESTAMP(3) DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_servizi_applicativi_1 UNIQUE (nome,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_applicativi_1 FOREIGN KEY (id_connettore_inv) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_applicativi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_servizi_applicativi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_servizi_applicativi_1 ON servizi_applicativi (nome,id_soggetto);
CREATE INDEX index_servizi_applicativi_2 ON servizi_applicativi (id_soggetto);



CREATE TABLE sa_ruoli
(
	id_servizio_applicativo BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_sa_ruoli_1 UNIQUE (id_servizio_applicativo,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_ruoli_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE UNIQUE INDEX index_sa_ruoli_1 ON sa_ruoli (id_servizio_applicativo,ruolo);
CREATE INDEX INDEX_SA_RUOLI ON sa_ruoli (id_servizio_applicativo);



CREATE TABLE sa_credenziali
(
	id_servizio_applicativo BIGINT NOT NULL,
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate MEDIUMBLOB,
	cert_strict_verification INT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_credenziali_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_credenziali PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_SA_CRED ON sa_credenziali (id_servizio_applicativo);



CREATE TABLE sa_properties
(
	id_servizio_applicativo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	enc_value TEXT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_sa_properties_1 UNIQUE (id_servizio_applicativo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_properties_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_properties PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_SA_PROP ON sa_properties (id_servizio_applicativo);


