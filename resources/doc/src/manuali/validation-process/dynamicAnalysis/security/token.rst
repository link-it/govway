.. _releaseProcessGovWay_dynamicAnalysis_security_token:

Token Policy e Attribute Authority
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_  verificano il processo di validazione e autorizzazione dei token OAuth2, quello di negoziazione e
quello di scambio di attributi con AttributeAuthority.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/token/validazione>`_; vengono verificate le token policy di validazione dei token.
- `org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/token/negoziazione>`_; vengono verificate le token policy di negoziazione dei token.
- `org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/token/attribute_authority>`_; verifica l'interazione con le Attribute Authorities.

Evidenze disponibili in:

- `Validazione e autorizzazione dei token OAuth2 <https://jenkins.link.it/govway-testsuite/trasparente_karate/TokenValidazione/html/>`_
- `Negoziazione dei token OAuth2 <https://jenkins.link.it/govway-testsuite/trasparente_karate/TokenNegoziazione/html/>`_
- `Scambio di attributi con AttributeAuthority <https://jenkins.link.it/govway-testsuite/trasparente_karate/TokenAttributeAuthority/html/>`_


