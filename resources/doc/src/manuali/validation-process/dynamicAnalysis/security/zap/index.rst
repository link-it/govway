.. _releaseProcessGovWay_dynamicAnalysis_zap:

OWASP Zed Attack Proxy (ZAP)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Identifica possibili vulnerabilità sia all'interno del componente runtime di GovWay che nelle API REST e le console di configurazione e monitoraggio tramite il tool `OWASP ZAP Proxy <https://www.zaproxy.org/>`_.

L'analisi viene effettuata ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ dove viene avviata automaticamente una verifica nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_. Maggiori dettagli vengono forniti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_zap_ci`.

Effettuato il checkout dei `dei sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_, è possibile anche avviare manualmente una analisi come descritto nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_zap_maven`.

.. toctree::
        :maxdepth: 2
        
	ci_jenkins
	maven
	
