-- Tracciamento
INSERT INTO tracce_appender (tipo) VALUES ('protocol');
INSERT INTO tracce_appender_prop (id_appender,nome,valore) VALUES ((select id from tracce_appender where tipo='protocol'),'datasource','org.openspcoop2.dataSource');
INSERT INTO tracce_appender_prop (id_appender,nome,valore) VALUES ((select id from tracce_appender where tipo='protocol'),'tipoDatabase','@TIPO_DATABASE@');
INSERT INTO tracce_appender_prop (id_appender,nome,valore) VALUES ((select id from tracce_appender where tipo='protocol'),'usePdDConnection','true'); 

-- MsgDiagnostici
INSERT INTO msgdiag_appender (tipo) VALUES ('protocol');
INSERT INTO msgdiag_appender_prop (id_appender,nome,valore) VALUES ((select id from msgdiag_appender where tipo='protocol'),'datasource','org.openspcoop2.dataSource');
INSERT INTO msgdiag_appender_prop (id_appender,nome,valore) VALUES ((select id from msgdiag_appender where tipo='protocol'),'usePdDConnection','true');

-- PdD
UPDATE pdd set nome='PdD@NOME_SOGGETTO@';
UPDATE pdd set superuser='@PDDCONSOLE_USERNAME@';

-- Configurazione Cache Registro
UPDATE configurazione set statocache='abilitato';
UPDATE configurazione set dimensionecache='10000';
UPDATE configurazione set algoritmocache='lru';
UPDATE configurazione set lifecache='7200';

-- Configurazione Cache Dati Configurazione
UPDATE configurazione set config_statocache='abilitato';
UPDATE configurazione set config_dimensionecache='10000';
UPDATE configurazione set config_algoritmocache='lru';
UPDATE configurazione set config_lifecache='7200';

-- Configurazione Cache Dati Autorizzazione
UPDATE configurazione set auth_statocache='abilitato';
UPDATE configurazione set auth_dimensionecache='5000';
UPDATE configurazione set auth_algoritmocache='lru';
UPDATE configurazione set auth_lifecache='7200';
