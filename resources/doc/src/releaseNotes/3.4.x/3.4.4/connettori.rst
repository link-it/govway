Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

- Il connettore JMS può adesso essere utilizzato anche per le API REST e per le azioni SOAP con profilo diverso da oneway: in precedenza la transazione terminava con l'errore "risposta applicativa attesa ma non ricevuta". Completata la pubblicazione viene restituita una risposta senza contenuto: per REST con codice HTTP 204, per SOAP con un SOAP Envelope dal Body vuoto oppure contenente l'elemento vuoto '<operazione>Response', secondo le convenzioni degli stili RPC e document/literal wrapped. Una risposta con un contenuto differente può essere prodotta tramite una trasformazione della risposta.

- Le proprietà del connettore JMS prive di un campo dedicato (es. 'context-\*', 'pool-\*', 'locations-cache') sono adesso gestibili sia dalla console che dalle API di configurazione.

- Il nome della coda, le credenziali e le proprietà del connettore JMS possono contenere valori dinamici risolti a runtime (es. ${header:NAME}), consentendo di pubblicare su code differenti in funzione della richiesta.

- Il contenuto delle richieste viene inoltrato al backend in funzione del metodo HTTP (RFC 9110): per OPTIONS solo se presente l'header 'Content-Type', per GET, HEAD e DELETE solo se abilitato tramite la proprietà 'connettori.request.forceSendContent.enabled'.

- Sono configurabili i limiti applicati alla lettura degli header HTTP della risposta del backend (dimensione del singolo header e numero massimo di header su HTTP/1.1, dimensione complessiva su HTTP/2); gli stessi limiti si applicano al client HTTP utilizzato internamente per il recupero di keystore, JWKS, risposte OCSP e token.
