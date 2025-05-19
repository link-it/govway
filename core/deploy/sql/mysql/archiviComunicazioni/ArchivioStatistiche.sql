CREATE TABLE statistiche
(
	tipo VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultima_generazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili','PdndGenerazioneTracciamento','PdndPubblicazioneTracciamento')),
	-- unique constraints
	CONSTRAINT unique_statistiche_1 UNIQUE (tipo),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;




-- STATISTICHE ORARIE

CREATE TABLE statistiche_orarie
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	stato_record INT NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR(255) NOT NULL,
	tipo_porta VARCHAR(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR(20) NOT NULL,
	mittente VARCHAR(255) NOT NULL,
	tipo_destinatario VARCHAR(20) NOT NULL,
	destinatario VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(20) NOT NULL,
	servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL,
	azione VARCHAR(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR(255) NOT NULL,
	trasporto_mittente VARCHAR(20) NOT NULL,
	token_issuer VARCHAR(20) NOT NULL,
	token_client_id VARCHAR(20) NOT NULL,
	token_subject VARCHAR(20) NOT NULL,
	token_username VARCHAR(20) NOT NULL,
	token_mail VARCHAR(20) NOT NULL,
	-- Esito della Transazione
	esito INT NOT NULL,
	esito_contesto VARCHAR(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR(100) NOT NULL,
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_orarie_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_orarie PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_STAT_HOUR_ENTRY ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_HOUR_FULL ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_HOUR ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_orarie_contenuti
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Risorsa Aggregata
	risorsa_nome TEXT NOT NULL,
	risorsa_valore VARCHAR(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR(255),
	filtro_valore_1 VARCHAR(4000),
	filtro_nome_2 VARCHAR(255),
	filtro_valore_2 VARCHAR(4000),
	filtro_nome_3 VARCHAR(255),
	filtro_valore_3 VARCHAR(4000),
	filtro_nome_4 VARCHAR(255),
	filtro_valore_4 VARCHAR(4000),
	filtro_nome_5 VARCHAR(255),
	filtro_valore_5 VARCHAR(4000),
	filtro_nome_6 VARCHAR(255),
	filtro_valore_6 VARCHAR(4000),
	filtro_nome_7 VARCHAR(255),
	filtro_valore_7 VARCHAR(4000),
	filtro_nome_8 VARCHAR(255),
	filtro_valore_8 VARCHAR(4000),
	filtro_nome_9 VARCHAR(255),
	filtro_valore_9 VARCHAR(4000),
	filtro_nome_10 VARCHAR(255),
	filtro_valore_10 VARCHAR(4000),
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_orarie_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_orarie(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_orarie_contenuti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_stat_c_orarie_1 ON stat_orarie_contenuti (id_stat);



-- STATISTICHE GIORNALIERE

CREATE TABLE statistiche_giornaliere
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	stato_record INT NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR(255) NOT NULL,
	tipo_porta VARCHAR(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR(20) NOT NULL,
	mittente VARCHAR(255) NOT NULL,
	tipo_destinatario VARCHAR(20) NOT NULL,
	destinatario VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(20) NOT NULL,
	servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL,
	azione VARCHAR(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR(255) NOT NULL,
	trasporto_mittente VARCHAR(20) NOT NULL,
	token_issuer VARCHAR(20) NOT NULL,
	token_client_id VARCHAR(20) NOT NULL,
	token_subject VARCHAR(20) NOT NULL,
	token_username VARCHAR(20) NOT NULL,
	token_mail VARCHAR(20) NOT NULL,
	-- Esito della Transazione
	esito INT NOT NULL,
	esito_contesto VARCHAR(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR(100) NOT NULL,
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_giornaliere_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_giornaliere PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_STAT_DAY_ENTRY ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_DAY_FULL ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_DAY ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_giorni_contenuti
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Risorsa Aggregata
	risorsa_nome TEXT NOT NULL,
	risorsa_valore VARCHAR(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR(255),
	filtro_valore_1 VARCHAR(4000),
	filtro_nome_2 VARCHAR(255),
	filtro_valore_2 VARCHAR(4000),
	filtro_nome_3 VARCHAR(255),
	filtro_valore_3 VARCHAR(4000),
	filtro_nome_4 VARCHAR(255),
	filtro_valore_4 VARCHAR(4000),
	filtro_nome_5 VARCHAR(255),
	filtro_valore_5 VARCHAR(4000),
	filtro_nome_6 VARCHAR(255),
	filtro_valore_6 VARCHAR(4000),
	filtro_nome_7 VARCHAR(255),
	filtro_valore_7 VARCHAR(4000),
	filtro_nome_8 VARCHAR(255),
	filtro_valore_8 VARCHAR(4000),
	filtro_nome_9 VARCHAR(255),
	filtro_valore_9 VARCHAR(4000),
	filtro_nome_10 VARCHAR(255),
	filtro_valore_10 VARCHAR(4000),
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_giorni_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_giornaliere(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_giorni_contenuti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_stat_c_giornaliere_1 ON stat_giorni_contenuti (id_stat);



-- STATISTICHE SETTIMANALI

CREATE TABLE statistiche_settimanali
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	stato_record INT NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR(255) NOT NULL,
	tipo_porta VARCHAR(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR(20) NOT NULL,
	mittente VARCHAR(255) NOT NULL,
	tipo_destinatario VARCHAR(20) NOT NULL,
	destinatario VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(20) NOT NULL,
	servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL,
	azione VARCHAR(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR(255) NOT NULL,
	trasporto_mittente VARCHAR(20) NOT NULL,
	token_issuer VARCHAR(20) NOT NULL,
	token_client_id VARCHAR(20) NOT NULL,
	token_subject VARCHAR(20) NOT NULL,
	token_username VARCHAR(20) NOT NULL,
	token_mail VARCHAR(20) NOT NULL,
	-- Esito della Transazione
	esito INT NOT NULL,
	esito_contesto VARCHAR(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR(100) NOT NULL,
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_settimanali_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_settimanali PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_STAT_WEEK_ENTRY ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_WEEK_FULL ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_WEEK ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_settimane_contenuti
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Risorsa Aggregata
	risorsa_nome TEXT NOT NULL,
	risorsa_valore VARCHAR(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR(255),
	filtro_valore_1 VARCHAR(4000),
	filtro_nome_2 VARCHAR(255),
	filtro_valore_2 VARCHAR(4000),
	filtro_nome_3 VARCHAR(255),
	filtro_valore_3 VARCHAR(4000),
	filtro_nome_4 VARCHAR(255),
	filtro_valore_4 VARCHAR(4000),
	filtro_nome_5 VARCHAR(255),
	filtro_valore_5 VARCHAR(4000),
	filtro_nome_6 VARCHAR(255),
	filtro_valore_6 VARCHAR(4000),
	filtro_nome_7 VARCHAR(255),
	filtro_valore_7 VARCHAR(4000),
	filtro_nome_8 VARCHAR(255),
	filtro_valore_8 VARCHAR(4000),
	filtro_nome_9 VARCHAR(255),
	filtro_valore_9 VARCHAR(4000),
	filtro_nome_10 VARCHAR(255),
	filtro_valore_10 VARCHAR(4000),
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_settimane_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_settimanali(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_settimane_contenuti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_stat_c_settimanali_1 ON stat_settimane_contenuti (id_stat);



-- STATISTICHE MENSILI

CREATE TABLE statistiche_mensili
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	stato_record INT NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR(255) NOT NULL,
	tipo_porta VARCHAR(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR(20) NOT NULL,
	mittente VARCHAR(255) NOT NULL,
	tipo_destinatario VARCHAR(20) NOT NULL,
	destinatario VARCHAR(255) NOT NULL,
	tipo_servizio VARCHAR(20) NOT NULL,
	servizio VARCHAR(255) NOT NULL,
	versione_servizio INT NOT NULL,
	azione VARCHAR(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR(255) NOT NULL,
	trasporto_mittente VARCHAR(20) NOT NULL,
	token_issuer VARCHAR(20) NOT NULL,
	token_client_id VARCHAR(20) NOT NULL,
	token_subject VARCHAR(20) NOT NULL,
	token_username VARCHAR(20) NOT NULL,
	token_mail VARCHAR(20) NOT NULL,
	-- Esito della Transazione
	esito INT NOT NULL,
	esito_contesto VARCHAR(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR(100) NOT NULL,
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_mensili_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_mensili PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_STAT_MONTH_ENTRY ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_MONTH_FULL ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_MONTH ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_mensili_contenuti
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Risorsa Aggregata
	risorsa_nome TEXT NOT NULL,
	risorsa_valore VARCHAR(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR(255),
	filtro_valore_1 VARCHAR(4000),
	filtro_nome_2 VARCHAR(255),
	filtro_valore_2 VARCHAR(4000),
	filtro_nome_3 VARCHAR(255),
	filtro_valore_3 VARCHAR(4000),
	filtro_nome_4 VARCHAR(255),
	filtro_valore_4 VARCHAR(4000),
	filtro_nome_5 VARCHAR(255),
	filtro_valore_5 VARCHAR(4000),
	filtro_nome_6 VARCHAR(255),
	filtro_valore_6 VARCHAR(4000),
	filtro_nome_7 VARCHAR(255),
	filtro_valore_7 VARCHAR(4000),
	filtro_nome_8 VARCHAR(255),
	filtro_valore_8 VARCHAR(4000),
	filtro_nome_9 VARCHAR(255),
	filtro_valore_9 VARCHAR(4000),
	filtro_nome_10 VARCHAR(255),
	filtro_valore_10 VARCHAR(4000),
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_mensili_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_mensili(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_mensili_contenuti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX idx_stat_c_mensili_1 ON stat_mensili_contenuti (id_stat);



-- STATISTICHE TRACING PDND

CREATE TABLE statistiche_pdnd_tracing
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_tracciamento TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_registrazione TIMESTAMP(3) DEFAULT 0,
	pdd_codice VARCHAR(255) NOT NULL,
	csv MEDIUMBLOB,
	method VARCHAR(20),
	stato_pdnd VARCHAR(20) NOT NULL DEFAULT 'WAITING',
	tentativi_pubblicazione INT NOT NULL DEFAULT 0,
	stato VARCHAR(20),
	tracing_id VARCHAR(36),
	error_details VARCHAR(255),
	history INT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_statistiche_pdnd_tracing_1 CHECK (method IN ('REPLACE','RECOVER','SUBMIT')),
	CONSTRAINT chk_statistiche_pdnd_tracing_2 CHECK (stato_pdnd IN ('WAITING','PENDING','OK','ERROR')),
	CONSTRAINT chk_statistiche_pdnd_tracing_3 CHECK (stato IN ('PUBLISHED','FAILED')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_pdnd_tracing PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs ROW_FORMAT DYNAMIC;

-- index
CREATE INDEX INDEX_PDND_TRACING_ACTIVE ON statistiche_pdnd_tracing (pdd_codice,data_tracciamento DESC,history);


