.. _releaseProcessGovWay_dynamicAnalysis_functional_trasformazione:

Trasformazioni
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le funzionalità di trasformazione dei messaggi.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `trasformazione.protocollo <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/trasformazione/protocollo>`_; viene attuata una trasformazione completa dei messaggi (contenuti, header, url) per verificare la funzionalità di trasformazione del protocollo rest(json)->soap(xml) e viceversa. 
- `trasformazione.form <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/trasformazione/form>`_; la trasformazione accede ad una richiesta di tipo 'application/x-www-form-urlencoded' al fine per riconoscere casi di errori generati durante la certificazione di uno SPIDProvider.
- `trasformazione.soap_action <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/trasformazione/soap_action>`_; vengono modificate le SOAPAction su messaggi SOAP.
- `trasformazione.info_integrazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/trasformazione/info_integrazione>`_; vengono inoltrate al backend informazioni estratte dalla richiesta.

Evidenze disponibili in:

- `risultati dei test del gruppo 'trasformazione.protocollo' <https://jenkins.link.it/govway-testsuite/trasparente_karate/TrasformazioneProtocollo/html/>`_
- `risultati dei test del gruppo 'trasformazione.form' <https://jenkins.link.it/govway-testsuite/trasparente_karate/TrasformazioneForm/html/>`_
- `risultati dei test del gruppo 'trasformazione.soap_action' <https://jenkins.link.it/govway-testsuite/trasparente_karate/TrasformazioneSoapAction/html/>`_
- `risultati dei test del gruppo 'trasformazione.info_integrazione' <https://jenkins.link.it/govway-testsuite/trasparente_karate/TrasformazioneInfoIntegrazione/html/>`_

Sono inoltre disponibili ulteriori test che verificano l'utilizzo di variabili dinamiche risolte a runtime dal gateway.

I sorgenti sono disponibili in `core/src/org/openspcoop2/pdd_test/ <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/pdd_test/>`_ relativamente ai seguenti package:

- `dynamic <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/pdd_test/dynamic>`_
- `trasformazioni <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/pdd_test/trasformazioni>`_

Evidenze disponibili in:

- `risultati dei test di trasformazione <https://jenkins.link.it/govway-testsuite/core/pdd/#/>`_




