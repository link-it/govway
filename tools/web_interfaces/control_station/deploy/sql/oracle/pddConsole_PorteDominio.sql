-- **** Porte di Dominio ****

CREATE SEQUENCE seq_pdd MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE pdd
(
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	-- ip pubblico
	ip VARCHAR2(255),
	-- porta pubblico
	porta NUMBER,
	-- protocollo pubblico
	protocollo VARCHAR2(255),
	-- ip gestione
	ip_gestione VARCHAR2(255),
	-- porta gestione
	porta_gestione NUMBER,
	-- protocollo gestione
	protocollo_gestione VARCHAR2(255),
	-- Tipo della Porta
	tipo VARCHAR2(255),
	implementazione VARCHAR2(255),
	subject VARCHAR2(255),
	password VARCHAR2(255),
	-- client auth: disabilitato/abilitato
	client_auth VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	superuser VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_pdd_1 CHECK (tipo IN ('operativo','nonoperativo','esterno')),
	-- unique constraints
	CONSTRAINT unique_pdd_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_pdd PRIMARY KEY (id)
);


ALTER TABLE pdd MODIFY implementazione DEFAULT 'standard';
ALTER TABLE pdd MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_pdd
BEFORE
insert on pdd
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_pdd.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


