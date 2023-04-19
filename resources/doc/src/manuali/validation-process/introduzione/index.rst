.. _releaseProcessGovWay:

Introduzione
-------------------------------

Ogni nuova versione di GovWay prima del rilascio viene sottoposta a 3 diversi tipi di verifiche di sicurezza al fine di assicurarne la stabilità e l'assenza di vulnerabilità note.

- :ref:`releaseProcessGovWay_staticCodeAnalysis`: identifica possibili vulnerabilità all'interno del codice sorgente tramite i tools `SpotBugs <https://spotbugs.github.io/>`_ e `SonarQube <https://sonarqube.org/>`_.

- :ref:`releaseProcessGovWay_dynamicAnalysis`: cerca vulnerabilità del software durante l'effettiva esecuzione del prodotto. L'analisi viene eseguita attraverso l'esecuzione di estese batterie di test realizzate tramite i tool `TestNG <https://testng.org/doc/>`_, `JUnit <https://junit.org/junit4/>`_, `Karate <https://karatelabs.github.io/karate/>`_ e `OWASP ZAP Proxy <https://www.zaproxy.org/>`_.

- :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis`: assicura che tutte le librerie terza parte utilizzate non siano soggette a vulnerabilità di sicurezza note, utilizzando il tool `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_.

Ognuna di tali fasi viene anche verificata per ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_. 
