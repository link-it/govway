.. _limitazioneNumeroRichieste:

Limitazione Numero di Richieste Complessive
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il primo livello di configurazione, presente nella pagina di accesso,
consente di impostare i seguenti parametri:

-  *Stato* (abilitato \| disabilitato \| warningOnly): Attiva il
   controllo sul numero di richieste simultanee in elaborazione.
   Selezionando l'opzione *abilitato* le richieste simultanee ricevute,
   che eccedono la soglia indicata (parametro *MaxRichiesteSimultanee*)
   verranno rifiutate restituendo al chiamante un errore. La tipologia
   di errore restituita è configurabile tramite l’elemento *Tipologia
   Errore* che appare solamente in caso di controllo abilitato.

   Il controllo sul numero di richieste simultanee in elaborazione può
   anche essere attivato in modalità *WarningOnly* dove, in caso il
   superamento della soglia, genera solamente un messaggio diagnostico
   di livello *error* e un evento che segnala l’accaduto.

-  *Max Richieste Simultanee*: Corrisponde al numero massimo di
   richieste simultanee accettate. In genere è possibile fornire un
   valore accurato dopo aver valutato la portata massima del prodotto
   installato, in base alle risorse hardware disponibili e ai parametri
   di dimensionamento delle risorse applicative (ad esempio: numero
   connessioni al database, dimensioni della memoria java, ecc).

   Al superamento di tale valore non verranno accettate ulteriori
   richieste concorrenti, che verranno quindi rifiutate. Al verificarsi
   di questa situazione il gateway emette un evento specifico. Queste
   transazioni vengono marcate con esito *Superamento Limite Richieste*
   e saranno registrate solamente se previsto dalla configurazione (per
   default non vengono registrate). Per i dettagli sulla configurazione
   delle transazioni da registrare in base all'esito consultare la sezione :ref:`tracciamento`.

-  *Tipo Errore per API SOAP* e *Includi Descrizione Errore* (Opzioni
   presenti solo con console in modalità avanzata): Imposta il tipo di
   errore restituito al chiamante nel caso di rifiuto dell'elaborazione
   per superamento della soglia del numero massimo di richieste
   simultanee. Le opzioni possibili sono le seguenti:

   -  *Fault*: viene generato un messaggio di Fault contenente un codice
      ed una descrizione dell’errore rilevato nel caso l’elemento
      *Includi Descrizione Errore* sia abilitato, o un codice di errore
      generico altrimenti.

   -  *Http 429 (Too Many Requests)*

      *Http 503 (Service Unavailable)*

      *Http 500 (Internal Server Error)*

      Viene generata una risposta HTTP con il codice selezionato,
      contenente una pagina html di errore, se l’elemento *Includi
      Descrizione Errore* è abilitato, o una risposta http vuota
      altrimenti.

-  *Visualizza Informazioni Runtime*: Selezionando questo collegamento
   si apre una pagina (:numref:`congestioneRealTime`) che mostra in real-time le seguenti
   informazioni:

   -  *Richieste Attive*: il numero di richieste simultanee attualmente
      in corso di elaborazione.

   -  *Stato Gateway*: indica se il gateway ha raggiunto o meno lo stato
      di congestionamento, e quindi superata la soglia sul numero
      massimo di richieste simultanee.

.. note::
   L'indicatore è attivo solo nel caso in cui lo stato della
   successiva opzione *Controllo della Congestione* sia abilitato.

   -  *Refresh*: collegamento che consente di aggiornare le informazioni
      presentate nello schermo.

   .. figure:: ../../_figure_console/ControlloTraffico-Runtime.png
    :scale: 100%
    :align: center
    :name: congestioneRealTime

    Dati di congestionamento in tempo reale
