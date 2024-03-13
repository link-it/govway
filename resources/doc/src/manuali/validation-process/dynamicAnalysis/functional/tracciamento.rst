.. _releaseProcessGovWay_dynamicAnalysis_functional_tracciamento:

Tracciamento
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le funzionalità di registrazione delle tracce.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `tracciamento.database <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/tracciamento/database>`_; viene verificata la registrazione delle transazioni su database rispetto alle fasi di tracciamento configurate. 
- `tracciamento.filetrace <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/tracciamento/filetrace>`_; viene verificata la registrazione delle transazioni su filesystem rispetto alle fasi di tracciamento e al fileTrace configurato. 


Evidenze disponibili in:

- `risultati dei test del gruppo 'tracciamento.database' <https://jenkins.link.it/govway-testsuite/trasparente_karate/TracciamentoDatabase/html/>`_
- `risultati dei test del gruppo 'tracciamento.filetrace' <https://jenkins.link.it/govway-testsuite/trasparente_karate/TracciamentoFiletrace/html/>`_




