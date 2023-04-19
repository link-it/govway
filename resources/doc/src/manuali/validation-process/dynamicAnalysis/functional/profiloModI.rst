.. _releaseProcessGovWay_dynamicAnalysis_functional_profiloModI:

Profilo "ModI"
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `Karate <https://karatelabs.github.io/karate/>`_ verificano tutte le funzionalità previsto dal profilo di interoperabiltà 'ModI'.

I sorgenti sono disponibili in `protocolli/modi/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/modi/testsuite/src/>`_ relativamente ai seguenti gruppi:

- `org.openspcoop2.core.protocolli.modipa.testsuite.rest.bloccante <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/rest/bloccante>`_; vengono verificati i profili di interazione bloccanti e CRUD su API REST.
- `org.openspcoop2.core.protocolli.modipa.testsuite.soap.bloccante <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/soap/bloccante>`_; viene verificato il profilo di interazione bloccante su API SOAP.
- `org.openspcoop2.core.protocolli.modipa.testsuite.rest.non_bloccante.push <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/rest/non_bloccante/push>`_; viene verificato il profilo di interazione non bloccante 'Push' su API REST.
- `org.openspcoop2.core.protocolli.modipa.testsuite.soap.non_bloccante.push <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/soap/non_bloccante/push>`_; viene verificato il profilo di interazione non bloccante 'Push' su API SOAP.
- `org.openspcoop2.core.protocolli.modipa.testsuite.rest.non_bloccante.pull <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/rest/non_bloccante/pull>`_; viene verificato il profilo di interazione non bloccante 'Pull' su API REST.
- `org.openspcoop2.core.protocolli.modipa.testsuite.soap.non_bloccante.pull <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/soap/non_bloccante/pull>`_; viene verificato il profilo di interazione non bloccante 'Pull' su API SOAP.
- `org.openspcoop2.core.protocolli.modipa.testsuite.rest.sicurezza_messaggio <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio>`_; vengono verificati tutti i pattern di sicurezza previsti dalla Linee Guida di Interoperabiltà ModI per quanto concerne API REST, come descritto nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_security_modi`.
- `org.openspcoop2.core.protocolli.modipa.testsuite.soap.sicurezza_messaggio <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio>`_; vengono verificati tutti i pattern di sicurezza previsti dalla Linee Guida di Interoperabiltà ModI per quanto concerne API SOAP, come descritto nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_security_modi`.

Evidenze disponibili in:

- `API REST - Pattern Interazione Bloccante <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/rest/bloccante/>`_
- `API SOAP - Pattern Interazione Bloccante <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/soap/bloccante/>`_
- `API REST - Pattern Interazione Non Bloccante Push <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/rest/non_bloccante/push/>`_
- `API SOAP - Pattern Interazione Non Bloccante Push <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/soap/non_bloccante/push/>`_
- `API REST - Pattern Interazione Non Bloccante Pull <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/rest/non_bloccante/pull/>`_
- `API SOAP - Pattern Interazione Non Bloccante Pull <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/soap/non_bloccante/pull/>`_
- `API REST - Pattern Sicurezza <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio/>`_
- `API SOAP - Pattern Sicurezza <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/>`_
