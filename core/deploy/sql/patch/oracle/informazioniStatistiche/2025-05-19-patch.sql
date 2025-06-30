
-- STATISTICHE TRACING PDND

CREATE SEQUENCE seq_statistiche_pdnd_tracing MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE statistiche_pdnd_tracing
(
	data_tracciamento TIMESTAMP NOT NULL,
	data_registrazione TIMESTAMP,
	data_pubblicazione TIMESTAMP,
	pdd_codice VARCHAR2(255) NOT NULL,
	csv BLOB,
	method VARCHAR2(20),
	stato_pdnd VARCHAR2(20) NOT NULL,
	tentativi_pubblicazione NUMBER NOT NULL,
	force_publish NUMBER NOT NULL,
	stato VARCHAR2(20),
	tracing_id VARCHAR2(36),
	error_details CLOB,
	history NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_statistiche_pdnd_tracing_1 CHECK (method IN ('REPLACE','RECOVER','SUBMIT')),
	CONSTRAINT chk_statistiche_pdnd_tracing_2 CHECK (stato_pdnd IN ('WAITING','PENDING','OK','ERROR')),
	CONSTRAINT chk_statistiche_pdnd_tracing_3 CHECK (stato IN ('PUBLISHED','FAILED')),
	-- fk/pk keys constraints
	CONSTRAINT pk_statistiche_pdnd_tracing PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PDND_TRACING_ACTIVE ON statistiche_pdnd_tracing (pdd_codice,data_tracciamento DESC,history);

ALTER TABLE statistiche_pdnd_tracing MODIFY stato_pdnd DEFAULT 'WAITING';
ALTER TABLE statistiche_pdnd_tracing MODIFY tentativi_pubblicazione DEFAULT 0;
ALTER TABLE statistiche_pdnd_tracing MODIFY force_publish DEFAULT 0;

CREATE TRIGGER trg_statistiche_pdnd_tracing
BEFORE
insert on statistiche_pdnd_tracing
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_statistiche_pdnd_tracing.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/

-- constraint
ALTER TABLE statistiche DROP CONSTRAINT chk_statistiche_1;
ALTER TABLE statistiche ADD CONSTRAINT chk_statistiche_1 CHECK (tipo IN ('StatisticheOrarie','StatisticheGiornaliere','StatisticheSettimanali','StatisticheMensili','PdndGenerazioneTracciamento','PdndPubblicazioneTracciamento'));
