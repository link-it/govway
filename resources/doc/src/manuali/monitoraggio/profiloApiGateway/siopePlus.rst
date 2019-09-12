.. _monitor_profiloAPIGateway_siopePlus:

SIOPE+
--------

Il Govlet "SIOPE+" è un wizard di configurazione per creare, nella maniera più rapida, le entità del registro di Govway per rendere operativi i flussi da scambiare con la piattaforma SIOPE+.
L'esecuzione del Govlet SIOPE+ produce una fruizione, riferita ad una WebAPI Rest, che consente di effettuare tutte le operazioni previste dal relativo protocollo di colloquio.

Al fine di rendere più efficaci le attività di monitoraggio dei flussi di comunicazione con SIOPE+, il Govlet attiva la correlazione applicativa per l'estrazione di informazioni dalle chiamate in transito. Grazie alla correlazione applicativa è possibile effettuare ricerche sulla console govwayMonitor, utilizzando gli identificativi estratti, come filtro, e visualizzare tali dati nel dettaglio delle transazioni. 

Il meccanismo di tracciamento attivato in configurazione prevede che vengano estratte le seguenti informazioni dalle URL di richiesta dell'applicativo chiamante:

- Codice Intermediario (IdA2A)
- Codice Ente (codice operatore)

Tali identificativi vengono memorizzati correlandoli alla transazione di appartenenza. In tal modo è possibile utilizzare la funzione di consultazione della console govwayMonitor, alla sezione "Monitoraggio > Storico", selezionando l'opzione "Identificativo Applicativo". La ricerca si perfeziona inserendo uno dei codici sopra citati nel campo "ID Applicativo", specificando che si tratta di una ricerca non esatta, poiché l'identificativo estratto è la concatenazione di due valori distinti (:numref:`SiopePlus-Ricerca`).

   .. figure:: ../_figure_monitoraggio/SiopePlus-Ricerca.jpg
    :scale: 100%
    :align: center
    :name: SiopePlus-Ricerca

    SIOPE+: elenco delle transazioni relative ad un codice ente

Consultando il dettaglio della transazione è possibile visualizzare l'intero identificativo estratto, tra le proprietà dell'elemento (:numref:`SiopePlus-Dettaglio`).

   .. figure:: ../_figure_monitoraggio/SiopePlus-Dettaglio.jpg
    :scale: 100%
    :align: center
    :name: SiopePlus-Dettaglio

    SIOPE+: Codice Ente e Codice Intermediario associato ad ogni transazione
