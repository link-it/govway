ALTER TABLE msgdiagnostici ADD applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI ALTER COLUMN SERVIZIO_APPLICATIVO SET DATA TYPE VARCHAR(2000);

ALTER TABLE transazioni ALTER COLUMN servizio_applicativo_erogatore SET DATA TYPE VARCHAR(2000);

ALTER TABLE dump_messaggi ADD servizio_applicativo_erogatore VARCHAR(2000);

CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
	data_uscita_richiesta TIMESTAMP,
	data_accettazione_risposta TIMESTAMP,
	data_ingresso_risposta TIMESTAMP,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes BIGINT,
	-- Dimensione messaggi gestiti
	risposta_ingresso_bytes BIGINT,
	codice_risposta VARCHAR(10),
	data_primo_tentativo TIMESTAMP,
	data_ultimo_errore TIMESTAMP,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore CLOB,
	numero_tentativi INT DEFAULT 0,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);
