.. _configAvanzataLoggerClusterId:

Logging con condivisione del filesystem su ambienti in cluster
---------------------------------------------------------------------

Le applicazioni GovWay generano log applicativi di debug, salvati nella directory di log specificata durante l'installazione (es. */var/log/govway*), come descritto nella sezione :ref:`configAvanzataLogger`.

Per consentire la condivisione dello stesso filesystem tra diverse istanze, è necessario ridefinire i file di configurazione log4j2, aggiungendo nel percorso di log una variabile che identifichi in modo univoco ciascuna istanza. 

GovWay dispone di alcune configurazioni built-in che consentono la modifica del percorso aggiungendo un identificativo del cluster al percorso originale definito nella configurazione. È possibile abilitare la configurazione built-in attraverso due differenti modalità:

- aggiungendo sul file di configurazione log4j2 della singola applicazione la proprietà 'option.clusterId';
- definendo una tra le variabile di sistema o java descritte nella tabella :ref:`fileConfigLo4j2ClusterIdTab`.
  
.. table:: Aggiunta dell'identificativo del nodo nel percorso di log 
   :widths: auto
   :name: fileConfigLo4j2ClusterIdTab

   ============================  =================================
   Nome archivio Applicativo     Nome Variabile                                                         
   ============================  =================================
   qualsiasi applicazione        GOVWAY_LOG_CLUSTER_ID
   govway.ear                    GOVWAY_RUN_LOG_CLUSTER_ID
   govwayConsole.war             GOVWAY_CONSOLE_LOG_CLUSTER_ID
   govwayMonitor.war             GOVWAY_MONITOR_LOG_CLUSTER_ID
   govwayAPIConfig.war           GOVWAY_API_CONFIG_LOG_CLUSTER_ID
   govwayAPIMonitor.war          GOVWAY_API_MONITOR_LOG_CLUSTER_ID
   ============================  =================================

Il valore da assegnare alla proprietà 'option.clusterId' o alle variabili sono:

- false (default) o true: per disabilitare o abilitare l'aggiunta dell'identificativo del nodo;
- idLogger1,..,idLoggerN: è possibile indicare i nomi dei logger per cui si desidera abilitare puntualmente la configurazione.


Come identificativo del nodo viene utilizzato per default la variabile d'ambiente o java 'HOSTNAME'.
È possibile personalizzare la variabile utilizzata, indicando un nome differente attraverso le due modalità:

- aggiungendo sul file di configurazione log4j2 della singola applicazione la proprietà 'option.env';
- definendo una tra le variabile di sistema o java descritte nella tabella :ref:`fileConfigLo4j2ClusterIdEnvTab`.
  
.. table:: Variabile che definisce l'identificativo del nodo utilizzato nel percorso di log 
   :widths: auto
   :name: fileConfigLo4j2ClusterIdEnvTab

   ============================  =======================================
   Nome archivio Applicativo     Nome Variabile                                                         
   ============================  =======================================
   qualsiasi applicazione        GOVWAY_LOG_CLUSTER_ID_ENV
   govway.ear                    GOVWAY_RUN_LOG_CLUSTER_ID_ENV
   govwayConsole.war             GOVWAY_CONSOLE_LOG_CLUSTER_ID_ENV
   govwayMonitor.war             GOVWAY_MONITOR_LOG_CLUSTER_ID_ENV
   govwayAPIConfig.war           GOVWAY_API_CONFIG_LOG_CLUSTER_ID_ENV
   govwayAPIMonitor.war          GOVWAY_API_MONITOR_LOG_CLUSTER_ID_ENV
   ============================  =======================================

Infine abilitando la configurazione, è possibile personalizzare la posizione dove viene aggiunto l'identificativo nel percorso di log attraverso le due modalità:

- aggiungendo sul file di configurazione log4j2 della singola applicazione la proprietà 'option.clusterId.strategy';
- definendo una tra le variabile di sistema o java descritte nella tabella :ref:`fileConfigLo4j2ClusterIdStrategyTab`.
  
.. table:: Posizione dell'identificativo del nodo nel percorso di log 
   :widths: auto
   :name: fileConfigLo4j2ClusterIdStrategyTab

   ============================  ==========================================
   Nome archivio Applicativo     Nome Variabile                                                         
   ============================  ==========================================
   qualsiasi applicazione        GOVWAY_LOG_CLUSTER_ID_STRATEGY
   govway.ear                    GOVWAY_RUN_LOG_CLUSTER_ID_STRATEGY
   govwayConsole.war             GOVWAY_CONSOLE_LOG_CLUSTER_ID_STRATEGY
   govwayMonitor.war             GOVWAY_MONITOR_LOG_CLUSTER_ID_STRATEGY
   govwayAPIConfig.war           GOVWAY_API_CONFIG_LOG_CLUSTER_ID_STRATEGY
   govwayAPIMonitor.war          GOVWAY_API_MONITOR_LOG_CLUSTER_ID_STRATEGY
   ============================  ==========================================

Il valore da assegnare alla proprietà 'option.clusterId.strategy' o alle variabili sono:

- directory (default): il file di log verra inserito all'interno di una sotto-directory che riporta l'identificativo del nodo;
- fileName: il nome del file di log verrà arricchito dell'identificativo del nodo che verrà aggiunto subito prima dell'estensione '.log'.
