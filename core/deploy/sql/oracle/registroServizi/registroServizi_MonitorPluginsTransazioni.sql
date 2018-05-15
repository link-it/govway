-- PERSONALIZZAZIONI/PLUGINS DELLE TRANSAZIONI

CREATE SEQUENCE seq_config_transazioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_transazioni
(
	enabled NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_conf_servizio_azione NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_trans_pers_1 UNIQUE (id_conf_servizio_azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_config_transazioni_1 FOREIGN KEY (id_conf_servizio_azione) REFERENCES plugins_conf_azioni(id) ON DELETE CASCADE,
	CONSTRAINT pk_config_transazioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_transazioni
BEFORE
insert on config_transazioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_transazioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_config_tran_plugins MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE config_tran_plugins
(
	id_conf_trans_plugin VARCHAR2(255) NOT NULL,
	enabled NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_configurazione_transazione NUMBER NOT NULL,
	id_plugin NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_conf_trans_plug_1 UNIQUE (id_configurazione_transazione,id_conf_trans_plugin),
	CONSTRAINT uniq_conf_trans_plug_2 UNIQUE (id_configurazione_transazione,id_plugin),
	-- fk/pk keys constraints
	CONSTRAINT fk_config_tran_plugins_1 FOREIGN KEY (id_configurazione_transazione) REFERENCES config_transazioni(id) ON DELETE CASCADE,
	CONSTRAINT fk_config_tran_plugins_2 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_config_tran_plugins PRIMARY KEY (id)
);

CREATE TRIGGER trg_config_tran_plugins
BEFORE
insert on config_tran_plugins
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_config_tran_plugins.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_configurazione_stati MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE configurazione_stati
(
	enabled NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	tipo_controllo VARCHAR2(255) NOT NULL,
	tipo_messaggio VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255),
	xpath VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_configurazione_transazione NUMBER NOT NULL,
	-- check constraints
	CONSTRAINT chk_configurazione_stati_1 CHECK (tipo_controllo IN ('Match','Equals','Exist')),
	CONSTRAINT chk_configurazione_stati_2 CHECK (tipo_messaggio IN ('Richiesta','Risposta')),
	-- unique constraints
	CONSTRAINT uniq_trans_pers_stati_1 UNIQUE (id_configurazione_transazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_configurazione_stati_1 FOREIGN KEY (id_configurazione_transazione) REFERENCES config_transazioni(id) ON DELETE CASCADE,
	CONSTRAINT pk_configurazione_stati PRIMARY KEY (id)
);

CREATE TRIGGER trg_configurazione_stati
BEFORE
insert on configurazione_stati
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_configurazione_stati.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_conf_risorse_contenuti MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE conf_risorse_contenuti
(
	abilita_anonimizzazione NUMBER NOT NULL,
	abilita_compressione NUMBER NOT NULL,
	tipo_compressione VARCHAR2(255),
	carattere_maschera CHAR(1),
	num_char_maschera NUMBER,
	posizionamento_maschera VARCHAR2(255),
	tipo_mascheramento VARCHAR2(255),
	enabled NUMBER NOT NULL,
	nome VARCHAR2(255) NOT NULL,
	tipo_messaggio VARCHAR2(255) NOT NULL,
	xpath VARCHAR2(255) NOT NULL,
	stat_enabled NUMBER NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_conf_transazione NUMBER NOT NULL,
	id_configurazione_stato NUMBER,
	-- check constraints
	CONSTRAINT chk_conf_risorse_contenuti_1 CHECK (tipo_compressione IN ('Deflater','Gzip','Zip')),
	CONSTRAINT chk_conf_risorse_contenuti_2 CHECK (posizionamento_maschera IN ('Primi','Ultimi')),
	CONSTRAINT chk_conf_risorse_contenuti_3 CHECK (tipo_mascheramento IN ('Fisico','Logico')),
	CONSTRAINT chk_conf_risorse_contenuti_4 CHECK (tipo_messaggio IN ('Richiesta','Risposta')),
	-- unique constraints
	CONSTRAINT uniq_trans_pers_risorse_1 UNIQUE (id_conf_transazione,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_conf_risorse_contenuti_1 FOREIGN KEY (id_conf_transazione) REFERENCES config_transazioni(id) ON DELETE CASCADE,
	CONSTRAINT fk_conf_risorse_contenuti_2 FOREIGN KEY (id_configurazione_stato) REFERENCES configurazione_stati(id) ON DELETE CASCADE,
	CONSTRAINT pk_conf_risorse_contenuti PRIMARY KEY (id)
);


ALTER TABLE conf_risorse_contenuti MODIFY abilita_anonimizzazione DEFAULT 0;
ALTER TABLE conf_risorse_contenuti MODIFY abilita_compressione DEFAULT 0;
ALTER TABLE conf_risorse_contenuti MODIFY stat_enabled DEFAULT 0;

CREATE TRIGGER trg_conf_risorse_contenuti
BEFORE
insert on conf_risorse_contenuti
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_conf_risorse_contenuti.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


