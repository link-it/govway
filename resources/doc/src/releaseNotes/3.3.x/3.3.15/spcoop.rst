Miglioramenti al Profilo di Interoperabilità 'SPCoop'
-----------------------------------------------------

.. note::

   La funzionalità è stata introdotta nella versione '3.3.15.p2'

Se il gateway fruitore riceve dalla controparte erogatrice del servizio un messaggio di errore SPCoop come risposta, la busta viene validata e viene generato un messaggio applicativo di errore che viene ritornato all'applicativo mittente, come descritto nel documento "Sistema pubblico di cooperazione: PORTA DI DOMINIO v1.1", voce "PD_UR-5". 

Anche con la voce "Sbustamento SPCoop" disabilitata, viene comunque restituito un messaggio applicativo di errore.

È stata aggiunta la possibilità di modificare il comportamento di default, precedentemente descritto, per inoltrare all'applicativo mittente esattamente il messaggio di errore SPCoop ricevuto dalla controparte. 
