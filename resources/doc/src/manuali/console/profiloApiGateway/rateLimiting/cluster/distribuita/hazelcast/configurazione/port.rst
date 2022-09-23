.. _headerGWRateLimitingCluster_distribuita_hazelcastConfig_port:

Porte dedicate ad ogni tipo di misurazione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Per ogni algoritmo utilizzato per collezionare i contatori relativi alle polici di RateLimiting (descritti nelle sezioni ':ref:`headerGWRateLimitingCluster_distribuita_hazelcast`' e ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastOpzioniAvanzate`') viene associata una istanza di Hazelcast dedicata attivata con una configurazione specifica.

Ad ogni istanza viene associato un cluster name differente formato dal valore configurato su govway (descritto nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_clusterName`') e un suffisso che riporta la modalità di conteggio selezionata (es. con un cluster name 'govway' le istanze verranno configurate come 'govway-atomic-long', 'govway-pncounter', ...).

Inoltre la configurazione associata ad ogni istanza deve possedere una porta univoca che consente l'attivazione di molteplici istanze Hazelcast. 

Per questo motivo la funzionalità 'auto-increment' deve rimanere disabilitata in modo che ogni nodo del cluster sappia esattamente la porta associata all'algoritmo configurato.

.. note::
  Per preservare l'associazione porta-algoritmo, se nel file *<directory-lavoro>/govway_local.hazelcast.yaml* (descritto nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_sharedConfig`') viene definito l'elemento 'port' la sua configurazione viene ignorata. Per modificare la porta si deve agire sulla configurazione specifica dell'algoritmo riportata di seguito in questa pagina.


Di seguito vengono riportate le configurazioni yaml di default associate agli algoritmi descritti nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcast`'.

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

