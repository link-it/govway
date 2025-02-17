.. _configAvanzataLoggerConsole:

Logging su Console
---------------------------------------------------------------------

Le applicazioni GovWay generano log applicativi di debug, salvati nella directory di log specificata durante l'installazione (es. */var/log/govway*), come descritto nella sezione :ref:`configAvanzataLogger`.

È possibile abilitare anche il logging su console attraverso due differenti modalità:

- aggiungendo sul file di configurazione log4j2 della singola applicazione la proprietà 'option.stdout';
- definendo una tra le variabile di sistema o java descritte nella tabella :ref:`fileConfigLo4j2ConsoleTab`.
  
.. table:: Variabili per abilitare il logging su console
   :widths: auto
   :name: fileConfigLo4j2ConsoleTab

   ============================  ==============================
   Nome archivio Applicativo     Nome Variabile                                                         
   ============================  ==============================
   qualsiasi applicazione        GOVWAY_LOG_STDOUT
   govway.ear                    GOVWAY_RUN_LOG_STDOUT
   govwayConsole.war             GOVWAY_CONSOLE_LOG_STDOUT
   govwayMonitor.war             GOVWAY_MONITOR_LOG_STDOUT
   govwayAPIConfig.war           GOVWAY_API_CONFIG_LOG_STDOUT
   govwayAPIMonitor.war          GOVWAY_API_MONITOR_LOG_STDOUT
   ============================  ==============================

Il valore da assegnare alla proprietà 'option.stdout' o alle variabili sono:

- false (default) o true: per disabilitare o abilitare i log su console;
- idLogger1,..,idLoggerN: è possibile indicare i nomi dei logger per cui si desidera abilitare puntualmente il logging su console.

Il log emesso su console per default, a differenza dei log salvati su file, presentano un prefisso che riporta il nome dell'applicazione e il nome del logger. Il formato utilizzato viene definito nel console appender 'STDOUT' presente in tutti i file di configurazione \*.log4j2.properties descritti nella sezione :ref:`configAvanzataLogger`.
