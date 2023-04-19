.. _releaseProcessGovWay_dynamicAnalysis_functional_headerIntegrazione:

Header di Integrazione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ verificano le funzionalità di integrazione con i backend per lo scambio di informazioni.

I sorgenti sono disponibili in `protocolli/trasparente/testsuite/karate/src <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/>`_ relativamente ai seguenti gruppi:

- `integrazione.autenticazione <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/integrazione/autenticazione>`_; verifica l'integrazione che consente di generare Header HTTP utilizzabili dal backend per autenticare l'API Gateway.

- `integrazione.template <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/integrazione/template>`_; verifica  l'integrazione 'template' che consente di applicare una trasformazione al messaggio.

- `integrazione.accept <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/integrazione/accept>`_; verifica l'utilizzo dell'header 'Accept' per API REST.

- `integrazione.json <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/integrazione/json>`_; verifica l'utilizzo dell'interscambio di informazioni tramite una struttura JSON.

Evidenze disponibili in:

- `risultati dei test del gruppo 'integrazione.autenticazione' <https://jenkins.link.it/govway-testsuite/trasparente_karate/IntegrazioneAutenticazione/html/>`_
- `risultati dei test del gruppo 'integrazione.template' <https://jenkins.link.it/govway-testsuite/trasparente_karate/IntegrazioneTemplate/html/>`_
- `risultati dei test del gruppo 'integrazione.accept' <https://jenkins.link.it/govway-testsuite/trasparente_karate/IntegrazioneAccept/html/>`_
- `risultati dei test del gruppo 'integrazione.json' <https://jenkins.link.it/govway-testsuite/trasparente_karate/IntegrazioneJson/html/>`_ 

Sono inoltre disponibili ulteriori test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ i cui sorgenti sono disponibili in `protocolli/spcoop/testsuite/src <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integrazione>`_ relativamente ai seguenti gruppi:

- `Integrazione <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integrazione/Integrazione.java>`_; vengono verificate le funzionalita di integrazione tramite header soap, header di trasporto, url e identificazione basata su contentuto, url o input.
- `IntegrazioneConnettoreHTTPCORE <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integrazione/IntegrazioneConnettoreHTTPCORE.java>`_; simile al precedente gruppo, viene però utilizzato il connettore 'httpcore'.
- `IntegrazioneConnettoreSAAJ <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integrazione/IntegrazioneConnettoreHTTPCORE.java>`_; simile al precedente gruppo, viene però utilizzato il connettore 'saaj'.
- `RichiesteApplicativeScorrette <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integrazione/LetturaCredenzialiIngresso.java>`_; vengono generate richieste applicative scorrette che il gateway deve riconoscere e gestire.

Evidenze disponibili in:

- `risultati dei test sugli header di integrazione <https://jenkins.link.it/govway-testsuite/spcoop/Integrazione/default/>`_


