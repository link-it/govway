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

Oltre ai controlli standard, previsti per default sul servizio, è possibile abilitarne uno ulteriore che si occupa di invocare una API di HealthCheck configurata su GovWay. Per attivare questo controllo è ncessario editare il file *<directory-lavoro>/govway_local.properties* e abilitare l'health check per API REST e/o per API SOAP, indicando il corretto endpoint di esposizione dell'API:

   ::

      # ================================================
      # Health check
      # Se abilitato, il servizio /govway/check invocherà anche l'API REST e/o SOAP per verificare il corretto funzionamento di GovWay
      # API REST
      org.openspcoop2.pdd.check.healthCheck.apiRest.enabled=true
      org.openspcoop2.pdd.check.healthCheck.apiRest.endpoint=http://localhost:8080/govway/ENTE/api-rest-status/v1/status
      # API SOAP
      org.openspcoop2.pdd.check.healthCheck.apiSoap.enabled=true
      org.openspcoop2.pdd.check.healthCheck.apiSoap.endpoint=http://localhost:8080/govway/ENTE/api-soap-status/v1
      ...
      # ================================================
      
Infine il servizio di 'health check' può essere invocato con dei parametri ad hoc per richiedere una verifica che i dati statistici risultino aggiornati; di seguito i parametri utilizzabili:

- *executeHourlyHealthCheckStats=true* : verifica l'aggiornamento dei dati campionati per ora;
- *executeDailyHealthCheckStats=true* : verifica l'aggiornamento dei dati campionati per giorno;
- *executeHealthCheckStats=true* : verifica l'aggiornamento dei dati per qualsiasi campionamento attivo.

La configurazione delle verifiche attive e i livelli di soglia di default possono essere configurati agendo nel file *<directory-lavoro>/govway_local.properties*, nella sezione 'Health check - Report statistici'.

I livelli di soglia possono anche essere ridefiniti tramite i seguenti ulteriori parametri: hourlyHealthCheckStatsThreshold, dailyHealthCheckStatsThreshold e healthCheckStatsThreshold.

Di seguito un esempio di errore derivante da un aggiornamento dei dati statistici orari non correttamente aggiornati:

       ::

          > curl -v http://localhost:8080/govway/check?executeHourlyHealthCheckStats=true&hourlyHealthCheckStatsThreshold=1
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
          Statistics HealthCheck failed
          Hourly statistical information found whose last update is older than the allowed threshold (1); last generation date: 2024-07-29_15:00:00.000

.. toctree::
        :maxdepth: 2
        
	manager
