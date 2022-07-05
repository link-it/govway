.. _headerGWRateLimitingCluster_distribuita_redis:

Redis
~~~~~~~~~~

Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito implementato tramite Redis (https://redis.io/).

La url di connessione verso il database Redis deve essere configurata sul file *<directory-lavoro>/govway_local.properties* tramite la seguente proprietà:

   ::

      # Connection Url (possono essere fornite più url separate da virgola)
      # usare rediss:// per TLS (con due s)
      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.connectionUrl=redis://<HOST>:<PORT>

Su Redis viene attualmente supportata una tecnica di sincronizzazione con misurazione esatta attivabile impostando la sincronizzazione '*Distribuita*', implementazione '*Redis*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo atomic-long-counters*' (:numref:`configurazioneSincronizzazioneRateLimitingRedisAtomicLongCounters`). Con questa modalità sia il dato 'master' che quelli locali al nodo risultano essere sempre aggiornati.

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingRedisAtomicLongCounters.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingRedisAtomicLongCounters

    Sincronizzazione Distribuita 'Redis' con misurazione delle metriche esatta
