DROP INDEX index_soggetti_1 ON soggetti;
CREATE UNIQUE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);
DROP INDEX index_soggetti_2 ON soggetti;
CREATE UNIQUE INDEX index_soggetti_2 ON soggetti (codice_ipa);

DROP INDEX index_pdd_1 ON pdd;
CREATE UNIQUE INDEX index_pdd_1 ON pdd (nome);

DROP INDEX index_politiche_sicurezza_1 ON politiche_sicurezza;
CREATE UNIQUE INDEX index_politiche_sicurezza_1 ON politiche_sicurezza (id_fruitore,id_servizio,id_servizio_applicativo);
