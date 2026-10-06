.. _osservabilitaIntegrazione:

Integrazione con i sistemi di monitoraggio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Prometheus (pull)
^^^^^^^^^^^^^^^^^

Per acquisire le metriche esposte da GovWay è sufficiente configurare un *job* di scrape in
Prometheus che punti all'endpoint ``/metrics`` (vedi :ref:`osservabilitaEndpoint`):

.. code-block:: yaml

   scrape_configs:
     - job_name: govway
       metrics_path: /govway/metrics
       static_configs:
         - targets: ['<hostname-gateway>:<porta>']

Le metriche possono poi essere interrogate in PromQL e visualizzate in *Grafana*; ad esempio, il
tasso di richieste per esito:

.. code-block:: text

   sum by (result_class) (rate(govway_requests_total[5m]))

o la latenza al 95° percentile per erogazione o fruizione (richiede le metriche di dettaglio,
:ref:`osservabilitaMetricheDettaglio`):

.. code-block:: text

   histogram_quantile(0.95, sum by (interface_id, le) (rate(govway_service_request_duration_seconds_bucket{phase="total"}[5m])))

o il numero di richieste per classe di esito di tutte le erogazioni e fruizioni delle API con un
determinato tag (la label ``tags`` riporta i tag dell'API separati da virgola):

.. code-block:: text

   sum by (result_class) (rate(govway_service_requests_total{tags=~"(.*,)?Anagrafica(,.*)?"}[5m]))

OTLP (push)
^^^^^^^^^^^

Configurando un collettore di tipo ``otel`` (sezione :ref:`osservabilitaInstall`), GovWay invia
le metriche a un *OpenTelemetry Collector*, che può a sua volta inoltrarle al backend desiderato
(Prometheus, Grafana, backend cloud, ecc.). L'endpoint configurato deve essere l'URL **completo** del
ricevitore OTLP/HTTP del collector, comprensivo del path ``/v1/metrics`` (tipicamente sulla porta
``4318``), ad esempio ``http://collector:4318/v1/metrics``.

Le metriche inviate hanno gli stessi nomi, label e unità di misura di quelle esposte sull'endpoint
``/metrics``: in particolare i tempi sono espressi in secondi, sia nei valori sia nei confini dei bucket
degli istogrammi. Ogni invio riporta inoltre i seguenti attributi di risorsa, che consentono al
collector di distinguere le metriche provenienti dai diversi nodi di un cluster:

- ``service.name``: per default ``govway``;
- ``service.instance.id``: identificativo del nodo; per default corrisponde all'identificativo
  univoco assegnato al nodo in un'installazione in load balancing (proprietà
  ``org.openspcoop2.pdd.cluster_id`` del file *<directory-lavoro>/govway_local.properties*, descritta
  nella sezione :ref:`cluster`) o, se non definito, al nome host della macchina;
- ``service.version``: per default la versione di GovWay.

I valori sono ridefinibili per ciascun collettore, come descritto nella sezione
:ref:`osservabilitaInstall`.

Esempio minimale di configurazione di un OpenTelemetry Collector che riceve via OTLP/HTTP ed espone
in modalità Prometheus:

.. code-block:: yaml

   receivers:
     otlp:
       protocols:
         http:
   exporters:
     prometheus:
       endpoint: 0.0.0.0:9464
   service:
     pipelines:
       metrics:
         receivers: [otlp]
         exporters: [prometheus]
