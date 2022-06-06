.. _install_rateLimiting:

RateLimiting - Policy di Default
--------------------------------

GovWay permette definire un rate limiting sulle singole erogazioni o fruizioni di API.  Per una descrizione dettagliata sulle policy di Rate Limiting supportate da GovWay si rimanda alla sezione :ref:`rateLimiting` della guida 'Console di Gestione'.

In presenza di una installazione con più nodi gateway attivi, GowWay per default effettua il conteggio delle metriche utilizzate dalle policy di rate limiting indipendentemente su ogni singolo nodo del cluster. Questa soluzione è certamente la più efficiente, ma presenta dei limiti evidenti se è necessaria una contabilità precisa del numero di accessi consentiti globalmente da parte dell’intero cluster. In tali situazioni è necessario modificare la configurazioni di default per attivare modalità di conteggio distribuite le quali richiedono alcune configurazioni del sistema descritte nella sezione :ref:`finalizzazioneHazelcast`.

Oltre al rate limiting GovWay consente di fissare un numero limite complessivo, indipendente dalle APIs, riguardo alle richieste gestibili simultaneamente dal gateway, bloccando le richieste in eccesso (:ref:`maxRequests`).

Sempre a livello globale, GovWay limita la dimensione massima accettata di una richiesta e di una risposta (:ref:`dimensioneMassimaMessaggi`).

.. toctree::
        :maxdepth: 2

	maxRequests
	dimensioneMassimaMessaggi
        hazelcast
