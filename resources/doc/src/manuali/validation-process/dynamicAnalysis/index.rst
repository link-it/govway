.. _releaseProcessGovWay_dynamicAnalysis:

Dynamic Analysis
~~~~~~~~~~~~~~~~~~~~~

L'analisi dinamica cerca vulnerabilità del software quando il codice è in modalità operativa.

L'analisi viene effettuata ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_ dove vengono avviati oltre 8.700 test realizzati con il tool `TestNG <https://testng.org/doc/>`_ e oltre 5.000 test realizzati tramite i tool `JUnit <https://junit.org/junit4/>`_ e `Karate <https://karatelabs.github.io/karate/>`_. Maggiori dettagli vengono forniti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_ci`.

All'interno degli oltre 13.000 test precedentemente indicati, quelli mirati agli aspetti di sicurezza vengono descritti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_security`. 

Ulteriori test mirati agli aspetti di sicurezza vengono effettuati utilizzando il tool `OWASP ZAP Proxy <https://www.zaproxy.org/>`_ per verificare sia il componente runtime di GovWay che le API REST e le console di configurazione e monitoraggio come descritto nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_zap`.

.. toctree::
        :maxdepth: 2
        
	ci_jenkins
        security/index
