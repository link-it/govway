Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

- Il connettore JMS può adesso essere utilizzato anche per le API REST e per le azioni SOAP con profilo diverso da oneway: in precedenza la transazione terminava con l'errore "risposta applicativa attesa ma non ricevuta". Completata la pubblicazione viene restituita una risposta senza contenuto: per REST con codice HTTP 204, per SOAP con un SOAP Envelope dal Body vuoto oppure contenente l'elemento vuoto '<operazione>Response', secondo le convenzioni degli stili RPC e document/literal wrapped. Una risposta con un contenuto differente può essere prodotta tramite una trasformazione della risposta.

- Il contenuto delle richieste viene inoltrato al backend in funzione del metodo HTTP (RFC 9110): per OPTIONS solo se presente l'header 'Content-Type', per GET, HEAD e DELETE solo se abilitato tramite la proprietà 'connettori.request.forceSendContent.enabled'.
