Miglioramenti alla funzionalità di Caching della Risposta
---------------------------------------------------------

Sono state introdotte le seguenti nuove funzionalità:

-  *Digest*: aggiunta la possibilà di indicare quali header (per default nessuno) e quali parametri della url (per default tutti) concorrano alla generazione del digest .

-  *Cache-Control*: aggiunta la gestione dell'header http 'Cache-Control' per quanto concerne le direttive 'no-cache', 'no-store' e 'max-age'. Su ogni erogazione o fruizione di API è possibile disabilitare la gestione di qualcuna o di tutte le direttive.

-  *Caching attivabile in funzione della Risposta*: la funzionalità di caching delle risposte è ora attivabile in funzione del return code http e del tipo di risposta ottenuta (fault).
