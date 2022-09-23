.. _headerGWRateLimitingCluster_distribuita_hazelcast:

Hazelcast
~~~~~~~~~~

Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito implementato tramite Hazelcast (https://github.com/hazelcast/).

GovWay consente di configurare 2 tecniche di sincronizzazione:

- *Misurazione Esatta*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo atomic-long-counters*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastAtomicLongCounters`). Con questa modalità sia il dato 'master' che quelli locali al nodo risultano essere sempre aggiornati.

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastAtomicLongCounters.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastAtomicLongCounters

    Sincronizzazione Distribuita 'Hazelcast' con misurazione delle metriche esatta

- *Misurazione Approssimata*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo pn-counters*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastPNCounters`). Con questa modalità l'incremento del dato 'master' viene effettuato sempre in maniera atomica, ma la sua esecuzione avviene in maniera asincrona senza bloccare il nodo che ha effettuato l'operazione che utilizza i "PN Counters" per consultare i dati locali al nodo. I "PN Counters" sono una struttura dati fornita da Hazelcast in cui tutti gli aggiornamenti effettuati su un nodo vengono replicati in modo asincrono sugli altri. Tuti i nodi convergono sullo stesso stato dopo un pò di tempo. Maggiori dettagli vengono forniti nella documentazione del prodotto: https://docs.hazelcast.com/hazelcast/5.1/data-structures/pn-counter

  .. figure:: ../../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastPNCounters.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastPNCounters

    Sincronizzazione Distribuita 'Hazelcast' con misurazione delle metriche approssimata tramite "PNCounters"

Nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcastConfig` vengono fornite informazioni sul tipo di configurazione utilizzata su Hazelcast, mentre nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcastLog` viene indicato dove è possibile reperire i log emessi da Hazelcast.

Altre modalità di utilizzo di hazelcast, forniti nelle precedenti versioni di GovWay, vengono descritte nella sezione :ref:`headerGWRateLimitingCluster_distribuita_hazelcastOpzioniAvanzate` ma ne si sconsiglia l'utilizzo poichè meno performanti rispetto alle due soluzioni sopra indicate.

.. toctree::
   :maxdepth: 2

   configurazione/index
   log
   opzioniAvanzate

