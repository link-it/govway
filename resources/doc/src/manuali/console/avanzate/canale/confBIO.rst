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

**Limiti sugli header HTTP della risposta**

Durante la lettura degli header HTTP della risposta ricevuta dal backend, il connettore applica due limiti:

- *maxHeaderLineLength*: dimensione massima di un singolo header HTTP nella forma 'Nome: valore', indicata in bytes (valore predefinito: 65536 bytes, ovvero 64 KB). Non si tratta quindi della dimensione complessiva di tutti gli header, ma del limite applicato ad ogni singolo header; il medesimo limite viene applicato anche alla riga di stato della risposta, es. 'HTTP/1.1 200 OK';

- *maxHeaderCount*: numero massimo di header HTTP presenti nella risposta (valore predefinito: 250).

Al superamento di uno dei due limiti la risposta non viene elaborata e la transazione termina con un errore di connettore, veicolato al client con l'errore 'APIUnavailable'. Un caso tipico di header di dimensioni rilevanti è quello dei profili di sicurezza messaggio ModI, dove un singolo header può veicolare un token JWT contenente il certificato X.509 del firmatario.

I limiti sono personalizzabili agendo sul file <directory-lavoro>/govway_local.properties e definendo le seguenti proprietà:

   ::

      # Dimensione massima, in bytes, di un singolo header HTTP nella forma 'Nome: valore'
      # (il medesimo limite viene applicato anche alla riga di stato della risposta).
      # Indicare il valore 0, o un valore negativo, per disabilitare il controllo.
      org.openspcoop2.pdd.connettori.syncClient.http1.maxHeaderLineLength=65536
      # Numero massimo di header HTTP ammessi nella risposta.
      # Indicare il valore 0, o un valore negativo, per disabilitare il controllo.
      org.openspcoop2.pdd.connettori.syncClient.http1.maxHeaderCount=250

.. note::
      I limiti riguardano il solo protocollo HTTP/1.1, l'unico utilizzato dal client sincrono (BIO): nella libreria Apache HttpClient 5 il supporto all'HTTP/2 è realizzato esclusivamente sul trasporto non bloccante, poiché la multiplazione di più stream sulla medesima connessione non è compatibile con il modello di I/O bloccante. Per i limiti applicati sulle connessioni HTTP/2 si rimanda quindi alla sezione :ref:`avanzate_canaleIO_confNIO`.


