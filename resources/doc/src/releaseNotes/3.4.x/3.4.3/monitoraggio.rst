Miglioramenti alla Console e alle API
---------------------------------------

Sono stati apportati i seguenti miglioramenti:

- Nuovo permesso associabile ad un'utenza ('Operatività API [O]') che consente ad un utente di accedere alla console di Monitoraggio per abilitare o disabilitare le erogazioni e le fruizioni configurate, eventualmente anche solo su singoli gruppi di risorse. Le operazioni di abilitazione/disabilitazione effettuate vengono registrate sul servizio di audit della console di gestione essendo a tutti gli effetti modifiche delle configurazioni.

- Aggiunta la possibilità di leggere il principal dell'utente autenticato via OIDC/OAuth2 direttamente dal token JWT (id_token o access_token), in alternativa all'endpoint /userinfo, utile con Authorization Server che non lo espongono o non vi restituiscono il principal.

- Introdotta una validazione client-side dei caratteri ammessi nei campi della console e del monitor, allineata al controllo già effettuato server-side: i campi non conformi vengono evidenziati in pagina con messaggio inline e il submit di salvataggio o di ricerca viene bloccato, posizionando automaticamente la pagina sul primo campo da correggere.
