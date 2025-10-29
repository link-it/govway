Miglioramenti alla funzionalità di Negoziazione Token
-----------------------------------------------------

.. note::

   La funzionalità è stata introdotta nella versione '3.3.4.p2'

È stata aggiunto il supporto per la negoziazione di token tramite la modalità di autenticazione descritta nella sezione 2.2 del RFC 7523 (https://datatracker.ietf.org/doc/html/rfc7523#section-2.2). È possibile configurare la policy per firmare l'asserzione JWT di autenticazione tramite un certificato X.509 o tramite un client secret.

È stata inoltre aggiunta la possibilità di configurare l'autenticazione server di una token policy per accettare qualsiasi certificato.

