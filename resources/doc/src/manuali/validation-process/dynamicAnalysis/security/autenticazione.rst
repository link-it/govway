.. _releaseProcessGovWay_dynamicAnalysis_security_authn:

Autenticazione
~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano le autenticazioni https, http-basic, principal e api-key.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.protocol.trasparente.testsuite.units.soap.authn <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/soap/authn>`_
- `org.openspcoop2.protocol.trasparente.testsuite.units.rest.auth <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/src/org/openspcoop2/protocol/trasparente/testsuite/units/rest/auth>`_

Evidenze disponibili in:

- `Autenticazione per le API Fruite <https://jenkins.link.it/govway-testsuite/trasparente/AutenticazionePortaDelegata/default/>`_
- `Autenticazione per le API Erogate <https://jenkins.link.it/govway-testsuite/trasparente/AutenticazionePortaApplicativa/default/>`_

Ulteriori evidenze dei test mirati alle sole API di tipo REST che verificano la gestione di header HTTP quali 'WWW-Authenticate', 'Authorization' etc sono disponibili in:

- `Autenticazione per le API REST Fruite <https://jenkins.link.it/govway-testsuite/trasparente/RESTAutenticazionePortaDelegata/default/>`_ 
- `Autenticazione per le API REST Erogate <https://jenkins.link.it/govway-testsuite/trasparente/RESTAutenticazionePortaApplicativa/default/>`_.

Altri test vengono realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ e verificano le autenticazioni tramite token OAuth2, e il forward delle credenziali tramite header HTTP dove l'autenticazione viene effettuata da un webserver. Vengono inoltre verificate le autenticazioni https, http-basic, principal e api-key già verificate tramite TestNG relativamente ai soli applicativi di dominio esterno.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/autenticazione/applicativi_token>`_
- `org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_esterni <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/autenticazione/applicativi_esterni>`_
- `org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/autenticazione/gestore_credenziali>`_
- `org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali_principal <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/autenticazione/gestore_credenziali_principal>`_

Evidenze disponibili in:

- `Autenticazione tramite token OAuth2 <https://jenkins.link.it/govway-testsuite/trasparente_karate/AutenticazioneApplicativiToken/html/>`_
- `Autenticazione applicativi di dominio esterno <https://jenkins.link.it/govway-testsuite/trasparente_karate/AutenticazioneApplicativiEsterni/html/>`_
- `Forward delle credenziali <https://jenkins.link.it/govway-testsuite/trasparente_karate/AutenticazioneGestoreCredenziali/html/>`_
- `Forward delle credenziali 'principal' <https://jenkins.link.it/govway-testsuite/trasparente_karate/AutenticazioneGestoreCredenzialiPrincipal/html/>`_
