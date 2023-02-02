.. _tokenNegoziazionePolicy:

Token Policy Negoziazione 
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
   -  *Signed JWT with Client Secret*: modalità di negoziazione identica alla precedente dove però l'asserzione JWT viene firmata tramite una chiave simmetrica;
   -  *Personalizzato*: consente di definire in ogni sua parte la richiesta http inoltrata all'endpoint di negoziazione token.

-  *URL*: endpoint del servizio di negoziazione token.

-  *Connection Timeout*: Tempo massimo in millisecondi di attesa per
   stabilire una connessione con il server di negoziazione token.

-  *Read Timeout*: Tempo massimo in millisecondi di attesa per la
   ricezione di una risposta dal server di negoziazione token.

-  *Https*: Parametri di configurazione nel caso in cui il server di
   negoziazione token richieda un accesso Https.

-  *Proxy*: Parametri di configurazione nel caso in cui il server di
   negoziazione token richieda l'uso di un proxy per l'accesso.

Nel caso sia attivato il flag relativo ad un Proxy o una configurazione Https saranno presentate delle sezioni omonime dove poter inserire i dati di configurazione richiesti.

I parametri di configurazione relativi al tipo di negoziazione del token configurato vengono descritti nelle sezioni ':ref:`tokenNegoziazionePolicy_notJwt`', ':ref:`tokenNegoziazionePolicy_jwt`' e ':ref:`tokenNegoziazionePolicy_custom`'.

Nella sezione 'Dati Richiesta' potranno invece essere definiti ulteriori criteri che riguardano la richiesta di un token. I dati presenti possono essere definiti tramite costanti o possono contenere parti dinamiche risolte a runtime dal Gateway (per maggiori dettagli :ref:`valoriDinamici`).

-  *Scope*: elenco di scope utente richiesti;

-  *Audience*: audience per il quale si vorrebbe ottenere il token;

-  *Parametri*: consente di indicare per riga ulteriori parametri (nome=valore) da inserire nella richiesta.

-  *Header HTTP*: consente di indicare per riga eventuali header HTTP (nome=valore) da inserire nella richiesta.

Infine nella sezione 'Token Forward' si può configurare la modalità di inoltro del token verso l'endpoint del connettore a cui verrà associata la policy che stiamo definendo:

-  *RFC 6750 - Bearer Token Usage (Authorization Request Header Field)*: Il token viene inoltrato al destinatario utilizzando l'header Authorization presente nella richiesta HTTP.

-  *RFC 6750 - Bearer Token Usage (URI Query Parameter)*: Il token viene inoltrato al destinatario tramite parametro access\_token della Query String.

-  *Header HTTP*: Il token viene inoltrato al destinatario utilizzando un header HTTP il cui nome deve essere specificato nel campo seguente.

-  *Parametro URL*: Il token viene inoltrato al destinatario utilizzando un parametro della Query String il cui nome deve essere specificato nel campo seguente.


Nelle sezioni successive vengono forniti i dettagli relativi alle modalità di negoziazione di un token nel caso sia previsto un jwt firmato o meno.

.. toctree::
        :maxdepth: 2

        tokenNegoziazione_notJwt
	tokenNegoziazione_jwt
	tokenNegoziazione_jwt_pdnd
	tokenNegoziazione_custom
