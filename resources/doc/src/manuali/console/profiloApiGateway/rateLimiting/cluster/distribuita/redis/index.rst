.. _headerGWRateLimitingCluster_distribuita_redis:

Redis
~~~~~~~~~~

Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito implementato tramite Redis (https://redis.io/).

Su Redis viene attualmente supportata una tecnica di sincronizzazione con misurazione esatta attivabile impostando la sincronizzazione '*Distribuita*', implementazione '*Redis*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo atomic-long-counters*' (:numref:`configurazioneSincronizzazioneRateLimitingRedisAtomicLongCounters`). Con questa modalità sia il dato 'master' che quelli locali al nodo risultano essere sempre aggiornati.

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingRedisAtomicLongCounters.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingRedisAtomicLongCounters

    Sincronizzazione Distribuita 'Redis' con misurazione delle metriche esatta

Nella sezione :ref:`headerGWRateLimitingCluster_distribuita_redis_connessione` viene descritto come configurare la connessione verso il database Redis (modalità cluster o single-endpoint, autenticazione, TLS), mentre nella sezione :ref:`headerGWRateLimitingCluster_distribuita_redis_ttl` viene descritta la configurazione del TTL applicato ai contatori.

.. toctree::
   :maxdepth: 2

   connessione
   configurazione

