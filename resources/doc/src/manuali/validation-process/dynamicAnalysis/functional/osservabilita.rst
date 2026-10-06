.. _releaseProcessGovWay_dynamicAnalysis_functional_osservabilita:

Metriche di Osservabilità
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le metriche di osservabilità esposte dal gateway in formato Prometheus.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `observability.metrics <https://github.com/link-it/govway/tree/3.4.x/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/observability/metrics>`_; vengono verificate le metriche esposte sull'endpoint '/govway/metrics': metriche di sistema di GovWay e JVM/process, metriche di transazione (richieste, latenze e dimensioni dei messaggi), tempi delle fasi di elaborazione e persistenza del tracciamento.


Evidenze disponibili in:

- `risultati dei test del gruppo 'observability.metrics' <https://jenkins.link.it/govway4-testsuite/trasparente_karate/ObservabilityMetrics/html/>`_

