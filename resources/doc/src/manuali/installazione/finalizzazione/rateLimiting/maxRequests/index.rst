.. _maxRequests:

Numero Complessivo Richieste Simultanee
-------------------------------------------------------

GovWay consente di fissare un numero limite complessivo, indipendente
dalle singole APIs, riguardo alle richieste gestibili simultaneamente
dal gateway, bloccando le richieste in eccesso (:ref:`maxRequests_policy`). 

È inoltre possibile configurare il numero massimo di connessioni HTTP mantenute aperte (in presenza di 'keep-alive') per stessa destinazione (:ref:`maxRequests_network`).

Nell'installazione di default entrambi i limiti sono fissati a '200'.

.. toctree::
        :maxdepth: 2

	policy
	network

