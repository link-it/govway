ALTER TABLE msgdiagnostici ADD applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI ALTER COLUMN SERVIZIO_APPLICATIVO SET DATA TYPE VARCHAR(2000);

ALTER TABLE transazioni ALTER COLUMN servizio_applicativo_erogatore SET DATA TYPE VARCHAR(2000);
ALTER TABLE transazioni ADD esito_sincrono INT;
ALTER TABLE transazioni ADD consegne_multiple INT;

ALTER TABLE dump_messaggi ADD servizio_applicativo_erogatore VARCHAR(2000);
ALTER TABLE dump_messaggi ADD data_consegna_erogatore TIMESTAMP;


CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
	connettore_nome VARCHAR(255),
	data_registrazione TIMESTAMP NOT NULL,
	-- Esito della Consegna
	consegna_terminata SMALLINT DEFAULT 0,
	data_messaggio_scaduto TIMESTAMP,
	dettaglio_esito INT,
	-- Consegna ad un Backend Applicativo
	consegna_trasparente SMALLINT DEFAULT 0,
	-- Consegna via Integration Manager
	consegna_im SMALLINT DEFAULT 0,
	-- Identificativo del messaggio
	identificativo_messaggio VARCHAR(255),
	-- Date
	data_accettazione_richiesta TIMESTAMP,
	data_uscita_richiesta TIMESTAMP,
	data_accettazione_risposta TIMESTAMP,
	data_ingresso_risposta TIMESTAMP,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes BIGINT,
	risposta_ingresso_bytes BIGINT,
	location_connettore CLOB,
	codice_risposta VARCHAR(10),
	-- Eventuale FAULT
	fault CLOB,
	formato_fault VARCHAR(20),
	-- Tentativi di Consegna
	data_primo_tentativo TIMESTAMP,
	numero_tentativi INT DEFAULT 0,
	-- Cluster ID
	cluster_id_in_coda VARCHAR(100),
	cluster_id_consegna VARCHAR(100),
	-- Informazioni relative all'ultimo tentativo di consegna fallito
	data_ultimo_errore TIMESTAMP,
	dettaglio_esito_ultimo_errore INT,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore CLOB,
	location_ultimo_errore CLOB,
	cluster_id_ultimo_errore VARCHAR(100),
	fault_ultimo_errore CLOB,
	formato_fault_ultimo_errore VARCHAR(20),
	-- Date relative alla gestione via IntegrationManager
	data_primo_prelievo_im TIMESTAMP,
	data_prelievo_im TIMESTAMP,
	numero_prelievi_im INT DEFAULT 0,
	data_eliminazione_im TIMESTAMP,
	cluster_id_prelievo_im VARCHAR(100),
	cluster_id_eliminazione_im VARCHAR(100),
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);



