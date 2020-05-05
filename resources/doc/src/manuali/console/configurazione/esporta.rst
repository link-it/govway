.. _esporta:

Esporta
-------

L'esportazione dei dati di configurazione dalla govwayConsole è
possibile nei modi seguenti:

-  Selezionando singolarmente le entità di configurazione da esportare,
   come ad esempio "Erogazioni" o "API", e premendo il pulsante
   *Esporta* (:numref:`esportaSingola`).

   .. figure:: ../_figure_console/Esportazione.png
    :scale: 100%
    :align: center
    :name: esportaSingola

    Esportazione di singole entità del registro

   Dopo aver selezionato il pulsante "Esporta", una seconda maschera (:numref:`esportaScelte`)
   riporta le seguenti informazioni:

   -  *Profilo Interoperabilità*: indicazione del profilo cui fa
      riferimento l'esportazione.

   -  *Soggetto*: indicazione del dominio cui fa
      riferimento l'esportazione.

   -  *Tipologia archivio*: se previsto dal Profilo di Interoperabilità, fa selezionare la tipologia di
      archivio da produrre. Il default è il formato *Govlet* standard di
      esportazione di Govway.

   -  *Policy di Configurazione*: se il flag viene abilitato vengono incluse nell'archivio esportato le policy globali (Token, Rate Limiting) condivise tra più API

   -  *Elementi di Registro*: se il flag viene abilitato vengono incluse nell'archivio esportato anche gli elementi del registro riferiti da quelli selezionati.

   .. figure:: ../_figure_console/Esportazione2.png
    :scale: 100%
    :align: center
    :name: esportaScelte

    Esportazione di entità nel registro: parametri

-  Tramite la voce di menu *Configurazione > Esporta* che presenta le
   opzioni mostrate in :numref:`esportaFig`.

   .. figure:: ../_figure_console/Esportazione3.png
    :scale: 100%
    :align: center
    :name: esportaFig

    Esportazione di entità nel registro

   Le opzioni presenti sono:

   -  *Profilo Interoperabilità*: indica quale profilo riguarda
      l'esportazione che si sta effettuando

   -  *Tipologia Archivio*: nei casi che lo prevedono, consente di
      specificare il formato dell'archivio di esportazione da produrre.

   -  *Modalità*: consente di specificare cosa esportare tra le seguenti
      possibilità:

      -  *Esportazione completa*: esportazione dell'intero repository di
         configurazione (limitatamente al profilo di interoperabilità
         selezionato, se diverso da "Tutti").

      -  *Registro*: esporta solo le entità del registro (erogazioni,
         fruizioni, api, ecc)

      -  *Configurazione*: esporta solo le entità della sezione
         Configurazione (token policy, tracciamento, ecc).

Il formato dell'archivio prodotto come risultato dell'esportazione
dipende dalla modalità cui fanno riferimento le entità selezionate.
