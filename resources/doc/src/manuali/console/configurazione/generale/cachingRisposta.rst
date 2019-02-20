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
   un valore hash calcolato rispetto ai dati della richiesta quali url
   di invocazione, header di trasporto e payload (se disponibile). E'
   possibile configurare quali parametri della richiesta utilizzare per
   generare il valore hash; per default vengono utilizzati tutti.

   .. figure:: ../../_figure_console/ConfigurazioneCachingRisposta.png
    :scale: 100%
    :align: center
    :name: cachingRispostaFig

    Maschera di configurazione per il Caching della Risposta
