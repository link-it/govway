.. _apiGwAutenticazione:

Autenticazione
^^^^^^^^^^^^^^

In questa sezione è possibile configurare il meccanismo di
autenticazione richiesto per l'accesso al servizio. Come mostrato in :numref:`autenticazione`,
si possono specificare:

-  Il tipo di autenticazione, distinto in base al protocollo di trasporto, selezionando uno tra i valori disponibili:

   -  *disabilitato*: nessuna autenticazione

   -  *ssl*: autenticazione ssl

   -  *basic*: autenticazione http-basic. Scegliendo questa opzione sarà anche possibile selezionare l'ulteriore opzione *Forward Authorization* per far sì che il gateway propaghi al destinatario l'header http "Authorization".

   -  *principal*: autenticazione di tipo principal. Selezionando questa modalità è necessario scegliere ulteriormente il tipo preciso di autenticazione tra le seguenti opzioni:

        - *Container*: il principal viene fornito direttamente dal container sul quale è in esecuzione il gateway.

        - *HeaderBased*: il principal viene estratto dallo specifico header http che viene indicato successivamente. È inoltre possibile attivare l'opzione *Forward Header* per far sì che il gateway propaghi il dato di autenticazione.

        - *FormBased*: il principal viene estratto da un parametro della query string il cui nome viene indicato successivamente. È inoltre possibile attivare l'opzione *Forward Parametro Url* per far sì che il gateway propaghi il dato di autenticazione.

        - *UrlBased*: il principal viene estratto direttamente dalla URL di invocazione tramite l'espressione regolare che viene fornita successivamente.

        - *Indirizzo IP*: il principal utilizzato è l'indirizzo IP di provenienza.

	- *Token*: opzione presente solamente se è stata attivata, al passo precedente, la gestione del token. Il principal viene letto da uno dei claim presenti nel token.

   -  *custom*: metodo di autenticazione fornito tramite estensione di
      GovWay

   Il flag *Opzionale* consente di non rendere bloccante il superamento
   dell'autenticazione per l'accesso al servizio.per il quale si procede come già descritto
   per l'attività di creazione dell'erogazione nella sezione :ref:`erogazione`.

-  Se è stata attivata, al passo precedente, la gestione del token sarà
   possibile aggiungere ulteriori criteri di autenticazione basati sul
   contenuto del token ricevuto. In tal caso è possibile autenticare la
   richiesta sulla base delle seguenti metainformazioni presenti nel
   token: Issuer, ClientId, Subject, Username, Email.

i criteri di autenticazione possono essere attuati sia a livello del
trasporto che del token (se abilitata la gestione del token al passo
precedente).

   .. figure:: ../../_figure_console/Autenticazione.png
    :scale: 100%
    :align: center
    :name: autenticazione

    Configurazione dell’autenticazione del servizio







