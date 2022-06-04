.. _headerGWRateLimitingCluster_distribuita:

Sincronizzazione Distribuita
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Una sincronizzazione distribuitta consente di implementare politiche di rate limiting, in presenza di un cluster di più nodi, senza dipendere da alcun bilanciamento del carico.

Il conteggio delle metriche viene attuato tramite un archivio dati centralizzato implementato tramite Hazelcast.

*Vantaggi*

Non vi è alcuna dipendenza rispetto al bilanciamento delle richieste.

*Svantaggi*

Aumento della latenza che impatta su ogni richiesta per via dell'archiviazione dei dati in remoto e per la concorrenza delle operazioni di incremento. Per ovviare al degrado delle performance vengono fornite differenti varianti di sincronizzazione che consentono di avere performance migliori a discapito della precisione nei conteggi. 


*Differenti tecniche di sincronizzazione*

Il principale problema con un archivio dati centralizzato è dovuto alla concorrenza delle operazioni attuate quando il numero di richieste simultanee è elevato. 

Ogni operazione, per poter mantenere una consistenza dei dati, viene eseguita impedendo a qualsiasi altra richiesta di modificare il contatore e quindi tale blocco diventa rapidamente un collo di bottiglia per le prestazioni.

Per poter migliorare le performance è possibile utilizzare approcci in cui l'operazione di incremento viene effettuata sempre in maniera atomica dal gestore dei dati remoto, ma la sua esecuzione avviene in maniera asincrona senza bloccare il client il quale utilizzerà una copia locale dei contatori per implementare la politica di rate limiting. Con questo approccio i dati remoti risultano sempre essere consistenti mentre la copia locale viene aggiornata con sincronizzazioni periodiche e quindi la precisione dei contatori soffre delle finestre di risincronizzazione.

Un altra modalità è quello di utilizzare un approccio ingenuo "get and set", in cui si recupera il contatore corrente, lo si incrementa e quindi lo si rinvia al datastore senza utilizzare tecniche di incremento atomico fornite dal gestore stesso. Con questa modalità i dati remoti diventeranno inconsistenti poichè potra capitare che richieste simultanee gestite su nodi differenti prelevino la stessa informazione e la modificano senza tenere conto delle altre richieste in essere. La modalità consente quindi di avere una base dati remota che però non garantirà una misurazione esatta delle metriche, ma bensì approssimata per difetto. Il vantaggio è che le performance sono migliori poichè vi sono meno colli di bottiglia dovuti alle operazioni atomiche ed inoltre anche le operazioni "set" possono essere configurate per essere riversate in maniera asincrona favorandone maggiore performance a discapito della precisione delle misurazioni.

Il grafico (:numref:`configurazioneSincronizzazioneRateLimitingGrafico`) mostra come le performance migliorano in maniera inversamente proporzionale alla precisione nei conteggi delle metriche. 

.. figure:: ../../../_figure_console/GraficoRateLimitingSincronizzazioneDistribuita.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingGrafico

    Sincronizzazione Distribuita - performance rispetto alla precisione nei conteggi.

Di seguito vengono fornite le varie modalità di sincronizzazione distribuita configurabili su GovWay in ordine dalla modalità che garantisce una misurazione esatta delle metriche a quella più lasca che consente di avere maggiori performance.

- *Misurazione Esatta*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo full-sync*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastFullSync`). Con questa modalità sia i dati remoti che quelli locali al nodo risultano essere sempre consistenti.

  .. figure:: ../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastFullSync.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastFullSync

    Sincronizzazione Distribuita con misurazione delle metriche esatta

- *Near Cache*: attivabile impostando la sincronizzazione '*Distribuita*' e scegliendo le voci '*Misurazione esatta*' e '*Algoritmo near-cache*' (:numref:`configurazioneSincronizzazioneRateLimitingHazelcastNearCache`). Con questa modalità l'incremento viene effettuato sempre in maniera atomica dal gestore dei dati remoto, ma la sua esecuzione avviene in maniera asincrona senza bloccare il client che utilizza una "NearCache" per consultare i dati locali al nodo. La "NearCache" è una struttura dati fornita da Hazelcast che viene risincronizzata rispetto ai dati remoti con sincronizzazioni periodiche. Maggiori dettagli vengono forniti nella documentazione del prodotto: https://docs.hazelcast.com/imdg/latest/performance/near-cache

  .. figure:: ../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingHazelcastNearCache.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingHazelcastNearCache

    Sincronizzazione Distribuita con misurazione delle metriche esatta in remoto ed utilizzo di una "NearCache" in locale





TODO: Fornire indicazioni su come modificare la configurazione hazelcast riportando anche i default utilizzzati (tanto sono 2) per ogni motore


