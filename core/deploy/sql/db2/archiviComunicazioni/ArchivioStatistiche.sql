CREATE TABLE statistiche
(
	tipo VARCHAR(255) NOT NULL,
	data_ultima_generazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili')),
	-- unique constraints
	CONSTRAINT unique_statistiche_1 UNIQUE (tipo)
);


-- STATISTICHE ORARIE

CREATE TABLE statistiche_orarie
(
	data TIMESTAMP NOT NULL,
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
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_statistiche_orarie_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_orarie PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_HOUR_ENTRY ON statistiche_orarie (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_HOUR_FULL ON statistiche_orarie (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail);
-- CREATE INDEX INDEX_STAT_HOUR ON statistiche_orarie (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_orarie_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
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
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_orarie_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_orarie(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_orarie_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_orarie_1 ON stat_orarie_contenuti (id_stat);



-- STATISTICHE GIORNALIERE

CREATE TABLE statistiche_giornaliere
(
	data TIMESTAMP NOT NULL,
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
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_statistiche_giornaliere_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_giornaliere PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_DAY_ENTRY ON statistiche_giornaliere (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_DAY_FULL ON statistiche_giornaliere (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail);
-- CREATE INDEX INDEX_STAT_DAY ON statistiche_giornaliere (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_giorni_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
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
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_giorni_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_giornaliere(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_giorni_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_giornaliere_1 ON stat_giorni_contenuti (id_stat);



-- STATISTICHE SETTIMANALI

CREATE TABLE statistiche_settimanali
(
	data TIMESTAMP NOT NULL,
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
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_statistiche_settimanali_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_settimanali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_WEEK_ENTRY ON statistiche_settimanali (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_WEEK_FULL ON statistiche_settimanali (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail);
-- CREATE INDEX INDEX_STAT_WEEK ON statistiche_settimanali (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_settimane_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
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
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_settimane_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_settimanali(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_settimane_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_settimanali_1 ON stat_settimane_contenuti (id_stat);



-- STATISTICHE MENSILI

CREATE TABLE statistiche_mensili
(
	data TIMESTAMP NOT NULL,
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
	-- Informazioni statistiche
	richieste INT NOT NULL,
	bytes_banda_complessiva BIGINT,
	bytes_banda_interna BIGINT,
	bytes_banda_esterna BIGINT,
	latenza_totale BIGINT,
	latenza_porta BIGINT,
	latenza_servizio BIGINT,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	-- check constraints
	CONSTRAINT chk_statistiche_mensili_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_mensili PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_MONTH_ENTRY ON statistiche_mensili (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_MONTH_FULL ON statistiche_mensili (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail);
-- CREATE INDEX INDEX_STAT_MONTH ON statistiche_mensili (data DESC,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);



CREATE TABLE stat_mensili_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
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
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE NO CACHE),
	id_stat BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_mensili_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_mensili(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_mensili_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_mensili_1 ON stat_mensili_contenuti (id_stat);


