-- PLUGINS PER LE RICERCHE PERSONALIZZATE

CREATE SEQUENCE seq_ricerche_personalizzate MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE ricerche_personalizzate
(
	id VARCHAR2(255) NOT NULL,
	enabled NUMBER NOT NULL,
	label VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	pid NUMBER NOT NULL,
	id_configurazione NUMBER NOT NULL,
	id_plugin NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_ricerche_pers_1 UNIQUE (id_configurazione,id),
	CONSTRAINT uniq_ricerche_pers_2 UNIQUE (id_configurazione,id_plugin),
	-- fk/pk keys constraints
	CONSTRAINT fk_ricerche_personalizzate_1 FOREIGN KEY (id_configurazione) REFERENCES plugins_conf_azioni(id) ON DELETE CASCADE,
	CONSTRAINT fk_ricerche_personalizzate_2 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_ricerche_personalizzate PRIMARY KEY (pid)
);

CREATE TRIGGER trg_ricerche_personalizzate
BEFORE
insert on ricerche_personalizzate
for each row
begin
   IF (:new.pid IS NULL) THEN
      SELECT seq_ricerche_personalizzate.nextval INTO :new.pid
                FROM DUAL;
   END IF;
end;
/


