-- Le tracce, i dump ed i diagnostici sono inseriti con le transazioni
-- Se si abilita un appender, disabilitare il salvataggio associato alla transazione
-- govway.properties:
--   org.openspcoop2.pdd.transazioni.tracce.enabled
--   org.openspcoop2.pdd.transazioni.diagnostici.enabled
--   org.openspcoop2.pdd.transazioni.dump.enabled

-- Tracciamento
-- INSERT INTO tracce_appender (tipo) VALUES ('protocol');
-- INSERT INTO tracce_appender_prop (id_appender,nome,valore) VALUES ((select id from tracce_appender where tipo='protocol'),'datasource','org.govway.datasource');
-- INSERT INTO tracce_appender_prop (id_appender,nome,valore) VALUES ((select id from tracce_appender where tipo='protocol'),'tipoDatabase','@TIPO_DATABASE@');
-- INSERT INTO tracce_appender_prop (id_appender,nome,valore) VALUES ((select id from tracce_appender where tipo='protocol'),'usePdDConnection','true'); 

-- Dump
-- INSERT INTO dump_appender (tipo) VALUES ('protocol');
-- INSERT INTO dump_appender_prop (id_appender,nome,valore) VALUES ((select id from dump_appender where tipo='protocol'),'datasource','org.govway.datasource');
-- INSERT INTO dump_appender_prop (id_appender,nome,valore) VALUES ((select id from dump_appender where tipo='protocol'),'tipoDatabase','@TIPO_DATABASE@');
-- INSERT INTO dump_appender_prop (id_appender,nome,valore) VALUES ((select id from dump_appender where tipo='protocol'),'usePdDConnection','true');

-- MsgDiagnostici
-- INSERT INTO msgdiag_appender (tipo) VALUES ('protocol');
-- INSERT INTO msgdiag_appender_prop (id_appender,nome,valore) VALUES ((select id from msgdiag_appender where tipo='protocol'),'datasource','org.govway.datasource');
-- INSERT INTO msgdiag_appender_prop (id_appender,nome,valore) VALUES ((select id from msgdiag_appender where tipo='protocol'),'usePdDConnection','true');

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

-- Configurazione Cache Dati Autenticazione
UPDATE configurazione set authn_statocache='abilitato';
UPDATE configurazione set authn_dimensionecache='5000';
UPDATE configurazione set authn_algoritmocache='lru';
UPDATE configurazione set authn_lifecache='7200';

-- Configurazione Cache Dati GestioneToken
UPDATE configurazione set token_statocache='abilitato';
UPDATE configurazione set token_dimensionecache='5000';
UPDATE configurazione set token_algoritmocache='lru';
UPDATE configurazione set token_lifecache='600';

-- Configurazione Cache Dati Keystore
UPDATE configurazione set keystore_statocache='abilitato';
UPDATE configurazione set keystore_dimensionecache='1000';
UPDATE configurazione set keystore_algoritmocache='lru';
UPDATE configurazione set keystore_lifecache='7200';
UPDATE configurazione set keystore_crl_lifecache='1800';

-- Configurazione Cache Consegna Applicativi
UPDATE configurazione set consegna_statocache='abilitato';
UPDATE configurazione set consegna_dimensionecache='10000';
UPDATE configurazione set consegna_algoritmocache='lru';
UPDATE configurazione set consegna_lifecache='-1';

-- Configurazione Cache Dati Richieste
UPDATE configurazione set dati_richieste_statocache='abilitato';
UPDATE configurazione set dati_richieste_dimensionecache='15000';
UPDATE configurazione set dati_richieste_algoritmocache='lru';
-- usare un intervallo uguale a quello minimo impostato in registro, config e keystore (anche crl)
UPDATE configurazione set dati_richieste_lifecache='1800';

-- Configurazione CORS
UPDATE configurazione set cors_stato='abilitato';
UPDATE configurazione set cors_tipo='gateway';
UPDATE configurazione set cors_all_allow_origins='abilitato';
UPDATE configurazione set cors_allow_headers='Authorization,Content-Type,SOAPAction,Cache-Control';
UPDATE configurazione set cors_allow_methods='GET,PUT,POST,DELETE,PATCH';
UPDATE configurazione set cors_allow_max_age=1;
UPDATE configurazione set cors_allow_max_age_seconds=28800;

-- Configurazione Cache Response
UPDATE configurazione set response_cache_stato='disabilitato';

-- Configurazione Cache ResponseCache
UPDATE configurazione set response_cache_statocache='abilitato';
UPDATE configurazione set response_cache_dimensionecache='10000';
UPDATE configurazione set response_cache_algoritmocache='lru';
UPDATE configurazione set response_cache_lifecache='-1';

-- Configurazione Corrispondenza Profilo con Registro
UPDATE configurazione set validazione_profilo='abilitato';
-- Disabilito per default la validazione con schema xsd dei protocolli
UPDATE configurazione set validazione_controllo='normale';

-- MaxConnections di default
INSERT INTO pdd_sys_props (nome, valore) VALUES ('http.maxConnections','200');

-- Configurazione ControlloTraffico
UPDATE ct_config set max_threads_tipo_errore='http429';
UPDATE ct_config set rt_tipo_errore='http429';
