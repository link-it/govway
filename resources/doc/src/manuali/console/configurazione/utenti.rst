.. _utenti:

Utenti
------

La sezione *Configurazione > Utenti* è dedicata alla gestione degli
utenti dei cruscotti grafici govwayConsole e govwayMonitor.

Prima di descrivere le funzionalità relative alla gestione utenti è
necessario fare una premessa sull'organizzazione dei permessi che sono
assegnabili ad un utente.

Le funzionalità delle console grafiche sono partizionate in gruppi cui
corrispondono puntuali permessi che possono essere concessi agli utenti
per limitarne l'operatività. Vediamo quali sono i gruppi funzionali, e
conseguentemente i permessi associabili a ciascun utente:

-  *Registro*

   -  *Gestione API [S]* - Gestione delle entità di configurazione dei
      servizi, quali: API, Erogazioni, Fruizioni, ecc.

-  *GovWay Monitor*

   -  *Monitoraggio [D]* - Accesso alle funzionalità di monitoraggio
      della console govwayMonitor.

   -  *Reportistica [R]* - Accesso alle funzionalità di reportistica
      della console govwayMonitor.

-  *Strumenti*

   -  *Auditing [A]* - Accesso alle funzionalità di consultazione delle
      tracce del servizio di Auditing.

-  *Configurazione*

   -  *[C]* - Accesso alle funzionalità di configurazione. Queste
      funzionalità sono quelle presenti nel menu di navigazione nel
      gruppo *Configurazione* e riguardano: tracciamento, controllo del
      traffico, import-export, ecc.

   -  *[U]* - Possibilità di gestire gli utenti delle console. Gli
      utenti con questo permesso, sono di fatto dei superutenti in
      quanto possono assumere l'identità di un qualunque utente del
      sistema.

-  *Altri Permessi (visibili solo configurazione specifica del
   prodotto)*

   -  *[P]* - Gestione delle entità di configurazione degli Accordi di
      Cooperazione e Servizi Composti.

   -  *[M]* - Accesso alle code messaggi sul gateway. Questa
      autorizzazione consente ad esempio di consultare i messaggi
      presenti nelle Message Box dell'Integration Manager ed
      eventualmente effettuare delle rimozioni.

L'applicazione, al termine dell'installazione, contiene una utenza
(credenziali indicate durante l'esecuzione dell'installer) che permette
di effettuare tutte le principali operazioni di gestione.

Gli utenti in possesso del permesso [U] possono creare dei nuovi utenti.
La maschera di creazione di un nuovo utente è quella mostrata in :numref:`utenteNew`.

   .. figure:: ../_figure_console/AggiungiUtente.png
    :scale: 40%
    :align: center
    :name: utenteNew

    Creazione nuovo utente

Le informazioni da inserire sono:

-  *Informazioni Utente*

   -  *Nome*

-  *Permessi di Gestione*: sezione che consente di assegnare i permessi
   all'utente e quindi decidere quali funzionalità rendergli
   accessibili.

-  *Profilo di Interoperabilità*: sezione che consente di decidere
   quali, tra i profili disponibili, rendere accessibili all'utente.

-  *Visibilità dati tramite govwayMonitor*: questa sezione è visibile
   solo se è stato abilitato uno dei permessi "GovWay Monitor". In
   questo contesto è possibile stabilire la visibilità dell'utente sulla
   console GovWay Monitor riguardo i seguenti:

   -  *Soggetti*: opzione visibile solo se attiva la modalità
      multi-tenant, consente di limitare la visibilità delle entità di
      monitoraggio ai soli soggetti interni indicati in una whitelist.
      Per configurare la whitelist è necessario salvare l'utente da
      creare e successivamente accedere in editing. In alternativa è
      possibile attivare il flag "Tutti" per non assegnare limitazioni.

   -  *API*: consente di limitare la visibilità delle entità di
      monitoraggio alle sole API indicate in una whitelist. Per
      configurare la whitelist è necessario salvare l'utente da creare e
      successivamente accedere in editing. In alternativa è possibile
      attivare il flag "Tutti" per non assegnare limitazioni.

-  *Modalità Interfaccia*: opzione per decidere quale modalità, tra
   standard e avanzata, è quella di default per l'utente.

-  *Password*: sezione per l'impostazione della password dell'utente.

La pagina indice della sezione Utenti visualizza gli utenti già presenti
nel sistema con i relativi permessi e i link per modificarli o assumerne
l'identità (:numref:`utenteList`)

   .. figure:: ../_figure_console/ElencoUtenti.png
    :scale: 100%
    :align: center
    :name: utenteList

    Lista degli utenti
