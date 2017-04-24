-- **** Mapping con Porte ****

CREATE SEQUENCE seq_mapping_fruizione_pd start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE mapping_fruizione_pd
(
	id_fruizione BIGINT NOT NULL,
	id_porta BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_mapping_fruizione_pd') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_mapping_fruizione_pd_1 UNIQUE (id_fruizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_fruizione_pd_1 FOREIGN KEY (id_fruizione) REFERENCES servizi_fruitori(id),
	CONSTRAINT fk_mapping_fruizione_pd_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_mapping_fruizione_pd PRIMARY KEY (id)
);




CREATE SEQUENCE seq_mapping_erogazione_pa start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE mapping_erogazione_pa
(
	id_erogazione BIGINT NOT NULL,
	id_porta BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT DEFAULT nextval('seq_mapping_erogazione_pa') NOT NULL,
	-- unique constraints
	CONSTRAINT unique_mapping_erogazione_pa_1 UNIQUE (id_erogazione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_erogazione_pa_1 FOREIGN KEY (id_erogazione) REFERENCES servizi(id),
	CONSTRAINT fk_mapping_erogazione_pa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_mapping_erogazione_pa PRIMARY KEY (id)
);



