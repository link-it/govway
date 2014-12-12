-- **** Servizi Applicativi ****

CREATE SEQUENCE seq_servizi_applicativi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_applicativi
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- * Risposta Asincrona *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentorisp NUMBER,
	getmsgrisp VARCHAR(255),
	-- FOREIGN KEY (id_gestione_errore_risp) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per la risposta asincrona
	id_gestione_errore_risp NUMBER,
	tipoauthrisp VARCHAR(255),
	utenterisp VARCHAR(255),
	passwordrisp VARCHAR(255),
	subjectrisp VARCHAR(255),
	invio_x_rif_risp VARCHAR(255),
	risposta_x_rif_risp VARCHAR(255),
	id_connettore_risp NUMBER NOT NULL,
	sbustamento_protocol_info_risp NUMBER,
	-- * Invocazione Servizio *
	-- valori 0/1 indicano rispettivamente FALSE/TRUE
	sbustamentoinv NUMBER,
	getmsginv VARCHAR(255),
	-- FOREIGN KEY (id_gestione_errore_inv) REFERENCES gestione_errore(id) Nota: indica un eventuale tipologia di gestione dell'errore per l'invocazione servizio
	id_gestione_errore_inv NUMBER,
	tipoauthinv VARCHAR(255),
	utenteinv VARCHAR(255),
	passwordinv VARCHAR(255),
	subjectinv VARCHAR(255),
	invio_x_rif_inv VARCHAR(255),
	risposta_x_rif_inv VARCHAR(255),
	id_connettore_inv NUMBER NOT NULL,
	sbustamento_protocol_info_inv NUMBER,
	-- * SoggettoErogatore *
	id_soggetto NUMBER NOT NULL,
	-- * Invocazione Porta *
	fault VARCHAR(255),
	fault_actor VARCHAR(255),
	generic_fault_code VARCHAR(255),
	prefix_fault_code VARCHAR(255),
	tipoauth VARCHAR(255),
	utente VARCHAR(255),
	password VARCHAR(255),
	subject VARCHAR(255),
	invio_x_rif VARCHAR(255),
	sbustamento_protocol_info NUMBER,
	tipologia_fruizione VARCHAR(255),
	tipologia_erogazione VARCHAR(255),
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_servizi_applicativi_1 UNIQUE (nome,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_applicativi_1 FOREIGN KEY (id_connettore_inv) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_applicativi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_servizi_applicativi PRIMARY KEY (id)
);


ALTER TABLE servizi_applicativi MODIFY sbustamentorisp DEFAULT 0;
ALTER TABLE servizi_applicativi MODIFY sbustamento_protocol_info_risp DEFAULT 1;
ALTER TABLE servizi_applicativi MODIFY sbustamentoinv DEFAULT 0;
ALTER TABLE servizi_applicativi MODIFY sbustamento_protocol_info_inv DEFAULT 1;
ALTER TABLE servizi_applicativi MODIFY sbustamento_protocol_info DEFAULT 1;
ALTER TABLE servizi_applicativi MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_servizi_applicativi
BEFORE
insert on servizi_applicativi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_applicativi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


