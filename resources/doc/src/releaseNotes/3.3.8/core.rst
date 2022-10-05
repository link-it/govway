Miglioramenti alle funzionalità base dell'API Gateway
------------------------------------------------------------

Sono stati introdotti significativi miglioramenti prestazionali:

- in presenza di stress test alcune comunicazioni terminavano con l'errore "Cannot assign requested address" dovuto ad un numero troppo basso (5) di connessioni http 'keep-alive' mantenute aperte per una stessa destinazione (numero aumentato per default a 200);

- risolto degrado delle performance, che avveniva in presenza di trasformazione Freemarker o Velocity, attraverso il salvataggio dell'oggetto template istanziato in cache;

- è adesso possibile configurare una 'DenyList' o una 'WhiteList' che consente di personalizzare gli header HTTP che devono essere registrati su database tramite la funzionalità di registrazione messaggi;

- migliorata la latenza introdotta da GovWay (attualmente pochi millisecondi) durante la gestione di una prima richiesta non ancora in cache, su API Rest contenenti molte risorse (es. 600), in cui la latenza introdotta da GovWay era nell'ordine dei secondi (5,7 secondi);

- le richieste che richiedevano la validazione/negoziazione di un token, il recupero di attributi da un AttributeAuthority o l'invocazione di meccanismi di autenticazione/autorizzazione esterni acceduti via http (implementati tramite plugin) potevano far scaturire l'errore "Could not acquire semaphore after 30000ms", quando il servizio http esterno contattato (es. Authorization Server) non rispondeva mandando la richiesta in read timeout e nel frattempo continuavano ad accumularsi richieste che necessitavano dell'invocazione del servizio esterno poichè l'informazione richiesta non era in cache. L'errore si amplificava poichè per stessa funzionalità (es. Token Policy di Negoziazione) non vi era un lock dedicato alla singola policy ma un lock condiviso tra tutte le policy. La problematica è stata risolta:

	- dedicando un lock ad ogni Token Policy, AttributeAuthority o tipo di autenticazione/autorizzazione;

	- abbassando i tempi di read-timeout di default a 10 secondi;

	- consentendo un numero di richieste parallele verso il servizio esterno quando le informazioni non sono in cache (default 10), in modo da non rendere seriale l'inizializzazione della cache.
