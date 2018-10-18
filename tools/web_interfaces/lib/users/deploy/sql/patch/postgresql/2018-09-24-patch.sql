ALTER TABLE users DROP COLUMN multi_tenant;
ALTER TABLE users ADD COLUMN soggetto_pddconsole VARCHAR(255);
ALTER TABLE users ADD COLUMN soggetto_pddmonitor VARCHAR(255);
ALTER TABLE users ADD COLUMN soggetti_all INT;
ALTER TABLE users ADD COLUMN servizi_all INT;

UPDATE users set soggetti_all=1;
UPDATE users set servizi_all=1;
