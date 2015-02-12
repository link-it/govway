-- **** Porte di Dominio ****

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
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	superuser VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- check constraints
	CONSTRAINT chk_pdd_1 CHECK (tipo IN ('operativo','nonoperativo','esterno')),
	-- unique constraints
	CONSTRAINT unique_pdd_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_pdd_1 ON pdd (nome);


