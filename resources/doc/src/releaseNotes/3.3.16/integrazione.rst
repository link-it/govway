Miglioramenti alla funzionalità 'Header di Integrazione'
--------------------------------------------------------

.. note::

   Nuova Funzionalità introdotta nella versione '3.3.16.p1'

È stata introdotta una nuova funzionalità che gestisce la riscrittura di eventuali header in ingresso con prefisso "GovWay-", producendo nuovi header HTTP con prefisso 'GovWay-Peer-'.

Questa funzionalità risulta particolarmente utile negli scenari di fruizione ModI o SPCoop, in cui anche la parte erogatrice è esposta tramite GovWay. In tali contesti, permette al client di ricevere entrambi gli identificativi generati dal GovWay locale e da quello dalla parte erogatrice, migliorando la tracciabilità e la gestione delle eventuali richieste di supporto.
