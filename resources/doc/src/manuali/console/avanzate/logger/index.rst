.. _configAvanzataLogger:

Logging Applicativo
---------------------------------------------------------------------

GovWay offre un sistema di tracciamento altamente flessibile, come descritto nella sezione :ref:`tracciamento`. Le tracce generate possono essere consultate tramite la console o le API di monitoraggio, consentendo analisi diagnostiche sulle richieste gestite. Oltre al tracciamento su database, è possibile attivare un tracciamento su file seguendo le istruzioni fornite nella sezione :ref:`tracciamentoTransazioniFileTrace`.

Oltre alle tracce, le applicazioni GovWay generano log applicativi di debug, salvati nella directory di log specificata durante l'installazione (es. */var/log/govway*). Di seguito sono riportate le configurazioni relative ai log applicativi di debug.

Il pacchetto software GovWay è composto da diversi archivi applicativi, ciascuno dei quali produce log distinti. I file di log vengono definiti all'interno di un file di configurazione log4j2, presente in ogni archivio, che specifica gli appender e i criteri di rotazione per ciascun file. Ogni proprietà di configurazione può essere modificata ridefinendola in un file locale, che può essere creato nella directory di lavoro specificata in fase di installazione (es. */etc/govway*).

La tabella seguente elenca, per ogni archivio applicativo, il file di configurazione log4j2 interno e il corrispondente file che può essere creato nella directory di lavoro per eventuali personalizzazioni.

.. table:: File di configurazione log4j2 delle applicazioni di GovWay
   :widths: auto
   :name: fileConfigLo4j2Tab

   ==========================================  ======================================================================  ==============================================
   Nome archivio Applicativo                   Configurazione log4j2                                                   File esterno per ridefinizione proprietà                                          
   ==========================================  ======================================================================  ==============================================
   govway.ear (wildfly)                        govway.ear/properties/govway.log4j2.properties                          govway_local.log4j2.properties
   govway.war (tomcat)                         govway.war/WEB-INF/classes/govway.log4j2.properties                     govway_local.log4j2.properties
   govwayConsole.war                           govwayConsole.war/WEB-INF/classes/console.log4j2.properties             console_local.log4j2.properties
   govwayConsole.war                           govwayConsole.war/WEB-INF/classes/console.audit.log4j2.properties       console_local.audit.log4j2.properties
   govwayMonitor.war                           govwayMonitor.war/WEB-INF/classes/monitor.log4j2.properties             monitor_local.log4j2.properties
   govwayAPIConfig.war                         govwayAPIConfig.war/WEB-INF/classes/rs-api-config.log4j2.properties     rs-api-config_local.log4j2.properties
   govwayAPIConfig.war                         govwayAPIConfig.war/WEB-INF/classes/console.audit.log4j2.properties     console_local.audit.log4j2.properties
   govwayAPIMonitor.war                        govwayAPIMonitor.war/WEB-INF/classes/rs-api-monitor.log4j2.properties   rs-api-monitor_local.log4j2.properties
   ==========================================  ======================================================================  ==============================================

.. note::
    All'interno degli archivi esistono degli ulteriori file di configurazione 'log4j2.properties' che possono essere ignorati; vengono sostituiti con i corrispettivi specifici nella fase di inizializzazione dell'applicazione.

.. toctree::
        :maxdepth: 2
        
	console
	clusterId
	json
