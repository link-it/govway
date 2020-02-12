ALTER TABLE msgdiagnostici ADD applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI ALTER COLUMN SERVIZIO_APPLICATIVO VARCHAR(2000);

ALTER TABLE transazioni ALTER COLUMN servizio_applicativo_erogatore VARCHAR(2000);
ALTER TABLE transazioni ADD esito_sincrono INT;
ALTER TABLE transazioni ADD consegne_multiple INT;

ALTER TABLE dump_messaggi ADD servizio_applicativo_erogatore VARCHAR(2000);
ALTER TABLE dump_messaggi ADD COLUMN data_consegna_erogatore DATETIME2;


CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
	connettore_nome VARCHAR(255),
	data_registrazione DATETIME2 NOT NULL,
	-- Esito della Consegna
	consegna_terminata BIT DEFAULT 'false',
	data_messaggio_scaduto DATETIME2,
	dettaglio_esito INT,
	-- Consegna ad un Backend Applicativo
	consegna_trasparente BIT DEFAULT 'false',
	-- Consegna via Integration Manager
	consegna_im BIT DEFAULT 'false',
	-- Identificativo del messaggio
	identificativo_messaggio VARCHAR(255),
	-- Date
	data_accettazione_richiesta DATETIME2,
	data_uscita_richiesta DATETIME2,
	data_accettazione_risposta DATETIME2,
	data_ingresso_risposta DATETIME2,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes BIGINT,
	risposta_ingresso_bytes BIGINT,
	location_connettore VARCHAR(max),
	codice_risposta VARCHAR(10),
	-- Eventuale FAULT
	fault VARCHAR(max),
	formato_fault VARCHAR(20),
	-- Tentativi di Consegna
	data_primo_tentativo DATETIME2,
	numero_tentativi INT DEFAULT 0,
	-- Cluster ID
	cluster_id_in_coda VARCHAR(100),
	cluster_id_consegna VARCHAR(100),
	-- Informazioni relative all'ultimo tentativo di consegna fallito
	data_ultimo_errore DATETIME2,
	dettaglio_esito_ultimo_errore INT,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore VARCHAR(max),
	location_ultimo_errore VARCHAR(max),
	cluster_id_ultimo_errore VARCHAR(100),
	fault_ultimo_errore VARCHAR(max),
	formato_fault_ultimo_errore VARCHAR(20),
	-- Date relative alla gestione via IntegrationManager
	data_primo_prelievo_im DATETIME2,
	data_prelievo_im DATETIME2,
	numero_prelievi_im INT DEFAULT 0,
	data_eliminazione_im DATETIME2,
	cluster_id_prelievo_im VARCHAR(100),
	cluster_id_eliminazione_im VARCHAR(100),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);



