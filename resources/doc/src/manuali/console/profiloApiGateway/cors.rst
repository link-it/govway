.. _configSpecificaCORS:

Gestione CORS
~~~~~~~~~~~~~

Quando un'applicazione client in esecuzione su un browser (es. codice
javascript) richiede l'accesso ad una risorsa di un differente dominio,
protocollo o porta tale richiesta viene gestita dal browser tramite una
politica di *cross-origin HTTP request (CORS)*. Il CORS definisce un
modo nel quale un browser ed un server (o il gateway) possono interagire
per abilitare interazioni attraverso differenti domini.

In GovWay è possibile abilitare la gestione del CORS sia globalmente, in
modo che sia valida per tutte le APIs, che singolarmente sulla singola
erogazione o fruizione.

È possibile modificare le impostazioni CORS seguendo il collegamento
presente nella riga *Gestione CORS* del dettaglio di una erogazione o
fruizione. L'impostazione permette di ridefinire la configurazione
globale; i campi del form sono i medesimi descritti nella configurazione
globale (sezione :ref:`console_cors`).
