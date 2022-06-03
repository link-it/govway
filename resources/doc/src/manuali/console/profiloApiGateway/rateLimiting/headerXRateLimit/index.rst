.. _headerGWRateLimiting:

Header HTTP informativi restituiti ai client: quote e finestre temporali
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

All'applicativo client vengono restituiti header http informativi che consentono di conoscere:

- il numero massimo di richieste effettuabili (quota);
- la finestra temporale in cui si applica la quota (informazione disponibile solamente con opzione 'window' abilitata, descritta nel seguito di questa sezione);
- il numero di secondi mancanti alla prossima finestra temporale dove il numero di richieste conteggiate verrà azzerato;
- il numero di richieste ancora effettuabili nella finestra temporale in corso;
- in caso di violazione della policy, il numero di secondi dopo i quali riprovare ad utilizzare il servizio.

Il nome dell'header HTTP utilizzato da GovWay varia a seconda della metrica associata ad una policy. Ad esempio nella tabella :numref:`headerGwRateLimitingMetricaNumeroRichieste` vengono riportati i nomi degli header HTTP utilizzati per la metrica 'Numero Risorse' che contengono le informazioni sopra descritte.

Se verranno configurate policy con metriche differenti verranno restituite al client i corrispettivi header HTTP previsti per ogni metrica verificata. Una descrizione completa dei nomi degli header http utilizzati per ogni metrica è disponibile nella sezione :ref:`headerGWRateLimitingHeader`.

In presenza di molteplici policy con la medesima metrica verranno ritornate le informazioni relative alla policy più restrittiva.

L'indicazione sul numero di secondi dopo i quali il client può riprovare ad utilizzare il servizio, in caso di violazione di una policy, viene invece riportato nell'header http descritto nella tabella :numref:`headerGwRateLimitingRetryAfter`. Il numero indicato nell'header *Retry-After* viene calcolato sommando al numero di secondi mancante alla prossima finestra temporale, un tempo di backoff rappresentato da un numero random di secondi tra 0 e 60. L'aggiunto del tempo di backoff mira ad evitare che al ripristino dell'intervallo tutti i client in attesa concentrino le richieste nel medesimo istante.

È possibile personalizzare gli header http restituiti al client disabilitandone la generazione oppure facendo ritornare anche l'informazione sulla finestra temporale. Le modalità di personalizzazione vengono descritte nella sezione :ref:`headerGWRateLimitingConfig`.

.. toctree::
   :maxdepth: 2

   header
   configurazione

