-- **** Serial for ID ****

CREATE TABLE ID_MESSAGGIO
(
	COUNTER BIGINT NOT NULL,
	PROTOCOLLO VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT pk_ID_MESSAGGIO PRIMARY KEY (PROTOCOLLO)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;


CREATE TABLE ID_MESSAGGIO_RELATIVO
(
	COUNTER BIGINT NOT NULL,
	PROTOCOLLO VARCHAR(255) NOT NULL,
	INFO_ASSOCIATA VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT pk_ID_MESSAGGIO_RELATIVO PRIMARY KEY (PROTOCOLLO,INFO_ASSOCIATA)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;


CREATE TABLE ID_MESSAGGIO_PRG
(
	PROGRESSIVO VARCHAR(255) NOT NULL,
	PROTOCOLLO VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT pk_ID_MESSAGGIO_PRG PRIMARY KEY (PROTOCOLLO)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;


CREATE TABLE ID_MESSAGGIO_RELATIVO_PRG
(
	PROGRESSIVO VARCHAR(255) NOT NULL,
	PROTOCOLLO VARCHAR(255) NOT NULL,
	INFO_ASSOCIATA VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT pk_ID_MESSAGGIO_RELATIVO_PRG PRIMARY KEY (PROTOCOLLO,INFO_ASSOCIATA)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

