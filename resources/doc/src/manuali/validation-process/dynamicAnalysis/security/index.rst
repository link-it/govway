.. _releaseProcessGovWay_dynamicAnalysis_security:

Security Tests
~~~~~~~~~~~~~~~~

All'interno dei test descritti nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_ci` risiedono test dedicati alla sicurezza che vengono descritti nei paragrafi seguenti.

Ulteriori test mirati agli aspetti di sicurezza vengono effettuati utilizzando il tool `OWASP ZAP Proxy <https://www.zaproxy.org/>`_ descritto nella sezione :ref:`releaseProcessGovWay_dynamicAnalysis_zap`.

Se i test rilevano un nuovo problema o una regressione viene avviata una gestione della vulnerabilità come descritto nel manuale :ref:`vulnerabilityManagement`.

.. toctree::
        :maxdepth: 2
        
	autenticazione
        autorizzazione
	modi
	token
	apikey
	byok
	messageSecurity
	certificate
	ocsp
	cors
	xxe
	zap/index

