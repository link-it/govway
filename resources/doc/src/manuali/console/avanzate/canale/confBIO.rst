.. _avanzate_canaleIO_confBIO:

Configurazione I/O BIO
~~~~~~~~~~~~~~~~~~~~~~~~~

Tutti gli aspetti di configurazione relativi alla modalità di gestione dell'I/O in modalità BIO, descritti nella sezione :ref:`avanzate_canaleIO` possono essere definiti nel file <directory-lavoro>/govway_local.properties.

**Connection Pool**

Attraverso le proprietà riportate di seguito è possibile specificare:

- il numero massimo di connessioni per singola rotta;

- il numero massimo complessivo di connessioni attivabili verso i backend;

- definisce l’intervallo di tempo (in millisecondi) di inattività dopo il quale una connessione persistente deve essere convalidata prima di essere riutilizzata dal client sincrono. Questo parametro serve a prevenire il riutilizzo di connessioni che potrebbero essere state chiuse dal server o interrotte in modo silente durante l'inattività.

   ::

      # Maximum limit of connection on a per route basis
      org.openspcoop2.pdd.connettori.syncClient.maxPerRoute=200
      # Maximum limit of connection on total
      org.openspcoop2.pdd.connettori.syncClient.maxTotal=10000
      # Time interval (in milliseconds) after which idle persistent connections should be validated before reuse. Helps avoid using closed or stale connections.
      org.openspcoop2.pdd.connettori.syncClient.validateAfterInactivity=2000

Oltre ad una configurazione generale è possibile impostare dei valori specifici per un server indicando l'hostname e/o la porta tramite le seguenti proprietà:

   ::

      # È possibile impostare un'impostazione specifica per un server indicando l'hostname e/o la porta e utilizzando uno dei seguenti nomi di proprietà:
      # - maxPerRoute
      # - maxTotal
      # - validateAfterInactivity
      org.openspcoop2.pdd.connettori.syncClient.<nomeProprieta>.<hostname>.<port>=
      org.openspcoop2.pdd.connettori.syncClient.<nomeProprieta>.<hostname>\:<port>=
      org.openspcoop2.pdd.connettori.syncClient.<nomeProprieta>.<hostname>=

I valori definiti nel file <directory-lavoro>/govway_local.properties rappresentano la configurazione di default. È possibile utilizzare valori differenti sulla singola erogazione o fruizione registrando le seguenti :ref:`configProprieta`:

- *connettori.connection.pool.maxPerRoute*
- *connettori.connection.pool.maxTotal*
- connettori.connection.pool.validateAfterInactivity

**Gestione Connessioni Idle**

La chiusura di connessioni idle viene gestita tramite un thread dedicato che viene schedulato ogni minuto. Le connessioni che risultano in stato idle da più di 30 secondi vengono chiuse. Tutti questi aspetti possono essere personalizzati agendo sul file <directory-lavoro>/govway_local.properties e definendo le seguenti proprietà:

   ::

      # Close connections that have been idle longer than X sec
      # Set an empty value to disable the check.
      org.openspcoop2.pdd.connettori.syncClient.closeIdleConnectionsAfterSeconds=30

      # A check is performed at intervals of X seconds.
      org.openspcoop2.pdd.connettori.syncClient.closeIdleConnectionsCheckIntervalSeconds=60
      # The status of the connection pool is recorded in the 'govway_connettori.log' file.
      org.openspcoop2.pdd.connettori.syncClient.closeIdleConnections.debug=true


