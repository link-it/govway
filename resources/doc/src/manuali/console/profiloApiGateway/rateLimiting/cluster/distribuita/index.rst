.. _headerGWRateLimitingCluster_distribuita:

Sincronizzazione Distribuita
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Questa modalità consente di implementare qualunque politica di rate limiting in maniera effettivamente distribuita tra i nodi del cluster, indipendentemente dalle modalità previste per il bilanciamento del carico.

Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito. 


*Vantaggi*

Non vi è alcuna dipendenza rispetto alla modalità di bilanciamento del carico, nè alcuna limitazione sul tipo di policy applicabile.

*Svantaggi*

Aumento della latenza di gestione delle richieste, dovuta alla concorrenza delle operazioni sui contatori distribuiti. Per ovviare al potenziale degrado prestazionale vengono fornite varie modalità di sincronizzazione che consentono di ottimizzare le prestazioni, rinunciando alla completa precisione dei conteggi. 

*Differenti tecniche di sincronizzazione*

Il principale problema con un archivio dati centralizzato è dovuto alla concorrenza delle operazioni di aggiornamento che devono essere effettuate in maniera atomica per assicurare la consistenza del dato totale conteggiato. In presenza di elevato traffico questo può comportare un apprezzabile degrado prestazionale.

Per mitigare il problema, è possibile utilizzare modalità asincrone di aggiornamento dei dati tra i nodi del cluster. Con questo approccio il dato 'master' risulta sempre consistente mentre la copia locale di ogni nodo viene aggiornata con sincronizzazioni periodiche, con la controindicazione che la precisione dei conteggi soffre delle finestre di risincronizzazione.

*Soluzioni*

GovWay consente di avere un conteggio delle metriche effettuato tramite una delle seguenti tipologie di archivio dati distribuito:

- :ref:`headerGWRateLimitingCluster_distribuita_hazelcast` (https://github.com/hazelcast/): soluzione fornita built-in con GovWay, che consente di implementare l'archivio dati distribuito tra i nodi del cluster;

- :ref:`headerGWRateLimitingCluster_distribuita_redis` (https://redis.io/); soluzione alternativa alla precedente, dove GovWay deve essere configurato per accedere ad un database redis; 

- :ref:`headerGWRateLimitingCluster_distribuita_embedded`; implementazione built-in, in cui il dato 'master' viene gestito sul database di GovWay. La soluzione viene fornita per ambienti di test in cui si desidera provare le funzionalità di rate limiting distribuito.

   .. note::
       La soluzione 'embedded' non è utilizzabile su ambienti di produzione.


.. toctree::
   :maxdepth: 2

   hazelcast/index
   redis
   embedded

