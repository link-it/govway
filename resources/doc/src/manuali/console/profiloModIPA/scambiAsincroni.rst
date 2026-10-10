.. _modipa_scambiAsincroni:

Scambi di Dati Asincroni PDND
------------------------------

Gli scambi di dati asincroni definiti dalla PDND consentono ad un e-service di rispondere in modo differito: il fruitore avvia l'interazione, l'erogatore lo notifica tramite una callback quando la risposta è disponibile e il fruitore la recupera successivamente, eventualmente confermandone la ricezione. Ogni passo dello scambio richiede un voucher PDND specifico, che riporta la fase dell'interazione e il suo identificativo.

GovWay gestisce lo scambio in modo trasparente sia per il fruitore che per l'erogatore: negozia i voucher con i claim previsti da ciascuna fase, determina e verifica la URL di callback, tiene traccia dello stato di ogni interazione rifiutando le fasi non ammesse o scadute e registra fase e URL di callback nelle transazioni. Le applicazioni si limitano a invocare le operazioni dell'API, scambiandosi l'identificativo dell'interazione tramite l'header di integrazione 'GovWay-Conversation-ID'.

Una descrizione delle fasi e del ruolo di GovWay viene fornita in :doc:`scambiAsincroni/panoramica`. Le sezioni successive descrivono la configurazione delle API, quella richiesta all'ente fruitore e all'ente erogatore, la gestione degli errori e le proprietà di configurazione avanzata.

.. toctree::
    :maxdepth: 2

    scambiAsincroni/panoramica
    scambiAsincroni/configurazioneApi
    scambiAsincroni/enteFruitore
    scambiAsincroni/enteErogatore
    scambiAsincroni/gestioneErrori
    scambiAsincroni/configurazioneProperties
