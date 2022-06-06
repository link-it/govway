.. _finalizzazioneHazelcast:

Configurazione di Hazelcast
------------------------------

GovWay consente di implementare qualunque politica di rate limiting in maniera effettivamente distribuita tra i nodi del cluster, indipendentemente dalle modalità previste per il bilanciamento del carico. Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito implementato tramite Hazelcast (https://github.com/hazelcast/). Per una descrizione dettagliata sul Rate Limiting attuabile su un cluster di nodi si rimanda alla sezione :ref:`headerGWRateLimitingCluster` della guida 'Console di Gestione' e nello specifico alla sezione :ref:`headerGWRateLimitingCluster_distribuita`.

Se viene attivata una modalità di sincronizzazione distribuita, i log emessi da Hazelcast riguardanti lo stato della sincronizzazione dei nodi del cluster sono riversati nel file di log *<directory-log>/govway_hazelcast.log*

Una volta avviata tale modalità si potrà riscontrare come nel log venga riportato il seguente warning:

   ::

        WARN <04-06-2022 11:38:24.537> com.hazelcast.logging.AbstractLogger.warning(89): Hazelcast is starting in a Java modular environment (Java 9 and newer) but without proper access to required Java packages. Use additional Java arguments to provide Hazelcast access to Java internal API. The internal API access is used to get the best performance results. Arguments to be used:
         --add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED 

Per fornire ad Hazelcast l'accesso alle API interne di java deve essere riportata la configurazione indicata nel warning tra le proprietà java dell'Application Server.
