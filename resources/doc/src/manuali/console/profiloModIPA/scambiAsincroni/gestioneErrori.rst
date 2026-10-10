.. _modipa_scambiAsincroni_errori:

Gestione degli Errori
---------------------

Le richieste che non rispettano lo stato dell'interazione o i parametri dello scambio vengono rifiutate da GovWay, sia nelle fruizioni che nelle erogazioni, senza essere inoltrate. L'errore viene restituito secondo le modalità previste da GovWay (:ref:`erroriGovWay`): per le API REST come Problem Details (RFC 7807), per le API SOAP come SOAP Fault. Il dettaglio dell'errore riporta la causa specifica.

- *AsyncInteractionNotFound* (400): l'interazione indicata nell'header 'GovWay-Conversation-ID' non risulta registrata o non è associata all'API, al fruitore o al consumer della richiesta (:ref:`errori_400_AsyncInteractionNotFound`).

- *AsyncInteractionExpired* (400): è scaduto il tempo previsto dall'API per la fase richiesta, cioè il *Tempo massimo di risposta* per la callback o la *Durata disponibilità dato* per l'ottenimento della risposta e la conferma (:ref:`errori_400_AsyncInteractionExpired`).

- *AsyncInteractionInvalidRequest* (400): la richiesta non è valida per la fase, ad esempio identificativo dell'interazione, numero di entità o URL di callback mancanti o non validi, numero di entità superiore al *Numero massimo risultati*, voucher privo dei claim previsti, URL di callback non corrispondente alle fruizioni dell'API di callback (:ref:`errori_400_AsyncInteractionInvalidRequest`).

- *AsyncInteractionInvalidState* (409): la fase richiesta non è ammessa nello stato attuale dell'interazione, ad esempio callback già invocata, risposta non ancora disponibile perché la callback non è stata ricevuta, conferma richiesta prima di aver ottenuto la risposta, interazione già confermata (:ref:`errori_409_AsyncInteractionInvalidState`).

Gli errori non imputabili al client, ad esempio una configurazione incompleta (es. erogazione dell'API di callback non presente o non unica) o un errore nell'accesso alle informazioni sulle interazioni, vengono restituiti come *APIUnavailable* (503) (:ref:`errori_503_APIUnavailable`).

Le richieste rifiutate non modificano lo stato dell'interazione. Lo stesso vale per le fasi a cui l'applicativo o l'erogatore risponde con un codice HTTP non previsto dall'*Esito Positivo*: la fase non viene registrata e può essere ripetuta.
