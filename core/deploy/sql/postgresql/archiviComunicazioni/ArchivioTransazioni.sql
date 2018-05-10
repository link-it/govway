-- TRANSAZIONI

CREATE TABLE transazioni
(
	-- Identificativo di transazione
	id VARCHAR(36) NOT NULL,
	-- Stato della Transazione
	stato VARCHAR(100),
	-- Ruolo della transazione
	-- sconosciuto (-1)
	-- invocazioneOneway (1)
	-- invocazioneSincrona (2)
	-- invocazioneAsincronaSimmetrica (3)
	-- rispostaAsincronaSimmetrica (4)
	-- invocazioneAsincronaAsimmetrica (5)
	-- richiestaStatoAsincronaAsimmetrica (6)
	-- integrationManager (7)
	ruolo_transazione INT NOT NULL,
	-- Esito della Transazione
	esito INT,
	esito_contesto VARCHAR(20),
	-- Protocollo utilizzato per la transazione
	protocollo VARCHAR(20) NOT NULL,
	-- Tempi di latenza
	data_accettazione_richiesta TIMESTAMP,
	data_ingresso_richiesta TIMESTAMP,
	data_uscita_richiesta TIMESTAMP,
	data_accettazione_risposta TIMESTAMP,
	data_ingresso_risposta TIMESTAMP,
	data_uscita_risposta TIMESTAMP,
	-- Dimensione messaggi gestiti
	richiesta_ingresso_bytes BIGINT,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes BIGINT,
	-- Dimensione messaggi gestiti
	risposta_ingresso_bytes BIGINT,
	-- Dimensione messaggi gestiti
	risposta_uscita_bytes BIGINT,
	-- Dati Porta di Dominio
	pdd_codice VARCHAR(110),
	pdd_tipo_soggetto VARCHAR(20),
	pdd_nome_soggetto VARCHAR(80),
	pdd_ruolo VARCHAR(20),
	-- Eventuali FAULT
	fault_integrazione TEXT,
	fault_cooperazione TEXT,
	-- Soggetto Fruitore
	tipo_soggetto_fruitore VARCHAR(20),
	nome_soggetto_fruitore VARCHAR(80),
	idporta_soggetto_fruitore VARCHAR(110),
	indirizzo_soggetto_fruitore VARCHAR(255),
	-- Soggetto Erogatore
	tipo_soggetto_erogatore VARCHAR(20),
	nome_soggetto_erogatore VARCHAR(80),
	idporta_soggetto_erogatore VARCHAR(110),
	indirizzo_soggetto_erogatore VARCHAR(110),
	-- Identificativi Messaggi
	id_messaggio_richiesta VARCHAR(255),
	id_messaggio_risposta VARCHAR(255),
	data_id_msg_richiesta TIMESTAMP,
	data_id_msg_risposta TIMESTAMP,
	-- Altre informazioni di protocollo
	profilo_collaborazione_op2 VARCHAR(255),
	profilo_collaborazione_prot VARCHAR(255),
	id_collaborazione VARCHAR(255),
	uri_accordo_servizio VARCHAR(255),
	tipo_servizio VARCHAR(20),
	nome_servizio VARCHAR(255),
	versione_servizio INT,
	azione VARCHAR(255),
	-- Identificativo asincrono se utilizzato come riferimento messaggio nella richiesta (2 fase asincrona)
	-- e altre informazioni utilizzate nei profili asincroni
	id_asincrono VARCHAR(255),
	tipo_servizio_correlato VARCHAR(20),
	nome_servizio_correlato VARCHAR(255),
	-- Header Protocollo richiesta/risposta
	header_protocollo_richiesta TEXT,
	digest_richiesta TEXT,
	prot_ext_info_richiesta TEXT,
	header_protocollo_risposta TEXT,
	digest_risposta TEXT,
	prot_ext_info_risposta TEXT,
	-- Tracciatura e Diagnostica emessa dalla PdD
	traccia_richiesta TEXT,
	traccia_risposta TEXT,
	diagnostici TEXT,
	diagnostici_list_1 VARCHAR(255),
	diagnostici_list_2 TEXT,
	diagnostici_list_ext TEXT,
	diagnostici_ext TEXT,
	-- informazioni di integrazione
	id_correlazione_applicativa VARCHAR(255),
	id_correlazione_risposta VARCHAR(255),
	servizio_applicativo_fruitore VARCHAR(255),
	servizio_applicativo_erogatore VARCHAR(255),
	operazione_im VARCHAR(255),
	location_richiesta VARCHAR(255),
	location_risposta VARCHAR(255),
	nome_porta VARCHAR(4000),
	credenziali VARCHAR(255),
	location_connettore TEXT,
	url_invocazione TEXT,
	-- filtro duplicati (0=originale,-1=duplicata,N=quanti duplicati esistono)
	duplicati_richiesta INT DEFAULT 0,
	duplicati_risposta INT DEFAULT 0,
	-- Cluster ID
	cluster_id VARCHAR(100),
	-- Indirizzo IP client letto dal socket
	socket_client_address VARCHAR(255),
	-- Indirizzo IP client letto dall'header di trasporto
	transport_client_address VARCHAR(255),
	-- Eventi emessi durante la gestione della transazione
	eventi_gestione VARCHAR(1000),
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_transazioni_1 CHECK (pdd_ruolo IN ('delegata','applicativa','router','integrationManager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_TR_ENTRY ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio);
-- CREATE INDEX INDEX_TR_MEDIUM ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,servizio_applicativo_erogatore,stato);
-- CREATE INDEX INDEX_TR_FULL ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,servizio_applicativo_erogatore,id_correlazione_applicativa,id_correlazione_risposta,stato,protocollo,eventi_gestione,cluster_id);
-- CREATE INDEX INDEX_TR_SEARCH ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,servizio_applicativo_erogatore,id_correlazione_applicativa,id_correlazione_risposta,stato,protocollo,eventi_gestione,cluster_id,id,data_uscita_richiesta,data_ingresso_risposta,data_uscita_risposta);
-- CREATE INDEX INDEX_TR_STATS ON transazioni (data_ingresso_richiesta,pdd_ruolo,pdd_codice,tipo_soggetto_fruitore,nome_soggetto_fruitore,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,azione,servizio_applicativo_fruitore,servizio_applicativo_erogatore,esito,esito_contesto,stato,data_uscita_richiesta,data_ingresso_risposta,data_uscita_risposta,richiesta_ingresso_bytes,richiesta_uscita_bytes,risposta_ingresso_bytes,risposta_uscita_bytes);
CREATE INDEX INDEX_TR_FILTROD_REQ ON transazioni (id_messaggio_richiesta,pdd_ruolo);
CREATE INDEX INDEX_TR_FILTROD_RES ON transazioni (id_messaggio_risposta,pdd_ruolo);
CREATE INDEX INDEX_TR_FILTROD_REQ_2 ON transazioni (data_id_msg_richiesta,id_messaggio_richiesta);
CREATE INDEX INDEX_TR_FILTROD_RES_2 ON transazioni (data_id_msg_risposta,id_messaggio_risposta);

-- DUMP - DATI

CREATE SEQUENCE seq_dump_messaggi start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE dump_messaggi
(
	id_transazione VARCHAR(255) NOT NULL,
	tipo_messaggio VARCHAR(255) NOT NULL,
	content_type VARCHAR(255),
	multipart_content_type VARCHAR(255),
	multipart_content_id VARCHAR(255),
	multipart_content_location VARCHAR(255),
	body BYTEA,
	dump_timestamp TIMESTAMP NOT NULL,
	post_process_header TEXT,
	post_process_filename VARCHAR(255),
	post_process_content BYTEA,
	post_process_config_id VARCHAR(4000),
	post_process_timestamp TIMESTAMP,
	post_processed INT DEFAULT 1,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_dump_messaggi') NOT NULL,
	-- check constraints
	CONSTRAINT chk_dump_messaggi_1 CHECK (tipo_messaggio IN ('RichiestaIngresso','RichiestaUscita','RispostaIngresso','RispostaUscita','RichiestaIngressoDumpBinario','RichiestaUscitaDumpBinario','RispostaIngressoDumpBinario','RispostaUscitaDumpBinario','IntegrationManager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_messaggi PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_messaggi_1 ON dump_messaggi (id_transazione);
CREATE INDEX index_dump_messaggi_2 ON dump_messaggi (post_processed,post_process_timestamp);
CREATE INDEX index_dump_messaggi_3 ON dump_messaggi (post_process_config_id);



CREATE SEQUENCE seq_dump_multipart_header start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE dump_multipart_header
(
	nome VARCHAR(255) NOT NULL,
	valore TEXT,
	dump_timestamp TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_dump_multipart_header') NOT NULL,
	id_messaggio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_dump_multipart_header_1 UNIQUE (id,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_multipart_header_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_multipart_header PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_multipart_header_1 ON dump_multipart_header (id_messaggio);



CREATE SEQUENCE seq_dump_header_trasporto start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE dump_header_trasporto
(
	nome VARCHAR(255) NOT NULL,
	valore TEXT,
	dump_timestamp TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_dump_header_trasporto') NOT NULL,
	id_messaggio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_dump_header_trasporto_1 UNIQUE (id,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_header_trasporto_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_header_trasporto PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_header_trasporto_1 ON dump_header_trasporto (id_messaggio);



CREATE SEQUENCE seq_dump_allegati start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE dump_allegati
(
	content_type VARCHAR(255),
	content_id VARCHAR(255),
	content_location VARCHAR(255),
	allegato BYTEA,
	dump_timestamp TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_dump_allegati') NOT NULL,
	id_messaggio BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_allegati_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_allegati PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_allegati_1 ON dump_allegati (id_messaggio);



CREATE SEQUENCE seq_dump_header_allegato start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE dump_header_allegato
(
	nome VARCHAR(255) NOT NULL,
	valore TEXT,
	dump_timestamp TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_dump_header_allegato') NOT NULL,
	id_allegato BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_dump_header_allegato_1 UNIQUE (id,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_header_allegato_1 FOREIGN KEY (id_allegato) REFERENCES dump_allegati(id),
	CONSTRAINT pk_dump_header_allegato PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_header_allegato_1 ON dump_header_allegato (id_allegato);



CREATE SEQUENCE seq_dump_contenuti start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 CYCLE;

CREATE TABLE dump_contenuti
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	valore_as_bytes BYTEA,
	dump_timestamp TIMESTAMP NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_dump_contenuti') NOT NULL,
	id_messaggio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_dump_contenuti_1 UNIQUE (id,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_contenuti_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_contenuti_1 ON dump_contenuti (id_messaggio);


