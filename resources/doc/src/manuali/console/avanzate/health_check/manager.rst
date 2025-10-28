.. _configAvanzataHealthCheckManager:

Health Check per ambiente Manager
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nelle installazioni in cui le console di gestione e monitoraggio sono distribuite su macchine diverse da quelle su cui sono attivi i nodi run, è possibile utilizzare il servizio built-in di health check fornito dalla console di monitoraggio, disponibile al contesto:

       ::

          /govwayMonitor/check

Il servizio, invocabile con una semplice GET, restituisce una risposta vuota con codice HTTP 200 nel caso in cui la console sia correttamente in funzione.

       ::

          > curl -v http://localhost:8080/govwayMonitor/check
          > GET /govwayMonitor/check HTTP/1.1
          > Host: localhost:8080
          > User-Agent: curl/7.66.0
          > Accept: */*
          > 
          < HTTP/1.1 200 OK
          < Connection: keep-alive
          < Content-Length: 0
          < Date: Mon, 10 Oct 2022 14:55:45 GMT


Nel caso in cui non si sia avviato correttamente viene restituito un codice HTTP 503:

       ::

          > curl -v http://localhost:8080/govwayMonitor/check
          > GET /govwayMonitor/check HTTP/1.1
          > Host: localhost:8080
          > User-Agent: curl/7.66.0
          > Accept: */*
          > 
          < HTTP/1.1 503 Service Unavailable
          < Connection: keep-alive
          < Content-Type: text/plain
          < Content-Length: 35
          < Date: Mon, 10 Oct 2022 16:13:59 GMT
          < 
          GovWay Monitor non inizializzato

Se invece vengono rilevati errori dopo che l'ambiente manager si è avviato correttamente viene restituito un codice HTTP 500 e nel payload viene riportata la motivazione dell'errore rilevato:

       ::

          > curl -v http://localhost:8080/govwayMonitor/check
          > GET /govwayMonitor/check HTTP/1.1
          > Host: localhost:8080
          > User-Agent: curl/7.66.0
          > Accept: */*
          > 
          < HTTP/1.1 500 Internal Server Error
          < Connection: keep-alive
          < Content-Type: text/plain
          < Content-Length: 203
          < Date: Mon, 10 Oct 2022 16:17:40 GMT
          < 
          GovWay Monitor ERROR: Database Configurazione: Get Connection failure: jakarta.resource.ResourceException: IJ000453: Unable to get managed connection for java:/org.govway.datasource.console; Database Tracciamento: Get Connection failure: jakarta.resource.ResourceException: IJ000453: Unable to get managed connection for java:/org.govway.datasource.console; Database Statistiche: Get Connection failure: jakarta.resource.ResourceException: IJ000453: Unable to get managed connection for java:/org.govway.datasource.console
