.. _releaseProcessGovWay_dynamicAnalysis_security_authz:

Autorizzazione
~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano i criteri di autorizzazione puntuale e per ruolo.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.protocol.trasparente.testsuite.units.soap.authz <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/soap/authz>`_

Evidenze disponibili in:

- `Autorizzazione per le API Fruite <https://jenkins.link.it/govway-testsuite/trasparente/AutorizzazionePortaDelegata/default/>`_
- `Autorizzazione per le API Erogatore <https://jenkins.link.it/govway-testsuite/trasparente/AutorizzazionePortaApplicativa/default/>`_

Altri test vengono realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ e verificano il processo di validazione e autorizzazione dei token OAuth2, quello di negoziazione e
quello di scambio di attributi con AttributeAuthority.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/token/validazione>`_
- `org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/token/negoziazione>`_
- `org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/token/attribute_authority>`_

Evidenze disponibili in:

- `Validazione e autorizzazione dei token OAuth2 <https://jenkins.link.it/govway-testsuite/trasparente_karate/TokenValidazione/html/>`_
- `Negoziazione dei token OAuth2 <https://jenkins.link.it/govway-testsuite/trasparente_karate/TokenNegoziazione/html/>`_
- `Scambio di attributi con AttributeAuthority <https://jenkins.link.it/govway-testsuite/trasparente_karate/TokenAttributeAuthority/html/>`_
