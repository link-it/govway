.. _releaseProcessGovWay_staticCodeAnalysis:

Static Code Analysis
~~~~~~~~~~~~~~~~~~~~~~~

In questa fase vengono identificate possibili vulnerabilità all'interno del codice sorgente cercando pattern riconducibili a bug improbabili da individuare tramite test dinamici (:ref:`releaseProcessGovWay_dynamicAnalysis`).

L'analisi viene effettuata tramite l'utilizzo dei seguenti tool:

- `SpotBugs <https://spotbugs.github.io/>`_
- `SonarQube <https://sonarqube.org/>`_

Il tool *SpotBugs* viene utilizzato fin dalle fasi di sviluppo dai programmatori tramite il `plugin per Eclipse <https://spotbugs.readthedocs.io/en/latest/eclipse.html>`_ come descritto nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_spotbugs_eclipse`.

Dopo aver lavorato su un branch dedicato alla realizzazione di una nuova funzionalità o di un bug fix, prima di riportare il lavoro sul master i sorgenti soggetti a modifica vengono verificati anche tramite il tool *SonarQube* (:ref:`releaseProcessGovWay_staticCodeAnalysis_sonarqube_eclipse`).

Ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ viene effettuata automaticamente una verifica dei sorgenti nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_ utilizzando entrambi i tools. Maggiori dettagli vengono forniti nelle sezioni :ref:`releaseProcessGovWay_staticCodeAnalysis_spotbugs_ci` e :ref:`releaseProcessGovWay_staticCodeAnalysis_sonarqube_ci`.

Infine effettuato il checkout dei `sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_, è sempre possibile avviare manualmente una analisi statica tramite uno dei tool seguendo le indicazioni fornite nelle sezioni :ref:`releaseProcessGovWay_staticCodeAnalysis_spotbugs_maven` e :ref:`releaseProcessGovWay_staticCodeAnalysis_sonarqube_maven`.

.. note::
      I problemi di sicurezza relativi alle librerie terza parte utilizzate sono trattati separatamente con il lavoro di analisi descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis` e di conseguenza il codice sorgente di tali librerie è escluso dall'analisi del codice statico.

.. toctree::
        :maxdepth: 2
        
	src/index
	spotbugs/index
	sonarqube/index
	
