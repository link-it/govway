-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_congestion_active_policy;
DROP TRIGGER trg_congestion_config_policy;
DROP TRIGGER trg_congestion_config;
DROP TABLE congestion_active_policy;
DROP TABLE congestion_config_policy;
DROP TABLE congestion_config;
DROP SEQUENCE seq_congestion_active_policy;
DROP SEQUENCE seq_congestion_config_policy;
DROP SEQUENCE seq_congestion_config;
