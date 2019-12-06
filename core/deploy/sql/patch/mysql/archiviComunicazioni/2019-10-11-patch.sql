ALTER TABLE msgdiagnostici ADD COLUMN applicativo VARCHAR(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI MODIFY COLUMN SERVIZIO_APPLICATIVO VARCHAR(2000);

ALTER TABLE transazioni MODIFY COLUMN servizio_applicativo_erogatore VARCHAR(2000);
ALTER TABLE transazioni ADD COLUMN esito_sincrono INT;
ALTER TABLE transazioni ADD COLUMN consegne_multiple INT;

ALTER TABLE dump_messaggi ADD COLUMN servizio_applicativo_erogatore VARCHAR(2000);
ALTER TABLE dump_messaggi ADD COLUMN data_consegna_erogatore TIMESTAMP(3) DEFAULT 0;


CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000) NOT NULL,
	connettore_nome VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Esito della Consegna
	consegna_terminata BOOLEAN DEFAULT false,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_messaggio_scaduto TIMESTAMP(3) DEFAULT 0,
	dettaglio_esito INT,
	-- Consegna ad un Backend Applicativo
	consegna_trasparente BOOLEAN DEFAULT false,
	-- Consegna via Integration Manager
	consegna_im BOOLEAN DEFAULT false,
	-- Identificativo del messaggio
	identificativo_messaggio VARCHAR(255),
	-- Date
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
	risposta_ingresso_bytes BIGINT,
	location_connettore TEXT,
	codice_risposta VARCHAR(10),
	-- Eventuale FAULT
	fault MEDIUMTEXT,
	formato_fault VARCHAR(20),
	-- Tentativi di Consegna
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_primo_tentativo TIMESTAMP(3) DEFAULT 0,
	numero_tentativi INT DEFAULT 0,
	-- Cluster ID
	cluster_id_in_coda VARCHAR(100),
	cluster_id_consegna VARCHAR(100),
	-- Informazioni relative all'ultimo tentativo di consegna fallito
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultimo_errore TIMESTAMP(3) DEFAULT 0,
	dettaglio_esito_ultimo_errore INT,
	codice_risposta_ultimo_errore VARCHAR(10),
	ultimo_errore MEDIUMTEXT,
	location_ultimo_errore TEXT,
	cluster_id_ultimo_errore VARCHAR(100),
	fault_ultimo_errore MEDIUMTEXT,
	formato_fault_ultimo_errore VARCHAR(20),
	-- Date relative alla gestione via IntegrationManager
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_primo_prelievo_im TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_prelievo_im TIMESTAMP(3) DEFAULT 0,
	numero_prelievi_im INT DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_eliminazione_im TIMESTAMP(3) DEFAULT 0,
	cluster_id_prelievo_im VARCHAR(100),
	cluster_id_eliminazione_im VARCHAR(100),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);


