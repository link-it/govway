-- PLUGINS PER GLI EVENTI

CREATE SEQUENCE seq_notifiche_eventi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE notifiche_eventi
(
	tipo VARCHAR2(100) NOT NULL,
	codice VARCHAR2(100) NOT NULL,
	severita NUMBER NOT NULL,
	ora_registrazione TIMESTAMP NOT NULL,
	descrizione VARCHAR2(255),
	id_transazione VARCHAR2(255),
	id_configurazione VARCHAR2(255),
	configurazione CLOB,
	cluster_id VARCHAR2(100),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_notifiche_eventi PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_EVENTI ON notifiche_eventi (ora_registrazione DESC,severita,tipo,codice,id_configurazione,cluster_id);
CREATE TRIGGER trg_notifiche_eventi
BEFORE
insert on notifiche_eventi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_notifiche_eventi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


