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

-- Configurazione
UPDATE configurazione set statocache='abilitato';
UPDATE configurazione set dimensionecache='10000';
UPDATE configurazione set algoritmocache='lru';
UPDATE configurazione set lifecache='7200';

