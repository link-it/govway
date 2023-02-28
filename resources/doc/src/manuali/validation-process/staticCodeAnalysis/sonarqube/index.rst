.. _releaseProcessGovWay_staticCodeAnalysis_sonarqube:

SonarQube
~~~~~~~~~~

In questa fase vengono identificate possibili vulnerabilità all'interno del codice sorgente tramite il tool `SonarQube <https://sonarqube.org/>`_.

Il tool viene utilizzato fin dalle fasi di sviluppo prima di integrare una nuova funzionalità o un fix di un bug sul master del progetto, verificando la qualità dei sorgenti tramite il `plugin per Eclipse <https://www.sonarsource.com/products/sonarlint/features/eclipse/>`_ come descritto nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_sonarqube_eclipse`.

Ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ viene effettuata automaticamente una verifica dei sorgenti nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_. Maggiori dettagli vengono forniti nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_sonarqube_ci`.

Una verifica manuale dei `sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_ è attuabile seguento le indicazioni presenti nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_sonarqube_maven`.

.. toctree::
        :maxdepth: 2
        
	ci_jenkins
	eclipse
	maven
	
