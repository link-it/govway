-- **** Porte di Dominio ****

CREATE SEQUENCE seq_pdd start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pdd
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	implementazione VARCHAR(255) DEFAULT 'standard',
	subject VARCHAR(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	superuser VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pdd') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_pdd_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd PRIMARY KEY (id)
);



