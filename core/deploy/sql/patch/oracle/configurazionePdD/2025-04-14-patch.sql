
-- **** Digests Servizi Params ****

CREATE SEQUENCE seq_servizi_digest_params MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE servizi_digest_params
(
	id_servizio NUMBER NOT NULL,
	serial_number NUMBER,
	data_registrazione TIMESTAMP NOT NULL,
	seed VARCHAR2(1024) NOT NULL,
	algorithm VARCHAR2(32) NOT NULL,
	lifetime NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_servizi_digest_params PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_REF_SERVIZI ON servizi_digest_params (id_servizio);
CREATE TRIGGER trg_servizi_digest_params
BEFORE
insert on servizi_digest_params
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_servizi_digest_params.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/

-- semaphore
INSERT INTO OP2_SEMAPHORE (applicative_id) VALUES ('ServiziDigestParamsUpdate');
