Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Per la funzionalità di controllo del traffico con sincronizzazione distribuita tramite Redis è stato aggiunto il supporto per la configurazione di un TTL (Time To Live), consentendo la pulizia automatica dei contatori non più attivi.

Per la funzionalità di controllo del traffico con sincronizzazione distribuita tramite hazelcast sono stati introdotti i seguenti miglioramenti:

- aggiunto timer periodico per la pulizia dei proxy Hazelcast orfani (contatori di intervalli di rate limiting scaduti);

- introdotta gestione puntuale delle eccezioni 'TargetNotMemberException' e 'MemberLeftException' relative a dismissioni dei nodi.

Infine, è stata rivista la gestione dei contatori delle richieste simultanee in ambienti distribuiti (Hazelcast/Redis), introducendo un meccanismo di scadenza che consente la pulizia automatica dei contatori ed evita che rimangano memorizzati indefinitamente.
