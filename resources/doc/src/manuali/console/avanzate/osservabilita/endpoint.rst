.. _osservabilitaEndpoint:

Endpoint /metrics
~~~~~~~~~~~~~~~~~

Quando è attivo un collettore di tipo ``prometheus`` (sezione :ref:`osservabilitaInstall`), GovWay
espone le metriche su un endpoint HTTP in modalità *pull*, invocabile con il metodo GET
all'indirizzo:

.. code-block:: text

   http://<hostname-gateway>:<porta>/govway/metrics

Se le metriche sono disabilitate (configurazione di default) oppure non è attivo un collettore di
tipo ``prometheus`` (ad esempio è configurato il solo collettore OTLP), l'endpoint risponde con
codice HTTP 404.

Il contenuto è restituito nel formato testuale Prometheus (``text/plain; version=0.0.4``). Ad ogni
scrape i gauge che campionano lo stato interno di GovWay e le metriche di sistema JVM/process
vengono letti *live*.

Esempio (estratto) della risposta:

.. code-block:: text

   # HELP govway_requests_total Number of handled requests
   # TYPE govway_requests_total counter
   govway_requests_total{http_status="200",protocol="trasparente",result_class="OK",result_code="0",role="inbound"} 42.0
   # HELP govway_request_duration_seconds Request processing latency
   # TYPE govway_request_duration_seconds histogram
   govway_request_duration_seconds_bucket{phase="total",protocol="trasparente",result_class="OK",role="inbound",le="0.1"} 40
   ...

.. warning::
   L'endpoint è esposto sulla stessa porta HTTP su cui GovWay riceve il traffico delle API e non
   prevede autenticazione: è necessario regolarne l'accesso a livello di rete o di reverse proxy,
   esponendolo solamente verso il sistema di monitoraggio.

L'elenco completo delle metriche esposte è descritto nella sezione :ref:`osservabilitaMetriche`.
