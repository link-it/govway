.. _osservabilitaInstall:

Metriche di Osservabilità
-------------------------

GovWay è in grado di esporre metriche operative (numero di richieste, latenze, dimensioni dei
messaggi, stato interno del gateway, risorse JVM, ecc.) verso i più diffusi sistemi di
monitoraggio infrastrutturale, adottando il modello dati e le convenzioni di
`Micrometer <https://micrometer.io>`_. Sono supportate due modalità di raccolta, attivabili
contemporaneamente:

- **pull (Prometheus)**: GovWay espone le metriche sull'endpoint HTTP ``/govway/metrics`` nel
  formato testuale Prometheus; è il sistema di monitoraggio a interrogare periodicamente il
  gateway (*scrape*);

- **push (OTLP)**: GovWay invia periodicamente le metriche a un collector compatibile
  `OpenTelemetry <https://opentelemetry.io>`_ (protocollo OTLP su HTTP/protobuf).

Per default le metriche sono **disabilitate**: nessun collettore è attivo e l'endpoint
``/govway/metrics`` risponde con codice HTTP 404. In questa sezione viene descritto come
abilitarle; l'endpoint, il catalogo delle metriche esposte, la configurazione avanzata e gli esempi
di integrazione con i sistemi di monitoraggio sono descritti nella sezione
:ref:`configAvanzataOsservabilita`.

File di configurazione
~~~~~~~~~~~~~~~~~~~~~~

La configurazione dei collettori si effettua nel file
*<directory-lavoro>/govway_local.observability.properties*, prodotto dall'installer, che ridefinisce
le proprietà di default presenti nel file *govway.observability.properties* interno all'archivio.
Le proprietà vengono validate all'avvio del gateway: una configurazione non valida ne impedisce
l'avvio (*fail-fast*). Una modifica richiede il riavvio del gateway.

L'elenco dei collettori attivi (nomi separati da virgola) è indicato nella proprietà
``observability.collectors``, vuota per default:

.. code-block:: properties

   # Elenco dei collettori attivi (nomi separati da virgola)
   observability.collectors=

Un *collettore* rappresenta una destinazione delle metriche: per ciascun collettore ``<name>``
elencato si definiscono il tipo (``prometheus`` oppure ``otel``) e la configurazione del segnale
``metrics``:

.. code-block:: properties

   observability.collector.<name>.type=(prometheus|otel)
   observability.collector.<name>.metrics.enabled=(true|false)

Vincoli:

- è ammesso **al massimo un collettore di tipo** ``prometheus`` (l'endpoint di scrape è unico);
- per i collettori di tipo ``otel`` (push) sono obbligatorie le proprietà ``endpoint`` (URL del
  collector) e ``stepS`` (intervallo di invio in secondi).

Tutti i collettori attivi ricevono le stesse misure: la medesima metrica viene pubblicata su tutte
le destinazioni configurate.

I valori delle proprietà possono riferire variabili di sistema o java tramite la sintassi ``${NOME}``
(ad esempio ``${HOSTNAME}``), comprese le variabili definite nella *Secrets Map*
(:ref:`govwaySecretsMap`).

Collettore Prometheus (pull)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il file prodotto dall'installer contiene già la definizione del collettore ``prometheus``; per
abilitarlo è sufficiente indicarlo tra i collettori attivi:

.. code-block:: properties

   observability.collectors=prometheus
   observability.collector.prometheus.type=prometheus

Le metriche vengono esposte sull'endpoint ``/govway/metrics`` del gateway (sezione
:ref:`osservabilitaEndpoint`).

.. warning::
   L'endpoint è esposto sulla stessa porta HTTP su cui GovWay riceve il traffico delle API e non
   prevede autenticazione. Le metriche contengono informazioni operative sul gateway (esiti, latenze,
   stato dei pool e della JVM): è necessario regolarne l'accesso a livello di rete o di reverse
   proxy, esponendolo solamente verso il sistema di monitoraggio.

Collettore OTLP (push)
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: properties

   observability.collectors=otel
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
   (:ref:`govwaySecretsMap`), riferite con la sintassi ``${NOME}``, per evitare di indicare la
   password in chiaro nel file.

**Identificazione del gateway.** Ogni invio riporta gli attributi di risorsa con cui GovWay si
presenta al collector, ridefinibili per ciascun collettore:

.. code-block:: properties

   # default: govway
   observability.collector.otel.resource.service.name=govway
   # default: versione di GovWay (es. 3.4.4)
   observability.collector.otel.resource.service.version=3.4.4
   # default: identificativo del nodo (org.openspcoop2.pdd.cluster_id) o, se non definito, nome host
   observability.collector.otel.resource.service.instance.id=${HOSTNAME}

Per default l'attributo ``service.instance.id`` assume il valore dell'identificativo univoco
assegnato al nodo in un'installazione in load balancing (proprietà ``org.openspcoop2.pdd.cluster_id``
del file *<directory-lavoro>/govway_local.properties*, sezione :ref:`cluster`) o, se non definito, il
nome host della macchina. L'attributo consente al collector di distinguere le metriche dei diversi
nodi di un cluster e deve quindi essere univoco per nodo: nelle installazioni in cui più istanze
condividono la stessa configurazione (es. più pod in Kubernetes) può essere valorizzato tramite una
variabile, come ``${HOSTNAME}`` nell'esempio.
