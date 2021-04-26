-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_users_servizi;
DROP TRIGGER trg_users_soggetti;
DROP TRIGGER trg_users_password;
DROP TRIGGER trg_users_stati;
DROP TRIGGER trg_users;
DROP TABLE users_servizi;
DROP TABLE users_soggetti;
DROP TABLE users_password;
DROP TABLE users_stati;
DROP TABLE users;
DROP SEQUENCE seq_users_servizi;
DROP SEQUENCE seq_users_soggetti;
DROP SEQUENCE seq_users_password;
DROP SEQUENCE seq_users_stati;
DROP SEQUENCE seq_users;
