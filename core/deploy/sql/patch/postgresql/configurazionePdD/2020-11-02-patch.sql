ALTER TABLE porte_applicative ADD COLUMN validazione_contenuti_soapa VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN validazione_contenuti_json VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN validazione_contenuti_pat_and VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN validazione_contenuti_pat_not VARCHAR(255);

ALTER TABLE porte_delegate ADD COLUMN validazione_contenuti_soapa VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN validazione_contenuti_json VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN validazione_contenuti_pat_and VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN validazione_contenuti_pat_not VARCHAR(255);


CREATE SEQUENCE seq_pa_val_pattern start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_val_pattern
(
	id_configurazione BIGINT NOT NULL,
	tipo_validazione VARCHAR(255) NOT NULL,
	nome VARCHAR(255) NOT NULL,
	regola TEXT NOT NULL,
	-- abilitato/disabilitato
	pattern_and VARCHAR(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_val_pattern') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_val_pattern_1 UNIQUE (tipo_validazione,id_configurazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pa_val_pattern PRIMARY KEY (id)
);




CREATE SEQUENCE seq_pa_val_richiesta start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_val_richiesta
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR(255),
	-- abilitato/disabilitato
	mtom VARCHAR(255),
	-- abilitato/disabilitato
	soapaction VARCHAR(255),
	-- Nome interfaccia json
	json VARCHAR(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR(255),
	applicabilita_azioni TEXT,
	applicabilita_ct TEXT,
	applicabilita_pattern TEXT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_val_richiesta') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_val_richiesta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pa_val_richiesta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_val_richiesta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_val_richiesta PRIMARY KEY (id)
);




CREATE SEQUENCE seq_pa_val_risposta start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pa_val_risposta
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR(255),
	-- abilitato/disabilitato
	mtom VARCHAR(255),
	-- Nome interfaccia json
	json VARCHAR(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR(255),
	applicabilita_azioni TEXT,
	applicabilita_ct TEXT,
	applicabilita_pattern TEXT,
	applicabilita_status_min INT,
	applicabilita_status_max INT,
	applicabilita_problem_detail VARCHAR(255),
	applicabilita_empty_response VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pa_val_risposta') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pa_val_risposta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pa_val_risposta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_val_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_val_risposta PRIMARY KEY (id)
);




CREATE SEQUENCE seq_pd_val_pattern start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_val_pattern
(
	id_configurazione BIGINT NOT NULL,
	tipo_validazione VARCHAR(255) NOT NULL,
	nome VARCHAR(255) NOT NULL,
	regola TEXT NOT NULL,
	-- abilitato/disabilitato
	pattern_and VARCHAR(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_val_pattern') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_val_pattern_1 UNIQUE (tipo_validazione,id_configurazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pd_val_pattern PRIMARY KEY (id)
);




CREATE SEQUENCE seq_pd_val_richiesta start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_val_richiesta
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR(255),
	-- abilitato/disabilitato
	mtom VARCHAR(255),
	-- abilitato/disabilitato
	soapaction VARCHAR(255),
	-- Nome interfaccia json
	json VARCHAR(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR(255),
	applicabilita_azioni TEXT,
	applicabilita_ct TEXT,
	applicabilita_pattern TEXT,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_val_richiesta') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_val_richiesta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pd_val_richiesta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_val_richiesta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_val_richiesta PRIMARY KEY (id)
);




CREATE SEQUENCE seq_pd_val_risposta start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pd_val_risposta
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	posizione INT NOT NULL,
	-- abilitato/disabilitato/warningOnly
	stato VARCHAR(255) NOT NULL,
	-- openspcoop/interface/xsd/json/pattern
	tipo VARCHAR(255),
	-- abilitato/disabilitato
	mtom VARCHAR(255),
	-- Nome interfaccia json
	json VARCHAR(255),
	-- abilitato/disabilitato
	pattern_and VARCHAR(255),
	-- abilitato/disabilitato
	pattern_not VARCHAR(255),
	applicabilita_azioni TEXT,
	applicabilita_ct TEXT,
	applicabilita_pattern TEXT,
	applicabilita_status_min INT,
	applicabilita_status_max INT,
	applicabilita_problem_detail VARCHAR(255),
	applicabilita_empty_response VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pd_val_risposta') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pd_val_risposta_1 UNIQUE (id_porta,nome),
	CONSTRAINT unique_pd_val_risposta_2 UNIQUE (id_porta,posizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_val_risposta_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_val_risposta PRIMARY KEY (id)
);



