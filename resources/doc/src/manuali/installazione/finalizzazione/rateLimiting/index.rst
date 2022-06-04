.. _install_rateLimiting:

RateLimiting - Policy di Default
--------------------------------

GovWay permette definire un rate limiting sulle singole erogazioni o fruizioni di API.  Per una descrizione dettagliata sulle policy di Rate Limiting supportate da GovWay si rimanda alla sezione :ref:`rateLimiting` della guida 'Console di Gestione'.

Tutti i contatori delle metriche, per default, vengono gestiti localmente in un nodo. Per poter utilizzare politiche di rate limiting su un cluster di nodi è possibile abilitare una sincronizzazione distribuita dei contatori che richiede l'utilizzo di un archivio dati centralizzato implementato tramite Hazelcast. Il suo utilizzo richiede alcune configurazioni del sistema descritte nella sezione :ref:`finalizzazioneHazelcast`.

Oltre al rate limiting GovWay consente di fissare un numero limite complessivo, indipendente dalle APIs, riguardo alle richieste gestibili simultaneamente dal gateway, bloccando le richieste in eccesso (:ref:`maxRequests`).

Sempre a livello globale, GovWay limita la dimensione massima accettata di una richiesta e di una risposta (:ref:`dimensioneMassimaMessaggi`).

.. toctree::
        :maxdepth: 2

	maxRequests
	dimensioneMassimaMessaggi
        hazelcast
