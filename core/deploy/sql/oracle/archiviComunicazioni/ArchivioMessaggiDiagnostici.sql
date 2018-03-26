CREATE SEQUENCE seq_msgdiagnostici MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE msgdiagnostici
(
	gdo TIMESTAMP NOT NULL,
	pdd_codice VARCHAR2(255) NOT NULL,
	pdd_tipo_soggetto VARCHAR2(255) NOT NULL,
	pdd_nome_soggetto VARCHAR2(255) NOT NULL,
	idfunzione VARCHAR2(255) NOT NULL,
	severita NUMBER NOT NULL,
	messaggio CLOB NOT NULL,
	-- Eventuale id della richiesta in gestione (informazione non definita dalla specifica)
	idmessaggio VARCHAR2(255),
	-- Eventuale id della risposta correlata alla richiesta (informazione non definita dalla specifica)
	idmessaggio_risposta VARCHAR2(255),
	-- Codice del diagnostico emesso
	codice VARCHAR2(255) NOT NULL,
	-- Protocollo (puo' non essere presente per i diagnostici di 'servizio' della porta)
	protocollo VARCHAR2(255),
	id_transazione VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_msgdiagnostici_1 CHECK (severita IN (0,1,2,3,4,5,6,7)),
	-- fk/pk keys constraints
	CONSTRAINT pk_msgdiagnostici PRIMARY KEY (id)
);

-- index
CREATE INDEX MSG_DIAG_TRANS ON msgdiagnostici (id_transazione);
CREATE INDEX MSG_DIAG_ID ON msgdiagnostici (idmessaggio);
CREATE INDEX MSG_DIAG_GDO ON msgdiagnostici (gdo DESC);
CREATE TRIGGER trg_msgdiagnostici
BEFORE
insert on msgdiagnostici
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_msgdiagnostici.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


