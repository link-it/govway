.. _gestioneErrori:

Classificazione degli Errori
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Una risposta con codice 2xx indica che l'operazione ha avuto successo mentre codici diversi indicano un problema imputabile al client (4xx su API REST o da un fault code 'Client' su API SOAP) o un errore dipendente dallo stato del servizio (5xx su API REST o da un fault code 'Server' su API SOAP).

La tabella :numref:`gestioneErroriTab` riporta l'elenco dei possibili codici di errore restituiti da GovWay. Per ognuno di questi, nella colonna 'Retry' è indicato se sia possibile o meno effettuare nuovi invii della stessa richiesta che ha ottenuto errore. Le indicazioni fornite sono le seguenti:

- Sì: il client può effettuare nuovamente la stessa richiesta;
- Sì, se idempotente: il client può effettuare nuovamente la stessa richiesta, ma solo se l'operazione sul backend applicativo è implementata in maniera idempotente.
- No: il client deve risolvere il problema segnalato prima di effettuare una nuova richiesta (ripetere la stessa richiesta produrrebbe sempre lo stesso esito).

   .. table:: Gestione degli Errori
      :widths: auto
      :name: gestioneErroriTab

      ============     ==========================================================  =============================
      REST / SOAP      GovWay-Transaction-ErrorType                                Retry
      ============     ==========================================================  =============================
      400 / Client     :ref:`errori_400`                                           No
      401 / Client     :ref:`errori_401`                                           No
      403 / Client     :ref:`errori_403`                                           No
      404 / Client     :ref:`errori_404`                                           No
      409 / Client     :ref:`errori_409`                                           No
      413 / Client     :ref:`errori_413`                                           No
      429 / Client     :ref:`errori_429_LimitExceeded` - 429 (Rate Limiting)       No, fino a reset *
      429 / Client     :ref:`errori_429_TooManyRequests` - 429 (Rate Limiting)     Sì
      502 / Server     :ref:`errori_502`                                           Sì, se idempotente 
      502 / Server     :ref:`errori_502_ResponseSizeExceeded` - 502 (Bad Gateway)  No 
      503 / Server     :ref:`errori_503`                                           Sì
      504 / Server     :ref:`errori_504`                                           Sì, se idempotente
      ============     ==========================================================  =============================

[*] Se vengono attivate policy di :ref:`rateLimiting` che prevedono un limite di richieste all'interno di una finestra temporale, GovWay genera un header HTTP che indica al client il numero di secondi che mancano alla nuova finestra temporale dove saranno resettati i contatori delle richieste effettuate. I nomi degli header, che cambiano in funzione delle policy attivate, vengono descritte nella sezione :ref:`headerRisposta`.

Nei casi in cui è prevista la rispedizione, GovWay genera un header 'Retry-After' che indica al client il numero di secondi di attesa prima di ripetere la richiesta.


.. toctree::
        :maxdepth: 2
        
        400/index
	401/index
	403/index
        404/index
	409/index
	413/index
	429/index
        502/index
	503/index
	504/index
