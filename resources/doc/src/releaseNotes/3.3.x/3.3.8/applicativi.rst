Miglioramenti alla funzionalità di Identificazione degli Applicativi
----------------------------------------------------------------------

È stata estesa la possibilità di censimento degli applicativi fornendo le seguenti nuove opzioni:

- registrare applicativi afferenti a domini esterni anche per il profilo APIGateway, con la possibilità di censirli puntualmente tra i criteri di autorizzazione per richiedente;

- registrare applicativi con credenziali di tipo 'token'. La nuova funzione consente al processo di autenticazione tramite token di identificare anche questo tipo di client, che saranno quindi riconoscibili nei log e utilizzabili per le ricerche negli strumenti di monitoraggio.

Nel controllo degli accessi di erogazioni e fruizioni è stata aggiunta la possibilità di autorizzare per richiedente o per ruolo gli applicativi identificati tramite token, prima autorizzabili sono sulla base dei valori dei token presentati.

Nel profilo di interoperabilità 'ModI' la nuova modalità consente:

	- di identificare ed autorizzare puntualmente gli applicativi registrati su PDND;

	- di attuare controlli di consistenza tra i certificati utilizzati dal chiamante nei casi di utilizzo simultaneo dell'header Authorization, generato dalla PDND, e dell'header Agid-JWT-Signature, generato dalla parte mittente.
