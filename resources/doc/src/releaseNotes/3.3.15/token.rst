Miglioramenti alla funzionalità di Gestione dei Token
-----------------------------------------------------

È ora possibile definire una token policy di validazione tramite una 'well-known URL' come descritto nella specifica 'https://swagger.io/docs/specification/authentication/openid-connect-discovery'.
	
Inoltre, è stata aggiunta la possibilità di definire il keystore contenente le chiavi necessarie per effettuare una 'validazione JWT' del token, anche indicando un endpoint.

È stata aggiunta la possibilità di utilizzare policy OCSP nei connettori HTTPS riferiti nelle token policy di validazione e negoziazione. 

Nella configurazione di una token policy di negoziazione, è ora possibile indicare di utilizzare direttamente il payload di risposta HTTP come access token.

Infine, è stato risolto un problema che si presentava selezionando un'autenticazione HTTPS client nella funzionalità specifica di introspection o userinfo, senza abilitare l'endpoint HTTPS nella sezione token. In questo scenario, la gestione personalizzata dei keystore utilizzati per la connessione HTTPS non veniva attivata.

