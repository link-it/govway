.. _releaseProcessGovWay_dynamicAnalysis_zap_maven:

OWASP ZAP Maven Plugin
~~~~~~~~~~~~~~~~~~~~~~~

Effettuato il checkout dei `dei sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_, è possibile avviare manualmente una analisi dinamica, tramite ZAP, utilizzando il seguente comando maven nella radice del progetto:

::

    mvn verify -Dzaproxy=verify -Dzaproxy.home=PATH_ASSOLUTO_TOOLS_ZAP -Dgovway.endpoint=http://127.0.0.1:8080 -Dgovway.ente=<SoggettoIndicatoInstaller> -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none

Come prerequisito all'esecuzione deve essere effettuato il download dell'ultima release del tool `OWASP ZAP Proxy <https://www.zaproxy.org/>`_.

Al termine dell'analisi viene prodotto un report nella directory 'zaproxy-reports' per ogni componente di GovWay analizzato.

Gli identificativi dei componenti verificati sono i seguenti: 

- api-rest-status: vengono verificate le vulnerabilità tipiche per API di tipo REST utilizzando il servizio di health check REST di govway;

- api-soap-status: vengono verificate le vulnerabilità tipiche per API di tipo SOAP utilizzando il servizio di health check SOAP di govway;

- api-config: viene verificata l'API di GovWay per la configurazione;

- api-monitor: viene verificata l'API di GoWay per il monitoraggio.

Per evitare la verifica di alcuni componenti è possibile utilizzare la proprietà 'zaproxy.skipTests'.   

L'esempio seguente attiva l'analisi solamente del componente runtime di GovWay per API REST e SOAP, escludendo gli altri test:

::

    mvn verify -Dzaproxy=verify
               -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none 
               -Dzaproxy.home=/tmp/ZAP_2.12.0
	       -Dgovway.endpoint=http://127.0.0.1:8080 -Dgovway.ente=Ente
               -Dzaproxy.skipTests=api-config,api-monitor
