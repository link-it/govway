-- **** Politiche di Sicurezza ****

CREATE SEQUENCE seq_politiche_sicurezza MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE politiche_sicurezza
(
	id_fruitore NUMBER NOT NULL,
	id_servizio NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_politiche_sicurezza_1 UNIQUE (id_fruitore,id_servizio,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_politiche_sicurezza_1 FOREIGN KEY (id_fruitore) REFERENCES soggetti(id),
	CONSTRAINT fk_politiche_sicurezza_2 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_politiche_sicurezza_3 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_politiche_sicurezza PRIMARY KEY (id)
);

CREATE TRIGGER trg_politiche_sicurezza
BEFORE
insert on politiche_sicurezza
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_politiche_sicurezza.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


