.. _releaseProcessGovWay_dynamicAnalysis_functional_apiConfig:

API di Configurazione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `Karate <https://karatelabs.github.io/karate/>`_ verificano le operazioni di configurazione del registro di Govway disponibili anche tramite la console "govwayConsole".

I sorgenti sono disponibili in `tools/rs/config/server/testsuite/src <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/>`_; i test verificano le operazioni CRUD per i seguenti oggetti del registro:

- `API <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/api>`_
- `Risorse per API REST <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/risorse>`_
- `Servizi per API SOAP <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/servizi>`_
- `Azioni di servizi per API SOAP <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/azioni>`_
- `Allegati <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/allegati>`_
- `Erogazioni <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/erogazioni>`_
- `Fruizioni <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/fruizioni>`_
- `Soggetti <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/soggetti>`_
- `Applicativi <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/applicativi>`_
- `Applicativi di tipo server <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/applicativi_server>`_
- `Ruoli <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/ruoli>`_
- `Scope <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/scope>`_
- `Stato di funzionamento dell'API <https://github.com/link-it/govway/tree/master/tools/rs/config/server/testsuite/src/org/openspcoop2/core/config/rs/testsuite/status>`_

Evidenze disponibili in `risultati dei test sull'API di Configurazione <https://jenkins.link.it/govway-testsuite/api_config/html/>`_
