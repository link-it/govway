.. _releaseProcessGovWay_thirdPartyDynamicAnalysis_ci:

Verifica automatica in Continuous Integration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ad ogni commit sul `branch 3.4.x dei sorgenti del progetto <https://github.com/link-it/govway/tree/3.4.x/>`_ viene avviata automaticamente una verifica delle librerie terza parte nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway4/job/GovWay/>`_, tramite entrambi i tool di analisi: OWASP Dependency-Check e OSV-Scanner.

.. toctree::
        :maxdepth: 2

	ci_jenkins_owasp
	ci_jenkins_osv
