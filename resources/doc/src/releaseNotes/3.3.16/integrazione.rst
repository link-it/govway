Miglioramenti alla funzionalità 'Header di Integrazione'
--------------------------------------------------------

.. note::

   Nuova Funzionalità introdotta nella versione '3.3.16.p1'

È stata introdotta una funzionalità che consente di restituire al client gli header generati dal backend aventi lo stesso prefisso GovWay-\*, relativi a identificativi o indicazioni di rate limiting, tramite nuovi header HTTP 'GovWay-Peer-\*'.

Questa funzionalità risulta particolarmente utile negli scenari di fruizione ModI o SPCoop, in cui anche la parte erogatrice è esposta tramite GovWay.

In tali contesti, permette al client di ricevere gli identificativi generati dalla parte erogatrice, migliorando la tracciabilità e la gestione delle richieste.
