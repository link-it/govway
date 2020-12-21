-- PLUGINS

CREATE SEQUENCE seq_plugins MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins
(
	tipo_plugin VARCHAR2(255) NOT NULL,
	class_name VARCHAR2(255) NOT NULL,
	tipo VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	label VARCHAR2(255) NOT NULL,
	stato NUMBER,
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_plugins_1 UNIQUE (tipo_plugin,class_name),
	CONSTRAINT unique_plugins_2 UNIQUE (tipo_plugin,tipo),
	CONSTRAINT unique_plugins_3 UNIQUE (tipo_plugin,label),
	-- fk/pk keys constraints
	CONSTRAINT pk_plugins PRIMARY KEY (id)
);


ALTER TABLE plugins MODIFY stato DEFAULT 1;

CREATE TRIGGER trg_plugins
BEFORE
insert on plugins
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_plugins_servizi_comp MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins_servizi_comp
(
	-- Accordo di Servizio
	uri_accordo VARCHAR2(255),
	-- Nome del port-type
	servizio VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_plugin NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_servizi_comp_1 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_servizi_comp PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_plug_ser_com_1 ON plugins_servizi_comp (id_plugin);
CREATE TRIGGER trg_plugins_servizi_comp
BEFORE
insert on plugins_servizi_comp
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins_servizi_comp.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_plugins_azioni_comp MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins_azioni_comp
(
	azione VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_plugin_servizio_comp NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_plugins_azioni_comp_1 UNIQUE (id_plugin_servizio_comp,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_azioni_comp_1 FOREIGN KEY (id_plugin_servizio_comp) REFERENCES plugins_servizi_comp(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_azioni_comp PRIMARY KEY (id)
);

CREATE TRIGGER trg_plugins_azioni_comp
BEFORE
insert on plugins_azioni_comp
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins_azioni_comp.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_plugins_props_comp MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins_props_comp
(
	nome VARCHAR2(255) NOT NULL,
	valore VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_plugin NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_props_comp_1 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_props_comp PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_plug_prop_com_1 ON plugins_props_comp (id_plugin);
CREATE TRIGGER trg_plugins_props_comp
BEFORE
insert on plugins_props_comp
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins_props_comp.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- CONFIGURAZIONE DEI SERVIZI PER PERSONALIZZAZIONI

CREATE SEQUENCE seq_plugins_conf_servizi MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins_conf_servizi
(
	-- Accordo di Servizio
	accordo VARCHAR2(255) NOT NULL,
	tipo_soggetto_referente VARCHAR2(255) NOT NULL,
	nome_soggetto_referente VARCHAR2(255) NOT NULL,
	versione NUMBER NOT NULL,
	-- Nome del port-type
	servizio VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT pk_plugins_conf_servizi PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_conf_servizi_1 ON plugins_conf_servizi (accordo,tipo_soggetto_referente,nome_soggetto_referente,versione,servizio);

ALTER TABLE plugins_conf_servizi MODIFY versione DEFAULT 1;

CREATE TRIGGER trg_plugins_conf_servizi
BEFORE
insert on plugins_conf_servizi
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins_conf_servizi.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



CREATE SEQUENCE seq_plugins_conf_azioni MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins_conf_azioni
(
	azione VARCHAR2(255) NOT NULL,
	-- fk/pk columns
	id NUMBER NOT NULL,
	id_config_servizio NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT unique_plugins_conf_azioni_1 UNIQUE (id_config_servizio,azione),
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_conf_azioni_1 FOREIGN KEY (id_config_servizio) REFERENCES plugins_conf_servizi(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_conf_azioni PRIMARY KEY (id)
);

CREATE TRIGGER trg_plugins_conf_azioni
BEFORE
insert on plugins_conf_azioni
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins_conf_azioni.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/



-- FILTRO PER PERSONALIZZAZIONI

CREATE SEQUENCE seq_plugins_conf_filtri MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;

CREATE TABLE plugins_conf_filtri
(
	-- Dati Identificativi Regola
	nome VARCHAR2(255) NOT NULL,
	descrizione VARCHAR2(255),
	-- Regole di Filtraggio
	tipo_mittente VARCHAR2(255),
	nome_mittente VARCHAR2(255),
	idporta_mittente VARCHAR2(255),
	tipo_destinatario VARCHAR2(255),
	nome_destinatario VARCHAR2(255),
	idporta_destinatario VARCHAR2(255),
	tipo_servizio VARCHAR2(255),
	nome_servizio VARCHAR2(255),
	versione_servizio NUMBER,
	azione VARCHAR2(255),
	-- fk/pk columns
	id NUMBER NOT NULL,
	-- unique constraints
	CONSTRAINT uniq_conf_filtri_1 UNIQUE (nome),
	-- fk/pk keys constraints
	CONSTRAINT pk_plugins_conf_filtri PRIMARY KEY (id)
);


ALTER TABLE plugins_conf_filtri MODIFY versione_servizio DEFAULT 1;

CREATE TRIGGER trg_plugins_conf_filtri
BEFORE
insert on plugins_conf_filtri
for each row
begin
   IF (:new.id IS NULL) THEN
      SELECT seq_plugins_conf_filtri.nextval INTO :new.id
                FROM DUAL;
   END IF;
end;
/


