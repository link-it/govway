.. _headerGWRateLimitingCluster_distribuita_hazelcastConfig:

Configurazione di Hazelcast
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Di seguito vengono mostrate le configurazioni Hazelcast di default utilizzate dai gestori delle policy di rate limiting descritti nella sezione :ref:`headerGWRateLimitingCluster_distribuita`.

Ad ogni tipo di gestore viene associata una istanza di Hazelcast dedicata che viene attivata con una configurazione specifica.

.. note::
  **Cluster Name.** 
  Per ogni gestore viene utilizzato un cluster name differente formato dal valore configurabile nella proprietà *org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.HAZELCAST.group_id* (default govway) agendo sul file *<directory-lavoro>/govway_local.properties*. Al valore indicato viene aggiunto un suffisso che riporta la modalità selezionata.

.. note::
  **Port.** 
  La configurazione associata ad ogni gestore deve possedere una porta univoca che consenta l'attivazione di molteplici istanze Hazelcast. La funzionalità 'auto-increment' deve rimanere disabilitata in modo che ogni nodo del cluster sappia esattamente la porta associata al tipo di gestore configurato.

- *Misurazione Esatta*:

   ::

      hazelcast:
        cluster-name: govway
        map:
          "hazelcast-*-rate-limiting":
            in-memory-format: BINARY
      
        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5701

- *Near Cache*:

   ::

      hazelcast:
        cluster-name: govway
        map:
          "hazelcast-*-rate-limiting":
            in-memory-format: BINARY
            backup-count: 0
            async-backup-count: 1
      
            near-cache:
              hazelcast-near-cache:
                in-memory-format: BINARY
                serialize-keys: false

        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5702

- *Local Cache*::

   ::

      hazelcast:
        cluster-name: govway
        map:
          "hazelcast-*-rate-limiting":
            in-memory-format: BINARY
            backup-count: 0
            async-backup-count: 1
      
        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5703

- *Misurazione approssimata con "get and set sincrono"*:

   ::

      hazelcast:
        cluster-name: govway
        map:
          "hazelcast-*-rate-limiting":
            in-memory-format: BINARY
            backup-count: 0
            async-backup-count: 1
      
            near-cache:
              hazelcast-near-cache:
                in-memory-format: BINARY
                serialize-keys: false

        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5704

- *Misurazione approssimata con "get and set asincrono"*:

   ::

      hazelcast:
        cluster-name: govway
        map:
          "hazelcast-*-rate-limiting":
            in-memory-format: BINARY
            backup-count: 0
            async-backup-count: 1
      
            near-cache:
              hazelcast-near-cache:
                in-memory-format: BINARY
                serialize-keys: false

        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5705

È possibile utilizzare una configurazione differente da quella di default definendo un file di configurazione yaml nella *<directory-lavoro>* di govway specifico per ogni modalità:

- *Misurazione Esatta*: *<directory-lavoro>/govway.hazelcast.yaml*

- *Near Cache*: *<directory-lavoro>/govway.hazelcast-near-cache.yaml*

- *Local Cache*: *<directory-lavoro>/govway.hazelcast-local-cache.yaml*

- *Misurazione approssimata con "get and set sincrono*: *<directory-lavoro>/govway.hazelcast-near-cache-unsafe-sync-map.yaml*

- *Misurazione approssimata con "get and set asincrono*: *<directory-lavoro>/govway.hazelcast-near-cache-unsafe-async-map.yaml*

