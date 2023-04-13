.. _releaseProcessGovWay_dynamicAnalysis_functional_apiREST:

Messaggi su API REST
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano le normali funzionalità di gateway per API REST, verificando lo scambio di messaggi json, xml, multipart e binari (pdf, doc, zip ...) tramite tutti i possibili metodi http (POST, GET, PUT, DELETE, ...) e i codici di risposta (2xx, 3xx, 4xx e 5xx). I verificano anche il corretto inoltro dell'header http 'X-HTTP-Method-Override' durante una POST.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src/.../rest/method <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/rest/method/>`_.

Evidenze disponibili in :

- `risultati dei test su API REST per fruizioni <https://jenkins.link.it/govway-testsuite/trasparente/REST.PD/default/>`_
- `risultati dei test su API REST per erogazioni <https://jenkins.link.it/govway-testsuite/trasparente/REST.PA/default/>`_

Sono inoltre disponibili ulteriori test che verificano la corretta gestione dell'header 'Content-Type' valorizzato con altri parametri oltre quelli previsti o valorizzato in maniera errata. I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src/.../rest/integrazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/rest/integrazione/>`_.

Evidenze disponibili in `risultati dei test su header Content-Type per API REST <https://jenkins.link.it/govway-testsuite/trasparente/Integrazione/default/>`_.


