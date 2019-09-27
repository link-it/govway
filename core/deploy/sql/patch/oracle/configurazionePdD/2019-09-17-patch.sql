-- DROP TRIGGER trg_config_protocolli;
-- DROP TABLE config_protocolli;
-- DROP SEQUENCE seq_config_protocolli;



CREATE SEQUENCE seq_config_url_invocazione MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_url_invocazione
(
	base_url VARCHAR2(255) NOT NULL,
	base_url_fruizione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_invocazione PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_url_invocazione
BEFORE
insert on config_url_invocazione
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_url_invocazione.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_config_url_regole MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_url_regole
(
	nome VARCHAR2(255) NOT NULL,
	posizione NUMBER NOT NULL,
	stato VARCHAR2(255),
	descrizione CLOB,
	regexpr NUMBER NOT NULL,
	regola VARCHAR2(255) NOT NULL,
	contesto_esterno VARCHAR2(255) NOT NULL,
	base_url VARCHAR2(255),
	protocollo VARCHAR2(255),
	ruolo VARCHAR2(255),
	service_binding VARCHAR2(255),
	tipo_soggetto VARCHAR2(255),
	nome_soggetto VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_config_url_regole_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_config_url_regole PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_url_regole
BEFORE
insert on config_url_regole
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_url_regole.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- Init Configurazione URL Invocazione
INSERT INTO config_url_invocazione (base_url) VALUES ('http://localhost:8080/govway/');

-- Regola per eDelivery
-- INSERT INTO config_url_regole (nome, posizione, stato, descrizione, regexpr, regola, contesto_esterno, base_url, protocollo, ruolo) VALUES ('Domibus',1,'abilitato','Servizio di ricezione dei messaggi AS4 dell''Access Point Domibus', 1, '^(.+)$', 'domibus-wildfly/services/msh', 'http://localhost:8080/','as4','portaApplicativa');



