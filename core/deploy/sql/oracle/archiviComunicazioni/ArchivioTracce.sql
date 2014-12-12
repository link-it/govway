CREATE SEQUENCE seq_tracce MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE tracce
(
	gdo TIMESTAMP NOT NULL,
	gdo_int NUMBER NOT NULL,
	pdd_codice VARCHAR(255) NOT NULL,
	pdd_tipo_soggetto VARCHAR(255) NOT NULL,
	pdd_nome_soggetto VARCHAR(255) NOT NULL,
	pdd_ruolo VARCHAR(255) NOT NULL,
	tipo_messaggio VARCHAR(255) NOT NULL,
	esito_elaborazione VARCHAR(255) NOT NULL,
	dettaglio_esito_elaborazione CLOB,
	mittente VARCHAR(255),
	tipo_mittente VARCHAR(255),
	idporta_mittente VARCHAR(255),
	indirizzo_mittente VARCHAR(255),
	destinatario VARCHAR(255),
	tipo_destinatario VARCHAR(255),
	idporta_destinatario VARCHAR(255),
	indirizzo_destinatario VARCHAR(255),
	profilo_collaborazione VARCHAR(255),
	profilo_collaborazione_meta VARCHAR(255),
	servizio_correlato VARCHAR(255),
	tipo_servizio_correlato VARCHAR(255),
	collaborazione VARCHAR(255),
	versione_servizio NUMBER,
	servizio VARCHAR(255),
	tipo_servizio VARCHAR(255),
	azione VARCHAR(255),
	id_messaggio VARCHAR(255),
	ora_registrazione TIMESTAMP,
	tipo_ora_reg VARCHAR(255),
	tipo_ora_reg_meta VARCHAR(255),
	rif_messaggio VARCHAR(255),
	scadenza TIMESTAMP,
	inoltro VARCHAR(255),
	inoltro_meta VARCHAR(255),
	conferma_ricezione NUMBER,
	sequenza NUMBER,
	-- Dati di integrazione
	location VARCHAR(255),
	correlazione_applicativa VARCHAR(255),
	correlazione_risposta VARCHAR(255),
	sa_fruitore VARCHAR(255),
	sa_erogatore VARCHAR(255),
	-- Protocollo
	protocollo VARCHAR(255) NOT NULL,
	-- Testsuite
	is_arrived NUMBER,
	soap_element CLOB,
	digest CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_tracce_1 CHECK (pdd_ruolo IN ('delegata','applicativa','integrationManager','router')),
	CONSTRAINT chk_tracce_2 CHECK (tipo_messaggio IN ('Richiesta','Risposta')),
	CONSTRAINT chk_tracce_3 CHECK (esito_elaborazione IN ('INVIATO','RICEVUTO','ERRORE')),
	-- fk/pk keys constraints
	CONSTRAINT pk_tracce PRIMARY KEY (id)
);

-- index
CREATE INDEX TRACCE_SEARCH_ID ON tracce (id_messaggio,pdd_codice);
CREATE INDEX TRACCE_SEARCH_RIF ON tracce (rif_messaggio,pdd_codice);
CREATE INDEX TRACCE_SEARCH_ID_SOGGETTO ON tracce (id_messaggio,pdd_tipo_soggetto,pdd_nome_soggetto);
CREATE INDEX TRACCE_SEARCH_RIF_SOGGETTO ON tracce (rif_messaggio,pdd_tipo_soggetto,pdd_nome_soggetto);

ALTER TABLE tracce MODIFY is_arrived DEFAULT 0;

CREATE TRIGGER trg_tracce
BEFORE
insert on tracce
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_riscontri MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE tracce_riscontri
(
	idtraccia NUMBER NOT NULL,
	riscontro VARCHAR(255),
	ora_registrazione TIMESTAMP,
	tipo_ora_reg VARCHAR(255),
	tipo_ora_reg_meta VARCHAR(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_riscontri_1 FOREIGN KEY (idtraccia) REFERENCES tracce(id) ON DELETE CASCADE,
	CONSTRAINT pk_tracce_riscontri PRIMARY KEY (id)
);

-- index
CREATE INDEX TRACCE_RIS ON tracce_riscontri (idtraccia);
CREATE TRIGGER trg_tracce_riscontri
BEFORE
insert on tracce_riscontri
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_riscontri.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_trasmissioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE tracce_trasmissioni
(
	idtraccia NUMBER NOT NULL,
	origine VARCHAR(255),
	tipo_origine VARCHAR(255),
	indirizzo_origine VARCHAR(255),
	idporta_origine VARCHAR(255),
	destinazione VARCHAR(255),
	tipo_destinazione VARCHAR(255),
	indirizzo_destinazione VARCHAR(255),
	idporta_destinazione VARCHAR(255),
	ora_registrazione TIMESTAMP,
	tipo_ora_reg VARCHAR(255),
	tipo_ora_reg_meta VARCHAR(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_trasmissioni_1 FOREIGN KEY (idtraccia) REFERENCES tracce(id) ON DELETE CASCADE,
	CONSTRAINT pk_tracce_trasmissioni PRIMARY KEY (id)
);

-- index
CREATE INDEX TRACCE_TR ON tracce_trasmissioni (idtraccia);
CREATE TRIGGER trg_tracce_trasmissioni
BEFORE
insert on tracce_trasmissioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_trasmissioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_eccezioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE tracce_eccezioni
(
	idtraccia NUMBER NOT NULL,
	contesto_codifica VARCHAR(255),
	contesto_codifica_meta VARCHAR(255),
	codice_eccezione VARCHAR(255),
	codice_eccezione_meta NUMBER,
	subcodice_eccezione_meta NUMBER,
	rilevanza VARCHAR(255),
	rilevanza_meta VARCHAR(255),
	posizione CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_eccezioni_1 FOREIGN KEY (idtraccia) REFERENCES tracce(id) ON DELETE CASCADE,
	CONSTRAINT pk_tracce_eccezioni PRIMARY KEY (id)
);

-- index
CREATE INDEX TRACCE_ECC ON tracce_eccezioni (idtraccia);
CREATE TRIGGER trg_tracce_eccezioni
BEFORE
insert on tracce_eccezioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_eccezioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_allegati MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE tracce_allegati
(
	idtraccia NUMBER NOT NULL,
	content_id VARCHAR(255),
	content_location VARCHAR(255),
	content_type VARCHAR(255),
	digest CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_allegati_1 FOREIGN KEY (idtraccia) REFERENCES tracce(id) ON DELETE CASCADE,
	CONSTRAINT pk_tracce_allegati PRIMARY KEY (id)
);

-- index
CREATE INDEX TRACCE_ALLEGATI_INDEX ON tracce_allegati (idtraccia);
CREATE TRIGGER trg_tracce_allegati
BEFORE
insert on tracce_allegati
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_allegati.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_tracce_ext_protocol_info MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE tracce_ext_protocol_info
(
	idtraccia NUMBER NOT NULL,
	name VARCHAR(255) NOT NULL,
	value CLOB,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_tracce_ext_protocol_info_1 FOREIGN KEY (idtraccia) REFERENCES tracce(id) ON DELETE CASCADE,
	CONSTRAINT pk_tracce_ext_protocol_info PRIMARY KEY (id)
);

-- index
CREATE INDEX TRACCE_EXT_INFO ON tracce_ext_protocol_info (idtraccia);
CREATE TRIGGER trg_tracce_ext_protocol_info
BEFORE
insert on tracce_ext_protocol_info
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_tracce_ext_protocol_info.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


