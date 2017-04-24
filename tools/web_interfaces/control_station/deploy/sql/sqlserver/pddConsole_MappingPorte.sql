-- **** Mapping con Porte ****

CREATE TABLE mapping_fruizione_pd
(
	id_fruizione BIGINT NOT NULL,
	id_porta BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_mapping_fruizione_pd_1 UNIQUE (id_fruizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_fruizione_pd_1 FOREIGN KEY (id_fruizione) REFERENCES servizi_fruitori(id),
	CONSTRAINT fk_mapping_fruizione_pd_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_mapping_fruizione_pd PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_mapping_fruizione_pd_1 ON mapping_fruizione_pd (id_fruizione);



CREATE TABLE mapping_erogazione_pa
(
	id_erogazione BIGINT NOT NULL,
	id_porta BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT IDENTITY,
	-- unique constraints
	CONSTRAINT unique_mapping_erogazione_pa_1 UNIQUE (id_erogazione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_erogazione_pa_1 FOREIGN KEY (id_erogazione) REFERENCES servizi(id),
	CONSTRAINT fk_mapping_erogazione_pa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_mapping_erogazione_pa PRIMARY KEY (id)
);

-- index
CREATE UNIQUE INDEX index_mapping_erogazione_pa_1 ON mapping_erogazione_pa (id_erogazione);


