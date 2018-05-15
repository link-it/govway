-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_conf_risorse_contenuti;
DROP TRIGGER trg_configurazione_stati;
DROP TRIGGER trg_config_tran_plugins;
DROP TRIGGER trg_config_transazioni;
DROP TABLE conf_risorse_contenuti;
DROP TABLE configurazione_stati;
DROP TABLE config_tran_plugins;
DROP TABLE config_transazioni;
DROP SEQUENCE seq_conf_risorse_contenuti;
DROP SEQUENCE seq_configurazione_stati;
DROP SEQUENCE seq_config_tran_plugins;
DROP SEQUENCE seq_config_transazioni;
