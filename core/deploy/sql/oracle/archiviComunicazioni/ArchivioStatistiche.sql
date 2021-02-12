CREATE TABLE statistiche
(
	tipo VARCHAR2(255) NOT NULL,
	data_ultima_generazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	-- check constraints
	CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili')),
	-- unique constraints
	CONSTRAINT unique_statistiche_1 UNIQUE (tipo)
);


-- STATISTICHE ORARIE

CREATE SEQUENCE seq_statistiche_orarie MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE statistiche_orarie
(
	data TIMESTAMP NOT NULL,
	stato_record NUMBER NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR2(255) NOT NULL,
	tipo_porta VARCHAR2(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR2(20) NOT NULL,
	mittente VARCHAR2(255) NOT NULL,
	tipo_destinatario VARCHAR2(20) NOT NULL,
	destinatario VARCHAR2(255) NOT NULL,
	tipo_servizio VARCHAR2(20) NOT NULL,
	servizio VARCHAR2(255) NOT NULL,
	versione_servizio NUMBER NOT NULL,
	azione VARCHAR2(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR2(255) NOT NULL,
	trasporto_mittente VARCHAR2(20) NOT NULL,
	token_issuer VARCHAR2(20) NOT NULL,
	token_client_id VARCHAR2(20) NOT NULL,
	token_subject VARCHAR2(20) NOT NULL,
	token_username VARCHAR2(20) NOT NULL,
	token_mail VARCHAR2(20) NOT NULL,
	-- Esito della Transazione
	esito NUMBER NOT NULL,
	esito_contesto VARCHAR2(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR2(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR2(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR2(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR2(100) NOT NULL,
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_statistiche_orarie_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_orarie PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_HOUR_ENTRY ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_HOUR_FULL ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_HOUR ON statistiche_orarie (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);
CREATE TRIGGER trg_statistiche_orarie
BEFORE
insert on statistiche_orarie
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_statistiche_orarie.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_stat_orarie_contenuti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE stat_orarie_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
	risorsa_valore VARCHAR2(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR2(255),
	filtro_valore_1 VARCHAR2(4000),
	filtro_nome_2 VARCHAR2(255),
	filtro_valore_2 VARCHAR2(4000),
	filtro_nome_3 VARCHAR2(255),
	filtro_valore_3 VARCHAR2(4000),
	filtro_nome_4 VARCHAR2(255),
	filtro_valore_4 VARCHAR2(4000),
	filtro_nome_5 VARCHAR2(255),
	filtro_valore_5 VARCHAR2(4000),
	filtro_nome_6 VARCHAR2(255),
	filtro_valore_6 VARCHAR2(4000),
	filtro_nome_7 VARCHAR2(255),
	filtro_valore_7 VARCHAR2(4000),
	filtro_nome_8 VARCHAR2(255),
	filtro_valore_8 VARCHAR2(4000),
	filtro_nome_9 VARCHAR2(255),
	filtro_valore_9 VARCHAR2(4000),
	filtro_nome_10 VARCHAR2(255),
	filtro_valore_10 VARCHAR2(4000),
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_stat NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_orarie_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_orarie(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_orarie_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_orarie_1 ON stat_orarie_contenuti (id_stat);
CREATE TRIGGER trg_stat_orarie_contenuti
BEFORE
insert on stat_orarie_contenuti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_stat_orarie_contenuti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- STATISTICHE GIORNALIERE

CREATE SEQUENCE seq_statistiche_giornaliere MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE statistiche_giornaliere
(
	data TIMESTAMP NOT NULL,
	stato_record NUMBER NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR2(255) NOT NULL,
	tipo_porta VARCHAR2(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR2(20) NOT NULL,
	mittente VARCHAR2(255) NOT NULL,
	tipo_destinatario VARCHAR2(20) NOT NULL,
	destinatario VARCHAR2(255) NOT NULL,
	tipo_servizio VARCHAR2(20) NOT NULL,
	servizio VARCHAR2(255) NOT NULL,
	versione_servizio NUMBER NOT NULL,
	azione VARCHAR2(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR2(255) NOT NULL,
	trasporto_mittente VARCHAR2(20) NOT NULL,
	token_issuer VARCHAR2(20) NOT NULL,
	token_client_id VARCHAR2(20) NOT NULL,
	token_subject VARCHAR2(20) NOT NULL,
	token_username VARCHAR2(20) NOT NULL,
	token_mail VARCHAR2(20) NOT NULL,
	-- Esito della Transazione
	esito NUMBER NOT NULL,
	esito_contesto VARCHAR2(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR2(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR2(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR2(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR2(100) NOT NULL,
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_statistiche_giornaliere_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_giornaliere PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_DAY_ENTRY ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_DAY_FULL ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_DAY ON statistiche_giornaliere (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);
CREATE TRIGGER trg_statistiche_giornaliere
BEFORE
insert on statistiche_giornaliere
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_statistiche_giornaliere.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_stat_giorni_contenuti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE stat_giorni_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
	risorsa_valore VARCHAR2(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR2(255),
	filtro_valore_1 VARCHAR2(4000),
	filtro_nome_2 VARCHAR2(255),
	filtro_valore_2 VARCHAR2(4000),
	filtro_nome_3 VARCHAR2(255),
	filtro_valore_3 VARCHAR2(4000),
	filtro_nome_4 VARCHAR2(255),
	filtro_valore_4 VARCHAR2(4000),
	filtro_nome_5 VARCHAR2(255),
	filtro_valore_5 VARCHAR2(4000),
	filtro_nome_6 VARCHAR2(255),
	filtro_valore_6 VARCHAR2(4000),
	filtro_nome_7 VARCHAR2(255),
	filtro_valore_7 VARCHAR2(4000),
	filtro_nome_8 VARCHAR2(255),
	filtro_valore_8 VARCHAR2(4000),
	filtro_nome_9 VARCHAR2(255),
	filtro_valore_9 VARCHAR2(4000),
	filtro_nome_10 VARCHAR2(255),
	filtro_valore_10 VARCHAR2(4000),
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_stat NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_giorni_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_giornaliere(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_giorni_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_giornaliere_1 ON stat_giorni_contenuti (id_stat);
CREATE TRIGGER trg_stat_giorni_contenuti
BEFORE
insert on stat_giorni_contenuti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_stat_giorni_contenuti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- STATISTICHE SETTIMANALI

CREATE SEQUENCE seq_statistiche_settimanali MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE statistiche_settimanali
(
	data TIMESTAMP NOT NULL,
	stato_record NUMBER NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR2(255) NOT NULL,
	tipo_porta VARCHAR2(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR2(20) NOT NULL,
	mittente VARCHAR2(255) NOT NULL,
	tipo_destinatario VARCHAR2(20) NOT NULL,
	destinatario VARCHAR2(255) NOT NULL,
	tipo_servizio VARCHAR2(20) NOT NULL,
	servizio VARCHAR2(255) NOT NULL,
	versione_servizio NUMBER NOT NULL,
	azione VARCHAR2(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR2(255) NOT NULL,
	trasporto_mittente VARCHAR2(20) NOT NULL,
	token_issuer VARCHAR2(20) NOT NULL,
	token_client_id VARCHAR2(20) NOT NULL,
	token_subject VARCHAR2(20) NOT NULL,
	token_username VARCHAR2(20) NOT NULL,
	token_mail VARCHAR2(20) NOT NULL,
	-- Esito della Transazione
	esito NUMBER NOT NULL,
	esito_contesto VARCHAR2(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR2(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR2(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR2(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR2(100) NOT NULL,
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_statistiche_settimanali_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_settimanali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_WEEK_ENTRY ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_WEEK_FULL ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_WEEK ON statistiche_settimanali (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);
CREATE TRIGGER trg_statistiche_settimanali
BEFORE
insert on statistiche_settimanali
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_statistiche_settimanali.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_stat_settimane_contenuti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE stat_settimane_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
	risorsa_valore VARCHAR2(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR2(255),
	filtro_valore_1 VARCHAR2(4000),
	filtro_nome_2 VARCHAR2(255),
	filtro_valore_2 VARCHAR2(4000),
	filtro_nome_3 VARCHAR2(255),
	filtro_valore_3 VARCHAR2(4000),
	filtro_nome_4 VARCHAR2(255),
	filtro_valore_4 VARCHAR2(4000),
	filtro_nome_5 VARCHAR2(255),
	filtro_valore_5 VARCHAR2(4000),
	filtro_nome_6 VARCHAR2(255),
	filtro_valore_6 VARCHAR2(4000),
	filtro_nome_7 VARCHAR2(255),
	filtro_valore_7 VARCHAR2(4000),
	filtro_nome_8 VARCHAR2(255),
	filtro_valore_8 VARCHAR2(4000),
	filtro_nome_9 VARCHAR2(255),
	filtro_valore_9 VARCHAR2(4000),
	filtro_nome_10 VARCHAR2(255),
	filtro_valore_10 VARCHAR2(4000),
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_stat NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_settimane_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_settimanali(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_settimane_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_settimanali_1 ON stat_settimane_contenuti (id_stat);
CREATE TRIGGER trg_stat_settimane_contenuti
BEFORE
insert on stat_settimane_contenuti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_stat_settimane_contenuti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- STATISTICHE MENSILI

CREATE SEQUENCE seq_statistiche_mensili MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE statistiche_mensili
(
	data TIMESTAMP NOT NULL,
	stato_record NUMBER NOT NULL,
	-- Informazioni porta di dominio
	id_porta VARCHAR2(255) NOT NULL,
	tipo_porta VARCHAR2(20) NOT NULL,
	-- Informazioni protocollo
	tipo_mittente VARCHAR2(20) NOT NULL,
	mittente VARCHAR2(255) NOT NULL,
	tipo_destinatario VARCHAR2(20) NOT NULL,
	destinatario VARCHAR2(255) NOT NULL,
	tipo_servizio VARCHAR2(20) NOT NULL,
	servizio VARCHAR2(255) NOT NULL,
	versione_servizio NUMBER NOT NULL,
	azione VARCHAR2(255) NOT NULL,
	-- Informazioni di integrazione
	servizio_applicativo VARCHAR2(255) NOT NULL,
	trasporto_mittente VARCHAR2(20) NOT NULL,
	token_issuer VARCHAR2(20) NOT NULL,
	token_client_id VARCHAR2(20) NOT NULL,
	token_subject VARCHAR2(20) NOT NULL,
	token_username VARCHAR2(20) NOT NULL,
	token_mail VARCHAR2(20) NOT NULL,
	-- Esito della Transazione
	esito NUMBER NOT NULL,
	esito_contesto VARCHAR2(20) NOT NULL,
	-- Indirizzo IP client
	client_address VARCHAR2(20) NOT NULL,
	-- Gruppi a cui appartiene l'api invocata
	gruppi VARCHAR2(20) NOT NULL,
	-- API implementata
	uri_api VARCHAR2(20) NOT NULL,
	-- Cluster ID
	cluster_id VARCHAR2(100) NOT NULL,
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_statistiche_mensili_1 CHECK (tipo_porta IN ('delegata','applicativa','router','integration_manager')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_mensili PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_STAT_MONTH_ENTRY ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio);
-- CREATE INDEX INDEX_STAT_MONTH_FULL ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id);
-- CREATE INDEX INDEX_STAT_MONTH ON statistiche_mensili (data DESC,stato_record,esito,esito_contesto,id_porta,tipo_porta,tipo_destinatario,destinatario,tipo_servizio,servizio,versione_servizio,azione,tipo_mittente,mittente,servizio_applicativo,trasporto_mittente,token_issuer,token_client_id,token_subject,token_username,token_mail,client_address,gruppi,uri_api,cluster_id,richieste,bytes_banda_complessiva,bytes_banda_interna,bytes_banda_esterna,latenza_totale,latenza_porta,latenza_servizio);
CREATE TRIGGER trg_statistiche_mensili
BEFORE
insert on statistiche_mensili
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_statistiche_mensili.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_stat_mensili_contenuti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE stat_mensili_contenuti
(
	data TIMESTAMP NOT NULL,
	-- Risorsa Aggregata
	risorsa_nome CLOB NOT NULL,
	risorsa_valore VARCHAR2(4000) NOT NULL,
	-- Filtri
	filtro_nome_1 VARCHAR2(255),
	filtro_valore_1 VARCHAR2(4000),
	filtro_nome_2 VARCHAR2(255),
	filtro_valore_2 VARCHAR2(4000),
	filtro_nome_3 VARCHAR2(255),
	filtro_valore_3 VARCHAR2(4000),
	filtro_nome_4 VARCHAR2(255),
	filtro_valore_4 VARCHAR2(4000),
	filtro_nome_5 VARCHAR2(255),
	filtro_valore_5 VARCHAR2(4000),
	filtro_nome_6 VARCHAR2(255),
	filtro_valore_6 VARCHAR2(4000),
	filtro_nome_7 VARCHAR2(255),
	filtro_valore_7 VARCHAR2(4000),
	filtro_nome_8 VARCHAR2(255),
	filtro_valore_8 VARCHAR2(4000),
	filtro_nome_9 VARCHAR2(255),
	filtro_valore_9 VARCHAR2(4000),
	filtro_nome_10 VARCHAR2(255),
	filtro_valore_10 VARCHAR2(4000),
	-- Informazioni statistiche
	richieste NUMBER NOT NULL,
	bytes_banda_complessiva NUMBER,
	bytes_banda_interna NUMBER,
	bytes_banda_esterna NUMBER,
	latenza_totale NUMBER,
	latenza_porta NUMBER,
	latenza_servizio NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_stat NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_mensili_contenuti_1 FOREIGN KEY (id_stat) REFERENCES statistiche_mensili(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_mensili_contenuti PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_stat_c_mensili_1 ON stat_mensili_contenuti (id_stat);
CREATE TRIGGER trg_stat_mensili_contenuti
BEFORE
insert on stat_mensili_contenuti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_stat_mensili_contenuti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


