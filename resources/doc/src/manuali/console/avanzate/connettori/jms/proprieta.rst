.. _avanzate_connettori_jms_proprieta:

Proprietà aggiuntive
********************

Tramite il link 'Proprietà' è possibile associare al connettore le proprietà descritte nella :numref:`ConnettoreJMSProprietaTab`, indicandone il nome completo (es. 'context-queue.queue/ordini'). Non sono associabili le proprietà già configurabili tramite i campi della maschera (es. 'context-java.naming.provider.url' corrisponde al campo 'Provider Url').

Il nome e il valore delle proprietà possono contenere parti dinamiche risolte a runtime, descritte nella sezione :ref:`valoriDinamici`.

.. table:: Proprietà aggiuntive del connettore JMS
   :widths: auto
   :name: ConnettoreJMSProprietaTab

   =================================  ==========================================================================================================================================================================================================================
   Proprietà                          Descrizione
   =================================  ==========================================================================================================================================================================================================================
   context-<nome>                     Proprietà '<nome>' del contesto JNDI utilizzato per la lookup della Connection Factory e della queue/topic (es. 'context-queue.queue/ordini' = 'ordini' per definire il binding di una coda su ActiveMQ).
   pool-<nome>                        Proprietà '<nome>' di un contesto JNDI distinto, utilizzato per la sola lookup della Connection Factory (es. un pool di connessioni locale all'application server).
   lookupDestination-<nome>           Proprietà '<nome>' aggiunta al contesto JNDI; nome e valore possono contenere anche le parole chiave '#Servizio', '#TipoServizio' e '#Azione', sostituite con i dati della richiesta.
   locations-cache                    'abilitata' o 'disabilitata' (default): consente di mantenere in cache le queue/topic ottenute tramite lookup JNDI.
   acknowledgeMode                    Modalità di acknowledge della sessione JMS: 'AUTO_ACKNOWLEDGE' (default), 'CLIENT_ACKNOWLEDGE' o 'DUPS_OK_ACKNOWLEDGE'.
   =================================  ==========================================================================================================================================================================================================================
