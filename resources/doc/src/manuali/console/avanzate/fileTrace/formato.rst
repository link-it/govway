.. _avanzate_fileTrace_format:

Configurazione dei Topic
-------------------------

Le informazioni inerenti le comunicazioni gestite dal gateway possono essere riversate in uno o più file di log attraverso la definizione di topic.

La configurazione permette di indicare uno o più topic da generare quando il gateway gestisce erogazioni o fruizioni di API. Nell'esempio seguente vengono registrati due topic sulle erogazioni, dove si vuole salvere le informazioni suddividendole tra richiesta e la risposta. Per quanto concerne la registrazione delle fruizioni si attiva invece solamente un unico topic.

   ::

      # Topic
      topic.erogazioni=inputRequest,inputResponse
      topic.fruizioni=output

Per default tutte le comunicazioni gestite dal gateway vengono veicolati nei topic registrati. È possibile escludere il riversamento nel topic di determinate comunicazioni tramite le seguenti proprietà:

- *log.topic.<erogazioni/fruizioni>.<nomeTopic>.requestSended* : se abilita, sul topic indicato verranno riversate solamente informazioni relative a comunicazioni per le quali il gateway è riuscito a spedire la richiesta verso il backend configurato;

- *log.topic.<erogazioni/fruizioni>.<nomeTopic>.[in/out>][Request/Response]ContentDefined* : se abilitata, verranno riversate informazioni sul topic solo se la richiesta o risposta indicata, in ingresso o uscita dal gateway, possiede un payload http.

Nell'esempio seguente il topic relativo alle fruizioni viene alimentato solamente se il gateway è riuscito a contattare il backend e la richiesta possedeva un payload http (vengono escluse ad esempio le HTTP GET).
Sui topic delle erogazioni viene invece attivato solamente il controllo sul payload http per il topic 'inputRequest'.

   ::

      # Erogazioni (Filtro per Payload HTTP)
      topic.erogazioni.inputRequest.inRequestContentDefined=true

      # Fruizioni (Filtro per RequestSended + Payload HTTP)
      topic.fruizioni.output.requestSended=true
      topic.fruizioni.output.outRequestContentDefined=true

La generazione dei file di log è gestita dalle seguenti proprietà:

- *log.config.file* : file contenente la configurazione *log4j2* nella quale devono essere definite le Category da associare ad ogni topic;

- *log.severity* (default: info): indica il livello di severità (trace/debug/info/warn/error) utilizzato durante il logging;

- *log.topic.<erogazioni/fruizioni>.<nomeTopic>=<categoryLog4j2>* : assegna la category al topic indicato per le erogazioni o fruizioni.

Nell'esempio seguente viene fornito un esempio di associazione di Category ad ogni topic e un estratto di configurazione log4j2 nella quale viene creato un file per ogni category.

   ::

      # Log4j2 Configuration File
      log.config.file=govway.fileTrace.log4j2.properties

      # trace/debug/info/warn/error
      log.severity=info

      # Category per ogni topic delle erogazioni
      category.topic.erogazioni.inputRequest=fileTrace.inputRequest
      category.topic.erogazioni.inputResponse=fileTrace.inputResponse
      # Category per ogni topic delle fruizioni
      # sintassi: log.topic.fruizioni.<nomeTopic>=<categoryLog4j2>
      category.topic.fruizioni.output=fileTrace.output

Estratto della configurazione Log4J2 dove per ogni category viene attivata una rotazione giornaliera:

   ::

      name = fileTracePropertiesConfig 

      # ** inputRequest **
      # Category
      logger.fileTrace_inputRequest.name = fileTrace.inputRequest
      logger.fileTrace_inputRequest.level = DEBUG
      logger.fileTrace_inputRequest.additivity = false
      logger.fileTrace_inputRequest.appenderRef.rolling.ref = fileTrace.inputRequest.rollingFile
      # FileAppender
      appender.fileTrace_inputRequest.type = RollingFile
      appender.fileTrace_inputRequest.name = fileTrace.inputRequest.rollingFile
      appender.fileTrace_inputRequest.fileName = /var/govway/log/fileTrace/inputRequest.log
      appender.fileTrace_inputRequest.filePattern = /var/govway/log/fileTrace/$${date:yyyy-MM}/inputRequest-%d{MM-dd-yyyy}.log.gz
      appender.fileTrace_inputRequest.layout.type = PatternLayout
      appender.fileTrace_inputRequest.layout.pattern = %m%n
      appender.fileTrace_inputRequest.policies.type = Policies
      appender.fileTrace_inputRequest.policies.time.type = TimeBasedTriggeringPolicy
      appender.fileTrace_inputRequest.strategy.type = DefaultRolloverStrategy

      # ** inputResponse** 
      # Category
      logger.fileTrace_inputResponse.name = fileTrace.inputResponse
      logger.fileTrace_inputResponse.level = DEBUG
      logger.fileTrace_inputResponse.additivity = false
      logger.fileTrace_inputResponse.appenderRef.rolling.ref = fileTrace.inputResponse.rollingFile
      # FileAppender
      appender.fileTrace_inputResponse.type = RollingFile
      appender.fileTrace_inputResponse.name = fileTrace.inputResponse.rollingFile
      appender.fileTrace_inputResponse.fileName = /var/govway/log/fileTrace/inputResponse.log
      appender.fileTrace_inputResponse.filePattern = /var/govway/log/fileTrace/$${date:yyyy-MM}/inputResponse-%d{MM-dd-yyyy}.log.gz
      appender.fileTrace_inputResponse.layout.type = PatternLayout
      appender.fileTrace_inputResponse.layout.pattern = %m%n
      appender.fileTrace_inputResponse.policies.type = Policies
      appender.fileTrace_inputResponse.policies.time.type = TimeBasedTriggeringPolicy
      appender.fileTrace_inputResponse.strategy.type = DefaultRolloverStrategy

      # ** output **
      # Category
      logger.fileTrace_output.name = fileTrace.output
      logger.fileTrace_output.level = DEBUG
      logger.fileTrace_output.additivity = false
      logger.fileTrace_output.appenderRef.rolling.ref = fileTrace.output.rollingFile
      # FileAppender
      appender.fileTrace_output.type = RollingFile
      appender.fileTrace_output.name = fileTrace.output.rollingFile
      appender.fileTrace_output.fileName = /var/govway/log/fileTrace/output.log
      appender.fileTrace_output.filePattern = /var/govway/log/fileTrace/$${date:yyyy-MM}/output-%d{MM-dd-yyyy}.log.gz
      appender.fileTrace_output.layout.type = PatternLayout
      appender.fileTrace_output.layout.pattern = %m%n
      appender.fileTrace_output.policies.type = Policies
      appender.fileTrace_output.policies.time.type = TimeBasedTriggeringPolicy
      appender.fileTrace_output.strategy.type = DefaultRolloverStrategy


Per ogni topic non rimane che definire le informazioni che si desidera tracciare attraverso la proprietà '*format.topic.<erogazioni/fruizioni>.<nomeTopic>*'. Le informazioni possono essere definite attraverso costanti o tramite quanto indicato nella sezione :ref:`avanzate_fileTrace_info`.

Di seguito un esempio:

   ::

      format.topic.erogazioni.inputRequest="req"|"${log:transactionId}"|"govway"|"${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"|"${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"
      format.topic.erogazioni.inputResponse="res"|"${log:transactionId}"|"govway"|"${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"|"${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"|"${log:outHttpStatus}"
      format.topic.fruizioni.output="output"|"${log:transactionId}"|"govway"|"${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"|"${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"|"${log:inHttpStatus}"
      
Le informazioni prodotte ad esempio per il topic inputRequest saranno le seguenti:

   ::

      "req"|"b6cdd758-342c-4599-ae95-33a781730b3f"|"govway"|"2020-06-26 12:46:50:629"|"+0200"|"192.168.1.2"|"HTTP/1.1"|"POST"
      "req"|"2a9dc253-9dd5-458b-8689-edee7c9ba139"|"govway"|"2020-06-26 12:47:50:561"|"+0200"|"192.168.1.2"|"HTTP/1.1"|"POST"
      "req"|"eeedb92b-66b5-451e-8266-ade2cf1f34ce"|"govway"|"2020-06-26 12:47:53:291"|"+0200"|"192.168.1.19"|"HTTP/1.1"|"POST"
      "req"|"b4355a45-71cc-4293-b3b7-a4622af8ea84"|"govway"|"2020-06-26 12:48:00:102"|"+0200"|"192.168.1.22"|"HTTP/1.1"|"POST"


Nell'esempio appena riportato si può notare come i 3 topic utilizzano una parte comune. È possibile ottimizzare le informazioni configurate attraverso la definizione di proprietà '*format.property.<posizione>.<nomeProprietà>=<valoreProprietà>*'. Le proprietà verranno risolte in ordine lessicografico rispetto alla posizione indicata, in modo da garantire la corretta risoluzione se si hanno proprietà che sono definite tramite altre proprietà.

Di seguito il precedente esempio ridefinto tramite proprietà:

   ::

      # properties
      format.property.001.commons.govway-id=govway
      format.property.001.commons.id="${log:transactionId}"|"${log:property(commons.govway-id)}"
      format.property.002.commons.data="${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"
      format.property.003.commons.remoteIP-protocol-method="${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"
      format.property.004.commons=${log:property(commons.id)}|${log:property(commons.data)}|${log:property(commons.remoteIP-protocol-method)}

      # topic
      format.topic.erogazioni.inputRequest="req"|${log:property(commons)}
      format.topic.erogazioni.inputResponse="res"|${log:property(commons)}|"${log:outHttpStatus}"
      format.topic.fruizioni.output="output"|${log:property(commons)}|"${log:inHttpStatus}"

È infine possibile definire l'escape di caratteri che possono essere presenti nelle informazioni da tracciare tramite la proprietà '*format.escape.<char>=<charEscaped>*'.

Di seguito un esempio di configurazione che effettua l'escape del carattere '\\"' sostituendolo con '\\\\"':

   ::

      format.escape."=\\"


.. note::
      Anche se la configurazione viene modificata, non sarà utilizzata dal Gateway fino ad un suo riavvio. È possibile forzare la rilettura immediata accendendo alla voce 'Strumenti - Runtime' della console di gestione e selezionando 'Aggiorna la configurazione' nella sezione "Informazioni Tracciamento - File Trace' (:numref:`UpdateFileTrace`)".

   .. figure:: ../../_figure_console/UpdateFileTrace.png
    :scale: 70%
    :align: center
    :name: UpdateFileTrace

    Aggiornamento della Configurazione di File Trace
