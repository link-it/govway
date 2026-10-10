.. _errori_409_AsyncInteractionInvalidState:

AsyncInteractionInvalidState
----------------------------

L'errore segnala che la fase richiesta non è ammessa nello stato attuale di uno scambio di dati asincrono PDND: ad esempio interazione già avviata o già confermata, callback già invocata, risposta non ancora disponibile perché la callback non è stata ricevuta, conferma di ricezione richiesta prima di aver ottenuto la risposta.

Il dettaglio dell'errore riporta la causa specifica.

Per maggiori dettagli sugli scambi di dati asincroni PDND è possibile consultare la sezione :ref:`modipa_scambiAsincroni` (in particolare :ref:`modipa_scambiAsincroni_errori`), mentre per il profilo di interoperabilità in generale la sezione :ref:`profiloModIPA`.
