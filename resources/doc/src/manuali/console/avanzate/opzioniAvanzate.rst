.. _configOpzioniAvanzate:

Opzioni Avanzate per Erogazioni/Fruizioni
-----------------------------------------

A partire dall'erogazione o fruizione di una API, accedendo alla sezione Configurazione, descritta nella sezione :ref:`configSpecifica`, in modalità avanzata compare
una sezione precedentemente non documentata denominata *Opzioni Avanzate*. 

All'interno di tale sezione è possibile configurare (:numref:`opzioniAvanzateAPI`):

- *Integrazione - Metadati*: per default non impostato, consente di attivare gli header di integrazione desiderati utilizzando le keyword, separate da virgola, descritta nella sezione :ref:`headerIntegrazione_other`.

- *Rate Limiting*: per default non impostato, consente di personalizzare le impostazioni che riguardano gli Header HTTP informativi restituiti ai client (vedi sezione :ref:`headerGWRateLimiting`) e il tipo di Rate Limiting in presenza di un cluster di nodi (vedi sezione :ref:`headerGWRateLimitingCluster`).

- *Handlers*: consente di attivare handler sul pipeline relativo alla gestione delle richieste o delle risposte di GovWay.

- *SOAP With Attachments - Gestione Body*: presente solamente per API di tipo SOAP consente tramite la voce 'allega' di spostare il contenuto presente nel body in un attachment o di eliminare il body dalla richiesta prima di inoltrare il messaggio.

   .. figure:: ../_figure_console/opzioniAvanzate.png
    :scale: 100%
    :align: center
    :name: opzioniAvanzateAPI

    Opzioni Avanzate di una API

