.. _osservabilitaConfigurazione:

Configurazione
~~~~~~~~~~~~~~~

La configurazione dell'osservabilità risiede nel file
*<directory-lavoro>/govway.observability.properties*. Il file è ridefinibile localmente tramite
*govway_local.observability.properties* e le proprietà vengono validate all'avvio del gateway:
una configurazione non valida impedisce l'avvio (*fail-fast*).

Collettori
^^^^^^^^^^

Un *collettore* rappresenta una destinazione delle metriche. È un'entità *signal-agnostic*: la
tipologia (``type``) è comune, mentre ogni segnale (per ora ``metrics``) ha la propria
configurazione. L'elenco dei collettori attivi è indicato in:

.. code-block:: properties

   # Elenco dei collettori attivi (nomi separati da virgola)
   observability.collectors=prometheus

Per ciascun collettore ``<name>`` si definisce il tipo e la configurazione del segnale ``metrics``:

.. code-block:: properties

   observability.collector.<name>.type=(prometheus|otel)
   observability.collector.<name>.metrics.enabled=(true|false)
   observability.collector.<name>.metrics.endpoint=<endpoint>

È possibile definire dei **default per segnale**, validi per tutti i collettori, omettendo il
segmento ``collector.<name>``:

.. code-block:: properties

   # default applicato a tutti i collettori
   observability.metrics.enabled=true

I valori specifici del collettore (``observability.collector.<name>.<chiave>``) hanno la
precedenza sui default (``observability.<chiave>``).

Vincoli:

- è ammesso **al massimo un collettore di tipo** ``prometheus`` (l'endpoint di scrape è unico);
- se ``metrics.enabled=true``, l'``endpoint`` è obbligatorio;
- per i collettori ``otel`` (push) è obbligatorio anche lo ``stepS`` (intervallo di invio).

Tutti i collettori con il segnale ``metrics`` abilitato condividono gli stessi meter (tramite un
*CompositeMeterRegistry*): la medesima misura viene quindi pubblicata su tutte le destinazioni
configurate.

Collettore Prometheus (pull)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: properties

   observability.collector.prometheus.type=prometheus
   observability.collector.prometheus.metrics.endpoint=/metrics

L'``endpoint`` indica il path HTTP su cui viene esposto lo scrape (vedi :ref:`osservabilitaEndpoint`).

Collettore OTLP (push)
^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: properties

   observability.collector.otel.type=otel
   # endpoint = URL completo del receiver OTLP/HTTP, comprensivo del path (es. '/v1/metrics')
   observability.collector.otel.metrics.endpoint=http://collector:4318/v1/metrics
   observability.collector.otel.metrics.stepS=30

Le metriche vengono inviate in *push* ogni ``stepS`` secondi, con encoding OTLP su HTTP/protobuf e
temporalità *cumulative* (compatibile con la semantica dei contatori Prometheus).

**Autenticazione.** Se il collector richiede autenticazione *Basic*, è possibile indicare le
credenziali; in tal caso GovWay invia ad ogni richiesta l'header
``Authorization: Basic <base64(username:password)>``:

.. code-block:: properties

   observability.collector.otel.metrics.credential.username=user
   observability.collector.otel.metrics.credential.password=secret

.. note::
   Le credenziali possono essere valorizzate tramite le variabili cifrate della *Secrets Map*
   (:ref:`govwaySecretsMap`) per evitare di indicare la password in chiaro nel file.

Metriche di sistema
^^^^^^^^^^^^^^^^^^^

L'esposizione delle metriche di sistema JVM/process (memoria, garbage collector, thread, CPU,
uptime, ecc.) è attivabile/disattivabile con:

.. code-block:: properties

   observability.metrics.system-metrics.enabled=true

Bucket degli istogrammi (SLO)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

I confini (*bucket*) degli istogrammi sono configurabili come liste di valori separati da virgola.
Sono definiti tre insiemi, applicati rispettivamente alla latenza delle richieste, alla dimensione
dei messaggi e alla persistenza del tracciamento:

.. code-block:: properties

   # latenza (millisecondi)
   observability.metrics.latency-slotMs=5,10,25,50,100,250,500,1000,2500,5000,10000
   # dimensione messaggi (byte)
   observability.metrics.size-slotByte=256,1024,4096,16384,65536,262144,1048576,4194304,16777216
   # persistenza tracciamento (millisecondi)
   observability.metrics.persistence-slotMs=5,10,25,50,100,250,500,1000,2500,5000,10000

I bucket vengono letti all'avvio: una modifica richiede il riavvio del gateway.
