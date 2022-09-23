.. _headerGWRateLimitingCluster_distribuita_hazelcastConfig_sharedConfig:

Discovery dei nodi del cluster
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La configurazione di default di hazelcast prova automaticamente a rilevare nodi del cluster presenti nella rete. Se in una medesima rete esistono cluster funzionali diversi, è necessario associare dei cluster name differenti in modo da raggruppare i nodi per gruppi funzionali (es. produzione, sviluppo, test). Maggiori informazioni su come modificare il cluster name vengono fornite nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_clusterName`'.

È possibile modificare la configurazione di discovery di default agendo sul file *<directory-lavoro>/govway_local.hazelcast.yaml*. Per una documentazione completa sugli elementi configurabili nel file è possibile consultare la documentazione di *Hazelcast*: 'https://docs.hazelcast.com/hazelcast/5.1/clusters/network-configuration'.

.. note::
  **Ambiente di Produzione** 
  In un ambiente di produzione non è consigliato utilizzare un discovery automatico (https://docs.hazelcast.com/hazelcast/5.1/clusters/discovery-mechanisms#auto-detection). Si consiglia di disabilitare l'elemento 'auto-detection' e di utilizzare un meccanismo alternativo come ad esempio il censimento puntuale dei nodi tramite l'elemento 'tpc-ip.member-list'.

La configurazione di rete definita nel file *<directory-lavoro>/govway_local.hazelcast.yaml* verrà applicata per tutte le istanze attivate relative agli algoritmi descritti nelle sezioni ':ref:`headerGWRateLimitingCluster_distribuita_hazelcast`' e ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastOpzioniAvanzate`'. 

Se nel file *<directory-lavoro>/govway_local.hazelcast.yaml* viene definito l'elemento 'port', la sua configurazione viene ignorata e non viene riportata sulle istanze attivate poichè, come descritto nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig_port`', ad ogni algoritmo viene associata una porta dedicata. Per modificare la porta si deve agire sulla configurazione specifica dell'algoritmo.

Per lo stesso motivo i membri definiti nell'elemento 'tpc-ip.member-list' non dovrebbero contenere l'indicazione della porta. In alternativa è possibile utilizzare la keyword 'GOVWAY_INSTANCE_PORT' che verrà sostituita per ogni algoritmo con la porta corretta.

