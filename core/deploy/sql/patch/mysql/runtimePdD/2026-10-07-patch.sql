-- **** Interazioni Asincrone PDND ****

CREATE TABLE PDND_INTERAZIONI_ASYNC
(
	INTERACTION_ID VARCHAR(255) NOT NULL,
	RUOLO VARCHAR(255) NOT NULL,
	FASE VARCHAR(255) NOT NULL,
	TIPO_FRUITORE VARCHAR(255),
	FRUITORE VARCHAR(255),
	TIPO_EROGATORE VARCHAR(255) NOT NULL,
	EROGATORE VARCHAR(255) NOT NULL,
	TIPO_SERVIZIO VARCHAR(255) NOT NULL,
	SERVIZIO VARCHAR(255) NOT NULL,
	VERSIONE_SERVIZIO INT NOT NULL,
	TIPO_SERVIZIO_CALLBACK VARCHAR(255),
	SERVIZIO_CALLBACK VARCHAR(255),
	VERSIONE_SERVIZIO_CALLBACK INT,
	PURPOSE_ID VARCHAR(255),
	CONSUMER_ID VARCHAR(255),
	CLIENT_ID VARCHAR(255),
	URL_CALLBACK VARCHAR(4000),
	ENTITY_NUMBER INT,
	TEMPO_MAX_CALLBACK BIGINT,
	TEMPO_DISPONIBILITA BIGINT,
	CONFERMA_RICHIESTA INT NOT NULL DEFAULT 0,
	LIMITE_ENTITA INT,
	NUMERO_GET_RESOURCE INT NOT NULL DEFAULT 0,
	ID_TRANSAZIONE_START VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	DATA_START TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	DATA_CALLBACK TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	DATA_GET_RESOURCE TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	DATA_CONFIRMATION TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	DATA_SCADENZA TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	DATA_AGGIORNAMENTO TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT uniq_pdnd_async_1 UNIQUE (INTERACTION_ID,RUOLO),
	-- fk/pk keys constraints
	CONSTRAINT pk_PDND_INTERAZIONI_ASYNC PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX PDND_ASYNC_SCADENZA ON PDND_INTERAZIONI_ASYNC (DATA_SCADENZA);
CREATE INDEX PDND_ASYNC_AGGIORNAMENTO ON PDND_INTERAZIONI_ASYNC (DATA_AGGIORNAMENTO);
CREATE INDEX PDND_ASYNC_SEARCH ON PDND_INTERAZIONI_ASYNC (DATA_START,RUOLO);
