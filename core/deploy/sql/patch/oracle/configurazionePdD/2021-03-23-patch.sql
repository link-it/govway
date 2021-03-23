
CREATE INDEX INDEX_SA_RUOLI ON sa_ruoli (id_servizio_applicativo);

CREATE SEQUENCE seq_sa_credenziali MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE sa_credenziali
(
	id_servizio_applicativo NUMBER NOT NULL,
	subject VARCHAR2(2800),
	cn_subject VARCHAR2(255),
	issuer VARCHAR2(2800),
	cn_issuer VARCHAR2(255),
	certificate BLOB,
	cert_strict_verification NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_credenziali_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_CRED ON sa_credenziali (id_servizio_applicativo);
CREATE TRIGGER trg_sa_credenziali
BEFORE
insert on sa_credenziali
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_sa_credenziali.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/

