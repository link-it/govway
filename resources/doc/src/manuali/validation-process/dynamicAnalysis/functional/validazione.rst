.. _releaseProcessGovWay_dynamicAnalysis_functional_validazione:

Validazione dei messaggi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le funzionalità di validazione dei messaggi tramite interfacce OpenAPI 3, Swagger 2 e WSDL.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `validazione.parametri <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/validazione/parametri>`_; viene verificata la serializzazione dei parametri descritta in `Swagger.io - Parameter Serialization <https://swagger.io/docs/specification/serialization/>`_. I test verificano anche la possibilità che http header e parametri della url possano esistere molteplici volte nella richiesta e nella risposta.
- `validazione.multipart <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/validazione/multipart>`_; vengono verificati i messaggi multipart descritti in `Swagger.io - Multipart Requests <https://swagger.io/docs/specification/describing-request-body/multipart-requests/>`_ e `Swagger.io - File Upload <https://swagger.io/docs/specification/describing-request-body/file-upload/>`_.
- `validazione.swagger_request_validator <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/validazione/swagger_request_validator>`_; viene verificata la funzionalità di validazione dei contenuti attuata tramite la libreria `swagger-request-validator <https://bitbucket.org/atlassian/swagger-request-validator>`_. Tra i vari test è presente anche la validazione tramite API complesse di dimensioni notevoli.
- `validazione.rpc <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/validazione/rpc>`_; verifica il funzionamento con messaggi definiti in interfacce WSDL con 'style RPC' e 'use literal' o 'encoded'. I test oltre alla validazione verificano anche il riconoscimento dell'operazione tramite l'ottimizzazione 'soap reader'.
- `other.api_grandi <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/other/api_grandi>`_; verifica l'utilizzo di API REST con un numero elevato di risorse.

Evidenze disponibili in:

- `risultati dei test del gruppo 'validazione.parametri' <https://jenkins.link.it/govway-testsuite/trasparente_karate/ValidazioneParametri/html/>`_
- `risultati dei test del gruppo 'validazione.multipart' <https://jenkins.link.it/govway-testsuite/trasparente_karate/ValidazioneMultipart/html/>`_
- `risultati dei test del gruppo 'validazione.swagger_request_validator' <https://jenkins.link.it/govway-testsuite/trasparente_karate/ValidazioneSwaggerRequestValidator/html/>`_
- `risultati dei test del gruppo 'validazione.rpc' <https://jenkins.link.it/govway-testsuite/trasparente_karate/ValidazioneRPC/html/>`_ 
- `risultati dei test del gruppo 'other.api_grandi' <https://jenkins.link.it/govway-testsuite/trasparente_karate/OtherApiGrandi/html/>`_ 

Sono inoltre disponibili ulteriori test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ che verificano la validazione effettuata tramite interfaccia WSDL e schemi XSD di un servizio dummy definito tramite molteplici port-types, operations e stili/usi differenti (wrapped document literal, rpc literal, encoded ...) che consentono di validare la conformità dei messaggi sui vari stili.

I sorgenti sono disponibili in:

- `validazione wsdl <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/validazione>`_

Evidenze disponibili in:

- `risultati dei test di validazione <https://jenkins.link.it/govway-testsuite/spcoop/ValidazioneContenutiApplicativi/default/>`_



