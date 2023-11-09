-- Configurazione
INSERT INTO configurazione (cadenza_inoltro, validazione_stato, validazione_controllo, msg_diag_severita, msg_diag_severita_log4j, auth_integration_manager,validazione_profilo, mod_risposta, indirizzo_telematico, routing_enabled, validazione_manifest, gestione_manifest, tracciamento_buste, dump, dump_bin_pd, dump_bin_pa, statocache,dimensionecache,algoritmocache,lifecache, config_statocache, config_dimensionecache, config_algoritmocache, config_lifecache, auth_statocache, auth_dimensionecache, auth_algoritmocache, auth_lifecache, authn_statocache, authn_dimensionecache, authn_algoritmocache, authn_lifecache, token_statocache, token_dimensionecache, token_algoritmocache, token_lifecache, aa_statocache, aa_dimensionecache, aa_algoritmocache, aa_lifecache, cors_stato, cors_tipo, cors_all_allow_origins, cors_allow_headers, cors_allow_methods, response_cache_stato, response_cache_statocache, response_cache_dimensionecache, response_cache_algoritmocache, response_cache_lifecache) VALUES( '60',       'abilitato',    'rigido', 'infoIntegration', 'infoIntegration', 'principal,ssl,basic', 'disabilitato','reply','disabilitato','disabilitato', 'abilitato', 'disabilitato', 'abilitato', 'disabilitato', 'disabilitato', 'disabilitato', 'abilitato','10000','lru','7200','abilitato','10000','lru','7200','abilitato','5000','lru','7200','abilitato','5000','lru','7200', 'abilitato','5000','lru','600','abilitato','5000','lru','7200', 'abilitato','gateway', 'abilitato', 'Authorization,Content-Type,SOAPAction','GET,PUT,POST,DELETE,PATCH','disabilitato','abilitato','10000','lru','1800');

-- Configurazione Controllo Traffico
INSERT INTO ct_config (max_threads,pd_connection_timeout,pd_read_timeout,pd_avg_time,pa_connection_timeout,pa_read_timeout,pa_avg_time,cache_size,cache_algorithm,cache_life_time) VALUES (200,10000,150000,10000,10000,120000,10000,10000,'LRU',300);

-- Init Configurazione URL Invocazione
INSERT INTO config_url_invocazione (base_url) VALUES ('http://localhost:8080/govway/');

-- Rotta di default per routing
insert INTO routing (tiporotta,registrorotta,is_default) VALUES ('registro',0,1);

-- Registro locale
insert INTO registri (nome,location,tipo) VALUES ('RegistroDB','org.govway.datasource.console','db');

-- Porta di Dominio locale
INSERT INTO pdd (nome,tipo,superuser) VALUES ('GovWay','operativo','amministratore');
