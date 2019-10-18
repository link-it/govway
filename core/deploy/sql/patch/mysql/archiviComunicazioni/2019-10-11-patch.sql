ALTER TABLE msgdiagnostici ADD COLUMN applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI MODIFY COLUMN SERVIZIO_APPLICATIVO VARCHAR(2000);

ALTER TABLE transazioni MODIFY COLUMN servizio_applicativo_erogatore VARCHAR(2000);

ALTER TABLE dump_messaggi ADD COLUMN servizio_applicativo_erogatore VARCHAR(2000);


CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Esito della Transazione
	consegna_successo BOOLEAN DEFAULT false,
	dettaglio_esito INT,
	-- Consegna via Integration Manager
	consegna_im BOOLEAN DEFAULT false,
	identificativo_messaggio VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_accettazione_richiesta TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_uscita_richiesta TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_accettazione_risposta TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ingresso_risposta TIMESTAMP(3) DEFAULT 0,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes BIGINT,
	-- Dimensione messaggi gestiti
	risposta_ingresso_bytes BIGINT,
	location_connettore TEXT,
	codice_risposta VARCHAR(10),
	-- Eventuali FAULT
	fault MEDIUMTEXT,
	formato_fault VARCHAR(20),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_primo_tentativo TIMESTAMP(3) DEFAULT 0,
	numero_tentativi INT DEFAULT 0,
	-- Cluster ID
	cluster_id VARCHAR(100),
	-- Informazioni relative all'ultimo tentativo di consegna fallito
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultimo_errore TIMESTAMP(3) DEFAULT 0,
	dettaglio_esito_ultimo_errore INT,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore MEDIUMTEXT,
	location_ultimo_errore TEXT,
	cluster_id_ultimo_errore VARCHAR(100),
	-- Eventuali FAULT
	fault_ultimo_errore MEDIUMTEXT,
	formato_fault_ultimo_errore VARCHAR(20),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);


