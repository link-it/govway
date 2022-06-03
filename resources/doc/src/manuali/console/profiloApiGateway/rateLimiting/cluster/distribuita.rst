.. _headerGWRateLimitingCluster_distribuita:

Sincronizzazione Distribuita
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

TODO
Una soluzione migliore, che consente di non essere legati al bilanciamento del carico, è quella in cui il conteggio viene attuato tramite un archivio dati centralizzato (es. Hazelcast o Redis). Questa soluzione presenta lo svantaggio dovuto all'aumento della latenza che impatterrà su ogni richiesta per via dell'archiviazione dei dati in remoto e per la concorrenza di tali operazioni.

