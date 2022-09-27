Miglioramenti alla funzionalità di Identificazione degli Applicativi
----------------------------------------------------------------------

È stata ampliato il censimento degli applicativi fornendo le seguenti possibilità:

- registrare applicativi di dominio "esterno" anche nel profilo APIGateway e censirli puntualmente tra i criteri di autorizzazione per richiedente;

- registrare applicativi con credenziali di tipo 'token'. La nuova tipologia di credenziali consentono al processo di autenticazione token di identificare gli applicativi al fine di registrarli nei log e poterli ricercare tramite gli strumenti di monitoraggio.

Nel controllo degli accessi di una erogazione o fruizione è stata aggiunta la possibilità di autorizzare per richiedente o per ruolo gli applicativi identificati tramite token.

Nel profilo di interoperabilità 'ModI' la nuova modalità consente:

	- identificare ed autorizzare puntualmente gli applicativi registrati su PDND;

	- attuare controlli di consistenza del chiamante nel caso di presenza sia dell'header Authorization, generato dalla PDND, sia dell'header Agid-JWT-Signature, generato dalla parte mittente.
