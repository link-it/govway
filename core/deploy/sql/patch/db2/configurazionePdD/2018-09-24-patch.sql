ALTER TABLE soggetti ADD is_default INT DEFAULT 0;

UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='gw');
UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='spc');
UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='sdi');
UPDATE soggetti SET is_default=1 WHERE id = (select min(id) from soggetti where tipo_soggetto='as4');

ALTER TABLE configurazione ADD multitenant_stato VARCHAR(255);
ALTER TABLE configurazione ADD multitenant_fruizioni VARCHAR(255);
ALTER TABLE configurazione ADD multitenant_erogazioni VARCHAR(255);


CREATE TABLE porte_applicative_sa_auth
(
        id_porta BIGINT NOT NULL,
        id_servizio_applicativo BIGINT NOT NULL,
        -- fk/pk columns
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE),
        -- unique constraints
        CONSTRAINT uniq_pa_sa_auth_1 UNIQUE (id_porta,id_servizio_applicativo),
        -- fk/pk keys constraints
        CONSTRAINT fk_porte_applicative_sa_auth_1 FOREIGN KEY (id_servizio_applicativo) REFERENCES servizi_applicativi(id),
        CONSTRAINT fk_porte_applicative_sa_auth_2 FOREIGN KEY (id_porta) REFERENCES porte_applicative(id),
        CONSTRAINT pk_porte_applicative_sa_auth PRIMARY KEY (id)
);

-- index
CREATE INDEX INDEX_PA_SA_AUTH ON porte_applicative_sa_auth (id_porta);

