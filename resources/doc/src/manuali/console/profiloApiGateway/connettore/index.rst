.. _configSpecificaConnettore:

Connettore
~~~~~~~~~~

È possibile modificare le impostazioni del connettore (ad esempio per
modificare l'endpoint o aggiungere il proxy) seguendo il collegamento
presente nella riga *Connettore* del dettaglio di una erogazione o
fruizione. I campi del form sono uguali a quelli già descritti per la
fase di creazione dell'erogazione (sezione :ref:`erogazione`). 
Ulteriori dettagli di configurazione e tipi di connettore
diversi da HTTP e HTTPS sono descritti nella sezione :ref:`avanzate_connettori`.

La sezione :ref:`verificaConnettivitaConnettore` descrive uno strumento per verificare la raggiungibilità dell'indirizzo impostato.

La sezione :ref:`applicativoServerConnettore` descrive invece come censire un applicazione di backend in modo da poterla utilizzare su più connettori relativi ad erogazioni di API.

Le sezioni successive, infine, descrivono le funzionalità inerenti l'utilizzo di endpoint multipli allo scopo di bilanciare il carico o differenziarlo rispetto a variabili presenti nella richiesta, sempre relativamente ad erogazioni di API.

.. note::
	Le funzionalità relative ad un applicativo 'Server' (sezione :ref:`applicativoServerConnettore`) e ai connettori multipli (:ref:`loadBalancerConnettore` e :ref:`consegnaCondizionaleConnettore`) sono applicabili solamente per le erogazioni di API.

.. toctree::
   :maxdepth: 2

   verificaConnettivita
   applicativoServer
   loadBalancer/index
   consegnaCondizionale/index
