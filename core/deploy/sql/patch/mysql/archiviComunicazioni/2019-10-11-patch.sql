ALTER TABLE msgdiagnostici ADD COLUMN applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI MODIFY COLUMN SERVIZIO_APPLICATIVO VARCHAR(2000);

ALTER TABLE transazioni MODIFY COLUMN servizio_applicativo_erogatore VARCHAR(2000);

ALTER TABLE dump_messaggi ADD COLUMN servizio_applicativo_erogatore VARCHAR(2000);

CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
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
	codice_risposta VARCHAR(10),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_primo_tentativo TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultimo_errore TIMESTAMP(3) DEFAULT 0,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore MEDIUMTEXT,
	numero_tentativi INT DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);

