.. _headerGWRateLimitingCluster_distribuita:

Sincronizzazione Distribuita
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Questa modalità consente di implementare qualunque politica di rate limiting in maniera effettivamente distribuita tra i nodi del cluster, indipendentemente dalle modalità previste per il bilanciamento del carico.

Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito implementato tramite Hazelcast (https://github.com/hazelcast/).

*Vantaggi*

Non vi è alcuna dipendenza rispetto alla modalità di bilanciamento del carico, nè alcuna limitazione sul tipo di policy applicabile.

*Svantaggi*

Aumento della latenza di gestione delle richieste, dovuta alla concorrenza delle operazioni sui contatori distribuiti. Per ovviare al potenziale degrado prestazionale vengono fornite varie modalità di sincronizzazione che consentono di ottimizzare le prestazioni, rinunciando alla completa precisione dei conteggi. 

*Differenti tecniche di sincronizzazione*

Il principale problema con un archivio dati centralizzato è dovuto alla concorrenza delle operazioni di aggiornamento che devono essere effettuate in maniera atomica per assicurare la consistenza del dato totale conteggiato. In presenza di elevato traffico questo può comportare un apprezzabile degrado prestazionale.

Per mitigare il problema, è possibile utilizzare modalità asincrone di aggiornamento dei dati tra i nodi del cluster. Con questo approccio il dato 'master' risulta sempre consistente mentre la copia locale di ogni nodo viene aggiornata con sincronizzazioni periodiche, con la controindicazione che la precisione dei conteggi soffre delle finestre di risincronizzazione.

Un'altra possibilità è quella di utilizzare un approccio "get and set", in cui si recupera il valore corrente, lo si incrementa e quindi lo si rispedisce al datastore senza utilizzare le tecniche di incremento atomico fornite dal gestore stesso. In questo modo il dato 'master' perderà di precisione poichè potrà capitare che richieste simultanee gestite su nodi differenti prelevino la stessa informazione e la modifichino senza tenere conto delle altre analoghe operazioni in corso, ottenendo così che il conteggio risulti approssimato per difetto. Per migliorare ulteriormente le prestazioni, anche l'operazione di 'set' può essere resa asincrona. 

Di seguito vengono fornite le varie modalità di sincronizzazione distribuita configurabili su GovWay, presentate in ordine, dalla modalità di 'Misurazione Esatta' che garantisce il rispetto puntuale delle politiche previste, fino alla modalità con "get and set asincrono", che è quella che garantisce le prestazioni migliori ma con una maggiore perdita di precisione dei conteggi.

- *Misurazione Esatta*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo full-sync*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastFullSync`). Con questa modalità sia il dato 'master' che quelli locali al nodo risultano essere sempre aggiornati.

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastFullSync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastFullSync

    Sincronizzazione Distribuita con misurazione delle metriche esatta

- *Near Cache*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo near-cache*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastNearCache`). Con questa modalità l'incremento del dato 'master' viene effettuato sempre in maniera atomica, ma la sua esecuzione avviene in maniera asincrona senza bloccare il nodo che ha effettuato l'operazione che utilizza una "NearCache" per consultare i dati locali al nodo. La "NearCache" è una struttura dati fornita da Hazelcast che viene risincronizzata rispetto ai dati remoti con sincronizzazioni periodiche. Maggiori dettagli vengono forniti nella documentazione del prodotto: https://docs.hazelcast.com/imdg/latest/performance/near-cache

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastNearCache.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastNearCache

    Sincronizzazione Distribuita con misurazione delle metriche esatta in remoto ed utilizzo di una "NearCache" in locale

- *Local Cache*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo local-cache*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastLocalCache`). Questa modalità è analoga alla precedente, ma i nodi del gateway, anzichè utilizzare la "NearCache" di Hazelcast, utilizzano una propria copia locale del dato che viene risincronizzata ogni 5 secondi. L'intervallo di risincronizzazione può essere modificato agendo sul file *<directory-lavoro>/govway_local.properties* tramite la seguente proprietà:

   ::

      # Intervallo di aggiornamento della cache in secondi
      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.HAZELCAST_LOCAL_CACHE.updateInterval=5

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastLocalCache.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastLocalCache

    Sincronizzazione Distribuita con misurazione delle metriche esatta in remoto ed utilizzo di una cache locale

- *Misurazione approssimata con "get and set sincrono"*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione approssimata*' e '*Algoritmo remote-sync*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastRemoteSync`). Con questa modalità l'incremento viene effettuato utilizzando un approccio "get and set" senza atomicità in cui la modifica del 'dato master' avviene tramite un'operazione sincrona.

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastRemoteSync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastRemoteSync

    Sincronizzazione Distribuita con misurazione delle metriche approssimata tramite algoritmo "get and set" con pubblicazione sincrona

- *Misurazione approssimata con "get and set asincrono"*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione approssimata*' e '*Algoritmo remote-async*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastRemoteAsync`). Come nella precedente modalità l'incremento viene effettuato utilizzando un approccio "get and set" senza atomicità in cui la modifica del 'dato master' avviene tramite un'operazione asincrona.

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastRemoteAsync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastRemoteAsync

    Sincronizzazione Distribuita con misurazione delle metriche approssimata tramite algoritmo "get and set" con pubblicazione asincrona


Nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig` vengono fornite informazioni sul tipo di configurazione utilizzata su Hazelcast, mentre nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcastLog` viene indicato dove è possibile reperire i log emessi da Hazelcast.

.. toctree::
   :maxdepth: 2

   configurazioneHazelcast
   logHazelcast

