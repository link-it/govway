.. _releaseProcessGovWay_dynamicAnalysis:

Dynamic Analysis
~~~~~~~~~~~~~~~~~~~~~

L'analisi dinamica cerca vulnerabilità del software durante l'effettiva esecuzione del prodotto. L'analisi viene effettuata ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_ dove vengono avviati oltre 8.800 test realizzati con il tool `TestNG <https://testng.org/doc/>`_ e oltre 8.900 test realizzati tramite i tool `JUnit <https://junit.org/junit4/>`_ e `Karate <https://karatelabs.github.io/karate/>`_. 

All'interno degli oltre 17.700 test, quelli mirati agli aspetti di sicurezza vengono descritti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_security`. 

Ulteriori test mirati agli aspetti di sicurezza vengono effettuati utilizzando il tool `OWASP ZAP Proxy <https://www.zaproxy.org/>`_ per verificare sia il componente runtime di GovWay che le API REST di configurazione e monitoraggio come descritto nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_zap`.

Maggiori dettagli sui test funzionali vengono invece forniti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_ci`.


.. toctree::
        :maxdepth: 2
        
        security/index
	functional/index
