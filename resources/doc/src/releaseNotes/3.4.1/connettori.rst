Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

Il canale NIO supporta ora la gestione di chiamate in modalità SSE (Server-Sent Events), che consentono al server di inviare eventi in streaming verso il client tramite una connessione long-lived identificata dal Content-Type text/event-stream.

Contestualmente, la gestione del canale NIO è stata ottimizzata introducendo l’utilizzo di virtual threads per lo streaming sia della richiesta che della risposta.

