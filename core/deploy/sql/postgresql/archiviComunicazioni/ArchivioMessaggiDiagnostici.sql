CREATE SEQUENCE seq_msgdiagnostici start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE msgdiagnostici
(
	gdo TIMESTAMP NOT NULL,
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
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_msgdiagnostici') NOT NULL,
	-- check constraints
	CONSTRAINT chk_msgdiagnostici_1 CHECK (severita IN (0,1,2,3,4,5,6,7)),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiagnostici PRIMARY KEY (id)
);

-- index
CREATE INDEX MSG_DIAG_ID ON msgdiagnostici (idmessaggio);
CREATE INDEX MSG_DIAG_GDO ON msgdiagnostici (gdo);



CREATE SEQUENCE seq_msgdiag_correlazione start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE msgdiag_correlazione
(
	-- Dati di Correlazione con i messaggi diagnostici
	idmessaggio VARCHAR(255) NOT NULL,
	pdd_codice VARCHAR(255) NOT NULL,
	pdd_tipo_soggetto VARCHAR(255) NOT NULL,
	pdd_nome_soggetto VARCHAR(255) NOT NULL,
	-- Data di registrazione
	gdo TIMESTAMP NOT NULL,
	-- nome porta delegata/applicativa
	porta VARCHAR(255),
	-- (1/0 true/false) True se siamo in un contesto di porta delegata, False se in un contesto di porta applicativa
	delegata INT NOT NULL,
	tipo_fruitore VARCHAR(255) NOT NULL,
	fruitore VARCHAR(255) NOT NULL,
	tipo_erogatore VARCHAR(255) NOT NULL,
	erogatore VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(255) NOT NULL,
	servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL,
	azione VARCHAR(255),
	-- Identificatore correlazione applicativa
	id_correlazione_applicativa VARCHAR(255),
	id_correlazione_risposta VARCHAR(255),
	-- Protocollo
	protocollo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_msgdiag_correlazione') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiag_correlazione PRIMARY KEY (id)
);

-- index
CREATE INDEX MSG_CORR_INDEX ON msgdiag_correlazione (idmessaggio,delegata);
CREATE INDEX MSG_CORR_GDO ON msgdiag_correlazione (gdo);
CREATE INDEX MSG_CORR_APP ON msgdiag_correlazione (id_correlazione_applicativa,delegata);



CREATE TABLE msgdiag_correlazione_sa
(
	id_correlazione BIGINT NOT NULL,
	-- Identita ServizioApplicativo
	servizio_applicativo VARCHAR(255) NOT NULL,
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT fk_msgdiag_correlazione_sa_1 FOREIGN KEY (id_correlazione) REFERENCES msgdiag_correlazione(id) ON DELETE CASCADE,
	CONSTRAINT pk_msgdiag_correlazione_sa PRIMARY KEY (id_correlazione,servizio_applicativo)
);

