-- **** Servizi Applicativi ****

CREATE SEQUENCE seq_servizi_applicativi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE servizi_applicativi
(
	nome VARCHAR(2000) NOT NULL,
	tipo VARCHAR(255),
	as_client INT,
	descrizione VARCHAR(4000),
	-- * Risposta Asincrona *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentorisp INT DEFAULT 0,
	getmsgrisp VARCHAR(255),
	-- FOREIGN KEY (id_gestione_errore_risp) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per la risposta asincrona
	id_gestione_errore_risp BIGINT,
	tipoauthrisp VARCHAR(255),
	utenterisp VARCHAR(255),
	passwordrisp VARCHAR(255),
	invio_x_rif_risp VARCHAR(255),
	risposta_x_rif_risp VARCHAR(255),
	id_connettore_risp BIGINT NOT NULL,
	sbustamento_protocol_info_risp INT DEFAULT 1,
	-- * Invocazione Servizio *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentoinv INT DEFAULT 0,
	getmsginv VARCHAR(255),
	-- FOREIGN KEY (id_gestione_errore_inv) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per l'invocazione servizio
	id_gestione_errore_inv BIGINT,
	tipoauthinv VARCHAR(255),
	utenteinv VARCHAR(255),
	passwordinv VARCHAR(255),
	invio_x_rif_inv VARCHAR(255),
	risposta_x_rif_inv VARCHAR(255),
	id_connettore_inv BIGINT NOT NULL,
	sbustamento_protocol_info_inv INT DEFAULT 1,
	-- * SoggettoErogatore *
	id_soggetto BIGINT NOT NULL,
	-- * Invocazione Porta *
	fault VARCHAR(255),
	fault_actor VARCHAR(255),
	generic_fault_code VARCHAR(255),
	prefix_fault_code VARCHAR(255),
	tipoauth VARCHAR(255),
	utente VARCHAR(2800),
	password VARCHAR(255),
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate BYTEA,
	cert_strict_verification INT,
	token_policy VARCHAR(255),
	invio_x_rif VARCHAR(255),
	sbustamento_protocol_info INT DEFAULT 1,
	tipologia_fruizione VARCHAR(255),
	tipologia_erogazione VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	utente_richiedente VARCHAR(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_servizi_applicativi') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_servizi_applicativi_1 UNIQUE (nome,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_applicativi_1 FOREIGN KEY (id_connettore_inv) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_applicativi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_servizi_applicativi PRIMARY KEY (id)
);

-- index
CREATE INDEX index_servizi_applicativi_1 ON servizi_applicativi (id_soggetto);



CREATE SEQUENCE seq_sa_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE sa_ruoli
(
	id_servizio_applicativo BIGINT NOT NULL,
	ruolo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_sa_ruoli') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_sa_ruoli_1 UNIQUE (id_servizio_applicativo,ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_ruoli_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_ruoli PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_RUOLI ON sa_ruoli (id_servizio_applicativo);



CREATE SEQUENCE seq_sa_credenziali start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE sa_credenziali
(
	id_servizio_applicativo BIGINT NOT NULL,
	subject VARCHAR(2800),
	cn_subject VARCHAR(255),
	issuer VARCHAR(2800),
	cn_issuer VARCHAR(255),
	certificate BYTEA,
	cert_strict_verification INT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_sa_credenziali') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_credenziali_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_CRED ON sa_credenziali (id_servizio_applicativo);



CREATE SEQUENCE seq_sa_properties start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE sa_properties
(
	id_servizio_applicativo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_sa_properties') NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_sa_properties_1 UNIQUE (id_servizio_applicativo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_properties_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_PROP ON sa_properties (id_servizio_applicativo);


