.. _headerGWRateLimitingCluster_distribuita_hazelcastConfig_clusterName:

Nome del Cluster
~~~~~~~~~~~~~~~~~~

Tutti i nodi che concorrono a formare un cluster hazelcast sono identificati da un nome di cluster identificativo. Se il processo di discovery dei nodi in una rete non utilizza un censimento puntuale dei nodi (es. lista di indirizzi ip) ma tecniche di auto-discovery o multicast, può essere necessario utilizzare un nome di cluster differente per raggruppare nodi della stessa rete in gruppi funzionali differenti (es. produzione, sviluppo, test). Ulteriori dettagli su come configurare il processo di discovery vengono forniti nella successiva sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_sharedConfig`'.

Per default il nome del cluster utilizzato è *govway*, ma è possibile ridefinirlo tramite 2 modalità:

- definire la proprietà *org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.HAZELCAST.group_id* (default govway) agendo sul file *<directory-lavoro>/govway_local.properties*;

- definire l'elemento *cluster-name* all'interno del file *<directory-lavoro>/govway_local.hazelcast.yaml*.

.. note::
  Nel caso vengano utilizzate entrambe le modalità sopra descritte, la definizione all'interno del file *<directory-lavoro>/govway_local.hazelcast.yaml* prevale.

