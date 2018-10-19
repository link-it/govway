CREATE TABLE msgdiagnostici
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	gdo TIMESTAMP(3) NOT NULL DEFAULT 0,
	pdd_codice VARCHAR(255) NOT NULL,
	pdd_tipo_soggetto VARCHAR(255) NOT NULL,
	pdd_nome_soggetto VARCHAR(255) NOT NULL,
	idfunzione VARCHAR(255) NOT NULL,
	severita INT NOT NULL,
	messaggio TEXT NOT NULL,
	-- Eventuale id della richiesta in gestione (informazione non definita dalla specifica)
	idmessaggio VARCHAR(255),
	-- Eventuale id della risposta correlata alla richiesta (informazione non definita dalla specifica)
	idmessaggio_risposta VARCHAR(255),
	-- Codice del diagnostico emesso
	codice VARCHAR(255) NOT NULL,
	-- Protocollo (puo' non essere presente per i diagnostici di 'servizio' della porta)
	protocollo VARCHAR(255),
	id_transazione VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_msgdiagnostici_1 CHECK (severita IN (0,1,2,3,4,5,6,7)),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiagnostici PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX MSG_DIAG_TRANS ON msgdiagnostici (id_transazione);
CREATE INDEX MSG_DIAG_ID ON msgdiagnostici (idmessaggio);
CREATE INDEX MSG_DIAG_GDO ON msgdiagnostici (gdo DESC);


