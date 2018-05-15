-- PLUGINS PER LE STATISTICHE PERSONALIZZATE

CREATE SEQUENCE seq_stat_personalizzate MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE stat_personalizzate
(
	id VARCHAR2(255) NOT NULL,
	enabled NUMBER NOT NULL,
	label VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	pid NUMBER NOT NULL,
	id_configurazione NUMBER NOT NULL,
	id_plugin NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_stat_pers_1 UNIQUE (id_configurazione,id),
	CONSTRAINT uniq_stat_pers_2 UNIQUE (id_configurazione,id_plugin),
	-- fk/pk keys constraints
	CONSTRAINT fk_stat_personalizzate_1 FOREIGN KEY (id_configurazione) REFERENCES plugins_conf_azioni(id) ON DELETE CASCADE,
	CONSTRAINT fk_stat_personalizzate_2 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_stat_personalizzate PRIMARY KEY (pid)
);

CREATE TRIGGER trg_stat_personalizzate
BEFORE
insert on stat_personalizzate
for each row
begin
   IF (:new.pid IS NULL) THEN
      SELECT seq_stat_personalizzate.nextval INTO :new.pid
                FROM DUAL;
   END IF;
end;
/


