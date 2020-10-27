-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX index_mapping_erogazione_pa_2;
-- DROP INDEX index_mapping_erogazione_pa_1;
-- DROP INDEX index_mapping_fruizione_pd_2;
-- DROP INDEX index_mapping_fruizione_pd_1;
DROP TRIGGER trg_mapping_erogazione_pa;
DROP TRIGGER trg_mapping_fruizione_pd;
DROP TABLE mapping_erogazione_pa;
DROP TABLE mapping_fruizione_pd;
DROP SEQUENCE seq_mapping_erogazione_pa;
DROP SEQUENCE seq_mapping_fruizione_pd;
