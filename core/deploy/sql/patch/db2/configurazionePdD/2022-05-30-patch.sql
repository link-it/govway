
CREATE TABLE ct_rt_props
(
	rt_prop_name VARCHAR(255) NOT NULL,
	rt_prop_value VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT uniq_rt_prop_policy_1 UNIQUE (rt_prop_name),
	-- fk/pk keys constraints
	CONSTRAINT pk_ct_rt_props PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX idx_rt_prop_policy_1 ON ct_rt_props (rt_prop_name);
CREATE INDEX idx_rt_prop_policy_2 ON ct_rt_props (rt_prop_value);


CREATE TABLE pa_ct_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT uniq_pa_ct_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pa_ct_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_pa_ct_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pa_ct_props_1 ON pa_ct_properties (valore);
CREATE INDEX INDEX_PA_CT_PROP ON pa_ct_properties (id_porta);

CREATE TABLE pd_ct_properties
(
	id_porta BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
	-- unique constraints
	CONSTRAINT uniq_pd_ct_props_1 UNIQUE (id_porta,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_pd_ct_properties_1 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_pd_ct_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX idx_pd_ct_props_1 ON pd_ct_properties (valore);
CREATE INDEX INDEX_PD_CT_PROP ON pd_ct_properties (id_porta);
