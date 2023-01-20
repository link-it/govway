.. _releaseProcessGovWay_dynamicAnalysis_security_ocsp:

Online Certificate Status Protocol (OCSP)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano tutte le utility che consentono di verificare la validità di un certificato tramite il protocollo OCSP definito nel RFC 2560. 

I sorgenti sono disponibili nella classe `tools/utils/src/org/openspcoop2/utils/test/certificate/TestOCSP.java <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/certificate/TestOCSP.java>`_.
Evidenze disponibili in:

- `Utilità di base <https://jenkins.link.it/govway-testsuite/core/utils/>`_

Sono inoltre disponibili ulteriori test che verificano l'utilizzo del protocollo OCSP nelle varie funzionalità di GovWay dove è necessaria una validazione del certificato: connettore https, autenticazione, gestione delle credenziali, validazione token di sicurezza JOSE su API REST e WSSecurity su API SOAP, validazione dei token OAuth2 e risposte JWS di AttributeAuthority.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.other.ocsp <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/other/ocsp>`_

Evidenze disponibili in:

- `OCSP <https://jenkins.link.it/govway-testsuite/trasparente_karate/OtherOCSP/html/>`_
