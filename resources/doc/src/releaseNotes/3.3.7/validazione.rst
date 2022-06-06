Miglioramenti alla funzionalità di Validazione dei Contenuti
------------------------------------------------------------

Aggiunto supporto per le richieste 'multipart/form-data' e 'multipart/mixed':

- https://swagger.io/docs/specification/describing-request-body/multipart-requests/

- https://swagger.io/docs/specification/describing-request-body/file-upload/

È stata inoltre introdotta la possibilità di ottimizzare (su una singola erogazione o fruizione) la validazione dei messaggi ricevuti, sospendendo l'analisi dello stream dopo aver validato tutti i metadati non binari previsti dall'interfaccia OpenAPI.

.. note::

   Benchè l'ottimizzazione consenta di ottenere significativi benefici prestazionali, rappresentando tipicamente le parti binarie la maggior dimensione del messaggio, non viene attivata per default poichè non consente di individuare se esistano "part" non previste dalla specifica (in presenza di 'additionalProperties=false') successive alle "part" non binarie.

