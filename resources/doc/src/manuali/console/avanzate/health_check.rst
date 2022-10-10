.. _configAvanzataHealthCheck:

Health Check
~~~~~~~~~~~~~

Govway espone un servizio built-in di health check al contesto:

       ::

          /govway/check

Il servizio, invocabile con una semplice GET, restituisce una risposta vuota con codice HTTP 200 nel caso in cui GovWay sia correttamente in funzione.

       ::

          > curl -v http://localhost:8080/govway/check
          > GET /govway/check HTTP/1.1
          > Host: localhost:8080
          > User-Agent: curl/7.66.0
          > Accept: */*
          > 
          < HTTP/1.1 200 OK
          < Connection: keep-alive
          < Content-Length: 0
          < Date: Mon, 10 Oct 2022 14:55:45 GMT


Nel caso in cui GovWay non si sia avviato correttamente viene restituito un codice HTTP 503:

       ::

          > curl -v http://localhost:8080/govway/check
          > GET /govway/check HTTP/1.1
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
          API Gateway GovWay non inzializzato

Se invece vengono rilevati errori dopo che GovWay si è avviato correttamente viene restituito un codice HTTP 500 e nel payload viene riportata la motivazione dell'errore rilevato:

       ::

          > curl -v http://localhost:8080/govway/check
          > GET /govway/check HTTP/1.1
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
          Risorse di sistema non disponibili: [Database] Connessione al database GovWay non disponibile: javax.resource.ResourceException: IJ000453: Unable to get managed connection for java:/org.govway.datasource
