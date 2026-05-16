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

La sezione :ref:`applicativoServerConnettore` descrive invece come censire un'applicazione di backend in modo da poterla riferire su diversi connettori relativi ad erogazioni di API.

Le sezioni successive descrivono le funzionalità inerenti l'utilizzo di endpoint multipli allo scopo di bilanciare il carico o differenziarlo rispetto a variabili presenti nella richiesta, sempre relativamente ad erogazioni di API.

La sezione :ref:`proxyPassReverse` descrive la funzionalità di riscrittura delle url negli header HTTP della risposta.

La sezione :ref:`contentLengthRisposta` descrive come configurare GovWay per preservare o forzare l'header *Content-Length* nella risposta inoltrata al client.

Infine la sezione :ref:`multipartTypeMissing` descrive come configurare GovWay per gestire un *Content-Type* '*multipart/related*' privo del parametro *type* obbligatorio (`RFC 2387, Section 3.1 <https://datatracker.ietf.org/doc/html/rfc2387#section-3.1>`_) ricevuto da un backend o da un client non conforme.

.. note::
	Le funzionalità relative ad un applicativo 'Server' (sezione :ref:`applicativoServerConnettore`) e ai connettori multipli (:ref:`loadBalancerConnettore` e :ref:`consegnaCondizionaleConnettore`) sono applicabili solamente per le erogazioni di API.

.. toctree::
   :maxdepth: 2

   verificaConnettivita
   applicativoServer
   loadBalancer/index
   consegnaCondizionale/index
   proxyPassReverse
   contentLength
   multipartType
