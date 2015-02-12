-- **** Porte di Dominio ****

CREATE SEQUENCE seq_pdd start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE pdd
(
	nome VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	-- ip pubblico
	ip VARCHAR(255),
	-- porta pubblico
	porta INT,
	-- protocollo pubblico
	protocollo VARCHAR(255),
	-- ip gestione
	ip_gestione VARCHAR(255),
	-- porta gestione
	porta_gestione INT,
	-- protocollo gestione
	protocollo_gestione VARCHAR(255),
	-- Tipo della Porta
	tipo VARCHAR(255),
	implementazione VARCHAR(255) DEFAULT 'standard',
	subject VARCHAR(255),
	password VARCHAR(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR(255),
	ora_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	superuser VARCHAR(255),
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_pdd') NOT NULL,
	-- check constraints
	CONSTRAINT chk_pdd_1 CHECK (tipo IN ('operativo','nonoperativo','esterno')),
	-- unique constraints
	CONSTRAINT unique_pdd_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd PRIMARY KEY (id)
);



