.. _headerGWRateLimitingCluster_distribuita_hazelcastConfig:

Configurazione di Hazelcast
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Di seguito vengono mostrate le configurazioni Hazelcast di default utilizzate dai gestori delle policy di rate limiting descritti nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcast`.

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

        network:
          port:
            auto-increment: false
            port: 5701

- *Misurazione Approssimata*:

   ::

      hazelcast:
        cluster-name: govway

        pn-counter:
          "pncounter-*-rl":
            # Lasciare abilitata la statistica altrimenti si ha il seguente bug nel govway_hazelcast.log:
            # java.lang.NullPointerException: null
            #  at com.hazelcast.internal.crdt.pncounter.PNCounterService.merge(PNCounterService.java)
            statistics-enabled: true

        network:
          port:
            auto-increment: false
            port: 5702

È possibile utilizzare una configurazione differente da quella di default definendo un file di configurazione yaml nella *<directory-lavoro>* di govway specifico per ogni modalità:

- *Misurazione Esatta*: *<directory-lavoro>/govway.hazelcast-atomic-long-counters.yaml*

- *Misurazione Approssimata*: *<directory-lavoro>/govway.hazelcast-pn-counters.yaml*

