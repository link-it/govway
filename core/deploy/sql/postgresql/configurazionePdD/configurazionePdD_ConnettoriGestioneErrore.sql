-- **** Connettori Gestione Errore ****

CREATE SEQUENCE seq_gestione_errore start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE gestione_errore
(
	-- accetta/rispedisci
	comportamento_default VARCHAR(255),
	cadenza_rispedizione VARCHAR(255),
	nome VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_gestione_errore') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_gestione_errore_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_gestione_errore PRIMARY KEY (id)
);




CREATE SEQUENCE seq_gestione_errore_trasporto start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE gestione_errore_trasporto
(
	id_gestione_errore BIGINT NOT NULL,
	valore_massimo INT,
	valore_minimo INT,
	-- accetta/rispedisci
	comportamento VARCHAR(255),
	cadenza_rispedizione VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_gestione_errore_trasporto') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_gestione_errore_trasporto_1 FOREIGN KEY (id_gestione_errore) REFERENCES gestione_errore(id),
	CONSTRAINT pk_gestione_errore_trasporto PRIMARY KEY (id)
);




CREATE SEQUENCE seq_gestione_errore_soap start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE gestione_errore_soap
(
	id_gestione_errore BIGINT NOT NULL,
	fault_actor VARCHAR(255),
	fault_code VARCHAR(255),
	fault_string VARCHAR(255),
	-- accetta/rispedisci
	comportamento VARCHAR(255),
	cadenza_rispedizione VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_gestione_errore_soap') NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_gestione_errore_soap_1 FOREIGN KEY (id_gestione_errore) REFERENCES gestione_errore(id),
	CONSTRAINT pk_gestione_errore_soap PRIMARY KEY (id)
);



