.. _headerGWRateLimitingCluster_distribuita_hazelcastOpzioniAvanzate:

Algoritmi Alternativi di Hazelcast
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le modalità in cui vengono collezionati i contatori relativi alle polici di RateLimiting in GovWay sono due:

- contatori relativi ad ogni informazione (numero di richieste, banda, intervallo temporale, richieste attive) gestiti singolarmente;

- contatori raggruppati in un oggetto che viene salvata in una mappa.

La seconda modalità, utilizzata nelle precedenti versioni di GovWay e descritta in questa sezione, è meno performante rispetto all'utilizzo dei singoli contatori descritti nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcast`'. Le modalità vengono comunque mantenute sia per backward compatibility che per supportare scenari avanzati di policy (vedi sezione ':ref:`registroPolicy`') che richiedono una gestione raggruppata in un unico oggetto dei vari contatori (es. intervallo con finestra 'precedente').


*Differenti tecniche di sincronizzazione su una mappa*

Nella sezione ':ref:`headerGWRateLimitingCluster_distribuita_hazelcast`' sono già stati descritti due approcci che si differenziano sulla gestione del dato locale al nodo, mentre l'aggiornamento del dato 'master' risulta sempre consistente. Il dato locale su un nodo viene letto in un caso sempre in maniera consistente accedendo al data master (misurazione esastta), mentre in un altro vi è una copia locale su ogni nodo che viene aggiornata con sincronizzazioni periodiche, con la controindicazione che la precisione dei conteggi soffre delle finestre di risincronizzazione (misurazione approssimata).

Oltre ai due approcci sopra indicati, nelle tecniche di sincronizzazione su una mappa viene anche utilizzato un terzo approccio "get and set", in cui si recupera il valore corrente, lo si incrementa e quindi lo si rispedisce al datastore senza utilizzare le tecniche di incremento atomico fornite dal gestore stesso. In questo modo il dato 'master' perderà di precisione poichè potrà capitare che richieste simultanee gestite su nodi differenti prelevino la stessa informazione e la modifichino senza tenere conto delle altre analoghe operazioni in corso, ottenendo così che il conteggio risulti approssimato per difetto. Per migliorare ulteriormente le prestazioni, anche l'operazione di 'set' può essere resa asincrona. 

Di seguito vengono fornite le varie modalità di sincronizzazione distribuita che utilizzano una mappa configurabili su GovWay, presentate in ordine, dalla modalità di 'Misurazione Esatta' che garantisce il rispetto puntuale delle politiche previste, fino alla modalità con "get and set asincrono", che è quella che garantisce le prestazioni migliori ma con una maggiore perdita di precisione dei conteggi.

.. note::
  **Configurazione degli algoritmi alternativi** 
  La configurazione di default disponibile sulla console di gestione (govwayConsole) non consente di selezionare gli algoritmi alternativi descritti in questa sezione. Per abilitarne la configurazione deve essere aggiunta la proprietà seguente al file *<directory-lavoro>/console_local.properties*:

   ::

      # Gestori delle Policy di RateLimiting
      controlloTraffico.policyRateLimiting.tipiGestori=LOCAL,LOCAL_DIVIDED_BY_NODES,DATABASE,HAZELCAST_ATOMIC_LONG,HAZELCAST_PNCOUNTER,HAZELCAST_MAP,HAZELCAST_NEAR_CACHE,HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP,HAZELCAST_LOCAL_CACHE,REDISSON_ATOMIC_LONG


- *Misurazione Esatta*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo map*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastFullSync`). Con questa modalità sia il dato 'master' che quelli locali al nodo risultano essere sempre aggiornati.

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastFullSync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastFullSync

    Sincronizzazione Distribuita con misurazione delle metriche esatta memorizzata su una mappa

- *Near Cache*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione approssimata*' e '*Algoritmo near-cache*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastNearCache`). Con questa modalità l'incremento del dato 'master' viene effettuato sempre in maniera atomica, ma la sua esecuzione avviene in maniera asincrona senza bloccare il nodo che ha effettuato l'operazione che utilizza una "NearCache" per consultare i dati locali al nodo. La "NearCache" è una struttura dati fornita da Hazelcast che viene risincronizzata rispetto ai dati remoti con sincronizzazioni periodiche. Maggiori dettagli vengono forniti nella documentazione del prodotto: https://docs.hazelcast.com/imdg/latest/performance/near-cache

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastNearCache.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastNearCache

    Sincronizzazione Distribuita con misurazione delle metriche esatta in remoto ed utilizzo di una "NearCache" in locale

- *Local Cache*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione approssimata*' e '*Algoritmo local-cache*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastLocalCache`). Questa modalità è analoga alla precedente, ma i nodi del gateway, anzichè utilizzare la "NearCache" di Hazelcast, utilizzano una propria copia locale del dato che viene risincronizzata ogni 5 secondi. L'intervallo di risincronizzazione può essere modificato agendo sul file *<directory-lavoro>/govway_local.properties* tramite la seguente proprietà:

   ::

      # Intervallo di aggiornamento della cache in secondi
      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.HAZELCAST_LOCAL_CACHE.updateInterval=5

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastLocalCache.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastLocalCache

    Sincronizzazione Distribuita con misurazione delle metriche esatta in remoto ed utilizzo di una cache locale

- *Misurazione approssimata con "get and set sincrono"*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione inconsistente*' e '*Algoritmo remote-sync*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastRemoteSync`). Con questa modalità l'incremento viene effettuato utilizzando un approccio "get and set" senza atomicità in cui la modifica del 'dato master' avviene tramite un'operazione sincrona.

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastRemoteSync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastRemoteSync

    Sincronizzazione Distribuita con misurazione delle metriche approssimata tramite algoritmo "get and set" con pubblicazione sincrona

- *Misurazione approssimata con "get and set asincrono"*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione inconsistente*' e '*Algoritmo remote-async*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastRemoteAsync`). Come nella precedente modalità l'incremento viene effettuato utilizzando un approccio "get and set" senza atomicità in cui la modifica del 'dato master' avviene tramite un'operazione asincrona.

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastRemoteAsync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastRemoteAsync

    Sincronizzazione Distribuita con misurazione delle metriche approssimata tramite algoritmo "get and set" con pubblicazione asincrona


*Configurazione di Hazelcast per la gestione della mappa*

Di seguito vengono mostrate le configurazioni Hazelcast di default utilizzate dai gestori delle policy di rate limiting descritti in questa sezione.

Ad ogni tipo di gestore viene associata una istanza di Hazelcast dedicata che viene attivata con una configurazione specifica.

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
            port: 5707

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
            in-memory-format: BINARY

        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5709

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
            in-memory-format: BINARY

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
            in-memory-format: BINARY

        serialization:
          serializers:
            - type-class: org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy
              class-name: org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.IDUnivocoGroupByPolicyStreamSerializer

        network:
          port:
            auto-increment: false
            port: 5705

È possibile utilizzare una configurazione differente da quella di default definendo un file di configurazione yaml nella *<directory-lavoro>* di govway specifico per ogni modalità:

- *Misurazione Esatta*: *<directory-lavoro>/govway.hazelcast-map.yaml*

- *Near Cache*: *<directory-lavoro>/govway.hazelcast-near-cache.yaml*

- *Local Cache*: *<directory-lavoro>/govway.hazelcast-local-cache.yaml*

- *Misurazione approssimata con "get and set sincrono*: *<directory-lavoro>/govway.hazelcast-near-cache-unsafe-sync-map.yaml*

- *Misurazione approssimata con "get and set asincrono*: *<directory-lavoro>/govway.hazelcast-near-cache-unsafe-async-map.yaml*

