Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Aggiunta una nuova metrica utilizzabile nelle politiche di Rate Limiting: "Numero completato con successo o fault applicativo".

Inoltre il controllo attuato dalla policy di rate limiting per dimensione messaggio è stato ottimizzato per utilizzare il valore dell'header HTTP 'Content-Length' se presente.

Infine nella funzionalità di controllo del traffico con sincronizzazione distribuita tramite hazelcast, è stato aggiunto un meccanismo di failover applicativo per la gestione dell'eccezione 'DistributedObjectDestroyedException' che può avvenire in casi limite durante l'utilizzo di AtomicLong e PNCounter in configurazioni del cluster senza CP Subsystem. Nell'intervento è stato reso configurabile il sistema di diagnostica di hazelcast e la validazione della configurazione utilizzata. 

