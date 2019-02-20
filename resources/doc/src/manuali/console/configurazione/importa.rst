.. _importa:

Importa
-------

L'importazione di entità nel registro può essere effettuata tramite la
sezione accessibile con la voce di menu *Importa* presente nella sezione
*Configurazione*.

Il form che compare per l'importazione è quello riportato in :numref:`importaFig`. I passi
da eseguire sono i seguenti:

-  Selezionare la modalità cui fanno riferimento le entità contenute
   nell'archivio da importare.

-  In base alla modalità selezionata potrebbero essere richieste
   ulteriori informazioni. Ad esempio, per il protocollo SPCoop, verrà
   richiesto quale tipo di archivio si vuole importare, a scelta tra:

   -  *spcoop*: il formato standard basato sulle specifiche SPCoop

   -  *govlet*: il formato di govway. Gli archivi con tale formato sono
      ottenibili o attraverso un'esportazione effettuabile tramite
      govwayConsole o scaricando le govlets disponibili sul sito del
      progetto che permettono di pre-configurare GovWay per uno
      specifico servizio

-  *Validazione Documenti* (disponibile solamente con interfaccia in
   modalità avanzata, per default è abilitato): Se attivato, questo flag
   indica che i documenti presenti nell'archivio vengono validati prima
   di essere importati (es. wsdl, xsd ...).

-  *Aggiornamento*: Se attivato, questo flag indica che l'archivio da
   importare costituisce un aggiornamento del registro attuale.

-  Selezionare dal filesystem il file che corrisponde all'archivio che
   deve essere importato.

   .. figure:: ../_figure_console/Importazione.png
    :scale: 100%
    :align: center
    :name: importaFig

    Importazione di entità nel registro
