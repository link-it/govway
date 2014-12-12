-- **** Servizi Applicativi ****

CREATE TABLE servizi_applicativi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- * Risposta Asincrona *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentorisp INT DEFAULT 0,
	getmsgrisp VARCHAR(255),
	-- FOREIGN KEY (id_gestione_errore_risp) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per la risposta asincrona
	id_gestione_errore_risp BIGINT,
	tipoauthrisp VARCHAR(255),
	utenterisp VARCHAR(255),
	passwordrisp VARCHAR(255),
	subjectrisp VARCHAR(255),
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
	subjectinv VARCHAR(255),
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
	utente VARCHAR(255),
	password VARCHAR(255),
	subject VARCHAR(255),
	invio_x_rif VARCHAR(255),
	sbustamento_protocol_info INT DEFAULT 1,
	tipologia_fruizione VARCHAR(255),
	tipologia_erogazione VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT unique_servizi_applicativi_1 UNIQUE (nome,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_applicativi_1 FOREIGN KEY (id_connettore_inv) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_applicativi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_servizi_applicativi PRIMARY KEY (id)
);

-- index
CREATE INDEX index_servizi_applicativi_1 ON servizi_applicativi (nome,id_soggetto);


