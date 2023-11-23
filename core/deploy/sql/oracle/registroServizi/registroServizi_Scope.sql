-- **** Scope ****

CREATE SEQUENCE seq_scope MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE scope
(
	nome VARCHAR2(255) NOT NULL,
	descrizione CLOB,
	tipologia VARCHAR2(255),
	nome_esterno VARCHAR2(255),
	contesto_utilizzo VARCHAR2(255) NOT NULL,
	superuser VARCHAR2(255),
	ora_registrazione TIMESTAMP,
	utente_richiedente VARCHAR2(255),
	data_creazione TIMESTAMP,
	utente_ultima_modifica VARCHAR2(255),
	data_ultima_modifica TIMESTAMP,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_scope_1 CHECK (contesto_utilizzo IN ('portaDelegata','portaApplicativa','qualsiasi')),
	-- unique constraints
	CONSTRAINT unique_scope_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_scope PRIMARY KEY (id)
);


ALTER TABLE scope MODIFY contesto_utilizzo DEFAULT 'qualsiasi';
ALTER TABLE scope MODIFY ora_registrazione DEFAULT CURRENT_TIMESTAMP;

CREATE TRIGGER trg_scope
BEFORE
insert on scope
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_scope.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


