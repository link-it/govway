.. _releaseProcessGovWay:

Processo di Rilascio di GovWay
-------------------------------

Ogni versione candidata al rilascio viene verificata tramite 3 tipi di controlli di sicurezza al fine di assicurarne la stabilità e l'assenza di vulnerabilità note.

- :ref:`releaseProcessGovWay_staticCodeAnalysis`: identifica possibili vulnerabilità all'interno del codice sorgente tramite il tool `SpotBugs <https://spotbugs.github.io/>`_.

- :ref:`releaseProcessGovWay_dynamicAnalysis`: cerca vulnerabilità del software quando il codice è in modalità operativa. L'analisi viene eseguita su un ambiente dove GovWay è in esecuzione attraverso l'esecuzione di batterie di test realizzate tramite i tool `TestNG <https://testng.org/doc/>`_, `JUnit <https://junit.org/junit4/>`_, `Karate <https://karatelabs.github.io/karate/>`_ e `OWASP ZAP Proxy <https://www.zaproxy.org/>`_.

- :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis`: assicura che le librerie terza parte utilizzate non siano soggette a vulnerabilità di sicurezza note tramite il tool `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_.

Inoltre ogni fase viene verificata anche per ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_. 

.. toctree::
        :maxdepth: 2
        
	staticCodeAnalysis/index
        dynamicAnalysis/index
	thirdPartyDependencyAnalysis/index
