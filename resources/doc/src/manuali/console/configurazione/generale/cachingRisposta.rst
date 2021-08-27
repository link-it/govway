.. _console_cachingRisposta:

Caching Risposta
~~~~~~~~~~~~~~~~

In GovWay è possibile abilitare il salvataggio delle risposte in una
cache globalmente in modo che sia attivo per tutte le APIs. Questa
funzionalità permette ad un backend server di non dover riprocessare le
stesse richieste più volte.

La configurazione permette di specificare i seguenti parametri:

-  *Stato*: Indicazione se il salvataggio delle risposte in cache è
   abilitata o meno globalmente su GovWay.

-  *Cache Timeout (secondi)*: intervallo di tempo, definito in secondi,
   per il quale la risposta salvata in cache viene utilizzata come
   risposte a successive richieste di un client.

-  *Dimensione Max Risposta*: se abilitato deve essere definita la
   dimensione massima (in kb) che una risposta può avere per essere
   salvata in cache.

-  *Generazione Hash*: ad ogni risposta salvata in cache viene associato
   un valore hash calcolato rispetto ai dati della richiesta che risultano abilitati tra le opzioni seguenti:

   - *URL di Richiesta*: viene utilizzata la URL della richiesta per il calcolo dell'hash.

   - *Payload*: viene utilizzato il payload della richiesta per il calcolo dell'hash.

    - *Headers*: vengono utilizzati gli header della richiesta indicati per il calcolo dell'hash. L'abilitazione di questa opzione comporta l'aggiunta di un elemento per consentire di specificare gli headers da selezionare.

-   *Cache Control*: opzioni aggiuntive per la gestione della cache basate sul header HTTP "Cache-Control":

    - *No Cache*: consente di attivare l'utilizzo della direttiva "no-cache" al fine di effettuare una richiesta evitando di ottenere una risposta dalla cache.

    - *Max Age*: consente di attivare l'utilizzo della direttiva "max-age" che consente di forzare il tempo di vita, al valore fornito, della risposta inserita in cache.

    - *No Store*: consente di attivare l'utilizzo della direttiva "no-store" che consente di impedire l'inserimento in cache della risposta generata dalla richiesta corrente.

   .. figure:: ../../_figure_console/ConfigurazioneCachingRisposta.png
    :scale: 100%
    :align: center
    :name: cachingRispostaFig

    Maschera di configurazione per il Caching della Risposta

Dopo aver salvato la configurazione fornita per il caching della risposta, appare la sezione *Configurazione Avanzata* che comprende il link *Regole*. Seguendo tale link è possibile definire ulteriori criteri avanzati per la gestione della cache.

.. note::
    In presenza di regole avanzate di configurazione, le risposte salvate in cache saranno solamente quelle che hanno un match con i criteri definiti in una regola.

Come si vede in :numref:`regoleCachingRispostaFig` ciascuna regola è composta dai seguenti campi:

-   *Codice Risposta*: codice HTTP ottenuto in risposta. Sono disponibili per la scelta le seguenti opzioni:

    - *Qualsiasi*: indica qualunque valore del codice HTTP restituito

    - *Singolo*: consente di specificare un singolo valore del codice HTTP restituito

    - *Intervallo*: consente di fornire l'intervallo dei valori ammessi per il codice HTTP restituito

-   *Cache Timeout (Secondi)*: indica in secondi il timeout applicato agli elementi in cache relativamente ai codici HTTP che soddisfano la regola.

-   *Fault*: opzione per specificare se anche i messaggi di fault devono essere inseriti in cache.

   .. figure:: ../../_figure_console/RegoleCachingRisposta.png
    :scale: 100%
    :align: center
    :name: regoleCachingRispostaFig

    Inserimento di una regola per il Caching della Risposta
