Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- Aggiunto supporto per wildfly 39.

- Aggiornati script SQL per compatibilità con MySQL 8.0+ (rimosso sql_mode 'NO_AUTO_CREATE_USER').

- Introdotta gestione delle variabili GOVWAY_CONF, GOVWAY_LOG, GOVWAY_DB_TYPE e GOVWAY_DEFAULT_ENTITY_NAME.

- Corretta la generazione dello script SQL 'GovWayTracciamento.sql' che, nella modalità di installazione avanzata con database dedicato al tracciamento, non includeva la creazione della tabella 'OP2_SEMAPHORE'.
