.. _configurazioneRateLimiting:

Rate Limiting
~~~~~~~~~~~~~

Questa sezione consente di creare e attivare le policy di controllo del
traffico. Gli elementi di configurazione presenti sono:

-  *Tipo Errore per API SOAP* e *Includi Descrizione Errore* (Opzioni
   presenti solo con console in modalità avanzata): Imposta il tipo di
   errore restituito al chiamante nel caso venga rilevata una violazione
   delle policy configurate:

   -  *Fault*: viene generato un messaggio di Fault contenente un codice
      ed una descrizione dell’errore rilevato nel caso l’elemento
      *Includi Descrizione Errore* sia abilitato, o un codice di errore
      generico altrimenti.

   -  *Http 429 (Too Many Requests)*

      *Http 503 (Service Unavailable)*

      *Http 500 (Internal Server Error)*

      viene generata una risposta HTTP con il codice selezionato
      contenente una pagina html di errore se l’elemento *Includi
      Descrizione Errore* è abilitato, od una risposta http vuota
      altrimenti.

-  *Registro Policy*: Consente di accedere al Registro delle Policy per
   visualizzare, modificare e creare le policy di controllo istanziabili
   per la configurazione del rate limiting. Tra parentesi viene
   visualizzato il numero di policy attualmente presenti nel registro.
   Questa funzionalità è descritta nella sezione :ref:`registroPolicy`.

-  *Policy Globali*: Consente di accedere al Registro delle Policy
   Attivate in ambito globale, cioè operative sul traffico complessivo
   che transita sul gateway. A queste policy si aggiungono quelle
   eventualmente definite localmente nella configurazione specifica di
   ciascuna erogazione/fruizione.
   Tra parentesi viene visualizzato il numero di policy attualmente attivate.
   Questa funzionalità è descritta nella sezione :ref:`trafficoPolicy`.
