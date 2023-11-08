.. _releaseProcessGovWay_dynamicAnalysis_security_message:

Sicurezza Messaggio
~~~~~~~~~~~~~~~~~~~~~

**WSSecurity per API SOAP**

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano tutte le funzionalità di cifratura, firma, SAML per WSSecurity su API SOAP.

I sorgenti sono disponibili in `protocolli/spcoop/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.protocol.spcoop.testsuite.units.sicurezza <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/sicurezza>`_

Evidenze disponibili in:

- `WSSecurity <https://jenkins.link.it/govway-testsuite/spcoop/Sicurezza/default/>`_

Sono inoltre disponibili ulteriori test che verificano la funzionalità 'Username Token'.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/other/wssecurity>`_

Evidenze disponibili in:

- `WSSecurity (Username) <https://jenkins.link.it/govway-testsuite/trasparente_karate/OtherWSSecurity/html/>`_

**JOSE per API REST**

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano tutte le funzionalità di cifratura e firma per JOSE su API REST.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.protocol.trasparente.testsuite.units.rest.jose <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/rest/jose>`_

Evidenze disponibili in:

- `JOSE <https://jenkins.link.it/govway-testsuite/trasparente/JOSE/default/>`_
