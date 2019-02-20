.. _cachingRisposta:

Caching della Risposta - Disk Cache
-----------------------------------

In GovWay è possibile abilitare il salvataggio delle risposte in una
cache. Questa funzionalità permette ad un backend server di non dover
riprocessare le stesse richieste più volte.

La configurazione di default prevede di salvare in una cache, che
risiede in memoria RAM, fino a 5.000 risposte (ogni risposta comporta il
salvataggio di due elementi in cache). In caso venga superato il numero
massimo di elementi che possano risiedere in cache, vengono eliminate le
risposte meno recenti secondo una politica *LRU*.

Per modificare la configurazione della cache in modo da aggiungere una
memoria secondaria dove salvare gli elementi in eccesso è possibile
agire sul file *<directory-lavoro>/govway_local.jcs.properties*
scommentando le seguenti:

::

   jcs.region.responseCaching=responseCachingDiskCache
   jcs.region.responseCaching.elementattributes.IsSpool=true                   
                           

Per ulteriori dettagli sui parametri di configurazione della memoria
secondaria si rimanda alla documentazione della cache
http://commons.apache.org/proper/commons-jcs/IndexedDiskCacheProperties.html.

La libreria di caching utilizzata da GovWay, (*JCS*:
http://commons.apache.org/proper/commons-jcs/) consente di definire
diversi tipi di memoria secondaria. Per ulteriori dettagli su come
abilitare i vari tipi di memoria si rimanda alla documentazione:
http://commons.apache.org/proper/commons-jcs/JCSPlugins.html.

   **Note**

   Accedendo alla sezione *'Configurazione - Generale', tramite
   l'utilizzo della govwayConsole in modalità avanzata*, è possibile
   modificare i parametri di configurazione (numero di elementi e
   politica di svecchiamento) della cache che risiede in memoria RAM
   tramite la sezione *'Cache (Risposte)'*.
