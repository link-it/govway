.. _apiGwAutorizzazione:

Autorizzazione
^^^^^^^^^^^^^^

L'autorizzazione è un ulteriore meccanismo per il controllo degli
accessi tramite il quale è possibile specificare con maggior dettaglio
le richieste che sono in grado di essere accettate per l'accesso al
servizio.

I meccanismi supportati, per specificare i criteri di autorizzazione,
sono i seguenti:

-  *Autorizzazione Puntuale*: superato il processo di autenticazione,
   saranno accettate le sole richieste provenienti dai mittenti indicati
   singolarmente nella lista fornita con il criterio. Dopo aver
   abilitato questa opzione, ed aver confermato tramite il pulsante
   Invia, sarà possibile fornire la lista dei mittenti autorizzati ad
   accedere al servizio.

   I mittenti che possono essere indicati sono Soggetti (solo nel caso
   delle erogazioni) e Applicativi. Tali entità dovranno essere
   precedentemente registrate sulla govwayConsole seguendo le
   indicazioni fornite in sezione :ref:`soggetto` e :ref:`applicativo`.

.. note::
       L'opzione di autorizzazione sui soggetti è disponibile solo se è
       stata attivata l'autenticazione.

.. note::
       L'opzione di autorizzazione sugli applicativi, nel caso di una
       erogazione, viene utilizzata per gestire l'accesso al servizio da
       parte di applicativi interni al dominio di GovWay.

-  *Autorizzazione per Ruoli*: consente di concedere l'autorizzazione
   per il servizio solo ai richiedenti in possesso di determinati ruoli
   nel proprio profilo. Dopo aver barrato questa opzione, ed aver
   confermato tramite il pulsante Invia, sarà possibile fornire una
   lista dei ruoli che devono essere posseduti dal chiamante per poter
   accedere al servizio. In particolare si dovrà anche specificare la
   *fonte* di provenienza dei ruoli, che può essere *esterna*, cioè
   proveniente dal sistema che ha autenticato il chiamante, oppure
   *registro*, cioè i ruoli che sono stati censiti nel registro di
   GovWay e assegnati al soggetto chiamante. Inoltre si deve scegliere
   l'opzione *Ruoli Richiesti* per indicare se, in presenza di più di un
   ruolo come criterio, il chiamante deve possedere "tutti" i ruoli
   indicati o "almeno uno".

   Per le indicazioni sul censimento dei ruoli fare riferimento alla sezione :ref:`ruolo`.

-  *Autorizzazione per Scope*: criterio di autorizzazione che verifica
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

       **Note**

       L'opzione di autorizzazione basata sugli scope è disponibile solo
       se è stata preventivamente attivata la Gestione Token e
       selezionata la relativa policy.

-  *Autorizzazione per Token Claims*: Se è stata abilitata la gestione
   del token si ha la possibilità di autorizzare le richieste inserendo
   i valori ammessi per i claims contenuti nel token. La configurazione
   viene effettuata inserendo nel campo di testo ciascun claim in una
   riga, facendo seguire dopo l'uguale i valori ammessi separati da
   virgola.

       **Note**

       L'opzione di autorizzazione basata sui token è disponibile solo
       se è stata preventivamente attivata la Gestione Token e
       selezionata la relativa policy.

-  *XACML-Policy*: È possibile basare il meccanismo di autorizzazione
   sulla valutazione di una policy xacml selezionando la relativa
   opzione sulla lista "Stato".

   Per le indicazioni di dettaglio sulla configurazione delle
   xacml-Policy si faccia riferimento alla sezione :ref:`xacml`.

-  *Custom*: Sulla lista "Stato", è possibile selezionare questo metodo
   di autorizzazione eventualmente fornito tramite estensione di GovWay.
