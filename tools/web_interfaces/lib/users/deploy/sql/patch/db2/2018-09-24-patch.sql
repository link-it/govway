ALTER TABLE users DROP COLUMN multi_tenant;
ALTER TABLE users ADD soggetto_pddconsole VARCHAR(255);
ALTER TABLE users ADD soggetto_pddmonitor VARCHAR(255);
ALTER TABLE users ADD soggetti_all INT;
ALTER TABLE users ADD servizi_all INT;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE users') ;

UPDATE users set soggetti_all=1;
UPDATE users set servizi_all=1;
