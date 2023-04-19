.. _releaseProcessGovWay_dynamicAnalysis_functional_responseCaching:

Caching Risposta
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano le funzionalità di salvataggio delle risposte in una cache. Le risposte, una volta salvate, saranno ritornate al client nelle successive invocazioni senza coinvolgere il backend.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/>`_ relativamente ai seguenti gruppi:

- `rest/response_caching <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/rest/response_caching>`_
- `soap/response_caching <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/soap/response_caching>`_

Evidenze disponibili in `risultati dei test sul Caching della Risposta <https://jenkins.link.it/govway-testsuite/trasparente/ResponseCaching/default/>`_

