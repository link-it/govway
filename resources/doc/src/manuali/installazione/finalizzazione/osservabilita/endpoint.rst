.. _osservabilitaEndpoint:

Endpoint /metrics
~~~~~~~~~~~~~~~~~~

Quando è configurato un collettore di tipo ``prometheus``, GovWay espone le metriche su un
endpoint HTTP in modalità *pull*, all'indirizzo:

.. code-block:: text

   http://<hostname-pdd>:<porta>/govway/metrics

Il path finale (``/metrics``) è quello indicato dalla proprietà
``observability.collector.<name>.metrics.endpoint`` (vedi :ref:`osservabilitaConfigurazione`).

Il contenuto è restituito nel formato testuale Prometheus (``text/plain; version=0.0.4``). Ad ogni
scrape i gauge che campionano lo stato interno di GovWay (Gruppo A) e i binder JVM/process vengono
letti *live*.

Esempio (estratto) della risposta:

.. code-block:: text

   # HELP govway_requests_total Number of handled requests
   # TYPE govway_requests_total counter
   govway_requests_total{outcome="ok",pdd_role="inbound",protocol="trasparente"} 42.0
   # HELP govway_request_duration_seconds Request processing latency
   # TYPE govway_request_duration_seconds histogram
   govway_request_duration_seconds_bucket{outcome="ok",pdd_role="inbound",protocol="trasparente",phase="total",le="0.1"} 40
   ...

.. note::
   L'endpoint espone informazioni operative sul gateway: è opportuno regolarne l'accesso a livello
   di rete/reverse proxy, esponendolo solo verso il sistema di monitoraggio.

L'elenco completo delle metriche esposte è descritto nella sezione :ref:`osservabilitaMetriche`.
