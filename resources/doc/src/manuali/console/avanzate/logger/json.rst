.. _configAvanzataLoggerJson:

Logging in formato JSON
---------------------------------------------------------------------

Le applicazioni GovWay generano log applicativi di debug, salvati nella directory di log specificata durante l'installazione (es. */var/log/govway*), come descritto nella sezione :ref:`configAvanzataLogger`.

È possibile abilitare la produzione dei log in formato json attraverso due differenti modalità:

- aggiungendo sul file di configurazione log4j2 della singola applicazione la proprietà 'option.json';
- definendo una tra le variabile di sistema o java descritte nella tabella :ref:`fileConfigLo4j2JsonTab`.
  
.. table:: Variabili per abilitare il logging in formato JSON
   :widths: auto
   :name: fileConfigLo4j2JsonTab

   ============================  ==============================
   Nome archivio Applicativo     Nome Variabile                                                         
   ============================  ==============================
   qualsiasi applicazione        GOVWAY_LOG_JSON
   govway.ear                    GOVWAY_RUN_LOG_JSON
   govwayConsole.war             GOVWAY_CONSOLE_LOG_JSON
   govwayMonitor.war             GOVWAY_MONITOR_LOG_JSON
   govwayAPIConfig.war           GOVWAY_API_CONFIG_LOG_JSON
   govwayAPIMonitor.war          GOVWAY_API_MONITOR_LOG_JSON
   ============================  ==============================

Il valore da assegnare alla proprietà 'option.json' o alle variabili sono:

- false (default) o true: per disabilitare o abilitare il formato json dei log;
- idLogger1,..,idLoggerN: è possibile indicare i nomi dei logger per cui si desidera abilitare puntualmente il formato json; si applica a tutti gli appenders connessi ai logger specificati.


GovWay genera un formato json per default utilizzando il template `JsonLayout.json <https://github.com/apache/logging-log4j2/tree/2.x/log4j-layout-template-json/src/main/resources/JsonLayout.json>`_ di log4j2.
È possibile personalizzare il template utilizzato scegliendone uno tra quelli disponibili in `event templates <https://logging.apache.org/log4j/2.x/manual/json-template-layout.html#event-templates>`_ o creandone uno personalizzato e indicando l'uri della risorsa del template (es. classpath:JsonLayout.json o file:/etc/govway/JsonLayout.json) attraverso due modalità:

- aggiungendo sul file di configurazione log4j2 della singola applicazione la proprietà 'option.json.template';
- definendo una tra le variabile di sistema o java descritte nella tabella :ref:`fileConfigLo4j2JsonTemplateTab`.
  
.. table:: Variabile che definisce il template utilizzato nel logging in formato JSON
   :widths: auto
   :name: fileConfigLo4j2JsonTemplateTab

   ============================  ======================================
   Nome archivio Applicativo     Nome Variabile                                                         
   ============================  ======================================
   qualsiasi applicazione        GOVWAY_LOG_JSON_TEMPLATE
   govway.ear                    GOVWAY_RUN_LOG_JSON_TEMPLATE
   govwayConsole.war             GOVWAY_CONSOLE_LOG_JSON_TEMPLATE
   govwayMonitor.war             GOVWAY_MONITOR_LOG_JSON_TEMPLATE
   govwayAPIConfig.war           GOVWAY_API_CONFIG_LOG_JSON_TEMPLATE
   govwayAPIMonitor.war          GOVWAY_API_MONITOR_LOG_JSON_TEMPLATE
   ============================  ======================================

