
CREATE INDEX INDEX_SOGGETTI_RUOLI ON soggetti_ruoli (id_soggetto);

CREATE SEQUENCE seq_soggetti_credenziali MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_credenziali
(
	id_soggetto NUMBER NOT NULL,
	subject VARCHAR2(2800),
	cn_subject VARCHAR2(255),
	issuer VARCHAR2(2800),
	cn_issuer VARCHAR2(255),
	certificate BLOB,
	cert_strict_verification NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_credenziali_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT pk_soggetti_credenziali PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SOGGETTI_CRED ON soggetti_credenziali (id_soggetto);
CREATE TRIGGER trg_soggetti_credenziali
BEFORE
insert on soggetti_credenziali
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_credenziali.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/

