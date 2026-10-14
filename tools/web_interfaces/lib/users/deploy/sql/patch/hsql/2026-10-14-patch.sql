INSERT INTO users_init_seq SELECT NEXT VALUE FOR seq_users FROM (VALUES(0)) WHERE NOT EXISTS (SELECT 1 FROM users_init_seq);
INSERT INTO users_stati_init_seq SELECT NEXT VALUE FOR seq_users_stati FROM (VALUES(0)) WHERE NOT EXISTS (SELECT 1 FROM users_stati_init_seq);
INSERT INTO users_password_init_seq SELECT NEXT VALUE FOR seq_users_password FROM (VALUES(0)) WHERE NOT EXISTS (SELECT 1 FROM users_password_init_seq);
INSERT INTO users_soggetti_init_seq SELECT NEXT VALUE FOR seq_users_soggetti FROM (VALUES(0)) WHERE NOT EXISTS (SELECT 1 FROM users_soggetti_init_seq);
INSERT INTO users_servizi_init_seq SELECT NEXT VALUE FOR seq_users_servizi FROM (VALUES(0)) WHERE NOT EXISTS (SELECT 1 FROM users_servizi_init_seq);
