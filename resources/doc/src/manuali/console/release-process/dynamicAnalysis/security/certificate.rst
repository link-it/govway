.. _releaseProcessGovWay_dynamicAnalysis_security_certs:

Gestione certificati X.509 e keystore JKS, PKCS12, e PKCS11
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano tutte le utility che consentono di processare certificati X.509, CRL, e keystore nei vari formati JKS, PKCS12 e PKCS11. Per quanto concerne la gestione dei keystore PKCS11 vengono utilizzati token creati con 'softhsm', il simulatore pkcs11 di dnssec. 

I sorgenti sono disponibili in `tools/utils/src/org/openspcoop2/utils/test <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test>`_ relativamente ai seguenti package:

- `org.openspcoop2.utils.test.security <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/security>`_
- `org.openspcoop2.utils.test.certificate <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/certificate>`_
- `org.openspcoop2.utils.test.crypto <https://github.com/link-it/govway/tree/master/tools/utils/src/org/openspcoop2/utils/test/crypto>`_

Evidenze disponibili in:

- `Utilità di base <https://jenkins.link.it/govway-testsuite/core/utils/>`_

Sono inoltre disponibili ulteriori test che verificano tutte le funzionalità di cifratura e firma tramite JOSE su API REST e tramite WSSecurity su API SOAP, la validazione e negoziazione dei token OAuth2, la connessione TLS etc utilizzando keystore di tipo PKCS11.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509 <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/pkcs11/x509>`_

Evidenze disponibili in:

- `PKCS11 <https://jenkins.link.it/govway-testsuite/trasparente_karate/PKCS11x509/html/>`_
