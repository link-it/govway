-- Eliminata tabella inutilizzata
DROP TABLE plugin_info;
DROP TRIGGER trg_plugins_filtro_comp;
DROP TABLE plugins_filtro_comp;
DROP SEQUENCE seq_plugins_filtro_comp;

-- Supporto per la registrazione delle classi di plugins
ALTER TABLE plugins ADD tipo_plugin VARCHAR2(255) NOT NULL;
ALTER TABLE plugins DROP CONSTRAINT unique_plugins_1;
ALTER TABLE plugins DROP CONSTRAINT unique_plugins_2;
ALTER TABLE plugins DROP COLUMN tipo;
ALTER TABLE plugins ADD tipo VARCHAR2(255) NOT NULL;
ALTER TABLE plugins ADD stato NUMBER;
ALTER TABLE plugins MODIFY stato DEFAULT 1;
ALTER TABLE plugins ADD CONSTRAINT unique_plugins_1 UNIQUE (tipo_plugin,class_name);
ALTER TABLE plugins ADD CONSTRAINT unique_plugins_2 UNIQUE (tipo_plugin,tipo);
ALTER TABLE plugins ADD CONSTRAINT unique_plugins_3 UNIQUE (tipo_plugin,label);


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


