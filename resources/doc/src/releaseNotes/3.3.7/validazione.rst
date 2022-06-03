Miglioramenti alla funzionalità di Validazione dei Contenuti
------------------------------------------------------------

Aggiunto supporto delle richieste 'multipart/form-data' e 'multipar/mixed':

- https://swagger.io/docs/specification/describing-request-body/multipart-requests/

- https://swagger.io/docs/specification/describing-request-body/file-upload/

È stata inoltre aggiunta la possibilità di attivare puntualmente su una erogazione o una fruizione la seguente ottimizzazione che si basa sul concetto per cui le parti "binarie" non richiedono una validazione rispetto ad uno schema e sono tipicamente serializzate dopo i metadati (plain o json):

- l'analisi dello stream termina dopo aver validato tutti i metadati non binari definiti nell'interfaccia OpenAPI. 

.. note::

   Benche l'ottimizzazione consente di avere benefici prestazionali visto che tipicamente le parti binarie rappresentano la maggior dimensione del messaggio in termini di bytes, non viene attivata per default poichè non consente di individuare se esistono "part" non definite nella specifica (in presenza di 'additionalProperties=false') inserite dopo le "part" non binarie.

