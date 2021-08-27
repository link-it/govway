.. _tokenNegoziazionePolicy:

Token Negoziazione Policy
-------------------------

Per poter definire politiche che consentono di spedire un Bearer Token verso l'endpoint associato ad un connettore è necessario creare delle Token Policy da riferire nelle configurazioni degli specifici servizi. La gestione delle Token Policy
si effettua andando alla sezione *Configurazione > Token Policy* della
govwayConsole. Per creare una nuova policy si utilizza il pulsante
*Aggiungi*. Il form di creazione appare inizialmente come quello
illustrato in :numref:`tokenPolicyFig`.


   .. figure:: ../../_figure_console/TokenPolicy-negoziazione-generale.png
    :scale: 100%
    :align: center
    :name: tokenNegoziazionePolicyFig

    Informazioni generali di una Token Policy

Inizialmente si inseriscono i dati identificativi:

-  *Nome*: nome univoco da assegnare alla policy

-  *Tipo*: deve essere selezionato il tipo *Negoziazione*

-  *Descrizione*: testo di descrizione generale della policy

Al passo successivo si inseriscono le Informazioni Generali. Nella
sezione *Token Endpoint* si specifica il tipo di negoziazione e i vari parametri necessari:

-  *Tipo*: indica la modalità di negoziazione del token. I valori possibili sono:

   -  *Client Credentials*: modalità di negoziazione 'Client Credentials Grant' descritta nel RFC 6749 (https://tools.ietf.org/html/rfc6749#page-40);
   -  *Resource Owner Password Credentials*: modalità di negoziazione 'Resource Owner Password Credentials Grant' descritta nel RFC 6749 (https://tools.ietf.org/html/rfc6749#page-37);
   -  *Signed JWT*: modalità di negoziazione 'Client Credentials Grant' descritta nella sezione 2.2 del RFC 7523 (https://datatracker.ietf.org/doc/html/rfc7523#section-2.2) che prevede lo scambio di un'asserzione JWT firmata tramite certificato x.509 con l'authorization server;
   -  *Signed JWT with Client Secret*: modalità di negoziazione identica alla precedente dove però l'asserzione JWT viene firmata tramite una chiave simmetrica.

-  *URL*: endpoint del servizio di negoziazione token.

-  *Connection Timeout*: Tempo massimo in millisecondi di attesa per
   stabilire una connessione con il server di negoziazione token.

-  *Read Timeout*: Tempo massimo in millisecondi di attesa per la
   ricezione di una risposta dal server di negoziazione token.

-  *Https*: Parametri di configurazione nel caso in cui il server di
   negoziazione token richieda un accesso Https.

-  *Proxy*: Parametri di configurazione nel caso in cui il server di
   negoziazione token richieda l'uso di un proxy per l'accesso.

Successivamente devono essere forniti i dati di configurazione specifici
dell'autenticazione utente, se il tipo di negoziazione selezionato è 'Resource Owner Password Credentials':

-  *Username* e *Password*: Dovranno essere forniti Username e Password dell'utente per cui verrà effettuata la negoziazione del token.

Successivamente devono essere forniti i dati di configurazione specifici dell'autenticazione client in caso di modalità di negoziazione che non prevedono un JWT firmato:

-  *Autenticazione Http Basic*: flag da attivare nel caso in cui il servizio di negoziazione richieda autenticazione di tipo HTTP-BASIC. In questo caso dovranno essere forniti Client-ID e Client-Secret nei campi successivi.

-  *Autenticazione Bearer*: flag da attivare nel caso in cui il servizio di negoziazione richieda autenticazione tramite un bearer token. Il token dovrà essere indicato nel campo successivo fornito.

-  *Autenticazione Https*: flag da attivare nel caso in cui il servizio di negoziazione richieda autenticazione di tipo Https. In questo caso dovranno essere forniti tutti i dati di configurazione nei campi presenti nella sezione 'https'.

Nel caso sia attivato il flag relativo ad un Proxy o una configurazione Https saranno presentate delle sezioni omonime dove poter inserire i dati di configurazione richiesti.

Se invece è previsto un JWT firmato saranno richiesti i parametri da inserire nel JWT (claims) e l'algoritmo di firma da utilizzare:

-  *Signature Algorithm*: algoritmo utilizzato per firmare l'asserzione jwt;

-  *Client ID*: identificativo del client censito sull'AuthorizationServer che verrà indicato nel claim 'sub' dell'asserzione JWT;

-  *Audience*: identifica l'authorization server come destinario dell'asserzione JWT (claim 'aud');

-  *Issuer*: identità del firmatario dell'asserzione JWT;

-  *Time to Live*: indica la validità temporale, in secondi, a partire dalla data di creazione dell'asserzione.

Se il tipo di negoziazione selezionato è 'Signed JWT with Client Secret', oltre al Client-ID viene richiesto anche il secret, concordato con l'authorization server, da utilizzare per firmare l'asserzione JWT. Nel caso invece sia stato selezionato il tipo 'Signed JWT' vengono richiesti i parametri di accesso al keystore per individuare la chiave privata da utilizzare per firmare l'asserzione.


Se invece è previsto un JWT firmato saranno richiesti i parametri da inserire nel JWT (claims) e l'algoritmo di firma da utilizzare:

-  *Signature Algorithm*: algoritmo utilizzato per firmare l'asserzione jwt;

-  *Client ID*: identificativo del client censito sull'AuthorizationServer che verrà indicato nel claim 'sub' dell'asserzione JWT;

-  *Audience*: identifica l'authorization server come destinario dell'asserzione JWT (claim 'aud');

-  *Issuer*: identità del firmatario dell'asserzione JWT;

-  *Time to Live*: indica la validità temporale, in secondi, a partire dalla data di creazione dell'asserzione.

Se il tipo di negoziazione selezionato è 'Signed JWT with Client Secret', oltre al Client-ID viene richiesto anche il secret, concordato con l'authorization server, da utilizzare per firmare l'asserzione JWT. Nel caso invece sia stato selezionato il tipo 'Signed JWT' vengono richiesti i parametri di accesso al keystore per individuare la chiave privata da utilizzare per firmare l'asserzione.


Nella sezione 'Configurazione' potranno invece essere definiti ulteriori criteri che riguardano la richiesta di un token:

-  *Scope*: Elenco di scope utente richiesti.

-  *Audience*: Audience per il quale si vorrebbe ottenere il token.

Infine nella sezione 'Token Forward' si può configurare la modalità di inoltro del token verso l'endpoint del connettore a cui verrà associata la policy che stiamo definendo:

-  *RFC 6750 - Bearer Token Usage (Authorization Request Header Field)*: Il token viene inoltrato al destinatario utilizzando l'header Authorization presente nella richiesta HTTP.

-  *RFC 6750 - Bearer Token Usage (URI Query Parameter)*: Il token viene inoltrato al destinatario tramite parametro access\_token della Query String.

-  *Header HTTP*: Il token viene inoltrato al destinatario utilizzando un header HTTP il cui nome deve essere specificato nel campo seguente.

-  *Parametro URL*: Il token viene inoltrato al destinatario utilizzando un parametro della Query String il cui nome deve essere specificato nel campo seguente.
