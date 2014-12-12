-- **** Politiche di Sicurezza ****

CREATE TABLE politiche_sicurezza
(
	id_fruitore BIGINT NOT NULL,
	id_servizio BIGINT NOT NULL,
	id_servizio_applicativo BIGINT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_politiche_sicurezza_1 UNIQUE (id_fruitore,id_servizio,id_servizio_applicativo),
	-- fk/pk keys constraints
	CONSTRAINT fk_politiche_sicurezza_1 FOREIGN KEY (id_fruitore) REFERENCES soggetti(id),
	CONSTRAINT fk_politiche_sicurezza_2 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
	CONSTRAINT fk_politiche_sicurezza_3 FOREIGN KEY (id_servizio) REFERENCES servizi(id),
	CONSTRAINT pk_politiche_sicurezza PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_politiche_sicurezza_1 ON politiche_sicurezza (id_fruitore,id_servizio,id_servizio_applicativo);


