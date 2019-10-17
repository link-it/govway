ALTER TABLE msgdiagnostici ADD applicativo VARCHAR2(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI MODIFY SERVIZIO_APPLICATIVO VARCHAR2(2000);

ALTER TABLE transazioni MODIFY servizio_applicativo_erogatore VARCHAR2(2000);

ALTER TABLE dump_messaggi ADD servizio_applicativo_erogatore VARCHAR2(2000);


CREATE SEQUENCE seq_transazioni_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR2(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR2(2000) NOT NULL,
	data_uscita_richiesta TIMESTAMP,
	data_accettazione_risposta TIMESTAMP,
	data_ingresso_risposta TIMESTAMP,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes NUMBER,
	-- Dimensione messaggi gestiti
	risposta_ingresso_bytes NUMBER,
	codice_risposta VARCHAR2(10),
	data_primo_tentativo TIMESTAMP,
	data_ultimo_errore TIMESTAMP,
	codice_risposta_ultimo_errore VARCHAR2(10),
	ultimo_errore CLOB,
	numero_tentativi NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);

ALTER TABLE transazioni_sa MODIFY numero_tentativi DEFAULT 0;

CREATE TRIGGER trg_transazioni_sa
BEFORE
insert on transazioni_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_transazioni_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


