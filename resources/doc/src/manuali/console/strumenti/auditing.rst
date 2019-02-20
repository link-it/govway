.. _auditing:

Auditing
--------

La funzionalità di *auditing* consente di tracciare il comportamento
degli utenti della govwayConsole, al fine di verificare le operazioni
eseguite e i loro effetti.

Per gli aspetti di configurazione della funzionalità di auditing si
rimanda alla sezione :ref:`auditingSetup`.

In questa sezione descriviamo le interfacce della govwayConsole dedicate
alla consultazione delle informazioni raccolte tramite il servizio di
auditing.

Gli utenti della govwayConsole aventi il permesso [A] Auditing (vedi :ref:`utenti`)
hanno accesso alla funzionalità di consultazione dei dati presenti nel
repository del servizio di auditing.

Per accedere al servizio di consultazione selezionare la voce
**Auditing** nella sezione **Reportistica** del menu laterale sinistro.
La consultazione dei dati di auditing avviene tramite ricerche
effettuate impostando i criteri attraverso il form riportato in :numref:`auditingSearch`.

   .. figure:: ../_figure_console/AuditingQuerySearch.jpg
    :scale: 100%
    :align: center
    :name: auditingSearch

    Maschera di ricerca dei dati di auditing

Vediamo adesso il significato dei parametri per la ricerca dei dati di
auditing:

-  *Criteri di Ricerca*

   -  **Inizio Intervallo**: Data iniziale che serve ad impostare
      l'intervallo temporale su cui restringere la ricerca dei dati di
      auditing. Lasciare il campo vuoto equivale all'impostazione
      *illimitato*.

   -  **Fine Intervallo**: Data finale che serve ad impostare
      l'intervallo temporale su cui restringere la ricerca dei dati di
      auditing. Lasciare il campo vuoto equivale all'impostazione
      *illimitato*.

   -  **Utente**: Consente di restringere la ricerca alle sole
      operazioni effettuate da un determinato utente. Il campo lasciato
      vuoto equivale a *qualasiasi utente*.

-  *Operazione*

   -  **Tipo**: Filtro per tipo di operazione, distinguendo tra:

      -  *ADD*: creazione di un'entità

      -  *CHANGE*: modifica di un'entità

      -  *DEL*: cancellazione di un'entità

      -  *LOGIN*: accesso alla govwayConsole

      -  *LOGOUT*: disconnessione dalla govwayConsole

   -  **Stato**: Filtro in base allo stato dell'operazione, distinguendo
      tra:

      -  *requesting*: in fase di richiesta

      -  *error*: terminata con errore

      -  *completed*: terminata correttamente

-  *Oggetto*

   -  **Tipo**: campo per restringere la ricerca alle sole operazioni
      riferite ad un determinato tipo di entità. Il campo è costituito
      da una lista a discesa popolata con tutte le tipologie di entità
      gestite dalla govwayConsole.

   -  **Identificativo**: campo testuale per restringere la ricerca alle
      sole operazioni effettuate su una specifica entità. La
      composizione dell'identificativo cambia in base alla tipologia
      dell'entità. Ad esempio un soggetto è identificato attraverso il
      tipo e il nome: Tipo/NomeSoggetto.

   -  **Id precedente alla modifica**: campo testuale analogo al
      precedente ma utile in quei casi in cui l'operazione che si sta
      cercando ha modificato i dati che compongono l'identificativo.

   -  **Contenuto**: pattern per la ricerca sul contenuto dell'entità
      associata all'operazione. Per utilizzare questo criterio di filtro
      il servizio di auditing deve essere configurato in modo da
      effettuare il dump degli oggetti.

Una volta effettuata la ricerca viene mostrata una pagina con la lista
dei risultati corrispondenti (vedi :numref:`auditingResult`).

   .. figure:: ../_figure_console/AuditingQueryResult.jpg
    :scale: 100%
    :align: center
    :name: auditingResult

    Risultato della ricerca dei dati di auditing

Ciascun elemento della lista
riporta i dati principali che identificano l'operazione. Selezionando
l'identificatore dell'operazione si visualizzano i dati di dettaglio
(vedi :numref:`auditingDetail`). Dal dettaglio dell'operazione, se è attivo il dump, si può
visualizzare il dettaglio dell'entità coinvolta nell'operazione e gli
eventuali documenti binari (ad esempio i file WSDL associati ad un
accordo di servizio).

   .. figure:: ../_figure_console/AuditingQueryDetail.jpg
    :scale: 100%
    :align: center
    :name: auditingDetail

    Dettaglio di una traccia di auditing
