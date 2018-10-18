ALTER TABLE soggetti ADD is_default NUMBER DEFAULT 0;

UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='gw');
UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='spc');
UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='sdi');
UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='as4');

ALTER TABLE configurazione ADD multitenant_stato VARCHAR2(255);
ALTER TABLE configurazione ADD multitenant_fruizioni VARCHAR2(255);
ALTER TABLE configurazione ADD multitenant_erogazioni VARCHAR2(255);


CREATE SEQUENCE seq_porte_applicative_sa_auth MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE porte_applicative_sa_auth
(
        id_porta NUMBER NOT NULL,
        id_servizio_applicativo NUMBER NOT NULL,
        -- fk/pk columns
        id NUMBER NOT NULL,
        -- unique constraints
        CONSTRAINT uniq_pa_sa_auth_1 UNIQUE (id_porta,id_servizio_applicativo),
        -- fk/pk keys constraints
        CONSTRAINT fk_porte_applicative_sa_auth_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
        CONSTRAINT fk_porte_applicative_sa_auth_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
        CONSTRAINT pk_porte_applicative_sa_auth PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_AUTH ON porte_applicative_sa_auth (id_porta);
CREATE TRIGGER trg_porte_applicative_sa_auth
BEFORE
insert on porte_applicative_sa_auth
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_porte_applicative_sa_auth.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/




