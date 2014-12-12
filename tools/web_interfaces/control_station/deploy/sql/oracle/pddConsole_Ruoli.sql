-- **** Ruoli ****

CREATE SEQUENCE seq_ruoli_sa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ruoli_sa
(
	id_accordo NUMBER NOT NULL,
	servizio_correlato NUMBER NOT NULL,
	id_servizio_applicativo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_ruoli_sa_1 UNIQUE (id_accordo,servizio_correlato,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_ruoli_sa_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_ruoli_sa_2 FOREIGN KEY (id_accordo) REFERENCES accordi(id),
	CONSTRAINT pk_ruoli_sa PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_RUOLI ON ruoli_sa (id_accordo,servizio_correlato);
CREATE INDEX INDEX_RUOLI_SA ON ruoli_sa (id_servizio_applicativo);
CREATE TRIGGER trg_ruoli_sa
BEFORE
insert on ruoli_sa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_ruoli_sa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


