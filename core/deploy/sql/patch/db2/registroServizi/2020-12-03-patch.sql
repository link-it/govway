-- Eliminata tabella inutilizzata
DROP TABLE plugin_info;
DROP TABLE plugins_filtro_comp;

-- Supporto per la registrazione delle classi di plugins
ALTER TABLE plugins ADD tipo_plugin VARCHAR(255);
ALTER TABLE plugins ALTER COLUMN tipo_plugin SET NOT NULL;
ALTER TABLE plugins DROP CONSTRAINT unique_plugins_1;
ALTER TABLE plugins DROP CONSTRAINT unique_plugins_2;
ALTER TABLE plugins DROP COLUMN tipo;
ALTER TABLE plugins ADD tipo VARCHAR(255);
ALTER TABLE plugins ALTER COLUMN tipo SET NOT NULL;
ALTER TABLE plugins ADD stato SMALLINT DEFAULT 1;
ALTER TABLE plugins ADD CONSTRAINT unique_plugins_1 UNIQUE (tipo_plugin,class_name);
ALTER TABLE plugins ADD CONSTRAINT unique_plugins_2 UNIQUE (tipo_plugin,tipo);
ALTER TABLE plugins ADD CONSTRAINT unique_plugins_3 UNIQUE (tipo_plugin,label);
CALL SYSPROC.ADMIN_CMD ('REORG TABLE plugins') ;


CREATE TABLE plugins_props_comp
(
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	id_plugin BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_plugins_props_comp_1 FOREIGN KEY (id_plugin) REFERENCES plugins(id) ON DELETE CASCADE,
	CONSTRAINT pk_plugins_props_comp PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_plug_prop_com_1 ON plugins_props_comp (id_plugin);
