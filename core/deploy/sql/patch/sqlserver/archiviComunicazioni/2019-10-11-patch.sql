ALTER TABLE msgdiagnostici ADD applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI ALTER COLUMN SERVIZIO_APPLICATIVO VARCHAR(2000);

ALTER TABLE transazioni ALTER COLUMN servizio_applicativo_erogatore VARCHAR(2000);

ALTER TABLE dump_messaggi ADD servizio_applicativo_erogatore VARCHAR(2000);


CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
	data_uscita_richiesta DATETIME2,
	data_accettazione_risposta DATETIME2,
	data_ingresso_risposta DATETIME2,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes BIGINT,
	-- Dimensione messaggi gestiti
	risposta_ingresso_bytes BIGINT,
	codice_risposta VARCHAR(10),
	data_primo_tentativo DATETIME2,
	data_ultimo_errore DATETIME2,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore VARCHAR(max),
	numero_tentativi INT DEFAULT 0,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);


