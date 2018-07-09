-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
-- DROP INDEX idx_cong_att_policy_1;
DROP TRIGGER trg_ct_active_policy;
DROP TRIGGER trg_ct_config_policy;
DROP TRIGGER trg_ct_config;
DROP TABLE ct_active_policy;
DROP TABLE ct_config_policy;
DROP TABLE ct_config;
DROP SEQUENCE seq_ct_active_policy;
DROP SEQUENCE seq_ct_config_policy;
DROP SEQUENCE seq_ct_config;
