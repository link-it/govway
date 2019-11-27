.. _apiGwAutenticazione:

Autenticazione Trasporto
^^^^^^^^^^^^^^^^^^^^^^^^

In questa sezione è possibile configurare il meccanismo di
autenticazione richiesto per l'accesso al servizio. Come mostrato in :numref:`autenticazione`,
si possono specificare:

-  Il tipo di autenticazione, distinto in base al protocollo di trasporto, selezionando uno tra i valori disponibili:

   -  *disabilitato*
	nessuna autenticazione

   -  *https* 
	La richiesta deve possedere un certificato client X509. La presenza del certificato client nella richiesta è obbligatoria a meno che non sia abilitato il flag *Opzionale*. Se è presente un certificato client, il gateway cercherà inoltre di identificare un applicativo o un soggetto a cui è stato associato il certificato come credenziale di accesso (per ulteriori dettagli si rimanda alle sezioni :ref:`soggetto` e :ref:`applicativo`); l'identificazione non è obbligatoria ma nel caso avvenga con successo l'applicativo o il soggetto verrà registrato nei log e potrà essere utilizzato anche ai fini di autorizzazione puntuale e per ruoli (:ref:`apiGwAutorizzazione`).

   -  *http-basic*
	La richiesta deve possedere un header http "Authorization" che veicola credenziali Basic (username e password) come indicato in 'https://tools.ietf.org/html/rfc2617#section-2'. Le credenziali devono corrispondere ad un applicativo o un soggetto registrato nel gateway. Abilitando l'ulteriore opzione *Forward Authorization* è possibile propagare all'endpoint di destinazione l'header http "Authorization" che altrimenti verrà consumata.

   -  *principal*
	La richiesta deve possedere il "principal" che identifica il chiamante. La modalità con cui il gateway può ottenere il principale deve essere scelta tra le seguenti opzioni:

        - *Container*: il principal viene fornito direttamente dal container sul quale è in esecuzione il gateway.

        - *Header HTTP*: il principal viene estratto dallo specifico header http che viene indicato successivamente. È inoltre possibile attivare l'opzione *Forward Header* per far sì che il gateway propaghi il dato di autenticazione.

        - *Parametro della Url*: il principal viene estratto da un parametro della query string il cui nome viene indicato successivamente. È inoltre possibile attivare l'opzione *Forward Parametro Url* per far sì che il gateway propaghi il dato di autenticazione.

        - *Url di Invocazione*: il principal viene estratto direttamente dalla URL di invocazione tramite l'espressione regolare che viene fornita successivamente.

        - *Client IP*: il principal utilizzato è l'indirizzo IP di provenienza.

	- *X-Forwarded-For*: il principal viene estratto dall'header http utilizzato per il mantenimento dell’IP di origine nel caso di nodi intermedi (es. X-Forwarded-For).

	- *Token*: opzione presente solamente se è stata attivata, al passo precedente, l'autenticazione del token. Il principal viene letto da uno dei claim presenti nel token.

	Il flag *Opzionale* consente di non rendere bloccante il superamento dell'autenticazione nel caso la richiesta non possiede il principal atteso.

   -  *custom*: 
	metodo di autenticazione fornito tramite personalizzazioni di GovWay


   .. figure:: ../../_figure_console/Autenticazione.png
    :scale: 100%
    :align: center
    :name: autenticazione

    Configurazione dell’autenticazione del servizio







