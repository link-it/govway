.. _releaseProcessGovWay_dynamicAnalysis_functional_correlazioneApplicativa:

Correlazione Applicativa
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I test realizzati tramite il tool `TestNG <https://testng.org/doc/>`_ verificano le funzionalità di correlazione applicativa che consente al gateway di estrarre un identificatore relativo al contenuto applicativo e associarlo alla traccia a completamento delle informazioni già presenti.

I sorgenti sono disponibili in `protocolli/spcoop/testsuite/src/.../integrazione/IntegrazioneCorrelazioneApplicativa.java <https://github.com/link-it/govway/tree/master/protocolli/spcoop/testsuite/src/org/openspcoop2/protocol/spcoop/testsuite/units/integrazione/IntegrazioneCorrelazioneApplicativa.java>`_.

Evidenze disponibili insieme ai test sugli header di integrazione:

- `risultati dei test per la correlazione applicativa <https://jenkins.link.it/govway-testsuite/spcoop/Integrazione/default/>`_

Sono inoltre disponibili ulteriori test realizzati tramite il tool `JUnit <https://junit.org/junit4/>`_ già descritti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_functional_headerIntegrazione` relativamente al gruppo `integrazione.json <https://github.com/link-it/govway/tree/master/protocolli/trasparente/testsuite/karate/src/org/openspcoop2/core/protocolli/trasparente/testsuite/integrazione/json>`_ all'interno del quale i test utilizzano la correlazione applicativa di tipo 'template', 'velocity' e 'freemarker'.

Evidenze disponibili in `risultati dei test del gruppo 'integrazione.json' <https://jenkins.link.it/govway-testsuite/trasparente_karate/IntegrazioneJson/html/>`_ 


