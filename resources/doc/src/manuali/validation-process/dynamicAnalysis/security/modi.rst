.. _releaseProcessGovWay_dynamicAnalysis_security_modi:

Pattern di Sicurezza ModI
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test sono realizzati tramite il tool `Karate <https://karatelabs.github.io/karate/>`_ e verificano tutti i pattern di sicurezza previsti dalla Linee Guida di Interoperabiltà ModI.

I sorgenti sono disponibili in `protocolli/modipa/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src>`_ relativamente ai seguenti package:

- `org.openspcoop2.core.protocolli.modipa.testsuite.rest.sicurezza_messaggio <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio>`_; vengono verificati tutti i pattern di sicurezza previsti su API REST:

	- `ID_AUTH_REST_01 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/idar01.feature>`_
	- `ID_AUTH_REST_02 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/idar02.feature>`_
	- `INTEGRITY_REST_01 con ID_AUTH_REST_01 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/idar03.feature>`_
	- `INTEGRITY_REST_01 con ID_AUTH_REST_02 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/idar0302.feature>`_
	- `Token 'JWT-Signature' personalizzato (es. Custom-JWT-Signature) <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/idar03custom.feature>`_
	- `Identificazione applicativo e autorizzazione tramite token ModI su API REST <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/autorizzazioneMessaggio.feature>`_
	- `Identificazione applicativo e autorizzazione tramite token PDND su API REST <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/autorizzazioneToken.feature>`_
	- `Identificazione applicativo e autorizzazione tramite token PDND + token Integrity su API REST <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/autorizzazioneMessaggioToken.feature>`_
	- `Negoziazione token PDND <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/negoziazioneToken.feature>`_
	- `Validazione dei token tramite servizio OCSP su API REST <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/rest/sicurezza-messaggio/idar01-ocsp.feature>`_

- `org.openspcoop2.core.protocolli.modipa.testsuite.soap.sicurezza_messaggio <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio>`_; vengono verificati tutti i pattern di sicurezza previsti su API SOAP:

	- `ID_AUTH_SOAP_01 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/idas01.feature>`_
	- `ID_AUTH_SOAP_02 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/idas02.feature>`_
	- `INTEGRITY_SOAP_01 con ID_AUTH_SOAP_01 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/idas03.feature>`_
	- `INTEGRITY_SOAP_01 con ID_AUTH_SOAP_02 <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/idas0302.feature>`_
	- `Identificazione applicativo e autorizzazione tramite token ModI su API SOAP <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/autorizzazioneMessaggio.feature>`_
	- `Identificazione applicativo e autorizzazione tramite token PDND su API SOAP <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/autorizzazioneToken.feature>`_
	- `Validazione dei token tramite servizio OCSP su API SOAP <https://github.com/link-it/govway/tree/master/protocolli/modipa/testsuite/src/test/soap/sicurezza-messaggio/idas01-ocsp.feature>`_

Evidenze disponibili in:

- `API REST <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio/>`_
- `API SOAP <https://jenkins.link.it/govway-testsuite/modipa/html/org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/>`_
