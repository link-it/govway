
CREATE SEQUENCE seq_sa_properties AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;

CREATE TABLE sa_properties
(
	id_servizio_applicativo BIGINT NOT NULL,
	nome VARCHAR(255) NOT NULL,
	valore VARCHAR(4000) NOT NULL,
	-- fk/pk columns
	id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
	-- unique constraints
	CONSTRAINT uniq_sa_properties_1 UNIQUE (id_servizio_applicativo,nome),
	-- fk/pk keys constraints
	CONSTRAINT fk_sa_properties_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT pk_sa_properties PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_SA_PROP ON sa_properties (id_servizio_applicativo);
CREATE TABLE sa_properties_init_seq (id BIGINT);
INSERT INTO sa_properties_init_seq VALUES (NEXT VALUE FOR seq_sa_properties);


