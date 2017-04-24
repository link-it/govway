-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_mapping_erogazione_pa;
DROP TRIGGER trg_mapping_fruizione_pd;
DROP TABLE mapping_erogazione_pa;
DROP TABLE mapping_fruizione_pd;
DROP SEQUENCE seq_mapping_erogazione_pa;
DROP SEQUENCE seq_mapping_fruizione_pd;
