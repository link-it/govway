.. _avanzate_canaleIO_confNIO:

Configurazione I/O NIO
~~~~~~~~~~~~~~~~~~~~~~~~~

Tutti gli aspetti di configurazione relativi alla modalità di gestione dell'I/O in modalità NIO, descritti nella sezione :ref:`avanzate_canaleIO` possono essere definiti nel file <directory-lavoro>/govway_local.properties.

**Connection Pool**

Attraverso le proprietà riportate di seguito è possibile specificare:

- il numero massimo di connessioni per singola rotta;

- il numero massimo complessivo di connessioni attivabili verso i backend;

- definisce l’intervallo di tempo (in millisecondi) di inattività dopo il quale una connessione persistente deve essere convalidata prima di essere riutilizzata dal client sincrono. Questo parametro serve a prevenire il riutilizzo di connessioni che potrebbero essere state chiuse dal server o interrotte in modo silente durante l'inattività.

   ::

      # Maximum limit of connection on a per route basis
      org.openspcoop2.pdd.connettori.asyncClient.maxPerRoute=200
      # Maximum limit of connection on total
      org.openspcoop2.pdd.connettori.asyncClient.maxTotal=10000
      # Time interval (in milliseconds) after which idle persistent connections should be validated before reuse. Helps avoid using closed or stale connections.
      org.openspcoop2.pdd.connettori.asyncClient.validateAfterInactivity=2000

Oltre ad una configurazione generale è possibile impostare dei valori specifici per un server indicando l'hostname e/o la porta tramite le seguenti proprietà:

   ::

      # È possibile impostare un'impostazione specifica per un server indicando l'hostname e/o la porta e utilizzando uno dei seguenti nomi di proprietà:
      # - maxPerRoute
      # - maxTotal
      # - validateAfterInactivity
      org.openspcoop2.pdd.connettori.asyncClient.<nomeProprieta>.<hostname>.<port>=
      org.openspcoop2.pdd.connettori.asyncClient.<nomeProprieta>.<hostname>\:<port>=
      org.openspcoop2.pdd.connettori.asyncClient.<nomeProprieta>.<hostname>=

I valori definiti nel file <directory-lavoro>/govway_local.properties rappresentano la configurazione di default. È possibile utilizzare valori differenti sulla singola erogazione o fruizione registrando le seguenti :ref:`configProprieta`:

- *connettori.connection.pool.maxPerRoute*
- *connettori.connection.pool.maxTotal*
- connettori.connection.pool.validateAfterInactivity

**Gestione Connessioni Idle**

La chiusura di connessioni idle viene gestita tramite un thread dedicato che viene schedulato ogni minuto. Le connessioni che risultano in stato idle da più di 30 secondi vengono chiuse. Tutti questi aspetti possono essere personalizzati agendo sul file <directory-lavoro>/govway_local.properties e definendo le seguenti proprietà:

   ::

      # Close connections that have been idle longer than X sec
      # Set an empty value to disable the check.
      org.openspcoop2.pdd.connettori.asyncClient.closeIdleConnectionsAfterSeconds=30

      # A check is performed at intervals of X seconds.
      org.openspcoop2.pdd.connettori.asyncClient.closeIdleConnectionsCheckIntervalSeconds=60
      # The status of the connection pool is recorded in the 'govway_connettori.log' file.
      org.openspcoop2.pdd.connettori.asyncClient.closeIdleConnections.debug=true

Oltre alla chiusura delle connessioni inattive (idle), il sistema gestisce anche la scadenza e il rilascio delle connessioni non più utilizzate nel tempo. Questo meccanismo è utile per liberare risorse e prevenire l’accumulo di client non più attivi, pur garantendo un margine temporale per eventuali richiami tardivi dovuti, ad esempio, a read timeout o a una lenta elaborazione dei dati.

La configurazione si basa su due parametri distinti:

- expireUnusedAfterSeconds: specifica l'intervallo di tempo (in secondi) oltre il quale un client asincrono viene considerato scaduto se non è stato utilizzato (valore predefinito: 300 secondi);
- closeUnusedAfterSeconds: indica dopo quanto tempo (in secondi) una connessione scaduta viene effettivamente chiusa e rimossa dal pool; questo ritardo consente ad eventuali thread ancora in esecuzione di completare l’elaborazione o rilevare eventuali errori (valore predefinito: 900 secondi).

   ::

      # Expire client that have been unused longer than X sec
      org.openspcoop2.pdd.connettori.asyncClient.expireUnusedAfterSeconds=300
      # Close client that have been unused longer than X sec
      org.openspcoop2.pdd.connettori.asyncClient.closeUnusedAfterSeconds=900

**Configurazione dei Thread del Client NIO**

l client HTTP asincrono utilizzato da GovWay nella modalità NIO è basato sulla libreria: org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient.

Questa implementazione utilizza un componente interno denominato IOReactor, responsabile della gestione non bloccante delle operazioni di I/O su socket. La seguente proprietà consente di definire il numero di thread che concorrono a realizzare il componente:

   ::

      org.openspcoop2.pdd.connettori.asyncRequest.httpclient.ioreactor.threadCount=NumeroIntero

Nella configurazione di default la proprietà non viene definita e il numero di thread è automaticamente impostato al numero di core CPU disponibili sulla macchina (valore restituito da Runtime.getRuntime().availableProcessors()). In ambienti ad alta concorrenza, dove molte connessioni simultanee vengono aperte verso i backend, può essere utile un tuning esplicito di questo parametro in base al carico osservato in modo da utilizzare un valore inferiore al numero dei processori disponibili.

.. note::
      Da documentazione della libreria `Apache HttpClient 5 <https://hc.apache.org/httpcomponents-client-5.5.x/index.html>`_ l’impostazione di un valore superiore al numero di core disponibili non sembra comportare vantaggi, e potrebbe introdurre overhead. 

**Configurazione Thread Pool Asincrono (Richieste & Risposte)**

Come descritto nella sezione :ref:`avanzate_canaleIO`, le richieste in modalità NIO vengono ricevute tramite un servlet HTTP, ma la gestione del ciclo richiesta–risposta è disaccoppiata tramite l’uso dell’oggetto jakarta.servlet.AsyncContext. Il thread del container viene liberato immediatamente e la continuazione della gestione viene delegata a thread applicativi provenienti da un pool dedicato.

Nella configurazione di default sono previsti due pool distinti:

- Pool richieste: gestisce l’elaborazione delle richieste in ingresso.
- Pool risposte: si occupa della fase di invio della risposta verso il client.

Entrambi i pool sono inizialmente configurati con una dimensione di 100 thread.

La dimensione dei pool può essere personalizzata attraverso le seguenti proprietà di configurazione:

   ::

      # request-nio
      org.openspcoop2.pdd.connettori.asyncThreadPool.executor.request-nio.size=100
      # response-nio
      org.openspcoop2.pdd.connettori.asyncThreadPool.executor.response-nio.size=100

.. note::
      Un dimensionamento non adeguato può influire sulle prestazioni, soprattutto in presenza di carichi elevati o backend lenti a rispondere. È consigliato effettuare tuning in base al profiling applicativo e al carico previsto.

*Configurazione Avanzata dei Thread Pool Asincroni per Fruizioni ed Erogazioni*

Oltre alla configurazione base dei thread pool per la gestione asincrona delle richieste e delle risposte, è possibile definire pool separati e personalizzati per le diverse fasi del flusso I/O in modalità stream. Questo consente una gestione più granulare e ottimizzata delle risorse in scenari complessi o ad alto carico.

È possibile specificare un identificativo (threadPool id) per ciascuna delle seguenti fasi:

   ::

      # - inRequest (erogazioni)
      org.openspcoop2.pdd.connettori.asyncThreadPool.inRequest=<idThreadPool>
      # - outResponse (erogazioni)
      org.openspcoop2.pdd.connettori.asyncThreadPool.outResponse=<idThreadPool>
      # - outRequest (fruizioni)
      org.openspcoop2.pdd.connettori.asyncThreadPool.outRequest=<idThreadPool>
      # - inResponse (fruizioni)
      org.openspcoop2.pdd.connettori.asyncThreadPool.inResponse=<idThreadPool>

Ogni threadPool id utilizzato in queste proprietà deve essere definito esplicitamente tramite la seguente configurazione:

   ::

      org.openspcoop2.pdd.connettori.asyncThreadPool.<id>.size=dimensione del thread executor
