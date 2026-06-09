Miglioramenti alla Console e alle API
---------------------------------------

Sono stati apportati i seguenti miglioramenti:

- Nuovo permesso associabile ad un'utenza ('Operatività API [O]') che consente ad un utente di accedere alla console di Monitoraggio per abilitare o disabilitare le erogazioni e le fruizioni configurate, eventualmente anche solo su singoli gruppi di risorse. Le operazioni di abilitazione/disabilitazione effettuate vengono registrate sul servizio di audit della console di gestione essendo a tutti gli effetti modifiche delle configurazioni.

- Aggiunta la possibilità di leggere il principal dell'utente autenticato via OIDC/OAuth2 direttamente dal token JWT (id_token o access_token), in alternativa all'endpoint /userinfo, utile con Authorization Server che non lo espongono o non vi restituiscono il principal.
