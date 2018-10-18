UPDATE audit_appender_prop SET value='java:/comp/env/org.govway.datasource.console' WHERE name='datasource' AND id_audit_appender=(select id from audit_appender where name='dbAppender');

UPDATE registri SET location='java:/comp/env/org.govway.datasource.console' WHERE nome='RegistroDB' AND tipo='db';

-- UPDATE tracce_appender_prop SET valore='java:/comp/env/org.govway.datasource' WHERE nome='datasource' AND id_appender=(select id from tracce_appender where tipo='protocol');
-- UPDATE msgdiag_appender_prop SET valore='java:/comp/env/org.govway.datasource' WHERE nome='datasource' AND id_appender=(select id from msgdiag_appender where tipo='protocol');
-- UPDATE dump_appender_prop SET valore='java:/comp/env/org.govway.datasource' WHERE nome='datasource' AND id_appender=(select id from msgdiag_appender where tipo='protocol');
