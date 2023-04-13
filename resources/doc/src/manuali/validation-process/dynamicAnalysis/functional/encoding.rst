.. _releaseProcessGovWay_dynamicAnalysis_functional_encoding:

Encoding
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le funzionalità che impattano sull'encoding dei messaggi.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `encoding.charset <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/encoding/charset>`_; verifica le comunicazioni in stream, con o senza registrazione dei messaggi, la costruzione di oggetti in memoria 'read only' o modificabili per trasformazioni, con charset 'UTF-8', 'UTF-16' e 'ISO-8859-1'.
- `encoding.entity_reference <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/encoding/entity_reference>`_; verifica le comunicazioni in stream, con o senza registrazione dei messaggi, e la costruzioni di header con richieste che contengono molti xml entity reference (2MB) che se trattate come contenuto SAAJ fanno salire il carico per il lavoro richiesto al GC (G1 Humongous Allocation).

Evidenze disponibili in:

- `risultati dei test del gruppo 'encoding.charset' <https://jenkins.link.it/govway-testsuite/trasparente_karate/EncodingCharset/html/>`_
- `risultati dei test del gruppo 'encoding.entity_reference' <https://jenkins.link.it/govway-testsuite/trasparente_karate/EncodingEntityReference/html/>`_

Sono inoltre disponibili ulteriori test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ che verificano la corretta gestione di messaggi che contengono caratteri non compresi nel set ASCII.

I sorgenti sono disponibili in `protocolli/spcoop/testsuite/src/.../others/XMLEncoding.java <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/others/XMLEncoding.java>`_.

Evidenze disponibili in `risultati dei test <https://jenkins.link.it/govway-testsuite/spcoop/Others/default/>`_



