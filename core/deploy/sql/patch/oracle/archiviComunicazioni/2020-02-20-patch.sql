ALTER TABLE msgdiagnostici ADD applicativo VARCHAR2(2000);

ALTER TABLE MSG_SERVIZI_APPLICATIVI MODIFY SERVIZIO_APPLICATIVO VARCHAR2(2000);

ALTER TABLE transazioni MODIFY servizio_applicativo_erogatore VARCHAR2(2000);
ALTER TABLE transazioni ADD esito_sincrono NUMBER;
ALTER TABLE transazioni ADD consegne_multiple NUMBER;

ALTER TABLE dump_messaggi ADD servizio_applicativo_erogatore VARCHAR2(2000);
ALTER TABLE dump_messaggi ADD data_consegna_erogatore TIMESTAMP;



CREATE SEQUENCE seq_transazioni_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 CYCLE;

CREATE TABLE transazioni_sa
(
	id_transazione VARCHAR2(255) NOT NULL,
	servizio_applicativo_erogatore VARCHAR2(2000) NOT NULL,
	connettore_nome VARCHAR2(255),
	data_registrazione TIMESTAMP NOT NULL,
	-- Esito della Consegna
	consegna_terminata NUMBER,
	data_messaggio_scaduto TIMESTAMP,
	dettaglio_esito NUMBER,
	-- Consegna ad un Backend Applicativo
	consegna_trasparente NUMBER,
	-- Consegna via Integration Manager
	consegna_im NUMBER,
	-- Identificativo del messaggio
	identificativo_messaggio VARCHAR2(255),
	-- Date
	data_accettazione_richiesta TIMESTAMP,
	data_uscita_richiesta TIMESTAMP,
	data_accettazione_risposta TIMESTAMP,
	data_ingresso_risposta TIMESTAMP,
	-- Dimensione messaggi gestiti
	richiesta_uscita_bytes NUMBER,
	risposta_ingresso_bytes NUMBER,
	location_connettore CLOB,
	codice_risposta VARCHAR2(10),
	-- Eventuale FAULT
	fault CLOB,
	formato_fault VARCHAR2(20),
	-- Tentativi di Consegna
	data_primo_tentativo TIMESTAMP,
	numero_tentativi NUMBER,
	-- Cluster ID
	cluster_id_in_coda VARCHAR2(100),
	cluster_id_consegna VARCHAR2(100),
	-- Informazioni relative all'ultimo tentativo di consegna fallito
	data_ultimo_errore TIMESTAMP,
	dettaglio_esito_ultimo_errore NUMBER,
	codice_risposta_ultimo_errore VARCHAR2(10),
	ultimo_errore CLOB,
	location_ultimo_errore CLOB,
	cluster_id_ultimo_errore VARCHAR2(100),
	fault_ultimo_errore CLOB,
	formato_fault_ultimo_errore VARCHAR2(20),
	-- Date relative alla gestione via IntegrationManager
	data_primo_prelievo_im TIMESTAMP,
	data_prelievo_im TIMESTAMP,
	numero_prelievi_im NUMBER,
	data_eliminazione_im TIMESTAMP,
	cluster_id_prelievo_im VARCHAR2(100),
	cluster_id_eliminazione_im VARCHAR2(100),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_transazioni_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX index_transazioni_sa_1 ON transazioni_sa (id_transazione);

ALTER TABLE transazioni_sa MODIFY consegna_terminata DEFAULT 0;
ALTER TABLE transazioni_sa MODIFY consegna_trasparente DEFAULT 0;
ALTER TABLE transazioni_sa MODIFY consegna_im DEFAULT 0;
ALTER TABLE transazioni_sa MODIFY numero_tentativi DEFAULT 0;
ALTER TABLE transazioni_sa MODIFY numero_prelievi_im DEFAULT 0;

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


