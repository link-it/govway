.. _apiGwAutorizzazione:

Autorizzazione
^^^^^^^^^^^^^^

L'autorizzazione è un ulteriore meccanismo per il controllo degli
accessi tramite il quale è possibile specificare con maggior dettaglio
le richieste che sono in grado di essere accettate per l'accesso al
servizio.

I meccanismi supportati, per specificare i criteri di autorizzazione,
sono i seguenti:

-  *Autorizzazione Trasporto per Richiedente*: (:numref:`controlloAccessiAutorizzazioneRichiedente`) superato il processo di autenticazione trasporto,
   saranno accettate le sole richieste provenienti dai mittenti indicati
   singolarmente nella lista fornita con il criterio. Dopo aver
   abilitato questa opzione, ed aver confermato tramite il pulsante
   Invia, sarà possibile fornire la lista dei mittenti autorizzati ad
   accedere al servizio.

   I mittenti che possono essere indicati sono Soggetti (solo nel caso
   delle erogazioni) e Applicativi. Tali entità dovranno essere
   precedentemente registrate sulla govwayConsole seguendo le
   indicazioni fornite in sezione :ref:`soggetto` e :ref:`applicativo` e dovranno possedere credenziali di accesso compatibili con l'autenticazione di trasporto configurata.

   .. note::
       L'opzione di autorizzazione trasporto per richiedente è disponibile solo se è
       stata attivata l'autenticazione trasporto.

   .. _controlloAccessiAutorizzazioneRichiedente:

   .. figure:: ../../_figure_console/AutorizzazioneRichiedente.png
    :scale: 80%
    :align: center

    Configurazione Autorizzazione Trasporto per Richiedente

-  *Autorizzazione Trasporto per Ruoli*: (:numref:`controlloAccessiAutorizzazioneRuoli`) consente di concedere l'autorizzazione
   per il servizio solo ai richiedenti in possesso di determinati ruoli
   nel proprio profilo. Dopo aver barrato questa opzione, ed aver
   confermato tramite il pulsante Invia, sarà possibile fornire una
   lista dei ruoli che devono essere posseduti dal chiamante per poter
   accedere al servizio. In particolare si dovrà anche specificare la
   *fonte* di provenienza dei ruoli, che può essere *esterna*, cioè
   proveniente dal sistema che ha autenticato il chiamante, oppure
   *registro*, cioè i ruoli che sono stati censiti nel registro di
   GovWay e assegnati all'applicativo o al soggetto chiamante identificato tramite l'autenticazione trasporto. Inoltre si deve scegliere
   l'opzione *Ruoli Richiesti* per indicare se, in presenza di più di un
   ruolo come criterio, il chiamante deve possedere "tutti" i ruoli
   indicati o "almeno uno".

   Per le indicazioni sul censimento dei ruoli fare riferimento alla sezione :ref:`ruolo`.

   .. _controlloAccessiAutorizzazioneRuoli:

   .. figure:: ../../_figure_console/AutorizzazioneRuoli.png
    :scale: 80%
    :align: center

    Configurazione Autorizzazione Trasporto per Ruoli

-  *Autorizzazione Trasporto per Richiedente e per Ruoli*: abilitando entrambi i criteri autorizzativi descritti precedentemente, il chiamante verrà autorizzato se soddisfa almeno uno dei due criteri.

-  *Autorizzazione Token per Richiedente*: (:numref:`controlloAccessiAutorizzazioneRichiedenteToken`) superato il processo di autenticazione token,
   saranno accettate le sole richieste provenienti dai mittenti indicati
   singolarmente nella lista fornita con il criterio. Dopo aver
   abilitato questa opzione, ed aver confermato tramite il pulsante
   Invia, sarà possibile fornire la lista dei mittenti autorizzati ad
   accedere al servizio.

   I mittenti che possono essere indicati sono Applicativi. Tali entità dovranno essere
   precedentemente registrate sulla govwayConsole seguendo le
   indicazioni fornite in :ref:`applicativo` e dovranno possedere credenziali di accesso di tipo 'token'.

   .. note::
       L'opzione di autorizzazione basata sui token è disponibile solo
       se è stata preventivamente attivata la Gestione Token e
       selezionata la relativa policy.

   .. _controlloAccessiAutorizzazioneRichiedenteToken:

   .. figure:: ../../_figure_console/AutorizzazioneRichiedenteToken.png
    :scale: 80%
    :align: center

    Configurazione Autorizzazione Token per Richiedente

-  *Autorizzazione Token per Ruoli*: (:numref:`controlloAccessiAutorizzazioneRuoliToken`) simile all'autorizzazione trasporto per ruoli, si differenzia nel fatto che i ruoli devono essere posseduti dagli applicativi censiti nel registro di GovWay tramite credenziali di tipo *token* (*fonte* di provenienza dei ruoli impostata a *registro*) o devono essere presenti all'interno del token ricevuto (*fonte* di provenienza dei ruoli impostata a *esterna*). Come per l'autorizzazione per trasporto è possibile scegliere
   l'opzione *Ruoli Richiesti* per indicare se, in presenza di più di un
   ruolo come criterio, il chiamante deve possedere "tutti" i ruoli
   indicati o "almeno uno".

   Per le indicazioni sul censimento dei ruoli fare riferimento alla sezione :ref:`ruolo`.

   .. note::
       L'opzione di autorizzazione basata sui token è disponibile solo
       se è stata preventivamente attivata la Gestione Token e
       selezionata la relativa policy.

   .. _controlloAccessiAutorizzazioneRuoliToken:

   .. figure:: ../../_figure_console/AutorizzazioneRuoliToken.png
    :scale: 80%
    :align: center

    Configurazione Autorizzazione Token per Ruoli

-  *Autorizzazione Token per Richiedente e per Ruoli*: abilitando entrambi i criteri autorizzativi descritti precedentemente, il chiamante verrà autorizzato se soddisfa almeno uno dei due criteri.

-  *Autorizzazione per Token Claims*: (:numref:`controlloAccessiAutorizzazioneTokenClaims`) Se è stata abilitata la gestione
   del token si ha la possibilità di autorizzare le richieste inserendo
   i valori ammessi per i claims contenuti nel token. La configurazione
   viene effettuata inserendo nel campo di testo ciascun claim in una
   riga, facendo seguire dopo l'uguale i valori ammessi separati da
   virgola.

   Per le indicazioni di dettaglio sui possibili controlli effettuabili su ogni claim si faccia riferimento alla sezione :ref:`tokenClaims`.

   .. note::

       L'opzione di autorizzazione basata sui token è disponibile solo
       se è stata preventivamente attivata la Gestione Token e
       selezionata la relativa policy.

   .. _controlloAccessiAutorizzazioneTokenClaims:

   .. figure:: ../../_figure_console/AutorizzazioneTokenClaims.png
    :scale: 90%
    :align: center

    Configurazione Autorizzazione per Token Claims

-  *Autorizzazione per Token Scope*: (:numref:`controlloAccessiAutorizzazioneScope`) criterio di autorizzazione che verifica
   la corrispondenza tra gli scope indicati e quelli estratti dal token
   presente nella richiesta ricevuta. Una volta attivata l'opzione si
   deve effettuare una scelta per l'elemento *Scope Richiesti*, tra i
   valori "tutti" (tutti gli scope indicati devono essere presenti nel
   token per superare l'autorizzazione) e "almeno uno" (è richiesta la
   presenza di almeno uno scope tra quelli indicati nella policy di
   autorizzazione). Dopo aver confermato la scelta con il pulsante
   "Invia" verrà richiesto di inserire gli scope tra quelli già censiti
   ed abilitati per l'uso nei contesti di erogazione (o qualsiasi
   contesto).

   Per le indicazioni sul censimento degli scope fare riferimento alla sezione :ref:`apiGwScope`.

   .. note::

       L'opzione di autorizzazione basata sugli scope è disponibile solo
       se è stata preventivamente attivata la Gestione Token e
       selezionata la relativa policy.

   .. _controlloAccessiAutorizzazioneScope:

   .. figure:: ../../_figure_console/AutorizzazioneScope.png
    :scale: 80%
    :align: center

    Configurazione Autorizzazione per Token Scope

-  *XACML-Policy*: (:numref:`controlloAccessiAutorizzazioneXACML`) È possibile basare il meccanismo di autorizzazione
   sulla valutazione di una policy xacml selezionando la relativa
   opzione sulla lista "Stato".

   Per le indicazioni di dettaglio sulla configurazione delle
   xacml-Policy si faccia riferimento alla sezione :ref:`xacml`.

   .. _controlloAccessiAutorizzazioneXACML:

   .. figure:: ../../_figure_console/AutorizzazioneXACML.png
    :scale: 80%
    :align: center

    Configurazione Autorizzazione XACML-Policy

-  *Custom*: Sulla lista "Stato", è possibile selezionare questo metodo
   di autorizzazione eventualmente fornito tramite estensione di GovWay.
