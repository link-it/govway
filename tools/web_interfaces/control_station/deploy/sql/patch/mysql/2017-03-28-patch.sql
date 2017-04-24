-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX index_politiche_sicurezza_1 ON politiche_sicurezza ;
DROP TABLE politiche_sicurezza;

-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX INDEX_RUOLI_SA ON ruoli_sa ;
-- DROP INDEX INDEX_RUOLI ON ruoli_sa ;
DROP TABLE ruoli_sa;


ALTER TABLE soggetti ADD COLUMN tipoauth VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN utente VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN password VARCHAR(255);
ALTER TABLE soggetti ADD COLUMN subject VARCHAR(255);


CREATE TABLE soggetti_ruoli
(
	id_soggetto BIGINT NOT NULL,
	id_ruolo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_soggetti_ruoli_1 UNIQUE (id_soggetto,id_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT fk_soggetti_ruoli_1 FOREIGN KEY (id_soggetto) REFERENCES soggetti(id),
	CONSTRAINT fk_soggetti_ruoli_2 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT pk_soggetti_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_soggetti_ruoli_1 ON soggetti_ruoli (id_soggetto,id_ruolo);


-- **** Mapping con Porte ****

CREATE TABLE mapping_fruizione_pd
(
	id_fruizione BIGINT NOT NULL,
	id_porta BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_mapping_fruizione_pd_1 UNIQUE (id_fruizione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_fruizione_pd_1 FOREIGN KEY (id_fruizione) REFERENCES servizi_fruitori(id),
	CONSTRAINT fk_mapping_fruizione_pd_2 FOREIGN KEY (id_porta) REFERENCES porte_delegate(id),
	CONSTRAINT pk_mapping_fruizione_pd PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_mapping_fruizione_pd_1 ON mapping_fruizione_pd (id_fruizione);



CREATE TABLE mapping_erogazione_pa
(
	id_erogazione BIGINT NOT NULL,
	id_porta BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_mapping_erogazione_pa_1 UNIQUE (id_erogazione),
	-- fk/pk keys constraints
	CONSTRAINT fk_mapping_erogazione_pa_1 FOREIGN KEY (id_erogazione) REFERENCES servizi(id),
	CONSTRAINT fk_mapping_erogazione_pa_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
	CONSTRAINT pk_mapping_erogazione_pa PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_mapping_erogazione_pa_1 ON mapping_erogazione_pa (id_erogazione);


