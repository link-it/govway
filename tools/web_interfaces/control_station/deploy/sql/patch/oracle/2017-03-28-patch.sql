-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_politiche_sicurezza;
DROP TABLE politiche_sicurezza;
DROP SEQUENCE seq_politiche_sicurezza;

-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX INDEX_RUOLI_SA;
-- DROP INDEX INDEX_RUOLI;
DROP TRIGGER trg_ruoli_sa;
DROP TABLE ruoli_sa;
DROP SEQUENCE seq_ruoli_sa;


ALTER TABLE soggetti ADD tipoauth VARCHAR(255);
ALTER TABLE soggetti ADD utente VARCHAR(255);
ALTER TABLE soggetti ADD password VARCHAR(255);
ALTER TABLE soggetti ADD subject VARCHAR(255);

 
CREATE SEQUENCE seq_soggetti_ruoli MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE soggetti_ruoli
(
	id_soggetto NUMBER NOT NULL,
	id_ruolo NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_soggetti_ruoli_1 UNIQUE (id_soggetto,id_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_ruoli_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_soggetti_ruoli_2 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT pk_soggetti_ruoli PRIMARY KEY (id)
);

CREATE TRIGGER trg_soggetti_ruoli
BEFORE
insert on soggetti_ruoli
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_soggetti_ruoli.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


-- **** Mapping con Porte ****

CREATE SEQUENCE seq_mapping_fruizione_pd MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE mapping_fruizione_pd
(
	id_fruizione NUMBER NOT NULL,
	id_porta NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_mapping_fruizione_pd_1 UNIQUE (id_fruizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_fruizione_pd_1 FOREIGN KEY (id_fruizione) REFERENCES servizi_fruitori(id),
	CONSTRAINT fk_mapping_fruizione_pd_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_mapping_fruizione_pd PRIMARY KEY (id)
);

CREATE TRIGGER trg_mapping_fruizione_pd
BEFORE
insert on mapping_fruizione_pd
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_mapping_fruizione_pd.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_mapping_erogazione_pa MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE mapping_erogazione_pa
(
	id_erogazione NUMBER NOT NULL,
	id_porta NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_mapping_erogazione_pa_1 UNIQUE (id_erogazione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_erogazione_pa_1 FOREIGN KEY (id_erogazione) REFERENCES servizi(id),
	CONSTRAINT fk_mapping_erogazione_pa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_mapping_erogazione_pa PRIMARY KEY (id)
);

CREATE TRIGGER trg_mapping_erogazione_pa
BEFORE
insert on mapping_erogazione_pa
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_mapping_erogazione_pa.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


