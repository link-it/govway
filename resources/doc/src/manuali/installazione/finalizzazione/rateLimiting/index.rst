.. _install_rateLimiting:

RateLimiting - Policy di Default
--------------------------------

GovWay permette definire un rate limiting sulle singole erogazioni o fruizioni di API.  Per una descrizione dettagliata sulle policy di Rate Limiting supportate da GovWay si rimanda alla sezione :ref:`rateLimiting` della guida 'Console di Gestione'.

Oltre al rate limiting GovWay consente di fissare un numero limite complessivo, indipendente dalle APIs, riguardo alle richieste gestibili simultaneamente dal gateway, bloccando le richieste in eccesso (:ref:`maxRequests`).

Sempre a livello globale, GovWay limita la dimensione massima accettata di una richiesta e di una risposta (:ref:`dimensioneMassimaMessaggi`).

.. toctree::
        :maxdepth: 2

	maxRequests
	dimensioneMassimaMessaggi
