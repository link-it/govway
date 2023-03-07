-- IDENTIFICATIVO MITTENTE

CREATE TABLE credenziale_mittente
(
	tipo VARCHAR(20) NOT NULL,
	credenziale VARCHAR(2900) NOT NULL,
	ora_registrazione DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_credenziale_mittente_1 UNIQUE (tipo,credenziale),
	-- fk/pk keys constraints
	CONSTRAINT pk_credenziale_mittente PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_credenziale_mittente_1 ON credenziale_mittente (tipo,credenziale);
CREATE INDEX CREDENZIALE_ORAREG ON credenziale_mittente (ora_registrazione);



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
	esito_sincrono INT,
	consegne_multiple INT,
	esito_contesto VARCHAR(20),
	-- Protocollo utilizzato per la transazione
	protocollo VARCHAR(20) NOT NULL,
	-- Informazioni Http
	tipo_richiesta VARCHAR(10),
	codice_risposta_ingresso VARCHAR(10),
	codice_risposta_uscita VARCHAR(10),
	-- Tempi di latenza
	data_accettazione_richiesta DATETIME2,
	data_ingresso_richiesta DATETIME2,
	data_ingresso_richiesta_stream DATETIME2,
	data_uscita_richiesta DATETIME2,
	data_uscita_richiesta_stream DATETIME2,
	data_accettazione_risposta DATETIME2,
	data_ingresso_risposta DATETIME2,
	data_ingresso_risposta_stream DATETIME2,
	data_uscita_risposta DATETIME2,
	data_uscita_risposta_stream DATETIME2,
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
	fault_integrazione VARCHAR(max),
	formato_fault_integrazione VARCHAR(20),
	fault_cooperazione VARCHAR(max),
	formato_fault_cooperazione VARCHAR(20),
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
	data_id_msg_richiesta DATETIME2,
	data_id_msg_risposta DATETIME2,
	-- Altre informazioni di protocollo
	profilo_collaborazione_op2 VARCHAR(255),
	profilo_collaborazione_prot VARCHAR(255),
	id_collaborazione VARCHAR(255),
	uri_accordo_servizio VARCHAR(1000),
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
	header_protocollo_richiesta VARCHAR(max),
	digest_richiesta VARCHAR(max),
	prot_ext_info_richiesta VARCHAR(max),
	header_protocollo_risposta VARCHAR(max),
	digest_risposta VARCHAR(max),
	prot_ext_info_risposta VARCHAR(max),
	-- Tracciatura e Diagnostica emessa dalla PdD
	traccia_richiesta VARCHAR(max),
	traccia_risposta VARCHAR(max),
	diagnostici VARCHAR(max),
	diagnostici_list_1 VARCHAR(255),
	diagnostici_list_2 VARCHAR(max),
	diagnostici_list_ext VARCHAR(max),
	diagnostici_ext VARCHAR(max),
	error_log VARCHAR(max),
	warning_log VARCHAR(max),
	-- informazioni di integrazione
	id_correlazione_applicativa VARCHAR(255),
	id_correlazione_risposta VARCHAR(255),
	servizio_applicativo_fruitore VARCHAR(255),
	servizio_applicativo_erogatore VARCHAR(2000),
	operazione_im VARCHAR(255),
	location_richiesta VARCHAR(255),
	location_risposta VARCHAR(255),
	nome_porta VARCHAR(2000),
	credenziali VARCHAR(max),
	location_connettore VARCHAR(max),
	url_invocazione VARCHAR(max),
	trasporto_mittente VARCHAR(20),
	token_issuer VARCHAR(20),
	token_client_id VARCHAR(20),
	token_subject VARCHAR(20),
	token_username VARCHAR(20),
	token_mail VARCHAR(20),
	token_info VARCHAR(max),
	tempi_elaborazione VARCHAR(4000),
	-- filtro duplicati (0=originale,-1=duplicata,N=quanti duplicati esistono)
	duplicati_richiesta INT DEFAULT 0,
	duplicati_risposta INT DEFAULT 0,
	-- Cluster ID
	cluster_id VARCHAR(100),
	-- Indirizzo IP client letto dal socket
	socket_client_address VARCHAR(255),
	-- Indirizzo IP client letto dall'header di trasporto
	transport_client_address VARCHAR(255),
	-- Indirizzo IP client
	client_address VARCHAR(20),
	-- Eventi emessi durante la gestione della transazione
	eventi_gestione VARCHAR(20),
	-- Tipologia di API
	tipo_api INT,
	-- API implementata
	uri_api VARCHAR(20),
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR(20),
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_transazioni_1 CHECK (pdd_ruolo IN ('delegata','applicativa','router','integrationManager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_TR_ENTRY ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio);
-- CREATE INDEX INDEX_TR_MEDIUM ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,stato,client_address,gruppi,uri_api);
-- CREATE INDEX INDEX_TR_FULL ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,versione_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,id_correlazione_applicativa,id_correlazione_risposta,protocollo,client_address,gruppi,uri_api,eventi_gestione,cluster_id);
-- CREATE INDEX INDEX_TR_SEARCH ON transazioni (data_ingresso_richiesta DESC,esito,esito_contesto,pdd_ruolo,pdd_codice,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,versione_servizio,azione,tipo_soggetto_fruitore,nome_soggetto_fruitore,servizio_applicativo_fruitore,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,id_correlazione_applicativa,id_correlazione_risposta,protocollo,client_address,gruppi,uri_api,eventi_gestione,cluster_id,id,data_uscita_richiesta,data_ingresso_risposta,data_uscita_risposta);
-- CREATE INDEX INDEX_TR_STATS ON transazioni (data_ingresso_richiesta,pdd_ruolo,pdd_codice,tipo_soggetto_fruitore,nome_soggetto_fruitore,tipo_soggetto_erogatore,nome_soggetto_erogatore,tipo_servizio,nome_servizio,versione_servizio,azione,servizio_applicativo_fruitore,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,esito,esito_contesto,client_address,gruppi,uri_api,cluster_id,data_uscita_richiesta,data_ingresso_risposta,data_uscita_risposta,richiesta_ingresso_bytes,richiesta_uscita_bytes,risposta_ingresso_bytes,risposta_uscita_bytes);
CREATE INDEX INDEX_TR_FILTROD_REQ ON transazioni (id_messaggio_richiesta,pdd_ruolo);
CREATE INDEX INDEX_TR_FILTROD_RES ON transazioni (id_messaggio_risposta,pdd_ruolo);
CREATE INDEX INDEX_TR_FILTROD_REQ_2 ON transazioni (data_id_msg_richiesta,id_messaggio_richiesta);
CREATE INDEX INDEX_TR_FILTROD_RES_2 ON transazioni (data_id_msg_risposta,id_messaggio_risposta);
CREATE INDEX INDEX_TR_COLLABORAZIONE ON transazioni (id_collaborazione);
CREATE INDEX INDEX_TR_RIF_RICHIESTA ON transazioni (id_asincrono);
CREATE INDEX INDEX_TR_CORRELAZIONE_REQ ON transazioni (id_correlazione_applicativa);
CREATE INDEX INDEX_TR_CORRELAZIONE_RES ON transazioni (id_correlazione_risposta);

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
	data_uscita_richiesta_stream DATETIME2,
	data_accettazione_risposta DATETIME2,
	data_ingresso_risposta DATETIME2,
	data_ingresso_risposta_stream DATETIME2,
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
	-- unique constraints
	CONSTRAINT unique_transazioni_sa_1 UNIQUE (id_transazione,servizio_applicativo_erogatore),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);
-- CREATE INDEX INDEX_TRSA_IN_QUEUE ON transazioni_sa (data_registrazione DESC,servizio_applicativo_erogatore,connettore_nome,consegna_terminata,dettaglio_esito,consegna_trasparente,consegna_im,numero_tentativi,codice_risposta,data_uscita_richiesta,data_ingresso_risposta,numero_prelievi_im,data_eliminazione_im);
-- CREATE INDEX INDEX_TRSA_SEND ON transazioni_sa (data_uscita_richiesta DESC,servizio_applicativo_erogatore,connettore_nome,data_ingresso_risposta,consegna_terminata,dettaglio_esito,consegna_trasparente,consegna_im,numero_tentativi,codice_risposta,data_registrazione);



CREATE TABLE transazioni_info
(
	tipo VARCHAR(255) NOT NULL,
	data DATETIME2 NOT NULL,
	-- fk/pk columns
	-- unique constraints
	CONSTRAINT unique_transazioni_info_1 UNIQUE (tipo)
);


CREATE TABLE transazioni_export
(
	-- Intervallo utilizzato dall'esportazione
	intervallo_inizio DATETIME2 NOT NULL,
	intervallo_fine DATETIME2 NOT NULL,
	-- Eventuale nome del file/dir dello zip esportato
	nome VARCHAR(255),
	-- Stato procedura Esportazione
	export_state VARCHAR(255) NOT NULL,
	export_error VARCHAR(max),
	export_time_start DATETIME2 NOT NULL,
	export_time_end DATETIME2,
	-- Stato procedura Eliminazione
	delete_state VARCHAR(255) NOT NULL,
	delete_error VARCHAR(max),
	delete_time_start DATETIME2,
	delete_time_end DATETIME2,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_transazioni_export_1 CHECK (export_state IN ('executing','completed','error')),
	CONSTRAINT chk_transazioni_export_2 CHECK (delete_state IN ('undefined','executing','completed','error')),
	-- unique constraints
	CONSTRAINT unique_transazioni_export_1 UNIQUE (intervallo_inizio,intervallo_fine),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_export PRIMARY KEY (id)
);




CREATE TABLE transazioni_esiti
(
	-- Codice numerico dell'esito della transazione
	govway_status INT NOT NULL,
	-- Identificativo dell'esito della transazione
	govway_status_key VARCHAR(255) NOT NULL,
	-- Frase dell'errore che identifica l'esito della transazione
	govway_status_detail VARCHAR(255) NOT NULL,
	-- Descrizione dell'esito della transazione
	govway_status_description VARCHAR(255) NOT NULL,
	-- Codice numerico della classe di esiti a cui appartiene la transazione
	govway_status_class INT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_tr_esiti_1 UNIQUE (govway_status),
	CONSTRAINT uniq_tr_esiti_2 UNIQUE (govway_status_key),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_esiti PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_tr_esiti_1 ON transazioni_esiti (govway_status);
CREATE UNIQUE INDEX idx_tr_esiti_2 ON transazioni_esiti (govway_status_key);



CREATE TABLE transazioni_classe_esiti
(
	-- Codice numerico della classe di appartenenza di un esito della transazione
	govway_status INT NOT NULL,
	-- Descrizione della classe di un esito
	govway_status_detail VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT uniq_tr_classe_esiti_1 UNIQUE (govway_status),
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_classe_esiti PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_tr_classe_esiti_1 ON transazioni_classe_esiti (govway_status);



-- DUMP - DATI

CREATE TABLE dump_messaggi
(
	id_transazione VARCHAR(255) NOT NULL,
	protocollo VARCHAR(20) NOT NULL,
	servizio_applicativo_erogatore VARCHAR(2000),
	data_consegna_erogatore DATETIME2,
	tipo_messaggio VARCHAR(255) NOT NULL,
	formato_messaggio VARCHAR(20),
	content_type VARCHAR(255),
	content_length BIGINT,
	multipart_content_type VARCHAR(255),
	multipart_content_id VARCHAR(255),
	multipart_content_location VARCHAR(255),
	body VARBINARY(MAX),
	dump_timestamp DATETIME2 NOT NULL,
	post_process_header VARCHAR(max),
	post_process_filename VARCHAR(255),
	post_process_content VARBINARY(MAX),
	post_process_config_id VARCHAR(2000),
	post_process_timestamp DATETIME2,
	post_processed INT DEFAULT 1,
	multipart_header_ext VARCHAR(max),
	header_ext VARCHAR(max),
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- check constraints
	CONSTRAINT chk_dump_messaggi_1 CHECK (tipo_messaggio IN ('RichiestaIngresso','RichiestaUscita','RispostaIngresso','RispostaUscita','RichiestaIngressoDumpBinario','RichiestaUscitaDumpBinario','RispostaIngressoDumpBinario','RispostaUscitaDumpBinario','IntegrationManager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_dump_messaggi PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_messaggi_1 ON dump_messaggi (id_transazione);
CREATE INDEX index_dump_messaggi_2 ON dump_messaggi (post_processed,post_process_timestamp);
CREATE INDEX index_dump_messaggi_3 ON dump_messaggi (post_process_config_id);



CREATE TABLE dump_multipart_header
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max),
	dump_timestamp DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_messaggio BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_multipart_header_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_multipart_header PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_multipart_header_1 ON dump_multipart_header (id_messaggio);



CREATE TABLE dump_header_trasporto
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max),
	dump_timestamp DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_messaggio BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_header_trasporto_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_header_trasporto PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_header_trasporto_1 ON dump_header_trasporto (id_messaggio);



CREATE TABLE dump_allegati
(
	content_type VARCHAR(255),
	content_id VARCHAR(255),
	content_location VARCHAR(255),
	allegato VARBINARY(MAX),
	dump_timestamp DATETIME2 NOT NULL,
	header_ext VARCHAR(max),
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_messaggio BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_allegati_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_allegati PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_allegati_1 ON dump_allegati (id_messaggio);



CREATE TABLE dump_header_allegato
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(max),
	dump_timestamp DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_allegato BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_header_allegato_1 FOREIGN KEY (id_allegato) REFERENCES dump_allegati(id),
	CONSTRAINT pk_dump_header_allegato PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_header_allegato_1 ON dump_header_allegato (id_allegato);



CREATE TABLE dump_contenuti
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	valore_as_bytes VARBINARY(MAX),
	dump_timestamp DATETIME2 NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	id_messaggio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_dump_contenuti_1 UNIQUE (id,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_dump_contenuti_1 FOREIGN KEY (id_messaggio) REFERENCES dump_messaggi(id),
	CONSTRAINT pk_dump_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX index_dump_contenuti_1 ON dump_contenuti (id_messaggio);


