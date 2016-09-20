-- **** Documenti ****

CREATE SEQUENCE seq_documenti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE documenti
(
	ruolo VARCHAR2(255) NOT NULL,
	-- tipo (es. xsd,xml...)
	tipo VARCHAR2(255) NOT NULL,
	-- nome documento
	nome VARCHAR2(255) NOT NULL,
	-- contenuto documento
	contenuto BLOB NOT NULL,
	-- idOggettoProprietarioDocumento
	id_proprietario NUMBER NOT NULL,
	-- tipoProprietario
	tipo_proprietario VARCHAR2(255) NOT NULL,
	ora_registrazione TIMESTAMP NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_documenti_1 CHECK (ruolo IN ('allegato','specificaSemiformale','specificaLivelloServizio','specificaSicurezza','specificaCoordinamento')),
	CONSTRAINT chk_documenti_2 CHECK (tipo_proprietario IN ('accordoServizio','accordoCooperazione','servizio')),
	-- unique constraints
	CONSTRAINT unique_documenti_1 UNIQUE (ruolo,tipo,nome,id_proprietario,tipo_proprietario),
	-- fk/pk keys constraints
	CONSTRAINT pk_documenti PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_DOC_SEARCH ON documenti (id_proprietario);

ALTER TABLE documenti MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_documenti
BEFORE
insert on documenti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_documenti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Accordi di Servizio Parte Comune ****

CREATE SEQUENCE seq_accordi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE accordi
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	profilo_collaborazione VARCHAR2(255),
	wsdl_definitorio CLOB,
	wsdl_concettuale CLOB,
	wsdl_logico_erogatore CLOB,
	wsdl_logico_fruitore CLOB,
	spec_conv_concettuale CLOB,
	spec_conv_erogatore CLOB,
	spec_conv_fruitore CLOB,
	filtro_duplicati VARCHAR2(255),
	conferma_ricezione VARCHAR2(255),
	identificativo_collaborazione VARCHAR2(255),
	consegna_in_ordine VARCHAR2(255),
	scadenza VARCHAR2(255),
	superuser VARCHAR2(255),
	-- id del soggetto referente
	id_referente NUMBER,
	versione VARCHAR2(255),
	-- 1/0 (vero/falso) indica se questo accordo di servizio e' utilizzabile in mancanza di azioni associate
	utilizzo_senza_azione NUMBER,
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato NUMBER,
	ora_registrazione TIMESTAMP,
	stato VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_accordi_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_accordi_1 UNIQUE (nome,id_referente,versione),
	-- fk/pk keys constraints
	CONSTRAINT pk_accordi PRIMARY KEY (id)
);


ALTER TABLE accordi MODIFY id_referente DEFAULT 0;
ALTER TABLE accordi MODIFY versione DEFAULT '';
ALTER TABLE accordi MODIFY utilizzo_senza_azione DEFAULT 1;
ALTER TABLE accordi MODIFY privato DEFAULT 0;
ALTER TABLE accordi MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE accordi MODIFY stato DEFAULT 'finale';

CREATE TRIGGER trg_accordi
BEFORE
insert on accordi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_accordi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_accordi_azioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE accordi_azioni
(
	id_accordo NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	profilo_collaborazione VARCHAR2(255),
	filtro_duplicati VARCHAR2(255),
	conferma_ricezione VARCHAR2(255),
	identificativo_collaborazione VARCHAR2(255),
	consegna_in_ordine VARCHAR2(255),
	scadenza VARCHAR2(255),
	correlata VARCHAR2(255),
	-- ridefinito/default
	profilo_azione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_accordi_azioni_1 UNIQUE (id_accordo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_azioni_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_accordi_azioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_accordi_azioni
BEFORE
insert on accordi_azioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_accordi_azioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_port_type MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE port_type
(
	id_accordo NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	profilo_collaborazione VARCHAR2(255),
	filtro_duplicati VARCHAR2(255),
	conferma_ricezione VARCHAR2(255),
	identificativo_collaborazione VARCHAR2(255),
	consegna_in_ordine VARCHAR2(255),
	scadenza VARCHAR2(255),
	-- ridefinito/default
	profilo_pt VARCHAR2(255),
	-- document/RPC
	soap_style VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_port_type_1 UNIQUE (id_accordo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_port_type_1 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_port_type PRIMARY KEY (id)
);

CREATE TRIGGER trg_port_type
BEFORE
insert on port_type
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_port_type.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_port_type_azioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE port_type_azioni
(
	id_port_type NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	profilo_collaborazione VARCHAR2(255),
	filtro_duplicati VARCHAR2(255),
	conferma_ricezione VARCHAR2(255),
	identificativo_collaborazione VARCHAR2(255),
	consegna_in_ordine VARCHAR2(255),
	scadenza VARCHAR2(255),
	correlata_servizio VARCHAR2(255),
	correlata VARCHAR2(255),
	-- ridefinito/default
	profilo_pt_azione VARCHAR2(255),
	-- document/rpc
	soap_style VARCHAR2(255),
	soap_action VARCHAR2(255),
	-- literal/encoded
	soap_use_msg_input VARCHAR2(255),
	-- namespace utilizzato per RPC
	soap_namespace_msg_input VARCHAR2(255),
	-- literal/encoded
	soap_use_msg_output VARCHAR2(255),
	-- namespace utilizzato per RPC
	soap_namespace_msg_output VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_port_type_azioni_1 UNIQUE (id_port_type,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_port_type_azioni_1 FOREIGN KEY (id_port_type) REFERENCES port_type(id),
	CONSTRAINT pk_port_type_azioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_port_type_azioni
BEFORE
insert on port_type_azioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_port_type_azioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_operation_messages MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE operation_messages
(
	id_port_type_azione NUMBER NOT NULL,
	-- true(1)/false(0), true indica un input-message, false un output-message
	input_message NUMBER,
	name VARCHAR2(255) NOT NULL,
	element_name VARCHAR2(255),
	element_namespace VARCHAR2(255),
	type_name VARCHAR2(255),
	type_namespace VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_operation_messages_1 FOREIGN KEY (id_port_type_azione) REFERENCES port_type_azioni(id),
	CONSTRAINT pk_operation_messages PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_OP_MESSAGES ON operation_messages (id_port_type_azione,input_message);

ALTER TABLE operation_messages MODIFY input_message DEFAULT 1;

CREATE TRIGGER trg_operation_messages
BEFORE
insert on operation_messages
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_operation_messages.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Accordi di Cooperazione ****

CREATE SEQUENCE seq_accordi_cooperazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE accordi_cooperazione
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	-- id del soggetto referente
	id_referente NUMBER,
	versione VARCHAR2(255),
	stato VARCHAR2(255) NOT NULL,
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato NUMBER,
	superuser VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_accordi_cooperazione_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_accordi_cooperazione_1 UNIQUE (nome,versione),
	-- fk/pk keys constraints
	CONSTRAINT pk_accordi_cooperazione PRIMARY KEY (id)
);


ALTER TABLE accordi_cooperazione MODIFY id_referente DEFAULT 0;
ALTER TABLE accordi_cooperazione MODIFY versione DEFAULT '';
ALTER TABLE accordi_cooperazione MODIFY stato DEFAULT 'finale';
ALTER TABLE accordi_cooperazione MODIFY privato DEFAULT 0;
ALTER TABLE accordi_cooperazione MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_accordi_cooperazione
BEFORE
insert on accordi_cooperazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_accordi_cooperazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_accordi_coop_partecipanti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE accordi_coop_partecipanti
(
	id_accordo_cooperazione NUMBER NOT NULL,
	id_soggetto NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_acc_coop_part_1 UNIQUE (id_accordo_cooperazione,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_accordi_coop_partecipanti_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_accordi_coop_partecipanti_2 FOREIGN KEY (id_accordo_cooperazione) REFERENCES accordi_cooperazione(id),
	CONSTRAINT pk_accordi_coop_partecipanti PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_AC_COOP_PAR ON accordi_coop_partecipanti (id_accordo_cooperazione);
CREATE TRIGGER trg_accordi_coop_partecipanti
BEFORE
insert on accordi_coop_partecipanti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_accordi_coop_partecipanti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Accordi di Servizio Parte Specifica ****

CREATE SEQUENCE seq_servizi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi
(
	nome_servizio VARCHAR2(255) NOT NULL,
	tipo_servizio VARCHAR2(255) NOT NULL,
	id_soggetto NUMBER NOT NULL,
	id_accordo NUMBER NOT NULL,
	servizio_correlato VARCHAR2(255),
	id_connettore NUMBER NOT NULL,
	wsdl_implementativo_erogatore CLOB,
	wsdl_implementativo_fruitore CLOB,
	superuser VARCHAR2(255),
	-- 1/0 (true/false) indica se il soggetto e' privato/pubblico
	privato NUMBER,
	ora_registrazione TIMESTAMP,
	port_type VARCHAR2(255),
	profilo VARCHAR2(255),
	descrizione VARCHAR2(255),
	stato VARCHAR2(255) NOT NULL,
	-- identificativi accordo di servizio parte specifica
	aps_nome VARCHAR2(255),
	aps_versione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_servizi_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_servizi_1 UNIQUE (nome_servizio,tipo_servizio,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_servizi_3 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_servizi PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_APS ON servizi (aps_nome,aps_versione,id_soggetto);

ALTER TABLE servizi MODIFY privato DEFAULT 0;
ALTER TABLE servizi MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE servizi MODIFY stato DEFAULT 'finale';

CREATE TRIGGER trg_servizi
BEFORE
insert on servizi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_servizi_fruitori MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_fruitori
(
	id_servizio NUMBER NOT NULL,
	id_soggetto NUMBER NOT NULL,
	id_connettore NUMBER NOT NULL,
	wsdl_implementativo_erogatore CLOB,
	wsdl_implementativo_fruitore CLOB,
	ora_registrazione TIMESTAMP,
	profilo VARCHAR2(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR2(255),
	stato VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_servizi_fruitori_1 CHECK (stato IN ('finale','bozza','operativo')),
	-- unique constraints
	CONSTRAINT unique_servizi_fruitori_1 UNIQUE (id_servizio,id_soggetto),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_fruitori_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_fruitori_2 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_servizi_fruitori_3 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_servizi_fruitori PRIMARY KEY (id)
);


ALTER TABLE servizi_fruitori MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE servizi_fruitori MODIFY stato DEFAULT 'finale';

CREATE TRIGGER trg_servizi_fruitori
BEFORE
insert on servizi_fruitori
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_fruitori.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_servizi_azioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_azioni
(
	nome_azione VARCHAR2(255) NOT NULL,
	id_servizio NUMBER NOT NULL,
	id_connettore NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_servizi_azioni_1 UNIQUE (nome_azione,id_servizio),
	-- fk/pk keys constraints
	CONSTRAINT fk_servizi_azioni_1 FOREIGN KEY (id_connettore) REFERENCES connettori(id),
	CONSTRAINT fk_servizi_azioni_2 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_servizi_azioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_servizi_azioni
BEFORE
insert on servizi_azioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_azioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- **** Accordi di Servizio Composti ****

CREATE SEQUENCE seq_acc_serv_composti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE acc_serv_composti
(
	id_accordo NUMBER NOT NULL,
	id_accordo_cooperazione NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_acc_serv_composti_1 UNIQUE (id_accordo),
	-- fk/pk keys constraints
	CONSTRAINT fk_acc_serv_composti_1 FOREIGN KEY (id_accordo_cooperazione) REFERENCES accordi_cooperazione(id),
	CONSTRAINT fk_acc_serv_composti_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_acc_serv_composti PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_AC_SC ON acc_serv_composti (id_accordo_cooperazione);
CREATE TRIGGER trg_acc_serv_composti
BEFORE
insert on acc_serv_composti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_acc_serv_composti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_acc_serv_componenti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE acc_serv_componenti
(
	id_servizio_composto NUMBER NOT NULL,
	id_servizio_componente NUMBER NOT NULL,
	azione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_acc_serv_componenti_1 FOREIGN KEY (id_servizio_composto) REFERENCES acc_serv_composti(id),
	CONSTRAINT fk_acc_serv_componenti_2 FOREIGN KEY (id_servizio_componente) REFERENCES servizi(id),
	CONSTRAINT pk_acc_serv_componenti PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_AC_SC_SC ON acc_serv_componenti (id_servizio_composto);
CREATE TRIGGER trg_acc_serv_componenti
BEFORE
insert on acc_serv_componenti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_acc_serv_componenti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


