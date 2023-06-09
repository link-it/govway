.. _releaseProcessGovWay_dynamicAnalysis_functional_registrazioneMessaggi:

Registrazione dei messaggi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le funzionalità di registrazione dei contenuti dei messaggi.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `registrazione_messaggi.dump_binario <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/registrazione_messaggi/dump_binario>`_; viene verificata la registrazione dei messaggi inviati e ricevuti dal gateway "as is" in forma 'binaria' su database. Viene inoltre verificata la funzionalità che consente di creare blackList o whiteList per quanto concerne gli header http registrati.
- `registrazione_messaggi.dump_normale <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/registrazione_messaggi/dump_normale>`_; viene verificata la precedente modalità di salvataggio in cui era il gateway ad analizzare in realtime la struttura multipart ed a salvarne i singoli elementi (envelope, attachments).


Evidenze disponibili in:

- `risultati dei test del gruppo 'registrazione_messaggi.dump_binario' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RegistrazioneMessaggiDumpBinario/html/>`_
- `risultati dei test del gruppo 'registrazione_messaggi.dump_normale' <https://jenkins.link.it/govway-testsuite/trasparente_karate/RegistrazioneMessaggiDumpNormale/html/>`_




