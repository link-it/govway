Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Aggiunta una nuova metrica utilizzabile nelle politiche di Rate Limiting: "Numero Richieste Completate con Successo o Fault Applicativi".

Inoltre il controllo attuato dalla policy di rate limiting per dimensione messaggio è stato ottimizzato per utilizzare il valore dell'header HTTP 'Content-Length' se presente.

Infine nella funzionalità di controllo del traffico con sincronizzazione distribuita tramite hazelcast, è stato introdotto un meccanismo di recupero in caso di eccezione 'DistributedObjectDestroyedException' che può avvenire in casi limite in configurazioni del cluster che non utilizzano il CP Subsystem. Nell'intervento è stato reso configurabile il sistema di diagnostica di hazelcast e la validazione della configurazione utilizzata.

