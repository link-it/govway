.. _headerGWRateLimitingCluster_distribuita_hazelcastConfig:

Configurazione di Hazelcast
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nelle sezioni successive viene mostrato come modificare le configurazioni Hazelcast di default utilizzate dai gestori delle policy di rate limiting descritti nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcast`.

Per ogni algoritmo utilizzato per collezionare i contatori relativi alle polici di RateLimiting viene associata una istanza di Hazelcast dedicata che viene attivata con una configurazione specifica configurabile nei seguenti aspetti:

- ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_clusterName`'
- ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_sharedConfig`'
- ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_port`'

.. toctree::
   :maxdepth: 2

   clusterName
   sharedConfig
   port
