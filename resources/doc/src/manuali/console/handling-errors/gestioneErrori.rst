.. _gestioneErrori:

Gestione degli Errori
~~~~~~~~~~~~~~~~~~~~~

Il codice HTTP indica se una operazione ha avuto successo.
Una risposta con codice 2xx indica che l'operazione ha avuto successo mentre altri errori indicano un problema imputabile al client (4xx su API REST o da un fault code 'Client' su API SOAP) o un errore che deve essere risolto agendo su GovWay (5xx su API REST o da un fault code 'Server' su API SOAP).

La tabella :numref:`gestioneErroriTab` mostra gli errori ritornati da GovWay. La tabella mostra nella colonna 'Retry' quali errori sono risolvibili semplicemente tramite un nuovo invio (poichè non imputabili al client). Le indicazioni fornite per un nuovo invio sono le seguenti:

- Si: il client può effettuare una nuova identica richiesta per ottenere una risposta con successo.
- Si se idempotentene: il client può effettuare una nuova identica richiesta, ma solo se l'operazione sul backend applicativo è stata implementata idenmpotente.
- No: il client deve risolvere il problema presente sulla sua richiesta prima di effettuarne una nuova.

   .. table:: Gestione degli Errori
      :widths: auto
      :name: gestioneErroriTab

      ============     ===================================     =================
      REST / SOAP      GovWay-Transaction-ErrorType            Retry
      ============     ===================================     =================
      400 / Client     :ref:`errori_400`                       No
      401 / Client     :ref:`errori_401`                       No
      403 / Client     :ref:`errori_403`                       No
      404 / Client     :ref:`errori_404`                       No
      409 / Client     :ref:`errori_409`                       No
      429 / Client     LimitExceeded - :ref:`errori_429`       No
      429 / Client     TooManyRequests - :ref:`errori_429`     Si
      502 / Server     :ref:`errori_502`                       Si se idempotente 
      503 / Server     :ref:`errori_503`                       Si
      504 / Server     :ref:`errori_504`                       Si se idempotente
      ============     ===================================     =================

GovWay, nei casi dove è consigliata la rispedizione, genera un header 'Retry-After' che indica al client il numero di secondi dopo i quali ripresentarsi.
Maggiori informazioni sugli header generati riguardanti la politica di rispedizione dei client sono trattate nella sezione :ref:`headerRisposta`.


.. toctree::
        :maxdepth: 2
        
        400/index
	401/index
	403/index
        404/index
	409/index
	429/index
        502/index
	503/index
	504/index
