.. _auditingSetup:

Auditing
--------

In questa sezione descriviamo le modalità di configurazione del servizio
di auditing, al fine di definire quali informazioni devono essere
tracciate, con che formato e con che livello di dettaglio.

Gli utenti con permesso [C] Configurazione (vedi sezione :ref:`utenti`) hanno la possibilità
di configurare il servizio di auditing, al fine di stabilire cosa
tracciare, con che formato e con che livello di dettaglio.

L'accesso alla funzionalità di configurazione del servizio di auditing
avviene tramite la voce *Auditing* nella sezione *Configurazione* del
menu laterale sinistro.

Se la maschera si presenta come in :numref:`auditingDisabilitatoFig` il servizio di auditing è
disabilitato e quindi nessun dato verrà tracciato.

   .. figure:: ../_figure_console/AuditingSetupDisabled.jpg
    :scale: 100%
    :align: center
    :name: auditingDisabilitatoFig

    Servizio di auditing disabilitato

Modificando lo *Stato* del servizio di auditing in **Abilitato**
appariranno ulteriori campi nel form (vedi :numref:`auditingAbilitatoFig`) per effettuare le
impostazioni.

   .. figure:: ../_figure_console/AuditingSetupEnabled.jpg
    :scale: 100%
    :align: center
    :name: auditingAbilitatoFig

    Servizio di auditing abilitato

La configurazione del servizio di auditing avviene tramite la creazione
di una lista di **Filtri**, ciascuno dei quali stabilisce un criterio
per stabilire se una data informazione deve o non deve essere tracciata.
Alle informazioni cui non si applica nessuno dei filtri definiti, viene
applicato il comportamento di default, i cui parametri sono presenti
nella schermata principale del servizio. Facendo riferimento alla :numref:`auditingAbilitatoFig`
vediamo quali sono i parametri per specificare il comportamento di
default:

-  **Audit** (abilitato/disabilitato): Se abilitato, tutte le
   informazioni, cui non risulta applicabile nessuno dei filtri
   impostati, verranno tracciate dal servizio di auditing.

-  **Dump** (abilitato/disabilitato): Questo campo viene preso in
   considerazione quando *Audit = abilitato*. Stabilisce, nei casi in
   cui non si applica nessun filtro, se oltre a tracciare i campi che
   descrivono l'operazione, devono essere tracciate anche le strutture
   dati coinvolte.

-  **Formato Dump** (JSON/XML): Stabilisce il formato in cui vengono
   memorizzate le strutture dati di cui si è scelto di effettuare il
   dump. Le opzioni possibili sono tra il formato standard JSON
   (`http://www.json.org <http://www.json.org/>`__) e la sua
   rappresentazione in formato XML.

-  **Log4J Auditing** (abilitato/disabilitato): Questa opzione consente
   di abilitare/disabilitare l'appender log4j relativo ai dati tracciati
   dal servizio di auditing.

Una volta stabilito il comportamento di default si potranno definire i
filtri specifici. Per passare alla sezione di gestione dei filtri si
seleziona *Visualizza* nella sezione Filtri. Nell'area di gestione
filtri viene mostrata la lista dei filtri esistenti con la possibilità
di modificare/cancellare gli esistenti o inserirne di nuovi. Si può
aggiungere un nuovo filtro premendo il pulsante *Aggiungi*. In :numref:`auditingFiltro` è
mostrata la maschera per la creazione di un nuovo filtro di auditing.

   .. figure:: ../_figure_console/AuditingSetupFilter.jpg
    :scale: 100%
    :align: center
    :name: auditingFiltro

    Creazione di un filtro per il servizio di auditing

Facendo riferimento alla :numref:`auditingFiltro` vediamo in dettaglio il significato dei campi
di un filtro:

-  *Filtro Generico*

   -  **Utente**: è possibile specificare in questo campo uno username
      relativo ad un utente della govwayConsole del quale si vogliono
      tracciare le operazioni effettuate. Lasciare il campo di testo
      vuoto equivale a *Qualsiasi Utente*

   -  **Tipo Operazione** (ADD/CHANGE/DEL): Specifica il tipo di
      operazione che si vuole tracciare distinguendo tra operazioni di
      creazione, modifica e cancellazione. Lasciare il campo vuoto
      equivale a *Qualsiasi Tipo*.

   -  **Tipo Oggetto**: Questo campo è costituito da una lista
      contenente tutte le entità gestibili tramite l'interfaccia
      govwayConsole (ad esempio: Accordo di Servizio, Porta Delegata,
      ecc). Consente di restringere il tracciamento alle sole operazioni
      riguardanti una determinata entità. Lasciare il campo vuoto
      equivale a *Qualsiasi Tipo Oggetto*.

   -  **Stato Operazione** (requesting/error/completed): Consente di
      restringere le operazioni da tracciare in base al loro stato:

      -  *requesting*: indica un'operazione in fase di richiesta e non
         ancora completata

      -  *error*: Indica un'operazione completata che ha restituito un
         errore

      -  *completed*: Indica un'operazione che è terminata correttamente

      Lasciare il campo vuoto equivale a *Qualsiasi Stato Operazione*.

-  *Filtro per contenuto*

   -  **Stato** (abilitato/disabilitato): Opzione che consente di
      abilitare il filtro basato sul contenuto degli oggetti coinvolti
      nell'operazione. Se l'opzione viene abilitata compariranno i 2
      campi descritti ai passi successivi.

   -  **Tipo** (normale/espressioneRegolare): Descrive se la stringa
      riportata nel campo Dump deve essere interpretata come pattern o
      come espressione regolare.

   -  **Dump**: Campo di testo per inserire il pattern (o espressione
      regolare) sulla base del quale verranno filtrate le operazioni. Il
      sistema di auditing traccerà soltanto le operazioni che
      coinvolgeranno entità il cui contenuto corrisponde alla stringa
      specificata.

-  *Azione*: indica quale azione deve essere effettuata al verificarsi
   delle condizioni del filtro

   -  **Stato** (abilitato/disabilitato): Se abilitato, al verificarsi
      delle condizioni impostate nel filtro, i dati dell'operazione
      verranno tracciati.

   -  **Dump** (abilitato/disabilitato): Se *Stato = abilitato* è
      possibile specificare se si deve effettuare anche il dump delle
      entità coinvolte nell'operazione. Ad esempio, se viene tracciata
      un'operazione di modifica di un Accordo di Servizio, si decide se
      si vuole effettuare anche il dump dell'Accordo di Servizio oggetto
      della modifica.
