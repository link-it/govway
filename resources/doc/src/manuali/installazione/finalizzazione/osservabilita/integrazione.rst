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
         - targets: ['<hostname-pdd>:<porta>']

Le metriche possono poi essere interrogate in PromQL e visualizzate in *Grafana*; ad esempio, il
tasso di richieste per esito:

.. code-block:: text

   sum by (outcome) (rate(govway_requests_total[5m]))

o la latenza al 95° percentile per servizio (richiede le metriche di dettaglio,
:ref:`osservabilitaMetricheDettaglio`):

.. code-block:: text

   histogram_quantile(0.95, sum by (service, le) (rate(govway_service_request_duration_seconds_bucket{phase="total"}[5m])))

OTLP (push)
^^^^^^^^^^^

Configurando un collettore di tipo ``otel`` (vedi :ref:`osservabilitaConfigurazione`), GovWay invia
le metriche a un *OpenTelemetry Collector*, che può a sua volta inoltrarle al backend desiderato
(Prometheus, Grafana, backend cloud, ecc.). L'endpoint configurato deve essere l'URL **completo** del
ricevitore OTLP/HTTP del collector, comprensivo del path ``/v1/metrics`` (tipicamente sulla porta
``4318``), ad esempio ``http://collector:4318/v1/metrics``.

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
