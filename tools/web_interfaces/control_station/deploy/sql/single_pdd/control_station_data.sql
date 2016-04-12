-- Configurazione
INSERT INTO configurazione (cadenza_inoltro, validazione_stato, validazione_controllo, msg_diag_severita, msg_diag_severita_log4j, auth_integration_manager,validazione_profilo, mod_risposta, indirizzo_telematico, routing_enabled, validazione_manifest, gestione_manifest, tracciamento_buste, tracciamento_dump, tracciamento_dump_bin_pd, tracciamento_dump_bin_pa, statocache,dimensionecache,algoritmocache,lifecache, config_statocache,config_dimensionecache,config_algoritmocache,config_lifecache,auth_statocache,auth_dimensionecache,auth_algoritmocache,auth_lifecache) VALUES( '60',       'abilitato',    'rigido', 'infoIntegration', 'infoIntegration', 'basic,ssl', 'disabilitato','reply','disabilitato','disabilitato', 'abilitato', 'disabilitato', 'abilitato', 'disabilitato', 'disabilitato', 'disabilitato', 'abilitato','10000','lru','7200','abilitato','10000','lru','7200','abilitato','5000','lru','7200');

-- Rotta di default per routing
insert INTO routing (tiporotta,registrorotta,is_default) VALUES ('registro',0,1);

-- Registro locale
insert INTO registri (nome,location,tipo) VALUES ('RegistroDB','org.openspcoop2.dataSource.pddConsole','db');

-- Porta di Dominio locale
INSERT INTO pdd (nome,tipo,superuser) VALUES ('PddOpenSPCoop','operativo','amministratore');
